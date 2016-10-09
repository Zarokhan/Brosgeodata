package se.mah.ae5929.brosgeodata.controllers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import se.mah.ae5929.brosgeodata.fragments.MainFragment;
import se.mah.ae5929.brosgeodata.main.TCPConnectionService;
import se.mah.ae5929.brosgeodata.main.MainActivity;
import se.mah.ae5929.brosgeodata.utility.BaseController;

/**
 * Created by Robin on 2016-10-04.
 */
public class MainController extends BaseController<MainActivity> {

    private static final String TAG = MainController.class.getName();

    private MainFragment mMainFrag;
    private GoogleMap mMap;

    private TCPConnectionService mService;
    private boolean mConnected = false;
    private boolean mBound = false;

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

        Intent serviceIntent = new Intent(getActivity(), TCPConnectionService.class);
        getActivity().bindService(serviceIntent, mConnection, 0);
        Log.d(TAG, "initializeController");
    }

    // Runs when the map is ready
    public void onMapReady(GoogleMap map) {
        this.mMap = map;

        LatLng malmö = new LatLng(55.606093, 13.000285);

        // Add a marker in Sydney and move the camera
        //mMap.addMarker(new MarkerOptions().position(malmö).title("Marker in Malmö"));
        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(malmö, 9.0f);
        mMap.moveCamera(camera);
        Log.d(TAG, "onMapReady");
    }

    public void onDestroy() {
        if(mBound){
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
