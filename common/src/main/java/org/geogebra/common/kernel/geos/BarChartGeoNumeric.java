package org.geogebra.common.kernel.geos;

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
	public ChartStyle getStyle() {
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
