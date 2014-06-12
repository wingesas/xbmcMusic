package de.wingesas.android.xbmcMusic.json.methods;

import org.json.JSONException;
import org.json.JSONObject;

import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class PlaylistClear extends JsonMethod<Boolean> {

	@Override
	protected String getMethod() {
		return "Playlist.Clear";
	}

	@Override
	protected JSONObject getParams() throws Exception {
		
		Integer playlistid = new PlaylistGetAudioPlaylist().execute();
		JSONObject params = new JSONObject();
		params.put("playlistid", playlistid);
		return params;
	}

	@Override
	public Boolean handleResult(String result) throws JSONException {
		JSONObject jsonResult = new JSONObject(result);
		return jsonResult.getString("result").equals("OK");
	}
}
