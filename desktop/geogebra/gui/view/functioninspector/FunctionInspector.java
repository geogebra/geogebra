/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.gui.view.functioninspector;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.gui.view.algebra.DialogType;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.View;
import geogebra.common.kernel.algos.AlgoCurvature;
import geogebra.common.kernel.algos.AlgoDependentFunction;
import geogebra.common.kernel.algos.AlgoDependentNumber;
import geogebra.common.kernel.algos.AlgoDependentPoint;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoFunctionInterval;
import geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import geogebra.common.kernel.algos.AlgoOsculatingCircle;
import geogebra.common.kernel.algos.AlgoPointOnPath;
import geogebra.common.kernel.algos.AlgoRoots;
import geogebra.common.kernel.algos.AlgoRootsPolynomial;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyVecNode;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.cas.AlgoDerivative;
import geogebra.common.kernel.cas.AlgoIntegralDefinite;
import geogebra.common.kernel.cas.AlgoLengthFunction;
import geogebra.common.kernel.cas.AlgoTangentFunctionPoint;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.optimization.ExtremumFinder;
import geogebra.common.kernel.roots.RealRootFunction;
import geogebra.common.main.App;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.Operation;
import geogebra.gui.GuiManagerD;
import geogebra.gui.dialog.InputDialogD;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;
import geogebra.gui.util.SpecialNumberFormat;
import geogebra.gui.util.SpecialNumberFormatInterface;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;


/**
 * View for inspecting selected GeoFunctions
 * 
 * @author G. Sturr, 2011-2-12
 * 
 */

