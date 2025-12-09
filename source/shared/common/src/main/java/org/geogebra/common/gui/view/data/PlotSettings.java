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

package org.geogebra.common.gui.view.data;

import org.geogebra.common.plugin.EuclidianStyleConstants;

public class PlotSettings {

	public double xMin = -10;
	public double xMax = 10;
	public double xAxesInterval = 1;
	public boolean xAxesIntervalAuto = true;
	public double yMin = -10;
	public double yMax = 10;
	public double yAxesInterval = 1;
	public boolean yAxesIntervalAuto = true;

	public double[] gridInterval = { 1, 1 };
	public boolean gridIntervalAuto = true;

	public int pointCaptureStyle = EuclidianStyleConstants.POINT_CAPTURING_OFF;

	public boolean showYAxis = false;
	public boolean showXAxis = true;
	public boolean showArrows = false;
	public boolean forceXAxisBuffer = false;
	public boolean[] isEdgeAxis = { false, false };
	public boolean[] isPositiveOnly = { false, false };
	public boolean showGrid = false;

	public boolean logXAxis = false;
	public boolean logYAxis = false;

	/**
	 * Default constructor
	 */
	public PlotSettings() {

	}

	/**
	 * Partial default constructor
	 * 
	 * @param xMinEV
	 *            x-min for graphics
	 * @param xMaxEV
	 *            x-max for graphics
	 * @param yMinEV
	 *            y-min for graphics
	 * @param yMaxEV
	 *            y-max for graphics
	 * @param showYAxis
	 *            whether to show y axis
	 * @param showArrows
	 *            whether to show arrows
	 * @param forceXAxisBuffer
	 *            whether to force margin below the x-axis to make labels
	 *            visible
	 * @param isEdgeAxis
	 *            whether to stick axes to the edge
	 */
	public PlotSettings(double xMinEV, double xMaxEV, double yMinEV,
			double yMaxEV, boolean showYAxis, boolean showArrows,
			boolean forceXAxisBuffer, boolean[] isEdgeAxis) {
		this.xMin = xMinEV;
		this.xMax = xMaxEV;
		this.yMin = yMinEV;
		this.yMax = yMaxEV;
		this.showYAxis = showYAxis;
		this.showArrows = showArrows;
		this.forceXAxisBuffer = forceXAxisBuffer;
		this.isEdgeAxis = isEdgeAxis;
	}

}