package com.snoopy.android.musicremote;

import com.snoopy.android.musicremote.Player.Player;
import com.snoopy.android.musicremote.browser.Browser;
import com.snoopy.android.musicremote.business.Connection;

/**
 * Base remote interface
 */
public interface IGlobalFeatures {

	static final int LAYOUT_BROWSER_ONLY = 0;
	static final int LAYOUT_BROWSER_PLAYLIST = 1;
	
	/**
	 * @return The global connection object
	 */
	public Connection getConnection();
	
	/**
	 * @return The main browser class
	 */
	public Browser getBrowser();
	
	/**
	 * @return The main player class
	 */
	public Player getPlayer();
	
	/**
	 * @return the current layout
	 */
	public int getLayout();
	
	/**
	 * Display main menu
	 */
	public void onMainMenu();
}
