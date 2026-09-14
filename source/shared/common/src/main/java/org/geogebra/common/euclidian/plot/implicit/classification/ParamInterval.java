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

package org.geogebra.common.euclidian.plot.implicit.classification;

/**
 * Closed parameter interval on a contour segment.
 */
public final class ParamInterval {
	private final double startT;
	private final double endT;

	/**
	 * @param startT start parameter
	 * @param endT end parameter
	 */
	public ParamInterval(double startT, double endT) {
		this.startT = startT;
		this.endT = endT;
	}

	public double getStartT() {
		return startT;
	}

	public double getEndT() {
		return endT;
	}
}
