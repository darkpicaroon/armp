package com.android.armp.localized;

import java.text.Collator;

import com.android.armp.model.Music;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

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
		}
		
		Log.d(TAG, "Nb results for "+artist+": "+cursor.getCount());
		cursor.close();
	}
}
