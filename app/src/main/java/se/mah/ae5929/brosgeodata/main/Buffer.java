package se.mah.ae5929.brosgeodata.main;

import java.util.LinkedList;

/**
 * Created by Zarokhan on 2016-10-09.
 */
public class Buffer<Type> {
    private LinkedList<Type> list = new LinkedList<Type>();

    public synchronized void put(Type obj) {
        list.add(obj);
    }

    public synchronized Type get() throws InterruptedException {
        return list.pollFirst();
    }
}
