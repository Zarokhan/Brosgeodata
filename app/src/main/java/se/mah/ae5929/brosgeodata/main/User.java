package se.mah.ae5929.brosgeodata.main;

/**
 * Created by Zarokhan on 2016-10-21.
 */
public class User
{
    public long mID = -1;
    public String mUser;
    public String mGroup;
    public Pair<Double, Double> mLocation;

    public User(long ID, String user, String group)
    {
        mID = ID;
        mUser = user;
        mGroup = group;
        mLocation = new Pair<Double, Double>();
    }

    public User(String group, String name, Double lat, Double lon)
    {
        mGroup = group;
        mUser = name;
        mLocation = new Pair<Double, Double>();
        mLocation.setFirst(lat);
        mLocation.setSecond(lon);
    }
}
