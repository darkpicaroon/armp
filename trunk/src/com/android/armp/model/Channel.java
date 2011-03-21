package com.android.armp.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Channel implements Serializable {
	private int mId;
	private int mSpotId;
	private String mName;
	private String mUser;
	private String mGenre;
	private int mCountOfMusics;
	private int mCreationTime;
	private int mLastUpdate;
	private List<Music> mMusics;

	public Channel(int id) {
		this.mId = id;
		this.mMusics = new ArrayList<Music>();
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeBytes(mId + "," + mSpotId + "," + mName + "," + mUser + ","
				+ mGenre + "," + mCountOfMusics + "," + mCreationTime + ","
				+ mLastUpdate);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		String obj = in.readLine();
		String[] fields = obj.split(",");

		mId = new Integer(fields[0]);
		mSpotId = new Integer(fields[1]);
		mName = fields[2];
		mUser = fields[3];
		mGenre = fields[4];
		mCountOfMusics = new Integer(fields[5]);
		mCreationTime = new Integer(fields[6]);
		mLastUpdate = new Integer(fields[7]);
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

	public void setName(String name) {
		this.mName = name;
	}

	public String getName() {
		return mName;
	}

	public String getUser() {
		return mUser;
	}

	public void setUser(String user) {
		this.mUser = user;
	}

	public String getGenre() {
		return this.mGenre;
	}

	public void setGenre(String genre) {
		this.mGenre = genre;
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
		return mLastUpdate;
	}

	public void setMusics(List<Music> mMusics) {
		this.mMusics = mMusics;
	}

	public List<Music> getMusics() {
		return mMusics;
	}

	public void setCountOfMusics(int countOfMusics) {
		this.mCountOfMusics = countOfMusics;
	}

	public int getCountOfMusics() {
		return mCountOfMusics;
	}

	@Override
	public String toString() {
		return "Name:" + mName + " - CreationTime:" + mCreationTime
				+ " - LastUpdate:" + mLastUpdate + " - NbMusics:"
				+ mCountOfMusics;
	}
}
