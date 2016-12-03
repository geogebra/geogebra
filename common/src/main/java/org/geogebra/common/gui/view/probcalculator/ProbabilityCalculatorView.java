package org.geogebra.common.gui.view.probcalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.data.PlotSettings;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.algos.AlgoBarChart;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.algos.AlgoDependentPoint;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.algos.AlgoListElement;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.algos.AlgoPolyLine;
import org.geogebra.common.kernel.algos.AlgoRayPointVector;
import org.geogebra.common.kernel.algos.AlgoSequence;
import org.geogebra.common.kernel.algos.AlgoStepGraph;
import org.geogebra.common.kernel.algos.AlgoStickGraph;
import org.geogebra.common.kernel.algos.AlgoTake;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.cas.AlgoIntegralDefinite;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoAxis;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.statistics.AlgoBinomialDist;
import org.geogebra.common.kernel.statistics.AlgoCauchyDF;
import org.geogebra.common.kernel.statistics.AlgoChiSquaredDF;
import org.geogebra.common.kernel.statistics.AlgoDistributionDF;
import org.geogebra.common.kernel.statistics.AlgoExponentialDF;
import org.geogebra.common.kernel.statistics.AlgoFDistributionDF;
import org.geogebra.common.kernel.statistics.AlgoGammaDF;
import org.geogebra.common.kernel.statistics.AlgoHyperGeometric;
import org.geogebra.common.kernel.statistics.AlgoInversePascal;
import org.geogebra.common.kernel.statistics.AlgoInversePoisson;
import org.geogebra.common.kernel.statistics.AlgoLogNormalDF;
import org.geogebra.common.kernel.statistics.AlgoLogisticDF;
import org.geogebra.common.kernel.statistics.AlgoNormalDF;
import org.geogebra.common.kernel.statistics.AlgoPascal;
import org.geogebra.common.kernel.statistics.AlgoPoisson;
import org.geogebra.common.kernel.statistics.AlgoTDistributionDF;
import org.geogebra.common.kernel.statistics.AlgoWeibullDF;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.DIST;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;

/**
 * @author gabor
 * 
 * Commmon view for ProbabilityCalculator
 *
 */
public abstract class ProbabilityCalculatorView implements View, SettingListener, SetLabels {
	
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

	// colors
	private static final GColor colorPDF() {
		return GeoGebraColorConstants.DARKBLUE;
	}

	private static final GColor COLOR_NORMAL_OVERLAY = GColor.RED;

	private static final GColor COLOR_PDF_FILL = GColor.BLUE;

	private static final GColor COLOR_POINT = GColor.BLACK;

	
	
	protected int defaultDividerSize;
	public EuclidianView plotPanel;
	
	protected ProbabilityTable table;
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
	protected ArrayList<GeoElementND> plotGeoList;
	protected GeoPoint lowPoint, highPoint, curvePoint;
	protected GeoElement densityCurve, integral, ySegment, xSegment,
			discreteIntervalGraph, normalOverlay;
	protected GeoElementND discreteGraph;
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
	private double low = 0;
	private double high = 1;

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
	
	protected PlotSettings plotSettings;
	
	protected ProbabilityManager probManager;
	protected GeoFunction pdfCurve;

	
	public ProbabilityCalculatorView(App app) {
		
		isIniting = true;
		
		this.app = app;
		this.loc = app.getLocalization();
		kernel = app.getKernel();
		cons = kernel.getConstruction();
		
		// Initialize settings and register listener
		app.getSettings().getProbCalcSettings().addListener(this);
		
		probManager = new ProbabilityManager(app, this);
		plotSettings = new PlotSettings();
		plotGeoList = new ArrayList<GeoElementND>();

	}
	
