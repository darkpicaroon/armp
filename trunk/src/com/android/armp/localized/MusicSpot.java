package com.android.armp.localized;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class MusicSpot implements Serializable {
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
	
	@Override
	public String toString(){
		//SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//String cTime = curFormater.format(mCreationTime);
		
		return new String(mId+","+mLatitude+","+mLongitude+","+mRadius);//+","+cTime);
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {		
		out.writeBytes(this.toString());
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		String obj = in.readLine();
		String[] fields = obj.split(",");
		
		mId = new Integer(fields[0]);
		mLatitude = new Double(fields[1]);
		mLongitude = new Double(fields[2]);
		mRadius = new Float(fields[3]);
		
		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			Date dateObj = curFormater.parse(fields[4]);
			setCreationTime(dateObj);
		} catch (Exception e) {

		}
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
	
	public MusicChannel getChannel(int channelId) {
		if (mChannels != null && mChannels.size() > 0 && channelId > 0) {
			for (MusicChannel mc : mChannels) {
				if (mc.getId() == channelId) {
					return mc;
				}
			}
		}
		return null;
	}
}
