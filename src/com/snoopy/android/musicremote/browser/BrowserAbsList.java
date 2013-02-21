package com.snoopy.android.musicremote.browser;

import java.util.List;

import org.xbmc.android.jsonrpc.api.AbstractCall;
import org.xbmc.android.jsonrpc.api.AbstractModel;
import org.xbmc.android.jsonrpc.io.ApiCallback;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.snoopy.android.musicremote.R;
import com.snoopy.android.musicremote.RemoteAbsFragment;
import com.snoopy.android.musicremote.widgets.RemoteListLayout;

/**
 * Base class for all remote lists
 */
public abstract class BrowserAbsList<Element extends AbstractModel> extends RemoteAbsFragment
{
	/**
	 * Listener to catch loaded event
	 */
	public static class OnLoadedListener {
		public void onLoaded() {}
	}
	private OnLoadedListener mOnLoadedListener = null;

	
	/**
	 * Internal list class
	 */
	protected RemoteListLayout mRemoteList = null;

	
	/**
	 * Return a string to be displayed in the list header
	 */
	protected abstract String getHeader();
	
	
	/**
	 * Callback on item selected
	 */
	protected void onItemSelected(Element element) {};
	
	/**
	 * Return the XBMC query to send for filling the list.
	 * On error, this function should return null and set a warning
	 */
	protected abstract AbstractCall<Element> getRemoteQuery();
	
	
	/**
	 * Return a new appropriate list adapter for this class.  
	 */
	protected abstract ArrayAdapter<Element> getArrayAdapter(List<Element> elements);
	
	
	/**
	 * Set a listener on loaded event
	 */
	public void setOnLoadedListener(OnLoadedListener listener) {
		mOnLoadedListener = listener;
	}

	/**
	 * Display a warning
	 */
	public void setWarning(String message) {
		mRemoteList.setWarning(message);
	}
	
	/**
	 * Return the list view
	 */
	public ListView getListView() {
		return mRemoteList.getListView();
	}
	
	/**
	 * Build the view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		if (mRemoteList == null) {
			mRemoteList = (RemoteListLayout)inflater.inflate(R.layout.remote_list, container, false);
			mRemoteList.setHeader(getHeader());
			mRemoteList.getListView().setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					@SuppressWarnings("unchecked")
					ArrayAdapter<Element> adapter = (ArrayAdapter<Element>)mRemoteList.getAdapter();
					if (adapter != null) {
						onItemSelected(adapter.getItem(position));
					}
				}
			});
			mRemoteList.setOnButtonMenuListener(new OnClickListener() {		
				@Override
				public void onClick(View v) {
					onMainMenu();
				}
			});
		}

		return mRemoteList;
	}

	
	
	/**
	 * Refresh the list
	 */
	@Override
	protected void refresh()
	{
		mRemoteList.getListView().setAdapter(null);
		mRemoteList.clearWarning();
		mRemoteList.setLoading();	
		mRemoteList.setHeader(getHeader() + " (loading)");
		

		AbstractCall<Element> query = getRemoteQuery();
		if (query == null) return;
		
		// Process query
		getConnection().call(query, new ApiCallback<Element>() {
			public void onResponse(AbstractCall<Element> query) {
				if (getActivity() == null) {
					// we have been detached after the send. drop result.
					needRefresh();
					return;
				}
				List<Element> elements = query.getResults();
				getListView().setAdapter(getArrayAdapter(elements));
				if (elements.isEmpty()) mRemoteList.setWarning("No elements founds!");
				mRemoteList.clearLoading();
				mRemoteList.setHeader(getHeader() + " (" + String.valueOf(elements.size()) + ")");
				if (mOnLoadedListener != null) mOnLoadedListener.onLoaded();
			}
			public void onError(int code, String message, String hint) {
				mRemoteList.setWarning("XBMC connection error!");
				if (mOnLoadedListener != null) mOnLoadedListener.onLoaded();
			}
		});
	}
	
	
	@Override
	public void onStart() {
		super.onStart();
		switch(getLayout()) {
		case LAYOUT_BROWSER_ONLY:
			mRemoteList.setMenuButtonVisibility(true);
			break;
		case LAYOUT_BROWSER_PLAYLIST:
			mRemoteList.setMenuButtonVisibility(false);
			break;			
		}
	}


}
