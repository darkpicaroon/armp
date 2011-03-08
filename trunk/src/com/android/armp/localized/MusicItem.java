package com.android.armp.localized;

public class MusicItem {
	private int mId;
	private String mSource;
	private String mArtist;
	private String mTitle;
	private String mAlbum;
	private int mYear;
	private int mGenreId;
	private int mDuration;

	public MusicItem(int id) {
		this.mId = id;
	}
	
	public int getId() {
		return this.mId;
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
