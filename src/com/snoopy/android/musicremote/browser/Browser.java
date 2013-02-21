package com.snoopy.android.musicremote.browser;

import org.xbmc.android.jsonrpc.api.model.AudioModel.AlbumDetail;
import org.xbmc.android.jsonrpc.api.model.AudioModel.ArtistDetail;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snoopy.android.musicremote.R;


/**
 * Base browser class
 */
public class Browser extends Fragment {

	/**
	 * Root browser
	 */
	private BrowserTabRoot mBrowserRoot;

	/**
	 * One artist browser
	 */
	private BrowserTabOneArtist mBrowserOneArtist;
	
	/**
	 * Album artist songs browser
	 */
	private BrowserTabArtistAlbumSongs mBrowserArtistAlbumSongs;

	
	/**
	 * Ctor
	 */
	public Browser() {
        mBrowserRoot = new BrowserTabRoot();
        mBrowserOneArtist = new BrowserTabOneArtist();
        mBrowserArtistAlbumSongs = new BrowserTabArtistAlbumSongs();
	}
		
	/**
	 * Create main view
	 */
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.browser, container, false);
        browseRoot();
        return view;
     }
    
	
	/**
	 * Browser all artists
	 */
	public void browseRoot() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();	
		ft.replace(R.id.view_browser_container, mBrowserRoot);
		ft.commit();
	}

	
	/**
	 * Browse one artist
	 */
	public void browseOneArtist(ArtistDetail artist) {
		mBrowserOneArtist.setArtist(artist);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.view_browser_container, mBrowserOneArtist);		
		ft.addToBackStack(null);
		ft.commit();
	}

	/**
	 * Browse songs of one artist album
	 */
	public void browserArtistAlbumSongs(ArtistDetail artist, AlbumDetail album) {
		mBrowserArtistAlbumSongs.setAlbumArtist(album, artist);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.view_browser_container, mBrowserArtistAlbumSongs);		
		ft.addToBackStack(null);
		ft.commit();
	}	
}
