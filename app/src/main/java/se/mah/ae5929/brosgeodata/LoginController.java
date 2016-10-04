package se.mah.ae5929.brosgeodata;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;

import se.mah.ae5929.brosgeodata.fragments.LoginFragment;
import se.mah.ae5929.brosgeodata.utility.BaseController;

/**
 * Created by Robin on 2016-10-04.
 */
public class LoginController extends BaseController<LoginActivity> {

    private LoginFragment loginFrag = null;
    private UserLoginTask mAuthTask = null;

    private String alias;

    public LoginController(LoginActivity activity) {
        super(activity);
        initLoginPhase();
    }

    private void initLoginPhase(){
        loginFrag = new LoginFragment();
        loginFrag.setController(this);
        getActivity().addFragment(loginFrag, "LOGIN");
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(String alias) {
        this.alias = alias;

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        loginFrag.resetErrors();

        loginFrag.showProgress(true);
        mAuthTask = new UserLoginTask(alias);
        mAuthTask.execute((Void) null);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mAlias;

        UserLoginTask(String alias) {
            this.mAlias = alias;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
