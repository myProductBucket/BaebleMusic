package com.baeble.www.baebleapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class PreferencesUtil
{

	public static final String PREF_FILE_NAME = "baeblemusic.pref";
	public static final String IS_MAIL_ADDRESS_POSTED = "IS_MAIL_ADDRESS_POSTED";

	public static void saveString(final Context context, String key, String value)
	{
		new AsyncTask<String, Void, Void>()
		{
			@Override
			protected Void doInBackground(String... params)
			{
				SharedPreferences.Editor editor = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE).edit();
				editor.putString(params[0], params[1]);
				editor.commit();
				return null;
			}
		}.execute(key, value);
	}

	public static void saveLong(final Context context, String key, long value)
	{
		new AsyncTask<String, Void, Void>()
		{
			@Override
			protected Void doInBackground(String... params)
			{
				SharedPreferences.Editor editor = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE).edit();
				editor.putLong(params[0], Long.valueOf(params[1]));
				editor.commit();
				return null;
			}
		}.execute(key, String.valueOf(value));
	}

	public static void saveBoolean(final Context context, String key, boolean value)
	{
		new AsyncTask<String, Void, Void>()
		{
			@Override
			protected Void doInBackground(String... params)
			{
				SharedPreferences.Editor editor = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE).edit();
				editor.putBoolean(params[0], Boolean.valueOf(params[1]));
				editor.commit();
				return null;
			}
		}.execute(key, String.valueOf(value));
	}

	public static void saveInt(final Context context, String key, int value)
	{
		new AsyncTask<String, Void, Void>()
		{
			@Override
			protected Void doInBackground(String... params)
			{
				SharedPreferences.Editor editor = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE).edit();
				editor.putInt(params[0], Integer.valueOf(params[1]));
				editor.commit();
				return null;
			}
		}.execute(key, String.valueOf(value));
	}

	public static String loadString(Context context, String key)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
		return preferences.getString(key, "");
	}

	public static long loadLong(Context context, String key, long defaultValue)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
		return preferences.getLong(key, defaultValue);
	}

	public static int loadInt(Context context, String key, int defaultValue)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
		return preferences.getInt(key, defaultValue);
	}

	public static boolean loadBool(Context context, String key, boolean defaultValue)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(key, defaultValue);
	}

	public static void clearAll(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}
}