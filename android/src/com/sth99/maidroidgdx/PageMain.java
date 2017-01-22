package com.sth99.maidroidgdx;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.HashMap;

public class PageMain extends Activity {

    private ImageButton btAutoPlay;
    private ImageButton btPlay;
    private ImageButton btSettings;
    private ImageButton btChooseMap;
    private ImageButton btLinks;
    private MainPageButtonListener defaultListener = new MainPageButtonListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainscreen);
        btChooseMap = (ImageButton) findViewById(R.id.imageButtonChooseMap);
        btAutoPlay = (ImageButton) findViewById(R.id.imageButtonAutoPlay);
        btPlay = (ImageButton) findViewById(R.id.imageButtonPlay);
        btSettings = (ImageButton) findViewById(R.id.ImageButtonSettings);
        btLinks = (ImageButton) findViewById(R.id.ImageButtonLink);

        btChooseMap.setOnClickListener(new MainPageButtonListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                startActivity(new Intent(PageMain.this, ChooseMapActivity.class));
            }
        });
        btAutoPlay.setOnClickListener(new MainPageButtonListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                Engine.autoPlay = true;
                startActivity(new Intent(PageMain.this, AndroidLauncher.class));
            }
        });
        btPlay.setOnClickListener(new MainPageButtonListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                Engine.autoPlay = false;
                startActivity(new Intent(PageMain.this, AndroidLauncher.class));
            }
        });
        btSettings.setOnClickListener(new MainPageButtonListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                startActivity(new Intent(PageMain.this, SettingsActivity.class));
            }
        });
        btLinks.setOnClickListener(new MainPageButtonListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                startActivity(new Intent(PageMain.this, LinkActivity.class));
            }
        });
        SharedPreferences shp = getSharedPreferences("com.sth99.maidroidgdx_preferences", MODE_PRIVATE);
        SettingsActivity.universalPrefs = new HashMap<>();
        SettingsActivity.universalPrefs.putAll(shp.getAll());
        System.out.println(SettingsActivity.universalPrefs);
        Engine.setApplySettings(new P2GSetter());
    }
}

class MainPageButtonListener implements View.OnClickListener {
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        //do something shared
    }
}
