package org.geogebra.common.gui.view.probcalculator;

import java.util.ArrayList;
import java.util.TreeSet;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.data.PlotSettings;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.algos.AlgoBarChart;
import org.geogebra.common.kernel.algos.AlgoDependentBoolean;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.algos.AlgoDependentPoint;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.algos.AlgoListLength;
import org.geogebra.common.kernel.algos.AlgoMax;
import org.geogebra.common.kernel.algos.AlgoMin;
import org.geogebra.common.kernel.algos.AlgoPolyLine;
import org.geogebra.common.kernel.algos.AlgoRayPointVector;
import org.geogebra.common.kernel.algos.AlgoStepGraph;
import org.geogebra.common.kernel.algos.AlgoStickGraph;
import org.geogebra.common.kernel.algos.AlgoTake;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.cas.AlgoIntegralDefinite;
import org.geogebra.common.kernel.geos.GProperty;
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
import org.geogebra.common.kernel.statistics.AlgoDistributionDF;
import org.geogebra.common.kernel.statistics.AlgoLogNormalDF;
import org.geogebra.common.kernel.statistics.AlgoLogisticDF;
import org.geogebra.common.kernel.statistics.AlgoNormalDF;
import org.geogebra.common.kernel.statistics.CmdRealDistribution2Params;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Common view for probability calculator
 *
 * @author gabor
 */
