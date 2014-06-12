package de.wingesas.android.xbmcMusic.data;

public class SongItem extends MusicListItem {
	private int duration;
	private String album;

	public SongItem(int songid, String label, String artist, int duration) {
		super(songid, label);
		this.artist = artist;
		this.setDuration(duration);
	}

	@Override
	public String getIdLabel() {
		return SONG_ID;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	@Override
	public String toString() {
		return getLabel() + " " + getAlbum() + " " + getArtist();
	}
}
