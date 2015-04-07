package org.geogebra.common.move.ggtapi.models;

public class SyncEvent {

	private int id;
	private long timestamp;
	private boolean delete;
	private boolean unfavorite;
	private boolean favorite;
	private boolean zapped;

	public SyncEvent(int id, long timestamp) {
		this.id = id;
		this.timestamp = timestamp;
	}

	public int getID() {
		return id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public boolean isDelete() {
		return this.delete;
	}

	public boolean isUnfavorite() {
		return this.unfavorite;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public void setUnfavorite(boolean unfavorite) {
		this.unfavorite = unfavorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public boolean isFavorite() {
		return this.favorite;
	}

	public boolean isZapped() {
		return zapped;
	}

	public void setZapped(boolean zapped) {
		this.zapped = zapped;
	}

}
