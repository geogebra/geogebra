package geogebra.common.move.ggtapi.models;

public class SyncEvent {

	private int id;
	private long timestamp;
	private boolean delete;
	private boolean unfavorite;

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

}
