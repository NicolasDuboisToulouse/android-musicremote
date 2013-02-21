package com.snoopy.android.musicremote.Player;

import java.util.List;

import org.xbmc.android.jsonrpc.api.AbstractCall;
import org.xbmc.android.jsonrpc.api.model.AudioModel.SongDetail;
import org.xbmc.android.jsonrpc.api.model.ListModel.AllItems;
import org.xbmc.android.jsonrpc.api.model.PlaylistModel;
import org.xbmc.android.jsonrpc.io.ApiCallback;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.snoopy.android.musicremote.R;
import com.snoopy.android.musicremote.RemoteAbsFragment;
import com.snoopy.android.musicremote.Player.PlayerPollingThread.IPlayerNotification;
import com.snoopy.android.musicremote.listAdapters.ListAdapterPlaylistItem;
import com.snoopy.android.musicremote.widgets.RemoteListLayout;

/**
 * Playlist fragment
 */
public class Playlist extends RemoteAbsFragment implements IPlayerNotification
{
	/**
	 * Internal list class
	 */
	protected RemoteListLayout mRemoteList = null;

	/**
	 * list adapter
	 */
	private ListAdapterPlaylistItem mListAdapter = null;

	/**
	 * Queue a song. Will be played by callback if playlist empty.
	 */
	public void queue(SongDetail song) {
		getConnection().call(new org.xbmc.android.jsonrpc.api.call.Playlist.Add(
				Player.PLAYER_MUSIC, 
				new PlaylistModel.Item(new PlaylistModel.Item.Songid(song.songid))),
				new ApiCallback<String>() {
								@Override
								public void onResponse(AbstractCall<String> call) {}
								@Override
								public void onError(int code, String message, String hint) {}
								});
	}
	
	
	/**
	 * Build the view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		if (mRemoteList == null) {
			mRemoteList = (RemoteListLayout)inflater.inflate(R.layout.remote_list, container, false);
			mRemoteList.setHeader("Playlist");
			mRemoteList.getListView().setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (mListAdapter != null) {
						getPlayer().play(position);
					}
				}
			});
			
			mRemoteList.setOnButtonMenuListener(new OnClickListener() {		
				@Override
				public void onClick(View v) {
					onMainMenu();
				}
			});
		}
		return mRemoteList;
	}

	
	@Override
	public void onStart() {
		super.onStart();
		mRemoteList.clearWarning();
		switch(getLayout()) {
		case LAYOUT_BROWSER_ONLY:
			mRemoteList.setMenuButtonVisibility(false);
			break;
		case LAYOUT_BROWSER_PLAYLIST:
			mRemoteList.setMenuButtonVisibility(true);
			break;			
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		//Thread will be destroyed onPause. We need to re-attach each time.
		getPlayer().getPollingThread().addListener(this);
	}
	
	@Override
	public void onSongPlayingChanged(int songId) {
		mListAdapter.setPlayingSongId(songId);
		mListAdapter.notifyDataSetChanged();
	}


	@Override
	public void onPlaylistChanged(List<AllItems> songs, int mPlayingSongId) {
		if (getActivity() == null) {
			// we have been detached after the send. drop result.
			return;
		}
		
		// Display a message if songs was added to playlist
		int oldSize = (mListAdapter != null)? mListAdapter.getCount() : 0;
		if (oldSize < songs.size()) {
			Toast.makeText(getActivity(), "Song(s) added to playlist", Toast.LENGTH_SHORT).show();
		}
		
		// Store scroll position to restore it once adapter changed
		int firstVisiblePos = mRemoteList.getListView().getFirstVisiblePosition();
		View firstView = mRemoteList.getListView().getChildAt(0);
		int scrollOffset = (firstView == null) ? 0 : firstView.getTop();
		
		
		mListAdapter = new ListAdapterPlaylistItem(getActivity(), songs);
		mListAdapter.setPlayingSongId(mPlayingSongId);
		mRemoteList.getListView().setAdapter(mListAdapter);
		mRemoteList.getListView().setSelectionFromTop(firstVisiblePos, scrollOffset);
		
		// Immediately play song if no song playing
		if (songs.size() > 0 && mPlayingSongId == Player.NO_ITEM_PLAYING) {
			getPlayer().play(0);
		}
	}


	@Override
	public void onError(int code, String message, String hint) {
		mRemoteList.setWarning("XBMC connection error!");
		mListAdapter = null;
		mRemoteList.getListView().setAdapter(null);
	}	
}
