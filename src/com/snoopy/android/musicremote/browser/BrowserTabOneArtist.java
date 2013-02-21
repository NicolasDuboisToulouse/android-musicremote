package com.snoopy.android.musicremote.browser;

import org.xbmc.android.jsonrpc.api.model.AudioModel.ArtistDetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snoopy.android.musicremote.R;
import com.snoopy.android.musicremote.browser.BrowserAbsList.OnLoadedListener;

/**
 * Browse albums or songs of one artist
 * 
 * This is a tabbed view with one tab to browse albums and one to browse songs.
 * 
 */
public class BrowserTabOneArtist extends BrowserAbsTabs {

	/**
	 * Internal view
	 */
	private View mView = null;

	/**
	 * Artist album browser
	 */
	private BrowserListAlbums mBrowserOneArtistAlbums;
	
	/**
	 * Artist song browser
	 */
	private BrowserListOneArtistSongs mBrowserOneArtistSongs;
	

	/**
	 * Ctor
	 */
	public BrowserTabOneArtist() {
		mBrowserOneArtistAlbums = new BrowserListAlbums();		
		mBrowserOneArtistSongs = new BrowserListOneArtistSongs();
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
			addTab(mBrowserOneArtistAlbums, R.drawable.button_album);
			addTab(mBrowserOneArtistSongs, R.drawable.button_song);
			
			mBrowserOneArtistAlbums.setOnLoadedListener(new OnLoadedListener() {
				@Override
				public void onLoaded() {
					if (mBrowserOneArtistAlbums.haveAlbums() == false) {
						setButtonsVisibility(false);
						setTab(mBrowserOneArtistSongs);
					}
				}
			});

		}
		return mView;
	}
	
	/**
	 * @param artist new artist
	 */
	public void setArtist(ArtistDetail artist) {
		mBrowserOneArtistAlbums.setArtist(artist);
		mBrowserOneArtistSongs.setArtist(artist);
		if (mView != null) {
			setButtonsVisibility(true);
			setTab(mBrowserOneArtistAlbums);
		}

	}
}
