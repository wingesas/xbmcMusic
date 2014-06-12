package de.wingesas.android.xbmcMusic.json;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import de.wingesas.android.xbmcMusic.NotifyManager;
import de.wingesas.android.xbmcMusic.R;
import de.wingesas.android.xbmcMusic.data.FileSystemItem;
import de.wingesas.android.xbmcMusic.data.MusicListItem;
import de.wingesas.android.xbmcMusic.data.PlaylistItem;
import de.wingesas.android.xbmcMusic.json.methods.FilesGetDirectory;
import de.wingesas.android.xbmcMusic.json.methods.PlayerOpen;
import de.wingesas.android.xbmcMusic.json.methods.PlaylistAdd;
import de.wingesas.android.xbmcMusic.json.methods.PlaylistClear;
import de.wingesas.android.xbmcMusic.json.methods.PlaylistGetAudioPlaylist;

public class PlaybackManager extends AsyncTask<MusicListItem, String, Boolean> {

	private NotifyManager notifyManager;
	private PlaybackMode playbackMode;
	private Exception taskException;

	public enum PlaybackMode {
		PLAY, QUEUE
	};

	public PlaybackManager(NotifyManager notifyManager, PlaybackMode playbackMode) {
		this.notifyManager = notifyManager;
		this.playbackMode = playbackMode;
	}

	@Override
	protected Boolean doInBackground(MusicListItem... params) {

		boolean result = true;
		Resources res = notifyManager.getActivity().getResources();

		try {

			List<MusicListItem> playbackItems = new ArrayList<MusicListItem>();

			for (MusicListItem playbackItem : params) {
				if (playbackItem.isDirectory()) {
					for (FileSystemItem childItem : new FilesGetDirectory(playbackItem.getFile()).execute())
						if (!childItem.isDirectory())
							playbackItems.add(childItem);
				} else
					playbackItems.add(playbackItem);
			}

			if (playbackMode == PlaybackMode.PLAY && playbackItems.size() > 1)
				result &= new PlaylistClear().execute();

			if (playbackItems.isEmpty())
				publishProgress(res.getString(R.string.directory_no_media));
			else {
				if (playbackItems.size() == 1) {

					publishProgress(String.format(
							res.getString(playbackMode == PlaybackMode.PLAY ? R.string.playing : R.string.queueing),
							playbackItems.get(0).getLabel()));

					if (playbackMode == PlaybackMode.PLAY)
						result &= new PlayerOpen(playbackItems.get(0)).execute();
					else {
						Integer playlistid = new PlaylistGetAudioPlaylist().execute();
						result &= new PlaylistAdd(playbackItems.get(0), playlistid).execute();
					}
				} else {

					Integer playlistid = new PlaylistGetAudioPlaylist().execute();

					for (int i = 0; i < playbackItems.size(); i++) {
						result &= new PlaylistAdd(playbackItems.get(i), playlistid).execute();

						if (i == 0) {
							publishProgress(res.getString(playbackMode == PlaybackMode.PLAY ? R.string.playing_multiple
									: R.string.queueing_multiple));

							if (playbackMode == PlaybackMode.PLAY)
								result &= new PlayerOpen(new PlaylistItem(playlistid)).execute();
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "doInBackground", e);
			this.taskException = e;
		}

		return result;
	}

	@Override
	protected void onProgressUpdate(String... progress) {
		notifyManager.onMessage(progress[0]);
		super.onProgressUpdate(progress);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (taskException != null)
			notifyManager.onError(this.taskException);
		else {
			super.onPostExecute(result);
		}
	}
}
