package se.mah.ae5929.brosgeodata.main;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Zarokhan on 2016-10-17.
 */
public class MainBuffer {

    private int mID;
    private String mUser;
    private String mGroup;

    public synchronized LatLng getmLocation() {
        return mLocation;
    }

    public synchronized void setmLocation(LatLng mLocation) {
        this.mLocation = mLocation;
    }

    public synchronized int getmID() {
        return mID;
    }

    public synchronized void setmID(int mID) {
        this.mID = mID;
    }

    public synchronized String getmUser() {
        return mUser;
    }

    public synchronized void setmUser(String mUser) {
        this.mUser = mUser;
    }

    public synchronized String getmGroup() {
        return mGroup;
    }

    public synchronized void setmGroup(String mGroup) {
        this.mGroup = mGroup;
    }

    private LatLng mLocation;



}
