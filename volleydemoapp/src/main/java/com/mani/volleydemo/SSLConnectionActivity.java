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

package com.mani.volleydemo;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mani.volleydemo.ssl.SslHttpStack;

/**
 * Demonstrates how to execute Https (SSL) Connectivity Request using Volley library.
 * 
 * @author Mani Selvaraj
 *
 */

public class SSLConnectionActivity extends Activity {

	private Button mTrigger;
	private RequestQueue mVolleyQueue;
	private ProgressDialog mProgress;
	private final String TAG_REQUEST = "MY_TAG";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.json_object_layout);
		
		actionBarSetup();
		
		// Initialise Volley Request Queue with SSL HttpStack to handle secured connection.
		mVolleyQueue = Volley.newRequestQueue(this,new SslHttpStack(true));

		mTrigger = (Button) findViewById(R.id.send_http);
		mTrigger.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showProgress();
				makeSampleHttpsRequest();
			}
		});
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void actionBarSetup() {
	  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	    ActionBar ab = getActionBar();
	    ab.setTitle("SSL Connection");
	  }
	}

	public void onDestroy() {
		super.onDestroy();
	}
	
	/*
	 * Dont forget to cancel the request when the UI is not visible.
	 * @see android.app.Activity#onStop()
	 */
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
		//( or )
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
		Toast.makeText(SSLConnectionActivity.this, msg, Toast.LENGTH_LONG).show();
	}
	
	/*
	 * The endpoint is for demo.Please use your correct endpoint to test Secured Http request.
	 */
	private void makeSampleHttpsRequest() {
		
		String url = "https://[YOUR END POINT]";
		
		
		JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				stopProgress();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				stopProgress();
				showToast(error.getMessage());
			}
		});
		jsonObjRequest.setTag(TAG_REQUEST);	
		mVolleyQueue.add(jsonObjRequest);
	}
}
