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
 * Modes of moving elements in graphics view.
 */
public enum MoveMode {
	NONE,
	POINT,
	VIEW,
	ROTATE_VIEW,
	PLANE,
	LINE,
	CONIC,
	VECTOR,
	VECTOR_STARTPOINT,
	FUNCTION,
	LABEL,
	TEXT,
	NUMERIC,
	SLIDER,
	IMAGE,
	ROTATE,
	DEPENDENT,
	MULTIPLE_OBJECTS,
	X_AXIS,
	Y_AXIS,
	BOOLEAN,
	WIDGET,
	VECTOR_NO_GRID,
	POINT_WITH_OFFSET,
	FREEHAND,
	ATTACH_DETACH,
	IMPLICIT_CURVE,
	Z_AXIS,
	AUDIO_SLIDER,
}
