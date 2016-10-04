package se.mah.ae5929.brosgeodata.controllers;

import android.content.Intent;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import se.mah.ae5929.brosgeodata.fragments.MainFragment;
import se.mah.ae5929.brosgeodata.main.MainActivity;
import se.mah.ae5929.brosgeodata.utility.BaseController;

/**
 * Created by Robin on 2016-10-04.
 */
public class MainController extends BaseController<MainActivity> {

    private MainFragment mMainFrag;
    private GoogleMap mMap;

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
    }

    public void onMapReady(GoogleMap map){
        this.mMap = map;
        // Add a marker in Sydney and move the camera
        LatLng malmö = new LatLng(55.606093, 13.000285);
        mMap.addMarker(new MarkerOptions().position(malmö).title("Marker in Malmö"));
        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(malmö, 9.0f);
        mMap.moveCamera(camera);
    }
}
