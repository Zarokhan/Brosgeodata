package se.mah.ae5929.brosgeodata.main;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import se.mah.ae5929.brosgeodata.fragments.MainFragment;
import se.mah.ae5929.brosgeodata.utility.BaseController;
import se.mah.ae5929.brosgeodata.service.TCPConnectionService;

/**
 * Created by Robin on 2016-10-04.
 */
public class MainController extends BaseController<MainActivity> {

    private static final String TAG = MainController.class.getName();

    private MainFragment mMainFrag;
    private GoogleMap mMap;

    private TCPConnectionService mService;
    private boolean mBound = false;

    //private Map<Marker, CustomMarker>

    private int mID;
    private String mAlias;
    private String mGroup;
    private LatLng mLocation;

    public MainController(MainActivity activity) { super(activity); }

    @Override
    protected void initializeController() {
        Intent intent = getActivity().getIntent();
        mID = intent.getIntExtra("aliasid", -1);
        mAlias = intent.getStringExtra("alias");

        mMainFrag = new MainFragment();
        mMainFrag.setController(this);

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
        mLocation = new LatLng(location.getLatitude(), location.getLongitude());

        float zoom = 9.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, zoom));
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
        /*
        JSONObject register = new JSONObject();
        JSONObject deregister = new JSONObject();
        JSONObject requestMembers = new JSONObject();
        JSONObject groups = new JSONObject();
        JSONObject position = new JSONObject();

        try {
            // Register
            register.put("type", "register");
            register.put("group", mGroup);
            register.put("member", mAlias);
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

        if(mBound) {
            //mService.send();
        }
    }

    // Stops the service
    public void onStop() {
        if(mBound){
            mService.disconnect();
            getActivity().unbindService(mConnection);
            mBound = false;
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
