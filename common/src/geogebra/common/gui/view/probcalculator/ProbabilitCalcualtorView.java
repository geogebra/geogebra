package geogebra.common.gui.view.probcalculator;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.main.settings.ProbabilityCalculatorSettings.DIST;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author gabor
 * 
 * Commmon view for ProbabilityCalculator
 *
 */
public abstract class ProbabilitCalcualtorView implements View {
	
	/**
	 * Application
	 */
	protected App app;
	/**
	 * Kernel
	 */
	protected Kernel kernel;
	/**
	 * Localization
	 */
	protected Localization loc;
	/**
	 * Construction
	 */
	protected Construction cons;
	
	/**
	 * @param app Application
	 */
	
	// enable/disable integral ---- use for testing
	protected boolean hasIntegral = true;
	
	// selected distribution mode
	protected DIST selectedDist = DIST.NORMAL; // default: startup with normal
													// distribution

		// distribution fields
	protected String[][] parameterLabels;
	protected final static int maxParameterCount = 3; // maximum number of
														// parameters allowed for a
														// distribution
	protected double[] parameters;
	protected boolean isCumulative = false;

	// maps for the distribution ComboBox
	protected HashMap<DIST, String> distributionMap;
	protected HashMap<String, DIST> reverseDistributionMap;

	// GeoElements
	protected ArrayList<GeoElement> plotGeoList;
	protected GeoPoint lowPoint, highPoint, curvePoint;
	protected GeoElement densityCurve, integral, ySegment, xSegment,
				discreteGraph, discreteIntervalGraph, normalOverlay;
	protected GeoList discreteValueList, discreteProbList, intervalProbList,
				intervalValueList;
		// private GeoList parmList;
	protected ArrayList<GeoElement> pointList;
	
	// initing
		protected boolean isIniting;
		protected boolean isSettingAxisPoints = false;

		// probability calculation modes
		public static final int PROB_INTERVAL = 0;
		public static final int PROB_LEFT = 1;
		public static final int PROB_RIGHT = 2;
		protected int probMode = PROB_INTERVAL;

		// interval values
		protected double low = 0, high = 1;

		// current probability result
		protected double probability;

		// rounding
		protected int printDecimals = 4, printFigures = -1;

		// flags
		protected boolean validProb;
		protected boolean showProbGeos = true;
		protected boolean showNormalOverlay = false;
		
		protected static final float opacityIntegral = 0.5f;
		protected static final float opacityDiscrete = 0.0f; // entire bar chart
		protected static final float opacityDiscreteInterval = 0.5f; // bar chart
																	// interval
		protected static final int thicknessCurve = 4;
		protected static final int thicknessBarChart = 3;

		protected boolean removeFromConstruction = true;

		protected static final double nearlyOne = 1 - 1E-6;

		// discrete graph types
		public static final int GRAPH_BAR = 0;
		public static final int GRAPH_LINE = 1;
		public static final int GRAPH_STEP = 2;
		protected int graphTypePDF = GRAPH_BAR;
		protected int graphTypeCDF = GRAPH_STEP;
		protected int graphType = GRAPH_BAR;

	
	public ProbabilitCalcualtorView(App app) {
		this.app = app;
		this.loc = app.getLocalization();
		kernel = app.getKernel();
		cons = kernel.getConstruction();
	}
	
	/**
	 * Returns the maximum value in the discrete value list.
	 * 
	 * @return
	 */
	public int getDiscreteXMax() {
		if (discreteValueList != null) {
			GeoNumeric geo = (GeoNumeric) discreteValueList
					.get(discreteValueList.size() - 1);
			return (int) geo.getDouble();
		}
		return -1;
	}

	/**
	 * Returns the minimum value in the discrete value list.
	 * 
	 * @return
	 */
	public int getDiscreteXMin() {
		if (discreteValueList != null) {
			GeoNumeric geo = (GeoNumeric) discreteValueList.get(0);
			return (int) geo.getDouble();
		}
		return -1;
	}
	

	public void add(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void remove(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void rename(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void update(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void repaintView() {
		// TODO Auto-generated method stub

	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	public void clearView() {
		// TODO Auto-generated method stub

	}

	public void setMode(int mode, ModeSetter m) {
		// TODO Auto-generated method stub

	}

	public int getViewID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isShowing() {
		// TODO Auto-generated method stub
		return false;
	}

}
