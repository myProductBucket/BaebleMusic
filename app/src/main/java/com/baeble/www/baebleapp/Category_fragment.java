package com.baeble.www.baebleapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.baeble.www.baebleapp.API.SvcApiRestCallback;
import com.baeble.www.baebleapp.API.SvcApiService;
import com.baeble.www.baebleapp.model.FeaturedVideo;
import com.baeble.www.baebleapp.model.SvcError;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

    public class Category_fragment extends Fragment {
        public static final String TAG = Category_fragment.class.getSimpleName();
	    GridView gridView_category;
        CategoryVideoGridAdapter adapter;
        public static List<FeaturedVideo>fVideos;
        public static Boolean isCategoryDownloadSucceed = false;
	    View view;
	    private Handler mainHandler;
	    private ProgressDialog progress;
        private boolean isSucceed = true;
        int categoryIdx = 0;
        String [] names={"Featured","Trending","Concerts","Music Videos","Interviews"};
        String [] categoryApiNames={"feat","trend","con","mus","int"};

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view=inflater.inflate(R.layout.concert_fragment,null);
            mainHandler = new Handler(getActivity().getMainLooper());
            progress = new ProgressDialog(getActivity());

            boolean isLandscapeMode = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

            gridView_category=(GridView)view.findViewById(R.id.gridView_concert);
            gridView_category.setScrollingCacheEnabled(true);
            gridView_category.setNumColumns(isLandscapeMode? 2 : 1);
            gridView_category.setHorizontalSpacing(isLandscapeMode? 20 : 0);
            gridView_category.setVerticalSpacing(20);

            gridView_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.tabFrameLayout, new Concerts_Fragment()).commit();
                    Main_Tab_Activity.txt_tabFeatured.setBackgroundResource(R.drawable.unsel);
                    Main_Tab_Activity.txt_tabCategory.setBackgroundResource(R.drawable.unsel);
                    Main_Tab_Activity.txt_tabCateroryDetail.setBackgroundResource(R.drawable.sel);
                    Main_Tab_Activity.txt_tabFeatured.setAlpha(0.5f);
                    Main_Tab_Activity.txt_tabCategory.setAlpha(0.5f);
                    Main_Tab_Activity.txt_tabCateroryDetail.setAlpha(1f);
                    Main_Tab_Activity.txt_tabCateroryDetail.setText(names[position].toUpperCase());
                    Main_Tab_Activity.txt_tabFeatured.setTextColor(Color.parseColor("#979797"));
                    Main_Tab_Activity.txt_tabCategory.setTextColor(Color.parseColor("#979797"));
                    Main_Tab_Activity.txt_tabCateroryDetail.setTextColor(Color.parseColor("#ec6d36"));
                }
            });
            return view;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            progress = ProgressDialog.show(getActivity(), "Please wait", getResources().getString(R.string.LoadingCategoryList),
                    true, false);
            if(isCategoryDownloadSucceed == false) {
                fVideos = new ArrayList<FeaturedVideo>();
                downloadCategoryList();
            }else {
                adapter = new CategoryVideoGridAdapter((Main_Tab_Activity) getActivity(), fVideos);
                gridView_category.setAdapter(adapter);
                progress.dismiss();
            }
        }

        private void downloadCategoryList(){
            SvcApiService.getUserIdEndPoint().GetCategoryVideos(1, 1, categoryApiNames[categoryIdx], new SvcApiRestCallback<List<FeaturedVideo>>() {
                @Override
                public void failure(SvcError svcError) {
                    Log.d(TAG, "Failed to download category list");

                    if(fVideos.size() > 0) {
                        fVideos.clear();
                        adapter = new CategoryVideoGridAdapter((Main_Tab_Activity) getActivity(), fVideos);
                        gridView_category.setAdapter(adapter);
                    }

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
                    isSucceed = true;
                    Log.d(TAG, "Success to download category list " + categoryApiNames[categoryIdx]);

                    if(featuredVideos != null) {
                        if (featuredVideos.size() > 0) {
                            fVideos.add(featuredVideos.get(0));
                        }else{
                            isSucceed = false;
                        }
                    }else{
                        isSucceed = false;
                    }

                    if (categoryIdx == (categoryApiNames.length - 1)) {
                        Runnable progressRunnable = new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                                if(isSucceed == true) {
                                    isCategoryDownloadSucceed = true;
                                    adapter = new CategoryVideoGridAdapter((Main_Tab_Activity) getActivity(), fVideos);
                                    gridView_category.setAdapter(adapter);
                                }else{
                                    showErrorMessage();
                                }
                            }
                        };
                        mainHandler.post(progressRunnable);
                    } else {
                        if(isSucceed == true) {
                            categoryIdx++;
                            downloadCategoryList();
                        }else{
                            Runnable progressRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                        showErrorMessage();
                                }
                            };
                            mainHandler.post(progressRunnable);
                        }
                    }
                }
            });
        }

        private void showErrorMessage(){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Data Error");
            builder.setMessage("Press OK to try again");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    downloadCategoryList();
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

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            boolean isLandscapeMode = (newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE);
            gridView_category.setNumColumns(isLandscapeMode? 2 : 1);
            gridView_category.setHorizontalSpacing(isLandscapeMode? 20 : 0);
            gridView_category.setVerticalSpacing(20);
        }

        @Override
        public void onResume() {
            super.onResume();
            Splash_Activity.processAnalyticsTracking("Category - Home");
        }
    }