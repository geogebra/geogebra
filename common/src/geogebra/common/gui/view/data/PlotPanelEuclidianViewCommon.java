package geogebra.common.gui.view.data;


/**
 * @author gabor
 * Common things used by plotPanelEuclidianViewsW/D
 *
 */
public class PlotPanelEuclidianViewCommon {
	/** Plot panel viewID. This is not a constant; it is assigned by GuiManager. */
	private int viewID;
	/**
	 * Flag to determine if the mouse is over the drag region, a thin rectangle
	 * at the top of the panel
	 */
	private boolean overDragRegion;
	/** Settings to control EuclidianView features (e.g. axes visibility) */
	private PlotSettings plotSettings;
	public static boolean showGrid = false;
	public static boolean[] showAxes = { true, true };

	/**
	 * @param overDragRegion
	 * 
	 * Constructor
	 */
	public PlotPanelEuclidianViewCommon(boolean overDragRegion) {
		this.overDragRegion = overDragRegion;
	}

	public int getViewID() {
		return viewID;
	}

	public void setViewID(int viewID) {
		this.viewID = viewID;
	}

	public boolean isOverDragRegion() {
		return overDragRegion;
	}

	public void setOverDragRegion(boolean overDragRegion) {
		this.overDragRegion = overDragRegion;
	}

	public PlotSettings getPlotSettings() {
		return plotSettings;
	}

	public void setPlotSettings(PlotSettings plotSettings) {
		this.plotSettings = plotSettings;
	}
}