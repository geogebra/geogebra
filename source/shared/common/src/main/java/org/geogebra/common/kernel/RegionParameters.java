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

import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author Mathieu Blossier
 */
public class RegionParameters {

	private double t1;
	private double t2;

	// private boolean isDefined; //tells if parameters have been fed with
	// "real" numbers

	private Coords normal; // normal on the region at this place

	/**
	 * says if the point is on the path defined by the frontier of the region
	 */
	private boolean isOnPath = false;
	private int regionType = GeoQuadricNDConstants.QUADRIC_NOT_CLASSIFIED;

	/**
	 * Creates new region parameters
	 */
	public RegionParameters() {
		this(Double.NaN, Double.NaN);
	}

	/**
	 * Creates new region parameters
	 * 
	 * @param t1
	 *            first parameter
	 * @param t2
	 *            second parameter
	 */
	private RegionParameters(double t1, double t2) {

		this.t1 = t1;
		this.t2 = t2;

		regionType = GeoQuadricNDConstants.QUADRIC_NOT_CLASSIFIED;

		normal = new Coords(0, 0, 1, 0); // z-direction by default

	}

	/**
	 * @param rp
	 *            copy parameters from given RegionParameters
	 */
	final public void set(RegionParameters rp) {
		setT1(rp.t1);
		setT2(rp.t2);
		this.regionType = rp.regionType;
	}

	/*
	 * void appendXML(StringBuilder sb) { // pathParameter sb.append(
	 * "\t<pathParameter val=\""); sb.append(t); if (branch > 0) { sb.append(
	 * "\" branch=\""); sb.append(branch); } if (pathType > -1) { sb.append(
	 * "\" type=\""); sb.append(pathType); } sb.append("\"/>\n"); }
	 */

	/**
	 * @return first parameter
	 */
	public final double getT1() {
		return t1;
	}

	/**
	 * @param t1
	 *            new first parameter
	 */
	public final void setT1(double t1) {
		if (Double.isNaN(t1)) {
			return;
		}
		this.t1 = t1;
	}

	/**
	 * @return second parameter
	 */
	public final double getT2() {
		return t2;
	}

	/**
	 * @param t2
	 *            new second parameter
	 */
	public final void setT2(double t2) {
		if (Double.isNaN(t2)) {
			return;
		}
		this.t2 = t2;
	}

	/**
	 * @param normal
	 *            normal to the region at this place
	 */
	public void setNormal(Coords normal) {
		this.normal = normal;
	}

	/**
	 * @return normal to the region at this place
	 */
	public Coords getNormal() {
		return this.normal;
	}

	// //////////////////////////////////
	// POINT ON PATH

	/**
	 * set if the point is on the path defined by the frontier of the region
	 * 
	 * @param isOnPath
	 *            true if the point is on the frontier
	 */
	public final void setIsOnPath(boolean isOnPath) {
		this.isOnPath = isOnPath;
	}

	/**
	 * says if the point in on the path defined by the frontier of the region
	 * 
	 * @return true if the point in on the path defined by the frontier of the
	 *         region
	 */
	public final boolean isOnPath() {
		return isOnPath;
	}

	/**
	 * 
	 * @return true if at least one of the parameters is NaN
	 */
	public boolean isNaN() {
		return Double.isNaN(t1) || Double.isNaN(t2);
	}

	/**
	 * @return region type (for quadrics)
	 */
	public final int getRegionType() {
		return regionType;
	}

	/**
	 * @param regionType
	 *            new region type (for quadrics)
	 */
	public final void setRegionType(int regionType) {
		this.regionType = regionType;
	}

}
