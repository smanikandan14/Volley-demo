/**
 * Copyright 2013 Mani Selvaraj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.volleysample;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.volleysample.ssl.SslHttpStack;

/**
 * Demonstrates: StringObjectRequest usage.  
 * StringObjectRequest converts the response to String. ( can be json,xml )You can then do the corresponding
 * parsing to parse your response.
 *   
 * @author Mani Selvaraj
 *
 */
public class StringObjectRequestActivity extends Activity {

		private Button mTrigger;
		private TextView mResultView;
		private RequestQueue mVolleyQueue;
		private ProgressDialog mProgress;
		private final String TAG_REQUEST = "MY_TAG";
		

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.string_object_layout);
			
			actionBarSetup();
			
			// Initialise Volley Request Queue. 
			mVolleyQueue = Volley.newRequestQueue(this,new SslHttpStack(true));

			mResultView = (TextView) findViewById(R.id.result_txt);
			mTrigger = (Button) findViewById(R.id.send_http);
			mTrigger.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showProgress();
					makeSampleHttpRequest();
				}
			});
		}
		
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		private void actionBarSetup() {
		  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    ActionBar ab = getActionBar();
		    ab.setTitle("StringObjectRequest");
		  }
		}

		public void onDestroy() {
			super.onDestroy();
		}
		
		public void onStop() {
			super.onStop();
			if(mProgress != null)
				mProgress.dismiss();
			// Keep the list of requests dispatched in a List<Request<T>> mRequestList;
			/*
			 for( Request<T> req : mRequestList) {
			 	req.cancel();
			 }
			 */
			//jsonObjRequest.cancel();
			//( or )
			mVolleyQueue.cancelAll(TAG_REQUEST);
		}
		
		private void showProgress() {
			mProgress = ProgressDialog.show(this, "", "Loading...");
		}
		
		private void stopProgress() {
			mProgress.cancel();
		}
		
		private void showToast(String msg) {
			Toast.makeText(StringObjectRequestActivity.this, msg, Toast.LENGTH_LONG).show();
		}
		
		private void makeSampleHttpRequest() {
			
			String url = "http://api.openweathermap.org/data/2.5/weather?q=London,uk";
			
			StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					mResultView.setText(response);
					stopProgress();
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					stopProgress();
					showToast(error.getMessage());
				}
			});

			// To EXPERIMENT. Enable response caching to quickly fetch the response from cache, if set true.
			// Volley decides whether to cache the response or not, based on response headers obtained. Some of the parameters
			// to look for are Cache-control,maxAge, Expires.
			
			// In case of weather api, the response headers has "Cache-Control: no-cache, must-revalidate" In this case, even if 
			// setShouldCache() api is set true, Volley decides not to store the response, because server has sent response headers as "must-revalidate"
			// So storing response doesnt make sense here. Some of these intelligences are implemented already in Volley, you need not take the burden of
			// parsing response headers.
			
			stringRequest.setShouldCache(true);
			stringRequest.setTag(TAG_REQUEST);	
			mVolleyQueue.add(stringRequest);
 	}
}
