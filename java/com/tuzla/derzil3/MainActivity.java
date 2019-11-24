package com.tuzla.derzil3;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.tuzla.database.mDataBase.DBAdapter;
import com.tuzla.database.mDataObject.Ziller;
import com.tuzla.database.swipeActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler();
    Timer timer;
    Switch m1;
    SharedPreferences pref;

    public static final int REQUEST_CODE = 101;
    static DBAdapter db;
    static ArrayList<Ziller> zillers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m1 = findViewById(R.id.switchHizmet);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        pref = getApplicationContext().getSharedPreferences("derzilPref", Context.MODE_PRIVATE);

        //arkaplan hizmeti tercihe göre başlat
        SharedPreferences.Editor editor = pref.edit();
        if (!pref.contains("hizmetDurumu")) {
            editor.putBoolean("hizmetDurumu", false);
            editor.apply();
        }
        if (!pref.contains("red")) {
            editor.putInt("red", 61);
            editor.apply();
        }
        if (!pref.contains("green")) {
            editor.putInt("green", 90);
            editor.apply();
        }
        if (!pref.contains("blue")) {
            editor.putInt("blue", 254);
            editor.apply();
        }
        if (!pref.contains("alpha")) {
            editor.putInt("alpha", 50);
            editor.apply();
        }
        if (!pref.contains("fontSize")) {
            editor.putInt("fontSize", 2);
            editor.apply();
        }
        if (!pref.contains("titresim")) {
            editor.putBoolean("titresim", false);
            editor.apply();
        }
        if (!pref.contains("ses")) {
            editor.putBoolean("ses", true);
            editor.apply();
        }

        if (pref.getBoolean("hizmetDurumu", false)) {
            if (!checkServiceRunning()) startService();
            m1.setTag("ATLA");
            m1.setChecked(true);
            m1.setTag(null);
        } else {
            stopService();
            m1.setTag("ATLA");
            m1.setChecked(false);
            m1.setTag(null);
        }

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        anaProgramdaBilgileriGuncelle();
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 100, 1000);//ana program refresh

        getZiller(); //veritabanından listeyi al
        alarmlariAyarla(); //alarmları oluştur
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        resetAlarm(); //alarmları durdur
    }

    @Override
    protected void onResume() {
        super.onResume();
        getZiller(); //veritabanından listeyi al
        alarmlariAyarla(); //alarmları oluştur
    }

    private static boolean gunKontrol(String gunler) {
        Calendar calendar = Calendar.getInstance();
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        //PAZAR 1, pzt 2, sali 3, çars 4, pers 5, cuma 6, cmt 7
        //bizim vt'de Pzrt,..., Pzr
        if (weekday == 1) return gunler.substring(6, 7).equals("1");
        else if (weekday == 2) return gunler.substring(0, 1).equals("1");
        else if (weekday == 3) return gunler.substring(1, 2).equals("1");
        else if (weekday == 4) return gunler.substring(2, 3).equals("1");
        else if (weekday == 5) return gunler.substring(3, 4).equals("1");
        else if (weekday == 6) return gunler.substring(4, 5).equals("1");
        else if (weekday == 7) return gunler.substring(5, 6).equals("1");
        else return false;
    }

    //GET DATA
    public void getZiller() {
        zillers.clear();

        db = new DBAdapter(this);
        db.openDB();

        Cursor c = db.retrieve();

        int say = 0;

        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String zaman = c.getString(2);
            String gunler = c.getString(3);
            int aktif = c.getInt(4);

            Ziller p = new Ziller();
            p.setId(id);
            p.setName(name);
            p.setZaman(zaman);
            p.setGunler(gunler);
            p.setAktif(aktif);

            //aktif olanları ve hafta günü aynı olanları
            //saat dakika süre bilgileri düzgün olmalı
            //geçmiş saat önemli değil

            int saat = saatGetir(zaman);
            int dakika = dakikaGetir(zaman);

            if (aktif == 1 && gunKontrol(gunler) && saat != -1 && dakika != -1) {
                zillers.add(p);
                say++;

                // Log.e("alarm " + say,  name + " " + saat + ":" + dakika);
            }
        }

        db.closeDB();
    }

    private int dakikaGetir(String zaman) {
        int dakika;

        String[] ilk = zaman.split("-"); //- ile ayrılan süre

        if (ilk.length != 2) return -1;

        String[] ikinci = ilk[0].split(":"); //ilk bölüm saat:dakika

        if (ikinci.length != 2) return -1;

        try {
            dakika = Integer.parseInt(ikinci[1].trim());
        } catch (NumberFormatException e) {
            return -1;
        }

        if (dakika < 0 || dakika > 59)
            return -1;
        else
            return dakika;
    }

    private int saatGetir(String zaman) {
        int saat;

        String[] ilk = zaman.split("-"); //- ile ayrılan süre

        if (ilk.length != 2) return -1;

        String[] ikinci = ilk[0].split(":"); //ilk bölüm saat:dakika

        if (ikinci.length != 2) return -1;

        try {
            saat = Integer.parseInt(ikinci[0].trim());
        } catch (NumberFormatException e) {
            return -1;
        }

        if (saat < 0 || saat > 23)
            return -1;
        else
            return saat;
    }

    private int sureGetir(String zaman) {
        int sure;

        String[] ilk = zaman.split("-"); //- ile ayrılan süre

        if (ilk.length != 2) return -1;

        String[] ikinci = ilk[0].split(":"); //ilk bölüm saat:dakika

        if (ikinci.length != 2) return -1;

        try {
            sure = Integer.parseInt(ilk[1].trim());
        } catch (NumberFormatException e) {
            return -1;
        }

        if (sure < 0 || sure > 1000)
            return -1;
        else
            return sure;
    }

    private void resetAlarm() {
        //getting the alarm manager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //creating a new intent specifying the broadcast receiver
        Intent intent = new Intent(this, MyAlarm.class);

        //creating a pending intent using the intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // Cancel alarms
        try {
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        } catch (Exception e) {
            Log.e("alarm ", "AlarmManager not canceled!" + e.toString());
        }
    }

    private void setAlarm(long zilTime) {
        //getting the alarm manager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //creating a new intent specifying the broadcast receiver
        Intent intent = new Intent(this, MyAlarm.class);
        intent.putExtra("alarm","var");

        //creating a pending intent using the intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_ONE_SHOT); //this PendingIntent can be used only once

        assert alarmManager != null;

        //RTC pil tasarrufu seçeneği?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    zilTime,
                    pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    zilTime,
                    pendingIntent);
        }
    }

    private void alarmlariAyarla() {

        for (int i = 0; i < zillers.size(); i++) {
            //tüm zilleri ayarlar, eski olması önemli değil

            int saat = saatGetir(zillers.get(i).getZaman());
            int dakika = dakikaGetir(zillers.get(i).getZaman());
            int sure = sureGetir(zillers.get(i).getZaman());

            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    saat,
                    dakika,
                    0);

            //eski alarmları kapat
            resetAlarm();
            setAlarm(calendar.getTimeInMillis());               //teneffüs başı
            if (sure > 0)
                setAlarm(calendar.getTimeInMillis() + sure * 1000); //teneffüs sonu
            //zil adı lazım, ama bildirimde yok ki
            //globalDegerler.gelecekAlarmAdi = zillers.get(i).getName();
        }
    }

    public void anaProgramdaBilgileriGuncelle() {
        TextView m2 = findViewById(R.id.mesaj2);

        if (!checkServiceRunning()) {
            globalDegerler.hizmetDurumuMesaji = getResources().getString(R.string.arkaplanHizmetiNo);
        } else {
            globalDegerler.hizmetDurumuMesaji = getResources().getString(R.string.arkaplanHizmetiOk);
        }

        m1.setText(globalDegerler.hizmetDurumuMesaji);
        m2.setText(globalDegerler.hizmetBildirimiMesaji);

        //Widget Ayarlarını uygula
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.zaman_app_widget);

        view.setTextViewText(R.id.teneffustextView, globalDegerler.hizmetBildirimiMesaji);

        view.setInt(R.id.rootBG, "setBackgroundColor",
                ColorUtils.setAlphaComponent(Color.argb(
                        pref.getInt("alpha", 50)
                        , pref.getInt("red", 61)
                        , pref.getInt("green", 90)
                        , pref.getInt("blue", 254)),
                        pref.getInt("alpha", 50)));


        switch (pref.getInt("fontSize", 2)) {
            case 1:
                view.setTextViewTextSize(R.id.teneffustextView, TypedValue.COMPLEX_UNIT_DIP, 14);
                m2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                break;
            case 2:
                view.setTextViewTextSize(R.id.teneffustextView, TypedValue.COMPLEX_UNIT_DIP, 16);
                m2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                break;
            case 3:
                view.setTextViewTextSize(R.id.teneffustextView, TypedValue.COMPLEX_UNIT_DIP, 18);
                m2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                break;
            case 4:
                view.setTextViewTextSize(R.id.teneffustextView, TypedValue.COMPLEX_UNIT_DIP, 20);
                m2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                break;
        }

        ComponentName theWidget = new ComponentName(this, zamanAppWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(theWidget, view);
    }

    public boolean checkServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.tuzla.derzil3.alarmServis".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startService() {
        Intent serviceIntent = new Intent(this, alarmServis.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        Toast.makeText(this, getResources().getString(R.string.arkaplanHizmetiOk), Toast.LENGTH_SHORT).show();
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, alarmServis.class);
        Toast.makeText(this, getResources().getString(R.string.arkaplanHizmetiNo), Toast.LENGTH_SHORT).show();
        stopService(serviceIntent);
    }

    public void servisKapaAc(View view) {
        if (m1.getTag() != "ATLA") {
            if (!checkServiceRunning()) {
                startService();

                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("hizmetDurumu", true);
                editor.apply();
            } else {
                stopService();

                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("hizmetDurumu", false);
                editor.apply();
            }
        }
    }

    public void ayarlariAc(View view) {
        Intent i = new Intent(MainActivity.this, ayarlarActivity.class);
        startActivity(i);
    }

    public void zilleriAc(View view) {
        Intent i = new Intent(MainActivity.this, swipeActivity.class);
        startActivity(i);
    }
}