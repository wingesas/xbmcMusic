package de.wingesas.android.xbmcMusic.json.methods;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import de.wingesas.android.xbmcMusic.data.SongItem;
import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class AudioLibraryGetSongs extends JsonMethod<List<SongItem>> {

	private int id = -1;
	private String idLabel = null;
	private String sort = null;

	public AudioLibraryGetSongs() {
	}

	public AudioLibraryGetSongs(int id, String idLabel) {
		this.id = id;
		this.idLabel = idLabel;
	}

	public AudioLibraryGetSongs(int id, String idLabel, String sort) {
		this(id, idLabel);
		this.sort = sort;
	}

	@Override
	protected String getMethod() {
		return "AudioLibrary.GetSongs";
	}

	@Override
	protected JSONObject getParams() throws Exception {

		JSONObject params = new JSONObject();

		Integer xbmcVersion = new ApplicationVersion().execute();

		if (xbmcVersion == 11 && id > -1 && idLabel != null)
			params.put(idLabel, id);

		if (xbmcVersion == 12 && id > -1 && idLabel != null)
			params.put("filter", new JSONObject().put(idLabel, id));

		if (sort != null)
			params.put("sort", new JSONObject().put("method", sort));
		else
			params.put("sort", new JSONObject().put("method", "track"));

		params.put("properties", new JSONArray().put("artist").put("duration").put("album"));

		return params;
	}

	@Override
	public List<SongItem> handleResult(String result) throws JSONException {

		JSONObject jsonResult = new JSONObject(result).getJSONObject("result");
		List<SongItem> items = new ArrayList<SongItem>();

		Integer xbmcVersion = 11;
		try {
			xbmcVersion = new ApplicationVersion().execute();
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "AudioLibrary.GetAlbums", e);
		}

		if (jsonResult.has("songs")) {
			JSONArray array = jsonResult.getJSONArray("songs");

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

				SongItem item = new SongItem(object.getInt("songid"), object.getString("label"), artist,
						object.has("duration") ? object.getInt("duration") : null);

				if (object.has("album"))
					item.setAlbum(object.getString("album"));

				items.add(item);
			}
		}

		return items.isEmpty() ? null : items;
	}
}
