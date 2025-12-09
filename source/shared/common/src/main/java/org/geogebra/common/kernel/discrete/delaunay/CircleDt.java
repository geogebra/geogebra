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

package org.geogebra.common.kernel.discrete.delaunay;

/**
 * this class represents a simple circle. <br>
 * it is used by the Delaunay Triangulation class. <br>
 * <br>
 * note that this class is immutable.
 * 
 * @see DelaunayTriangulation
 */
public class CircleDt {

	private PointDt c;
	private double r;

	/**
	 * Constructor. <br>
	 * Constructs a new Circle_dt.
	 * 
	 * @param c
	 *            Center of the circle.
	 * @param r
	 *            Radius of the circle.
	 */
	public CircleDt(PointDt c, double r) {
		this.c = c;
		this.r = r;
	}

	/**
	 * Copy Constructor. <br>
	 * Creates a new Circle with same properties of <code>circ</code>.
	 * 
	 * @param circ
	 *            Circle to clone.
	 */
	public CircleDt(CircleDt circ) {
		this.c = circ.c;
		this.r = circ.r;
	}

	@Override
	public String toString() {
		return " Circle[" + c.toString() + "|" + r + "|"
				+ (int) Math.round(Math.sqrt(r)) + "]";
	}

	/**
	 * Gets the center of the circle.
	 * 
	 * @return the center of the circle.
	 */
	public PointDt center() {
		return this.c;
	}

	/**
	 * Gets the radius of the circle.
	 * 
	 * @return the radius of the circle.
	 */
	public double radius() {
		return this.r;
	}
}
