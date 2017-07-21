package com.baeble.www.baebleapp.tv;

import android.os.Bundle;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.baeble.www.baebleapp.R;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (getCurrentFocus() != null) {
                if (getCurrentFocus().getClass() == ImageCardView.class) {

                } else {
                    onBackPressed();
                }
            } else {
                onBackPressed();
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (findViewById(R.id.lb_search_bar_speech_orb) != null) {
                findViewById(R.id.lb_search_bar_speech_orb).requestFocus();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
