package com.spencerrthomas.stopannouncer;

import java.util.HashMap;


public class StopHelpers {

	public static String addressToTTSString(String address) {
		HashMap<String, String> replaceMap = new HashMap<String, String>();
		replaceMap.put("NE", "Northeast");
		replaceMap.put("NW", "Northwest");
		replaceMap.put("SE", "southeast");
		replaceMap.put("SW", "southwest");
		replaceMap.put("E", "east");
		replaceMap.put("W", "west");
		replaceMap.put("S", "south");
		replaceMap.put("N", "north");
		replaceMap.put("ST", "street");
		replaceMap.put("AVE", "avenue");
		
		String[] words = address.split(" ");
		StringBuilder returnString = new StringBuilder();
		for(String word : words) {
			if (replaceMap.containsKey(word.toUpperCase())) {
				word = ""; //+ replaceMap.get(word.toUpperCase());
			}
			word = word.toLowerCase();
			returnString.append(word + " ");
		}
		return returnString.toString();
	}
}
