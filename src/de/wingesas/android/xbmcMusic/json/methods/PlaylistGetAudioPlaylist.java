package de.wingesas.android.xbmcMusic.json.methods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class PlaylistGetAudioPlaylist extends JsonMethod<Integer> {

	@Override
	protected String getMethod() {
		return "Playlist.GetPlaylists";
	}

	@Override
	protected JSONObject getParams() throws JSONException {
		return null;
	}

	@Override
	public Integer handleResult(String result) throws JSONException {
		JSONArray array = new JSONObject(result).getJSONArray("result");

		Integer playlistid = null;

		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			String type = object.getString("type");

			if (type.equals("audio"))
				playlistid = object.getInt("playlistid");
		}
		return playlistid;
	}

}
