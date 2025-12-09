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

package org.geogebra.common.euclidian.plot.implicit;

/**
 * Bernstein polynomial plotter settings.
 */
public final class BernsteinPlotterSettings {
	private boolean visualDebug;
	private boolean updateEnabled;
	private int minCellSizeInPixels;

	/**
	 * Constructor with default settings.
	 */
	public BernsteinPlotterSettings() {
		this(true, false, 4);
	}

	/**
	 *
	 * @param updateEnabled if update enabled for the plotter (set false for debug only)
	 * @param visualDebug if plotter should display additional info like borders of the cells
	 * @param minCellSizeInPixels the smallest cell the algorithm splits cells into
	 */
	public BernsteinPlotterSettings(boolean updateEnabled, boolean visualDebug,
			int minCellSizeInPixels) {
		this.updateEnabled = updateEnabled;
		this.visualDebug = visualDebug;
		this.minCellSizeInPixels = minCellSizeInPixels;
	}

	boolean hasVisualDebug() {
		return visualDebug;
	}

	boolean isUpdateEnabled() {
		return updateEnabled;
	}

	int minCellSizeInPixels() {
		return minCellSizeInPixels;
	}
}
