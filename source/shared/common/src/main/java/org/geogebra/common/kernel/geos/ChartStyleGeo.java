package org.geogebra.common.kernel.geos;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.algos.ChartStyle;

/**
 * Chart construction elements with stylable parts.
 */
public interface ChartStyleGeo {
	/**
	 * @return style information for all individual parts
	 */
	@Nonnull ChartStyle getStyle();

	/**
	 * @return number of parts
	 */
	int getIntervals();
}
