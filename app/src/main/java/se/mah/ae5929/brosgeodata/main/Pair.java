package se.mah.ae5929.brosgeodata.main;

/**
 * Created by Zarokhan on 2016-10-21.
 */
public class Pair <first, second> {
    private first first;
    private second second;

    public Pair() {}

    public Pair(first f, second s) {
        this.first = f;
        this.second = s;
    }

    public void setFirst(first f) { this.first = f; }
    public void setSecond(second s) { second = s; }

    public first getFirst() { return first; }
    public second getSecond() { return second; }

    @Override
    public int hashCode() { return first.hashCode() ^ second.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair pairo = (Pair) o;
        return this.first.equals(pairo.getFirst()) &&
                this.second.equals(pairo.getSecond());
    }

}
