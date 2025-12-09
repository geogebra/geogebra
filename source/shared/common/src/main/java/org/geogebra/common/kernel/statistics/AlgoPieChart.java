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

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;

public class AlgoPieChart extends AlgoElement {
	private final GeoList data;
	private final GeoPieChart chart;
	private final GeoPoint center;
	private final GeoNumberValue radius;

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
			if (isValidValue(value)) {
				sum += value;
			} else if (value < 0) {
				chart.setUndefined();
				return;
			}
		}
		for (int i = 0; i < data.size(); i++) {
			double value = data.get(i).evaluateDouble();
			if (isValidValue(value)) {
				chart.getData().add(value / sum);
			}
		}
		chart.setCenter(new GPoint2D(center.getInhomX(), center.getInhomY()));
		if (radius != null) {
			chart.setRadius(radius.getDouble());
		}
		this.updateDefaultStyle();
	}

	private void updateDefaultStyle() {
		for (int i = 0; i < chart.getData().size() ; i++) {
			if (chart.getStyle().getBarColor(i + 1) != null) {
				continue;
			}
			int[] order = new int[]{0x6557d2, 0xe0bf00, 0x3bb4a6, 0xda6a9d, 0x3b1c32, 0xff8c70};
			GColor baseColor = GColor.newColorRGB(order[i % 6]);
			double overlay = Math.pow(0.6, Math.floor(i / 6.0));
			GColor color = GColor.mixColors(GColor.WHITE, baseColor, overlay, 255);
			chart.getStyle().setBarColor(color, i + 1);
		}
	}

	@Override
	public GetCommand getClassName() {
		return Commands.PieChart;
	}

	public GeoElement getChart() {
		return chart;
	}

	private boolean isValidValue(double value) {
		return Double.isFinite(value) && value >= 0;
	}
}
