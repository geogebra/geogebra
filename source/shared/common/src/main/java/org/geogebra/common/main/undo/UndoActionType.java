package org.geogebra.common.main.undo;

public enum UndoActionType {
	/** Action affects the style XML of all objects */
	STYLE,
	/** For inline texts the action affects content, for others style XML */
	STYLE_OR_CONTENT,
	/** For tables affects content, for other objects style */
	STYLE_OR_TABLE_CONTENT
}
