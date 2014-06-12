package de.wingesas.android.xbmcMusic.data;

public class AlbumItem extends MusicListItem {
	private int year;

	public AlbumItem(int albumid, String label, String artist, int year) {
		super(albumid, label);
		this.artist = artist;
		this.setYear(year);
	}

	@Override
	public String getIdLabel() {
		return ALBUM_ID;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
	
	@Override
	public String toString() {
		return getArtist() + " " + getYear() + " " + getLabel();
	}
}
