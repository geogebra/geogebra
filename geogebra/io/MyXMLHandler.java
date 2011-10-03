/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * MyXMLHandler.java
 *
 * Created on 14. Juni 2003, 12:04
 */

package geogebra.io;

import geogebra.GeoGebra;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.view.probcalculator.ProbabilityCalculator;
import geogebra.gui.view.spreadsheet.TraceSettings;
import geogebra.io.layout.DockPanelXml;
import geogebra.io.layout.DockSplitPaneXml;
import geogebra.io.layout.Perspective;
import geogebra.kernel.AbsoluteScreenLocateable;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoButton;
import geogebra.kernel.GeoCasCell;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoTextField;
import geogebra.kernel.GeoUserInputElement;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.Kernel;
import geogebra.kernel.LimitedPath;
import geogebra.kernel.Locateable;
import geogebra.kernel.Macro;
import geogebra.kernel.MacroKernel;
import geogebra.kernel.PointProperties;
import geogebra.kernel.TextProperties;
import geogebra.kernel.Traceable;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.kernel.implicit.GeoImplicitPoly;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.parser.Parser;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra.main.settings.ConstructionProtocolSettings;
import geogebra.main.settings.EuclidianSettings;
import geogebra.main.settings.KeyboardSettings;
import geogebra.main.settings.SpreadsheetSettings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.swing.JSplitPane;
import javax.swing.ToolTipManager;

import org.xml.sax.SAXException;

/**
 * 
 * @author Markus Hohenwarter
 */
// public class MyXMLHandler extends DefaultHandler {
public class MyXMLHandler implements DocHandler {

	private static final double FORMAT = Double.parseDouble(GeoGebra.XML_FILE_FORMAT);

	private static final int MODE_INVALID = -1;
	private static final int MODE_GEOGEBRA = 1;
	private static final int MODE_MACRO = 50;
	private static final int MODE_EUCLIDIAN_VIEW = 100;
	/** currently parsing tags for Euclidian3D view */
	protected static final int MODE_EUCLIDIAN_VIEW3D = 101; //only for 3D
	private static final int MODE_SPREADSHEET_VIEW = 150;
	private static final int MODE_ALGEBRA_VIEW = 151;
	//private static final int MODE_CAS_VIEW = 160;
	private static final int MODE_CONST_CAS_CELL = 161;
	private static final int MODE_CAS_CELL_PAIR = 162;
	private static final int MODE_CAS_INPUT_CELL = 163;
	private static final int MODE_CAS_OUTPUT_CELL = 164;
	private static final int MODE_PROBABILITY_CALCULATOR = 170;
	private static final int MODE_KERNEL = 200;
	private static final int MODE_CONSTRUCTION = 300;
	private static final int MODE_CONST_GEO_ELEMENT = 301;
	private static final int MODE_CONST_COMMAND = 302;
	
	private static final int MODE_GUI = 400;
	private static final int MODE_GUI_PERSPECTIVES = 401; // <perspectives>
	private static final int MODE_GUI_PERSPECTIVE = 402; // <perspective>
	private static final int MODE_GUI_PERSPECTIVE_PANES = 403; // <perspective> <panes /> </perspective>
	private static final int MODE_GUI_PERSPECTIVE_VIEWS = 404; // <perspective> <views /> </perspective>

	// these two will be reused in OptionsAdvanced
	final public static int[] menuFontSizes = {12, 14, 16, 18, 20, 24, 28, 32};
	final public static String[] tooltipTimeouts = new String[] {
		"1",
		"3",
		"5",
		"10",
		"20",
		"30",
		"60",
		"0"
	};

	private int mode;
	private int constMode; // submode for <construction>
	private int casMode; // submode for <cascell>

	protected GeoElement geo;
	private GeoCasCell geoCasCell;
	private Command cmd;
	private Macro macro;
	/** application */
	protected Application app;
	
	private String[] macroInputLabels, macroOutputLabels;
	private GeoElement[] cmdOutput;
	private boolean startAnimation;
	
	/**
	 * The point style of the document, for versions < 3.3
	 */
	private int docPointStyle; 

	// for macros we need to change the kernel, so remember the original kernel
	// too
	private Kernel kernel, origKernel;
	/** construction */
	protected Construction cons;

	private Construction origCons;
	private Parser parser, origParser;

	// List of LocateableExpPair objects
	// for setting the start points at the end of the construction
	// (needed for GeoText and GeoVector)
	private LinkedList<LocateableExpPair> startPointList = new LinkedList<LocateableExpPair>();

	// List of GeoExpPair objects
	// for setting the linked geos needed for GeoTextFields
	private LinkedList<GeoExpPair> linkedGeoList = new LinkedList<GeoExpPair>();

	// List of GeoExpPair condition objects
	// for setting the conditions at the end of the construction
	// (needed for GeoText and GeoVector)
	private LinkedList<GeoExpPair> showObjectConditionList = new LinkedList<GeoExpPair>();
	private LinkedList<GeoExpPair> dynamicColorList = new LinkedList<GeoExpPair>();
	private LinkedList<GeoExpPair> animationSpeedList = new LinkedList<GeoExpPair>();
	private LinkedList<GeoExpPair> animationStepList = new LinkedList<GeoExpPair>();
	private LinkedList<GeoElement> animatingList = new LinkedList<GeoElement>();
	private LinkedList<GeoExpPair> minMaxList = new LinkedList<GeoExpPair>();
	

	private class GeoExpPair {
		GeoElement geo;
		String exp;

		GeoExpPair(GeoElement g, String exp) {
			geo = g;
			this.exp = exp;
		}
	}
	
	private class LocateableExpPair {
		Locateable locateable;
		String exp; // String with expression to create point 
		GeoPointND point; // free point
		int number; // number of startPoint

		LocateableExpPair(Locateable g, String s, int n) {
			locateable = g;
			exp = s;
			number = n;
		}
		
		LocateableExpPair(Locateable g, GeoPointND p, int n) {
			locateable = g;
			point = p;
			number = n;
		}
	}

	// construction step stored in <consProtNavigation> : handled after parsing
	private int consStep;

	private double ggbFileFormat;
	
	private boolean hasGuiElement = false;

	/**
	 * The storage container for all GUI related information of the current document.
	 */
	private Perspective tmp_perspective;
	
	/**
	 * A vector with all perspectives we have read in this document.
	 */
	private ArrayList<Perspective> tmp_perspectives = new ArrayList<Perspective>();
	
	/**
	 * Array lists to store temporary panes and views of a perspective.
	 */
	private ArrayList<DockSplitPaneXml> tmp_panes;
	private ArrayList<DockPanelXml>tmp_views;
	
	/**
	 * Backward compatibility for version < 3.03 where no layout component was used.
	 * Temporary storage for the split divider location of the split panes #1/#2. 
	 */
	private int tmp_sp1, tmp_sp2;
	
	/**
	 * If the split divider is horizontal. (version < 3.03)
	 */
	private boolean tmp_spHorizontal;
	
	/**
	 * If the algebra or spreadsheet view is visible. (version < 3.03)
	 */
	private boolean tmp_showAlgebra, tmp_showSpreadsheet;

	//indicate the view no currently parsing
	private int viewNo=0;

	// flag so that we can reset EVSettings the first time we get them (for EV1 and EV2)
	private boolean resetEVsettingsNeeded = false;

	

	/** Creates a new instance of MyXMLHandler 
	 * @param kernel 
	 * @param cons */
	public MyXMLHandler(Kernel kernel, Construction cons) {
		origKernel = kernel;
		origCons = cons;
		origParser = new Parser(origKernel, origCons);
		app = origKernel.getApplication();
		initKernelVars();

		mode = MODE_INVALID;
		constMode = MODE_CONSTRUCTION;
	}

	private void reset(boolean start) {
		startPointList.clear();
		showObjectConditionList.clear();
		dynamicColorList.clear();
		if (start) consStep = -2;

		mode = MODE_INVALID;
		constMode = MODE_CONSTRUCTION;
		hasGuiElement = false;

		initKernelVars();
	}

	private void initKernelVars() {
		this.kernel = origKernel;
		this.parser = origParser;
		this.cons = origKernel.getConstruction();
	}

	public int getConsStep() {
		return consStep;
	}

	// ===============================================
	// SAX ContentHandler methods
	// ===============================================

	final public void text(String str) throws SAXException {
	}

	final public void startDocument() throws SAXException {
		reset(true);			
	}

	final public void endDocument() throws SAXException {				
		if (mode == MODE_INVALID)
			throw new SAXException(app.getPlain("XMLTagANotFound","<geogebra>"));
	}

	final public void startElement(String eName, LinkedHashMap<String, String> attrs)
			throws SAXException {
		// final public void startElement(
		// String namespaceURI,
		// String sName,
		// String qName,
		// LinkedHashMap<String, String> attrs)
		// throws SAXException {
		// String eName = qName;

		switch (mode) {
		case MODE_GEOGEBRA: // top level mode
			startGeoGebraElement(eName, attrs);
			break;

		case MODE_EUCLIDIAN_VIEW:
			startEuclidianViewElement(eName, attrs);
			break;			
			
		case MODE_EUCLIDIAN_VIEW3D:
			startEuclidianView3DElement(eName, attrs);
			break;

		case MODE_SPREADSHEET_VIEW:
			startSpreadsheetViewElement(eName, attrs);
			break;

		case MODE_ALGEBRA_VIEW:
			startAlgebraViewElement(eName, attrs);
			break;

//		case MODE_CAS_VIEW:
//			startCASViewElement(eName, attrs);
//			break;
			
		case MODE_PROBABILITY_CALCULATOR:
			startProbabilityCalculatorElement(eName, attrs);
			break;

		case MODE_KERNEL:
			startKernelElement(eName, attrs);
			break;

		case MODE_MACRO:
			startMacroElement(eName, attrs);
			break;

		case MODE_CONSTRUCTION:
			startConstructionElement(eName, attrs);
			break;

		case MODE_GUI:
			startGuiElement(eName, attrs);
			break;
			
		case MODE_GUI_PERSPECTIVES:
			startGuiPerspectivesElement(eName, attrs);
			break;
		
		case MODE_GUI_PERSPECTIVE:
			startGuiPerspectiveElement(eName, attrs);
			break;
		
		case MODE_GUI_PERSPECTIVE_PANES:
			startGuiPanesElement(eName, attrs);
			break;
			
		case MODE_GUI_PERSPECTIVE_VIEWS:
			startGuiViewsElement(eName, attrs);
			break;

		case MODE_INVALID:
			// is this a geogebra file?
			if (eName.equals("geogebra")) {
				mode = MODE_GEOGEBRA;
				// check file format version
				try {
					ggbFileFormat = Double.parseDouble((String) attrs
							.get("format"));
					
					ggbFileFormat = kernel.checkDecimalFraction(ggbFileFormat);

					if (ggbFileFormat > FORMAT) {
						System.err.println(app.getError("FileFormatNewer")
								+ ": " + ggbFileFormat); // Michael
						// Borcherds
					}

					// removed - doesn't work over an undo
					// fileFormat dependent settings for downward compatibility
					//if (ggbFileFormat < 2.6) {
					//	kernel.arcusFunctionCreatesAngle = true;
					//}
					
					
					if (ggbFileFormat < 3.0) {
						// before V3.0 the kernel had continuity always on
						if (!(kernel instanceof MacroKernel))
							kernel.setContinuous(true);

						// before V3.0 the automaticGridDistanceFactor was 0.5
						EuclidianView.automaticGridDistanceFactor = 0.5;
					}

				} catch (Exception e) {
					throw new MyError(app, "FileFormatUnknown");
				}
				
				String uniqueId = (String) attrs.get("id");
				if (uniqueId != null) app.setUniqueId(uniqueId);
			}
			break;

		default:
			System.err.println("unknown mode: " + mode);
		}
	}

	private void startScriptingElement(String eName,
			LinkedHashMap<String, String> attrs) {
		try{
			String scriptingLanguage = attrs.get("language");
			app.setScriptingLanguage(scriptingLanguage);
			
			boolean blockScripting = "true".equals(attrs.get("blocked"));
			app.setBlockUpdateScripts(blockScripting);
			
			boolean scriptingDisabled = "true".equals(attrs.get("disabled"));
			app.setScriptingDisabled(scriptingDisabled);
		}catch(Exception e){
			System.err.println("error in element <scripting>");
		}
	}

	// set mode back to geogebra mode
	final public void endElement(String eName)
	// public void endElement(String namespaceURI, String sName, String qName)
			throws SAXException {
		// String eName = qName;
		switch (mode) {
		case MODE_EUCLIDIAN_VIEW:
			if (eName.equals("euclidianView")){
				if(viewNo==2){
					viewNo=0;
				}
				mode = MODE_GEOGEBRA;
			}
			break;
		case MODE_EUCLIDIAN_VIEW3D:
			if (eName.equals("euclidianView3D"))
				mode = MODE_GEOGEBRA;
			break;

		case MODE_ALGEBRA_VIEW:
			if (eName.equals("algebraView"))
				mode = MODE_GEOGEBRA;
			break;

		case MODE_SPREADSHEET_VIEW:
			if (eName.equals("spreadsheetView"))
				mode = MODE_GEOGEBRA;
			break;
			
		case MODE_PROBABILITY_CALCULATOR:
			if (eName.equals("probabilityCalculator"))
				mode = MODE_GEOGEBRA;
			break;
			
//		case MODE_CAS_VIEW:
//			if (eName.equals("casView"))
//				mode = MODE_GEOGEBRA;
//			break;

		case MODE_KERNEL:
			if (eName.equals("kernel"))
				mode = MODE_GEOGEBRA;
			break;

		case MODE_GUI:
			if (eName.equals("gui"))
				mode = MODE_GEOGEBRA;
			break;
			
		case MODE_GUI_PERSPECTIVES:
			if(eName.equals("perspectives"))
				mode = MODE_GUI;
				endGuiPerspectivesElement(); // save all perspectives
			break;
			
		case MODE_GUI_PERSPECTIVE:
			if(eName.equals("perspective"))
				mode = MODE_GUI_PERSPECTIVES;
				endGuiPerspectiveElement(); // save views & panes of the perspective
			break;
			
		case MODE_GUI_PERSPECTIVE_PANES:
			if(eName.equals("panes"))
				mode = MODE_GUI_PERSPECTIVE;
			break;
			
		case MODE_GUI_PERSPECTIVE_VIEWS:
			if(eName.equals("views"))
				mode = MODE_GUI_PERSPECTIVE;
			break;

		case MODE_CONSTRUCTION:
			endConstructionElement(eName);
			break;

		case MODE_MACRO:
			if (eName.equals("macro")) {
				endMacro();
				mode = MODE_GEOGEBRA;
			}
			break;

		case MODE_GEOGEBRA:
			if (eName.equals("geogebra")) {
				// start animation if necessary
				if (startAnimation) {
					if (app.isApplet())
						// start later, in initInBackground()
						kernel.setWantAnimationStarted(true);
					else
						kernel.getAnimatonManager().startAnimation();					
				}
				
				// perform tasks to maintain backward compability
				if(ggbFileFormat < 3.3 && hasGuiElement) {
					createCompabilityLayout();
				}
			}
			break;
		}
	}

