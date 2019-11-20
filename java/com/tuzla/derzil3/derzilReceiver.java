package com.tuzla.derzil3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class derzilReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            //cihaz açılınca servisi çalıştırır
            SharedPreferences pref = context.getApplicationContext().getSharedPreferences("derzilPref", 0);
            if (!pref.contains("hizmetDurumu")) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("hizmetDurumu", false);
                editor.apply();
            }
            if (pref.getBoolean("hizmetDurumu", false)) {
                Intent myIntent = new Intent(context, alarmServis.class);
                ContextCompat.startForegroundService(context, myIntent);
                Toast.makeText(context, context.getResources().getString(R.string.arkaplanHizmetiOk), Toast.LENGTH_SHORT).show();
            } else {
                //bu hiç görülmüyor
                Toast.makeText(context, context.getResources().getString(R.string.arkaplanHizmetiNo), Toast.LENGTH_SHORT).show();
            }
        }
    }
}