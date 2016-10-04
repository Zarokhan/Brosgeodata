package se.mah.ae5929.brosgeodata.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import se.mah.ae5929.brosgeodata.LoginController;
import se.mah.ae5929.brosgeodata.R;
import se.mah.ae5929.brosgeodata.utility.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment<LoginController> {

    private AutoCompleteTextView mAliasView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mAliasSignInButton;

    public LoginFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initFragmentComponents(view);
        return view;
    }

    @Override
    protected void initFragmentComponents(View view) {
        // Set up the login form.
        mAliasView = (AutoCompleteTextView) view.findViewById(R.id.alias);

        mAliasSignInButton = (Button) view.findViewById(R.id.sign_in_button);
        mAliasSignInButton.setOnClickListener(new SignInListener());

        mLoginFormView = view.findViewById(R.id.login_form);
        mProgressView = view.findViewById(R.id.progressBar);

        key = "loginfragment";
    }

    private class SignInListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(isAliasValid())
                controller.attemptLogin(mAliasView.getText().toString());
        }
    }

    private boolean isAliasValid() {
        return mAliasView.getText().toString().length() > 3;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(key, Activity.MODE_PRIVATE);
        String alias = sharedPreferences.getString("alias", "");
        mAliasView.setText(alias);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(key, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String alias = mAliasView.getText().toString();
        editor.putString("alias", alias);
        editor.apply();
    }

}
