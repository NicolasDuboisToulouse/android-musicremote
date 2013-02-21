package com.snoopy.android.musicremote.listAdapters;

import java.util.List;

import org.xbmc.android.jsonrpc.api.model.AudioModel.AlbumDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snoopy.android.musicremote.R;

public class ListAdapterAlbums extends ArrayAdapter<AlbumDetail> {

	private class ViewItemContent {
		@SuppressWarnings("unused")
		ImageView image;
		TextView title;
		TextView artist;
		TextView year;
	}
	
	private final LayoutInflater inflater;
	
	public ListAdapterAlbums(Context context, List<AlbumDetail> albums) {
		super(context, 0, albums);
		inflater = LayoutInflater.from(context);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewItemContent content;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_album, null);
			content = new ViewItemContent();
			content.image = (ImageView) convertView.findViewById(R.id.view_album_item_image);
			content.title = (TextView) convertView.findViewById(R.id.view_album_item_name);
			content.artist = (TextView) convertView.findViewById(R.id.view_album_item_artist);
			content.year = (TextView) convertView.findViewById(R.id.view_album_item_year);
			convertView.setTag(content);
		} else {
			content = (ViewItemContent) convertView.getTag();
		}
		
		final AlbumDetail album = getItem(position);
		content.title.setText(album.label);
		
		String artists = null;
		for (int i = 0; i < album.artist.size(); i++) {
			if (artists == null) artists = album.artist.get(i);
			else artists += ", " + album.artist.get(i);
		}		
		
		content.artist.setText(artists);
		if (album.year != 0) {
			content.year.setText(String.valueOf(album.year));
		} else {
			content.year.setText(R.string.year_default);
		}

		return convertView;
	}
}
