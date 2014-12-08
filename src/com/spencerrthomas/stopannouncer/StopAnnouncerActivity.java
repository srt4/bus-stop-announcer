package com.spencerrthomas.stopannouncer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;

import com.spencerrthomas.stopannouncer.model.Route;
import com.spencerrthomas.stopannouncer.model.Stop;

public class StopAnnouncerActivity extends Activity implements OnInitListener {

    public static final String ROUTE_NUMBER = "271";
    private static final int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech tts;
    private Route route;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        final Stop stop = new Stop("");
        stop.setLatitude(47.12222);
        stop.setLongitude(-122.0148324);

        final StopRetrieverTask retriever = new StopRetrieverTask(this);
        retriever.execute("271");
        
        final Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    }

    void addStopOrders(final JSONObject jso) throws JSONException {
    	final JSONArray stopGroupings = jso.getJSONArray("stopGroupings");
    	final JSONObject stopGrouping = stopGroupings.getJSONObject(0);
    	final JSONArray stopGroups = stopGrouping.getJSONArray("stopGroups");
    	final JSONObject stopGroup = stopGroups.getJSONObject(0);
    	final JSONArray stopIds = stopGroup.getJSONArray("stopIds");
    	
    	for(int i = 0; i < stopIds.length(); i++) {
    		route.addStopOrder(stopIds.getString(i));
    	}
    }

    void addStops(JSONArray stops) throws JSONException {
    	route = new Route(ROUTE_NUMBER);
    	
    	for(int i = 0; i < stops.length(); i++) { // JSONArray is not iterable
    		JSONObject stop = (JSONObject) stops.get(i);
    		Stop s = new Stop(stop.getString("name"));
    		s.setLatitude(Double.parseDouble(stop.getString("lat")));
    		s.setLongitude(Double.parseDouble(stop.getString("lon")));
    		s.setId(stop.getString("id"));
    		try {
    			s.setDirection(stop.getString("direction").charAt(0));
    		} catch(StringIndexOutOfBoundsException e) {
    			s.setDirection('Z');
    		}
    		route.addStop(s);
    	}
    }
    
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
	    if (requestCode == MY_DATA_CHECK_CODE) {
	        if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
	            tts = new TextToSpeech(this, this);
	
	        } 
	        else {
	            // missing data, install it
	            final Intent installIntent = new Intent();
	            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
	            startActivity(installIntent);
	        }
	     }
   }
    
    @Override
    public void onInit(final int status) {
      if (status != TextToSpeech.SUCCESS) {
            Toast.makeText(StopAnnouncerActivity.this, "Error occurred while initializing " +
                    "Text-To-Speech engine", Toast.LENGTH_LONG).show();
      }
    }

    TextToSpeech getTts() {
        return tts;
    }

    Route getRoute() {
        return route;
    }

}