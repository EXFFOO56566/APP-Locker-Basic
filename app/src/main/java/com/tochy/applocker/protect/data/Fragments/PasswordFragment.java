package com.tochy.applocker.protect.data.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tochy.applocker.protect.data.PasswordSetActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.takwolf.android.lock9.Lock9View;

import com.tochy.applocker.protect.data.AppLockConstants;
import com.tochy.applocker.protect.data.Custom.FlatButton;

import com.tochy.applocker.protect.data.R;

/**
 * Created by tochy.
 */
public class PasswordFragment extends Fragment {
    Lock9View lock9View;
    FlatButton confirmButton, retryButton;
    TextView textView, textView2;
    boolean isEnteringFirstTime = true;
    boolean isEnteringSecondTime = false;
    String enteredPassword;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    AdView mAdView;

    public PasswordFragment() {
        super();
    }

    public static PasswordFragment newInstance() {
        PasswordFragment f = new PasswordFragment();
        return (f);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_password_set, container, false);

        lock9View = (Lock9View) v.findViewById(R.id.lock_9_view);
        confirmButton = (FlatButton) v.findViewById(R.id.confirmButton);
        textView = (TextView) v.findViewById(R.id.textView);
        textView2 = (TextView) v.findViewById(R.id.textView2);
        final boolean instancePwdActivty = false;
        sharedPreferences = getActivity().getSharedPreferences(AppLockConstants.MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        textView2.setText("Enter your Recent Pattern");

        mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        lock9View.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {


                if (password.matches(sharedPreferences.getString(AppLockConstants.PASSWORD, ""))) {

                    //sharedPreferences.getString(AppLockConstants.PASSWORD, "").matches(password)
                    // sharedPreferences.edit().putBoolean("dont_show_recovery_second_time",true).apply();
                    textView2.setVisibility(View.VISIBLE); //  enter your recent pattern

                    Intent i = new Intent(getActivity(), PasswordSetActivity.class);
                    getActivity().startActivity(i);
                    textView2.setText("Re-Draw your Pattern");

                    sharedPreferences.edit().remove("dnt_show_passwrd_recovery").apply();
                    getActivity().finish();

                } else {
                    textView2.setVisibility(View.VISIBLE);
                    textView2.setText("Password is incorrect - ReEnter");
                }
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
    }
}
