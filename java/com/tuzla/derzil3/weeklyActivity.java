package com.tuzla.derzil3;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tuzla.database.mDataBase.DBAdapter;
import com.tuzla.database.mDataObject.Ziller;
import com.tuzla.database.mRecycler.MyAdapter;
import com.tuzla.database.swipeActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class weeklyActivity extends AppCompatActivity {
    static MyAdapter adapter;
    static DBAdapter db;
    static ArrayList<Ziller> zillers = new ArrayList<>();
    Handler handler = new Handler();
    int textViewCount = 170; //saatlik metinler 24x7
    TextView[] textViewArray = new TextView[textViewCount];

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

        Collections.sort(zillers, new swipeActivity.dakikaComparator());

        db.closeDB();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly);

        FloatingActionButton fabHome = findViewById(R.id.fabHome2);
        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geri();
            }
        });

        adapter = new MyAdapter(this, zillers);
        db = new DBAdapter(this);

        getTextViews(); //biraz ZAMAN alıyor 168 nesne okumak

        haftalikBilgileriGuncelle();

//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        haftalikBilgileriGuncelle();
//                    }
//                });
//            }
//        };
//        Timer timer;
//        timer = new Timer();
//        timer.schedule(timerTask, 100, GLOBAL_tazeleme);//ana program refresh
    }


    private void getTextViews() {

        //pz
        textViewArray[1] = findViewById(R.id.textView33);
        textViewArray[2] = findViewById(R.id.textView34);
        textViewArray[3] = findViewById(R.id.textView35);
        textViewArray[4] = findViewById(R.id.textView36);
        textViewArray[5] = findViewById(R.id.textView37);
        textViewArray[6] = findViewById(R.id.textView38);
        textViewArray[7] = findViewById(R.id.textView39);
        textViewArray[8] = findViewById(R.id.textView40);
        textViewArray[9] = findViewById(R.id.textView41);
        textViewArray[10] = findViewById(R.id.textView42);
        textViewArray[11] = findViewById(R.id.textView43);
        textViewArray[12] = findViewById(R.id.textView44);
        textViewArray[13] = findViewById(R.id.textView45);
        textViewArray[14] = findViewById(R.id.textView46);
        textViewArray[15] = findViewById(R.id.textView47);
        textViewArray[16] = findViewById(R.id.textView48);
        textViewArray[17] = findViewById(R.id.textView49);
        textViewArray[18] = findViewById(R.id.textView50);
        textViewArray[19] = findViewById(R.id.textView51);
        textViewArray[20] = findViewById(R.id.textView52);
        textViewArray[21] = findViewById(R.id.textView53);
        textViewArray[22] = findViewById(R.id.textView54);
        textViewArray[23] = findViewById(R.id.textView55);
        textViewArray[24] = findViewById(R.id.textView56);
//sa
        textViewArray[25] = findViewById(R.id.textView57);
        textViewArray[26] = findViewById(R.id.textView58);
        textViewArray[27] = findViewById(R.id.textView59);
        textViewArray[28] = findViewById(R.id.textView60);
        textViewArray[29] = findViewById(R.id.textView61);
        textViewArray[30] = findViewById(R.id.textView62);
        textViewArray[31] = findViewById(R.id.textView63);
        textViewArray[32] = findViewById(R.id.textView64);
        textViewArray[33] = findViewById(R.id.textView65);
        textViewArray[34] = findViewById(R.id.textView66);
        textViewArray[35] = findViewById(R.id.textView67);
        textViewArray[36] = findViewById(R.id.textView68);
        textViewArray[37] = findViewById(R.id.textView69);
        textViewArray[38] = findViewById(R.id.textView70);
        textViewArray[39] = findViewById(R.id.textView71);
        textViewArray[40] = findViewById(R.id.textView72);
        textViewArray[41] = findViewById(R.id.textView73);
        textViewArray[42] = findViewById(R.id.textView74);
        textViewArray[43] = findViewById(R.id.textView75);
        textViewArray[44] = findViewById(R.id.textView76);
        textViewArray[45] = findViewById(R.id.textView77);
        textViewArray[46] = findViewById(R.id.textView78);
        textViewArray[47] = findViewById(R.id.textView79);
        textViewArray[48] = findViewById(R.id.textView80);
//ca
        textViewArray[49] = findViewById(R.id.textView81);
        textViewArray[50] = findViewById(R.id.textView82);
        textViewArray[51] = findViewById(R.id.textView83);
        textViewArray[52] = findViewById(R.id.textView84);
        textViewArray[53] = findViewById(R.id.textView85);
        textViewArray[54] = findViewById(R.id.textView86);
        textViewArray[55] = findViewById(R.id.textView87);
        textViewArray[56] = findViewById(R.id.textView88);
        textViewArray[57] = findViewById(R.id.textView89);
        textViewArray[58] = findViewById(R.id.textView90);
        textViewArray[59] = findViewById(R.id.textView91);
        textViewArray[60] = findViewById(R.id.textView92);
        textViewArray[61] = findViewById(R.id.textView93);
        textViewArray[62] = findViewById(R.id.textView94);
        textViewArray[63] = findViewById(R.id.textView95);
        textViewArray[64] = findViewById(R.id.textView96);
        textViewArray[65] = findViewById(R.id.textView97);
        textViewArray[66] = findViewById(R.id.textView98);
        textViewArray[67] = findViewById(R.id.textView99);
        textViewArray[68] = findViewById(R.id.textView100);
        textViewArray[69] = findViewById(R.id.textView101);
        textViewArray[70] = findViewById(R.id.textView102);
        textViewArray[71] = findViewById(R.id.textView103);
        textViewArray[72] = findViewById(R.id.textView104);
        //pe
        textViewArray[73] = findViewById(R.id.textView105);
        textViewArray[74] = findViewById(R.id.textView106);
        textViewArray[75] = findViewById(R.id.textView107);
        textViewArray[76] = findViewById(R.id.textView108);
        textViewArray[77] = findViewById(R.id.textView109);
        textViewArray[78] = findViewById(R.id.textView110);
        textViewArray[79] = findViewById(R.id.textView111);
        textViewArray[80] = findViewById(R.id.textView112);
        textViewArray[81] = findViewById(R.id.textView113);
        textViewArray[82] = findViewById(R.id.textView114);
        textViewArray[83] = findViewById(R.id.textView115);
        textViewArray[84] = findViewById(R.id.textView116);
        textViewArray[85] = findViewById(R.id.textView117);
        textViewArray[86] = findViewById(R.id.textView118);
        textViewArray[87] = findViewById(R.id.textView119);
        textViewArray[88] = findViewById(R.id.textView120);
        textViewArray[89] = findViewById(R.id.textView121);
        textViewArray[90] = findViewById(R.id.textView122);
        textViewArray[91] = findViewById(R.id.textView123);
        textViewArray[92] = findViewById(R.id.textView124);
        textViewArray[93] = findViewById(R.id.textView125);
        textViewArray[94] = findViewById(R.id.textView126);
        textViewArray[95] = findViewById(R.id.textView127);
        textViewArray[96] = findViewById(R.id.textView128);
        //cu
        textViewArray[97] = findViewById(R.id.textView129);
        textViewArray[98] = findViewById(R.id.textView130);
        textViewArray[99] = findViewById(R.id.textView131);
        textViewArray[100] = findViewById(R.id.textView132);
        textViewArray[101] = findViewById(R.id.textView133);
        textViewArray[102] = findViewById(R.id.textView134);
        textViewArray[103] = findViewById(R.id.textView135);
        textViewArray[104] = findViewById(R.id.textView136);
        textViewArray[105] = findViewById(R.id.textView137);
        textViewArray[106] = findViewById(R.id.textView138);
        textViewArray[107] = findViewById(R.id.textView139);
        textViewArray[108] = findViewById(R.id.textView140);
        textViewArray[109] = findViewById(R.id.textView141);
        textViewArray[110] = findViewById(R.id.textView142);
        textViewArray[111] = findViewById(R.id.textView143);
        textViewArray[112] = findViewById(R.id.textView144);
        textViewArray[113] = findViewById(R.id.textView145);
        textViewArray[114] = findViewById(R.id.textView146);
        textViewArray[115] = findViewById(R.id.textView147);
        textViewArray[116] = findViewById(R.id.textView148);
        textViewArray[117] = findViewById(R.id.textView149);
        textViewArray[118] = findViewById(R.id.textView150);
        textViewArray[119] = findViewById(R.id.textView151);
        textViewArray[120] = findViewById(R.id.textView152);
        //ct
        textViewArray[121] = findViewById(R.id.textView153);
        textViewArray[122] = findViewById(R.id.textView154);
        textViewArray[123] = findViewById(R.id.textView155);
        textViewArray[124] = findViewById(R.id.textView156);
        textViewArray[125] = findViewById(R.id.textView157);
        textViewArray[126] = findViewById(R.id.textView158);
        textViewArray[127] = findViewById(R.id.textView159);
        textViewArray[128] = findViewById(R.id.textView160);
        textViewArray[129] = findViewById(R.id.textView161);
        textViewArray[130] = findViewById(R.id.textView162);
        textViewArray[131] = findViewById(R.id.textView163);
        textViewArray[132] = findViewById(R.id.textView164);
        textViewArray[133] = findViewById(R.id.textView165);
        textViewArray[134] = findViewById(R.id.textView166);
        textViewArray[135] = findViewById(R.id.textView167);
        textViewArray[136] = findViewById(R.id.textView168);
        textViewArray[137] = findViewById(R.id.textView169);
        textViewArray[138] = findViewById(R.id.textView170);
        textViewArray[139] = findViewById(R.id.textView171);
        textViewArray[140] = findViewById(R.id.textView172);
        textViewArray[141] = findViewById(R.id.textView173);
        textViewArray[142] = findViewById(R.id.textView174);
        textViewArray[143] = findViewById(R.id.textView175);
        textViewArray[144] = findViewById(R.id.textView176);
        //pa
        textViewArray[145] = findViewById(R.id.textView177);
        textViewArray[146] = findViewById(R.id.textView178);
        textViewArray[147] = findViewById(R.id.textView179);
        textViewArray[148] = findViewById(R.id.textView180);
        textViewArray[149] = findViewById(R.id.textView181);
        textViewArray[150] = findViewById(R.id.textView182);
        textViewArray[151] = findViewById(R.id.textView183);
        textViewArray[152] = findViewById(R.id.textView184);
        textViewArray[153] = findViewById(R.id.textView185);
        textViewArray[154] = findViewById(R.id.textView186);
        textViewArray[155] = findViewById(R.id.textView187);
        textViewArray[156] = findViewById(R.id.textView188);
        textViewArray[157] = findViewById(R.id.textView189);
        textViewArray[158] = findViewById(R.id.textView190);
        textViewArray[159] = findViewById(R.id.textView191);
        textViewArray[160] = findViewById(R.id.textView192);
        textViewArray[161] = findViewById(R.id.textView193);
        textViewArray[162] = findViewById(R.id.textView194);
        textViewArray[163] = findViewById(R.id.textView195);
        textViewArray[164] = findViewById(R.id.textView196);
        textViewArray[165] = findViewById(R.id.textView197);
        textViewArray[166] = findViewById(R.id.textView198);
        textViewArray[167] = findViewById(R.id.textView199);
        textViewArray[0] = findViewById(R.id.textView200); //Pazar gününün son elemanı
    }

    private void haftalikBilgileriGuncelle() {
        getZiller();

        for (int i = 0; i < zillers.size(); i++) {
            //veritabanındaki saat:dakika
            int saat = saatGetir(zillers.get(i).getZaman());
            String gun = zillers.get(i).getGunler();

            int ekle = 0;
            for (int j = 0; j < 7; j++) {
                //7 gün vtden gelen
                if (gun.startsWith("1", j)) {
                    ekle = 24 * j;
                    //24 saat mod
                    textViewArray[ekle + saat - 6].setText(zillers.get(i).getName());
                    textViewArray[ekle + saat - 6].setTextSize(12.0f);

                    if (zillers.get(i).getAktif() != 1) {
                        textViewArray[ekle + saat - 6].setBackgroundColor(getResources().getColor(R.color.acikGri));
                        textViewArray[ekle + saat - 6].setTextColor(getResources().getColor(R.color.koyuGri));
                    } else {
                        textViewArray[ekle + saat - 6].setTextColor(getResources().getColor(R.color.beyaz));
                        textViewArray[ekle + saat - 6].setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }
                }
            }
        }//for zillers

        Calendar calendar = Calendar.getInstance();
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        //PAZAR 1, pzt 2, sali 3, çars 4, pers 5, cuma 6, cmt 7
        if (weekday == Calendar.MONDAY) {
            if ((24 * 0) + hour - 6 <= 0)
                textViewArray[(24 * 1) + hour - 6].setBackgroundColor(getResources().getColor(R.color.colorAccent));
            else
                textViewArray[(24 * 0) + hour - 6].setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else if (weekday == Calendar.TUESDAY) {
            if ((24 * 1) + hour - 6 <= 24)
                textViewArray[(24 * 2) + hour - 6].setBackgroundColor(getResources().getColor(R.color.colorAccent));
            else
                textViewArray[(24 * 1) + hour - 6].setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else if (weekday == Calendar.WEDNESDAY) {
            if ((24 * 2) + hour - 6 <= 24 * 2)
                textViewArray[(24 * 3) + hour - 6].setBackgroundColor(getResources().getColor(R.color.colorAccent));
            else
                textViewArray[(24 * 2) + hour - 6].setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else if (weekday == Calendar.THURSDAY) {
            if ((24 * 3) + hour - 6 <= 24 * 3)
                textViewArray[(24 * 4) + hour - 6].setBackgroundColor(getResources().getColor(R.color.colorAccent));
            else
                textViewArray[(24 * 3) + hour - 6].setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else if (weekday == Calendar.FRIDAY) {
            if ((24 * 4) + hour - 6 <= 24 * 4)
                textViewArray[(24 * 5) + hour - 6].setBackgroundColor(getResources().getColor(R.color.colorAccent));
            else
                textViewArray[(24 * 4) + hour - 6].setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else if (weekday == Calendar.SATURDAY) {
            if ((24 * 5) + hour - 6 <= 24 * 5)
                textViewArray[(24 * 6) + hour - 6].setBackgroundColor(getResources().getColor(R.color.colorAccent));
            else
                textViewArray[(24 * 5) + hour - 6].setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else if (weekday == Calendar.SUNDAY) {
            if ((24 * 6) + hour - 6 == 24 * 6)
                textViewArray[(24 * 0) + hour - 6].setBackgroundColor(getResources().getColor(R.color.colorAccent));
            else if ((24 * 6) + hour - 6 < 24 * 6)
                textViewArray[(24 * 6) + hour + 18].setBackgroundColor(getResources().getColor(R.color.colorAccent));
            else
                textViewArray[(24 * 6) + hour - 6].setBackgroundColor(getResources().getColor(R.color.colorAccent));
            //textViewArray[(24 * 6) + hour - 6].setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }

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

    private void geri() {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
    }

}