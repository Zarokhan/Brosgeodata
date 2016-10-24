package se.mah.ae5929.brosgeodata.mainutility;

import android.content.Intent;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Zarokhan on 2016-10-21.
 */
public class MainBuffer {

    private User mMe;
    private LinkedList<String> mGroups;
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
        mMe.setLocation(new Pair<Double, Double>(lat, lon));
    }

    public synchronized void setID(long ID) {
        mMe.setID(ID);
    }

    public synchronized void setIDString(String IDString) {
        mMe.setIDString(IDString);
    }

    public synchronized void setGroup(String gr)
    {
        mMe.setGroup(gr);
    }

    /*
     * Getters
     */

    public synchronized String[] getGroups()
    {
        String[] sarray = new String[mGroups.size()];
        for(int i = 0; i < mGroups.size(); i++)
            sarray[i] = mGroups.get(i);

        return sarray;
    }

    public synchronized LinkedList<User> getUsers() {
        return mUsers;
    }

    public synchronized double getLatitude(){
        return mMe.getLocation().getFirst();
    }

    public synchronized double getLongitude() {
        return mMe.getLocation().getSecond();
    }

    public synchronized long getID() {
        return mMe.getID();
    }

    public synchronized String getIDString() {
        return mMe.getIDString();
    }

    public synchronized String getGroup() {
        return mMe.getGroup();
    }

    public synchronized User getUser() {
        return mMe;
    }
}
