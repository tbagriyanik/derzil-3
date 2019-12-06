package com.tuzla.database;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tuzla.database.mDataBase.Constants;
import com.tuzla.database.mDataBase.DBAdapter;
import com.tuzla.database.mDataBase.DBHelper;
import com.tuzla.database.mDataObject.Ziller;
import com.tuzla.database.mRecycler.MyAdapter;
import com.tuzla.database.mSwiper.SwipeHelper;
import com.tuzla.derzil3.MainActivity;
import com.tuzla.derzil3.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class swipeActivity extends AppCompatActivity {

    static RecyclerView rv;
    static MyAdapter adapter;
    static DBAdapter db;
    static ArrayList<Ziller> zillers = new ArrayList<>();
    EditText nameEditText, durationEditText;
    TextView saatDakikaTextView;
    CheckBox hafta1, hafta2, hafta3, hafta4, hafta5, hafta6, hafta7;
    Switch switchActive;
    Button saveBtn;
    Button importBtn;
    Dialog d;
    private SwipeRefreshLayout swipeContainer;

    //RETRIEVE
    public static void getZiller() {
        zillers.clear();

        db.openDB();
        Cursor c = db.retrieve();

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

            zillers.add(p);
        }

        Collections.sort(zillers, new dakikaComparator());

        db.closeDB();

        if (zillers.size() > 0)
            rv.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        FloatingActionButton fabRefresh = findViewById(R.id.fabRefresh);
        FloatingActionButton fabHome = findViewById(R.id.fabHome);

        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyAdapter(this, zillers);

        db = new DBAdapter(this);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callbackGeri = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                geri();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callbackGeri);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog();
            }
        });
        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getZiller();
            }
        });
        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geri();
            }
        });

        ItemTouchHelper.Callback callback = new SwipeHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(rv);

        swipeContainer = findViewById(R.id.swipeRefresh);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getZiller();
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getZiller();
        if (zillers.size() > 0)
            Toast.makeText(this, getResources().getString(R.string.listeIpucu), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, getResources().getString(R.string.zilYok), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getZiller();
    }

    private void geri() {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
    }

    private void displayDialog() {
        d = new Dialog(this);
        d.setTitle(getResources().getString(R.string.app_name));
        d.setCancelable(true);

        d.setContentView(R.layout.dialog_layout);

        //Diyalog formundaki nesneleri al
        nameEditText = d.findViewById(R.id.nameEditTxt);
        durationEditText = d.findViewById(R.id.durationEditTxt);
        saatDakikaTextView = d.findViewById(R.id.textViewSaatDakika);
        try {
            saatDakikaTextView.setText(new SimpleDateFormat("HH:mm").format(
                    new SimpleDateFormat("HH:mm").parse(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" +
                            Calendar.getInstance().get(Calendar.MINUTE))));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        hafta1 = d.findViewById(R.id.checkBox1);
        hafta2 = d.findViewById(R.id.checkBox2);
        hafta3 = d.findViewById(R.id.checkBox3);
        hafta4 = d.findViewById(R.id.checkBox4);
        hafta5 = d.findViewById(R.id.checkBox5);
        hafta6 = d.findViewById(R.id.checkBox6);
        hafta7 = d.findViewById(R.id.checkBox7);
        switchActive = d.findViewById(R.id.switchActive);

        saatDakikaTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int saat = Integer.parseInt(saatDakikaTextView.getText().toString().split(":")[0]);
                int dakika = Integer.parseInt(saatDakikaTextView.getText().toString().split(":")[1]);

                TimePickerDialog tpd = new TimePickerDialog(swipeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // hourOfDay ve minute değerleri seçilen saat değerleridir.
                                // Edittextte bu değerleri gösteriyoruz.
                                try {
                                    saatDakikaTextView.setText(new SimpleDateFormat("HH:mm").format(
                                            new SimpleDateFormat("HH:mm").parse(hourOfDay + ":" + minute)));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, saat, dakika, true);
                // timepicker açıldığında set edilecek değerleri buraya yazıyoruz.
                // şimdiki zamanı göstermesi için yukarda tanımladğımız değişkenleri kullanıyoruz.
                // true değeri 24 saatlik format için.

                // dialog penceresinin button bilgilerini ayarlıyoruz ve ekranda gösteriyoruz.
                tpd.setButton(TimePickerDialog.BUTTON_POSITIVE, swipeActivity.this.getResources().getString(R.string.sec), tpd);
                tpd.setButton(TimePickerDialog.BUTTON_NEGATIVE, swipeActivity.this.getResources().getString(R.string.iptal), tpd);
                tpd.show();
            }
        });

        saveBtn = d.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //yeni değerleri veritabanına yolla
                String yeniGunler = "";
                yeniGunler += hafta1.isChecked() ? "1" : "0";
                yeniGunler += hafta2.isChecked() ? "1" : "0";
                yeniGunler += hafta3.isChecked() ? "1" : "0";
                yeniGunler += hafta4.isChecked() ? "1" : "0";
                yeniGunler += hafta5.isChecked() ? "1" : "0";
                yeniGunler += hafta6.isChecked() ? "1" : "0";
                yeniGunler += hafta7.isChecked() ? "1" : "0";

                swipeActivity.this.save(nameEditText.getText().toString(),
                        saatDakikaTextView.getText() + "-" + durationEditText.getText().toString(),
                        yeniGunler,
                        switchActive.isChecked() ? 1 : -1);
                getZiller();
            }
        });

        importBtn = d.findViewById(R.id.importBtn);
        importBtn.setVisibility(View.VISIBLE);
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //yeni değerleri dosyadan veritabanına yolla
                CSVReader csvReader = null;
                File exportDir = new File(Environment.getExternalStorageDirectory(), "/derZilData/derZil.csv");
                if (!exportDir.exists()) {
                    Toast.makeText(swipeActivity.this,
                            Environment.getExternalStorageDirectory() + "/derZilData/derZil.csv \nNot found!"
                            , Toast.LENGTH_SHORT).show();
                    return; //no data exist
                }

                try {
                    csvReader = new CSVReader(new FileReader(
                            Environment.getExternalStorageDirectory() + "/derZilData/derZil.csv"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(swipeActivity.this,
                            getResources().getString(R.string.Error) + "\n" + e.toString()
                            , Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] nextLine = new String[0];
                int count = 0;
                StringBuilder columns = new StringBuilder();

                DBHelper helper = new DBHelper(swipeActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();

                while (true) {
                    try {
                        assert csvReader != null;
                        if ((nextLine = csvReader.readNext()) == null) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String values = new String();
                    // nextLine[] is an array of values from the line
                    if (count > 0) values += " (";
                    //i=0 olunca id dir
                    for (int i = 0; i <= nextLine.length - 1; i++) {
                        if (count > 0) {
                            //kolon satırı atlandı count==0
                            if (i == 0)
                                values += "null , ";
                            else if (i < nextLine.length - 1)
                                values += DatabaseUtils.sqlEscapeString(nextLine[i]) + (", ");
                            else
                                values += DatabaseUtils.sqlEscapeString(nextLine[i]);
                        }
                    }//one line each

                    if (count > 0) values += (")");

                    if (values.length() != 0)
                        db.execSQL("Insert INTO " + Constants.TB_NAME + "  values " +
                                values.toString());

                    count++;
                }

                db.close();

                Toast.makeText(swipeActivity.this, getResources().getString(R.string.Success),
                        Toast.LENGTH_SHORT).show();
            }
        });
        //SHOW DIALOG
        d.show();
    }

    //SAVE
    private void save(String name, String zaman, String gunler, int aktif) {
        DBAdapter db = new DBAdapter(this);
        db.openDB();
        if (!TextUtils.isEmpty(name.trim()) && !TextUtils.isEmpty(zaman.trim())) {
            if (db.add(name.trim(), zaman.replace(" ", ""), gunler, aktif)) {
                nameEditText.setText("");
                durationEditText.setText("0");
                d.dismiss();
                Toast.makeText(this, getResources().getString(R.string.Success), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.Error), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.emptyError), Toast.LENGTH_SHORT).show();
        }

        db.closeDB();
    }

    public static class dakikaComparator implements Comparator<Ziller> {
        @Override
        public int compare(Ziller o1, Ziller o2) {
            int dakika1, dakika2, saat1, saat2, sure1, sure2;

            String[] ilk1 = o1.getZaman().split("-"); //- ile ayrılan süreyi sonra kullanacağız
            String[] ilk2 = o2.getZaman().split("-");
            if (ilk1.length != 2 || ilk2.length != 2)
                return 0;

            String[] gelen1 = ilk1[0].split(":"); //ilk bölüm saat:dakika
            String[] gelen2 = ilk2[0].split(":");
            if (gelen1.length != 2 || gelen2.length != 2)
                return 0;

            try {
                //yazim hatasi olabilir, içi boş olabilir
                dakika1 = Integer.parseInt(gelen1[1].trim());
                dakika2 = Integer.parseInt(gelen2[1].trim());
                saat1 = Integer.parseInt(gelen1[0].trim());
                saat2 = Integer.parseInt(gelen2[0].trim());
                sure1 = dakika1 + saat1 * 60;
                sure2 = dakika2 + saat2 * 60;
            } catch (NumberFormatException e) {
                return 0;
            }

            if (sure1 > sure2)
                return 1;
            else if (sure1 < sure2)
                return -1;
            else
                return 0;
        }
    }
}