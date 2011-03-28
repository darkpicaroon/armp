package com.android.armp.localized;

import java.text.Collator;

import com.android.armp.model.Music;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

public class MusicSourceSolver{
	private static final String TAG = "MusicSourceSolver";
	private static ContentResolver mContentResolver;
	
	private static MusicSourceSolver mInstance = null;
	
	/** These are the columns in the music cursor that we are interested in. */
    static final String[] CURSOR_COLS = new String[] {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.TITLE_KEY,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TRACK
    };
	
	private MusicSourceSolver(Context context) {
		mContentResolver = context.getContentResolver();
	}
	
	public static MusicSourceSolver getInstance(Context context) {
		if(mInstance == null)
			mInstance = new MusicSourceSolver(context);
		return mInstance;
	}

	public static void solveMusicSource(Music m) {
		String artist = m.getArtist();
		String title = m.getTitle();
		String album = m.getAlbum();
		
		Log.d(TAG, "Solving music source: "+title+" - "+artist+" - "+album);
		
		// The content provider uri to look into
		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		
		StringBuilder where = new StringBuilder();
        where.append(MediaStore.Audio.Media.TITLE + " != ''");
        
        // Add in the filtering constraints
        String [] searchWords = {title, artist, album};
        String [] keywords = new String[searchWords.length];
        Collator col = Collator.getInstance();
        col.setStrength(Collator.PRIMARY);
        
        for (int i = 0; i < searchWords.length; i++) {
            String key = MediaStore.Audio.keyFor(searchWords[i]);
            key = key.replace("\\", "\\\\");
            key = key.replace("%", "\\%");
            key = key.replace("_", "\\_");
            keywords[i] = '%' + key + '%';
        }
        for (int i = 0; i < searchWords.length; i++) {
            where.append(" AND ");
            where.append(MediaStore.Audio.Media.ARTIST_KEY + "||");
            where.append(MediaStore.Audio.Media.ALBUM_KEY + "||");
            where.append(MediaStore.Audio.Media.TITLE_KEY + " LIKE ? ESCAPE '\\'");
        }

        // Perform the query
		Cursor cursor = mContentResolver.query(
		        uri,
		        CURSOR_COLS,
		        where.toString(),
		        keywords,
		        MediaStore.Audio.Media.TITLE
		);		
		
		if(cursor.getCount() == 1) {
			cursor.moveToFirst();
			int colIdx = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
			long newId = cursor.getLong(colIdx);
	        m.setSource(ContentUris.withAppendedId(uri, newId).toString());
	        colIdx = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
	        m.setArtistId(cursor.getLong(colIdx));
	        colIdx = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
	        m.setAlbumId(cursor.getLong(colIdx));
	        m.setIsPlayable(true);
		}
		else {
			// get Music preview from iTunes
			if (cursor.getCount() == 0) {
				// iTunes sample request
				// http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/wa/wsSearch?term=jack+johnson

				String iTunesUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/wa/wsSearch?entity=musicTrack&media=music&limit=3&term=";

				Log.d(TAG, "Url for iTunes = " + iTunesUrl);

				String term = artist+"+"+title;
				String previewUrl = "null";
				AndroidHttpClient httpclient = null;
				try {
					Log.i(TAG, "params before sanitization : " + artist + title);
					term = term.replace(' ', '+');
					term = term.replace('-', '+');
					Log.i(TAG, "params after sanitization : " + term);
					
					httpclient = AndroidHttpClient.newInstance("");
					HttpGet httpget = new HttpGet(iTunesUrl + term);
					HttpResponse res = httpclient.execute(httpget);

					HttpEntity httpentity = res.getEntity();
					if (httpentity != null) {
						// A Simple JSON Response Read
						InputStream instream = httpentity.getContent();
						String result = convertStreamToString(instream);
						JSONObject json = new JSONObject(result);

						// A Simple JSONObject Creation
						int countresult = json.getInt("resultCount");
						if (countresult == 0) {
							Log.d(TAG, "no result found on iTunes for " + artist);
						} else {
							Log.d(TAG, "there is " + countresult
									+ " on iTunes for " + artist);
							JSONArray results = new JSONArray();
							results = json.getJSONArray("results");
							int n = results.length();
							for (int i = 0; i < n; i++) {
								JSONObject j = results.getJSONObject(i);
								previewUrl = (j.getString("previewUrl").contains(
										"mzstatic") && previewUrl == "null") ? previewUrl = j
										.getString("previewUrl") : previewUrl;
							}
							if (previewUrl != "null") {
								Log.d(TAG, " WANTING TO PLAY : " + previewUrl);
								m.setSource(previewUrl);
								m.setIsPlayable(true);
							}
							else{
								Log.d(TAG, " No preview found : ");
								m.setIsPlayable(false);
							}
						}

						// Closing the input stream will trigger connection release
						instream.close();
					}

				} catch (Exception e) {
					Log.d(TAG, e.toString());
				} finally {
					httpclient.close();
				}
			}
			m.setIsPlayable(false);
		}
		
		Log.d(TAG, "Nb results for "+artist+": "+cursor.getCount());
		cursor.close();
	}
	
	
	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
