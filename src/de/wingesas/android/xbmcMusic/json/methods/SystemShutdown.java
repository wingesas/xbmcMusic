package de.wingesas.android.xbmcMusic.json.methods;

import org.json.JSONException;
import org.json.JSONObject;

import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class SystemShutdown extends JsonMethod<Boolean> {

	@Override
	protected String getMethod() {
		return "System.Shutdown";
	}

	@Override
	protected JSONObject getParams() throws Exception {
		return null;
	}

	@Override
	public Boolean handleResult(String result) throws JSONException {
		JSONObject jsonResult = new JSONObject(result);
		return jsonResult.getString("result").equals("OK");
	}
}
