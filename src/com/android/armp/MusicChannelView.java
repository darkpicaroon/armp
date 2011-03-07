package com.android.armp;

import java.util.ArrayList;

import com.android.armp.LocalizedMusicSpot.MusicChannel;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MusicChannelView extends ListActivity {

	private LocalizedMusicSpot mSpot;
	private ArrayList<MusicChannel> mChannels = null;
	private MusicChannelAdapter mAdapter;
    
	public MusicChannelView(LocalizedMusicSpot spot, ArrayList<MusicChannel> channels) {
		this.mSpot = spot;
		this.mChannels = channels;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localized_music_channel_list);
        this.mAdapter = new MusicChannelAdapter(this, R.layout.localized_music_channel_item, mChannels);
        setListAdapter(mAdapter);
    }

    private static class MusicChannelAdapter extends ArrayAdapter<MusicChannel> {
		private ArrayList<MusicChannel> mItems;

		public MusicChannelAdapter(Context context, int textViewResourceId,
				ArrayList<MusicChannel> items) {
			super(context, textViewResourceId);
			this.mItems = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = super.getView(position, convertView, parent);
			}
			MusicChannel c = mItems.get(position);
			if (c != null) {
				TextView line1 = (TextView) v
						.findViewById(R.id.channel_item_line1);
				TextView line2 = (TextView) v
						.findViewById(R.id.channel_item_line2);
				if (line1 != null) {
					line1.setText("Name: " + c.getName());
				}
				if (line2 != null) {
					line2.setText("Genre: " + c.getGenreId());
				}
			}
			return v;
		}
	}
}
