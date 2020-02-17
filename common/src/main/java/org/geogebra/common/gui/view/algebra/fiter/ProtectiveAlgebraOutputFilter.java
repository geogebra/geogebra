package org.geogebra.common.gui.view.algebra.fiter;

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
