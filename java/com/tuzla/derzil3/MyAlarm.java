package com.tuzla.derzil3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.VIBRATOR_SERVICE;
import static com.tuzla.derzil3.globalDegerler.dismiss_All;
import static com.tuzla.derzil3.globalDegerler.dismiss_Next;

public class MyAlarm extends BroadcastReceiver {
    SharedPreferences pref;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        assert extras != null;
        String gelenBilgi = extras.getString("gunlukGuncelleme");
        if (gelenBilgi != null && gelenBilgi.equals("1")) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            Log.e("alarm BİTT", "Zaman: " + formatter.format(date));
            return;
        }

        pref = context.getApplicationContext().
                getSharedPreferences("derzilPref", Context.MODE_PRIVATE);

        if (!pref.getBoolean("hizmetDurumu", false)) {
            return;
        }
        if (dismiss_Next) {
            dismiss_Next = false;
            return;
        }
        if (dismiss_All) {
            return;
        }

        if (pref.getBoolean("titresim", false)) {
            //titreşim desteği
            Vibrator v = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
            long[] pattern = {0, 200, 50, 200, 50};

            if (v.hasVibrator())
                v.vibrate(pattern, -1);
        }

        if (pref.getBoolean("ses", true)) {
            //ses desteği

            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (pref.contains("ringtone")) {
                alarmUri = Uri.parse(pref.getString("ringtone", alarmUri.toString()));
            }
            Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
            ringtone.play();

            Log.e("alarm Tipi1", pref.getString("ringtone", alarmUri.toString()));
        }

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        Log.e("alarm BİTTİ", "Zaman: " + formatter.format(date));
    }
}