	protected void setLabelArrays() {

		distributionMap = probManager.getDistributionMap();
		reverseDistributionMap = probManager.getReverseDistributionMap();
		parameterLabels = ProbabilityManager.getParameterLabelArray(app
				.getLocalization());
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
	
	public void setGraphType(int type) {

		if (graphType == type)
			return;

		graphType = type;
		if (isCumulative)
			graphTypeCDF = type;
		else
			graphTypePDF = type;

		updateAll();
	}

	public int getGraphType() {
		return graphType;
	}

	public int getPrintDecimals() {
		return printDecimals;
	}

	public int getPrintFigures() {
		return printFigures;
	}
	
	public void setProbabilityCalculator(DIST distributionType,
			double[] parameters, boolean isCumulative) {

		this.selectedDist = distributionType;
		this.isCumulative = isCumulative;
		this.parameters = parameters;
		if (parameters == null)
			this.parameters = ProbabilityManager
					.getDefaultParameters(selectedDist);

		updateAll();
	}
	
	public PlotSettings getPlotSettings() {
		return plotSettings;
	}

	public void setPlotSettings(PlotSettings plotSettings) {
		this.plotSettings = plotSettings;
	}

	public boolean isShowNormalOverlay() {
		return showNormalOverlay;
	}

	public void setShowNormalOverlay(boolean showNormalOverlay) {
		this.showNormalOverlay = showNormalOverlay;
	}
	
	public abstract void updateAll();
	
// =================================================
	// Getters/Setters
	// =================================================

	public DIST getSelectedDist() {
		return selectedDist;
	}

	public double getLow() {
		return low;
	}

	public double getHigh() {
		return high;
	}

	public int getProbMode() {
		return probMode;
	}

	public boolean isCumulative() {
		return isCumulative;
	}
	
	// =================================================
	// Plotting
	// =================================================

	/**
	 * Creates the required GeoElements for the currently selected distribution
	 * type and parameters.
	 */
	protected void createGeoElements() {

		this.removeGeos();

		String expr;

		// create low point

		GeoAxis path = (GeoAxis) kernel.lookupLabel(loc.getPlain("xAxis"));

		AlgoPointOnPath algoLow = new AlgoPointOnPath(cons, path, 0d, 0d);
		cons.removeFromConstructionList(algoLow);

		lowPoint = (GeoPoint) algoLow.getOutput(0);

		lowPoint.setObjColor(COLOR_POINT);
		lowPoint.setPointSize(4);
		lowPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH);
		lowPoint.setLayer(5);
		plotGeoList.add(lowPoint);

		// create high point

		AlgoPointOnPath algoHigh = new AlgoPointOnPath(cons, path, 0d, 0d);
		cons.removeFromConstructionList(algoHigh);

		highPoint = (GeoPoint) algoHigh.getOutput(0);

		highPoint.setObjColor(COLOR_POINT);
		highPoint.setPointSize(4);
		highPoint
				.setPointStyle(EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH);
		highPoint.setLayer(5);
		plotGeoList.add(highPoint);

		pointList = new ArrayList<GeoElement>();
		pointList.add(lowPoint);
		pointList.add(highPoint);

		// Set the axis points so they are not equal. This needs to be done
		// before the integral geo is created.
		setXAxisPoints();

		if (probManager.isDiscrete(selectedDist)) {

			// discrete distribution
			// ====================================================

			// create discrete bar graph and associated lists
			createDiscreteLists();

			// create discrete graph
			// ============================

			if (graphType == GRAPH_STEP) {

				GeoBoolean t = new GeoBoolean(cons);
				t.setValue(true);

				AlgoStepGraph algoStepGraph = new AlgoStepGraph(cons,
						discreteValueList, discreteProbList, t);

				cons.removeFromConstructionList(algoStepGraph);
				discreteGraph = algoStepGraph.getOutput(0);

			} else {
				AlgoBarChart algoBarChart;
				if (graphType == GRAPH_LINE) {
					GeoNumberValue zeroWidth = new GeoNumeric(cons, 0);
					algoBarChart = new AlgoBarChart(cons, discreteValueList,
							discreteProbList, zeroWidth);
				} else {
					GeoNumberValue oneWidth = new GeoNumeric(cons, 1);
					algoBarChart = new AlgoBarChart(cons, discreteValueList,
							discreteProbList, oneWidth);
				}
				cons.removeFromConstructionList(algoBarChart);
				discreteGraph = algoBarChart.getOutput(0);

			}

			discreteGraph.setObjColor(colorPDF());
			discreteGraph.setAlphaValue(opacityDiscrete);
			discreteGraph.setLineThickness(thicknessBarChart);
			discreteGraph.setLayer(1);
			discreteGraph.setFixed(true);
			discreteGraph.setSelectionAllowed(false);
			discreteGraph.setEuclidianVisible(true);
			plotGeoList.add(discreteGraph);

			// ============================
			// create lists for the discrete interval graph

			// Use Take[] to create a subset of the full discrete graph:
			// Take[discreteList, x(lowPoint) + offset, x(highPoint) + offset]
			//
			// The offset accounts for Sequence[] starting its count at 1 and
			// the starting value of the discrete x list. Thus,
			// offset = 1 - lowest discrete x value

			double firstX = ((GeoNumeric) discreteValueList.get(0)).getDouble();
			MyDouble offset = new MyDouble(kernel, 1d - firstX + 0.5);

			ExpressionNode low = new ExpressionNode(kernel, lowPoint,
					Operation.XCOORD, null);
			ExpressionNode high = new ExpressionNode(kernel, highPoint,
					Operation.XCOORD, null);
			ExpressionNode lowPlusOffset = new ExpressionNode(kernel, low,
					Operation.PLUS, offset);
			ExpressionNode highPlusOffset = new ExpressionNode(kernel, high,
					Operation.PLUS, offset);

			AlgoDependentNumber xLow;
			if (isCumulative)
				// for cumulative bar graphs we only show a single bar
				xLow = new AlgoDependentNumber(cons, highPlusOffset, false);
			else
				xLow = new AlgoDependentNumber(cons, lowPlusOffset, false);
			cons.removeFromConstructionList(xLow);

			AlgoDependentNumber xHigh = new AlgoDependentNumber(cons,
					highPlusOffset, false);
			cons.removeFromConstructionList(xHigh);

			AlgoTake take = new AlgoTake(cons, discreteValueList,
					(GeoNumeric) xLow.getOutput(0),
					(GeoNumeric) xHigh.getOutput(0));
			cons.removeFromConstructionList(take);
			intervalValueList = (GeoList) take.getOutput(0);

			AlgoTake take2 = new AlgoTake(cons, discreteProbList,
					(GeoNumeric) xLow.getOutput(0),
					(GeoNumeric) xHigh.getOutput(0));
			cons.removeFromConstructionList(take2);
			intervalProbList = (GeoList) take2.getOutput(0);

			// ============================
			// create the interval graph

			if (isCumulative) {
				GeoBoolean t = new GeoBoolean(cons);
				t.setValue(true);
				AlgoStickGraph algoStickGraph = new AlgoStickGraph(cons,
						intervalValueList, intervalProbList, t);
				cons.removeFromConstructionList(algoStickGraph);
				discreteIntervalGraph = algoStickGraph.getOutput(0);

			}

			else if (graphType == GRAPH_STEP) {
				GeoBoolean t = new GeoBoolean(cons);
				t.setValue(true);
				AlgoStepGraph algoStepGraph2 = new AlgoStepGraph(cons,
						intervalValueList, intervalProbList, t);
				cons.removeFromConstructionList(algoStepGraph2);
				discreteIntervalGraph = algoStepGraph2.getOutput(0);

			} else {
				AlgoBarChart barChart;
				if (graphType == GRAPH_LINE) {
					GeoNumberValue zeroWidth2 = new GeoNumeric(cons, 0d);
					barChart = new AlgoBarChart(cons, intervalValueList,
							intervalProbList, zeroWidth2);
				} else {
					GeoNumberValue oneWidth2 = new GeoNumeric(cons, 1);
					barChart = new AlgoBarChart(cons, intervalValueList,
							intervalProbList, oneWidth2);
				}
				discreteIntervalGraph = barChart.getOutput(0);
				cons.removeFromConstructionList(barChart);
			}

			if (isCumulative) {
				discreteIntervalGraph.setObjColor(GColor.RED);
				discreteIntervalGraph.setLineThickness(3);
				discreteIntervalGraph
						.setLineType(EuclidianStyleConstants.LINE_TYPE_FULL);
			} else if (graphType == GRAPH_LINE || graphType == GRAPH_STEP) {
				discreteIntervalGraph.setObjColor(COLOR_PDF_FILL);
				discreteIntervalGraph.setLineThickness(thicknessBarChart + 2);
			} else {
				discreteIntervalGraph.setObjColor(COLOR_PDF_FILL);
				discreteIntervalGraph.setAlphaValue(opacityDiscreteInterval);
				discreteIntervalGraph.setLineThickness(thicknessBarChart);
			}

			discreteIntervalGraph.setEuclidianVisible(showProbGeos);
			discreteIntervalGraph.setLayer(discreteGraph.getLayer() + 1);
			discreteIntervalGraph.setFixed(true);
			discreteIntervalGraph.setSelectionAllowed(false);
			discreteIntervalGraph.updateCascade();
			plotGeoList.add(discreteIntervalGraph);

			GeoLine axis = new GeoLine(cons);
			axis.setCoords(0, 1, 0);
			axis.setLayer(4);
			axis.setObjColor(app.getEuclidianView1().getAxesColor());
			axis.setLineThickness(discreteIntervalGraph.getLineThickness());
			axis.setFixed(true);
			axis.setSelectionAllowed(false);
			axis.updateCascade();
			plotGeoList.add(axis);

		} else {

			// continuous distribution
			// ====================================================

			// create density curve
			densityCurve = buildDensityCurveExpression(selectedDist ,isCumulative);
			if(isCumulative && (selectedDist == DIST.F || selectedDist == DIST.EXPONENTIAL)){
				pdfCurve = buildDensityCurveExpression(selectedDist ,false);
				cons.removeFromConstructionList(pdfCurve);
			}
			densityCurve.setObjColor(colorPDF());
			densityCurve.setLineThickness(thicknessCurve);
			densityCurve.setFixed(true);
			densityCurve.setSelectionAllowed(false);
			densityCurve.setEuclidianVisible(true);
			plotGeoList.add(densityCurve);

			if (hasIntegral) {
				GeoBoolean f = new GeoBoolean(cons);
				f.setValue(false);

				ExpressionNode low = new ExpressionNode(kernel, lowPoint,
						Operation.XCOORD, null);
				ExpressionNode high = new ExpressionNode(kernel, highPoint,
						Operation.XCOORD, null);

				AlgoDependentNumber xLow = new AlgoDependentNumber(cons, low,
						false);
				cons.removeFromConstructionList(xLow);
				AlgoDependentNumber xHigh = new AlgoDependentNumber(cons, high,
						false);
				cons.removeFromConstructionList(xHigh);

				AlgoIntegralDefinite algoIntegral = new AlgoIntegralDefinite(
						cons, (GeoFunction) densityCurve,
						(GeoNumberValue) xLow.getOutput(0),
						(GeoNumberValue) xHigh.getOutput(0), f);
				cons.removeFromConstructionList(algoIntegral);

				integral = algoIntegral.getOutput(0);
				integral.setObjColor(COLOR_PDF_FILL);
				integral.setAlphaValue(opacityIntegral);
				integral.setEuclidianVisible(showProbGeos);
				// make sure doesn't interfere with dragging of point
				integral.setSelectionAllowed(false);
				plotGeoList.add(integral);
			}

			if (isCumulative) {

				// point on curve
				GeoFunction f = (GeoFunction) densityCurve;
				ExpressionNode highPointX = new ExpressionNode(kernel,
						highPoint, Operation.XCOORD, null);
				ExpressionNode curveY = new ExpressionNode(kernel, f,
						Operation.FUNCTION, highPointX);

				MyVecNode curveVec = new MyVecNode(kernel, highPointX, curveY);
				ExpressionNode curvePointNode = new ExpressionNode(kernel,
						curveVec, Operation.NO_OPERATION, null);
				curvePointNode.setForcePoint();

				AlgoDependentPoint pAlgo = new AlgoDependentPoint(cons,
						curvePointNode, false);
				cons.removeFromConstructionList(pAlgo);

				curvePoint = (GeoPoint) pAlgo.getOutput(0);
				curvePoint.setObjColor(COLOR_POINT);
				curvePoint.setPointSize(4);
				curvePoint.setLayer(f.getLayer() + 1);
				curvePoint.setSelectionAllowed(false);
				plotGeoList.add(curvePoint);

				// create vertical line segment
				ExpressionNode xcoord = new ExpressionNode(kernel, curvePoint,
						Operation.XCOORD, null);
				MyVecNode vec = new MyVecNode(kernel, xcoord, new MyDouble(
						kernel, 0.0));
				ExpressionNode point = new ExpressionNode(kernel, vec,
						Operation.NO_OPERATION, null);
				point.setForcePoint();
				AlgoDependentPoint pointAlgo = new AlgoDependentPoint(cons,
						point, false);
				cons.removeFromConstructionList(pointAlgo);

				AlgoJoinPointsSegment seg1 = new AlgoJoinPointsSegment(cons,
						curvePoint, (GeoPoint) pointAlgo.getOutput(0),
						null, false);
				// cons.removeFromConstructionList(seg1);
				xSegment = seg1.getOutput(0);
				xSegment.setObjColor(GColor.BLUE);
				xSegment.setLineThickness(3);
				xSegment.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
				xSegment.setEuclidianVisible(showProbGeos);
				xSegment.setFixed(true);
				xSegment.setSelectionAllowed(false);
				plotGeoList.add(xSegment);

				// create horizontal ray
				ExpressionNode ycoord = new ExpressionNode(kernel, curvePoint,
						Operation.YCOORD, null);
				MyVecNode vecy = new MyVecNode(kernel,
						new MyDouble(kernel, 0.0), ycoord);
				ExpressionNode pointy = new ExpressionNode(kernel, vecy,
						Operation.NO_OPERATION, null);
				pointy.setForcePoint();
				GeoVector v = new GeoVector(cons);
				v.setCoords(-1d, 0d, 1d);

				AlgoRayPointVector seg2 = new AlgoRayPointVector(cons,
						curvePoint, v);
				cons.removeFromConstructionList(seg2);
				ySegment = seg2.getOutput(0);
				ySegment.setObjColor(GColor.RED);
				ySegment.setLineThickness(3);
				ySegment.setLineType(EuclidianStyleConstants.LINE_TYPE_FULL);
				ySegment.setEuclidianVisible(showProbGeos);
				ySegment.setFixed(true);
				ySegment.setSelectionAllowed(false);
				plotGeoList.add(ySegment);
			}

		}

		if (showNormalOverlay) {
			Double[] m = probManager.getDistributionMeasures(selectedDist,
					parameters);
			if (m[0] != null && m[1] != null) {
				normalOverlay = createNormalCurveOverlay(m[0], m[1]);
				plotGeoList.add(normalOverlay);
			}
		}

		hideAllGeosFromViews();
		// labelAllGeos();
		hideToolTips();

	}
	
