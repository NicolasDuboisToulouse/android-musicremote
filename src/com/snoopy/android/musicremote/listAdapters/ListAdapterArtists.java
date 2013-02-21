package com.snoopy.android.musicremote.listAdapters;

import java.util.List;

import org.xbmc.android.jsonrpc.api.model.AudioModel.ArtistDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snoopy.android.musicremote.R;

public class ListAdapterArtists extends ArrayAdapter<ArtistDetail> {

	private class ViewItemContent {
		@SuppressWarnings("unused")
		ImageView image;
		TextView text;
	}
	
	private final LayoutInflater inflater;

	public ListAdapterArtists(Context context, List<ArtistDetail> artists) {
		super(context, 0, artists);
		inflater = LayoutInflater.from(context);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewItemContent content;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_artist, null);
			content = new ViewItemContent();
			content.image = (ImageView) convertView.findViewById(R.id.view_artist_item_image);
			content.text = (TextView) convertView.findViewById(R.id.view_artist_item_name);
			convertView.setTag(content);
		} else {
			content = (ViewItemContent) convertView.getTag();
		}
		
		content.text.setText(getItem(position).artist);

		return convertView;
	}
}
