package de.wingesas.android.xbmcMusic.json.methods;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class PlayerGoTo extends JsonMethod<Boolean> {
	
	private Integer playerid;	
	private int position;

	public PlayerGoTo(Integer playerid, int position) {
		this.playerid=playerid;
		this.position=position;
	}

	@Override
	protected String getMethod() {
		return "Player.GoTo";
	}

	@Override
	protected JSONObject getParams() throws Exception {

		JSONObject params = new JSONObject();
		params.put("playerid", playerid);
		
		Integer xbmcVersion = 11;
		try {
			xbmcVersion = new ApplicationVersion().execute();
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "xbmcVersion", e);
		}
		
		if (xbmcVersion == 11)
			params.put("position", position);
		
		if (xbmcVersion == 12)
			params.put("to", position);

		return params;
	}

	@Override
	public Boolean handleResult(String result) throws JSONException {
		JSONObject jsonResult = new JSONObject(result);
		return jsonResult.getString("result").equals("OK");
	}
}
