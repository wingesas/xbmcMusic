package de.wingesas.android.xbmcMusic.fragments;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import de.wingesas.android.xbmcMusic.HomeActivity;
import de.wingesas.android.xbmcMusic.MainActivity;
import de.wingesas.android.xbmcMusic.MainApplication;
import de.wingesas.android.xbmcMusic.NotifyManager;
import de.wingesas.android.xbmcMusic.R;
import de.wingesas.android.xbmcMusic.data.PlaylistItem;
import de.wingesas.android.xbmcMusic.json.JsonLoader;
import de.wingesas.android.xbmcMusic.json.JsonTask.TaskResultListener;
import de.wingesas.android.xbmcMusic.json.PlayerTask;
import de.wingesas.android.xbmcMusic.json.PlayerTask.Action;
import de.wingesas.android.xbmcMusic.json.methods.PlaylistGetItems;

@TargetApi(11)
public class PlaylistFragment extends ListFragment implements LoaderCallbacks<List<PlaylistItem>>, NotifyManager {

	protected ArrayAdapter<PlaylistItem> adapter;
	private Activity activity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.activity = activity;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ListView listView = getListView();
		/*listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = getActivity().getMenuInflater();
				inflater.inflate(R.menu.playlist_cab_menu, menu);
				mode.setTitle(R.string.playlist_cab_menu_title);
				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
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
					mode.setSubtitle("" + checkedCount + " "
							+ getResources().getString(R.string.list_row_selected_plural));
					break;
				}
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.menu_playlist_remove_item:
					mode.finish();
					break;
				}
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
			}

		});*/

		adapter = new PlaylistArrayAdapter(getActivity());
		setListAdapter(adapter);

		if (savedInstanceState == null)
			setHasOptionsMenu(true);
		getLoaderManager().initLoader(0, getArguments(), this).forceLoad();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.playlist_activity, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		TaskResultListener<Boolean> resultListener = new TaskResultListener<Boolean>() {
			@Override
			public void onTaskComplete(Boolean result) {
				if (result != null && result)
					PlaylistFragment.this.getLoaderManager().restartLoader(0, null, PlaylistFragment.this).forceLoad();
			}
		};

		switch (item.getItemId()) {
		case android.R.id.home:

			Bundle extras = activity.getIntent().getExtras();

			if (extras != null
					&& extras.getString(MainActivity.INTENT_SOURCE).equals(MainActivity.class.getSimpleName())) {
				Intent intent = new Intent(activity, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			} else {
				Intent intent = new Intent(activity, HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			return true;
		case R.id.menu_reload:
			getLoaderManager().restartLoader(0, null, this).forceLoad();
			return true;
		case R.id.menu_playback_prev:
			new PlayerTask(resultListener).execute(new Action[] { Action.GoPrevious });
			return true;
		case R.id.menu_playback_pause:
			new PlayerTask(null).execute(new Action[] { Action.PlayPause });
			return true;
		case R.id.menu_playback_stop:
			new PlayerTask(resultListener).execute(new Action[] { Action.Stop });
			return true;
		case R.id.menu_playback_next:
			new PlayerTask(resultListener).execute(new Action[] { Action.GoNext });
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		TaskResultListener<Boolean> resultListener = new TaskResultListener<Boolean>() {
			@Override
			public void onTaskComplete(Boolean result) {
				if (result != null && result)
					PlaylistFragment.this.getLoaderManager().restartLoader(0, null, PlaylistFragment.this).forceLoad();
			}
		};

		new PlayerTask(resultListener, position).execute(new Action[] { Action.GoTo });
	}

	@Override
	public Loader<List<PlaylistItem>> onCreateLoader(int id, Bundle args) {
		activity.setProgressBarIndeterminateVisibility(true);
		return new JsonLoader<List<PlaylistItem>>(activity, new PlaylistGetItems());
	}

	@Override
	public void onLoadFinished(Loader<List<PlaylistItem>> loader, List<PlaylistItem> data) {
		adapter.clear();
		if (data != null)
			adapter.addAll(data);

		activity.setProgressBarIndeterminateVisibility(false);
	}

	@Override
	public void onLoaderReset(Loader<List<PlaylistItem>> loader) {

	}

	public class PlaylistArrayAdapter extends ArrayAdapter<PlaylistItem> {

		private final LayoutInflater inflater;

		public PlaylistArrayAdapter(Context context) {
			super(context, android.R.layout.simple_list_item_1);
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_row, parent, false);

				holder = new ViewHolder();
				holder.imageItemType = (ImageView) convertView.findViewById(R.id.image_item_type);
				holder.textLabel = (TextView) convertView.findViewById(R.id.text_label);
				holder.textSubLabel = (TextView) convertView.findViewById(R.id.text_sub_label);
				holder.textSubExtra = (TextView) convertView.findViewById(R.id.text_sub_extra);
				holder.subContainer = convertView.findViewById(R.id.sub_container);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();

			PlaylistItem item = getItem(position);

			holder.textLabel.setText(item.getLabel());

			if (item.getArtist() != null && item.getDuration() > 0) {
				holder.subContainer.setVisibility(View.VISIBLE);
				holder.textSubLabel.setText(item.getArtist());

				int duration = item.getDuration();
				holder.textSubExtra.setText(String.format("%d:%02d", duration / 60, duration % 60));
			} else {
				holder.subContainer.setVisibility(View.GONE);
			}

			if (item.isCurrentlyPlayed())
				holder.imageItemType.setImageResource(R.drawable.ic_action_headphones);
			else
				holder.imageItemType.setImageResource(R.drawable.ic_action_headphones_inactive);

			return convertView;
		}
	}

	static class ViewHolder {
		ImageView imageItemType;
		TextView textLabel;
		TextView textSubLabel;
		TextView textSubExtra;
		View subContainer;
	}

	@Override
	public void onAsyncTaskStarted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAsyncTaskFinished() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(Exception e) {
		MainApplication.onErrorHandler(getActivity(), e);
	}

	@Override
	public void onMessage(String message) {
		// TODO Auto-generated method stub

	}
}
