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