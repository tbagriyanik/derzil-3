package com.tuzla.derzil3;

import android.app.Application;

public class globalDegerler extends Application {
    //program içinde veri alışverişi
    public static String GLOBAL_hizmetDurumuMesaji = ".";
    public static String GLOBAL_hizmetBildirimiMesaji = ".";
    public static String GLOBAL_sonGun = ""; //boş ise yenilenir
    public static int GLOBAL_tazeleme = 1500;
    public static boolean dismiss_Next = false;    //sonraki alarm ses/titreşim iptal
    public static boolean dismiss_All = false;     //kalan alarm ses/titreşim iptal
}