	// =================================================
		// Geo Handlers
		// =================================================

	private GeoElementND createGeoFromString(String text,
				boolean suppressLabelCreation) {

			try {

				// create the geo
				// ================================
				boolean oldSuppressLabelMode = cons.isSuppressLabelsActive();
				if (suppressLabelCreation)
					cons.setSuppressLabelCreation(true);

				// workaround for eg CmdNormal -> always creates undo point
				boolean oldEnableUndo = cons.isUndoEnabled();
				cons.setUndoEnabled(false);

			GeoElementND[] geos = kernel.getAlgebraProcessor()
						.processAlgebraCommandNoExceptions(text, false);

				cons.setUndoEnabled(oldEnableUndo);

				if (suppressLabelCreation)
					cons.setSuppressLabelCreation(oldSuppressLabelMode);

				return geos[0];

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		private void hideAllGeosFromViews() {
		for (GeoElementND geo : plotGeoList) {
			hideGeoFromViews(geo.toGeoElement());
			}
		}

		private void hideGeoFromViews(GeoElement geo) {
			// add the geo to our view and remove it from EV
			geo.addView(plotPanel.getViewID());
			plotPanel.add(geo);
			geo.removeView(App.VIEW_EUCLIDIAN);
			app.getEuclidianView1().remove(geo);
		}

		private void hideToolTips() {
		for (GeoElementND geo : plotGeoList) {
				geo.setTooltipMode(GeoElement.TOOLTIP_OFF);
			}
		}


	/**
	 * Creates a step function for a discrete distribution.
	 * 
	 * @param xList
	 *            list with x values
	 * @param probList
	 *            list with y = P(x) values
	 * @return AlgoPolyLine implementation of a step function
	 */
	public AlgoPolyLine createStepFunction(GeoList xList, GeoList probList) {

		// Extract x/y coordinates from the lists.
		double[] xCoords = new double[xList.size()];
		double yCoords[] = new double[probList.size()];
		int n = yCoords.length;
		for (int i = 0; i < n; i++) {
			xCoords[i] = ((GeoNumeric) xList.get(i)).getDouble();
			yCoords[i] = ((GeoNumeric) probList.get(i)).getDouble();
		}

		// Create the PolyLine as:
		// (x0, P(x0)),
		// (x1, P(x0)), (x1, P(x1)),
		// (x2, P(x1)), (x2, P(x2)),
		// ...
		// (xn-1, P(xn-2), (xn-1, P(xn-1))

		GeoPointND[] points = new GeoPoint[2 * n - 1];
		boolean suppressLabelCreation = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// first point is special
		points[0] = new GeoPoint(cons, null, xCoords[0], yCoords[0], 1.0);

		// remaining points
		for (int i = 1; i < n; i++) {
			points[2 * i - 1] = new GeoPoint(cons, null, xCoords[i],
					yCoords[i - 1], 1.0);
			points[2 * i] = new GeoPoint(cons, null, xCoords[i], yCoords[i],
					1.0);
		}

		cons.setSuppressLabelCreation(suppressLabelCreation);

		// System.out.println("===============================================");
		// System.out.println("left border: " + Arrays.toString(xCoords));
		// System.out.println("yval: " + Arrays.toString(yCoords));
		// System.out.println("polyline points: " + Arrays.toString(points));

		AlgoPolyLine polyLine = new AlgoPolyLine(cons, points, false);

		return polyLine;
	}

	public GeoElement createNormalCurveOverlay(double mean, double sigma) {

		AlgoNormalDF algo = new AlgoNormalDF(cons, new GeoNumeric(cons, mean),
				new GeoNumeric(cons, sigma), new GeoBoolean(cons, isCumulative));
		cons.removeFromConstructionList(algo);

		GeoElement geo = algo.getResult();

		geo.setObjColor(COLOR_NORMAL_OVERLAY);
		geo.setLineThickness(thicknessCurve - 1);
		geo.setEuclidianVisible(true);
		geo.setFixed(true);
		geo.setSelectionAllowed(false);
		return geo;
	}
	
	/**
	 * Returns the appropriate plot dimensions for a given distribution and
	 * parameter set. Plot dimensions are returned as an array of double: {xMin,
	 * xMax, yMin, yMax}
	 */
	protected double[] getPlotDimensions() {

		return probManager.getPlotDimensions(selectedDist, parameters,
				pdfCurve == null ? densityCurve : pdfCurve, isCumulative);

	}

	

	// ============================================================
	// Number Format
	// ============================================================

	/**
	 * Formats a number string using local format settings.
	 */
	public String format(double x) {
		StringTemplate highPrecision;

		// override the default decimal place setting
		if (printDecimals >= 0) {
			int d = printDecimals < 4 ? 4 : printDecimals;
			highPrecision = StringTemplate.printDecimals(StringType.GEOGEBRA,
					d, false);
		} else {
			highPrecision = StringTemplate.printFigures(StringType.GEOGEBRA,
					printFigures, false);
		}
		// get the formatted string
		String result = kernel.format(x, highPrecision);

		return result;
	}

	/**
	 * Calculates and sets the plot dimensions, the axes intervals and the point
	 * capture style for the the currently selected distribution.
	 */
	public void updatePlotSettings() {

		double xMin, xMax, yMin, yMax;

		// get the plot window dimensions
		double[] d = getPlotDimensions();
		xMin = d[0];
		xMax = d[1];
		yMin = d[2];
		yMax = d[3];

		// System.out.println(d[0] + "," + d[1] + "," + d[2] + "," + d[3]);

		if (plotSettings == null)
			plotSettings = new PlotSettings();

		plotSettings.xMin = xMin;
		plotSettings.xMax = xMax;
		plotSettings.yMin = yMin;
		plotSettings.yMax = yMax;

		// axes
		// plotSettings.showYAxis = probManager.isDiscrete(selectedDist);
		plotSettings.showYAxis = isCumulative();

		plotSettings.isEdgeAxis[0] = false;
		plotSettings.isEdgeAxis[1] = true;
		plotSettings.forceXAxisBuffer = true;

		if (probManager.isDiscrete(selectedDist)) {
			// discrete axis points should jump from point to point
			plotSettings.pointCaptureStyle = EuclidianStyleConstants.POINT_CAPTURING_ON_GRID;
			// TODO --- need an adaptive setting here for when we have too many
			// intervals
			plotSettings.gridInterval[0] = 1;
			plotSettings.gridIntervalAuto = false;
			plotSettings.xAxesIntervalAuto = true;
		} else {
			plotSettings.pointCaptureStyle = EuclidianStyleConstants.POINT_CAPTURING_OFF;
			plotSettings.xAxesIntervalAuto = true;
			plotPanelUpdateSettings(plotSettings);
		}

		plotPanelUpdateSettings(plotSettings);

	}
	
	/**
	 * updates plot panel in subclasses
	 */
	protected abstract void plotPanelUpdateSettings(PlotSettings settings);

	/**
	 * Adjusts the interval control points to match the current low and high
	 * values. The low and high values are changeable from the input fields, so
	 * this method is called after a field change.
	 */
	public void setXAxisPoints() {

		isSettingAxisPoints = true;

		lowPoint.setCoords(getLow(), 0.0, 1.0);
		highPoint.setCoords(getHigh(), 0.0, 1.0);
		plotPanel.repaint();
		GeoElement.updateCascade(pointList, getTempSet(), false);
		tempSet.clear();

		if (probManager.isDiscrete(selectedDist))
			table.setSelectionByRowValue((int) getLow(), (int) getHigh());

		isSettingAxisPoints = false;
	}
	
	public void removeGeos() {
		if (pointList != null)
			pointList.clear();
		clearPlotGeoList();
		plotPanel.clearView();
	}
	
	/**
	 * Creates two GeoLists: discreteProbList and discreteValueList. These store
	 * the probabilities and values of the currently selected discrete
	 * distribution.
	 */
	private void createDiscreteLists() {

		ExpressionNode nPlusOne;
		AlgoDependentNumber plusOneAlgo;
		switch (selectedDist) {

		case BINOMIAL:

			/*
			 * n = "Element[" + parmList.getLabel() + ",1]"; p = "Element[" +
			 * parmList.getLabel() + ",2]";
			 * 
			 * expr = "Sequence[k,k,0," + n + "]"; discreteValueList = (GeoList)
			 * createGeoFromString(expr);
			 * 
			 * expr = "Sequence[BinomialDist[" + n + "," + p + ","; expr +=
			 * "Element[" + discreteValueList.getLabel() + ",k]," +
			 * isCumulative; expr += "],k,1," + n + "+ 1 ]";
			 * 
			 * //System.out.println(expr); discreteProbList = (GeoList)
			 * createGeoFromString(expr);
			 */

			GeoNumeric k = new GeoNumeric(cons);
			GeoNumeric k2 = new GeoNumeric(cons);
			GeoNumeric nGeo = new GeoNumeric(cons, parameters[0]);
			GeoNumeric nPlusOneGeo = new GeoNumeric(cons, parameters[0] + 1);
			GeoNumeric pGeo = new GeoNumeric(cons, parameters[1]);

			AlgoSequence algoSeq = new AlgoSequence(cons, k2, k2,
					new GeoNumeric(cons, 0.0), nGeo, null);
			discreteValueList = (GeoList) algoSeq.getOutput(0);

			AlgoListElement algo = new AlgoListElement(cons, discreteValueList,
					k);
			cons.removeFromConstructionList(algo);

			AlgoBinomialDist algo2 = new AlgoBinomialDist(cons,
					nGeo, pGeo, (GeoNumberValue) algo.getOutput(0),
					new GeoBoolean(
							cons, isCumulative));
			cons.removeFromConstructionList(algo2);

			AlgoSequence algoSeq2 = new AlgoSequence(cons,
					algo2.getOutput(0), k, new GeoNumeric(cons, 1.0),
					nPlusOneGeo, null);
			cons.removeFromConstructionList(algoSeq2);

			discreteProbList = (GeoList) algoSeq2.getOutput(0);

			break;

		case PASCAL:

			nGeo = new GeoNumeric(cons, parameters[0]);
			pGeo = new GeoNumeric(cons, parameters[1]);
			k = new GeoNumeric(cons);
			k2 = new GeoNumeric(cons);

			AlgoInversePascal n2 = new AlgoInversePascal(cons, nGeo, pGeo,
					new GeoNumeric(cons, nearlyOne));
			cons.removeFromConstructionList(n2);
			GeoElementND n2Geo = n2.getOutput(0);

			algoSeq = new AlgoSequence(cons, k, k, new GeoNumeric(cons, 0.0),
					(GeoNumberValue) n2Geo, null);
			removeFromAlgorithmList(algoSeq);
			discreteValueList = (GeoList) algoSeq.getOutput(0);

			algo = new AlgoListElement(cons, discreteValueList, k2);
			cons.removeFromConstructionList(algo);

			AlgoPascal algoPascal = new AlgoPascal(cons, nGeo, pGeo,
					(GeoNumberValue) algo.getOutput(0),
					new GeoBoolean(
							cons, isCumulative));
			cons.removeFromConstructionList(algoPascal);

			nPlusOne = new ExpressionNode(kernel, n2Geo, Operation.PLUS,
					new MyDouble(kernel, 1.0));
			plusOneAlgo = new AlgoDependentNumber(cons, nPlusOne, false);
			cons.removeFromConstructionList(plusOneAlgo);

			algoSeq2 = new AlgoSequence(cons, algoPascal.getOutput(0),
					k2,
					new GeoNumeric(cons, 1.0),
					(GeoNumberValue) plusOneAlgo.getOutput(0), null);
			cons.removeFromConstructionList(algoSeq2);

			discreteProbList = (GeoList) algoSeq2.getOutput(0);

			break;

		case POISSON:

			GeoNumeric meanGeo = new GeoNumeric(cons, parameters[0]);
			k = new GeoNumeric(cons);
			k2 = new GeoNumeric(cons);

			AlgoInversePoisson maxSequenceValue = new AlgoInversePoisson(cons,
					meanGeo, new GeoNumeric(cons, nearlyOne));
			cons.removeFromConstructionList(maxSequenceValue);
			GeoElement maxDiscreteGeo = maxSequenceValue.getOutput(0);

			algoSeq = new AlgoSequence(cons, k, k, new GeoNumeric(cons, 0.0),
					(GeoNumberValue) maxDiscreteGeo, null);
			removeFromAlgorithmList(algoSeq);
			discreteValueList = (GeoList) algoSeq.getOutput(0);

			algo = new AlgoListElement(cons, discreteValueList, k2);
			cons.removeFromConstructionList(algo);

			AlgoPoisson poisson = new AlgoPoisson(cons, meanGeo,
					(GeoNumberValue) algo.getOutput(0),
					new GeoBoolean(
							cons, isCumulative));
			cons.removeFromConstructionList(poisson);

			nPlusOne = new ExpressionNode(kernel, maxDiscreteGeo,
					Operation.PLUS, new MyDouble(kernel, 1.0));
			plusOneAlgo = new AlgoDependentNumber(cons, nPlusOne, false);
			cons.removeFromConstructionList(plusOneAlgo);

			algoSeq2 = new AlgoSequence(cons, poisson.getOutput(0), k2,
					new GeoNumeric(cons, 1.0),
					(GeoNumberValue) plusOneAlgo.getOutput(0), null);
			cons.removeFromConstructionList(algoSeq2);

			discreteProbList = (GeoList) algoSeq2.getOutput(0);

			break;

		case HYPERGEOMETRIC:
			/*
			 * p = "" + parameters[0]; // population size n = "" +
			 * parameters[1]; // n s = "" + parameters[2]; // sample size
			 * 
			 * expr = "Sequence[k,k,0," + n + "]"; discreteValueList = (GeoList)
			 * createGeoFromString(expr);
			 * 
			 * expr = "Sequence[HyperGeometric[" + p + "," + n + "," + s + ",";
			 * expr += "Element[" + discreteValueList.getLabel() + ",k]," +
			 * isCumulative; expr += "],k,1," + n + "+ 1 ]";
			 * 
			 * //System.out.println(expr); discreteProbList = (GeoList)
			 * createGeoFromString(expr);
			 */

			double p = parameters[0]; // population size
			double n = parameters[1]; // n
			double s = parameters[2]; // sample size

			// ================================================
			// interval bounds:
			// [ max(0, n + s - p) , min(n, s) ]
			// =================================================

			double lowBound = Math.max(0, n + s - p);
			double highBound = Math.min(n, s);
			double length = highBound - lowBound + 1;

			GeoNumeric lowGeo = new GeoNumeric(cons, lowBound);
			GeoNumeric highGeo = new GeoNumeric(cons, highBound);
			GeoNumeric lengthGeo = new GeoNumeric(cons, length);

			pGeo = new GeoNumeric(cons, p);
			nGeo = new GeoNumeric(cons, n);
			GeoNumeric sGeo = new GeoNumeric(cons, s);

			k = new GeoNumeric(cons);
			k2 = new GeoNumeric(cons);

			algoSeq = new AlgoSequence(cons, k, k, lowGeo, highGeo, null);
			removeFromAlgorithmList(algoSeq);
			discreteValueList = (GeoList) algoSeq.getOutput(0);

			algo = new AlgoListElement(cons, discreteValueList, k2);
			cons.removeFromConstructionList(algo);

			AlgoHyperGeometric hyperGeometric = new AlgoHyperGeometric(cons,
					pGeo, nGeo, sGeo, (GeoNumberValue) algo.getOutput(0),
					new GeoBoolean(cons, isCumulative));
			cons.removeFromConstructionList(hyperGeometric);

			algoSeq2 = new AlgoSequence(cons,
					hyperGeometric.getOutput(0), k2,
					new GeoNumeric(cons, 1.0), lengthGeo, null);
			cons.removeFromConstructionList(algoSeq2);
			discreteProbList = (GeoList) algoSeq2.getOutput(0);

			break;

		}

		plotGeoList.add(discreteProbList);
		discreteProbList.setEuclidianVisible(true);
		discreteProbList.setAuxiliaryObject(true);
		discreteProbList.setLabelVisible(false);
		discreteProbList.setFixed(true);
		discreteProbList.setSelectionAllowed(false);

		return;
	}


	private void clearPlotGeoList() {

		for (GeoElementND geo : plotGeoList) {
			if (geo != null) {
				geo.setFixed(false);
				geo.remove();
			}
		}
		plotGeoList.clear();
	}

	private TreeSet<AlgoElement> tempSet;

	private TreeSet<AlgoElement> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<AlgoElement>();
		}
		return tempSet;
	}
	
