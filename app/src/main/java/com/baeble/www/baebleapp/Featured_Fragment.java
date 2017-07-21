package com.baeble.www.baebleapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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
import android.widget.Toast;

import com.baeble.www.baebleapp.API.SvcApiRestCallback;
import com.baeble.www.baebleapp.API.SvcApiService;
import com.baeble.www.baebleapp.model.FeaturedVideo;
import com.baeble.www.baebleapp.model.SvcError;
import com.baeble.www.baebleapp.tv.DetailsActivity;
import com.baeble.www.baebleapp.tv.models.Card;
import com.baeble.www.baebleapp.tv.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit.client.Client;
import retrofit.client.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Featured_Fragment extends Fragment implements View.OnClickListener
{
	GridView gridView_featured;
	FeaturedVideoGridAdapter adapter;
	public static ArrayList<FeaturedVideo> fVideos;
	private Handler mainHandler;
	private static final String TAG = Featured_Fragment.class.getSimpleName();
	private ProgressDialog progress;
	private boolean isSucceed = true;
	private int mCurrentPage = 1;
	private final static int ITEMS_PPAGE = 100;
	private boolean mLoading = true;
	private boolean mLastPage = false;
	private int mVisibleThreshold = 100;

	public final static String CATEGORY_FEATURED = "featured";
	public String URLToHit = "http://svctest.baeblemusic.com/BaebleService.svc/json/addEmailCapture/";

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.featured_layout_fragment, null);
		mainHandler = new Handler(getActivity().getMainLooper());
		progress = new ProgressDialog(getActivity());
		fVideos = new ArrayList<FeaturedVideo>();

		boolean isLandScapeMode = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
		gridView_featured = (GridView) view.findViewById(R.id.gridView_featured);
		gridView_featured.setScrollingCacheEnabled(true);
		gridView_featured.setNumColumns(isLandScapeMode ? 2 : 1);
		gridView_featured.setHorizontalSpacing(isLandScapeMode ? 20 : 0);
		gridView_featured.setVerticalSpacing(20);

		gridView_featured.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Main_Tab_Activity.featuredVideos = fVideos;
				Intent intent = new Intent(getActivity(), Video_Activity.class);
				intent.putExtra("curPosition", position);
				intent.putExtra("category", CATEGORY_FEATURED);
				startActivity(intent);
			}
		});

		gridView_featured.setOnScrollListener(new AbsListView.OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				if (!mLastPage && !mLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + mVisibleThreshold))
				{
					mLoading = true;
					new AddItemsAsyncTask().execute();
				}
			}
		});
		return view;
	}

	@Override
	public void onClick(final View view)
	{
		Log.d(TAG, "onClick called!");
		view.post(new Runnable()
		{
			@Override
			public void run()
			{
				showPopupMenu(view);
			}
		});
	}

	private void showPopupMenu(View view)
	{
		final FeaturedVideo item = (FeaturedVideo) view.getTag();
		Context wrapper = new ContextThemeWrapper(getActivity(), R.style.AppThemeCustom);
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
					Main_Tab_Activity main = (Main_Tab_Activity) getActivity();
					main.shareOnFacebook(item);
					return true;
				}
				return false;
			}
		});

		// Finally show the PopupMenu
		popup.show();
	}

	public Uri getImageUri(Context inContext, Bitmap inImage)
	{
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		downloadNewestVideo();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			gridView_featured.setNumColumns(2);
			gridView_featured.setHorizontalSpacing(20);
			gridView_featured.setVerticalSpacing(20);
		}
		else
		{
			gridView_featured.setNumColumns(1);
			gridView_featured.setHorizontalSpacing(0);
			gridView_featured.setVerticalSpacing(20);
		}
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
			String categoryApiName = "new";
			mCurrentPage++;
			SvcApiService.getUserIdEndPoint().GetCategoryVideos(mCurrentPage, ITEMS_PPAGE, categoryApiName, new SvcApiRestCallback<List<FeaturedVideo>>()
			{
				@Override
				public void failure(SvcError svcError)
				{
					Log.d(TAG, "Failed to download more concert video info");
					progress.dismiss();
					mLoading = false;
				}

				@Override
				public void success(List<FeaturedVideo> featuredVideos, Response response)
				{

					if (featuredVideos != null)
					{
						if (featuredVideos.size() > 0)
						{
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
		protected void onPostExecute(String args)
		{

		}
	}

	private void showErrorMessage()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Data Error");
		builder.setMessage("Press OK to try again");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialogInterface, int i)
			{
				dialogInterface.dismiss();
				downloadNewestVideo();
			}
		});

		builder.setNegativeButton("Close", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialogInterface, int i)
			{
				dialogInterface.dismiss();
			}
		});
		builder.show();
	}
	//
	// private void loadFeaturedVideo(){
	//
	// progress = ProgressDialog.show(getActivity(), "Please
	// wait",getResources().getString(R.string.DownloadingFeaturedVideo),
	// true, false);
	//
	// SvcApiService.getUserIdEndPoint().GetFeaturedVideos(new SvcApiRestCallback<List<FeaturedVideo>>() {
	// @Override
	// public void failure(SvcError svcError) {
	// Log.d(TAG, "Failed to download featured video info");
	// if (fVideos.size() > 0) {
	// fVideos.clear();
	// adapter = new FeaturedVideoGridAdapter((Main_Tab_Activity) getActivity(), fVideos, Featured_Fragment.this);
	// gridView_featured.setAdapter(adapter);
	// }
	// Runnable progressRunnable = new Runnable() {
	// @Override
	// public void run() {
	// progress.dismiss();
	// showErrorMessage();
	// }
	// };
	// mainHandler.post(progressRunnable);
	//
	// }
	//
	// @Override
	// public void success(List<FeaturedVideo> featuredVideos, Response response) {
	// Log.d(TAG, "Success to download featured video info");
	// isSucceed = true;
	// if (featuredVideos != null) {
	// if (featuredVideos.size() > 0) {
	// for (int videoIndex = 0; videoIndex < featuredVideos.size(); videoIndex++) {
	// if (checkJsonValidity(featuredVideos.get(videoIndex)))
	// fVideos.add(featuredVideos.get(videoIndex));
	// else
	// isSucceed = false;
	// }
	// } else {
	// isSucceed = false;
	// }
	// } else {
	// isSucceed = false;
	// }
	//
	// Runnable progressRunnable = new Runnable() {
	// @Override
	// public void run() {
	// progress.dismiss();
	// if (isSucceed == true) {
	// adapter = new FeaturedVideoGridAdapter((Main_Tab_Activity) getActivity(), fVideos, Featured_Fragment.this);
	// gridView_featured.setAdapter(adapter);
	// ((Main_Tab_Activity) getActivity()).checkDeeplink();
	// } else {
	// showErrorMessage();
	// }
	//
	// }
	// };
	// mainHandler.post(progressRunnable);
	// }
	// });
	// }

	public void downloadNewestVideo()
	{
		String categoryApiName = "new";
		String categoryProgressName = getResources().getString(R.string.DownloadingNewestVideo);
		if (progress == null)
		{
			progress = ProgressDialog.show(getActivity(), "Please wait", categoryProgressName, true, false);
		}
		SvcApiService.getUserIdEndPoint().GetCategoryVideos(mCurrentPage, ITEMS_PPAGE, categoryApiName, new SvcApiRestCallback<List<FeaturedVideo>>()
		{
			@Override
			public void failure(SvcError svcError)
			{
				Log.d(TAG, "Failed to download featured video info");
				if (fVideos.size() > 0)
				{
					fVideos.clear();
					adapter = new FeaturedVideoGridAdapter((Main_Tab_Activity) getActivity(), fVideos, Featured_Fragment.this);
					gridView_featured.setAdapter(adapter);
				}
				mLoading = false;
				Runnable progressRunnable = new Runnable()
				{
					@Override
					public void run()
					{
						progress.dismiss();
						showErrorMessage();
					}
				};
				mainHandler.post(progressRunnable);
			}

			@Override
			public void success(List<FeaturedVideo> featuredVideos, Response response)
			{

				mLoading = false;
				isSucceed = true;

				if (featuredVideos != null)
				{
					if (featuredVideos.size() > 0)
					{
						Log.d(TAG, "Success to download featured video info = " + featuredVideos.size());
						for (int videoIndex = 0; videoIndex < featuredVideos.size(); videoIndex++)
						{
							if (checkJsonValidity(featuredVideos.get(videoIndex)))
							{
								fVideos.add(featuredVideos.get(videoIndex));
							}
							else
							{
								isSucceed = false;
								Log.d(TAG, "failed to check featured video validity index= " + videoIndex);
								Log.d(TAG, "video name " + featuredVideos.get(videoIndex).getVideoName());
								Log.d(TAG, "video image= " + featuredVideos.get(videoIndex).getVideoImage());
								Log.d(TAG, "video band name= " + featuredVideos.get(videoIndex).getBandName());
								Log.d(TAG, "video mp4 path= " + featuredVideos.get(videoIndex).getMp4Path());
								Log.d(TAG, "video m3u8 path= " + featuredVideos.get(videoIndex).getM3u8Path());
							}
						}
					}
					else
					{
						isSucceed = false;
					}
				}
				else
				{
					isSucceed = false;
				}

				Runnable progressRunnable = new Runnable()
				{
					@Override
					public void run()
					{
						progress.dismiss();
						if (isSucceed)
						{
							adapter = new FeaturedVideoGridAdapter((Main_Tab_Activity) getActivity(), fVideos, Featured_Fragment.this);
							gridView_featured.setAdapter(adapter);

							boolean isMailPosted = PreferencesUtil.loadBool(getActivity(), PreferencesUtil.IS_MAIL_ADDRESS_POSTED, false);
							if (!isMailPosted)
							{
								// here put the mail ID to server
								String emailID = Utils.getPrimaryEmailAddress(getActivity());
								if (emailID != null)
								{
									URLToHit = URLToHit + emailID;
									Utils.postEmailCall(URLToHit, getActivity());
									System.out.println("Primary Email Address is :" + emailID);
								}
							}

						}
						else
						{
							showErrorMessage();
						}

					}
				};
				mainHandler.post(progressRunnable);

			}
		});
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Splash_Activity.processAnalyticsTracking("Newest");
	}

	private boolean checkJsonValidity(FeaturedVideo fvideo)
	{
		if (fvideo.getVideoType().isEmpty() || fvideo.getVideoName().isEmpty() || fvideo.getVideoImage().isEmpty() || fvideo.getBandName().isEmpty() || fvideo.getM3u8Path().isEmpty() || fvideo.getMp4Path().isEmpty())
			return false;

		return true;
	}

}
