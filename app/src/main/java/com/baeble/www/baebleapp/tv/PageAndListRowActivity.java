package com.baeble.www.baebleapp.tv;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.baeble.www.baebleapp.PreferencesUtil;
import com.baeble.www.baebleapp.R;
import com.baeble.www.baebleapp.tv.utils.Utils;

/**
 * Activity showcasing the use of {@link android.support.v17.leanback.widget.PageRow} and
 * {@link android.support.v17.leanback.widget.ListRow}.
 */
public class PageAndListRowActivity extends Activity
{
	public static int REQUEST_CODE = 8756;
	public String URLToHit = "http://svctest.baeblemusic.com/BaebleService.svc/json/addEmailCapture/";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tv_page_list_row);
		// Push Email ID to server
		boolean isMailPosted = PreferencesUtil.loadBool(PageAndListRowActivity.this, PreferencesUtil.IS_MAIL_ADDRESS_POSTED, false);
		if (!isMailPosted)
		{
			String emailID = Utils.getPrimaryEmailAddress(this);
			if (emailID != null)
			{
				URLToHit = URLToHit + emailID;
				Utils.postEmailCall(URLToHit, PageAndListRowActivity.this);
			}
		}
	}
}
