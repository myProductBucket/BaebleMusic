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

package com.baeble.www.baebleapp.tv.app.page;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.PageRow;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.view.View;

import com.baeble.www.baebleapp.API.SvcApiRestCallback;
import com.baeble.www.baebleapp.API.SvcApiService;
import com.baeble.www.baebleapp.Main_Tab_Activity;
import com.baeble.www.baebleapp.R;
import com.baeble.www.baebleapp.model.FeaturedVideo;
import com.baeble.www.baebleapp.model.SvcError;
import com.baeble.www.baebleapp.tv.DetailsActivity;
import com.baeble.www.baebleapp.tv.cards.presenters.CardPresenterSelector;
import com.baeble.www.baebleapp.tv.models.Card;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

public class PageAndListRowFragment extends BrowseFragment
{
	private static final int HEADER_ID_NEWEST = 0;
	private static final int HEADER_ID_FEATURED = 1;
	private static final int HEADER_ID_TRENDING = 2;
	private static final int HEADER_ID_CONCERTS = 3;
	private static final int HEADER_ID_MUSIC_VIDEOS = 4;
	private static final int HEADER_ID_INTERVIEWS = 5;

	private static final int ITEMS_PPAGE = 100;
	private BackgroundManager mBackgroundManager;
	private static final String[] NAMES = { "Newest", "Featured", "Trending", "Concerts", "Music Videos", "Interviews" };
	private static final String[] CATEGORY_API_NAMES = { "new", "feat", "trend", "con", "mus", "int" };

