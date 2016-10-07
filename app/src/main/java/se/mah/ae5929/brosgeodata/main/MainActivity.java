package se.mah.ae5929.brosgeodata.main;

import android.os.Bundle;

import se.mah.ae5929.brosgeodata.R;
import se.mah.ae5929.brosgeodata.controllers.MainController;
import se.mah.ae5929.brosgeodata.utility.BaseActivity;

public class MainActivity extends BaseActivity<MainController> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContainerViewId(R.id.main_container);
        MainController c = new MainController(this);
        setController(c);
    }

    @Override
    protected void onStart(){
        super.onStart();
        controller.onStart();
    }

    @Override
    protected void onStop(){
        super.onStop();
        controller.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        controller.onPause();
    }
}
