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

public class PlayVideoGridAdapter extends BaseAdapter {
	private static final String TAG = PlayVideoGridAdapter.class.getSimpleName();
    private int vnum = 0;
    List<FeaturedVideo> fVideos;
    private final Video_Activity main;
    public int curPosition;
    private ImageLoader imageLoader;
    public CountDownTimer countDownTimer;
    private int prevItemPosition = 0;
    private List <ViewMap> backupViewList;
    private int preDisplayPosition = 0;
    private int visibleViewCount = 0;
    private final int TIMER_CYCLE_COUNT = 100000;//ms
    private final int TIMER_TICK_COUNT = 200;//ms
    private  int maxVisibleCount = 5;
    private class ViewMap{
        public View view;
        public int position;
    }


    PlayVideoGridAdapter(Video_Activity videoActivity, List<FeaturedVideo> featuredVideos){
        fVideos = featuredVideos;
        vnum = fVideos.size();
        main = videoActivity;
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
        ImageView featuredItemImage;
        LinearLayout playItemMoreLayout;
    }

    public void add(List<FeaturedVideo> featuredVideos) {
        for (int index = 0; index < featuredVideos.size(); index++) {
            fVideos.add(featuredVideos.get(index));
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view == null) {
            view = main.getLayoutInflater().inflate(R.layout.horizontal_list_item, null);
            holder = new ViewHolder();
            holder.featuredItemTitle = (TextView) view.findViewById(R.id.txt_titlePlayItem);
            holder.artistItemTitle = (TextView) view.findViewById(R.id.txt_artistPlayItem);
            holder.featuredItemImage = (ImageView) view.findViewById(R.id.img_gridPlayItem);
            holder.playItemMoreLayout = (LinearLayout)view.findViewById(R.id.img_gridPlayMoreLayout);
            view.setTag(holder);
            view.setId(visibleViewCount);
            visibleViewCount++;
            //Log.d(TAG, "Visible Maximum View Count = " + visibleViewCount);
        }else{
            holder = (ViewHolder)view.getTag();
        }

        holder.playItemMoreLayout.setTag(getItem(position));
        holder.playItemMoreLayout.setOnClickListener(main);
        holder.featuredItemTitle.setText(fVideos.get(position).getVideoName());
        holder.artistItemTitle.setText(fVideos.get(position).getBandName());
        holder.featuredItemImage.setImageResource(R.drawable.thumb_size);

        Log.d(TAG, "get view position = " + position);
        if (position == 0){
            Log.d(TAG, "current position = " + curPosition + "prePosition = " + preDisplayPosition +"visibleViewCount = " + visibleViewCount);
            if(curPosition <= visibleViewCount) {
                String videoImgUrl = fVideos.get(position).getVideoImage();
                imageLoader.displayImage(videoImgUrl, holder.featuredItemImage);
            }
            if(fVideos.size() <= visibleViewCount){
                curPosition = position;
                addViewMapToBackList(view, position);
                preDisplayPosition = 0;
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
        countDownTimer = new CountDownTimer(TIMER_CYCLE_COUNT, TIMER_TICK_COUNT) {

            public void onTick(long millisUntilFinished) {

                /*Notify that the video number changed due to download more videos*/
                if(vnum != fVideos.size()){
                    preDisplayPosition = 0;
                    curPosition = prevItemPosition = visibleViewCount;
                    vnum = fVideos.size();
                }

                //Log.d(TAG, "resetTimer: cur positon = " + curPosition + " pre Position = " + prevItemPosition + "Pre display point = " + preDisplayPosition);
//                if(curPosition == 0)
//                    return;

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

                        if(curPosition == 0)
                            startIndex -= 1;

                        if(downLimit < 0) {
                            downLimit = 0;
                        }

                        //Log.d(TAG, "displayCount = " + displayCount + "visibleViewCount = " + visibleViewCount);
                        //Log.d(TAG, "start index = " + startIndex + " downlimit = " + downLimit + " backuplistsize = " + backupViewList.size() + " visible count=" + visibleViewCount);

                        for (int displayIdx = startIndex; displayIdx >= downLimit; displayIdx--) {
                            View curView = backupViewList.get(displayIdx).view;
                            if(isUsedView[curView.getId()] == false){
                                isUsedView[curView.getId()] = true;
                                ViewHolder holder = (ViewHolder) curView.getTag();
                                int imageIndex = backupViewList.get(displayIdx).position;
                                String videoImgUrl = fVideos.get(imageIndex).getVideoImage();
                                imageLoader.displayImage(videoImgUrl, holder.featuredItemImage);
                                //Log.d(TAG, "dispaly video index = " + imageIndex + " viewId = " + curView.getId());
                            }else{
                                //Log.d(TAG, "used video view = " + curView.getId());
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

}
