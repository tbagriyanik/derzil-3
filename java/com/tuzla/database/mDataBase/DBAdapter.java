package com.tuzla.database.mDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {

    Context c;
    SQLiteDatabase db;
    DBHelper helper;

    public DBAdapter(Context c) {
        this.c = c;
        helper = new DBHelper(c);
    }

    //OPEN DB
    public void openDB() {
        try {
            db = helper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //CLOSE DB
    public void closeDB() {
        try {
            helper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean saatFormat(String gelen) {
        int dakika, saat, sure;

        String[] ilk = gelen.split("-"); //- ile ayrılan süreyi sonra kullanacağız

        if (ilk.length != 2) return false;

        String[] ikinci = ilk[0].split(":"); //ilk bölüm saat:dakika

        if (ikinci.length != 2) return false;

        try {
            //yazim hatasi olabilir, içi boş olabilir
            dakika = Integer.parseInt(ikinci[1].trim());
            saat = Integer.parseInt(ikinci[0].trim());
            sure = Integer.parseInt(ilk[1].trim());
        } catch (NumberFormatException e) {
            return false;
        }

        if (dakika < 0 || dakika > 59
                || saat < 0 || saat > 23
                || sure < 0 || sure > 1000)
            return false;
        else
            return true;
    }

    //EDIT/UPDATE
    public boolean edit(int id, String name, String zaman, String gunler, int aktif) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(Constants.NAME, name);
            cv.put(Constants.ZAMAN, zaman);
            cv.put(Constants.GUNLER, gunler);
            cv.put(Constants.AKTIF, aktif);

            if (!saatFormat(zaman)) return false;

            db.update(Constants.TB_NAME, cv,
                    Constants.ROW_ID + "=" + id, null);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //INSERT/SAVE
    public boolean add(String name, String zaman, String gunler, int aktif) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(Constants.NAME, name);
            cv.put(Constants.ZAMAN, zaman);
            cv.put(Constants.GUNLER, gunler);
            cv.put(Constants.AKTIF, aktif);

            if (!saatFormat(zaman)) return false;

            db.insert(Constants.TB_NAME, Constants.ROW_ID, cv);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //SELECT/RETRIEVE
    public Cursor retrieve() {
        String[] columns = {Constants.ROW_ID, Constants.NAME, Constants.ZAMAN, Constants.GUNLER, Constants.AKTIF};

        return db.query(Constants.TB_NAME, columns, null,
                null, null, null, null);
    }

    //DELETE/REMOVE
    public boolean delete(int id) {
        try {
            int result = db.delete(Constants.TB_NAME,
                    Constants.ROW_ID + " =?",
                    new String[]{String.valueOf(id)});
            if (result > 0) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}













