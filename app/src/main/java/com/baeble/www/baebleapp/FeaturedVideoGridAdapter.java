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

public class FeaturedVideoGridAdapter extends BaseAdapter
{
	List<FeaturedVideo> fVideos;
	private final Main_Tab_Activity main;
	private Featured_Fragment featuredFragment;

	FeaturedVideoGridAdapter(Main_Tab_Activity mainActivity, List<FeaturedVideo> featuredVideos, Featured_Fragment fragment)
	{
		fVideos = featuredVideos;
		main = mainActivity;
		featuredFragment = fragment;
	}

	@Override
	public int getCount()
	{
		return fVideos.size();
	}

	@Override
	public Object getItem(int position)
	{
		return fVideos.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	static class ViewHolder
	{
		TextView featuredItemTitle;
		FetchableImageView featuredItemImage;
		LinearLayout featuredMoreLayout;
	}

	public void add(List<FeaturedVideo> featuredVideos)
	{
		for (int index = 0; index < featuredVideos.size(); index++)
		{
			fVideos.add(featuredVideos.get(index));
		}
	}

	@Override
	public View getView(int position, View view, ViewGroup parent)
	{
		ViewHolder holder;

		if (view == null)
		{
			view = main.getLayoutInflater().inflate(R.layout.featured_list_item, null);

			holder = new ViewHolder();
			holder.featuredItemTitle = (TextView) view.findViewById(R.id.txt_gridFeaturedItem);
			holder.featuredItemImage = (FetchableImageView) view.findViewById(R.id.img_gridFeaturedItem);
			holder.featuredMoreLayout = (LinearLayout) view.findViewById(R.id.img_gridFeaturedMoreLayout);

			int gridHeight = (Main_Tab_Activity.phoneWidth - 40) * 9 / 16;
			holder.featuredItemImage.getLayoutParams().height = gridHeight;

			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}
		if (fVideos.get(position).getVideoType().equalsIgnoreCase("HLS"))
		{
			System.out.print("");
		}
		if (fVideos.get(position).getVideoType().equalsIgnoreCase("MUS"))
		{
			holder.featuredItemTitle.setText(fVideos.get(position).getBandName() + " - " + fVideos.get(position).getVideoName());
		}
		else
		{
			holder.featuredItemTitle.setText(fVideos.get(position).getVideoName());
		}
		String videoImgUrl = fVideos.get(position).getVideoImage();
		holder.featuredItemImage.setImageResource(R.drawable.thumb_size);
		ShutterbugManager.getSharedImageManager(main.getApplicationContext()).download(videoImgUrl, holder.featuredItemImage);

		holder.featuredMoreLayout.setTag(getItem(position));
		holder.featuredMoreLayout.setOnClickListener(featuredFragment);

		return view;
	}
}
