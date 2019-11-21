package com.tuzla.database;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.tuzla.database.mDataBase.DBAdapter;
import com.tuzla.database.mDataObject.Ziller;
import com.tuzla.database.mRecycler.MyAdapter;
import com.tuzla.database.mSwiper.SwipeHelper;
import com.tuzla.derzil3.MainActivity;
import com.tuzla.derzil3.R;

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
                    new SimpleDateFormat("HH:mm").parse(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"+
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

            Log.wtf("sure1 ", Integer.toString(sure1));
            Log.wtf("sure2 ", Integer.toString(sure2));
            Log.wtf("---", "---");

            if (sure1 > sure2)
                return 1;
            else if (sure1 < sure2)
                return -1;
            else
                return 0;
        }
    }
}