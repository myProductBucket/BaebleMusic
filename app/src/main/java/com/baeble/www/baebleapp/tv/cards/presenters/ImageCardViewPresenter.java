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

package com.baeble.www.baebleapp.tv.cards.presenters;

import android.content.Context;
import android.support.v17.leanback.widget.ImageCardView;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;

import com.baeble.www.baebleapp.R;
import com.baeble.www.baebleapp.tv.app.page.OnItemViewedListener;
import com.baeble.www.baebleapp.tv.models.Card;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

/**
 * A very basic {@link ImageCardView} {@link android.support.v17.leanback.widget.Presenter}.You can
 * pass a custom style for the ImageCardView in the constructor. Use the default constructor to
 * create a Presenter with a default ImageCardView style.
 */
public class ImageCardViewPresenter extends AbstractCardPresenter<ImageCardView> {

    private OnItemViewedListener onItemViewedListener;

    public ImageCardViewPresenter(Context context, int cardThemeResId, OnItemViewedListener onItemViewedListener) {
        super(new ContextThemeWrapper(context, cardThemeResId));
        this.onItemViewedListener = onItemViewedListener;
    }

    public ImageCardViewPresenter(Context context, OnItemViewedListener onItemViewedListener) {
        this(context, R.style.DefaultCardTheme, onItemViewedListener);
    }

    @Override
    protected ImageCardView onCreateView() {
        ImageCardView imageCardView = new ImageCardView(getContext());
//        imageCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "Clicked on ImageCardView", Toast.LENGTH_SHORT).show();
//            }
//        });
        return imageCardView;
    }

    @Override
    public void onBindViewHolder(Card card, final ImageCardView cardView) {
        cardView.setTag(card);
        cardView.setTitleText(card.getTitle());
        cardView.setContentText(card.getDescription());
        if (!TextUtils.isEmpty(card.getImageUrl())) {
            Glide.with(getContext())
                    .load(card.getImageUrl())
                    .asBitmap()
                    .into(cardView.getMainImageView());
        }
        if (onItemViewedListener != null) {
            onItemViewedListener.onItemViewed(card.getPosition());
        }
    }

}
