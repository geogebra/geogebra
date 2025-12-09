/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
	/** Arrow cross */
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
	TEXT,
	/** Indicates grabbing is possible */
	GRAB,
	/** Grabbing is ongoing */
	GRABBING,
	/** Zoom in (desktop only) */
	ZOOM_IN,
	/** Zoom out (desktop only) */
	ZOOM_OUT
}
