package de.wingesas.android.xbmcMusic.fragments;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Loader;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import de.wingesas.android.xbmcMusic.R;
import de.wingesas.android.xbmcMusic.data.FileSystemItem;
import de.wingesas.android.xbmcMusic.json.JsonLoader;
import de.wingesas.android.xbmcMusic.json.JsonMethod;
import de.wingesas.android.xbmcMusic.json.methods.FilesGetDirectory;

@TargetApi(11)
public class FileSystemListFragment extends ItemListFragment<FileSystemItem> {

	public static FileSystemListFragment newInstance(String parent) {
		FileSystemListFragment fragment = new FileSystemListFragment();
		Bundle args = new Bundle();
		if (parent != null)
			args.putString("parent", parent);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	protected ArrayAdapter<FileSystemItem> createListAdapter(Activity activity) {

		return new ItemListAdapter<FileSystemItem>(activity) {

			@Override
			protected void prepareView(int position) {
				label = getItem(position).getLabel();
				itemTypeImageResource = getItem(position).isDirectory() ? 0 : R.drawable.ic_action_music_1;
			}
		};
	}

	@Override
	protected Loader<List<FileSystemItem>> createLoader() {

		String parent = null;
		if (getArguments() != null)
			parent = getArguments().getString("parent");
		JsonMethod<List<FileSystemItem>> jsonMethod = new FilesGetDirectory(parent);
		Loader<List<FileSystemItem>> loader = new JsonLoader<List<FileSystemItem>>(notifyManager, jsonMethod);

		return loader;
	}
}
