package se.mah.ae5929.brosgeodata.service;

import java.io.Serializable;

/**
 * Created by Zarokhan on 2016-10-09.
 * not in use
 */
public class Expression implements Serializable {
    private static final long serialVersionUID = 1L;
    private int obj1;
    private int obj2;
    private char operation;

    public Expression(int obj1, int obj2, char operation) {
        this.obj1 = obj1;
        this.obj2 = obj2;
        this.operation = operation;
    }

    public int getObj1() {
        return obj1;
    }

    public int getObj2() {
        return obj2;
    }

    public char getOperation() {
        return operation;
    }
}