	/**
	 * Exports all GeoElements that are currently displayed in this panel to a
	 * target EuclidianView.
	 * 
	 * @param euclidianViewID
	 *            viewID of the target EuclidianView
	 */
	public void exportGeosToEV(int euclidianViewID) {

		app.setWaitCursor();
		ArrayList<GeoElementND> newGeoList = new ArrayList<GeoElementND>();
		String expr;

		try {
			app.storeUndoInfo();

			// some commented out code removed 2012-3-1

			// create low point
			expr = "Point[" + loc.getPlain("xAxis") + "]";
			GeoPoint lowPointCopy = (GeoPoint) createGeoFromString(expr, false);
			lowPointCopy.setVisualStyle(lowPoint);
			lowPointCopy.setLabelVisible(false);
			lowPointCopy.setCoords(getLow(), 0, 1);
			lowPointCopy.setLabel(null);
			newGeoList.add(lowPointCopy);

			// create high point
			GeoPoint highPointCopy = (GeoPoint) createGeoFromString(expr, false);
			highPointCopy.setVisualStyle(lowPoint);
			highPointCopy.setLabelVisible(false);
			highPointCopy.setCoords(getHigh(), 0, 1);
			highPointCopy.setLabel(null);
			newGeoList.add(highPointCopy);
			StringTemplate tpl = StringTemplate.maxPrecision;

			// create discrete bar charts and associated lists
			if (probManager.isDiscrete(selectedDist)) {

				// create full bar chart
				// ============================
				GeoElement discreteProbListCopy = discreteProbList.copy();
				newGeoList.add(discreteProbListCopy);

				GeoElement discreteValueListCopy = discreteValueList.copy();
				newGeoList.add(discreteValueList);

				if (graphType == GRAPH_LINE) {
					expr = "BarChart["
							+ discreteValueListCopy
									.getLabel(StringTemplate.maxPrecision)
							+ ","
							+ discreteProbListCopy
									.getLabel(StringTemplate.maxPrecision)
							+ ",0]";
				} else if (graphType == GRAPH_BAR) {
					expr = "BarChart["
							+ discreteValueListCopy
									.getLabel(StringTemplate.maxPrecision)
							+ ","
							+ discreteProbListCopy
									.getLabel(StringTemplate.maxPrecision)
							+ ",1]";
				} else if (graphType == GRAPH_STEP) {
					// TODO: polyline
				}

				GeoElementND discreteGraphCopy = createGeoFromString(expr,
						false);
				discreteGraphCopy.setLabel(null);
				discreteGraphCopy.setVisualStyle(discreteGraph.toGeoElement());
				newGeoList.add(discreteGraphCopy);

				// create interval bar chart
				// ============================
				double offset = 1 - ((GeoNumeric) discreteValueList.get(0))
						.getDouble() + 0.5;
				expr = "Take[" + discreteProbListCopy.getLabel(tpl) + ", x("
						+ lowPointCopy.getLabel(tpl) + ")+" + offset + ", x("
						+ highPointCopy.getLabel(tpl) + ")+" + offset + "]";
				GeoElementND intervalProbList = createGeoFromString(expr, false);
				newGeoList.add(intervalProbList);

				expr = "Take[" + discreteValueListCopy.getLabel(tpl) + ", x("
						+ lowPointCopy.getLabel(tpl) + ")+" + offset + ", x("
						+ highPointCopy.getLabel(tpl) + ")+" + offset + "]";
				GeoElementND intervalValueList = createGeoFromString(expr,
						false);
				newGeoList.add(intervalValueList);

				if (graphType == GRAPH_LINE)
					expr = "BarChart[" + intervalValueList.getLabel(tpl) + ","
							+ intervalProbList.getLabel(tpl) + ",0]";
				else
					expr = "BarChart[" + intervalValueList.getLabel(tpl) + ","
							+ intervalProbList.getLabel(tpl) + ",1]";

				GeoElementND discreteIntervalGraphCopy = createGeoFromString(
						expr, false);
				discreteIntervalGraphCopy.setLabel(null);
				discreteIntervalGraphCopy.setVisualStyle(discreteIntervalGraph);
				newGeoList.add(discreteIntervalGraphCopy);

			}

			// create density curve and integral
			else {

				// density curve
				GeoElement densityCurveCopy = densityCurve.copyInternal(cons);
				densityCurveCopy.setLabel(null);
				densityCurveCopy.setVisualStyle(densityCurve);
				newGeoList.add(densityCurveCopy);

				// integral
				if (!isCumulative) {
					expr = "Integral[" + densityCurveCopy.getLabel(tpl)
							+ ", x(" + lowPointCopy.getLabel(tpl) + "), x("
							+ highPointCopy.getLabel(tpl) + ") , true ]";
					GeoElementND integralCopy = createGeoFromString(expr, false);
					integralCopy.setVisualStyle(integral);
					integralCopy.setLabel(null);
					newGeoList.add(integralCopy);
				}
			}

			// normal overlay
			if (showNormalOverlay) {
				GeoElement normalOverlayCopy = normalOverlay.copyInternal(cons);
				normalOverlayCopy.setLabel(null);
				normalOverlayCopy.setVisualStyle(normalOverlay);
				newGeoList.add(normalOverlayCopy);
			}

			// set the EV location and auxiliary = false for all of the new geos
			for (GeoElementND geo : newGeoList) {
				geo.setAuxiliaryObject(false);
				if (euclidianViewID == App.VIEW_EUCLIDIAN) {
					geo.addView(App.VIEW_EUCLIDIAN);
					geo.removeView(App.VIEW_EUCLIDIAN2);
					geo.update();
				} else if (euclidianViewID == App.VIEW_EUCLIDIAN2) {
					geo.addView(App.VIEW_EUCLIDIAN2);
					geo.removeView(App.VIEW_EUCLIDIAN);
					geo.update();
				}
			}

			// set the window dimensions of the target EV to match the prob calc
			// dimensions

			EuclidianView ev = (EuclidianView) app.getView(euclidianViewID);

			ev.setRealWorldCoordSystem(plotSettings.xMin, plotSettings.xMax,
					plotSettings.yMin, plotSettings.yMax);
			ev.setAutomaticAxesNumberingDistance(
					plotSettings.xAxesIntervalAuto, 0);
			ev.setAutomaticAxesNumberingDistance(
					plotSettings.yAxesIntervalAuto, 1);
			if (!plotSettings.xAxesIntervalAuto) {
				ev.setAxesNumberingDistance(new GeoNumeric(cons,
						plotSettings.xAxesInterval), 0);
			}
			if (!plotSettings.yAxesIntervalAuto) {
				ev.setAxesNumberingDistance(new GeoNumeric(cons,
						plotSettings.yAxesInterval), 1);
			}
			ev.updateBackground();

			// remove the new geos from our temporary list
			newGeoList.clear();

		} catch (Exception e) {
			e.printStackTrace();
			app.setDefaultCursor();
		}

		app.setDefaultCursor();
	}

