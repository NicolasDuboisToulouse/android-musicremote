package com.snoopy.android.musicremote.browser;

import java.util.List;

import org.xbmc.android.jsonrpc.api.AbstractCall;
import org.xbmc.android.jsonrpc.api.call.AudioLibrary;
import org.xbmc.android.jsonrpc.api.model.AudioModel.ArtistDetail;
import org.xbmc.android.jsonrpc.api.model.AudioModel.SongDetail;
import org.xbmc.android.jsonrpc.api.model.ListModel.Sort;

import android.widget.ArrayAdapter;

import com.snoopy.android.musicremote.listAdapters.ListAdapterSongs;


/**
 * Browse all song of the given artist
 */
public class BrowserListOneArtistSongs extends BrowserAbsList<SongDetail>
{
	/**
	 * Current artist to be displayed
	 */
	private ArtistDetail mArtist;

		
	/**
	 * @param artist new artist
	 */
	public void setArtist(ArtistDetail artist) {
		mArtist = artist;
		needRefresh();
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
		if (mArtist == null) {
			return "Artist";
		} else {
			return mArtist.artist;			
		}
	}


	/**
	 * Build XBMC query
	 */
	@Override
	protected AbstractCall<SongDetail> getRemoteQuery() {

		if (mArtist == null) {
			setWarning("No artist selected");
			return null;
		}

		return new AudioLibrary.GetSongs(
				null,
				new Sort(false,                                                // sort, ignoreArticles
						SongDetail.TITLE,                                      // sort, by
						Sort.Order.ASCENDING),                                 // sort, order
				new AudioLibrary.GetSongs.FilterArtistId(mArtist.artistid),    // filter
				SongDetail.DURATION, SongDetail.ARTIST);
	}

	
	/**
	 * Build the list adapter
	 */
	@Override
	protected ArrayAdapter<SongDetail> getArrayAdapter(List<SongDetail> songs) {
		return new ListAdapterSongs(getActivity(), songs);
	}
}
