package com.stakoun.geoconnect;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class UberDialogFragment extends DialogFragment
{
    private MapsActivity mapsActivity;
    private double destLat;
    private double destLng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b)
    {
        Window window = getDialog().getWindow();

        window.setGravity(Gravity.TOP|Gravity.LEFT);

        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 300;
        params.y = 50;
        window.setAttributes(params);

        return getView();
    }

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
                            String uri = "uber://?action=setPickup&pickup=my_location&dropoff[latitude]=" + Double.toString(destLat) + "&dropoff[longitude]=" + Double.toString(destLng);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(uri));
                            startActivity(intent);
                        } catch (PackageManager.NameNotFoundException e) {
                            String url = "https://m.uber.com/sign-up";
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

    public void setDestination(double lat, double lng) {
        destLat = lat;
        destLng = lng;
    }

}
