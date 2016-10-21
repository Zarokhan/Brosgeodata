package se.mah.ae5929.brosgeodata.main;

import android.content.Intent;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Zarokhan on 2016-10-21.
 */
public class MainBuffer {

    private User mMe;
    private Queue<String> mGroups;
    private LinkedList<User> mUsers;

    public MainBuffer(Intent intent)
    {
        mMe = new User(-1, intent.getStringExtra("alias"), intent.getStringExtra("group"));
        mGroups = new LinkedList<String>();
        mUsers = new LinkedList<User>();
    }

    /*
     * Setters & Utility
     */

    public synchronized void addGroup(String group) {
        mGroups.add(group);
    }

    public synchronized void addUser(User user) {
        mUsers.add(user);
    }

    public synchronized void clearGroups() {
        mGroups.clear();
    }

    public synchronized void setLocation(double lat, double lon) {
        mMe.mLocation.setFirst(lat);
        mMe.mLocation.setSecond(lon);
    }

    public synchronized void setID(long ID) {
        mMe.mID = ID;
    }

    /*
     * Getters
     */

    public synchronized double getLatitude(){
        return mMe.mLocation.getFirst();
    }

    public synchronized double getLongitude() {
        return mMe.mLocation.getSecond();
    }

    public synchronized double getID() {
        return mMe.mID;
    }

    public synchronized String getGroup() {
        return mMe.mGroup;
    }

    public synchronized String getUser() {
        return mMe.mUser;
    }
}
