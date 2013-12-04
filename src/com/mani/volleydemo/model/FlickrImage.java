package com.mani.volleydemo.model;


/**
 * Holds the data for Flickr photo that is used to display Flickr Images in ListViews.
 * 
 * @author Mani Selvaraj
 *
 */
public class FlickrImage {

	
	String id;
	
	String secret;
	
	String server;
	
	String farm;
	
	String title;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getFarm() {
		return farm;
	}

	public void setFarm(String farm) {
		this.farm = farm;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
