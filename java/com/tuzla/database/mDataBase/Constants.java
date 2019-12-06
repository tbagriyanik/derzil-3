package com.tuzla.database.mDataBase;

public class Constants {

    //COLUMNS
    public static final String ROW_ID = "id";
    public static final String NAME = "name";
    public static final String ZAMAN = "zaman";
    public static final String GUNLER = "gunler";
    public static final String AKTIF = "aktif";

    //DB PROPS
    public static final String DB_NAME = "ziller_DB";
    public static final String TB_NAME = "ziller_TB";
    public static final int DB_VERSION = 1;

    //CREATE TABLE
    static final String CREATE_TB = "CREATE TABLE " + TB_NAME
            + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME + " VARCHAR(30) NOT NULL, "
            + ZAMAN + " VARCHAR(10) NOT NULL, "
            + GUNLER + " VARCHAR(7) NOT NULL, "
            + AKTIF + " INTEGER NOT NULL  );";

    //INSERT A SAMPLE RECORD
    static final String INSERT_SAMPLE = "INSERT INTO " + TB_NAME + " VALUES " +
            "(null, 'Sample 1', '12:00-10', '1111100', '1');";

    //DROP TB
    static final String DROP_TB = "DROP TABLE IF EXISTS " + TB_NAME;


}
