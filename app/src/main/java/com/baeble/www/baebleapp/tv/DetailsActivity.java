package com.baeble.www.baebleapp.tv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baeble.www.baebleapp.R;
import com.baeble.www.baebleapp.Video_Activity;
import com.baeble.www.baebleapp.model.FeaturedVideo;
import com.baeble.www.baebleapp.tv.models.Card;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailsActivity extends FragmentActivity {
    public static final String EXTRA_IMAGE = "image";
    public static final String EXTRA_BAND_NAME = "bandName";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_DESC = "videoDescription";
    public static final String EXTRA_CUR_POS = "curPosition";
    public static final String EXTRA_VIDEO_CATEGORY = "video_category";
    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_VIDEOS = "array list videos";
    private Card selectedCard;
    private String category = "";
    ArrayList<Card> mFeaturedVideos;
    RecyclerView recyclerSuggestedVideos;
    MoviesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);

        initCard();
        displayCard();

        Button button = (Button) findViewById(R.id.play_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVideo();
            }
        });

        ArrayList itemsList = new ArrayList();
        itemsList.addAll(mFeaturedVideos);

        recyclerSuggestedVideos = (RecyclerView) findViewById(R.id.bottom_content);
        if (recyclerSuggestedVideos != null) {
            if (recyclerSuggestedVideos.getAdapter() != null) {
                MoviesAdapter mAdapter = (MoviesAdapter) recyclerSuggestedVideos.getAdapter();
                mAdapter.moviesList.clear();
                mAdapter.moviesList.addAll(itemsList);
            } else {
                mAdapter = new MoviesAdapter(itemsList);
                recyclerSuggestedVideos.setAdapter(mAdapter);
            }
        }
        button.requestFocus();
    }

    private void displayCard() {
        ImageView videoImage = (ImageView) findViewById(R.id.video_image);
        Picasso.with(this)
                .load(selectedCard.getImageUrl())
                .error(R.drawable.thumb_size)
                .placeholder(R.drawable.thumb_size)
                .into(videoImage);

        TextView contentTitle = (TextView) findViewById(R.id.content_caption);
        contentTitle.setText(selectedCard.getTitle());
        TextView contentSubTitle = (TextView) findViewById(R.id.content_sub_caption);
        contentSubTitle.setText(selectedCard.getBandName());
        TextView contentDescription = (TextView) findViewById(R.id.content_description);
        contentDescription.setText(selectedCard.getVideoDescription());
    }

    private void initCard() {
        mFeaturedVideos = new ArrayList<Card>();
        selectedCard = new Card();
        Intent videoToDisplay = getIntent();
        if (videoToDisplay != null) {
            selectedCard.setTitle(videoToDisplay.getStringExtra(EXTRA_TITLE));
            selectedCard.setImageUrl(videoToDisplay.getStringExtra(EXTRA_IMAGE));
            selectedCard.setBandName(videoToDisplay.getStringExtra(EXTRA_BAND_NAME));
            selectedCard.setVideoDescription(videoToDisplay.getStringExtra(EXTRA_DESC));
            selectedCard.setPosition(videoToDisplay.getIntExtra(EXTRA_CUR_POS, 0));
            selectedCard.setCategory(videoToDisplay.getStringExtra(EXTRA_VIDEO_CATEGORY));
            category = videoToDisplay.getStringExtra(EXTRA_CATEGORY);
            ArrayList<FeaturedVideo> featureVideos = videoToDisplay.getParcelableArrayListExtra(EXTRA_VIDEOS);

            for (int pointer = 0; pointer < featureVideos.size(); pointer += 1) {
                FeaturedVideo featuredVideo = featureVideos.get(pointer);
                Card card = new Card();
                card.setImageUrl(featuredVideo.getVideoImage());
                card.setBandName(featuredVideo.getBandName());
                card.setTitle(featuredVideo.getVideoName());
                card.setVideoDescription(featuredVideo.getVideoShortDescription());
                card.setPosition(pointer);
                card.setCategory(featuredVideo.getVideoType());
                mFeaturedVideos.add(card);
            }
            mFeaturedVideos.remove(selectedCard.getPosition());
        }
    }

    public void replaceCardWith(int positionInsertAt) {
        Card newToDisplay = mAdapter.moviesList.get(positionInsertAt);
        mAdapter.moviesList.remove(positionInsertAt);
        mAdapter.moviesList.add(positionInsertAt, selectedCard);

        mAdapter.notifyDataSetChanged();
        selectedCard = newToDisplay;
        displayCard();
    }

    private void startVideo() {
        Intent intent = new Intent(DetailsActivity.this, Video_Activity.class);
        intent.putExtra(DetailsActivity.EXTRA_CUR_POS, selectedCard.getPosition());
        intent.putExtra(DetailsActivity.EXTRA_CATEGORY, category);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (getCurrentFocus() != null && getCurrentFocus().getClass() != RelativeLayout.class) {
                onBackPressed();
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

        private ArrayList<Card> moviesList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public ImageView videoImage;

            public MyViewHolder(View view) {
                super(view);
                view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            v.animate().scaleX(1.2f).start();
                            v.animate().scaleY(1.2f).start();
                        } else {
                            v.animate().scaleX(1).start();
                            v.animate().scaleY(1).start();
                        }
                    }
                });
                title = (TextView) view.findViewById(R.id.title);
                videoImage = (ImageView) view.findViewById(R.id.video_image_small);
            }
        }


        public MoviesAdapter(ArrayList<Card> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.detailes_more_video_card, parent, false);

            itemView.setOnClickListener(new MyOnClickListener());

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Card movie = moviesList.get(position);
            Picasso.with(DetailsActivity.this)
                    .load(moviesList.get(position).getImageUrl())
                    .error(R.drawable.thumb_size)
                    .placeholder(R.drawable.thumb_size).into(holder.videoImage);
            holder.title.setText(movie.getTitle());
            holder.title.setTag(position);
        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int itemPosition = recyclerSuggestedVideos.indexOfChild(v);
            if (v.getClass().equals(RelativeLayout.class)) {
                if (v.findViewById(R.id.title) != null) {
                    View title = v.findViewById(R.id.title);
                    int position = (int) title.getTag();
                    replaceCardWith(position);
                    mFeaturedVideos.get(position).getTitle();
                }
            }
        }
    }

}
