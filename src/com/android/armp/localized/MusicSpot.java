package com.android.armp.localized;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MusicSpot {
	private int mId;
	private double mLatitude;
	private double mLongitude;
	private float mRadius;
	private Date mCreationTime;
	private List<MusicChannel> mChannels;

	public MusicSpot(int id) {
		this.mId = id;
		setChannels(new ArrayList<MusicChannel>());
	}

	public MusicSpot(int id, double latitude, double longitude, float radius, Date creationTime) {
		this.mId = id;
		setLatitude(latitude);
		setLongitude(longitude);
		setRadius(radius);
		setCreationTime(creationTime);
		setChannels(new ArrayList<MusicChannel>());
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
	 * 
	 * @return Ray in meters
	 */
	public float getRadius() {
		return mRadius;
	}

	public void setRadius(float mRay) {
		this.mRadius = mRay;
	}

	public void setCreationTime(Date mCreationTime) {
		this.mCreationTime = mCreationTime;
	}

	public Date getCreationTime() {
		return mCreationTime;
	}

	public void setChannels(List<MusicChannel> mChannels) {
		this.mChannels = mChannels;
	}

	public List<MusicChannel> getChannels() {
		return mChannels;
	}
}
