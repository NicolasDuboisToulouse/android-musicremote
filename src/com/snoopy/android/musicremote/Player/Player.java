package com.snoopy.android.musicremote.Player;

import org.xbmc.android.jsonrpc.api.AbstractCall;
import org.xbmc.android.jsonrpc.api.call.Player.Open.ItemPlaylistIdPosition;
import org.xbmc.android.jsonrpc.api.model.AudioModel.SongDetail;
import org.xbmc.android.jsonrpc.io.ApiCallback;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snoopy.android.musicremote.R;
import com.snoopy.android.musicremote.RemoteAbsFragment;

/**
 * Player
 */
public class Player extends RemoteAbsFragment
{
	/**
	 * XBMC music playlist/player ID (hard-coded in XBMC)
	 */
	public static final int PLAYER_MUSIC = 0;
	
	/**
	 * Default playing song
	 */
	public static final int NO_ITEM_PLAYING = -1;
		

	/**
	 * Internal thread to refresh the list
	 */
	private PlayerPollingThread mPollingThread;
	
	/**
	 * Playlist
	 */
	private Playlist mPlaylist = null;
	
	
	/**
	 * Return the current polling thread
	 * May return null if fragment is not started
	 */
	PlayerPollingThread getPollingThread() {
		return mPollingThread;
	}
	
	/**
	 * Queue a song in the playlist. Will be played by callback if playlist empty.
	 */
	public void queue(SongDetail song) {
		getPlaylist().queue(song);
	}
	
	/**
	 * Play a song in the playlist
	 */
	public void play(int playlistItemId) {
		getConnection().call(new org.xbmc.android.jsonrpc.api.call.Player.Open(
				new ItemPlaylistIdPosition(PLAYER_MUSIC, playlistItemId)),
				new ApiCallback<String>() {
			@Override
			public void onResponse(AbstractCall<String> call) {}
			@Override
			public void onError(int code, String message, String hint) {}
		});
	}
	
	
	/**
	 * Access to the playlist
	 */
	public Playlist getPlaylist() {
		if (mPlaylist == null) {
			mPlaylist = (Playlist)getFragmentManager().findFragmentById(R.id.playlist);
		}
		return mPlaylist;
	}
	

	/**
	 * Build the view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
        return inflater.inflate(R.layout.player, container, false);
	}

		
	/**
	 * Start pooling thread on resume
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (mPollingThread == null) {
			mPollingThread = new PlayerPollingThread(this);
			mPollingThread.start();
		}
	}
	
	/**
	 * Stop pooling thread on pause
	 */
	@Override
	public void onPause() {
		super.onPause();
		try {
			mPollingThread.interrupt();
			mPollingThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mPollingThread = null;
	}
}