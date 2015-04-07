package org.geogebra.common.geogebra3D.archimedean.support;

import org.geogebra.common.kernel.Matrix.Coords;

/**
 * Simple class for expressing 3D points.
 * 
 * @author kasparianr
 * 
 */

public class Point extends Coords {
	public Point(double x, double y, double z) {
		super(x, y, z, 1.0);
	}

	public String toString() {
		return "(" + getX() + ", " + getY() + ", " + getZ() + ")";
	}

}
