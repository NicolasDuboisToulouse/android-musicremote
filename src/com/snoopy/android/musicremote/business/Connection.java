package com.snoopy.android.musicremote.business;

import org.xbmc.android.jsonrpc.api.AbstractCall;
import org.xbmc.android.jsonrpc.config.HostConfig;
import org.xbmc.android.jsonrpc.io.ApiCallback;
import org.xbmc.android.jsonrpc.io.ConnectionManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * Wrapper to the ConnectionManager class to allow configuration update.
 */
public class Connection {
	
	/**
	 * Internal connection
	 */
	private ConnectionManager mConnectionManager;

	/**
	 * application context
	 */
	private final Context mAppContext;
	
	
	/**
	 * Ctor
	 * @param appConext application context
	 */
	public Connection(Context appContext) {
		mAppContext = appContext;
	}
	
	
	/**
	 * Make an API call
	 * @param call
	 * @param callback
	 */
	public <T> void call(AbstractCall<T> call, ApiCallback<T> callback) {
		getConnection().call(call, callback);
	}
	
	/**
	 * Close the connection
	 */
	public void disconnect() {
		if (mConnectionManager != null) {
			mConnectionManager.disconnect();
			mConnectionManager = null;
		}
	}
	
	/**
	 * Destroy the connection
	 */
	public void forceDestroy() {
		if (mConnectionManager != null) {
			mConnectionManager.forceDestroy();
			mConnectionManager = null;
		}		
	}

	
	/**
	 * @return the connection
	 */
	public synchronized ConnectionManager getConnection() {
		if (mConnectionManager == null) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
			Log.i("kikoo", "create connection to " + preferences.getString("xbmc_host", "xbmc"));
			HostConfig hostConfig = new HostConfig(preferences.getString("xbmc_host", "xbmc"), 
													8080, // HTTP port: Unneeded
													9090, // Not configurable throw XBMC gui
													"",   // HTTP user: Unneeded, 
													"");  // HTTP password: Unneeded);
			mConnectionManager = new ConnectionManager(mAppContext, hostConfig);
		}
		return mConnectionManager;
	}	
}
