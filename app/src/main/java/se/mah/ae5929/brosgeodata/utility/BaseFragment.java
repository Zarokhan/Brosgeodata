package se.mah.ae5929.brosgeodata.utility;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Robin on 2016-10-04.
 */
public abstract class BaseFragment<MyController extends BaseController> extends Fragment {

    protected MyController controller;
    protected String name;
    protected String key;

    public void setController(MyController controller){
        this.controller = controller;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //getActivity().setTitle(name);
    }

    protected abstract void initFragmentComponents(View view);

    public String getName() {return this.name;}
    protected MyController getController(){
        return controller;
    }
}
