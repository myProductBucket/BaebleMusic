/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.baeble.www.baebleapp.tv.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;

import com.baeble.www.baebleapp.PreferencesUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * A collection of utility methods, all static.
 */
public class Utils
{
	public static Context context;

	public static int convertDpToPixel(Context ctx, int dp)
	{
		float density = ctx.getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}

	/**
	 * Will read the content from a given {@link InputStream} and return it as a {@link String}.
	 *
	 * @param inputStream
	 *            The {@link InputStream} which should be read.
	 * @return Returns <code>null</code> if the the {@link InputStream} could not be read. Else returns the content of
	 *         the {@link InputStream} as {@link String}.
	 */
	public static String inputStreamToString(InputStream inputStream)
	{
		try
		{
			byte[] bytes = new byte[inputStream.available()];
			inputStream.read(bytes, 0, bytes.length);
			String json = new String(bytes);
			return json;
		}
		catch (IOException e)
		{
			return null;
		}
	}

	public static Uri getResourceUri(Context context, int resID)
	{
		return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(resID) + '/' + context.getResources().getResourceTypeName(resID) + '/' + context.getResources().getResourceEntryName(resID));
	}

	public static String getPrimaryEmailAddress(Context context)
	{
		AccountManager manager = AccountManager.get(context);
		Account[] accounts = manager.getAccountsByType("com.google");
		List<String> possibleEmails = new LinkedList<String>();

		for (Account account : accounts)
		{
			// account.name as an email address only for certain account.type values.
			possibleEmails.add(account.name);
		}

		if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null)
		{
			String email = possibleEmails.get(0);
			return email;
		}
		return null;
	}

	public static void postEmailCall(String emailID, Context context)
	{
		Utils.context = context;
		new PostDataAsynchTask().execute(emailID);
	}

	public static class PostDataAsynchTask extends AsyncTask<String, Void, Void>
	{
		boolean isResultSuccess = false;

		@Override
		protected Void doInBackground(String... params)
		{
			try
			{
				HttpGet httpget = new HttpGet(params[0]);
				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse response = httpClient.execute(httpget);

				if (response != null)
				{
					isResultSuccess = true;
					// String result = EntityUtils.toString(response.getEntity());
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid)
		{
			super.onPostExecute(aVoid);
			if (isResultSuccess)
			{
				PreferencesUtil.saveBoolean(Utils.context, PreferencesUtil.IS_MAIL_ADDRESS_POSTED, true);
			}
		}
	}
}
