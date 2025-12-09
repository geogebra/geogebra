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

package org.geogebra.common.euclidian;

import org.geogebra.common.kernel.Kernel;

/**
 * Hosts the euclidian views.
 */
public interface EuclidianHost {

	/**
	 * @return euclidian view; if not present yet, new one is created
	 */
	EuclidianView createEuclidianView();

	/**
	 * @return active euclidian view (may be EV, EV2 or 3D)
	 */
	EuclidianView getActiveEuclidianView();

	/**
	 * @return whether EV2 was initialized
	 */
	boolean hasEuclidianView2EitherShowingOrNot(int idx);

	/**
	 * @return whether EV2 is visible
	 */
	boolean isShowingEuclidianView2(int idx);

	/**
	 * @param kernel kernel
	 * @return euclidian controller
	 */
	EuclidianController newEuclidianController(Kernel kernel);

	/**
	 * @return DrawEquation instance
	 */
	DrawEquation getDrawEquation();
}
