package org.geogebra.common.exam.restrictions.wtr;

import javax.annotation.Nullable;

import org.geogebra.common.gui.view.algebra.filter.AlgebraOutputFilter;
import org.geogebra.common.kernel.kernelND.GeoElementND;

public class WtrAlgebraOutputFilter implements AlgebraOutputFilter {
	private final @Nullable AlgebraOutputFilter wrappedFilter;
	private final AlgebraConversionFilter conversionFilter;

	/**
	 * @param wrappedFilter parent filter
	 */
	public WtrAlgebraOutputFilter(@Nullable AlgebraOutputFilter wrappedFilter) {
		this.wrappedFilter = wrappedFilter;
		this.conversionFilter = new AlgebraConversionFilter();
	}

	@Override
	public boolean isAllowed(GeoElementND element) {
		if (element == null) {
			return false;
		}
		if (!conversionFilter.isAllowed(element)) {
			return false;
		}
		if (wrappedFilter != null) {
			return wrappedFilter.isAllowed(element);
		}
		return true;
	}
}
