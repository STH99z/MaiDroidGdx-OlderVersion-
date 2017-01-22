package com.sth99.maidroidgdx;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ChooseMapActivity extends Activity {
    private Button bBA;
    private Button bBK;
    private Button bCA;
    private Button bCY;
    private Button bOB;
    private Button bCN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_map);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        bBA = (Button) findViewById(R.id.bMIAba);
        bBK = (Button) findViewById(R.id.bMIAbbkk);
        bCA = (Button) findViewById(R.id.bMIAcali);
        bCY = (Button) findViewById(R.id.bMIAcyc);
        bOB = (Button) findViewById(R.id.bMIAoboro);
        bCN = (Button) findViewById(R.id.bMIAconn);
        bBA.setOnClickListener(new AssetSongClickListener("ba", getApplicationContext()));
        bBK.setOnClickListener(new AssetSongClickListener("bbkk", getApplicationContext()));
        bCA.setOnClickListener(new AssetSongClickListener("caliburne", getApplicationContext()));
        bCY.setOnClickListener(new AssetSongClickListener("cycles", getApplicationContext()));
        bOB.setOnClickListener(new AssetSongClickListener("oboro", getApplicationContext()));
        bCN.setOnClickListener(new AssetSongClickListener("connect", getApplicationContext()));
    }
}

class AssetSongClickListener implements View.OnClickListener {
    private String mapDirName;
    private Context appContext;

    public AssetSongClickListener(String mapDirName, Context appContext) {
        this.mapDirName = mapDirName;
        this.appContext = appContext;
    }

    @Override
    public void onClick(View v) {
        Engine.mapDirName = mapDirName;
        Toast toast = Toast.makeText(appContext, "Map \"" + mapDirName + "\" selected.", Toast.LENGTH_SHORT);
        toast.show();
    }
}