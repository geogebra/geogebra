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

package geogebra.common.io;

import geogebra.common.GeoGebraConstants;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.io.layout.DockSplitPaneData;
import geogebra.common.io.layout.Perspective;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.KernelCAS;
import geogebra.common.kernel.Locateable;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.MacroKernel;
import geogebra.common.kernel.PathRegionHandling;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.kernel.geos.GeoUserInputElement;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.LimitedPath;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.common.kernel.geos.TextProperties;
import geogebra.common.kernel.geos.Traceable;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.CoordStyle;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.parser.Parser;
import geogebra.common.main.App;
import geogebra.common.main.MyError;
import geogebra.common.main.settings.ConstructionProtocolSettings;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.main.settings.KeyboardSettings;
import geogebra.common.main.settings.ProbabilityCalculatorSettings.DIST;
import geogebra.common.main.settings.SpreadsheetSettings;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.ScriptType;
import geogebra.common.plugin.script.Script;
import geogebra.common.util.SpreadsheetTraceSettings;
import geogebra.common.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.swing.SwingConstants;

import org.xml.sax.SAXException;

/**
 * 
 * @author Markus Hohenwarter
 */
// public class MyXMLHandler extends DefaultHandler {
public class MyXMLHandler implements DocHandler {

	private static final double FORMAT = StringUtil
			.parseDouble(GeoGebraConstants.XML_FILE_FORMAT);

	private static final int MODE_INVALID = -1;
	private static final int MODE_GEOGEBRA = 1;
	private static final int MODE_MACRO = 50;
	private static final int MODE_EUCLIDIAN_VIEW = 100;
	/** currently parsing tags for Euclidian3D view */
	protected static final int MODE_EUCLIDIAN_VIEW3D = 101; // only for 3D
	private static final int MODE_SPREADSHEET_VIEW = 150;
	private static final int MODE_ALGEBRA_VIEW = 151;
	// private static final int MODE_CAS_VIEW = 160;
	private static final int MODE_CONST_CAS_CELL = 161;
	private static final int MODE_CAS_CELL_PAIR = 162;
	private static final int MODE_CAS_INPUT_CELL = 163;
	private static final int MODE_CAS_OUTPUT_CELL = 164;
	private static final int MODE_CAS_TEXT_CELL = 165; 
	private static final int MODE_PROBABILITY_CALCULATOR = 170;
	private static final int MODE_KERNEL = 200;
	private static final int MODE_CONSTRUCTION = 300;
	private static final int MODE_CONST_GEO_ELEMENT = 301;
	private static final int MODE_CONST_COMMAND = 302;

	private static final int MODE_GUI = 400;
	private static final int MODE_GUI_PERSPECTIVES = 401; // <perspectives>
	private static final int MODE_GUI_PERSPECTIVE = 402; // <perspective>
	private static final int MODE_GUI_PERSPECTIVE_PANES = 403; // <perspective>
																// <panes />
																// </perspective>
	private static final int MODE_GUI_PERSPECTIVE_VIEWS = 404; // <perspective>
																// <views />
																// </perspective>

	/** available font sizes (will be reused in OptionsAdvanced)*/
	final public static int[] menuFontSizes = { 12, 14, 16, 18, 20, 24, 28, 32 };
	/** available tooltip timeouts (will be reused in OptionsAdvanced)*/
	final public static String[] tooltipTimeouts = new String[] { "1", "3",
			"5", "10", "20", "30", "60", "0" };
	/** available CAS timeout options (will be reused in OptionsCAS)*/
	final public static Integer[] cbTimeoutOptions = { 5, 10, 20, 30, 60 };

	/** See JSplitPane.HORIZONTAL_SPLIT */
	private static final int JSplitPane_HORIZONTAL_SPLIT = 1;
	/** See JSplitPane.VERTICAL_SPLIT */
	private static final int JSplitPane_VERTICAL_SPLIT = 0;

	/** we used minimal text size of 4px until 4.0 for texts,
	 * because the font size setting was additive. Not needed
	 * with current multiplicative approach, just for opening old files.
	 */
	private static final double MIN_TEXT_SIZE = 4;

	private int mode;
	private int constMode; // submode for <construction>
	private int casMode; // submode for <cascell>
	/** currently parsed element */
	protected GeoElement geo;
	private GeoCasCell geoCasCell;
	private Command cmd;
	private Macro macro;
	/** application */
	protected App app;

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
	private LinkedList<GeoNumericMinMax> minMaxList = new LinkedList<GeoNumericMinMax>();

	private class GeoExpPair {
		private GeoElement geoElement;
		String exp;

		GeoExpPair(GeoElement g, String exp) {
			setGeo(g);
			this.exp = exp;
		}

		GeoElement getGeo() {
			return geoElement;
		}

		void setGeo(GeoElement geo) {
			this.geoElement = geo;
		}
	}
	
	private class GeoNumericMinMax {
		private GeoElement geoElement;
		String min;
		String max;

		GeoNumericMinMax(GeoElement g, String min,String max) {
			setGeo(g);
			this.min = min;
			this.max = max;
		}

		GeoElement getGeo() {
			return geoElement;
		}

		void setGeo(GeoElement geo) {
			this.geoElement = geo;
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
	 * The storage container for all GUI related information of the current
	 * document.
	 */
	private Perspective tmp_perspective;

	/**
	 * A vector with all perspectives we have read in this document.
	 */
	private ArrayList<Perspective> tmp_perspectives = new ArrayList<Perspective>();

	/**
	 * Array lists to store temporary panes and views of a perspective.
	 */
	private ArrayList<DockSplitPaneData> tmp_panes;
	private ArrayList<DockPanelData> tmp_views;

	/**
	 * Backward compatibility for version < 3.03 where no layout component was
	 * used. Temporary storage for the split divider location of the split panes
	 * #1/#2.
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


	// flag so that we can reset EVSettings the first time we get them (for EV1
	// and EV2)
	private boolean resetEVsettingsNeeded = false;

	/**
	 * Creates a new instance of MyXMLHandler
	 * 
	 * @param kernel kernel
	 * @param cons construction
	 */
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
		if (start)
			consStep = -2;

		mode = MODE_INVALID;
		constMode = MODE_CONSTRUCTION;
		hasGuiElement = false;

		initKernelVars();

		xmin.clear();
		xmax.clear();
		ymin.clear();
		ymax.clear();
	}

	private void initKernelVars() {
		this.kernel = origKernel;
		this.parser = origParser;
		this.cons = origKernel.getConstruction();
	}

	public int getConsStep() {
		return consStep;
	}

	/**
	 * @param integer option index
	 * @return timeout in seconds
	 */
	public static Integer getTimeoutOption(long integer) {
		for (int i = 0; i < cbTimeoutOptions.length; i++)
			if (cbTimeoutOptions[i].intValue() == integer)
				return cbTimeoutOptions[i];
		return cbTimeoutOptions[0];
	}

	// ===============================================
	// SAX ContentHandler methods
	// ===============================================

	final public void text(String str) throws SAXException {
		//do nothing
	}

	final public void startDocument() throws SAXException {
		reset(true);
	}

	final public void endDocument() throws SAXException {
		if (mode == MODE_INVALID)
			throw new SAXException(
					app.getPlain("XMLTagANotFound", "<geogebra>"));
	}

	final public void startElement(String eName,
			LinkedHashMap<String, String> attrs) throws SAXException {
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

		// case MODE_CAS_VIEW:
		// startCASViewElement(eName, attrs);
		// break;

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
			if ("geogebra".equals(eName)) {
				mode = MODE_GEOGEBRA;
				// check file format version
				try {
					ggbFileFormat = StringUtil.parseDouble(attrs.get("format"));

					ggbFileFormat = Kernel.checkDecimalFraction(ggbFileFormat);

					if (ggbFileFormat > FORMAT) {
						System.err.println(app.getError("FileFormatNewer")
								+ ": " + ggbFileFormat); // Michael
						// Borcherds
					}

					// removed - doesn't work over an undo
					// fileFormat dependent settings for downward compatibility
					// if (ggbFileFormat < 2.6) {
					// kernel.arcusFunctionCreatesAngle = true;
					// }

					if (ggbFileFormat < 3.0) {
						// before V3.0 the kernel had continuity always on
						if (!(kernel instanceof MacroKernel))
							kernel.setContinuous(true);

						// before V3.0 the automaticGridDistanceFactor was 0.5
						EuclidianStyleConstants.automaticGridDistanceFactor = 0.5;
					}

				} catch (Exception e) {
					throw new MyError(app, "FileFormatUnknown");
				}

				String ggbVersion = attrs.get("version");
				app.setFileVersion(ggbVersion);

				String uniqueId = attrs.get("id");
				if (uniqueId != null)
					app.setUniqueId(uniqueId);
			}
			break;

		default:
			System.err.println("unknown mode: " + mode);
		}
	}

	private void startScriptingElement(LinkedHashMap<String, String> attrs) {
		try {
			String scriptingLanguage = attrs.get("language");
			app.setScriptingLanguage(scriptingLanguage);

			boolean blockScripting = "true".equals(attrs.get("blocked"));
			app.setBlockUpdateScripts(blockScripting);

			boolean scriptingDisabled = "true".equals(attrs.get("disabled"));
			app.setScriptingDisabled(scriptingDisabled);
		} catch (Exception e) {
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
			if ("euclidianView".equals(eName)) {
				evSet = null;
				mode = MODE_GEOGEBRA;
			}
			break;
		case MODE_EUCLIDIAN_VIEW3D:
			if ("euclidianView3D".equals(eName))
				mode = MODE_GEOGEBRA;
			break;

		case MODE_ALGEBRA_VIEW:
			if ("algebraView".equals(eName))
				mode = MODE_GEOGEBRA;
			break;

		case MODE_SPREADSHEET_VIEW:
			if ("spreadsheetView".equals(eName))
				mode = MODE_GEOGEBRA;
			break;

		case MODE_PROBABILITY_CALCULATOR:
			if ("probabilityCalculator".equals(eName))
				mode = MODE_GEOGEBRA;
			break;

		// case MODE_CAS_VIEW:
		// if ("casView".equals(eName))
		// mode = MODE_GEOGEBRA;
		// break;

		case MODE_KERNEL:
			if ("kernel".equals(eName))
				mode = MODE_GEOGEBRA;
			break;

		case MODE_GUI:
			if ("gui".equals(eName))
				mode = MODE_GEOGEBRA;
			break;

		case MODE_GUI_PERSPECTIVES:
			if ("perspectives".equals(eName))
				mode = MODE_GUI;
			endGuiPerspectivesElement(); // save all perspectives
			break;

		case MODE_GUI_PERSPECTIVE:
			if ("perspective".equals(eName))
				mode = MODE_GUI_PERSPECTIVES;
			endGuiPerspectiveElement(); // save views & panes of the perspective
			break;

		case MODE_GUI_PERSPECTIVE_PANES:
			if ("panes".equals(eName))
				mode = MODE_GUI_PERSPECTIVE;
			break;

		case MODE_GUI_PERSPECTIVE_VIEWS:
			if ("views".equals(eName))
				mode = MODE_GUI_PERSPECTIVE;
			break;

		case MODE_CONSTRUCTION:
			endConstructionElement(eName);
			break;

		case MODE_MACRO:
			if ("macro".equals(eName)) {
				endMacro();
				mode = MODE_GEOGEBRA;
			}
			break;

		case MODE_GEOGEBRA:
			if ("geogebra".equals(eName)) {
				// start animation if necessary
				if (startAnimation) {
					if (app.isApplet())
						// start later, in initInBackground()
						kernel.setWantAnimationStarted();
					else
						kernel.getAnimatonManager().startAnimation();
				}

				// perform tasks to maintain backward compability
				if (ggbFileFormat < 3.3 && hasGuiElement) {
					createCompabilityLayout();
				}
			}
			break;
		}
	}

	// ====================================
	// <geogebra>
	// ====================================
	private void startGeoGebraElement(String eName,
			LinkedHashMap<String, String> attrs) {
		if ("euclidianView".equals(eName)) {
			mode = MODE_EUCLIDIAN_VIEW;
			resetEVsettingsNeeded = true;
		} else if ("euclidianView3D".equals(eName)) {
			mode = MODE_EUCLIDIAN_VIEW3D;
		} else if ("algebraView".equals(eName)) {
			mode = MODE_ALGEBRA_VIEW;
		} else if ("kernel".equals(eName)) {
			// default value
			// (make sure old files work)
			kernel.setUsePathAndRegionParameters(PathRegionHandling.ON);
			mode = MODE_KERNEL;
		} else if ("spreadsheetView".equals(eName)) {
			mode = MODE_SPREADSHEET_VIEW;
			// } else if ("casView".equals(eName)) {
			// mode = MODE_CAS_VIEW;
		} else if ("scripting".equals(eName)) {
			startScriptingElement(attrs);
		} else if ("probabilityCalculator".equals(eName)) {
			mode = MODE_PROBABILITY_CALCULATOR;
		} else if ("gui".equals(eName)) {
			mode = MODE_GUI;
			hasGuiElement = true;

			if (ggbFileFormat < 3.3)
				tmp_perspective = new Perspective("tmp");
		} else if ("macro".equals(eName)) {
			mode = MODE_MACRO;
			initMacro(attrs);
		} else if ("construction".equals(eName)) {
			mode = MODE_CONSTRUCTION;
			handleConstruction(attrs);
		} else if ("casSession".equals(eName)) {
			// old <casSession> is now <cascell> in <construction>
			// not used anymore after 2011-08-16
			mode = MODE_CONSTRUCTION;
			constMode = MODE_CONST_CAS_CELL;
		} else if ("keyboard".equals(eName)) {
			handleKeyboard(attrs);
		} else {
			System.err.println("unknown tag in <geogebra>: " + eName);
		}
	}

