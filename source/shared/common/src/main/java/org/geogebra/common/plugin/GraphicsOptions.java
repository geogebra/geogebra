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

package org.geogebra.common.plugin;

import java.util.function.Consumer;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings;

public class GraphicsOptions {

	/**
	 * @param es settings
	 * @param opts output options
	 * @param app app
	 */
	public static void apply(EuclidianSettings es, JsObjectWrapper opts, App app) {
		opts.ifIntPropertySet("rightAngleStyle", app::setRightAngleStyle);
		es.beginBatch();
		opts.ifIntPropertySet("pointCapturing", es::setPointCapturing);
		opts.ifPropertySet("grid", es::showGrid);
		opts.ifPropertySet("gridIsBold", es::setGridIsBold);
		opts.ifIntPropertySet("gridType", es::setGridType);
		opts.ifPropertySet("bgColor",
				(Consumer<String>) val3 -> es.setBackground(GColor.parseHexColor(val3)));
		opts.ifPropertySet("gridColor",
				(Consumer<String>) val2 -> es.setGridColor(GColor.parseHexColor(val2)));
		opts.ifPropertySet("axesColor",
				(Consumer<String>) val2 -> es.setAxesColor(GColor.parseHexColor(val2)));
		opts.ifIntPropertySet("rulerType", es::setRulerType);
		opts.ifObjectPropertySet("axes", axes -> {
			for (char axis = 'x'; axis <= 'z'; axis++) {
				final int axisNo = axis - 'x';
				axes.ifObjectPropertySet(String.valueOf(axis),
						axisOptions -> setAxisOptions(axisNo, axisOptions, es));
			}
		});

		opts.ifObjectPropertySet("gridDistance", distances ->
				setGridDistances(distances, es));
	}

	private static void setGridDistances(JsObjectWrapper distanceOptions,
			EuclidianSettings es) {

		if (isDistanceAutomatic(distanceOptions)) {
			es.setAutomaticGridDistance(true, true);
			return;
		}

		double[] distances = new double[]{
				getDistanceOption(distanceOptions, "x", 0),
				getDistanceOption(distanceOptions, "y", 0),
				getDistanceOption(distanceOptions, "theta", Math.PI / 6)
		};

		if (distances[0] > 0 && distances[1] > 0) {
			es.setGridDistances(distances);
		}
	}

	private static double getDistanceOption(JsObjectWrapper distanceOptions, String name,
			double defaultValue) {
		Object xValue = distanceOptions.getValue(name);
		return xValue != null
				? ((Number) xValue).doubleValue()
				: defaultValue;
	}

	private static boolean isDistanceAutomatic(JsObjectWrapper opts) {
		return opts.getValue("x") == null && opts.getValue("y") == null;
	}

	private static void setAxisOptions(int axisNo, JsObjectWrapper axisOptions,
			EuclidianSettings es) {
		axisOptions.ifPropertySet("visible",
				(Consumer<Boolean>) val -> es.setShowAxis(axisNo, val));
		axisOptions.ifPropertySet("positiveAxis",
				(Consumer<Boolean>) val -> es.setPositiveAxis(axisNo, val));
		axisOptions.ifPropertySet("showNumbers",
				(Consumer<Boolean>) val -> es.setShowAxisNumbers(axisNo, val));
		axisOptions.ifIntPropertySet("tickStyle", val -> es.setAxisTickStyle(axisNo, val));
		axisOptions.ifPropertySet("label",
				(Consumer<String>) val -> es.setAxisLabel(axisNo, val));
		axisOptions.ifPropertySet("unitLabel",
				(Consumer<String>) val -> es.setAxisUnitLabel(axisNo, val));
	}
}
