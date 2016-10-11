package se.mah.ae5929.brosgeodata.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import se.mah.ae5929.brosgeodata.R;
import se.mah.ae5929.brosgeodata.main.MainController;
import se.mah.ae5929.brosgeodata.utility.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends BaseFragment<MainController> implements OnMapReadyCallback {

    private GoogleMap mMap;

    public MainFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initFragmentComponents(view);
        return view;
    }

    @Override
    protected void initFragmentComponents(View view) {
        key = "mainfragment";

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map); //(SupportMapFragment) getFragmentManager().findFragmentById(R.id.map); //
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        controller.onMapReady(mMap);
    }
}
