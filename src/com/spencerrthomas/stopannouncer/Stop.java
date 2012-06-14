package com.spencerrthomas.stopannouncer;

import android.location.Location;

public class Stop extends Location {
	private String stopName;
	private String id;
	public boolean hasBeenSpoken; 
	private char direction; 
	
	/**
	 * 
	 * @param stopName
	 * @param location
	 */
	public Stop(String stopName, double lat, double lon) {
		super("");
		this.setStopName(stopName);
		this.setLatitude(lat);
		this.setLongitude(lon);
	}
	
	/**
	 * 
	 * @param location
	 */
	public Stop(String stopName) {
		this(stopName, -1.0, -1.0);
	}

	public String getStopName() {
		return stopName;
	}

	public void setStopName(String stopName) {
		this.stopName = stopName;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	@Override
	public String toString() {
		return stopName;// + ": " + this.getLatitude() + ", " + this.getLongitude();
	}

	public char getDirection() {
		return (direction + "".toUpperCase()).charAt(0);
	}

	public void setDirection(char direction) {
		this.direction = direction;
	}
}