	private ArrayObjectAdapter mRowsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setupUi();
		loadData();
		mBackgroundManager = BackgroundManager.getInstance(getActivity());
		mBackgroundManager.setColor(getResources().getColor(R.color.browse_fragment_background));
		mBackgroundManager.attach(getActivity().getWindow());
		getMainFragmentRegistry().registerFragment(PageRow.class, new PageRowFragmentFactory(mBackgroundManager));
	}

	private void setupUi()
	{
		setHeadersState(HEADERS_ENABLED);
		setHeadersTransitionOnBackEnabled(true);
		setBrandColor(getResources().getColor(R.color.fastlane_background));
		prepareEntranceTransition();
	}

	private void loadData()
	{
		mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
		setAdapter(mRowsAdapter);

		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				createRows();
				startEntranceTransition();
			}
		}, 2000);
	}

	private void createRows()
	{
		HeaderItem headerItem1 = new HeaderItem(HEADER_ID_NEWEST, NAMES[HEADER_ID_NEWEST]);
		PageRow pageRow1 = new PageRow(headerItem1);
		mRowsAdapter.add(pageRow1);

		HeaderItem headerItem2 = new HeaderItem(HEADER_ID_FEATURED, NAMES[HEADER_ID_FEATURED]);
		PageRow pageRow2 = new PageRow(headerItem2);
		mRowsAdapter.add(pageRow2);

		HeaderItem headerItem3 = new HeaderItem(HEADER_ID_TRENDING, NAMES[HEADER_ID_TRENDING]);
		PageRow pageRow3 = new PageRow(headerItem3);
		mRowsAdapter.add(pageRow3);

		HeaderItem headerItem4 = new HeaderItem(HEADER_ID_CONCERTS, NAMES[HEADER_ID_CONCERTS]);
		PageRow pageRow4 = new PageRow(headerItem4);
		mRowsAdapter.add(pageRow4);

		HeaderItem headerItem5 = new HeaderItem(HEADER_ID_MUSIC_VIDEOS, NAMES[HEADER_ID_MUSIC_VIDEOS]);
		PageRow pageRow5 = new PageRow(headerItem5);
		mRowsAdapter.add(pageRow5);

		HeaderItem headerItem6 = new HeaderItem(HEADER_ID_INTERVIEWS, NAMES[HEADER_ID_INTERVIEWS]);
		PageRow pageRow6 = new PageRow(headerItem6);
		mRowsAdapter.add(pageRow6);
	}

	private static class PageRowFragmentFactory extends BrowseFragment.FragmentFactory
	{
		private final BackgroundManager mBackgroundManager;

		PageRowFragmentFactory(BackgroundManager backgroundManager)
		{
			this.mBackgroundManager = backgroundManager;
		}

		@Override
		public Fragment createFragment(Object rowObj)
		{
			Row row = (Row) rowObj;
			mBackgroundManager.setDrawable(null);
			String categoryApiName = CATEGORY_API_NAMES[(int) row.getHeaderItem().getId()];
			return CategoryFragment.newInstance(categoryApiName);
		}
	}

	public static class CategoryFragment extends GridFragment implements OnItemViewedListener
	{
		private static final int COLUMNS = 4;
		private final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_SMALL;
		private ArrayObjectAdapter mAdapter;

		private boolean loading = false;
		private int currentPage = 1;
		private int maxViewedItem = -1;

		private static final String ARG_CATEGORY_API_NAME = "categoryApiName";

		public static CategoryFragment newInstance(String categoryApiName)
		{
			CategoryFragment fragment = new CategoryFragment();

			Bundle args = new Bundle();
			args.putString(ARG_CATEGORY_API_NAME, categoryApiName);
			fragment.setArguments(args);

			return fragment;
		}

		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			setupAdapter();
			Main_Tab_Activity.featuredVideos = new ArrayList<FeaturedVideo>();
			loadData(getArguments().getString(ARG_CATEGORY_API_NAME));
			getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
		}

		private void setupAdapter()
		{
			VerticalGridPresenter presenter = new VerticalGridPresenter(ZOOM_FACTOR);
			presenter.setNumberOfColumns(COLUMNS);
			setGridPresenter(presenter);

			CardPresenterSelector cardPresenter = new CardPresenterSelector(getActivity(), this);
			mAdapter = new ArrayObjectAdapter(cardPresenter);
			setAdapter(mAdapter);

			setOnItemViewClickedListener(new OnItemViewClickedListener()
			{
				@Override
				public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row)
				{
					Card card = (Card) item;

					Intent intent = new Intent(getActivity(), DetailsActivity.class);
					intent.putExtra(DetailsActivity.EXTRA_IMAGE, card.getImageUrl());
					intent.putExtra(DetailsActivity.EXTRA_BAND_NAME, card.getBandName());
					intent.putExtra(DetailsActivity.EXTRA_TITLE, card.getTitle());
					intent.putExtra(DetailsActivity.EXTRA_DESC, card.getVideoDescription());
					intent.putExtra(DetailsActivity.EXTRA_CUR_POS, card.getPosition());
					intent.putExtra(DetailsActivity.EXTRA_VIDEO_CATEGORY, card.getCategory());
					intent.putExtra(DetailsActivity.EXTRA_CATEGORY, card.getCategory());
					intent.putParcelableArrayListExtra(DetailsActivity.EXTRA_VIDEOS, Main_Tab_Activity.featuredVideos);
					startActivity(intent);
				}
			});
		}

		private void loadData(final String categoryApiName)
		{
			loading = true;
			SvcApiService.getUserIdEndPoint().GetCategoryVideos(currentPage, ITEMS_PPAGE, categoryApiName, new SvcApiRestCallback<List<FeaturedVideo>>()
			{
				@Override
				public void failure(SvcError svcError)
				{
					loading = false;
				}

				@Override
				public void success(List<FeaturedVideo> featuredVideos, Response response)
				{
					if (featuredVideos != null)
					{
						Main_Tab_Activity.featuredVideos.addAll(featuredVideos);
						for (int i = 0; i < featuredVideos.size(); i++)
						{
							FeaturedVideo video = featuredVideos.get(i);
							Card card = new Card();
							card.setBandName(video.getBandName());
							if (video.getVideoType().equalsIgnoreCase("MUS"))
							{
								card.setTitle(video.getBandName() + " - " + video.getVideoName());
							}
							else
							{
								card.setTitle(video.getVideoName());
							}

							card.setVideoDescription(video.getVideoShortDescription());
							card.setType(Card.Type.GRID_SQUARE);
							card.setImageUrl(video.getVideoImage());
							card.setCategory(video.getVideoType());
							card.setPosition(mAdapter.size());
							mAdapter.add(card);
						}
					}
					loading = false;
					progressBar.setVisibility(View.GONE);
				}
			});
		}

		@Override
		public void onItemViewed(int position)
		{
			if (position > maxViewedItem)
			{
				maxViewedItem = position;
			}
			if (!loading && (mAdapter.size() <= (maxViewedItem + 1)))
			{
				currentPage++;
				progressBar.setVisibility(View.VISIBLE);
				loadData(getArguments().getString(ARG_CATEGORY_API_NAME));
			}
		}
	}
}
