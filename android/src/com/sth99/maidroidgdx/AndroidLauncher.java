package com.sth99.maidroidgdx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.sth99.maidroidgdx.MaiDroidGdx;

public class AndroidLauncher extends AndroidApplication {
    private MaiDroidGdx mdg;

//    private PowerManager powerManager;
//    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        config.hideStatusBar = true;
        config.useImmersiveMode = true;
        config.maxSimultaneousSounds = 32;
        config.useWakelock = true;
        mdg = new MaiDroidGdx(new Runnable() {
            @Override
            public void run() {
                AndroidLauncher.this.finish();
            }
        });
        initialize(mdg, config);
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key.  The default implementation simply finishes the current activity,
     * but you can override this to do whatever you want.
     */
    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
