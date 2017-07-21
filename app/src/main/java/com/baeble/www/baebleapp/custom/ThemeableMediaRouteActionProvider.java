package com.baeble.www.baebleapp.custom;

import android.content.Context;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.app.MediaRouteButton;

/**
 * Created by abdulrehman on 4/26/17.
 */

/**
 * A MediaRouteActionProvider that allows the use of a ThemeableMediaRouteButton.
 */
public class ThemeableMediaRouteActionProvider extends MediaRouteActionProvider
{

	public ThemeableMediaRouteActionProvider(Context context)
	{
		super(context);
	}

	@Override
	public MediaRouteButton onCreateMediaRouteButton()
	{
		return new ThemeableMediaRouteButton(getContext());
	}
}
