package se.mah.ae5929.brosgeodata.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import se.mah.ae5929.brosgeodata.R;
import se.mah.ae5929.brosgeodata.controllers.LoginController;
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_CANCELED && requestCode==NAME) {
            controller.confirmGPS();
        }
    }
}
