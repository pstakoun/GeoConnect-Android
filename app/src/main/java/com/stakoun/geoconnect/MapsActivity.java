package com.stakoun.geoconnect;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity
{
    private MapsActivity mapsActivity;
    private GoogleMap mMap;
    private Waypoint[] waypoints;
    private ImageView settingsImageView;
    private WaypointListener waypointListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        addSettingsIcon();
        mapsActivity = this;
        waypointListener = new WaypointListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setUpMapIfNeeded();
        new GetWaypointsTask().execute();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 10, waypointListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 10, waypointListener);
        } catch (SecurityException e) {
            Log.e("requestLocationUpdates", e.getMessage());
        }
    }

    public void openSettings(View v)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void setUpMapIfNeeded()
    {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap()
    {
        mMap.setMyLocationEnabled(true);

        final LocationManager locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        final Criteria criteria = new Criteria();

        ContextCompat.checkSelfPermission(this, "ACCESS_FINE_LOCATION");
        Location loc = locMan.getLastKnownLocation(locMan.getBestProvider(criteria, false));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 10f));

        /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                          @Override
                                          public boolean onMarkerClick(Marker marker) {
                                              UberDialogFragment uberDialogFragment = new UberDialogFragment();
                                              uberDialogFragment.setMapsActivity(mapsActivity);
                                              uberDialogFragment.setDestination(marker.getPosition().latitude, marker.getPosition().longitude);
                                              uberDialogFragment.show(getSupportFragmentManager(), "UberDialog");
                                              return false;
                                          }
                                      }
        );*/
    }

    private void addSettingsIcon()
    {
        settingsImageView = new ImageView(this);
        settingsImageView.setImageResource(R.drawable.settings);
    }

    private void setWaypoints(Waypoint[] waypoints)
    {
        this.waypoints = waypoints;
        new SetWaypointsTask().execute();
    }

    private class GetWaypointsTask extends AsyncTask<Void, Void, Waypoint[]>
    {
        protected Waypoint[] doInBackground(Void... params)
        {
            Waypoint[] points = null;
            try {
                URL url = new URL("https://geoconnect-kshen3778.c9.io/waypoints.php");
                URLConnection urlConnection = url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String json = inputStreamToString(in);
                points = new Gson().fromJson(json, Waypoint[].class);
            } catch (IOException e) {
                Log.e("getWaypointsTask", e.getMessage());
            }
            return points;
        }

        protected void onPostExecute(Waypoint[] waypoints)
        {
            setWaypoints(waypoints);
        }

        private String inputStreamToString(InputStream inputStream) throws IOException
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
            return result;
        }
    }

    private class SetWaypointsTask extends AsyncTask<Void, Void, ArrayList>
    {
        protected ArrayList<MarkerOptions> doInBackground(Void... params)
        {
            ArrayList<MarkerOptions> markerOptionsArrayList = new ArrayList<MarkerOptions>();
            for (Waypoint waypoint : waypoints) {
                Geocoder geocoder = new Geocoder(mapsActivity);
                try {
                    List<Address> list = geocoder.getFromLocationName(waypoint.getAddress(), 1);
                    if (list.size() > 0) {
                        Address address = list.get(0);
                        waypoint.setLatitude(address.getLatitude());
                        waypoint.setLongitude(address.getLongitude());
                    } else {
                        Log.e("Geocoder", "Invalid address!");
                        continue;
                    }
                } catch (IOException e) {
                    Log.e("Geocoder", e.getMessage());
                    continue;
                }
                markerOptionsArrayList.add(new MarkerOptions()
                        .position(new LatLng(waypoint.getLatitude(), waypoint.getLongitude()))
                        .title(waypoint.getTitle())
                        .snippet(waypoint.getInfo()));
            }
            return markerOptionsArrayList;
        }

        protected void onPostExecute(ArrayList markerOptionsArrayList)
        {
            for (MarkerOptions markerOptions : (ArrayList<MarkerOptions>) markerOptionsArrayList) {
                mMap.addMarker(markerOptions);
            }
        }

    }

    public Waypoint[] getWaypoints() {
        return waypoints;
    }

}
