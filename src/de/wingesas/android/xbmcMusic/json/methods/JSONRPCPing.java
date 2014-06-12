package de.wingesas.android.xbmcMusic.json.methods;

import org.json.JSONException;
import org.json.JSONObject;

import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class JSONRPCPing extends JsonMethod<Boolean> {

	@Override
	protected String getMethod() {
		return "JSONRPC.Ping";
	}

	@Override
	protected JSONObject getParams() {
		return null;
	}

	@Override
	public Boolean handleResult(String result) throws JSONException {
		JSONObject jsonResult = new JSONObject(result);
		return jsonResult.getString("result").equals("pong");
	}
}
