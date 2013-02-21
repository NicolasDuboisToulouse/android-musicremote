package com.snoopy.android.musicremote.browser;

import java.util.List;

import org.xbmc.android.jsonrpc.api.AbstractCall;
import org.xbmc.android.jsonrpc.api.call.AudioLibrary;
import org.xbmc.android.jsonrpc.api.model.AudioModel.AlbumDetail;
import org.xbmc.android.jsonrpc.api.model.AudioModel.ArtistDetail;
import org.xbmc.android.jsonrpc.api.model.ListModel.Sort;

import android.widget.ArrayAdapter;

import com.snoopy.android.musicremote.listAdapters.ListAdapterAlbums;

/**
 * Browse and select albums. May be filtered by artist.
 */
public class BrowserListAlbums extends BrowserAbsList<AlbumDetail> {



	/**
	 * Current artist to be displayed if any
	 */
	private ArtistDetail mArtist = null;
	
	/**
	 * Album count
	 */
	private int albumCount = 0;	
	
	/**
	 * Set artist filter
	 * @param artist new artist
	 */
	public void setArtist(ArtistDetail artist) {
		mArtist = artist;
		needRefresh();
	}
	
	/**
	 * Remove artist filter
	 */
	public void clearArtist() {
		mArtist = null;
		needRefresh();		
	}

	/**
	 * @return true if there is at least one album
	 * This value is updated only once we receive data from XBMC.
	 * The better way to use it is to use OnLoadedListener.
	 */
	public boolean haveAlbums() {
		return albumCount > 0;
	}

	
	/**
	 * Browse album on item selected
	 */
	@Override
	protected void onItemSelected(AlbumDetail album) {
		getBrowser().browserArtistAlbumSongs(mArtist, album);
	};

	
	/**
	 * Build header
	 */
	@Override
	protected String getHeader()
	{
		if (mArtist != null) {
			return mArtist.artist  + " - Albums";
		} else {
			return "Albums";
		}
	}

	/**
	 * Build XBMC query
	 */
	@Override
	protected AbstractCall<AlbumDetail> getRemoteQuery() {
		AbstractCall<AlbumDetail> albumQuery = null;
		if (mArtist != null) {
			albumQuery = 
				new AudioLibrary.GetAlbums(
						null,                                                          // limits 
						new Sort(false,                                                // sort, ignoreArticles
								AlbumDetail.YEAR,                                      // sort, by
								Sort.Order.ASCENDING),                                 // sort, order
						new AudioLibrary.GetAlbums.FilterArtistId(mArtist.artistid),   // filter
						AlbumDetail.ARTIST, AlbumDetail.YEAR, AlbumDetail.THUMBNAIL);  // Extra info
		} else {
			albumQuery = 
					new AudioLibrary.GetAlbums(
							null,                                                          // limits 
							new Sort(false,                                                // sort, ignoreArticles
									AlbumDetail.LABEL,                                     // sort, by
									Sort.Order.ASCENDING),                                 // sort, order
							AlbumDetail.ARTIST, AlbumDetail.YEAR, AlbumDetail.THUMBNAIL);  // Extra info			
		}

		return albumQuery;
	}

	
	/**
	 * Build the list adapter
	 */
	@Override
	protected ArrayAdapter<AlbumDetail> getArrayAdapter(
			List<AlbumDetail> albums) {
		
		albumCount = albums.size();
		
		// Remove the stupid "Singles" album from the count.
		// I don't find any way to prevent XBMC to generate this album.
		// Not removed from the list in case of it really exist an album named "Singles".
		if (albumCount == 1 && albums.get(0).label.compareTo("Singles") == 0) {
			albumCount = 0;
		}
		
		return new ListAdapterAlbums(getActivity(), albums);
	}
}
