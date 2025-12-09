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

package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

/**
 * Represents a simple bounds rectangle with x and y min and max.
 */
public class BoundsRectangle {

	private final double xmin;
	private final double xmax;
	private final double ymin;
	private final double ymax;

	/**
	 *
	 * @param xmin x minimum
	 * @param xmax x maximum
	 * @param ymin y minimum
	 * @param ymax y maximum
	 */
	public BoundsRectangle(double xmin, double xmax, double ymin, double ymax) {
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
	}

	public BoundsRectangle(EuclidianViewBounds bounds) {
		this(bounds.getXmin(), bounds.getXmax(), bounds.getYmin(), bounds.getYmax());
	}

	public double getXmin() {
		return xmin;
	}

	public double getXmax() {
		return xmax;
	}

	public double getYmin() {
		return ymin;
	}

	public double getYmax() {
		return ymax;
	}

	@Override
	public String toString() {
		return "BoundsRectangle{"
				+ "xmin=" + xmin
				+ ", xmax=" + xmax
				+ ", ymin=" + ymin
				+ ", ymax=" + ymax
				+ '}';
	}
}
