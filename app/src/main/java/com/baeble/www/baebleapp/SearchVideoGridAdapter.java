package com.baeble.www.baebleapp;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baeble.www.baebleapp.model.FeaturedVideo;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZaneH_000 on 10/18/2015.
 */

public class SearchVideoGridAdapter extends BaseAdapter {
    private static final String TAG = SearchVideoGridAdapter.class.getSimpleName();
    private int curPosition = 0;
    private int vnum = 0;
    List<FeaturedVideo> fVideos;
    private final Search_Activity main;
    private ImageLoader imageLoader;
    public CountDownTimer countDownTimer = null;
    private int prevItemPosition = 0;
    private List <ViewMap> backupViewList;
    private int preDisplayPosition = 0;
    private int visibleViewCount = 0;
    private int firstViewId = 0;
    private int maxVisibleCount = 10;
    private final int TIMER_CYCLE_COUNT = 100000;//ms
    private final int TIMER_TICK_COUNT = 200;//ms

    private class ViewMap{
        public View view;
        public int position;
    }

    SearchVideoGridAdapter(Search_Activity search_Activity, List < FeaturedVideo > featuredVideos){
        fVideos = featuredVideos;
        vnum = fVideos.size();
        main = search_Activity;
        backupViewList = new ArrayList<ViewMap>();

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                main.getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();
        ImageLoader.getInstance().init(config);

        imageLoader = ImageLoader.getInstance();

        resetTimer();
    }

    public void destory(){
        countDownTimer.cancel();
    }

    @Override
    public int getCount() {
        return fVideos.size();
    }
    @Override
    public Object getItem(int position) {
        return fVideos.get(position);
    }
    @Override
    public long getItemId(int position) {
        return  0;
    }

    static class ViewHolder{
        TextView featuredItemTitle;
        TextView artistItemTitle;
        TextView videoTypeTitle;
        ImageView featuredItemImage;
        LinearLayout concertMoreLayout;
    }

    public void add(List<FeaturedVideo> featuredVideos){
        for (int index = 0; index < featuredVideos.size(); index++) {
            fVideos.add(featuredVideos.get(index));
        }
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            Log.d(TAG, "list view item create index = " + position);
            view = main.getLayoutInflater().inflate(R.layout.search_list_item, null);
            holder = new ViewHolder();
            holder.featuredItemTitle = (TextView) view.findViewById(R.id.txt_titleSearchItem);
            holder.artistItemTitle = (TextView) view.findViewById(R.id.txt_artistSearchItem);
            holder.videoTypeTitle = (TextView) view.findViewById(R.id.txt_videoTypeSearchItem);
            holder.featuredItemImage = (ImageView) view.findViewById(R.id.img_gridSearchItem);
            holder.concertMoreLayout = (LinearLayout) view.findViewById(R.id.img_gridSearchMoreLayout);
            view.setTag(holder);
            view.setId(visibleViewCount);
            visibleViewCount++;

            int gridHeight = (Main_Tab_Activity.phoneWidth - 40) * 3 / 4;
            holder.featuredItemImage.getLayoutParams().height = gridHeight;

            Log.d(TAG, "Visible Maximum View Count = " + visibleViewCount);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.featuredItemTitle.setText(fVideos.get(position).getVideoName());
        holder.artistItemTitle.setText(fVideos.get(position).getBandName());
        holder.videoTypeTitle.setText(convertVideoType(fVideos.get(position).getVideoType()));
        holder.featuredItemImage.setImageResource(R.drawable.thumb_size);
        holder.concertMoreLayout.setTag(getItem(position));
        holder.concertMoreLayout.setOnClickListener(main);

        Log.d(TAG, "get view position = " + position);
        if (position == 0){
//            if(curPosition <= visibleViewCount) {
            if(curPosition <= 1) {
                String videoImgUrl = fVideos.get(position).getVideoImage();
                            imageLoader.displayImage(videoImgUrl, holder.featuredItemImage);
                if(curPosition == 3) {
                    firstViewId = view.getId();
                    Log.d(TAG, "Frist View Id = " + firstViewId);
                }
            }
        }else{
            curPosition = position;
            addViewMapToBackList(view, position);
        }

        return view;
    }

    private void addViewMapToBackList (View view, int position){
        ViewMap viewMap = new ViewMap();
        viewMap.view = view;
        viewMap.position = position;
        backupViewList.add(viewMap);
    }

    void resetTimer() {

        if(countDownTimer != null){
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(TIMER_CYCLE_COUNT, TIMER_TICK_COUNT) {

            public void onTick(long millisUntilFinished) {

                /*Notify that the video number changed due to download more videos*/
                if(vnum != fVideos.size()){
                    preDisplayPosition = 0;
                    curPosition = prevItemPosition = visibleViewCount;
                    vnum = fVideos.size();
                }

                //Log.d(TAG, "resetTimer: cur positon = " + curPosition + " pre Position = " + prevItemPosition + "Pre display point = " + preDisplayPosition);
                if(curPosition == 0 || fVideos.size() == 0)
                    return;

                if(curPosition == preDisplayPosition){
                    return;
                }

                if(curPosition == prevItemPosition){
                    int displayCount = 0;

                    if(backupViewList.size() > 0){
                        boolean isUsedView[] = new boolean[visibleViewCount];
                        for(int viewIdx = 0; viewIdx < visibleViewCount; viewIdx++)
                            isUsedView[viewIdx] = false;

                        if (Math.abs(curPosition - preDisplayPosition) < maxVisibleCount)
                            displayCount = Math.abs(curPosition - preDisplayPosition);
                        else
                            displayCount = maxVisibleCount;

                        int startIndex = backupViewList.size() - 1;
                        int downLimit = backupViewList.size() - displayCount;

                        if(downLimit < 0)
                            downLimit = 0;

                        Log.d(TAG, "startIdx = " + startIndex + " downLimit = " + downLimit + " displayCount = " + displayCount);

                        for (int displayIdx = startIndex; displayIdx >= downLimit; displayIdx--) {
                            View curView = backupViewList.get(displayIdx).view;

                            if(curPosition == 1 && firstViewId == curView.getId()){
                                isUsedView[firstViewId] = true;
                                //Log.d(TAG, "Disable view index = " + firstViewId);
                            }

                            if(isUsedView[curView.getId()] == false){
                                isUsedView[curView.getId()] = true;
                                ViewHolder holder = (ViewHolder) curView.getTag();
                                int imageIndex = backupViewList.get(displayIdx).position;
                                String videoImgUrl = fVideos.get(imageIndex).getVideoImage();

                                imageLoader.displayImage(videoImgUrl, holder.featuredItemImage);
                                Log.d(TAG, "dispaly video index = " + imageIndex + " viewId = " + curView.getId());
                            }else{
                                Log.d(TAG, "used video view = " + curView.getId());
                            }
                        }
                        preDisplayPosition = curPosition;
                    }
                }

                prevItemPosition = curPosition;
            }

            public void onFinish() {
                Log.d(TAG, "onFinish called and restart timer");
                resetTimer();
            }
        };
        countDownTimer.start();
    }

    public String convertVideoType(String abbrVideoType) {
        String resultString = "Music Videos";
        switch (abbrVideoType) {
            case "CON":
                resultString = "Concerts";
                break;
            case "MUS":
                resultString = "Music Videos";
                break;
            case "INT":
                resultString = "Interviews";
                break;
            default:
                resultString = "Music Videos";
                break;
        }
        return resultString;
    }
}
