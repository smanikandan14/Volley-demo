package com.example.volleysample.toolbox;

import java.util.HashMap;

import java.util.Map;

import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

/**
 * Custom Implementation of com.android.volley.toolbox.JsonObjectRequest 
 * to handle the headers.
 * @author Mani Selvaraj
 *
 */
public class MyJsonRequest extends JsonObjectRequest{

	private Map<String, String> headers = new HashMap<String, String>();
	private Priority priority = null;
	
	public MyJsonRequest(int method, String url, JSONObject jsonRequest,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super(method, url, jsonRequest, listener, errorListener);
		// TODO Auto-generated constructor stub
	}

    public MyJsonRequest(String url, JSONObject jsonRequest, Listener<JSONObject> listener,
            ErrorListener errorListener) {
    	super(url,jsonRequest,listener,errorListener);
    }
    
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return headers;
	}
	
	public void setHeader(String title, String content) {
		 headers.put(title, content);
	}
	
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	
	/*
	 * If prioirty set use it,else returned NORMAL
	 * @see com.android.volley.Request#getPriority()
	 */
    public Priority getPriority() {
    	if( this.priority != null) {
    		return priority;
    	} else {
    		return Priority.NORMAL;	
    	}
    }

}
