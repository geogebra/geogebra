package geogebra.common.plugin;

public enum EventType {
	CLICK("click"),
	UPDATE("update");
	
	private String eventName;

	EventType(String name) {
		this.eventName = name;
	}
	
	public String getName() {
		return eventName;
	}
}
