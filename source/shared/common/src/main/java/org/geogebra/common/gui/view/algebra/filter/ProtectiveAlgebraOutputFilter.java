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

package org.geogebra.common.gui.view.algebra.filter;

import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Filters the output on the Algebra View
 */
public class ProtectiveAlgebraOutputFilter implements AlgebraOutputFilter {

    private FunctionAndEquationFilter functionAndEquationFilter = new FunctionAndEquationFilter();

	/**
	 * Checks whether the geo element's output is allowed.
	 * @param geoElement geo element
	 * @return True if the geo element's output can be shown, otherwise false.
	 */
	@Override
	public boolean isAllowed(GeoElementND geoElement) {
		return functionAndEquationFilter.isAllowed(geoElement);
    }
}
