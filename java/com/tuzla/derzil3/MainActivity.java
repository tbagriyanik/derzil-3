package com.tuzla.derzil3;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.tuzla.database.swipeActivity;

import java.util.Timer;
import java.util.TimerTask;

import static com.tuzla.derzil3.globalDegerler.GLOBAL_hizmetBildirimiMesaji;
import static com.tuzla.derzil3.globalDegerler.GLOBAL_hizmetDurumuMesaji;
import static com.tuzla.derzil3.globalDegerler.GLOBAL_tazeleme;
import static com.tuzla.derzil3.globalDegerler.dismiss_All;
import static com.tuzla.derzil3.globalDegerler.dismiss_Next;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Handler handler = new Handler();
    Timer timer;
    Switch m1, swNext, swAll;
    SharedPreferences pref;

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m1 = findViewById(R.id.switchHizmet);
        swNext = findViewById(R.id.switchNext);
        swAll = findViewById(R.id.switchAll);

        verifyStoragePermissions(this);

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
        timer.schedule(timerTask, 100, GLOBAL_tazeleme);//ana program refresh
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void anaProgramdaBilgileriGuncelle() {
        TextView m2 = findViewById(R.id.mesaj2);

        if (!checkServiceRunning()) {
            GLOBAL_hizmetDurumuMesaji = getResources().getString(R.string.arkaplanHizmetiNo);
        } else {
            GLOBAL_hizmetDurumuMesaji = getResources().getString(R.string.arkaplanHizmetiOk);
        }

        m1.setText(GLOBAL_hizmetDurumuMesaji);
        m2.setText(GLOBAL_hizmetBildirimiMesaji);
        swNext.setChecked(globalDegerler.dismiss_Next);
        swAll.setChecked(globalDegerler.dismiss_All);

        //Widget Ayarlarını uygula
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.zaman_app_widget);

        view.setTextViewText(R.id.teneffustextView, GLOBAL_hizmetBildirimiMesaji);

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
                m2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                break;
            case 2:
                view.setTextViewTextSize(R.id.teneffustextView, TypedValue.COMPLEX_UNIT_DIP, 14);
                m2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
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


    public void dismissNextClick(View view) {
        dismiss_Next = !dismiss_Next;
    }

    public void dismissAllClick(View view) {
        dismiss_All = !dismiss_All;
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

    public void zillerHaftalikAc(View view) {
        Intent i = new Intent(MainActivity.this, weeklyActivity.class);
        startActivity(i);
    }

}