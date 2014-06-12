package de.wingesas.android.xbmcMusic.data;

public class FileSystemItem extends MusicListItem {

	private String filetype;
	private String type;
	

	public FileSystemItem(String file, String filetype, String label, String type, boolean directory) {
		super(label);
		this.file = file;
		this.filetype = filetype;
		this.type = type;
		this.directory = directory;
	}	

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return getLabel();
	}

	@Override
	public String getIdLabel() {
		return FILE;
	}
}
