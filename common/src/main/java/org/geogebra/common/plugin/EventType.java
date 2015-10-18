package org.geogebra.common.plugin;

public enum EventType {
	CLICK("click"), UPDATE("update"), UPDATE_STYLE("updateStyle"), ADD("add"), STOREUNDO(
			"add"), REMOVE(
			"remove"), RENAME("rename"), RENAME_COMPLETE("renameComplete"), ADD_POLYGON(
			"addPolygon"), ADD_POLYGON_COMPLETE("addPolygonComplete"), MOVING_GEOS(
			"movingGeos"), MOVED_GEOS("movedGeos"), PASTE_ELMS("pasteElms"), PASTE_ELMS_COMPLETE(
			"pasteElmsComplete"), DELETE_GEOS("deleteGeos"), LOGIN("login"), SET_MODE(
			"setMode"), SHOW_NAVIGATION_BAR("showNavigationBar"), SHOW_STYLE_BAR(
			"showStyleBar"), PERSPECTIVE_CHANGE("perspectiveChange");

	private String eventName;

	EventType(String name) {
		this.eventName = name;
	}

	public String getName() {
		return eventName;
	}
}
