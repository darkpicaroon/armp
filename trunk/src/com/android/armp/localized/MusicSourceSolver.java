package com.android.armp.localized;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.provider.MediaStore;
import android.util.Log;

import com.android.armp.model.Music;

public class MusicSourceSolver {
	private static final String TAG = "MusicSourceSolver";
	private static ContentResolver mContentResolver;

	private static MusicSourceSolver mInstance = null;

	// thread pool to manage iTunes queries.
	private static ExecutorService pool = Executors.newFixedThreadPool(10);

	/** These are the columns in the music cursor that we are interested in. */
	static final String[] CURSOR_COLS = new String[] {
			MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
			MediaStore.Audio.Media.TITLE_KEY, MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ALBUM_ID,
			MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ARTIST_ID,
			MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.TRACK };

	private MusicSourceSolver(Context context) {
		mContentResolver = context.getContentResolver();
	}

	public static MusicSourceSolver getInstance(Context context) {
		if (mInstance == null)
			mInstance = new MusicSourceSolver(context);
		return mInstance;
	}

	public static void solveMusicSource(Music m) {
		boolean found = false;
		String artist = m.getArtist();
		String title = m.getTitle();
		String album = m.getAlbum();

		if (mContentResolver != null) {
			// Log.d(TAG, "Solving music source: "+title+" - "+artist+" - "+album);
	
			// The content provider uri to look into
			Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	
			StringBuilder where = new StringBuilder();
			where.append(MediaStore.Audio.Media.TITLE + " != ''");
	
			// Add in the filtering constraints
			String[] searchWords = { title, artist, album };
			String[] keywords = new String[searchWords.length];
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
				where.append(MediaStore.Audio.Media.TITLE_KEY
						+ " LIKE ? ESCAPE '\\'");
			}
	
			// Perform the query
			Cursor cursor = mContentResolver.query(uri, CURSOR_COLS,
					where.toString(), keywords, MediaStore.Audio.Media.TITLE);
	
			if (cursor != null) {
				if (cursor.getCount() == 1) {
					cursor.moveToFirst();
					int colIdx = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
					long newId = cursor.getLong(colIdx);
					m.setSource(ContentUris.withAppendedId(uri, newId).toString());
					colIdx = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
					m.setArtistId(cursor.getLong(colIdx));
					colIdx = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
					m.setAlbumId(cursor.getLong(colIdx));
					m.setIsPlayable(true);
					found = true;
				}
				Log.v(TAG, "Nb results for " + artist + ": " + cursor.getCount());
				cursor.close();
			}
		}
		
		if (!found) {
			Log.v(TAG, "Track not found on local device, trying iTunes");
			try {
				pool.execute(new HttpPreviewRequest(artist, title, m));
			} catch (Exception e) {
				Log.d(TAG, "Exception while launching new thread in pool.");
			}
		}
	}

	/**
	 * HTTP ITUNES REQUEST FUNCTIONS
	 */
	private static class HttpPreviewRequest implements Runnable {
		private String artist;
		private String title;
		private Music m;

		public HttpPreviewRequest(String artist, String title, Music m) {
			this.artist = artist;
			this.title = title;
			this.m = m;
		}

		public void run() {
			AndroidHttpClient httpclient = null;

			String previewUrl = "null";
			String term = this.artist + "+" + this.title;
			String iTunesUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/wa/wsSearch?entity=musicTrack&media=music&limit=3&term=";

			try {
				Log.v(TAG, "params before sanitization : " + artist + title);
				term = term.replace(' ', '+');
				term = term.replace('-', '+');
				term = term.replace('\'', '+');
				Log.v(TAG, "params after sanitization : " + term);

				httpclient = AndroidHttpClient.newInstance("");
				HttpGet httpget = new HttpGet(iTunesUrl + term);
				HttpResponse res = httpclient.execute(httpget);

				HttpEntity httpentity = res.getEntity();
				if (httpentity != null) {
					InputStream instream = httpentity.getContent();
					String result = convertStreamToString(instream);
					JSONObject json = new JSONObject(result);

					int countresult = json.getInt("resultCount");
					if (countresult == 0) {
						Log.v(TAG, "no result found on iTunes for: " + artist
								+ " " + title);
						m.setIsPlayable(false);
					} else {
						Log.v(TAG, "there is/are " + countresult
								+ " on iTunes for " + artist + " " + title);
						JSONArray results = new JSONArray();
						results = json.getJSONArray("results");

						for (int i = 0; i < results.length(); i++) {
							JSONObject j = results.getJSONObject(i);
							previewUrl = (j.getString("previewUrl").contains(
									"mzstatic") && previewUrl == "null") ? previewUrl = j
									.getString("previewUrl") : previewUrl;
						}

						if (previewUrl != "null") {
							Log.v(TAG, " Found a preview on iTunes at: "
									+ previewUrl);
							m.setSource(previewUrl);
							m.setIsPlayable(true);
						} else {
							Log.v(TAG, " No preview found on iTunes!");
							m.setIsPlayable(false);
						}
					}
					instream.close();
				}
			} catch (Exception e) {
				Log.d(TAG, e.toString());
			} finally {
				if (httpclient != null)
					httpclient.close();
			}
		}

		private static String convertStreamToString(InputStream is) {
			/*
			 * To convert the InputStream to String we use the
			 * BufferedReader.readLine() method. We iterate until the
			 * BufferedReader return null which means there's no more data to
			 * read. Each line will appended to a StringBuilder and returned as
			 * String.
			 */
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append("\n");
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

}
