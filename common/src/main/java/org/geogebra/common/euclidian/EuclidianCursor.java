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
	/** Axis resize x */
	RESIZE_Y,
	/** Axis resize x */
	RESIZE_Z,
	/** Axis resize z */
	RESIZE_NESW,
	/** nesw resize */
	RESIZE_NWSE,
	/** nwse resize */
	ERASER,
	/** Pen */
	PEN
}
