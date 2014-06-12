package de.wingesas.android.xbmcMusic.data;


public abstract class MusicListItem {

	public static final String ARTIST_ID = "artistid";
	public static final String ALBUM_ID = "albumid";
	public static final String SONG_ID = "songid";
	public static final String GENRE_ID = "genreid";
	public static final String FILE = "file";
	public static final String DIRECTORY = "directory";
	public static final String PLAYLIST_ID = "playlistid";

	protected int id;
	protected String artist;
	protected String label;
	protected String file;
	protected boolean directory;

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public final String getFile() {
		return file;
	}

	public final void setFile(String file) {
		this.file = file;
	}

	public MusicListItem(String label) {
		this.label = label;
	}

	public MusicListItem(int id, String label) {
		this(label);
		this.id = id;
	}
	
	public MusicListItem(int id) {
		this.id = id;
	}

	public final int getId() {
		return this.id;
	}

	public abstract String getIdLabel();

	public final boolean isFile() {
		return this instanceof FileSystemItem;
	}

	public final String getLabel() {
		return label;
	}

	public final void setLabel(String label) {
		this.label = label;
	}

	public final String getArtist() {
		return artist;
	}

	public final void setArtist(String artist) {
		this.artist = artist;
	}
}
