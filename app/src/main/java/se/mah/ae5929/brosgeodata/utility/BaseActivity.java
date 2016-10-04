package se.mah.ae5929.brosgeodata.utility;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Robin on 2016-10-04.
 */

public class BaseActivity <MyController extends BaseController> extends AppCompatActivity {

    private static final int SIZE = 10;
    private Fragment[] activeFragments = new Fragment[SIZE];

    private @IdRes int containerViewId = 0;
    protected MyController controller;

    public void setController(MyController controller) {this.controller = controller;}
    public void setContainerViewId(@IdRes int res)
    {
        this.containerViewId = res;
    }

    // Adds fragment to overview fragment container
    public void addFragment(Fragment frag, String tag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(containerViewId, frag, tag);
        ft.commit();
    }

    // Adds fragments to overview fragment container
    public void addFragments(Fragment[] frag, String tag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        for(int i = 0; i < frag.length; ++i)
            ft.replace(containerViewId, frag[1], tag);

        ft.commit();
    }

    // Removes fragment
    public void removeFragment(Fragment frag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.remove(frag);
        ft.commit();
    }

    // Clears fragments in activity
    public void clearFragments(){
        for(int i = 0; i < activeFragments.length; ++i){
            removeFragment(activeFragments[i]);
        }
    }
}
