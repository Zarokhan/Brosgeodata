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

import se.mah.ae5929.brosgeodata.fragments.MainFragment;
import se.mah.ae5929.brosgeodata.service.TCPConnectionService;
import se.mah.ae5929.brosgeodata.utility.BaseController;

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
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());

        float zoom = 9.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoom));
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
