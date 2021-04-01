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

	/** when an undo point is created */
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

	/** SESSION_EXPIRED */
	SESSION_EXPIRED("sessionExpired"),

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

	/** eg when the user presses the Undo button */
	UNDO("undo"),

	/** eg when the user presses the redo button */
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

	/** rename slide (whiteboard) */
	RENAME_SLIDE("renameSlide"),

	/** duplicate slide (whiteboard): source index, target ID, source ID */
	PASTE_SLIDE("pasteSlide"),

	/** Move slide: sourceindex, target index */
	MOVE_SLIDE("moveSlide"),

	/** Clear slide: slide ID */
	CLEAR_SLIDE("clearSlide"),

	/** select slide */
	SELECT_SLIDE("selectSlide"),

	/** Key typed in editor */
	EDITOR_KEY_TYPED("editorKeyTyped"),
	/** Editing started for an object (or new input) */
	EDITOR_START("editorStart"),
	/** Editing stopped for an object (or new input) */
	EDITOR_STOP("editorStop"),
	/** Undoable event happened in external object */
	EMBEDDED_STORE_UNDO("embeddedStoreUndo"),
	/** Prune state list in external object */
	EMBEDDED_PRUNE_STATE_LIST("embeddedPruneStateList"),

	/**
	 * Change the whole content (base64) of embedded applet
	 */
	EMBEDDED_CONTENT_CHANGED("embeddedContentChanged"),

	/** Algebra Panel selected */
	ALGEBRA_PANEL_SELECTED("algebraPanelSelected"),
	/** Tools Panel selected */
	TOOLS_PANEL_SELECTED("toolsPanelSelected"),
	/** Table Panel selected */
	TABLE_PANEL_SELECTED("tablePanelSelected"),
	/** Side Panel opened */
	SIDE_PANEL_OPENED("sidePanelOpened"),
	/** Side Panel closed */
	SIDE_PANEL_CLOSED("sidePanelClosed"),

	/** 2D view changed (panned, zoomed, axis scale changed) */
	VIEW_CHANGED_2D("viewChanged2D"),
	/** 3D view changed (panned, zoomed, rotated, axis scale changed) */
	VIEW_CHANGED_3D("viewChanged3D"),

	/** mouse down or touch start event */
	MOUSE_DOWN("mouseDown"),
	/** i.e. mouse up, touch end, finished dragging a point, or segment etc. */
	DRAG_END("dragEnd"),

	/**
	 * keyboard opened by the user
	 */
	OPEN_KEYBOARD("openKeyboard"),
	/**
	 * keyboard closed by the user
	 */
	CLOSE_KEYBOARD("closeKeyboard"),

	/** start animation event */
	START_ANIMATION("startAnimation"),

	/** stop animation event */
	STOP_ANIMATION("stopAnimation"),

	/** dropdown opened */
	DROPDOWN_OPENED("dropdownOpened"),

	/** dropdown closed */
	DROPDOWN_CLOSED("dropdownClosed"),

	/** dropdown item focused */
	DROPDOWN_ITEM_FOCUSED("dropdownItemFocused"),

	/** Layer change in notes*/
	ORDERING_CHANGE("orderingChange"),

	GROUP_OBJECTS("groupObjects"),

	UNGROUP_OBJECTS("ungroupObjects"),

	ADD_TV("addGeoToTV"),

	REMOVE_TV("removeGeoFromTV"),

	/** min, max and step */
	SET_VALUES_TV("setValuesOfTV"),

	/** column, true if show, false otherwise */
	SHOW_POINTS_TV("showPointsTV");

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
