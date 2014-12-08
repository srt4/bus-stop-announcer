package com.spencerrthomas.stopannouncer;

import android.content.Context;
import android.location.LocationManager;
import android.widget.TextView;

public class LocationManagerInitializerRunnable implements Runnable {

    private final StopAnnouncerActivity stopAnnouncerActivity;

    public LocationManagerInitializerRunnable(final StopAnnouncerActivity stopAnnouncerActivity) {
        this.stopAnnouncerActivity = stopAnnouncerActivity;
    }

    public void run() {
        final LocationManager locManager = (LocationManager)
                stopAnnouncerActivity.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(gpsEnabled) {
            final StopAnnouncingLocationListener locListener = new StopAnnouncingLocationListener(
                    stopAnnouncerActivity.getTts(),
                    (TextView)stopAnnouncerActivity.findViewById(R.id.route),
                    (TextView)stopAnnouncerActivity.findViewById(R.id.currentStop),
                    stopAnnouncerActivity.getRoute()
            );
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, locListener);
        }
    }

}
