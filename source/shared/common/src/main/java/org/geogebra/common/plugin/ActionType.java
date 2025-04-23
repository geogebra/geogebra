package org.geogebra.common.plugin;

/**
 * Types of actions that can be undone/redone
 * by an {@link org.geogebra.common.main.undo.ActionExecutor}.
 * Values overlap with {@link EventType}, which is for reporting only.
 */
public enum ActionType {
	REMOVE, ADD, UPDATE,
	CLEAR_PAGE, REMOVE_PAGE, ADD_PAGE, PASTE_PAGE, MOVE_PAGE, RENAME_PAGE,
	EMBEDDED_PRUNE_STATE_LIST, REDO, SET_CONTENT, UNDO,
	SPLIT_STROKE, MERGE_STROKE, UPDATE_ORDERING
}