	@Override
	public int getViewID() {
		return App.VIEW_PROBABILITY_CALCULATOR;
	}

	public void settingsChanged(AbstractSettings settings) {
		ProbabilityCalculatorSettings pcSettings = (ProbabilityCalculatorSettings) settings;
		setProbabilityCalculator(pcSettings.getDistributionType(),
				pcSettings.getParameters(), pcSettings.isCumulative());
		if(pcSettings.isIntervalSet()){
			this.probMode = pcSettings.getProbMode();
			this.setInterval(pcSettings.getLow(),pcSettings.getHigh());
			
			
		}

	}

	protected abstract void setInterval(double low2, double high2);

	public void add(GeoElement geo) {
	}

	public void updatePreviewFromInputBar(GeoElement[] geos) {
		// TODO
	}

	public void remove(GeoElement geo) {
	}

	public void rename(GeoElement geo) {
	}

	// Handles user point changes in the EV plot panel
	@Override
	public void update(GeoElement geo) {
		if (!isSettingAxisPoints && !isIniting) {
			if (geo.equals(lowPoint)) {
				if (isValidInterval(probMode, lowPoint.getInhomX(), getHigh())) {
					setLow(lowPoint.getInhomX());
					updateIntervalProbability();
					updateGUI();
					if (probManager.isDiscrete(selectedDist))
						table.setSelectionByRowValue((int) getLow(), (int) getHigh());
				} else {
					setXAxisPoints();
				}
			}
			if (geo.equals(highPoint)) {
				if (isValidInterval(probMode, getLow(), highPoint.getInhomX())) {
					setHigh(highPoint.getInhomX());
					updateIntervalProbability();
					updateGUI();
					if (probManager.isDiscrete(selectedDist))
						table.setSelectionByRowValue((int) getLow(), (int) getHigh());
				} else {
					setXAxisPoints();
				}
			}
			updateRounding();
		}
		

		// statCalculator.updateResult();
	}
	
