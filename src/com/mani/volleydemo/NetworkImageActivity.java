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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.mani.volleydemo.toolbox.FadeInImageListener;
import com.mani.volleydemo.util.BitmapUtil;


/**
 * Demonstrates how to execute ImageRequest and NetworkImageView to download a image from a URL using Volley library.
 * @author Mani Selvaraj
 *
 */

public class NetworkImageActivity extends Activity {

	private Button mTrigger;
	private RequestQueue mVolleyQueue;
	private ListView mListView;
	private ImageView mImageView1;
	private ImageView mImageView2;
	private ImageView mImageView3;
	private NetworkImageView mNetworkImageView;
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
	        entry.data = data ; */
			
	        entry.data = BitmapUtil.convertBitmapToBytes(bitmap) ;
	        put(url, entry);
	    }
	}
	
	JsonObjectRequest jsonObjRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.networkimage_layout);
		
		actionBarSetup();
		
		// Initialise Volley Request Queue. 
		mVolleyQueue = Volley.newRequestQueue(this);

		int max_cache_size = 1000000;
		mImageLoader = new ImageLoader(mVolleyQueue, new DiskBitmapCache(getCacheDir(),max_cache_size));
		
		//Memorycache is always faster than DiskCache. Check it our for yourself.
		//mImageLoader = new ImageLoader(mVolleyQueue, new BitmapCache(max_cache_size));

		mDataList = new ArrayList<DataModel>();
		
		mListView = (ListView) findViewById(R.id.image_list);
		mImageView1 = (ImageView) findViewById(R.id.imageview1);
		mImageView2 = (ImageView) findViewById(R.id.imageview2);
		mImageView3 = (ImageView) findViewById(R.id.imageview3);
		mNetworkImageView = (NetworkImageView) findViewById(R.id.networkimageview);
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
		
		String testUrlToDownloadImage1 = "https://farm3.static.flickr.com/2833/9112621564_32bdfd58f3_q.jpg";
		String testUrlToDownloadImage2 = "https://farm3.static.flickr.com/2848/9110760994_c8dc834397_q.jpg";
			
		/* Demonstrating 3 ways of image downloading.
		  1 - Using ImageLoader and passing a url and imageListener. Additionally u can pass w & h
		  2 - User NetworkImageView and pass a url & ImageLoader
		  
		  The above 2 uses underlying 'ImageRequest' to initiate the download.
		  3 - Directly use ImageRequest api, by passing url, w & h, listeners, and BitmapConfig
		  It has default retry mechanism i set to 2 maximum retries.
		*/

		//1) In case you are showing image as user icon normally 50x50, you can specify the width & height.
        mImageLoader.get(testUrlToDownloadImage1, 
							ImageLoader.getImageListener(mImageView1, 
															R.drawable.flickr, 
															android.R.drawable.ic_dialog_alert),
							//You can specify width & height of the bitmap to be scaled down when the image is downloaded.
							50,50);

        //1 & 2) are almost same. Demonstrating you can apply animations while showing the downloaded image.
        // You can use nice entry animations while showing images in a listview.Uses custom implemented 'FadeInImageListener'.
		mImageLoader.get(testUrlToDownloadImage2, new FadeInImageListener(mImageView2,this));
		
		//3)
		ImageRequest imgRequest = new ImageRequest(testUrlToDownloadImage2, new Response.Listener<Bitmap>() {
				@Override
				public void onResponse(Bitmap response) {
					mImageView3.setImageBitmap(response);
				}
			}, 0, 0, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					mImageView3.setImageResource(R.drawable.ic_launcher);
				}
			});
		mVolleyQueue.add(imgRequest);
		
		//4)
		mNetworkImageView.setImageUrl(testUrlToDownloadImage1, mImageLoader);

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void actionBarSetup() {
	  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	    ActionBar ab = getActionBar();
	    ab.setTitle("ImageLoading");
	  }
	}

	public void onDestroy() {
		super.onDestroy();
		System.out.println("######### onDestroy ######### "+mAdapter);
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
		builder.appendQueryParameter("api_key", "5e045abd4baba4bbcd866e1864ca9d7b");
		builder.appendQueryParameter("method", "flickr.interestingness.getList");
		builder.appendQueryParameter("format", "json");
		builder.appendQueryParameter("nojsoncallback", "1");
		
		
		jsonObjRequest = new JsonObjectRequest(Request.Method.GET, builder.toString(), null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				System.out.println("####### Response JsonObjectRequest SUCCESS  ######## "+response.toString());
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
				stopProgress();
				System.out.println("####### onErrorResponse ########## "+error.getMessage()); 
				showToast(error.getMessage());
			}
		});

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
		Toast.makeText(NetworkImageActivity.this, msg, Toast.LENGTH_LONG).show();
	}
	
	private void parseFlickrImageResponse(JSONObject response) throws JSONException {
		System.out.println("#######  parseFlickrImageResponse   ######## "+mAdapter);
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
					
					String imageUrl = "https://farm" + farm + ".static.flickr.com/" + server + "/" + id + "_" + secret + "_t.jpg";
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
            System.out.println("#######  getView   ########### "+position); 
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.networkimage_list_item, null);
                holder = new ViewHolder();
                holder.image = (NetworkImageView) convertView.findViewById(R.id.image);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            
            holder.title.setText(mDataList.get(position).getTitle());
            // As contrast to 
            holder.image.setImageUrl(mDataList.get(position).getImageUrl(),mImageLoader);
            return convertView;
        }
        
        class ViewHolder {
            TextView title;
            NetworkImageView image;
        }	
        
	}	
}
