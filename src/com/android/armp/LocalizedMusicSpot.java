package com.android.armp;

import java.util.Date;
import java.util.HashMap;

public class LocalizedMusicSpot extends Object {
	private int mId;
	private double mLattitude;
	private double mLongitude;
	private Date mCreationTime;
	private HashMap<Integer, MusicChannel> mChannels;
	private static LocalizedMusicSpot mInstance = null;
	
	public static MusicChannel newMusicChannel() {
		return mInstance.new MusicChannel();
	}
	
	public static Music newMusic() {
		return mInstance.new Music();
	}
	
	public class MusicChannel extends Object {
		private String mName;
		private int mGenreId;
		private int mNbMusic;
		private Date mCreationTime;
		private Date mLastUpdate;
		private HashMap<Integer, Music> mMusics;

		public void setmName(String mName) {
			this.mName = mName;
		}
		
		@Override
		public String toString() {
			return "Name:"+mName+" - GenreId:"+mGenreId+" - CreationTime:"
			+mCreationTime.toString()+" - LastUpdate:"+mLastUpdate.toString()+
			" - NbMusics:"+mNbMusic;
		}

		public String getmName() {
			return mName;
		}

		public void setmGenreId(int mGenreId) {
			this.mGenreId = mGenreId;
		}

		public int getmGenreId() {
			return mGenreId;
		}

		public void setmCreationTime(Date mCreationTime) {
			this.mCreationTime = (Date) mCreationTime.clone();
		}

		public Date getmCreationTime() {
			return mCreationTime;
		}

		public void setmLastUpdate(Date mLastUpdate) {
			this.mLastUpdate = (Date) mLastUpdate.clone();
		}

		public Date getmLastUpdate() {
			return mLastUpdate;
		}

		public void setmMusics(HashMap<Integer, Music> mMusics) {
			this.mMusics = mMusics;
		}

		public HashMap<Integer, Music> getmMusics() {
			return mMusics;
		}

		public void setNbMusic(int mNbMusic) {
			this.mNbMusic = mNbMusic;
		}

		public int getNbMusic() {
			return mNbMusic;
		};
	};
	
	public class Music extends Object {
		private String mSource;
		private String mArtist;
		private String mTitle;
		private String mAlbum;
		private int mYear;
		private int mGenreId;
		private int mDuration;
		
		public Music() {
			
		}
		
		public void setmSource(String mSource) {
			this.mSource = mSource;
		}

		public String getmSource() {
			return mSource;
		}

		public void setmArtist(String mArtist) {
			this.mArtist = mArtist;
		}

		public String getmArtist() {
			return mArtist;
		}

		public void setmTitle(String mTitle) {
			this.mTitle = mTitle;
		}

		public String getmTitle() {
			return mTitle;
		}

		public void setmAlbum(String mAlbum) {
			this.mAlbum = mAlbum;
		}

		public String getmAlbum() {
			return mAlbum;
		}

		public void setmYear(int mYear) {
			this.mYear = mYear;
		}

		public int getmYear() {
			return mYear;
		}

		public void setmGenreId(int mGenreId) {
			this.mGenreId = mGenreId;
		}

		public int getmGenreId() {
			return mGenreId;
		}

		public void setmDuration(int mDuration) {
			this.mDuration = mDuration;
		}

		public int getmDuration() {
			return mDuration;
		}		
	}
	
	public LocalizedMusicSpot() {
		setmChannels(new HashMap<Integer, MusicChannel>());
		mInstance = this;
	}
	
	public LocalizedMusicSpot(double lon, double lat, Date crea) {
		setmLattitude(lat);
		setmLongitude(lon);
		setmCreationTime(crea);
		setmChannels(new HashMap<Integer, MusicChannel>());
		
		mInstance = this;
	}

	public void setmLattitude(double mLattitude) {
		this.mLattitude = mLattitude;
	}

	public double getmLattitude() {
		return mLattitude;
	}

	public void setmLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}

	public double getmLongitude() {
		return mLongitude;
	}

	public void setmCreationTime(Date mCreationTime) {
		this.mCreationTime = mCreationTime;
	}

	public Date getmCreationTime() {
		return mCreationTime;
	}

	public void setmChannels(HashMap<Integer, MusicChannel> mChannels) {
		this.mChannels = mChannels;
	}

	public HashMap<Integer, MusicChannel> getmChannels() {
		return mChannels;
	}
}