	/**
	 * Returns an interval probability for the currently selected distribution
	 * and probability mode. If mode == PROB_INTERVAL then P(low <= X <= high)
	 * is returned. If mode == PROB_LEFT then P(low <= X) is returned. If mode
	 * == PROB_RIGHT then P(X <= high) is returned.
	 */
	protected double intervalProbability() {

		return probManager.intervalProbability(getLow(), getHigh(), selectedDist,
				parameters, probMode);
	}

	/**
	 * Returns an inverse probability for a selected distribution.
	 * 
	 * @param prob
	 */
	protected double inverseProbability(double prob) {

		return probManager.inverseProbability(selectedDist, prob, parameters);
	}
	
	protected void updateIntervalProbability() {
		probability = intervalProbability();
		if (probmanagerIsDiscrete())
			this.discreteIntervalGraph.updateCascade();
		else if (hasIntegral)
			this.integral.updateCascade();
	}
	
	protected boolean isValidInterval(int probMode, double xLow, double xHigh) {

		if (probMode == PROB_INTERVAL && xHigh < xLow)
			return false;

		// don't allow non-integer bounds for discrete dist.
		if (probManager.isDiscrete(selectedDist)
				&& (Math.floor(xLow) != xLow || Math.floor(xHigh) != xHigh)) {
			return false;
		}

		boolean isValid = true;
		switch (selectedDist) {

		case BINOMIAL:
		case HYPERGEOMETRIC:
			isValid = xLow >= getDiscreteXMin() && xHigh <= getDiscreteXMax();
			break;

		case POISSON:
		case PASCAL:
			isValid = xLow >= getDiscreteXMin();
			break;

		case CHISQUARE:
		case EXPONENTIAL:
			if (probMode != PROB_LEFT)
				isValid = xLow >= 0;
			break;

		case F:
			if (probMode != PROB_LEFT)
				isValid = xLow > 0;
			break;

		}

		return isValid;

	}

