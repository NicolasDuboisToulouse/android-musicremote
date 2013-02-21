package com.snoopy.android.musicremote;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;

import com.snoopy.android.musicremote.Player.Player;
import com.snoopy.android.musicremote.browser.Browser;
import com.snoopy.android.musicremote.business.Connection;
import com.snoopy.android.musicremote.preferences.PreferencesActivity;

public class MainActivity extends Activity implements IGlobalFeatures
{
	/**
	 * Connection to XBMC
	 */
	private Connection mConnection;
	
	/**
	 * Browser fragment
	 */
	private Browser mBrowser;
	
	/**
	 * Player fragment
	 */
	private Player mPlayer;

    /**
     * Preferences observer
     */
    SharedPreferences.OnSharedPreferenceChangeListener mPreferencesListener;

    /**
     * Creation of activity
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);	
				
		// Disconnect on preferences change
		if (mPreferencesListener == null) {
	    	mPreferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
	    		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
	    			getConnection().forceDestroy();
	    		}
	    	};
	    	PreferenceManager.getDefaultSharedPreferences(this).
  				registerOnSharedPreferenceChangeListener(mPreferencesListener);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// Connect to service at startup (pooling thread can't to this)
		getConnection().getConnection();
	}
	
	/**
	 * Disconnect on destroy
	 */
	@Override
	public void onDestroy() {
		mConnection.disconnect();
		mConnection = null;
		super.onDestroy();
	}

	/**
	 * Return the XBMC connection
	 */
	@Override
	public Connection getConnection() {
		if (mConnection == null) {
			mConnection = new Connection(getApplicationContext());
		}
		return mConnection;
	}
	
	/**
	 * Return the browser fragment
	 */
	@Override
	public Browser getBrowser() {
		if (mBrowser == null) {
			mBrowser = (Browser)getFragmentManager().findFragmentById(R.id.browser);
		}
		return mBrowser;
	}
	
	/**
	 * Return the player fragment
	 */
	@Override
	public Player getPlayer() {
		if (mPlayer == null) {
			mPlayer = (Player)getFragmentManager().findFragmentById(R.id.player);
		}
		return mPlayer;
		
	}
	
	/**
	 * Return ther current layout
	 */
	@Override
	public int getLayout() {
		return LAYOUT_BROWSER_PLAYLIST;
	}
	
	
	/**
	 * Display main menu.
	 * (no menu for now, directly switch to preferences activity)
	 */
	@Override
	public void onMainMenu() {
		startActivity(new Intent(this, PreferencesActivity.class));
	}
}
