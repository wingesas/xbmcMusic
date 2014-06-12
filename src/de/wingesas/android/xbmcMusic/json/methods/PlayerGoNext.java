package de.wingesas.android.xbmcMusic.json.methods;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class PlayerGoNext extends JsonMethod<Boolean> {

	private Integer playerid;
	private Integer xbmcVersion = 11;

	public PlayerGoNext(Integer playerid) {
		this.playerid = playerid;

		try {
			xbmcVersion = new ApplicationVersion().execute();
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "xbmcVersion", e);
		}
	}

	@Override
	protected String getMethod() {

		if (xbmcVersion == 11) {
			return "Player.GoNext";
		} else {
			if (xbmcVersion == 12) {
				return "Player.GoTo";
			} else
				return null;
		}
	}

	@Override
	protected JSONObject getParams() throws Exception {

		JSONObject params = new JSONObject();
		params.put("playerid", playerid);

		if (xbmcVersion == 12) {
			params.put("to", "next");
		}

		return params;
	}

	@Override
	public Boolean handleResult(String result) throws JSONException {
		JSONObject jsonResult = new JSONObject(result);
		return jsonResult.getString("result").equals("OK");
	}
}