	protected boolean isValidParameter(double parameter, int index) {

		boolean[] isValid = { true, true, true };

		switch (selectedDist) {

		case F:
		case STUDENT:
		case EXPONENTIAL:
		case WEIBULL:
		case POISSON:
			if (index == 0) {
				// all parameters must be positive
				isValid[0] = parameter > 0;
			}
			break;

		case CAUCHY:
		case LOGISTIC:
			if (index == 1) {
				// scale must be positive
				isValid[1] = index == 1 && parameter > 0;
			}
			break;

		case CHISQUARE:
			if (index == 0) {
				// df >= 1, integer
				isValid[0] = Math.floor(parameter) == parameter
						&& parameter >= 1;
			}
			break;

		case BINOMIAL:
			if (index == 0) {
				// n >= 0, integer
				isValid[0] = Math.floor(parameter) == parameter
						&& parameter >= 0;
			} else if (index == 1) {
				// p is probability value
				isValid[1] = parameter >= 0 && parameter <= 1;
			}
			break;

		case PASCAL:
			if (index == 0) {
				// n >= 1, integer
				isValid[0] = Math.floor(parameter) == parameter
						&& parameter >= 1;
			} else if (index == 1) {
				// p is probability value
				isValid[1] = index == 1 && parameter >= 0 && parameter <= 1;
			}
			break;

		case HYPERGEOMETRIC:
			if (index == 0) {
				// population size: N >= 1, integer
				isValid[0] = index == 0 && Math.floor(parameter) == parameter
						&& parameter >= 1;
			} else if (index == 1) {
				// successes in the population: n >= 0 and <= N, integer
				isValid[1] = index == 1 && Math.floor(parameter) == parameter
						&& parameter >= 0 && parameter <= parameters[0];
			} else if (index == 2) {
				// sample size: s>= 1 and s<= N, integer
				isValid[2] = index == 2 && Math.floor(parameter) == parameter
						&& parameter >= 1 && parameter <= parameters[0];
			}
			break;

		// these distributions have no parameter restrictions
		// case DIST.NORMAL:
		// case DIST.LOGNORMAL:
		}

		return isValid[0] && isValid[1] && isValid[2];

	}

