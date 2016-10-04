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
}