public class FunctionInspector extends InputDialogD 
implements View, MouseListener, ListSelectionListener, 
KeyListener, ActionListener, SpecialNumberFormatInterface {

	private static final long serialVersionUID = 1L;
	
	private Color DISPLAY_GEO_COLOR = Color.RED;
	private static final Color DISPLAY_GEO2_COLOR = Color.RED;

	private static final Color EVEN_ROW_COLOR = new Color(241, 245, 250);
	private static final Color TABLE_GRID_COLOR = geogebra.awt.GColorD.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR);

	private static final int minRows = 12;

	// column types
	private static final int COL_DERIVATIVE = 0;
	private static final int COL_DERIVATIVE2 = 1;
	private static final int COL_DIFFERENCE = 2;
	private static final int COL_CURVATURE = 3;


	// ggb fields
	private Kernel kernel;
	private Construction cons;
	private EuclidianView activeEV;

	// table fields
	private InspectorTable tableXY, tableInterval;
	private DefaultTableModel modelXY, modelInterval;
	private String[] columnNames;

	// list to store column types of dynamically appended columns 
	private ArrayList<Integer> extraColumnList;


	// GUI 
	private JLabel lblGeoName, lblStep, lblInterval;
	private MyTextField fldStep, fldLow, fldHigh;
	private JButton btnRemoveColumn;
	private JToggleButton btnOscCircle, btnTangent, btnXYSegments, btnTable;
	private PopupMenuButton btnAddColumn, btnOptions;
	private JTabbedPane tabPanel;
	private JPanel intervalTabPanel, pointTabPanel, headerPanel, helpPanel;


	// Geos
	private GeoElement tangentLine, oscCircle, xSegment, ySegment;
	private GeoElement functionInterval, integralGeo, lengthGeo, areaGeo;
	private GeoFunction derivative, derivative2, selectedGeo;
	private GeoPoint testPoint, lowPoint, highPoint, minPoint, maxPoint;
	private GeoList pts;

	private ArrayList<GeoElement> intervalTabGeoList, pointTabGeoList, hiddenGeoList;
	private GeoElement[] rootGeos;

	// stores lists of column data from the point panel table
	private ArrayList<Double[]> xyTableCopyList = new ArrayList<Double[]>();

	private boolean isIniting;
	private double xMin, xMax, start =-1, step = 0.1;
	private double initialX;

	private boolean isChangingValue;
	private int pointCount = 9;


	private SpecialNumberFormat nf;
	private JButton btnHelp;


	/** Constructor */
	public FunctionInspector(AppD app, GeoFunction selectedGeo) {

		super(app.getFrame(), false);
		this.app = app;	
		kernel = app.getKernel();
		cons = kernel.getConstruction();

		nf = new SpecialNumberFormat(app, this);

		boolean showApply = false;
		this.selectedGeo = selectedGeo;
		activeEV = app.getActiveEuclidianView();	

		extraColumnList = new ArrayList<Integer>();


		// setup InputDialog GUI
		isIniting = true;
		String title = app.getMenu("FunctionInspector");
		createGUI(title, "", false, 16, 1, false, false, false, showApply, DialogType.TextArea);
		this.btOK.setVisible(false);
		this.btCancel.setVisible(false);


		// lists of all geos we create
		intervalTabGeoList = new ArrayList<GeoElement>();
		pointTabGeoList = new ArrayList<GeoElement>();
		hiddenGeoList = new ArrayList<GeoElement>();

		// create the GUI components
		createGUIElements();


		// build dialog content pane
		createHeaderPanel();
		createTabPanel();

		wrappedDialog.getContentPane().add(headerPanel,BorderLayout.NORTH);
		wrappedDialog.getContentPane().add(tabPanel,BorderLayout.CENTER);

		centerOnScreen();
		wrappedDialog.setResizable(true);


		// attach this view to the kernel
		app.getKernel().attach(this);


		// update and load selected function 
		updateFonts();
		setLabels();
		insertGeoElement(selectedGeo);
		handleTabChange();

		//addHelpButton(Application.WIKI_MANUAL);

		wrappedDialog.pack();

		isIniting = false;

	}


	private void createTabPanel(){

		createTabPointPanel();
		createTabIntervalPanel();

		// build tab panel
		tabPanel = new JTabbedPane();		
		tabPanel.addTab("Interval", intervalTabPanel);
		tabPanel.addTab("Point", pointTabPanel);

		tabPanel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				handleTabChange();
			}

		});
	}

	private void createHeaderPanel(){

		createHelpPanel();

		headerPanel = new JPanel(new BorderLayout());
		headerPanel.add(lblGeoName, BorderLayout.CENTER);	
		headerPanel.add(helpPanel,BorderLayout.EAST);
		headerPanel.setBorder(BorderFactory.createEmptyBorder(2,5,2,2));
	}


	private void createHelpPanel(){

		createOptionsButton();
		helpPanel = new JPanel(new FlowLayout());
		helpPanel.add(btnHelp);
		helpPanel.add(btnOptions);
	}


	private void createTabIntervalPanel(){
		JToolBar intervalTB = new JToolBar();   //JPanel(new FlowLayout(FlowLayout.LEFT));
		intervalTB.setFloatable(false);
		intervalTB.add(fldLow);
		intervalTB.add(lblInterval);
		intervalTB.add(fldHigh);

		intervalTabPanel = new JPanel(new BorderLayout(5,5));
		intervalTabPanel.add(new JScrollPane(tableInterval), BorderLayout.CENTER);
		intervalTabPanel.add(intervalTB, BorderLayout.SOUTH);

	}

	private void createTabPointPanel(){


		// create step toolbar
		JToolBar tb1 = new JToolBar();   
		tb1.setFloatable(false);
		tb1.add(lblStep);
		tb1.add(fldStep);

		// create add/remove column toolbar
		JToolBar tb2 = new JToolBar();
		tb2.setFloatable(false);
		tb2.add(btnAddColumn);
		tb2.add(btnRemoveColumn);


		// create toggle graphics panel

		FlowLayout flow = new FlowLayout(FlowLayout.CENTER); 
		flow.setHgap(4);              
		JPanel tb3 = new JPanel(flow);
		//JToolBar tb3 = new JToolBar();
		//tb3.setFloatable(false);
		tb3.add(btnTable);
		tb3.add(btnXYSegments);
		tb3.add(btnTangent);
		tb3.add(btnOscCircle);
		JPanel toggleGraphicsPanel = new JPanel(new BorderLayout());
		toggleGraphicsPanel.add(tb3, BorderLayout.CENTER);



		// create the panel
		
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(tb1,BorderLayout.WEST);
		northPanel.add(tb2,BorderLayout.EAST);

		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(toggleGraphicsPanel,BorderLayout.CENTER);

		JScrollPane scroller = new JScrollPane(tableXY);

		pointTabPanel = new JPanel(new BorderLayout(2,2));
		pointTabPanel.add(northPanel,BorderLayout.NORTH);
		pointTabPanel.add(scroller,BorderLayout.CENTER);
		pointTabPanel.add(southPanel,BorderLayout.SOUTH);

	}





	//  Create GUI elements 
	// =====================================

	private void createGUIElements(){

		// create XY table
		tableXY = new InspectorTable(app, this, minRows, InspectorTable.TYPE_XY);
		modelXY = new DefaultTableModel();
		modelXY.addColumn("x");
		modelXY.addColumn("y(x)");
		modelXY.setRowCount(pointCount);
		tableXY.setModel(modelXY);


		tableXY.getSelectionModel().addListSelectionListener(this);
		//tableXY.addKeyListener(this);
		tableXY.setMyCellEditor(0);


		// create interval table
		tableInterval = new InspectorTable(app, this, minRows, InspectorTable.TYPE_INTERVAL);
		modelInterval = new DefaultTableModel();
		modelInterval.setColumnCount(2);
		modelInterval.setRowCount(pointCount);
		tableInterval.setModel(modelInterval);
		tableInterval.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {  
				updateIntervalGeoVisiblity();
			}
		});


		lblGeoName = new JLabel(getTitleString());
		lblGeoName.setFont(app.getBoldFont());

		lblStep = new JLabel();
		fldStep = new MyTextField(app);
		fldStep.addActionListener(this);
		fldStep.setColumns(6);

		lblInterval = new JLabel();
		fldLow = new MyTextField(app);
		fldLow.addActionListener(this);
		fldLow.setColumns(6);
		fldHigh = new MyTextField(app);
		fldHigh.addActionListener(this);
		fldHigh.setColumns(6);

		btnOscCircle = new JToggleButton(app.getImageIcon("osculating_circle.png"));
		btnTangent = new JToggleButton(app.getImageIcon("tangent_line.png"));
		btnXYSegments = new JToggleButton(app.getImageIcon("xy_segments.png"));
		btnTable = new JToggleButton(app.getImageIcon("xy_table.png"));
		
		btnOscCircle.addActionListener(this);
		btnTangent.addActionListener(this);
		btnXYSegments.addActionListener(this);
		btnTable.addActionListener(this);

		//btnOscCircle.setPreferredSize(new Dimension(24,24));
		//btnTangent.setPreferredSize(new Dimension(24,24));
		//btnXYSegments.setPreferredSize(new Dimension(24,24));
		//btnTable.setPreferredSize(new Dimension(24,24));

		btnXYSegments.setSelected(true);

		btnRemoveColumn = new JButton();
		btnRemoveColumn.addActionListener(this);

		btnHelp = new JButton(app.getImageIcon("help.png"));
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						app.getGuiManager().openHelp("Function_Inspector_Tool");
					}
				};
				runner.start();
			}
		});	
		btnHelp.setFocusable(false);

		createBtnAddColumn();
	}

	private void createBtnAddColumn() {
		columnNames = new String[4];
		columnNames[COL_DERIVATIVE] =	app.getPlain("fncInspector.Derivative");
		columnNames[COL_DERIVATIVE2] =	app.getPlain("fncInspector.Derivative2");
		columnNames[COL_CURVATURE] =	app.getPlain("fncInspector.Curvature");
		columnNames[COL_DIFFERENCE] =	app.getPlain("fncInspector.Difference");
		btnAddColumn = new PopupMenuButton(app, columnNames, -1, 1, 
				new Dimension(0, 18), geogebra.common.gui.util.SelectionTable.MODE_TEXT);
		btnAddColumn.setKeepVisible(false);
		btnAddColumn.setStandardButton(true);
		btnAddColumn.setFixedIcon(GeoGebraIcon.createEmptyIcon(1, 1));
		btnAddColumn.setText("\u271A");
		btnAddColumn.addActionListener(this);
	}



	public void setLabels() {

		wrappedDialog.setTitle(app.getMenu("FunctionInspector"));
		lblStep.setText(app.getMenu("Step") + ":");		
		lblInterval.setText(" \u2264 x \u2264 " );	// <= x <=

		// header text
		String[] intervalColumnNames = {app.getPlain("fncInspector.Property"), app.getPlain("fncInspector.Value")};
		modelInterval.setColumnIdentifiers(intervalColumnNames);

		tabPanel.setTitleAt(1, app.getPlain("fncInspector.Points"));
		tabPanel.setTitleAt(0, app.getPlain("fncInspector.Interval"));
		lblGeoName.setText(getTitleString());


		//tool tips
		btnHelp.setToolTipText(app.getPlain("ShowOnlineHelp"));
		btnOscCircle.setToolTipText(app.getPlainTooltip("fncInspector.showOscCircle"));
		btnXYSegments.setToolTipText(app.getPlainTooltip("fncInspector.showXYLines"));
		btnTable.setToolTipText(app.getPlainTooltip("fncInspector.showTable"));
		btnTangent.setToolTipText(app.getPlainTooltip("fncInspector.showTangent"));
		btnAddColumn.setToolTipText(app.getPlainTooltip("fncInspector.addColumn"));
		btnRemoveColumn.setToolTipText(app.getPlainTooltip("fncInspector.removeColumn"));
		fldStep.setToolTipText(app.getPlainTooltip("fncInspector.step"));
		lblStep.setToolTipText(app.getPlainTooltip("fncInspector.step"));	


		// add/remove extra column buttons
		btnRemoveColumn.setText("\u2718");
		//btnAddColumn.setText("\u271A");

		Container c = btnAddColumn.getParent();
		c.removeAll();
		createBtnAddColumn();		
		c.add(btnAddColumn);
		c.add(btnRemoveColumn);

		createOptionsButton();

	}


	private String getTitleString(){

		if(selectedGeo == null)
			return app.getMenu("SelectObject");
		return selectedGeo.getAlgebraDescriptionDefault();
	}



	//  GUI update
	// =====================================
	private void updateGUI(){

		if(tabPanel.getSelectedComponent()==intervalTabPanel){

			updateIntervalTable();
			updateIntervalGeoVisiblity();

		}else{

			tangentLine.setEuclidianVisible(btnTangent.isSelected());
			tangentLine.update();
			oscCircle.setEuclidianVisible(btnOscCircle.isSelected());
			oscCircle.update();
			xSegment.setEuclidianVisible(btnXYSegments.isSelected());
			xSegment.update();
			ySegment.setEuclidianVisible(btnXYSegments.isSelected());
			ySegment.update();
			lblStep.setVisible(btnTable.isSelected());
			fldStep.setVisible(btnTable.isSelected());
			pts.setEuclidianVisible(btnTable.isSelected());
			pts.updateRepaint();

			tableXY.getSelectionModel().removeListSelectionListener(this);

			// reset table model and update the XYtable
			tableXY.setCellEditable(-1, -1);

			if(btnTable.isSelected()){
				modelXY.setRowCount(pointCount);
				tableXY.setCellEditable((pointCount -1)/2,0);
				//	tableXY.setRowSelectionAllowed(true);
				tableXY.changeSelection((pointCount - 1)/2, 0, false, false);

			}else{

				modelXY.setRowCount(1);
				tableXY.setCellEditable(0,0);
				tableXY.changeSelection(0, 0, false, false);
				//	tableXY.setRowSelectionAllowed(false);
			}

			updateXYTable();
			updateTestPoint();
			tableXY.getSelectionModel().addListSelectionListener(this);

		} 

	}

	private void handleTabChange(){

		boolean isInterval = tabPanel.getSelectedComponent()==intervalTabPanel;

		updateIntervalFields();

		for(GeoElement geo: intervalTabGeoList){
			geo.setEuclidianVisible(isInterval);
			geo.update();
		}	
		for(GeoElement geo: pointTabGeoList){
			geo.setEuclidianVisible(!isInterval);
			geo.update();
		}	

		activeEV.repaint();
		updateGUI();

	}



	private void updateIntervalFields(){

		if(tabPanel.getSelectedComponent()==intervalTabPanel){

			double[] coords = new double[3];
			lowPoint.getCoords(coords);
			fldLow.setText(nf.format(coords[0]));
			highPoint.getCoords(coords);
			fldHigh.setText(nf.format(coords[0]));
			updateIntervalTable();
		}
	}


	private ArrayList<String> property = new ArrayList<String>();
	private ArrayList<String> value = new ArrayList<String>();
	// store number values for copy 
	private ArrayList<Double[]> value2 = new ArrayList<Double[]>();


	/**
	 * Updates the interval table. The max, min, roots, area etc. for
	 * the current interval are calculated and put into the IntervalTable model.
	 */
	private void updateIntervalTable(){

		isChangingValue = true;

		property.clear();
		value.clear();
		value2.clear();

		// prepare algos and other objects needed for the calcs
		//=======================================================

		double[] coords = new double[3];
		lowPoint.getCoords(coords);
		xMin = coords[0];
		highPoint.getCoords(coords);
		xMax = coords[0];


		ExtremumFinder ef = new ExtremumFinder();
		RealRootFunction fun = selectedGeo.getRealRootFunctionY();    

		// get the table
		double integral = ((GeoNumeric) integralGeo).getDouble();
		double area = ((GeoNumeric) areaGeo).getDouble();
		double mean = integral/(xMax - xMin);
		double length = ((GeoNumeric) lengthGeo).getDouble();

		double yMin = selectedGeo.evaluate(xMin);
		double yMax = selectedGeo.evaluate(xMax);
		double xMinInt = ef.findMinimum(xMin,xMax,fun,5.0E-8);
		double xMaxInt = ef.findMaximum(xMin,xMax,fun,5.0E-8);
		double yMinInt = selectedGeo.evaluate(xMinInt);
		double yMaxInt = selectedGeo.evaluate(xMaxInt);

		if(yMin < yMinInt){
			yMinInt = yMin;
			xMinInt = xMin;
		}

		if(yMax > yMaxInt){
			yMaxInt = yMax;
			xMaxInt = xMax;
		}

		minPoint.setCoords(xMinInt, yMinInt, 1.0);
		//minPoint.setEuclidianVisible(!(minPoint.isEqual(lowPoint) || minPoint.isEqual(highPoint)));
		minPoint.update();
		maxPoint.setCoords(xMaxInt, yMaxInt, 1.0);
		//maxPoint.setEuclidianVisible(!(maxPoint.isEqual(lowPoint) || maxPoint.isEqual(highPoint)));
		maxPoint.update();




		// set the property/value pairs 
		//=================================================

		property.add(app.getCommand("Min"));
		value.add("(" + nf.format(xMinInt) + " , " + nf.format(yMinInt) + ")" );
		Double[] min = {xMinInt, yMinInt};
		value2.add(min);

		property.add(app.getCommand("Max"));
		value.add("(" + nf.format(xMaxInt) + " , " + nf.format(yMaxInt) + ")" );
		Double[] max = {xMaxInt, yMaxInt};
		value2.add(max);

		property.add(null);
		value.add(null );
		value2.add(null );

		// calculate roots
		ExpressionNode low = new ExpressionNode(kernel, lowPoint, Operation.XCOORD, null);
		ExpressionNode high = new ExpressionNode(kernel, highPoint, Operation.XCOORD, null);				
		AlgoDependentNumber xLow = new AlgoDependentNumber(cons, low, false);
		cons.removeFromConstructionList(xLow);
		AlgoDependentNumber xHigh = new AlgoDependentNumber(cons, high, false);
		cons.removeFromConstructionList(xHigh);

		AlgoElement roots;
		
		if (selectedGeo.isPolynomialFunction(false)) {
			roots = new AlgoRootsPolynomial(cons, selectedGeo);
		} else {
			roots = new AlgoRoots(cons, selectedGeo, (GeoNumeric)xLow.getGeoElements()[0], (GeoNumeric)xHigh.getGeoElements()[0]);			
		}
		
		cons.removeFromConstructionList(roots);		
		rootGeos = roots.getGeoElements();

		property.add(app.getCommand("Root"));
		
		int count = 0;
		double x = Double.NaN;
		double root = Double.NaN;
		
		// count how many roots in range
		for (int i = 0 ; i < rootGeos.length ; i++) {
			GeoPoint p = ((GeoPoint)rootGeos[i]);
			if (p.isDefined()) {
				double rt = p.inhomX;
				if (Kernel.isGreaterEqual(rt, xMin) && Kernel.isGreaterEqual(xMax, rt)) {
					root = rt;
					count ++;
				}
			}
		}
		StringTemplate tpl = StringTemplate.defaultTemplate;
		switch (count) {
		case 0: 
			value.add(app.getPlain("fncInspector.NoRoots"));
			value2.add(null);
			break;
		case 1: 
			value.add(kernel.format(root,tpl));
			Double[] r = {root};
			value2.add(r);
			break;
		default: 
			value.add(app.getPlain("fncInspector.MultipleRoots"));
			value2.add(null);

		}


		property.add(null);
		value.add(null );
		value2.add(null);

		property.add(app.getCommand("Integral"));
		value.add(nf.format(integral));
		Double[] in = {integral};
		value2.add(in);
		
		property.add(app.getCommand("Area"));
		value.add(nf.format(area));
		Double[] a = {area};
		value2.add(a);

		property.add(app.getCommand("Mean"));
		value.add(nf.format(mean));
		Double[] m= {mean};
		value2.add(m);

		property.add(app.getCommand("Length"));
		value.add(nf.format(length));
		Double[] l= {length};
		value2.add(l);



		// load the model with these pairs
		modelInterval.setRowCount(property.size());

		for(int i=0; i < property.size(); i++){
			modelInterval.setValueAt(property.get(i),i,0);
			modelInterval.setValueAt(value.get(i),i,1);
		}


		//tableInterval.setColumnWidths();
		isChangingValue = false;

	}




	/**
	 * Updates the XYTable with the coordinates of the current sample points and
	 * any related values (e.g. derivative, difference)
	 */
	private void updateXYTable(){

		isChangingValue = true;

		//String lbl = selectedGeo.getLabel();
		GeoFunction f = selectedGeo;

		// init the copy array 
		xyTableCopyList.clear();
		Double[] xArray = new Double[modelXY.getRowCount()];
		Double[] yArray = new Double[modelXY.getRowCount()];

		if(btnTable.isSelected()){
			double x = start - step*(pointCount-1)/2;
			double y;
			for(int i=0; i < modelXY.getRowCount(); i++){
				y = f.evaluate(x); 
				modelXY.setValueAt(nf.format(x),i,0);
				modelXY.setValueAt(nf.format(y),i,1);
				((GeoPoint) pts.get(i)).setCoords(x, y, 1);

				// collect x, y points into the copy arrays
				xArray[i] = x;
				yArray[i] = y;

				x = x + step;
			}

			pts.updateRepaint();	
		}
		else{
			double x = start;
			double y = f.evaluate(x); 
			modelXY.setValueAt(nf.format(x),0,0);
			modelXY.setValueAt(nf.format(y),0,1);

			// collect x, y points into the copy arrays
			xArray[0] = x;
			yArray[0] = y;

		}

		xyTableCopyList.add(xArray);
		xyTableCopyList.add(yArray);

		// update any extra columns added by the user (these will show derivatives, differences etc.) 
		updateExtraColumns();


		isChangingValue = false;
	}

	/**
	 * Updates any extra columns added by the user to the XYTable.
	 */
	private void updateExtraColumns(){

		if(extraColumnList.size()==0) return;

		for(int column = 2; column < extraColumnList.size() + 2; column ++ ){

			Double[] copyArray = new Double[modelXY.getRowCount()];

			int columnType = extraColumnList.get(column-2);
			switch (columnType){

			case COL_DERIVATIVE:

				for(int row=0; row < modelXY.getRowCount(); row++){
					double x = Double.parseDouble((String) modelXY.getValueAt(row, 0));
					double d = derivative.evaluate(x);// evaluateExpression(derivative.getLabel() + "(" + x + ")");
					modelXY.setValueAt(nf.format(d),row,column);
					copyArray[row] = d; 
				}	
				break;

			case COL_DERIVATIVE2:

				for(int row=0; row < modelXY.getRowCount(); row++){
					double x = Double.parseDouble((String) modelXY.getValueAt(row, 0));
					double d2 = derivative2.evaluate(x);//evaluateExpression(derivative2.getLabel() + "(" + x + ")");
					modelXY.setValueAt(nf.format(d2),row,column);
					copyArray[row] = d2; 
				}	
				break;

			case COL_CURVATURE:

				for(int row=0; row < modelXY.getRowCount(); row++){
					double x = Double.parseDouble((String) modelXY.getValueAt(row, 0));
					double y = Double.parseDouble((String) modelXY.getValueAt(row, 1));

					MyVecNode vec = new MyVecNode( kernel, new MyDouble(kernel, x), new MyDouble(kernel, y));

					ExpressionNode point = new ExpressionNode(kernel, vec, Operation.NO_OPERATION, null);
					point.setForcePoint();

					AlgoDependentPoint pointAlgo = new AlgoDependentPoint(cons, point, false);
					cons.removeFromConstructionList(pointAlgo);

					AlgoCurvature curvature = new AlgoCurvature(cons, (GeoPoint) pointAlgo.getGeoElements()[0], selectedGeo);
					cons.removeFromConstructionList(curvature);

					double c = ((GeoNumeric)curvature.getGeoElements()[0]).getDouble();

					//double c = evaluateExpression(
					//		"Curvature[ (" + x + "," + y  + ")," + selectedGeo.getLabel() + "]");
					modelXY.setValueAt(nf.format(c),row,column);
					copyArray[row] = c; 
				}	
				break;

			case COL_DIFFERENCE:

				for(int row=1; row < modelXY.getRowCount(); row++){
					if(modelXY.getValueAt(row-1, column -1) != null){
						double prev = Double.parseDouble((String) modelXY.getValueAt(row-1, column -1));
						double x = Double.parseDouble((String) modelXY.getValueAt(row, column-1));
						modelXY.setValueAt(nf.format(x - prev),row,column);
						copyArray[row] = x-prev; 
					}else{
						modelXY.setValueAt(null,row,column);
						copyArray[row] = null; 
					}

				}	
				break;

			}

			xyTableCopyList.add(copyArray);

		}
	}



	private void addColumn(int columnType){
		extraColumnList.add(columnType);
		modelXY.addColumn(columnNames[columnType]);
		tableXY.setMyCellEditor(0);
		updateXYTable();
	}

	private void removeColumn(){
		int count = tableXY.getColumnCount();
		if(count <= 2) return;

		extraColumnList.remove(extraColumnList.size()-1);
		modelXY.setColumnCount(modelXY.getColumnCount()-1);
		tableXY.setMyCellEditor(0);
		updateXYTable();

	}






	//  Action and Other Event Handlers
	// =====================================

	@Override
	public void actionPerformed(ActionEvent e) {	
		Object source = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField)source);
		}
		else if (source == btnAddColumn) {
			addColumn(btnAddColumn.getSelectedIndex());
		}	

		else if (source == btnRemoveColumn) {
			removeColumn();
		}	

		else if (source == btnOscCircle 
				|| source == btnTangent 
				|| source == btnTable
				|| source == btnXYSegments) {
			updateGUI();
		}


	}	

	private void doTextFieldActionPerformed(JTextField source) {
		try {

			String inputText = source.getText().trim();
			if (inputText == null) return;

			// allow input such as sqrt(2)
			NumberValue nv;
			nv = kernel.getAlgebraProcessor().evaluateToNumeric(inputText, false);		
			double value = nv.getDouble();



			if (source == fldStep){ 
				step = value;	
				updateXYTable();		
			}	
			else if (source == fldLow){ 
				isChangingValue = true;
				double y = selectedGeo.evaluate(value);
				lowPoint.setCoords(value, y, 1);
				lowPoint.updateCascade();
				lowPoint.updateRepaint();
				isChangingValue = false;
				updateIntervalTable();	
			}	
			else if (source == fldHigh){ 
				isChangingValue = true;
				double y = selectedGeo.evaluate(value);
				highPoint.setCoords(value, y, 1);
				highPoint.updateCascade();
				highPoint.updateRepaint();
				isChangingValue = false;
				updateIntervalTable();	
			}	



		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}



	@Override
	public void setVisible(boolean isVisible) {	

		if (isVisible) {
			app.getKernel().attach(this);
		} else {
			app.getKernel().detach(this);
			clearGeoList();
		}
		super.setVisible(isVisible);
	}





	// ====================================================
	//          View Implementation
	// ====================================================

	public void update(GeoElement geo) {

		if(selectedGeo == null 
				|| testPoint == null 
				|| lowPoint == null 
				|| highPoint == null 
				|| isChangingValue 
				|| isIniting ) 
			return;


		if(selectedGeo.equals(geo)){
			lblGeoName.setText(selectedGeo.toString(StringTemplate.defaultTemplate));
		}

		else if(tabPanel.getSelectedComponent() == pointTabPanel && testPoint.equals(geo)){
			double[] coords = new double[3];
			testPoint.getCoords(coords);
			this.start = coords[0];
			updateXYTable();
			tableXY.getSelectionModel().removeListSelectionListener(this);
			
			if(btnTable.isSelected() && tableXY.getSelectedRow() != 4)
				tableXY.changeSelection(4, 0, false, false);
			else if( !btnTable.isSelected() && tableXY.getSelectedRow() != 0)
				tableXY.changeSelection(0, 0, false, false);
			
			tableXY.getSelectionModel().addListSelectionListener(this);
			return;
		}

		else if(tabPanel.getSelectedComponent() == intervalTabPanel 
				&& (lowPoint.equals(geo) || highPoint.equals(geo)) ){


			if(lowPoint.x > highPoint.x){
				if(lowPoint.equals(geo))
					doTextFieldActionPerformed(fldLow);
				else
					doTextFieldActionPerformed(fldHigh);

			}


			updateIntervalFields();
			return;
		}

	}
	

	final public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}

	public void add(GeoElement geo) {}
	public void remove(GeoElement geo) {}
	public void rename(GeoElement geo) {}
	public void updateAuxiliaryObject(GeoElement geo) {}
	public void repaintView() {}
	public void reset() {
		setVisible(false);
	}
	public void clearView() {}
	public void setMode(int mode) {}




	// ====================================================
	//         Table Selection Listener
	// ====================================================

	public void valueChanged(ListSelectionEvent e) {

		if (e.getValueIsAdjusting() || isChangingValue) return;

		tableXY.getSelectionModel().removeListSelectionListener(this);
		if (e.getSource() == tableXY.getSelectionModel()) {
			// row selection changed
			updateTestPoint();
		}		
		tableXY.getSelectionModel().addListSelectionListener(this);
	}




	// ====================================================
	//    Geo Selection Listener
	// ====================================================

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		// TODO: not working directly yet, currently the listener
		// is in InputDialog, so an overridden insertGeoElement() is used instead
	}
	@Override
	public void insertGeoElement(GeoElement geo) {
		if(geo == null 
				|| !geo.isGeoFunction())
		{
			return;
		}

		activeEV = app.getActiveEuclidianView();	
		selectedGeo = (GeoFunction)geo;

		lblGeoName.setText(getTitleString());

		initialX = 0.5* (activeEV.getXmin()- activeEV.getXmin());
		start = initialX;

		// initial step = EV grid step 
		step = 0.25 * kernel.getApplication().getActiveEuclidianView().getGridDistances()[0];
		fldStep.removeActionListener(this);
		fldStep.setText("" + step);
		fldStep.addActionListener(this);

		defineDisplayGeos();

		double x = initialX - 4*step; 
		double y = selectedGeo.evaluate(x); 
		lowPoint.setCoords(x, y, 1);

		x = initialX + 4*step; 
		y = selectedGeo.evaluate(x); 
		highPoint.setCoords(x, y, 1);

		lowPoint.updateCascade();
		highPoint.updateCascade();

		updateGUI();
	}





	// ====================================================
	//      Key Listeners
	// ====================================================

	public void keyPressed(KeyEvent e) {

		int key = e.getKeyCode();

		tableXY.getSelectionModel().removeListSelectionListener(this);
		switch (key){
		case KeyEvent.VK_UP:
			if(tableXY.getSelectedRow()==0){
				start = start-step;
				updateXYTable();
				updateTestPoint();
			}
			break;

		case KeyEvent.VK_DOWN:
			if(tableXY.getSelectedRow()==tableXY.getRowCount()-1){
				start = start+step;
				updateXYTable();
				tableXY.changeSelection(tableXY.getRowCount()-1, 0, false, false);
				updateTestPoint();
			}
			break;
		}

		tableXY.getSelectionModel().addListSelectionListener(this);

	}

	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}




	//      Mouse Listeners
	//=========================================

	public void mouseClicked(MouseEvent arg0) { }
	public void mouseEntered(MouseEvent arg0) { }
	public void mouseExited(MouseEvent arg0) { }
	public void mousePressed(MouseEvent arg0) { }
	public void mouseReleased(MouseEvent arg0) { }




	// ====================================================
	//  Update/Create Display Geos
	// ====================================================

	private void defineDisplayGeos(){

		// remove all geos
		clearGeoList();

		GeoFunction f = selectedGeo;

		// create XY table geos
		//========================================
		// test point
		AlgoPointOnPath pAlgo = new AlgoPointOnPath(cons, f, (activeEV.getXmin() + activeEV.getXmax()) / 2, 0);
		cons.removeFromConstructionList(pAlgo);
		testPoint = (GeoPoint) pAlgo.getGeoElements()[0];
		testPoint.setObjColor(new geogebra.awt.GColorD(DISPLAY_GEO_COLOR));
		testPoint.setPointSize(4);
		testPoint.setLayer(f.getLayer()+1);
		pointTabGeoList.add(testPoint);


		// X segment
		ExpressionNode xcoord = new ExpressionNode(kernel, testPoint, Operation.XCOORD, null);
		MyVecNode vec = new MyVecNode( kernel, xcoord, new MyDouble(kernel, 0.0));
		ExpressionNode point = new ExpressionNode(kernel, vec, Operation.NO_OPERATION, null);
		point.setForcePoint();
		AlgoDependentPoint pointAlgo = new AlgoDependentPoint(cons, point, false);
		cons.removeFromConstructionList(pointAlgo);

		AlgoJoinPointsSegment seg1 = new AlgoJoinPointsSegment(cons, testPoint, (GeoPoint)pointAlgo.getGeoElements()[0], null);
		cons.removeFromConstructionList(seg1);	
		xSegment = seg1.getGeoElements()[0];
		xSegment.setObjColor(new geogebra.awt.GColorD(DISPLAY_GEO_COLOR));
		xSegment.setLineThickness(3);
		xSegment.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		xSegment.setEuclidianVisible(true);
		xSegment.setFixed(true);
		pointTabGeoList.add(xSegment);


		// Y segment
		ExpressionNode ycoord = new ExpressionNode(kernel, testPoint, Operation.YCOORD, null);
		MyVecNode vecy = new MyVecNode( kernel, new MyDouble(kernel, 0.0), ycoord);
		ExpressionNode pointy = new ExpressionNode(kernel, vecy, Operation.NO_OPERATION, null);
		pointy.setForcePoint();
		AlgoDependentPoint pointAlgoy = new AlgoDependentPoint(cons, pointy, false);
		cons.removeFromConstructionList(pointAlgoy);	

		AlgoJoinPointsSegment seg2 = new AlgoJoinPointsSegment(cons, testPoint, (GeoPoint)pointAlgoy.getGeoElements()[0], null);
		cons.removeFromConstructionList(seg2);

		ySegment = seg2.getGeoElements()[0];
		ySegment.setObjColor(new geogebra.awt.GColorD(DISPLAY_GEO_COLOR));
		ySegment.setLineThickness(3);
		ySegment.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		ySegment.setEuclidianVisible(true);
		ySegment.setFixed(true);
		pointTabGeoList.add(ySegment);


		// tangent line		
		AlgoTangentFunctionPoint tangent = new AlgoTangentFunctionPoint(cons, testPoint, f);
		cons.removeFromConstructionList(tangent);
		tangentLine = tangent.getGeoElements()[0];
		tangentLine.setObjColor(new geogebra.awt.GColorD(DISPLAY_GEO_COLOR));
		tangentLine.setEuclidianVisible(false);
		pointTabGeoList.add(tangentLine);


		// osculating circle
		AlgoOsculatingCircle oc = new AlgoOsculatingCircle(cons, testPoint, f);
		cons.removeFromConstructionList(oc);
		oscCircle = oc.getGeoElements()[0];
		oscCircle.setObjColor(new geogebra.awt.GColorD(DISPLAY_GEO_COLOR));
		oscCircle.setEuclidianVisible(false);
		pointTabGeoList.add(oscCircle);


		// derivative
		AlgoDerivative deriv = new AlgoDerivative(cons, f);
		cons.removeFromConstructionList(deriv);
		derivative = (GeoFunction)deriv.getGeoElements()[0];
		derivative.setEuclidianVisible(false);
		hiddenGeoList.add(derivative);

		// 2nd derivative
		AlgoDerivative deriv2 = new AlgoDerivative(cons, f, null, new MyDouble(kernel, 2.0));
		cons.removeFromConstructionList(deriv2);
		derivative2 = (GeoFunction)deriv2.getGeoElements()[0];
		derivative2.setEuclidianVisible(false);
		hiddenGeoList.add(derivative2);


		// point list
		pts = new GeoList(cons);
		pts.setEuclidianVisible(true);
		pts.setObjColor(GeoGebraColorConstants.DARKGRAY);
		pts.setPointSize(3);
		pts.setLayer(f.getLayer()+1);
		for(int i = 0; i < pointCount; i++){
			pts.add(new GeoPoint(cons));
		}
		pointTabGeoList.add(pts);



		// create interval table geos
		//================================================

		// interval points
		AlgoPointOnPath pxAlgo = new AlgoPointOnPath(cons, f, (2 * activeEV.getXmin() + activeEV.getXmax()) / 3, 0);
		cons.removeFromConstructionList(pxAlgo);
		lowPoint = (GeoPoint) pxAlgo.getGeoElements()[0];
		lowPoint.setEuclidianVisible(false);
		lowPoint.setPointSize(4);
		lowPoint.setObjColor(new geogebra.awt.GColorD(DISPLAY_GEO_COLOR));
		lowPoint.setLayer(f.getLayer()+1);
		intervalTabGeoList.add(lowPoint);


		AlgoPointOnPath pyAlgo = new AlgoPointOnPath(cons, f, (activeEV.getXmin() + 2 * activeEV.getXmax()) / 3, 0);
		cons.removeFromConstructionList(pyAlgo);
		highPoint = (GeoPoint) pyAlgo.getGeoElements()[0];
		highPoint.setEuclidianVisible(false);
		highPoint.setPointSize(4);
		highPoint.setObjColor(new geogebra.awt.GColorD(DISPLAY_GEO_COLOR));
		highPoint.setLayer(f.getLayer()+1);
		intervalTabGeoList.add(highPoint);


		ExpressionNode low = new ExpressionNode(kernel, lowPoint, Operation.XCOORD, null);
		ExpressionNode high = new ExpressionNode(kernel, highPoint, Operation.XCOORD, null);				
		AlgoDependentNumber xLow = new AlgoDependentNumber(cons, low, false);
		cons.removeFromConstructionList(xLow);
		AlgoDependentNumber xHigh = new AlgoDependentNumber(cons, high, false);
		cons.removeFromConstructionList(xHigh);


		AlgoFunctionInterval interval = new AlgoFunctionInterval(cons, f, (NumberValue)xLow.getGeoElements()[0], (NumberValue)xHigh.getGeoElements()[0]);
		cons.removeFromConstructionList(interval);	

		functionInterval = interval.getGeoElements()[0];
		functionInterval.setEuclidianVisible(false);
		functionInterval.setLineThickness(selectedGeo.getLineThickness()+5);
		functionInterval.setObjColor(new geogebra.awt.GColorD(DISPLAY_GEO_COLOR));
		functionInterval.setLayer(f.getLayer()+1);
		intervalTabGeoList.add(functionInterval);

		AlgoIntegralDefinite inte = new AlgoIntegralDefinite(cons, selectedGeo, (NumberValue)xLow.getGeoElements()[0], (NumberValue)xHigh.getGeoElements()[0], null, false);
		cons.removeFromConstructionList(inte);
		integralGeo = inte.getGeoElements()[0];
		integralGeo.setEuclidianVisible(false);
		integralGeo.setObjColor(new geogebra.awt.GColorD(DISPLAY_GEO_COLOR));
		intervalTabGeoList.add(integralGeo);
		
		ExpressionNode en = new ExpressionNode(kernel, selectedGeo, Operation.ABS, null);
		AlgoDependentFunction funAlgo = new AlgoDependentFunction(cons, (Function) en.evaluate(StringTemplate.defaultTemplate));
		cons.removeFromConstructionList(funAlgo);
		
		//the antiderivative of a function containing the absolute function might be difficult to find if it exists at all. Therefore the definite integral is calculated numerically.
		AlgoIntegralDefinite area = new AlgoIntegralDefinite(cons, (GeoFunction)funAlgo.getGeoElements()[0], (NumberValue)xLow.getGeoElements()[0], (NumberValue)xHigh.getGeoElements()[0], null, true);
		cons.removeFromConstructionList(area);
		areaGeo = area.getGeoElements()[0];
		areaGeo.setEuclidianVisible(false);
		intervalTabGeoList.add(areaGeo);
		

		AlgoLengthFunction len = new AlgoLengthFunction(cons, selectedGeo, (GeoNumeric)xLow.getGeoElements()[0], (GeoNumeric)xHigh.getGeoElements()[0]);
		cons.removeFromConstructionList(len);
		lengthGeo = len.getGeoElements()[0];
		hiddenGeoList.add(lengthGeo);

		minPoint = new GeoPoint(cons);
		minPoint.setEuclidianVisible(false);
		minPoint.setPointSize(4);
		minPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND);
		minPoint.setObjColor(new geogebra.awt.GColorD(DISPLAY_GEO_COLOR).darker());
		minPoint.setLayer(f.getLayer()+1);
		minPoint.setFixed(true);
		intervalTabGeoList.add(minPoint);

		maxPoint = new GeoPoint(cons);
		maxPoint.setEuclidianVisible(false);
		maxPoint.setPointSize(4);
		maxPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND);
		maxPoint.setObjColor(new geogebra.awt.GColorD(DISPLAY_GEO_COLOR).darker());
		maxPoint.setLayer(f.getLayer()+1);
		maxPoint.setFixed(true);
		intervalTabGeoList.add(maxPoint);




		// process the geos
		// ==================================================

		// add the display geos to the active EV and hide the tooltips 
		for(GeoElement geo:intervalTabGeoList){
			activeEV.add(geo);
			geo.setTooltipMode(GeoElement.TOOLTIP_OFF);
			geo.update();

		}	
		for(GeoElement geo:pointTabGeoList){
			activeEV.add(geo);
			geo.setTooltipMode(GeoElement.TOOLTIP_OFF);
			geo.update();
		}	

		updateTestPoint();
		activeEV.repaint();


	}



	private void updateTestPoint(){

		if(testPoint == null || isIniting ) return;

		isChangingValue = true;
		int row = tableXY.getSelectedRow();
		if (row >= 0){
			double x = Double.parseDouble((String) modelXY.getValueAt(row, 0));
			double y = selectedGeo.evaluate(x); 
			testPoint.setCoords(x, y, 1);
			testPoint.updateRepaint();	
		}
		isChangingValue = false;

	}

	private void clearGeoList(){
		for(GeoElement geo : intervalTabGeoList){
			if(geo != null){
				geo.remove();
			}
		}
		intervalTabGeoList.clear();

		for(GeoElement geo : pointTabGeoList){
			if(geo != null){
				geo.remove();
			}
		}
		pointTabGeoList.clear();

		for(GeoElement geo : hiddenGeoList){
			if(geo != null){
				geo.remove();
			}
		}
		hiddenGeoList.clear();

		rootGeos = null;
	}

	public void updateFonts(){
		wrappedDialog.setFont(app.getPlainFont());
		tableXY.setFont(app.getPlainFont());
		tableInterval.setFont(app.getPlainFont());
		MyTextField dummyField = new MyTextField(app);
		tableXY.setRowHeight(dummyField.getPreferredSize().height);
		tableInterval.setRowHeight(dummyField.getPreferredSize().height);

		GuiManagerD.setFontRecursive(wrappedDialog, app.getPlainFont());
	}



	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		if (!wrappedDialog.isModal()) {
			//if(app.getMode() == EuclidianConstants.MODE_FUNCTION_INSPECTOR)
			//app.setSelectionListenerMode(sl);
		}
		app.getGuiManager().setCurrentTextfield(this, true);
	}





	public void changeStart(double x) {
		tableXY.getSelectionModel().removeListSelectionListener(this);
		try {
			start = x;
			//Application.debug("" + start);
			updateXYTable();
			updateTestPoint();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		tableXY.getSelectionModel().addListSelectionListener(this);
	}



	private void updateIntervalGeoVisiblity(){

		//	minPoint.setEuclidianVisible(tableInterval.isRowSelected(0));
		minPoint.setEuclidianVisible(false);
		minPoint.update();
		//	maxPoint.setEuclidianVisible(tableInterval.isRowSelected(1));
		maxPoint.setEuclidianVisible(false);
		maxPoint.update();




		//	integralGeo.setEuclidianVisible(tableInterval.isRowSelected(5));
		areaGeo.setEuclidianVisible(false);
		areaGeo.update();
		integralGeo.setEuclidianVisible(true);
		integralGeo.update();

		activeEV.repaint();
	}



	public SpecialNumberFormat getMyNumberFormat() {
		return nf;
	}


	public void changedNumberFormat() {
		this.updateGUI();
		this.updateIntervalFields();
		this.updateTestPoint();

	}


	private void createOptionsButton(){

		if(btnOptions == null){
			btnOptions = new PopupMenuButton(app);
			btnOptions.setKeepVisible(true);
			btnOptions.setStandardButton(true);
			btnOptions.setFixedIcon(app.getImageIcon("tool.png"));
			btnOptions.setDownwardPopup(true);
		}

		btnOptions.removeAllMenuItems();

		btnOptions.setToolTipText(app.getMenu("Options"));


		// copy to spreadsheet
		JMenuItem mi = new JMenuItem(app.getMenu("CopyToSpreadsheet"));
		mi.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				doCopyToSpreadsheet();
			}
		});
		mi.setEnabled(app.getGuiManager().hasSpreadsheetView());
		btnOptions.addPopupMenuItem(mi);


		// rounding
		btnOptions.addPopupMenuItem(getMyNumberFormat().createMenuDecimalPlaces());

	}




	private void doCopyToSpreadsheet(){

		SpreadsheetView sp = app.getGuiManager().getSpreadsheetView();
		if(sp == null) return;

		Construction cons = app.getKernel().getConstruction();
		GeoElement geo = null;
		String str;
		Double number;
		int targetColumn = app.getSpreadsheetTableModel().getHighestUsedColumn();

		if(tabPanel.getSelectedComponent() == pointTabPanel){

			for(int c = 0; c < tableXY.getColumnCount(); c++ ){
				targetColumn ++;
				for(int row = 0; row < tableXY.getRowCount() + 1; row++){
					// copy table header
					if(row == 0){
						geo = new GeoText(cons, tableXY.getColumnName(c));
						processCellGeo(geo,targetColumn, row);
					}
					// copy column data value
					else if(xyTableCopyList.get(c)[row-1] != null){
						geo = new GeoNumeric(cons, xyTableCopyList.get(c)[row-1]);
						processCellGeo(geo,targetColumn, row);
					}
				}
			}
		}

		else{
			for(int c = 0; c < tableInterval.getColumnCount(); c++ ){
				targetColumn ++;
				for(int row = 0; row < tableInterval.getRowCount(); row++){

					// first column has property names
					if(c == 0 && property.get(row) != null){
						geo = new GeoText(cons, property.get(row));	
						processCellGeo(geo,targetColumn, row);
					}

					// remaining columns have data
					else if(value2.get(row) != null){

						for(int k = 0; k < value2.get(row).length; k++)
							if(value2.get(row)[k] != null){	
								geo = new GeoNumeric(cons, value2.get(row)[k]);
								processCellGeo(geo,targetColumn + k, row);
							}
					}
				}
			}
		}

	}

	private static void processCellGeo(GeoElement geo, int column, int row){
		geo.setLabel(GeoElementSpreadsheet.getSpreadsheetCellName(column, row));
		geo.setEuclidianVisible(false);
		geo.setAuxiliaryObject(true);
		geo.update();
	}

	public int getViewID() {
		return App.VIEW_FUNCTION_INSPECTOR;
	}


	public boolean hasFocus() {
		return wrappedDialog.hasFocus();
	}
	
	
}





