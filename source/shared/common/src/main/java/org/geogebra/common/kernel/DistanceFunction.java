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

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Evaluates distance between f(T) and distant point for parametric curve f
 *
 */
public interface DistanceFunction extends UnivariateFunction {

	@Override
	double value(double pathParam);

	/**
	 * @param p
	 *            distant point
	 */
	void setDistantPoint(GeoPointND p);

}
