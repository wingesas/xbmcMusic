package de.wingesas.android.xbmcMusic.data;

public class ArtistItem extends MusicListItem {

	public ArtistItem(int artistid, String label) {
		super(artistid, label);
	}

	@Override
	public String getIdLabel() {
		return ARTIST_ID;
	}
	
	@Override
	public String toString() {
		return getLabel();
	}
}
