package de.wingesas.android.xbmcMusic.fragments;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Loader;
import android.widget.ArrayAdapter;
import de.wingesas.android.xbmcMusic.R;
import de.wingesas.android.xbmcMusic.data.ArtistItem;
import de.wingesas.android.xbmcMusic.json.JsonLoader;
import de.wingesas.android.xbmcMusic.json.JsonMethod;
import de.wingesas.android.xbmcMusic.json.methods.AudioLibraryGetArtists;

@TargetApi(11)
public class ArtistListFragment extends ItemListFragment<ArtistItem> {
	
	@Override
	protected ArrayAdapter<ArtistItem> createListAdapter(Activity activity) {

		return new ItemListAdapter<ArtistItem>(activity) {

			@Override
			protected void prepareView(int position) {
				label = getItem(position).getLabel();
				itemTypeImageResource = R.drawable.ic_action_mic;
			}
		};
	}

	@Override
	protected Loader<List<ArtistItem>> createLoader() {
		JsonMethod<List<ArtistItem>> jsonMethod = new AudioLibraryGetArtists();
		Loader<List<ArtistItem>> loader = new JsonLoader<List<ArtistItem>>(notifyManager, jsonMethod);
		return loader;
	}
}
