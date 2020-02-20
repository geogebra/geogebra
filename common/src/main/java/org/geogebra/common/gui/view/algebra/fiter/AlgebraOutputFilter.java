package org.geogebra.common.gui.view.algebra.fiter;

import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Filter for the algebra output row.
 */
public interface AlgebraOutputFilter {

	/**
	 * Whether the output row is allowed for the GeoElement
	 * @param element the element for which we check whether we're allowed to show the output row
	 * @return True if the output row is allowed for the element, otherwise false.
	 */
	boolean isAllowed(GeoElementND element);
}
