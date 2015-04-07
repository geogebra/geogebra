/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel;

/**
 * @author Markus Hohenwarter
 */
public class PathParameter {
	/** parameter value */
	public double t;
	private int pathType = -1;

	// private boolean isDefined; //tells if parameters have been fed with
	// "real" numbers

	/**
	 * Creates new path parameter
	 */
	public PathParameter() {
		t = Double.NaN;
	}

	/**
	 * Copy constructor
	 * 
	 * @param pp
	 *            path parameter to copy
	 */
	public PathParameter(PathParameter pp) {
		set(pp);
	}

	/**
	 * @param t
	 *            value of parameter
	 */
	public PathParameter(double t) {
		this.t = t;
	}

	/**
	 * @param pp
	 *            path parameter to copy
	 */
	final public void set(PathParameter pp) {
		t = pp.t;
		pathType = pp.pathType;
	}

	/**
	 * @return path type (for conics conic type)
	 */
	public final int getPathType() {
		return pathType;
	}

	/**
	 * @param pathType
	 *            new path type (for conics conic type)
	 */
	public final void setPathType(int pathType) {
		this.pathType = pathType;
	}

	/**
	 * @return value of parameter
	 */
	public final double getT() {
		return t;
	}

	/**
	 * @param t
	 *            new value of parameter
	 */
	public final void setT(double t) {
		if (Double.isNaN(t)) {
			// isDefined=false;
			return;
		}

		this.t = t;
		// isDefined=true;
	}

}
