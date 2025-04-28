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
