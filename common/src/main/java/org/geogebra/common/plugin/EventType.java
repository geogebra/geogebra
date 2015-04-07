package org.geogebra.common.plugin;

public enum EventType {
	CLICK("click"), UPDATE("update"), ADD("add"), STOREUNDO("add"), REMOVE(
			"remove"), RENAME("rename"), RENAME_COMPLETE("renameComplete"), ADD_POLYGON(
			"addPolygon"), ADD_POLYGON_COMPLETE("addPolygonComplete"), MOVING_GEOS(
			"movingGeos"), MOVED_GEOS("movedGeos"), PASTE_ELMS("pasteElms"), PASTE_ELMS_COMPLETE(
			"pasteElmsComplete"), DELETE_GEOS("deleteGeos"), LOGIN("login");

	private String eventName;

	EventType(String name) {
		this.eventName = name;
	}

	public String getName() {
		return eventName;
	}
}
