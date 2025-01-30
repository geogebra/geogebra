package org.geogebra.common.exam.restrictions.realschule;

import javax.annotation.Nullable;

import org.geogebra.common.gui.view.algebra.fiter.AlgebraOutputFilter;
import org.geogebra.common.kernel.kernelND.GeoElementND;

public final class RealschuleAlgebraOutputFilter implements AlgebraOutputFilter {

	private final @Nullable AlgebraOutputFilter wrappedFilter;

	public RealschuleAlgebraOutputFilter(@Nullable AlgebraOutputFilter wrappedFilter) {
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