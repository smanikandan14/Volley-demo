package com.example.volleysample;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends Activity {

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
	        
	        
	        // Convert Bitmap to bytes.
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			
			//Down size the bitmap.If not done, OutofMemoryError occurs while decoding.
			Bitmap downSized = BitmapUtil.downSizeBitmap(bitmap, 50);
			
			downSized.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] data = baos.toByteArray();
			
			System.out.println("####### Size of bitmap is ######### "+url+" : "+data.length);
	        entry.data = data ;//buffer.array();
	 
	        put(url, entry);
	    }
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		
		// Volley initilasations 
		mVolleyQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mVolleyQueue, new DiskBitmapCache(getCacheDir()));
		
		mDataList = new ArrayList<DataModel>();
		
		mListView = (ListView) findViewById(R.id.image_list);
		mTrigger = (Button) findViewById(R.id.send_http);
		
		mAdapter = new EfficientAdapter(this);
		mListView.setAdapter(mAdapter);
		
		String url = "https://www.googleapis.com/customsearch/v1?key=AIzaSyAN2eOCD6fqq8EjZxaoOcMsJbJY9DmE0hY&cx=013773464437216104273:zbokd7bqpbi&q=android+volley&alt=json&searchType=image";
		
		final JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				System.out.println("####### Response SUCCESS  ######## "+response.toString());
				try {
					parseGoogleSearchImageResult(response);
					mAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(MainActivity.this, "JSON parse error", Toast.LENGTH_LONG).show();
				}
				mVolleyQueue.cancelAll(TAG_REQUEST);
				stopProgress();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				stopProgress();
				Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
			}
		});
		
		
		jsonObjRequest.setTag(TAG_REQUEST);
		
		final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Listener<String>() {
			@Override
			public void onResponse(String response) {
				System.out.println("####### Response SUCCESS  ######## "+response.toString());
				stopProgress();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				stopProgress();
				Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
			}
		});

		mTrigger.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showProgress();
				mVolleyQueue.add(jsonObjRequest);
				//mVolleyQueue.add(stringRequest);
			}
		});
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
	
	private void parseGoogleSearchImageResult(JSONObject response) throws JSONException {
		if(response.has("items")) {
			try {
				JSONArray items = response.getJSONArray("items");
				for(int index = 0 ; index < items.length(); index++) {
				
					JSONObject jsonObj = items.getJSONObject(index);
					
					if(jsonObj.has("link")) {
						DataModel model = new DataModel();
						model.setImageUrl(jsonObj.getString("link"));
						if(jsonObj.has("title")) {
							model.setTitle(jsonObj.getString("title"));
						}
						mDataList.add(model);
					}
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
            mImageLoader.get(mDataList.get(position).getImageUrl(), ImageLoader.getImageListener(holder.image, android.R.drawable.ic_dialog_email, android.R.drawable.ic_dialog_alert));
            return convertView;
        }
        
        class ViewHolder {
            TextView title;
            ImageView image;
        }	
        
	}	
}
