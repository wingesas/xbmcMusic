package de.wingesas.android.xbmcMusic.data;

public class PlaylistItem extends MusicListItem {
	private int duration;
	private boolean currentlyPlayed=false;
	
	public PlaylistItem(int id) {
		super(id);
	}
	
	public PlaylistItem(int id, String label) {
		super(id, label);
	}

	@Override
	public String getIdLabel() {
		return PLAYLIST_ID;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public boolean isCurrentlyPlayed() {
		return currentlyPlayed;
	}

	public void setCurrentlyPlayed(boolean currentlyPlayed) {
		this.currentlyPlayed = currentlyPlayed;
	}
}
