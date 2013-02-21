package com.snoopy.android.musicremote.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xbmc.android.jsonrpc.api.AbstractCall;
import org.xbmc.android.jsonrpc.api.call.Player.GetActivePlayers.GetActivePlayersResult;
import org.xbmc.android.jsonrpc.api.model.ListModel.AllItems;
import org.xbmc.android.jsonrpc.io.ApiCallback;

import com.snoopy.android.musicremote.IGlobalFeatures;

/*
 * Thread to update playlist and played item 
 */
public class PlayerPollingThread extends Thread 
{
	/**
	 * Notification interface
	 */
	public interface IPlayerNotification
	{
		/**
		 * Called when the currently playing song has changed
		 * Will return Player.NO_ITEM_PLAYING if no song are currently playing
		 */
		public void onSongPlayingChanged(int songId);
		
		/**
		 * Called when the playlist content has changed
		 */
		public void onPlaylistChanged(List<AllItems> songs, int playingSongId);
		
		/**
		 * XBMC connection error
		 */
		public void onError(int code, String message, String hint);		
	}
	
	/**
	 * Connection access
	 */
	final IGlobalFeatures mGlobalFeatures;
	
	/**
	 * Playlist content
	 */
	private List<AllItems> mPlaylistContent;
	
	/**
	 * Current playing item
	 */
	private int mPlayingSongId = Player.NO_ITEM_PLAYING;

	
	/**
	 * Client to notify
	 * TODO: change this to an array
	 */
	private List<IPlayerNotification> mNotifications;
	
	
	
	/**
	 * Ctor
	 */
	public PlayerPollingThread(IGlobalFeatures globalFeatures)
	{
		mGlobalFeatures = globalFeatures;
		mNotifications = new ArrayList<IPlayerNotification>();
	}
	
	/**
	 * Add a notification listener
	 */
	public void addListener(IPlayerNotification listener)
	{
		mNotifications.add(listener);
	}
	
	
	/**
	 * Main loop
	 */
	@Override
	public void run() {
		while (!isInterrupted() ) {
			try {
				updatePlaylist();
				updatePlayingItem();
				sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	/**
	 * Check if we need to refresh the playlist
	 */
	private void updatePlaylist() 
	{	
		AbstractCall<AllItems> playlistQuery = new org.xbmc.android.jsonrpc.api.call.Playlist.
				GetItems(Player.PLAYER_MUSIC, 
						AllItems.DURATION, 
						AllItems.ARTIST,
						AllItems.TITLE);
		
		mGlobalFeatures.getConnection().call(playlistQuery, new ApiCallback<AllItems>() {
			public void onResponse(AbstractCall<AllItems> query) {
				List<AllItems> elements = query.getResults();
				
				// Check if playlist is up to date to prevent changing list adapter
				// we can't use AllItems.equal: it's not implemented
				if (mPlaylistContent != null && mPlaylistContent.size() == elements.size()) {
					boolean upToDate = true;
					for (int pos = 0; pos < elements.size(); pos++) {
						if (mPlaylistContent.get(pos).id.equals(elements.get(pos).id) == false) {
							upToDate = false;
							break;
						}
					}
					if (upToDate) return;
				}

				mPlaylistContent = elements;
				
				Iterator<IPlayerNotification> iter = mNotifications.iterator();
				while (iter.hasNext()) {
					iter.next().onPlaylistChanged(elements, mPlayingSongId);
				}
			}
			
			public void onError(int code, String message, String hint) 
			{
				notifyError(code, message, hint);
			}
		});
	}
	
	
	/**
	 * Check if we need to refresh the playing item
	 */
	private void updatePlayingItem()
	{
		mGlobalFeatures.getConnection().call(new org.xbmc.android.jsonrpc.api.call.Player.GetActivePlayers(), 
				new ApiCallback<GetActivePlayersResult>() {

			@Override
			public void onResponse(AbstractCall<GetActivePlayersResult> query) {
				boolean audioPlayerPlaying = false;
				List<GetActivePlayersResult> elements = query.getResults();
				for (int i = 0; i < elements.size(); i++) {
					if (elements.get(i).playerid == Player.PLAYER_MUSIC) {
						audioPlayerPlaying = true;
						break;
					}
				}
				
				if (audioPlayerPlaying) {
					AbstractCall<AllItems> playingQuery = new org.xbmc.android.jsonrpc.api.call.Player.GetItem(Player.PLAYER_MUSIC);
					mGlobalFeatures.getConnection().call(playingQuery, new ApiCallback<AllItems>() {
						public void onResponse(AbstractCall<AllItems> query) {
							setPlayingSongId(query.getResult().id);
						}
						public void onError(int code, String message, String hint) {
							setPlayingSongId(Player.NO_ITEM_PLAYING);
							notifyError(code, message, hint);
						}
					});
				} else {
					setPlayingSongId(Player.NO_ITEM_PLAYING);
				}
								
			}

			@Override
			public void onError(int code, String message, String hint) {
				setPlayingSongId(Player.NO_ITEM_PLAYING);
				notifyError(code, message, hint);
			}
		});
	}
	
	
	/**
	 * Update list on playing ID changed
	 */
	private void setPlayingSongId(int playingSongId) {
		if (mPlayingSongId != playingSongId) {
			mPlayingSongId = playingSongId;
			Iterator<IPlayerNotification> iter = mNotifications.iterator();
			while (iter.hasNext()) {
				iter.next().onSongPlayingChanged(playingSongId);
			}
		}
	}
	
	/**
	 * Notify an error
	 */
	private void notifyError(int code, String message, String hint) {		
		Iterator<IPlayerNotification> iter = mNotifications.iterator();
		while (iter.hasNext()) {
			iter.next().onError(code, message, hint);
		}
		
		// We get there in 2 cases:
		// - We can't connect to XBMC
		// - There is an API error (this thread is bad wrotten)
		// This thread consume resources and it need to be restarted to reconfigure XBMC host.
		// So, we abort.
		interrupt();
	}
}
