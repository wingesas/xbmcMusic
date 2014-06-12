package de.wingesas.android.xbmcMusic.json.methods;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import de.wingesas.android.xbmcMusic.data.AlbumItem;
import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class AudioLibraryGetAlbums extends JsonMethod<List<AlbumItem>> {

	private int id = -1;
	private String idLabel = null;

	public AudioLibraryGetAlbums() {
	}

	public AudioLibraryGetAlbums(int id, String idLabel) {
		this.id = id;
		this.idLabel = idLabel;
	}

	@Override
	protected String getMethod() {
		return "AudioLibrary.GetAlbums";
	}

	@Override
	protected JSONObject getParams() throws Exception {

		JSONObject params = new JSONObject();
		Integer xbmcVersion = new ApplicationVersion().execute();

		if (xbmcVersion == 11 && id > -1 && idLabel != null)
			params.put(idLabel, id);

		if (xbmcVersion == 12 && id > -1 && idLabel != null)
			params.put("filter", new JSONObject().put(idLabel, id));

		params.put("sort", new JSONObject().put("method", "label"));
		params.put("properties", new JSONArray().put("title").put("artist").put("year"));

		return params;
	}

	@Override
	public List<AlbumItem> handleResult(String result) throws JSONException {

		JSONObject jsonResult = new JSONObject(result).getJSONObject("result");

		List<AlbumItem> items = new ArrayList<AlbumItem>();

		Integer xbmcVersion = 11;
		try {
			xbmcVersion = new ApplicationVersion().execute();
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "AudioLibrary.GetAlbums", e);
		}

		if (jsonResult.has("albums")) {
			JSONArray array = jsonResult.getJSONArray("albums");

			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);

				String artist = null;
				if (object.has("artist")) {
					if (xbmcVersion == 11)
						artist = object.getString("artist");

					if (xbmcVersion == 12 && object.getJSONArray("artist").length() > 0) {
						artist = (String) object.getJSONArray("artist").get(0);
					}
				}

				items.add(new AlbumItem(object.getInt("albumid"), object.getString("label"), artist,
						object.has("year") ? object.getInt("year") : null));
			}
		}

		return items.isEmpty() ? null : items;
	}
}
