package com.tuzla.database;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

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

import java.util.ArrayList;

public class swipeActivity extends AppCompatActivity {

    RecyclerView rv;
    MyAdapter adapter;
    EditText nameEditText, zamanEditText;
    CheckBox hafta1, hafta2, hafta3, hafta4, hafta5, hafta6, hafta7;
    Switch switchActive;

    Button saveBtn;
    ArrayList<Ziller> zillers = new ArrayList<>();

    Dialog d;
    private SwipeRefreshLayout swipeContainer;

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
        zamanEditText = d.findViewById(R.id.zamanEditTxt);
        hafta1 = d.findViewById(R.id.checkBox1);
        hafta2 = d.findViewById(R.id.checkBox2);
        hafta3 = d.findViewById(R.id.checkBox3);
        hafta4 = d.findViewById(R.id.checkBox4);
        hafta5 = d.findViewById(R.id.checkBox5);
        hafta6 = d.findViewById(R.id.checkBox6);
        hafta7 = d.findViewById(R.id.checkBox7);
        switchActive = d.findViewById(R.id.switchActive);

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

                save(nameEditText.getText().toString(),
                        zamanEditText.getText().toString(),
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
            if (db.add(name.trim(), zaman.trim(), gunler, aktif)) {
                nameEditText.setText("");
                zamanEditText.setText("");
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

    //RETRIEVE
    private void getZiller() {
        zillers.clear();

        DBAdapter db = new DBAdapter(this);
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
        db.closeDB();

        if (zillers.size() > 0)
            rv.setAdapter(adapter);
    }
}