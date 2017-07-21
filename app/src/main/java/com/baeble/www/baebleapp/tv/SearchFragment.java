package com.baeble.www.baebleapp.tv;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ListRowView;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SpeechRecognitionCallback;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.baeble.www.baebleapp.API.SvcApiRestCallback;
import com.baeble.www.baebleapp.API.SvcApiService;
import com.baeble.www.baebleapp.R;
import com.baeble.www.baebleapp.model.FeaturedVideo;
import com.baeble.www.baebleapp.model.SvcError;
import com.baeble.www.baebleapp.tv.app.page.OnItemViewedListener;
import com.baeble.www.baebleapp.tv.cards.presenters.CardPresenterSelector;
import com.baeble.www.baebleapp.tv.models.Card;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

public class SearchFragment extends android.support.v17.leanback.app.SearchFragment implements android.support.v17.leanback.app.SearchFragment.SearchResultProvider {

    private static final String TAG = SearchFragment.class.getSimpleName();
    private static final int REQUEST_SPEECH = 0x00000010;
    private ArrayObjectAdapter mRowsAdapter;
    private static final int SEARCH_DELAY_MS = 200;
    private Handler mHandler = new Handler();
    private SearchRunnable mDelayedLoad;
    private ArrayObjectAdapter listRowAdapter;
    private String finalSearchQuery = "";
    CardPresenterSelector cardPresenter;
    private int cartSpaces = 20;

    ArrayList<FeaturedVideo> searchResults;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSearchResultProvider(this);
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        mDelayedLoad = new SearchRunnable();
        mDelayedLoad.run();
        cardPresenter = new CardPresenterSelector(getActivity(), new CustomItemViewedListener());
        listRowAdapter = new ArrayObjectAdapter(cardPresenter);
        setSpeechRecognitionCallback(new SpeechRecognitionCallback() {
            @Override
            public void recognizeSpeech() {
                startRecording();
            }
        });

        mRowsAdapter.add(new ListRow(new HeaderItem(""), listRowAdapter));
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return mRowsAdapter;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        updateUI();
        mRowsAdapter.clear();
        if (!newQuery.trim().isEmpty()) {
            addHeader("Searching for \"" + newQuery + "\"");
        }
        if (!TextUtils.isEmpty(newQuery)) {
            finalSearchQuery = newQuery;
            mDelayedLoad.setSearchQuery(newQuery);
            mHandler.removeCallbacks(mDelayedLoad);
            mHandler.postDelayed(mDelayedLoad, SEARCH_DELAY_MS);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        updateUI();
        mRowsAdapter.clear();
        if (!query.trim().isEmpty()) {
            addHeader("Searching for \"" + query + "\"");
        }
        if (!TextUtils.isEmpty(query)) {
            finalSearchQuery = query;
            mDelayedLoad.setSearchQuery(query);
            mHandler.removeCallbacks(mDelayedLoad);
            mHandler.postDelayed(mDelayedLoad, SEARCH_DELAY_MS);
        }
        return true;
    }

    private void startRecording() {
        Log.v(TAG, "recognizeSpeech");
        try {
            startActivityForResult(getRecognizerIntent(), REQUEST_SPEECH);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Cannot find activity for speech recognizer", e);
        }
    }

    private class SearchRunnable implements Runnable {
        private String query;

        public void setSearchQuery(String query) {
            this.query = query;
        }

        @Override
        public void run() {
            if (query != null && !query.isEmpty()) {
                hideProgressBar(false);
                loadData(query);
            }
        }
    }

