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

import org.geogebra.common.kernel.cas.AlgoDependentCasCell;
import org.geogebra.common.kernel.cas.AlgoTangentCurve;
import org.geogebra.common.kernel.cas.AlgoTangentFunctionPoint;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Algo dispatcher for CAS algos
 *
 */
public class KernelCAS {

	/**
	 * GeoCasCell dependent on other variables, e.g. m := c + 3
	 * 
	 * @param geoCasCell
	 *            the dependent cell
	 * 
	 * @return resulting casCell created using geoCasCell.copy().
	 */
	final public static GeoCasCell dependentCasCell(GeoCasCell geoCasCell) {
		AlgoDependentCasCell algo = new AlgoDependentCasCell(geoCasCell);
		return algo.getCasCell();
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param P
	 *            point
	 * @param f
	 *            function
	 * @return tangent to function through point
	 */
	public static GeoLine tangent(Construction cons, String label, GeoPointND P,
			GeoFunctionable f) {
		AlgoTangentFunctionPoint algo = new AlgoTangentFunctionPoint(cons,
				label, P, f);
		GeoLine t = algo.getTangent();
		t.setToExplicitForm();
		t.update();
		return t;
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param P
	 *            point
	 * @param f
	 *            curve
	 * @return tangent to curve through point
	 */
	public static GeoLine tangent(Construction cons, String label, GeoPointND P,
			GeoCurveCartesian f) {
		AlgoTangentCurve algo = new AlgoTangentCurve(cons, label, P, f);
		GeoLine t = algo.getTangent();
		t.setToExplicitForm();
		t.update();
		return t;
	}

}
