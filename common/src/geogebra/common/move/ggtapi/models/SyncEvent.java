package geogebra.common.move.ggtapi.models;

public class SyncEvent {

	private int id;
	private long timestamp;

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

}
