package de.wingesas.android.xbmcMusic.json.methods;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.wingesas.android.xbmcMusic.data.ArtistItem;
import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class AudioLibraryGetArtists extends JsonMethod<List<ArtistItem>> {

	private int genreid = -1;

	@Override
	protected String getMethod() {
		return "AudioLibrary.GetArtists";
	}

	@Override
	protected JSONObject getParams() throws JSONException {
		JSONObject params = new JSONObject();

		if (genreid > -1)
			params.put("genreid", genreid);

		params.put("sort", new JSONObject().put("method", "label"));

		return params;
	}

	@Override
	public List<ArtistItem> handleResult(String result) throws JSONException {

		JSONObject jsonResult = new JSONObject(result).getJSONObject("result");

		List<ArtistItem> items = new ArrayList<ArtistItem>();
		
		if (jsonResult.has("artists")) {
			JSONArray array = jsonResult.getJSONArray("artists");

			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				int artistid = object.getInt("artistid");
				String label = object.getString("label");

				items.add(new ArtistItem(artistid, label));
			}
		}

		return items.isEmpty() ? null : items;
	}

}
