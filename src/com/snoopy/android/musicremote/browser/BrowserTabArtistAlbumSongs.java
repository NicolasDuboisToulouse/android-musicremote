package com.snoopy.android.musicremote.browser;

import org.xbmc.android.jsonrpc.api.model.AudioModel.AlbumDetail;
import org.xbmc.android.jsonrpc.api.model.AudioModel.ArtistDetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snoopy.android.musicremote.R;
import com.snoopy.android.musicremote.browser.BrowserAbsList.OnLoadedListener;


/**
 * Browse songs of an album/artist
 * 
 * This is a tab view with one tab displaying album filtered by artist
 * and the second one displaying all artists.
 * 
 * If you don't set artist, this class behave like BrowserAlbumSongs.
 *
 */
public class BrowserTabArtistAlbumSongs extends BrowserAbsTabs {
	
	/**
	 * Internal view
	 */
	private View mView = null;

	/**
	 * Artist album browser
	 */
	private BrowserListAlbumSongs mAlbumArtistSongs;
	
	/**
	 * Artist song browser
	 */
	private BrowserListAlbumSongs mAlbumAllSongs;
	

	/**
	 * Ctor
	 */
	public BrowserTabArtistAlbumSongs() {
		mAlbumArtistSongs = new BrowserListAlbumSongs();		
		mAlbumAllSongs = new BrowserListAlbumSongs();
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
			addTab(mAlbumArtistSongs, R.drawable.button_artist);
			addTab(mAlbumAllSongs, R.drawable.button_all_artists);
			
			setButtonsVisibility(false);
			
			mAlbumArtistSongs.setOnLoadedListener(new OnLoadedListener() {
				@Override
				public void onLoaded() {
					if (mAlbumArtistSongs.haveOtherArtists() == true) {
						setButtonsVisibility(true);
					}
				}
			});
		}
		return mView;
	}
	
	/**
	 * Set album to be displayed and artist filter
	 * If artist is null, this will class behave like BrowserAlbumSongs.
	 */
	public void setAlbumArtist(AlbumDetail album, ArtistDetail artist) {
		mAlbumArtistSongs.setAlbumArtist(album, artist);
		mAlbumAllSongs.setAlbum(album);
		if (mView != null) {
			setButtonsVisibility(false);
			setTab(mAlbumArtistSongs);
		}
	}
}
