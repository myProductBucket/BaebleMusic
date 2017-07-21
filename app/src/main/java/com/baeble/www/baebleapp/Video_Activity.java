package com.baeble.www.baebleapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v17.leanback.app.PlaybackOverlayFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baeble.www.baebleapp.API.SvcApiRestCallback;
import com.baeble.www.baebleapp.API.SvcApiService;
import com.baeble.www.baebleapp.custom.ThemeableMediaRouteActionProvider;
import com.baeble.www.baebleapp.model.FeaturedVideo;
import com.baeble.www.baebleapp.model.SvcError;
import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.cast.CastManager;
import com.longtailvideo.jwplayer.core.PlayerState;
import com.longtailvideo.jwplayer.events.listeners.VideoPlayerEvents;
import com.longtailvideo.jwplayer.media.ads.Ad;
import com.longtailvideo.jwplayer.media.ads.AdBreak;
import com.longtailvideo.jwplayer.media.ads.AdSource;
import com.longtailvideo.jwplayer.media.captions.Caption;
import com.longtailvideo.jwplayer.media.playlists.MediaSource;
import com.longtailvideo.jwplayer.media.playlists.PlaylistItem;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import retrofit.client.Response;

public class Video_Activity extends AppCompatActivity implements View.OnClickListener
{
	ActionBar actionBar;
	HorizontalListView HorizontalListView;
	RelativeLayout ly_topBar;
	LinearLayout ly_backLayout;
	ImageView img_searchVideoView, img_backVideoView;
	View viewOrange_line_bottom, viewOrange_line_top;
	LinearLayout playMoreIcon;
	PlayVideoGridAdapter adapter;
	List<FeaturedVideo> fVideos;
	int fVideoNum;
	TextView txt_playvideotitle;
	int curPosition;
	String categoryApiName;
	JWPlayerView jwPlayer;

	LinearLayout jwPlayerLayout;
	Boolean isMp4Playing = false;
	Boolean isPlayListItemStarted = false;
	int playListItemTimeOffset = 0;
	Boolean isExit = false;

