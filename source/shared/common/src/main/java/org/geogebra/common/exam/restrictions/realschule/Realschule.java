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

package org.geogebra.common.exam.restrictions.realschule;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.statistics.AlgoFitImplicit;
import org.geogebra.common.kernel.statistics.AlgoFitLineX;
import org.geogebra.common.kernel.statistics.AlgoFitLineY;
import org.geogebra.common.kernel.statistics.FitAlgo;

/**
 * Shared logic for Realschule exam restrictions.
 */
public class Realschule {

	static boolean isCalculatedEquationAllowed(@CheckForNull GeoElementND element) {
		if (element == null) {
			return false;
		}
		if (isAllowedFitCommand(element.getParentAlgorithm())) {
			return true;
		}
		if ((element.isGeoLine()
				|| element.isGeoRay()
				|| element.isGeoConic()
				|| element.isGeoFunction()
				|| element.isImplicitEquation())
				&& (element.getParentAlgorithm() != null)) {
			return false;
		}
		return true;
	}

	private static boolean isAllowedFitCommand(@CheckForNull AlgoElement algo) {
		if (algo == null) {
			return false;
		}
		return algo instanceof FitAlgo || algo instanceof AlgoFitLineX
				|| algo instanceof AlgoFitLineY || algo instanceof AlgoFitImplicit;
	}
}