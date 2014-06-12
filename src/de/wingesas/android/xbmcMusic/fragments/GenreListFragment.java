package de.wingesas.android.xbmcMusic.fragments;

import java.util.List;

import android.app.Activity;
import android.content.Loader;
import android.widget.ArrayAdapter;
import de.wingesas.android.xbmcMusic.R;
import de.wingesas.android.xbmcMusic.data.GenreItem;
import de.wingesas.android.xbmcMusic.json.JsonLoader;
import de.wingesas.android.xbmcMusic.json.methods.AudioLibraryGetGenres;

public class GenreListFragment extends ItemListFragment<GenreItem> {

	@Override
	protected ArrayAdapter<GenreItem> createListAdapter(Activity activity) {
		return new ItemListAdapter<GenreItem>(activity) {

			@Override
			protected void prepareView(int position) {
				label = getItem(position).getLabel();
				itemTypeImageResource = R.drawable.ic_action_guitar;
			}
		};
	}

	@Override
	protected Loader<List<GenreItem>> createLoader() {
		return new JsonLoader<List<GenreItem>>(notifyManager, new AudioLibraryGetGenres());
	}

}
