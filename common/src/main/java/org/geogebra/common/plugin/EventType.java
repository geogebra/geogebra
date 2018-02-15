package org.geogebra.common.plugin;

/**
 * Type of event for clientListener / updateListener etc.
 *
 */
public enum EventType {
	/** CLICK */
	CLICK("click"),

	/** UPDATE */
	UPDATE("update"),

	/** UPDATE_STYLE */
	UPDATE_STYLE("updateStyle"),

	/** ADD */
	ADD("add"),

	/** STOREUNDO */
	STOREUNDO("storeUndo"),

	/** REMOVE */
	REMOVE("remove"),

	/** RENAME */
	RENAME("rename"),

	/** RENAME_COMPLETE */
	RENAME_COMPLETE("renameComplete"),

	/** ADD_POLYGON */
	ADD_POLYGON("addPolygon"),

	/** ADD_POLYGON_COMPLETE */
	ADD_POLYGON_COMPLETE("addPolygonComplete"),

	/** MOVING_GEOS */
	MOVING_GEOS("movingGeos"),

	/** MOVED_GEOS */
	MOVED_GEOS("movedGeos"),

	/** PASTE_ELMS */
	PASTE_ELMS("pasteElms"),

	/** PASTE_ELMS_COMPLETE */
	PASTE_ELMS_COMPLETE("pasteElmsComplete"),

	/** DELETE_GEOS */
	DELETE_GEOS("deleteGeos"),

	/** LOGIN */
	LOGIN("login"),

	/** SET_MODE */
	SET_MODE("setMode"),

	/** SHOW_NAVIGATION_BAR */
	SHOW_NAVIGATION_BAR("showNavigationBar"),

	/** SHOW_STYLE_BAR */
	SHOW_STYLE_BAR("showStyleBar"),

	/** OPEN_MENU */
	OPEN_MENU("openMenu"),

	/** PERSPECTIVE_CHANGE */
	PERSPECTIVE_CHANGE("perspectiveChange"),

	/**
	 * RELATION_TOOL // called when the user uses the Relation Tool (send the
	 * text from the dialog)
	 */
	RELATION_TOOL("relationTool"),

	/** SELECT */
	SELECT("select"),

	/** DESELECT */
	DESELECT("deselect"),

	/** UNDO */
	UNDO("undo"),

	/** REDO */
	REDO("redo"),

	/** EXPORT */
	EXPORT("export"),

	/** OPEN_DIALOG */
	OPEN_DIALOG("openDialog"),
	/** Custom tool created */
	ADD_MACRO("addMacro"),
	/** Custom tool removed */
	REMOVE_MACRO("removeMacro"),
	/** Custom tool renamed */
	RENAME_MACRO("renameMacro"),
	/** construction cleared */
	CLEAR("clear"),
	/** new slide (whiteboard) */
	ADD_SLIDE("addSlide"),
	/** remove slide (whiteboard) */
	REMOVE_SLIDE("removeSlide"),
	/** duplicate slide (whiteboard) */
	DUPLICATE_SLIDE("duplicateSlide"), MOVE_SLIDE("moveSlide");

	private String eventName;

	EventType(String name) {
		this.eventName = name;
	}

	/**
	 * @return event name
	 */
	public String getName() {
		return eventName;
	}
}
