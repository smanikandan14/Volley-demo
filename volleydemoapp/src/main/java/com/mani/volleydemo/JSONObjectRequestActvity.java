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

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mani.volleydemo.toolbox.FadeInImageListener;
import com.mani.volleydemo.util.BitmapUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates how to execute JSON Request using Volley library.
 * @author Mani Selvaraj
 *
 */

public class JSONObjectRequestActvity extends Activity {

	private Button mTrigger;
	private RequestQueue mVolleyQueue;
	private ListView mListView;
	private EfficientAdapter mAdapter;
	private ProgressDialog mProgress;
	private List<DataModel> mDataList;
	
	private ImageLoader mImageLoader;
	
	private final String TAG_REQUEST = "MY_TAG";
	
	private class DataModel {
		private String mImageUrl;
		private String mTitle;
		
		public String getImageUrl() {
			return mImageUrl;
		}
		public void setImageUrl(String mImageUrl) {
			this.mImageUrl = mImageUrl;
		}
		public String getTitle() {
			return mTitle;
		}
		public void setTitle(String mTitle) {
			this.mTitle = mTitle;
		}
		
	}
	
	public class BitmapCache extends LruCache<String,Bitmap> implements ImageCache {
	    public BitmapCache(int maxSize) {
	        super(maxSize);
	    }
	 
	    @Override
	    public Bitmap getBitmap(String url) {
	        return (Bitmap)get(url);
	    }
	 
	    @Override
	    public void putBitmap(String url, Bitmap bitmap) {
	        put(url, bitmap);
	    }
	}
	
	/*
	 * Extends from DisckBasedCache --> Utility from volley toolbox.
	 * Also implements ImageCache, so that we can pass this custom implementation
	 * to ImageLoader. 
	 */
	public  class DiskBitmapCache extends DiskBasedCache implements ImageCache {
		 
	    public DiskBitmapCache(File rootDirectory, int maxCacheSizeInBytes) {
	        super(rootDirectory, maxCacheSizeInBytes);
	    }
	 
	    public DiskBitmapCache(File cacheDir) {
	        super(cacheDir);
	    }
	 
	    public Bitmap getBitmap(String url) {
	        final Entry requestedItem = get(url);
	 
	        if (requestedItem == null)
	            return null;
	 
	        return BitmapFactory.decodeByteArray(requestedItem.data, 0, requestedItem.data.length);
	    }
	 
	    public void putBitmap(String url, Bitmap bitmap) {
	        
	    	final Entry entry = new Entry();
	        
/*			//Down size the bitmap.If not done, OutofMemoryError occurs while decoding large bitmaps.
 			// If w & h is set during image request ( using ImageLoader ) then this is not required.
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Bitmap downSized = BitmapUtil.downSizeBitmap(bitmap, 50);
			
			downSized.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] data = baos.toByteArray();
			
			System.out.println("####### Size of bitmap is ######### "+url+" : "+data.length);
	        entry.data = data ; */
			
	        entry.data = BitmapUtil.convertBitmapToBytes(bitmap) ;
	        put(url, entry);
	    }
	}
	
	JsonObjectRequest jsonObjRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.json_object_layout);
		
		actionBarSetup();
		
		// Initialise Volley Request Queue. 
		mVolleyQueue = Volley.newRequestQueue(this);

		int max_cache_size = 1000000;
		mImageLoader = new ImageLoader(mVolleyQueue, new DiskBitmapCache(getCacheDir(),max_cache_size));
		
		//Memory cache is always faster than DiskCache. Check it our for yourself.
		//mImageLoader = new ImageLoader(mVolleyQueue, new BitmapCache(max_cache_size));

		mDataList = new ArrayList<DataModel>();
		
		mListView = (ListView) findViewById(R.id.image_list);
		mTrigger = (Button) findViewById(R.id.send_http);
		
		mAdapter = new EfficientAdapter(this);
		mListView.setAdapter(mAdapter);

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
	    ab.setTitle("JSONRequest");
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
		//mVolleyQueue.cancelAll(TAG_REQUEST);
	}

	private void makeSampleHttpRequest() {
		
		String url = "https://api.flickr.com/services/rest";
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("api_key", "75ee6c644cad38dc8e53d3598c8e6b6c");
		builder.appendQueryParameter("method", "flickr.interestingness.getList");
		builder.appendQueryParameter("format", "json");
		builder.appendQueryParameter("nojsoncallback", "1");
		
		
		jsonObjRequest = new JsonObjectRequest(Request.Method.GET, builder.toString(), null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					parseFlickrImageResponse(response);
					mAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
					showToast("JSON parse error");
				}
				stopProgress();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// Handle your error types accordingly.For Timeout & No connection error, you can show 'retry' button.
				// For AuthFailure, you can re login with user credentials.
				// For ClientError, 400 & 401, Errors happening on client side when sending api request.
				// In this case you can check how client is forming the api and debug accordingly.
				// For ServerError 5xx, you can do retry or handle accordingly.
				if( error instanceof NetworkError) {
				} else if( error instanceof ClientError) { 
				} else if( error instanceof ServerError) {
				} else if( error instanceof AuthFailureError) {
				} else if( error instanceof ParseError) {
				} else if( error instanceof NoConnectionError) {
				} else if( error instanceof TimeoutError) {
				}

				stopProgress();
				showToast(error.getMessage());
			}
		});
		
		//Set a retry policy in case of SocketTimeout & ConnectionTimeout Exceptions. Volley does retry for you if you have specified the policy.
		jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		jsonObjRequest.setTag(TAG_REQUEST);	
		mVolleyQueue.add(jsonObjRequest);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void showProgress() {
		mProgress = ProgressDialog.show(this, "", "Loading...");
	}
	
	private void stopProgress() {
		mProgress.cancel();
	}
	
	private void showToast(String msg) {
		Toast.makeText(JSONObjectRequestActvity.this, msg, Toast.LENGTH_LONG).show();
	}
	
	private void parseFlickrImageResponse(JSONObject response) throws JSONException {
		
		if(response.has("photos")) {
			try {
				JSONObject photos = response.getJSONObject("photos");
				JSONArray items = photos.getJSONArray("photo");

				mDataList.clear();
				
				for(int index = 0 ; index < items.length(); index++) {
				
					JSONObject jsonObj = items.getJSONObject(index);
					
					String farm = jsonObj.getString("farm");
					String id = jsonObj.getString("id");
					String secret = jsonObj.getString("secret");
					String server = jsonObj.getString("server");
					
					String imageUrl = "http://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret + "_t.jpg";
					DataModel model = new DataModel();
					model.setImageUrl(imageUrl);
					model.setTitle(jsonObj.getString("title"));
					mDataList.add(model);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private  class EfficientAdapter extends BaseAdapter {
		
        private LayoutInflater mInflater;
        
        public EfficientAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return mDataList.size();
        }
        
        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item, null);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            
            holder.title.setText(mDataList.get(position).getTitle());
            System.out.println("###### Image Url ###### "+mDataList.get(position).getImageUrl());
            mImageLoader.get(mDataList.get(position).getImageUrl(), new FadeInImageListener(holder.image,JSONObjectRequestActvity.this));
            
/*            mImageLoader.get(mDataList.get(position).getImageUrl(), 
            							ImageLoader.getImageListener(holder.image, R.drawable.flickr, android.R.drawable.ic_dialog_alert),
            							//You can specify width & height of the bitmap to be scaled down when the image is downloaded.
            							50,50); */
            return convertView;
        }
        
        class ViewHolder {
            TextView title;
            ImageView image;
        }	
        
	}	

}

