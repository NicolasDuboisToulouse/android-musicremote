package com.snoopy.android.musicremote.browser;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.snoopy.android.musicremote.RemoteAbsFragment;
import com.snoopy.android.musicremote.R;

/**
 * Base class for tabbed browsing
 */
@SuppressLint("UseSparseArrays")
public abstract class BrowserAbsTabs extends RemoteAbsFragment {
	
	/**
	 * Internal radio group
	 */
	private RadioGroup mRadioGroup = null;

	/**
	 * Radio button to content
	 */
	private Map<Integer, RemoteAbsFragment> mContents;
	
	
	/**
	 * Content to radio button
	 */
	private Map<RemoteAbsFragment, Integer> mContentToRadio;

	/**
	 * Currently selected tab
	 */
	private int mCurrentRadioId = View.NO_ID;
	
	/**
	 * Tab user want to select
	 */
	private int mRequestedTab = View.NO_ID;

	/**
	 * ID of container view
	 */
	private int mContainerId = View.NO_ID;
		
	/**
	 * Build the view
	 * Usage : override this method with :
	 *  View view = super.onCreateView(inflater, container, savedInstanceState)
	 *  addtab(...)
	 *  ..
	 *  return view;
	 */
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.browser_tabs, container, false);

		// The fragment manager work on activity hierarchy and not on parent's fragment one.
		// So Each instance of this base class have to use different Ids for their container view.
		mContainerId = generateViewId();
		View tabContent = view.findViewById(R.id.view_tabs_content);
		tabContent.setId(mContainerId);
				
		
		mContents = new HashMap<Integer, RemoteAbsFragment>();
		mContentToRadio = new HashMap<RemoteAbsFragment, Integer>();
		
		mRadioGroup = (RadioGroup) view.findViewById(R.id.view_tabs_group);
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {			

				// This callback can be called several times for one check
				// and we can't call replace twice (fragment manager issue).
				if (mCurrentRadioId == checkedId) return;
				
				// This callback is also called on an button "uncheck"...
				if (((RadioButton)group.findViewById(checkedId)).isChecked()) {
					mCurrentRadioId = checkedId;
					
					// Replace Fragment only if needed (we can't call replace on the same fragment)
					Fragment tab = mContents.get(checkedId);
					FragmentManager fm = getFragmentManager();
					if (fm.findFragmentById(mContainerId) != tab) {
						FragmentTransaction ft = fm.beginTransaction();	
						ft.replace(mContainerId, tab);
						ft.commit();
					}
				}
			}
		});

		return view;
	}

	/**
	 * Add a new tab
	 * @param tab tab to be added
	 * @param drawableId id of tab icon
	 */
	protected void addTab(RemoteAbsFragment tab, int drawableId) 
	{	
		int buttonId = generateViewId();
		Drawable icon = getResources().getDrawable(drawableId);
		RadioButton button = new RadioButton(getActivity());
		button.setBackgroundDrawable(icon); // If we don't set this one, button won't scale correctly.
		button.setButtonDrawable(icon);
		button.setId(buttonId);
		
		
		// Add separators between buttons
		if (mContents.size() != 0) {
			ImageView image = new ImageView(getActivity());
			image.setImageDrawable(getResources().getDrawable(R.drawable.button_separator));
			mRadioGroup.addView(image);
		}
		
		mRadioGroup.addView(button);

		mContents.put(buttonId, tab);
		mContentToRadio.put(tab, buttonId);
		
		if (mContents.size() == 1) setTab(tab);
	}
	
	/**
	 * Switch to the given tab
	 * @param tab
	 */
	protected void setTab(RemoteAbsFragment tab) 
	{
		if (mContentToRadio.containsKey(tab)) {
			if (this.isAdded()) {
				// We let the onCheckedChanged callback do the job
				mRadioGroup.check(mContentToRadio.get(tab));
			} else {
				// We can't select a tab if fragment is not started. We'll do this onStart().
				mRequestedTab = mContentToRadio.get(tab);
			}
		}
	}
	
	
	/**
	 * Display/hide buttons
	 */
	protected void setButtonsVisibility(boolean visible) {
		if (visible) {
			mRadioGroup.setVisibility(View.VISIBLE);
		} else {
			mRadioGroup.setVisibility(View.GONE);			
		}
	}
	
	
	/**
	 * onStart: Handle a tab-change request during fragment stopped.
	 */
	@Override
	public void onStart() {
		super.onStart();
		if (mRequestedTab != View.NO_ID) mRadioGroup.check(mRequestedTab);
		mRequestedTab = View.NO_ID;
	}
}
