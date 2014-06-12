package de.wingesas.android.xbmcMusic.json;

import android.os.AsyncTask;
import de.wingesas.android.xbmcMusic.data.PlaylistItem;
import de.wingesas.android.xbmcMusic.json.JsonTask.TaskResultListener;
import de.wingesas.android.xbmcMusic.json.methods.PlayerGetActiveAudioPlayer;
import de.wingesas.android.xbmcMusic.json.methods.PlayerGoNext;
import de.wingesas.android.xbmcMusic.json.methods.PlayerGoPrevious;
import de.wingesas.android.xbmcMusic.json.methods.PlayerGoTo;
import de.wingesas.android.xbmcMusic.json.methods.PlayerOpen;
import de.wingesas.android.xbmcMusic.json.methods.PlayerPlayPause;
import de.wingesas.android.xbmcMusic.json.methods.PlayerStop;
import de.wingesas.android.xbmcMusic.json.methods.PlaylistGetAudioPlaylist;
import de.wingesas.android.xbmcMusic.json.methods.PlaylistGetItems;

public class PlayerTask extends AsyncTask<PlayerTask.Action, String, Boolean> {

	private TaskResultListener<Boolean> resultListener;
	private Integer gotoPosition;

	public enum Action {
		GoNext, GoPrevious, GoTo, PlayPause, Stop
	}

	public PlayerTask(TaskResultListener<Boolean> resultListener) {
		this.resultListener = resultListener;
	}

	public PlayerTask(TaskResultListener<Boolean> resultListener, Integer gotoPosition) {
		this(resultListener);
		this.gotoPosition = gotoPosition;
	}

	@Override
	protected Boolean doInBackground(PlayerTask.Action... params) {

		boolean result = true;

		try {
			Integer playerid = new PlayerGetActiveAudioPlayer().execute();
			Integer playlistid = null;
			Boolean hasPlaylist = null;

			for (Action action : params) {

				if (action == Action.PlayPause || action == Action.Stop) {
					if (playerid != null) {
						if (action == Action.PlayPause)
							result &= new PlayerPlayPause(playerid).execute();

						if (action == Action.Stop)
							result &= new PlayerStop(playerid).execute();
					}
				} else {
					if (playerid != null) {
						if (action == Action.GoNext)
							result &= new PlayerGoNext(playerid).execute();

						if (action == Action.GoPrevious)
							result &= new PlayerGoPrevious(playerid).execute();

						if (action == Action.GoTo)
							result &= new PlayerGoTo(playerid, gotoPosition).execute();
					} else {
						if (playlistid == null) {
							playlistid = new PlaylistGetAudioPlaylist().execute();
							hasPlaylist = new PlaylistGetItems(playlistid).execute().size() > 0;
						}

						if (hasPlaylist
								&& (action == Action.GoNext || action == Action.GoPrevious || action == Action.GoTo)) {
							result &= new PlayerOpen(new PlaylistItem(playlistid), gotoPosition).execute();
							//if (action == Action.GoTo)
								//result &= new PlayerGoTo(playerid, gotoPosition).execute();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		if (resultListener != null)
			resultListener.onTaskComplete(result);
	}
}
