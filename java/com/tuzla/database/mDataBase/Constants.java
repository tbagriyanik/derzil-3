package com.tuzla.database.mDataBase;

public class Constants {

    //COLUMNS
    static final String ROW_ID = "id";
    static final String NAME = "name";
    static final String ZAMAN = "zaman";
    static final String GUNLER = "gunler";
    static final String AKTIF = "aktif";

    //DB PROPS
    static final String DB_NAME = "ziller_DB";
    static final String TB_NAME = "ziller_TB";
    static final int DB_VERSION = 1;

    //CREATE TABLE
    static final String CREATE_TB = "CREATE TABLE " + TB_NAME
            + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME + " VARCHAR(30) NOT NULL, "
            + ZAMAN + " VARCHAR(10) NOT NULL, "
            + GUNLER + " VARCHAR(7) NOT NULL, "
            + AKTIF + " INTEGER NOT NULL  );";

    //DROP TB
    static final String DROP_TB = "DROP TABLE IF EXISTS " + TB_NAME;


}
