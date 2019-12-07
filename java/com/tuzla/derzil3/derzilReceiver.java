package com.tuzla.derzil3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
            gunlukAlarmGuncelleme(context);
        }
    }

    private void gunlukAlarmGuncelleme(Context context) {
        Intent myIntent = new Intent(context, MyAlarm.class);
        myIntent.putExtra("gunlukGuncelleme", "1");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(context,
                0,
                myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 30); //saat 00:00:30 da tetikleme
        calendar.set(Calendar.MILLISECOND, 0);

        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        Log.e("alarm BAŞLATT", "Zaman: " + formatter.format(date));

    }
}