package de.wingesas.android.xbmcMusic.json.methods;

import org.json.JSONException;
import org.json.JSONObject;

import de.wingesas.android.xbmcMusic.data.MusicListItem;
import de.wingesas.android.xbmcMusic.json.JsonMethod;

public class PlaylistAdd extends JsonMethod<Boolean> {

	private MusicListItem musicItem;
	private int playlistid = -1;

	public PlaylistAdd(MusicListItem musicItem) {
		this.musicItem = musicItem;
	}

	public PlaylistAdd(MusicListItem musicItem, int playlistid) {
		this(musicItem);
		this.playlistid = playlistid;
	}

	@Override
	protected String getMethod() {
		return "Playlist.Add";
	}

	@Override
	protected JSONObject getParams() throws Exception {

		if (playlistid == -1)
			playlistid = new PlaylistGetAudioPlaylist().execute();

		JSONObject params = new JSONObject();
		params.put("playlistid", playlistid);

		JSONObject object = new JSONObject();

		if (musicItem.isFile())
			object.put(musicItem.getIdLabel(), musicItem.getFile());
		else
			object.put(musicItem.getIdLabel(), musicItem.getId());

		params.put("item", object);

		return params;
	}

	@Override
	public Boolean handleResult(String result) throws JSONException {
		JSONObject jsonResult = new JSONObject(result);
		return jsonResult.getString("result").equals("OK");
	}
}
