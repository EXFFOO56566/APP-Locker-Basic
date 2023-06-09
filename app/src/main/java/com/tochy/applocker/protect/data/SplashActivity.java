package com.tochy.applocker.protect.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.tochy.applocker.protect.data.R;

import com.tochy.applocker.protect.data.services.AlarmReceiver;
import com.tochy.applocker.protect.data.services.AppCheckServices;

/**
 * Created by tochy.
 */
public class SplashActivity extends AppCompatActivity {


    private static int SPLASH_TIME_OUT = 1000;
    SharedPreferences sharedPreferences;
    Context context;
    private static boolean instancePwdActivty = false;
    private static boolean firsttimeInstallApp = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        /****************************** very important don't miss it *****************************/
        startService(new Intent(SplashActivity.this, AppCheckServices.class));
       // startService(new Intent(SplashActivity.this, UnInstallReceiver.class));

        try {
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 999, alarmIntent, 0);
            int interval = (86400 * 1000) / 4;
            if (manager != null) {
                manager.cancel(pendingIntent);
            }
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /***************************************************************************************/

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setBackgroundColor(getResources().getColor(R.color.splash_bg));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(layoutParams);

        TextView textView = new TextView(this);
        textView.setText(getResources().getString(R.string.app_name));
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTextSize(32);
        textView.setGravity(Gravity.CENTER);
        linearLayout.addView(textView);

        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.bg_splash));
        linearLayout.addView(imageView);

        setContentView(linearLayout);
        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        final boolean isPasswordSet = sharedPreferences.getBoolean(AppLockConstants.IS_PASSWORD_SET, false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPasswordSet) {
                    instancePwdActivty = true;
                    sharedPreferences.edit().putBoolean("instancePwdActivty",instancePwdActivty).commit();
                    Intent i = new Intent(SplashActivity.this, PasswordActivity.class);
                    startActivity(i);
                } else {
                    firsttimeInstallApp = true;
                    sharedPreferences.edit().putBoolean(AppLockConstants.FIRST_TIME_APP_INSTALL,true).commit();
                    Intent i = new Intent(SplashActivity.this, PasswordSetActivity.class);
                    startActivity(i);
                }
                finish();
            }
        }, SPLASH_TIME_OUT);

        //Google Analytics
        Tracker t = ((AppLockApplication) getApplication()).getTracker(AppLockApplication.TrackerName.APP_TRACKER);
        t.setScreenName(AppLockConstants.SPLASH_SCREEN);
        t.send(new HitBuilders.AppViewBuilder().build());

    }

    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(context).reportActivityStart(this);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(context).reportActivityStop(this);
        super.onStop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
