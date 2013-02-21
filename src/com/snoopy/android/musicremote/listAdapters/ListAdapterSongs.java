package com.snoopy.android.musicremote.listAdapters;

import java.util.List;

import org.xbmc.android.jsonrpc.api.model.AudioModel.SongDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snoopy.android.musicremote.R;

public class ListAdapterSongs extends ArrayAdapter<SongDetail> {

	private class ViewItemContent {
		TextView title;
		TextView artist;
		TextView duration;
	}
	
	private final LayoutInflater inflater;

	public ListAdapterSongs(Context context, List<SongDetail> songs) {
		super(context, 0, songs);
		inflater = LayoutInflater.from(context);
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewItemContent content;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_song, null);
			content = new ViewItemContent();
			content.title = (TextView) convertView.findViewById(R.id.view_song_item_name);
			content.artist = (TextView) convertView.findViewById(R.id.view_song_item_artist);
			content.duration = (TextView) convertView.findViewById(R.id.view_song_item_duration);
			convertView.setTag(content);
		} else {
			content = (ViewItemContent) convertView.getTag();
		}
		
		final SongDetail song = getItem(position);
		
		content.title.setText(song.label);
		
		String artists = null;
		for (int i = 0; i < song.artist.size(); i++) {
			if (artists == null) artists = song.artist.get(i);
			else artists += ", " + song.artist.get(i);
		}		
		content.artist.setText(artists);
		
		int minuts = song.duration / 60;
		int seconds = song.duration % 60;
		content.duration.setText(String.format("%d:%02d", minuts, seconds));

		return convertView;
	}

}
