package com.baeble.www.baebleapp;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.baeble.www.baebleapp.tv.PageAndListRowActivity;

/**
 * Created by a.rehman.nazar@gmail.com on 12/23/16.
 */

public class StartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UiModeManager uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION)
        {
            Intent intent = new Intent(StartActivity.this, PageAndListRowActivity.class);
            startActivity(intent);
            StartActivity.this.finish();
        }
        else
        {
            Intent intent = new Intent(StartActivity.this, Splash_Activity.class);
            startActivity(intent);
            StartActivity.this.finish();
        }
    }
}
