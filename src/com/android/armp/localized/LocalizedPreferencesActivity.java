package com.android.armp.localized;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.armp.LocalizedMusicActivity;
import com.android.armp.R;
import com.android.armp.facebook.BaseRequestListener;
import com.android.armp.facebook.SessionEvents;
import com.android.armp.facebook.SessionEvents.AuthListener;
import com.android.armp.facebook.SessionEvents.LogoutListener;
import com.android.armp.facebook.SessionStore;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Util;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class LocalizedPreferencesActivity extends PreferenceActivity {
	private final static String TAG = "LocalizedPrefrences";
	private SharedPreferences mPreferences;
	private Facebook mFacebook = null;
	private AsyncFacebookRunner mAsyncRunner = null;
	private String[] mPermissions;
	private Handler mHandler;
	private LocalizedPreferencesActivity mActivity = null;
	private SessionListener mSessionListener = new SessionListener();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mActivity = this;

        addPreferencesFromResource(R.xml.localized_preferences);
        
        mHandler = new Handler();
        mPermissions = new String[] { "publish_stream" };
        
        // Initialize preferences
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        Preference myPref = (Preference)findPreference("fb_login");
        myPref.setOnPreferenceClickListener(mFbLoginPrefListener);
        
        // Create facebook object
        Bundle extras = getIntent().getExtras(); 
    	String value = extras.getString("FB_APP_ID");
    	mFacebook = new Facebook(value);
    	mAsyncRunner = new AsyncFacebookRunner(mFacebook);
    	
    	SessionStore.restore(mFacebook, this);
        SessionEvents.addAuthListener(mSessionListener);
        SessionEvents.addLogoutListener(mSessionListener);
        
        if (mFacebook.isSessionValid()) {
        	myPref.setTitle(R.string.fb_logout);
        	myPref.setSummary("");
        	mAsyncRunner.request("me", new NameRequestListener());
        }
    }
	
	private OnPreferenceClickListener mFbLoginPrefListener = new OnPreferenceClickListener() {
    	public boolean onPreferenceClick(Preference preference) {
    		if (mFacebook.isSessionValid()) {
                SessionEvents.onLogoutBegin();
                
                mAsyncRunner.logout(getApplicationContext(), new LogoutRequestListener());
            } else {
            	mFacebook.authorize(mActivity, mPermissions, new LoginDialogListener());
            }
    		
    		return true;
    	}
    };
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        mFacebook.authorizeCallback(requestCode, resultCode, data);
    }
    
    private final class LoginDialogListener implements DialogListener {
        public void onComplete(Bundle values) {
            SessionEvents.onLoginSuccess();
        }

        public void onFacebookError(FacebookError error) {
            SessionEvents.onLoginError(error.getMessage());
        }
        
        public void onError(DialogError error) {
            SessionEvents.onLoginError(error.getMessage());
        }

        public void onCancel() {
            SessionEvents.onLoginError("Action Canceled");
        }
    }
    
    private class LogoutRequestListener extends BaseRequestListener {
        public void onComplete(String response, final Object state) {
            // callback should be run in the original thread, 
            // not the background thread
            mHandler.post(new Runnable() {
                public void run() {
                    SessionEvents.onLogoutFinish();
                }
            });
        }
    }
    
    public class NameRequestListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
            try {
                // process the response here: executed in background thread
                JSONObject json = Util.parseJson(response);
                final String name = json.getString("name");

                LocalizedPreferencesActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                    	Preference myPref = (Preference)findPreference("fb_login");
                    	myPref.setSummary(R.string.fb_logout_desc);
                    	String desc = mActivity.getResources().getString(R.string.fb_logout_desc);
                    	desc += " " + name;
                    	myPref.setSummary(desc);
                    }
                });
            } catch (JSONException e) {
                Log.w(TAG, "JSON Error in response");
            } catch (FacebookError e) {
                Log.w(TAG, "Facebook Error: " + e.getMessage());
            }
        }
    }

    private class SessionListener implements AuthListener, LogoutListener {
    	ProgressDialog mProgressLogout;
    	
        public void onAuthSucceed() {            
            SessionStore.save(mFacebook, getApplicationContext());
            Preference myPref = (Preference)findPreference("fb_login");
            myPref.setTitle(R.string.fb_logout);
            mAsyncRunner.request("me", new NameRequestListener());
        }

        public void onAuthFail(String error) {
        }
        
        public void onLogoutBegin() {
        	mProgressLogout = ProgressDialog.show(mActivity, "",
    				"Logging out from facebook", true, false);
        }
        
        public void onLogoutFinish() {
        	mProgressLogout.dismiss();
            SessionStore.clear(getApplicationContext());
            Preference myPref = (Preference)findPreference("fb_login");
            myPref.setTitle(R.string.fb_login);
        	myPref.setSummary(R.string.fb_login_desc);
        }
    }
}