    private void loadData(final String strKeyword) {
        listRowAdapter.clear();
        SvcApiService.getUserIdEndPoint().GetSearchVideos(strKeyword, getString(R.string.all_categories), new SvcApiRestCallback<List<FeaturedVideo>>() {
            @Override
            public void failure(SvcError svcError) {
                hideProgressBar(true);
            }

            @Override
            public void success(List<FeaturedVideo> featuredVideos, Response response) {

                if (!finalSearchQuery.trim().equals(strKeyword.trim())) {
                    hideProgressBar(true);
                    return;
                }
                if (featuredVideos != null) {
                    mRowsAdapter.clear();
                    HeaderItem header = new HeaderItem("Search results \"" + strKeyword + "\"");
                    searchResults = new ArrayList<FeaturedVideo>();
                    for (int i = 0; i < featuredVideos.size(); i++) {
                        FeaturedVideo video = featuredVideos.get(i);
                        Card card = new Card();
                        card.setVideoDescription(video.getVideoShortDescription());
                        card.setDescription(video.getVideoShortDescription());
                        card.setTitle(video.getVideoName());
                        card.setBandName(video.getBandName());
                        card.setImageUrl(video.getVideoImage());
                        card.setCategory(video.getVideoType());
                        card.setPosition(listRowAdapter.size());
                        card.setType(Card.Type.GRID_SQUARE);
                        listRowAdapter.add(card);
                    }
                    searchResults.addAll(featuredVideos);
                    mRowsAdapter.add(new ListRow(header, listRowAdapter));
                    hideProgressBar(true);
                } else {
                    hideProgressBar(true);
                    addHeader("No matches found for \"" + strKeyword + "\"");
                }
                updateUI();
            }
        });
    }

    private void updateUI() {
        if (getView() != null && ((ViewGroup) ((ViewGroup) getView()).getChildAt(0)).getChildAt(0) instanceof VerticalGridView) {
            VerticalGridView verticalGridView = ((VerticalGridView) ((ViewGroup) ((ViewGroup) getView()).getChildAt(0)).getChildAt(0));
            verticalGridView.setWindowAlignmentOffsetPercent(10);
            verticalGridView.offsetChildrenHorizontal(10);
            verticalGridView.setPadding(100, 0, 100, 0);

            LinearLayout firstChild = (LinearLayout) verticalGridView.getChildAt(0);
            if (firstChild != null && firstChild.getChildAt(1) != null && firstChild.getChildAt(1) instanceof ListRowView) {
                ListRowView listRowView = (ListRowView) firstChild.getChildAt(1);
                if (listRowView.getChildAt(0) != null && listRowView.getChildAt(0) instanceof HorizontalGridView) {
                    HorizontalGridView horizontalGridView = (HorizontalGridView) listRowView.getChildAt(0);
                    horizontalGridView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                        @Override
                        public void onChildViewAttachedToWindow(View view) {
                            if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                                p.setMargins(cartSpaces, 0, cartSpaces, 0);
                                view.requestLayout();
                            }
                        }

                        @Override
                        public void onChildViewDetachedFromWindow(View view) {
                        }
                    });
                }
            }
        }
    }

    private void addHeader(String headerText) {
        mRowsAdapter.clear();
        HeaderItem header = new HeaderItem(headerText);
        mRowsAdapter.add(new ListRow(header, new ArrayObjectAdapter()));
    }

    private class CustomItemViewedListener implements OnItemViewedListener {
        public CustomItemViewedListener() {
            super();
            setOnItemViewClickedListener(new OnItemViewClickedListener() {
                @Override
                public void onItemClicked(
                        Presenter.ViewHolder itemViewHolder,
                        Object item,
                        RowPresenter.ViewHolder rowViewHolder,
                        Row row) {
                    Card card = (Card) item;
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(DetailsActivity.EXTRA_IMAGE, card.getImageUrl());
                    intent.putExtra(DetailsActivity.EXTRA_BAND_NAME, card.getBandName());
                    intent.putExtra(DetailsActivity.EXTRA_TITLE, card.getTitle());
                    intent.putExtra(DetailsActivity.EXTRA_DESC, card.getVideoDescription());
                    intent.putExtra(DetailsActivity.EXTRA_CUR_POS, card.getPosition());
                    intent.putExtra(DetailsActivity.EXTRA_CATEGORY, card.getCategory());
                    ArrayList<FeaturedVideo> featuredVideos = new ArrayList<FeaturedVideo>(searchResults.subList(0, searchResults.size() > 100 ? 100 : searchResults.size()));
                    intent.putParcelableArrayListExtra(DetailsActivity.EXTRA_VIDEOS, featuredVideos);
                    startActivity(intent);
                }
            });
        }

        @Override
        public void onItemViewed(int position) {

        }
    }

    private void hideProgressBar(boolean hide) {
        if (getActivity() != null && getActivity().findViewById(R.id.progressBar) != null) {
            ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
            if (hide) {
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }
}
