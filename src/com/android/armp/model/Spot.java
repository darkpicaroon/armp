package com.android.armp.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Spot implements Serializable {
	private int mId;
	private String mUser;
	private String mName;
	private double mLatitude;
	private double mLongitude;
	private int mRadius;
	private int mColor;
	private int mCreationTime;
	private int mLastUpdate;
	private ArrayList<Channel> mChannels;

	public Spot(int id) {
		this.mId = id;
		setChannels(new ArrayList<Channel>());
	}

	public Spot(int id, String user, String name, double latitude,
			double longitude, int radius, int color, int creationTime,
			int lastUpdate) {
		this.mId = id;
		setUser(user);
		setName(name);
		setLatitude(latitude);
		setLongitude(longitude);
		setRadius(radius);
		setColor(color);
		setCreationTime(creationTime);
		setLastUpdate(lastUpdate);
		setChannels(new ArrayList<Channel>());
	}

	@Override
	public String toString() {
		return new String(mId + "," + mUser + "," + mName + "," + mLatitude
				+ "," + mLongitude + "," + mRadius + "," + mColor + ","
				+ mCreationTime + "," + mLastUpdate);
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeBytes(this.toString());
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		String obj = in.readLine();
		String[] fields = obj.split(",");

		mId = new Integer(fields[0]);
		mUser = fields[1];
		mName = fields[2];
		mLatitude = new Double(fields[3]);
		mLongitude = new Double(fields[4]);
		mRadius = new Integer(fields[5]);
		mColor = new Integer(fields[6]);
		mCreationTime = new Integer(fields[7]);
		mLastUpdate = new Integer(fields[8]);
	}

	public int getId() {
		return this.mId;
	}

	public void setUser(String user) {
		this.mUser = user;
	}

	public String getUser() {
		return this.mUser;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getName() {
		return this.mName;
	}

	public void setLatitude(double latitude) {
		this.mLatitude = latitude;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public void setLongitude(double longitude) {
		this.mLongitude = longitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public float getRadius() {
		return mRadius;
	}

	public void setRadius(int radius) {
		this.mRadius = radius;
	}

	public void setColor(int color) {
		this.mColor = color;
	}

	public int getColor() {
		return this.mColor;
	}

	public void setCreationTime(int creationTime) {
		this.mCreationTime = creationTime;
	}

	public int getCreationTime() {
		return mCreationTime;
	}

	public void setLastUpdate(int lastUpdate) {
		this.mLastUpdate = lastUpdate;
	}

	public int getLastUpdate() {
		return this.mLastUpdate;
	}

	public void setChannels(ArrayList<Channel> mChannels) {
		this.mChannels = mChannels;
	}

	public ArrayList<Channel> getChannels() {
		return mChannels;
	}

	public Channel getChannel(int channelId) {
		if (mChannels != null && mChannels.size() > 0 && channelId > 0) {
			for (Channel mc : mChannels) {
				if (mc.getId() == channelId) {
					return mc;
				}
			}
		}
		return null;
	}
}
