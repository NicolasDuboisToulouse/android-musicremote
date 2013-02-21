package com.snoopy.android.musicremote.browser;

import java.util.List;

import org.xbmc.android.jsonrpc.api.AbstractCall;
import org.xbmc.android.jsonrpc.api.call.AudioLibrary;
import org.xbmc.android.jsonrpc.api.model.AudioModel.ArtistDetail;
import org.xbmc.android.jsonrpc.api.model.ListModel.Sort;

import android.widget.ArrayAdapter;

import com.snoopy.android.musicremote.listAdapters.ListAdapterArtists;

/**
 * Browse artists
 */
public class BrowserListArtists extends BrowserAbsList<ArtistDetail> {

	/**
	 * Browse artist on item selected
	 */
	@Override
	protected void onItemSelected(ArtistDetail artist) {
		getBrowser().browseOneArtist(artist);
	};

	
	/**
	 * Build header
	 */
	@Override
	protected String getHeader() {
		return "Artists";
	}

	
	/**
	 * Build XBMC query
	 */
	@Override
	protected AbstractCall<ArtistDetail> getRemoteQuery() {
		return new AudioLibrary.GetArtists(
						false,                                             // not only album artists 
						null,                                              // limits
						new Sort(false,                                    // sort, ignoreArticles
								ArtistDetail.LABEL,                        // sort, by
								Sort.Order.ASCENDING),                     // sort, order
						(AudioLibrary.GetArtists.FilterGenreId)null,       // filter
						ArtistDetail.THUMBNAIL);                           // Extra infos
	}

	
	/**
	 * Build the list adapter
	 */
	@Override
	protected ArrayAdapter<ArtistDetail> getArrayAdapter(
			List<ArtistDetail> artists) {
		return new ListAdapterArtists(getActivity(), artists);
	}
}
