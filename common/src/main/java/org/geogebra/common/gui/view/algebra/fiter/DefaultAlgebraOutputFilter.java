package org.geogebra.common.gui.view.algebra.fiter;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Allows every kind of output row.
 */
public class DefaultAlgebraOutputFilter implements AlgebraOutputFilter {

	@Override
	public boolean isAllowed(GeoElement element) {
		return true;
	}
}
