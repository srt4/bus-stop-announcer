package com.spencerrthomas.stopannouncer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.location.Location;

public class Route {
	private Map<String, Stop> stops;
	private String name;
	public List<String> stopOrder;
	
	/**
	 * 
	 * @param name
	 */
	public Route(String name) {
		stops = new HashMap<String, Stop>();
		stopOrder = new ArrayList<String>();
		this.name = name;	
	}
	
	/**
	 * add stop
	 */
	public void addStop(Stop s) {
		stops.put(s.getId(), s);
	}
	
	/**
	 * get stop
	 */
	public Stop getStop(String id) {
		return stops.get(id);
	}

	public Stop getStopAfter(Stop stop) {
		return getStopAfter(stop.getId());
	}
	
	public Stop getStopAfter(String id) {
		String nextId = id;
		for (int i = 0; i < stopOrder.size() - 2; i++) {
			if (stopOrder.get(i).equals(stops.get(id).getId())) {
				System.out.println("Got here");
				nextId = stops.get( stopOrder.get(i + 1) ).getId();
			}
		}
		return stops.get(nextId);
	}
	
	public void addStopOrder(String s) {
		stopOrder.add(s);
	}
	
	/**
	 * 
	 * @param my
	 */
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
