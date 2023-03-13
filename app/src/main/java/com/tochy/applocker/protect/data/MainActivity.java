package com.tochy.applocker.protect.data;

import android.app.Dialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.KeyboardUtil;

import java.util.ArrayList;
import java.util.List;

import com.tochy.applocker.protect.data.Custom.FlatButton;
import com.tochy.applocker.protect.data.appinfo.AppInfo;
import com.tochy.applocker.protect.data.Fragments.AllAppFragment;
import com.tochy.applocker.protect.data.Fragments.HideAppFragment;
import com.tochy.applocker.protect.data.Fragments.PasswordFragment;

import com.tochy.applocker.protect.data.R;

import com.tochy.applocker.protect.data.Utils.AppLockLogEvents;
import com.tochy.applocker.protect.data.Utils.MyUtils;
/**
 * Created by tochy.
 */

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    Context context;
    Dialog dialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    long numOfTimesAppOpened = 0;
    boolean isRated = false;
    AlertDialog alertdialog;
    public DrawerLayout mDrawerLayout;
    public RecyclerView left_drawer;
    public LinearLayoutManager layoutManager;
    List<UsageStats> queryUsageStats;
    boolean firsttimeInstallApp = false;
    InterstitialAd mInterstitialAd;
    //save our header or result
    private Drawer.Result result = null;

    /**
     * get the list of all installed applications in the device
     *
     * @return ArrayList of installed applications or null
     */
    public static List<AppInfo> getListOfInstalledApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<AppInfo> installedApps = new ArrayList();
        List<PackageInfo> apps = packageManager.getInstalledPackages(PackageManager.SIGNATURE_MATCH);
        if (apps != null && !apps.isEmpty()) {

            for (int i = 0; i < apps.size(); i++) {
                PackageInfo p = apps.get(i);
                ApplicationInfo appInfo = null;
                try {
                    appInfo = packageManager.getApplicationInfo(p.packageName, 0);
                    AppInfo app = new AppInfo();
                    app.setName(p.applicationInfo.loadLabel(packageManager).toString());
                    app.setPackageName(p.packageName);
                    app.setVersionName(p.versionName);
                    app.setVersionCode(p.versionCode);
                    app.setIcon(p.applicationInfo.loadIcon(packageManager));

                    installedApps.add(app);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return installedApps;
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        numOfTimesAppOpened = sharedPreferences.getLong(AppLockConstants.NUM_OF_TIMES_APP_OPENED, 0) + 1;
        isRated = sharedPreferences.getBoolean(AppLockConstants.IS_RATED, false);
        editor.putLong(AppLockConstants.NUM_OF_TIMES_APP_OPENED, numOfTimesAppOpened);
        editor.commit();
        mInterstitialAd = new InterstitialAd(this);

        //Google Analytics
        Tracker t = ((AppLockApplication) getApplication()).getTracker(AppLockApplication.TrackerName.APP_TRACKER);
        t.setScreenName(AppLockConstants.MAIN_SCREEN);
        t.send(new HitBuilders.AppViewBuilder().build());

        if (Build.VERSION.SDK_INT > 20) {

            final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0,
                    System.currentTimeMillis());


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Allow Tochy App lock to use Access usage ");
            builder.setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(intent);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            alertdialog = builder.create();
            if (queryUsageStats.isEmpty()) {
                alertdialog.show();
            }
        }

        fragmentManager = getSupportFragmentManager();

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
//        left_drawer = (RecyclerView) findViewById(R.id.left_drawer);
//        left_drawer.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(MainActivity.this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        left_drawer.setLayoutManager(layoutManager);

//
//        final View framemain = findViewById(R.id.rel_layout);
//        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
//                R.string.drawer_close) {
//            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//
//                super.onDrawerSlide(drawerView, slideOffset);
//                float moveFactor = (left_drawer.getWidth() * slideOffset);
//                framemain.setTranslationX(moveFactor);
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//            }
//        };
//        mDrawerLayout.setDrawerListener(new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close));
//        mDrawerLayout.addDrawerListener(toggle);
//        left_drawer.setAdapter(new MainDrawerAdapter());


        //Create the drawer
        result = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDisplayBelowToolbar(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withTextColorRes(R.color.txt_clr).withName("All Applications").withIcon(R.drawable.home_icon),
                        new PrimaryDrawerItem().withTextColorRes(R.color.txt_clr).withName("Locked Applications").withIcon(R.drawable.locked_icon),
                        new PrimaryDrawerItem().withTextColorRes(R.color.txt_clr).withName("Unlocked Applications").withIcon(R.drawable.unlocked_icon),
                        new PrimaryDrawerItem().withTextColorRes(R.color.txt_clr).withName("Change Password").withIcon(R.drawable.change_password_icon),
                        new PrimaryDrawerItem().withTextColorRes(R.color.txt_clr).withName("Allow Access").withIcon(R.drawable.allow_access_icon),
                        new PrimaryDrawerItem().withTextColorRes(R.color.txt_clr).withName("Hide App").withIcon(R.mipmap.ic_launcher)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof Nameable) {

                            if (position == 0) {
                                getSupportActionBar().setTitle("All Applications");
                                Fragment f = AllAppFragment.newInstance(AppLockConstants.ALL_APPS);
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
                                AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Show All Applications Clicked", "show_all_applications_clicked", "");
                            }

                            if (position == 1) {
                                getSupportActionBar().setTitle("Locked Applications");
                                Fragment f = AllAppFragment.newInstance(AppLockConstants.LOCKED);
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
                                AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Show Locked Applications Clicked", "show_locked_applications_clicked", "");
                            }

                            if (position == 2) {
                                getSupportActionBar().setTitle("Unlocked Applications");
                                Fragment f = AllAppFragment.newInstance(AppLockConstants.UNLOCKED);
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
                                AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Show Unlocked Applications Clicked", "show_unLocked_applications_clicked", "");
                            }

                            if (position == 3) {
                                getSupportActionBar().setTitle("Change Password");
                                sharedPreferences.edit().putBoolean("dnt_show_passwrd_recovery", true).apply();
                                Fragment f = PasswordFragment.newInstance();
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
                                AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Password Changed Clicked", "password_changed_clicked", "");
                            }

                            if (position == 4) {
                                final Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "If you have not allowed , allow App Lock so that it can work properly", Toast.LENGTH_LONG).show();
                                AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Allow Access", "allow_access", "");
                                result.setSelection(0);
                            }
                            if (position == 5) {
                                getSupportActionBar().setTitle("Hide App");
                                Fragment f = HideAppFragment.newInstance();
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
                                AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Allow Access", "allow_access", "");

                            }

                        }
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        KeyboardUtil.hideKeyboard(MainActivity.this);
                    }


                    @Override
                    public void onDrawerClosed(View drawerView) {


                    }
                })
                .withFireOnInitialOnClick(true)
                .withSavedInstance(savedInstanceState)
                .build();

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });

        //react on the keyboard
        result.keyboardSupportEnabled(this, true);

        if ((MyUtils.isInternetConnected(getApplicationContext()) && !isRated) && (numOfTimesAppOpened == 5 || numOfTimesAppOpened == 8 || numOfTimesAppOpened == 10 || numOfTimesAppOpened >= 12)) {
            showRateDialog().show();
        }

    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            if (getCurrentFragment() instanceof AllAppFragment) {
                super.onBackPressed();
            } else {
                fragmentManager.popBackStack();
                getSupportActionBar().setTitle("AllAppFragment");
                Fragment f = AllAppFragment.newInstance(AppLockConstants.ALL_APPS);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
                result.setSelection(0);
            }
        }
    }

    /**
     * Returns currentfragment
     *
     * @return
     */
    public Fragment getCurrentFragment() {
        // TODO Auto-generated method stub
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);

        super.onSaveInstanceState(outState);
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
    public void onDestroy() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onDestroy();
    }

    public Dialog showRateDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams WMLP = dialog.getWindow().getAttributes();
        WMLP.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(WMLP);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.popup_rate);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.ratingBar);
        final FlatButton flatButton = (FlatButton) dialog.findViewById(R.id.button);
        final boolean[] canGoToPlayStore = {false};
        final float[] ratingGivenByUser = {0};


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                flatButton.setVisibility(View.VISIBLE);
                editor.putBoolean(AppLockConstants.IS_RATED, true);
                editor.commit();
                ratingGivenByUser[0] = rating;
                if (rating >= 4) {
                    canGoToPlayStore[0] = true;
                    flatButton.setText("Show your love on playstore");
                } else {
                    canGoToPlayStore[0] = false;
                    flatButton.setText("Thanks for your rating");
                }
                AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Rate Given By User", "rate_given_by_user", String.valueOf(rating));
            }
        });

        flatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canGoToPlayStore[0]) {
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.app.locker.protect.data");
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(goToMarket);
                    dialog.cancel();
                    AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Going To Playstore To Rate", "going_to_playstore_to_rate", String.valueOf(ratingGivenByUser[0]));
                } else {
                    dialog.cancel();
                }

            }
        });

        return dialog;
    }
}


