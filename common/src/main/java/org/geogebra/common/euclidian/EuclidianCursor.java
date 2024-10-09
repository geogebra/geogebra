package org.geogebra.common.euclidian;

/**
 * Cursor types for web and desktop
 */
public enum EuclidianCursor {
	/** Hit (arrow) */
	HIT,
	/** Cross */
	DEFAULT,
	/** Hand */
	DRAG,
	/** Arrow */
	MOVE,
	/** invisible, eg. for pen */
	TRANSPARENT,
	/** Axis resize x */
	RESIZE_X,
	/** Axis resize y */
	RESIZE_Y,
	/** Axis resize z */
	RESIZE_Z,
	/** nesw resize */
	RESIZE_NESW,
	/** nwse resize */
	RESIZE_NWSE,
	/** ew resize */
	RESIZE_EW,
	/** ns resize */
	RESIZE_NS,
	/** Crosshair */
	CROSSHAIR,
	/** Eraser */
	ERASER,
	/** Pen */
	PEN,
	/** Highlighter */
	HIGHLIGHTER,
	/** Rotation */
	ROTATION,
	/** Mindmap */
	MINDMAP,
	/** Table */
	TABLE,
	/** Text */
	TEXT
}