	private static final String TAG = Video_Activity.class.getSimpleName();
	private boolean mLoading = false;
	private boolean mLastPage = false;
	private int mCurrentPage = 1;
	private int mVisibleThreshold = 100;
	private final static int ITEMS_PPAGE = 100;
	private int FirstVisibleItemNum = 6;
	private Uri kwicrUriM3u8;
	private Uri kwicrUriMp4;
	private Uri kwicrUriWebm;
	CallbackManager callbackManager;
	ShareDialog shareDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_layout_activity);

		callbackManager = CallbackManager.Factory.create();
		shareDialog = new ShareDialog(this);

		curPosition = getIntent().getExtras().getInt("curPosition");
		categoryApiName = getIntent().getExtras().getString("category");

		fVideos = new ArrayList<FeaturedVideo>();
		if (categoryApiName.equals("search"))
		{
			for (int index = 0; index < Search_Activity.searchVideos.size(); index++)
			{
				fVideos.add(Search_Activity.searchVideos.get(index));
			}
		}
		else if (categoryApiName.equals("branch"))
		{
			for (int index = 0; index < Main_Tab_Activity.deepLinkVideos.size(); index++)
			{
				fVideos.add(Main_Tab_Activity.deepLinkVideos.get(index));
			}
		}
		else if (Main_Tab_Activity.featuredVideos != null)
		{
			for (int index = 0; index < Main_Tab_Activity.featuredVideos.size(); index++)
			{
				fVideos.add(Main_Tab_Activity.featuredVideos.get(index));
			}
		}

		fVideoNum = fVideos.size();
		mCurrentPage = fVideoNum / ITEMS_PPAGE;
		txt_playvideotitle = (TextView) findViewById(R.id.txt_playvideotitle);
		txt_playvideotitle.setText(fVideos.get(curPosition).getVideoName());

		adapter = new PlayVideoGridAdapter((Video_Activity) this, fVideos);

		// Set up the ActionBar
		ly_topBar = (RelativeLayout) getLayoutInflater().inflate(R.layout.actionbar_layout, null);
		actionBar = getSupportActionBar();

		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(0xFF212221));

		actionBar.setCustomView(ly_topBar);

		Toolbar parent = (Toolbar) ly_topBar.getParent();// first get parent toolbar of current action bar
		parent.setContentInsetsAbsolute(0, 0);// set padding programmatically to 0dp

		viewOrange_line_bottom = (View) findViewById(R.id.viewOrange_line_bottom);
		viewOrange_line_top = (View) findViewById(R.id.viewOrange_line_top);

		HorizontalListView = (HorizontalListView) findViewById(R.id.HorizontalListView);
		HorizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				txt_playvideotitle.setText(fVideos.get(position).getVideoName());
				loadVideoURL(position, true);
			}
		});

		HorizontalListView.setOnScrollStateChangedListener(new HorizontalScrollDetection());

		img_backVideoView = (ImageView) findViewById(R.id.img_backVideoView);
		img_backVideoView.setOnClickListener(this);
		ly_backLayout = (LinearLayout) findViewById(R.id.ly_backLayout);

		playMoreIcon = (LinearLayout) findViewById(R.id.layout_playVideoMoreIcon);
		playMoreIcon.setOnClickListener(this);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			actionBar.hide();
			viewOrange_line_bottom.setVisibility(View.GONE);
			viewOrange_line_top.setVisibility(View.GONE);
			HorizontalListView.setVisibility(View.GONE);
			ly_backLayout.setVisibility(View.GONE);

		}
		else
		{
			actionBar.show();
			viewOrange_line_bottom.setVisibility(View.VISIBLE);
			viewOrange_line_top.setVisibility(View.VISIBLE);
			HorizontalListView.setVisibility(View.VISIBLE);
			ly_backLayout.setVisibility(View.VISIBLE);
		}

		HorizontalListView.setAdapter(adapter);
		HorizontalListView.setSelection(curPosition);

		jwPlayer = (JWPlayerView) findViewById(R.id.jwplayer);
		jwPlayer.setFocusableInTouchMode(true);
		jwPlayerLayout = (LinearLayout) findViewById(R.id.jwplayer_layout);

		loadVideoURL(curPosition, true);

		jwPlayer.addOnErrorListener(new VideoPlayerEvents.OnErrorListener()
		{
			@Override
			public void onError(String s)
			{
				Log.d(TAG, "addOnErrorListener called index = " + curPosition);
				Log.d(TAG, "addOnErrorListener error: " + s);
				if (isMp4Playing == true)
				{
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
					return;
				}
				loadVideoURL(curPosition, false);
				isMp4Playing = true;
			}
		});

		jwPlayer.addOnSetupErrorListener(new VideoPlayerEvents.OnSetupErrorListener()
		{
			@Override
			public void onSetupError(String s)
			{

				Log.d(TAG, "addOnSetupErrorListener called index = " + curPosition);

				if (isMp4Playing == true)
				{
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
					return;
				}

				loadVideoURL(curPosition, false);
				isMp4Playing = true;
			}
		});
		jwPlayer.addOnCompleteListener(new VideoPlayerEvents.OnCompleteListener()
		{
			@Override
			public void onComplete()
			{
				Log.d(TAG, "addOnPlaylistCompleteListener called");
				int nextPosition = curPosition + 1;
				if (nextPosition > fVideos.size() - 1)
				{
					nextPosition = 0;
				}

				try
				{
					FeaturedVideo fVideo = fVideos.get(nextPosition);
					if (checkJsonValidity(fVideo) == false)
					{
						Log.d(TAG, "addOnCompleteListener: fvideo null");
						return;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return;
				}

				txt_playvideotitle.setText(fVideos.get(nextPosition).getVideoName());
				loadVideoURL(nextPosition, true);
				isMp4Playing = false;
			}
		});

		jwPlayer.addOnPauseListener(new VideoPlayerEvents.OnPauseListener()
		{
			@Override
			public void onPause(PlayerState playerState)
			{
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			}
		});

		jwPlayer.addOnPlayListener(new VideoPlayerEvents.OnPlayListener()
		{
			@Override
			public void onPlay(PlayerState playerState)
			{
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				if (isPlayListItemStarted == true)
				{
					isPlayListItemStarted = false;
					jwPlayer.seek(playListItemTimeOffset);
				}
			}
		});

		jwPlayer.addOnPlaylistItemListener(new VideoPlayerEvents.OnPlaylistItemListener()
		{
			@Override
			public void onPlaylistItem(int index, PlaylistItem playlistItem)
			{
				if (playlistItem.getImage() != null)
				{
					playListItemTimeOffset = Integer.parseInt(playlistItem.getImage()) / 24 * 1000;
					if (playListItemTimeOffset > 0)
					{
						isPlayListItemStarted = true;
					}
				}
			}
		});

		isExit = false;
	}

	public void loadVideoURL(final int position, boolean isEnabledHDSource)
	{

		try
		{
			FeaturedVideo fVideo = fVideos.get(position);
			if (checkJsonValidity(fVideo) == false)
			{
				Log.d(TAG, "loadVideoURL: fvideo null");
				return;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}

		boolean isSupportedM3U8 = true;
		boolean isSupportedWebm = true;

		curPosition = position;

		if (fVideos.get(position).getVideoType().equals("MUS") || fVideos.get(position).getVideoType().equals("INT"))
		{
			// isSupportedM3U8 = false;
			isSupportedWebm = true;
		}
		else
		{
			// isSupportedM3U8 = true;
			isSupportedWebm = false;
		}

		if (jwPlayer.getState() == PlayerState.PLAYING)
			jwPlayer.stop();

		playMoreIcon.setTag(fVideos.get(position));

		List<PlaylistItem> playArray = new ArrayList<>();

		List<MediaSource> mediaSources = new ArrayList<>();
		// This single source is the default video to playback
		String urlM3u8 = fVideos.get(position).getM3u8PathNo4K();
		if (urlM3u8 == null || urlM3u8 == "")
		{
			urlM3u8 = fVideos.get(position).getM3u8Path();
		}
		String urlMp4 = fVideos.get(position).getMp4Path();
		String urlWebm = fVideos.get(position).getWebmPath();

		final Uri originalUriM3u8 = Uri.parse(urlM3u8);
		final Uri originalUriMp4 = Uri.parse(urlMp4);
		final Uri originalUriWebm = Uri.parse(urlWebm);

		if (!urlM3u8.equals(""))
		{
			// kwicrUriM3u8 = Kwicr.getKwicrUri(originalUriM3u8);
			kwicrUriM3u8 = originalUriM3u8;
		}
		else
		{
			isSupportedM3U8 = false;
		}

		if (!urlWebm.equals(""))
		{
			// kwicrUriWebm = Kwicr.getKwicrUri(originalUriWebm);
			kwicrUriWebm = originalUriWebm;
		}
		else
		{
			isSupportedWebm = false;
		}

		if (!urlMp4.equals(""))
		{
			// kwicrUriMp4 = Kwicr.getKwicrUri(originalUriMp4);
			kwicrUriMp4 = originalUriMp4;
		}

		if (isEnabledHDSource && isSupportedM3U8)
		{
			mediaSources.add(new MediaSource(kwicrUriM3u8.toString()));
		}
		else if (isEnabledHDSource && isSupportedWebm)
		{
			mediaSources.add(new MediaSource(kwicrUriMp4.toString()));
		}
		else
		{
			mediaSources.add(new MediaSource(kwicrUriMp4.toString()));
		}
		// Create a list of Caption objects to represent the captions tracks
		List<Caption> captionTracks = new ArrayList<>();
		// if(fVideos.get(position).getVideoType().equals("INT"))
		{
			String captions = fVideos.get(position).getCaptions();
			if (!captions.isEmpty())
			{
				// Create a Caption pointing to English subtitles and add it to the list
				Caption captionEn = new Caption(captions);
				captionEn.setDefault(false);
				captionTracks.add(captionEn);
			}
			Log.d(TAG, "caption path " + captions);
		}
		Log.d(TAG, "play video path " + mediaSources.get(0).getFile());

		LinkedList<AdBreak> schedule = new LinkedList<>();
		FeaturedVideo.AdsModel adsModel[] = fVideos.get(position).getAds();
		if (adsModel != null)
		{
			if ((Main_Tab_Activity.adsPlayerCount++) % 3 == 0)
			{
				Log.d(TAG, "preroll_mobile_android " + adsModel[0].getPrerollMobileAndroidTv());
				String vastTag = adsModel[0].getPrerollMobileAndroidTv();
				Ad ad = new Ad(AdSource.VAST, vastTag);
				AdBreak adBreak = new AdBreak("preroll_mobile_android", ad);
				schedule.add(adBreak);
			}
		}

		// String vastTag =
		// "https://pubads.g.doubleclick.net/gampad/ads?sz=640x360&iu=/18057787/bae_preroll_spotx_mobile2&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&url=http://www.baeblemusic.com&description_url=http://www.baeblemusic.com&correlator=";
//		 String vastTag =
//		 "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
//		 String vastTag = testUrl + "[" + testUrl + "]";
		// Ad ad = new Ad(AdSource.IMA, vastTag);
		// AdBreak adBreak = new AdBreak("0", ad);
		// schedule.add(adBreak);

		if (fVideos.get(position).getVideoType().equals("CON"))
		{
			for (FeaturedVideo.SetlistModel setlistitem : fVideos.get(position).getSetList())
			{
				PlaylistItem concertVideoItem = new PlaylistItem();
				concertVideoItem.setSources(mediaSources);
				concertVideoItem.setImage(setlistitem.getStartTimeIdx());
				if (!schedule.isEmpty())
					concertVideoItem.setAdSchedule(schedule);

				if (captionTracks.size() > 0)
				{
					concertVideoItem.setCaptions(captionTracks);
				}
				playArray.add(concertVideoItem);
			}
			jwPlayer.load(playArray);

		}
		else
		{
			PlaylistItem item = new PlaylistItem();
			item.setSources(mediaSources);

			if (!schedule.isEmpty())
				item.setAdSchedule(schedule);

			if (captionTracks.size() > 0)
			{
				item.setCaptions(captionTracks);
			}
			playArray.add(item);
			jwPlayer.load(playArray);
		}

		// jwPlayer.setSkin("http://dev.baeblemusic.com/app_assets/css/jwplayer_skin_iOS.css");

		jwPlayer.play();
		isMp4Playing = false;

		if (isEnabledHDSource || !isSupportedM3U8)
		{
			String videoName = "Video: " + fVideos.get(position).getVideoName() + " - Baeble Music";
			Splash_Activity.processAnalyticsTracking(videoName);
		}
	}

	@Override
	public void onClick(final View v)
	{
		switch (v.getId())
		{
		case R.id.img_backVideoView:
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			jwPlayer.stop();
			adapter.countDownTimer.cancel();
			isExit = true;
			finish();
			break;
		case R.id.img_gridPlayMoreLayout:
			v.post(new Runnable()
			{
				@Override
				public void run()
				{
					showPopupMenu(v);
				}
			});
			break;
		case R.id.layout_playVideoMoreIcon:
			showPopupMenu(v);
			break;
		default:
			break;
		}
	}

	private void showPopupMenu(View view)
	{
		// Retrieve the clicked item from view's tag
		final FeaturedVideo item = (FeaturedVideo) view.getTag();

		Context wrapper = new ContextThemeWrapper(this, R.style.AppThemeCustom);
		PopupMenu popup = new PopupMenu(wrapper, view);

		try
		{
			Field[] fields = popup.getClass().getDeclaredFields();
			for (Field field : fields)
			{
				if ("mPopup".equals(field.getName()))
				{
					field.setAccessible(true);
					Object menuPopupHelper = field.get(popup);
					Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
					Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
					setForceIcons.invoke(menuPopupHelper, true);
					break;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		// Inflate our menu resource into the PopupMenu's Menu
		popup.getMenuInflater().inflate(R.menu.menu_video_list, popup.getMenu());

		// Set a listener so we are notified if a menu item is clicked
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem menuItem)
			{
				switch (menuItem.getItemId())
				{
				case R.id.action_one:
					jwPlayer.stop();
					shareOnFacebook(item);
					return true;
				}
				return false;
			}
		});

		// Finally show the PopupMenu
		popup.show();
	}

	public boolean isLandscapeMode = false;

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		Log.d(TAG, "screen configuration changed " + newConfig.orientation);
		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			isLandscapeMode = true;
		}
		else
		{
			isLandscapeMode = false;
		}

		jwPlayer.setFullscreen(isLandscapeMode, true);

		ly_backLayout.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				adapter.notifyDataSetChanged();

				if (isLandscapeMode)
				{
					actionBar.hide();
					viewOrange_line_bottom.setVisibility(View.GONE);
					viewOrange_line_top.setVisibility(View.GONE);
					HorizontalListView.setVisibility(View.GONE);
					ly_backLayout.setVisibility(View.GONE);
				}
				else
				{
					actionBar.show();
					viewOrange_line_bottom.setVisibility(View.VISIBLE);
					viewOrange_line_top.setVisibility(View.VISIBLE);
					HorizontalListView.setVisibility(View.VISIBLE);
					ly_backLayout.setVisibility(View.VISIBLE);
				}
			}
		}, 100);
	}

	@Override
	protected void onResume()
	{
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Splash_Activity.stopOtherPlayer(this);
		// Let JW Player know that the app has returned from the background
		jwPlayer.onResume();
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// Let JW Player know that the app is going to the background
		jwPlayer.onPause();
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// Let JW Player know that the app is being destroyed
		// jwPlayer.onDestroy();
		super.onDestroy();
	}

	@Override
	public void finish()
	{
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		jwPlayer.stop();
		jwPlayer.onDestroy();
		adapter.countDownTimer.cancel();
		isExit = true;
		super.finish();
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	//// Exit fullscreen when the user pressed the Back button
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// if (jwPlayer.getFullscreen()) {
	// jwPlayer.setFullscreen(false, true);
	// return false;
	// }
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
			System.out.println("");
			if (jwPlayer.getFullscreen())
			{
				jwPlayer.setFullscreen(false, true);
				return false;
			}
			else
			{
				finish();
			}
		case KeyEvent.KEYCODE_MEDIA_PLAY:
			jwPlayer.pause();
			isMp4Playing = false;
			System.out.println("");
			return true;
		case KeyEvent.KEYCODE_MEDIA_PAUSE:
			jwPlayer.play();
			isMp4Playing = true;
			System.out.println("");
			return true;
		case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
			if (isMp4Playing)
			{
				jwPlayer.pause();
				isMp4Playing = false;
			}
			else
			{
				jwPlayer.play();
				isMp4Playing = true;
			}
			return true;
		// case KeyEvent.KEYCODE_DPAD_CENTER:
		// System.out.println("");
		// return true;
		// case KeyEvent.KEYCODE_DPAD_DOWN:
		// System.out.println("");
		// return true;
		// case KeyEvent.KEYCODE_DPAD_UP:
		// System.out.println("");
		// return true;
		// case KeyEvent.KEYCODE_DPAD_LEFT:
		// System.out.println("");
		// return true;
		// case KeyEvent.KEYCODE_DPAD_RIGHT:
		// System.out.println("");
		// return true;
		default:
			return super.onKeyUp(keyCode, event);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		// Create the chromecast button
		MenuItem castButton = menu.add(Menu.NONE, R.id.media_route_menu_item, Menu.NONE, R.string.ccl_media_route_menu_title);
		// Make the button always visible
		MenuItemCompat.setShowAsAction(castButton, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		// Set the action provider to MediaRouteActionProvider
		MenuItemCompat.setActionProvider(castButton, new ThemeableMediaRouteActionProvider(Video_Activity.this));
		// Register the MediaRouterButton on the JW Player SDK
		CastManager.getInstance().addMediaRouterButton(menu, R.id.media_route_menu_item);

		// Create search button
		MenuItem searchButton = menu.add(Menu.NONE, 1, Menu.NONE, R.string.search);
		searchButton.setIcon(R.drawable.search);
		MenuItemCompat.setShowAsAction(searchButton, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case 1:
			if (categoryApiName.equals("search"))
			{
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				jwPlayer.stop();
				adapter.countDownTimer.cancel();
				isExit = true;
				finish();
			}
			else
			{
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				jwPlayer.stop();
				adapter.countDownTimer.cancel();
				isExit = true;
				Intent intent = new Intent(Video_Activity.this, Search_Activity.class);
				startActivity(intent);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class HorizontalScrollDetection implements com.baeble.www.baebleapp.HorizontalListView.OnScrollStateChangedListener
	{
		public HorizontalScrollDetection()
		{

		}

		public void onScrollStateChanged(ScrollState scrollState)
		{

			if (scrollState == ScrollState.SCROLL_STATE_TOUCH_SCROLL)
			{

				int totalItemCount = adapter.getCount();
				int visibleItemCount = adapter.curPosition;
				int firstVisibleItem = FirstVisibleItemNum;

				// Log.d("onScrollStateChanged:",
				// "Touch Scroll detected. totlaVideoCount=" + totalItemCount
				// + " visibleItemCount=" + adapter.curPosition
				// + " FirstVisibleCount = " + firstVisibleItem);

				if (!mLastPage && !mLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + mVisibleThreshold))
				{

					if (!categoryApiName.equals(Featured_Fragment.CATEGORY_FEATURED) && !categoryApiName.equals("search"))
					{
						mLoading = true;
						new AddItemsAsyncTask().execute();
					}
				}
			}
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{

		return super.dispatchKeyEvent(event);
	}

	public class AddItemsAsyncTask extends AsyncTask<Integer, String, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... args)
		{

			mCurrentPage++;
			SvcApiService.getUserIdEndPoint().GetCategoryVideos(mCurrentPage, ITEMS_PPAGE, categoryApiName, new SvcApiRestCallback<List<FeaturedVideo>>()
			{
				@Override
				public void failure(SvcError svcError)
				{
					if (isExit == true)
					{
						Log.d(TAG, "Fail to API request(result: success) due to Activity exit");
						return;
					}
					Log.d(TAG, "Failed to download more concert video info");
					mLoading = false;
				}

				@Override
				public void success(List<FeaturedVideo> featuredVideos, Response response)
				{
					if (isExit == true)
					{
						Log.d(TAG, "Failed to API request(result: success)) due to Activity exit");
						return;
					}

					if (featuredVideos == null)
					{
						Log.d(TAG, "Failed to download more concert video, info address null");
						return;
					}

					if (featuredVideos.size() > 0)
					{
						Log.d(TAG, "Success to download more concert video info " + featuredVideos.size());
						adapter.add(featuredVideos);
						adapter.notifyDataSetChanged();
					}
					mLoading = false;
				}
			});
			return null;
		}

		@Override
		protected void onPostExecute(String args)
		{

		}
	}

	private boolean checkJsonValidity(FeaturedVideo fvideo)
	{
		if (fvideo.getVideoType().isEmpty() || fvideo.getVideoName().isEmpty() || fvideo.getVideoImage().isEmpty() || fvideo.getBandName().isEmpty())
			return false;

		return true;
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{
		Log.d(TAG, "onActivityResult called");
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	public void shareOnFacebook(FeaturedVideo item)
	{
		if (ShareDialog.canShow(ShareLinkContent.class))
		{
			makeBranchMessage(item);
		}
	}

	FeaturedVideo selectedVideo;

	private void makeBranchMessage(FeaturedVideo video)
	{
		selectedVideo = video;
		BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
				// The identifier is what Branch will use to de-dupe the content across many different Universal Objects
				.setCanonicalIdentifier(video.getVideoID())
				// The canonical URL for SEO purposes (optional)
				.setCanonicalUrl("https://branch.io/deepviews")
				// This is where you define the open graph structure and how the object will appear on Facebook or in a
				// deepview
				.setTitle("Baeble Music Videos : " + video.getVideoName()).setContentDescription(video.getVideoShortDescription()).setContentImageUrl(video.getVideoImage())
				// You use this to specify whether this content can be discovered publicly - default is public
				.setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
				// Here is where you can add custom keys/values to the deep link data
				.addContentMetadata("videotype", video.getVideoType()).addContentMetadata("videoid", video.getVideoID());

		branchUniversalObject.registerView();

		LinkProperties linkProperties = new LinkProperties().setChannel("facebook").setFeature("sharing").addControlParameter("$desktop_url", video.getPageURL());
		// .addControlParameter("$ios_url", "https://itunes.apple.com/us/app/baeble-music-videos/id1070313507?mt=8")
		// .addControlParameter("$android_url",
		// "https://play.google.com/store/apps/details?id=com.baeble.www.baebleapp");

		branchUniversalObject.generateShortUrl(this, linkProperties, new Branch.BranchLinkCreateListener()
		{
			@Override
			public void onLinkCreate(String url, BranchError error)
			{

				if (error == null)
				{
					// Toast.makeText(getApplicationContext(), "onLinkCreate() called url= " + url,
					// Toast.LENGTH_LONG).show();
					makeShareContent(selectedVideo, url);
				}
				else
				{
					// Toast.makeText(getApplicationContext(), "onLinkCreate() called " + error.toString(),
					// Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private void makeShareContent(FeaturedVideo item, String branchURL)
	{
		ShareLinkContent content = new ShareLinkContent.Builder().setContentUrl(Uri.parse(branchURL != null ? branchURL : "https://developers.facebook.com")).setContentTitle(item.getVideoName()).setContentDescription(item.getVideoShortDescription()).setImageUrl(Uri.parse(item.getVideoImage())).build();
		shareDialog.show(content);
	}
}
