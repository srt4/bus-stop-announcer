package com.spencerrthomas.stopannouncer;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class StopRetrieverTask extends AsyncTask<String, Void, Void> {

    private final StopAnnouncerActivity stopAnnouncerActivity;

    public StopRetrieverTask(final StopAnnouncerActivity stopAnnouncerActivity) {
        this.stopAnnouncerActivity = stopAnnouncerActivity;
    }

    @Override
    protected Void doInBackground(final String... args) {
        final String route = args[0];

        final DefaultHttpClient dhc = new DefaultHttpClient();
        final HttpGet get = new HttpGet("http://api.onebusaway.org/api/where/stops-for-route/1_"
                + route + ".json?key=TEST");

        final HttpResponse hr;
        try {
            hr = dhc.execute(get);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(
                    hr.getEntity().getContent(), "UTF-8"));
            final String json = reader.readLine();
            final JSONObject jsoP = new JSONObject(json);
            final JSONObject jso = jsoP.getJSONObject("data");
            final JSONArray stops = jso.getJSONArray("stops");
            stopAnnouncerActivity.addStops(stops);
            stopAnnouncerActivity.addStopOrders(jso);


            final Runnable initializeLocationRunnable = new LocationManagerInitializerRunnable(
                    stopAnnouncerActivity);
            stopAnnouncerActivity.runOnUiThread(initializeLocationRunnable);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
