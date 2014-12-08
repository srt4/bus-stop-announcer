package com.spencerrthomas.stopannouncer;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import com.spencerrthomas.stopannouncer.model.Route;
import com.spencerrthomas.stopannouncer.model.Stop;

public class StopAnnouncingLocationListener implements LocationListener {

    private final TextToSpeech announcer;
    private final TextView routeTextView;
    private final TextView currentStopTextView;
    private final Route route;

    public StopAnnouncingLocationListener(final TextToSpeech announcer,
                                          final TextView routeTextView,
                                          final TextView currentStopTextView,
                                          final Route route) {
        assert route != null && announcer != null && routeTextView != null
                && currentStopTextView != null;

        this.announcer = announcer;
        this.routeTextView = routeTextView;
        this.currentStopTextView = currentStopTextView;
        this.route = route;
    }

    @Override
    public void onLocationChanged(final Location location) {
        if (location == null) {
            return;
        }

        routeTextView.setText(location.getLatitude() + ", " + location.getLongitude());

        final Stop closest = route.closestStop(location);
        closest.announceIfNecessary(announcer, location);
        currentStopTextView.setText(closest.toString());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}

}
