package com.tuzla.derzil3;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tuzla.derzil3.ui.ayarlar.AyarlarFragment;

public class ayarlarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ayarlar_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, AyarlarFragment.newInstance())
                    .commitNow();
        }
    }
}
