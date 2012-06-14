package com.spencerrthomas.stopannouncer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.TextView;
import android.widget.Toast;

public class StopAnnouncerActivity extends Activity implements OnInitListener {
	
	private HashMap<String, Stop> stops; 
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech tts;
    private Route fortyFour; 


	
    /** 
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        stops = new HashMap<String, Stop>();
        
        setContentView(R.layout.main);
        
        Stop s = new Stop("");
        s.setLatitude(47.12222);
        s.setLongitude(-122.0148324);
        System.out.println(s.toString() + "  ");
        
        GetStopsForRoute gsfr = new GetStopsForRoute();
        gsfr.execute("271");
        
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
        
    }
   
    
    
    private class GetStopsForRoute extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... args) {
			String route = args[0];
			
			DefaultHttpClient dhc = new DefaultHttpClient();
			HttpGet get = new HttpGet("http://api.onebusaway.org/api/where/stops-for-route/1_" + route + ".json?key=TEST");
			
			HttpResponse hr = null;
			try {
				hr = dhc.execute(get);
				BufferedReader reader = new BufferedReader(new InputStreamReader(hr.getEntity().getContent(), "UTF-8"));
				String json = reader.readLine();
				System.out.println(json);
				JSONObject jso = new JSONObject(json);
				jso = jso.getJSONObject("data");
				JSONArray stops = jso.getJSONArray("stops");
				System.out.println(stops.length());
				
				// add stops
				addStops(stops);
				
				// add stop orders
				addStopOrders(jso);
				
				// loc aware  
				Runnable r = new Runnable() {
					public void run() {
						// get location
						
						LocationManager locManager = (LocationManager) StopAnnouncerActivity.this.getSystemService(Context.LOCATION_SERVICE);
						boolean gpsEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
						
						if(gpsEnabled) {
							LocListener locListener = new LocListener();
							locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
						}
					}
				};
				
				runOnUiThread(r);
				//Thread t = new Thread(r);
				//t.start();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
    }
    
    private void addStopOrders(JSONObject jso) throws JSONException {
    	///response/data/entry/stopGroupings/stopGrouping/stopGroups
    	JSONArray stopGroupings = jso.getJSONArray("stopGroupings");
    	JSONObject stopGrouping = stopGroupings.getJSONObject(0);
    	JSONArray stopGroups = stopGrouping.getJSONArray("stopGroups");
    	JSONObject stopGroup = stopGroups.getJSONObject(0);
    	JSONArray stopIds = stopGroup.getJSONArray("stopIds");
    	
    	for(int i = 0; i < stopIds.length(); i++) {
    		fortyFour.addStopOrder(stopIds.getString(i));
    		System.out.println(fortyFour.getStop((String) stopIds.get(i)));
    	}
    	
    	// now, get the location
    	
    	/*
    	tts.speak(
    			StopHelpers.addressToTTSString("Now arriving at;;; " + fortyFour.closestStop(new Stop("test", 47.660575,-122.313473))), TextToSpeech.QUEUE_ADD, null);
    	*/
    }
    private void addStops(JSONArray stops) throws JSONException {
    	fortyFour = new Route("44");
    	
    	for(int i = 0; i < stops.length(); i++) // JSONArray is not iterable
    	{
    		JSONObject stop = (JSONObject) stops.get(i);
    		Stop s = new Stop(stop.getString("name"));
    		s.setLatitude(Double.parseDouble(stop.getString("lat")));
    		s.setLongitude(Double.parseDouble(stop.getString("lon")));
    		s.setId(stop.getString("id"));
    		try { 
    			System.out.println(stop.getString("direction"));
    			s.setDirection(stop.getString("direction").charAt(0));
    		} catch(StringIndexOutOfBoundsException e) {
    			s.setDirection('Z');
    		}
    		fortyFour.addStop(s);
    	}
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == MY_DATA_CHECK_CODE) {
	        if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
	            // success, create the TTS instance
	            tts = new TextToSpeech(this, this);
	
	        } 
	        else {
	            // missing data, install it
	            Intent installIntent = new Intent();
	            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
	            startActivity(installIntent);
	        }
	     }
   }
    
    @Override
    public void onInit(int status) {                
      if (status == TextToSpeech.SUCCESS) {
            Toast.makeText(StopAnnouncerActivity.this, "Text-To-Speech engine is initialized", Toast.LENGTH_LONG).show();
            String text = "Now arriving at; 41st and 15th St;;;; Next stop;;;; 45th and Brooklyn";
            //tts.speak(text, TextToSpeech.QUEUE_ADD, null);    
      }
      else if (status == TextToSpeech.ERROR) {
            Toast.makeText(StopAnnouncerActivity.this, "Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_LONG).show();
      }
    }

    private class LocListener implements LocationListener {

		@Override
		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub
			if (arg0 != null) {
				System.out.println(arg0);
			}
			((TextView)findViewById(R.id.route)).setText(arg0.getLatitude() + ", " + arg0.getLongitude());
			Stop closest = fortyFour.closestStop(arg0);
			Stop next = fortyFour.getStopAfter(closest);
			System.out.println(arg0.distanceTo(closest) + "");

			if (arg0.distanceTo(closest) < 200 && !closest.hasBeenSpoken) {
				tts.speak("now arriving at ;;; " 
							+ StopHelpers.addressToTTSString(closest.getStopName())
							+ ";;; next stop ;;; " + 
							StopHelpers.addressToTTSString(next.getStopName()), TextToSpeech.QUEUE_ADD, null);
				closest.hasBeenSpoken = true;
			}
			
			((TextView)findViewById(R.id.currentStop)).setText(closest + "");
			((TextView)findViewById(R.id.nextStop)).setText("" + next);
		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
    	
    }
}