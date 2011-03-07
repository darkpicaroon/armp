package com.android.armp;

import java.util.Date;
import java.util.HashMap;

public class LocalizedMusicSpot extends Object {
	private int mId;
	private double mLatitude;
	private double mLongitude;
	private float mRay;
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

		public void setName(String mName) {
			this.mName = mName;
		}
		
		@Override
		public String toString() {
			return "Name:"+mName+" - GenreId:"+mGenreId+" - CreationTime:"
			+mCreationTime.toString()+" - LastUpdate:"+mLastUpdate.toString()+
			" - NbMusics:"+mNbMusic;
		}

		public String getName() {
			return mName;
		}

		public void setGenreId(int mGenreId) {
			this.mGenreId = mGenreId;
		}

		public int getGenreId() {
			return mGenreId;
		}

		public void setCreationTime(Date mCreationTime) {
			this.mCreationTime = (Date) mCreationTime.clone();
		}

		public Date getCreationTime() {
			return mCreationTime;
		}

		public void setLastUpdate(Date mLastUpdate) {
			this.mLastUpdate = (Date) mLastUpdate.clone();
		}

		public Date getLastUpdate() {
			return mLastUpdate;
		}

		public void setMusics(HashMap<Integer, Music> mMusics) {
			this.mMusics = mMusics;
		}

		public HashMap<Integer, Music> getMusics() {
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
		
		public void setSource(String mSource) {
			this.mSource = mSource;
		}

		public String getSource() {
			return mSource;
		}

		public void setArtist(String mArtist) {
			this.mArtist = mArtist;
		}

		public String getArtist() {
			return mArtist;
		}

		public void setTitle(String mTitle) {
			this.mTitle = mTitle;
		}

		public String getTitle() {
			return mTitle;
		}

		public void setAlbum(String mAlbum) {
			this.mAlbum = mAlbum;
		}

		public String getAlbum() {
			return mAlbum;
		}

		public void setYear(int mYear) {
			this.mYear = mYear;
		}

		public int getYear() {
			return mYear;
		}

		public void setGenreId(int mGenreId) {
			this.mGenreId = mGenreId;
		}

		public int getGenreId() {
			return mGenreId;
		}

		public void setDuration(int mDuration) {
			this.mDuration = mDuration;
		}

		public int getDuration() {
			return mDuration;
		}		
	}
	
	public LocalizedMusicSpot() {
		setChannels(new HashMap<Integer, MusicChannel>());
		mInstance = this;
	}
	
	public LocalizedMusicSpot(int id, double latitude, double longitude, float ray, Date creationTime) {
		this.mId = id;
		setLatitude(latitude);
		setLongitude(longitude);
		setRay(ray);
		setCreationTime(creationTime);
		setChannels(new HashMap<Integer, MusicChannel>());
		
		mInstance = this;
	}
	
	public int getId() {
		return this.mId;
	}

	public void setLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public void setLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}

	public double getLongitude() {
		return mLongitude;
	}
	
	/**
	 * Ray of the spot
	 * @return Ray in meters
	 */
	public float getRay() {
		return mRay;
	}

	public void setRay(float mRay) {
		this.mRay = mRay;
	}

	public void setCreationTime(Date mCreationTime) {
		this.mCreationTime = mCreationTime;
	}

	public Date getCreationTime() {
		return mCreationTime;
	}

	public void setChannels(HashMap<Integer, MusicChannel> mChannels) {
		this.mChannels = mChannels;
	}

	public HashMap<Integer, MusicChannel> getChannels() {
		return mChannels;
	}
}
