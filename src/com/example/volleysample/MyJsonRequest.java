package com.example.volleysample;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

public class MyJsonRequest extends JsonObjectRequest{

	public MyJsonRequest(int method, String url, JSONObject jsonRequest,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		
		super(method, url, jsonRequest, listener, errorListener);
		
		
	}

	private Map<String, String> headers = new HashMap<String, String>();
	
	@Override
	 public Map<String, String> getHeaders() throws AuthFailureError {
		return headers;
	 }
	
}