	// ====================================
	// <geogebra>
	// ====================================
	private void startGeoGebraElement(String eName, LinkedHashMap<String, String> attrs) {
		if (eName.equals("euclidianView")) {
			mode = MODE_EUCLIDIAN_VIEW;
			resetEVsettingsNeeded  = true;
		}else if (eName.equals("euclidianView3D")) {
			mode = MODE_EUCLIDIAN_VIEW3D;
		}else if (eName.equals("algebraView")) {
			mode = MODE_ALGEBRA_VIEW;
		} else if (eName.equals("kernel")) {
				mode = MODE_KERNEL;
		} else if (eName.equals("spreadsheetView")) {
			mode = MODE_SPREADSHEET_VIEW;
//		} else if (eName.equals("casView")) {
//			mode = MODE_CAS_VIEW;		
		} else if (eName.equals("scripting")) {
			startScriptingElement(eName,attrs);
		}  
		else if (eName.equals("probabilityCalculator")) {
			mode = MODE_PROBABILITY_CALCULATOR;
		} 
		else if (eName.equals("gui")) {
			mode = MODE_GUI;
			hasGuiElement = true;
			
			if(ggbFileFormat < 3.3)
				tmp_perspective = new Perspective("tmp");
		} else if (eName.equals("macro")) {
			mode = MODE_MACRO;
			initMacro(attrs);
		} else if (eName.equals("construction")) {
			mode = MODE_CONSTRUCTION;
			handleConstruction(attrs);
		}
		else if (eName.equals("casSession")) {
			// old <casSession> is now <cascell> in <construction>
			// not used anymore after 2011-08-16
			mode = MODE_CONSTRUCTION;
			constMode = MODE_CONST_CAS_CELL;
		}
		else if (eName.equals("keyboard")) {
			handleKeyboard(attrs);
		}
		else {
			System.err.println("unknown tag in <geogebra>: " + eName);
		}
	}

	private void handleKeyboard(LinkedHashMap<String, String> attrs) {
		//TODO what if GuiManager is null?
		try{			
			int width = Integer.parseInt(attrs.get("width"));			
			KeyboardSettings kbs = app.getSettings().getKeyboard();			
			kbs.setKeyboardWidth(width);
			int height = Integer.parseInt(attrs.get("height"));			
			kbs.setKeyboardHeight(height);
			float opacity = Float.parseFloat(attrs.get("opacity"));
			kbs.setKeyboardOpacity(opacity);
			boolean showOnStart = Boolean.parseBoolean(attrs.get("show"));			
			kbs.setShowKeyboardOnStart(showOnStart);			
			kbs.setKeyboardLocale(attrs.get("language"));
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("error in element <keyboard>");
		}
		
	}

	private void startMacroElement(String eName, LinkedHashMap<String, String> attrs) {
		if (eName.equals("macroInput")) {
			macroInputLabels = getAttributeStrings(attrs);
		} else if (eName.equals("macroOutput")) {
			macroOutputLabels = getAttributeStrings(attrs);
		} else if (eName.equals("construction")) {
			mode = MODE_CONSTRUCTION;
			handleConstruction(attrs);
		} else {
			System.err.println("unknown tag in <macro>: " + eName);
		}
	}
	
	
	// ====================================
	// <euclidianView3D> only used in 3D
	// ====================================
	/** only used in MyXMLHandler3D
	 * @param eName
	 * @param attrs
	 */
	protected void startEuclidianView3DElement(String eName, LinkedHashMap<String, String> attrs) {
		Application.debug("TODO : warn that it's a 3D file");
	}
	
	

	// ====================================
	// <euclidianView>
	// ====================================
	
	private void startEuclidianViewElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;
		EuclidianSettings evSet=null;
		
		// must do this first
		if(eName.equals("viewNumber")){
			int number = Integer.parseInt((String) attrs.get("viewNo"));
			if(number==2){
				viewNo=number;
			}
		}

		if(viewNo==2){
			evSet = app.getSettings().getEuclidian(2);
		}
		else{
			evSet = app.getSettings().getEuclidian(1);
		}

		// make sure eg is reset the first time (for each EV) we get the settings
		// "viewNumber" not stored for EV1 so we need to do this here
		if (resetEVsettingsNeeded) {
			resetEVsettingsNeeded = false;
			evSet.reset();
		}
		
		switch (eName.charAt(0)) {
		case 'a':
			if (eName.equals("axesColor")) {
				ok = handleAxesColor(evSet, attrs);
				break;
			} else if (eName.equals("axis")) {
				ok = handleAxis(evSet, attrs);
				break;
			}

		case 'b':
			if (eName.equals("bgColor")) {
				ok = handleBgColor(evSet, attrs);
				break;
			}

		case 'c':
			if (eName.equals("coordSystem")) {
				ok = handleCoordSystem(evSet, attrs);
				break;
			}

		case 'e':
			if (eName.equals("evSettings")) {
				ok = handleEvSettings(evSet, attrs);
				break;
			}

		case 'g':
			if (eName.equals("grid")) {
				ok = handleGrid(evSet, attrs);
				break;
			} else if (eName.equals("gridColor")) {
				ok = handleGridColor(evSet, attrs);
				break;
			}
		case 'l':
			if (eName.equals("lineStyle")) {
				ok = handleLineStyle(evSet, attrs);
				break;
			}

		case 's':
			if (eName.equals("size")) {
				ok = handleEvSize(evSet, attrs);
				break;
			}
		case 'v':
			if(eName.equals("viewNumber")){
				/* moved earlier, must check first
				int number = Integer.parseInt((String) attrs.get("viewNo"));
				if(number==2){
					viewNo=number;
				}*/
				ok = true;
				break;
			}

		default:
			System.err.println("unknown tag in <euclidianView>: " + eName);
		}

		if (!ok)
			System.err.println("error in <euclidianView>: " + eName);
	}

	// ====================================
	// <SpreadsheetView>
	// ====================================
	private void startSpreadsheetViewElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;

		switch (eName.charAt(0)) {

		case 'l':
			if (eName.equals("layout")) {
				ok = handleSpreadsheetLayout(attrs);
				break;
			}

		case 'p':
			if (eName.equals("prefCellSize")) {
				ok = handleSpreadsheetCellSize(attrs);
				break;
			}
			
		case 's':
			if (eName.equals("size")) {
				ok = handleSpreadsheetSize(attrs);
				break;
			}
			if (eName.equals("spreadsheetColumn")) {
				ok = handleSpreadsheetColumn(attrs);
				break;
			}
			if (eName.equals("spreadsheetRow")) {
				ok = handleSpreadsheetRow(attrs);
				break;
			}
			if (eName.equals("selection")) {
				ok = handleSpreadsheetInitalSelection(attrs);
				break;
			}

			if (eName.equals("spreadsheetBrowser")) {
				ok = handleSpreadsheetBrowser(attrs);
				break;
			}
			if (eName.equals("spreadsheetCellFormat")) {
				ok = handleSpreadsheetFormat(attrs);
				break;
			}
			
		default:
			System.err.println("unknown tag in <spreadsheetView>: " + eName);
		}

		if (!ok)
			System.err.println("error in <spreadsheetView>: " + eName);
	}

	// ====================================
	// <ProbabilityCalculator>
	// ====================================
	private void startProbabilityCalculatorElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;
		
		switch (eName.charAt(0)) {

		case 'd':
			if (eName.equals("distribution")) {
				if (app.useFullGui())
					ok = handleProbabilityDistribution(attrs);
				break;
			}
		default:
			System.err.println("unknown tag in <probabilityCalculator>: " + eName);
		}

		if (!ok)
			System.err.println("error in <probabilityCalculator>: " + eName);
	}

	private boolean handleProbabilityDistribution(LinkedHashMap<String, String> attrs) {

		try {
			int distributionType = Integer.parseInt((String) attrs.get("type"));
			app.getSettings().getProbCalcSettings().setDistributionType(distributionType);
			
			boolean isCumulative = parseBoolean((String) attrs.get("isCumulative"));
			app.getSettings().getProbCalcSettings().setCumulative(isCumulative);
			
			
			// get parameters from comma delimited string
			String parmString = (String) attrs.get("parameters");
			String[] parmStringArray = parmString.split(",");
			double[] parameters = new double[parmStringArray.length];
			for(int i = 0; i < parmStringArray.length; i++)
				parameters[i] = Double.parseDouble(parmStringArray[i]);

			app.getSettings().getProbCalcSettings().setParameters(parameters);
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}


	// ====================================
	// <AlgebraView>
	// ====================================
	private void startAlgebraViewElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;

		switch (eName.charAt(0)) {
		default:
			System.err.println("unknown tag in <algebraView>: " + eName);
		}

		if (!ok)
			System.err.println("error in <algebraView>: " + eName);
	}

	// ====================================
	// <CASView>
	// ====================================
