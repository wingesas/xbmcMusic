package de.wingesas.android.xbmcMusic;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import de.wingesas.android.xbmcMusic.data.AlbumItem;
import de.wingesas.android.xbmcMusic.data.ArtistItem;
import de.wingesas.android.xbmcMusic.data.FileSystemItem;
import de.wingesas.android.xbmcMusic.data.GenreItem;
import de.wingesas.android.xbmcMusic.data.MusicListItem;
import de.wingesas.android.xbmcMusic.data.SongItem;
import de.wingesas.android.xbmcMusic.fragments.AlbumListFragment;
import de.wingesas.android.xbmcMusic.fragments.ArtistListFragment;
import de.wingesas.android.xbmcMusic.fragments.FileSystemListFragment;
import de.wingesas.android.xbmcMusic.fragments.GenreListFragment;
import de.wingesas.android.xbmcMusic.fragments.ItemListFragment.OnItemClickedListener;
import de.wingesas.android.xbmcMusic.fragments.PlaylistActivity;
import de.wingesas.android.xbmcMusic.fragments.SongListFragment;
import de.wingesas.android.xbmcMusic.json.JsonTask;
import de.wingesas.android.xbmcMusic.json.PlaybackManager;
import de.wingesas.android.xbmcMusic.json.PlaybackManager.PlaybackMode;
import de.wingesas.android.xbmcMusic.json.methods.SystemShutdown;
import de.wingesas.android.xbmcMusic.preference.PreferenceActivity;

@TargetApi(11)
public class MainActivity extends Activity implements OnItemClickedListener<MusicListItem>, NotifyManager {

	private static final String ARTISTS = "ARTISTS";
	private static final String ALBUMS = "ALBUMS";
	private static final String SONGS = "SONGS";
	private static final String GENRE = "GENRE";
	private static final String FILES = "FILES";

	public static final String INTENT_SOURCE = "INTENT_SOURCE";

	private String key;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(0, ActionBar.DISPLAY_USE_LOGO);
		actionBar.setDisplayHomeAsUpEnabled(true);

		setProgressBarIndeterminateVisibility(false);

		if (savedInstanceState == null) {
			key = getIntent().getExtras().getString("KEY");

			if (key.equals(ARTISTS))
				replaceFragment(new ArtistListFragment(), ARTISTS, false);

			if (key.equals(ALBUMS))
				replaceFragment(new AlbumListFragment(), ALBUMS, false);

			if (key.equals(SONGS))
				replaceFragment(new ArtistListFragment(), SONGS, false);

			if (key.equals(GENRE))
				replaceFragment(new GenreListFragment(), GENRE, false);

			if (key.equals(FILES))
				replaceFragment(new FileSystemListFragment(), FILES, false);
		} else
			key = savedInstanceState.getString("key");

		String[] keys = getResources().getStringArray(R.array.navigation_list_actions_keys);
		String[] values = getResources().getStringArray(R.array.navigation_list_actions);

		for (int i = 0; i < keys.length; i++) {
			if (keys[i].equals(key))
				actionBar.setTitle(values[i]);
		}

		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			MainApplication app = (MainApplication) getApplication();
			return app.handleVolumeKeyEvent("KeyDown", keyCode, this);
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			MainApplication app = (MainApplication) getApplication();
			return app.handleVolumeKeyEvent("KeyUp", keyCode, this);
		} else
			return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString("key", key);
	}

	@Override
	public void onItemClicked(MusicListItem item) {

		if (item instanceof SongItem || (item instanceof FileSystemItem && !((FileSystemItem) item).isDirectory())) {

			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String prefPlaybackMode = pref.getString(PreferenceActivity.PLAYBACK_MODE, PreferenceActivity.PLAY);

			PlaybackMode playbackMode = prefPlaybackMode.equals(PreferenceActivity.PLAY) ? PlaybackManager.PlaybackMode.PLAY
					: PlaybackManager.PlaybackMode.QUEUE;

			new PlaybackManager(this, playbackMode).execute(new MusicListItem[] { item });
		}

		if (item instanceof ArtistItem) {
			if (key.equals(SONGS))
				replaceFragment(SongListFragment.newInstance(item.getIdLabel(), item.getId(), "album"));
			else
				replaceFragment(AlbumListFragment.newInstance(item.getIdLabel(), item.getId()));
		}

		if (item instanceof AlbumItem) {
			replaceFragment(SongListFragment.newInstance(item.getIdLabel(), item.getId(), null));
		}

		if (item instanceof GenreItem) {
			replaceFragment(SongListFragment.newInstance(item.getIdLabel(), item.getId(), "artist"));
		}

		if (item instanceof FileSystemItem) {
			FileSystemItem mediaItem = (FileSystemItem) item;

			if (mediaItem.isDirectory()) { // clicked on directory?
				// requery directory with clicked item as parent
				replaceFragment(FileSystemListFragment.newInstance(mediaItem.getFile()),
						FileSystemListFragment.class.getSimpleName());
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case android.R.id.home:
			intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.menu_shutdown:
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			String host = sharedPref
					.getString(PreferenceActivity.HOST_ADDRESS, PreferenceActivity.HOST_ADDRESS_DEFAULT);

			if (host != null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setIcon(R.drawable.ic_action_warning);
				builder.setTitle(R.string.shutdown_host);
				builder.setMessage(String.format(getResources().getString(R.string.shutdown_host_ask), host));
				builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new JsonTask<Boolean>(null).execute(new SystemShutdown[] { new SystemShutdown() });
					}
				});
				builder.setNegativeButton(android.R.string.cancel, null);
				builder.show();
			}

			return true;
		case R.id.menu_item_playlist:
			intent = new Intent(this, PlaylistActivity.class);
			intent.putExtra(INTENT_SOURCE, getClass().getSimpleName());
			startActivity(intent);
			return true;
		case R.id.menu_item_preferences:
			intent = new Intent(this, PreferenceActivity.class);
			intent.putExtra(INTENT_SOURCE, getClass().getSimpleName());
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void replaceFragment(Fragment fragment) {
		replaceFragment(fragment, null, true);
	}

	@TargetApi(11)
	private void replaceFragment(Fragment fragment, String tag) {
		replaceFragment(fragment, tag, true);
	}

	private void replaceFragment(Fragment fragment, String tag, boolean doAddToBackStack) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(android.R.id.content, fragment, tag);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.setBreadCrumbTitle(tag);
		if (doAddToBackStack)
			transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onAsyncTaskStarted() {
		setProgressBarIndeterminateVisibility(true);
	}

	@Override
	public void onAsyncTaskFinished() {
		setProgressBarIndeterminateVisibility(false);
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void onError(Exception e) {
		setProgressBarIndeterminateVisibility(false);

		MainApplication.onErrorHandler(this, e);
	}

	@Override
	public void onMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
}
