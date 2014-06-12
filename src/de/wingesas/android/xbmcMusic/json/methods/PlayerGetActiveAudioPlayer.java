package de.wingesas.android.xbmcMusic.json.methods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class PlayerGetActiveAudioPlayer extends JsonMethod<Integer> {

	@Override
	protected String getMethod() {
		return "Player.GetActivePlayers";
	}

	@Override
	protected JSONObject getParams() throws JSONException {
		return null;
	}

	@Override
	public Integer handleResult(String result) throws JSONException {
		
		JSONArray array = new JSONObject(result).getJSONArray("result");

		Integer playerid = null;

		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			String type = object.getString("type");

			if (type.equals("audio"))
				playerid = object.getInt("playerid");
		}
		return playerid;
	}

}
