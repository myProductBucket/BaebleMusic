package com.baeble.www.baebleapp.custom;

/**
 * Created by abdulrehman on 4/26/17.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.MediaRouteButton;
import android.util.AttributeSet;

import com.baeble.www.baebleapp.R;

/**
 * Allows theming of the MediaRouteButton using the theme associated with the context passed in.
 */
public class ThemeableMediaRouteButton extends MediaRouteButton
{

	private static final String TAG = ThemeableMediaRouteButton.class.getSimpleName();

	private int mMinWidth;
	private int mMinHeight;
	private int mColor;
	private Drawable mRemoteIndicator;

	public ThemeableMediaRouteButton(Context context)
	{
		this(context, null);
	}

	public ThemeableMediaRouteButton(Context context, AttributeSet attrs)
	{
		this(context, attrs, R.attr.mediaRouteButtonStyle);
	}

	public ThemeableMediaRouteButton(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ThemeableMediaRouteButton, defStyleAttr, 0);
		mColor = a.getColor(R.styleable.ThemeableMediaRouteButton_iconColor, 0);
		setRemoteIndicatorDrawable(a.getDrawable(R.styleable.ThemeableMediaRouteButton_routeEnabledDrawable));
		mMinWidth = a.getDimensionPixelSize(R.styleable.ThemeableMediaRouteButton_android_minWidth, 0);
		mMinHeight = a.getDimensionPixelSize(R.styleable.ThemeableMediaRouteButton_android_minHeight, 0);

		a.recycle();
	}

	@Override
	protected void drawableStateChanged()
	{
		super.drawableStateChanged();

		if (mRemoteIndicator != null)
		{
			int[] myDrawableState = getDrawableState();
			mRemoteIndicator.setState(myDrawableState);
			invalidate();
		}
	}

	public void setRemoteIndicatorDrawable(Drawable d)
	{
		if (mRemoteIndicator != null)
		{
			mRemoteIndicator.setCallback(null);
			unscheduleDrawable(mRemoteIndicator);
		}
		mRemoteIndicator = d;
		if (d != null)
		{
			d.setColorFilter(mColor, PorterDuff.Mode.SRC_ATOP);
			d.setCallback(this);
			d.setState(getDrawableState());
			d.setVisible(getVisibility() == VISIBLE, false);
		}

		refreshDrawableState();
	}

	@Override
	protected boolean verifyDrawable(Drawable who)
	{
		return super.verifyDrawable(who) || who == mRemoteIndicator;
	}

	@Override
	public void jumpDrawablesToCurrentState()
	{
		// We can't call super to handle the background so we do it ourselves.
		// super.jumpDrawablesToCurrentState();
		if (getBackground() != null)
		{
			DrawableCompat.jumpToCurrentState(getBackground());
		}
		// Handle our own remote indicator.
		if (mRemoteIndicator != null)
		{
			DrawableCompat.jumpToCurrentState(mRemoteIndicator);
		}
	}

	@Override
	public void setVisibility(int visibility)
	{
		super.setVisibility(visibility);

		if (mRemoteIndicator != null)
		{
			mRemoteIndicator.setVisible(getVisibility() == VISIBLE, false);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		final int minWidth = Math.max(mMinWidth, mRemoteIndicator != null ? mRemoteIndicator.getIntrinsicWidth() : 0);
		final int minHeight = Math.max(mMinHeight, mRemoteIndicator != null ? mRemoteIndicator.getIntrinsicHeight() : 0);
		int width;
		switch (widthMode)
		{
		case MeasureSpec.EXACTLY:
			width = widthSize;
			break;
		case MeasureSpec.AT_MOST:
			width = Math.min(widthSize, minWidth + getPaddingLeft() + getPaddingRight());
			break;
		default:
		case MeasureSpec.UNSPECIFIED:
			width = minWidth + getPaddingLeft() + getPaddingRight();
			break;
		}
		int height;
		switch (heightMode)
		{
		case MeasureSpec.EXACTLY:
			height = heightSize;
			break;
		case MeasureSpec.AT_MOST:
			height = Math.min(heightSize, minHeight + getPaddingTop() + getPaddingBottom());
			break;
		default:
		case MeasureSpec.UNSPECIFIED:
			height = minHeight + getPaddingTop() + getPaddingBottom();
			break;
		}
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if (mRemoteIndicator != null)
		{
			final int left = getPaddingLeft();
			final int right = getWidth() - getPaddingRight();
			final int top = getPaddingTop();
			final int bottom = getHeight() - getPaddingBottom();

			final int drawWidth = mRemoteIndicator.getIntrinsicWidth();
			final int drawHeight = mRemoteIndicator.getIntrinsicHeight();
			final int drawLeft = left + (right - left - drawWidth) / 2;
			final int drawTop = top + (bottom - top - drawHeight) / 2;

			mRemoteIndicator.setBounds(drawLeft, drawTop, drawLeft + drawWidth, drawTop + drawHeight);
			mRemoteIndicator.draw(canvas);
		}
	}
}
