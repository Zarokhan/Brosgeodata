package se.mah.ae5929.brosgeodata.main;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import se.mah.ae5929.brosgeodata.R;
import se.mah.ae5929.brosgeodata.fragments.MainFragment;
import se.mah.ae5929.brosgeodata.utility.BaseController;
import se.mah.ae5929.brosgeodata.service.TCPConnectionService;

/**
 * Created by Robin on 2016-10-04.
 */
public class MainController extends BaseController<MainActivity> {

    private static final String TAG = MainController.class.getName();
    private static final LatLng GÄDDAN = new LatLng(55.6075872, 12.9891138);

    private MainFragment mMainFrag;
    private GoogleMap mMap;

    private TCPConnectionService mService;
    private boolean mBound = false;

    private int mID;
    private String mUser;
    private String mGroup;
    private LatLng mLocation;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private MainListener mMainThread;

    public MainController(MainActivity activity) {
        super(activity);
    }

    @Override
    protected void initializeController() {
        Intent intent = getActivity().getIntent();
        mID = intent.getIntExtra("aliasid", -1);
        mUser = intent.getStringExtra("alias");
        mGroup = intent.getStringExtra("group");

        mMainFrag = new MainFragment();
        mMainFrag.setController(this);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocList();

        mMainThread = new MainListener();
        mMainThread.start();

        getActivity().addFragment(mMainFrag, "MAIN");

        Log.d(TAG, "initializeController");
    }

    // Runs when the map is ready
    public void onMapReady(GoogleMap map) {
        this.mMap = map;

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mMap.setMyLocationEnabled(true);

        Location location = getCurrentLocation();
        if(location != null)
            mLocation = new LatLng(location.getLatitude(), location.getLongitude());
        else
            mLocation = GÄDDAN;

        Resources res = getActivity().getResources();
        float zoom = 9.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, zoom));
        mMap.addMarker(new MarkerOptions().position(mLocation).title(res.getString(R.string.marker_my_position)));
        Log.d(TAG, "onMapReady");
    }

    private Location getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return null;
        }
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        return locationManager.getLastKnownLocation(provider);
    }


    // Start point of service
    public void onStart() {
        Intent serviceIntent = new Intent(getActivity(), TCPConnectionService.class);
        getActivity().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "onStart");
    }

    public void onResume() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);

        /*

        JSONObject deregister = new JSONObject();
        JSONObject requestMembers = new JSONObject();
        JSONObject groups = new JSONObject();
        JSONObject position = new JSONObject();

        try {
            // Register

            // deregister
            deregister.put("type", "unregister");
            deregister.put("id", mID);
            // request group members
            requestMembers.put("type", "members");
            requestMembers.put("group", mGroup);
            // current group
            groups.put("type", "groups");
            // set position
            position.put("type", "location");
            position.put("id", mID);
            position.put("longitude", (float)mLocation.longitude);
            position.put("latitude", (float)mLocation.latitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
    }

    public void onPause() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mLocationManager.removeUpdates(mLocationListener);
    }

    // Stops the service
    public void onStop() {
        if(mBound){
            mService.disconnect();
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    // Handling main stuff
    private class MainListener extends Thread {
        private final String TAG = MainListener.class.getName();

        @Override
        public void run() {
            try {
                // Wait if we are not yet connected;
                while(mService == null || !mService.isConnected()){
                    Log.d(TAG, "Waiting for connection");
                    this.sleep(1000);
                }

                Log.d(TAG, "Register user");

                JSONObject register = new JSONObject();
                register.put("type", "register");
                register.put("group", mGroup);
                register.put("member", mUser);

                Log.d(TAG, register.toString());

                mService.send(register.toString());

                String answer = null;
                while(answer == null)
                    answer = mService.receive();

                Log.d(TAG, answer);

                // Register user
                // Retreive ID
                // Register group
                // Get group id
                // Handle group already exist
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "MainListener complete");
        }
    }

    private class LocList implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            mLocation = new LatLng(lat, lon);
            Log.d(TAG, "onLocationChanged Lng=" + lon + " Lat=" + lat);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TCPConnectionService.TCPConnectionBinder binder = (TCPConnectionService.TCPConnectionBinder) service;
            mService = binder.getService();
            mBound = true;
            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            mService = null;
            Log.d(TAG, "onServiceDisconnected");
        }
    };
}
