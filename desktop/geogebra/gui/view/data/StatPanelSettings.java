package geogebra.gui.view.data;

import geogebra.gui.view.data.DataVariable.GroupType;


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
	protected boolean isCumulative = false;
	protected boolean useManualClasses = false;
	protected boolean hasOverlayNormal = false;
	protected boolean hasOverlayPolygon = false;
	protected boolean showFrequencyTable = false;
	protected boolean showHistogram = true;
	protected boolean showScatterplotLine = false;
	protected boolean showOutliers = true;

	protected double classStart = 0;
	protected double classWidth = 5;
	protected int numClasses = 5;

	protected boolean isLeftRule = true;

	// bar chart options
	protected double barWidth = 0.5;
	protected boolean isAutomaticBarWidth = true;

	// graph options
	protected boolean isAutomaticWindow = true;

	// stemplot options
	protected int stemAdjust = 0;
	

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

	public GroupType groupType() {
		return dataSource.getGroupType();
	}

	public boolean isNumericData() {
		return dataSource.isNumericData();
	}
	
	public boolean isPointList() {
		return dataSource.isPointData();
	}

}
