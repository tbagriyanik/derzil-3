package com.tuzla.derzil3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.VIBRATOR_SERVICE;

public class MyAlarm extends BroadcastReceiver {
    MediaPlayer mp;
    SharedPreferences pref;

    @Override
    public void onReceive(Context context, Intent intent) {
        pref = context.getApplicationContext().getSharedPreferences("derzilPref", Context.MODE_PRIVATE);

        if (!pref.getBoolean("hizmetDurumu", false)) {
            return;
        }

        if (pref.getBoolean("titresim", false)) {
            //titreşim desteği
            Vibrator v = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
            long[] pattern = {0, 100, 50}; //, 200, 50, 200, 50, 200, 50 //3 kere zzıt

            if (v.hasVibrator())
                v.vibrate(pattern, -1);
        }

        if (pref.getBoolean("ses", true)) {
            //ses desteği

//            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//            if (alarmUri == null) {
//                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            }
//            Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
//            ringtone.play();
//MP3 seç demektense üstteki gibi olsa iyi, şimdilik çok uzunlar
            mp = MediaPlayer.create(context, R.raw.zil2);
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
            mp.start();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());

        Log.w("alarm ", globalDegerler.gelecekAlarmAdi + " " + formatter.format(date));
    }
}