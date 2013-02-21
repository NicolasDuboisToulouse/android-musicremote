package com.snoopy.android.musicremote.widgets;


import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.snoopy.android.musicremote.R;

public class RemoteListLayout extends LinearLayout
{
	/**
	 * List view
	 */
	private ListView mListView;

	/**
	 * Header View
	 */
	private TextView mHeader;
	
	/**
	 * Warning handling
	 */
	private View mWarningView;
	private TextView mWarningText;

	/**
	 * Loading handling
	 */
	private View mLoadingView;

	/**
	 * Button menu
	 */
	private View mButtonMenu;
	
	
	public RemoteListLayout(Context context) {
		super(context);
	}

	public RemoteListLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/**
	 * @return the internal list view
	 */
	public ListView getListView() {
		if (mListView == null) { 
			mListView = (ListView)findViewById(R.id.view_list);
			
		}
		return mListView;
	}
	
	/**
	 * Set a text in the list header
	 */
	public void setHeader(String text) {
		if (mHeader == null) {
			mHeader = (TextView)findViewById(R.id.view_header);
		}
		mHeader.setText(text);
	}
	
	public ListAdapter getAdapter() {
		return getListView().getAdapter();
	}
	
	
	/**
	 * Display a warning message. Replace the previous one if any.
	 */
	public void setWarning(String text) {
		clearLoading();
		if (mWarningView == null) {
			mWarningView = findViewById(R.id.view_warning);
			mWarningText = (TextView)mWarningView.findViewById(R.id.view_warning_text);
		}
		mWarningText.setText(text);
		mWarningView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Clear the warning message (if any)
	 */
	public void clearWarning() {
		if (mWarningView != null) {
			mWarningView.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Display "loading" message
	 */
	public void setLoading() {
		clearWarning();
		initLoading();
		mLoadingView.setVisibility(View.VISIBLE);	
	}
	
	/**
	 * Hide loading message
	 */
	public void clearLoading() {
		initLoading();
		mLoadingView.setVisibility(View.GONE);
	}

	/**
	 * Show or hide button menu
	 */
	public void setMenuButtonVisibility(boolean visible) {
		if (mButtonMenu == null) {
			mButtonMenu = findViewById(R.id.button_menu);
		}
		mButtonMenu.setVisibility((visible)? View.VISIBLE : View.GONE);
	}
	
	/**
	 * Set a listenener for button menu
	 */
	public void setOnButtonMenuListener(View.OnClickListener listener) {
		if (mButtonMenu == null) {
			mButtonMenu = findViewById(R.id.button_menu);
		}
		mButtonMenu.setOnClickListener(listener);
	}
	
	
	private void initLoading() {
		if (mLoadingView == null) {
			mLoadingView = findViewById(R.id.view_loading);
			ImageView icon = (ImageView)findViewById(R.id.view_loading_icon);
	        AnimationDrawable frameAnimation = (AnimationDrawable)icon.getDrawable();
	        frameAnimation.setCallback(icon);
	        frameAnimation.setVisible(true, true);
		}		
	}
}
