package geogebra.gui.view.spreadsheet.statdialog;


public class StatPanelSettings extends PlotSettings{

	public static final int TYPE_COUNT = 0;
	public static final int TYPE_RELATIVE = 1;
	public static final int TYPE_NORMALIZED = 2;
	public int frequencyType = TYPE_COUNT;

	public int sourceType = StatDialog.SOURCE_RAWDATA;
	
	// histogram options
	public boolean isCumulative = false;
	public boolean useManualClasses = false;
	public boolean hasOverlayNormal = false;
	public boolean hasOverlayPolygon = false;
	public boolean showFrequencyTable = false;
	public boolean showHistogram = true;
	public boolean showScatterplotLine = false;

	public double classStart = 0;
	public double classWidth = 5;
	public int numClasses = 5;
	
	public double[] classBorders;
	public boolean isLeftRule = true;


	// graph options
	public boolean isAutomaticWindow = true;

	/*
		public boolean showGrid = false;
		public double xMin = 0;
		public double xMax = 10;
		public double xInterval = 1;
		public double yMin = -10;
		public double yMax = 0;
		public double yInterval = 1;


		public double xMinAuto = 0;
		public double xMaxAuto = 10;
		public double xIntervalAuto = 1;
		public double yMinAuto = -10;
		public double yMaxAuto = 0;
		public double yIntervalAuto = 1;
	 */

	//public PlotPanelEuclidianView plotPanel;

	public int stemAdjust = 0;



	public StatPanelSettings(){
		super();
	}

}


