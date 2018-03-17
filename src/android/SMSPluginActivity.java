package com.rjfun.cordova.sms;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Map;
import java.util.HashMap;
import org.json.JSONObject;

public class SMSPluginActivity extends Activity {
    private static String TAG = "SMSPlugin";

    /*
     * this activity will be started if the user touches a notification that we own. 
     * We send it's data off to the push plugin for processing.
     * If needed, we boot up the main activity to kickstart the application. 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d(TAG, "==> SMSPluginActivity onCreate");
		
		
        if (getIntent().getExtras() != null) {
            String tmp = getIntent().getStringExtra("navCoords");
            Log.d(TAG, "==> SMS RECEIVED");
            JSONObject json = new JSONObject(tmp);
            SMSPlugin.onSMSArrive(json);
        }
		
        finish();

        forceMainActivityReload();
    }

    private void forceMainActivityReload() {
        PackageManager pm = getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());
        startActivity(launchIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
		Log.d(TAG, "==> SMSPluginActivity onResume");
    }
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "==> SMSPluginActivity onStart");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "==> SMSPluginActivity onStop");
	}

}