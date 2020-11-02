package com.tuzla.derzil3;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.graphics.ColorUtils;

import static com.tuzla.derzil3.globalDegerler.GLOBAL_hizmetDurumuMesaji;

public class zamanAppWidget extends AppWidgetProvider {
    private static SharedPreferences pref;

    private static final String ACTION_CLICK = "ACTION_CLICK";

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //düğmeye basınca ana program açılır, ilk mesaj yüklenir
        Intent intent = new Intent(context, MainActivity.class);

        intent.setAction(ACTION_CLICK);
        intent.putExtra("appWidgetId", appWidgetId);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.zaman_app_widget);
        views.setOnClickPendingIntent(R.id.ayarbutton, pendingIntent);

        views.setTextViewText(R.id.teneffustextView, GLOBAL_hizmetDurumuMesaji);

        pref = context.getApplicationContext().getSharedPreferences("derzilPref", Context.MODE_PRIVATE);

        views.setInt(R.id.rootBG, "setBackgroundColor",
                ColorUtils.setAlphaComponent(Color.argb(
                        pref.getInt("alpha", 50)
                        , pref.getInt("red", 61)
                        , pref.getInt("green", 90)
                        , pref.getInt("blue", 254)),
                        pref.getInt("alpha", 50)));

        switch (pref.getInt("fontSize", 2)) {
            case 1:
                views.setTextViewTextSize(R.id.teneffustextView, TypedValue.COMPLEX_UNIT_DIP, 12);
                break;
            case 2:
                views.setTextViewTextSize(R.id.teneffustextView, TypedValue.COMPLEX_UNIT_DIP, 14);
                break;
            case 3:
                views.setTextViewTextSize(R.id.teneffustextView, TypedValue.COMPLEX_UNIT_DIP, 18);
                break;
            case 4:
                views.setTextViewTextSize(R.id.teneffustextView, TypedValue.COMPLEX_UNIT_DIP, 20);
                break;
        }

        Bundle appWidgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int minWidth = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        //int minHeight = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

        if (minWidth > 150) { //&& minHeight > 120
            views.setViewVisibility(R.id.ayarbutton, View.VISIBLE);
            views.setViewVisibility(R.id.appwidget_text, View.VISIBLE);
        } else {
            views.setViewVisibility(R.id.ayarbutton, View.GONE);
            views.setViewVisibility(R.id.appwidget_text, View.GONE);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.zaman_app_widget);
        resizeWidget(newOptions, views);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void resizeWidget(Bundle appWidgetOptions, RemoteViews views) {
        int minWidth = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

        if (minWidth > 150) { //&& minHeight > 120
            views.setViewVisibility(R.id.ayarbutton, View.VISIBLE);
            views.setViewVisibility(R.id.appwidget_text, View.VISIBLE);
        } else {
            views.setViewVisibility(R.id.ayarbutton, View.GONE);
            views.setViewVisibility(R.id.appwidget_text, View.GONE);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();

        if (intentAction.equals(ACTION_CLICK)) {
            Bundle extras = intent.getExtras();
            Integer appWidgetId = extras.getInt("appWidgetId");
            //Toast.makeText(context, "TIK "+appWidgetId, Toast.LENGTH_SHORT).show();
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // when the last widget is disabled
        super.onDisabled(context);
    }

}