package com.baeble.www.baebleapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baeble.www.baebleapp.API.SvcApiRestCallback;
import com.baeble.www.baebleapp.API.SvcApiService;
import com.baeble.www.baebleapp.custom.ThemeableMediaRouteActionProvider;
import com.baeble.www.baebleapp.model.FeaturedVideo;
import com.baeble.www.baebleapp.model.SvcError;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.longtailvideo.jwplayer.cast.CastManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import retrofit.client.Response;

public class Main_Tab_Activity extends AppCompatActivity implements View.OnClickListener
{

	public static int REQUEST_CODE = 8756;
	public static String TAG = Main_Tab_Activity.class.getSimpleName();
	public static int phoneWidth = 0;
	public static int adsPlayerCount = 0;

	ImageView img_searchMainTab;
	static Custom_TextView txt_tabFeatured, txt_tabCategory, txt_tabCateroryDetail;
	public static ArrayList<FeaturedVideo> featuredVideos;
	ShareDialog shareDialog;
	CallbackManager callbackManager;
	/* variables for push notification */
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private BroadcastReceiver mRegistrationBroadcastReceiver;
	RelativeLayout ly_topBar;

	boolean isDeepLinkLoaded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		FacebookSdk.sdkInitialize(getApplicationContext());
		callbackManager = CallbackManager.Factory.create();
		shareDialog = new ShareDialog(this);

		setContentView(R.layout.main_tab_screen_activity);

		new MediaRouteActionProvider(this);

