package de.wingesas.android.xbmcMusic.json.methods;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import de.wingesas.android.xbmcMusic.data.MusicListItem;
import de.wingesas.android.xbmcMusic.json.JsonMethod;

@TargetApi(11)
public class PlayerOpen extends JsonMethod<Boolean> {

	private Integer position;
	private MusicListItem musicItem;

	public PlayerOpen(MusicListItem item) {
		this.musicItem = item;
	}

	public PlayerOpen(MusicListItem item, Integer position) {
		this(item);
		this.position = position;
	}

	@Override
	protected String getMethod() {
		return "Player.Open";
	}

	@Override
	protected JSONObject getParams() throws JSONException {

		JSONObject object = new JSONObject();

		if (musicItem.isFile())
			object.put(musicItem.getIdLabel(), musicItem.getFile());
		else
			object.put(musicItem.getIdLabel(), musicItem.getId());

		if (position != null)
			object.put("position", position);

		JSONObject params = new JSONObject();
		params.put("item", object);

		return params;
	}

	@Override
	public Boolean handleResult(String result) throws JSONException {
		JSONObject jsonResult = new JSONObject(result);
		return jsonResult.getString("result").equals("OK");
	}
}
