package se.mah.ae5929.brosgeodata.main;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Zarokhan on 2016-10-07.
 * My bound service
 * Bound service allows cross communication between service and activity/controller
 * Good because allows for controller to access service public methods
 */
public class BroService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = BroService.class.getName();

    private final IBinder mBro = new BroBinder();

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    //private boolean isRunning = true;

    /* Google api stuff below */

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d(TAG, "onConnected, IsConnected: " + mGoogleApiClient.isConnected() + " Location: " + mLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /* Service override methods & Binder class */

    @Override
    public void onCreate(){
        super.onCreate();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy(){
        mGoogleApiClient.disconnect();
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    public class BroBinder extends Binder {
        public BroService getService(){
            return BroService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBro;
    }

    /* My own methods */

    public Location getLastKnownLocation(){
        return mLocation;
    }
}
