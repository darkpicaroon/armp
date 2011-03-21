package com.android.armp.model;

public class Music {
	private int mId;
	private int mMetadataId;
	private String mSource;
	private String mArtist;
	private String mTitle;
	private String mAlbum;
	private String mGenre;
	private int mYear;
	private int mDuration;
	private int mTrackNumber;

	public Music(int id) {
		this.mId = id;
	}

	public Music(int id, String artist, String album) {
		this.mArtist = artist;
		this.mAlbum = album;
		this.mId = id;
	}

	public int getId() {
		return this.mId;
	}

	public void setMetadataId(int metadataId) {
		this.mMetadataId = metadataId;
	}

	public int getMetataId() {
		return this.mMetadataId;
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

	public void setGenre(String genre) {
		this.mGenre = genre;
	}

	public String getGenre() {
		return mGenre;
	}

	public void setDuration(int mDuration) {
		this.mDuration = mDuration;
	}

	public int getDuration() {
		return mDuration;
	}

	public void setTrackNumber(int trackNbr) {
		this.mTrackNumber = trackNbr;
	}

	public int getTrackNumber() {
		return this.mTrackNumber;
	}
}