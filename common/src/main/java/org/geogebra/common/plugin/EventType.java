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

	/** After object was redefined */
	REDEFINE("redefine"),

	/** RENAME_COMPLETE */
	RENAME_COMPLETE("renameComplete"),

	/** Start batch of add events */
	BATCH_ADD_STARTED("batchAddStarted"),

	/** Finish batch of add events */
	BATCH_ADD_COMPLETE("batchAddComplete"),

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

	/** new page (whiteboard) */
	ADD_PAGE("addPage"),

	/** remove page (whiteboard) */
	REMOVE_PAGE("removePage"),

	/** rename page (whiteboard) */
	RENAME_PAGE("renamePage"),

	/** duplicate page (whiteboard): source index, target ID, source ID */
	PASTE_PAGE("pastePage"),

	/** Move page: sourceindex, target index */
	MOVE_PAGE("movePage"),

	/** Clear page: page ID */
	CLEAR_PAGE("clearPage"),

	/** select page */
	SELECT_PAGE("selectPage"),

	/** Key typed in editor */
	EDITOR_KEY_TYPED("editorKeyTyped"),
	/** Editing started for an object (or new input) */
	EDITOR_START("editorStart"),
	/** Editing stopped for an object (or new input) */
	EDITOR_STOP("editorStop"),
	/** Undoable event happened in external object */
	EMBEDDED_STORE_UNDO("embeddedStoreUndo"),

	/** Embed finished loading **/
	EMBED_LOADED("embedLoaded"),
	/** Algebra Panel selected */
	ALGEBRA_PANEL_SELECTED("algebraPanelSelected"),

	/** Spreadsheet panel selected */
	SPREADSHEET_PANEL_SELECTED("spreadsheetPanelSelected"),
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
	SHOW_POINTS_TV("showPointsTV"),

	/** prevent selection of element by other users */
	LOCK_TEXT_ELEMENT("lockTextElement"),

	/** unlock text element for other users */
	UNLOCK_TEXT_ELEMENT("unlockTextElement"),

	/**
	 * Toolbar settings changed
	 */
	TOOLBAR_CHANGED("toolbarChanged"),

	/**
	 *  Switch between calculators
	 */
	SWITCH_CALC("switchCalculator"),

	/**
	 * View properties changed, such as background color or axes settings
	 */
	VIEW_PROPERTIES_CHANGED("viewPropertiesChanged"), LOAD_PAGE("loadPage"),
	/** Spotlight hidden */
	HIDE_SPOTLIGHT("hideSpotlight");

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
