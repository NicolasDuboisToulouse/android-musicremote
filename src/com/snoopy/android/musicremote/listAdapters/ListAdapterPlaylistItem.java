package com.snoopy.android.musicremote.listAdapters;

import java.util.List;

import org.xbmc.android.jsonrpc.api.model.ListModel.AllItems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snoopy.android.musicremote.R;
import com.snoopy.android.musicremote.Player.Player;

public class ListAdapterPlaylistItem extends ArrayAdapter<AllItems> {

	private class ViewItemContent {
		TextView  title;
		TextView  artist;
		TextView  duration;
		ImageView image;
	}
	
	private final LayoutInflater inflater;

	private int mPlayingSongId = Player.NO_ITEM_PLAYING;
	
		
	public ListAdapterPlaylistItem(Context context, List<AllItems> item) {
		super(context, 0, item);
		inflater = LayoutInflater.from(context);
	}
	
	public void setPlayingSongId(int id) {
		mPlayingSongId = id;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewItemContent content;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_playlist, null);
			content = new ViewItemContent();
			content.title = (TextView) convertView.findViewById(R.id.view_song_item_name);
			content.artist = (TextView) convertView.findViewById(R.id.view_song_item_artist);
			content.duration = (TextView) convertView.findViewById(R.id.view_song_item_duration);
			content.image = (ImageView) convertView.findViewById(R.id.view_playlist_item_image);
			convertView.setTag(content);
		} else {
			content = (ViewItemContent) convertView.getTag();
		}
		
		final AllItems song = getItem(position);
		
		content.title.setText(song.title);
		
		String artists = null;
		for (int i = 0; i < song.artist.size(); i++) {
			if (artists == null) artists = song.artist.get(i);
			else artists += ", " + song.artist.get(i);
		}
		content.artist.setText(artists);
		
		int minuts = song.duration / 60;
		int seconds = song.duration % 60;
		content.duration.setText(String.format("%d:%02d", minuts, seconds));

		// This don't look to be optimized...
		if (song.id == mPlayingSongId) {
			content.image.setImageDrawable(
					getContext().getResources().getDrawable(R.drawable.playlist_play));
		} else {
			content.image.setImageDrawable(
					getContext().getResources().getDrawable(R.drawable.playlist_blank));			
		}
		
		return convertView;
	}

}
