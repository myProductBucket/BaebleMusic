package com.baeble.www.baebleapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupMenu;

import com.baeble.www.baebleapp.API.SvcApiRestCallback;
import com.baeble.www.baebleapp.API.SvcApiService;
import com.baeble.www.baebleapp.model.FeaturedVideo;
import com.baeble.www.baebleapp.model.SvcError;
import com.baeble.www.baebleapp.tv.DetailsActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by abc on 6/4/2015.
 */
public class Concerts_Fragment extends Fragment implements View.OnClickListener {
    private static final String TAG = Featured_Fragment.class.getSimpleName();
    GridView gridView_concert;
    String categoryName;
    public ConcertVideoGridAdapter adapter;
    ArrayList<FeaturedVideo> fVideos;
    private Handler mainHandler;

    private ProgressDialog progress;

    private boolean mLoading = true;
    private boolean mLastPage = false;
    private int mCurrentPage = 1;
    private int mVisibleThreshold = 100;
    private final static int ITEMS_PPAGE = 100;
    private String categoryApiName = "feat";

    private boolean isSucceed = true;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.concert_fragment, null);
        mainHandler = new Handler(getActivity().getMainLooper());
        progress = new ProgressDialog(getActivity());

        fVideos = new ArrayList<FeaturedVideo>();

        boolean isLandScapeMode = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        gridView_concert = (GridView) view.findViewById(R.id.gridView_concert);
        gridView_concert.setScrollingCacheEnabled(true);
        gridView_concert.setNumColumns(isLandScapeMode ? 3 : 2);
        gridView_concert.setHorizontalSpacing(10);
        gridView_concert.setVerticalSpacing(20);

        gridView_concert.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Main_Tab_Activity.featuredVideos = fVideos;
                Intent intent = new Intent(getActivity(), Video_Activity.class);
                intent.putExtra(DetailsActivity.EXTRA_CATEGORY, categoryApiName);
                intent.putExtra(DetailsActivity.EXTRA_CUR_POS, position);
                startActivity(intent);
            }
        });

        gridView_concert.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!mLastPage && !mLoading &&
                        (totalItemCount - visibleItemCount) <= (firstVisibleItem + mVisibleThreshold)) {
                    mLoading = true;
                    new AddItemsAsyncTask().execute();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        downloadCategoryVideo();
    }

    public void downloadCategoryVideo() {
        categoryName = Main_Tab_Activity.txt_tabCateroryDetail.getText().toString();
        String categoryProgressName = "new";

        switch (categoryName) {
            case "FEATURED":
                categoryApiName = "feat";
                categoryProgressName = getResources().getString(R.string.DownloadingFeaturedVideo);
                break;
            case "TRENDING":
                categoryApiName = "trend";
                categoryProgressName = getResources().getString(R.string.DownloadingTrendingVideo);
                break;
            case "CONCERTS":
                categoryApiName = "con";
                categoryProgressName = getResources().getString(R.string.DownloadingConcertVideo);
                break;
            case "MUSIC VIDEOS":
                categoryApiName = "mus";
                categoryProgressName = getResources().getString(R.string.DownloadingMusicVideo);
                break;
            case "INTERVIEWS":
                categoryApiName = "int";
                categoryProgressName = getResources().getString(R.string.DownloadingInterviewVideo);
                break;
            default:
                categoryApiName = "feat";
                categoryProgressName = getResources().getString(R.string.DownloadingFeaturedVideo);
                break;
        }

        progress = ProgressDialog.show(getActivity(), "Please wait", categoryProgressName,
                true, false);

        SvcApiService.getUserIdEndPoint().GetCategoryVideos(mCurrentPage, ITEMS_PPAGE, categoryApiName, new SvcApiRestCallback<List<FeaturedVideo>>() {
            @Override
            public void failure(SvcError svcError) {
                Log.d(TAG, "Failed to download featured video info");
                if (fVideos.size() > 0) {
                    fVideos.clear();
                    adapter = new ConcertVideoGridAdapter((Main_Tab_Activity) getActivity(), fVideos, Concerts_Fragment.this);
                    gridView_concert.setAdapter(adapter);
                }
                mLoading = false;
                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        showErrorMessage();
                    }
                };
                mainHandler.post(progressRunnable);
            }

            @Override
            public void success(List<FeaturedVideo> featuredVideos, Response response) {

                mLoading = false;
                isSucceed = true;

                if (featuredVideos != null) {
                    if (featuredVideos.size() > 0) {
                        Log.d(TAG, "Success to download featured video info = " + featuredVideos.size());
                        for (int videoIndex = 0; videoIndex < featuredVideos.size(); videoIndex++) {
                            if (checkJsonValidity(featuredVideos.get(videoIndex))) {
                                fVideos.add(featuredVideos.get(videoIndex));
                            } else {
                                isSucceed = false;
                                Log.d(TAG, "failed to check featured video validity index= " + videoIndex);
                                Log.d(TAG, "video name " + featuredVideos.get(videoIndex).getVideoName());
                                Log.d(TAG, "video image= " + featuredVideos.get(videoIndex).getVideoImage());
                                Log.d(TAG, "video band name= " + featuredVideos.get(videoIndex).getBandName());
                                Log.d(TAG, "video mp4 path= " + featuredVideos.get(videoIndex).getMp4Path());
                                Log.d(TAG, "video m3u8 path= " + featuredVideos.get(videoIndex).getM3u8Path());
                            }
                        }
                    } else {
                        isSucceed = false;
                    }
                } else {
                    isSucceed = false;
                }

                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        if (isSucceed) {
                            adapter = new ConcertVideoGridAdapter((Main_Tab_Activity) getActivity(), fVideos, Concerts_Fragment.this);
                            gridView_concert.setAdapter(adapter);
                        } else {
                            showErrorMessage();
                        }

                    }
                };
                mainHandler.post(progressRunnable);

            }
        });
    }

    @Override
    public void onClick(final View view) {
        Log.d(TAG, "onClick called!");
        view.post(new Runnable() {
            @Override
            public void run() {
                showPopupMenu(view);
            }
        });
    }

    private void showPopupMenu(View view) {
        // Retrieve the clicked item from view's tag
        final FeaturedVideo item = (FeaturedVideo) view.getTag();

        Context wrapper = new ContextThemeWrapper(getActivity(), R.style.AppThemeCustom);
        PopupMenu popup = new PopupMenu(wrapper, view);

        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.menu_video_list, popup.getMenu());
        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_one:
                        Main_Tab_Activity main = (Main_Tab_Activity) getActivity();
                        main.shareOnFacebook(item);
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adapter != null)
            adapter.countDownTimer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.countDownTimer.start();

        trackScreenName();
    }

    private void trackScreenName() {
        String name = "Category - Featured";
        switch (categoryName) {
            case "FEATURED":
                name = "Category - Featured";
                break;
            case "TRENDING":
                name = "Category - Trending";
                break;
            case "CONCERTS":
                name = "Category - Concerts";
                break;
            case "MUSIC VIDEOS":
                name = "Category - Music Videos";
                break;
            case "INTERVIEWS":
                name = "Category - Interviews";
                break;
        }
        Splash_Activity.processAnalyticsTracking(name);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean isLandScapeMode = (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
        gridView_concert.setNumColumns(isLandScapeMode ? 3 : 2);
        gridView_concert.setHorizontalSpacing(10);
        gridView_concert.setVerticalSpacing(20);
    }

    public class AddItemsAsyncTask extends AsyncTask<Integer, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... args) {

            mCurrentPage++;
            SvcApiService.getUserIdEndPoint().GetCategoryVideos(mCurrentPage, ITEMS_PPAGE, categoryApiName, new SvcApiRestCallback<List<FeaturedVideo>>() {
                @Override
                public void failure(SvcError svcError) {
                    Log.d(TAG, "Failed to download more concert video info");
                    progress.dismiss();
                    mLoading = false;
                }

                @Override
                public void success(List<FeaturedVideo> featuredVideos, Response response) {

                    if (featuredVideos != null) {
                        if (featuredVideos.size() > 0) {
                            Log.d(TAG, "Success to download more concert video info " + featuredVideos.size());
                            adapter.add(featuredVideos);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    progress.dismiss();
                    mLoading = false;
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String args) {

        }
    }

    private void showErrorMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Data Error");
        builder.setMessage("Press OK to try again");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                downloadCategoryVideo();
            }
        });

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private boolean checkJsonValidity(FeaturedVideo fvideo) {
        if (fvideo.getVideoType().isEmpty()
                || fvideo.getVideoName().isEmpty()
                || fvideo.getVideoImage().isEmpty()
                || fvideo.getBandName().isEmpty()
                || fvideo.getM3u8Path().isEmpty()
                || fvideo.getMp4Path().isEmpty())
            return false;

        return true;
    }
}