	private void handleKeyboard(LinkedHashMap<String, String> attrs) {
		// TODO what if GuiManager is null?
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("error in element <keyboard>");
		}

	}

	private void startMacroElement(String eName,
			LinkedHashMap<String, String> attrs) {
		if ("macroInput".equals(eName)) {
			macroInputLabels = getAttributeStrings(attrs);
		} else if ("macroOutput".equals(eName)) {
			macroOutputLabels = getAttributeStrings(attrs);
		} else if ("construction".equals(eName)) {
			mode = MODE_CONSTRUCTION;
			handleConstruction(attrs);
		} else {
			System.err.println("unknown tag in <macro>: " + eName);
		}
	}

	// ====================================
	// <euclidianView3D> only used in 3D
	// ====================================
	/**
	 * only used in MyXMLHandler3D
	 * 
	 * @param eName element name
	 * @param attrs attributes
	 */
	protected void startEuclidianView3DElement(String eName,
			LinkedHashMap<String, String> attrs) {
		App.debug("TODO : warn that it's a 3D file");
	}

	// ====================================
	// <euclidianView>
	// ====================================

	private EuclidianSettings evSet = null;
	
	private void startEuclidianViewElement(String eName,
			LinkedHashMap<String, String> attrs) {
		boolean ok = true;

		// must do this first
		if ("viewNumber".equals(eName)) {
			int number = Integer.parseInt(attrs.get("viewNo"));
			if (number == 2) 
				evSet = app.getSettings().getEuclidian(2);
			else
				evSet = app.getSettings().getEuclidian(1);
		}else if ("viewId".equals(eName)){
			String plane = attrs.get("plane");
			evSet = app.getSettings().getEuclidianForPlane(plane);
			if (evSet == null)
				evSet = app.getSettings().createEuclidianForPlane(plane);
		}

		if (evSet == null)
			evSet = app.getSettings().getEuclidian(1);

		// make sure eg is reset the first time (for each EV) we get the
		// settings
		// "viewNumber" not stored for EV1 so we need to do this here
		if (resetEVsettingsNeeded) {
			resetEVsettingsNeeded = false;
			evSet.reset();
		}

		switch (firstChar(eName)) {
		case 'a':
			if ("axesColor".equals(eName)) {
				ok = handleAxesColor(evSet, attrs);
				break;
			} else if ("axis".equals(eName)) {
				ok = handleAxis(evSet, attrs);
				break;
			}

		case 'b':
			if ("bgColor".equals(eName)) {
				ok = handleBgColor(evSet, attrs);
				break;
			}

		case 'c':
			if ("coordSystem".equals(eName)) {
				ok = handleCoordSystem(evSet, attrs);
				break;
			}

		case 'e':
			if ("evSettings".equals(eName)) {
				ok = handleEvSettings(evSet, attrs);
				break;
			}

		case 'g':
			if ("grid".equals(eName)) {
				ok = handleGrid(evSet, attrs);
				break;
			} else if ("gridColor".equals(eName)) {
				ok = handleGridColor(evSet, attrs);
				break;
			}
		case 'l':
			if ("lineStyle".equals(eName)) {
				ok = handleLineStyle(evSet, attrs);
				break;
			}

		case 's':
			if ("size".equals(eName)) {
				ok = handleEvSize(evSet, attrs);
				break;
			}
		case 'v':
			if ("viewNumber".equals(eName)) {
				/*
				 * moved earlier, must check first int number =
				 * Integer.parseInt((String) attrs.get("viewNo"));
				 * if(number==2){ viewNo=number; }
				 */
				ok = true;
				break;
			}else if ("viewId".equals(eName)) {
				/*
				 * moved earlier, must check first if for EuclidianViewForPlane
				 */
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
	private void startSpreadsheetViewElement(String eName,
			LinkedHashMap<String, String> attrs) {
		boolean ok = true;

		switch (firstChar(eName)) {

		case 'l':
			if ("layout".equals(eName)) {
				ok = handleSpreadsheetLayout(attrs);
				break;
			}

		case 'p':
			if ("prefCellSize".equals(eName)) {
				ok = handleSpreadsheetCellSize(attrs);
				break;
			}

		case 's':
			if ("size".equals(eName)) {
				ok = handleSpreadsheetSize(attrs);
				break;
			}
			if ("spreadsheetColumn".equals(eName)) {
				ok = handleSpreadsheetColumn(attrs);
				break;
			}
			if ("spreadsheetRow".equals(eName)) {
				ok = handleSpreadsheetRow(attrs);
				break;
			}
			if ("selection".equals(eName)) {
				ok = handleSpreadsheetInitalSelection(attrs);
				break;
			}

			if ("spreadsheetBrowser".equals(eName)) {
				ok = handleSpreadsheetBrowser(attrs);
				break;
			}
			if ("spreadsheetCellFormat".equals(eName)) {
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
	private void startProbabilityCalculatorElement(String eName,
			LinkedHashMap<String, String> attrs) {
		boolean ok = true;

		switch (firstChar(eName)) {

		case 'd':
			if ("distribution".equals(eName)) {
				if (app.isUsingFullGui())
					ok = handleProbabilityDistribution(attrs);
				break;
			}
		default:
			System.err.println("unknown tag in <probabilityCalculator>: "
					+ eName);
		}

		if (!ok)
			System.err.println("error in <probabilityCalculator>: " + eName);
	}

	private boolean handleProbabilityDistribution(
			LinkedHashMap<String, String> attrs) {

		try {
			int distributionType = Integer.parseInt(attrs.get("type"));
			app.getSettings().getProbCalcSettings()
					.setDistributionType(DIST.values()[distributionType]);

			boolean isCumulative = parseBoolean(attrs.get("isCumulative"));
			app.getSettings().getProbCalcSettings().setCumulative(isCumulative);

			// get parameters from comma delimited string
			String parmString = attrs.get("parameters");
			String[] parmStringArray = parmString.split(",");
			double[] parameters = new double[parmStringArray.length];
			for (int i = 0; i < parmStringArray.length; i++)
				parameters[i] = StringUtil.parseDouble(parmStringArray[i]);

			app.getSettings().getProbCalcSettings().setParameters(parameters);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// ====================================
	// <AlgebraView>
	// ====================================
	/**
	 * @param attrs  attributes TODO create some actual attributes
	 */
	private void startAlgebraViewElement(String eName,
			LinkedHashMap<String, String> attrs) {
		boolean ok = true;
		
		switch (firstChar(eName)) {
		case 'a':
			if ("auxiliary".equals(eName)) {
				ok = handleAlgebraViewShowAuxiliaryObjects(attrs);
				break;
			}
		case 'c':
			if ("collapsed".equals(eName)) {
				ok = handleAlgebraViewCollapsedNodes(attrs);
				break;
			}
		case 'm':
			if ("mode".equals(eName)) {
				ok = handleAlgebraViewMode(attrs);
				break;
			}
		default:
			System.err.println("unknown tag in <algebraView>: " + eName);
		}

		if (!ok)
			System.err.println("error in <algebraView>: " + eName);
	}

	// ====================================
	// <CASView>
	// ====================================
	// private void startCASViewElement(String eName, LinkedHashMap<String,
	// String> attrs) {
	// boolean ok = true;
	//
	// switch (firstChar(eName)) {
	// case 's':
	// if ("size".equals(eName)) {
	// ok = handleCASSize(app.getGuiManager().getCasView(), attrs);
	// break;
	// }
	//
	// default:
	// System.err.println("unknown tag in <casView>: " + eName);
	// }
	//
	// if (!ok)
	// System.err.println("error in <casView>: " + eName);
	//
	// }

	private HashMap<EuclidianSettings, String> xmin = new HashMap<EuclidianSettings, String>(),
			xmax = new HashMap<EuclidianSettings, String>(),
			ymin = new HashMap<EuclidianSettings, String>(),
			ymax = new HashMap<EuclidianSettings, String>();

	private boolean handleCoordSystem(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {

		if (xmin.keySet().size() > 1) {
			xmin.clear();
			xmax.clear();
			ymin.clear();
			ymax.clear();
		}
		if (attrs.get("xZero") != null) {
			try {
				double xZero = StringUtil.parseDouble(attrs.get("xZero"));
				double yZero = StringUtil.parseDouble(attrs.get("yZero"));
				double scale = StringUtil.parseDouble(attrs.get("scale"));

				// new since version 2.5
				double yscale = scale;
				String strYscale = attrs.get("yscale");
				if (strYscale != null) {
					yscale = StringUtil.parseDouble(strYscale);
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
		}
		try {
			xmin.put(ev, attrs.get("xMin"));
			xmax.put(ev, attrs.get("xMax"));
			ymin.put(ev, attrs.get("yMin"));
			ymax.put(ev, attrs.get("yMax"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleEvSettings(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		try {
			// axes attribute was removed with V3.0, see handleAxis()
			// this code is for downward compatibility
			String strAxes = attrs.get("axes");
			if (strAxes != null) {
				boolean showAxes = parseBoolean(strAxes);
				// #2534
				ev.setShowAxes(showAxes, showAxes);
			}

			ev.showGrid(parseBoolean(attrs.get("grid")));

			try {
				ev.setGridIsBold(parseBoolean(attrs.get("gridIsBold"))); // Michael
																			// Borcherds
				// 2008-04-11
			} catch (Exception e) {
				//not a number: ignore
			}
			
			try {
				if(attrs.get("lockedAxesRatio")!=null){
					ev.setLockedAxesRatio(StringUtil.parseDouble(attrs.get("lockedAxesRatio"))); 
				}
			} catch (Exception e) {
				//not a number: ignore
			}


			try {
				ev.setGridType(Integer.parseInt(attrs.get("gridType"))); // Michael
																			// Borcherds
				// 2008-04-30
			} catch (Exception e) {
				//not a number: ignore
			}

			String str = attrs.get("pointCapturing");
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
				ev.setPointCapturing(EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC);
			}

			// if there is a point style given save it
			if (ggbFileFormat < 3.3) {
				String strPointStyle = attrs.get("pointStyle");
				if (strPointStyle != null) {
					docPointStyle = Integer.parseInt(strPointStyle);
				} else {
					docPointStyle = EuclidianStyleConstants.POINT_STYLE_DOT;
				}

				// TODO save as default construction (F.S.)
			} else {
				docPointStyle = -1;
			}

			// Michael Borcherds 2008-05-12
			// size of checkbox
			String strBooleanSize = attrs.get("checkboxSize");
			if (strBooleanSize != null)
				app.booleanSize = Integer.parseInt(strBooleanSize);
			// ev.setBooleanSize(Integer.parseInt(strBooleanSize));

			boolean asm = parseBoolean(attrs.get("allowShowMouseCoords"));
			ev.setAllowShowMouseCoords(asm);

			String att = attrs.get("allowToolTips");
			if (att != null)
				ev.setAllowToolTips(Integer.parseInt(att));
			else
				ev.setAllowToolTips(EuclidianStyleConstants.TOOLTIPS_AUTOMATIC);

			// v3.0: appearance of right angle
			String strRightAngleStyle = attrs.get("rightAngleStyle");
			if (strRightAngleStyle == null)
				// before v3.0 the default was a dot to show a right angle
				// ev.setRightAngleStyle(EuclidianView.RIGHT_ANGLE_STYLE_DOT);
				app.rightAngleStyle = EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT;
			else
				// ev.setRightAngleStyle(Integer.parseInt(strRightAngleStyle));
				app.rightAngleStyle = Integer.parseInt(strRightAngleStyle);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	//is there a reason why it was static?
	private boolean handleEvSize(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		// removed, needed to resize applet correctly
		// if (app.isApplet())
		// return true;

		try {
			int width;
			int height;
			if (!App.isFullAppGui()) {
				width = (app.getDataParamWidth() > 0 && !app.getUseFullGui()) ? app.getDataParamWidth() :  Integer.parseInt(attrs.get("width"));
				height = (app.getDataParamHeight() > 0 && !app.getUseFullGui()) ? app.getDataParamHeight() : Integer.parseInt(attrs.get("height"));
			} else {
				width = app.getAppCanvasWidth();
				height = app.getAppCanvasHeight();
			}
			ev.setPreferredSize(geogebra.common.factories.AwtFactory.prototype
					.newDimension(width, height));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSpreadsheetSize(LinkedHashMap<String, String> attrs) {
		if (app.isApplet())
			return true;

		try {
			int width = Integer.parseInt(attrs.get("width"));
			int height = Integer.parseInt(attrs.get("height"));
			app.getSettings()
					.getSpreadsheet()
					.setPreferredSize(
							geogebra.common.factories.AwtFactory.prototype
									.newDimension(width, height));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSpreadsheetColumn(LinkedHashMap<String, String> attrs) {

		try {
			int col = Integer.parseInt(attrs.get("id"));
			int width = Integer.parseInt(attrs.get("width"));
			app.getSettings().getSpreadsheet().addWidth(col, width);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSpreadsheetCellSize(
			LinkedHashMap<String, String> attrs) {

		try {
			int width = Integer.parseInt(attrs.get("width"));
			int height = Integer.parseInt(attrs.get("height"));
			app.getSettings().getSpreadsheet().setPreferredColumnWidth(width);
			app.getSettings().getSpreadsheet().setPreferredRowHeight(height);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSpreadsheetFormat(LinkedHashMap<String, String> attrs) {

		try {
			String cellFormat = attrs.get("formatMap");
			app.getSettings().getSpreadsheet().setCellFormat(cellFormat);
			return true;

		} catch (Exception e) {
			App.printStacktrace(e.getMessage());
			return false;
		}
	}

	private boolean handleSpreadsheetRow(LinkedHashMap<String, String> attrs) {

		try {
			int row = Integer.parseInt(attrs.get("id"));
			int height = Integer.parseInt(attrs.get("height"));
			app.getSettings().getSpreadsheet().addHeight(row, height);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSpreadsheetLayout(LinkedHashMap<String, String> attrs) {

		SpreadsheetSettings settings = app.getSettings().getSpreadsheet();
		try {
			settings.setShowFormulaBar(parseBoolean(attrs.get("showFormulaBar")));
			settings.setShowGrid(parseBoolean(attrs.get("showGrid")));
			settings.setShowFileBrowser(parseBoolean(attrs
					.get("showBrowserPanel")));
			settings.setShowColumnHeader(parseBoolean(attrs
					.get("showColumnHeader")));
			settings.setShowRowHeader(parseBoolean(attrs.get("showRowHeader")));
			settings.setShowHScrollBar(parseBoolean(attrs.get("showHScrollBar")));
			settings.setShowVScrollBar(parseBoolean(attrs.get("showVScrollBar")));
			settings.setAllowSpecialEditor(parseBoolean(attrs
					.get("allowSpecialEditor")));
			settings.setAllowToolTips(parseBoolean(attrs.get("allowToolTips")));
			settings.setEqualsRequired(parseBoolean(attrs.get("equalsRequired")));
			settings.setEnableAutoComplete(parseBoolean(attrs.get("autoComplete")));
			return true;

		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSpreadsheetInitalSelection(
			LinkedHashMap<String, String> attrs) {

		SpreadsheetSettings settings = app.getSettings().getSpreadsheet();
		try {

			int hScroll = Integer.parseInt(attrs.get("hScroll"));
			int vScroll = Integer.parseInt(attrs.get("vScroll"));
			settings.setScrollPosition(new geogebra.common.awt.GPoint(hScroll,
					vScroll));

			int row = Integer.parseInt(attrs.get("row"));
			int column = Integer.parseInt(attrs.get("column"));
			settings.setScrollPosition(new geogebra.common.awt.GPoint(row,
					column));

			return true;

		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSpreadsheetBrowser(LinkedHashMap<String, String> attrs) {

		SpreadsheetSettings settings = app.getSettings().getSpreadsheet();
		try {
			if (Boolean.parseBoolean(attrs.get("default"))) {
				settings.setDefaultBrowser(true);

			} else {
				settings.setInitialBrowserMode(Integer.parseInt(attrs
						.get("mode")));
				settings.setInitialFilePath(attrs.get("dir"));
				settings.setInitialURL(attrs.get("URL"));
			}

			return true;

		} catch (Exception e) {
			return false;
		}
	}

	// private boolean handleCASSize(CasManager casView, LinkedHashMap<String,
	// String> attrs) {
	// if (app.isApplet())
	// return true;
	//
	// try {
	// int width = Integer.parseInt((String) attrs.get("width"));
	// int height = Integer.parseInt((String) attrs.get("height"));
	//
	// // it seems that this statement does not work, because now cas use
	// // its own frame. --Quan Yuan
	// ((JComponent) app.getCasView()).setPreferredSize(new Dimension(
	// width, height));
	// return true;
	// } catch (Exception e) {
	// e.printStackTrace();
	// return false;
	// }
	// }

	private static boolean handleBgColor(EuclidianSettings evSet,
			LinkedHashMap<String, String> attrs) {
		geogebra.common.awt.GColor col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		evSet.setBackground(col);
		return true;
	}

	private static boolean handleAxesColor(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		geogebra.common.awt.GColor col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		ev.setAxesColor(col);
		return true;
	}

	private static boolean handleGridColor(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		geogebra.common.awt.GColor col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		ev.setGridColor(col);
		return true;
	}

	private static boolean handleLineStyle(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		try {
			ev.setAxesLineStyle(Integer.parseInt(attrs.get("axes")));
			ev.setGridLineStyle(Integer.parseInt(attrs.get("grid")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static boolean handleGrid(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		// <grid distX="2.0" distY="4.0"/>
		try {
			double[] dists = new double[3];
			dists[0] = StringUtil.parseDouble(attrs.get("distX"));
			dists[1] = StringUtil.parseDouble(attrs.get("distY"));

			// in v4.0 the polar grid adds an angle step element to
			// gridDistances
			String theta = attrs.get("distTheta");
			if (theta != null)
				dists[2] = StringUtil.parseDouble(attrs.get("distTheta"));
			else
				dists[2] = Math.PI / 6; // default

			ev.setGridDistances(dists);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * <axis id="0" label="x" unitLabel="x" showNumbers="true"
	 * tickDistance="2"/>
	 * 
	 * @param ev settings
	 * @param attrs attributes of &lt;axis> tag
	 * @return true iff succesful
	 */
	protected boolean handleAxis(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {

		try {
			int axis = Integer.parseInt(attrs.get("id"));
			String strShowAxis = attrs.get("show");
			String label = attrs.get("label");
			String unitLabel = attrs.get("unitLabel");
			boolean showNumbers = parseBoolean(attrs.get("showNumbers"));

			// show this axis
			if (strShowAxis != null) {
				boolean showAxis = parseBoolean(strShowAxis);
				ev.setShowAxis(axis, showAxis);
			}

			// set label
			ev.setAxisLabel(axis, label);
			/*
			 * if (label != null && label.length() > 0) { String[] labels =
			 * ev.getAxesLabels(); labels[axis] = label;
			 * ev.setAxesLabels(labels); }
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
			 * boolean showNums[] = ev.getShowAxesNumbers(); showNums[axis] =
			 * showNumbers; ev.setShowAxesNumbers(showNums);
			 */

			// check if tickDistance is given
			String strTickDist = attrs.get("tickDistance");
			if (strTickDist != null) {
				double tickDist = StringUtil.parseDouble(strTickDist);
				ev.setAxesNumberingDistance(tickDist, axis);
			}

			// tick style
			String strTickStyle = attrs.get("tickStyle");
			if (strTickStyle != null) {
				int tickStyle = Integer.parseInt(strTickStyle);
				// ev.getAxesTickStyles()[axis] = tickStyle;
				ev.setAxisTickStyle(axis, tickStyle);
			} else {
				// before v3.0 the default tickStyle was MAJOR_MINOR
				// ev.getAxesTickStyles()[axis] =
				// EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR_MINOR;
				ev.setAxisTickStyle(axis,
						EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR_MINOR);
			}

			// axis crossing
			String axisCross = attrs.get("axisCross");
			String axisCrossEdge = attrs.get("axisCrossEdge");
			boolean acb = false;
			if (axisCrossEdge != null) {
				acb = parseBoolean(axisCrossEdge);
			}
			if (acb) {
				ev.setAxisCross(axis, 0);
				ev.setDrawBorderAxes(axis, true);
			} else if (axisCross != null) {
				double ac = StringUtil.parseDouble(axisCross);
				ev.setAxisCross(axis, ac);
				ev.setDrawBorderAxes(axis, false);
			} else {
				ev.setAxisCross(axis, 0);
				ev.setDrawBorderAxes(axis, false);
			}

			// positive direction only
			String posAxis = attrs.get("positiveAxis");
			if (posAxis != null) {
				boolean isPositive = Boolean.parseBoolean(posAxis);
				ev.setPositiveAxis(axis, isPositive);
			}

			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}
	}

	// ====================================
	// <kernel>
	// ====================================
	private void startKernelElement(String eName,
			LinkedHashMap<String, String> attrs) {
		if ("angleUnit".equals(eName)) {
			handleAngleUnit(attrs);
		} else if ("algebraStyle".equals(eName)) { // G.Sturr 2009-10-18
			handleAlgebraStyle(attrs);
		} else if ("coordStyle".equals(eName)) {
			handleKernelCoordStyle(attrs);
		} else if ("angleFromInvTrig".equals(eName)) {
			handleKernelInvTrig(attrs);
		} else if ("continuous".equals(eName)) {
			handleKernelContinuous(attrs);
		} else if ("usePathAndRegionParameters".equals(eName)) {
			handleKernelUsePathAndRegionParameters(attrs);
		} else if ("decimals".equals(eName)) {
			handleKernelDecimals(attrs);
		} else if ("significantfigures".equals(eName)) {
			handleKernelFigures(attrs);
		} else if ("startAnimation".equals(eName)) {
			handleKernelStartAnimation(attrs);
		} else if ("localization".equals(eName)) {
			handleKernelLocalization(attrs);
		} else if ("casSettings".equals(eName)) {
			handleCasSettings(attrs);
		} else
			System.err.println("unknown tag in <kernel>: " + eName);
	}

	private boolean handleAngleUnit(LinkedHashMap<String, String> attrs) {
		if (attrs == null)
			return false;
		String angleUnit = attrs.get("val");
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
			kernel.setCoordStyle(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKernelInvTrig(LinkedHashMap<String, String> attrs) {
		try {
			kernel.setInverseTrigReturnsAngle(parseBoolean(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKernelDecimals(LinkedHashMap<String, String> attrs) {
		try {
			kernel.setPrintDecimals(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKernelStartAnimation(
			LinkedHashMap<String, String> attrs) {
		try {
			startAnimation = parseBoolean(attrs.get("val"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKernelLocalization(LinkedHashMap<String, String> attrs) {
		try {
			boolean digits = parseBoolean(attrs.get("digits"));
			app.setUseLocalizedDigits(digits);
			boolean labels = parseBoolean(attrs.get("labels"));
			app.setUseLocalizedLabels(labels);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Handle the casSettings XML element which is responsible for setting the Options CAS dialog
	 * @param attrs - mapping of attributes names and values 
	 * @return whether the operation was successful
	 */
	private boolean handleCasSettings(LinkedHashMap<String, String> attrs) {
		try {
			boolean expRoots = parseBoolean(attrs.get("expRoots"));
			app.getSettings().getCasSettings().setShowExpAsRoots(expRoots);
			int timeout = Integer.parseInt(attrs.get("timeout"));
			if (timeout > 0)
				app.getSettings().getCasSettings().setTimeoutMilliseconds(
					getTimeoutOption(timeout) * 1000);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKernelFigures(LinkedHashMap<String, String> attrs) {
		try {
			kernel.setPrintFigures(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKernelContinuous(LinkedHashMap<String, String> attrs) {
		try {
			kernel.setContinuous(parseBoolean(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKernelUsePathAndRegionParameters(
			LinkedHashMap<String, String> attrs) {
		try {
			kernel.setUsePathAndRegionParameters(PathRegionHandling.parse(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// ====================================
	// <gui>
	// ====================================
	private void startGuiElement(String eName,
			LinkedHashMap<String, String> attrs) {
		boolean ok = true;
		switch (firstChar(eName)) {
		case 'c':
			if ("consProtColumns".equals(eName))
				ok = handleConsProtColumns(app, attrs);
			else if ("consProtocol".equals(eName))
				ok = handleConsProtocol(attrs);
			else if ("consProtNavigationBar".equals(eName))
				ok = handleConsProtNavigationBar(app, attrs);
			break;

		case 'f':
			if ("font".equals(eName))
				ok = handleFont(app, attrs);
			break;

		case 'g':
			if ("graphicsSettings".equals(eName))
				ok = handleGraphicsSettings(attrs);
			break;

		case 'm':
			if ("menuFont".equals(eName))
				ok = handleMenuFont(app, attrs);
			else if ("mouse".equals(eName))
				ok = handleMouse(app, attrs);
			break;

		case 'l':
			if ("labelingStyle".equals(eName))
				ok = handleLabelingStyle(app, attrs);
			break;

		case 'p':
			if ("perspectives".equals(eName)) {
				mode = MODE_GUI_PERSPECTIVES;
				tmp_perspectives.clear();
			}
			break;

		case 's':
			if ("show".equals(eName))
				ok = handleGuiShow(app, attrs);
			else if ("splitDivider".equals(eName))
				ok = handleSplitDivider(attrs);
			else if ("settings".equals(eName))
				ok = handleGuiSettings(app, attrs);
			break;

		case 't':
			if ("toolbar".equals(eName))
				ok = handleToolbar(attrs);
			else if ("tooltipSettings".equals(eName))
				ok = handleTooltipSettings(app, attrs);
			break;

		case 'w':
			if ("window".equals(eName))
				ok = handleWindowSize(app, attrs);
			break;

		default:
			App.error("unknown tag in <gui>: " + eName);
		}

		if (!ok)
			App.error("error in <gui>: " + eName);
	}

	/**
	 * Take care of backward compatibility for the dynamic layout component
	 */
	private void createCompabilityLayout() {
		int splitOrientation = tmp_spHorizontal ? JSplitPane_HORIZONTAL_SPLIT
				: JSplitPane_VERTICAL_SPLIT;

		String defEV, defSV, defAV;

		// we have to create the definitions for the single views manually to
		// prevent nullpointers
		if (splitOrientation == JSplitPane_HORIZONTAL_SPLIT) {
			if (tmp_showSpreadsheet && tmp_showAlgebra) {
				defEV = "1,3";
				defSV = "1,1";
				defAV = "3";
			} else {
				if (tmp_showSpreadsheet) {
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
			if (tmp_showSpreadsheet && tmp_showAlgebra) {
				defEV = "0";
				defAV = "2,0";
				defSV = "2,2";
			} else {
				if (tmp_showSpreadsheet) {
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

		// construct default xml data in case we're using an old version which
		// didn't
		// store the layout xml.
		DockPanelData[] dpXml = new DockPanelData[] {
				new DockPanelData(App.VIEW_EUCLIDIAN, null,
						true, false, false,
						geogebra.common.factories.AwtFactory.prototype
								.newRectangle(400, 400), defEV, 200),
				new DockPanelData(App.VIEW_ALGEBRA, null,
						tmp_showAlgebra, false, false,
						geogebra.common.factories.AwtFactory.prototype
								.newRectangle(200, 400), defAV, 200),
				new DockPanelData(App.VIEW_SPREADSHEET, null,
						tmp_showSpreadsheet, false, false,
						geogebra.common.factories.AwtFactory.prototype
								.newRectangle(400, 400), defSV, 200) };
		tmp_perspective.setDockPanelData(dpXml);
		tmp_perspective.setShowToolBar(true);

		geogebra.common.awt.GDimension evSize = app.getSettings()
				.getEuclidian(1).getPreferredSize();

		// calculate window dimensions
		int width = evSize.getWidth();
		int height = evSize.getHeight();

		// minimal size for documents, necessary for GeoGebra < 3
		if (width <= 100 || height <= 100) {
			width = 600;
			height = 440;
		}

		if (splitOrientation == JSplitPane_HORIZONTAL_SPLIT) {
			if (tmp_showSpreadsheet) {
				width += 5 + app.getSettings().getSpreadsheet().preferredSize()
						.getWidth();
			}

			if (tmp_showAlgebra) {
				width += 5 + tmp_sp2;
			}
		} else {
			if (tmp_showSpreadsheet) {
				height += 5 + app.getSettings().getSpreadsheet()
						.preferredSize().getHeight();
			}
			if (tmp_showAlgebra) {
				height += 5 + tmp_sp2;
			}
		}

		DockSplitPaneData[] spXml;

		// use two split panes in case all three views are visible
		if (tmp_showSpreadsheet && tmp_showAlgebra) {
			int total = (splitOrientation == JSplitPane_HORIZONTAL_SPLIT ? width
					: height);
			float relative1 = (float) tmp_sp2 / total;
			float relative2 = (float) tmp_sp1 / (total - tmp_sp2);
			spXml = new DockSplitPaneData[] {
					new DockSplitPaneData("", relative1, splitOrientation),
					new DockSplitPaneData(
							(splitOrientation == JSplitPane_HORIZONTAL_SPLIT ? "1"
									: "2"), relative2, splitOrientation) };
		} else {
			int total = (splitOrientation == JSplitPane_HORIZONTAL_SPLIT ? width
					: height);
			float relative;
			if (tmp_showSpreadsheet) {
				relative = (float) tmp_sp1 / total;
			} else {
				relative = (float) tmp_sp2 / total;
			}
			spXml = new DockSplitPaneData[] { new DockSplitPaneData("",
					relative, splitOrientation) };
		}

		// additional space for toolbar and others, we add this here
		// as it shouldn't influence the relative positions of the
		// split pane dividers above
		width += 15;
		height += 90;

		if (tmp_perspective.getShowInputPanel()) {
			height += 50;
		}

		tmp_perspective.setSplitPaneData(spXml);

		tmp_perspectives = new ArrayList<Perspective>();
		tmp_perspectives.add(tmp_perspective);
		app.setPreferredSize(geogebra.common.factories.AwtFactory.prototype
				.newDimension(width, height));
		app.setTmpPerspectives(tmp_perspectives);
	}

	private static boolean handleConsProtColumns(App app,
			LinkedHashMap<String, String> attrs) {
		try {

			/*
			 * TODO: iterator() function caused error in GWT. Why?
			 * 
			 * //Iterator<String> it = attrs.keySet().iterator(); Set<String>
			 * temp = attrs.keySet(); Iterator<String> it = temp.iterator();
			 * 
			 * int colCounter = 0; boolean[] colsVis = new
			 * boolean[attrs.keySet().size()]; while (it.hasNext()) { Object ob
			 * = attrs.get(it.next()); boolean isVisible = parseBoolean((String)
			 * ob); colsVis[colCounter++] = isVisible; }
			 */

			boolean[] colsVis = new boolean[attrs.keySet().size()];

			ArrayList<String> keys = new ArrayList<String>(attrs.keySet());
			for (String key : keys) {
				int k = Integer.parseInt(key.substring(3));
				colsVis[k] = Boolean.parseBoolean(attrs.get(key));

			}

			ConstructionProtocolSettings cpSettings = app.getSettings()
					.getConstructionProtocol();
			cpSettings.setColsVisibility(colsVis);

			return true;
		} catch (Exception e) {
			App.debug(e);
			return false;
		}
	}

	private boolean handleConsProtocol(LinkedHashMap<String, String> attrs) {
		try {
			// boolean useColors = parseBoolean((String)
			// attrs.get("useColors"));
			// TODO: set useColors for consProt

			boolean showOnlyBreakpoints = parseBoolean(attrs
					.get("showOnlyBreakpoints"));
			kernel.getConstruction().setShowOnlyBreakpoints(showOnlyBreakpoints);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleConsProtNavigationBar(App app1,
			LinkedHashMap<String, String> attrs) {
		try {
			boolean show = parseBoolean(attrs.get("show"));
			boolean playButton = parseBoolean(attrs.get("playButton"));
			double playDelay = StringUtil.parseDouble(attrs.get("playDelay"));
			boolean showProtButton = parseBoolean(attrs.get("protButton"));

			// Maybe there is not guiManager yet. In this case we store the
			// navigation bar's states in ConstructionProtocolSettings
			app1.setShowConstructionProtocolNavigation(show, playButton,
					playDelay, showProtButton);

			// construction step: handled at end of parsing
			String strConsStep = attrs.get("consStep");
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
	 * @param app1
	 * @param attrs
	 * @return
	 */
	private boolean handleGuiShow(App app1,
			LinkedHashMap<String, String> attrs) {
		try {
			// backward compatibility to versions without the layout component
			if (ggbFileFormat < 3.3) {
				tmp_showAlgebra = parseBoolean(attrs.get("algebraView"));

				// Michael Borcherds 2008-04-25
				tmp_showSpreadsheet = parseBoolean(attrs.get("spreadsheetView"));
			}

			String str = attrs.get("auxiliaryObjects");
			boolean auxiliaryObjects = (str != null && str.equals("true"));
			app1.setShowAuxiliaryObjects(auxiliaryObjects);

			str = attrs.get("algebraInput");
			boolean algebraInput = (str == null || str.equals("true"));
			tmp_perspective.setShowInputPanel(algebraInput);

			str = attrs.get("cmdList");
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
	private static boolean handleGuiSettings(App app,
			LinkedHashMap<String, String> attrs) {
		try {
			boolean ignoreDocument = !attrs.get("ignoreDocument").equals(
					"false");
			app.getSettings().getLayout()
					.setIgnoreDocumentLayout(ignoreDocument);

			boolean showTitleBar = !attrs.get("showTitleBar").equals("false");
			app.getSettings().getLayout().setShowTitleBar(showTitleBar);

			if (attrs.containsKey("allowStyleBar")) {
				boolean allowStyleBar = !attrs.get("allowStyleBar").equals(
						"false");
				app.getSettings().getLayout().setAllowStyleBar(allowStyleBar);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			App.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	private boolean handleGraphicsSettings(LinkedHashMap<String, String> attrs) {
		try {
			if ("true".equals(attrs.get("javaLatexFonts")))
				app.getDrawEquation().setUseJavaFontsForLaTeX(app, true);
			else if ("false".equals(attrs.get("javaLatexFonts")))
				app.getDrawEquation().setUseJavaFontsForLaTeX(app, false);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			App.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	/**
	 * Kept for backward compatibility with version < 3.3
	 * 
	 * @param attrs
	 * @return
	 */
	private boolean handleSplitDivider(LinkedHashMap<String, String> attrs) {
		try {
			tmp_spHorizontal = !"false".equals(attrs.get("horizontal"));

			// There were just two panels in GeoGebra < 3.2, therefore just one
			// split divider position
			// may be given. 'loc' in < 3.2 corresponds to 'loc2' in 3.2+.
			if (attrs.get("loc2") == null) {
				attrs.put("loc2", attrs.get("loc"));
				attrs.put("loc", "0"); // prevent NP exception in
										// Integer.parseInt()
			}

			if (tmp_spHorizontal) {
				tmp_sp1 = Integer.parseInt(attrs.get("loc"));
				tmp_sp2 = Integer.parseInt(attrs.get("loc2"));
			} else {
				String strLocVert = attrs.get("locVertical");
				if (strLocVert != null) {
					tmp_sp1 = Integer.parseInt(strLocVert);
				} else {
					tmp_sp1 = Integer.parseInt(attrs.get("loc"));
				}

				String strLocVert2 = attrs.get("locVertical2");
				if (strLocVert2 != null) {
					tmp_sp2 = Integer.parseInt(strLocVert2);
				} else {
					tmp_sp2 = Integer.parseInt(attrs.get("loc2"));
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleToolbar(LinkedHashMap<String, String> attrs) {
		try {
			String toolbarStr = attrs.get("str");
			if (toolbarStr != null) {
				// GeoGebra 3.2 or older

				// substitute 1000+x to 100000+x (macro numbers)
				// this seems to be easier with a loop
				boolean afterspace = true;
				int thousande = 0;
				StringBuilder addendum = new StringBuilder();
				StringBuilder converted = new StringBuilder();
				for (int lv = 0; lv < toolbarStr.length(); lv++) {
					if (toolbarStr.charAt(lv) == ' ') {
						afterspace = true;
						thousande = 0;
						addendum = new StringBuilder();
						converted.append(toolbarStr.charAt(lv));
					} else if (afterspace) {
						if (thousande == 0) {
							if (toolbarStr.charAt(lv) == '1') {
								thousande++;
								addendum = new StringBuilder();
								addendum.append(toolbarStr.charAt(lv));
							} else {
								afterspace = false;
								thousande = 0;
								converted.append(toolbarStr.charAt(lv));
							}
						} else if (thousande < 4) {
							if (StringUtil.isDigit(toolbarStr.charAt(lv))) {
								thousande++;
								addendum.append(toolbarStr.charAt(lv));
							} else {
								thousande = 0;
								afterspace = false;
								converted.append(addendum);
								converted.append(toolbarStr.charAt(lv));
							}
						}
						if (thousande == 4) {
							if (lv + 1 == toolbarStr.length()) {
								converted.append("100"
										+ addendum.toString().substring(1));
							} else if (!StringUtil.isDigit(toolbarStr
									.charAt(lv + 1))) {
								converted.append("100"
										+ addendum.toString().substring(1));
							} else {
								converted.append(addendum);
							}
							afterspace = false;
							thousande = 0;
						}
					} else {
						converted.append(toolbarStr.charAt(lv));
					}
				}
				toolbarStr = converted.toString();

				tmp_perspective.setShowToolBar(true);
				tmp_perspective.setToolbarDefinition(toolbarStr);
		
			} else { 
				// GeoGebra 4.0
				String showToolBar = attrs.get("show");
				if (showToolBar == null) {
					tmp_perspective.setShowToolBar(true);
				} else {
					tmp_perspective.setShowToolBar(showToolBar.equals("true"));
				}
				tmp_perspective.setToolbarDefinition(attrs.get("items"));
				
				// GeoGebra 4.2 (supports toolbar position and toggling help)
				if(attrs.get("position") != null){			
					Integer toolBarPosition = Integer.parseInt(attrs.get("position"));
					tmp_perspective.setToolBarPosition(toolBarPosition);
					String showToolBarHelp = attrs.get("show");
					tmp_perspective.setShowToolBarHelp(!attrs.get("help")
							.equals("false"));
				}else{
					tmp_perspective.setToolBarPosition(SwingConstants.NORTH);
					tmp_perspective.setShowToolBarHelp(true);
				}

			}
			return true;
		} catch (Exception e) {
			App.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	/**
	 * Handle the window size: <window width=".." height=".." />
	 * 
	 * @param app
	 * @param attrs
	 * @return
	 */
	private static boolean handleWindowSize(App app,
			LinkedHashMap<String, String> attrs) {
		try {
			geogebra.common.awt.GDimension size = geogebra.common.factories.AwtFactory.prototype
					.newDimension(Integer.parseInt(attrs.get("width")),
							Integer.parseInt(attrs.get("height")));
			app.setPreferredSize(size);
			return true;
		} catch (Exception e) {
			App.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	private static boolean handleFont(App app,
			LinkedHashMap<String, String> attrs) {
		try {
			int guiSize = Integer.parseInt(attrs.get("size"));

			// old versions do just have a single font size and derive the font
			// size for
			// the axes / euclidian view from this single size
			// if(ggbFileFormat < 3.3) {
			// app.setFontSize(guiSize, false);
			// app.setAxesFontSize(guiSize - 2, false); // always 2 points
			// smaller than the default size
			// } else {
			// int axesSize = Integer.parseInt((String) attrs.get("axesSize"));
			// app.setAxesFontSize(axesSize, false);
			//
			// int euclidianSize = Integer.parseInt((String)
			// attrs.get("euclidianSize"));
			// app.setEuclidianFontSize(euclidianSize, false);
			// }

			app.setFontSize(guiSize); // set gui font size and update all fonts
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static boolean handleMenuFont(App app,
			LinkedHashMap<String, String> attrs) {
		try {
			int guiSize = Integer.parseInt(attrs.get("size"));
			if (guiSize <= 0) {
				app.setGUIFontSize(-1); // default
			} else {
				for (int i = 0; i < menuFontSizes.length; i++) {
					if (menuFontSizes[i] >= guiSize) {
						guiSize = menuFontSizes[i];
						break;
					}
				}
				if (guiSize > menuFontSizes[menuFontSizes.length - 1])
					guiSize = menuFontSizes[menuFontSizes.length - 1];
				app.setGUIFontSize(guiSize);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static boolean handleTooltipSettings(App app,
			LinkedHashMap<String, String> attrs) {
		try {
			String ttl = attrs.get("language");
			if ("".equals(ttl)) {
				app.setTooltipLanguage(null);
			} else if (ttl != null) {
				app.setTooltipLanguage(ttl);
			}
			int ttt = -1;
			try { // "off" will be -1
				ttt = Integer.parseInt(attrs.get("timeout"));
			} catch (NumberFormatException e) {
				//not a number, do nothing (use -1)
			}
			app.setTooltipTimeout(ttt);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static boolean handleMouse(App app,
			LinkedHashMap<String, String> attrs) {
			app.reverseMouseWheel("true".equals(attrs.get("reverseWheel")));
			
			return true;

	}

	private static boolean handleLabelingStyle(App app,
			LinkedHashMap<String, String> attrs) {
		try {
			int style = Integer.parseInt(attrs.get("val"));
			app.setLabelingStyle(style);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// ====================================
	// <perspectives>
	// ====================================
	private void startGuiPerspectivesElement(String eName,
			LinkedHashMap<String, String> attrs) {
		boolean ok = true;

		if ("perspective".equals(eName))
			ok = handlePerspective(attrs);
		else
			App
					.debug("unknown tag in <perspectives>: " + eName);

		if (!ok)
			App.debug("error in <perspectives>: " + eName);
	}

	/**
	 * Create a new temporary perspective for the current <perspective> element
	 * 
	 * @param attrs
	 * @return
	 */
	private boolean handlePerspective(LinkedHashMap<String, String> attrs) {
		try {
			tmp_perspective = new Perspective(attrs.get("id"));
			tmp_perspectives.add(tmp_perspective);

			if (tmp_panes == null) {
				tmp_panes = new ArrayList<DockSplitPaneData>();
			} else {
				tmp_panes.clear();
			}

			if (tmp_views == null) {
				tmp_views = new ArrayList<DockPanelData>();
			} else {
				tmp_views.clear();
			}
			mode = MODE_GUI_PERSPECTIVE;

			return true;
		} catch (Exception e) {
			App.debug(e.getMessage() + ": " + e.getCause());
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
	private void startGuiPerspectiveElement(String eName,
			LinkedHashMap<String, String> attrs) {
		boolean ok = true;

		switch (firstChar(eName)) {
		case 'd':
			if ("dockBar".equals(eName)) {
				ok = handleDockBar(attrs);
				break;
			}
			
		case 'i':
			if ("input".equals(eName)) {
				ok = handleAlgebraInput(attrs);
				break;
			}

		case 'p':
			if ("panes".equals(eName)) {
				mode = MODE_GUI_PERSPECTIVE_PANES;
				break;
			}

		case 's':
			if ("show".equals(eName)) {
				ok = handleGuiShow(app, attrs);
				break;
			}

		case 't':
			if ("toolbar".equals(eName)) {
				ok = handleToolbar(attrs);
				break;
			}

		case 'v':
			if ("views".equals(eName)) {
				mode = MODE_GUI_PERSPECTIVE_VIEWS;
				break;
			}

		default:
			App.debug("unknown tag in <perspective>: " + eName);
		}

		if (!ok)
			App.debug("error in <perspective>: " + eName);
	}

	private boolean handleAlgebraInput(LinkedHashMap<String, String> attrs) {
		try {
			tmp_perspective.setShowInputPanel(!attrs.get("show")
					.equals("false"));
			tmp_perspective.setShowInputPanelCommands(!attrs.get("cmd").equals(
					"false"));
			tmp_perspective.setShowInputPanelOnTop(!attrs.get("top").equals(
					"false"));

			return true;
		} catch (Exception e) {
			App.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	private boolean handleDockBar(LinkedHashMap<String, String> attrs) {
		try {
			tmp_perspective.setShowDockBar(!attrs.get("show")
					.equals("false"));
			tmp_perspective.setDockBarEast(!attrs.get("east").equals(
					"false"));

			return true;
		} catch (Exception e) {
			App.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}
	
	private void endGuiPerspectiveElement() {
		DockPanelData[] dpInfo = new DockPanelData[tmp_views.size()];
		DockSplitPaneData[] spInfo = new DockSplitPaneData[tmp_panes.size()];
		tmp_perspective.setDockPanelData(tmp_views.toArray(dpInfo));
		tmp_perspective.setSplitPaneData(tmp_panes.toArray(spInfo));
	}

	// ====================================
	// <views>
	// ====================================
	private void startGuiViewsElement(String eName,
			LinkedHashMap<String, String> attrs) {
		boolean ok = true;

		if ("view".equals(eName))
			ok = handleView(attrs);
		else
			App.debug("unknown tag in <views>: " + eName);

		if (!ok)
			App.debug("error in <views>: " + eName);
	}

	/**
	 * Handle a view. <view id=".." visible=".." inframe=".." stylebar=".."
	 * window=".." location=".." size=".." />
	 * 
	 * @param attrs
	 * @return
	 */
	private boolean handleView(LinkedHashMap<String, String> attrs) {
		try {
			int viewId = Integer.parseInt(attrs.get("id"));
			String toolbar = attrs.get("toolbar");
			boolean isVisible = !attrs.get("visible").equals("false");
			boolean openInFrame = !attrs.get("inframe").equals("false");

			String showStyleBarStr = attrs.get("stylebar");
			boolean showStyleBar = (showStyleBarStr != null ? !showStyleBarStr
					.equals("false") : false);

			// the window rectangle is given in the format "x,y,width,height"
			String[] window = attrs.get("window").split(",");
			geogebra.common.awt.GRectangle windowRect = geogebra.common.factories.AwtFactory.prototype
					.newRectangle(Integer.parseInt(window[0]),
							Integer.parseInt(window[1]),
							Integer.parseInt(window[2]),
							Integer.parseInt(window[3]));

			String embeddedDef = attrs.get("location");
			int embeddedSize = Integer.parseInt(attrs.get("size"));
			
			String plane = attrs.get("plane");

			tmp_views.add(new DockPanelData(viewId, toolbar, isVisible,
					openInFrame, showStyleBar, windowRect, embeddedDef,
					embeddedSize, plane));

			return true;
		} catch (Exception e) {
			App.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	// ====================================
	// <panes>
	// ====================================
	private void startGuiPanesElement(String eName,
			LinkedHashMap<String, String> attrs) {
		boolean ok = true;

		if ("pane".equals(eName))
			ok = handlePane(attrs);
		else
			App.debug("unknown tag in <panes>: " + eName);

		if (!ok)
			App.debug("error in <panes>: " + eName);
	}

	/**
	 * Handle a pane. <pane location".." divider=".." orientation=".." />
	 * 
	 * @param attrs
	 * @return
	 */
	private boolean handlePane(LinkedHashMap<String, String> attrs) {
		try {
			String location = attrs.get("location");
			double dividerLocation = StringUtil.parseDouble(attrs.get("divider"));
			int orientation = Integer.parseInt(attrs.get("orientation"));

			tmp_panes.add(new DockSplitPaneData(location, dividerLocation,
					orientation));

			return true;
		} catch (Exception e) {
			App.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	// ====================================
	// <construction>
	// ====================================
	private void handleConstruction(LinkedHashMap<String, String> attrs) {
		try {
			String title = attrs.get("title");
			String author = attrs.get("author");
			String date = attrs.get("date");
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
			String cmdName = attrs.get("cmdName");
			String toolName = attrs.get("toolName");
			String toolHelp = attrs.get("toolHelp");
			String iconFile = attrs.get("iconFile");
			boolean copyCaptions = parseBoolean(attrs.get("copyCaptions"));
			String strShowInToolBar = attrs.get("showInToolBar");

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

			MacroKernel macroKernel = kernel.newMacroKernel();
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
		String above = attrs.get("above");
		String below = attrs.get("below");
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
			if ("cellPair".equals(eName)) {
				casMode = MODE_CAS_CELL_PAIR;
				startCellPair();
			} else {
				System.err.println("unknown tag in <cellPair>: " + eName);
			}
			break;

		case MODE_CAS_CELL_PAIR:
			if ("inputCell".equals(eName)) {
				casMode = MODE_CAS_INPUT_CELL;
			} else if ("outputCell".equals(eName)) {
				casMode = MODE_CAS_OUTPUT_CELL;
			} else if ("useAsText".equals(eName)) {
				casMode = MODE_CAS_TEXT_CELL;
			} else {
				System.err.println("unknown tag in <cellPair>: " + eName);
			}
			break;

		case MODE_CAS_TEXT_CELL:
			startCellTextElement(eName, attrs);
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
			if ("cascell".equals(eName)) {
				mode = MODE_CONSTRUCTION;
				constMode = MODE_CONSTRUCTION;
				casMode = MODE_CONST_CAS_CELL;
				geoCasCell = null;
			}
			break;

		case MODE_CAS_CELL_PAIR:
			if ("cellPair".equals(eName)) {
				casMode = MODE_CONST_CAS_CELL;
				endCellPair(eName);
			}
			break;

		case MODE_CAS_TEXT_CELL:
			if ("useAsText".equals(eName))
				casMode = MODE_CAS_CELL_PAIR;
			break;
			
		case MODE_CAS_INPUT_CELL:
			if ("inputCell".equals(eName))
				casMode = MODE_CAS_CELL_PAIR;
			break;

		case MODE_CAS_OUTPUT_CELL:
			if ("outputCell".equals(eName))
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
				// free cell, e.g. m := 7 creates twinGeo m = 7
				cons.addToConstructionList(geoCasCell, true);
				if (geoCasCell.isAssignmentVariableDefined()) {
					// make sure assignment is sent to underlying CAS, e.g. f(x)
					// := x^2
					// and twinGeo is created
					geoCasCell.computeOutput();
					// a non-native cell may have dependant twin geo even if inputs are constants
					geoCasCell.setLabelOfTwinGeo();
					if(geoCasCell.hasTwinGeo() && !geoCasCell.getTwinGeo().isInConstructionList()){
						if(!geoCasCell.getTwinGeo().getParentAlgorithm().isInConstructionList())
							geoCasCell.getTwinGeo().getParentAlgorithm().addToConstructionList();
					}
				}
				// otherwise keep loaded output and avoid unnecessary
				// computation
			} else {
				// create algorithm for dependent cell
				// this also creates twinGeo if necessary
				// output is not computed again, see AlgoDependenCasCell
				// constructor
				KernelCAS.DependentCasCell(geoCasCell);
			}
		} catch (Exception e) {
			System.err.println("error when processing <cellpair>: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	private void startCellOutputElement(String eName,
			LinkedHashMap<String, String> attrs) {
		if (geoCasCell == null) {
			System.err.println("no element set for <" + eName + ">");
			return;
		}

		boolean ok = true;
		switch (firstChar(eName)) {
		case 'e':
			if ("expression".equals(eName)) {
				ok = handleCasCellOutput(attrs);
				break;
			}

		default:
			System.err.println("unknown tag in <outputCell>: " + eName);
		}

		if (!ok)
			System.err.println("error in <outputCell>: " + eName);

	}

	private void startCellInputElement(String eName,
			LinkedHashMap<String, String> attrs) {
		if (geoCasCell == null) {
			System.err.println("no element set for <" + eName + ">");
			return;
		}

		boolean ok = true;
		switch (firstChar(eName)) {
		case 'e':
			if ("expression".equals(eName)) {
				ok = handleCasCellInput(attrs);
				break;
			}

			// case 'c':
			// if ("color".equals(eName)) {
			// ok = handleCASPairColor(attrs);
			// break;
			// }

		default:
			System.err.println("unknown tag in <inputCell>: " + eName);
		}

		if (!ok)
			System.err.println("error in <inputCell>: " + eName);
	}
	
	private void startCellTextElement(String eName,
			LinkedHashMap<String, String> attrs) {
		if (geoCasCell == null) {
			System.err.println("no element set for <" + eName + ">");
			return;
		}

		geoCasCell.setUseAsText(true);
		
		boolean ok = true;

		if ("FontStyle".equals(eName)) {
				String style = attrs.get("value");
				geoCasCell.setFontStyle(Integer.parseInt(style));	
		} else if ("FontSizeM".equals(eName)) {
				String size = attrs.get("value");
				if(StringUtil.parseDouble(size)>0)
					geoCasCell.setFontSizeMultiplier(StringUtil.parseDouble(size));			
		} else if ("FontColor".equals(eName)) {
			String r = attrs.get("r");
			String b = attrs.get("b");
			String g = attrs.get("g");
			geoCasCell.setFontColor(geogebra.common.factories.AwtFactory.prototype.newColor(Integer.parseInt(r),Integer.parseInt(g),Integer.parseInt(b)));			
		} else
			System.err.println("unknown tag in <useAsText>: " + eName);

		if (!ok)
			System.err.println("error in <useAsText>: " + eName);
		
	}

	private void startConstructionElement(String eName,
			LinkedHashMap<String, String> attrs) {
		// handle construction mode

		// Application.debug("constMode = "+constMode+", eName = "+eName);

		switch (constMode) {
		case MODE_CONSTRUCTION:
			if ("element".equals(eName)) {
				cons.setOutputGeo(null);
				constMode = MODE_CONST_GEO_ELEMENT;
				geo = getGeoElement(attrs);
			} else if ("command".equals(eName)) {
				cons.setOutputGeo(null);
				constMode = MODE_CONST_COMMAND;
				cmd = getCommand(attrs);
			} else if ("expression".equals(eName)) {
				startExpressionElement(attrs);
			} else if ("cascell".equals(eName)) {
				constMode = MODE_CONST_CAS_CELL;
				casMode = MODE_CONST_CAS_CELL;
			} else if ("worksheetText".equals(eName)) {
				handleWorksheetText(attrs);
			} else {
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
			if ("construction".equals(eName)) {
				// process start points at end of construction
				processStartPointList();
				processLinkedGeoList();
				processShowObjectConditionList();
				processDynamicColorList();
				processAnimationSpeedList();
				processAnimationStepList();
				processMinMaxList();
				processEvSizes();
				processAnimatingList(); // must be after min/maxList otherwise
										// GeoElement.setAnimating doesn't work
				
				// now called from MyXMLio.doParseXML()
				//if (spreadsheetTraceNeeded) {
				//	// don't want to initialize trace manager unless necessary
				//	app.getTraceManager().loadTraceGeoCollection();
				//}

				if (kernel == origKernel) {
					mode = MODE_GEOGEBRA;
				} else {
					// macro construction
					mode = MODE_MACRO;
				}
			}
			break;

		case MODE_CONST_GEO_ELEMENT:
			if ("element".equals(eName))
				constMode = MODE_CONSTRUCTION;
			break;

		case MODE_CONST_COMMAND:
			if ("command".equals(eName)) {
				cons.setOutputGeo(null);
				constMode = MODE_CONSTRUCTION;
			}
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
		// Set<EuclidianSettings> eSet0 = xmin.keySet();
		ArrayList<EuclidianSettings> eSet = new ArrayList<EuclidianSettings>(
				xmin.keySet());
		for (EuclidianSettings ev : eSet) {
			if (xmin.get(ev) == null) {
				ev.setXminObject(null, true);
			} else {
				NumberValue n = kernel.getAlgebraProcessor().evaluateToNumeric(
						xmin.get(ev), true);
				ev.setXminObject(n, true);
			}
		}
		for (EuclidianSettings ev : eSet) {
			if (xmax.get(ev) == null) {
				ev.setXmaxObject(null, true);
			} else {
				NumberValue n = kernel.getAlgebraProcessor().evaluateToNumeric(
						xmax.get(ev), true);
				ev.setXmaxObject(n, true);
			}
		}
		for (EuclidianSettings ev : eSet) {
			if (ymin.get(ev) == null) {
				ev.setYminObject(null, true);
			} else {
				NumberValue n = kernel.getAlgebraProcessor().evaluateToNumeric(
						ymin.get(ev), true);
				ev.setYminObject(n, true);
			}
		}
		for (EuclidianSettings ev : eSet) {
			if (ymax.get(ev) == null) {
				ev.setYmaxObject(null, true);
			} else {
				NumberValue n = kernel.getAlgebraProcessor().evaluateToNumeric(
						ymax.get(ev), true);
				ev.setYmaxObject(n, true);
			}
			// ev.updateBounds();
		}
	}

	// called when <element> is encountered
	// e.g. for <element type="point" label="P">
	private GeoElement getGeoElement(LinkedHashMap<String, String> attrs) {
		GeoElement geo1 = null;
		String label = attrs.get("label");
		String type = attrs.get("type");
		String defaultset = attrs.get("default");
		if (label == null || type == null) {
			System.err.println("attributes missing in <element>");
			return geo1;
		}

		if (defaultset == null || !kernel.getElementDefaultAllowed()) {
			// does a geo element with this label exist?
			geo1 = kernel.lookupLabel(label);
			// Application.debug(label+", geo="+geo);
			if (geo1 == null) {

				// try to find an algo on which this label depends
				// geo = cons.resolveLabelDependency(label,
				// kernel.getClassType(type));
				// if none, create new geo
				geo1 = kernel.createGeoElement(cons, type);
				geo1.setLoadedLabel(label);

				// Application.debug(label+", "+geo.isLabelSet());

				// independent GeoElements should be hidden by default
				// (as older versions of this file format did not
				// store show/hide information for all kinds of objects,
				// e.g. GeoNumeric)
				geo1.setEuclidianVisible(false);
			}
		} else {
			int defset = Integer.parseInt(defaultset);
			geo1 = kernel.getConstruction().getConstructionDefaults()
					.getDefaultGeo(defset);
			if (geo1 == null) {
				// wrong default setting, act as if there were no default set
				geo1 = kernel.lookupLabel(label);
				if (geo1 == null) {
					geo1 = kernel.createGeoElement(cons, type);
					geo1.setLoadedLabel(label);
					geo1.setEuclidianVisible(false);
				}
			}
		}

		// use default point style on points
		if (geo1.getGeoClassType().equals(GeoClass.POINT) && ggbFileFormat < 3.3) {
			((PointProperties) geo1).setPointStyle(docPointStyle);
		}

		// for downward compatibility
		if (geo1.isLimitedPath()) {
			LimitedPath lp = (LimitedPath) geo1;
			// old default value for intersections of segments, ...
			// V2.5: default of "allow outlying intersections" is now false
			lp.setAllowOutlyingIntersections(true);

			// old default value for geometric transforms of segments, ...
			// V2.6: default of "keep type on geometric transform" is now true
			lp.setKeepTypeOnGeometricTransform(false);
		}

		return geo1;
	}

	/**
	 * Handle start tag inside &lt;element>
	 * @param eName element name
	 * @param attrs attributes
	 */
	protected void startGeoElement(String eName,
			LinkedHashMap<String, String> attrs) {
		if (geo == null) {
			System.err.println("no element set for <" + eName + ">");
			return;
		}

		boolean ok = true;
		ScriptType scriptType = ScriptType.getTypeWithXMLName(eName);
		if (scriptType != null) {
			ok = handleScript(attrs, scriptType);
		}
		else switch (firstChar(eName)) {
		case 'a':
			if ("auxiliary".equals(eName)) {
				ok = handleAuxiliary(attrs);
				break;
			} else if ("animation".equals(eName)) {
				ok = handleAnimation(attrs);
				break;
			} else if ("arcSize".equals(eName)) {
				ok = handleArcSize(attrs);
				break;
			} else if ("allowReflexAngle".equals(eName)) {
				ok = handleAllowReflexAngle(attrs);
				break;
			} else if ("absoluteScreenLocation".equals(eName)) {
				ok = handleAbsoluteScreenLocation(attrs);
				break;
			}

		case 'b':
			if ("breakpoint".equals(eName)) {
				ok = handleBreakpoint(attrs);
				break;
			} else if ("bgColor".equals(eName)) {
				ok = handleBgColor(attrs);
				break;
			}

		case 'c':
			if ("coords".equals(eName)) {
				ok = handleCoords(attrs);
				break;
			} else if ("coordStyle".equals(eName)) {
				ok = handleCoordStyle(attrs);
				break;
			} else if ("caption".equals(eName)) {
				ok = handleCaption(attrs);
				break;
			} else if ("condition".equals(eName)) {
				ok = handleCondition(attrs);
				break;
			} else if ("checkbox".equals(eName)) {
				ok = handleCheckbox(attrs);
				break;
			} else if ("coefficients".equals(eName)) {
				ok = handleCoefficients(attrs);
				break;
			} else if ("comboBox".equals(eName)) {
				ok = handleComboBox(attrs);
				break;
			}

		case 'd':
			if ("decoration".equals(eName)) {
				ok = handleDecoration(attrs);
				break;
			} else if ("decimals".equals(eName)) {
				ok = handleTextDecimals(attrs);
				break;
			}

		case 'e':
			if ("eqnStyle".equals(eName)) {
				ok = handleEqnStyle(attrs);
				break;
			} else if ("eigenvectors".equals(eName)) {
				ok = handleEigenvectors(attrs);
				break;
			} else if ("emphasizeRightAngle".equals(eName)) {
				ok = handleEmphasizeRightAngle(attrs);
				break;
			}

		case 'f':
			if ("fixed".equals(eName)) {
				ok = handleFixed(attrs);
				break;
			} else if ("file".equals(eName)) {
				ok = handleFile(attrs);
				break;
			} else if ("font".equals(eName)) {
				ok = handleTextFont(attrs);
				break;
			}
			// Michael Borcherds 2007-11-19
			else if ("forceReflexAngle".equals(eName)) {
				ok = handleForceReflexAngle(attrs);
				break;
			}
		case 'i':
			if ("isLaTeX".equals(eName)) {
				ok = handleIsLaTeX(attrs);
				break;
			} else if ("inBackground".equals(eName)) {
				ok = handleInBackground(attrs);
				break;
			} else if ("interpolate".equals(eName)) {
				ok = handleInterpolate(attrs);
				break;
			}

		case 'k':
			if ("keepTypeOnTransform".equals(eName)) {
				ok = handleKeepTypeOnTransform(attrs);
				break;
			}

		case 'l':
			if ("lineStyle".equals(eName)) {
				ok = handleLineStyle(attrs);
				break;
			} else if ("labelOffset".equals(eName)) {
				ok = handleLabelOffset(attrs);
				break;
			} else if ("labelMode".equals(eName)) {
				ok = handleLabelMode(attrs);
				break;
			} else if ("layer".equals(eName)) {
				ok = handleLayer(attrs);
				break;
			} else if ("linkedGeo".equals(eName)) {
				ok = handleLinkedGeo(attrs);
				break;
			} else if ("length".equals(eName)) {
				ok = handleLength(attrs);
				break;
			} else if ("listType".equals(eName)) {
				ok = handleListType(attrs);
				break;
			}

		case 'm':
			if ("matrix".equals(eName)) {
				ok = handleMatrix(attrs);
				break;
			}

		case 'o':
			if ("objColor".equals(eName)) {
				ok = handleObjColor(attrs);
				break;
			} else if ("outlyingIntersections".equals(eName)) {
				ok = handleOutlyingIntersections(attrs);
				break;
			} /*
			 * else if ("objCoords".equals(eName)) { ok =
			 * handleObjCoords(attrs); break; }
			 */

		case 'p':
			if ("pointSize".equals(eName)) {
				ok = handlePointSize(attrs);
				break;
			}

			// Florian Sonner 2008-07-17
			else if ("pointStyle".equals(eName)) {
				ok = handlePointStyle(attrs);
				break;
			} 
			/*
			 * should not be needed else if ("pathParameter".equals(eName)) { ok
			 * = handlePathParameter(attrs); break; }
			 */

		case 's':
			if ("show".equals(eName)) {
				ok = handleShow(attrs);
				break;
			} else if ("showOnAxis".equals(eName)) {
				ok = handleShowOnAxis(attrs);
				break;
			} else if ("startPoint".equals(eName)) {
				ok = handleStartPoint(attrs);
				break;
			} else if ("slider".equals(eName)) {
				ok = handleSlider(attrs);
				break;
			} else if ("slopeTriangleSize".equals(eName)) {
				ok = handleSlopeTriangleSize(attrs);
				break;
			} else if ("significantfigures".equals(eName)) {
				ok = handleTextFigures(attrs);
				break;
			} else if ("spreadsheetTrace".equals(eName)) {
				ok = handleSpreadsheetTrace(attrs);
				break;
			} else if ("showTrimmed".equals(eName)) {
				ok = handleShowTrimmed(attrs);
				break;
			} else if ("selectionAllowed".equals(eName)) {
				ok = handleSelectionAllowed(attrs);
				break;
			} else if ("selectedIndex".equals(eName)) {
				ok = handleSelectedIndex(attrs);
				break;
			}

		case 't':
			if ("trace".equals(eName)) {
				ok = handleTrace(attrs);
				break;
			} else if ("tooltipMode".equals(eName)) {
				ok = handleTooltipMode(attrs);
				break;
			}

		case 'u':
			if ("userinput".equals(eName)) {
				ok = handleUserInput(attrs);
				break;
			}

		case 'v':
			if ("value".equals(eName)) {
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

	private static char firstChar(String eName) {
		if(eName==null || eName.length()==0)
			return '?';
		return eName.charAt(0);
	}

	private boolean handleShow(LinkedHashMap<String, String> attrs) {
		try {
			geo.setEuclidianVisible(parseBoolean(attrs.get("object")));
			geo.setLabelVisible(parseBoolean(attrs.get("label")));

			// bit 0 -> display object in EV1, 0 = true (default)
			// bit 1 -> display object in EV2, 0 = false (default)
			int EVs = 0; // default, display in just EV1
			String str = attrs.get("ev");
			if (str != null)
				EVs = Integer.parseInt(str);

			if ((EVs & 1) == 0) // bit 0
				geo.addView(App.VIEW_EUCLIDIAN);
			else
				geo.removeView(App.VIEW_EUCLIDIAN);

			if ((EVs & 2) == 2) { // bit 1
				geo.addView(App.VIEW_EUCLIDIAN2);
			} else {
				geo.removeView(App.VIEW_EUCLIDIAN2);
			}
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleShowOnAxis(LinkedHashMap<String, String> attrs) {
		try {
			if (!(geo instanceof GeoFunction))
				return false;
			((GeoFunction) geo).setShowOnAxis(parseBoolean(attrs.get("val")));
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleObjColor(LinkedHashMap<String, String> attrs) {
		geogebra.common.awt.GColor col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		geo.setObjColor(col);

		// Dynamic colors
		// Michael Borcherds 2008-04-02
		String red = attrs.get("dynamicr");
		String green = attrs.get("dynamicg");
		String blue = attrs.get("dynamicb");
		String alpha = attrs.get("dynamica");
		String colorSpace = attrs.get("colorSpace");

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
					geo.setColorSpace(colorSpace == null ? GeoElement.COLORSPACE_RGB
							: Integer.parseInt(colorSpace));
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Error loading Dynamic Colors");
			}

		String angle = attrs.get("hatchAngle");
		if (angle != null) {
			geo.setHatchingAngle(Integer.parseInt(angle));
			geo.setFillType(GeoElement.FILL_HATCH);
		}

		String inverse = attrs.get("inverseFill");
		if (inverse != null) {
			geo.setInverseFill(Boolean.parseBoolean(inverse));
		}

		String distance = attrs.get("hatchDistance");
		if (angle != null) {
			geo.setHatchingDistance(Integer.parseInt(distance));
			geo.setFillType(GeoElement.FILL_HATCH);
		}

		String filename = attrs.get("image");
		if (filename != null) {
			geo.setFillImage(filename);
			geo.setFillType(GeoElement.FILL_IMAGE);
		}

		alpha = attrs.get("alpha");
		if (alpha != null && (!geo.isGeoList() || ggbFileFormat > 3.19)) // ignore
																			// alpha
																			// value
																			// for
																			// lists
																			// prior
																			// to
																			// GeoGebra
																			// 3.2
			geo.setAlphaValue(Float.parseFloat(alpha));
		return true;
	}

	private boolean handleBgColor(LinkedHashMap<String, String> attrs) {
		geogebra.common.awt.GColor col = handleColorAlphaAttrs(attrs);
		if (col == null)
			return false;
		geo.setBackgroundColor(col);
		geo.updateRepaint();

		return true;
	}

	/*
	 * expects r, g, b attributes to build a color
	 */
	private static geogebra.common.awt.GColor handleColorAttrs(
			LinkedHashMap<String, String> attrs) {
		try {
			int red = Integer.parseInt(attrs.get("r"));
			int green = Integer.parseInt(attrs.get("g"));
			int blue = Integer.parseInt(attrs.get("b"));
			return geogebra.common.factories.AwtFactory.prototype.newColor(red,
					green, blue);
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * expects r, g, b, alpha attributes to build a color
	 */
	private static geogebra.common.awt.GColor handleColorAlphaAttrs(
			LinkedHashMap<String, String> attrs) {
		try {
			int red = Integer.parseInt(attrs.get("r"));
			int green = Integer.parseInt(attrs.get("g"));
			int blue = Integer.parseInt(attrs.get("b"));
			int alpha = Integer.parseInt(attrs.get("alpha"));
			return geogebra.common.factories.AwtFactory.prototype.newColor(red,
					green, blue, alpha);
		} catch (Exception e) {
			return null;
		}
	}

	private boolean handleLineStyle(LinkedHashMap<String, String> attrs) {
		try {
			geo.setLineType(Integer.parseInt(attrs.get("type")));
			geo.setLineThickness(Integer.parseInt(attrs.get("thickness")));

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
			geo.setDecorationType(Integer.parseInt(attrs.get("type")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleEqnStyle(LinkedHashMap<String, String> attrs) {
		// line
		if (geo.isGeoLine()) {
			GeoLineND line = (GeoLineND) geo;
			String style = attrs.get("style");
			if (style.equals("implicit")) {
				line.setToImplicit();
			} else if (style.equals("explicit")) {
				line.setToExplicit();
			} else if (style.equals("parametric")) {
				String parameter = attrs.get("parameter");
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
			String style = attrs.get("style");
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
		if (!(geo instanceof CoordStyle)) {
			System.err.println("wrong element type for <coordStyle>: "
					+ geo.getClass());
			return false;
		}
		CoordStyle v = (CoordStyle) geo;
		String style = attrs.get("style");
		if (style.equals("cartesian")) {
			v.setCartesian();
		} else if (style.equals("polar")) {
			v.setPolar();
		} else if (style.equals("complex")) {
			v.setComplex();
		} else if (style.equals("cartesian3d")) {
			v.setCartesian3D();
		} else {
			System.err.println("unknown style in <coordStyle>: " + style);
			return false;
		}
		return true;
	}

	private boolean handleCaption(LinkedHashMap<String, String> attrs) {
		try {
			geo.setCaption(attrs.get("val"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleScript(LinkedHashMap<String, String> attrs,
			ScriptType type) {
		try {
			String text = attrs.get("val");
			if (text != null && text.length() > 0) {
				Script script = app.createScript(type, text, false);
				geo.setClickScript(script);
			}
			text = attrs.get("onUpdate");
			if (text != null && text.length() > 0) {
				Script script = app.createScript(type, text, false);
				geo.setUpdateScript(script);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleCondition(LinkedHashMap<String, String> attrs) {
		try {
			// condition for visibility of object
			String strShowObjectCond = attrs.get("showObject");
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
			bool.setCheckboxFixed(parseBoolean(attrs.get("fixed")));
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
			App.debug("wrong element type for <value>: "
					+ geo.getClass());
			return false;
		}

		try {
			String strVal = attrs.get("val");
			if (isNumber) {
				GeoNumeric n = (GeoNumeric) geo;
				n.setValue(StringUtil.parseDouble(strVal));

				// random
				n.setRandom("true".equals(attrs.get("random")));

			} else if (isBoolean) {
				GeoBoolean bool = (GeoBoolean) geo;
				bool.setValue(parseBoolean(strVal));
			} else if (isButton) {
				// XXX What's this javascript doing here? (Arnaud)
				GeoButton button = (GeoButton) geo;
				Script script = app.createScript(ScriptType.JAVASCRIPT, strVal, false);
				button.setClickScript(script);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handlePointSize(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof PointProperties)) {
			App.debug("wrong element type for <pointSize>: "
					+ geo.getClass());
			return false;
		}

		try {
			PointProperties p = (PointProperties) geo;
			p.setPointSize(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// Florian Sonner 2008-07-17
	private boolean handlePointStyle(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof PointProperties)) {
			App.debug("wrong element type for <pointStyle>: "
					+ geo.getClass());
			return false;
		}

		try {
			PointProperties p = (PointProperties) geo;

			int style = Integer.parseInt(attrs.get("val"));

			if (style == -1) {
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
			geo.setLayer(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	

		
	private boolean handleCasCellInput(LinkedHashMap<String, String> attrs) {
		try {
			String input = attrs.get("value");
			geoCasCell.setInput(input);

			String prefix = attrs.get("prefix");
			String eval = attrs.get("eval");
			String postfix = attrs.get("postfix");
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
		if(geoCasCell.isUseAsText())
			return true;
		try {
			String output = attrs.get("value");
			boolean error = parseBoolean(attrs.get("error"));
			boolean nativeOutput = parseBoolean(attrs.get("native"));
			geoCasCell.setNative(nativeOutput);
			boolean pointList = parseBoolean(attrs.get("pointList"));
			geoCasCell.setPointList(pointList);
			if (error) {
				geoCasCell.setError(output);
			} else {
				if(!nativeOutput){
					geoCasCell.computeOutput();
				}else
					geoCasCell.setOutput(output,false);
			}
			

			String evalCommandComment = attrs.get("evalCommand");
			if (evalCommandComment != null) {
				geoCasCell.setEvalCommand(evalCommandComment);
			}

			String evalComment = attrs.get("evalComment");
			if (evalComment != null) {
				geoCasCell.setEvalComment(evalComment);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// private boolean handleCASPairColor(LinkedHashMap<String, String> attrs) {
	// Color col = handleColorAttrs(attrs);
	// if (col == null)
	// return false;
	// // geo.setObjColor(col);
	//
	// return true;
	// }

	/*
	 * this should not be needed private boolean
	 * handlePathParameter(LinkedHashMap<String, String> attrs) { if
	 * (!(geo.isGeoPoint())) { Application.debug(
	 * "wrong element type for <handlePathParameter>: " + geo.getClass());
	 * return false; }
	 * 
	 * try { GeoPoint p = (GeoPoint) geo; PathParameter param = new
	 * PathParameter(); double t = StringUtil.parseDouble((String)
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

			//make sure 
			String strMin = attrs.get("min");
			String strMax = attrs.get("max");
			if (strMin != null || strMax !=null) {
				minMaxList.add(new GeoNumericMinMax(geo, strMin,strMax));
			}

			String str = attrs.get("absoluteScreenLocation");
			if (str != null) {
				num.setAbsoluteScreenLocActive(parseBoolean(str));
			} else {
				num.setAbsoluteScreenLocActive(false);
			}

			double x = StringUtil.parseDouble(attrs.get("x"));
			double y = StringUtil.parseDouble(attrs.get("y"));
			num.setSliderLocation(x, y, true);
			num.setSliderWidth(StringUtil.parseDouble(attrs.get("width")));
			num.setSliderFixed(parseBoolean(attrs.get("fixed")));
			num.setSliderHorizontal(parseBoolean(attrs.get("horizontal")));

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
			t.setTrace(parseBoolean(attrs.get("val")));
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

			// set geo for tracing
			geo.setSpreadsheetTrace(parseBoolean(attrs.get("val")));

			SpreadsheetTraceSettings t = geo.getTraceSettings();
			t.traceColumn1 = Integer.parseInt(attrs.get("traceColumn1"));
			t.traceColumn2 = Integer.parseInt(attrs.get("traceColumn2"));
			t.traceRow1 = Integer.parseInt(attrs.get("traceRow1"));
			t.traceRow2 = Integer.parseInt(attrs.get("traceRow2"));
			t.tracingRow = Integer.parseInt(attrs.get("tracingRow"));
			t.numRows = Integer.parseInt(attrs.get("numRows"));
			t.headerOffset = Integer.parseInt(attrs.get("headerOffset"));

			t.doColumnReset = (parseBoolean(attrs.get("doColumnReset")));
			t.doRowLimit = (parseBoolean(attrs.get("doRowLimit")));
			t.showLabel = (parseBoolean(attrs.get("showLabel")));
			t.showTraceList = (parseBoolean(attrs.get("showTraceList")));
			t.doTraceGeoCopy = (parseBoolean(attrs.get("doTraceGeoCopy")));
			
			app.setNeedsSpreadsheetTableModel();
			
			// app.getTraceManager().loadTraceGeoCollection(); is called when construction loaded to add geo to trace list

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		/*
		 * OLD CODE
		 * 
		 * if (!(geo instanceof GeoPoint)) {
		 * System.err.println("wrong element type for <trace>: " +
		 * geo.getClass()); return false; }
		 * 
		 * try { GeoPoint p = (GeoPoint) geo;
		 * p.setSpreadsheetTrace(parseBoolean((String) attrs.get("val")));
		 * return true; } catch (Exception e) { return false; }
		 */
		// END G.Sturr

	}

	private boolean handleShowTrimmed(LinkedHashMap<String, String> attrs) {
		try {
			geo.setShowTrimmedIntersectionLines(parseBoolean(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSelectionAllowed(LinkedHashMap<String, String> attrs) {
		try {
			geo.setSelectionAllowed(parseBoolean(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSelectedIndex(LinkedHashMap<String, String> attrs) {
		try {
			if (geo.isGeoList())
				((GeoList) geo).setSelectedIndex(Integer.parseInt(attrs
						.get("val")), false);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleAnimation(LinkedHashMap<String, String> attrs) {
		try {

			String strStep = attrs.get("step");
			if (strStep != null) {
				// store speed expression to be processed later
				animationStepList.add(new GeoExpPair(geo, strStep));
			}
			String strSpeed = attrs.get("speed");
			if (strSpeed != null) {
				// store speed expression to be processed later
				animationSpeedList.add(new GeoExpPair(geo, strSpeed));
			}

			String type = attrs.get("type");
			if (type != null)
				geo.setAnimationType(Integer.parseInt(type));

			// doesn't work for hidden sliders now that intervalMin/Max are set
			// at end of XML (dynamic slider range(
			// geo.setAnimating(parseBoolean((String) attrs.get("playing")));

			// replacement
			if (parseBoolean(attrs.get("playing")))
				animatingList.add(geo);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleFixed(LinkedHashMap<String, String> attrs) {
		try {
			geo.setFixed(parseBoolean(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleBreakpoint(LinkedHashMap<String, String> attrs) {
		try {
			geo.setConsProtocolBreakpoint(parseBoolean(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleFile(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoImage() || geo.isGeoButton())) {
			System.err.println("wrong element type for <file>: "
					+ geo.getClass());
			return false;
		}

		try {
			geo.setImageFileName(attrs.get("name"));
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
			
			String oldSize = attrs.get("size");
			// multiplier, new from ggb42
			String size = attrs.get("sizeM"); 
			
			if (size == null) {
				double appSize =app.getFontSize();
				double oldSizeInt = Integer.parseInt(oldSize);
				text.setFontSizeMultiplier(Math.max(appSize+oldSizeInt, MIN_TEXT_SIZE) / appSize); 
			} else {
				text.setFontSizeMultiplier(StringUtil.parseDouble(size));
			}
			if (serif != null)
				text.setSerifFont(parseBoolean((String) serif));
			if (style != null)
				text.setFontStyle(Integer.parseInt((String) style));
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
			text.setPrintDecimals(Integer.parseInt(attrs.get("val")), true);
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
			text.setPrintFigures(Integer.parseInt(attrs.get("val")), true);
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
			((GeoImage) geo).setInBackground(parseBoolean(attrs.get("val")));
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
			((GeoImage) geo).setInterpolate(parseBoolean(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleAuxiliary(LinkedHashMap<String, String> attrs) {
		try {
			geo.setAuxiliaryObject(parseBoolean(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleIsLaTeX(LinkedHashMap<String, String> attrs) {
		try {

			((GeoText) geo).setLaTeX(parseBoolean(attrs.get("val")), false);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleArcSize(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof GeoAngle)) {
			App.error("wrong element type for <arcSize>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoAngle angle = (GeoAngle) geo;
			angle.setArcSize(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleAbsoluteScreenLocation(
			LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof AbsoluteScreenLocateable)) {
			App
					.error("wrong element type for <absoluteScreenLocation>: "
							+ geo.getClass());
			return false;
		}

		try {
			AbsoluteScreenLocateable absLoc = (AbsoluteScreenLocateable) geo;
			int x = Integer.parseInt(attrs.get("x"));
			int y = Integer.parseInt(attrs.get("y"));
			absLoc.setAbsoluteScreenLoc(x, y);
			absLoc.setAbsoluteScreenLocActive(true);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleAllowReflexAngle(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoAngle())) {
			App.error("wrong element type for <allowReflexAngle>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoAngle angle = (GeoAngle) geo;
			angle.setAllowReflexAngle(parseBoolean(attrs.get("val")));
			return true;
		} catch (Exception e) {

			return false;
		}
	}

	private boolean handleEmphasizeRightAngle(
			LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoAngle())) {
			App.error("wrong element type for <emphasizeRightAngle>: "
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

	private boolean handleComboBox(
			LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoList())) {
			System.err.println("wrong element type for <comboBox>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoList list = (GeoList) geo;
			list.setDrawAsComboBox(parseBoolean(attrs.get("val")));
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
			angle.setForceReflexAngle(parseBoolean(attrs.get("val")));
			return true;
		} catch (Exception e) {

			return false;
		}
	}

	// Michael Borcherds 2007-11-19

	private boolean handleOutlyingIntersections(
			LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof LimitedPath)) {
			App
					.debug("wrong element type for <outlyingIntersections>: "
							+ geo.getClass());
			return false;
		}

		try {
			LimitedPath lpath = (LimitedPath) geo;
			lpath.setAllowOutlyingIntersections(parseBoolean(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKeepTypeOnTransform(
			LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof LimitedPath)) {
			App
					.debug("wrong element type for <outlyingIntersections>: "
							+ geo.getGeoClassType());
			return false;
		}

		try {
			LimitedPath lpath = (LimitedPath) geo;
			lpath.setKeepTypeOnGeometricTransform(parseBoolean(attrs.get("val")));
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
			num.setSlopeTriangleSize(Integer.parseInt(attrs.get("val")));
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
		String exp = attrs.get("exp");
		if (exp == null) // try deprecated attribute
			exp = attrs.get("label");

		// for corners a number of the startPoint is given
		int number = 0;
		try {
			number = Integer.parseInt(attrs.get("number"));
		} catch (Exception e) {
			//do nothing
		}

		if (exp != null) {
			// store (geo, epxression, number) values
			// they will be processed in processStartPoints() later
			startPointList.add(new LocateableExpPair(loc, exp, number));
			loc.setWaitForStartPoint();
		} else {
			// absolute start point (coords expected)
			try {
				/*
				 * double x = StringUtil.parseDouble((String) attrs.get("x"));
				 * double y = StringUtil.parseDouble((String) attrs.get("y"));
				 * double z = StringUtil.parseDouble((String) attrs.get("z"));
				 * GeoPoint p = new GeoPoint(cons); p.setCoords(x, y, z);
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

	/**
	 * create absolute start point (coords expected)
	 * 
	 * @param attrs tag atributes
	 * @return start point
	 */
	protected GeoPointND handleAbsoluteStartPoint(
			LinkedHashMap<String, String> attrs) {
		double x = Double.NaN;
		double y = Double.NaN;
		double z = Double.NaN;
		x = StringUtil.parseDouble(attrs.get("x"));
		y = StringUtil.parseDouble(attrs.get("y"));
		z = StringUtil.parseDouble(attrs.get("z"));
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
				GeoPointND P = pair.point != null ? pair.point : algProc
						.evaluateToPoint(pair.exp, true, true);
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
		String val = attrs.get("val");

		if (geo instanceof GeoTextField) {
			((GeoTextField) geo).setLength(Integer.parseInt(val));
		} else {
			throw new MyError(app, "handleLength: " + geo.getGeoClassType());
		}

		return true;
	}

	private boolean handleListType(LinkedHashMap<String, String> attrs) {

		// name of geo type, eg "point"
		String val = attrs.get("val");

		if (geo instanceof GeoList) {
			((GeoList) geo).setTypeStringForXML(val);
		} else {
			throw new MyError(app, "handleLength: " + geo.getGeoClassType());
		}

		return true;
	}

	/**
	 * Linked Geos have to be handled at the end of the construction, because
	 * they could depend on objects that are defined after this GeoElement.
	 * 
	 * So we store all (geo, expression) pairs and process them at the end of
	 * the construction.
	 * 
	 * @see processLinkedGeoList
	 */
	private boolean handleLinkedGeo(LinkedHashMap<String, String> attrs) {

		// name of linked geo
		String exp = attrs.get("exp");

		if (exp != null) {
			// store (geo, epxression, number) values
			// they will be processed in processLinkedGeos() later
			linkedGeoList.add(new GeoExpPair(geo, exp));
		} else
			return false;

		return true;
	}

	private void processLinkedGeoList() {
		try {
			Iterator<GeoExpPair> it = linkedGeoList.iterator();

			while (it.hasNext()) {
				GeoExpPair pair = it.next();

				((GeoTextField) pair.getGeo()).setLinkedGeo(kernel
						.lookupLabel(pair.exp));
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
				pair.getGeo().setShowObjectCondition(condition);
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
				GeoExpPair pair = it.next();
				NumberValue num = algProc.evaluateToNumeric(pair.exp, false);
				pair.getGeo().setAnimationSpeedObject(num);
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
				GeoExpPair pair = it.next();
				NumberValue num = algProc.evaluateToNumeric(pair.exp, false);
				pair.getGeo().setAnimationStep(num);
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
				GeoElement geo1 = it.next();
				geo1.setAnimating(true);
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
			Iterator<GeoNumericMinMax> it = minMaxList.iterator();
			AlgebraProcessor algProc = kernel.getAlgebraProcessor();

			while (it.hasNext()) {
				GeoNumericMinMax pair = it.next();
				// the setIntervalMin and setIntervalMax methods might turn ?
				// into defined
				// this is intentional, but when loading a file we must override
				// it for 3.2 compatibility
				boolean wasDefined = pair.getGeo().isDefined();
				if(pair.min!=null){
					NumberValue num = algProc.evaluateToNumeric(pair.min, false);
					((GeoNumeric) pair.getGeo()).setIntervalMin(num);
				}
				
				if(pair.max!=null){
					NumberValue num2 = algProc.evaluateToNumeric(pair.max, false);
					((GeoNumeric) pair.getGeo()).setIntervalMax(num2);
				}
				
				if (!wasDefined)
					pair.getGeo().setUndefined();
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
				pair.getGeo().setColorFunction(algProc.evaluateToList(pair.exp));
			}
		} catch (Exception e) {
			dynamicColorList.clear();
			e.printStackTrace();
			throw new MyError(app, "dynamicColorList: " + e.toString());
		}
		dynamicColorList.clear();
	}

	/*
	 * private void processDynamicCoordinatesList() { try {
	 * 
	 * Iterator it = dynamicCoordinatesList.iterator(); AlgebraProcessor algProc
	 * = kernel.getAlgebraProcessor();
	 * 
	 * while (it.hasNext()) { GeoExpPair pair = (GeoExpPair) it.next();
	 * ((GeoPoint
	 * )(pair.geo)).setCoordinateFunction(algProc.evaluateToList(pair.exp)); } }
	 * catch (Exception e) { dynamicCoordinatesList.clear();
	 * e.printStackTrace(); throw new MyError(app, "dynamicCoordinatesList: " +
	 * e.toString()); } dynamicCoordinatesList.clear(); }
	 */

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
			conic.setEigenvectors(StringUtil.parseDouble(attrs.get("x0")),
					StringUtil.parseDouble(attrs.get("y0")),
					StringUtil.parseDouble(attrs.get("z0")),
					StringUtil.parseDouble(attrs.get("x1")),
					StringUtil.parseDouble(attrs.get("y1")),
					StringUtil.parseDouble(attrs.get("z1")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleMatrix(LinkedHashMap<String, String> attrs) {
		if (!geo.isGeoConic()) {
			System.err.println("wrong element type for <matrix>: "
					+ geo.getClass());
			return false;
		}
		try {
			if (geo.isGeoConic()) {
				GeoConic conic = (GeoConic) geo;
				// set matrix and classify conic now
				// <eigenvectors> should have been set earlier
				double[] matrix = { StringUtil.parseDouble(attrs.get("A0")),
						StringUtil.parseDouble(attrs.get("A1")),
						StringUtil.parseDouble(attrs.get("A2")),
						StringUtil.parseDouble(attrs.get("A3")),
						StringUtil.parseDouble(attrs.get("A4")),
						StringUtil.parseDouble(attrs.get("A5")) };
				conic.setMatrix(matrix);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleLabelOffset(LinkedHashMap<String, String> attrs) {
		try {
			geo.labelOffsetX = Integer.parseInt(attrs.get("x"));
			geo.labelOffsetY = Integer.parseInt(attrs.get("y"));

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleLabelMode(LinkedHashMap<String, String> attrs) {
		try {
			geo.setLabelMode(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleTooltipMode(LinkedHashMap<String, String> attrs) {
		try {
			geo.setTooltipMode(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleCoefficients(LinkedHashMap<String, String> attrs) {
		// Application.debug(attrs.toString());
		if (!(geo.isGeoImplicitPoly())) {
			App.warn("wrong element type for <coefficients>: "
					+ geo.getClass());
			return false;
		}
		try {
			String rep = attrs.get("rep");
			if (rep == null)
				return false;
			if (attrs.get("rep").equals("array")) {
				String data = attrs.get("data");
				if (data == null)
					return false;
				ArrayList<ArrayList<Double>> collect = new ArrayList<ArrayList<Double>>();
				ArrayList<Double> newRow = new ArrayList<Double>();
				int start = 0;
				for (int c = 1; c < data.length(); c++) {
					switch (data.charAt(c)) {
					case '[':
						if (newRow.size() > 0)
							return false;
						start = c + 1;
						break;
					case ']':
						newRow.add(StringUtil.parseDouble(data.substring(start, c)));
						start = c + 1;
						collect.add(newRow);
						newRow = new ArrayList<Double>();
						c++; // jump over ','
						break;
					case ',':
						newRow.add(StringUtil.parseDouble(data.substring(start, c)));
						start = c + 1;
					}
				}
				double[][] coeff = new double[collect.size()][];
				for (int i = 0; i < collect.size(); i++) {
					ArrayList<Double> row = collect.get(i);
					coeff[i] = new double[row.size()];
					for (int j = 0; j < row.size(); j++) {
						coeff[i][j] = row.get(j);
					}
				}
				((GeoImplicitPoly) geo).setCoeff(coeff);
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	private boolean handleUserInput(LinkedHashMap<String, String> attrs) {
		// Application.debug(attrs.toString());
		if (!(geo instanceof GeoUserInputElement)) {
			App.warn("wrong element type for <userinput>: "
					+ geo.getClass());
			return false;
		}
		try {
			if (geo.isIndependent()) {
				String value = attrs.get("value");
				if (value == null)
					return false;
				ValidExpression ve = parser.parseGeoGebraExpression(value);
				((GeoUserInputElement) geo).setUserInput(ve);
			}
			if (attrs.get("show") != null && attrs.get("show").equals("true"))
				((GeoUserInputElement) geo).setInputForm();
			else
				((GeoUserInputElement) geo).setExtendedForm();
			if (attrs.get("valid") != null) {
				((GeoUserInputElement) geo).setValidInputForm(attrs
						.get("valid").equals("true"));
			}
			return true;
		} catch (Exception e) {
			App.debug(e.getMessage());
			return false;
		}
	}

	// ====================================
	// <command>
	// ====================================

	// called when <command> is encountered
	// e.g. for <command name="Intersect">
	private Command getCommand(LinkedHashMap<String, String> attrs) {
		Command command = null;
		String name = attrs.get("name");

		String type = attrs.get("type");
		if (type != null) {
			cons.setOutputGeo(type);
		}
		
		String var = attrs.get("var");
		if (var != null) {
			App.debug("reading var");
			cons.registerFunctionVariable(var);
		}

		// Application.debug(name);
		if (name != null)
			command = new Command(kernel, name, false); // do not translate name
		else
			throw new MyError(app, "name missing in <command>");
		return command;
	}

	private void startCommandElement(String eName,
			LinkedHashMap<String, String> attrs) {
		boolean ok = true;

		if ("input".equals(eName)) {
			if (cmd == null)
				throw new MyError(app, "no command set for <input>");
			ok = handleCmdInput(attrs);
		} else if ("output".equals(eName)) {
			ok = handleCmdOutput(attrs);
		} else
			System.err.println("unknown tag in <command>: " + eName);

		if (!ok)
			System.err.println("error in <command>: " + eName);
	}

	private boolean handleCmdInput(LinkedHashMap<String, String> attrs) {
		GeoElement geo1;
		ExpressionNode en;
		String arg = null;

		//Collection<String> values = attrs.values();

		// TODO: it doesn't work with GWT. why?
		// Iterator<String> it = values.iterator();
		// while (it.hasNext()) {

		ArrayList<String> keys = new ArrayList<String>(attrs.keySet());
		for (String key : keys) {

			// parse argument expressions
			try {
				// arg = it.next();
				arg = attrs.get(key);

				// for downward compatibility: lookup label first
				// as this could be some weird name that can't be parsed
				// e.g. "1/2_{a,b}" could be a label name
				// we don't want to override local variables with this fix,
				// therefore we
				// make exception for Sequence and CurveCartesian
				if (cmd.getName().equals("Sequence")
						|| cmd.getName().equals("CurveCartesian"))
					geo1 = null;
				else
					geo1 = kernel.lookupLabel(arg);

				// Application.debug("input : "+geo.getLabel());

				// arg is a label and does not conatin $ signs (e.g. $A1 in
				// spreadsheet)
				if (geo1 != null && arg.indexOf('$') < 0) {
					en = new ExpressionNode(kernel, geo1);
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

	private boolean handleCmdOutput(LinkedHashMap<String, String> attrs) {
		try {
			// set labels for command processing
			String label;
			int countLabels = 0;
			/*
			 * TODO Doesn't work with GWT. why? Collection<String> values =
			 * attrs.values(); Iterator<String> it = values.iterator(); while
			 * (it.hasNext()) { label = it.next();
			 */

			ArrayList<String> attrKeys = new ArrayList<String>(attrs.keySet());
			for (String key : attrKeys) {
				label = attrs.get(key);
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
			cons.registerFunctionVariable(null);
			String cmdName = cmd.getName();
			if (cmdOutput == null)
				throw new MyError(app, "processing of command " + cmd
						+ " failed");
			cmd = null;

			// ensure that labels are set for invisible objects too
			if (attrs.size() != cmdOutput.length) {
				App
						.debug("error in <output>: wrong number of labels for command "
								+ cmdName);
				System.err.println("   cmdOutput.length = " + cmdOutput.length
						+ ", labels = " + attrs.size());
				return false;
			}
			// enforce setting of labels
			// (important for invisible objects like intersection points)

			// it = values.iterator();
			int i = 0;
			/*
			 * while (it.hasNext()) { label = it.next();
			 */
			for (String key : attrKeys) {
				label = attrs.get(key);
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
	private static String[] getAttributeStrings(
			LinkedHashMap<String, String> attrs) {
		Collection<String> values = attrs.values();
		Iterator<String> it = values.iterator();

		String[] ret = new String[values.size()];
		int i = 0;

		while (it.hasNext()) {
			ret[i] = it.next();
			i++;
		}
		return ret;
	}

	// ====================================
	// <expression>
	// ====================================
	private void startExpressionElement(LinkedHashMap<String, String> attrs) {
		String label = attrs.get("label");

		// ignore twinGeo expressions coming from CAS cells
		// e.g. the GeoCasCell f(x) := x^2 automatically creates a twinGeo f
		// where we don't want the expression of f to be processed again
		GeoElement geo1 = kernel.lookupLabel(label);
		if (geo1 != null && geo1.getCorrespondingCasCell() != null)
			return;

		String exp = attrs.get("exp");
		if (exp == null)
			throw new MyError(app, "exp missing in <expression>");

		// type may be vector or point, this is important to distinguish between
		// them
		String type = attrs.get("type");

		// parse expression and process it
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(exp);
			if (label != null)
				ve.setLabel(label);

			// enforce point or vector or line or plane type if it was given in
			// attribute type
			if (type != null) {
				if (type.equals("point") && ve instanceof ExpressionNode) {
					((ExpressionNode) ve).setForcePoint();
				} else if (type.equals("vector") && ve instanceof ExpressionNode) {
					((ExpressionNode) ve).setForceVector();
				//we must check that we have Equation here as xAxis 
				//has also type "line" but is parsed as ExpressionNode
				} else if (ve instanceof Equation){
					if(type.equals("line") ) {
						((Equation) ve).setForceLine();
					} else if (type.equals("plane")) {
						((Equation) ve).setForcePlane();
					} else if (type.equals("conic")) {
						((Equation) ve).setForceConic();
					} else if (type.equals("implicitPoly")) {
						((Equation) ve).setForceImplicitPoly();
					}
				}
			}

			// Application.debug(""+kernel.getAlgebraProcessor());

			GeoElement[] result = kernel.getAlgebraProcessor()
					.processValidExpression(ve);

			// ensure that labels are set for invisible objects too
			if (result != null && label != null && result.length == 1) {
				result[0].setLoadedLabel(label);
			} else {
				System.err.println("error in <expression>: " + exp
						+ ", label: " + label);
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
	
	
	
	private boolean handleAlgebraViewMode(LinkedHashMap<String, String> attrs) {

		try {
			int val = Integer.parseInt(attrs.get("val"));
			app.getSettings().getAlgebra().setTreeMode(val);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean handleAlgebraViewShowAuxiliaryObjects(LinkedHashMap<String, String> attrs) {

		try {
			boolean b = parseBoolean(attrs.get("show"));
			app.getSettings().getAlgebra().setShowAuxiliaryObjects(b);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean handleAlgebraViewCollapsedNodes(LinkedHashMap<String, String> attrs) {

		try {
			String[] strings = attrs.get("val").split(",");
			int[] vals = new int[strings.length];
			for (int i=0; i<strings.length; i++)
				vals[i]=Integer.parseInt(strings[i]);
			app.getSettings().getAlgebra().setCollapsedNodes(vals);
			return true;
		} catch (Exception e) {
			return false;
		}
	}


	// ====================================
	// UTILS
	// ====================================

	/**
	 * Parse string to boolean
	 * 
	 * @param str input string
	 * @return true for "true", false otherwise
	 */
	protected boolean parseBoolean(String str) {
		return "true".equals(str);
	}

	/**
	 * Parse string to boolean
	 * 
	 * @param str input string
	 * @return false for "fale", true otherwise
	 */
	protected boolean parseBooleanRev(String str) {
		return !"false".equals(str);
	}
}
