/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.baeble.www.baebleapp.tv.app.page;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.TitleViewAdapter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baeble.www.baebleapp.R;
import com.baeble.www.baebleapp.tv.PageAndListRowActivity;
import com.baeble.www.baebleapp.tv.SearchActivity;

/**
 * Custom title view to be used in {@link android.support.v17.leanback.app.BrowseFragment}.
 */
public class CustomTitleView extends RelativeLayout implements TitleViewAdapter.Provider {
    private final View mSearchOrbView;

    private final TitleViewAdapter mTitleViewAdapter = new TitleViewAdapter() {
        @Override
        public View getSearchAffordanceView() {
            return mSearchOrbView;
        }

        @Override
        public void setTitle(CharSequence titleText) {
        }

        @Override
        public void setBadgeDrawable(Drawable drawable) {
            //CustomTitleView.this.setBadgeDrawable(drawable);
        }

        @Override
        public void updateComponentsVisibility(int flags) {

            int visibility = (flags & SEARCH_VIEW_VISIBLE) == SEARCH_VIEW_VISIBLE
                    ? View.VISIBLE : View.INVISIBLE;
            mSearchOrbView.setVisibility(visibility);
        }
    };

    public CustomTitleView(Context context) {
        this(context, null);
    }

    public CustomTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        View root = LayoutInflater.from(context).inflate(R.layout.tv_custom_titleview, this);
        mSearchOrbView = root.findViewById(R.id.search_orb);

        ImageView mIcon = (ImageView) mSearchOrbView.findViewById(R.id.icon);
        mIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        mSearchOrbView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getContext() instanceof PageAndListRowActivity) {
                    PageAndListRowActivity currentActivity = (PageAndListRowActivity) getContext();
                    Intent intent = new Intent(currentActivity, SearchActivity.class);
                    currentActivity.startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Could not apply search", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public TitleViewAdapter getTitleViewAdapter() {
        return mTitleViewAdapter;
    }
}
