package com.android.armp.localized;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.armp.LocalizedMusicActivity;
import com.android.armp.MusicUtils;
import com.android.armp.R;

public class MusicChannelView extends ListActivity {

	private MusicSpot mSpot;
	private ArrayList<MusicChannel> mChannels = null;
	private MusicChannelAdapter mAdapter;
	
	private static MusicSpot mOldSpot = null;
	private static ArrayList<MusicChannel> mOldChannels = null;
	
	public MusicChannelView() {
		
	}
	/*public MusicChannelView(MusicSpot spot, List<MusicChannel> channels) {
		this.mSpot = spot;
		this.mChannels = channels;
	}*/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Bundle b = this.getIntent().getExtras();
		mSpot = (MusicSpot) b.get(LocalizedMusicActivity.spotTag);
		if(mSpot == null) {
			mSpot = mOldSpot;
		} else {
			mOldSpot = mSpot;
		}
		mChannels = (ArrayList<MusicChannel>)b.get(LocalizedMusicActivity.channelsTag);
		if(mChannels == null) {
			mChannels = mOldChannels;
		} else {
			mOldChannels = mChannels;
		}
		
		setContentView(R.layout.localized_music_channel_list);
		this.mAdapter = new MusicChannelAdapter(MusicChannelView.this,
				R.layout.localized_music_channel_item, mChannels);
		setListAdapter(mAdapter);
		
		MusicUtils.updateButtonBar(this, R.id.maptab);
	}

	/*private class MusicChannelAdapter extends ArrayAdapter<MusicChannel> {
		private List<MusicChannel> mItems;
		private Context context;

		public MusicChannelAdapter(Context context, int textViewResourceId,
				List<MusicChannel> mChannels) {
			super(context, textViewResourceId);
			this.mItems = mChannels;
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			Log.d("Coucou", "Here I am");
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = inflater.inflate(R.layout.localized_music_channel_item, null);
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
					line2.setText("Genre: " + c.getGenre());
				}
			}
			return v;
		}
	} */
	
	private class MusicChannelAdapter extends ArrayAdapter<MusicChannel> {

        private ArrayList<MusicChannel> items;

        public MusicChannelAdapter(Context context, int textViewResourceId, ArrayList<MusicChannel> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.localized_music_channel_item, null);
                }
                MusicChannel mc = items.get(position);
                if (mc != null) {
                        TextView tt = (TextView) v.findViewById(R.id.channel_item_line1);
                        TextView bt = (TextView) v.findViewById(R.id.channel_item_line2);
                        if (tt != null) {
                              tt.setText("Name: "+mc.getName());                            
                        }
                        if(bt != null){
                              bt.setText("Genre: "+ mc.getGenre());
                        }
                }
                return v;
        }
}
}
