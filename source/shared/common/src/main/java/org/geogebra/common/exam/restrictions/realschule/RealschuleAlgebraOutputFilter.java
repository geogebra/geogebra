package org.geogebra.common.exam.restrictions.realschule;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.view.algebra.filter.AlgebraOutputFilter;
import org.geogebra.common.kernel.kernelND.GeoElementND;

public final class RealschuleAlgebraOutputFilter implements AlgebraOutputFilter {

	private final @CheckForNull AlgebraOutputFilter wrappedFilter;

	public RealschuleAlgebraOutputFilter(@CheckForNull AlgebraOutputFilter wrappedFilter) {
		this.wrappedFilter = wrappedFilter;
	}

	@Override
	public boolean isAllowed(GeoElementND element) {
		if (element == null) {
			return false;
		}
		if (!Realschule.isCalculatedEquationAllowed(element)) {
			return false;
		}
		if (wrappedFilter != null) {
			return wrappedFilter.isAllowed(element);
		}
		return true;
	}
}