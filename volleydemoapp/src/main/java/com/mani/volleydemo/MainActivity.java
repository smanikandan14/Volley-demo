package com.mani.volleydemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

public class MainActivity extends Activity {

	private Button mJsonRequest;
	private Button mStringRequest;
	private Button mGsonParse;
	private Button mNetworkImage;
	private Button mSslRequest;
	private Button mMultipartRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		
		TextView tt = (TextView) findViewById(R.id.test);
		tt.setMovementMethod(LinkMovementMethod.getInstance());
		
    	String str = "Demonstration of Volley library announced by Android Team in [Google I/0 2013]. Find the source code [github].Demo uses Flickr REST apis.[Avoid using api key for your usage.]Thanks.";
    	
    	SpannableStringBuilder ssb = new SpannableStringBuilder(str);

    	    int idx1 = str.indexOf("[");
    	    int idx2 = 0;
    	    while (idx1 != -1) {
    	        idx2 = str.indexOf("]", idx1) + 1;

    	        final String clickString = str.substring(idx1, idx2);
    	        
    	        if(clickString.equals("[Google I/0 2013]")) {
        	        ssb.setSpan(new ClickableSpan() {
        	            @Override
        	            public void onClick(View widget) {
        	                Toast.makeText(MainActivity.this, clickString,
        	                        Toast.LENGTH_SHORT).show();

        	            	String url = "http://www.youtube.com/watch?v=yhv8l9F44qo";
        	            	Intent i = new Intent(Intent.ACTION_VIEW);
        	            	i.setData(Uri.parse(url));
        	            	startActivity(i);
        	            }
        	        }, idx1, idx2, 0);

    	        }  
    	        
    	        if (clickString.equals("[github]")) {
        	        ssb.setSpan(new ClickableSpan() {
        	            @Override
        	            public void onClick(View widget) {
        	            	String url = "https://github.com/smanikandan14/Volley-demo";
        	            	Intent i = new Intent(Intent.ACTION_VIEW);
        	            	i.setData(Uri.parse(url));
        	            	startActivity(i);      
        	                Toast.makeText(MainActivity.this, clickString,
        	                        Toast.LENGTH_SHORT).show();

        	            }
        	        }, idx1, idx2, 0);
    	        }
    	        idx1 = str.indexOf("[", idx2);
    	    }

    	tt.setText(ssb, BufferType.SPANNABLE);    	    
		
    	mJsonRequest = (Button) findViewById(R.id.json_request);
		mJsonRequest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,JSONObjectRequestActvity.class));
			}
		});

		mStringRequest = (Button) findViewById(R.id.string_request);
		mStringRequest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,StringObjectRequestActivity.class));
			}
		});
		
		mGsonParse = (Button) findViewById(R.id.gson_response);
		mGsonParse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,GSONObjectRequestActivity.class));
			}
		});

		mNetworkImage = (Button) findViewById(R.id.networkimage);
		mNetworkImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,NetworkImageActivity.class));
			}
		});

		mSslRequest = (Button) findViewById(R.id.ssl_connection);
		mSslRequest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,SSLConnectionActivity.class));
			}
		});
		
		mMultipartRequest = (Button) findViewById(R.id.multipart_request);
		mMultipartRequest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//startActivity(new Intent(MainActivity.this,MultiPartRequestActivity.class));
			}
		});
		
	}

}
