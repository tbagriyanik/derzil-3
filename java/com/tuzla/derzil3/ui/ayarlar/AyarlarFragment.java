package com.tuzla.derzil3.ui.ayarlar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tuzla.derzil3.MainActivity;
import com.tuzla.derzil3.R;

public class AyarlarFragment extends Fragment {

    SharedPreferences pref;
    private AyarlarViewModel mViewModel;

    public static AyarlarFragment newInstance() {
        return new AyarlarFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View vw = inflater.inflate(R.layout.ayarlar_fragment2, container, false);

        //TANIMLAMALAR
        final Switch sw1 = vw.findViewById(R.id.switch1);
        final Switch sw2 = vw.findViewById(R.id.switch2);
        final SeekBar red = vw.findViewById(R.id.seekBarR);
        final SeekBar gre = vw.findViewById(R.id.seekBarG);
        final SeekBar blu = vw.findViewById(R.id.seekBarB);
        final SeekBar alp = vw.findViewById(R.id.seekBarA);
        final TextView TextRgba = vw.findViewById(R.id.textViewRGBA);
        final Button defaultVal = vw.findViewById(R.id.buttonDefault);
        final RadioGroup rg1 = vw.findViewById(R.id.widgetFontSize);

        pref = this.getActivity().getSharedPreferences("derzilPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                geri();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        FloatingActionButton fabHome = vw.findViewById(R.id.fabHome);
        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geri();
            }
        });

        //VARSAYILAN tercihler
        if (!pref.contains("titresim")) {
            editor.putBoolean("titresim", false);
            editor.apply();
        }
        if (!pref.contains("ses")) {
            editor.putBoolean("ses", true);
            editor.apply();
        }
        if (!pref.contains("alpha")) {
            editor.putInt("alpha", 50);
            editor.apply();
        }
        if (!pref.contains("fontSize")) {
            editor.putInt("fontSize", 2);
            editor.apply();
        }

        //NESNELERİN ilk değerleri
        sw1.setChecked(pref.getBoolean("titresim", false));
        sw2.setChecked(pref.getBoolean("ses", true));

        red.setProgress(pref.getInt("red", 61));
        gre.setProgress(pref.getInt("green", 90));
        blu.setProgress(pref.getInt("blue", 254));
        alp.setProgress(pref.getInt("alpha", 50));
        TextRgba.setBackgroundColor(Color.argb(
                alp.getProgress()
                , red.getProgress()
                , gre.getProgress()
                , blu.getProgress()));
        switch (pref.getInt("fontSize", 2)) {
            case 1:
                rg1.check(R.id.radioButton1);
                break;
            case 2:
                rg1.check(R.id.radioButton2);
                break;
            case 3:
                rg1.check(R.id.radioButton3);
                break;
            case 4:
                rg1.check(R.id.radioButton4);
                break;
        }

        //NESNELERİN olayları
        defaultVal.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              sw1.setChecked(false);
                                              sw2.setChecked(true);
                                              sw1.callOnClick();
                                              sw2.callOnClick();
                                              red.setProgress(61);
                                              gre.setProgress(90);
                                              blu.setProgress(254);
                                              alp.setProgress(50);
                                              rg1.check(R.id.radioButton2);
                                              rg1.callOnClick();
                                          }
                                      }
        );

        sw1.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       SharedPreferences.Editor editor = pref.edit();
                                       editor.putBoolean("titresim", sw1.isChecked());
                                       editor.apply();
                                   }
                               }
        );
        sw2.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       SharedPreferences.Editor editor = pref.edit();
                                       editor.putBoolean("ses", sw2.isChecked());
                                       editor.apply();
                                   }
                               }
        );

        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("red", progress);
                editor.apply();
                TextRgba.setBackgroundColor(Color.argb(
                        alp.getProgress()
                        , red.getProgress()
                        , gre.getProgress()
                        , blu.getProgress()));
            }
        });
        gre.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("green", progress);
                editor.apply();
                TextRgba.setBackgroundColor(Color.argb(
                        alp.getProgress()
                        , red.getProgress()
                        , gre.getProgress()
                        , blu.getProgress()));
            }
        });
        blu.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("blue", progress);
                editor.apply();
                TextRgba.setBackgroundColor(Color.argb(
                        alp.getProgress()
                        , red.getProgress()
                        , gre.getProgress()
                        , blu.getProgress()));
            }
        });
        alp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("alpha", progress);
                editor.apply();
                TextRgba.setBackgroundColor(Color.argb(
                        alp.getProgress()
                        , red.getProgress()
                        , gre.getProgress()
                        , blu.getProgress()));
            }
        });
        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = pref.edit();
                switch (checkedId) {
                    case R.id.radioButton1:
                        editor.putInt("fontSize", 1);
                        editor.apply();
                        break;
                    case R.id.radioButton2:
                        editor.putInt("fontSize", 2);
                        editor.apply();
                        break;
                    case R.id.radioButton3:
                        editor.putInt("fontSize", 3);
                        editor.apply();
                        break;
                    case R.id.radioButton4:
                        editor.putInt("fontSize", 4);
                        editor.apply();
                        break;
                }
            }
        });

        return vw;
    }

    private void geri() {
        Intent myIntent = new Intent(getActivity(), MainActivity.class);
        startActivityForResult(myIntent, 0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AyarlarViewModel.class);

    }

}