		// to print out the key hash
		try
		{
			PackageInfo info = getPackageManager().getPackageInfo("com.baeble.www.baebleapp", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures)
			{
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		}
		catch (PackageManager.NameNotFoundException e)
		{

		}
		catch (NoSuchAlgorithmException e)
		{

		}

		/* registeration receiver for push notification */
		mRegistrationBroadcastReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
				boolean sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
			}
		};

		if (checkPlayServices())
		{
			// Start IntentService to register this application with GCM.
			Intent intent = new Intent(this, RegistrationIntentService.class);
			startService(intent);
		}

		// Set up the ActionBar
		ly_topBar = (RelativeLayout) getLayoutInflater().inflate(R.layout.actionbar_layout, null);
		final ActionBar actionBar = getSupportActionBar();

		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(0xFF212221));

		actionBar.setCustomView(ly_topBar);

		Toolbar parent = (Toolbar) ly_topBar.getParent();// first get parent toolbar of current action bar
		parent.setContentInsetsAbsolute(0, 0);// set padding programmatically to 0dp

		txt_tabFeatured = (Custom_TextView) findViewById(R.id.txt_tabFeatured);
		txt_tabFeatured.setOnClickListener(this);

		txt_tabCategory = (Custom_TextView) findViewById(R.id.txt_tabCategory);
		txt_tabCategory.setOnClickListener(this);

		txt_tabCateroryDetail = (Custom_TextView) findViewById(R.id.txt_tabCateroryDetail);
		txt_tabCateroryDetail.setOnClickListener(this);

		changeTabDisplayState(true);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		if (size.x < size.y)
			phoneWidth = size.x;
		else
			phoneWidth = size.y;

		checkDeeplink();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.txt_tabFeatured:
			changeTabDisplayState(true);
			break;
		case R.id.txt_tabCategory:
			changeTabDisplayState(false);
			break;
		}
	}

	private void changeTabDisplayState(boolean isFeaturedTabSeleted)
	{
		txt_tabCateroryDetail.setEnabled(false);
		txt_tabFeatured.setBackgroundResource(isFeaturedTabSeleted ? R.drawable.sel : R.drawable.unsel);
		txt_tabCategory.setBackgroundResource(isFeaturedTabSeleted ? R.drawable.unsel : R.drawable.sel);
		txt_tabCateroryDetail.setBackgroundResource(R.drawable.unsel);
		txt_tabFeatured.setTextColor(Color.parseColor(isFeaturedTabSeleted ? "#ec6d36" : "#979797"));
		txt_tabCategory.setTextColor(Color.parseColor(isFeaturedTabSeleted ? "#979797" : "#ec6d36"));
		txt_tabCateroryDetail.setTextColor(Color.parseColor("#979797"));
		txt_tabFeatured.setAlpha(isFeaturedTabSeleted ? 1f : 0.5f);
		txt_tabCategory.setAlpha(isFeaturedTabSeleted ? 0.5f : 1f);
		txt_tabCateroryDetail.setAlpha(0.5f);
		txt_tabCateroryDetail.setText("");

		if (isFeaturedTabSeleted)
			getSupportFragmentManager().beginTransaction().replace(R.id.tabFrameLayout, new Featured_Fragment()).commit();
		else
			getSupportFragmentManager().beginTransaction().replace(R.id.tabFrameLayout, new Category_fragment()).commit();

	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it doesn't, display a dialog that allows
	 * users to download the APK from the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices()
	{
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS)
		{
			if (apiAvailability.isUserResolvableError(resultCode))
			{
				apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			}
			else
			{
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Splash_Activity.stopOtherPlayer(this);
		LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
	}

	protected void onPause()
	{
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
		super.onPause();
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{
		Log.d(TAG, "onActivityResult called");
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
		{
			String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			System.out.println(accountName);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		// Create the chromecast button
		MenuItem castButton = menu.add(Menu.NONE, R.id.media_route_menu_item, Menu.NONE, R.string.ccl_media_route_menu_title);
		 castButton.setIcon(R.drawable.chromecast);
		// Make the button always visible
		MenuItemCompat.setShowAsAction(castButton, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		// Set the action provider to MediaRouteActionProvider
		MenuItemCompat.setActionProvider(castButton, new ThemeableMediaRouteActionProvider(this));
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
			Intent intent = new Intent(Main_Tab_Activity.this, Search_Activity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
				.setTitle("Baeble Music Videos : " + video.getVideoName()).setContentDescription(video.getVideoDescription()).setContentImageUrl(video.getVideoImage())
				// You use this to specify whether this content can be discovered publicly - default is public
				.setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
				// Here is where you can add custom keys/values to the deep link data
				.addContentMetadata("videotype", video.getVideoType()).addContentMetadata("videoid", video.getVideoID());

		branchUniversalObject.registerView();

		LinkProperties linkProperties = new LinkProperties().setChannel("facebook").setFeature("sharing").addControlParameter("$desktop_url", video.getPageURL());
		// .addControlParameter("$ios_url", "https://itunes.apple.com/us/app/baeble-music-videos/id1070313507?mt=8");
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

	public void checkDeeplink()
	{

		if (isDeepLinkLoaded == true)
			return;

		isDeepLinkLoaded = true;

		Branch branch = Branch.getInstance();
		branch.initSession(new Branch.BranchReferralInitListener()
		{
			@Override
			public void onInitFinished(JSONObject referringParams, BranchError error)
			{
				if (error == null)
				{
					try
					{
						String videotype = Branch.getInstance().getLatestReferringParams().getString("videotype");
						String videoid = Branch.getInstance().getLatestReferringParams().getString("videoid");

						// Toast.makeText(getApplicationContext(), "MainTab: deeplinke param videoid = " + videoid,
						// Toast.LENGTH_LONG).show();
						// Toast.makeText(getApplicationContext(), "MainTab: deeplinke param videotype = " + videotype,
						// Toast.LENGTH_LONG).show();

						GetBaebleVideoJsonByID(videotype, videoid);

					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					Log.i("MyApp", error.getMessage());
				}
			}
		}, this.getIntent().getData(), this);
	}

	private ProgressDialog progress;
	private Handler mainHandler;
	public static List<FeaturedVideo> deepLinkVideos;
	private boolean isSucceed = true;

	private void GetBaebleVideoJsonByID(String videoType, String videoId)
	{
		mainHandler = new Handler(this.getMainLooper());
		progress = ProgressDialog.show(this, "Please wait", getResources().getString(R.string.DownloadingDeepLinkVideo), true, false);

		SvcApiRestCallback apiCallback = new SvcApiRestCallback<List<FeaturedVideo>>()
		{
			@Override
			public void failure(SvcError svcError)
			{
				Log.d(TAG, "Failed to download featured video info");
				Runnable progressRunnable = new Runnable()
				{
					@Override
					public void run()
					{
						progress.dismiss();
						// Toast.makeText(getApplicationContext(), "DeepLink download failed",
						// Toast.LENGTH_LONG).show();
					}
				};
				mainHandler.post(progressRunnable);
			}

			@Override
			public void success(final List<FeaturedVideo> featuredVideos, Response response)
			{

				Runnable progressRunnable = new Runnable()
				{
					@Override
					public void run()
					{
						progress.dismiss();
						if (isSucceed == true)
						{
							// Toast.makeText(getApplicationContext(), "DeepLink download success",
							// Toast.LENGTH_LONG).show();
							if (featuredVideos != null)
							{
								// Toast.makeText(getApplicationContext(), "video name = " +
								// featuredVideos.get(0).getVideoName(), Toast.LENGTH_LONG).show();
								deepLinkVideos = new ArrayList<FeaturedVideo>();
								deepLinkVideos.add(featuredVideos.get(0));
								Intent intent = new Intent(Main_Tab_Activity.this, Video_Activity.class);
								intent.putExtra("curPosition", 0);
								intent.putExtra("category", "branch");
								startActivity(intent);
							}
							else
							{
								// Toast.makeText(getApplicationContext(), "video name = null",
								// Toast.LENGTH_LONG).show();
							}
						}
					}
				};
				mainHandler.post(progressRunnable);
			}
		};

		switch (videoType)
		{
		case "CON":
			SvcApiService.getUserIdEndPoint().GetConcertsJsonByID(videoId, apiCallback);
			break;
		case "MUS":
			SvcApiService.getUserIdEndPoint().GetMusicVideosJsonByID(videoId, apiCallback);
			break;
		case "INT":
			SvcApiService.getUserIdEndPoint().GetInterviewsJsonByID(videoId, apiCallback);
			break;
		}
	}
}