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

package org.geogebra.common.kernel.matrix;

/**
 * Used to find the nearest point of a given point.
 * 
 * @author Mathieu
 *
 */
public class CoordNearest {

	private Coords point;
	private double currentDistance;
	private Coords currentNearest;

	/**
	 * 
	 * @param point
	 *            reference point
	 */
	public CoordNearest(Coords point) {
		this.point = point;
		currentDistance = Double.POSITIVE_INFINITY;
		currentNearest = new Coords(2);
	}

	/**
	 * check if point p is nearer than current
	 * 
	 * @param p
	 *            point
	 * @return true if p is nearer to reference point
	 */
	public boolean check(Coords p) {
		double distance = p.distance(point);
		if (distance < currentDistance) {
			currentDistance = distance;
			currentNearest.set2(p);
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @return nearest point
	 */
	public Coords get() {
		return currentNearest;
	}

}
