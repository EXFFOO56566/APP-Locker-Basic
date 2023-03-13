package com.tochy.applocker.protect.data.services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tochy.applocker.protect.data.SplashActivity;

/**
 * Created by tochy.
 */
public class HideAppIconReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {


        Log.e("app icon receiver ", "onReceive()+");
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){

            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            Log.e("number ",number+"");
            if (number == null){
               Log.e("Error entering number ", "number is null "+ number+"" );
            }else{
                Bundle bundle = new Bundle();
                bundle.putString("hideAppPass", number);

                String passValue ="#0000";
                if(number.matches(passValue)){

// unhide app icon
                    PackageManager p =context.getPackageManager();
                    ComponentName componentName = new ComponentName(context, SplashActivity.class);
                    p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    Toast.makeText(context,"You have disable hide app from home",Toast.LENGTH_LONG).show();

                }else{
                    System.out.println("password does not match");
                }

            }
        }
    }
}
