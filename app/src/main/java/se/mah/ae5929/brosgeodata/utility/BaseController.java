package se.mah.ae5929.brosgeodata.utility;

import android.app.Activity;

/**
 * Created by Robin on 2016-10-04.
 */
public class BaseController<MyActivity extends Activity> {
    protected MyActivity activity;

    public BaseController(MyActivity activity){
        this.activity = activity;
    }

    protected MyActivity getActivity(){
        return activity;
    }
}