	/**
	 * Adjust local rounding constants to match global rounding constants and
	 * update GUI when needed
	 */
	private void updateRounding() {

		if (kernel.useSignificantFigures) {
			if (printFigures != kernel.getPrintFigures()) {
				printFigures = kernel.getPrintFigures();
				printDecimals = -1;
				updateDiscreteTable();
				updateGUI();
			}
		} else if (printDecimals != kernel.getPrintDecimals()) {
			printDecimals = kernel.getPrintDecimals();
			updateDiscreteTable();
			updateGUI();
		}
	}
	
	protected abstract void updateDiscreteTable();
	
	protected abstract void updateGUI();
	
	protected boolean probmanagerIsDiscrete() {
		return probManager.isDiscrete(selectedDist);
	}

	protected int[] generateFirstXLastXCommon() {
		int firstXLastX [] = new int[2];
		if(discreteValueList == null){
			this.createDiscreteLists();
		}
		firstXLastX[0] = (int) ((GeoNumeric) discreteValueList.get(0)).getDouble();
		firstXLastX[1] = (int) ((GeoNumeric) discreteValueList.get(discreteValueList
				.size() - 1)).getDouble();
		
		return firstXLastX;
	}

	@Override
	final public void updateVisualStyle(GeoElement geo, GProperty prop) {
		update(geo);
	}

	public void attachView() {
		// clearView();
		// kernel.notifyAddAll(this);
		kernel.attach(this);
	}

	public void detachView() {
		removeGeos();
		kernel.detach(this);
		// plotPanel.detachView();
		// clearView();
		// kernel.notifyRemoveAll(this);
	}
	
	public void updateAuxiliaryObject(GeoElement geo) {
		
	}

	public void repaintView() {
	}

	public void reset() {
		
	}

	public void clearView() {
		// TODO Auto-generated method stub

	}

	public void setMode(int mode, ModeSetter m) {
		// TODO Auto-generated method stub

	}

	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isShowing() {
		return app.showView(App.VIEW_PROBABILITY_CALCULATOR);
	}
	
	public boolean doRemoveFromConstruction() {
		return removeFromConstruction;
	}

	public void setRemoveFromConstruction(boolean removeFromConstruction) {
		this.removeFromConstruction = removeFromConstruction;
	}

	private void removeFromConstructionList(ConstructionElement ce) {
		if (removeFromConstruction)
			cons.removeFromConstructionList(ce);
	}

	private void removeFromAlgorithmList(AlgoElement algo) {
		if (removeFromConstruction)
			cons.removeFromAlgorithmList(algo);
	}
	
	/**
	 * Builds a string that can be used by the algebra processor to create a
	 * GeoFunction representation of a given density curve.
	 * 
	 * @param distType
	 * @param parms
	 * @return
	 */
	private GeoFunction buildDensityCurveExpression(DIST type, boolean cumulative) {

		GeoNumeric param1 = null, param2 = null;

		if (parameters.length > 0) {
			param1 = new GeoNumeric(cons, parameters[0]);
		}
		if (parameters.length > 1) {
			param2 = new GeoNumeric(cons, parameters[1]);
		}

		AlgoDistributionDF ret = null;
		GeoBoolean cumulativeGeo = new GeoBoolean(cons, cumulative);
		switch (type) {
		case NORMAL:
			ret = new AlgoNormalDF(cons, param1, param2, cumulativeGeo);
			break;
		case STUDENT:
			ret = new AlgoTDistributionDF(cons, param1, cumulativeGeo);
			break;
		case CHISQUARE:
			ret = new AlgoChiSquaredDF(cons, param1, cumulativeGeo);
			break;
		case F:
			ret = new AlgoFDistributionDF(cons, param1, param2, cumulativeGeo);
			break;
		case CAUCHY:
			ret = new AlgoCauchyDF(cons, param1, param2, cumulativeGeo);
			break;
		case EXPONENTIAL:
			ret = new AlgoExponentialDF(cons, param1, cumulativeGeo);
			break;
		case GAMMA:
			ret = new AlgoGammaDF(cons, param1, param2, cumulativeGeo);
			break;
		case WEIBULL:
			ret = new AlgoWeibullDF(cons, param1, param2, cumulativeGeo);
			break;
		case LOGNORMAL:
			ret = new AlgoLogNormalDF(cons, param1, param2, cumulativeGeo);
			break;
		case LOGISTIC:
			ret = new AlgoLogisticDF(cons, param1, param2, cumulativeGeo);
			break;

		case BINOMIAL:
		case PASCAL:
		case POISSON:
		case HYPERGEOMETRIC:
			Log.error("Not continuous distribution");
			break;
		default:
			Log.error("Missing case for density curve");
		}

		if (ret != null) {
			cons.removeFromConstructionList((AlgoElement) ret);
			return ret.getResult();
		}

		return null;

	}
	
	// ============================================================
	// XML
	// ============================================================

	/**
	 * returns settings in XML format
	 */
	public void getXML(StringBuilder sb) {

		if (selectedDist == null)
			return;

		sb.append("<probabilityCalculator>\n");
		sb.append("\t<distribution");

		sb.append(" type=\"");
		sb.append(selectedDist.ordinal());
		sb.append("\"");

		sb.append(" isCumulative=\"");
		sb.append(isCumulative ? "true" : "false");
		sb.append("\"");

		sb.append(" parameters" + "=\"");
		for (int i = 0; i < parameters.length; i++) {
			sb.append(parameters[i]);
			sb.append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append("\"");

		sb.append("/>\n");
		
		sb.append("\t<interval");

		sb.append(" mode=\"");
		sb.append(this.probMode);
		sb.append("\"");

		sb.append(" low=\"");
		sb.append(getLow());
		sb.append("\"");
		
		sb.append(" high=\"");
		sb.append(getHigh());
		sb.append("\"");

		sb.append("/>\n");
		sb.append("</probabilityCalculator>\n");
	}

	@Override
	public void startBatchUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endBatchUpdate() {
		// TODO Auto-generated method stub
		
	}
	
	public String getMeanSigma() {
		Double[] val = probManager.getDistributionMeasures(selectedDist,
				parameters);

		// mean/sigma are undefined for the Cauchy distribution
		// and F-distribution with certain parameters

		String mean = val[0] == null ? "?" : format(val[0]);
		String sigma = val[1] == null ? "?" : format(val[1]);

		String meanSigmaStr = Unicode.mu + " = " + mean + "   " + Unicode.sigma
				+ " = " + sigma;

		return meanSigmaStr;
	}

	protected void setHigh(double high) {
		this.high = high;
	}

	protected void setLow(double low) {
		this.low = low;
	}

	public boolean isOverlayDefined() {
		return !((selectedDist == DIST.CAUCHY) || (selectedDist == DIST.F && parameters[1] < 4));
	}
}
