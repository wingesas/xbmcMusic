package de.wingesas.android.xbmcMusic.json.methods;

import org.json.JSONException;
import org.json.JSONObject;

import de.wingesas.android.xbmcMusic.data.PlaylistItem;
import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class PlayerGetItem extends JsonMethod<PlaylistItem> {
	
	private Integer playerid;
	
	public PlayerGetItem(Integer playerid) {
		this.playerid = playerid;
	}

	@Override
	protected String getMethod() {
		return "Player.GetItem";
	}

	@Override
	protected JSONObject getParams() throws Exception {

		JSONObject params = new JSONObject();
		params.put("playerid", playerid);

		return params;
	}

	@Override
	public PlaylistItem handleResult(String result) throws JSONException {

		PlaylistItem playlistItem = null;

		if (new JSONObject(result).has("result")) {
			JSONObject jsonResult = new JSONObject(result).getJSONObject("result");

			if (jsonResult.has("item")) {
				JSONObject item = jsonResult.getJSONObject("item");
				playlistItem = new PlaylistItem(item.has("id") ? item.getInt("id") : -1, item.getString("label"));
			}
		}

		return playlistItem;
	}
}
