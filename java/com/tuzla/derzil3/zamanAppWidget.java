package com.tuzla.derzil3;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.RemoteViews;

import androidx.core.graphics.ColorUtils;

public class zamanAppWidget extends AppWidgetProvider {
    private static SharedPreferences pref;

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {

        //düğmeye basınca ana program açılır, ilk mesaj yüklenir
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,0);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.zaman_app_widget);
        views.setOnClickPendingIntent(R.id.ayarbutton, pendingIntent);

        views.setTextViewText(R.id.teneffustextView, globalDegerler.hizmetDurumuMesaji);

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

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
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