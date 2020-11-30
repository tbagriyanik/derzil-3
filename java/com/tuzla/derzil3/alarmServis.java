package com.tuzla.derzil3;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.tuzla.database.mDataBase.DBAdapter;
import com.tuzla.database.mDataObject.Ziller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.tuzla.derzil3.App.CHANNEL_ID;
import static com.tuzla.derzil3.globalDegerler.GLOBAL_hizmetBildirimiMesaji;
import static com.tuzla.derzil3.globalDegerler.GLOBAL_sonGun;
import static com.tuzla.derzil3.globalDegerler.GLOBAL_tazeleme;

public class alarmServis extends Service {
    private static final int NOTIF_ID = 1;
    static DBAdapter db;

    static ArrayList<Ziller> zillers = new ArrayList<>();
    private final Handler handler = new Handler();
    PendingIntent pendingIntent = null;
    ArrayList<PendingIntent> pendingIntentArrayList = new ArrayList<>();
    SharedPreferences pref;
    private final Runnable runnable = new Runnable() {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {

            getZiller(); //veritabanından listeyi al
            akilliZilBilgisi(); //alarm bilgilerine göre
            bilgileriGuncelle(GLOBAL_hizmetBildirimiMesaji);//widget ve notification güncelleme

            handler.postDelayed(this, GLOBAL_tazeleme); //1.5 saniye refresh
        }
    };

    private static boolean gunKontrol(String gunler) {
        Calendar calendar = Calendar.getInstance();
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        //PAZAR 1, pzt 2, sali 3, çars 4, pers 5, cuma 6, cmt 7
        //bizim vt'de Pzrt,..., Pzr
        if (weekday == 1) return gunler.startsWith("1", 6);
        else if (weekday == 2) return gunler.startsWith("1");
        else if (weekday == 3) return gunler.startsWith("1", 1);
        else if (weekday == 4) return gunler.startsWith("1", 2);
        else if (weekday == 5) return gunler.startsWith("1", 3);
        else if (weekday == 6) return gunler.startsWith("1", 4);
        else if (weekday == 7) return gunler.startsWith("1", 5);
        else return false;
    }

    @Override
    public void onCreate() {
        this.startForeground();
        super.onCreate();
    }

    private void startForeground() {
        startForeground(NOTIF_ID, getMyActivityNotification(""));
    }

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

                //Log.e("alarm " + say, name + " " + saat + ":" + dakika);
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

