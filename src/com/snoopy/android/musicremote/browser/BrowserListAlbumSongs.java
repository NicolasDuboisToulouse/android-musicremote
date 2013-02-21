package com.snoopy.android.musicremote.browser;

import java.util.Iterator;
import java.util.List;

import org.xbmc.android.jsonrpc.api.AbstractCall;
import org.xbmc.android.jsonrpc.api.call.AudioLibrary;
import org.xbmc.android.jsonrpc.api.model.AudioModel.AlbumDetail;
import org.xbmc.android.jsonrpc.api.model.AudioModel.ArtistDetail;
import org.xbmc.android.jsonrpc.api.model.AudioModel.SongDetail;
import org.xbmc.android.jsonrpc.api.model.ListModel.Sort;

import android.widget.ArrayAdapter;

import com.snoopy.android.musicremote.listAdapters.ListAdapterSongs;



/**
 * Album songs browser.
 * 
 * Browse song of an album. Allow artist filter.
 * 
 */
public class BrowserListAlbumSongs extends BrowserAbsList<SongDetail> 
{
	
	/**
	 * Current artist to be displayed
	 */
	private ArtistDetail mArtist;
	
	/**
	 * Current album to be displayed
	 */
	private AlbumDetail mAlbum;
	
	/**
	 * Do we have other artists in this album ?
	 */
	private boolean mHaveOtherArtists = false;

	
	/**
	 * Set album and filter by artist
	 * @param album new album
	 * @param artist artist filter
	 */
	public void setAlbumArtist(AlbumDetail album, ArtistDetail artist) {
		mAlbum = album;
		mArtist = artist;
		needRefresh();
	}

	/**
	 * Set album but don't filter by artist
	 * @param album
	 */
	public void setAlbum(AlbumDetail album) {
		mAlbum = album;
		mArtist = null;
		needRefresh();
	}
	
	/**
	 * return true if current album have other artists than the selected one.
	 * (return false if no artist set)
	 * This value is updated only once we receive data from XBMC.
	 * The better way to use it is to use OnLoadedListener.
	 */
	public boolean haveOtherArtists() {
		return mHaveOtherArtists;
	}


	/**
	 * Browse song on item selected
	 */
	@Override
	protected void onItemSelected(SongDetail song) {
		getPlayer().queue(song);
	};

	
	/**
	 * Build header
	 */
	@Override
	protected String getHeader() {
		if (mAlbum == null) {
			return "Album";
		} else {
			if (mArtist != null) {
				return mArtist.artist  + " - " + mAlbum.label;
			} else {
				return mAlbum.label;
			}
		}
	}
	
	
	/**
	 * Build XBMC query
	 */
	@Override
	protected AbstractCall<SongDetail> getRemoteQuery() {
		if (mAlbum == null) {
			setWarning("No album selected");
			return null;
		}

		// TODO: is there any way to filter by albumid AND by artistid ?
		return new AudioLibrary.GetSongs(
						null,
						new Sort(false,                                                // sort, ignoreArticles
								SongDetail.TITLE,                                      // sort, by
								Sort.Order.ASCENDING),                                 // sort, order
						new AudioLibrary.GetSongs.FilterAlbumId(mAlbum.albumid),       // filter
						SongDetail.DURATION, SongDetail.ARTISTID, SongDetail.ARTIST);
	}


	/**
	 * Build the list adapter
	 */
	@Override
	protected ArrayAdapter<SongDetail> getArrayAdapter(List<SongDetail> songs) {

		mHaveOtherArtists = false;
		if (mArtist != null) {
			// TODO: manual filter by artist (see TODO upside)
			Iterator<SongDetail> it = songs.iterator();
			while( it.hasNext() ) {
				SongDetail song = it.next();
				if(song.artistid.contains(mArtist.artistid) == false) {
					mHaveOtherArtists = true;
					it.remove();
				}
			}
		}

		return new ListAdapterSongs(getActivity(), songs);
	}	
}
