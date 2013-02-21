package com.snoopy.android.musicremote;

import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.snoopy.android.musicremote.Player.Player;
import com.snoopy.android.musicremote.browser.Browser;
import com.snoopy.android.musicremote.business.Connection;

/**
 * Base class for all remote fragments
 */
public abstract class RemoteAbsFragment extends Fragment implements IGlobalFeatures {
	
	/**
	 * Base remote activity
	 */
	private IGlobalFeatures mRemoteActivity;
	
	
	/**
	 * Do we need to refresh ?
	 */
	private boolean mNeedRefresh = true;
	

	/**
	 * View-id generator (implement in View class for API 17)
	 */
    private static final AtomicInteger mNextViewId = new AtomicInteger(1);

    
    /**
     * Preferences observer
     */
    SharedPreferences.OnSharedPreferenceChangeListener mPreferencesListener;
    
        
    /**
     * Load remote interface
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	mRemoteActivity = (IGlobalFeatures) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IRemoteActivity");
        }
    }

    
	
	/**
	 * Find the connection
	 */
    @Override
	public Connection getConnection() {
		return mRemoteActivity.getConnection();
	}
	
	/**
	 * Find the browser
	 */
    @Override
	public Browser getBrowser() {
		return mRemoteActivity.getBrowser();
	}

	/**
	 * Find the player
	 */
    @Override
	public Player getPlayer() {
		return mRemoteActivity.getPlayer();
	}

    /**
     * Return the layout
     */
    @Override
    public int getLayout() {
    	return mRemoteActivity.getLayout();
    }

    /**
     * Display main menu
     */
	@Override
	public void onMainMenu() {
		mRemoteActivity.onMainMenu();
	}	

    
	/**
	 * Start the fragment
	 * Handle a refresh pending
	 */
	@Override
	public void onStart() {
		super.onStart();
		if (mNeedRefresh) {
			mNeedRefresh = false;
			refresh();
		}
		
		// Refresh on preferences change
		if (mPreferencesListener == null) {
	    	mPreferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
	    		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
	    			needRefresh();
	    		}
	    	};
	    	PreferenceManager.getDefaultSharedPreferences(getActivity()).
  				registerOnSharedPreferenceChangeListener(mPreferencesListener);
		}
	}
	
  		
	/**
	 * Request for refresh
	 * The fragment will be refreshed immediately if it's already started or when started.
	 */
	public void needRefresh() {
		if (isResumed()) {
			refresh();
		} else {
			mNeedRefresh = true;
		}
	}
		
	/**
	 * Refresh the fragment
	 * Don't call this one directly, call needRefresh()
	 */
	protected void refresh() {}


	/**
	 * Generate a new view ID unused by any view in the activity
	 * aapt-generated IDs have the high byte nonzero so this function
	 * forks for the 2^24 first calls only...
	 * (This function is implemented in View in API 17)
	 */
	public static int generateViewId() {
		return mNextViewId.addAndGet(1);
	}
}
