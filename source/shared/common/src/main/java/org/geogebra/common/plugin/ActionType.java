package org.geogebra.common.plugin;

/**
 * Types of actions that can be undone/redone
 * by an {@link org.geogebra.common.main.undo.ActionExecutor}.
 * Values overlap with {@link EventType}, which is for reporting only.
 */
public enum ActionType {
	// actions affecting page content
	REMOVE, ADD, UPDATE, SPLIT_STROKE, MERGE_STROKE, UPDATE_ORDERING,
	EMBEDDED_PRUNE_STATE_LIST, REDO, SET_CONTENT, UNDO,
	// actions affecting whole pages
	CLEAR_PAGE, REMOVE_PAGE, ADD_PAGE, PASTE_PAGE,
	MOVE_PAGE, RENAME_PAGE;

	/**
	 * @return whether this action changes individual elements rather than whole pages
	 */
	public boolean isGeoElementAction() {
		return compareTo(CLEAR_PAGE) < 0;
	}
}
