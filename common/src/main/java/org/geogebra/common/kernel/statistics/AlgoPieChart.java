package org.geogebra.common.kernel.statistics;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.ChartStyle;
import org.geogebra.common.kernel.algos.ChartStyleAlgo;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;

public class AlgoPieChart extends AlgoElement implements ChartStyleAlgo {
	private final GeoList data;
	private final GeoPieChart chart;
	private final GeoPoint center;
	private final GeoNumberValue radius;
	private final ChartStyle chartStyle = new ChartStyle(new int[]{0x6557d2, 0xe0bf00,
			0x3bb4a6, 0xda6a9d, 0x3b1c32, 0xff8c70});

	/**
	 * @param cons construction
	 * @param data data
	 * @param center center of the circle
	 * @param radius circle radius
	 */
	public AlgoPieChart(Construction cons, GeoList data, GeoPoint center, GeoNumberValue radius) {
		super(cons);
		this.data = data;
		this.chart = new GeoPieChart(cons);
		this.center = center == null ? new GeoPoint(cons, 0, 0, 1) : center;
		this.radius = radius;
		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		input = radius == null ? new GeoElement[] {data, center}
			: new GeoElement[] {data, center, radius.toGeoElement()};
		setOnlyOutput(chart);
		setDependencies();
	}

	@Override
	public void compute() {
		if (isUndefined()) {
			chart.setUndefined();
			return;
		}
		chart.getData().clear();
		double sum = 0;
		for (int i = 0; i < data.size(); i++) {
			double value = data.get(i).evaluateDouble();
			if (MyDouble.isFinite(value) && value >= 0) {
				sum += value;
				chart.getData().add(value);
			} else {
				chart.setUndefined();
				return;
			}
		}
		for (int i = 0; i < data.size(); i++) {
			chart.getData().set(i, chart.getData().get(i) / sum);
		}
		chart.setCenter(new GPoint2D(center.getInhomX(), center.getInhomY()));
		if (radius != null) {
			chart.setRadius(radius.getDouble());
		}
		this.updateDefaultStyle();
	}

	private void updateDefaultStyle() {
		for (int i = 0; i < data.size() ; i++) {
			if (chartStyle.getBarColor(i + 1) != null) {
				continue;
			}
			int[] order = new int[]{0x6557d2, 0xe0bf00, 0x3bb4a6, 0xda6a9d, 0x3b1c32, 0xff8c70};
			GColor baseColor = GColor.newColorRGB(order[i % 6]);
			double overlay = Math.pow(0.6, Math.floor(i / 6.0));
			GColor color = GColor.mixColors(GColor.WHITE, baseColor, overlay, 255);
			chartStyle.setBarColor(color, i + 1);
		}
	}

	@Override
	public GetCommand getClassName() {
		return Commands.PieChart;
	}

	public GeoElement getChart() {
		return chart;
	}

	@Override
	public ChartStyle getStyle() {
		return chartStyle;
	}

	@Override
	public int getIntervals() {
		return data.size();
	}
}
