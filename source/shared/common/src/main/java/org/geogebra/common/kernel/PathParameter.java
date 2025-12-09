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
