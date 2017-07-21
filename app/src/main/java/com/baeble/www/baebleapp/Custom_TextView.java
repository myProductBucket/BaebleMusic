package com.baeble.www.baebleapp;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * Created by abc on 6/10/2015.
 */
    public class Custom_TextView extends TextView {
        public Custom_TextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/HelveticaNeue.ttf"));
        }
        public Custom_TextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/HelveticaNeue.ttf"));
        }
        public Custom_TextView(Context context) {
            super(context);
            this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/HelveticaNeue.ttf"));
        }
    }

