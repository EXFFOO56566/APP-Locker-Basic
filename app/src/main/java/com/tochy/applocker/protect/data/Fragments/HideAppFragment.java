package com.tochy.applocker.protect.data.Fragments;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.tochy.applocker.protect.data.SplashActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tochy.applocker.protect.data.R;

/**
 * Created by tochy.
 */
public class HideAppFragment extends Fragment implements View.OnClickListener {

    Switch toggleButton;
    AdView mAdView;

    public static HideAppFragment newInstance() {
        HideAppFragment hideAppFragment = new HideAppFragment();
        return (hideAppFragment);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toggleButton = (Switch) view.findViewById(R.id.id_toggle);
        //     CardView linearLayout = (CardView) view.findViewById(R.id.id_hideapp_layout);

        toggleButton.setOnClickListener(this);
        //     linearLayout.setOnClickListener(this);
        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_hideapp, container, false);
        return v;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.id_toggle:

                hideAppIcon();

                break;
            case R.id.id_hideapp_layout:

                hideAppIcon();

                break;
            default:
                break;
        }
    }

    private void hideAppIcon() {


        if (toggleButton.isChecked()) {

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());

            builder.setMessage("Are you sure you want to hide AppLocker icon from home...?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(getContext(), "You have succesfully hide app from home", Toast.LENGTH_LONG).show();

                    toggleButton.setChecked(true);
// hide App icon
                    PackageManager p = getContext().getPackageManager();
                    ComponentName componentName = new ComponentName(getActivity(), SplashActivity.class);
                    p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    toggleButton.setChecked(false);
                }
            });

            builder.show();

        } else {
            toggleButton.setChecked(false);
        }
    }
}
