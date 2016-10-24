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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import se.mah.ae5929.brosgeodata.fragments.MainFragment;
import se.mah.ae5929.brosgeodata.utility.BaseController;
import se.mah.ae5929.brosgeodata.service.TCPConnectionService;
import se.mah.ae5929.brosgeodata.mainutility.MainBuffer;
import se.mah.ae5929.brosgeodata.mainutility.Pair;
import se.mah.ae5929.brosgeodata.mainutility.User;

/**
 * Created by Robin on 2016-10-04.
 */
public class MainController extends BaseController<MainActivity> {

    private static final String TAG = MainController.class.getName();
    private static final LatLng GÄDDAN = new LatLng(55.6075872, 12.9891138);

    private MainFragment mMainFrag;
    private GoogleMap mMap;
    private float zoom = 9.0f;
    private LinkedList<Pair<Marker, User>> mMarkers;

    private TCPConnectionService mService;
    private boolean mBound = false;
    private ServiceConnection mConnection;

    private MainBuffer mBuffer;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private MainListener mMainThread;

    public MainController(MainActivity activity) {
        super(activity);
    }

    /*
    * When fragment loaded
    * */
    @Override
    protected void initializeController() {
        Intent intent = getActivity().getIntent();
        mBuffer = new MainBuffer(intent);

        mMarkers = new LinkedList();

        mMainFrag = new MainFragment();
        mMainFrag.setController(this);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocList();

        getActivity().addFragment(mMainFrag, "MAIN");

        Intent serviceIntent = new Intent(getActivity(), TCPConnectionService.class);
        mConnection = new ServerConn();
        getActivity().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);

        mMainThread = new MainListener();
        mMainThread.start();

        Log.d(TAG, "initializeController");
    }

    /*
    * Map ready for use
    * */
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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoom));
        Marker marker = mMap.addMarker(new MarkerOptions().position(loc).title(mBuffer.getUser().getName() + " - " + mBuffer.getUser().getGroup()));
        mMarkers.add(new Pair<>(marker, mBuffer.getUser()));
        Log.d(TAG, "onMapReady");
    }

    public void zoomIn() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mBuffer.getLatitude(), mBuffer.getLongitude()), zoom));
    }

    /*
    * Update player markers on map
    * */
    private void UpdateMarkers()
    {
        // Clear users
        for(int i = 0; i < mMarkers.size(); ++i)
            mMarkers.get(i).getFirst().remove();
        mMarkers.clear();

        LinkedList<User> temp = mBuffer.getUsers();
        for (int i = 0; i < temp.size(); ++i)
        {
            User usr = temp.get(i);
            LatLng pos = new LatLng(usr.getLocation().getFirst(), usr.getLocation().getSecond());
            Marker marker = mMap.addMarker(new MarkerOptions().position(pos).title(usr.getName() + " - " + usr.getGroup()));
            mMarkers.add(new Pair<>(marker, usr));
        }
    }

    /*
    * Get the current gps location
    * */
    private Location getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return null;
        }
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        return locationManager.getLastKnownLocation(provider);
    }


    /*
    * Activity on start
    * */
    public void onStart() {
        Log.d(TAG, "onStart");
    }

    /*
    * Activity on resume
    * */
    public void onResume() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
    }

    /*
    * Activity on pause
    * */
    public void onPause() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mLocationManager.removeUpdates(mLocationListener);
    }

    /*
    * Activity on stop
    * */
    public void onStop() {
        mMainThread.terminate();
    }

    /*
    * Unbind service
    * Note: Test if it work correctly
    * */
    public void UnbindTCPService() {
        if(mService.isConnected())
            mService.disconnect();
        if(mBound)
        {
            getActivity().unbindService(mConnection);
            mBound = false;
            Log.d(TAG, "Service disconnected & unbound");
        }
    }



    /*
    * Main controller worker thread
    * Handles data from the server
    * */
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

                // Main loop
                while(mRunning)
                {
                    MessageListener();
                    SetPosition();
                    CurrentGroups();
                    sleep(1000);
                }

                DeregisterUser();

                if(mService.isConnected())
                    mService.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "MainListener complete");
        }

        /*
        * Handles server incoming messages.
        * */
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
                            mBuffer.setIDString(id);
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

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.updateNavDrawer(mBuffer.getGroups());
                            }
                        });
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UpdateMarkers();
                            }
                        });
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

        /*
        * Request current groups
        * */
        private void CurrentGroups() throws JSONException {
            JSONObject obj = new JSONObject();
            obj.put("type", "groups");
            mService.send(obj.toString());
        }

        /*
        * Send user location
        * */
        private void SetPosition() throws JSONException {
            JSONObject location = new JSONObject();
            location.put("type", "location");
            location.put("id", mBuffer.getIDString());
            location.put("longitude", String.format("%.6f", mBuffer.getLongitude()).replace(",", "."));
            location.put("latitude", String.format("%.6f", mBuffer.getLatitude()).replace(",", "."));
            mService.send(location.toString());

            Log.d(TAG, "Location sent");
        }

        /*
        * Send registration
        * */
        private void RegisterUser() throws JSONException, InterruptedException {
            Log.d(TAG, "Register user");

            JSONObject register = new JSONObject();
            register.put("type", "register");
            register.put("group", mBuffer.getGroup());
            register.put("member", mBuffer.getUser().getName());

            // Send registration
            mService.send(register.toString());
            Log.d(TAG, "Registration complete");
        }

        /*
        * Deregister user
        * Logout user
        * */
        private void DeregisterUser() throws JSONException {
            // Unregister
            JSONObject unregister = new JSONObject();
            unregister.put("type", "unregister");
            unregister.put("id", mBuffer.getID());
            mService.send(unregister.toString());
            Log.d(TAG, "Unregistered user");
        }

        /*
        * Stop thread
        * */
        public void terminate() {
            mRunning = false;
        }
    }

    /*
    * Location listener
    * */
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

    /*
    * Connection for bound server
    * */
    private class ServerConn implements ServiceConnection {

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
    }
}
