package se.mah.ae5929.brosgeodata.main;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.Locale;

import se.mah.ae5929.brosgeodata.R;
import se.mah.ae5929.brosgeodata.utility.BaseActivity;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity<LoginController> {
    public static final int NAME = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setContainerViewId(R.id.login_container);
        LoginController c = new LoginController(this);
        setController(c);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_CANCELED && requestCode==NAME) {
            controller.confirmGPS();
            controller.confirmNetwork();
        }
        else if (resultCode == Activity.RESULT_OK && requestCode==NAME) {
            String action = data.getStringExtra("action");
            if(action.equals("exit")) {
                finish();
            } else if (action.equals("reregister")) {
                String group = data.getStringExtra("group");
                controller.setGroup(group);
            }
        }
    }
}

