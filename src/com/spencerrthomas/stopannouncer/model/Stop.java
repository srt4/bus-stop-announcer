package com.spencerrthomas.stopannouncer.model;

import android.location.Location;
import android.speech.tts.TextToSpeech;

import com.spencerrthomas.stopannouncer.AnnouncerHelper;

public class Stop extends Location {
	private String stopName;
	private String id;
	private boolean hasBeenAnnounced;
	private char direction; 

	public Stop(String stopName, double lat, double lon) {
		super((String)null);
		this.setStopName(stopName);
		this.setLatitude(lat);
		this.setLongitude(lon);
	}

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

    public boolean hasBeenAnnounced() {
        return hasBeenAnnounced;
    }
	
	@Override
	public String toString() {
		return stopName;
	}

	public char getDirection() {
        return Character.toUpperCase(direction);
	}

	public void setDirection(final char direction) {
		this.direction = direction;
	}

    public void announceIfNecessary(final TextToSpeech announcer, final Location location) {
        if (location.distanceTo(this) > 200 || hasBeenAnnounced()) {
            return; // already announced &/or the stop is too far away
        }

        announcer.speak("now arriving at ;;; " + AnnouncerHelper.addressToTTSString(getStopName()),
                TextToSpeech.QUEUE_ADD, null);
        this.hasBeenAnnounced = true;
    }

}