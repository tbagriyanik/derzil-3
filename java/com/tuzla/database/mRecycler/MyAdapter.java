package com.tuzla.database.mRecycler;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.tuzla.database.mDataBase.DBAdapter;
import com.tuzla.database.mDataObject.Ziller;
import com.tuzla.database.swipeActivity;
import com.tuzla.derzil3.R;

import java.util.ArrayList;
import java.util.Calendar;


public class MyAdapter extends RecyclerView.Adapter<MyHolder> {

    RecyclerView rv;
    private Context c;
    private ArrayList<Ziller> zillers;
    private EditText nameEditText, durationEditText;
    private TextView saatDakikaTextView;
    private CheckBox hafta1, hafta2, hafta3, hafta4, hafta5, hafta6, hafta7;
    private Switch switchActive;
    private Button saveBtn, duplicateBtn;
    private Dialog d;

    public MyAdapter(Context c, ArrayList<Ziller> zillers) {
        this.c = c;
        this.zillers = zillers;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zil_item, parent, false);
        MyHolder holder = new MyHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        //veritabanından gelen bilgileri listedekilere bağla
        holder.nametxt.setText(zillers.get(position).getName());
        holder.zamantxt.setText(zillers.get(position).getZaman());

        DBAdapter db = new DBAdapter(c);
        if (!db.saatFormat(zillers.get(position).getZaman())) {
            holder.zamantxt.setTextColor(Color.RED);
        }

        if (zillers.get(position).getAktif() == 1)
            holder.nametxt.setTextColor(Color.BLACK);
        else
            holder.nametxt.setTextColor(Color.LTGRAY);

        String gunler = "";
        String gelenBilgi = zillers.get(position).getGunler();

        gunler += gelenBilgi.substring(0, 1).equals("1") ? c.getResources().getString(R.string.hafta1) : "-";
        gunler += gelenBilgi.substring(1, 2).equals("1") ? c.getResources().getString(R.string.hafta2) : "-";
        gunler += gelenBilgi.substring(2, 3).equals("1") ? c.getResources().getString(R.string.hafta3) : "-";
        gunler += gelenBilgi.substring(3, 4).equals("1") ? c.getResources().getString(R.string.hafta4) : "-";
        gunler += gelenBilgi.substring(4, 5).equals("1") ? c.getResources().getString(R.string.hafta5) : "-";
        gunler += gelenBilgi.substring(5, 6).equals("1") ? c.getResources().getString(R.string.hafta6) : "-";
        gunler += gelenBilgi.substring(6, 7).equals("1") ? c.getResources().getString(R.string.hafta7) : "-";

        holder.gunlertxt.setText(gunler);

