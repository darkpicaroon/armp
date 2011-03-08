package com.android.armp.localized;

import java.util.Date;
import java.util.List;

public class MusicChannel {
	private int mId;
	private int mSpotId;
	private String mName;
	private int mGenreId;
	private String mGenre;
	private int mNbMusic;
	private Date mCreationTime;
	private Date mLastUpdate;
	private List<MusicItem> mMusics;

	public MusicChannel(int id) {
		this.mId = id;
	}
	
	public int getId() {
		return this.mId;
	}
	
	public int getSpotId() {
		return this.mSpotId;
	}
	
	public void setSpotId(int spotId) {
		this.mSpotId = spotId;
	}

	public void setName(String mName) {
		this.mName = mName;
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
	
	public String getGenre() {
		return this.mGenre;
	}
	
	public void setGenre(String genre) {
		this.mGenre = genre;
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

	public void setMusics(List<MusicItem> mMusics) {
		this.mMusics = mMusics;
	}

	public List<MusicItem> getMusics() {
		return mMusics;
	}

	public void setNbMusic(int mNbMusic) {
		this.mNbMusic = mNbMusic;
	}

	public int getNbMusic() {
		return mNbMusic;
	}

	@Override
	public String toString() {
		return "Name:" + mName + " - GenreId:" + mGenreId + " - CreationTime:"
				+ mCreationTime.toString() + " - LastUpdate:"
				+ mLastUpdate.toString() + " - NbMusics:" + mNbMusic;
	}
}