public abstract class ProbabilityCalculatorView
		implements View, SettingListener, SetLabels {

	public static final double PADDING_TOP_PX = 20.0;
	private final DiscreteDistributionFactory discreteDistributionFactory;
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

	private static final GColor COLOR_NORMAL_OVERLAY = GColor.RED;

	private static final GColor COLOR_PDF_FILL = GColor.BLUE;

	static final GColor COLOR_POINT = GColor.BLACK;

	private EuclidianView plotPanel;

	private @CheckForNull ProbabilityTable table;
	/** enable/disable integral ---- use for testing */
	protected boolean hasIntegral = true;

	/** selected distribution mode */
	protected Dist selectedDist = Dist.NORMAL; // default: startup with normal
	// distribution

	// distribution fields
	private String[][] parameterLabels;
	/**
	 * maximum number of parameters allowed for a distribution
	 */
	public final static int maxParameterCount = 3;
	protected GeoNumberValue[] parameters;
	protected boolean isCumulative = false;

	// GeoElements
	protected ArrayList<GeoElementND> plotGeoList;
	private final ProbabilityXAxis xAxis;
	private GeoFunction densityCurve;
	private GeoElement integral;
	private GeoElement discreteIntervalGraph;
	private GeoElement normalOverlay;
	private GeoElementND discreteGraph;
	private GeoList discreteValueList;
	private GeoList discreteProbList;
	private ArrayList<GeoElement> pointList;

	// initing
	protected boolean isIniting;
	protected boolean isSettingAxisPoints = false;

	// probability calculation modes
	public static final int PROB_INTERVAL = 0;
	public static final int PROB_LEFT = 1;
	public static final int PROB_RIGHT = 2;
	public static final int PROB_TWO_TAILED = 3;
	protected int probMode = PROB_INTERVAL;

	// interval values
	protected GeoNumberValue low;
	protected GeoNumberValue high;

	// current probability result
	protected double probability;
	protected double leftProbability;
	protected double rightProbability;

	// rounding
	protected int printDecimals = 4;
	protected int printFigures = -1;
	protected StringTemplate stringTemplate;

	// flags
	protected boolean showProbGeos = true;
	protected boolean showNormalOverlay = false;

	private static final double opacityIntegral = 0.5f;
	private static final double opacityDiscrete = 0.0f; // entire bar chart
	private static final double opacityDiscreteInterval = 0.5f; // bar chart
	// interval
	private static final int thicknessCurve = 4;
	private static final int thicknessBarChart = 3;

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
	private TreeSet<AlgoElement> tempSet;
	private GeoElement integralLeft;
	private GeoElement integralRight;
	private DiscreteTwoTailedGraph discreteTwoTailedGraph;

	/**
	 * @param app application
	 */
	public ProbabilityCalculatorView(App app) {
		isIniting = true;
		this.app = app;
		this.loc = app.getLocalization();
		kernel = app.getKernel();
		cons = kernel.getConstruction();
		low = new GeoNumeric(cons, 0);
		high = new GeoNumeric(cons, 1);

		// Initialize settings and register listener
		app.getSettings().getProbCalcSettings().addListener(this);

		probManager = new ProbabilityManager(app, this);
		plotSettings = new PlotSettings();
		plotGeoList = new ArrayList<>();
		xAxis = new ProbabilityXAxis(app);
		discreteDistributionFactory = new DiscreteDistributionFactory(cons);
		updateRoundingFlags();
	}

	/**
	 * Update localization arrays
	 */
	protected void setLabelArrays() {
		if (app.getConfig().hasDistributionView()) {
			parameterLabels = probManager.getParameterLabelArrayPrefixed(app.getLocalization());
		} else {
			parameterLabels = probManager.getParameterLabelArray(app.getLocalization());
		}
	}

	/**
	 * Returns the maximum value in the discrete value list.
	 * @return maximum value in the discrete value list.
	 */
	public int getDiscreteXMax() {
		if (discreteValueList != null && discreteValueList.size() > 0) {
			GeoNumeric geo = (GeoNumeric) discreteValueList
					.get(discreteValueList.size() - 1);
			return (int) geo.getDouble();
		}
		return -1;
	}

	/**
	 * Returns the minimum value in the discrete value list.
	 * @return minimum value in the discrete value list.
	 */
	public int getDiscreteXMin() {
		if (discreteValueList != null) {
			GeoNumeric geo = (GeoNumeric) discreteValueList.get(0);
			return (int) geo.getDouble();
		}
		return -1;
	}

	/**
	 * @param type one of GRAPH_BAR, GRAPH_STEP, GRAPH_LINE
	 */
	public void setGraphType(int type) {
		if (graphType == type) {
			return;
		}

		graphType = type;
		if (isCumulative) {
			graphTypeCDF = type;
		} else {
			graphTypePDF = type;
		}

		updateAll(true);
	}

	/**
	 * @param isCumulative whether to show cumulative distribution
	 */
	public final void setCumulative(boolean isCumulative) {
		if (setCumulativeNoFire(isCumulative)) {
			changeProbabilityType();
			updateAll(true);
		}
	}

	private boolean setCumulativeNoFire(boolean isCumulative) {
		if (this.isCumulative == isCumulative) {
			return false;
		}
		this.isCumulative = isCumulative;
		graphType = isCumulative ? graphTypeCDF : graphTypePDF;
		return true;
	}

	protected abstract void changeProbabilityType();

	protected void validateLowHigh(int oldProbMode) {
		if (probMode == PROB_LEFT && oldProbMode == PROB_RIGHT) {
			setHigh(getLow());
		} else if (probMode == PROB_RIGHT && oldProbMode == PROB_LEFT) {
			setLow(getHigh());
		}
		if (probMode != PROB_LEFT && getLow() <= plotSettings.xMin) {
			setLowDefault();
		}
		if (probMode != PROB_RIGHT && getHigh() >= plotSettings.xMax) {
			setHighDefault();
		}
	}

	/**
	 * @return graph type (one of GRAPH_BAR, GRAPH_STEP, GRAPH_LINE)
	 */
	public int getGraphType() {
		return graphType;
	}

	/**
	 * @return print decimals
	 */
	public int getPrintDecimals() {
		return printDecimals;
	}

	/**
	 * @return significant figures
	 */
	public int getPrintFigures() {
		return printFigures;
	}

	/**
	 * @return parameters
	 */
	public GeoNumberValue[] getParameters() {
		return parameters;
	}

	/**
	 * @param distributionType distribution type
	 * @param parameters distribution parameters
	 * @param isCumulative whether it's cumulative
	 */
	public void setProbabilityCalculator(Dist distributionType,
			GeoNumberValue[] parameters, boolean isCumulative) {
		setProbabilityCalculatorNoFire(distributionType, parameters, isCumulative);
		updateAll(true);
	}

	protected void setProbabilityCalculatorNoFire(Dist distributionType,
			GeoNumberValue[] parameters, boolean isCumulative) {
		this.selectedDist = distributionType;
		setCumulativeNoFire(isCumulative);

		this.parameters = parameters;
		if (parameters == null || parameters.length == 0 || parameters[0] == null) {
			this.parameters = ProbabilityManager
					.getDefaultParameters(selectedDist, cons);
		}
	}

	/**
	 * @return plot settings
	 */
	public PlotSettings getPlotSettings() {
		return plotSettings;
	}

	/**
	 * @param plotSettings plot settings
	 */
	public void setPlotSettings(PlotSettings plotSettings) {
		this.plotSettings = plotSettings;
	}

	public boolean isShowNormalOverlay() {
		return showNormalOverlay;
	}

	public void setShowNormalOverlay(boolean showNormalOverlay) {
		this.showNormalOverlay = showNormalOverlay;
	}

	/**
	 * Update distribution, plot, table, UI and stylebar
	 * @param setDefaultBounds whether to reset low/high to default too
	 */
	public void updateAll(boolean setDefaultBounds) {
		updateOutput(true);
		if (setDefaultBounds && !isIniting) {
			setDefaultBounds();
		}
		if (getResultPanel() != null) {
			updateProbabilityType(getResultPanel());
		}
		updateGUI();
		updateStylebar();
	}

	/**
	 * @return the reult panel
	 */
	public abstract ResultPanel getResultPanel();

	protected abstract void updateOutput(boolean updateDistributionView);

	protected void updateStylebar() {
		// desktop only
	}

	// =================================================
	// Getters/Setters
	// =================================================

	public Dist getSelectedDist() {
		return selectedDist;
	}

	public double getLow() {
		return low.getDouble();
	}

	public double getHigh() {
		return high.getDouble();
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
	private static GColor colorPDF() {
		return GeoGebraColorConstants.DARKBLUE;
	}

	/**
	 * Creates the required GeoElements for the currently selected distribution
	 * type and parameters.
	 */
	public void createGeoElements() {
		removeGeos();
		createXAxisPoints();
		createDistribution();
		if (showNormalOverlay) {
			createOverlay();
		}
		hideAllGeosFromViews();
		hideToolTips();
	}

	private void createDistribution() {
		if (probManager.isDiscrete(selectedDist)) {
			createDiscreteDistribution();
		} else {
			createContinuousDistribution();
		}
	}

	private void createXAxisPoints() {
		pointList = new ArrayList<>();
		xAxis.addToList(pointList);
		setXAxisPoints();
		plotGeoList.add(xAxis.lowPoint());
		plotGeoList.add(xAxis.highPoint());
	}

	private void createContinuousDistribution() {
		createDensityCurve();
		if (hasIntegral) {
			createIntegral();
		}

		if (isCumulative) {
			createCumulativeSegments();
		}
	}

	private void createCumulativeSegments() {
		// point on curve
		GeoFunction f = densityCurve;
		ExpressionNode highPointX = new ExpressionNode(kernel,
				xAxis.highPoint(), Operation.XCOORD, null);
		ExpressionNode curveY = new ExpressionNode(kernel, f,
				Operation.FUNCTION, highPointX);

		MyVecNode curveVec = new MyVecNode(kernel, highPointX, curveY);
		ExpressionNode curvePointNode = new ExpressionNode(kernel,
				curveVec, Operation.NO_OPERATION, null);
		curvePointNode.setForcePoint();

		AlgoDependentPoint pAlgo = new AlgoDependentPoint(cons,
				curvePointNode, false);
		cons.removeFromConstructionList(pAlgo);

		GeoPoint curvePoint = (GeoPoint) pAlgo.getOutput(0);
		curvePoint.setObjColor(COLOR_POINT);
		curvePoint.setPointSize(4);
		curvePoint.setLayer(f.getLayer() + 1);
		curvePoint.setSelectionAllowed(false);
		plotGeoList.add(curvePoint);

		// create vertical line segment
		ExpressionNode xcoord = new ExpressionNode(kernel, curvePoint,
				Operation.XCOORD, null);
		MyVecNode vec = new MyVecNode(kernel, xcoord,
				new MyDouble(kernel, 0.0));
		ExpressionNode point = new ExpressionNode(kernel, vec,
				Operation.NO_OPERATION, null);
		point.setForcePoint();
		AlgoDependentPoint pointAlgo = new AlgoDependentPoint(cons,
				point, false);
		cons.removeFromConstructionList(pointAlgo);

		AlgoJoinPointsSegment seg1 = new AlgoJoinPointsSegment(cons,
				curvePoint, (GeoPoint) pointAlgo.getOutput(0), null,
				false);
		GeoElement xSegment = seg1.getOutput(0);
		xSegment.setObjColor(GColor.BLUE);
		xSegment.setLineThickness(3);
		xSegment.setLineType(
				EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
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
		GeoElement ySegment = seg2.getOutput(0);
		ySegment.setObjColor(GColor.RED);
		ySegment.setLineThickness(3);
		ySegment.setLineType(EuclidianStyleConstants.LINE_TYPE_FULL);
		ySegment.setEuclidianVisible(showProbGeos);
		ySegment.setFixed(true);
		ySegment.setSelectionAllowed(false);
		plotGeoList.add(ySegment);
	}

	private void createDensityCurve() {
		densityCurve = buildDensityCurveExpression(selectedDist,
				isCumulative);
		if (isCumulative && (selectedDist == Dist.F
				|| selectedDist == Dist.EXPONENTIAL)) {
			pdfCurve = buildDensityCurveExpression(selectedDist, false);
			cons.removeFromConstructionList(pdfCurve);
		}
		densityCurve.setObjColor(colorPDF());
		densityCurve.setLineThickness(thicknessCurve);
		densityCurve.setFixed(true);
		densityCurve.setSelectionAllowed(false);
		densityCurve.setEuclidianVisible(true);
		plotGeoList.add(densityCurve);
	}

	private void createOverlay() {
		Double[] m = probManager.getDistributionMeasures(selectedDist,
				parameters);
		if (m[0] != null && m[1] != null) {
			normalOverlay = createNormalCurveOverlay(m[0], m[1]);
			plotGeoList.add(normalOverlay);
		}
	}

	private void createDiscreteDistribution() {
		createDiscreteLists();
		if (discreteValueList.size() == 0) {
			return;
		}
		if (graphType == GRAPH_STEP) {
			createStepChart();
		} else {
			createBarChart();
		}

		styleDiscreteGraph();
		plotGeoList.add(discreteGraph);

		// ============================
		// create lists for the discrete interval graph

		// Use Take[] to create a subset of the full discrete graph:
		// Take[discreteList, x(lowPoint) + offset, x(highPoint) + offset]
		//
		// The offset accounts for Sequence[] starting its count at 1 and
		// the starting value of the discrete x list. Thus,
		// offset = 1 - lowest discrete x value

		double firstX = discreteValueAt(0);
		MyDouble offset = new MyDouble(kernel, 1d - firstX + 0.5);

		ExpressionNode low1 = xAxis.getLowExpression();
		ExpressionNode high1 = xAxis.getHighExpression();
		ExpressionNode lowPlusOffset = new ExpressionNode(kernel, low1,
				Operation.PLUS, offset);
		ExpressionNode highPlusOffset = new ExpressionNode(kernel, high1,
				Operation.PLUS, offset);

		GeoNumberValue xLow;
		GeoNumberValue xMin;
		GeoNumberValue xMax;
		AlgoDependentNumber xHigh = getDependentNumber(highPlusOffset);
		if (isCumulative) {
			// for cumulative bar graphs we only show a single bar
			xLow = getDependentNumber(highPlusOffset).getNumber();
			xMin = xLow;
			xMax = xLow;
		} else {
			xLow = getDependentNumber(lowPlusOffset).getNumber();
			xMin = xLow;
			xMax = xHigh.getNumber();
		}

		if (isTwoTailedMode()) {
			ExpressionNode xminPlusOne = new ExpressionNode(kernel,
					xMin, Operation.PLUS, new MyDouble(kernel, 1));
			ExpressionNode ex = new ExpressionNode(kernel,
					new MyNumberPair(kernel,
							new ExpressionNode(kernel,
									xLow, Operation.EQUAL_BOOLEAN, xHigh.getNumber()),
							xminPlusOne), Operation.IF_ELSE, xMax);
			AlgoDependentNumber algoDepNumber = getDependentNumber(ex);
			createTwoTailedDiscreteGraph(capMax(xMin), capMax(algoDepNumber.getNumber()));
		} else {
			createSimpleDiscreteGraph(capMax(xMin), capMax(xMax));
		}
		createAxis();
	}

	protected AlgoDependentNumber getDependentNumber(ExpressionNode expr) {
		return new AlgoDependentNumber(cons,
				expr, false, null, false);
	}

	private void createSimpleDiscreteGraph(GeoNumberValue xMin, GeoNumberValue xMax) {
		GeoList intervalValueList = takeSubList(discreteValueList, xMin, xMax);
		GeoList intervalProbList = takeSubList(discreteProbList, xMin, xMax);

		discreteIntervalGraph = createIntervalGraph(intervalValueList, intervalProbList);
		plotGeoList.add(discreteIntervalGraph);
		hideTwoTailedDiscreteGraph();
	}

	private void hideTwoTailedDiscreteGraph() {
		if (discreteTwoTailedGraph == null) {
			return;
		}

		discreteTwoTailedGraph.removeFrom(plotGeoList);
	}

	private void createTwoTailedDiscreteGraph(GeoNumberValue xMin,
			GeoNumberValue xMax) {
		GeoNumeric left = new GeoNumeric(cons, 1);
		GeoList leftValues = takeSubList(discreteValueList, left, xMin);
		GeoList rightValues = takeSubList(discreteValueList, xMax, null);
		GeoList leftProbs = takeSubList(discreteProbList, left, xMin);
		GeoList rightProbs = takeSubList(discreteProbList, xMax, null);
		GeoElement discreteIntervalGraphLeft = createIntervalGraph(leftValues, leftProbs);
		GeoElement discreteIntervalGraphRight = createIntervalGraph(rightValues, rightProbs);
		discreteTwoTailedGraph =
				new DiscreteTwoTailedGraph(discreteIntervalGraphLeft, discreteIntervalGraphRight);
		discreteTwoTailedGraph.addTo(plotGeoList);
		hideSimpleDiscreteGraph();
	}

	private GeoNumberValue capMax(GeoNumberValue xMin) {
		GeoNumberValue length = new AlgoListLength(cons, discreteValueList).getLength();
		AlgoMax algoMax = new AlgoMax(cons, xMin, new GeoNumeric(cons, 1));
		AlgoMin algoMin = new AlgoMin(cons, algoMax.getResult(), length);
		cons.removeFromConstructionList(length.getParentAlgorithm());
		cons.removeFromConstructionList(algoMin);
		cons.removeFromConstructionList(algoMax);
		return algoMin.getResult();
	}

	private void hideSimpleDiscreteGraph() {
		if (discreteIntervalGraph == null) {
			return;
		}

		plotGeoList.remove(discreteIntervalGraph);
	}

	private GeoElement createIntervalGraph(GeoList values, GeoList probabilities) {
		GeoElement graph;
		if (isCumulative) {
			GeoBoolean t = new GeoBoolean(cons);
			t.setValue(true);
			AlgoStickGraph algoStickGraph = new AlgoStickGraph(cons, values, probabilities, t);
			cons.removeFromConstructionList(algoStickGraph);
			graph = algoStickGraph.getOutput(0);

		} else if (graphType == GRAPH_STEP) {
			GeoBoolean t = new GeoBoolean(cons);
			t.setValue(true);
			AlgoStepGraph algoStepGraph2 = new AlgoStepGraph(cons, values, probabilities, t);
			cons.removeFromConstructionList(algoStepGraph2);
			graph = algoStepGraph2.getOutput(0);

		} else {
			AlgoBarChart barChart;
			if (graphType == GRAPH_LINE) {
				GeoNumberValue zeroWidth2 = new GeoNumeric(cons, 0d);
				barChart = new AlgoBarChart(cons, values, probabilities, zeroWidth2);
			} else {
				GeoNumberValue oneWidth2 = new GeoNumeric(cons, 1);
				barChart = new AlgoBarChart(cons, values, probabilities, oneWidth2);
			}
			graph = barChart.getOutput(0);
			cons.removeFromConstructionList(barChart);
		}

		if (isCumulative) {
			graph.setObjColor(GColor.RED);
			graph.setLineThickness(3);
			graph
					.setLineType(EuclidianStyleConstants.LINE_TYPE_FULL);
		} else if (graphType == GRAPH_LINE || graphType == GRAPH_STEP) {
			graph.setObjColor(COLOR_PDF_FILL);
			graph.setLineThickness(thicknessBarChart + 2);
		} else {
			graph.setObjColor(COLOR_PDF_FILL);
			graph.setAlphaValue(opacityDiscreteInterval);
			graph.setLineThickness(thicknessBarChart);
		}

		graph.setEuclidianVisible(showProbGeos);
		graph.setLayer(discreteGraph.getLayer() + 1);
		graph.setFixed(true);
		graph.setSelectionAllowed(false);
		graph.updateCascade();
		return graph;
	}

	protected GeoList takeSubList(GeoList list, GeoNumberValue min, GeoNumberValue max) {
		AlgoTake take = new AlgoTake(cons, list, (GeoNumeric) min, (GeoNumeric) max);
		cons.removeFromConstructionList(take);
		return (GeoList) take.getOutput(0);
	}

	private void createStepChart() {
		GeoBoolean t = new GeoBoolean(cons);
		t.setValue(true);

		AlgoStepGraph algoStepGraph = new AlgoStepGraph(cons,
				discreteValueList, discreteProbList, t);

		cons.removeFromConstructionList(algoStepGraph);
		discreteGraph = algoStepGraph.getOutput(0);
	}

	private void createBarChart() {
		GeoNumeric width = new GeoNumeric(cons, graphType == GRAPH_LINE ? 0 : 1);
		AlgoBarChart algoBarChart = new AlgoBarChart(cons, discreteValueList,
				discreteProbList, width);
		cons.removeFromConstructionList(algoBarChart);
		discreteGraph = algoBarChart.getOutput(0);
	}

	private void styleDiscreteGraph() {
		discreteGraph.setObjColor(colorPDF());
		discreteGraph.setAlphaValue(opacityDiscrete);
		discreteGraph.setLineThickness(thicknessBarChart);
		discreteGraph.setLayer(1);
		discreteGraph.setFixed(true);
		discreteGraph.setSelectionAllowed(false);
		discreteGraph.setEuclidianVisible(true);
	}

	private void createAxis() {
		GeoLine axis = new GeoLine(cons);
		axis.setCoords(0, 1, 0);
		axis.setLayer(4);
		axis.setObjColor(app.getEuclidianView1().getAxesColor());
		axis.setLineThickness(getDiscreteLineThickness());
		axis.setFixed(true);
		axis.setSelectionAllowed(false);
		axis.updateCascade();
		plotGeoList.add(axis);
	}

	private int getDiscreteLineThickness() {
		return discreteIntervalGraph == null ? discreteTwoTailedGraph.getLineThickness()
				: discreteIntervalGraph.getLineThickness();
	}

	/**
	 * Creates the integral graph by the given mode.
	 */
	protected void createIntegral() {
		GeoNumberValue xLowOutput = getXOutputFrom(xAxis.getLowExpression());
		GeoNumberValue xHighOutput = getXOutputFrom(xAxis.getHighExpression());
		if (isTwoTailedMode()) {
			GeoNumeric minX = new GeoNumeric(cons, Double.NEGATIVE_INFINITY);
			GeoNumeric maxX = new GeoNumeric(cons, Double.POSITIVE_INFINITY);
			cons.removeFromConstructionList(minX);
			cons.removeFromConstructionList(maxX);
			integralLeft = createIntegral(minX, xLowOutput);
			integralRight = createIntegral(xHighOutput, maxX);
			addTwoTailedGraph();
		} else {
			integral = createIntegral(xLowOutput, xHighOutput);
			setConditionToShow(integral);
			removeTwoTailedGraph();
		}
	}

	protected void addTwoTailedGraph() {
		plotGeoList.remove(integral);
		plotGeoList.add(integralLeft);
		plotGeoList.add(integralRight);
	}

	protected void removeTwoTailedGraph() {
		plotGeoList.remove(integralLeft);
		plotGeoList.remove(integralRight);
		plotGeoList.add(integral);
	}

	/**
	 * Set probability mode
	 * @param mode mode
	 */
	public void setProbabilityMode(int mode) {
		if (isCumulative) {
			probMode = PROB_LEFT;
		} else {
			int oldProbMode = probMode;
			if (oldProbMode == PROB_TWO_TAILED) {
				removeTwoTailedGraph();
			}
			probMode = mode;

			if (probMode == PROB_TWO_TAILED) {
				addTwoTailedGraph();
			}
			validateLowHigh(oldProbMode);
		}
		updateProbabilityType(getResultPanel());
		updateResult(getResultPanel());
	}

	private GeoElement createIntegral(GeoNumberValue low, GeoNumberValue high) {
		GeoBoolean f = new GeoBoolean(cons);
		f.setValue(false);

		AlgoIntegralDefinite algoIntegral = new AlgoIntegralDefinite(
				cons, densityCurve, low, high, f);
		cons.removeFromConstructionList(algoIntegral);

		GeoElement output = algoIntegral.getOutput(0);
		output.setObjColor(COLOR_PDF_FILL);
		output.setAlphaValue(opacityIntegral);
		output.setEuclidianVisible(showProbGeos);
		// make sure doesn't interfere with dragging of point
		output.setSelectionAllowed(false);
		plotGeoList.add(output);
		return output;
	}

	private void setConditionToShow(GeoElement integral) {
		AlgoDependentBoolean cond = new AlgoDependentBoolean(kernel.getConstruction(),
				new ExpressionNode(kernel, xAxis.getLowExpression(), Operation.LESS_EQUAL,
						xAxis.getHighExpression()), false);
		try {
			integral.setShowObjectCondition(cond.getGeoBoolean());
		} catch (CircularDefinitionException e) {
			// cannot happen because condition is not depending on integral
		}
	}

	private GeoNumberValue getXOutputFrom(ExpressionNode pointCoord) {
		AlgoDependentNumber x = new AlgoDependentNumber(cons, pointCoord,
				false);
		cons.removeFromConstructionList(x);
		return (GeoNumberValue) x.getOutput(0);
	}

	// =================================================
	// Geo Handlers
	// =================================================

	private GeoElementND createGeoFromString(String text) {
		return kernel.getAlgebraProcessor()
					.processAlgebraCommandNoExceptions(text, false)[0];
	}

	private void hideAllGeosFromViews() {
		for (GeoElementND geo : plotGeoList) {
			hideGeoFromViews(geo.toGeoElement());
		}
	}

	private void hideGeoFromViews(GeoElement geo) {
		// add the geo to our view and remove it from EV
		geo.addView(getPlotPanel().getViewID());
		getPlotPanel().add(geo);
		geo.removeView(App.VIEW_EUCLIDIAN);
		app.getEuclidianView1().remove(geo);
	}

	private void hideToolTips() {
		for (GeoElementND geo : plotGeoList) {
			geo.setTooltipMode(GeoElementND.TOOLTIP_OFF);
		}
	}

	/**
	 * Creates a step function for a discrete distribution.
	 * @param xList list with x values
	 * @param probList list with y = P(x) values
	 * @return AlgoPolyLine implementation of a step function
	 */
	public AlgoPolyLine createStepFunction(GeoList xList, GeoList probList) {

		// Extract x/y coordinates from the lists.
		double[] xCoords = new double[xList.size()];
		double[] yCoords = new double[probList.size()];
		int n = yCoords.length;
		for (int i = 0; i < n; i++) {
			xCoords[i] = xList.get(i).evaluateDouble();
			yCoords[i] = probList.get(i).evaluateDouble();
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

		return new AlgoPolyLine(cons, points);
	}

	/**
	 * @param mean mean
	 * @param sigma standard deviation
	 * @return normal curve overlay
	 */
	public GeoElement createNormalCurveOverlay(double mean, double sigma) {
		AlgoNormalDF algo = new AlgoNormalDF(cons, new GeoNumeric(cons, mean),
				new GeoNumeric(cons, sigma),
				new GeoBoolean(cons, isCumulative));
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
	 * @return plot width and height
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
	 * @param x number
	 * @return formatted number
	 */
	public String format(double x) {
		return kernel.format(x, getStringTemplate());
	}

	private StringTemplate getStringTemplate() {
		if (stringTemplate == null) {
			// override the default decimal place setting
			if (printDecimals >= 0) {
				int decimals = Math.max(printDecimals, 4);
				stringTemplate = StringTemplate.printDecimals(StringType.GEOGEBRA, decimals, false);
			} else {
				stringTemplate =
						StringTemplate.printFigures(StringType.GEOGEBRA, printFigures, false);
			}
		}
		return stringTemplate;
	}

	/**
	 * @param val value
	 * @return formatted string (definition if present, value otherwise)
	 */
	public String format(GeoNumberValue val) {
		return val.getRedefineString(false, false, StringTemplate.editorTemplate);
	}

	/**
	 * Calculates and sets the plot dimensions, the axes intervals and the point
	 * capture style for the the currently selected distribution.
	 */
	public void updatePlotSettings() {
		// get the plot window dimensions
		double[] d = getPlotDimensions();
		double xMin = d[0];
		double xMax = d[1];
		double yMin = d[2];
		double yMax = d[3] + (d[3] - d[2]) * PADDING_TOP_PX / plotPanel.getHeight() ;

		if (plotSettings == null) {
			plotSettings = new PlotSettings();
		}

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
			plotSettings.xAxesIntervalAuto = false;
			plotSettings.xAxesInterval = 1;
		} else {
			plotSettings.pointCaptureStyle = EuclidianStyleConstants.POINT_CAPTURING_OFF;
			plotSettings.xAxesIntervalAuto = true;
		}

		plotPanelUpdateSettings(plotSettings);
	}

	/**
	 * updates plot panel in subclasses
	 * @param settings plot settings
	 */
	protected abstract void plotPanelUpdateSettings(PlotSettings settings);

	/**
	 * Adjusts the interval control points to match the current low and high
	 * values. The low and high values are changeable from the input fields, so
	 * this method is called after a field change.
	 */
	public void setXAxisPoints() {
		isSettingAxisPoints = true;

		xAxis.lowPoint().setCoords(roundIfDiscrete(getLow()), 0.0, 1.0);
		xAxis.highPoint().setCoords(roundIfDiscrete(getHigh()), 0.0, 1.0);
		getPlotPanel().repaint();
		GeoElement.updateCascade(pointList, getTempSet(), false);
		tempSet.clear();

		if (probManager.isDiscrete(selectedDist)) {
			selectProbabilityTableRows();
		}

		isSettingAxisPoints = false;
	}

	/**
	 * @return true if mode is ][
	 */
	public boolean isTwoTailedMode() {
		return probMode == PROB_TWO_TAILED;
	}

	/**
	 * Remove plot geos
	 */
	public void removeGeos() {
		if (pointList != null) {
			pointList.clear();
		}
		clearPlotGeoList();
		getPlotPanel().clearView();
	}

	/**
	 * Creates two GeoLists: discreteProbList and discreteValueList. These store
	 * the probabilities and values of the currently selected discrete
	 * distribution.
	 */
	private void createDiscreteLists() {
		DiscreteProbability discreteProbability =
				discreteDistributionFactory.create(selectedDist, parameters, isCumulative);

		discreteValueList = discreteProbability.values();
		discreteProbList = discreteProbability.probabilities();

		plotGeoList.add(discreteProbList);
		discreteProbList.setEuclidianVisible(true);
		discreteProbList.setAuxiliaryObject(true);
		discreteProbList.setLabelVisible(false);
		discreteProbList.setFixed(true);
		discreteProbList.setSelectionAllowed(false);
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

	private TreeSet<AlgoElement> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<>();
		}
		return tempSet;
	}

	/**
	 * Exports all GeoElements that are currently displayed in this panel to a
	 * target EuclidianView.
	 * @param euclidianViewID viewID of the target EuclidianView
	 */
	public void exportGeosToEV(int euclidianViewID) {

		app.setWaitCursor();
		ArrayList<GeoElementND> newGeoList = new ArrayList<>();
		String expr;

		try {
			app.storeUndoInfo();

			// some commented out code removed 2012-3-1

			// create low point
			expr = "Point[" + loc.getMenu("xAxis") + "]";
			GeoPoint lowPointCopy = (GeoPoint) createGeoFromString(expr);
			lowPointCopy.setVisualStyle(xAxis.lowPoint());
			lowPointCopy.setLabelVisible(false);
			lowPointCopy.setCoords(getLow(), 0, 1);
			lowPointCopy.updateVisualStyleRepaint(GProperty.COMBINED);
			newGeoList.add(lowPointCopy);

			// create high point
			GeoPoint highPointCopy = (GeoPoint) createGeoFromString(expr);
			highPointCopy.setVisualStyle(xAxis.lowPoint());
			highPointCopy.setLabelVisible(false);
			highPointCopy.setCoords(getHigh(), 0, 1);
			highPointCopy.updateVisualStyleRepaint(GProperty.COMBINED);
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
							+ "," + discreteProbListCopy.getLabel(
							StringTemplate.maxPrecision)
							+ ",0]";
				} else if (graphType == GRAPH_BAR) {
					expr = "BarChart["
							+ discreteValueListCopy
							.getLabel(StringTemplate.maxPrecision)
							+ "," + discreteProbListCopy.getLabel(
							StringTemplate.maxPrecision)
							+ ",1]";
				} else if (graphType == GRAPH_STEP) {
					expr = "StepGraph["
							+ discreteValueListCopy
							.getLabel(StringTemplate.maxPrecision)
							+ "," + discreteProbListCopy.getLabel(
							StringTemplate.maxPrecision)
							+ ",true]";
				}

				GeoElementND discreteGraphCopy = createGeoFromString(expr);
				discreteGraphCopy.setLabel(null);
				discreteGraphCopy.setVisualStyle(discreteGraph.toGeoElement());
				newGeoList.add(discreteGraphCopy);

				// create interval bar chart
				// ============================
				expr = sublist(discreteProbListCopy, lowPointCopy, highPointCopy, tpl);
				GeoElementND intervalProbList1 = createGeoFromString(expr);
				newGeoList.add(intervalProbList1);

				expr = sublist(discreteValueListCopy, lowPointCopy, highPointCopy, tpl);
				GeoElementND intervalValueList1 = createGeoFromString(expr);
				newGeoList.add(intervalValueList1);

				if (graphType == GRAPH_LINE) {
					expr = "BarChart[" + intervalValueList1.getLabel(tpl) + ","
							+ intervalProbList1.getLabel(tpl) + ",0]";
				} else if (graphType == GRAPH_STEP) {
					expr = "StepGraph[" + intervalValueList1.getLabel(tpl) + ","
							+ intervalProbList1.getLabel(tpl) + ",true]";
				} else {
					expr = "BarChart[" + intervalValueList1.getLabel(tpl) + ","
							+ intervalProbList1.getLabel(tpl) + ",1]";
				}

				GeoElementND discreteIntervalGraphCopy = createGeoFromString(
						expr);
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
					exportIntegral(newGeoList, densityCurveCopy, lowPointCopy, highPointCopy, tpl);
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
			ev.setAutomaticAxesNumberingDistance(plotSettings.xAxesIntervalAuto,
					0);
			ev.setAutomaticAxesNumberingDistance(plotSettings.yAxesIntervalAuto,
					1);
			if (!plotSettings.xAxesIntervalAuto) {
				ev.setAxesNumberingDistance(
						new GeoNumeric(cons, plotSettings.xAxesInterval), 0);
			}
			if (!plotSettings.yAxesIntervalAuto) {
				ev.setAxesNumberingDistance(
						new GeoNumeric(cons, plotSettings.yAxesInterval), 1);
			}
			ev.updateBackground();

			// remove the new geos from our temporary list
			newGeoList.clear();

		} catch (Exception e) {
			Log.debug(e);
			app.setDefaultCursor();
		}

		app.setDefaultCursor();
	}

	private String sublist(GeoElement discreteProbListCopy, GeoPoint lowPointCopy,
			GeoPoint highPointCopy, StringTemplate tpl) {
		double offset = 1
				- discreteValueAt(0)
				+ 0.5;
		String listLabel = discreteProbListCopy.getLabel(tpl);
		if (isTwoTailedMode()) {
			return "Join[First[" + listLabel + ", x("
					+ lowPointCopy.getLabel(tpl) + ")+" + offset + "], "
					+ "Take[" + listLabel
					+ ",x(" + highPointCopy.getLabel(tpl) + ")+" + offset + "]]";
		} else {
			return "Take[" + listLabel + ", x("
					+ lowPointCopy.getLabel(tpl) + ")+" + offset + ", x("
					+ highPointCopy.getLabel(tpl) + ")+" + offset + "]";
		}
	}

	private void exportIntegral(ArrayList<GeoElementND> newGeoList, GeoElement densityCurveCopy,
			GeoPoint lowPointCopy, GeoPoint highPointCopy, StringTemplate tpl) {
		if (isTwoTailedMode()) {
			exportSingleIntegral(newGeoList, densityCurveCopy, "Corner[1]",
					lowPointCopy.getLabel(tpl), tpl);
			exportSingleIntegral(newGeoList, densityCurveCopy, highPointCopy.getLabel(tpl),
					"Corner[3]", tpl);
		} else {
			exportSingleIntegral(newGeoList, densityCurveCopy, lowPointCopy.getLabel(tpl),
					highPointCopy.getLabel(tpl), tpl);
		}
	}

	private void exportSingleIntegral(ArrayList<GeoElementND> newGeoList,
			GeoElement densityCurveCopy, String lowPointLabel, String highPointLabel,
			StringTemplate tpl) {
		String expr = "Integral[" + densityCurveCopy.getLabel(tpl) + ", x("
				+ lowPointLabel + "), x("
				+ highPointLabel + ") , true ]";
		GeoElementND integralCopy = createGeoFromString(expr);
		integralCopy.setVisualStyle(integral);
		integralCopy.setLabel(null);
		newGeoList.add(integralCopy);
	}

	@Override
	public int getViewID() {
		return App.VIEW_PROBABILITY_CALCULATOR;
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		ProbabilityCalculatorSettings pcSettings =
				(ProbabilityCalculatorSettings) settings;
		setProbabilityCalculatorNoFire(pcSettings.getDistributionType(),
				pcSettings.getParameters(), pcSettings.isCumulative());
		this.probMode = pcSettings.getProbMode();
		if (pcSettings.isIntervalSet()) {
			setLow(pcSettings.getLow());
			setHigh(pcSettings.getHigh());
		}
		setShowNormalOverlay(((ProbabilityCalculatorSettings) settings).isOverlayActive());
		updateAll(!pcSettings.isIntervalSet());
		if (getStatCalculator() != null) {
			getStatCalculator().settingsChanged();
		}
	}

	/**
	 * Set random variable interval.
	 * @param low2 lower bound
	 * @param high2 upper bound
	 */
	public abstract void setInterval(double low2, double high2);

	@Override
	public void add(GeoElement geo) {
		// view does not handle external geos
	}

	@Override
	public void updatePreviewFromInputBar(GeoElement[] geos) {
		// view does not handle external geos
	}

	@Override
	public void remove(GeoElement geo) {
		// view does not handle external geos
	}

	@Override
	public void rename(GeoElement geo) {
		// view does not handle external geos
	}

	// Handles user point changes in the EV plot panel
	@Override
	public void update(GeoElement geo) {
		if (isSettingAxisPoints || isIniting) {
			return;
		}

		if (geo.equals(xAxis.lowPoint())) {
			if (isValidIntervalForDrag(xAxis.lowPoint().getInhomX(), getHigh())) {
				low = asNumeric(xAxis.lowPoint(), low);
				updateIntervalProbability();
				updateGUI();
				if (probManager.isDiscrete(selectedDist)) {
					selectProbabilityTableRows();
				}
			} else {
				setXAxisPoints();
			}
		}

		if (geo.equals(xAxis.highPoint())) {
			if (isValidIntervalForDrag(getLow(), xAxis.highPoint().getInhomX())) {
				high = asNumeric(xAxis.highPoint(), high);
				updateIntervalProbability();
				updateGUI();
				if (probManager.isDiscrete(selectedDist)) {
					selectProbabilityTableRows();
				}
			} else {
				setXAxisPoints();
			}
		}
		updateRounding();
	}

	protected void selectProbabilityTableRows() {
		int start = (int) roundToInt(getLow());
		int end = Math.min((int) roundToInt(getHigh()), getDiscreteXMax());
		if (table != null) {
			if (isTwoTailedMode()) {
				table.setTwoTailedSelection(start, end);
			} else {
				table.setSelectionByRowValue(start, end);
			}
		}
	}

	private GeoNumberValue asNumeric(GeoPoint point, GeoNumberValue number) {
		double value = point.getInhomX();
		if (number instanceof GeoNumeric) {
			((GeoNumeric) number).setValue(value);
			return number;
		}
		return new GeoNumeric(cons, value);
	}

	/**
	 * Returns an interval probability for the currently selected distribution
	 * and probability mode. If mode == PROB_INTERVAL then P(low &lt;= X &lt;= high)
	 * is returned. If mode == PROB_LEFT then P(low &lt;= X) is returned. If mode
	 * == PROB_RIGHT then P(X &lt;= high) is returned.
	 * @return probability of selected interval
	 */
	protected double intervalProbability() {
		return probManager.intervalProbability(roundIfDiscrete(getLow()),
				roundIfDiscrete(getHigh()),
				selectedDist, parameters, probMode);
	}

	private double roundIfDiscrete(double value) {
		return isDiscreteProbability() ? roundToInt(value) : value;
	}

	private static double roundToInt(double value) {
		double decimalPat = value - (int) value;
		return decimalPat < 0.5 ? Math.floor(value) : Math.ceil(value);
	}

	protected double rightProbability(double high) {
		return probManager.rightProbability(roundIfDiscrete(high), parameters, selectedDist);
	}

	protected double leftProbability() {
		return probManager.probability(roundIfDiscrete(getLow()), parameters, selectedDist, true);
	}

	/**
	 * Returns an inverse probability for a selected distribution.
	 * @param prob cumulative probability
	 * @return inverse probability
	 */
	public double inverseProbability(double prob) {
		return probManager.inverseProbability(selectedDist, prob, parameters);
	}

	/**
	 * @param probability to format.
	 * @return probability formatted as String
	 */
	public String getProbabilityText(double probability) {
		return probability >= 0 ? format(probability) : "?";
	}

	/**
	 * Update probability value and the graph
	 */
	public void updateIntervalProbability() {
		probability = intervalProbability();
		if (isDiscreteProbability()) {
			if (isTwoTailedMode()) {

				if (discreteTwoTailedGraph == null) {
					createIntegral();
				}
				leftProbability = leftProbability();
				rightProbability = rightProbability(getLow() == getHigh()
						? getHigh()
						: getHigh() - 1);
				discreteTwoTailedGraph.updateCascade();
			} else {
				this.discreteIntervalGraph.updateCascade();
			}
		} else if (hasIntegral) {
			if (isTwoTailedMode()) {
				leftProbability = leftProbability();
				rightProbability = rightProbability(getHigh());
				if (integralLeft == null) {
					createIntegral();
				}

				integralLeft.updateCascade();
				integralRight.updateCascade();
			} else {
				if (integral == null) {
					createIntegral();
				}
				this.integral.updateCascade();
			}
		}
	}

	/**
	 * Validation for typing -- less strict than dragging, probability out of domain considered 0
	 * @param xLow - interval min
	 * @param xHigh - interval max
	 * @return whether interval is valid for given mode
	 */
	// TODO remove this method if discrete input rounding is OK.
	public boolean isValidInterval(double xLow, double xHigh) {
		return true;
	}

	/**
	 * Validation for point dragging -- do not let the points out of sensible area
	 * @param xLow - interval min
	 * @param xHigh - interval max
	 * @return whether interval is valid for given mode
	 */
	public boolean isValidIntervalForDrag(double xLow, double xHigh) {
		// don't allow non-integer bounds for discrete dist.
		if (!isValidInterval(xLow, xHigh)) {
			return false;
		}

		boolean isValid = true;
		switch (selectedDist) {

		default:
			Log.debug("Unknown distribution: " + selectedDist);
			return true;
		case STUDENT:
		case CAUCHY:
		case LOGISTIC:
		case NORMAL:
			return true;
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
		case GAMMA:
		case WEIBULL:
		case LOGNORMAL:
			if (probMode != PROB_LEFT) {
				isValid = xLow >= 0;
			}
			break;
		case BETA:
			if (probMode != PROB_LEFT) {
				isValid = xLow >= 0;
			}
			if (probMode != PROB_RIGHT) {
				isValid &= xHigh <= 1;
			}
			break;
		case F:
			if (probMode != PROB_LEFT) {
				isValid = xLow > 0;
			}
			break;

		}

		return isValid;
	}

	/**
	 * @param parameter new user value
	 * @param index parameter index
	 * @return whether new value is valid and differs from the old one
	 */
	public boolean isValidParameterChange(double parameter, int index) {
		if (MyDouble.exactEqual(parameters[index].getDouble(), parameter)) {
			return false;
		}
		switch (selectedDist) {

		default:
			Log.debug("Unknown distribution");
			return true;
		case NORMAL:
			return true;
		case F:
		case STUDENT:
		case EXPONENTIAL:
		case WEIBULL:
		case POISSON:
			if (index == 0) {
				// all parameters must be positive
				return parameter > 0;
			}
			break;

		case CAUCHY:
		case LOGISTIC:
			if (index == 1) {
				// scale must be positive
				return parameter > 0;
			}
			break;

		case CHISQUARE:
			if (index == 0) {
				// df >= 1, integer
				return Math.floor(parameter) == parameter
						&& parameter >= 1;
			}
			break;

		case BINOMIAL:
			if (index == 0) {
				// n >= 0, integer
				return Math.floor(parameter) == parameter
						&& parameter >= 0;
			} else if (index == 1) {
				// p is probability value
				return parameter >= 0 && parameter <= 1;
			}
			break;

		case PASCAL:
			if (index == 0) {
				// n >= 1, integer
				return Math.floor(parameter) == parameter
						&& parameter >= 1;
			} else if (index == 1) {
				// p is probability value
				return parameter >= 0 && parameter <= 1;
			}
			break;

		case HYPERGEOMETRIC:
			if (index == 0) {
				// population size: N >= 1, integer
				return Math.floor(parameter) == parameter && parameter >= 1;
			} else if (index == 1) {
				// successes in the population: n >= 0 and <= N, integer
				return Math.floor(parameter) == parameter && parameter >= 0
						&& parameter <= parameters[0].getDouble();
			} else if (index == 2) {
				// sample size: s>= 1 and s<= N, integer
				return Math.floor(parameter) == parameter && parameter >= 1
						&& parameter <= parameters[0].getDouble();
			}
			break;

		// these distributions have no parameter restrictions
		// case DIST.NORMAL:
		// case DIST.LOGNORMAL:
		}

		return true;
	}

	/**
	 * Adjust local rounding constants to match global rounding constants and
	 * update GUI when needed
	 */
	private void updateRounding() {
		if (updateRoundingFlags()) {
			updateDiscreteTable();
			updateGUI();
		}
	}

	protected boolean updateRoundingFlags() {
		if (kernel.useSignificantFigures) {
			if (printFigures != kernel.getPrintFigures()) {
				printFigures = kernel.getPrintFigures();
				printDecimals = -1;
				stringTemplate = null;
				return true;
			}
		} else if (printDecimals != kernel.getPrintDecimals()) {
			printDecimals = kernel.getPrintDecimals();
			stringTemplate = null;
			return true;
		}
		return false;
	}

	protected abstract void updateDiscreteTable();

	protected abstract void updateGUI();

	/**
	 * @return whether selected probability distribution is discrete
	 */
	public boolean isDiscreteProbability() {
		return probManager.isDiscrete(selectedDist);
	}

	protected int[] generateFirstXLastXCommon() {
		int[] firstXLastX = new int[2];
		if (discreteValueList == null) {
			this.createDiscreteLists();
		}
		firstXLastX[0] = (int) discreteValueAt(0);
		firstXLastX[1] = (int) discreteValueAt(discreteValueList.size() - 1);

		return firstXLastX;
	}

	@Override
	final public void updateVisualStyle(GeoElement geo, GProperty prop) {
		update(geo);
	}

	/**
	 * Attach to kernel
	 */
	public void attachView() {
		// clearView();
		// kernel.notifyAddAll(this);
		kernel.attach(this);
	}

	/**
	 * Detach from kernel
	 */
	public void detachView() {
		removeGeos();
		kernel.detach(this);
		// plotPanel.detachView();
		// clearView();
		// kernel.notifyRemoveAll(this);
	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {
		// not needed for prob. calc
	}

	@Override
	public void repaintView() {
		// not needed for prob. calc
	}

	@Override
	public void reset() {
		// not needed for prob. calc
	}

	@Override
	public void clearView() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setMode(int mode, ModeSetter m) {
		// not needed for prob. calc
	}

	@Override
	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isShowing() {
		return app.showView(App.VIEW_PROBABILITY_CALCULATOR);
	}

	/**
	 * Builds a GeoFunction representation of a given density curve.
	 * @param type distribution type
	 * @param cumulative whether it's cumulative
	 * @return function
	 */
	private GeoFunction buildDensityCurveExpression(Dist type,
			boolean cumulative) {

		GeoNumberValue param1, param2 = null;

		if (parameters.length > 0) {
			param1 = parameters[0];
		} else {
			return null;
		}
		if (parameters.length > 1) {
			param2 = parameters[1];
		}

		AlgoDistributionDF ret = null;
		GeoBoolean cumulativeGeo = new GeoBoolean(cons, cumulative);
		switch (type) {
		case NORMAL:
		case STUDENT:
		case CHISQUARE:
		case F:
		case CAUCHY:
		case EXPONENTIAL:
		case BETA:
		case GAMMA:
		case WEIBULL:
			ret = CmdRealDistribution2Params.getAlgoDF(type, param1, param2, cumulativeGeo);
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
	 * @param sb XML builder
	 */
	public void getXML(StringBuilder sb) {

		if (selectedDist == null) {
			return;
		}

		sb.append("<probabilityCalculator>\n");
		sb.append("\t<distribution");

		sb.append(" type=\"");
		sb.append(selectedDist.ordinal());
		sb.append("\"");

		sb.append(" isCumulative=\"");
		sb.append(isCumulative ? "true" : "false");
		sb.append("\"");

		sb.append(" isOverlayActive=\"");
		sb.append(isShowNormalOverlay() ? "true" : "false");
		sb.append("\"");

		sb.append(" parameters=\"");
		for (GeoNumberValue parameter : parameters) {
			sb.append(parameter.getLabel(StringTemplate.xmlTemplate));
			sb.append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append("\"/>\n");

		sb.append("\t<interval");

		sb.append(" mode=\"");
		sb.append(this.probMode);
		sb.append("\"");

		sb.append(" low=\"");
		sb.append(getLow());
		sb.append("\"");

		sb.append(" high=\"");
		sb.append(getHigh());
		sb.append("\"/>\n");
		if (getStatCalculator() != null) {
			getStatCalculator().getXML(sb, !isDistributionTabOpen());
		}
		sb.append("</probabilityCalculator>\n");
	}

	protected abstract boolean isDistributionTabOpen();

	protected abstract StatisticsCalculator getStatCalculator();

	@Override
	public void startBatchUpdate() {
		// no batch needed
	}

	@Override
	public void endBatchUpdate() {
		// no batch needed
	}

	/**
	 * @return information about mean and standard deviation
	 */
	public String getMeanSigma() {
		Double[] val = probManager.getDistributionMeasures(selectedDist,
				parameters);

		// mean/sigma are undefined for the Cauchy distribution
		// and F-distribution with certain parameters

		String mean = val[0] == null ? "?" : format(val[0]);
		String sigma = val[1] == null ? "?" : format(val[1]);

		return Unicode.mu + " = " + mean + "   " + Unicode.sigma
				+ " = " + sigma;
	}

	public void setHigh(double highValue) {
		this.high = new GeoNumeric(cons, highValue);
	}

	public void setHigh(GeoNumberValue high) {
		this.high = high;
	}

	public void setLow(double lowValue) {
		this.low = new GeoNumeric(cons, lowValue);
	}

	public void setLow(GeoNumberValue low) {
		this.low = low;
	}

	/**
	 * @return whether overlay can be constructed
	 */
	public boolean isOverlayDefined() {
		return !((selectedDist == Dist.CAUCHY)
				|| (selectedDist == Dist.F && parameters[1].getDouble() < 4));
	}

	public String[][] getParameterLabels() {
		return parameterLabels;
	}

	/**
	 * @return the probability distribution manager
	 */
	public abstract ProbabilityManager getProbManager();

	public EuclidianView getPlotPanel() {
		return plotPanel;
	}

	public void setPlotPanel(EuclidianView plotPanel) {
		this.plotPanel = plotPanel;
	}

	protected ProbabilityTable getTable() {
		return table;
	}

	public void setTable(ProbabilityTable table) {
		this.table = table;
	}

	/**
	 * Updates the two graphs of tails.
	 */
	protected void updateDiscreteGraphs() {
		discreteGraph.update();
		if (isTwoTailedMode()) {
			discreteTwoTailedGraph.update();

		} else {
			discreteIntervalGraph.update();
		}
	}

	protected void updateOutputForIntervals() {
		hasIntegral = !isCumulative;
		createGeoElements();
		if (isDiscreteProbability()) {
			addRemoveTable(true);
		} else {
			addRemoveTable(false);
			densityCurve.update();
		}
		getPlotPanel().repaintView();
	}

	protected abstract void addRemoveTable(boolean showTable);

	/**
	 * update probability
	 * @param resultPanel - panel of results
	 */
	public void updateProbabilityType(ResultPanel resultPanel) {
		if (isIniting || parameters == null) {
			return;
		}

		boolean isDiscrete = isDiscreteProbability();
		int oldProbMode = probMode;
		updateOutputForIntervals();
		if (probMode == PROB_INTERVAL) {
			xAxis.showBothPoints(showProbGeos);
			resultPanel.showInterval();
		} else if (probMode == PROB_TWO_TAILED) {
			xAxis.showBothPoints(showProbGeos);
			showTwoTailed(resultPanel);
		} else if (probMode == PROB_LEFT) {
			resultPanel.showLeft();
			switchToLeftProbability(oldProbMode, isDiscrete);
		} else if (probMode == PROB_RIGHT) {
			resultPanel.showRight();
			switchToRightProbability(oldProbMode, isDiscrete);
		}

		// make result field editable for inverse probability calculation
		resultPanel.setResultEditable(probMode != PROB_INTERVAL && probMode != PROB_TWO_TAILED);

		if (isDiscrete) {
			setHigh(Math.round(getHigh()));
			setLow(Math.round(getLow()));

			// make sure arrow keys move points in 1s
			xAxis.setAnimationStep(1);
		} else {
			xAxis.setAnimationStep(0.1);
		}
		setXAxisPoints();
		updateIntervalProbability();
	}

	private void switchToLeftProbability(int oldProbMode, boolean isDiscrete) {
		if (oldProbMode == PROB_RIGHT) {
			setHighDefault();
		}

		if (isDiscrete) {
			setLow(discreteValueAt(0));
			updateOutputForIntervals();
		} else {
			setLowOffscreen();
		}
		xAxis.showHighOnly(showProbGeos);
	}

	private double discreteValueAt(int i) {
		return ((GeoNumeric) discreteValueList.get(i)).getDouble();
	}

	private void setLowOffscreen() {
		setLow(plotSettings.xMin - 1);
	}

	private void switchToRightProbability(int oldProbMode, boolean isDiscrete) {
		if (oldProbMode == PROB_LEFT) {
			setLowDefault();
		}

		if (isDiscrete) {
			setHigh(discreteValueAt(discreteValueList.size() - 1));
			updateOutputForIntervals();
		} else {
			setHighOffscreen();
		}

		xAxis.showLowOnly(showProbGeos);
	}

	protected void setLowDefault() {
		setLow(plotSettings.xMin
				+ 0.4 * (plotSettings.xMax - plotSettings.xMin));
	}

	private void setHighOffscreen() {
		setHigh(plotSettings.xMax + 1);
	}

	protected void setDefaultBounds() {
		setLowDefault();
		setHighDefault();

		if (probMode == PROB_LEFT) {
			setLow(isDiscreteProbability() ? getDiscreteXMin() : Double.NEGATIVE_INFINITY);
		}

		if (probMode == PROB_RIGHT) {
			setHigh(isDiscreteProbability() ? getDiscreteXMax() : Double.POSITIVE_INFINITY);
		}
	}

	protected void setHighDefault() {
		setHigh(getDefaultHigh());
	}

	private double getDefaultHigh() {
		return plotSettings.xMin
				+ 0.6 * (plotSettings.xMax - plotSettings.xMin);
	}

	private void showTwoTailed(ResultPanel resultPanel) {
		if (low.getDouble() == high.getDouble()) {
			resultPanel.showTwoTailedOnePoint();
		} else {
			resultPanel.showTwoTailed();
		}
		updateGreaterSign(resultPanel);
	}

	/**
	 * Updates result panel if it exists
	 */
	public void updateResult() {
		if (getResultPanel() != null) {
			updateResult(getResultPanel());
		}
	}

	protected void updateResult(ResultPanel resultPanel) {
		updateLowHigh(resultPanel);
		if (probMode == PROB_TWO_TAILED) {
			showTwoTailed(resultPanel);
			updateTwoTailedResults(resultPanel);
		} else {
			resultPanel.updateResult(getProbabilityText(probability));
		}
		resultPanel.setResultEditable(isResultEditable());
	}

	private void updateTwoTailedResults(ResultPanel resultPanel) {
		resultPanel.updateTwoTailedResult(
				getProbabilityText(leftProbability),
				getProbabilityText(rightProbability));
		updateGreaterSign(resultPanel);
		resultPanel.updateResult(getProbabilityText(leftProbability + rightProbability));
	}

	/**
	 * Sets &gt; or &gt;= on demand
	 * @param resultPanel to display.
	 */
	public void updateGreaterSign(ResultPanel resultPanel) {
		if (getHigh() == getLow()) {
			resultPanel.setGreaterThan();
		} else {
			resultPanel.setGreaterOrEqualThan();
		}
	}

	protected void updateLowHigh(ResultPanel resultPanel) {
		resultPanel.updateLowHigh(format(low), format(high));
	}

	private boolean isResultEditable() {
		return probMode != ProbabilityCalculatorView.PROB_INTERVAL
				&& !isTwoTailedMode();
	}

	/**
	 * Sets the distribution type. This will destroy all GeoElements and create
	 * new ones.
	 */
	protected void updateDistribution() {
		hasIntegral = !isCumulative;
		createGeoElements();

		if (isDiscreteProbability()) {
			updateDiscreteGraphs();
			addRemoveTable(true);
		} else {
			addRemoveTable(false);
			if (densityCurve != null) {
				densityCurve.update();
			}

			if (pdfCurve != null) {
				pdfCurve.update();
			}
			if (hasIntegral && integral != null) {
				integral.update();
			}
		}
		onDistributionUpdate();
	}

	protected abstract void onDistributionUpdate();

	public boolean isIniting() {
		return isIniting;
	}

	public double getRightProbability() {
		return rightProbability;
	}

	public double getLeftProbability() {
		return leftProbability;
	}

	public double getProbability() {
		return probability;
	}

	/**
	 * Call this when parameters change.
	 */
	public void onParameterUpdate() {
		updateOutput(false);
		updateResult();
	}

	/**
	 * @param disable whether to disable or not
	 */
	public void disableInterval(boolean disable) {
		// overridden for platform
	}
}
