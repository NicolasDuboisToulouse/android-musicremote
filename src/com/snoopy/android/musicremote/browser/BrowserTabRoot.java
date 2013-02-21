package com.snoopy.android.musicremote.browser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snoopy.android.musicremote.R;

/**
 * Browse the root
 * 
 */
public class BrowserTabRoot extends BrowserAbsTabs {

	/**
	 * Internal view
	 */
	private View mView = null;

	/**
	 * Artists browser
	 */
	private BrowserListArtists mBrowserArtists;
	
	/**
	 * Albums browser
	 */
	private BrowserListAlbums mBrowserAlbums;
	

	/**
	 * Ctor
	 */
	public BrowserTabRoot() {
		mBrowserArtists = new BrowserListArtists();		
		mBrowserAlbums = new BrowserListAlbums();
	}
	
	/**
	 * Build the view
	 */
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
	{
		if (mView == null) {
			mView = super.onCreateView(inflater, container, savedInstanceState);
			addTab(mBrowserArtists, R.drawable.button_all_artists);
			addTab(mBrowserAlbums, R.drawable.button_all_albums);
		}
		return mView;
	}
}
