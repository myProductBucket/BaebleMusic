/*
 * Copyright (C) 2016 The Android Open Source Project
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

import com.baeble.www.baebleapp.tv.models.Card;
import com.baeble.www.baebleapp.tv.models.VideoCard;
import com.bumptech.glide.Glide;

/**
 * Presenter for rendering video cards on the Vertical Grid fragment.
 */
public class VideoCardViewPresenter extends ImageCardViewPresenter {

    public VideoCardViewPresenter(Context context, int cardThemeResId) {
        super(context, cardThemeResId, null);
    }

    public VideoCardViewPresenter(Context context) {
        super(context, null);
    }

    @Override
    public void onBindViewHolder(Card card, final ImageCardView cardView) {
        super.onBindViewHolder(card, cardView);
        VideoCard videoCard = (VideoCard) card;
        Glide.with(getContext())
                .load(videoCard.getImageUrl())
                .asBitmap()
                .into(cardView.getMainImageView());

    }

}
