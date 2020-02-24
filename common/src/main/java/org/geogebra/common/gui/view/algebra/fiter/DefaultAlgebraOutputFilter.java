package org.geogebra.common.gui.view.algebra.fiter;

import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Allows every kind of output row.
 */
public class DefaultAlgebraOutputFilter implements AlgebraOutputFilter {

	@Override
	public boolean isAllowed(GeoElementND element) {
		return true;
	}
}
