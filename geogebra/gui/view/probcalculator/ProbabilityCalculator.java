package geogebra.gui.view.probcalculator;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.GuiManager;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.view.spreadsheet.statdialog.PlotPanelEuclidianView;
import geogebra.gui.view.spreadsheet.statdialog.PlotSettings;
import geogebra.kernel.AlgoBarChart;
import geogebra.kernel.AlgoDependentNumber;
import geogebra.kernel.AlgoDependentPoint;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.AlgoJoinPointsSegment;
import geogebra.kernel.AlgoListElement;
import geogebra.kernel.AlgoPointOnPath;
import geogebra.kernel.AlgoRayPointVector;
import geogebra.kernel.AlgoSequence;
import geogebra.kernel.AlgoTake;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionElement;
import geogebra.kernel.GeoAxis;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoRay;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;
import geogebra.kernel.View;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.MyVecNode;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.cas.AlgoIntegralDefinite;
import geogebra.kernel.statistics.AlgoBinomialDist;
import geogebra.kernel.statistics.AlgoHyperGeometric;
import geogebra.kernel.statistics.AlgoInversePascal;
import geogebra.kernel.statistics.AlgoInversePoisson;
import geogebra.kernel.statistics.AlgoPascal;
import geogebra.kernel.statistics.AlgoPoisson;
import geogebra.main.Application;
import geogebra.main.GeoGebraColorConstants;
import geogebra.main.settings.AbstractSettings;
import geogebra.main.settings.ProbabilityCalculatorSettings;
import geogebra.main.settings.SettingListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Dialog that displays the graphs of various probability density functions with
 * interactive controls for calculating interval probabilities.
 * 
 * @author G. Sturr
 * 
 */
