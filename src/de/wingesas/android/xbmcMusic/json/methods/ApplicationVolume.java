package de.wingesas.android.xbmcMusic.json.methods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class ApplicationVolume extends JsonMethod<Integer> {

	private Action action;
	private int volume = 0;

	public enum Action {
		GET, SET
	}

	public ApplicationVolume(Action action) {
		this(action, 0);
	}
	
	public ApplicationVolume(Action action, int volume) {
		this.action = action;
		this.volume = volume;
	}

	@Override
	protected String getMethod() {
		if (action == Action.GET)
			return "Application.GetProperties";
		else
			return "Application.SetVolume";
	}

	@Override
	protected JSONObject getParams() throws Exception {

		JSONObject params = new JSONObject();

		if (action == Action.GET) {
			params.put("properties", new JSONArray().put("volume"));
		} else
			params.put("volume", volume);

		return params;
	}

	@Override
	public Integer handleResult(String result) throws JSONException {

		JSONObject jsonResult = new JSONObject(result);

		if (action == Action.GET) {
			return jsonResult.getJSONObject("result").getInt("volume");
		} else {
			return jsonResult.getInt("result");
		}
	}
}
