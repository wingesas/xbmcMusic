package de.wingesas.android.xbmcMusic.json.methods;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import de.wingesas.android.xbmcMusic.data.PlaylistItem;
import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class PlaylistGetItems extends JsonMethod<List<PlaylistItem>> {

	private Integer playlistid;

	public PlaylistGetItems() {
	}

	public PlaylistGetItems(Integer playlistid) {
		this.playlistid = playlistid;
	}

	@Override
	protected String getMethod() {
		return "Playlist.GetItems";
	}

	@Override
	protected JSONObject getParams() throws Exception {

		if (playlistid == null)
			playlistid = new PlaylistGetAudioPlaylist().execute();

		JSONObject params = new JSONObject();
		params.put("playlistid", playlistid);

		JSONArray array = new JSONArray();
		array.put("duration").put("artist");
		params.put("properties", array);

		return params;
	}

	@Override
	public List<PlaylistItem> handleResult(String result) throws JSONException {

		List<PlaylistItem> items = new ArrayList<PlaylistItem>();
		JSONObject jsonResult = new JSONObject(result).getJSONObject("result");

		if (jsonResult.has("items")) {
			JSONArray array = jsonResult.getJSONArray("items");

			PlaylistItem currentlyPlayed = null;
			try {
				Integer playerid = new PlayerGetActiveAudioPlayer().execute();
				if (playerid != null)
					currentlyPlayed = new PlayerGetItem(playerid).execute();
			} catch (Exception e) {
				Log.e(PlaylistGetItems.class.getSimpleName(), "PlayerGetItem", e);
			}

			Integer xbmcVersion = 11;
			try {
				xbmcVersion = new ApplicationVersion().execute();
			} catch (Exception e) {
				Log.e(getClass().getSimpleName(), "AudioLibrary.GetAlbums", e);
			}

			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				String label = object.getString("label");

				PlaylistItem item = new PlaylistItem(object.has("id") ? object.getInt("id") : -1, label);
				if (object.has("artist")) {
					
					String artist = null;

					if (xbmcVersion == 11)
						artist = object.getString("artist");

					if (xbmcVersion == 12 && object.getJSONArray("artist").length() > 0) {
						artist = (String) object.getJSONArray("artist").get(0);
					}

					item.setArtist(artist);
				}

				if (object.has("duration"))
					item.setDuration(object.getInt("duration"));

				if (currentlyPlayed != null && currentlyPlayed.getId() == item.getId()
						&& currentlyPlayed.getLabel().equals(item.getLabel())) {
					item.setCurrentlyPlayed(true);
				}

				items.add(item);
			}
		}

		return items.isEmpty() ? null : items;
	}
}
