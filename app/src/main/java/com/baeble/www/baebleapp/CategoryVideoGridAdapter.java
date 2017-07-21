package com.baeble.www.baebleapp;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applidium.shutterbug.FetchableImageView;
import com.applidium.shutterbug.utils.ShutterbugManager;
import com.baeble.www.baebleapp.model.FeaturedVideo;

import java.util.List;

public class CategoryVideoGridAdapter extends BaseAdapter {
    List<FeaturedVideo> fVideos;
    private final Main_Tab_Activity main;
    String [] names={"Featured","Trending","Concerts","Music Videos","Interviews"};

    CategoryVideoGridAdapter(Main_Tab_Activity mainActivity, List<FeaturedVideo> featuredVideos){
        fVideos = featuredVideos;
        main = mainActivity;
    }

    @Override
    public int getCount() {
        return fVideos.size();
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder{
        TextView featuredItemTitle;
        FetchableImageView featuredItemImage;
    }

    public void add(List<FeaturedVideo> featuredVideos){
        for (int index = 0; index < featuredVideos.size(); index++) {
            fVideos.add(featuredVideos.get(index));
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if(view == null) {
            view = main.getLayoutInflater().inflate(R.layout.featured_list_item, null);

            LinearLayout moreIcon = (LinearLayout)view.findViewById(R.id.img_gridFeaturedMoreLayout);
            moreIcon.setVisibility(View.GONE);

            holder = new ViewHolder();
            holder.featuredItemTitle = (TextView) view.findViewById(R.id.txt_gridFeaturedItem);
            holder.featuredItemImage = (FetchableImageView) view.findViewById(R.id.img_gridFeaturedItem);
            view.setTag(holder);

            int gridHeight = (Main_Tab_Activity.phoneWidth - 40) * 9 / 16;
            holder.featuredItemImage.getLayoutParams().height = gridHeight;

        }else{
            holder = (ViewHolder)view.getTag();
        }

        holder.featuredItemTitle.setText(names[position]);
        String videoImgUrl = fVideos.get(position).getVideoImage();
        holder.featuredItemImage.setImageResource(R.drawable.thumb_size);
        ShutterbugManager.getSharedImageManager(main.getApplicationContext()).download(videoImgUrl, holder.featuredItemImage);

        return view;
    }
}