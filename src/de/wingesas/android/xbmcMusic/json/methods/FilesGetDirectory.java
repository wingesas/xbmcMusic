package de.wingesas.android.xbmcMusic.json.methods;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.wingesas.android.xbmcMusic.data.FileSystemItem;
import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class FilesGetDirectory extends JsonMethod<List<FileSystemItem>> {

	// private static final String TAG = JsonFiles.class.getSimpleName();
	private String parentDirectory;

	public FilesGetDirectory(String parentDirectory) {
		this.parentDirectory = parentDirectory;
	}

	@Override
	protected String getMethod() {
		return parentDirectory == null ? "Files.GetSources" : "Files.GetDirectory";
	}

	@Override
	protected JSONObject getParams() throws JSONException {

		JSONObject params = new JSONObject();
		params.put("media", "music");

		if (parentDirectory != null)
			params.put("directory", parentDirectory);

		params.put("sort", new JSONObject().put("method", "file"));

		return params;
	}

	@Override
	public List<FileSystemItem> handleResult(String result) throws JSONException {

		JSONObject jsonResult = new JSONObject(result).getJSONObject("result");

		List<FileSystemItem> items = new ArrayList<FileSystemItem>();

		JSONArray array = null;
		if (jsonResult.has("sources"))
			array = jsonResult.getJSONArray("sources");
		else
			array = jsonResult.getJSONArray("files");

		for (int i = 0; i < array.length(); i++) {

			JSONObject object = array.getJSONObject(i);
			String file = object.getString("file");
			String label = object.getString("label");

			String filetype = null, type = null;

			if (object.has("filetype"))
				filetype = object.getString("filetype");

			if (object.has("type"))
				type = object.getString("type");

			if (filetype == null || (filetype != null && filetype.equals("directory")))
				items.add(new FileSystemItem(file, filetype, label, type, true));
			else
				items.add(new FileSystemItem(file, filetype, label, type, false));
		}

		return items.isEmpty() ? null : items;
	}
}
