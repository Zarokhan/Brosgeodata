package se.mah.ae5929.brosgeodata.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import se.mah.ae5929.brosgeodata.R;
import se.mah.ae5929.brosgeodata.fragments.LoginFragment;
import se.mah.ae5929.brosgeodata.utility.BaseController;

/**
 * Created by Robin on 2016-10-04.
 */
public class LoginController extends BaseController<LoginActivity> {

    private LoginFragment mLoginFrag;
    private UserLoginTask mLoginTask;

    private String mUsername;
    private String mGroup;

    public LoginController(LoginActivity activity) { super(activity); }

    @Override
    protected void initializeController() {
        mLoginFrag = new LoginFragment();
        mLoginFrag.setController(this);
        getActivity().addFragment(mLoginFrag, "LOGIN");

        confirmGPS();
        confirmNetwork();
    }

    public void confirmNetwork() {
        if(!isOnline(getActivity())){
            Resources res = getActivity().getResources();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle(res.getString(R.string.prompt_title_net));
            alertDialogBuilder.setMessage(res.getString(R.string.prompt_desc_net));
            alertDialogBuilder.setPositiveButton(res.getString(R.string.prompt_positive), new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);

                    getActivity().startActivityForResult(intent, LoginActivity.NAME);
                }
            });
            alertDialogBuilder.setNegativeButton(res.getString(R.string.prompt_negative), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });

            AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();
        }
    }

    private boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr.getActiveNetworkInfo();

        return (info != null && info.isConnected());
    }

    public void confirmGPS(){
        LocationManager service = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!enabled){
            Resources res = getActivity().getResources();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle(res.getString(R.string.prompt_title));
            alertDialogBuilder.setMessage(res.getString(R.string.prompt_desc));
            alertDialogBuilder.setPositiveButton(res.getString(R.string.prompt_positive), new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                    getActivity().startActivityForResult(intent, LoginActivity.NAME);
                }
            });
            alertDialogBuilder.setNegativeButton(res.getString(R.string.prompt_negative), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });

            AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(String alias, String group) {
        this.mUsername = alias;
        this.mGroup = group;

        if (mLoginTask != null) {
            return;
        }

        // Reset errors.
        mLoginFrag.resetErrors();

        mLoginFrag.showProgress(true);
        mLoginTask = new UserLoginTask();
        mLoginTask.execute(alias);
    }

    private void accessGranted(int id) {
        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
        intent.putExtra("aliasid", id);
        intent.putExtra("alias", mUsername);
        intent.putExtra("group", mGroup);
        activity.startActivityForResult(intent, LoginActivity.NAME);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... params) {
            String alias = params[0];
            Log.e("CONNECT", "CONNECTING");

            // CONNECT TO SERVER HERE
            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            return 11; // return ID here
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            mLoginFrag.showProgress(false);
            accessGranted(result);
            mLoginTask = null;
        }
    }
}
