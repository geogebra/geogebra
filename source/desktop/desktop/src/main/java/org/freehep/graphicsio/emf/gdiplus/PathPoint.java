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

package org.freehep.graphicsio.emf.gdiplus;

public class PathPoint {

	public static final int TYPE_INVALID = -1; // extra: invalid type
	public static final int TYPE_START = 0; // move
	public static final int TYPE_LINE = 1; // line
	public static final int TYPE_BEZIER = 3; // default Bezier (= cubic Bezier)
	public static final int TYPE_PATH_TYPE_MASK = 0x07; // type mask (lowest 3
														// bits).
	public static final int TYPE_DASH_MODE = 0x10; // currently in dash mode.
	public static final int TYPE_PATH_MARKER = 0x20; // a marker for the path.
	public static final int TYPE_CLOSE_SUBPATH = 0x80; // closed flag

	private float x, y;
	private int type;

	public PathPoint() {
		this.type = TYPE_INVALID;
		this.x = 0;
		this.y = 0;
	}

	public PathPoint(int type, double x, double y) {
		this(type, (float) x, (float) y);
	}

	public PathPoint(int type, float x, float y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getX() {
		return x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getY() {
		return y;
	}
}
