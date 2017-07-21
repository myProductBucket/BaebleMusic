package com.baeble.www.baebleapp;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.baeble.www.baebleapp.tv.utils.Utils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kwicr.sdk.Kwicr;
import com.kwicr.sdk.KwicrStatusListener;


public class Splash_Activity extends Activity
{
	private static final String TAG = Splash_Activity.class.getSimpleName();
	private static AnalyticsApplication application;

	RelativeLayout relative_splash;
	private final int SPLASH_SCREEN_HOLD_TIME = 3000; // ms

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);

		// Obtain the shared Tracker instance for Google Analytics.
		application = (AnalyticsApplication) getApplication();
		processAnalyticsTracking("Splash");

		// Initialize the KWICR SDK
		// Kwicr.startKwicrAcceleration(getApplication(), "BdTF0ubOq55Mli8C2MLEj8ZCQF6lrCFY");
		Kwicr.setStatusListener(new KwicrStatusListener()
		{
			@Override
			public void onAccelerationStateChanged(final boolean kwicrReady, final boolean accelerating, final int reasonCode, final String reasonMessage)
			{

				Log.d(TAG, "kwicrReady = " + kwicrReady + ", accelerating=" + accelerating);
				if (reasonCode != 0)
				{
					Log.d(TAG, "reasonCode=" + reasonCode + ", reasonMessage=" + reasonMessage);
				}
			}
		});

		/* stop the other app's music play when launch the app */
		stopOtherPlayer(this);

		relative_splash = (RelativeLayout) findViewById(R.id.relative_splash);
		relative_splash.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				Intent intent = new Intent(Splash_Activity.this, Main_Tab_Activity.class);
				startActivity(intent);
				finish();
			}
		}, SPLASH_SCREEN_HOLD_TIME);

	}

	public static void processAnalyticsTracking(String name)
	{
		try
		{
			Tracker mTracker = application.getDefaultTracker();
			mTracker.enableAdvertisingIdCollection(true);
			mTracker.setScreenName(name);
			mTracker.send(new HitBuilders.ScreenViewBuilder().build());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.d(TAG, "PAT: application = " + application);
		}
	}

	public static void stopOtherPlayer(Context context)
	{
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		// Request audio focus for playback
		int result = am.requestAudioFocus(null,
				// Use the music stream.
				AudioManager.STREAM_MUSIC,
				// Request permanent focus.
				AudioManager.AUDIOFOCUS_GAIN);

		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
		{
			Log.d(TAG, "other app had stopped playing song now , so u can do u stuff now");
		}
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public void onNewIntent(Intent intent)
	{
		this.setIntent(intent);
	}



}
