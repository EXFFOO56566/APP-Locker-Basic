package com.tochy.applocker.protect.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.takwolf.android.lock9.Lock9View;

import com.tochy.applocker.protect.data.Custom.FlatButton;
import com.tochy.applocker.protect.data.R;

import com.tochy.applocker.protect.data.Utils.AppLockLogEvents;


public class PasswordSetActivity extends AppCompatActivity {
    Lock9View lock9View;
    FlatButton confirmButton, retryButton;
    TextView textView, textView2;
    boolean isEnteringFirstTime = true;
    boolean isEnteringSecondTime = false;
    String enteredPassword;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    boolean instancePwdActivty = false;
    AdView mAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_password_set);
        lock9View = (Lock9View) findViewById(R.id.lock_9_view);
        textView2 = (TextView) findViewById(R.id.textView2);

        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();
//        textView2.setVisibility(View.VISIBLE);
//        textView2.setText("Draw Pattern");
//        textView.setVisibility(View.INVISIBLE);
        //Google Analytics
        textView2.setText("Draw Pattern");
        Tracker t = ((AppLockApplication) getApplication()).getTracker(AppLockApplication.TrackerName.APP_TRACKER);
        t.setScreenName(AppLockConstants.FIRST_TIME_PASSWORD_SET_SCREEN);
        t.send(new HitBuilders.AppViewBuilder().build());

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        lock9View.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {

                if (isEnteringFirstTime) {

                    enteredPassword = password;
                    isEnteringFirstTime = false;
                    isEnteringSecondTime = true;
                    textView2.setText("Re-Draw Pattern");

                } else if (isEnteringSecondTime) {

                    if (enteredPassword.matches(password)) {

                        boolean first_time_app_install = sharedPreferences.getBoolean(AppLockConstants.FIRST_TIME_APP_INSTALL, false);

                        if (first_time_app_install) {

                            if (sharedPreferences.getBoolean("dnt_show_passwrd_recovery", true)) {

                                Intent i = new Intent(PasswordSetActivity.this, MainActivity.class);
                                startActivity(i);
                                editor.putString(AppLockConstants.PASSWORD, enteredPassword);
                                editor.commit();
                                finish();
                            }

                            editor.putString(AppLockConstants.PASSWORD, enteredPassword);
                            editor.commit();

                            Intent i = new Intent(PasswordSetActivity.this, PasswordRecoverSetActivity.class);
                            startActivity(i);
                            finish();
                            sharedPreferences.edit().remove(AppLockConstants.FIRST_TIME_APP_INSTALL).commit();

                        } else {

                            Intent i = new Intent(PasswordSetActivity.this, MainActivity.class);
                            startActivity(i);
                            editor.putString(AppLockConstants.PASSWORD, enteredPassword);
                            editor.commit();
                            finish();
                            AppLockLogEvents.logEvents(AppLockConstants.FIRST_TIME_PASSWORD_SET_SCREEN, "Confirm Password", "confirm_password", "");
                        }
                    } else {

                        textView2.setText("Draw Pattern");
                        Toast.makeText(getApplicationContext(), "Both Pattern did not match - Try again", Toast.LENGTH_SHORT).show();
                        isEnteringFirstTime = true;
                        isEnteringSecondTime = false;
                        //   textView.setVisibility(View.VISIBLE);
                        //                 retryButton.setEnabled(false);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(context).reportActivityStart(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(context).reportActivityStop(this);
        super.onStop();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Intent i = new Intent(this, PasswordActivity.class);
        startActivity(i);

        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
