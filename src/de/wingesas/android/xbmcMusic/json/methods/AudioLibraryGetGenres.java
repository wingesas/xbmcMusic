package de.wingesas.android.xbmcMusic.json.methods;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.wingesas.android.xbmcMusic.data.GenreItem;
import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class AudioLibraryGetGenres extends JsonMethod<List<GenreItem>> {

	@Override
	protected String getMethod() {
		return "AudioLibrary.GetGenres";
	}

	@Override
	protected JSONObject getParams() throws Exception {
		return null;
	}

	@Override
	public List<GenreItem> handleResult(String result) throws JSONException {
		JSONObject jsonResult = new JSONObject(result).getJSONObject("result");
		List<GenreItem> items = new ArrayList<GenreItem>();

		if (jsonResult.has("genres")) {
			JSONArray array = jsonResult.getJSONArray("genres");

			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);

				items.add(new GenreItem(object.getInt("genreid"), object.getString("label")));
			}
		}

		return items.isEmpty() ? null : items;
	}

}
