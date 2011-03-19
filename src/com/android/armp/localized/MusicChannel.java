package com.android.armp.localized;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MusicChannel implements Serializable {
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
		this.mMusics = new ArrayList<MusicItem>();
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String cTime = curFormater.format(mCreationTime), 
			   uTime = curFormater.format(mLastUpdate);
		
		out.writeBytes(mId+","+mSpotId+","+mName+","+mGenreId+","+
						mGenre+","+mNbMusic+","+cTime+","+uTime);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String obj = in.readLine();
		String[] fields = obj.split(",");
		
		mId = new Integer(fields[0]);
		mSpotId = new Integer(fields[1]);
		mName = fields[2];
		mGenreId = new Integer(fields[3]);
		mGenre = fields[4];
		mNbMusic = new Integer(fields[5]);
		
		try {
			setCreationTime(curFormater.parse(fields[6]));
			setLastUpdate(curFormater.parse(fields[7]));
		} catch (Exception e) {

		}
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
