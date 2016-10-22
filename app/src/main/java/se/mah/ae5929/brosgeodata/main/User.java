package se.mah.ae5929.brosgeodata.main;

/**
 * Created by Zarokhan on 2016-10-21.
 */
public class User
{
    private long ID = -1;
    private String IDString;
    private String name;
    private String group;
    private Pair<Double, Double> location;

    public User(long ID, String name, String group)
    {
        this.ID = ID;
        this.name = name;
        this.group = group;
        location = new Pair<Double, Double>();
    }

    public User(String group, String name, Double lat, Double lon)
    {
        this.group = group;
        this.name = name;
        location = new Pair<Double, Double>();
        location.setFirst(lat);
        location.setSecond(lon);
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getIDString() {
        return IDString;
    }

    public void setIDString(String IDString) {
        this.IDString = IDString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Pair<Double, Double> getLocation() {
        return location;
    }

    public void setLocation(Pair<Double, Double> location) {
        this.location = location;
    }
}
