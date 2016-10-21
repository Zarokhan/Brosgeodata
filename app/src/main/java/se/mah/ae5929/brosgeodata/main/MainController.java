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

import org.json.JSONArray;
import org.json.JSONException;
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

    private MainBuffer mBuffer;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private MainListener mMainThread;

    public MainController(MainActivity activity) {
        super(activity);
    }

    @Override
    protected void initializeController() {
        Intent intent = getActivity().getIntent();
        mBuffer = new MainBuffer(intent);

        mMainFrag = new MainFragment();
        mMainFrag.setController(this);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocList();

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
            mBuffer.setLocation(location.getLatitude(), location.getLongitude());
        else
            mBuffer.setLocation(GÄDDAN.latitude, GÄDDAN.longitude);

        LatLng loc = new LatLng(mBuffer.getLatitude(), mBuffer.getLongitude());
        Resources res = getActivity().getResources();
        float zoom = 9.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoom));
        mMap.addMarker(new MarkerOptions().position(loc).title(res.getString(R.string.marker_my_position)));
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

        mMainThread = new MainListener();
        mMainThread.start();

        Log.d(TAG, "onStart");
    }

    public void onResume() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
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
            mMainThread.terminate();
        }
    }

    // Handling main stuff
    private class MainListener extends Thread {
        private final String TAG = MainListener.class.getName();
        private boolean mRunning;

        @Override
        public void run() {
            mRunning = true;
            try {
                // Wait if we are not yet connected;
                while(mService == null || !mService.isConnected()){
                    Log.d(TAG, "Waiting for connection");
                    sleep(1000);
                }

                RegisterUser();
                CurrentGroups();

                // Main loop
                while(mRunning)
                {
                    //UpdateGroups();
                    MessageListener();
                    SetPosition();
                    sleep(1000);
                }

                DeregisterUser();
                UnbindTCPService();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "MainListener complete");
        }

        private void MessageListener() throws InterruptedException {
            String answer = null;
            while(answer == null || answer.isEmpty())
            {
                answer = mService.receive();
                try {
                    JSONObject jsonobj = new JSONObject(answer);
                    String type = jsonobj.getString("type");

                    if(type.equals("register"))
                    {
                        String group = jsonobj.getString("group");
                        if(mBuffer.getGroup().equals(group))
                        {
                            String id = (String)jsonobj.get("id");
                            Long parsedID = Long.parseLong(id.split(",")[2]);
                            mBuffer.setID(parsedID);
                        }
                    }
                    else if(type.equals("groups"))
                    {
                        mBuffer.clearGroups();
                        JSONArray jsonArray = new JSONArray(jsonobj.getString("groups"));
                        for(int i = 0; i < jsonArray.length(); ++i)
                        {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String group = obj.getString("group");
                            mBuffer.addGroup(group);
                        }
                    }
                    else if(type.equals("locations"))
                    {
                        String group = jsonobj.getString("group");
                        JSONArray jsonArray = new JSONArray(jsonobj.getString("location"));
                        for(int i = 0; i < jsonArray.length(); ++i)
                        {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String name = obj.getString("member");
                            double lon = Double.parseDouble(obj.getString("longitude"));
                            double lat = Double.parseDouble(obj.getString("latitude"));
                            mBuffer.addUser(new User(group, name, lat, lon));
                        }
                    }
                    else if (type.equals("exception"))
                    {
                        String msg = jsonobj.getString("message");
                        Log.d(TAG, "Exception: " + msg);
                    }
                    else
                    {
                        answer = null;
                    }
                } catch (Exception e) {
                    answer = null;
                }
            }
        }

        private void CurrentGroups() throws JSONException {
            JSONObject obj = new JSONObject();
            obj.put("type", "groups");
            mService.send(obj.toString());
        }

        private void SetPosition() throws JSONException {
            JSONObject location = new JSONObject();
            location.put("type", "location");
            location.put("id", mBuffer.getID());
            location.put("longitude", "" + mBuffer.getLongitude());
            location.put("latitude", "" + mBuffer.getLatitude());
            mService.send(location.toString());

            Log.d(TAG, "Location sent");
        }

        private void RegisterUser() throws JSONException, InterruptedException {
            Log.d(TAG, "Register user");

            JSONObject register = new JSONObject();
            register.put("type", "register");
            register.put("group", mBuffer.getGroup());
            register.put("member", mBuffer.getUser());

            // Send registration
            mService.send(register.toString());
            Log.d(TAG, "Registration complete");
        }

        private void UnbindTCPService() throws InterruptedException {
            // disconnect remove service
            mService.disconnect();
            while(mService.isConnected())
                wait();
            getActivity().unbindService(mConnection);
            mBound = false;
            Log.d(TAG, "Service disconnected & unbound");
        }

        private void DeregisterUser() throws JSONException {
            // Unregister
            JSONObject unregister = new JSONObject();
            unregister.put("type", "unregister");
            unregister.put("id", mBuffer.getID());
            mService.send(unregister.toString());
            Log.d(TAG, "Unregistered user");
        }

        /*
        private void UpdateGroups() throws JSONException, InterruptedException {
            mBuffer.clearGroups();

            // Get all groups
            JSONObject getgroups = new JSONObject();
            getgroups.put("type", "groups");
            mService.send(getgroups.toString());
            String answer = null;

            while(answer == null || answer.isEmpty())
            {
                answer = mService.receive();
                try {
                    JSONObject jsonobj = new JSONObject(answer);
                    String group = jsonobj.getString("group");

                    if(jsonobj.getString("type").equals("locations"))
                    {
                        JSONArray jsonArray = new JSONArray(jsonobj.getString("location"));
                        for (int i = 0; i < jsonArray.length(); ++i)
                        {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String name = obj.getString("member");
                            double lon = Double.parseDouble(obj.getString("longitude"));
                            double lat = Double.parseDouble(obj.getString("latitude"));

                            mBuffer.addUser(new User(group, name, lat, lon));
                        }
                    }
                    else
                        answer = null;
                } catch (Exception e) {
                    answer = null;
                }
            }
            Log.d(TAG, "Fetch all groups");
        }*/

        public void terminate() {
            mRunning = false;
        }
    }

    private class LocList implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            mBuffer.setLocation(lat, lon);
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
