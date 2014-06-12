package de.wingesas.android.xbmcMusic.data;

public class GenreItem extends MusicListItem {

	public GenreItem(int id, String label) {
		super(id, label);
	}

	@Override
	public String getIdLabel() {
		return GENRE_ID;
	}
	
	@Override
	public String toString() {
		return getLabel();
	}
}
