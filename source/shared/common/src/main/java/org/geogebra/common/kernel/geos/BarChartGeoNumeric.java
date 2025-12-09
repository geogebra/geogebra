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

package org.geogebra.common.kernel.geos;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.ChartStyle;

public class BarChartGeoNumeric extends GeoNumeric implements ChartStyleGeo {
	private String toolTipText;
	private final ChartStyle chartStyle = new ChartStyle(null);

	private int intervals;

	public BarChartGeoNumeric(Construction cons) {
		super(cons);
	}

	@Override
	public String getTooltipText(final boolean colored,
			final boolean alwaysOn) {
		return toolTipText;
	}

	public void setToolTipText(String toolTipText) {
		this.toolTipText = toolTipText;
	}

	@Override
	public @Nonnull ChartStyle getStyle() {
		return chartStyle;
	}

	@Override
	public int getIntervals() {
		return intervals;
	}

	@Override
	public BarChartGeoNumeric copy() {
		BarChartGeoNumeric copy = new BarChartGeoNumeric(cons);
		copy.value = value;
		copy.intervals = intervals;
		copy.setDrawable(isDrawable, false);
		return copy;
	}

	public void setIntervals(int intervals) {
		this.intervals = intervals;
	}
}
