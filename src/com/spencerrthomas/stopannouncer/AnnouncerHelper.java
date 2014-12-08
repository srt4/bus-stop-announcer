package com.spencerrthomas.stopannouncer;

import java.util.HashMap;
import java.util.Map;


public class AnnouncerHelper {

	public static String addressToTTSString(String address) {
		Map<String, String> replaceMap = new HashMap<>();
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
			returnString.append(word).append(" ");
		}
		return returnString.toString();
	}

}
