package com.spencerrthomas.stopannouncer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.location.Location;

public class Route {

	private Map<String, Stop> stops;
	private String name;
	private List<String> stopOrder;

	public Route(String name) {
		stops = new HashMap<String, Stop>();
		stopOrder = new ArrayList<String>();
		this.name = name;	
	}

	public void addStop(Stop s) {
		stops.put(s.getId(), s);
	}
	
	public void addStopOrder(String s) {
		stopOrder.add(s);
	}

	public Stop closestStop(Location my) {
		Stop closest = null;
		if (stopOrder == null || stopOrder.isEmpty()) {
			for(Stop s : stops.values()) {
				if (closest == null || s.distanceTo(my) < closest.distanceTo(my)) {
					closest = s;
				}
			} 
		}
		else {
			for(String stopId : stopOrder) {
				boolean proceed = false; 
				Stop s = stops.get(stopId);
				if (closest == null) closest = s;
				switch (s.getDirection()) {
				case 'N':
					proceed = s.getLatitude() > my.getLatitude();
					break;
				case 'S':
					proceed = s.getLatitude() < my.getLatitude();
					break;
				case 'E':
					proceed = s.getLongitude() < my.getLongitude();
					break;
				case 'W':
					proceed = s.getLongitude() > my.getLongitude();
					break;
				}
				
				if (proceed && s.distanceTo(my) < closest.distanceTo(my)) {
					closest = s;
				}
			}
		}
		return closest;
	}
	
	@Override
	public String toString() {
		return "Route " + this.name;
	}

}
