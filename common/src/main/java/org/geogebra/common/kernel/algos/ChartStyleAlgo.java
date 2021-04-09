package org.geogebra.common.kernel.algos;

import javax.annotation.Nonnull;

public interface ChartStyleAlgo {
	@Nonnull ChartStyle getStyle();

	int getIntervals();
}
