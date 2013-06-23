package com.example.volleysample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button mJsonRequest;
	private Button mStringRequest;
	private Button mGsonParse;
	private Button mNetworkImage;
	private Button mSslRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		
	
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

	}

}
