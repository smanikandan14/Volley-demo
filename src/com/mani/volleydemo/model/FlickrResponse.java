package com.mani.volleydemo.model;

import java.util.List;

public class FlickrResponse {

	public String id;
	
	List<FlickrImage> photo;

	public List<FlickrImage> getPhotos() {
		return photo;
	}
}
