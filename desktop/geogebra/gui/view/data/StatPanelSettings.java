package geogebra.gui.view.data;

/**
 * @author G. Sturr
 * 
 *         Settings for DataAnalysisView displays
 * 
 */
public class StatPanelSettings extends PlotSettings {

	public DataSource dataSource;

	// histogram types
	public static final int TYPE_COUNT = 0;
	public static final int TYPE_RELATIVE = 1;
	public static final int TYPE_NORMALIZED = 2;
	public int frequencyType = TYPE_COUNT;

	// histogram options
	public boolean isCumulative = false;
	public boolean useManualClasses = false;
	public boolean hasOverlayNormal = false;
	public boolean hasOverlayPolygon = false;
	public boolean showFrequencyTable = false;
	public boolean showHistogram = true;
	public boolean showScatterplotLine = false;
	public boolean showOutliers = true;

	public double classStart = 0;
	public double classWidth = 5;
	public int numClasses = 5;

	public double[] classBorders;
	public boolean isLeftRule = true;

	// bar chart options
	public double barWidth = 0.5;
	public boolean isAutomaticBarWidth = true;

	// graph options
	public boolean isAutomaticWindow = true;

	// stemplot options
	public int stemAdjust = 0;

	/**************************************************
	 * Construct StatPanelSettings
	 */
	public StatPanelSettings() {
		super();
	}

	// ==================================================
	// Getters/Setters
	// ==================================================

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public int sourceType() {
		return dataSource.getSourceType();
	}

	public boolean isNumericData() {
		return dataSource.isNumericData();
	}

}
