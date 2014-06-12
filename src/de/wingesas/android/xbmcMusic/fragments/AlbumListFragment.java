package de.wingesas.android.xbmcMusic.fragments;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Loader;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import de.wingesas.android.xbmcMusic.R;
import de.wingesas.android.xbmcMusic.data.AlbumItem;
import de.wingesas.android.xbmcMusic.json.JsonLoader;
import de.wingesas.android.xbmcMusic.json.JsonMethod;
import de.wingesas.android.xbmcMusic.json.methods.AudioLibraryGetAlbums;

@TargetApi(11)
public class AlbumListFragment extends ItemListFragment<AlbumItem> {

	public static ListFragment newInstance(String idLabel, int id) {

		Bundle args = new Bundle();
		args.putInt(KEY_ID, id);
		args.putString(KEY_ID_LABEL, idLabel);

		ListFragment fragment = new AlbumListFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected ArrayAdapter<AlbumItem> createListAdapter(Activity activity) {

		return new ItemListAdapter<AlbumItem>(activity) {

			@Override
			protected void prepareView(int position) {
				label = getItem(position).getLabel();
				subLabel = getItem(position).getArtist();
				if (getItem(position).getYear() > 0)
					subExtra = String.valueOf(getItem(position).getYear());
				else
					subExtra="";
				itemTypeImageResource = R.drawable.ic_action_record;
			}
		};
	}

	@Override
	protected Loader<List<AlbumItem>> createLoader() {

		Bundle args = getArguments();
		JsonMethod<List<AlbumItem>> method;

		if (args != null && args.containsKey(KEY_ID) && args.containsKey(KEY_ID_LABEL))
			method = new AudioLibraryGetAlbums(args.getInt(KEY_ID), args.getString(KEY_ID_LABEL));
		else
			method = new AudioLibraryGetAlbums();

		return new JsonLoader<List<AlbumItem>>(notifyManager, method);
	}
}
