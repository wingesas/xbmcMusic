package de.wingesas.android.xbmcMusic.json.methods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class ApplicationVersion extends JsonMethod<Integer> {

	@Override
	protected String getMethod() {
		return "Application.GetProperties";
	}

	@Override
	protected JSONObject getParams() throws Exception {
		
		JSONObject params = new JSONObject();
		params.put("properties", new JSONArray().put("version"));

		return params;
	}

	@Override
	public Integer handleResult(String result) throws JSONException {
		
		JSONObject jsonResult = new JSONObject(result);
		JSONObject version = jsonResult.getJSONObject("result").getJSONObject("version");
		return version.getInt("major");
	}
}