        holder.editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sildikten sonra index güncellenmesi yaptık
                MyAdapter.this.displayEditDialog(zillers.get(position).getId(),
                        zillers.get(position).getName(),
                        zillers.get(position).getZaman(),
                        zillers.get(position).getGunler(),
                        zillers.get(position).getAktif());
            }
        });
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

    private int sureGetir(String zaman) {
        int sure;

        String[] ilk = zaman.split("-"); //- ile ayrılan süre

        if (ilk.length != 2) return -1;

        String[] ikinci = ilk[0].split(":"); //ilk bölüm saat:dakika

        if (ikinci.length != 2) return -1;

        try {
            sure = Integer.parseInt(ilk[1].trim());
        } catch (NumberFormatException e) {
            return -1;
        }

        if (sure < 0 || sure > 1000)
            return -1;
        else
            return sure;
    }

    private void displayEditDialog(final int id, String oldname, final String oldzaman, String oldgunler, int oldaktif) {
        d = new Dialog(c);
        d.setTitle(c.getResources().getString(R.string.app_name));
        d.setCancelable(true);

        d.setContentView(R.layout.dialog_layout);

        //Diyalog formundaki nesneleri al
        nameEditText = d.findViewById(R.id.nameEditTxt);
        durationEditText = d.findViewById(R.id.durationEditTxt);
        saatDakikaTextView = d.findViewById(R.id.textViewSaatDakika);
        hafta1 = d.findViewById(R.id.checkBox1);
        hafta2 = d.findViewById(R.id.checkBox2);
        hafta3 = d.findViewById(R.id.checkBox3);
        hafta4 = d.findViewById(R.id.checkBox4);
        hafta5 = d.findViewById(R.id.checkBox5);
        hafta6 = d.findViewById(R.id.checkBox6);
        hafta7 = d.findViewById(R.id.checkBox7);
        switchActive = d.findViewById(R.id.switchActive);

        nameEditText.setText(oldname);
        String duration = sureGetir(oldzaman)+ "";
        String saatDakika = saatGetir(oldzaman)+":"+dakikaGetir(oldzaman);
        durationEditText.setText(duration);
        saatDakikaTextView.setText(saatDakika);
        switchActive.setChecked(oldaktif == 1);

        if (oldgunler.length() < 7)
            oldgunler = "0000000";

        hafta1.setChecked(oldgunler.substring(0, 1).equals("1"));
        hafta2.setChecked(oldgunler.substring(1, 2).equals("1"));
        hafta3.setChecked(oldgunler.substring(2, 3).equals("1"));
        hafta4.setChecked(oldgunler.substring(3, 4).equals("1"));
        hafta5.setChecked(oldgunler.substring(4, 5).equals("1"));
        hafta6.setChecked(oldgunler.substring(5, 6).equals("1"));
        hafta7.setChecked(oldgunler.substring(6, 7).equals("1"));

        saatDakikaTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Şimdiki zaman bilgilerini alıyoruz. güncel saat, güncel dakika.
                final Calendar takvim = Calendar.getInstance();
                int saat = saatGetir(oldzaman);
                int dakika = dakikaGetir(oldzaman);

                TimePickerDialog tpd = new TimePickerDialog(c,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // hourOfDay ve minute değerleri seçilen saat değerleridir.
                                // Edittextte bu değerleri gösteriyoruz.
                                saatDakikaTextView.setText(hourOfDay + ":" + minute);
                            }
                        }, saat, dakika, true);
                // timepicker açıldığında set edilecek değerleri buraya yazıyoruz.
                // şimdiki zamanı göstermesi için yukarda tanımladğımız değişkenleri kullanıyoruz.
                // true değeri 24 saatlik format için.

                // dialog penceresinin button bilgilerini ayarlıyoruz ve ekranda gösteriyoruz.
                tpd.setButton(TimePickerDialog.BUTTON_POSITIVE, c.getResources().getString(R.string.sec), tpd);
                tpd.setButton(TimePickerDialog.BUTTON_NEGATIVE, c.getResources().getString(R.string.iptal), tpd);
                tpd.show();
            }
        });

        saveBtn = d.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String yeniGunler = "";
                yeniGunler += hafta1.isChecked() ? "1" : "0";
                yeniGunler += hafta2.isChecked() ? "1" : "0";
                yeniGunler += hafta3.isChecked() ? "1" : "0";
                yeniGunler += hafta4.isChecked() ? "1" : "0";
                yeniGunler += hafta5.isChecked() ? "1" : "0";
                yeniGunler += hafta6.isChecked() ? "1" : "0";
                yeniGunler += hafta7.isChecked() ? "1" : "0";

                MyAdapter.this.edit(id, nameEditText.getText().toString()
                        , saatDakikaTextView.getText() + "-" + durationEditText.getText().toString()
                        , yeniGunler
                        , switchActive.isChecked() ? 1 : -1);
                //getZiller(); //otomatik tazeleme var
            }
        });

        duplicateBtn = d.findViewById(R.id.duplicateBtn);
        duplicateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String yeniGunler = "";
                yeniGunler += hafta1.isChecked() ? "1" : "0";
                yeniGunler += hafta2.isChecked() ? "1" : "0";
                yeniGunler += hafta3.isChecked() ? "1" : "0";
                yeniGunler += hafta4.isChecked() ? "1" : "0";
                yeniGunler += hafta5.isChecked() ? "1" : "0";
                yeniGunler += hafta6.isChecked() ? "1" : "0";
                yeniGunler += hafta7.isChecked() ? "1" : "0";

                MyAdapter.this.save(nameEditText.getText().toString()
                        , saatDakikaTextView.getText() + "-" + durationEditText.getText().toString()
                        , yeniGunler
                        , switchActive.isChecked() ? 1 : -1);
                //getZiller(); //otomatik tazeleme var
            }
        });
        duplicateBtn.setVisibility(View.VISIBLE);

        //SHOW DIALOG
        d.show();
    }

    //UPDATE/EDIT
    private void edit(int id, String name, String zaman, String gunler, int aktif) {
        DBAdapter db = new DBAdapter(c);
        db.openDB();
        if (!TextUtils.isEmpty(name.trim()) && !TextUtils.isEmpty(zaman.trim())) {
            if (db.edit(id, name.trim(), zaman.replace(" ", ""), gunler, aktif)) {
                nameEditText.setText("");
                durationEditText.setText("0");
                Toast.makeText(c, c.getResources().getString(R.string.Success), Toast.LENGTH_SHORT).show();
                d.dismiss();
            } else {
                Toast.makeText(c, c.getResources().getString(R.string.Error), Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(c, c.getResources().getString(R.string.emptyError), Toast.LENGTH_SHORT).show();
        db.closeDB();
    }

    //SAVE/ADD
    private void save(String name, String zaman, String gunler, int aktif) {
        DBAdapter db = new DBAdapter(c);
        db.openDB();
        if (!TextUtils.isEmpty(name.trim()) && !TextUtils.isEmpty(zaman.trim())) {
            if (db.add(name.trim(), zaman.replace(" ", ""), gunler, aktif)) {
                nameEditText.setText("");
                durationEditText.setText("0");
                d.dismiss();
                Toast.makeText(c, c.getResources().getString(R.string.Success), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(c, c.getResources().getString(R.string.Error), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(c, c.getResources().getString(R.string.emptyError), Toast.LENGTH_SHORT).show();
        }

        db.closeDB();
    }

    @Override
    public int getItemCount() {
        return zillers.size();
    }

    public void deleteZil(int pos) {
        //GET ID
        Ziller p = zillers.get(pos);
        int id = p.getId();

        //DELETE FROM DB
        DBAdapter db = new DBAdapter(c);
        db.openDB();
        if (db.delete(id)) {
            zillers.remove(pos);
            Toast.makeText(c, c.getResources().getString(R.string.Success), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(c, c.getResources().getString(R.string.Error), Toast.LENGTH_SHORT).show();
        }

        db.closeDB();

        swipeActivity.getZiller();

        this.notifyItemRemoved(pos);
    }
}