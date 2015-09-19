package com.stakoun.geoconnect;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Peter on 2015-09-19.
 */
public class WaypointListener implements LocationListener
{
    MapsActivity mapsActivity;

    public WaypointListener(MapsActivity mapsActivity)
    {
        this.mapsActivity = mapsActivity;
    }

    public void onLocationChanged(Location location)
    {
        Waypoint[] waypoints = mapsActivity.getWaypoints();
        if (waypoints == null) {
            return;
        }
        for (Waypoint waypoint : waypoints) {
            float[] result = new float[1];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), waypoint.getLatitude(), waypoint.getLongitude(), result);
            if (result[0] <= waypoint.getRadius()) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mapsActivity);
                if (waypoint.canSendNotif() && prefs.getBoolean("notifToggle", true)) {
                    sendAlert(waypoint);
                }
            }
        }
    }

    private void sendAlert(Waypoint waypoint)
    {
        int id = 0;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mapsActivity);

        mBuilder.setContentTitle(waypoint.getTitle() + " at " + waypoint.getAddress());
        mBuilder.setContentText(waypoint.getInfo());

        Intent resultIntent = new Intent(mapsActivity, MapsActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mapsActivity);
        stackBuilder.addParentStack(MapsActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) mapsActivity.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(id, mBuilder.build());

        waypoint.updateLastNotif();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) { }

    public void onProviderEnabled(String provider) { }

    public void onProviderDisabled(String provider) { }

}