public class ProbabilityCalculator extends JPanel 
implements View, ActionListener, FocusListener, ChangeListener, SettingListener   {

	// enable/disable integral ---- use for testing
	private boolean hasIntegral = true; 

	//ggb fields
	private Application app;
	private Construction cons;
	private Kernel kernel; 
	private ProbabilityManager probManager;
	private ProbabiltyCalculatorStyleBar styleBar;


	// selected distribution mode
	private int selectedDist = ProbabilityManager.DIST_NORMAL;  // default: startup with normal distribution


	// distribution fields 
	private String[][] parameterLabels;
	private final static int maxParameterCount = 3; // maximum number of parameters allowed for a distribution
	private double[] parameters;
	private boolean isCumulative = false;
	private boolean isLineGraph = false;

	// maps for the distribution ComboBox 
	private HashMap<Integer, String> distributionMap;
	private HashMap<String, Integer> reverseDistributionMap;


	// GeoElements
	private ArrayList<GeoElement> plotGeoList;
	private GeoPoint lowPoint, highPoint, curvePoint;
	private GeoElement densityCurve, integral, ySegment, xSegment;
	private GeoElement discreteGraph, discreteIntervalGraph;
	private GeoList discreteValueList, discreteProbList, intervalProbList, intervalValueList;
	//private GeoList parmList;
	private ArrayList<GeoElement> pointList;


	// GUI elements
	private JComboBox comboDistribution, comboProbType;
	private JTextField[] fldParameterArray;
	private JTextField fldLow,fldHigh,fldResult;
	private JLabel[] lblParameterArray;
	private JLabel lblBetween, lblProbOf, lblEndProbOf,lblProb , lblDist;

	private JSlider[] sliderArray;
	private ListSeparatorRenderer comboRenderer;


	// GUI layout panels
	private JPanel controlPanel, distPanel, probPanel, tablePanel;
	private JSplitPane mainSplitPane, plotSplitPane;;	
	private int defaultDividerSize;  
	private PlotPanelEuclidianView plotPanel;
	private PlotSettings plotSettings;
	private ProbabilityTable table;


	// initing
	private boolean isIniting;
	private boolean isSettingAxisPoints = false;


	// probability calculation modes
	protected static final int PROB_INTERVAL = 0;
	protected static final int PROB_LEFT = 1;
	protected static final int PROB_RIGHT = 2;
	private int probMode = PROB_INTERVAL;


	//interval values
	private double low = 0, high = 1;

	// current probability result
	private double probability;


	// rounding 
	private int printDecimals = 4,  printFigures = -1;

	// valid prob interval flag
	boolean validProb;

	// colors
	private static final Color COLOR_PDF = GeoGebraColorConstants.DARKBLUE;
	private static final Color COLOR_PDF_FILL = GeoGebraColorConstants.BLUE;  
	private static final Color COLOR_POINT = Color.BLACK;

	private static final float opacityIntegral = 0.5f; 
	private static final float opacityDiscrete = 0.0f; // entire bar chart
	private static final float opacityDiscreteInterval = 0.5f; // bar chart interval
	private static final int thicknessCurve = 4;
	private static final int thicknessBarChart = 3;


	private boolean removeFromConstruction = true;



	private static final double nearlyOne = 1 - 1E-6;



	/*************************************************
	 * Construct the dialog
	 */
	public ProbabilityCalculator(Application app) {

		isIniting = true;
		this.app = app;	
		kernel = app.getKernel();
		cons = kernel.getConstruction();

		// Initialize settings and register listener
		app.getSettings().getProbCalcSettings().addListener(this);

		probManager = new ProbabilityManager(app, this);
		plotSettings = new PlotSettings();	
		plotGeoList = new ArrayList<GeoElement>();

		createLayoutPanels();
		buildLayout();
		isIniting = false;


		//setProbabilityCalculator(selectedDist, null, isCumulative);

		attachView();
		settingsChanged(app.getSettings().getProbCalcSettings());

	} 
	/**************** end constructor ****************/




	public void setProbabilityCalculator(int distributionType, double[] parameters, boolean isCumulative){

		this.selectedDist = distributionType;
		this.isCumulative = isCumulative;
		this.parameters = parameters;
		if(parameters == null)
			this.parameters = probManager.getDefaultParameterMap().get(selectedDist);	

		//this.buildLayout();
		//isIniting = true;
		updateAll();
		//isIniting = false;
	}


	/**
	 * @return The style bar for this view.
	 */
	public ProbabiltyCalculatorStyleBar getStyleBar() {
		if(styleBar == null) {
			styleBar = new ProbabiltyCalculatorStyleBar(app, this);
		}

		return styleBar;
	}



	//=================================================
	//       Getters/Setters
	//=================================================

	public ProbabilityManager getProbManager() {
		return probManager;
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
	public void setCumulative(boolean isCumulative) {

		if(this.isCumulative == isCumulative) return;

		this.isCumulative = isCumulative;
		
		// in cumulative mode only left-sided intervals are allowed
		setProbabilityComboBoxMenu();
		if(!isCumulative)
			// make sure left-sided is still selected when reverting to non-cumulative mode
			comboProbType.setSelectedIndex(PROB_LEFT);
		updateAll();
		
	}

	public boolean isLineGraph() {
		return isLineGraph;
	}
	
	public void setLineGraph(boolean isLineGraph) {
		if(this.isLineGraph = isLineGraph) return;
		
		this.isLineGraph = isLineGraph;
		updateAll();
	}


	public int getPrintDecimals() {
		return printDecimals;
	}
	public int getPrintFigures() {
		return printFigures;
	}
	public PlotSettings getPlotSettings() {
		return plotSettings;
	}
	public void setPlotSettings(PlotSettings plotSettings) {
		this.plotSettings = plotSettings;
	}
	//=================================================
	//       GUI
	//=================================================


	private void createLayoutPanels() {

		try {		

			// control panel 
			//======================================================
			distPanel = this.createDistributionPanel();	
			probPanel = this.createProbabilityPanel();
			//distPanel.setBorder(BorderFactory.createEtchedBorder());
			//probPanel.setBorder(BorderFactory.createEtchedBorder());

			//probPanel.setBorder(BorderFactory.createCompoundBorder(
			//		BorderFactory.createEmptyBorder(2, 2, 2, 2),
			//		BorderFactory.createEtchedBorder())); 
			//distPanel.setBorder(probPanel.getBorder());


			Box vBox = Box.createVerticalBox();
			vBox.add(distPanel);
			vBox.add(probPanel);

			controlPanel = new JPanel(new BorderLayout());
			controlPanel.add(vBox, BorderLayout.NORTH);
			controlPanel.setBorder(BorderFactory.createEmptyBorder());

			controlPanel.setMinimumSize(controlPanel.getPreferredSize());



			// plot panel (extension of EuclidianView)
			//======================================================
			plotPanel = new PlotPanelEuclidianView(app.getKernel());
			plotPanel.setMouseEnabled(true);
			plotPanel.setMouseMotionEnabled(true);

			plotPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(2, 2, 2, 2),
					BorderFactory.createBevelBorder(BevelBorder.LOWERED))); 

			plotPanel.setBorder(BorderFactory.createEmptyBorder());

			plotPanel.setBorder(BorderFactory.createEmptyBorder());

			addPlotPanelExportMenu(plotPanel);


			// table panel
			//======================================================
			table = new ProbabilityTable(app, this);
			table.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, SystemColor.controlShadow));
			tablePanel = new JPanel(new BorderLayout());
			tablePanel.add(table, BorderLayout.CENTER);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void buildLayout(){

		this.removeAll();


		plotSplitPane = new JSplitPane();
		plotSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		plotSplitPane.setLeftComponent(plotPanel);
		plotSplitPane.setResizeWeight(1);
		plotSplitPane.setBorder(BorderFactory.createEmptyBorder());
		defaultDividerSize = plotSplitPane.getDividerSize();

		JScrollPane scroller = new JScrollPane(controlPanel);
		scroller.setBorder(BorderFactory.createEmptyBorder());

		mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, plotSplitPane, scroller);
		mainSplitPane.setResizeWeight(1);
		mainSplitPane.setBorder(BorderFactory.createEmptyBorder());

		this.setLayout(new BorderLayout());
		this.add(mainSplitPane, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createEmptyBorder(0,2,2,2));

		setLabels();
	}




	private void addRemoveTable(boolean showTable){
		if(showTable){
			plotSplitPane.setRightComponent(tablePanel);
			plotSplitPane.setDividerSize(defaultDividerSize);
		}else{
			plotSplitPane.setRightComponent(null);
			plotSplitPane.setDividerSize(0);
		}
	}

	private ListSeparatorRenderer getComboRenderer(){
		if (comboRenderer == null)
			comboRenderer = new ListSeparatorRenderer();
		return comboRenderer;

	}


	private JPanel createDistributionPanel(){

		setLabelArrays();
		comboDistribution = new JComboBox();
		comboDistribution.setRenderer(getComboRenderer());
		comboDistribution.setMaximumRowCount(ProbabilityManager.distCount+1);
		//setComboDistribution();
		comboDistribution.addActionListener(this);
		lblDist = new JLabel();

		JPanel cbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//	cbPanel.add(lblDist);
		cbPanel.add(comboDistribution);



		// create parameter panel
		JPanel parameterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//parameterPanel.setAlignmentX(0.0f);
		//	parameterPanel.add(comboDistribution);

		lblParameterArray = new JLabel[maxParameterCount];
		fldParameterArray = new JTextField[maxParameterCount];
		//	sliderArray = new JSlider[maxParameterCount];

		for(int i = 0; i < maxParameterCount; ++i){
			lblParameterArray[i] = new JLabel();
			fldParameterArray[i] = new MyTextField(app);
			fldParameterArray[i].setColumns(6);
			fldParameterArray[i].addActionListener(this);
			fldParameterArray[i].addFocusListener(this);
			//	sliderArray[i] = new JSlider();
			//	sliderArray[i].setPreferredSize(fldParameterArray[i].getPreferredSize());
			//	sliderArray[i].addChangeListener(this);


			Box hBox = Box.createHorizontalBox();
			hBox.add(Box.createRigidArea(new Dimension(3,0)));
			hBox.add(lblParameterArray[i]);
			hBox.add(Box.createRigidArea(new Dimension(3,0)));
			JPanel labelPanel = new JPanel(new BorderLayout());
			labelPanel.add(hBox, BorderLayout.NORTH);

			JPanel fldSliderPanel = new JPanel(new BorderLayout());
			fldSliderPanel.add(fldParameterArray[i], BorderLayout.NORTH);
			//	fldSliderPanel.add(sliderArray[i], BorderLayout.SOUTH);

			JPanel fullParmPanel = new JPanel(new BorderLayout());
			fullParmPanel.add(labelPanel, BorderLayout.WEST);
			fullParmPanel.add(fldSliderPanel, BorderLayout.EAST);

			parameterPanel.add(fullParmPanel);

		}

		// put the parameter panel in WEST of a new JPanel and return the result
		JPanel distPanel = new JPanel(new BorderLayout());
		distPanel.setLayout (new FlowLayout(FlowLayout.LEFT));
		distPanel.add(cbPanel);
		distPanel.add(parameterPanel);

		return distPanel;
	}



	private JPanel createProbabilityPanel(){

		// create probability mode JComboBox and put it in a JPanel
		comboProbType = new JComboBox();
		comboProbType.setRenderer(getComboRenderer());
		comboProbType.addActionListener(this);
		lblProb = new JLabel();

		JPanel cbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//	cbPanel.add(lblProb);
		cbPanel.add(comboProbType);


		lblProbOf = new JLabel();
		lblBetween = new JLabel();   // <= X <=
		lblEndProbOf = new JLabel();
		fldLow = new MyTextField(app);
		fldLow.setColumns(6);
		fldLow.addActionListener(this);
		fldLow.addFocusListener(this);

		fldHigh = new MyTextField(app);
		fldHigh.setColumns(6);
		fldHigh.addActionListener(this);
		fldHigh.addFocusListener(this);

		fldResult = new MyTextField(app);
		fldResult.setColumns(6);
		fldResult.addActionListener(this);
		fldResult.addFocusListener(this);

		// create panel to hold the entry fields
		JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		//	fieldPanel.add(cbPanel);

		Box hBox = Box.createHorizontalBox();
		hBox.add(lblProbOf);
		hBox.add(Box.createRigidArea(new Dimension(3,0)));
		hBox.add(fldLow);
		hBox.add(Box.createRigidArea(new Dimension(3,0)));
		hBox.add(lblBetween);
		hBox.add(Box.createRigidArea(new Dimension(3,0)));
		hBox.add(fldHigh);
		hBox.add(Box.createRigidArea(new Dimension(3,0)));
		hBox.add(lblEndProbOf);
		fieldPanel.add(hBox);
		fieldPanel.add(fldResult);


		// put all sub-panels together and return the result 
		//	JPanel probPanel = new JPanel(new BorderLayout());
		//	probPanel.add(fieldPanel,BorderLayout.CENTER);
		//	probPanel.add(cbPanel,BorderLayout.WEST);

		JPanel probPanel = new JPanel();
		probPanel.setLayout (new FlowLayout(FlowLayout.LEFT));
		probPanel.add(cbPanel);
		probPanel.add(fieldPanel);


		return probPanel;

	}




	//=================================================
	//       Plotting
	//=================================================


	/**
	 * Creates the required GeoElements for the currently selected distribution
	 * type and parameters. 
	 */
	private void createGeoElements(){

		this.removeGeos();

		String expr;

		//create low point

		GeoAxis path = (GeoAxis)kernel.lookupLabel(app.getPlain("xAxis"));

		AlgoPointOnPath algoLow = new AlgoPointOnPath(cons, (Path)path, 0d, 0d);
		cons.removeFromConstructionList(algoLow);

		lowPoint = (GeoPoint) algoLow.getGeoElements()[0];

		lowPoint.setObjColor(COLOR_POINT);
		lowPoint.setPointSize(4);
		lowPoint.setPointStyle(EuclidianView.POINT_STYLE_TRIANGLE_NORTH);
		lowPoint.setLayer(5);
		plotGeoList.add(lowPoint);


		// create high point

		AlgoPointOnPath algoHigh = new AlgoPointOnPath(cons, (Path)path, 0d, 0d);
		cons.removeFromConstructionList(algoHigh);

		highPoint = (GeoPoint) algoHigh.getGeoElements()[0];

		highPoint.setObjColor(COLOR_POINT);
		highPoint.setPointSize(4);
		highPoint.setPointStyle(EuclidianView.POINT_STYLE_TRIANGLE_NORTH);
		highPoint.setLayer(5);
		plotGeoList.add(highPoint);

		pointList = new ArrayList<GeoElement>();
		pointList.add(lowPoint);
		pointList.add(highPoint);

		// Set the axis points so they are not equal. This needs to be done 
		// before the integral geo is created.
		setXAxisPoints();







		if(probManager.isDiscrete(selectedDist)){   

			// discrete distribution 
			// ====================================================

			// create discrete bar graph and associated lists
			createDiscreteLists();
			//expr = "BarChart[" + discreteValueList.getLabel() + "," + discreteProbList.getLabel() + "]";


			AlgoBarChart algoBarChart;
			if(isLineGraph){
				NumberValue zeroWidth = new GeoNumeric(cons, 0);
				algoBarChart = new AlgoBarChart(cons, discreteValueList, discreteProbList, zeroWidth);
			}else{
				algoBarChart = new AlgoBarChart(cons, discreteValueList, discreteProbList);
			}
			cons.removeFromConstructionList(algoBarChart);


			//discreteGraph = createGeoFromString(expr);
			discreteGraph = algoBarChart.getGeoElements()[0];
			discreteGraph.setObjColor(COLOR_PDF);
			discreteGraph.setAlphaValue(opacityDiscrete);
			discreteGraph.setLineThickness(thicknessBarChart);
			discreteGraph.setLayer(1);
			discreteGraph.setFixed(true);
			discreteGraph.setEuclidianVisible(true);
			plotGeoList.add(discreteGraph);


			// create discrete interval bar graph and associated lists
			
			// Use Take[] to create a subset of the full discrete graph:
			//     Take[discreteList, x(lowPoint) + offset, x(highPoint) + offset] 
			//
			// The offset accounts for Sequence[] starting its count at 1 and 
			// the starting value of the discrete x list. Thus,
			//     offset = 1 - lowest discrete x value 

			double  firstX = ((GeoNumeric)discreteValueList.get(0)).getDouble();
			MyDouble offset = new MyDouble(kernel, 1d - firstX);

			ExpressionNode low = new ExpressionNode(kernel, lowPoint, ExpressionNode.XCOORD, null);
			ExpressionNode high = new ExpressionNode(kernel, highPoint, ExpressionNode.XCOORD, null);				
			ExpressionNode lowPlusOffset = new ExpressionNode(kernel, low, ExpressionNode.PLUS, offset);
			ExpressionNode highPlusOffset = new ExpressionNode(kernel, high, ExpressionNode.PLUS, offset);	
			
			AlgoDependentNumber xLow;
			if(isCumulative)
				// for cumulative bar graphs we only show a single bar
				xLow = new AlgoDependentNumber(cons, highPlusOffset, false);
			else
				xLow = new AlgoDependentNumber(cons, lowPlusOffset, false);
			cons.removeFromConstructionList(xLow);
			
			AlgoDependentNumber xHigh = new AlgoDependentNumber(cons, highPlusOffset, false);
			cons.removeFromConstructionList(xHigh);

			
			AlgoTake take = new AlgoTake(cons, (GeoList)discreteValueList, 
					(GeoNumeric) xLow.getGeoElements()[0], (GeoNumeric) xHigh.getGeoElements()[0]);
			cons.removeFromConstructionList(take);
			intervalValueList = (GeoList) take.getGeoElements()[0];
			
			AlgoTake take2 = new AlgoTake(cons, (GeoList)discreteProbList, 
					(GeoNumeric)xLow.getGeoElements()[0], (GeoNumeric)xHigh.getGeoElements()[0]);
			cons.removeFromConstructionList(take2);
			intervalProbList = (GeoList) take2.getGeoElements()[0];


			AlgoBarChart barChart;
			if(isLineGraph){
				NumberValue zeroWidth2 = new GeoNumeric(cons, 0);
				barChart = new AlgoBarChart(cons, intervalValueList, intervalProbList, zeroWidth2);
			}else{
				barChart = new AlgoBarChart(cons, intervalValueList, intervalProbList);
			}
			cons.removeFromConstructionList(barChart);


			discreteIntervalGraph = barChart.getGeoElements()[0];

			//System.out.println(text);
			if(isLineGraph){
				discreteIntervalGraph.setObjColor(this.COLOR_PDF_FILL);
				discreteIntervalGraph.setLineThickness(thicknessBarChart+2);
			}
			else{
				discreteIntervalGraph.setObjColor(this.COLOR_PDF_FILL);
				discreteIntervalGraph.setAlphaValue(opacityDiscreteInterval);
				discreteIntervalGraph.setLineThickness(thicknessBarChart);
			}

			discreteIntervalGraph.setEuclidianVisible(true);
			discreteIntervalGraph.setLayer(discreteGraph.getLayer()+1);
			discreteIntervalGraph.setFixed(true);
			discreteIntervalGraph.updateCascade();
			plotGeoList.add(discreteIntervalGraph);


			GeoLine axis = new GeoLine(cons);		
			axis.setCoords(0, 1, 0);
			axis.setLayer(4);
			axis.setObjColor(app.getEuclidianView().getAxesColor());
			axis.setLineThickness(discreteIntervalGraph.lineThickness);
			axis.setFixed(true);
			axis.updateCascade();
			plotGeoList.add(axis);

		}else{ 

			// continuous distribution
			// ====================================================

			// create density curve
			expr = buildDensityCurveExpression(selectedDist);

			densityCurve = createGeoFromString(expr, null, true);

			cons.removeFromConstructionList(densityCurve.getParentAlgorithm());

			densityCurve.setObjColor(COLOR_PDF);
			densityCurve.setLineThickness(thicknessCurve);
			densityCurve.setFixed(true);
			densityCurve.setEuclidianVisible(true);
			plotGeoList.add(densityCurve);


			if(hasIntegral ){
				GeoBoolean f = new GeoBoolean(cons);
				f.setValue(false);

				ExpressionNode low = new ExpressionNode(kernel, lowPoint, ExpressionNode.XCOORD, null);
				ExpressionNode high = new ExpressionNode(kernel, highPoint, ExpressionNode.XCOORD, null);				

				AlgoDependentNumber xLow = new AlgoDependentNumber(cons, low, false);
				cons.removeFromConstructionList(xLow);
				AlgoDependentNumber xHigh = new AlgoDependentNumber(cons, high, false);
				cons.removeFromConstructionList(xHigh);

				AlgoIntegralDefinite algoIntegral = new AlgoIntegralDefinite(cons, (GeoFunction)densityCurve, (NumberValue) xLow.getGeoElements()[0], (NumberValue) xHigh.getGeoElements()[0], f);
				cons.removeFromConstructionList(algoIntegral);

				integral = algoIntegral.getGeoElements()[0];
				integral.setObjColor(COLOR_PDF_FILL);
				integral.setAlphaValue(opacityIntegral);
				integral.setEuclidianVisible(true);
				plotGeoList.add(integral);
			}



			if(isCumulative){
				
				// point on curve 
				GeoFunction f = (GeoFunction)densityCurve;	
				AlgoPointOnPath pAlgo = new AlgoPointOnPath(cons, (Path)f, highPoint.getInhomX(), 0);
				cons.removeFromConstructionList(pAlgo);
				curvePoint = (GeoPoint) pAlgo.getGeoElements()[0];
				
				
				/*
				ExpressionNode highPointX = new ExpressionNode(kernel, highPoint, ExpressionNode.XCOORD, null);
				
				// need expression for f evaluated xcoord of highPoint
				ExpressionNode curveY = new ExpressionNode(???????);
				
				MyVecNode curveVec = new MyVecNode( kernel, highPointX, curveY);
				ExpressionNode curvePointNode = new ExpressionNode(kernel, curveVec, ExpressionNode.NO_OPERATION, null);
				curvePointNode.setForcePoint();

				AlgoDependentPoint pAlgo = new AlgoDependentPoint(cons, curvePointNode, false);
				cons.removeFromConstructionList(pAlgo);
				*/
				
				
				curvePoint = (GeoPoint) pAlgo.getGeoElements()[0];
				curvePoint.setObjColor(COLOR_POINT);
				curvePoint.setPointSize(4);
				curvePoint.setLayer(f.getLayer()+1);
				plotGeoList.add(curvePoint);

				// create vertical line segment 
				ExpressionNode xcoord = new ExpressionNode(kernel, curvePoint, ExpressionNode.XCOORD, null);
				MyVecNode vec = new MyVecNode( kernel, xcoord, new MyDouble(kernel, 0.0));
				ExpressionNode point = new ExpressionNode(kernel, vec, ExpressionNode.NO_OPERATION, null);
				point.setForcePoint();
				AlgoDependentPoint pointAlgo = new AlgoDependentPoint(cons, point, false);
				cons.removeFromConstructionList(pointAlgo);

				AlgoJoinPointsSegment seg1 = new AlgoJoinPointsSegment(cons, curvePoint, (GeoPoint)pointAlgo.getGeoElements()[0], null);
				cons.removeFromConstructionList(seg1);	
				xSegment = (GeoSegment)seg1.getGeoElements()[0];
				xSegment.setObjColor(Color.blue);
				xSegment.setLineThickness(3);
				xSegment.setLineType(EuclidianView.LINE_TYPE_DASHED_SHORT);
				xSegment.setEuclidianVisible(true);
				xSegment.setFixed(true);
				plotGeoList.add(xSegment);

				// create horizontal ray 
				ExpressionNode ycoord = new ExpressionNode(kernel, curvePoint, ExpressionNode.YCOORD, null);
				MyVecNode vecy = new MyVecNode( kernel, new MyDouble(kernel, 0.0), ycoord);
				ExpressionNode pointy = new ExpressionNode(kernel, vecy, ExpressionNode.NO_OPERATION, null);
				pointy.setForcePoint();	
				GeoVector v = new GeoVector(cons);
				v.setCoords(-1d, 0d, 1d);

				AlgoRayPointVector seg2 = new AlgoRayPointVector(cons, curvePoint, v);
				cons.removeFromConstructionList(seg2);
				ySegment = (GeoRay)seg2.getGeoElements()[0];
				ySegment.setObjColor(Color.red);
				ySegment.setLineThickness(3);
				ySegment.setLineType(EuclidianView.LINE_TYPE_FULL);
				ySegment.setEuclidianVisible(true);
				ySegment.setFixed(true);
				plotGeoList.add(ySegment);
			}

		}

		hideAllGeosFromViews();
		//labelAllGeos();
		hideToolTips();

	}


	/**
	 * Calculates and sets the plot dimensions, the axes intervals and the point
	 * capture style for the the currently selected distribution.
	 */
	protected void updatePlotSettings(){

		double xMin, xMax, yMin, yMax;

		// get the plot window dimensions
		double[] d = getPlotDimensions();
		xMin = d[0]; xMax = d[1]; yMin = d[2]; yMax = d[3];

		//System.out.println(d[0] + "," + d[1] + "," + d[2] + "," + d[3]);

		if(plotSettings == null)
			plotSettings = new PlotSettings();	

		plotSettings.xMin = xMin;
		plotSettings.xMax = xMax;
		plotSettings.yMin = yMin;
		plotSettings.yMax = yMax;

		// don't show the y-axis for continuous case (edge axis looks bad)
		//	plotSettings.showYAxis = probManager.isDiscrete(selectedDist);
		plotSettings.showYAxis = true;


		plotSettings.isEdgeAxis[0] = false;
		plotSettings.isEdgeAxis[1] = true;
		plotSettings.forceXAxisBuffer = true;

		if(probManager.isDiscrete(selectedDist)){
			// discrete axis points should jump from point to point 
			plotSettings.pointCaptureStyle = EuclidianView.POINT_CAPTURING_ON_GRID;
			//TODO --- need an adaptive setting here for when we have too many intervals
			plotSettings.gridInterval[0] = 1;
			plotSettings.gridIntervalAuto = false;
			plotSettings.xAxesIntervalAuto = true;
		}
		else
		{	
			plotSettings.pointCaptureStyle = EuclidianView.POINT_CAPTURING_OFF;
			plotSettings.xAxesIntervalAuto = true;
			plotPanel.updateSettings(plotSettings);
		}	

		plotPanel.updateSettings(plotSettings);

	}


	/**
	 * Adjusts the interval control points to match the current low and high
	 * values. The low and high values are changeable from the input fields, so
	 * this method is called after a field change.
	 */
	public void setXAxisPoints(){

		isSettingAxisPoints = true;

		lowPoint.setCoords(low, 0.0, 1.0);
		highPoint.setCoords(high, 0.0, 1.0);
		plotPanel.repaint();
		GeoElement.updateCascade(pointList, getTempSet(), false);
		tempSet.clear();

		if(probManager.isDiscrete(selectedDist))
			table.setSelectionByRowValue((int)low, (int)high);

		isSettingAxisPoints = false;
	}


	private TreeSet tempSet;
	private TreeSet getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet();
		}
		return tempSet;
	}





	/**
	 * Returns an interval probability for the currently selected distribution and probability mode.
	 * If mode == PROB_INTERVAL then P(low <= X <= high) is returned.
	 * If mode == PROB_LEFT then P(low <= X) is returned.
	 * If mode == PROB_RIGHT then P(X <= high) is returned.
	 */
	private double intervalProbability(){

		return probManager.intervalProbability(low, high, selectedDist, parameters, probMode);
	}



	/**
	 * Returns an inverse probability for a selected distribution.
	 * @param prob 
	 */
	private double inverseProbability(double prob){

		return probManager.inverseProbability(selectedDist, prob, parameters);
	}



	private boolean isValidInterval(double xLow, double xHigh){

		if (xHigh < xLow) return false;

		boolean isValid = true;
		switch (selectedDist){

		case ProbabilityManager.DIST_BINOMIAL:
		case ProbabilityManager.DIST_HYPERGEOMETRIC: 
			isValid = xLow >= getDiscreteXMin() && xHigh <= getDiscreteXMax();
			break;

		case ProbabilityManager.DIST_POISSON: 
		case ProbabilityManager.DIST_PASCAL: 
			isValid = xLow >= getDiscreteXMin();   
			break;


		case ProbabilityManager.DIST_CHISQUARE:
		case ProbabilityManager.DIST_EXPONENTIAL:
			if(probMode != PROB_LEFT)
				isValid = xLow >= 0;   
				break;

		case ProbabilityManager.DIST_F:	
			if(probMode != PROB_LEFT)
				isValid = xLow > 0;   
				break;

		}

		return isValid;	

	}


	//=================================================
	//      Event Handlers 
	//=================================================


	public void updateFonts() {
		Font font = app.getPlainFont();
		setFont(font);
		GuiManager.setFontRecursive(this,font);
		lblDist.setFont(app.getItalicFont());
		lblProb.setFont(app.getItalicFont());
		plotPanel.updateFonts();
		table.updateFonts(font);

	}

	public void actionPerformed(ActionEvent e) {
		if(isIniting) return;
		Object source = e.getSource();	


		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField)source);
		}	


		if(source == comboDistribution){
			comboDistribution.removeActionListener(this);
			if(comboDistribution.getSelectedItem() != null) 
				if( comboDistribution.getSelectedItem().equals(ListSeparatorRenderer.SEPARATOR)){
					comboDistribution.setSelectedItem(distributionMap.get(selectedDist));
				}
				else if(selectedDist != this.reverseDistributionMap.get(comboDistribution.getSelectedItem())){
					selectedDist = this.reverseDistributionMap.get(comboDistribution.getSelectedItem());
					parameters = probManager.getDefaultParameterMap().get(selectedDist);
					this.setProbabilityCalculator(selectedDist, parameters, isCumulative);
				}
			comboDistribution.addActionListener(this);

			this.requestFocus();
		}

		else if (source == comboProbType) {					
			updateProbabilityType();
		}

		//btnClose.requestFocus();
	}


	private void doTextFieldActionPerformed(JTextField source) {
		if(isIniting) return;
		try {
			String inputText = source.getText().trim();
			//Double value = Double.parseDouble(source.getText());

			// allow input such as sqrt(2)
			NumberValue nv;
			nv = kernel.getAlgebraProcessor().evaluateToNumeric(inputText, false);		
			double value = nv.getDouble();

			if(source == fldLow){
				if(isValidInterval(value,high)){
					low = value;
					setXAxisPoints();
				}else{
					updateGUI();
				}

			}

			else if(source == fldHigh){
				if(isValidInterval(low,value)){
					high = value;
					setXAxisPoints();
				}else{
					updateGUI();
				}
			}

			// handle inverse probability
			else if(source == fldResult ){
				if(value < 0 || value > 1){
					updateGUI();
				}else{
					if(probMode == PROB_LEFT){
						high = inverseProbability(value);
					}
					if(probMode == PROB_RIGHT){
						low = inverseProbability(1-value);
					}
					setXAxisPoints();
				}
			}

			else 
				// handle parm entry
				for(int i=0; i< parameters.length; ++i)
					if (source == fldParameterArray[i]) {
						parameters[i] = value;
						// TODO
						//validateParms(selectedParms);
						//updatePlot();
						updateAll();
					}

			updateIntervalProbability();
			updateGUI();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}


	public void setInterval(double low, double high){
		fldHigh.removeActionListener(this);
		fldLow.removeActionListener(this);
		this.low = low;
		this.high = high;
		fldLow.setText(""+low);
		fldHigh.setText(""+high);
		setXAxisPoints();
		updateIntervalProbability();
		updateGUI();
		fldHigh.addActionListener(this);
		fldLow.addActionListener(this);
	}


	public void focusGained(FocusEvent arg0) {}

	public void focusLost(FocusEvent e) {
		doTextFieldActionPerformed((JTextField)(e.getSource()));
		updateGUI();
	}



	//=================================================
	//      Update Methods
	//=================================================


	public void updateAll(){
		updateFonts();
		updateDistribution();
		updatePlotSettings();
		updateIntervalProbability();
		updateDiscreteTable();
		setXAxisPoints();
		updateProbabilityType();
		updateGUI();
		//this.requestFocus();

	}

	
	private void updateGUI() {

		// set visibility and text of the parameter labels and fields
		for(int i = 0; i < maxParameterCount; ++i ){

			boolean hasParm = i < probManager.getParmCount()[selectedDist];

			lblParameterArray[i].setVisible(hasParm);
			fldParameterArray[i].setVisible(hasParm);

			// hide sliders for now ... need to work out slider range for each parm (tricky)
			//	sliderArray[i].setVisible(false);

			if(hasParm){
				// set label
				lblParameterArray[i].setVisible(true);
				lblParameterArray[i].setText(parameterLabels[selectedDist][i]);
				// set field
				fldParameterArray[i].removeActionListener(this);
				fldParameterArray[i].setText("" + format( parameters[i]));
				fldParameterArray[i].setCaretPosition(0);
				fldParameterArray[i].addActionListener(this);
			}
		}

		// set low/high interval field values 
		fldLow.setText("" + format(low));
		fldLow.setCaretPosition(0);
		fldHigh.setText("" + format(high));
		fldHigh.setCaretPosition(0);
		fldResult.setText("" + format(probability));
		fldResult.setCaretPosition(0);

		// set distribution combo box
		comboDistribution.removeActionListener(this);
		if(comboDistribution.getSelectedItem() != distributionMap.get(selectedDist))
			comboDistribution.setSelectedItem(distributionMap.get(selectedDist));
		comboDistribution.addActionListener(this);

	}


	


	private void updateIntervalProbability(){
		probability = intervalProbability();
		if(probManager.isDiscrete(selectedDist))
			this.discreteIntervalGraph.updateCascade();
		else
			if(hasIntegral)
				this.integral.updateCascade();
	}


	private void updateProbabilityType(){

		if(isIniting) return;	

		boolean isDiscrete = probManager.isDiscrete(selectedDist);

		if(isCumulative)
			probMode = PROB_LEFT;
		else
			probMode = comboProbType.getSelectedIndex();
		this.getPlotDimensions();

		if(probMode == PROB_INTERVAL){
			lowPoint.setEuclidianVisible(true);
			highPoint.setEuclidianVisible(true);			
			fldLow.setVisible(true);
			fldHigh.setVisible(true);
			lblBetween.setText(app.getMenu("XBetween"));

			low = plotSettings.xMin + 0.4*(plotSettings.xMax -plotSettings.xMin);
			high = plotSettings.xMin + 0.6*(plotSettings.xMax -plotSettings.xMin);

		}

		else if(probMode == PROB_LEFT){
			lowPoint.setEuclidianVisible(false);
			highPoint.setEuclidianVisible(true);
			fldLow.setVisible(false);
			fldHigh.setVisible(true);
			lblBetween.setText(app.getMenu("XLessThanOrEqual"));

			if(isDiscrete)
				low = ((GeoNumeric)discreteValueList.get(0)).getDouble();
			else		
				low = plotSettings.xMin - 1; // move offscreen so the integral looks complete

			high = plotSettings.xMin + 0.6*(plotSettings.xMax -plotSettings.xMin);
		}

		else if(probMode == PROB_RIGHT){
			lowPoint.setEuclidianVisible(true);
			highPoint.setEuclidianVisible(false);
			fldLow.setVisible(true);
			fldHigh.setVisible(false);
			lblBetween.setText(app.getMenu("LessThanOrEqualToX"));

			if(isDiscrete)
				high = ((GeoNumeric)discreteValueList.get(discreteValueList.size()-1)).getDouble();
			else
				high = plotSettings.xMax + 1; // move offscreen so the integral looks complete

			low = plotSettings.xMin + 0.6*(plotSettings.xMax -plotSettings.xMin);
		}

		// make result field editable for inverse probability calculation  
		if(probMode != PROB_INTERVAL){
			fldResult.setBackground(fldLow.getBackground());
			fldResult.setBorder(fldLow.getBorder());
			fldResult.setEditable(true);
			fldResult.setFocusable(true);

		}else{

			fldResult.setBackground(this.getBackground());
			fldResult.setBorder(BorderFactory.createEmptyBorder());
			fldResult.setEditable(false);
			fldResult.setFocusable(false);

		}

		if(isDiscrete){
			high = Math.round(high);
			low = Math.round(low);
		}
		setXAxisPoints();
		updateIntervalProbability();
		updateGUI();	
	}


	/**
	 * Sets the distribution type. 
	 * This will destroy all GeoElements and create new ones. 
	 */
	protected void updateDistribution(){

		hasIntegral = !isCumulative;
		createGeoElements();
		//setSliderDefaults();

		// update
		if(probManager.isDiscrete(selectedDist)){
			discreteGraph.update();
			discreteIntervalGraph.update();
			//updateDiscreteTable();
			addRemoveTable(true);
			//this.fldParameterArray[0].requestFocus();

		}else{
			addRemoveTable(false);
			densityCurve.update();
			if(hasIntegral)
				integral.update();
		}

		this.repaint();

	}


	private void updateDiscreteTable(){
		if(!probManager.isDiscrete(selectedDist))
			return;

		int firstX = (int) ((GeoNumeric)discreteValueList.get(0)).getDouble();
		int lastX = (int) ((GeoNumeric)discreteValueList.get(discreteValueList.size()-1)).getDouble();
		table.setTable(selectedDist, parameters, firstX, lastX, isCumulative);
	}


	protected void updatePrintFormat(int printDecimals, int printFigures){
		this.printDecimals = printDecimals;
		this.printFigures = printFigures;
		updateGUI();
		updateDiscreteTable();
	}



	//=================================================
	//      View Implementation
	//=================================================

	public void add(GeoElement geo) {}
	public void clearView() {
		//Application.debug("prob calc clear view");
		//this.removeGeos();
		//plotPanel.clearView();
	}
	public void remove(GeoElement geo) {}
	public void rename(GeoElement geo) {}
	public void repaintView() {}
	public void reset() {
		//Application.debug("prob calc reset");
		//updateAll();
	}
	public void setMode(int mode) {} 
	public void updateAuxiliaryObject(GeoElement geo) {}

	// Handles user point changes in the EV plot panel 
	public void update(GeoElement geo) {
		if(!isSettingAxisPoints && !isIniting){
			if(geo.equals(lowPoint)){	
				if(isValidInterval(lowPoint.getInhomX(), high)){
					low = lowPoint.getInhomX();
					updateIntervalProbability();
					updateGUI();
					if(probManager.isDiscrete(selectedDist))
						table.setSelectionByRowValue((int)low, (int)high);
				}else{
					setXAxisPoints();
				}
			}
			if(geo.equals(highPoint)){
				if(isValidInterval(low, highPoint.getInhomX())){
					high = highPoint.getInhomX();
					updateIntervalProbability();
					updateGUI();
					if(probManager.isDiscrete(selectedDist))
						table.setSelectionByRowValue((int)low, (int)high);
				}else{
					setXAxisPoints();
				}
			}
		}
	}


	final public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}

	public void attachView() {
		//clearView();
		//kernel.notifyAddAll(this);
		kernel.attach(this);		
	}

	public void detachView() {
		removeGeos();
		kernel.detach(this);
		//plotPanel.detachView();
		//clearView();
		//kernel.notifyRemoveAll(this);		
	}


	public void setLabels(){

		distPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Distribution")));
		probPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Probability")));
		setLabelArrays();

		lblDist.setText(app.getMenu("Distribution") + ": ");
		lblProb.setText(app.getMenu("Probability") + ": ");

		setProbabilityComboBoxMenu();

		lblBetween.setText(app.getMenu("XBetween"));   // <= X <=
		lblEndProbOf.setText(app.getMenu("EndProbabilityOf") + " = ");
		lblProbOf.setText(app.getMenu("ProbabilityOf"));

		setDistributionComboBoxMenu();

		if(table != null)
			table.setLabels();
		if(styleBar != null)
			styleBar.setLabels();

	}
	
	private void setLabelArrays(){

		distributionMap = probManager.getDistributionMap();
		reverseDistributionMap = probManager.getReverseDistributionMap();
		parameterLabels = ProbabilityManager.getParameterLabelArray(app);
	}
	
	private void setProbabilityComboBoxMenu(){
		
		comboProbType.removeActionListener(this);
		comboProbType.removeAllItems();
		if(isCumulative)
			comboProbType.addItem(app.getMenu("LeftProb"));
		else{
			comboProbType.addItem(app.getMenu("IntervalProb"));
			comboProbType.addItem(app.getMenu("LeftProb"));
			comboProbType.addItem(app.getMenu("RightProb"));
		}
		comboProbType.addActionListener(this);
		
	}
	

	private void setDistributionComboBoxMenu(){

		comboDistribution.removeActionListener(this);
		comboDistribution.removeAllItems();
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_NORMAL));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_STUDENT));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_CHISQUARE));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_F));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_EXPONENTIAL));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_CAUCHY));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_WEIBULL));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_GAMMA));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_LOGNORMAL));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_LOGISTIC));

		comboDistribution.addItem(ListSeparatorRenderer.SEPARATOR);

		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_BINOMIAL));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_PASCAL));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_POISSON));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_HYPERGEOMETRIC));

		comboDistribution.setSelectedItem(distributionMap.get(selectedDist));
		comboDistribution.addActionListener(this);

	}






	//=================================================
	//       Geo Handlers
	//=================================================
	
	
	private GeoElement createGeoFromString(String text, String label, boolean suppressLabelCreation ){

		try {

			// create the geo
			// ================================
			boolean oldSuppressLabelMode = cons.isSuppressLabelsActive();
			if(suppressLabelCreation)
				cons.setSuppressLabelCreation(true);

			// workaround for eg CmdNormal -> always creates undo point
			boolean oldEnableUndo = cons.isUndoEnabled();
			cons.setUndoEnabled(false);

			GeoElement[] geos = kernel.getAlgebraProcessor()
			.processAlgebraCommandNoExceptions(text, false);	

			cons.setUndoEnabled(oldEnableUndo);

			if(suppressLabelCreation)
				cons.setSuppressLabelCreation(oldSuppressLabelMode);

			return geos[0];

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public void removeGeos(){
		if(pointList != null)
			pointList.clear();
		clearPlotGeoList();
		plotPanel.clearView();
	}


	private void clearPlotGeoList(){

		for(GeoElement geo : plotGeoList){
			if(geo != null){
				geo.setFixed(false);
				geo.remove();
			}
		}
		plotGeoList.clear();
	}


	private void hideAllGeosFromViews(){
		for(GeoElement geo:plotGeoList){
			hideGeoFromViews(geo);
		}
	}

	private void hideGeoFromViews(GeoElement geo){
		// add the geo to our view and remove it from EV		
		geo.addView(plotPanel.getViewID());
		plotPanel.add(geo);
		geo.removeView(Application.VIEW_EUCLIDIAN);
		app.getEuclidianView().remove(geo);
	}

	private void hideToolTips(){
		for(GeoElement geo:plotGeoList){
			geo.setTooltipMode(GeoElement.TOOLTIP_OFF);
		}
	}




	/**
	 * Builds a string that can be used by the algebra processor to
	 * create a GeoFunction representation of a given density curve.
	 * 
	 * @param distType
	 * @param parms
	 * @return
	 */
	private String buildDensityCurveExpression(int type){
		String expr = "";

		// build geogebra string for creating a density curve with list values as parameters
		// e.g." Normal[0,1,x] "

		StringBuilder sb = new StringBuilder();
		sb.append(ProbabilityManager.getCommand()[type]);
		sb.append("[");
		for(int i=0; i < parameters.length; i++){
			sb.append(parameters[i] + ",");
		}
		if(isCumulative)
			sb.append("x, true]");
		else
			sb.append("x, false]");

		return sb.toString();
	}



	/**
	 * Creates two GeoLists: discreteProbList and discreteValueList. These store
	 * the probabilities and values of the currently selected discrete
	 * distribution.
	 */
	private void createDiscreteLists(){

		ExpressionNode nPlusOne;
		AlgoDependentNumber plusOneAlgo;
		switch(selectedDist){

		case ProbabilityManager.DIST_BINOMIAL:	

			/*n = "Element[" + parmList.getLabel() + ",1]";
			p = "Element[" + parmList.getLabel() + ",2]";

			expr = "Sequence[k,k,0," + n + "]";
			discreteValueList = (GeoList) createGeoFromString(expr);

			expr = "Sequence[BinomialDist[" + n + "," + p + ",";
			expr += "Element[" + discreteValueList.getLabel() + ",k]," + isCumulative;
			expr +=	"],k,1," + n + "+ 1 ]";

			//System.out.println(expr);
			discreteProbList = (GeoList) createGeoFromString(expr);*/


			GeoNumeric k = new GeoNumeric(cons);
			GeoNumeric k2 = new GeoNumeric(cons);
			GeoNumeric nGeo = new GeoNumeric(cons,parameters[0]);
			GeoNumeric nPlusOneGeo = new GeoNumeric(cons,parameters[0] + 1);
			GeoNumeric pGeo = new GeoNumeric(cons,parameters[1]);	

			AlgoSequence algoSeq = new AlgoSequence(cons, k2, k2, new MyDouble(kernel, 0.0), (NumberValue)nGeo, null);
			discreteValueList = (GeoList)algoSeq.getGeoElements()[0];

			AlgoListElement algo = new AlgoListElement(cons, discreteValueList, k);
			cons.removeFromConstructionList(algo);

			AlgoBinomialDist algo2 = new AlgoBinomialDist(cons, (NumberValue)nGeo, pGeo, (NumberValue)algo.getGeoElements()[0], new GeoBoolean(cons, isCumulative));
			cons.removeFromConstructionList(algo2);

			AlgoSequence algoSeq2 = new AlgoSequence(cons, algo2.getGeoElements()[0], k, new MyDouble(kernel, 1.0), (NumberValue)nPlusOneGeo, null);
			cons.removeFromConstructionList(algoSeq2);

			discreteProbList = (GeoList)algoSeq2.getGeoElements()[0];

			break;

		case ProbabilityManager.DIST_PASCAL:	

			nGeo = new GeoNumeric(cons,parameters[0]);
			pGeo = new GeoNumeric(cons,parameters[1]);	
			k = new GeoNumeric(cons);
			k2 = new GeoNumeric(cons);

			AlgoInversePascal n2 = new AlgoInversePascal(cons, nGeo, pGeo, new MyDouble(kernel, nearlyOne));
			cons.removeFromConstructionList(n2);
			GeoElement n2Geo = n2.getGeoElements()[0];

			algoSeq = new AlgoSequence(cons, k, k, new MyDouble(kernel, 0.0), (NumberValue)n2Geo, null);
			removeFromAlgorithmList(algoSeq);
			discreteValueList = (GeoList)algoSeq.getGeoElements()[0];

			algo = new AlgoListElement(cons, discreteValueList, k2);
			cons.removeFromConstructionList(algo);

			AlgoPascal pascal = new AlgoPascal(cons, nGeo, pGeo, (NumberValue)algo.getGeoElements()[0], new GeoBoolean(cons, isCumulative));
			cons.removeFromConstructionList(pascal);

			nPlusOne = new ExpressionNode(kernel, n2Geo, ExpressionNode.PLUS, new MyDouble(kernel, 1.0));
			plusOneAlgo = new AlgoDependentNumber(cons, nPlusOne, false);
			cons.removeFromConstructionList(plusOneAlgo);

			algoSeq2 = new AlgoSequence(cons, pascal.getGeoElements()[0], k2, new MyDouble(kernel, 1.0), (NumberValue)plusOneAlgo.getGeoElements()[0], null);
			cons.removeFromConstructionList(algoSeq2);

			discreteProbList = (GeoList) algoSeq2.getGeoElements()[0];

			break;

		case ProbabilityManager.DIST_POISSON:

			GeoNumeric meanGeo = new GeoNumeric(cons,parameters[0]);
			k = new GeoNumeric(cons);
			k2 = new GeoNumeric(cons);

			AlgoInversePoisson maxSequenceValue = new AlgoInversePoisson(cons, meanGeo, new MyDouble(kernel, nearlyOne));
			cons.removeFromConstructionList(maxSequenceValue);
			GeoElement maxDiscreteGeo = maxSequenceValue.getGeoElements()[0];

			algoSeq = new AlgoSequence(cons, k, k, new MyDouble(kernel, 0.0), (NumberValue)maxDiscreteGeo, null);
			removeFromAlgorithmList(algoSeq);
			discreteValueList = (GeoList)algoSeq.getGeoElements()[0];

			algo = new AlgoListElement(cons, discreteValueList, k2);
			cons.removeFromConstructionList(algo);

			AlgoPoisson poisson = new AlgoPoisson(cons, meanGeo, (NumberValue)algo.getGeoElements()[0], new GeoBoolean(cons, isCumulative));
			cons.removeFromConstructionList(poisson);

			nPlusOne = new ExpressionNode(kernel, maxDiscreteGeo, ExpressionNode.PLUS, new MyDouble(kernel, 1.0));
			plusOneAlgo = new AlgoDependentNumber(cons, nPlusOne, false);
			cons.removeFromConstructionList(plusOneAlgo);

			algoSeq2 = new AlgoSequence(cons, poisson.getGeoElements()[0], k2, new MyDouble(kernel, 1.0), (NumberValue)plusOneAlgo.getGeoElements()[0], null);
			cons.removeFromConstructionList(algoSeq2);

			discreteProbList = (GeoList) algoSeq2.getGeoElements()[0];

			break;


		case ProbabilityManager.DIST_HYPERGEOMETRIC:	
			/*
			p = "" + parameters[0];  // population size
			n = "" + parameters[1];  // n
			s = "" + parameters[2];  // sample size

			expr = "Sequence[k,k,0," + n + "]";
			discreteValueList = (GeoList) createGeoFromString(expr);

			expr = "Sequence[HyperGeometric[" + p + "," + n + "," + s + ",";
			expr += "Element[" + discreteValueList.getLabel() + ",k]," + isCumulative;
			expr +=	"],k,1," + n + "+ 1 ]";

			//System.out.println(expr);
			discreteProbList = (GeoList) createGeoFromString(expr);*/

			double p = parameters[0];  // population size
			double n = parameters[1];  // n
			double s = parameters[2];  // sample size

			// ================================================
			// interval bounds:
			//    [ max(0, n + s - p) ,  min(n, s) ] 
			//=================================================

			double lowBound = Math.max(0, n + s - p);
			double highBound = Math.min(n, s);	
			double length = highBound - lowBound + 1;

			GeoNumeric lowGeo = new GeoNumeric(cons,lowBound);
			GeoNumeric highGeo = new GeoNumeric(cons,highBound);
			GeoNumeric lengthGeo = new GeoNumeric(cons, length);

			pGeo = new GeoNumeric(cons,p);	
			nGeo = new GeoNumeric(cons,n);				
			GeoNumeric sGeo = new GeoNumeric(cons,s);	

			k = new GeoNumeric(cons);
			k2 = new GeoNumeric(cons);


			algoSeq = new AlgoSequence(cons, k, k, (NumberValue)lowGeo, (NumberValue)highGeo, null);
			removeFromAlgorithmList(algoSeq);
			discreteValueList = (GeoList)algoSeq.getGeoElements()[0];

			algo = new AlgoListElement(cons, discreteValueList, k2);
			cons.removeFromConstructionList(algo);

			AlgoHyperGeometric hyperGeometric = new AlgoHyperGeometric(cons, pGeo, nGeo, sGeo, (NumberValue)algo.getGeoElements()[0], new GeoBoolean(cons, isCumulative));
			cons.removeFromConstructionList(hyperGeometric);

			algoSeq2 = new AlgoSequence(cons, hyperGeometric.getGeoElements()[0], k2, new MyDouble(kernel, 1.0), (NumberValue)lengthGeo, null);
			cons.removeFromConstructionList(algoSeq2);
			discreteProbList = (GeoList) algoSeq2.getGeoElements()[0];

			break;


		}

		plotGeoList.add(discreteProbList);
		discreteProbList.setEuclidianVisible(true);	
		discreteProbList.setAuxiliaryObject(true);
		discreteProbList.setLabelVisible(false);
		discreteProbList.setFixed(true);


		return;
	}



	/**
	 * Returns the appropriate plot dimensions for a given distribution and parameter set. 
	 * Plot dimensions are returned as an array of double: {xMin, xMax, yMin, yMax} 	 
	 */
	private double[] getPlotDimensions(){

		return probManager.getPlotDimensions(selectedDist, parameters, densityCurve, isCumulative);

	}


	/**
	 * Returns the maximum value in the discrete value list.
	 * @return
	 */
	public int getDiscreteXMax(){
		if(discreteValueList != null){
			GeoNumeric geo = (GeoNumeric) discreteValueList.get(discreteValueList.size()-1);
			return (int) geo.getDouble();
		}
		return -1;
	}

	/**
	 * Returns the minimum value in the discrete value list.
	 * @return
	 */
	public int getDiscreteXMin(){
		if(discreteValueList != null){
			GeoNumeric geo = (GeoNumeric) discreteValueList.get(0);
			return (int) geo.getDouble();
		}
		return -1;
	}


	//============================================================
	//           ComboBox Renderer with SEPARATOR
	//============================================================

	static class ListSeparatorRenderer extends JLabel implements ListCellRenderer {

		public static final String SEPARATOR = "---";
		JSeparator separator;

		public ListSeparatorRenderer() {
			setOpaque(true);
			setBorder(new EmptyBorder(1, 1, 1, 1));
			separator = new JSeparator(JSeparator.HORIZONTAL);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			String str = (value == null) ? "" : value.toString();
			if (SEPARATOR.equals(str)) {
				return separator;
			}
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setFont(list.getFont());
			setText(str);
			return this;
		}
	}


	//============================================================
	//           Number Format
	//============================================================

	/**
	 * Formats a number string using local format settings
	 */
	public String format(double x){

		// override the default decimal place setting
		if(printDecimals >= 0)
			kernel.setTemporaryPrintDecimals(printDecimals);
		else
			kernel.setTemporaryPrintFigures(printFigures);

		// get the formatted string
		String result = kernel.format(x);

		// restore the default decimal place setting
		kernel.restorePrintAccuracy();

		return result;
	}



	//============================================================
	//           Sliders
	//============================================================


	private void setSliderDefaults(){
		for(int i = 0; i < probManager.getParmCount()[selectedDist]; i++){
			// TODO: this is breaking the discrete distributions
			//sliderArray[i].setValue((int) probManager.getDefaultParameterMap().get(selectedDist)[i]);
		}
	}


	public void stateChanged(ChangeEvent e) {
		if(isIniting) return;

		JSlider source = (JSlider)e.getSource();
		for(int i = 0 ; i < maxParameterCount; i++){
			if(source == sliderArray[i]){

				fldParameterArray[i].setText("" + sliderArray[i].getValue());
				doTextFieldActionPerformed(fldParameterArray[i]);

			}
		}

	}



	//============================================================
	//           XML
	//============================================================

	/**
	 * returns settings in XML format
	 */
	public void getXML(StringBuilder sb) {

		if(selectedDist == -1) return;

		sb.append("<probabilityCalculator>\n");
		sb.append("\t<distribution");

		sb.append(" type=\"");
		sb.append(selectedDist);
		sb.append("\"");


		sb.append(" isCumulative=\"");
		sb.append(isCumulative  ? "true" : "false" );
		sb.append("\"");

		sb.append(" parameters" + "=\"");
		for (int i = 0 ; i < parameters.length ; i++) {
			sb.append(parameters[i]);
			sb.append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append("\"");

		sb.append("/>\n");
		sb.append("</probabilityCalculator>\n");
	}



	public boolean doRemoveFromConstruction() {
		return removeFromConstruction;
	}
	public void setRemoveFromConstruction(boolean removeFromConstruction) {
		this.removeFromConstruction = removeFromConstruction;
	}

	private void removeFromConstructionList(ConstructionElement ce){
		if(removeFromConstruction)
			cons.removeFromConstructionList(ce);
	}

	private void removeFromAlgorithmList(AlgoElement algo){
		if(removeFromConstruction)
			cons.removeFromAlgorithmList(algo);
	}


	//============================================================
	//           Export
	//============================================================


	private void addPlotPanelExportMenu(PlotPanelEuclidianView plotPanel){

		AbstractAction exportToEVAction = new AbstractAction(app
				.getMenu("CopyToGraphics"), app
				.getImageIcon("edit-copy.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if(app.getShiftDown())
					exportGeosToEV(Application.VIEW_EUCLIDIAN2);
				else
					exportGeosToEV(Application.VIEW_EUCLIDIAN);
			}
		};

		plotPanel.appendActionList(exportToEVAction);
	}




	public void exportGeosToEV(int viewID){

		app.setWaitCursor();
		ArrayList<GeoElement> newGeoList = new ArrayList<GeoElement>();
		String expr;

		try {
			app.storeUndoInfo();


			/*

			// update the plot to get a new set of geos that exist in the construction
			removeFromConstruction = false;
			this.updateDistribution();

			// set the EV location and auxiliary = false for all of the new geos
			for(GeoElement geo: plotGeoList){
				geo.setLabel(null);
				geo.setAuxiliaryObject(false);
				if(viewID == Application.VIEW_EUCLIDIAN){
					geo.addView(Application.VIEW_EUCLIDIAN);
					geo.removeView(Application.VIEW_EUCLIDIAN2);
					geo.update();
				}
				if(viewID == Application.VIEW_EUCLIDIAN2){
					geo.addView(Application.VIEW_EUCLIDIAN2);
					geo.removeView(Application.VIEW_EUCLIDIAN);
					geo.update();
				}

			}


			// set the window dimensions of the target EV to match the plotPanel dimensions
			if(viewID == Application.VIEW_EUCLIDIAN)
				app.getEuclidianView().setRealWorldCoordSystem(plotSettings.xMin, plotSettings.xMax, 
						plotSettings.yMin, plotSettings.yMax);
			else if(viewID == Application.VIEW_EUCLIDIAN2)
				app.getEuclidianView2().setRealWorldCoordSystem(plotSettings.xMin, plotSettings.xMax, 
						plotSettings.yMin, plotSettings.yMax);


			// null our display geos and clear the plotGeoList to unlink the new geos
			lowPoint = null;
			highPoint = null;
			densityCurve = null; integral = null;
			discreteGraph = null; discreteIntervalGraph = null;
			discreteValueList = null; discreteProbList = null; intervalProbList=null; intervalValueList=null;

			pointList.clear();
			plotGeoList.clear();


			//update the plot in removeFromConstruction mode to get a new set of geos for our plot
			removeFromConstruction = true;
			this.updateDistribution();

			 */


			//create low point
			expr = "Point[" + app.getPlain("xAxis") + "]";
			GeoPoint lowPointCopy = (GeoPoint) createGeoFromString(expr,null,false);
			lowPointCopy.setVisualStyle(lowPoint);
			lowPointCopy.setLabelVisible(false);
			lowPointCopy.setCoords(low, 0, 1);
			lowPointCopy.setLabel(null);
			newGeoList.add(lowPointCopy);

			//create high point
			GeoPoint highPointCopy = (GeoPoint) createGeoFromString(expr,null,false);
			highPointCopy.setVisualStyle(lowPoint);
			highPointCopy.setLabelVisible(false);
			highPointCopy.setCoords(high, 0, 1);
			highPointCopy.setLabel(null);
			newGeoList.add(highPointCopy);


			// create discrete bar charts and associated lists
			if(probManager.isDiscrete(selectedDist)){

				// create full bar chart
				// ============================
				GeoElement discreteProbListCopy = discreteProbList.copy();
				newGeoList.add(discreteProbListCopy);

				GeoElement discreteValueListCopy = discreteValueList.copy();
				newGeoList.add(discreteValueList);

				expr = "BarChart[" + discreteValueListCopy.getLabel() + "," + discreteProbListCopy.getLabel() + ",0]";
				GeoElement discreteGraphCopy = createGeoFromString(expr,null,false);
				discreteGraphCopy.setLabel(null);
				discreteGraphCopy.setVisualStyle(discreteGraph);
				newGeoList.add(discreteGraphCopy);



				// create interval bar chart
				// ============================
				double offset = 1 - ((GeoNumeric)discreteValueList.get(0)).getDouble();  
				expr = "Take[" + discreteProbListCopy.getLabel()  + ", x(" 
				+ lowPointCopy.getLabel() + ")+" + offset + ", x(" + highPointCopy.getLabel() +")+" + offset +"]";
				GeoElement intervalProbList  = (GeoList) createGeoFromString(expr, null, false);
				newGeoList.add(intervalProbList);

				expr = "Take[" + discreteValueListCopy.getLabel()  + ", x(" 
				+ lowPointCopy.getLabel() + ")+" + offset + ", x(" + highPointCopy.getLabel() +")+" + offset +"]";
				GeoElement intervalValueList  = (GeoList) createGeoFromString(expr, null, false);
				newGeoList.add(intervalValueList);

				expr = "BarChart[" + intervalValueList.getLabel() + "," + intervalProbList.getLabel() + ",0]";
				GeoElement discreteIntervalGraphCopy  = createGeoFromString(expr, null, false);
				discreteIntervalGraphCopy.setLabel(null);
				discreteIntervalGraphCopy.setVisualStyle(discreteIntervalGraph);
				newGeoList.add(discreteIntervalGraphCopy);

			}


			// create density curve and integral
			else{

				//density curve
				GeoElement densityCurveCopy = densityCurve.copyInternal(cons);
				densityCurveCopy.setLabel(null);
				densityCurveCopy.setVisualStyle(densityCurve);
				newGeoList.add(densityCurveCopy);

				//integral
				expr = "Integral[" + densityCurveCopy.getLabel() + ", x(" + lowPointCopy.getLabel() 
				+ "), x(" + highPointCopy.getLabel() + ") , true ]";
				GeoElement integralCopy  = createGeoFromString(expr,null, false);
				integralCopy.setVisualStyle(integral);
				integralCopy.setLabel(null);
				newGeoList.add(integralCopy);
			}


			// set the EV location and auxiliary = false for all of the new geos
			for(GeoElement geo: newGeoList){
				geo.setAuxiliaryObject(false);
				if(viewID == Application.VIEW_EUCLIDIAN){
					geo.addView(Application.VIEW_EUCLIDIAN);
					geo.removeView(Application.VIEW_EUCLIDIAN2);
					geo.update();
				}
				else if(viewID == Application.VIEW_EUCLIDIAN2){
					geo.addView(Application.VIEW_EUCLIDIAN2);
					geo.removeView(Application.VIEW_EUCLIDIAN);
					geo.update();
				}
			}

			// set the window dimensions of the target EV to match the prob calc dimensions
			EuclidianView ev  = (EuclidianView) app.getView(viewID);
			ev.setRealWorldCoordSystem(plotSettings.xMin, plotSettings.xMax, plotSettings.yMin, plotSettings.yMax);
			ev.setAutomaticAxesNumberingDistance(plotSettings.xAxesIntervalAuto, 0);
			ev.setAutomaticAxesNumberingDistance(plotSettings.yAxesIntervalAuto, 1);
			if(!plotSettings.xAxesIntervalAuto){
				ev.setAxesNumberingDistance(plotSettings.xAxesInterval, 0);
			}
			if(!plotSettings.yAxesIntervalAuto){
				ev.setAxesNumberingDistance(plotSettings.yAxesInterval, 1);
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

	public int getViewID() {
		return Application.VIEW_PROBABILITY_CALCULATOR;
	}



	public void settingsChanged(AbstractSettings settings) {
		Application.debug("settings changed");

		ProbabilityCalculatorSettings pcSettings = (ProbabilityCalculatorSettings) settings;
		setProbabilityCalculator(pcSettings.getDistributionType(), pcSettings.getParameters(), pcSettings.isCumulative());

	}


}