    /**
     * Tüm alarmlar yok edilir
     */
    private void resetAlarms() {
        if (pendingIntentArrayList.size() > 0) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            for (int i = 0; i < pendingIntentArrayList.size(); i++) {
                pendingIntentArrayList.get(i).cancel();
                alarmManager.cancel(pendingIntentArrayList.get(i));
            }
            pendingIntentArrayList.clear();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setAlarm(long zilTime, String alarmAdi) {

        Intent intent = new Intent(getBaseContext(), MyAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(),
                ((int) zilTime), intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager2 != null;
        alarmManager2.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, zilTime, pendingIntent);

        pendingIntentArrayList.add(pendingIntent);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        Log.e("alarm BAŞLA ", formatter.format(date) + "\n"
                + alarmAdi + "-" + formatter.format(zilTime));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void akilliZilBilgisi() {

        if (zillers.size() == 0) {
            GLOBAL_hizmetBildirimiMesaji = getResources().getString(R.string.zilYok);
            return;
        }

        ArrayList<Long> surelerArray = new ArrayList<>();
        ArrayList<Long> teneffusBitisArray = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        String geciciTarih = calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                calendar.get(Calendar.MONTH) + "/" +
                calendar.get(Calendar.YEAR);

        if (GLOBAL_sonGun.equals("") || !GLOBAL_sonGun.equals(geciciTarih)) {
            //yeni bir güne geçilmiş ise yeni tarihi al ve alarmları temizle
            resetAlarms();
        }

        for (int i = 0; i < zillers.size(); i++) {
            //veritabanındaki saat:dakika
            int saat = saatGetir(zillers.get(i).getZaman());
            int dakika = dakikaGetir(zillers.get(i).getZaman());
            int sure = sureGetir(zillers.get(i).getZaman());

            //eğer "şu an" alarm öncesinde ise 1.ZİL
            calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    saat,
                    dakika,
                    0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (Calendar.getInstance().before(calendar)
                    &&
                    (GLOBAL_sonGun.equals("") ||
                            !GLOBAL_sonGun.equals(geciciTarih))) {
                setAlarm(calendar.getTimeInMillis(), zillers.get(i).getName() + " [İLK]");        //teneffüs başı
            }

            //teneffüs bitişi 2.ZİL
            calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    saat,
                    dakika + sure,
                    0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (Calendar.getInstance().before(calendar)
                    && (sure > 0) &&
                    (GLOBAL_sonGun.equals("") ||
                            !GLOBAL_sonGun.equals(geciciTarih))) {
                setAlarm(calendar.getTimeInMillis(), zillers.get(i).getName() + " [SON]"); //teneffüs sonu
            }

            Calendar currentTime = Calendar.getInstance();
            long min1 = currentTime.get(Calendar.HOUR_OF_DAY) * 60 + currentTime.get(Calendar.MINUTE);
            long min2 = saat * 60 + dakika;
            long diff = min2 - min1;
            surelerArray.add(diff);              //teneffüs başlangıç zamanı
            teneffusBitisArray.add(diff + sure); //teneffüs bitiş zamanı
            if (min1 >= min2 && min1 < min2 + sure) {
                GLOBAL_hizmetBildirimiMesaji = zillers.get(i).getName() + "\n" +
                        getResources().getString(R.string.toFinish) + " "
                        + (min2 + sure - min1)
                        + " " + getResources().getString(R.string.minutes);

                if (GLOBAL_sonGun.equals("") || !GLOBAL_sonGun.equals(geciciTarih)) {
                    //yeni bir güne geçilmiş ise yeni tarihi al ve alarmları temizle
                    GLOBAL_sonGun = geciciTarih;
                } // bu işlemi atlamamak lazım

                return; //alttakileri yapmasın, teneffüs içindeyiz
            }
        }

        if (GLOBAL_sonGun.equals("") || !GLOBAL_sonGun.equals(geciciTarih)) {
            //yeni bir güne geçilmiş ise yeni tarihi al ve alarmları temizle
            GLOBAL_sonGun = geciciTarih;
        }

        long enKucuk = Long.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < surelerArray.size(); i++)
            if (surelerArray.get(i) < enKucuk && surelerArray.get(i) > 0) {
                enKucuk = surelerArray.get(i);
                index = i;
            }

        if (enKucuk != Long.MAX_VALUE && index != -1) {
            if (enKucuk > 60) {
                int minutes = (int) (enKucuk % 60);
                int hours = (int) (enKucuk / 60);
                if (minutes < 10) {
                    GLOBAL_hizmetBildirimiMesaji = zillers.get(index).getName() + "\n" +
                            hours + ":0" + minutes
                            + " " + getResources().getString(R.string.hourLeft);
                } else
                    GLOBAL_hizmetBildirimiMesaji = zillers.get(index).getName() + "\n" +
                            hours + ":" + minutes
                            + " " + getResources().getString(R.string.hourLeft);
                //new DecimalFormat("#,#0.0").format(enKucuk / 60f)
            } else
                GLOBAL_hizmetBildirimiMesaji = zillers.get(index).getName() + "\n" +
                        enKucuk
                        + " " + getResources().getString(R.string.minutesLeft);

        } else
            GLOBAL_hizmetBildirimiMesaji = getResources().getString(R.string.zilYok);
    }

    private Notification getMyActivityNotification(String text) {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        Notification mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(text) //üst kalın başlık
                //.setContentText(text)
                .setOnlyAlertOnce(true) //1 kere açılır
                .setOngoing(true)
                .setSmallIcon(R.drawable.bell)
                .setColor(ContextCompat.getColor(this, R.color.arkaplan))
                .addAction(R.drawable.baseline_done_black_18dp, getResources().getString(R.string.dismissNext), makePendingIntent("dismiss_actionNext"))
                .addAction(R.drawable.baseline_done_all_black_18dp, getResources().getString(R.string.dismissAll), makePendingIntent("dismiss_actionAll"))
                .setContentIntent(pendingIntent).getNotification();
        return mBuilder;
    }

    public PendingIntent makePendingIntent(String name) {
        Intent intent = new Intent(this, derzilReceiver.class);
        intent.setAction(name);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        return pendingIntent;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(runnable, 2000);

        GLOBAL_hizmetBildirimiMesaji = "..";
        bilgileriGuncelle(GLOBAL_hizmetBildirimiMesaji);

        //do heavy work on a background thread
        return START_NOT_STICKY;
    }

    public void bilgileriGuncelle(String mesaj) {
        pref = getApplicationContext().getSharedPreferences("derzilPref", Context.MODE_PRIVATE);

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

        startForeground(NOTIF_ID, getMyActivityNotification(mesaj));
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