package de.wingesas.android.xbmcMusic.json.methods;

import org.json.JSONException;
import org.json.JSONObject;

import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class PlayerPlayPause extends JsonMethod<Boolean> {
	
	private Integer playerid;
	
	public PlayerPlayPause(Integer playerid) {
		this.playerid = playerid;
	}

	@Override
	protected String getMethod() {
		return "Player.PlayPause";
	}

	@Override
	protected JSONObject getParams() throws Exception {

		JSONObject params = new JSONObject();
		params.put("playerid", playerid);

		return params;
	}

	@Override
	public Boolean handleResult(String result) throws JSONException {
		JSONObject jsonResult = new JSONObject(result);
		return jsonResult.getString("result").equals("OK");
	}
}
