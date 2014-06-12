package de.wingesas.android.xbmcMusic.fragments;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;
import android.widget.AbsListView.MultiChoiceModeListener;
import de.wingesas.android.xbmcMusic.NotifyManager;
import de.wingesas.android.xbmcMusic.R;
import de.wingesas.android.xbmcMusic.data.MusicListItem;
import de.wingesas.android.xbmcMusic.json.PlaybackManager;
import de.wingesas.android.xbmcMusic.json.PlaybackManager.PlaybackMode;

@TargetApi(11)
public abstract class ItemListFragment<T extends MusicListItem> extends ListFragment implements
		LoaderCallbacks<List<T>>, OnQueryTextListener {

	protected static final String KEY_ID_LABEL = "KEY_ID_LABEL";
	protected static final String KEY_ID = "KEY_ID";
	protected static final String SORT_METHOD = "SORT_METHOD";

	protected ArrayAdapter<T> adapter;
	protected OnItemClickedListener<T> clickedListener;
	protected NotifyManager notifyManager;

	private ActionMode mActionMode;
	private ModeCallback modeCallback;

	protected abstract ArrayAdapter<T> createListAdapter(Activity activity);

	protected abstract Loader<List<T>> createLoader();

	public interface OnItemClickedListener<T> {
		public void onItemClicked(T item);
	}

	public interface LoaderCallbacksListener {
		public void onLoaderCreated();

		public void onLoaderFinished();
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			clickedListener = (OnItemClickedListener<T>) activity;
			notifyManager = (NotifyManager) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement "
					+ OnItemClickedListener.class.getSimpleName());
		}
	}

	@Override
	public final void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ListView lv = getListView();
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		modeCallback = new ModeCallback();
		lv.setMultiChoiceModeListener(modeCallback);

		setHasOptionsMenu(true);

		adapter = createListAdapter(getActivity());
		setListAdapter(adapter);
		getLoaderManager().initLoader(0, getArguments(), this).forceLoad();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem item = menu.add(R.string.search);
		item.setIcon(R.drawable.ic_action_search);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		SearchView searchView = new SearchView(getActivity());
		searchView.setOnQueryTextListener(this);
		item.setActionView(searchView);

		int searchButtonId = searchView.getContext().getResources()
				.getIdentifier("android:id/search_button", null, null);
		if (searchButtonId != 0) {
			ImageView searchButton = (ImageView) searchView.findViewById(searchButtonId);
			if (searchButton != null)
				searchButton.setImageResource(R.drawable.ic_action_search);
		}

		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onQueryTextChange(String newText) {
		String mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
		if (adapter != null)
			adapter.getFilter().filter(mCurFilter);

		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		// Don't care about this.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// if (item.getItemId() == R.id.menu_overflow)
		// mActionMode = getActivity().startActionMode(modeCallback);
		return true;
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		// Called when the action mode is created; startActionMode() was called
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.list_row_select, menu);
			return true;
		}

		// Called each time the action mode is shown. Always called after
		// onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false; // Return false if nothing is done
		}

		// Called when the user selects a contextual menu item
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			// case R.id.menu_share:
			// shareCurrentItem();
			// mode.finish(); // Action picked, so close the CAB
			// return true;
			default:
				return true;
			}
		}

		// Called when the user exits the action mode
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
		}
	};

	@Override
	public final void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (mActionMode != null) {
			l.setItemChecked(position, true);
		} else {

			T item = adapter.getItem(position);
			clickedListener.onItemClicked(item);
		}
	}

	@Override
	public final Loader<List<T>> onCreateLoader(int id, Bundle args) {
		notifyManager.onAsyncTaskStarted();
		return createLoader();
	}

	@Override
	public final void onLoadFinished(Loader<List<T>> loader, List<T> data) {
		adapter.clear();
		if (data != null)
			adapter.addAll(data);

		notifyManager.onAsyncTaskFinished();
	}

	@Override
	public final void onLoaderReset(Loader<List<T>> loader) {

	}

	private class ModeCallback implements MultiChoiceModeListener {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = getActivity().getMenuInflater();
			inflater.inflate(R.menu.list_row_select, menu);
			mode.setTitle(R.string.list_row_select_title);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

			ListView listView = getListView();
			ArrayList<MusicListItem> items = new ArrayList<MusicListItem>();
			SparseBooleanArray array = listView.getCheckedItemPositions();

			for (int i = 0; i < array.size(); i++) {
				if (array.get(array.keyAt(i)))
					items.add((MusicListItem) listView.getItemAtPosition(array.keyAt(i)));
			}

			MusicListItem[] musicItems = new MusicListItem[items.size()];
			items.toArray(musicItems);

			switch (item.getItemId()) {
			case R.id.menu_playback_play:
				new PlaybackManager(notifyManager, PlaybackMode.PLAY).execute(musicItems);
				mode.finish();
				break;
			case R.id.menu_playback_queue:
				new PlaybackManager(notifyManager, PlaybackMode.QUEUE).execute(musicItems);
				mode.finish();
				break;
			}
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
		}

		@Override
		public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
			final int checkedCount = getListView().getCheckedItemCount();
			switch (checkedCount) {
			case 0:
				mode.setSubtitle(null);
				break;
			case 1:
				mode.setSubtitle("" + checkedCount + " "
						+ getResources().getString(R.string.list_row_selected_singular));
				break;
			default:
				mode.setSubtitle("" + checkedCount + " " + getResources().getString(R.string.list_row_selected_plural));
				break;
			}
		}
	}
}