//	private void startCASViewElement(String eName, LinkedHashMap<String, String> attrs) {
//		boolean ok = true;
//
//		switch (eName.charAt(0)) {
//		case 's':
//			if (eName.equals("size")) {
//				ok = handleCASSize(app.getGuiManager().getCasView(), attrs);
//				break;
//			}
//
//		default:
//			System.err.println("unknown tag in <casView>: " + eName);
//		}
//
//		if (!ok)
//			System.err.println("error in <casView>: " + eName);
//
//	}

	private HashMap<EuclidianSettings,String>
		xmin = new HashMap<EuclidianSettings,String>(),
	    xmax = new HashMap<EuclidianSettings,String>(),
		ymin = new HashMap<EuclidianSettings,String>(),
		ymax = new HashMap<EuclidianSettings,String>();

	private boolean handleCoordSystem(EuclidianSettings ev, LinkedHashMap<String, String> attrs) {

		if (xmin.keySet().size() > 1) {
			xmin.clear();
			xmax.clear();
			ymin.clear();
			ymax.clear();
		}
		if (attrs.get("xZero") != null) {
			try {
				double xZero = Double.parseDouble((String) attrs.get("xZero"));
				double yZero = Double.parseDouble((String) attrs.get("yZero"));
				double scale = Double.parseDouble((String) attrs.get("scale"));

				// new since version 2.5
				double yscale = scale;
				String strYscale = (String) attrs.get("yscale");
				if (strYscale != null) {
					yscale = Double.parseDouble(strYscale);
				}
				ev.setCoordSystem(xZero, yZero, scale, yscale);
				
				xmin.put(ev, null);
				xmax.put(ev, null);
				ymin.put(ev, null);
				ymax.put(ev, null);
				return true;
			} catch (Exception e) {
				return false;
			}
		} else try {
			xmin.put(ev, attrs.get("xMin"));
			xmax.put(ev, attrs.get("xMax"));
			ymin.put(ev, attrs.get("yMin"));
			ymax.put(ev, attrs.get("yMax"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleEvSettings(EuclidianSettings ev, LinkedHashMap<String, String> attrs) {
		try {
			// axes attribute was removed with V3.0, see handleAxis()
			// this code is for downward compatibility
			String strAxes = (String) attrs.get("axes");
			if (strAxes != null) {
				boolean showAxes = parseBoolean(strAxes);
				//ev.showAxes(showAxes, showAxes);
				ev.setShowAxes(showAxes, true);
			}

			ev.showGrid(parseBoolean((String) attrs.get("grid")));

			try {
				ev
						.setGridIsBold(parseBoolean((String) attrs
								.get("gridIsBold"))); // Michael Borcherds
				// 2008-04-11
			} catch (Exception e) {
			}

			try {
				ev
						.setGridType(Integer.parseInt((String) attrs
								.get("gridType"))); // Michael Borcherds
				// 2008-04-30
			} catch (Exception e) {
			}

			String str = (String) attrs.get("pointCapturing");
			if (str != null) {
				// before GeoGebra 2.7 pointCapturing was either "true" or
				// "false"
				// now pointCapturing holds an int value
				int pointCapturingMode;
				if (str.equals("false"))
					pointCapturingMode = 0;
				else if (str.equals("true"))
					pointCapturingMode = 1;
				else
					// int value
					pointCapturingMode = Integer.parseInt(str);
				ev.setPointCapturing(pointCapturingMode);
			} else {
				ev.setPointCapturing(EuclidianView.POINT_CAPTURING_AUTOMATIC);
			}
			
			// if there is a point style given save it
			if(ggbFileFormat < 3.3) {
				String strPointStyle = (String) attrs.get("pointStyle");
				if (strPointStyle != null) {
					docPointStyle = Integer.parseInt(strPointStyle);
				} else {
					docPointStyle = EuclidianView.POINT_STYLE_DOT;
				}
				
				// TODO save as default construction (F.S.)
			} else {
				docPointStyle = -1;
			}

			// Michael Borcherds 2008-05-12
			// size of checkbox
			String strBooleanSize = (String) attrs.get("checkboxSize");
			if (strBooleanSize != null)
				app.booleanSize = Integer.parseInt(strBooleanSize);
				//ev.setBooleanSize(Integer.parseInt(strBooleanSize));
			
			boolean asm = parseBoolean((String) attrs.get("allowShowMouseCoords"));
			ev.setAllowShowMouseCoords(asm);

			String att = (String) attrs.get("allowToolTips");
			if (att != null)
				ev.setAllowToolTips(Integer.parseInt(att));
			else
				ev.setAllowToolTips(EuclidianView.TOOLTIPS_AUTOMATIC);

			// v3.0: appearance of right angle
			String strRightAngleStyle = (String) attrs.get("rightAngleStyle");
			if (strRightAngleStyle == null)
				// before v3.0 the default was a dot to show a right angle
				//ev.setRightAngleStyle(EuclidianView.RIGHT_ANGLE_STYLE_DOT);
				app.rightAngleStyle = EuclidianView.RIGHT_ANGLE_STYLE_DOT;
			else
				//ev.setRightAngleStyle(Integer.parseInt(strRightAngleStyle));
				app.rightAngleStyle = Integer.parseInt(strRightAngleStyle);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleEvSize(EuclidianSettings ev, LinkedHashMap<String, String> attrs) {
		// removed, needed to resize applet correctly
		//if (app.isApplet())
		//	return true;

		try {
			int width = Integer.parseInt((String) attrs.get("width"));
			int height = Integer.parseInt((String) attrs.get("height"));
			ev.setPreferredSize(new Dimension(width, height));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSpreadsheetSize(LinkedHashMap<String, String> attrs) {
		if (app.isApplet())
			return true;

		try {
			int width = Integer.parseInt((String) attrs.get("width"));
			int height = Integer.parseInt((String) attrs.get("height"));
			app.getSettings().getSpreadsheet().setPreferredSize(new Dimension(width,height));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSpreadsheetColumn(LinkedHashMap<String, String> attrs) {

		try {
			int col = Integer.parseInt((String) attrs.get("id"));
			int width = Integer.parseInt((String) attrs.get("width"));
			app.getSettings().getSpreadsheet().addWidth(col, width);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean handleSpreadsheetCellSize(LinkedHashMap<String, String> attrs) {

		try {
			int width = Integer.parseInt((String) attrs.get("width"));
			int height = Integer.parseInt((String) attrs.get("height"));
			app.getSettings().getSpreadsheet().setPreferredColumnWidth(width);
			app.getSettings().getSpreadsheet().setPreferredRowHeight(height);
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
	private boolean handleSpreadsheetFormat(LinkedHashMap<String, String> attrs) {

		try {
			String cellFormat = (String) attrs.get("formatMap");
			app.getSettings().getSpreadsheet().setCellFormat(cellFormat);	
			return true;
			
		} catch (Exception e) {
			Application.printStacktrace(e.getMessage());
			return false;
		}
	}
	
	
	
	private boolean handleSpreadsheetRow(LinkedHashMap<String, String> attrs) {

		try {
			int row = Integer.parseInt((String) attrs.get("id"));
			int height = Integer.parseInt((String) attrs.get("height"));
			app.getSettings().getSpreadsheet().addHeight(row, height);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean handleSpreadsheetLayout(LinkedHashMap<String, String> attrs) {

		SpreadsheetSettings settings = app.getSettings().getSpreadsheet();
		try {
			settings.setShowFormulaBar(parseBoolean((String) attrs.get("showFormulaBar")));	
			settings.setShowGrid(parseBoolean((String) attrs.get("showGrid")));	
			settings.setShowFileBrowser(parseBoolean((String) attrs.get("showBrowserPanel")));	
			settings.setShowColumnHeader(parseBoolean((String) attrs.get("showColumnHeader")));	
			settings.setShowRowHeader(parseBoolean((String) attrs.get("showRowHeader")));	
			settings.setShowHScrollBar(parseBoolean((String) attrs.get("showHScrollBar")));	
			settings.setShowVScrollBar(parseBoolean((String) attrs.get("showVScrollBar")));
			settings.setAllowSpecialEditor(parseBoolean((String) attrs.get("allowSpecialEditor")));
			settings.setAllowToolTips(parseBoolean((String) attrs.get("allowToolTips")));
			settings.setEqualsRequired(parseBoolean((String) attrs.get("equalsRequired")));
			return true;
			
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean handleSpreadsheetInitalSelection(LinkedHashMap<String, String> attrs) {

		SpreadsheetSettings settings = app.getSettings().getSpreadsheet();
		try {
			
			int hScroll = Integer.parseInt((String) attrs.get("hScroll"));
			int vScroll = Integer.parseInt((String) attrs.get("vScroll"));
			settings.setScrollPosition(new Point(hScroll, vScroll));
			
			
			int row = Integer.parseInt((String) attrs.get("row"));
			int column = Integer.parseInt((String) attrs.get("column"));
			settings.setScrollPosition(new Point(row, column));
			
			return true;
			
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean handleSpreadsheetBrowser(LinkedHashMap<String, String> attrs) {

		SpreadsheetSettings settings = app.getSettings().getSpreadsheet();
		try {
			if(Boolean.parseBoolean((String) attrs.get("default"))){
				settings.setDefaultBrowser(true);

			}else{
				settings.setInitialBrowserMode(Integer.parseInt((String) attrs.get("mode")));
				settings.setInitialFilePath((String) attrs.get("dir"));
				settings.setInitialURL((String) attrs.get("URL"));
			}

			return true;

		} catch (Exception e) {
			return false;
		}
	}
	
	
	

//	private boolean handleCASSize(CasManager casView, LinkedHashMap<String, String> attrs) {
//		if (app.isApplet())
//			return true;
//
//		try {
//			int width = Integer.parseInt((String) attrs.get("width"));
//			int height = Integer.parseInt((String) attrs.get("height"));
//
//			// it seems that this statement does not work, because now cas use
//			// its own frame. --Quan Yuan
//			((JComponent) app.getCasView()).setPreferredSize(new Dimension(
//					width, height));
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}

	private boolean handleBgColor(EuclidianSettings evSet, LinkedHashMap<String, String> attrs) {
		Color col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		evSet.setBackground(col);
		return true;
	}

	private boolean handleAxesColor(EuclidianSettings ev, LinkedHashMap<String, String> attrs) {
		Color col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		ev.setAxesColor(col);
		return true;
	}

	private boolean handleGridColor(EuclidianSettings ev, LinkedHashMap<String, String> attrs) {
		Color col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		ev.setGridColor(col);
		return true;
	}

	private boolean handleLineStyle(EuclidianSettings ev, LinkedHashMap<String, String> attrs) {
		try {
			ev.setAxesLineStyle(Integer.parseInt((String) attrs.get("axes")));
			ev.setGridLineStyle(Integer.parseInt((String) attrs.get("grid")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleGrid(EuclidianSettings ev, LinkedHashMap<String, String> attrs) {
		// <grid distX="2.0" distY="4.0"/>
		try {
			double[] dists = new double[3];
			dists[0] = Double.parseDouble((String) attrs.get("distX"));
			dists[1] = Double.parseDouble((String) attrs.get("distY"));
			
			// in v4.0 the polar grid adds an angle step element to gridDistances 
			String theta = (String) attrs.get("distTheta");
			if(theta !=null)
				dists[2] = Double.parseDouble((String) attrs.get("distTheta"));
			else
				dists[2] = Math.PI/6; //default
			
			ev.setGridDistances(dists);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * <axis id="0" label="x" unitLabel="x" showNumbers="true"
	 *	tickDistance="2"/>
	 * @param ev
	 * @param attrs
	 * @return true iff succesful
	 */
	protected boolean handleAxis(EuclidianSettings ev, LinkedHashMap<String, String> attrs) {
		
		try {
			int axis = Integer.parseInt((String) attrs.get("id"));
			String strShowAxis = (String) attrs.get("show");
			String label = (String) attrs.get("label");
			String unitLabel = (String) attrs.get("unitLabel");
			boolean showNumbers = parseBoolean((String) attrs.get("showNumbers"));

			// show this axis
			if (strShowAxis != null) {
				boolean showAxis = parseBoolean(strShowAxis);
				ev.setShowAxis(axis, showAxis);
			}

			// set label
			ev.setAxisLabel(axis, label);
			/*
			if (label != null && label.length() > 0) {
				String[] labels = ev.getAxesLabels();
				labels[axis] = label;
				ev.setAxesLabels(labels);
			}
			*/

			// set unitlabel
			if (unitLabel != null && unitLabel.length() > 0) {
				String[] unitLabels = ev.getAxesUnitLabels();
				unitLabels[axis] = unitLabel;
				ev.setAxesUnitLabels(unitLabels);
			}

			// set showNumbers
			ev.setShowAxisNumbers(axis, showNumbers);
			/*
			boolean showNums[] = ev.getShowAxesNumbers();
			showNums[axis] = showNumbers;
			ev.setShowAxesNumbers(showNums);
			*/

			// check if tickDistance is given
			String strTickDist = (String) attrs.get("tickDistance");
			if (strTickDist != null) {
				double tickDist = Double.parseDouble(strTickDist);
				ev.setAxesNumberingDistance(tickDist, axis);
			}

			// tick style
			String strTickStyle = (String) attrs.get("tickStyle");
			if (strTickStyle != null) {
				int tickStyle = Integer.parseInt(strTickStyle);
				//ev.getAxesTickStyles()[axis] = tickStyle;
				ev.setAxisTickStyle(axis, tickStyle);
			} else {
				// before v3.0 the default tickStyle was MAJOR_MINOR
				//ev.getAxesTickStyles()[axis] = EuclidianView.AXES_TICK_STYLE_MAJOR_MINOR;
				ev.setAxisTickStyle(axis, EuclidianView.AXES_TICK_STYLE_MAJOR_MINOR);
			}
			
			
			// axis crossing
			String axisCross = (String) attrs.get("axisCross");
			String axisCrossEdge = (String) attrs.get("axisCrossEdge");
			boolean acb = false;
			if (axisCrossEdge != null) {
				acb = parseBoolean(axisCrossEdge);
			}
			if (acb) {
				ev.setAxisCross(axis,0);
				ev.setDrawBorderAxes(axis, true);
			} else if (axisCross != null) {
				double ac = Double.parseDouble(axisCross);
				ev.setAxisCross(axis,ac);
				ev.setDrawBorderAxes(axis, false);
			} else {
				ev.setAxisCross(axis,0);
				ev.setDrawBorderAxes(axis, false);
			}

			// positive direction only
			String posAxis = (String) attrs.get("positiveAxis");
			if (posAxis != null) {
				boolean isPositive = Boolean.parseBoolean(posAxis);
				ev.setPositiveAxis(axis,isPositive);
			}
			
				
			
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}

	// ====================================
	// <kernel>
	// ====================================
	private void startKernelElement(String eName, LinkedHashMap<String, String> attrs) {
		if (eName.equals("angleUnit")) {
			handleAngleUnit(attrs);
		} else if (eName.equals("algebraStyle")) {    //G.Sturr 2009-10-18
			handleAlgebraStyle(attrs);
		} else if (eName.equals("coordStyle")) {
			handleKernelCoordStyle(attrs);
		} else if (eName.equals("angleFromInvTrig")) {
			handleKernelInvTrig(attrs);
		} else if (eName.equals("continuous")) {
			handleKernelContinuous(attrs);
		} else if (eName.equals("decimals")) {
			handleKernelDecimals(attrs);
		} else if (eName.equals("significantfigures")) {
			handleKernelFigures(attrs);
		} else if (eName.equals("startAnimation")) {
			handleKernelStartAnimation(attrs);
		} else if (eName.equals("localization")) {
			handleKernelLocalization(attrs);
		} else
			System.err.println("unknown tag in <kernel>: " + eName);
	}

	private boolean handleAngleUnit(LinkedHashMap<String, String> attrs) {
		if (attrs == null)
			return false;
		String angleUnit = (String) attrs.get("val");
		if (angleUnit == null)
			return false;

		if (angleUnit.equals("degree"))
			kernel.setAngleUnit(Kernel.ANGLE_DEGREE);
		else if (angleUnit.equals("radiant"))
			kernel.setAngleUnit(Kernel.ANGLE_RADIANT);
		else
			return false;
		return true;
	}
	
	private boolean handleAlgebraStyle(LinkedHashMap<String, String> attrs) {
		try {
			kernel.setAlgebraStyle(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKernelCoordStyle(LinkedHashMap<String, String> attrs) {
		try {
			kernel.setCoordStyle(Integer.parseInt((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKernelInvTrig(LinkedHashMap<String, String> attrs) {
		try {
			kernel.setInverseTrigReturnsAngle(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKernelDecimals(LinkedHashMap<String, String> attrs) {
		try {
			kernel
					.setPrintDecimals(Integer.parseInt((String) attrs
							.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKernelStartAnimation(LinkedHashMap<String, String> attrs) {
		try {
			startAnimation = parseBoolean((String) attrs.get("val"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKernelLocalization(LinkedHashMap<String, String> attrs) {
		try {
			boolean digits = parseBoolean((String) attrs.get("digits"));
			app.setUseLocalizedDigits(digits);
			boolean labels = parseBoolean((String) attrs.get("labels"));
			app.setUseLocalizedLabels(labels);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKernelFigures(LinkedHashMap<String, String> attrs) {
		try {
			kernel.setPrintFigures(Integer.parseInt((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKernelContinuous(LinkedHashMap<String, String> attrs) {
		try {
			kernel.setContinuous(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// ====================================
	// <gui>
	// ====================================
	private void startGuiElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;
		switch (eName.charAt(0)) {
		case 'c':
			if (eName.equals("consProtColumns"))
				ok = handleConsProtColumns(app, attrs);
			else if (eName.equals("consProtocol"))
				ok = handleConsProtocol(app, attrs);
			else if (eName.equals("consProtNavigationBar"))
				ok = handleConsProtNavigationBar(app, attrs);
			break;

		case 'f':
			if (eName.equals("font"))
				ok = handleFont(app, attrs);
			break;
			
		case 'g':
			if (eName.equals("graphicsSettings"))
				ok = handleGraphicsSettings(attrs);
			break;

		case 'm':
			if (eName.equals("menuFont"))
				ok = handleMenuFont(app, attrs);
			else if (eName.equals("mouse"))
				ok = handleMouse(app, attrs);
			break;

		case 'l':
			if (eName.equals("labelingStyle"))
				ok = handleLabelingStyle(app, attrs);
			break;
			
		case 'p':
			if(eName.equals("perspectives")) {
				mode = MODE_GUI_PERSPECTIVES;
				tmp_perspectives.clear();
			}
			break;
			
		case 's':
			if (eName.equals("show"))
				ok = handleGuiShow(app, attrs);
			else if (eName.equals("splitDivider"))
				ok = handleSplitDivider(app, attrs);
			else if (eName.equals("settings"))
				ok = handleGuiSettings(app, attrs);
			break;
			
		case 't':
			if (eName.equals("toolbar"))
				ok = handleToolbar(app, attrs);
			else if (eName.equals("tooltipSettings"))
				ok = handleTooltipSettings(app, attrs);
			break;
			
		case 'w':
			if(eName.equals("window"))
				ok = handleWindowSize(app, attrs);
			break;

		default:
			System.err.println("unknown tag in <gui>: " + eName);
		}

		if (!ok)
			System.err.println("error in <gui>: " + eName);
	}
	
	/**
	 * Take care of backward compatibility for the dynamic layout component
	 */
	private void createCompabilityLayout() {
		int splitOrientation = tmp_spHorizontal ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT;
		
		String defEV, defSV, defAV;
		
		// we have to create the definitions for the single views manually to prevent nullpointers
		if(splitOrientation == JSplitPane.HORIZONTAL_SPLIT) {
			if(tmp_showSpreadsheet && tmp_showAlgebra) {
				defEV = "1,3";
				defSV = "1,1";
				defAV = "3";
			} else {
				if(tmp_showSpreadsheet) {
					defEV = "3";
					defSV = "1";
					defAV = "3,3"; // not used directly
				} else {
					defEV = "1";
					defAV = "3";
					defSV = "1,1"; // not used directly
				}
			}
		} else {
			if(tmp_showSpreadsheet && tmp_showAlgebra) {
				defEV = "0";
				defAV = "2,0";
				defSV = "2,2";
			} else {
				if(tmp_showSpreadsheet) {
					defEV = "0";
					defSV = "2";
					defAV = "0,0"; // not used directly
				} else {
					defEV = "2";
					defAV = "0";
					defSV = "2,2"; // not used directly
				}
			}
		}
		
		// construct default xml data in case we're using an old version which didn't
		// store the layout xml.
		DockPanelXml[] dpXml = new DockPanelXml[] {
			new DockPanelXml(Application.VIEW_EUCLIDIAN, null, true, false, false, new Rectangle(400, 400), defEV, 200),
			new DockPanelXml(Application.VIEW_ALGEBRA, null, tmp_showAlgebra, false, false, new Rectangle(200, 400), defAV, 200),
			new DockPanelXml(Application.VIEW_SPREADSHEET, null, tmp_showSpreadsheet, false, false, new Rectangle(400, 400), defSV, 200)
		};
		tmp_perspective.setDockPanelInfo(dpXml);
		tmp_perspective.setShowToolBar(true);
		
		Dimension evSize = app.getSettings().getEuclidian(1).getPreferredSize();
		
		// calculate window dimensions
		int width = evSize.width;
		int height = evSize.height;
		
		// minimal size for documents, necessary for GeoGebra < 3
		if(width <= 100 || height <= 100) {
			width = 600;
			height = 440;
		}
	
		if(splitOrientation == JSplitPane.HORIZONTAL_SPLIT) {
			if(tmp_showSpreadsheet) {
				width += 5 + app.getSettings().getSpreadsheet().preferredSize().width;
			} 
			
			if(tmp_showAlgebra) {
				width += 5 + tmp_sp2;
			}
		} else {
			if(tmp_showSpreadsheet) {
				height += 5 + app.getSettings().getSpreadsheet().preferredSize().height;
			} 
			if(tmp_showAlgebra) {
				height += 5 + tmp_sp2;
			}
		}
		
		DockSplitPaneXml[] spXml;
		
		// use two split panes in case all three views are visible
		if(tmp_showSpreadsheet && tmp_showAlgebra) {
			int total = (splitOrientation == JSplitPane.HORIZONTAL_SPLIT ? width : height);
			float relative1 = (float)tmp_sp2 / total;
			float relative2 = (float)tmp_sp1 / (total - tmp_sp2);
			spXml = new DockSplitPaneXml[] {
				new DockSplitPaneXml("", relative1, splitOrientation),
				new DockSplitPaneXml((splitOrientation == JSplitPane.HORIZONTAL_SPLIT ? "1" : "2"), relative2, splitOrientation)
			}; 
		} else {
			int total = (splitOrientation == JSplitPane.HORIZONTAL_SPLIT ? width : height);
			float relative;
			if(tmp_showSpreadsheet) {
				relative = (float)tmp_sp1 / total;
			} else {
				relative = (float)tmp_sp2 / total;
			}
			spXml = new DockSplitPaneXml[] {
				new DockSplitPaneXml("", relative, splitOrientation)
			};
		}

		// additional space for toolbar and others, we add this here
		// as it shouldn't influence the relative positions of the 
		// split pane dividers above
		width += 15;
		height += 90;
		
		if(tmp_perspective.getShowInputPanel()) {
			height += 50;
		}
		
		tmp_perspective.setSplitPaneInfo(spXml);
		
		tmp_perspectives = new ArrayList<Perspective>();
		tmp_perspectives.add(tmp_perspective);
		app.setPreferredSize(new Dimension(width, height));
		app.setTmpPerspectives(tmp_perspectives);
	}

	private boolean handleConsProtColumns(Application app, LinkedHashMap<String, String> attrs) {
		try {
			// TODO: set visible state of columns in consProt
			/*
			 * Iterator it = attrs.keySet().iterator(); while (it.hasNext()) {
			 * Object ob = attrs.get(it.next());
			 * 
			 * boolean isVisible = parseBoolean((String) ob); }
			 */

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleConsProtocol(Application app, LinkedHashMap<String, String> attrs) {
		try {
			// boolean useColors = parseBoolean((String)
			// attrs.get("useColors"));
			// TODO: set useColors for consProt

			boolean showOnlyBreakpoints = parseBoolean((String) attrs
					.get("showOnlyBreakpoints"));
			kernel.setShowOnlyBreakpoints(showOnlyBreakpoints);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleConsProtNavigationBar(Application app,
			LinkedHashMap<String, String> attrs) {
		try {
			boolean show = parseBoolean((String) attrs.get("show"));
			boolean playButton = parseBoolean((String) attrs.get("playButton"));
			double playDelay = Double.parseDouble((String) attrs
					.get("playDelay"));
			boolean showProtButton = parseBoolean((String) attrs
					.get("protButton"));
			
			//Maybe there is not guiManager yet. In this case we store the
			//navigation bar's states in ConstructionProtocolSettings
			
			if(app.getGuiManager()!=null){
				app.setShowConstructionProtocolNavigation(show);			
				
				if (show) {
					app.getGuiManager().setShowConstructionProtocolNavigation(show,
						playButton, playDelay, showProtButton);
				}
			} else {
				ConstructionProtocolSettings cpSettings = app.getSettings().getConstructionProtocol(); 
				cpSettings.setShowPlayButton(playButton);
				cpSettings.setPlayDelay(playDelay);
				cpSettings.setShowConstructionProtocol(showProtButton);
				app.setShowConstructionProtocolNavigation(show);
				
				
			}
			
			
			// construction step: handled at end of parsing
			String strConsStep = (String) attrs.get("consStep");
			if (strConsStep != null)
				consStep = Integer.parseInt(strConsStep);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Backward compatibility for version < 3.3
	 * 
	 * @param app
	 * @param attrs
	 * @return
	 */
	private boolean handleGuiShow(Application app, LinkedHashMap<String, String> attrs) {
		try {
			// backward compatibility to versions without the layout component
			if(ggbFileFormat < 3.3) {
				tmp_showAlgebra = parseBoolean((String) attrs
						.get("algebraView"));

				// Michael Borcherds 2008-04-25
				tmp_showSpreadsheet = parseBoolean((String) attrs
						.get("spreadsheetView"));
			}

			String str = (String) attrs.get("auxiliaryObjects");
			boolean auxiliaryObjects = (str != null && str.equals("true"));
			app.setShowAuxiliaryObjects(auxiliaryObjects);

			str = (String) attrs.get("algebraInput");
			boolean algebraInput = (str == null || str.equals("true"));
			tmp_perspective.setShowInputPanel(algebraInput);

			str = (String) attrs.get("cmdList");
			boolean cmdList = (str == null || str.equals("true"));
			tmp_perspective.setShowInputPanelCommands(cmdList);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}
	
	/**
	 * Settings of the user, not saved in the file XML but for preferences XML.
	 * 
	 * <settings ignoreDocument=".." showTitleBar=".." />
	 * 
	 * @param app
	 * @param attrs
	 * @return
	 */
	private boolean handleGuiSettings(Application app, LinkedHashMap<String, String> attrs) {
		try {
			boolean ignoreDocument = !((String)attrs.get("ignoreDocument")).equals("false");
			app.getSettings().getLayout().setIgnoreDocumentLayout(ignoreDocument);
			
			boolean showTitleBar = !((String)attrs.get("showTitleBar")).equals("false");
			app.getSettings().getLayout().setShowTitleBar(showTitleBar);
			
			if(attrs.containsKey("allowStyleBar")) {
				boolean allowStyleBar = !((String)attrs.get("allowStyleBar")).equals("false"); 
				app.getSettings().getLayout().setAllowStyleBar(allowStyleBar);
			} 
			
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			Application.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}
	
	private boolean handleGraphicsSettings(LinkedHashMap<String, String> attrs) {
		try {
			if ("true".equals((String) attrs.get("javaLatexFonts")))
				app.getDrawEquation().setUseJavaFontsForLaTeX(app, true);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			Application.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	/**
	 * Kept for backward compatibility with version < 3.3
	 * 
	 * @param app
	 * @param attrs
	 * @return
	 */
	private boolean handleSplitDivider(Application app, LinkedHashMap<String, String> attrs) {
		try {
			tmp_spHorizontal = !"false".equals((String) attrs.get("horizontal"));
			
			// There were just two panels in GeoGebra < 3.2, therefore just one split divider position
			// may be given. 'loc' in < 3.2 corresponds to 'loc2' in 3.2+.
			if(attrs.get("loc2") == null) {
				attrs.put("loc2", attrs.get("loc"));
				attrs.put("loc", "0"); // prevent NP exception in Integer.parseInt()
			}
			
			if(tmp_spHorizontal) {
				tmp_sp1 = Integer.parseInt((String) attrs.get("loc"));
				tmp_sp2 = Integer.parseInt((String) attrs.get("loc2"));
			} else {
				String strLocVert = (String) attrs.get("locVertical");
				if (strLocVert != null) {
					tmp_sp1 = Integer.parseInt(strLocVert);
				} else {
					tmp_sp1 = Integer.parseInt((String) attrs.get("loc"));
				}
				
				String strLocVert2 = (String) attrs.get("locVertical2");
				if (strLocVert2 != null) {
					tmp_sp2 = Integer.parseInt(strLocVert2);
				} else {
					tmp_sp2 = Integer.parseInt((String) attrs.get("loc2"));
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleToolbar(Application app, LinkedHashMap<String, String> attrs) {
		try {
			String showToolBar = (String)attrs.get("show");
			
			if(showToolBar == null) {
				tmp_perspective.setShowToolBar(true);
			} else {
				tmp_perspective.setShowToolBar(showToolBar.equals("true"));
			}
			
			tmp_perspective.setToolbarDefinition((String) attrs.get("items"));
			return true;
		} catch (Exception e) {
			Application.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}
	
	/**
	 * Handle the window size:
	 * <window width=".." height=".." />
	 * 
	 * @param app
	 * @param attrs
	 * @return
	 */
	private boolean handleWindowSize(Application app, LinkedHashMap<String, String> attrs) {
		try {
			Dimension size = new Dimension(
				Integer.parseInt((String)attrs.get("width")),
				Integer.parseInt((String)attrs.get("height"))
			);
			app.setPreferredSize(size);
			return true;
		} catch (Exception e) {
			Application.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	private boolean handleFont(Application app, LinkedHashMap<String, String> attrs) {
		try {			
			int guiSize = Integer.parseInt((String) attrs.get("size"));			

			// old versions do just have a single font size and derive the font size for
			// the axes / euclidian view from this single size
//			if(ggbFileFormat < 3.3) {
//				app.setFontSize(guiSize, false);
//				app.setAxesFontSize(guiSize - 2, false); // always 2 points smaller than the default size
//			} else {
//				int axesSize = Integer.parseInt((String) attrs.get("axesSize"));
//				app.setAxesFontSize(axesSize, false);
//				
//				int euclidianSize = Integer.parseInt((String) attrs.get("euclidianSize"));
//				app.setEuclidianFontSize(euclidianSize, false);
//			}

			app.setFontSize(guiSize); // set gui font size and update all fonts
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean handleMenuFont(Application app, LinkedHashMap<String, String> attrs) {
		try {			
			int guiSize = Integer.parseInt((String) attrs.get("size"));
			if (guiSize <= 0) {
				app.setGUIFontSize(-1); // default
			} else {
				for (int i = 0; i < menuFontSizes.length; i++) {
					if (menuFontSizes[i] >= guiSize) {
						guiSize = menuFontSizes[i];
						break;
					}
				}
				if (guiSize > menuFontSizes[menuFontSizes.length-1])
					guiSize = menuFontSizes[menuFontSizes.length-1];
				app.setGUIFontSize(guiSize);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleTooltipSettings(Application app, LinkedHashMap<String, String> attrs) {
		try {
			String ttl = (String) attrs.get("language");
			if (ttl != null) {
				boolean found = false;
				for (int i = 0; i < Application.supportedLocales.size(); i++) {
					if (Application.supportedLocales.get(i).toString().equals(ttl)) {
						app.setTooltipLanguage(Application.supportedLocales.get(i));
						found = true;
						break;
					}
				}
				if (!found) {
					app.setTooltipLanguage(null);
				}
			}
			int ttt = -1;
			try { // "off" will be -1
				ttt = Integer.parseInt(attrs.get("timeout"));
			} catch (NumberFormatException e) {
			}
			if (ttt > 0)
			{
				ToolTipManager.sharedInstance().setDismissDelay(ttt * 1000);
				// make it fit into tooltipTimeouts array:
				ToolTipManager.sharedInstance().setDismissDelay(app.getTooltipTimeout() * 1000);
			}
			else
			{
				ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleMouse(Application app, LinkedHashMap<String, String> attrs) {
		try {			
			app.reverseMouseWheel(!((String)attrs.get("reverseWheel")).equals("false"));

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleLabelingStyle(Application app, LinkedHashMap<String, String> attrs) {
		try {
			int style = Integer.parseInt((String) attrs.get("val"));
			app.setLabelingStyle(style);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// ====================================
	// <perspectives>
	// ====================================
	private void startGuiPerspectivesElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;
		
		if(eName.equals("perspective"))
			ok = handlePerspective(attrs);
		else 
			Application.debug("unknown tag in <perspectives>: " + eName);

		if (!ok)
			Application.debug("error in <perspectives>: " + eName);
	}
	
	/**
	 * Create a new temporary perspective for the current <perspective> element
	 * 
	 * @param attrs
	 * @return
	 */
	private boolean handlePerspective(LinkedHashMap<String, String> attrs) {
		try {
			tmp_perspective = new Perspective((String)attrs.get("id"));
			tmp_perspectives.add(tmp_perspective);
			
			if(tmp_panes == null) {
				tmp_panes = new ArrayList<DockSplitPaneXml>();
			} else {
				tmp_panes.clear();
			}
			
			if(tmp_views == null) {
				tmp_views = new ArrayList<DockPanelXml>();
			} else {
				tmp_views.clear();
			}
			mode = MODE_GUI_PERSPECTIVE;
			
			return true;
		} catch(Exception e) {
			Application.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}
	
	/**
	 * Save all perspectives in the application.
	 */
	private void endGuiPerspectivesElement() {
		app.setTmpPerspectives(tmp_perspectives);
	}

	// ====================================
	// <perspective>
	// ====================================
	private void startGuiPerspectiveElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;
		
		switch (eName.charAt(0)) {
		case 'i':
			if(eName.equals("input")) {
				ok = handleAlgebraInput(attrs);
				break;
			}
			
		case 'p':
			if(eName.equals("panes")) {
				mode = MODE_GUI_PERSPECTIVE_PANES;
				break;
			}
			
		case 's':
			if(eName.equals("show")) {
				ok = handleGuiShow(app, attrs);
				break;
			}
			
		case 't':
			if(eName.equals("toolbar")) {
				ok = handleToolbar(app, attrs);
				break;
			}
			
		case 'v':
			if(eName.equals("views")) {
				mode = MODE_GUI_PERSPECTIVE_VIEWS;
				break;
			}

		default:
			Application.debug("unknown tag in <perspective>: " + eName);
		}

		if (!ok)
			Application.debug("error in <perspective>: " + eName);
	}
	
	private boolean handleAlgebraInput(LinkedHashMap<String, String> attrs) {
		try {
			tmp_perspective.setShowInputPanel(!((String)attrs.get("show")).equals("false"));
			tmp_perspective.setShowInputPanelCommands(!((String)attrs.get("cmd")).equals("false"));
			tmp_perspective.setShowInputPanelOnTop(!((String)attrs.get("top")).equals("false"));
			
			return true;
		} catch(Exception e) {
			Application.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}
	
	private void endGuiPerspectiveElement() {
		DockPanelXml[] dpInfo = new DockPanelXml[tmp_views.size()];
		DockSplitPaneXml[] spInfo = new DockSplitPaneXml[tmp_panes.size()];
		tmp_perspective.setDockPanelInfo((DockPanelXml[])tmp_views.toArray(dpInfo));
		tmp_perspective.setSplitPaneInfo((DockSplitPaneXml[])tmp_panes.toArray(spInfo));
	}

	// ====================================
	// <views>
	// ====================================
	private void startGuiViewsElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;
		
		if(eName.equals("view"))
			ok = handleView(attrs);
		else 
			Application.debug("unknown tag in <views>: " + eName);

		if (!ok)
			Application.debug("error in <views>: " + eName);
	}
	
	/**
	 * Handle a view.
	 * <view id=".." visible=".." inframe=".." stylebar=".." window=".." location=".." size=".." />
	 * 
	 * @param attrs
	 * @return
	 */
	private boolean handleView(LinkedHashMap<String, String> attrs) {
		try {
			int viewId = Integer.parseInt((String)attrs.get("id"));
			String toolbar = attrs.get("toolbar");
			boolean isVisible = !((String)attrs.get("visible")).equals("false");
			boolean openInFrame = !((String)attrs.get("inframe")).equals("false");
			
			String showStyleBarStr = (String)attrs.get("stylebar");
			boolean showStyleBar = (showStyleBarStr != null ? !showStyleBarStr.equals("false") : false);
			
			// the window rectangle is given in the format "x,y,width,height"
			String[] window = ((String)attrs.get("window")).split(",");
			Rectangle windowRect = new Rectangle(
				Integer.parseInt(window[0]),
				Integer.parseInt(window[1]),
				Integer.parseInt(window[2]),
				Integer.parseInt(window[3])
			);
			
			String embeddedDef = (String)attrs.get("location");
			int embeddedSize = Integer.parseInt((String)attrs.get("size"));
			
			tmp_views.add(new DockPanelXml(viewId, toolbar, isVisible, openInFrame, showStyleBar, windowRect, embeddedDef, embeddedSize));
			
			return true;
		} catch(Exception e) {
			Application.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	// ====================================
	// <panes>
	// ====================================
	private void startGuiPanesElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;
		
		if(eName.equals("pane"))
			ok = handlePane(attrs);
		else 
			Application.debug("unknown tag in <panes>: " + eName);

		if (!ok)
			Application.debug("error in <panes>: " + eName);
	}
	
	/**
	 * Handle a pane.
	 * <pane location".." divider=".." orientation=".." />
	 * 
	 * @param attrs
	 * @return
	 */
	private boolean handlePane(LinkedHashMap<String, String> attrs) {
		try {
			String location = (String)attrs.get("location");
			double dividerLocation = Double.parseDouble((String)attrs.get("divider"));
			int orientation = Integer.parseInt((String)attrs.get("orientation"));
			
			tmp_panes.add(new DockSplitPaneXml(location, dividerLocation, orientation));
			
			return true;
		} catch(Exception e) {
			Application.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	// ====================================
	// <construction>
	// ====================================
	private void handleConstruction(LinkedHashMap<String, String> attrs) {
		try {
			String title = (String) attrs.get("title");
			String author = (String) attrs.get("author");
			String date = (String) attrs.get("date");
			if (title != null)
				cons.setTitle(title);
			if (author != null)
				cons.setAuthor(author);
			if (date != null)
				cons.setDate(date);
		} catch (Exception e) {
			System.err.println("error in <construction>");
		}
	}

	private void initMacro(LinkedHashMap<String, String> attrs) {
		try {
			String cmdName = (String) attrs.get("cmdName");
			String toolName = (String) attrs.get("toolName");
			String toolHelp = (String) attrs.get("toolHelp");
			String iconFile = (String) attrs.get("iconFile");
			boolean copyCaptions = parseBoolean((String) attrs.get("copyCaptions"));
			String strShowInToolBar = (String) attrs.get("showInToolBar");

			// Make sure we don't have a macro with the same name in kernel.
			// This can happen when a macro file (ggt) is loaded because
			// the previous macros are not cleared in this case.
			int n = 0;
			String myCmdName = cmdName;
			while (kernel.getMacro(myCmdName) != null) {
				n++;
				myCmdName = cmdName + n;
			}

			// create macro and a kernel for it
			macro = new Macro(kernel, myCmdName);
			macro.setToolName(toolName);
			macro.setCopyCaptionsAndVisibility(copyCaptions);
			macro.setToolHelp(toolHelp);
			macro.setIconFileName(iconFile);
			boolean showTool = strShowInToolBar == null ? true
					: parseBoolean(strShowInToolBar);
			macro.setShowInToolBar(showTool);

			MacroKernel macroKernel = new MacroKernel(kernel);
			macroKernel.setContinuous(false);

			// we have to change the construction object temporarily so
			// everything
			// is done in the macro construction from now on
			kernel = macroKernel;
			cons = macroKernel.getConstruction();
			parser = new Parser(macroKernel, cons);

		} catch (Exception e) {
			System.err.println("error in <macro>");
		}
	}

	private void endMacro() {
		// cons now holds a reference to the macroConstruction
		macro.initMacro(cons, macroInputLabels, macroOutputLabels);
		// ad the newly built macro to the kernel
		origKernel.addMacro(macro);

		// set kernel and construction back to the original values
		initKernelVars();
	}

	/*
	 * <worksheetText above="blabla" below="morebla" />
	 */
	private void handleWorksheetText(LinkedHashMap<String, String> attrs) {
		String above = (String) attrs.get("above");
		String below = (String) attrs.get("below");
		cons.setWorksheetText(above, 0);
		cons.setWorksheetText(below, 1);
	}

	// ====================================
	// <cascell>
	// ====================================
	private void startCasCell(String eName, LinkedHashMap<String, String> attrs) {
		// handle cas session mode
		switch (casMode) {
		case MODE_CONST_CAS_CELL:
			if (eName.equals("cellPair")) {
				casMode = MODE_CAS_CELL_PAIR;
				startCellPair();
			} else {
				System.err.println("unknown tag in <cellPair>: " + eName);
			}
			break;

		case MODE_CAS_CELL_PAIR:
			if (eName.equals("inputCell")) {
				casMode = MODE_CAS_INPUT_CELL;
			} else if (eName.equals("outputCell")) {
				casMode = MODE_CAS_OUTPUT_CELL;
			} else {
				System.err.println("unknown tag in <cellPair>: " + eName);
			}
			break;

		case MODE_CAS_INPUT_CELL:
			startCellInputElement(eName, attrs);
			break;

		case MODE_CAS_OUTPUT_CELL:
			startCellOutputElement(eName, attrs);
			break;

		default:
			System.err.println("unknown cas session mode:" + constMode);
		}
	}
	
	private void endCasCell(String eName) {
		switch (casMode) {
		case MODE_CONST_CAS_CELL:
			if (eName.equals("cascell")) {
				mode = MODE_CONSTRUCTION;
				constMode = MODE_CONSTRUCTION;
				casMode = MODE_CONST_CAS_CELL;
				geoCasCell = null;
			}
			break;

		case MODE_CAS_CELL_PAIR:
			if (eName.equals("cellPair")) {
				casMode = MODE_CONST_CAS_CELL;
				endCellPair(eName);
			}
			break;

		case MODE_CAS_INPUT_CELL:
			if (eName.equals("inputCell"))
				casMode = MODE_CAS_CELL_PAIR;
			break;

		case MODE_CAS_OUTPUT_CELL:
			if (eName.equals("outputCell"))
				casMode = MODE_CAS_CELL_PAIR;
			break;

		default:
			casMode = MODE_CONST_CAS_CELL; // set back mode
			System.err.println("unknown cas session mode:" + constMode);
		}

	}
	
	private void startCellPair() {
		geoCasCell = new GeoCasCell(cons);
	}
	
	private void endCellPair(String eName) {
		if (geoCasCell == null) {
			System.err.println("no element set for <" + eName + ">");
			return;
		}
		
		try {
			// create necessary algorithm and twinGeo	
			boolean independentCell = geoCasCell.getGeoElementVariables() == null;	
			if (independentCell) {
				// free cell, e.g. m := 7  creates twinGeo m = 7
				cons.addToConstructionList(geoCasCell, true);	
				if (geoCasCell.isAssignment()) {
					// make sure assignment is sent to underlying CAS, e.g. f(x) := x^2
					// and twinGeo is created
					geoCasCell.computeOutput();
					geoCasCell.setLabelOfTwinGeo();
				}
				// otherwise keep loaded output and avoid unnecessary computation
			} else {
				// create algorithm for dependent cell
				// this also creates twinGeo if necessary
				// output is not computed again, see AlgoDependenCasCell constructor
				kernel.DependentCasCell(geoCasCell);
			}
		} catch (Exception e) {
			System.err.println("error when processing <cellpair>: " + e.getMessage());
			e.printStackTrace();
		}
	}
	

	private void startCellOutputElement(String eName, LinkedHashMap<String, String> attrs) {
		if (geoCasCell == null) {
			System.err.println("no element set for <" + eName + ">");
			return;
		}

		boolean ok = true;
		switch (eName.charAt(0)) {
		case 'e':
			if (eName.equals("expression")) {
				ok = handleCasCellOutput(attrs);
				break;
			}

		default:
			System.err.println("unknown tag in <outputCell>: " + eName);
		}

		if (!ok)
			System.err.println("error in <outputCell>: " + eName);

	}

	private void startCellInputElement(String eName, LinkedHashMap<String, String> attrs) {
		if (geoCasCell == null) {
			System.err.println("no element set for <" + eName + ">");
			return;
		}

		boolean ok = true;
		switch (eName.charAt(0)) {
		case 'e':
			if (eName.equals("expression")) {
				ok = handleCasCellInput(attrs);
				break;
			}

//		case 'c':
//			if (eName.equals("color")) {
//				ok = handleCASPairColor(attrs);
//				break;
//			}

		default:
			System.err.println("unknown tag in <inputCell>: " + eName);
		}

		if (!ok)
			System.err.println("error in <inputCell>: " + eName);
	}

	private void startConstructionElement(String eName, LinkedHashMap<String, String> attrs) {
		// handle construction mode
		
		//Application.debug("constMode = "+constMode+", eName = "+eName);
		
		switch (constMode) {
		case MODE_CONSTRUCTION:
			if (eName.equals("element")) {
				constMode = MODE_CONST_GEO_ELEMENT;
				geo = getGeoElement(attrs);
			} else if (eName.equals("command")) {
				constMode = MODE_CONST_COMMAND;
				cmd = getCommand(attrs);
			} else if (eName.equals("expression")) {
				startExpressionElement(eName, attrs);
			} else if (eName.equals("cascell")) {
				constMode = MODE_CONST_CAS_CELL;
				casMode = MODE_CONST_CAS_CELL;
			} else if (eName.equals("worksheetText")) {
				handleWorksheetText(attrs);
			}
			else {
				System.err.println("unknown tag in <construction>: " + eName);
			}
			break;

		case MODE_CONST_GEO_ELEMENT:
			startGeoElement(eName, attrs);
			break;

		case MODE_CONST_COMMAND:
			startCommandElement(eName, attrs);
			break;
			
		case MODE_CONST_CAS_CELL:
			startCasCell(eName, attrs);
			break;

		default:
			System.err.println("unknown construction mode:" + constMode);
		}
	}

	private void endConstructionElement(String eName) {
		switch (constMode) {
		case MODE_CONSTRUCTION:
			if (eName.equals("construction")) {
				// process start points at end of construction
				processStartPointList();
				processLinkedGeoList();
				processShowObjectConditionList();
				processDynamicColorList();
				processAnimationSpeedList();
				processAnimationStepList();
				processMinMaxList();
				processEvSizes();
				processAnimatingList(); // must be after min/maxList otherwise GeoElement.setAnimating doesn't work

				if (kernel == origKernel) {
					mode = MODE_GEOGEBRA;
				} else {
					// macro construction
					mode = MODE_MACRO;
				}
			}
			break;

		case MODE_CONST_GEO_ELEMENT:
			if (eName.equals("element"))
				constMode = MODE_CONSTRUCTION;
			break;						

		case MODE_CONST_COMMAND:
			if (eName.equals("command"))
				constMode = MODE_CONSTRUCTION;
			break;
			
		case MODE_CONST_CAS_CELL:
			endCasCell(eName);			
			break;

		default:
			constMode = MODE_CONSTRUCTION; // set back mode
			System.err.println("unknown construction mode:" + constMode);
		}
	}

	// ====================================
	// <element>
	// ====================================

	private void processEvSizes() {
		for(EuclidianSettings ev:xmin.keySet()){			
			if (xmin.get(ev) == null) {
				ev.setXminObject(null, true);
			} else {
				NumberValue n = kernel.getAlgebraProcessor().evaluateToNumeric(xmin.get(ev),true);			
				ev.setXminObject(n, true);
			}
		}
		for(EuclidianSettings ev:xmax.keySet()){
			if (xmax.get(ev) == null) {
				ev.setXmaxObject(null, true);
			} else {
				NumberValue n = kernel.getAlgebraProcessor().evaluateToNumeric(xmax.get(ev),true);			
				ev.setXmaxObject(n, true);
			}
		}
		for(EuclidianSettings ev:ymin.keySet()){
			if (ymin.get(ev) == null) {
				ev.setYminObject(null, true);
			} else {
				NumberValue n = kernel.getAlgebraProcessor().evaluateToNumeric(ymin.get(ev),true);
				ev.setYminObject(n, true);
			}
		}
		for(EuclidianSettings ev:ymax.keySet()){
			if (ymax.get(ev) == null) {
				ev.setYmaxObject(null, true);
			} else {
				NumberValue n = kernel.getAlgebraProcessor().evaluateToNumeric(ymax.get(ev),true);
				ev.setYmaxObject(n, true);
			}
			//ev.updateBounds();
		}
		
		
	}

	// called when <element> is encountered
	// e.g. for <element type="point" label="P">
	private GeoElement getGeoElement(LinkedHashMap<String, String> attrs) {
		GeoElement geo = null;
		String label = (String) attrs.get("label");
		String type = (String) attrs.get("type");
		String defaultset = (String) attrs.get("default");
		if (label == null || type == null) {
			System.err.println("attributes missing in <element>");
			return geo;
		}

		if (defaultset == null || !kernel.getElementDefaultAllowed()) {
			// does a geo element with this label exist?
			geo = kernel.lookupLabel(label);
			//Application.debug(label+", geo="+geo);
			if (geo == null) {
		
				// try to find an algo on which this label depends
				//geo = cons.resolveLabelDependency(label, kernel.getClassType(type));
				//if none, create new geo
				if (geo==null){
					geo = kernel.createGeoElement(cons, type);
					geo.setLoadedLabel(label);
				}
			
				//Application.debug(label+", "+geo.isLabelSet());

				// independent GeoElements should be hidden by default
				// (as older versions of this file format did not
				// store show/hide information for all kinds of objects,
				// e.g. GeoNumeric)
				geo.setEuclidianVisible(false);
			}
		} else {
			int defset = Integer.parseInt(defaultset);
			geo = kernel.getConstruction().getConstructionDefaults().getDefaultGeo(defset);
			if (geo == null) {
				// wrong default setting, act as if there were no default set
				geo = kernel.lookupLabel(label);
				if (geo == null) {
					geo = kernel.createGeoElement(cons, type);
					geo.setLoadedLabel(label);
					geo.setEuclidianVisible(false);
				}
			}
		}
		
		// use default point style on points
		if(geo.getGeoClassType() == GeoElement.GEO_CLASS_POINT && ggbFileFormat < 3.3) {
			((PointProperties)geo).setPointStyle(docPointStyle);
		}

		// for downward compatibility
		if (geo.isLimitedPath()) {
			LimitedPath lp = (LimitedPath) geo;
			// old default value for intersections of segments, ...
			// V2.5: default of "allow outlying intersections" is now false
			lp.setAllowOutlyingIntersections(true);

			// old default value for geometric transforms of segments, ...
			// V2.6: default of "keep type on geometric transform" is now true
			lp.setKeepTypeOnGeometricTransform(false);
		}

		return geo;
	}

	protected void startGeoElement(String eName, LinkedHashMap<String, String> attrs) {
		if (geo == null) {
			System.err.println("no element set for <" + eName + ">");
			return;
		}

		boolean ok = true;
		switch (eName.charAt(0)) {
		case 'a':
			if (eName.equals("auxiliary")) {
				ok = handleAuxiliary(attrs);
				break;
			} else if (eName.equals("animation")) {
				ok = handleAnimation(attrs);
				break;
			} else if (eName.equals("arcSize")) {
				ok = handleArcSize(attrs);
				break;
			} else if (eName.equals("allowReflexAngle")) {
				ok = handleAllowReflexAngle(attrs);
				break; 
			} else if (eName.equals("absoluteScreenLocation")) {
				ok = handleAbsoluteScreenLocation(attrs);
				break;
			}

		case 'b':
			if (eName.equals("breakpoint")) {
				ok = handleBreakpoint(attrs);
				break;
			} else if (eName.equals("bgColor")) {
				ok = handleBgColor(attrs);
				break;
			}

		case 'c':
			if (eName.equals("coords")) {
				ok = handleCoords(attrs);
				break;
			} else if (eName.equals("coordStyle")) {
				ok = handleCoordStyle(attrs);
				break;
			} else if (eName.equals("caption")) {
				ok = handleCaption(attrs);
				break;
			} else if (eName.equals("condition")) {
				ok = handleCondition(attrs);
				break;
			} else if (eName.equals("checkbox")) {
				ok = handleCheckbox(attrs);
				break;
			} else if (eName.equals("coefficients")){
				ok = handleCoefficients(attrs);
				break;
			}

		case 'd':
			if (eName.equals("decoration")) {
				ok = handleDecoration(attrs);
				break;
			} else if (eName.equals("decimals")) {
				ok = handleTextDecimals(attrs);
				break;
			}

		case 'e':
			if (eName.equals("eqnStyle")) {
				ok = handleEqnStyle(attrs);
				break;
			} else if (eName.equals("eigenvectors")) {
				ok = handleEigenvectors(attrs);
				break;
			} else if (eName.equals("emphasizeRightAngle")) {
				ok = handleEmphasizeRightAngle(attrs);
				break; 
			} 

		case 'f':
			if (eName.equals("fixed")) {
				ok = handleFixed(attrs);
				break;
			} else if (eName.equals("file")) {
				ok = handleFile(attrs);
				break;
			} else if (eName.equals("font")) {
				ok = handleTextFont(attrs);
				break;
			}
			// Michael Borcherds 2007-11-19
			else if (eName.equals("forceReflexAngle")) {
				ok = handleForceReflexAngle(attrs);
				break;
			}
			// Michael Borcherds 2007-11-19

		case 'g':
			if (eName.equals("ggbscript")) {
				ok = handleScript(attrs,false);
				break;
			}

		case 'i':
			if (eName.equals("isLaTeX")) {
				ok = handleIsLaTeX(attrs);
				break;
			} else if (eName.equals("inBackground")) {
				ok = handleInBackground(attrs);
				break;
			} else if (eName.equals("interpolate")) {
				ok = handleInterpolate(attrs);
				break;
			}

		case 'j':
			if (eName.equals("javascript")) {
				ok = handleScript(attrs,true);
				break;
			}

		case 'k':
			if (eName.equals("keepTypeOnTransform")) {
				ok = handleKeepTypeOnTransform(attrs);
				break;
			}

		case 'l':
			if (eName.equals("lineStyle")) {
				ok = handleLineStyle(attrs);
				break;
			} else if (eName.equals("labelOffset")) {
				ok = handleLabelOffset(attrs);
				break;
			} else if (eName.equals("labelMode")) {
				ok = handleLabelMode(attrs);
				break;
			} else if (eName.equals("layer")) {
				ok = handleLayer(attrs);
				break;
			} else if (eName.equals("linkedGeo")) {
				ok = handleLinkedGeo(attrs);
				break;
			} else if (eName.equals("length")) {
				ok = handleLength(attrs);
				break;
			}

		case 'm':
			if (eName.equals("matrix")) {
				ok = handleMatrix(attrs);
				break;
			}

		case 'o':
			if (eName.equals("objColor")) {
				ok = handleObjColor(attrs);
				break;
			} else if (eName.equals("outlyingIntersections")) {
				ok = handleOutlyingIntersections(attrs);
				break;
			} /*else if (eName.equals("objCoords")) {
				ok = handleObjCoords(attrs);
				break;
			}*/

		case 'p':
			if (eName.equals("pointSize")) {
				ok = handlePointSize(attrs);
				break;
			}

			// Florian Sonner 2008-07-17
			else if (eName.equals("pointStyle")) {
				ok = handlePointStyle(attrs);
				break;
			}
			/*
			 * should not be needed else if (eName.equals("pathParameter")) { ok =
			 * handlePathParameter(attrs); break; }
			 */

		case 's':
			if (eName.equals("show")) {
				ok = handleShow(attrs);
				break;
			}else if (eName.equals("showOnAxis")) {
				ok = handleShowOnAxis(attrs);
				break;
			}
			else if (eName.equals("startPoint")) {
				ok = handleStartPoint(attrs);
				break;
			} else if (eName.equals("slider")) {
				ok = handleSlider(attrs);
				break;
			} else if (eName.equals("slopeTriangleSize")) {
				ok = handleSlopeTriangleSize(attrs);
				break;
			} else if (eName.equals("significantfigures")) {
				ok = handleTextFigures(attrs);
				break;
			} else if (eName.equals("spreadsheetTrace")) {
				ok = handleSpreadsheetTrace(attrs);
				break;
			} else if (eName.equals("showTrimmed")) {
				ok = handleShowTrimmed(attrs);
				break;
			} else if (eName.equals("selectionAllowed")) {
				ok = handleSelectionAllowed(attrs);
				break;
			} else if (eName.equals("selectedIndex")) {
				ok = handleSelectedIndex(attrs);
				break;
			}

		case 't':
			if (eName.equals("trace")) {
				ok = handleTrace(attrs);
				break;
			} else if (eName.equals("tooltipMode")) {
				ok = handleTooltipMode(attrs);
				break;
			}
			
		case 'u':
			if (eName.equals("userinput")){
				ok =handleUserInput(attrs);
				break;
			}

		case 'v':
			if (eName.equals("value")) {
				ok = handleValue(attrs);
				break;
			}

		default:
			System.err.println("unknown tag in <element>: " + eName);
		}

		if (!ok) {
			System.err.println("error in <element>: " + eName);
		}
	}



	private boolean handleShow(LinkedHashMap<String, String> attrs) {
		try {
			geo.setEuclidianVisible(parseBoolean((String) attrs.get("object")));
			geo.setLabelVisible(parseBoolean((String) attrs.get("label")));
			
			// bit 0 -> display object in EV1, 0 = true (default)
			// bit 1 -> display object in EV2, 0 = false (default)
			int EVs = 0; // default, display in just EV1
			String str = (String) attrs.get("ev");
			if (str != null) 
			  EVs = Integer.parseInt(str);
			
			if ((EVs & 1) == 0) // bit 0
				geo.addView(Application.VIEW_EUCLIDIAN);
			else
				geo.removeView(Application.VIEW_EUCLIDIAN);
			
			if ((EVs & 2) == 2) { // bit 1
				geo.addView(Application.VIEW_EUCLIDIAN2);		
			} else {
				geo.removeView(Application.VIEW_EUCLIDIAN2);
			}
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleShowOnAxis(LinkedHashMap<String, String> attrs) {
		try {
			if(!(geo instanceof GeoFunction))
				return false;
			((GeoFunction)geo).setShowOnAxis(parseBoolean((String) attrs.get("val")));
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	private boolean handleObjColor(LinkedHashMap<String, String> attrs) {
		Color col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		geo.setObjColor(col);

		// Dynamic colors
		// Michael Borcherds 2008-04-02
		String red = (String) attrs.get("dynamicr");
		String green = (String) attrs.get("dynamicg");
		String blue = (String) attrs.get("dynamicb");
		String alpha = (String) attrs.get("dynamica");
		String colorSpace = (String) attrs.get("colorSpace");

		if (red != null && green != null && blue != null)
			try {
				if (!red.equals("") || !green.equals("") || !blue.equals("")) {
					if (red.equals(""))
						red = "0";
					if (green.equals(""))
						green = "0";
					if (blue.equals(""))
						blue = "0";
					
					StringBuilder sb = new StringBuilder();
					sb.append('{');
					sb.append(red);
					sb.append(',');
					sb.append(green);
					sb.append(',');
					sb.append(blue);
					if (alpha != null && !alpha.equals("")) {
						sb.append(',');
						sb.append(alpha);
					}
					sb.append('}');

					// need to to this at end of construction (dependencies!)
					dynamicColorList.add(new GeoExpPair(geo, sb.toString()));
					geo.setColorSpace(colorSpace == null ? GeoElement.COLORSPACE_RGB : Integer.parseInt(colorSpace));
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Error loading Dynamic Colors");
			}
			
			String angle = (String) attrs.get("hatchAngle");
			if (angle != null) {
				geo.setHatchingAngle(Integer.parseInt(angle));
				geo.setFillType(GeoElement.FILL_HATCH);
			}
			
			String inverse = (String) attrs.get("inverseFill");
			if (inverse != null) {
				geo.setInverseFill(Boolean.parseBoolean(inverse));				
			}

			String distance = (String) attrs.get("hatchDistance");
			if (angle != null) {
				geo.setHatchingDistance(Integer.parseInt(distance));
				geo.setFillType(GeoElement.FILL_HATCH);
			}

			String filename = (String) attrs.get("image");
			if (filename != null) {
				geo.setFillImage(filename);
				geo.setFillType(GeoElement.FILL_IMAGE);
			}

		alpha = (String) attrs.get("alpha");
		if (alpha != null
				&& (!geo.isGeoList() || ggbFileFormat > 3.19)) // ignore alpha value for lists prior to GeoGebra 3.2
			geo.setAlphaValue(Float.parseFloat(alpha));
		return true;
	}
	
	private boolean handleBgColor(LinkedHashMap<String, String> attrs) {
		Color col = handleColorAlphaAttrs(attrs);
		if (col == null)
			return false;
		geo.setBackgroundColor(col);
		geo.updateRepaint();

		return true;
	}
	
	/*
	 * expects r, g, b attributes to build a color
	 */
	private Color handleColorAttrs(LinkedHashMap<String, String> attrs) {
		try {
			int red = Integer.parseInt((String) attrs.get("r"));
			int green = Integer.parseInt((String) attrs.get("g"));
			int blue = Integer.parseInt((String) attrs.get("b"));
			return new Color(red, green, blue);
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * expects r, g, b, alpha attributes to build a color
	 */
	private Color handleColorAlphaAttrs(LinkedHashMap<String, String> attrs) {
		try {
			int red = Integer.parseInt((String) attrs.get("r"));
			int green = Integer.parseInt((String) attrs.get("g"));
			int blue = Integer.parseInt((String) attrs.get("b"));
			int alpha = Integer.parseInt((String) attrs.get("alpha"));
			return new Color(red, green, blue, alpha);
		} catch (Exception e) {
			return null;
		}
	}

	private boolean handleLineStyle(LinkedHashMap<String, String> attrs) {
		try {
			geo.setLineType(Integer.parseInt( attrs.get("type")));			
			geo.setLineThickness(Integer.parseInt( attrs.get("thickness")));	
			
			// for 3D
			String typeHidden = attrs.get("typeHidden");
			if (typeHidden != null)
				geo.setLineTypeHidden(Integer.parseInt(typeHidden));	
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleDecoration(LinkedHashMap<String, String> attrs) {
		try {
			geo.setDecorationType(Integer.parseInt((String) attrs.get("type")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleEqnStyle(LinkedHashMap<String, String> attrs) {
		// line
		if (geo.isGeoLine()) {
			GeoLine line = (GeoLine) geo;
			String style = (String) attrs.get("style");
			if (style.equals("implicit")) {
				line.setToImplicit();
			} else if (style.equals("explicit")) {
				line.setToExplicit();
			} else if (style.equals("parametric")) {
				String parameter = (String) attrs.get("parameter");
				line.setToParametric(parameter);
			} else {
				System.err.println("unknown style for line in <eqnStyle>: "
						+ style);
				return false;
			}
		}
		// conic
		else if (geo.isGeoConic()) {
			GeoConicND conic = (GeoConicND) geo;
			String style = (String) attrs.get("style");
			if (style.equals("implicit")) {
				conic.setToImplicit();
			} else if (style.equals("specific")) {
				conic.setToSpecific();
			} else if (style.equals("explicit")) {
				conic.setToExplicit();
			} else {
				System.err.println("unknown style for conic in <eqnStyle>: "
						+ style);
				return false;
			}
		} else {
			System.err.println("wrong element type for <eqnStyle>: "
					+ geo.getClass());
			return false;
		}
		return true;
	}

	private boolean handleCoords(LinkedHashMap<String, String> attrs) {
		
		return kernel.handleCoords(geo, attrs);
		
	}
	
	// for point or vector
	private boolean handleCoordStyle(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoPoint() || geo.isGeoVector())) {
			System.err.println("wrong element type for <coordStyle>: "
					+ geo.getClass());
			return false;
		}
		GeoVec3D v = (GeoVec3D) geo;
		String style = (String) attrs.get("style");
		if (style.equals("cartesian")) {
			v.setCartesian();
		} else if (style.equals("polar")) {
			v.setPolar();
		} else if (style.equals("complex")) {
			v.setComplex();
		} else {
			System.err.println("unknown style in <coordStyle>: " + style);
			return false;
		}
		return true;
	}

	private boolean handleCaption(LinkedHashMap<String, String> attrs) {
		try {
			geo.setCaption((String) attrs.get("val"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleScript(LinkedHashMap<String, String> attrs,boolean javaScript) {
		try {			
			String clickScript = (String) attrs.get("val");
			if(clickScript != null && clickScript.length()>0){
				geo.setClickScript(clickScript, false);
				geo.setClickJavaScript(javaScript);
			}
			String updateScript = (String) attrs.get("onUpdate");			
			if(updateScript != null && updateScript.length()>0){
				geo.setUpdateScript(updateScript, false);
				geo.setUpdateJavaScript(javaScript);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	

	private boolean handleCondition(LinkedHashMap<String, String> attrs) {
		try {
			// condition for visibility of object
			String strShowObjectCond = (String) attrs.get("showObject");
			if (strShowObjectCond != null) {
				// store (geo, epxression) values
				// they will be processed in processShowObjectConditionList()
				// later
				showObjectConditionList.add(new GeoExpPair(geo,
						strShowObjectCond));
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleCheckbox(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoBoolean())) {
			System.err.println("wrong element type for <checkbox>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoBoolean bool = (GeoBoolean) geo;
			bool.setCheckboxFixed(parseBoolean((String) attrs.get("fixed")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleValue(LinkedHashMap<String, String> attrs) {
		boolean isBoolean = geo.isGeoBoolean();
		boolean isNumber = geo.isGeoNumeric();
		boolean isButton = geo.isGeoButton();

		if (!(isNumber || isBoolean || isButton)) {
			Application.debug("wrong element type for <value>: "
					+ geo.getClass());
			return false;
		}

		try {
			String strVal = (String) attrs.get("val");
			if (isNumber) {
				GeoNumeric n = (GeoNumeric) geo;
				n.setValue(Double.parseDouble(strVal));
				
				// random
				n.setRandom("true".equals(attrs.get("random")));
			
			} else if (isBoolean) {
				GeoBoolean bool = (GeoBoolean) geo;
				bool.setValue(parseBoolean(strVal));
			} else if (isButton) {
				GeoButton button = (GeoButton)geo;
				button.setClickScript(strVal, false);
				button.setClickJavaScript(true);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handlePointSize(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof PointProperties)) {
			Application.debug("wrong element type for <pointSize>: "
					+ geo.getClass());
			return false;
		}

		try {
			PointProperties p = (PointProperties) geo;
			p.setPointSize(Integer.parseInt((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// Florian Sonner 2008-07-17
	private boolean handlePointStyle(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof PointProperties)) {
			Application.debug("wrong element type for <pointStyle>: "
					+ geo.getClass());
			return false;
		}

		try {
			PointProperties p = (PointProperties) geo;
			
			int style = Integer.parseInt((String) attrs.get("val"));
			
			if(style == -1) {
				style = docPointStyle;
			}
			p.setPointStyle(style);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// Michael Borcherds 2008-02-26
	private boolean handleLayer(LinkedHashMap<String, String> attrs) {

		try {
			geo.setLayer(Integer.parseInt((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleCasCellInput(LinkedHashMap<String, String> attrs) {
		try {
			String input = (String) attrs.get("value");
			geoCasCell.setInput(input);
			
			String prefix = (String) attrs.get("prefix");
			String eval = (String) attrs.get("eval");
			String postfix = (String) attrs.get("postfix");
			if (eval != null) {
				geoCasCell.setProcessingInformation(prefix, eval, postfix);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleCasCellOutput(LinkedHashMap<String, String> attrs) {
		try {
			String output = (String) attrs.get("value");
			boolean error = parseBoolean((String) attrs.get("error"));
			
			if (error) {
				geoCasCell.setError(output);
			} else {
				geoCasCell.setOutput(output);
			}
			
			String evalCommandComment = (String) attrs.get("evalCommand");
			if (evalCommandComment != null) {
				geoCasCell.setEvalCommand(evalCommandComment);
			}
			
			String evalComment = (String) attrs.get("evalComment");
			if (evalComment != null) {
				geoCasCell.setEvalComment(evalComment);
			}			
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

//	private boolean handleCASPairColor(LinkedHashMap<String, String> attrs) {
//		Color col = handleColorAttrs(attrs);
//		if (col == null)
//			return false;
//		// geo.setObjColor(col);
//
//		return true;
//	}

	/*
	 * this should not be needed private boolean
	 * handlePathParameter(LinkedHashMap<String, String> attrs) { if (!(geo.isGeoPoint())) {
	 * Application.debug( "wrong element type for <handlePathParameter>: " +
	 * geo.getClass()); return false; }
	 * 
	 * try { GeoPoint p = (GeoPoint) geo; PathParameter param = new
	 * PathParameter(); double t = Double.parseDouble((String)
	 * attrs.get("val")); param.setT(t);
	 * 
	 * String strBranch = (String) attrs.get("branch"); if (strBranch != null) {
	 * param.setBranch(Integer.parseInt(strBranch)); }
	 * 
	 * String strType = (String) attrs.get("type"); if (strType != null) {
	 * param.setPathType(Integer.parseInt(strType)); }
	 * 
	 * p.initPathParameter(param); return true; } catch (Exception e) { return
	 * false; } }
	 */

	private boolean handleSlider(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoNumeric())) {
			System.err.println("wrong element type for <slider>: "
					+ geo.getClass());
			return false;
		}

		try {
			// don't create sliders in macro construction
			if (geo.getKernel().isMacroKernel())
				return true;

			GeoNumeric num = (GeoNumeric) geo;

			String str = (String) attrs.get("min");
			if (str != null) {
				minMaxList.add(new GeoExpPair(geo, str));
				String str2 = (String) attrs.get("max");
				minMaxList.add(new GeoExpPair(geo, str2));
			}

			str = (String) attrs.get("absoluteScreenLocation");
			if (str != null) {
				num.setAbsoluteScreenLocActive(parseBoolean(str));
			} else {
				num.setAbsoluteScreenLocActive(false);
			}

			double x = Double.parseDouble((String) attrs.get("x"));
			double y = Double.parseDouble((String) attrs.get("y"));
			num.setSliderLocation(x, y, true);
			num.setSliderWidth(Double.parseDouble((String) attrs.get("width")));
			num.setSliderFixed(parseBoolean((String) attrs.get("fixed")));
			num.setSliderHorizontal(parseBoolean((String) attrs
					.get("horizontal")));

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleTrace(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof Traceable)) {
			System.err.println("wrong element type for <trace>: "
					+ geo.getClass());
			return false;
		}

		try {
			Traceable t = (Traceable) geo;
			t.setTrace(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSpreadsheetTrace(LinkedHashMap<String, String> attrs) {
		
		// G.Sturr 2010-5-30
		// XML handling for new tracing code  
		if (!geo.isSpreadsheetTraceable()) {
			System.err.println("wrong element type for <trace>: "
					+ geo.getClass());
			return false;
		}

		try {
			
			// Make sure thatSpreadsheetView is attached. If a file 
			// was saved with the spreadsheet closed, it opens without
			// attaching the view and trace geos will not be activated.
			//app.getGuiManager().attachSpreadsheetView();
			
			// set geo for tracing
			geo.setSpreadsheetTrace(parseBoolean((String) attrs.get("val")));
			
			TraceSettings t = geo.getTraceSettings();
			t.traceColumn1 = Integer.parseInt((String) attrs.get("traceColumn1"));
			t.traceColumn2 = Integer.parseInt((String) attrs.get("traceColumn2"));
			t.traceRow1 = Integer.parseInt((String) attrs.get("traceRow1"));
			t.traceRow2 = Integer.parseInt((String) attrs.get("traceRow2"));
			t.tracingRow = Integer.parseInt((String) attrs.get("tracingRow"));
			t.numRows = Integer.parseInt((String) attrs.get("numRows"));	
			t.headerOffset = Integer.parseInt((String) attrs.get("headerOffset"));
				
			t.doColumnReset = (parseBoolean((String) attrs.get("doColumnReset")));
			t.doRowLimit = (parseBoolean((String) attrs.get("doRowLimit")));
			t.showLabel = (parseBoolean((String) attrs.get("showLabel")));
			t.showTraceList = (parseBoolean((String) attrs.get("showTraceList")));
			t.doTraceGeoCopy = (parseBoolean((String) attrs.get("doTraceGeoCopy")));
					
			return true;
			
		} catch (Exception e) {
			return false;
		}
	
	
		/*  OLD CODE
		
		if (!(geo instanceof GeoPoint)) {
			System.err.println("wrong element type for <trace>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoPoint p = (GeoPoint) geo;
			p.setSpreadsheetTrace(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
		
		*/ 
		// END G.Sturr
		
	}

	private boolean handleShowTrimmed(LinkedHashMap<String, String> attrs) {
		try {
			geo.setShowTrimmedIntersectionLines(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSelectionAllowed(LinkedHashMap<String, String> attrs) {
		try {
			geo.setSelectionAllowed(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSelectedIndex(LinkedHashMap<String, String> attrs) {
		try {
			if (geo.isGeoList())
			((GeoList)geo).setSelectedIndex(Integer.parseInt(((String) attrs.get("val"))));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleAnimation(LinkedHashMap<String, String> attrs) {
		try {
			
			
			String strStep = (String) attrs.get("step");
			if (strStep != null) {
				// store speed expression to be processed later
				animationStepList.add(new GeoExpPair(geo, strStep));			
			}
			String strSpeed = (String) attrs.get("speed");
			if (strSpeed != null) {
				// store speed expression to be processed later
				animationSpeedList.add(new GeoExpPair(geo, strSpeed));			
			}
				
			String type = (String) attrs.get("type");
			if (type != null)
				geo.setAnimationType(Integer.parseInt(type));
			
			// doesn't work for hidden sliders now that intervalMin/Max are set at end of XML (dynamic slider range(
			//geo.setAnimating(parseBoolean((String) attrs.get("playing")));
			
			// replacement
			if (parseBoolean((String) attrs.get("playing")))
				animatingList.add(geo);			

			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleFixed(LinkedHashMap<String, String> attrs) {
		try {
			geo.setFixed(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleBreakpoint(LinkedHashMap<String, String> attrs) {
		try {
			geo.setConsProtocolBreakpoint(parseBoolean((String) attrs
					.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleFile(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoImage())) {
			System.err.println("wrong element type for <file>: "
					+ geo.getClass());
			return false;
		}

		try {
			((GeoImage) geo).setImageFileName((String) attrs.get("name"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// <font serif="false" size="12" style="0">
	private boolean handleTextFont(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof TextProperties)) {
			System.err.println("wrong element type for <font>: "
					+ geo.getClass());
			return false;
		}
		
		Object serif = attrs.get("serif");
		Object style = attrs.get("style");

		try {
			TextProperties text = (TextProperties) geo;
			text.setFontSize(Integer.parseInt((String) attrs.get("size"))); // compulsory
			if (serif != null) text.setSerifFont(parseBoolean((String) serif));
			if (style != null) text.setFontStyle(Integer.parseInt((String) style));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleTextDecimals(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof TextProperties)) {
			System.err.println("wrong element type for <decimals>: "
					+ geo.getClass());
			return false;
		}

		try {
			TextProperties text = (TextProperties) geo;
			text.setPrintDecimals(Integer.parseInt((String) attrs.get("val")), true);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleTextFigures(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof TextProperties)) {
			System.err.println("wrong element type for <decimals>: "
					+ geo.getClass());
			return false;
		}

		try {
			TextProperties text = (TextProperties) geo;
			text.setPrintFigures(Integer.parseInt((String) attrs.get("val")), true);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleInBackground(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoImage())) {
			System.err.println("wrong element type for <inBackground>: "
					+ geo.getClass());
			return false;
		}

		try {
			((GeoImage) geo).setInBackground(parseBoolean((String) attrs
					.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean handleInterpolate(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoImage())) {
			System.err.println("wrong element type for <interpolate>: "
					+ geo.getClass());
			return false;
		}

		try {
			((GeoImage) geo).setInterpolate(parseBoolean((String) attrs
					.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	

	private boolean handleAuxiliary(LinkedHashMap<String, String> attrs) {
		try {
			geo.setAuxiliaryObject(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleIsLaTeX(LinkedHashMap<String, String> attrs) {
		try {


			((GeoText) geo).setLaTeX(parseBoolean((String) attrs.get("val")),
					false);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleArcSize(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof GeoAngle)) {
			System.err.println("wrong element type for <arcSize>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoAngle angle = (GeoAngle) geo;
			angle.setArcSize(Integer.parseInt((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleAbsoluteScreenLocation(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof AbsoluteScreenLocateable)) {
			Application
					.debug("wrong element type for <absoluteScreenLocation>: "
							+ geo.getClass());
			return false;
		}

		try {
			AbsoluteScreenLocateable absLoc = (AbsoluteScreenLocateable) geo;
			int x = Integer.parseInt((String) attrs.get("x"));
			int y = Integer.parseInt((String) attrs.get("y"));
			absLoc.setAbsoluteScreenLoc(x, y);
			absLoc.setAbsoluteScreenLocActive(true);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleAllowReflexAngle(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoAngle())) {
			System.err.println("wrong element type for <allowReflexAngle>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoAngle angle = (GeoAngle) geo;
			angle.setAllowReflexAngle(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {

			return false;
		}
	}
		
	private boolean handleEmphasizeRightAngle(LinkedHashMap<String,String> attrs) {
		if (!(geo.isGeoAngle())) {
			System.err.println("wrong element type for <emphasizeRightAngle>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoAngle angle = (GeoAngle) geo;
			angle.setEmphasizeRightAngle(parseBoolean(attrs.get("val")));
			return true;
		} catch (Exception e) {

			return false;
		}
	}

	// Michael Borcherds 2007-11-19
	private boolean handleForceReflexAngle(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoAngle())) {
			System.err.println("wrong element type for <forceReflexAngle>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoAngle angle = (GeoAngle) geo;
			angle.setForceReflexAngle(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {

			return false;
		}
	}

	// Michael Borcherds 2007-11-19

	private boolean handleOutlyingIntersections(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof LimitedPath)) {
			Application
					.debug("wrong element type for <outlyingIntersections>: "
							+ geo.getClass());
			return false;
		}

		try {
			LimitedPath lpath = (LimitedPath) geo;
			lpath.setAllowOutlyingIntersections(parseBoolean((String) attrs
					.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKeepTypeOnTransform(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof LimitedPath)) {
			Application
					.debug("wrong element type for <outlyingIntersections>: "
							+ geo.getClassName());
			return false;
		}

		try {
			LimitedPath lpath = (LimitedPath) geo;
			lpath.setKeepTypeOnGeometricTransform(parseBoolean((String) attrs
					.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSlopeTriangleSize(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoNumeric())) {
			System.err.println("wrong element type for <slopeTriangleSize>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoNumeric num = (GeoNumeric) geo;
			num.setSlopeTriangleSize(Integer
					.parseInt((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Start Points have to be handled at the end of the construction, because
	 * they could depend on objects that are defined after this GeoElement.
	 * 
	 * So we store all (geo, startpoint expression) pairs and process them at
	 * the end of the construction.
	 * 
	 * @see processStartPointList
	 */
	private boolean handleStartPoint(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof Locateable)) {
			System.err.println("wrong element type for <startPoint>: "
					+ geo.getClass());
			return false;
		}
		Locateable loc = (Locateable) geo;

		// relative start point (expression or label expected)
		String exp = (String) attrs.get("exp");
		if (exp == null) // try deprecated attribute
			exp = (String) attrs.get("label");

		// for corners a number of the startPoint is given
		int number = 0;
		try {
			number = Integer.parseInt((String) attrs.get("number"));
		} catch (Exception e) {
		}

		if (exp != null) {
			// store (geo, epxression, number) values
			// they will be processed in processStartPoints() later
			startPointList.add(new LocateableExpPair(loc, exp, number));	
			loc.setWaitForStartPoint();
		}
		else {
			// absolute start point (coords expected)
			try {
				/*
				double x = Double.parseDouble((String) attrs.get("x"));
				double y = Double.parseDouble((String) attrs.get("y"));
				double z = Double.parseDouble((String) attrs.get("z"));
				GeoPoint p = new GeoPoint(cons);
				p.setCoords(x, y, z);
				*/
				
				GeoPointND p = handleAbsoluteStartPoint(attrs);
				
				if (number == 0) {
					// set first start point right away
					loc.setStartPoint(p);
				} else {
					// set other start points later
					// store (geo, point, number) values
					// they will be processed in processStartPoints() later
					startPointList.add(new LocateableExpPair(loc, p, number));	
					loc.setWaitForStartPoint();
				}				
			} catch (Exception e) {
				return false;
			}
		}
		
		return true;
	}
	
	/** create absolute start point (coords expected) 
	 * @param attrs 
	 * @return start point */
	protected GeoPointND handleAbsoluteStartPoint(LinkedHashMap<String, String> attrs) {
		double x = Double.parseDouble((String) attrs.get("x"));
		double y = Double.parseDouble((String) attrs.get("y"));
		double z = Double.parseDouble((String) attrs.get("z"));
		GeoPoint p = new GeoPoint(cons);
		p.setCoords(x, y, z);
		return p;
	}

	private void processStartPointList() {
		try {
			Iterator<LocateableExpPair> it = startPointList.iterator();
			AlgebraProcessor algProc = kernel.getAlgebraProcessor();

			while (it.hasNext()) {
				LocateableExpPair pair = it.next();
				GeoPointND P = pair.point != null ? pair.point : 
								algProc.evaluateToPoint(pair.exp);
				pair.locateable.setStartPoint(P, pair.number);
								
			}
		} catch (Exception e) {
			startPointList.clear();
			e.printStackTrace();
			throw new MyError(app, "processStartPointList: " + e.toString());
		}
		startPointList.clear();
	}

	private boolean handleLength(LinkedHashMap<String, String> attrs) {

		// name of linked geo
		String val = (String) attrs.get("val");

		if (geo instanceof GeoTextField) {
			((GeoTextField)geo).setLength(Integer.parseInt(val));
		} else {
			throw new MyError(app, "handleLength: " + geo.getClassName());
		}
		
		return true;
	}

	/**
	 * Linked Geos have to be handled at the end of the construction, because
	 * they could depend on objects that are defined after this GeoElement.
	 * 
	 * So we store all (geo, expression) pairs and process them at
	 * the end of the construction.
	 * 
	 * @see processLinkedGeoList
	 */
	private boolean handleLinkedGeo(LinkedHashMap<String, String> attrs) {

		// name of linked geo
		String exp = (String) attrs.get("exp");

		if (exp != null) {
			// store (geo, epxression, number) values
			// they will be processed in processLinkedGeos() later
			linkedGeoList.add(new GeoExpPair(geo, exp));	
		}
		else 
			return false;
		
		return true;
	}

	private void processLinkedGeoList() {
		try {
			Iterator<GeoExpPair> it = linkedGeoList.iterator();

			while (it.hasNext()) {
				GeoExpPair pair = (GeoExpPair) it.next();
				
				((GeoTextField)pair.geo).setLinkedGeo(kernel.lookupLabel(pair.exp));
			}
		} catch (Exception e) {
			linkedGeoList.clear();
			e.printStackTrace();
			throw new MyError(app, "processlinkedGeoList: " + e.toString());
		}
		linkedGeoList.clear();
	}

	private void processShowObjectConditionList() {
		try {
			Iterator<GeoExpPair> it = showObjectConditionList.iterator();
			AlgebraProcessor algProc = kernel.getAlgebraProcessor();

			while (it.hasNext()) {
				GeoExpPair pair = it.next();
				GeoBoolean condition = algProc.evaluateToBoolean(pair.exp);
				pair.geo.setShowObjectCondition(condition);
			}
		} catch (Exception e) {
			showObjectConditionList.clear();
			e.printStackTrace();
			throw new MyError(app, "processShowObjectConditionList: "
					+ e.toString());
		}
		showObjectConditionList.clear();
	}
	
	private void processAnimationSpeedList() {
		try {
			Iterator<GeoExpPair> it = animationSpeedList.iterator();
			AlgebraProcessor algProc = kernel.getAlgebraProcessor();

			while (it.hasNext()) {
				GeoExpPair pair =  it.next();
				NumberValue num = algProc.evaluateToNumeric(pair.exp, false);
				pair.geo.setAnimationSpeedObject(num);
			}
		} catch (Exception e) {
			animationSpeedList.clear();
			e.printStackTrace();
			throw new MyError(app, "processAnimationSpeedList: " + e.toString());
		}
		animationSpeedList.clear();
	}
	
	private void processAnimationStepList() {
		try {
			Iterator<GeoExpPair> it = animationStepList.iterator();
			AlgebraProcessor algProc = kernel.getAlgebraProcessor();

			while (it.hasNext()) {
				GeoExpPair pair =  it.next();
				NumberValue num = algProc.evaluateToNumeric(pair.exp, false);
				pair.geo.setAnimationStep(num);
			}
		} catch (Exception e) {
			animationStepList.clear();
			e.printStackTrace();
			throw new MyError(app, "processAnimationStepList: " + e.toString());
		}
		animationSpeedList.clear();
	}
	
	private void processAnimatingList() {
		try {
			Iterator<GeoElement> it = animatingList.iterator();			

			while (it.hasNext()) {
				GeoElement geo = it.next();
				geo.setAnimating(true);
			}
		} catch (Exception e) {
			animatingList.clear();
			e.printStackTrace();
			throw new MyError(app, "processAnimatingList: " + e.toString());
		}
		animatingList.clear();
	}
	
	private void processMinMaxList() {
		try {
			Iterator<GeoExpPair> it = minMaxList.iterator();
			AlgebraProcessor algProc = kernel.getAlgebraProcessor();

			while (it.hasNext()) {
				GeoExpPair pair = it.next();
				//the setIntervalMin and setIntervalMax methods might turn ? into defined
				//this is intentional, but when loading a file we must override it for 3.2 compatibility
				boolean wasDefined = pair.geo.isDefined();
				NumberValue num = algProc.evaluateToNumeric(pair.exp, false);
				((GeoNumeric)pair.geo).setIntervalMin(num);
				GeoExpPair pair2 = it.next();
				NumberValue num2 = algProc.evaluateToNumeric(pair2.exp, false);
				((GeoNumeric)pair.geo).setIntervalMax(num2);
				if(!wasDefined)
					pair.geo.setUndefined();
			}
		} catch (Exception e) {
			minMaxList.clear();
			e.printStackTrace();
			throw new MyError(app, "processMinMaxList: " + e.toString());
		}
		minMaxList.clear();
	}

	// Michael Borcherds 2008-05-18
	private void processDynamicColorList() {
		try {
			Iterator<GeoExpPair> it = dynamicColorList.iterator();
			AlgebraProcessor algProc = kernel.getAlgebraProcessor();

			while (it.hasNext()) {
				GeoExpPair pair = it.next();
				pair.geo.setColorFunction(algProc.evaluateToList(pair.exp));
			}
		} catch (Exception e) {
			dynamicColorList.clear();
			e.printStackTrace();
			throw new MyError(app, "dynamicColorList: " + e.toString());
		}
		dynamicColorList.clear();
	}

	/*
	private void processDynamicCoordinatesList() {
		try {

			Iterator it = dynamicCoordinatesList.iterator();
			AlgebraProcessor algProc = kernel.getAlgebraProcessor();

			while (it.hasNext()) {
				GeoExpPair pair = (GeoExpPair) it.next();
				((GeoPoint)(pair.geo)).setCoordinateFunction(algProc.evaluateToList(pair.exp));
			}
		} catch (Exception e) {
			dynamicCoordinatesList.clear();
			e.printStackTrace();
			throw new MyError(app, "dynamicCoordinatesList: " + e.toString());
		}
		dynamicCoordinatesList.clear();
	}*/

	private boolean handleEigenvectors(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoConic())) {
			System.err.println("wrong element type for <eigenvectors>: "
					+ geo.getClass());
			return false;
		}
		try {
			GeoConic conic = (GeoConic) geo;
			// set eigenvectors, but don't classify conic now
			// classifyConic() will be called in handleMatrix() by
			// conic.setMatrix()
			conic.setEigenvectors(Double.parseDouble((String) attrs.get("x0")),
					Double.parseDouble((String) attrs.get("y0")), Double
							.parseDouble((String) attrs.get("z0")), Double
							.parseDouble((String) attrs.get("x1")), Double
							.parseDouble((String) attrs.get("y1")), Double
							.parseDouble((String) attrs.get("z1")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleMatrix(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoConic()) && !(geo.isGeoCubic())) {
			System.err.println("wrong element type for <matrix>: "
					+ geo.getClass());
			return false;
		}
		try {
			if (geo.isGeoConic()) {
			GeoConic conic = (GeoConic) geo;
			// set matrix and classify conic now
			// <eigenvectors> should have been set earlier
			double[] matrix = { Double.parseDouble((String) attrs.get("A0")),
					Double.parseDouble((String) attrs.get("A1")),
					Double.parseDouble((String) attrs.get("A2")),
					Double.parseDouble((String) attrs.get("A3")),
					Double.parseDouble((String) attrs.get("A4")),
					Double.parseDouble((String) attrs.get("A5")) };
			conic.setMatrix(matrix);
			} 
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleLabelOffset(LinkedHashMap<String, String> attrs) {
		try {
			geo.labelOffsetX = Integer.parseInt((String) attrs.get("x"));
			geo.labelOffsetY = Integer.parseInt((String) attrs.get("y"));

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleLabelMode(LinkedHashMap<String, String> attrs) {
		try {
			geo.setLabelMode(Integer.parseInt((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleTooltipMode(LinkedHashMap<String, String> attrs) {
		try {
			geo.setTooltipMode(Integer.parseInt((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleCoefficients(LinkedHashMap<String, String> attrs) {
	//	Application.debug(attrs.toString());
		if (!(geo.isGeoImplicitPoly())) {
			Application.debug("wrong element type for <coefficients>: "
					+ geo.getClass(),1);
			return false;
		}
		try {
			String rep=attrs.get("rep");
			if (rep==null)
				return false;
			if (attrs.get("rep").equals("array")){
				String data=attrs.get("data");
				if (data==null)
					return false;
				ArrayList<ArrayList<Double>> collect=new ArrayList<ArrayList<Double>>();
				ArrayList<Double> newRow=new ArrayList<Double>();
				int start=0;
				for(int c=1;c<data.length();c++){
					switch (data.charAt(c)){
					case '[':
						if (newRow.size()>0)
							return false;
						start=c+1;
						break;
					case ']': 
						newRow.add(Double.parseDouble(data.substring(start, c)));
						start=c+1;
						collect.add(newRow);
						newRow=new ArrayList<Double>();
						c++; //jump over ','
						break;
					case ',':
						newRow.add(Double.parseDouble(data.substring(start, c)));
						start=c+1;
					}
				}
				double[][] coeff=new double[collect.size()][];
				for (int i=0;i<collect.size();i++){
					ArrayList<Double> row=collect.get(i);
					coeff[i]=new double[row.size()];
					for (int j=0;j<row.size();j++){
						coeff[i][j]=row.get(j);
					}
				}
				((GeoImplicitPoly)geo).setCoeff(coeff);
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	private boolean handleUserInput(LinkedHashMap<String, String> attrs) {
//		Application.debug(attrs.toString());
		if (!(geo instanceof GeoUserInputElement)) {
			Application.debug("wrong element type for <userinput>: "
					+ geo.getClass(),1);
			return false;
		}
		try{
			if (geo.isIndependent()){
				String value=attrs.get("value");
				if (value==null)
					return false;
				ValidExpression ve=parser.parseGeoGebraExpression(value);
				((GeoUserInputElement)geo).setUserInput(ve);
			}
			if (attrs.get("show")!=null && attrs.get("show").equals("true"))
				((GeoUserInputElement)geo).setInputForm();
			else
				((GeoUserInputElement)geo).setExtendedForm();
			if (attrs.get("valid")!=null){
				((GeoUserInputElement)geo).setValidInputForm(attrs.get("valid").equals("true"));
			}
			return true;
		}catch (Exception e) {
			Application.debug(e.getMessage());
			return false;
		}
	}
	
	// ====================================
	// <command>
	// ====================================

	// called when <command> is encountered
	// e.g. for <command name="Intersect">
	private Command getCommand(LinkedHashMap<String, String> attrs) {
		Command cmd = null;
		String name = (String) attrs.get("name");

		//Application.debug(name);
		if (name != null)
			cmd = new Command(kernel, name, false); // do not translate name
		else
			throw new MyError(app, "name missing in <command>");
		return cmd;
	}

	private void startCommandElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;
		
		if (eName.equals("input")) {
			if (cmd == null)
				throw new MyError(app, "no command set for <input>");
			ok = handleCmdInput(attrs);
		} else if (eName.equals("output")) {
			ok = handleCmdOutput(attrs);
		} else
			System.err.println("unknown tag in <command>: " + eName);

		if (!ok)
			System.err.println("error in <command>: " + eName);
	}

	private boolean handleCmdInput(LinkedHashMap<String, String> attrs) {
		GeoElement geo;
		ExpressionNode en;
		String arg = null;

		Collection<String> values = attrs.values();
		Iterator<String> it = values.iterator();
		while (it.hasNext()) {
			// parse argument expressions
			try {
				arg = (String) it.next();

				// for downward compatibility: lookup label first
				// as this could be some weird name that can't be parsed
				// e.g. "1/2_{a,b}" could be a label name
				// we don't want to override local variables with this fix, therefore we
				// make exception for Sequence and CurveCartesian
				if(cmd.getName().equals("Sequence")||cmd.getName().equals("CurveCartesian"))
					geo = null;
				else
					geo = kernel.lookupLabel(arg);
				
				
				//Application.debug("input : "+geo.getLabel());

				// arg is a label and does not conatin $ signs (e.g. $A1 in
				// spreadsheet)
				if (geo != null && arg.indexOf('$') < 0) {
					en = new ExpressionNode(kernel, geo);
				} else {
					// parse argument expressions
					en = parser.parseCmdExpression(arg);
				}
				cmd.addArgument(en);
			} catch (Exception e) {
				e.printStackTrace();
				throw new MyError(app, "unknown command input: " + arg);
			} catch (Error e) {
				e.printStackTrace();
				throw new MyError(app, "unknown command input: " + arg);
			}
		}
		return true;
	}

	private boolean handleCmdOutput(LinkedHashMap<String,String> attrs) {
		try {
			// set labels for command processing
			String label;
			Collection<String> values = attrs.values();
			Iterator<String> it = values.iterator();
			int countLabels = 0;
			while (it.hasNext()) {
				label = (String) it.next();
				if ("".equals(label))
					label = null;
				else
					countLabels++;
				cmd.addLabel(label);
			}

			// it is possible that we get a command that has been saved
			// where NONE of its output objects had a label
			// (e.g. intersection that never produced any points).
			// Such a command should not be processed as it might
			// use up labels that are needed later on.
			// For example, since v3.0 every intersection command shows
			// at least one labeled (and possibly undefined) point
			// whereas in v2.7 the label was not set before an intersection
			// point became defined for the first time.
			// THUS: let's not process commands with no labels for their output
			if (countLabels == 0)
				return true;

			// process the command
			cmdOutput = kernel.getAlgebraProcessor().processCommand(cmd, true);
			String cmdName = cmd.getName();
			if (cmdOutput == null)
				throw new MyError(app, "processing of command " + cmd
						+ " failed");
			cmd = null;
			

			// ensure that labels are set for invisible objects too
			if (attrs.size() != cmdOutput.length) {
				Application
						.debug("error in <output>: wrong number of labels for command "
								+ cmdName);
				System.err.println("   cmdOutput.length = " + cmdOutput.length
						+ ", labels = " + attrs.size());
				return false;
			}			
			// enforce setting of labels
			// (important for invisible objects like intersection points)
			it = values.iterator();
			int i = 0;
			while (it.hasNext()) {
				label = (String) it.next();				
				
				if ("".equals(label))
					label = null;

				if (label != null && cmdOutput[i] != null) {
					cmdOutput[i].setLoadedLabel(label);
				}
				i++;
			}
			return true;
		} catch (MyError e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new MyError(app, "processing of command: " + cmd);
		}
	}
	
	/**
	 * Reads all attributes into a String array.
	 * 
	 * @param attrs
	 * @return
	 */
	private String[] getAttributeStrings(LinkedHashMap<String, String> attrs) {
		Collection<String> values = attrs.values();
		Iterator<String> it = values.iterator();

		String[] ret = new String[values.size()];
		int i = 0;

		while (it.hasNext()) {
			ret[i] = (String) it.next();
			i++;
		}
		return ret;
	}

	// ====================================
	// <expression>
	// ====================================
	private void startExpressionElement(String eName, LinkedHashMap<String, String> attrs) {
		String label = (String) attrs.get("label");
		
		// ignore twinGeo expressions coming from CAS cells
		// e.g. the GeoCasCell f(x) := x^2 automatically creates a twinGeo f
		// where we don't want the expression of f to be processed again
		GeoElement geo = kernel.lookupLabel(label);
		if (geo != null && geo.getCorrespondingCasCell() != null)
			return;
		
		String exp = (String) attrs.get("exp");
		if (exp == null)
			throw new MyError(app, "exp missing in <expression>");

		// type may be vector or point, this is important to distinguish between
		// them
		String type = (String) attrs.get("type");

		// parse expression and process it
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(exp);
			if (label != null)
				ve.setLabel(label);

			// enforce point or vector or line or plane type if it was given in attribute type
			if (type != null) {
				if (type.equals("point")) {
					((ExpressionNode) ve).setForcePoint();
				} else if (type.equals("vector")) {
					((ExpressionNode) ve).setForceVector();
				} else if (type.equals("line")) {
					((Equation) ve).setForceLine();
				} else if (type.equals("plane")) {
					((Equation) ve).setForcePlane();
				} else if (type.equals("conic")){
					((Equation) ve).setForceConic();
				} else if (type.equals("implicitPoly")){
					((Equation) ve).setForceImplicitPoly();
				}
			}
			
			//Application.debug(""+kernel.getAlgebraProcessor());

			GeoElement[] result = kernel.getAlgebraProcessor()
					.processValidExpression(ve);

			// ensure that labels are set for invisible objects too
			if (result != null && label != null && result.length == 1) {
				result[0].setLoadedLabel(label);
			} else {
				System.err.println("error in <expression>: " + exp + ", label: "
						+ label);
			}

		} catch (Exception e) {
			String msg = "error in <expression>: label=" + label + ", exp= "
					+ exp;
			System.err.println(msg);
			e.printStackTrace();
			throw new MyError(app, msg);
		} catch (Error e) {
			String msg = "error in <expression>: label = " + label + ", exp = "
					+ exp;
			System.err.println(msg);
			e.printStackTrace();
			throw new MyError(app, msg);
		}
	}

	// ====================================
	// UTILS
	// ====================================

	/**
	 * Parse string to boolean
	 * @param str 
	 * @return true for "true", false otherwise
	 * @throws Exception 
	 */
	protected boolean parseBoolean(String str) throws Exception {
		return "true".equals(str);
	}
	/**
	 * Parse string to boolean
	 * @param str 
	 * @return false for "fale", true otherwise
	 * @throws Exception 
	 */
	protected boolean parseBooleanRev(String str) throws Exception {
		return !"false".equals(str);
	}
}
