package com.snoopy.android.musicremote.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.snoopy.android.musicremote.R;

public class PreferencesFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
