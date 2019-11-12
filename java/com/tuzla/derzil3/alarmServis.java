package com.tuzla.derzil3;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.TypedValue;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.tuzla.derzil3.App.CHANNEL_ID;

public class alarmServis extends Service {
    private static final int notif_id = 1;
    PendingIntent pendingIntent = null;
    MediaPlayer mp;
    SharedPreferences pref;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            Calendar sCalendar = Calendar.getInstance();
            Date now = new Date();
            String dayLongName = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

            //PAZAR 1, pzt 2, sali 3, çars 4, pers 5, cuma 6, cmt 7
            globalDegerler.hizmetBildirimiMesaji = MessageFormat.format("{0} {1}", dayLongName,
                    new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(now));

            bilgileriGuncelle(globalDegerler.hizmetBildirimiMesaji);

            handler.postDelayed(this, 2000); //2000 değil 60*1000 olacak?
        }
    };

    @Override
    public void onCreate() {
        this.startForeground();
        super.onCreate();
    }

    private void startForeground() {
        startForeground(notif_id, getMyActivityNotification(""));
    }

    private Notification getMyActivityNotification(String text) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(text) //üst kalın başlık
                //.setContentText(text)
                .setOnlyAlertOnce(true) //1 kere açılır
                .setOngoing(true)
                .setSmallIcon(R.drawable.bell)
                .setColor(ContextCompat.getColor(this, R.color.arkaplan))
                .setContentIntent(pendingIntent).getNotification();
        return mBuilder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Calendar calendar = Calendar.getInstance();
        //long kalanSaniye = (60 - calendar.get(Calendar.SECOND)) * 1000 - calendar.get(Calendar.MILLISECOND);
        //mesela şu an 40 ise 20 saniye sonra kod başlasın, tam 00 saniye olur ?? olmuyor sanki

//        Date date = new Date();
//        handler.postAtTime(runnable, toWholeMinute(date, 1)); //hemen çalışsın
        handler.postDelayed(runnable, 2000);

        globalDegerler.hizmetBildirimiMesaji = "...";

        bilgileriGuncelle(globalDegerler.hizmetBildirimiMesaji);

        //do heavy work on a background thread

        return START_NOT_STICKY;
    }

    public void bilgileriGuncelle(String mesaj) {
        pref = getApplicationContext().getSharedPreferences("derzilPref", Context.MODE_PRIVATE);

        if (pref.getBoolean("titresim", false)) {
            //titreşim desteği
            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = {0, 100, 50}; //, 200, 50, 200, 50, 200, 50 //3 kere zzıt

            if (v.hasVibrator())
                v.vibrate(pattern, -1);
        }

        if (pref.getBoolean("ses", true)) {
            //ses desteği
            mp = MediaPlayer.create(this, R.raw.zil2);
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
            mp.start();
        }

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.zaman_app_widget);

        view.setInt(R.id.rootBG, "setBackgroundColor",
                ColorUtils.setAlphaComponent(Color.argb(
                        pref.getInt("alpha", 50)
                        , pref.getInt("red", 61)
                        , pref.getInt("green", 90)
                        , pref.getInt("blue", 254)),
                        pref.getInt("alpha", 50)));


        switch (pref.getInt("fontSize", 2)) {
            case 1:
                view.setTextViewTextSize(R.id.teneffustextView, TypedValue.COMPLEX_UNIT_DIP, 12);
                break;
            case 2:
                view.setTextViewTextSize(R.id.teneffustextView, TypedValue.COMPLEX_UNIT_DIP, 14);
                break;
            case 3:
                view.setTextViewTextSize(R.id.teneffustextView, TypedValue.COMPLEX_UNIT_DIP, 18);
                break;
            case 4:
                view.setTextViewTextSize(R.id.teneffustextView, TypedValue.COMPLEX_UNIT_DIP, 20);
                break;
        }

        view.setTextViewText(R.id.teneffustextView, mesaj);

        ComponentName theWidget = new ComponentName(this, zamanAppWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(theWidget, view);

        startForeground(notif_id, getMyActivityNotification(mesaj));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); //stop timer
        stopSelf(); //servisi durdur
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}