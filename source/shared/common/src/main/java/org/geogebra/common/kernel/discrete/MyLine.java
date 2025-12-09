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

package org.geogebra.common.kernel.discrete;

import org.geogebra.common.awt.GPoint2D;

public class MyLine {
	public GPoint2D p1;
	public GPoint2D p2;

	/**
	 * @param p1
	 *            start point
	 * @param p2
	 *            end point
	 */
	public MyLine(GPoint2D p1, GPoint2D p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	/**
	 * @return squared length
	 */
	public double lengthSquared() {
		return GPoint2D.distanceSq(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

}
