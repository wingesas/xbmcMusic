package de.wingesas.android.xbmcMusic.fragments;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Loader;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import de.wingesas.android.xbmcMusic.R;
import de.wingesas.android.xbmcMusic.data.ArtistItem;
import de.wingesas.android.xbmcMusic.data.SongItem;
import de.wingesas.android.xbmcMusic.json.JsonLoader;
import de.wingesas.android.xbmcMusic.json.JsonMethod;
import de.wingesas.android.xbmcMusic.json.methods.AudioLibraryGetSongs;

@TargetApi(11)
public class SongListFragment extends ItemListFragment<SongItem> {

	public static ListFragment newInstance(String idLabel, int id, String sort) {

		Bundle args = new Bundle();
		args.putInt(KEY_ID, id);
		args.putString(KEY_ID_LABEL, idLabel);
		if (sort != null)
			args.putString(SORT_METHOD, sort);

		ListFragment fragment = new SongListFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected ArrayAdapter<SongItem> createListAdapter(Activity activity) {

		return new ItemListAdapter<SongItem>(activity) {

			@Override
			protected void prepareView(int position) {
				label = getItem(position).getLabel();

				Bundle args = getArguments();
				if (args != null && args.getString(KEY_ID_LABEL).equals(ArtistItem.ARTIST_ID))
					subLabel = getItem(position).getAlbum();
				else
					subLabel = getItem(position).getArtist();

				int duration = getItem(position).getDuration();
				subExtra = String.format("%d:%02d", duration / 60, duration % 60);
				itemTypeImageResource = R.drawable.ic_action_music_1;
			}
		};
	}

	@Override
	protected Loader<List<SongItem>> createLoader() {

		Bundle args = getArguments();
		JsonMethod<List<SongItem>> method;

		if (args != null && args.containsKey(KEY_ID) && args.containsKey(KEY_ID_LABEL))
			method = new AudioLibraryGetSongs(args.getInt(KEY_ID), args.getString(KEY_ID_LABEL),
					args.getString(SORT_METHOD));
		else
			method = new AudioLibraryGetSongs();

		return new JsonLoader<List<SongItem>>(notifyManager, method);
	}
}
