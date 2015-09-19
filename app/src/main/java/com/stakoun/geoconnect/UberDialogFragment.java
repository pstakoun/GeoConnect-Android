package com.stakoun.geoconnect;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class UberDialogFragment extends DialogFragment
{
    private MapsActivity mapsActivity;
    private double originLat;
    private double originLng;
    private double destLat;
    private double destLng;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.request_uber)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            PackageManager pm = mapsActivity.getPackageManager();
                            pm.getPackageInfo("com.ubercab", PackageManager.GET_ACTIVITIES);
                            String uri = "uber://?action=setPickup&pickup=my_location&client_id=YOUR_CLIENT_ID";
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(uri));
                            startActivity(intent);
                        } catch (PackageManager.NameNotFoundException e) {
                            String url = "https://m.uber.com/sign-up?client_id=YOUR_CLIENT_ID";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }

    public void setMapsActivity(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
    }

    public void setOrigin(double lat, double lng) {
        originLat = lat;
        originLng = lng;
    }

    public void setDestination(double lat, double lng) {
        destLat = lat;
        destLng = lng;
    }

}
