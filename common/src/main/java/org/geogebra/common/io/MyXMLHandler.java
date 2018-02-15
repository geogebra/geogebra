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

package org.geogebra.common.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.dialog.options.OptionsCAS;
import org.geogebra.common.gui.view.data.DataAnalysisModel.Regression;
import org.geogebra.common.gui.view.data.DataDisplayModel.PlotType;
import org.geogebra.common.gui.view.probcalculator.StatisticsCollection;
import org.geogebra.common.gui.view.probcalculator.StatisticsCollection.Procedure;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.DockSplitPaneData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.KernelCAS;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.MacroConstruction;
import org.geogebra.common.kernel.MacroKernel;
import org.geogebra.common.kernel.PathRegionHandling;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoBarChart;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.Variable;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.FillType;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.kernel.geos.LimitedPath;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.CoordStyle;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.parser.GParser;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.kernel.prover.AlgoProve;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.ConstructionProtocolSettings;
import org.geogebra.common.main.settings.DataAnalysisSettings;
import org.geogebra.common.main.settings.DataCollectionSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.KeyboardSettings;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.plugin.SensorLogger.Types;
import org.geogebra.common.plugin.script.JsScript;
import org.geogebra.common.plugin.script.Script;
import org.geogebra.common.util.Assignment;
import org.geogebra.common.util.Assignment.Result;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.Exercise;
import org.geogebra.common.util.GeoAssignment;
import org.geogebra.common.util.SpreadsheetTraceSettings;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Util;
import org.geogebra.common.util.debug.Log;
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
	private static final int MODE_ASSIGNMENT = 60;
	private static final int MODE_EUCLIDIAN_VIEW = 100;
	/** currently parsing tags for Euclidian3D view */
	protected static final int MODE_EUCLIDIAN_VIEW3D = 101; // only for 3D
	private static final int MODE_SPREADSHEET_VIEW = 150;
	private static final int MODE_ALGEBRA_VIEW = 151;
	private static final int MODE_DATA_COLLECTION_VIEW = 152;
	// private static final int MODE_CAS_VIEW = 160;
	private static final int MODE_CONST_CAS_CELL = 161;
	private static final int MODE_CAS_CELL_PAIR = 162;
	private static final int MODE_CAS_INPUT_CELL = 163;
	private static final int MODE_CAS_OUTPUT_CELL = 164;
	private static final int MODE_CAS_TEXT_CELL = 165;
	private static final int MODE_CAS_MAP = 166;
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

	private static final int MODE_DATA_ANALYSIS = 450;

	private static final int MODE_DEFAULTS = 500;
	private static final int MODE_DEFAULT_GEO = 501;

	/**
	 * we used minimal text size of 4px until 4.0 for texts, because the font
	 * size setting was additive. Not needed with current multiplicative
	 * approach, just for opening old files.
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
	private Exercise exercise;
	private Assignment assignment;
	/** application */
	protected final App app;
	/** lacalization */
	protected final Localization loc;

	private String[] macroInputLabels, macroOutputLabels;
	private GeoElementND[] cmdOutput;
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

	private Parser parser, origParser;

	// List of LocateableExpPair objects
	// for setting the start points at the end of the construction
	// (needed for GeoText and GeoVector)
	private LinkedList<LocateableExpPair> startPointList = new LinkedList<>();

	// List of GeoExpPair objects
	// for setting the linked geos needed for GeoTextFields
	private LinkedList<GeoExpPair> linkedGeoList = new LinkedList<>();

	// List of GeoExpPair condition objects
	// for setting the conditions at the end of the construction
	// (needed for GeoText and GeoVector)
	private LinkedList<GeoExpPair> showObjectConditionList = new LinkedList<>();
	private LinkedList<GeoExpPair> dynamicColorList = new LinkedList<>();
	private LinkedList<GeoExpPair> animationSpeedList = new LinkedList<>();
	private LinkedList<GeoExpPair> animationStepList = new LinkedList<>();
	private LinkedList<GeoElement> animatingList = new LinkedList<>();
	private LinkedList<GeoNumericMinMax> minMaxList = new LinkedList<>();
	/** errors encountered during load */
	ArrayList<String> errors = new ArrayList<>();
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
	private ArrayList<Perspective> tmp_perspectives = new ArrayList<>();

	/**
	 * Array lists to store temporary panes and views of a perspective.
	 */
	private ArrayList<DockSplitPaneData> tmp_panes;
	private ArrayList<DockPanelData> tmp_views;

	private CompatibilityLayout compLayout = new CompatibilityLayout();

	/**
	 * flag so that we can reset EVSettings the first time we get them (for EV1
	 * and EV2)
	 */
	protected boolean resetEVsettingsNeeded = false;
	/** Euclidian settings */
	protected EuclidianSettings evSet = null;
	private static boolean isPreferencesXML = false;

	private boolean lineStyleTagProcessed;
	private boolean symbolicTagProcessed;

	private TreeMap<String, String> casMap;

	private int casMapParent;

	private HashMap<EuclidianSettings, String> xmin = new HashMap<>(),
			xmax = new HashMap<>(), ymin = new HashMap<>(),
			xtick = new HashMap<>(), ytick = new HashMap<>(),
			ztick = new HashMap<>(), ymax = new HashMap<>();

	private boolean sliderTagProcessed, fontTagProcessed;

	private ArrayList<String> entries;

	private static class GeoExpPair {
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

	private static class GeoNumericMinMax {
		private GeoElement geoElement;
		String min;
		String max;

		GeoNumericMinMax(GeoElement g, String min, String max) {
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

	private static class LocateableExpPair {
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


	/**
	 * Creates a new instance of MyXMLHandler
	 * 
	 * @param kernel
	 *            kernel
	 * @param cons
	 *            construction
	 */
	public MyXMLHandler(Kernel kernel, Construction cons) {
		origKernel = kernel;
		origParser = new GParser(origKernel, cons);
		app = origKernel.getApplication();
		loc = app.getLocalization();
		initKernelVars();

		mode = MODE_INVALID;
		constMode = MODE_CONSTRUCTION;
	}

	private void reset(boolean start) {
		startPointList.clear();
		showObjectConditionList.clear();
		dynamicColorList.clear();

		linkedGeoList.clear();
		animatingList.clear();
		minMaxList.clear();
		animationStepList.clear();
		animationSpeedList.clear();
		errors.clear();

		if (start) {
			consStep = -2;
		}

		mode = MODE_INVALID;
		constMode = MODE_CONSTRUCTION;
		hasGuiElement = false;
		sliderTagProcessed = false;
		fontTagProcessed = false;
		lineStyleTagProcessed = false;
		symbolicTagProcessed = false;
		compLayout = new CompatibilityLayout();

		initKernelVars();

		xmin.clear();
		xmax.clear();
		ymin.clear();
		ymax.clear();
		xtick.clear();
		ytick.clear();
		ztick.clear();
	}

	private void initKernelVars() {
		this.kernel = origKernel;
		this.parser = origParser;
		this.cons = origKernel.getConstruction();
	}

	@Override
	public int getConsStep() {
		return consStep;
	}

	// ===============================================
	// SAX ContentHandler methods
	// ===============================================

	@Override
	final public void text(String str) throws SAXException {
		// do nothing
	}

	@Override
	final public void startDocument() throws SAXException {
		reset(true);
	}

	@Override
	final public void endDocument() throws SAXException {
		if (errors.size() > 0) {
			String[] a = new String[errors.size()];
			errors.toArray(a);
			app.showError(new MyError(loc, a));
		}
		if (mode == MODE_INVALID) {
			throw new SAXException(
					loc.getPlain("XMLTagANotFound", "<geogebra>"));
		}
	}

	/**
	 * @return whether errors were produced by parsing last file
	 */
	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	@Override
	final public void startElement(String eName,
			LinkedHashMap<String, String> attrs) throws SAXException {
		// final public void startElement(
		// String namespaceURI,
		// String sName,
		// String qName,
		// LinkedHashMap<String, String> attrs)
		// throws SAXException {
		// String eName = qName;

		if (kernel.userStopsLoading()) {
			kernel.setUserStopsLoading(false);
			throw new SAXException("User has cancelled loading");
		}

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

		case MODE_DATA_COLLECTION_VIEW:
			startDataCollectionViewElement(eName, attrs);
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

		case MODE_ASSIGNMENT:
			startResultElement(eName, attrs);
			break;

		case MODE_DEFAULTS:
			startDefault(eName, attrs);
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

		case MODE_DATA_ANALYSIS:
			startDataAnalysisElement(eName, attrs);
			break;

		case MODE_INVALID:
			startTopLevel(eName, attrs);
			break;

		default:
			Log.error("unknown mode: " + mode);
		}
	}

	private void startTopLevel(String eName, LinkedHashMap<String, String> attrs) {
		// is this a geogebra file?
		if ("geogebra".equals(eName)) {
			mode = MODE_GEOGEBRA;
			// check file format version
			try {
				ggbFileFormat = StringUtil.parseDouble(attrs.get("format"));

				ggbFileFormat = DoubleUtil.checkDecimalFraction(ggbFileFormat);

				if (ggbFileFormat > FORMAT) {
					Log.warn("Newer file format: " + ggbFileFormat);
				}

				if (ggbFileFormat < 3.0) {
					// before V3.0 the kernel had continuity always on
					if (!(kernel instanceof MacroKernel)) {
						kernel.setContinuous(true);
					}

				}

			} catch (RuntimeException e) {
				errors.add(loc.getError("FileFormatUnknown"));
			}

			String ggbVersion = attrs.get("version");
			app.setFileVersion(ggbVersion, attrs.get("app"));
			String uniqueId = attrs.get("id");
			if (uniqueId != null) {
				app.setUniqueId(uniqueId);
			}
		}

	}

	private void startDataAnalysisElement(String eName,
			LinkedHashMap<String, String> attrs) {
		DataAnalysisSettings das = app.getSettings().getDataAnalysis();
		if ("item".equals(eName)) {
			das.addItem(attrs.get("ranges"));
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
		} catch (RuntimeException e) {
			Log.error("error in element <scripting>");
		}
	}

	// set mode back to geogebra mode
	@Override
	final public void endElement(String eName)
			// public void endElement(String namespaceURI, String sName, String
			// qName)
			throws SAXException {
		// String eName = qName;
		switch (mode) {
		default:
			Log.debug("missing case " + mode);
			break;
		case MODE_EUCLIDIAN_VIEW:
			// we should set the EV sizes if they were not yet set
			app.ensureEvSizeSet(evSet);

			if ("euclidianView".equals(eName)) {
				evSet = null;
				mode = MODE_GEOGEBRA;
			}
			break;
		case MODE_EUCLIDIAN_VIEW3D:
			if ("euclidianView3D".equals(eName)) {
				evSet = null;
				mode = MODE_GEOGEBRA;
			}
			break;

		case MODE_ALGEBRA_VIEW:
			if ("algebraView".equals(eName)) {
				mode = MODE_GEOGEBRA;
			}
			break;

		case MODE_SPREADSHEET_VIEW:
			if ("spreadsheetView".equals(eName)) {
				mode = MODE_GEOGEBRA;
			}
			break;

		case MODE_DATA_COLLECTION_VIEW:
			if ("dataCollectionView".equals(eName)) {
				mode = MODE_GEOGEBRA;
			}
			break;

		case MODE_PROBABILITY_CALCULATOR:
			if ("probabilityCalculator".equals(eName)) {
				mode = MODE_GEOGEBRA;
			} else {
				endProbabilityCalculator(eName);
			}
			break;

		// case MODE_CAS_VIEW:
		// if ("casView".equals(eName))
		// mode = MODE_GEOGEBRA;
		// break;

		case MODE_KERNEL:
			if ("kernel".equals(eName)) {
				mode = MODE_GEOGEBRA;
			}
			break;

		case MODE_GUI:
			if ("gui".equals(eName)) {
				mode = MODE_GEOGEBRA;
			}
			break;
		case MODE_DATA_ANALYSIS:
			if ("dataAnalysis".equals(eName)) {
				mode = MODE_GUI;
			}
			break;
		case MODE_GUI_PERSPECTIVES:
			if ("perspectives".equals(eName)) {
				mode = MODE_GUI;
			}
			endGuiPerspectivesElement(); // save all perspectives
			break;

		case MODE_GUI_PERSPECTIVE:
			if ("perspective".equals(eName)) {
				mode = MODE_GUI_PERSPECTIVES;
			}
			endGuiPerspectiveElement(); // save views & panes of the perspective
			break;

		case MODE_GUI_PERSPECTIVE_PANES:
			if ("panes".equals(eName)) {
				mode = MODE_GUI_PERSPECTIVE;
			}
			break;

		case MODE_GUI_PERSPECTIVE_VIEWS:
			if ("views".equals(eName)) {
				mode = MODE_GUI_PERSPECTIVE;
			}
			break;

		case MODE_CONSTRUCTION:
			endConstructionElement(eName);
			break;

		case MODE_DEFAULTS:
			endDefaultElement(eName);
			break;

		case MODE_MACRO:
			if ("macro".equals(eName)) {
				endMacro();
				mode = MODE_GEOGEBRA;
			}
			break;

		case MODE_ASSIGNMENT:
			endExerciseElement(eName);
			break;

		case MODE_GEOGEBRA:
			if ("geogebra".equals(eName)) {
				// start animation if necessary
				if (startAnimation) {
					if (app.isDesktop()) {
						// start later, in initInBackground()
						kernel.setWantAnimationStarted(true);
					} else {
						kernel.getAnimatonManager().startAnimation();
					}
				}

				// perform tasks to maintain backward compability
				if (hasGuiElement) {
					if (ggbFileFormat < 3.3) {
						createCompabilityLayout();
					} else if (!isPreferencesXML
							&& tmp_perspectives.isEmpty()) {
						// a specific 4.2 ggb file needed this
						createCompabilityLayout();
					}
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
			resetEVsettingsNeeded = true;
		} else if ("algebraView".equals(eName)) {
			mode = MODE_ALGEBRA_VIEW;
		} else if ("kernel".equals(eName)) {
			// default value
			// (make sure old files work)
			kernel.setUsePathAndRegionParameters(PathRegionHandling.ON);
			mode = MODE_KERNEL;
		} else if ("spreadsheetView".equals(eName)) {
			mode = MODE_SPREADSHEET_VIEW;
		} else if ("dataCollectionView".equals(eName)) {
			mode = MODE_DATA_COLLECTION_VIEW;
		} else if ("scripting".equals(eName)) {
			startScriptingElement(attrs);
		} else if ("probabilityCalculator".equals(eName)) {
			mode = MODE_PROBABILITY_CALCULATOR;
		} else if ("gui".equals(eName)) {
			mode = MODE_GUI;
			hasGuiElement = true;
			isPreferencesXML = false;

			// if (ggbFileFormat < 3.3) // safe to reset every time
			tmp_perspective = new Perspective("tmp");
			tmp_perspectives.clear();

		} else if ("macro".equals(eName)) {
			mode = MODE_MACRO;
			initMacro(attrs);
		} else if ("assignment".equals(eName)) {
			mode = MODE_ASSIGNMENT;
			initExercise(attrs);
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
		} else if ("defaults".equals(eName)) {
			mode = MODE_DEFAULTS;
			constMode = MODE_DEFAULTS;
		} else {
			Log.error("unknown tag in <geogebra>: " + eName);
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
			double opacity = Double.parseDouble(attrs.get("opacity"));
			kbs.setKeyboardOpacity(opacity);
			boolean showOnStart = Boolean.parseBoolean(attrs.get("show"));
			kbs.setShowKeyboardOnStart(showOnStart);
			kbs.setKeyboardLocale(attrs.get("language"));
		} catch (RuntimeException e) {
			e.printStackTrace();
			Log.error("error in element <keyboard>");
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
			Log.error("unknown tag in <macro>: " + eName);
		}
	}

	private void startResultElement(String eName,
			LinkedHashMap<String, String> attrs) {
		if ("result".equals(eName)) {
			String name = attrs.get("name");
			String hint = attrs.get("hint");
			String fractionS = attrs.get("fraction");
			if (hint != null && !hint.isEmpty()) {
				assignment.setHintForResult(Result.valueOf(name), hint);
			}
			if (fractionS != null && !fractionS.isEmpty()) {
				assignment.setFractionForResult(Result.valueOf(name),
						Float.parseFloat(fractionS));
			}
		} else {
			Log.error("unknown tag in <assignment>: " + eName);
		}

	}

	// ====================================
	// <euclidianView3D> only used in 3D
	// ====================================
	/**
	 * only used in MyXMLHandler3D
	 * 
	 * @param eName
	 *            element name
	 * @param attrs
	 *            attributes
	 */
	protected void startEuclidianView3DElement(String eName,
			LinkedHashMap<String, String> attrs) {
		Log.debug("TODO : warn that it's a 3D file");
	}

	// ====================================
	// <euclidianView>
	// ====================================
	/**
	 * check if eName equals "viewId" and set evSet to the correct settings
	 * (only used for 3D)
	 * 
	 * @param eName
	 *            element name
	 * @param attrs
	 *            attributes
	 */
	protected void startEuclidianViewElementCheckViewId(String eName,
			LinkedHashMap<String, String> attrs) {
		// only used in 3D
	}

	/**
	 * switch name for euclidian view element
	 * 
	 * @param eName
	 *            element name
	 * @param attrs
	 *            attributes
	 * @param firstChar
	 *            first character of element name
	 * @return true if ok
	 */
	protected boolean startEuclidianViewElementSwitch(String eName,
			LinkedHashMap<String, String> attrs, char firstChar) {

		boolean ok = true;

		switch (firstChar) {
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
			} else if ("labelStyle".equals(eName)) {
				ok = handleLabelStyle(evSet, attrs);
				break;
			}

		case 's':
			if ("size".equals(eName)) {
				ok = handleEvSize(evSet, attrs);
				break;
			}
			break;
		case 'v':
			if ("viewNumber".equals(eName)) {
				/*
				 * moved earlier, must check first int number =
				 * Integer.parseInt((String) attrs.get("viewNo"));
				 * if(number==2){ viewNo=number; }
				 */
				ok = true;
				break;
			} else if ("viewId".equals(eName)) {
				/*
				 * moved earlier, must check first if for EuclidianViewForPlane
				 */
				ok = true;
				break;
			}

		default:
			Log.error("unknown tag in <euclidianView>: " + eName);
		}

		return ok;
	}

	private boolean handleExtraTag(LinkedHashMap<String, String> attrs) {
		AlgoBarChart algo = (AlgoBarChart) geo.getParentAlgorithm();
		if (!"".equals(attrs.get("key")) && !"".equals(attrs.get("value"))
				&& !"".equals(attrs.get("barNumber"))) {
			if (attrs.get("key").equals("barAlpha")) {
				algo.setBarAlpha(Float.parseFloat(attrs.get("value")),
						Integer.parseInt(attrs.get("barNumber")));
				return true;
			} else if (attrs.get("key").equals("barHatchDistance")) {
				algo.setBarHatchDistance(Integer.parseInt(attrs.get("value")),
						Integer.parseInt(attrs.get("barNumber")));
				return true;
			} else if (attrs.get("key").equals("barFillType")) {
				algo.setBarFillType(
						FillType.values()[Integer.parseInt(attrs.get("value"))],
						Integer.parseInt(attrs.get("barNumber")));
				return true;
			} else if (attrs.get("key").equals("barHatchAngle")) {
				algo.setBarHatchAngle(Integer.parseInt(attrs.get("value")),
						Integer.parseInt(attrs.get("barNumber")));
				return true;
			} else if (attrs.get("key").equals("barImage")) {
				algo.setBarImage(attrs.get("value"),
						Integer.parseInt(attrs.get("barNumber")));
				return true;
			} else if (attrs.get("key").equals("barSymbol")) {
				algo.setBarSymbol(attrs.get("value"),
						Integer.parseInt(attrs.get("barNumber")));
				return true;
			} else if (attrs.get("key").equals("barColor")) {
				String[] c = attrs.get("value").split(",");
				algo.setBarColor(
						GColor.newColor(Integer.parseInt(c[0].substring(5)),
								Integer.parseInt(c[1]), Integer.parseInt(c[2])),
						Integer.parseInt(attrs.get("barNumber")));
				return true;
			}
		}
		return false;
	}

	private void startEuclidianViewElement(String eName,
			LinkedHashMap<String, String> attrs) {

		// must do this first
		if ("viewNumber".equals(eName)) {
			int number = Integer.parseInt(attrs.get("viewNo"));
			if (number == 2) {
				evSet = app.getSettings().getEuclidian(2);
			} else {
				evSet = app.getSettings().getEuclidian(1);
			}
		} else {
			startEuclidianViewElementCheckViewId(eName, attrs);
		}

		if (evSet == null) {
			evSet = app.getSettings().getEuclidian(1);
		}

		// make sure eg is reset the first time (for each EV) we get the
		// settings
		// "viewNumber" not stored for EV1 so we need to do this here
		if (resetEVsettingsNeeded) {
			resetEVsettingsNeeded = false;
			evSet.reset();
		}

		if (!startEuclidianViewElementSwitch(eName, attrs, firstChar(eName))) {
			Log.error("error in <euclidianView>: " + eName);
		}
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

			if ("spreadsheetCellFormat".equals(eName)) {
				ok = handleSpreadsheetFormat(attrs);
				break;
			}

		default:
			Log.error("unknown tag in <spreadsheetView>: " + eName);
		}

		if (!ok) {
			Log.error("error in <spreadsheetView>: " + eName);
		}
	}

	// ====================================
	// <DataCollectionView>
	// ====================================
	private void startDataCollectionViewElement(String eName,
			LinkedHashMap<String, String> attrs) {
		Types type = Types.lookup(eName);
		String mappedGeoLabel = attrs.get("geo");

		if (type != null) {
			Log.debug("found sensor mapping " + type + " = " + mappedGeoLabel);
			DataCollectionSettings settings = app.getSettings()
					.getDataCollection();
			settings.mapSensorToGeo(type, mappedGeoLabel);
		} else {
			Log.error("unknown tag in <dataCollectionView>: " + eName + " = "
					+ geo);
		}
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
				if (app.isUsingFullGui()) {
					ok = handleProbabilityDistribution(attrs);
				}
				break;
			}
		case 'i':
			if ("interval".equals(eName)) {
				if (app.isUsingFullGui()) {
					ok = handleProbabilityInterval(attrs);
				}
				break;
			}
		case 's':
			if ("statisticsCollection".equals(eName)) {
				ok = handleStatisticsCollection(attrs);
				break;
			}
		case 'e':
			if ("entry".equals(eName)) {
				ok = handleEntry(attrs);
				break;
			}
		default:
			Log.error("unknown tag in <probabilityCalculator>: " + eName);
		}

		if (!ok) {
			Log.error("error in <probabilityCalculator>: " + eName);
		}
	}

	private boolean handleEntry(LinkedHashMap<String, String> attrs) {
		if (entries == null) {
			entries = new ArrayList<>();
		}
		String val = attrs.get("val");
		entries.add("".equals(val) ? null : val);
		return true;
	}

	private boolean handleProbabilityDistribution(
			LinkedHashMap<String, String> attrs) {

		try {
			int distributionType = Integer.parseInt(attrs.get("type"));
			app.getSettings().getProbCalcSettings()
					.setDistributionType(Dist.values()[distributionType]);

			boolean isCumulative = parseBoolean(attrs.get("isCumulative"));
			app.getSettings().getProbCalcSettings().setCumulative(isCumulative);

			// get parameters from comma delimited string
			String parmString = attrs.get("parameters");
			String[] parmStringArray = parmString.split(",");
			double[] parameters = new double[parmStringArray.length];
			for (int i = 0; i < parmStringArray.length; i++) {
				parameters[i] = StringUtil.parseDouble(parmStringArray[i]);
			}

			app.getSettings().getProbCalcSettings().setParameters(parameters);

			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleProbabilityInterval(
			LinkedHashMap<String, String> attrs) {

		try {
			int probMode = Integer.parseInt(attrs.get("mode"));
			app.getSettings().getProbCalcSettings().setProbMode(probMode);

			app.getSettings().getProbCalcSettings()
					.setLow(StringUtil.parseDouble(attrs.get("low")));
			app.getSettings().getProbCalcSettings()
					.setHigh(StringUtil.parseDouble(attrs.get("high")));

			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleStatisticsCollection(
			LinkedHashMap<String, String> attrs) {

		try {
			entries = null;
			StatisticsCollection stats = app.getSettings().getProbCalcSettings()
					.getCollection();
			stats.mean = StringUtil.parseDouble(attrs.get("mean"));
			stats.n = StringUtil.parseDouble(attrs.get("n"));
			stats.sd = StringUtil.parseDouble(attrs.get("sd"));
			stats.count = StringUtil.parseDouble(attrs.get("count"));

			stats.mean2 = StringUtil.parseDouble(attrs.get("mean2"));
			stats.n2 = StringUtil.parseDouble(attrs.get("n2"));
			stats.sd2 = StringUtil.parseDouble(attrs.get("sd2"));
			stats.count2 = StringUtil.parseDouble(attrs.get("count2"));

			stats.nullHyp = StringUtil.parseDouble(attrs.get("nullHyp"));
			stats.level = StringUtil.parseDouble(attrs.get("level"));
			stats.setActive(parseBoolean(attrs.get("active")));
			stats.showExpected = parseBoolean(attrs.get("showExpected"));
			stats.showDiff = parseBoolean(attrs.get("showDiff"));
			stats.showColPercent = parseBoolean(attrs.get("showColPercent"));
			stats.showRowPercent = parseBoolean(attrs.get("showRowPercent"));
			stats.setTail(attrs.get("tail"));
			stats.setSelectedProcedure(Procedure.valueOf(attrs.get("procedure")));
			if (!StringUtil.empty(attrs.get("columns"))) {
				stats.columns = (int) StringUtil
					.parseDouble(attrs.get("columns"));
			}

			return true;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void endProbabilityCalculator(String name) {
		if ("statisticsCollection".equals(name) && entries != null) {
			StatisticsCollection stats = app.getSettings().getProbCalcSettings()
					.getCollection();
			int cols = stats.columns;
			stats.chiSquareData = new String[entries.size() / cols][cols];

			for (int i = 0; i < entries.size(); i++) {
				stats.chiSquareData[i / cols][i % cols] = entries
						.get(i);
			}
		}
	}

	// ====================================
	// <AlgebraView>
	// ====================================
	/**
	 * @param attrs
	 *            attributes TODO create some actual attributes
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
			Log.error("unknown tag in <algebraView>: " + eName);
		}

		if (!ok) {
			Log.error("error in <algebraView>: " + eName);
		}
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
	// Log.error("unknown tag in <casView>: " + eName);
	// }
	//
	// if (!ok)
	// Log.error("error in <casView>: " + eName);
	//
	// }

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
				ev.setCoordSystem(xZero, yZero, scale, yscale, true);

				xmin.put(ev, null);
				xmax.put(ev, null);
				ymin.put(ev, null);
				ymax.put(ev, null);
				return true;
			} catch (RuntimeException e) {
				return false;
			}
		}
		try {
			xmin.put(ev, attrs.get("xMin"));
			xmax.put(ev, attrs.get("xMax"));
			ymin.put(ev, attrs.get("yMin"));
			ymax.put(ev, attrs.get("yMax"));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	/**
	 * Basic ev settings like grid / axes visible
	 * 
	 * @param ev
	 *            settings
	 * @param attrs
	 *            tag attributes
	 * @return success
	 */
	protected boolean handleEvSettings(EuclidianSettings ev,
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
				ev.setGridIsBold(parseBoolean(attrs.get("gridIsBold")));
			} catch (RuntimeException e) {
				// not a number: ignore
			}

			try {
				if (attrs.get("lockedAxesRatio") != null) {
					ev.setLockedAxesRatio(StringUtil
							.parseDouble(attrs.get("lockedAxesRatio")));
				}
			} catch (RuntimeException e) {
				// not a number: ignore
			}

			try {
				ev.setGridType(Integer.parseInt(attrs.get("gridType")));
			} catch (RuntimeException e) {
				// not a number: ignore
			}

			String str = attrs.get("pointCapturing");
			if (str != null) {
				// before GeoGebra 2.7 pointCapturing was either "true" or
				// "false"
				// now pointCapturing holds an int value
				int pointCapturingMode;
				if ("false".equals(str)) {
					pointCapturingMode = 0;
				} else if ("true".equals(str)) {
					pointCapturingMode = 1;
				} else {
					// int value
					pointCapturingMode = Integer.parseInt(str);

					// bug: POINT_CAPTURING_STICKY_POINTS written to XML
					// sometimes
					if (pointCapturingMode > EuclidianStyleConstants.POINT_CAPTURING_XML_MAX) {
						pointCapturingMode = EuclidianStyleConstants.POINT_CAPTURING_DEFAULT;
					}
				}
				ev.setPointCapturing(pointCapturingMode);
			} else {
				ev.setPointCapturing(
						EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC);
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

			String strBooleanSize = attrs.get("checkboxSize");
			if (strBooleanSize != null) {
				app.setCheckboxSize(Integer.parseInt(strBooleanSize));
			}
			// ev.setBooleanSize(Integer.parseInt(strBooleanSize));

			boolean asm = parseBoolean(attrs.get("allowShowMouseCoords"));
			ev.setAllowShowMouseCoords(asm);

			String att = attrs.get("allowToolTips");
			if (att != null) {
				ev.setAllowToolTips(Integer.parseInt(att));
			} else {
				ev.setAllowToolTips(EuclidianStyleConstants.TOOLTIPS_AUTOMATIC);
			}

			String del = attrs.get("deleteToolSize");
			if (del != null) {
				ev.setDeleteToolSize(Integer.parseInt(del));
			}

			// v3.0: appearance of right angle
			String strRightAngleStyle = attrs.get("rightAngleStyle");
			if (strRightAngleStyle == null) {
				// before v3.0 the default was a dot to show a right angle
				// ev.setRightAngleStyle(EuclidianView.RIGHT_ANGLE_STYLE_DOT);
				if (!ev.is3D()) {
					app.setRightAngleStyle(
							EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT);
				} else {
					app.setRightAngleStyle(
							app.getLocalization().getRightAngleStyle());
				}
			} else {
				if (!ev.isViewForPlane()) {
					// ev.setRightAngleStyle(Integer.parseInt(strRightAngleStyle));
					app.setRightAngleStyle(
							Integer.parseInt(strRightAngleStyle));
				}
			}

			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	// is there a reason why it was static?
	private boolean handleEvSize(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		// removed, needed to resize applet correctly
		// if (app.isApplet())
		// return true;

		try {
			int width;
			int height;
			if (!isPreferencesXML) {
				// border excluded in getAppletWidth
				width = (app.getAppletWidth() > 0 && !app.getUseFullGui())
						? app.getAppletWidth()
						: Integer.parseInt(attrs.get("width"));
				height = (app.getAppletHeight() > 0
						&& !app.getUseFullGui())
								? app.getAppletHeight()
						: Integer.parseInt(attrs.get("height"));

				ev.setPreferredSize(
						AwtFactory.getPrototype().newDimension(width, height));
				ev.setSizeFromFile(AwtFactory.getPrototype().newDimension(
						Integer.parseInt(attrs.get("width")),
						Integer.parseInt(attrs.get("height"))));
			}
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleSpreadsheetSize(LinkedHashMap<String, String> attrs) {
		if (app.isApplet() && !app.isHTML5Applet()) {
			return true;
		}

		try {
			int width = Integer.parseInt(attrs.get("width"));
			int height = Integer.parseInt(attrs.get("height"));
			app.getSettings().getSpreadsheet().setPreferredSize(
					AwtFactory.getPrototype().newDimension(width, height));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleSpreadsheetColumn(
			LinkedHashMap<String, String> attrs) {

		try {
			int col = Integer.parseInt(attrs.get("id"));
			int width = Integer.parseInt(attrs.get("width"));
			app.getSettings().getSpreadsheet().addWidth(col, width);
			return true;
		} catch (RuntimeException e) {
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
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleSpreadsheetFormat(
			LinkedHashMap<String, String> attrs) {

		try {
			String cellFormat = attrs.get("formatMap");
			app.getSettings().getSpreadsheet().setCellFormat(cellFormat);
			return true;

		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleSpreadsheetRow(LinkedHashMap<String, String> attrs) {

		try {
			int row = Integer.parseInt(attrs.get("id"));
			int height = Integer.parseInt(attrs.get("height"));
			app.getSettings().getSpreadsheet().addHeight(row, height);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleSpreadsheetLayout(
			LinkedHashMap<String, String> attrs) {

		SpreadsheetSettings settings = app.getSettings().getSpreadsheet();
		try {
			settings.setShowFormulaBar(
					parseBoolean(attrs.get("showFormulaBar")));
			settings.setShowGrid(parseBoolean(attrs.get("showGrid")));
			settings.setShowColumnHeader(
					parseBoolean(attrs.get("showColumnHeader")));
			settings.setShowRowHeader(parseBoolean(attrs.get("showRowHeader")));
			settings.setShowHScrollBar(
					parseBoolean(attrs.get("showHScrollBar")));
			settings.setShowVScrollBar(
					parseBoolean(attrs.get("showVScrollBar")));
			settings.setAllowSpecialEditor(
					parseBoolean(attrs.get("allowSpecialEditor")));
			settings.setAllowToolTips(parseBoolean(attrs.get("allowToolTips")));
			settings.setEqualsRequired(
					parseBoolean(attrs.get("equalsRequired")));
			settings.setEnableAutoComplete(
					parseBoolean(attrs.get("autoComplete")));
			return true;

		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleSpreadsheetInitalSelection(
			LinkedHashMap<String, String> attrs) {

		SpreadsheetSettings settings = app.getSettings().getSpreadsheet();
		try {

			int hScroll = Integer.parseInt(attrs.get("hScroll"));
			int vScroll = Integer.parseInt(attrs.get("vScroll"));
			settings.setScrollPosition(new GPoint(hScroll, vScroll));

			int row = Integer.parseInt(attrs.get("row"));
			int column = Integer.parseInt(attrs.get("column"));
			settings.setScrollPosition(new GPoint(row, column));

			return true;

		} catch (RuntimeException e) {
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
	// } catch(RuntimeException e) {
	// e.printStackTrace();
	// return false;
	// }
	// }
	/**
	 * Background color handlig for view
	 * 
	 * @param evSet
	 *            settings
	 * @param attrs
	 *            tag attributes
	 * @return success
	 */
	protected static boolean handleBgColor(EuclidianSettings evSet,
			LinkedHashMap<String, String> attrs) {
		GColor col = handleColorAttrs(attrs);
		if (col == null) {
			return false;
		}
		evSet.setBackground(col);
		return true;
	}

	private static boolean handleAxesColor(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		GColor col = handleColorAttrs(attrs);
		if (col == null) {
			return false;
		}
		ev.setAxesColor(col);
		return true;
	}

	private static boolean handleGridColor(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		GColor col = handleColorAttrs(attrs);
		if (col == null) {
			return false;
		}
		ev.setGridColor(col);
		return true;
	}

	private static boolean handleLineStyle(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		try {
			ev.setAxesLineStyle(Integer.parseInt(attrs.get("axes")));
			ev.setGridLineStyle(Integer.parseInt(attrs.get("grid")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	/**
	 * Label style for axes
	 * 
	 * @param ev
	 *            euclidian settings
	 * @param attrs
	 *            tag attributes
	 * @return success
	 */
	protected static boolean handleLabelStyle(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		try {
			ev.setAxisFontStyle(Integer.parseInt(attrs.get("axes")));
			ev.setAxesLabelsSerif("true".equals(attrs.get("serif")));
			return true;
		} catch (RuntimeException e) {
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
			if (theta != null) {
				dists[2] = StringUtil.parseDouble(attrs.get("distTheta"));
			}
			else {
				dists[2] = Math.PI / 6; // default
			}

			ev.setGridDistances(dists);

			return true;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * <axis id="0" label="x" unitLabel="x" showNumbers="true" tickDistance=
	 * "2"/>
	 * 
	 * @param ev
	 *            settings
	 * @param attrs
	 *            attributes of &lt;axis> tag
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

			String selectionAllowedStr = attrs.get("selectionAllowed");
			if (selectionAllowedStr != null) {
				boolean selectionAllowed = parseBoolean(selectionAllowedStr);
				ev.setSelectionAllowed(axis, selectionAllowed);
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
			String tickExpr = attrs.get("tickExpression");
			if (tickExpr != null) {
				if (axis == 0) {
					xtick.put(evSet, tickExpr);
				} else if (axis == 1) {
					ytick.put(evSet, tickExpr);
				} else {
					ztick.put(evSet, tickExpr);
				}
			}
			String strTickDist = attrs.get("tickDistance");
			if (strTickDist != null) {
				double tickDist = StringUtil.parseDouble(strTickDist);
				GeoNumeric distNum = new GeoNumeric(cons, tickDist);
				if (StringUtil.empty(tickExpr)
						&& DoubleUtil.isInteger(tickDist * 24 / Math.PI)) {
					int num = (int) Math.round(tickDist * 24 / Math.PI);
					int gcd = (int) Kernel.gcd(num, 24);
					int den = 24 / gcd;
					num = num / gcd;
					ExpressionNode def = new ExpressionNode(kernel, Math.PI)
							.multiplyR(num);
					if (den != 1) {
						def = def.divide(den);
					}
					distNum.setDefinition(def);
				}
				ev.setAxesNumberingDistance(distNum, axis);
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
		} catch (RuntimeException e) {
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
		} else if (!"uses3D".equals(eName)) {
			Log.error("unknown tag in <kernel>: " + eName);
		}
	}

	private boolean handleAngleUnit(LinkedHashMap<String, String> attrs) {
		if (attrs == null) {
			return false;
		}
		String angleUnit = attrs.get("val");
		if (angleUnit == null) {
			return false;
		}

		if ("degree".equals(angleUnit)) {
			kernel.setAngleUnit(Kernel.ANGLE_DEGREE);
		} else if ("radiant".equals(angleUnit)) {
			kernel.setAngleUnit(Kernel.ANGLE_RADIANT);
		} else {
			return false;
		}
		return true;
	}

	private boolean handleAlgebraStyle(LinkedHashMap<String, String> attrs) {
		try {
			kernel.setAlgebraStyle(Integer.parseInt(attrs.get("val")));
			if (attrs.containsKey("spreadsheet")) {
				kernel.setAlgebraStyleSpreadsheet(
						Integer.parseInt(attrs.get("spreadsheet")));
			} else {
				// old files only have val, use that for spreadsheet too
				kernel.setAlgebraStyleSpreadsheet(
						Integer.parseInt(attrs.get("val")));
			}
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleKernelCoordStyle(
			LinkedHashMap<String, String> attrs) {
		try {
			kernel.setCoordStyle(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleKernelInvTrig(LinkedHashMap<String, String> attrs) {
		try {
			kernel.setInverseTrigReturnsAngle(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleKernelDecimals(LinkedHashMap<String, String> attrs) {
		try {
			kernel.setPrintDecimals(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleKernelStartAnimation(
			LinkedHashMap<String, String> attrs) {
		try {
			startAnimation = parseBoolean(attrs.get("val"));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleKernelLocalization(
			LinkedHashMap<String, String> attrs) {
		try {
			boolean digits = parseBoolean(attrs.get("digits"));
			loc.setUseLocalizedDigits(digits, app);
			boolean labels = parseBoolean(attrs.get("labels"));
			loc.setUseLocalizedLabels(labels);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	/**
	 * Handle the casSettings XML element which is responsible for setting the
	 * Options CAS dialog
	 * 
	 * @param attrs
	 *            - mapping of attributes names and values
	 * @return whether the operation was successful
	 */
	private boolean handleCasSettings(LinkedHashMap<String, String> attrs) {
		try {
			boolean expRoots = parseBoolean(attrs.get("expRoots"));
			app.getSettings().getCasSettings().setShowExpAsRoots(expRoots);
			int timeout = Integer.parseInt(attrs.get("timeout"));
			if (timeout > 0) {
				app.getSettings().getCasSettings().setTimeoutMilliseconds(
						OptionsCAS.getTimeoutOption(timeout) * 1000);
			}
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleKernelFigures(LinkedHashMap<String, String> attrs) {
		try {
			kernel.setPrintFigures(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleKernelContinuous(
			LinkedHashMap<String, String> attrs) {
		try {
			kernel.setContinuous(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleKernelUsePathAndRegionParameters(
			LinkedHashMap<String, String> attrs) {
		try {
			kernel.setUsePathAndRegionParameters(
					PathRegionHandling.parse(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
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
			if ("consProtColumns".equals(eName)) {
				ok = handleConsProtColumns(app, attrs);
			} else if ("consProtocol".equals(eName)) {
				ok = handleConsProtocol(attrs);
			} else if ("consProtNavigationBar".equals(eName)) {
				ok = handleConsProtNavigationBar(app, attrs);
			}
			break;
		case 'd':
			if ("dataAnalysis".equals(eName)) {
				ok = handleDataAnalysis(attrs);
			}
			break;
		case 'f':
			if ("font".equals(eName)) {
				ok = handleFont(app, attrs);
			}
			break;

		case 'm':
			if ("menuFont".equals(eName)) {
				ok = handleMenuFont(app, attrs);
			}
			break;

		case 'l':
			if ("labelingStyle".equals(eName)) {
				ok = handleLabelingStyle(app, attrs);
			}
			break;

		case 'p':
			if ("perspectives".equals(eName)) {
				mode = MODE_GUI_PERSPECTIVES;
				tmp_perspectives.clear();
			}
			break;

		case 's':
			if ("show".equals(eName)) {
				ok = handleGuiShow(app, attrs);
			} else if ("splitDivider".equals(eName)) {
				ok = compLayout.handleSplitDivider(attrs);
			} else if ("settings".equals(eName)) {
				ok = handleGuiSettings(app, attrs);
			}
			break;

		case 't':
			if ("toolbar".equals(eName)) {
				ok = handleToolbar(attrs);
			} else if ("tooltipSettings".equals(eName)) {
				ok = handleTooltipSettings(app, attrs);
			}
			break;

		case 'w':
			if ("window".equals(eName)) {
				ok = handleWindowSize(app, attrs);
			}
			break;

		default:
			Log.error("unknown tag in <gui>: " + eName);
		}

		if (!ok) {
			Log.error("error in <gui>: " + eName);
		}
	}

	private boolean handleDataAnalysis(LinkedHashMap<String, String> attrs) {
		mode = MODE_DATA_ANALYSIS;
		try {
			app.getSettings().getDataAnalysis()
					.setMode(Integer.parseInt(attrs.get("mode")));
			app.getSettings().getDataAnalysis()
					.setRegression(Regression.valueOf(attrs.get("regression")));
			app.getSettings().getDataAnalysis().setPlotType(0,
					PlotType.valueOf(attrs.get("plot1")));
			app.getSettings().getDataAnalysis().setPlotType(1,
					PlotType.valueOf(attrs.get("plot1")));
		} catch (RuntimeException e) {
			return false;
		}
		return true;
	}

	/**
	 * Take care of backward compatibility for the dynamic layout component
	 */
	private void createCompabilityLayout() {
		this.compLayout.update(tmp_perspective, app);

		tmp_perspectives = new ArrayList<>();
		tmp_perspectives.add(tmp_perspective);
		app.setPreferredSize(compLayout.getDimension());
		app.setTmpPerspectives(tmp_perspectives);
	}

	private static boolean handleConsProtColumns(App app,
			LinkedHashMap<String, String> attrs) {
		try {

			boolean[] colsVis = new boolean[attrs.keySet().size()];

			ArrayList<String> keys = new ArrayList<>(attrs.keySet());
			for (String key : keys) {
				int k = Integer.parseInt(key.substring(3));
				colsVis[k] = Boolean.parseBoolean(attrs.get(key));
			}

			ConstructionProtocolSettings cpSettings = app.getSettings()
					.getConstructionProtocol();
			cpSettings.setColsVisibility(colsVis);

			return true;
		} catch (RuntimeException e) {
			Log.debug(e);
			return false;
		}
	}

	private boolean handleConsProtocol(LinkedHashMap<String, String> attrs) {
		try {
			// boolean useColors = parseBoolean((String)
			// attrs.get("useColors"));
			// TODO: set useColors for consProt

			boolean showOnlyBreakpoints = parseBoolean(
					attrs.get("showOnlyBreakpoints"));
			kernel.getConstruction()
					.setShowOnlyBreakpoints(showOnlyBreakpoints);

			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleConsProtNavigationBar(App app1,
			LinkedHashMap<String, String> attrs) {
		try {

			boolean playButton = parseBoolean(attrs.get("playButton"));
			double playDelay = StringUtil.parseDouble(attrs.get("playDelay"));
			boolean showProtButton = parseBoolean(attrs.get("protButton"));

			String showStr = attrs.get("show");
			if (showStr == null) { // new XML
				String idStr = attrs.get("id");
				for (String id : idStr.split(" ")) {
					int viewId = Integer.parseInt(id);
					app1.setShowConstructionProtocolNavigation(true, viewId,
							playButton, playDelay, showProtButton);
				}
			} else { // old XML
				boolean show = parseBoolean(attrs.get("show"));
				// Maybe there is not guiManager yet. In this case we store the
				// navigation bar's states in ConstructionProtocolSettings
				app1.setShowConstructionProtocolNavigation(show,
						App.VIEW_EUCLIDIAN, playButton, playDelay,
						showProtButton);
			}

			// construction step: handled at end of parsing
			String strConsStep = attrs.get("consStep");
			if (strConsStep != null) {
				consStep = Integer.parseInt(strConsStep);
			}

			return true;
		} catch (RuntimeException e) {
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
			// if (ggbFileFormat < 3.3) {// also used in some special, newer
			// files
			compLayout.showAlgebra = parseBoolean(attrs.get("algebraView"));

			compLayout.showSpreadsheet = parseBoolean(
					attrs.get("spreadsheetView"));

			String str = attrs.get("auxiliaryObjects");
			boolean auxiliaryObjects = (str != null && "true".equals(str));
			app1.setShowAuxiliaryObjects(auxiliaryObjects);

			str = attrs.get("algebraInput");
			boolean algebraInput = (str == null || "true".equals(str));
			tmp_perspective.setShowInputPanel(algebraInput);

			str = attrs.get("cmdList");
			boolean cmdList = (str == null || "true".equals(str));
			tmp_perspective.setShowInputPanelCommands(cmdList);

			return true;
		} catch (RuntimeException e) {
			e.printStackTrace();
			Log.error(e.getMessage() + ": " + e.getCause());
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

		// set that XML load is a preferences settings
		isPreferencesXML = true;

		try {
			boolean ignoreDocument = !attrs.get("ignoreDocument")
					.equals("false");
			app.getSettings().getLayout()
					.setIgnoreDocumentLayout(ignoreDocument);

			boolean showTitleBar = !attrs.get("showTitleBar").equals("false");
			app.getSettings().getLayout().setShowTitleBar(showTitleBar);

			if (attrs.containsKey("allowStyleBar")) {
				boolean allowStyleBar = !attrs.get("allowStyleBar")
						.equals("false");
				app.getSettings().getLayout().setAllowStyleBar(allowStyleBar);
			}

			return true;
		} catch (RuntimeException e) {
			e.printStackTrace();
			Log.warn(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}



	private boolean handleToolbar(LinkedHashMap<String, String> attrs) {
		try {
			String toolbarStr = attrs.get("str");
			if (toolbarStr != null) {
				// GeoGebra 3.2 or older

				// eg 0 39 59 || 1001 5 19 | 2 15 45 18 , 7 37 | 4 3 8 9 , 13 44
				// , 58 , 47 || 16 51 | 10 34 53 11 , 24 20 22 , 21 23 | 55 56
				// 57 , 12 || 36 46 , 38 49 50 | 30 29 54 32 31 33 | 25 52 , 17
				// 26 , 14 || 40 41 42 , 27 28 35 , 6

				// substitute 1000+x to 100000+x (macro numbers)
				StringBuilder converted = new StringBuilder();
				for (int lv = 0; lv < toolbarStr.length(); lv++) {

					char c = toolbarStr.charAt(lv);
					if (Character.isDigit(c)) {

						StringBuilder numStr = new StringBuilder();
						char cc;
						while (lv < toolbarStr.length() && Character
								.isDigit(cc = toolbarStr.charAt(lv))) {
							numStr.append(cc);
							lv++;
						}

						int num = Integer.parseInt(numStr.toString());

						if (num > 999) {
							num = num + 100000 - 1000;
						}

						converted.append(num);
						converted.append(" ");

					} else {
						// space or comma or |
						converted.append(c);
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
					tmp_perspective.setShowToolBar("true".equals(showToolBar));
				}
				String items = attrs.get("items");
				tmp_perspective.setToolbarDefinition(
						"null".equals(items) ? null : items);

				// GeoGebra 4.2 (supports toolbar position and toggling help)
				if (attrs.get("position") != null) {
					Integer toolBarPosition = Integer
							.parseInt(attrs.get("position"));
					tmp_perspective.setToolBarPosition(toolBarPosition);
					tmp_perspective.setShowToolBarHelp(
							!attrs.get("help").equals("false"));
				} else {
					tmp_perspective.setToolBarPosition(SwingConstants.NORTH);
					tmp_perspective.setShowToolBarHelp(true);
				}

			}
			return true;
		} catch (RuntimeException e) {
			Log.warn(e.getMessage() + ": " + e.getCause());
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
			GDimension size = AwtFactory.getPrototype().newDimension(
					Integer.parseInt(attrs.get("width")),
					Integer.parseInt(attrs.get("height")));
			app.setPreferredSize(size);
			return true;
		} catch (RuntimeException e) {
			Log.warn(e.getMessage() + ": " + e.getCause());
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

			app.setFontSize(guiSize, true); // set gui font size and update all
											// fonts
			return true;
		} catch (RuntimeException e) {
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
				for (int i = 0; i < Util.menuFontSizesLength(); i++) {
					if (Util.menuFontSizes(i) >= guiSize) {
						guiSize = Util.menuFontSizes(i);
						break;
					}
				}
				if (guiSize > Util
						.menuFontSizes(Util.menuFontSizesLength() - 1)) {
					guiSize = Util
							.menuFontSizes(Util.menuFontSizesLength() - 1);
				}
				app.setGUIFontSize(guiSize);
			}
			return true;
		} catch (RuntimeException e) {
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
				// not a number, do nothing (use -1)
			}
			app.setTooltipTimeout(ttt);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private static boolean handleLabelingStyle(App app,
			LinkedHashMap<String, String> attrs) {
		try {
			int style = Integer.parseInt(attrs.get("val"));
			app.setLabelingStyle(style);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	// ====================================
	// <perspectives>
	// ====================================
	private void startGuiPerspectivesElement(String eName,
			LinkedHashMap<String, String> attrs) {
		boolean ok = true;

		if ("perspective".equals(eName)) {
			ok = handlePerspective(attrs);
		} else {
			Log.warn("unknown tag in <perspectives>: " + eName);
		}
		if (!ok) {
			Log.warn("error in <perspectives>: " + eName);
		}
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
				tmp_panes = new ArrayList<>();
			} else {
				tmp_panes.clear();
			}

			if (tmp_views == null) {
				tmp_views = new ArrayList<>();
			} else {
				tmp_views.clear();
			}
			mode = MODE_GUI_PERSPECTIVE;

			return true;
		} catch (RuntimeException e) {
			Log.debug(e.getMessage() + ": " + e.getCause());
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
			Log.debug("unknown tag in <perspective>: " + eName);
		}

		if (!ok) {
			Log.debug("error in <perspective>: " + eName);
		}
	}

	private boolean handleAlgebraInput(LinkedHashMap<String, String> attrs) {
		try {
			tmp_perspective
					.setShowInputPanel(!attrs.get("show").equals("false"));
			tmp_perspective.setShowInputPanelCommands(
					!attrs.get("cmd").equals("false"));
			InputPosition ip = attrs.get("top").equals("true")
					? InputPosition.top
					: ("false".equals(attrs.get("top")) ? InputPosition.bottom
							: InputPosition.algebraView);
			tmp_perspective.setInputPosition(ip);

			return true;
		} catch (RuntimeException e) {
			Log.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	private boolean handleDockBar(LinkedHashMap<String, String> attrs) {
		try {
			tmp_perspective.setShowDockBar(!attrs.get("show").equals("false"));
			tmp_perspective.setDockBarEast(!attrs.get("east").equals("false"));

			return true;
		} catch (RuntimeException e) {
			Log.debug(e.getMessage() + ": " + e.getCause());
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

		if ("view".equals(eName)) {
			ok = handleView(attrs);
		} else {
			Log.debug("unknown tag in <views>: " + eName);
		}

		if (!ok) {
			Log.debug("error in <views>: " + eName);
		}
	}

	/**
	 * Handle a view.
	 * <view id=".." visible=".." inframe=".." stylebar=".." window=".."
	 * location=".." size=".." />
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
			boolean showStyleBar = !"false".equals(showStyleBarStr);

			// the window rectangle is given in the format "x,y,width,height"
			String[] window = attrs.get("window").split(",");
			GRectangle windowRect = AwtFactory.getPrototype().newRectangle(
					Integer.parseInt(window[0]), Integer.parseInt(window[1]),
					Integer.parseInt(window[2]), Integer.parseInt(window[3]));

			String embeddedDef = attrs.get("location");
			int embeddedSize = Integer.parseInt(attrs.get("size"));

			String plane = attrs.get("plane");
			DockPanelData dp = new DockPanelData(viewId, toolbar, isVisible,
					openInFrame, showStyleBar, windowRect, embeddedDef,
					embeddedSize, plane);
			if (app.getConfig() != null) {
				app.getConfig().adjust(dp);
			}
			tmp_views.add(dp);

			return true;
		} catch (RuntimeException e) {
			Log.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	// ====================================
	// <panes>
	// ====================================
	private void startGuiPanesElement(String eName,
			LinkedHashMap<String, String> attrs) {
		boolean ok = true;

		if ("pane".equals(eName)) {
			ok = handlePane(attrs);
		} else {
			Log.debug("unknown tag in <panes>: " + eName);
		}

		if (!ok) {
			Log.debug("error in <panes>: " + eName);
		}
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
			double dividerLocation = StringUtil
					.parseDouble(attrs.get("divider"));
			int orientation = Integer.parseInt(attrs.get("orientation"));

			tmp_panes.add(new DockSplitPaneData(location, dividerLocation,
					orientation));

			return true;
		} catch (RuntimeException e) {
			Log.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	// ====================================
	// <construction>
	// ====================================
	private void handleConstruction(LinkedHashMap<String, String> attrs) {
		try {
			cons.setAllowUnboundedAngles(
					DoubleUtil.isGreaterEqual(ggbFileFormat, 4.4));
			String title = attrs.get("title");
			String author = attrs.get("author");
			String date = attrs.get("date");
			if (title != null) {
				cons.setTitle(title);
			}
			if (author != null) {
				cons.setAuthor(author);
			}
			if (date != null) {
				cons.setDate(date);
			}
		} catch (RuntimeException e) {
			Log.error("error in <construction>");
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
			Integer viewId = null;
			if (attrs.containsKey("viewId")) {
				viewId = Integer.parseInt(attrs.get("viewId"));
			}
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
			boolean showTool = strShowInToolBar == null || parseBoolean(strShowInToolBar);
			macro.setShowInToolBar(showTool);
			macro.setViewId(viewId);

			MacroKernel macroKernel = kernel.newMacroKernel();
			macroKernel.setContinuous(false);

			// we have to change the construction object temporarily so
			// everything
			// is done in the macro construction from now on
			kernel = macroKernel;
			cons = macroKernel.getConstruction();
			parser = new GParser(macroKernel, cons);

		} catch (RuntimeException e) {
			Log.error("error in <macro>");
		}
	}

	private void endMacro() {
		// cons now holds a reference to the macroConstruction
		macro.initMacro(cons, macroInputLabels, macroOutputLabels);
		// ad the newly built macro to the kernel
		origKernel.addMacro(macro);
		// update construction resets the nearto relations in macro, so "outer
		// world" won't affect it
		cons.updateConstruction();
		// set kernel and construction back to the original values
		initKernelVars();
	}

	private void initExercise(LinkedHashMap<String, String> attrs) {
		if (exercise == null) {
			exercise = kernel.getExercise();
			exercise.reset();
		}
		String name = attrs.get("commandName");
		if (name == null) {
			name = attrs.get("toolName");
		}
		if (name == null) {
			name = attrs.get("booleanName");
			if (name != null) {
				assignment = exercise.addAssignment(name);
			}
		} else {
			Macro m = kernel.getMacro(name);
			// this should not be needed but for files saved between 41946 and
			// 42226
			// fileloading won't work (only files created in beta, probably
			// only Judith and me, but...)
			if (m == null) {
				m = kernel.getMacro(name.replace(" ", ""));
			}
			assignment = exercise.addAssignment(m);

			String op = attrs.get("checkOperation");
			if (op == null) {
				((GeoAssignment) assignment).setCheckOperation("AreEqual");
			} else {
				((GeoAssignment) assignment).setCheckOperation(op);
			}
		}
	}

	private void endExerciseElement(String eName) {
		if ("assignment".equals(eName)) {
			mode = MODE_GEOGEBRA;
		}
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
	private void startCasCell(String eName,
			LinkedHashMap<String, String> attrs) {
		// handle cas session mode
		switch (casMode) {
		case MODE_CONST_CAS_CELL:
			if ("cellPair".equals(eName)) {
				casMode = MODE_CAS_CELL_PAIR;
				startCellPair();
			} else {
				Log.error("unknown tag in <cellPair>: " + eName);
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
				Log.error("unknown tag in <cellPair>: " + eName);
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
			Log.error("unknown cas session mode:" + constMode);
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
			if ("useAsText".equals(eName)) {
				casMode = MODE_CAS_CELL_PAIR;
			}
			break;

		case MODE_CAS_INPUT_CELL:
			if ("inputCell".equals(eName)) {
				casMode = MODE_CAS_CELL_PAIR;
			}
			break;

		case MODE_CAS_OUTPUT_CELL:
			if ("outputCell".equals(eName)) {
				casMode = MODE_CAS_CELL_PAIR;
			}
			break;

		default:
			casMode = MODE_CONST_CAS_CELL; // set back mode
			Log.error("unknown cas session mode:" + constMode);
		}

	}

	private void startCellPair() {
		geoCasCell = new GeoCasCell(cons);
	}

	private void endCellPair(String eName) {
		if (geoCasCell == null) {
			Log.error("no element set for <" + eName + ">");
			return;
		}

		try {
			// create necessary algorithm and twinGeo
			boolean independentCell = !geoCasCell.hasVariablesOrCommands();
			if (independentCell) {
				// free cell, e.g. m := 7 creates twinGeo m = 7

				// if this is the first cell, and there is no input, we return
				// sometimes saved files contain one empty cell pair, see #2469
				// attachment
				if (cons.getCasCell(0) == null && geoCasCell
						.getInput(StringTemplate.defaultTemplate).equals("")) {
					return;
				}
				cons.addToConstructionList(geoCasCell, true);
				cons.addToGeoSetWithCasCells(geoCasCell);
				if (geoCasCell.isAssignmentVariableDefined()) {
					// a non-native cell may have dependent twin geo even if
					// inputs are constants
					// update twin GeoElement

					// cas is loaded
					// we need to recalculate the output
					if (kernel.getConstruction()
							.isUpdateConstructionRunning()) {
						geoCasCell.computeOutput();
					} else {
						geoCasCell.updateTwinGeo(false);
					}
					geoCasCell.setLabelOfTwinGeo();
					if (geoCasCell.hasTwinGeo() && !geoCasCell.getTwinGeo()
							.isInConstructionList()) {
						if (!geoCasCell.getTwinGeo().getParentAlgorithm()
								.isInConstructionList()) {
							geoCasCell.getTwinGeo().getParentAlgorithm()
									.addToConstructionList();
						}
					}
				} else if (geoCasCell.isOutputEmpty()
						&& kernel.isGeoGebraCASready()) { // output is computed
															// if it is empty
															// (redefinitions
															// only)
					geoCasCell.computeOutput();
				}
				// otherwise keep loaded output and avoid unnecessary
				// computation
			} else {
				// create algorithm for dependent cell
				// this also creates twinGeo if necessary
				// output is not computed again, see AlgoDependenCasCell
				// constructor
				KernelCAS.dependentCasCell(geoCasCell);
			}
		} catch (RuntimeException e) {
			Log.error("error when processing <cellpair>: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void startCellOutputElement(String eName,
			LinkedHashMap<String, String> attrs) {
		if (geoCasCell == null) {
			Log.error("no element set for <" + eName + ">");
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
			Log.error("unknown tag in <outputCell>: " + eName);
		}

		if (!ok) {
			Log.error("error in <outputCell>: " + eName);
		}

	}

	private void startCellInputElement(String eName,
			LinkedHashMap<String, String> attrs) {
		if (geoCasCell == null) {
			Log.error("no element set for <" + eName + ">");
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
			Log.error("unknown tag in <inputCell>: " + eName);
		}

		if (!ok) {
			Log.error("error in <inputCell>: " + eName);
		}
	}

	private void startCellTextElement(String eName,
			LinkedHashMap<String, String> attrs) {
		if (geoCasCell == null) {
			Log.error("no element set for <" + eName + ">");
			return;
		}

		geoCasCell.setUseAsText(true);

		boolean ok = true;

		if ("FontStyle".equals(eName)) {
			String style = attrs.get("value");
			geoCasCell.setFontStyle(Integer.parseInt(style));
		} else if ("FontSizeM".equals(eName)) {
			String size = attrs.get("value");
			if (StringUtil.parseDouble(size) > 0) {
				geoCasCell.setFontSizeMultiplier(StringUtil.parseDouble(size));
			}
		} else if ("FontColor".equals(eName)) {
			String r = attrs.get("r");
			String b = attrs.get("b");
			String g = attrs.get("g");
			geoCasCell.setFontColor(GColor.newColor(Integer.parseInt(r),
					Integer.parseInt(g), Integer.parseInt(b)));
		} else {
			Log.error("unknown tag in <useAsText>: " + eName);
		}

		if (!ok) {
			Log.error("error in <useAsText>: " + eName);
		}

	}

	private void startDefault(String eName,
			LinkedHashMap<String, String> attrs) {

		switch (constMode) {
		case MODE_DEFAULTS:
			if ("element".equals(eName)) {
				boolean old = kernel.getElementDefaultAllowed();
				kernel.setElementDefaultAllowed(true);
				constMode = MODE_DEFAULT_GEO;
				geo = getGeoElement(attrs);
				geo.setLineOpacity(255);
				kernel.setElementDefaultAllowed(old);
			} else {
				Log.error("unknown tag in <default>: " + eName);
			}
			break;

		case MODE_DEFAULT_GEO:
			startGeoElement(eName, attrs);
			break;

		default:
			Log.error("unknown default mode:" + constMode);
		}

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
				sliderTagProcessed = false;
				fontTagProcessed = false;
				symbolicTagProcessed = false;
				lineStyleTagProcessed = false;
				geo.setLineOpacity(255);
				if (geo instanceof VectorNDValue) {
					((VectorNDValue) geo)
							.setMode(((VectorNDValue) geo).getDimension() == 3
									? Kernel.COORD_CARTESIAN_3D
									: Kernel.COORD_CARTESIAN);
				} else if (geo instanceof GeoPolyLine) {
					((GeoPolyLine) geo).setVisibleInView3D(false);
				} else if (geo instanceof GeoFunction) {
					geo.setFixed(false);
				} else if (geo instanceof GeoAngle) {
					((GeoAngle) geo).setEmphasizeRightAngle(true);
				} else if (geo instanceof GeoText) {
					geo.setBackgroundColor(null);
				}
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
				Log.error("unknown tag in <construction>: " + eName);
			}
			break;

		case MODE_CONST_GEO_ELEMENT:
			startGeoElement(eName, attrs);
			break;

		case MODE_CONST_COMMAND:
			startCommandElement(eName, attrs);
			break;
		case MODE_CAS_MAP:
			handleMapEntry(attrs);
			break;
		case MODE_CONST_CAS_CELL:
			startCasCell(eName, attrs);
			break;

		default:
			Log.error("unknown construction mode:" + constMode);
		}
	}

	private void handleMapEntry(LinkedHashMap<String, String> attrs) {
		this.casMap.put(attrs.get("key"), attrs.get("val"));

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
				// if (spreadsheetTraceNeeded) {
				// // don't want to initialize trace manager unless necessary
				// app.getTraceManager().loadTraceGeoCollection();
				// }

				if (kernel == origKernel) {
					mode = MODE_GEOGEBRA;
				} else {
					// macro construction
					mode = MODE_MACRO;
				}
			}
			break;

		case MODE_CONST_GEO_ELEMENT:
			if ("element".equals(eName)) {
				if (!sliderTagProcessed && geo.isGeoNumeric()) {
					((GeoNumeric) geo).setShowExtendedAV(false);
				} else if (!fontTagProcessed && geo.isGeoText()) {
					((TextProperties) geo).setFontSizeMultiplier(1);
					((TextProperties) geo).setSerifFont(false);
					((TextProperties) geo).setFontStyle(GFont.PLAIN);
				} else if (!lineStyleTagProcessed && ((geo.isGeoFunctionNVar()
						&& ((GeoFunctionNVar) geo).isFun2Var())
						|| geo.isGeoSurfaceCartesian())) {
					geo.setLineThickness(0);
				}
				if (!symbolicTagProcessed && geo.isGeoText()) {
					((GeoText) geo).setSymbolicMode(false, false);
				}
				if (casMap != null && geo instanceof CasEvaluableFunction) {
					((CasEvaluableFunction) geo).updateCASEvalMap(casMap);
				}

				if (geo.isGeoImage()
						&& ((GeoImage) geo).isCentered()) {
					((GeoImage) geo).setCentered(true);
				}
				casMap = null;
				constMode = MODE_CONSTRUCTION;
			}

			break;

		case MODE_CONST_COMMAND:
			if ("command".equals(eName)) {
				cons.setOutputGeo(null);
				casMap = null;
				constMode = MODE_CONSTRUCTION;
			}
			break;
		case MODE_CAS_MAP:
			if ("casMap".equals(eName)) {
				constMode = casMapParent;
			}
			break;
		case MODE_CONST_CAS_CELL:
			endCasCell(eName);
			break;

		default:
			constMode = MODE_CONSTRUCTION; // set back mode
			Log.error("unknown construction mode:" + constMode);
		}
	}

	private void endDefaultElement(String eName) {
		switch (constMode) {
		case MODE_DEFAULTS:
			if ("defaults".equals(eName)) {
				mode = MODE_GEOGEBRA;
				constMode = MODE_CONSTRUCTION;
				this.processMinMaxList();
				this.processAnimationStepList();
				this.processAnimationSpeedList();
			}
			break;

		case MODE_DEFAULT_GEO:
			if ("element".equals(eName)) {
				constMode = MODE_DEFAULTS;
			}
			break;

		default:
			constMode = MODE_DEFAULTS; // set back mode
			Log.error("unknown defaults mode:" + constMode);
		}
	}

	// ====================================
	// <element>
	// ====================================

	private void processEvSizes() {
		// Set<EuclidianSettings> eSet0 = xmin.keySet();
		ArrayList<EuclidianSettings> eSet = new ArrayList<>(
				xmin.keySet());
		eSet.addAll(xtick.keySet());
		eSet.addAll(ytick.keySet());
		eSet.addAll(ztick.keySet());
		for (EuclidianSettings ev : eSet) {
			if (xmin.get(ev) == null) {
				ev.setXminObject(null, true);
			} else {
				NumberValue n = getAlgProcessor()
						.evaluateToNumeric(xmin.get(ev), handler);
				ev.setXminObject(n, true);
			}
		}
		for (EuclidianSettings ev : eSet) {
			if (xmax.get(ev) == null) {
				ev.setXmaxObject(null, true);
			} else {
				NumberValue n = getAlgProcessor()
						.evaluateToNumeric(xmax.get(ev), handler);
				ev.setXmaxObject(n, true);
			}
		}
		for (EuclidianSettings ev : eSet) {
			if (ymin.get(ev) == null) {
				ev.setYminObject(null, true);
			} else {
				NumberValue n = getAlgProcessor()
						.evaluateToNumeric(ymin.get(ev), handler);
				ev.setYminObject(n, true);
			}
		}
		for (EuclidianSettings ev : eSet) {
			if (ymax.get(ev) == null) {
				ev.setYmaxObject(null, true);
			} else {
				NumberValue n = getAlgProcessor()
						.evaluateToNumeric(ymax.get(ev), handler);
				ev.setYmaxObject(n, true);
			}
			// ev.updateBounds();
		}
		for (EuclidianSettings ev : eSet) {
			if (!StringUtil.empty(xtick.get(ev))) {

				GeoNumberValue n = getAlgProcessor()
						.evaluateToNumeric(xtick.get(ev), handler);
				ev.setAxisNumberingDistance(0, n);
			}
			// ev.updateBounds();
		}
		for (EuclidianSettings ev : eSet) {
			if (!StringUtil.empty(ytick.get(ev))) {

				GeoNumberValue n = getAlgProcessor()
						.evaluateToNumeric(ytick.get(ev), handler);
				ev.setAxisNumberingDistance(1, n);
			}
			// ev.updateBounds();
		}
		for (EuclidianSettings ev : eSet) {
			if (!StringUtil.empty(ztick.get(ev))) {

				GeoNumberValue n = getAlgProcessor()
						.evaluateToNumeric(ztick.get(ev), handler);
				ev.setAxisNumberingDistance(2, n);
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
			Log.error("attributes missing in <element>");
			return geo1;
		}

		if (defaultset == null || !kernel.getElementDefaultAllowed()) {
			// does a geo element with this label exist?
			geo1 = kernel.lookupLabel(label);

			// Application.debug(label+", geo="+geo);
			// needed for TRAC-2719
			// if geo wasn't found in construction list
			// look in cas
			if (geo1 == null) {
				geo1 = kernel.lookupCasCellLabel(label);
			}
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
		if (geo1.getGeoClassType().equals(GeoClass.POINT)
				&& ggbFileFormat < 3.3) {
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
	 * 
	 * @param eName
	 *            element name
	 * @param attrs
	 *            attributes
	 */
	protected void startGeoElement(String eName,
			LinkedHashMap<String, String> attrs) {
		if (geo == null) {
			Log.error("no element set for <" + eName + ">");
			return;
		}

		boolean ok = true;
		ScriptType scriptType = ScriptType.getTypeWithXMLName(eName);
		if (scriptType != null) {
			ok = handleScript(attrs, scriptType);
		} else {
			switch (firstChar(eName)) {
			case 'a':
				if ("auxiliary".equals(eName)) {
					ok = handleAuxiliary(attrs);
					break;
				}
				if ("autocolor".equals(eName)) {
					ok = handleAutocolor(attrs);
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
					ok = handleAbsoluteScreenLocation(attrs, true);
					break;
				} else if ("angleStyle".equals(eName)) {
					ok = handleAngleStyle(attrs);
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
				} else if ("curveParam".equals(eName)) {
					ok = handleCurveParam(attrs);
					break;
				} else if ("casMap".equals(eName)) {
					casMap = new TreeMap<>();
					constMode = MODE_CAS_MAP;
					casMapParent = MODE_CONST_GEO_ELEMENT;
					ok = true;
					break;
				}

			case 'd':
				if ("decoration".equals(eName)) {
					ok = handleDecoration(attrs);
					break;
				} else if ("decimals".equals(eName)) {
					ok = handleTextDecimals(attrs);
					break;
				} else if ("dimensions".equals(eName)) {
					ok = handleDimensions(attrs);
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
				} else if ("forceReflexAngle".equals(eName)) {
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
				} else if ("isShape".equals(eName)) {
					ok = handleIsShape(attrs);
					break;
				} else if ("centered".equals(eName)) {
					ok = handleCentered(attrs);
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
				} else if ("listener".equals(eName)) {
					ok = handleListeners(attrs);
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

				else if ("pointStyle".equals(eName)) {
					ok = handlePointStyle(attrs);
					break;
				}
				/*
				 * should not be needed else if ("pathParameter".equals(eName))
				 * { ok = handlePathParameter(attrs); break; }
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
				} else if ("symbolic".equals(eName)) {
					ok = handleSymbolic(attrs);
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
				} else if ("tag".equals(eName)) {
					ok = handleExtraTag(attrs);
					break;
				} else if ("tags".equals(eName)) {
					ok = true;
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
				Log.error("unknown tag in <element>: " + eName);
			}
		}

		if (!ok) {
			Log.error("error in <element>: " + eName);
		}
	}

	private boolean handleDimensions(LinkedHashMap<String, String> attrs) {
		String width = attrs.get("width");
		String height = attrs.get("height");
		if (width != null && height != null) {
			if (width.matches("\\d{2,3}") && height.matches("\\d{2,3}")) {
				if (geo.isGeoButton()) {
					GeoButton button = (GeoButton) geo;
					button.setWidth(Integer.parseInt(width));
					button.setHeight(Integer.parseInt(height));
					button.setFixedSize(true);
					return true;
				}
				return false;
			}
			return true;
		}
		return false;
	}

	private static char firstChar(String eName) {
		if (eName == null || eName.length() == 0) {
			return '?';
		}
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
			if (str != null) {
				EVs = Integer.parseInt(str);
			}

			if ((EVs & 1) == 0) {
				geo.addView(App.VIEW_EUCLIDIAN);
			} else {
				geo.removeView(App.VIEW_EUCLIDIAN);
			}

			if ((EVs & 2) == 2) { // bit 1
				geo.addView(App.VIEW_EUCLIDIAN2);
			} else {
				geo.removeView(App.VIEW_EUCLIDIAN2);
			}

			if ((EVs & 4) == 4) { // bit 2
				geo.addViews3D();
			}

			if ((EVs & 8) == 8) { // bit 3
				geo.removeViews3D();
			}

			if ((EVs & 16) == 16) { // bit 4
				geo.setVisibleInViewForPlane(true);
				if (!(cons instanceof MacroConstruction)) {
					app.addToViewsForPlane(geo);
				}
			}

			if ((EVs & 32) == 32) { // bit 5
				geo.setVisibleInViewForPlane(false);
				app.removeFromViewsForPlane(geo);
			}

			return true;

		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleShowOnAxis(LinkedHashMap<String, String> attrs) {
		try {
			if (!(geo instanceof GeoFunction)) {
				return false;
			}
			((GeoFunction) geo).setShowOnAxis(parseBoolean(attrs.get("val")));
			return true;

		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleObjColor(LinkedHashMap<String, String> attrs) {
		GColor col = handleColorAttrs(attrs);
		if (col == null) {
			return false;
		}
		geo.setObjColor(col);

		// Dynamic colors
		// Michael Borcherds 2008-04-02
		String red = attrs.get("dynamicr");
		String green = attrs.get("dynamicg");
		String blue = attrs.get("dynamicb");
		String alpha = attrs.get("dynamica");
		String colorSpace = attrs.get("colorSpace");

		if (red != null && green != null && blue != null) {
			try {
				if (!"".equals(red) || !"".equals(green) || !"".equals(blue)) {
					if ("".equals(red)) {
						red = "0";
					}
					if ("".equals(green)) {
						green = "0";
					}
					if ("".equals(blue)) {
						blue = "0";
					}

					StringBuilder sb = new StringBuilder();
					sb.append('{');
					sb.append(red);
					sb.append(',');
					sb.append(green);
					sb.append(',');
					sb.append(blue);
					if (alpha != null && !"".equals(alpha)) {
						sb.append(',');
						sb.append(alpha);
					}
					sb.append('}');

					// need to to this at end of construction (dependencies!)
					dynamicColorList.add(new GeoExpPair(geo, sb.toString()));
					geo.setColorSpace(
							colorSpace == null ? GeoElement.COLORSPACE_RGB
									: Integer.parseInt(colorSpace));
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
				Log.error("Error loading Dynamic Colors");
			}
		}

		String angle = attrs.get("hatchAngle");
		if (angle != null) {
			geo.setHatchingAngle(Integer.parseInt(angle));
		}

		String inverse = attrs.get("inverseFill");
		if (inverse != null) {
			geo.setInverseFill(Boolean.parseBoolean(inverse));
		}

		String distance = attrs.get("hatchDistance");
		if (angle != null) {
			geo.setHatchingDistance(Integer.parseInt(distance));
			// Old files don't store fillType, just fillDistance. New files
			// override this below.
			geo.setFillType(FillType.HATCH);
		}

		String fillType = attrs.get("fillType");
		if (fillType != null) {
			geo.setFillType(
					GeoElement.FillType.values()[Integer.parseInt(fillType)]);
		}
		String fillSymbol = attrs.get("fillSymbol");
		if (fillSymbol != null) {
			geo.setFillSymbol(fillSymbol);
		}
		String filename = attrs.get("image");
		if (filename != null) {
			geo.setFillImage(filename);
			geo.setFillType(GeoElement.FillType.IMAGE);
		}

		alpha = attrs.get("alpha");
		// ignore alpha value for lists prior to GeoGebra 3.2
		if (alpha != null && (!geo.isGeoList() || ggbFileFormat > 3.19)) {
			geo.setAlphaValue(Float.parseFloat(alpha));
		}
		return true;
	}

	private boolean handleBgColor(LinkedHashMap<String, String> attrs) {
		GColor col = handleColorAlphaAttrs(attrs);
		if (col == null) {
			return false;
		}
		geo.setBackgroundColor(col);

		return true;
	}

	/*
	 * expects r, g, b attributes to build a color
	 */
	private static GColor handleColorAttrs(
			LinkedHashMap<String, String> attrs) {
		try {
			int red = Integer.parseInt(attrs.get("r"));
			int green = Integer.parseInt(attrs.get("g"));
			int blue = Integer.parseInt(attrs.get("b"));
			return GColor.newColor(red, green, blue);
		} catch (RuntimeException e) {
			return null;
		}
	}

	/*
	 * expects r, g, b, alpha attributes to build a color
	 */
	private static GColor handleColorAlphaAttrs(
			LinkedHashMap<String, String> attrs) {
		try {
			int red = Integer.parseInt(attrs.get("r"));
			int green = Integer.parseInt(attrs.get("g"));
			int blue = Integer.parseInt(attrs.get("b"));
			int alpha = Integer.parseInt(attrs.get("alpha"));
			return GColor.newColor(red, green, blue, alpha);
		} catch (RuntimeException e) {
			return null;
		}
	}

	private boolean handleLineStyle(LinkedHashMap<String, String> attrs) {
		try {
			lineStyleTagProcessed = true;
			geo.setLineType(Integer.parseInt(attrs.get("type")));
			geo.setLineThickness(Integer.parseInt(attrs.get("thickness")));

			// for 3D
			String typeHidden = attrs.get("typeHidden");
			if (typeHidden != null) {
				geo.setLineTypeHidden(Integer.parseInt(typeHidden));
			}
			String opacity = attrs.get("opacity");
			if (opacity != null) {
				geo.setLineOpacity(Integer.parseInt(opacity));
			}

			return true;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleDecoration(LinkedHashMap<String, String> attrs) {
		try {
			geo.setDecorationType(Integer.parseInt(attrs.get("type")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleEqnStyle(LinkedHashMap<String, String> attrs) {
		if (geo instanceof EquationValue) {
			String style = attrs.get("style");
			String parameter = attrs.get("parameter");
			if (!((EquationValue) geo).setTypeFromXML(style, parameter)) {
				Log.error("unknown style for conic in <eqnStyle>: " + style);
			}
		}
		else {
			Log.error("wrong element type for <eqnStyle>: " + geo.getClass());
			return false;
		}
		return true;
	}

	private boolean handleCurveParam(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof GeoVec3D)) {
			Log.debug("wrong element type for <curveParam>: " + geo.getClass());
			return false;
		}
		GeoVec3D v = (GeoVec3D) geo;

		try {
			String tAttr = attrs.get("t");

			if (tAttr != null) {
				// AlgoPointOnPath
				double t = StringUtil.parseDouble(tAttr);
				((GeoPoint) v).getPathParameter().setT(t);
			}

			return true;

		} catch (RuntimeException e) {
			Log.error("problem in <curveParam>: " + e.getMessage());
			return false;
		}
	}

	private boolean handleCoords(LinkedHashMap<String, String> attrs) {
		ExpressionNode def = geo.getDefinition();
		boolean success = kernel.handleCoords(geo, attrs);
		geo.setDefinition(def);
		return success;
	}

	// for point or vector
	private boolean handleCoordStyle(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof CoordStyle)) {
			Log.error("wrong element type for <coordStyle>: " + geo.getClass());
			return false;
		}
		CoordStyle v = (CoordStyle) geo;
		String style = attrs.get("style");
		if ("cartesian".equals(style)) {
			v.setCartesian();
		} else if ("polar".equals(style)) {
			v.setPolar();
		} else if ("complex".equals(style)) {
			v.setComplex();
		} else if ("cartesian3d".equals(style)) {
			v.setCartesian3D();
		} else if ("spherical".equals(style)) {
			v.setSpherical();
		} else {
			Log.error("unknown style in <coordStyle>: " + style);
			return false;
		}
		return true;
	}

	private boolean handleListeners(LinkedHashMap<String, String> attrs) {
		try {
			if ("objectUpdate".equals(attrs.get("type"))) {
				app.getScriptManager().getUpdateListenerMap().put(geo,
						JsScript.fromName(app, attrs.get("val")));
			}
			if ("objectClick".equals(attrs.get("type"))) {
				app.getScriptManager().getClickListenerMap().put(geo,
						JsScript.fromName(app, attrs.get("val")));
			}
			return true;
		} catch (RuntimeException e) {
			Log.error(e.getMessage());
			return false;
		}
	}

	private boolean handleCaption(LinkedHashMap<String, String> attrs) {
		try {
			geo.setCaption(attrs.get("val"));
			return true;
		} catch (RuntimeException e) {
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
		} catch (RuntimeException e) {
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
				showObjectConditionList
						.add(new GeoExpPair(geo, strShowObjectCond));
			}

			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleCheckbox(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoBoolean())) {
			Log.error("wrong element type for <checkbox>: " + geo.getClass());
			return false;
		}

		try {
			GeoBoolean bool = (GeoBoolean) geo;
			bool.setCheckboxFixed(parseBoolean(attrs.get("fixed")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleValue(LinkedHashMap<String, String> attrs) {
		boolean isBoolean = geo.isGeoBoolean();
		boolean isNumber = geo.isGeoNumeric();
		// GGB-244 something that was formerly just a number is now a segment:
		// hide it!
		if (geo.isNumberValue() && !isNumber && !isBoolean) {
			geo.setEuclidianVisible(false);
			return true;
		}
		// set value even when definition exists; might be needed if value
		// depends on Corner
		ExpressionNode oldDef = geo.getDefinition();
		if (!(isNumber || isBoolean || geo.isGeoButton())) {
			Log.debug("wrong element type for <value>: " + geo.getClass());
			return false;
		}

		try {
			String strVal = attrs.get("val");
			if (isNumber) {
				GeoNumeric n = (GeoNumeric) geo;
				n.setValue(StringUtil.parseDouble(strVal));

				// random
				n.setRandom("true".equals(attrs.get("random")));
				n.setDefinition(oldDef);

			} else if (isBoolean) {
				GeoBoolean bool = (GeoBoolean) geo;
				/*
				 * GGB-1372: use the recently computed value instead of the
				 * saved one for the Prove command
				 */
				if (!(geo.getParentAlgorithm() instanceof AlgoProve)) {
					bool.setValue(parseBoolean(strVal));
				}
				bool.setDefinition(oldDef);
			} else if (geo.isGeoButton()) {
				// XXX What's this javascript doing here? (Arnaud)
				GeoButton button = (GeoButton) geo;
				Script script = app.createScript(ScriptType.JAVASCRIPT, strVal,
						false);
				button.setClickScript(script);
			}
			return true;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handlePointSize(LinkedHashMap<String, String> attrs) {
		if (geo.isGeoNumeric()) {
			((GeoNumeric) geo).setSliderBlobSize(
					StringUtil.parseDouble(attrs.get("val")));
			return true;
		}
		if (!(geo instanceof PointProperties)) {
			Log.debug("wrong element type for <pointSize>: " + geo.getClass());
			return false;
		}

		try {
			PointProperties p = (PointProperties) geo;
			p.setPointSize(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handlePointStyle(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof PointProperties)) {
			Log.debug("wrong element type for <pointStyle>: " + geo.getClass());
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
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleLayer(LinkedHashMap<String, String> attrs) {

		try {
			geo.setLayer(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleCasCellInput(LinkedHashMap<String, String> attrs) {
		try {
			String input = attrs.get("value");
			geoCasCell.setInput(input, true);
			boolean pointList = parseBoolean(attrs.get("pointList"));
			geoCasCell.setPointList(pointList);

			String prefix = attrs.get("prefix");
			String eval = attrs.get("eval");
			String postfix = attrs.get("postfix");
			if (eval != null) {
				geoCasCell.setProcessingInformation(prefix, eval, postfix);
			}
			String evalCmd = attrs.get("evalCmd");
			geoCasCell.setEvalCommand(evalCmd);
			return true;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleCasCellOutput(LinkedHashMap<String, String> attrs) {
		if (geoCasCell.isUseAsText()) {
			return true;
		}
		try {
			String output = attrs.get("value");
			boolean error = parseBoolean(attrs.get("error"));
			boolean nativeOutput = parseBoolean(attrs.get("native"));
			geoCasCell.setNative(nativeOutput);
			if (error) {
				geoCasCell.setError(output);
			} else {
				if (!nativeOutput) {
					geoCasCell.computeOutput();
				} else {
					geoCasCell.setOutput(output, false);
				}
			}

			String evalCommandComment = attrs.get("evalCommand");
			if (evalCommandComment != null) {
				geoCasCell.setEvalCommand(evalCommandComment);
			} else {
				geoCasCell.setEvalCommand("");
			}

			String evalComment = attrs.get("evalComment");
			if (evalComment != null) {
				geoCasCell.setEvalComment(evalComment);
			}
			return true;
		} catch (RuntimeException e) {
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
	 * p.initPathParameter(param); return true; } catch(RuntimeException e) {
	 * return false; } }
	 */

	private boolean handleSlider(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoNumeric())) {
			Log.error("wrong element type for <slider>: " + geo.getClass());
			return false;
		}

		try {
			sliderTagProcessed = true;
			// don't create sliders in macro construction
			if (geo.getKernel().isMacroKernel()) {
				return true;
			}

			GeoNumeric num = (GeoNumeric) geo;

			// make sure
			String strMin = attrs.get("min");
			String strMax = attrs.get("max");
			if (strMin != null || strMax != null) {
				minMaxList.add(new GeoNumericMinMax(geo, strMin, strMax));
			}

			String str = attrs.get("absoluteScreenLocation");
			if (str != null) {
				num.setAbsoluteScreenLocActive(parseBoolean(str));
			} else {
				num.setAbsoluteScreenLocActive(false);
			}

			// null in preferences
			if (attrs.get("x") != null) {
				double x = StringUtil.parseDouble(attrs.get("x"));
				double y = StringUtil.parseDouble(attrs.get("y"));
				num.setSliderLocation(x, y, true);
			}

			num.setSliderWidth(StringUtil.parseDouble(attrs.get("width")),
					true);
			num.setSliderFixed(parseBoolean(attrs.get("fixed")));
			num.setShowExtendedAV(parseBoolean(attrs.get("showAlgebra")));

			num.setSliderHorizontal(parseBoolean(attrs.get("horizontal")));

			return true;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleTrace(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof Traceable)) {
			Log.error("wrong element type for <trace>: " + geo.getClass());
			return false;
		}

		try {
			Traceable t = (Traceable) geo;
			t.setTrace(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleSpreadsheetTrace(
			LinkedHashMap<String, String> attrs) {

		// G.Sturr 2010-5-30
		// XML handling for new tracing code
		if (!geo.isSpreadsheetTraceable()) {
			Log.error("wrong element type for <trace>: " + geo.getClass());
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

			String stringPause = attrs.get("pause");
			if (stringPause == null) {
				t.pause = false;
			} else {
				t.pause = parseBoolean(stringPause);
			}

			app.setNeedsSpreadsheetTableModel();

			// app.getTraceManager().loadTraceGeoCollection(); is called when
			// construction loaded to add geo to trace list

			return true;

		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}

		/*
		 * OLD CODE
		 * 
		 * if (!(geo instanceof GeoPoint)) { Log.error(
		 * "wrong element type for <trace>: " + geo.getClass()); return false; }
		 * 
		 * try { GeoPoint p = (GeoPoint) geo;
		 * p.setSpreadsheetTrace(parseBoolean((String) attrs.get("val")));
		 * return true; } catch(RuntimeException e) { return false; }
		 */
		// END G.Sturr

	}

	private boolean handleShowTrimmed(LinkedHashMap<String, String> attrs) {
		try {
			geo.setShowTrimmedIntersectionLines(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleSelectionAllowed(
			LinkedHashMap<String, String> attrs) {
		try {
			geo.setSelectionAllowed(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleSelectedIndex(LinkedHashMap<String, String> attrs) {
		try {
			if (geo.isGeoList()) {
				((GeoList) geo).setSelectedIndex(
						Integer.parseInt(attrs.get("val")), false);
			}
			return true;
		} catch (RuntimeException e) {
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
			if (type != null) {
				geo.setAnimationType(Integer.parseInt(type));
			}

			// doesn't work for hidden sliders now that intervalMin/Max are set
			// at end of XML (dynamic slider range(
			// geo.setAnimating(parseBoolean((String) attrs.get("playing")));

			// replacement
			if (parseBoolean(attrs.get("playing"))) {
				animatingList.add(geo);
			}

			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleFixed(LinkedHashMap<String, String> attrs) {
		try {
			geo.setFixed(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleIsShape(LinkedHashMap<String, String> attrs) {
		try {
			geo.setIsShape(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleBreakpoint(LinkedHashMap<String, String> attrs) {
		try {
			geo.setConsProtocolBreakpoint(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleFile(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoImage() || geo.isGeoButton() || geo.isGeoTurtle())) {
			Log.error("wrong element type for <file>: " + geo.getClass());
			return false;
		}

		try {
			geo.setImageFileName(attrs.get("name"));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	// <font serif="false" size="12" style="0">
	private boolean handleTextFont(LinkedHashMap<String, String> attrs) {
		this.fontTagProcessed = true;
		if (!(geo instanceof TextProperties)) {
			Log.error("wrong element type for <font>: " + geo.getClass());
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
				double appSize = app.getFontSize();
				double oldSizeInt = Integer.parseInt(oldSize);
				text.setFontSizeMultiplier(
						Math.max(appSize + oldSizeInt, MIN_TEXT_SIZE)
								/ appSize);
			} else {
				text.setFontSizeMultiplier(StringUtil.parseDouble(size));
			}
			if (serif != null) {
				text.setSerifFont(parseBoolean((String) serif));
			}
			if (style != null) {
				text.setFontStyle(Integer.parseInt((String) style));
			}
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleTextDecimals(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof TextProperties)) {
			Log.error("wrong element type for <decimals>: " + geo.getClass());
			return false;
		}

		try {
			TextProperties text = (TextProperties) geo;
			text.setPrintDecimals(Integer.parseInt(attrs.get("val")), true);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleTextFigures(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof TextProperties)) {
			Log.error("wrong element type for <decimals>: " + geo.getClass());
			return false;
		}

		try {
			TextProperties text = (TextProperties) geo;
			text.setPrintFigures(Integer.parseInt(attrs.get("val")), true);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleInBackground(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoImage())) {
			Log.error(
					"wrong element type for <inBackground>: " + geo.getClass());
			return false;
		}

		try {
			((GeoImage) geo).setInBackground(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleCentered(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoImage())) {
			Log.error("wrong element type for <centered>: " + geo.getClass());
			return false;
		}

		try {
			((GeoImage) geo).setCentered(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}
	private boolean handleInterpolate(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoImage())) {
			Log.error(
					"wrong element type for <interpolate>: " + geo.getClass());
			return false;
		}

		try {
			((GeoImage) geo).setInterpolate(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleAuxiliary(LinkedHashMap<String, String> attrs) {
		try {
			geo.setAuxiliaryObject(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleAutocolor(LinkedHashMap<String, String> attrs) {
		try {
			geo.setAutoColor(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleIsLaTeX(LinkedHashMap<String, String> attrs) {
		try {

			((GeoText) geo).setLaTeX(parseBoolean(attrs.get("val")), false);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleArcSize(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof AngleProperties)) {
			Log.error("wrong element type for <arcSize>: " + geo.getClass());
			return false;
		}

		try {
			AngleProperties angle = (AngleProperties) geo;
			angle.setArcSize(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleAbsoluteScreenLocation(
			LinkedHashMap<String, String> attrs, boolean absolute) {
		if (!(geo instanceof AbsoluteScreenLocateable)) {
			Log.error("wrong element type for <absoluteScreenLocation>: "
					+ geo.getClass());
			return false;
		}

		try {
			AbsoluteScreenLocateable absLoc = (AbsoluteScreenLocateable) geo;
			double x = Double.parseDouble(attrs.get("x"));
			double y = Double.parseDouble(attrs.get("y"));
			if (absolute) {
				absLoc.setAbsoluteScreenLoc((int) x, (int) y);
				absLoc.setAbsoluteScreenLocActive(true);
			} else {
				absLoc.setRealWorldLoc(x, y);
			}

			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleAllowReflexAngle(
			LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof AngleProperties)) {
			Log.error("wrong element type for <allowReflexAngle>: "
					+ geo.getClass());
			return false;
		}

		try {
			AngleProperties angle = (AngleProperties) geo;
			angle.setAllowReflexAngle(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {

			return false;
		}
	}

	private boolean handleEmphasizeRightAngle(
			LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof AngleProperties)) {
			Log.error("wrong element type for <emphasizeRightAngle>: "
					+ geo.getClass());
			return false;
		}

		try {
			AngleProperties angle = (AngleProperties) geo;
			angle.setEmphasizeRightAngle(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {

			return false;
		}
	}

	private boolean handleComboBox(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoList())) {
			Log.error("wrong element type for <comboBox>: " + geo.getClass());
			return false;
		}

		try {
			GeoList list = (GeoList) geo;
			list.setDrawAsComboBox(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {

			return false;
		}
	}

	private boolean handleAngleStyle(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof AngleProperties)) {
			Log.error("wrong element type for <angleStyle>: " + geo.getClass());
			return false;
		}

		try {
			AngleProperties angle = (AngleProperties) geo;
			angle.setAngleStyle(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {

			return false;
		}
	}

	/*
	 * needed for old files (4.2 and earlier)
	 */
	private boolean handleForceReflexAngle(
			LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof AngleProperties)) {
			Log.error("wrong element type for <forceReflexAngle>: "
					+ geo.getClass());
			return false;
		}

		try {
			AngleProperties angle = (AngleProperties) geo;
			angle.setForceReflexAngle(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {

			return false;
		}
	}

	private boolean handleOutlyingIntersections(
			LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof LimitedPath)) {
			Log.debug("wrong element type for <outlyingIntersections>: "
					+ geo.getClass());
			return false;
		}

		try {
			LimitedPath lpath = (LimitedPath) geo;
			lpath.setAllowOutlyingIntersections(parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleKeepTypeOnTransform(
			LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof LimitedPath)) {
			Log.debug("wrong element type for <outlyingIntersections>: "
					+ geo.getGeoClassType());
			return false;
		}

		try {
			LimitedPath lpath = (LimitedPath) geo;
			lpath.setKeepTypeOnGeometricTransform(
					parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleSymbolic(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof HasSymbolicMode)) {
			Log.error("wrong element type for <symbolic>: " + geo.getClass());
			return false;
		}
		symbolicTagProcessed = true;
		try {
			HasSymbolicMode num = (HasSymbolicMode) geo;
			num.setSymbolicMode(parseBoolean(attrs.get("val")), false);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleSlopeTriangleSize(
			LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoNumeric())) {
			Log.error("wrong element type for <slopeTriangleSize>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoNumeric num = (GeoNumeric) geo;
			num.setSlopeTriangleSize(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
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
			if (geo instanceof GeoButton) {
				return handleAbsoluteScreenLocation(attrs, false);
			}
			Log.error("wrong element type for <startPoint>: " + geo.getClass());
			return false;
		}
		Locateable locGeo = (Locateable) geo;

		// relative start point (expression or label expected)
		String exp = attrs.get("exp");
		if (exp == null) {
			exp = attrs.get("label");
		}

		// for corners a number of the startPoint is given
		int number = 0;
		try {
			number = Integer.parseInt(attrs.get("number"));
		} catch (RuntimeException e) {
			// do nothing
		}

		if (exp != null) {
			// store (geo, epxression, number) values
			// they will be processed in processStartPoints() later
			startPointList.add(new LocateableExpPair(locGeo, exp, number));
			locGeo.setWaitForStartPoint();
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
					locGeo.setStartPoint(p);
				} else {
					// set other start points later
					// store (geo, point, number) values
					// they will be processed in processStartPoints() later
					startPointList
							.add(new LocateableExpPair(locGeo, p, number));
					locGeo.setWaitForStartPoint();
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
	 * @param attrs
	 *            tag atributes
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
			AlgebraProcessor algProc = getAlgProcessor();

			while (it.hasNext()) {
				LocateableExpPair pair = it.next();
				GeoPointND P = pair.point != null ? pair.point
						: algProc.evaluateToPoint(pair.exp,
								ErrorHelper.silent(), true);
				pair.locateable.setStartPoint(P, pair.number);

			}
		} catch (Exception e) {
			startPointList.clear();
			e.printStackTrace();
			errors.add("Invalid start point: " + e.toString());
		}
		startPointList.clear();
	}

	private boolean handleLength(LinkedHashMap<String, String> attrs) {

		// name of linked geo
		String val = attrs.get("val");

		if (geo instanceof GeoInputBox) {
			((GeoInputBox) geo).setLength(Integer.parseInt(val));
		} else {
			Log.error("Length not supported for " + geo.getGeoClassType());
		}

		return true;
	}

	private boolean handleListType(LinkedHashMap<String, String> attrs) {

		// name of geo type, eg "point"
		String val = attrs.get("val");

		if (geo instanceof GeoList) {
			((GeoList) geo).setTypeStringForXML(val);
		} else {
			Log.error("handleListType: expected LIST, got "
					+ geo.getGeoClassType());
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
		} else {
			return false;
		}

		return true;
	}

	private void processLinkedGeoList() {
		try {
			Iterator<GeoExpPair> it = linkedGeoList.iterator();

			while (it.hasNext()) {
				GeoExpPair pair = it.next();

				((GeoInputBox) pair.getGeo())
						.setLinkedGeo(kernel.lookupLabel(pair.exp));
			}
		} catch (RuntimeException e) {
			linkedGeoList.clear();
			e.printStackTrace();
			errors.add("Invalid linked geo " + e.toString());
		}
		linkedGeoList.clear();
	}

	private void processShowObjectConditionList() {
		Iterator<GeoExpPair> it = showObjectConditionList.iterator();
		AlgebraProcessor algProc = getAlgProcessor();

		while (it.hasNext()) {
			try {
				GeoExpPair pair = it.next();
				GeoBoolean condition = algProc.evaluateToBoolean(pair.exp,
						ErrorHelper.silent());
				if (condition != null) {
					pair.getGeo().setShowObjectCondition(condition);
				} else {
					errors.add("Invalid condition to show object: " + pair.exp);
				}

			} catch (Exception e) {
				showObjectConditionList.clear();
				e.printStackTrace();
				errors.add("Invalid condition to show object: " + e.toString());
			}
		}
		showObjectConditionList.clear();
	}

	private void processAnimationSpeedList() {
		try {
			Iterator<GeoExpPair> it = animationSpeedList.iterator();
			AlgebraProcessor algProc = getAlgProcessor();

			while (it.hasNext()) {
				GeoExpPair pair = it.next();
				GeoNumberValue num = algProc.evaluateToNumeric(pair.exp,
						handler);
				pair.getGeo().setAnimationSpeedObject(num);
			}
		} catch (RuntimeException e) {
			animationSpeedList.clear();
			e.printStackTrace();
			errors.add("Invalid animation speed: " + e.toString());
		}
		animationSpeedList.clear();
	}

	private void processAnimationStepList() {
		try {
			Iterator<GeoExpPair> it = animationStepList.iterator();
			AlgebraProcessor algProc = getAlgProcessor();

			while (it.hasNext()) {
				GeoExpPair pair = it.next();
				NumberValue num = algProc.evaluateToNumeric(pair.exp, handler);
				if (pair.getGeo().isGeoNumeric()) {
					((GeoNumeric) pair.getGeo())
							.setAutoStep(Double.isNaN(num.getDouble()));
				}
				pair.getGeo().setAnimationStep(num);

			}
		} catch (RuntimeException e) {
			animationStepList.clear();
			e.printStackTrace();
			errors.add("Invalid animation step: " + e.toString());
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
		} catch (RuntimeException e) {
			errors.add("Invalid animating: " + e.toString());
		}
		animatingList.clear();
	}

	private ErrorHandler handler = new ErrorHandler() {

		@Override
		public void showError(String msg) {
			errors.add(msg);

		}

		@Override
		public void resetError() {
			showError(null);
		}

		@Override
		public void showCommandError(String command, String message) {
			errors.add(message);

		}

		@Override
		public String getCurrentCommand() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean onUndefinedVariables(String string,
				AsyncOperation<String[]> callback) {
			// TODO Auto-generated method stub
			return false;
		}
	};

	private void processMinMaxList() {
		try {
			Iterator<GeoNumericMinMax> it = minMaxList.iterator();
			AlgebraProcessor algProc = getAlgProcessor();

			while (it.hasNext()) {
				GeoNumericMinMax pair = it.next();
				// the setIntervalMin and setIntervalMax methods might turn ?
				// into defined
				// this is intentional, but when loading a file we must override
				// it for 3.2 compatibility
				boolean wasDefined = pair.getGeo().isDefined();
				if (pair.min != null) {
					NumberValue num = algProc.evaluateToNumeric(pair.min,
							handler);
					((GeoNumeric) pair.getGeo()).setIntervalMin(num);
				}

				if (pair.max != null) {
					NumberValue num2 = algProc.evaluateToNumeric(pair.max,
							handler);
					((GeoNumeric) pair.getGeo()).setIntervalMax(num2);
				}

				if (!wasDefined) {
					pair.getGeo().setUndefined();
				}
			}
		} catch (RuntimeException e) {
			minMaxList.clear();
			e.printStackTrace();
			errors.add("Invalid min/max: " + e.toString());
		}
		minMaxList.clear();
	}

	// Michael Borcherds 2008-05-18
	private void processDynamicColorList() {
		try {
			Iterator<GeoExpPair> it = dynamicColorList.iterator();
			AlgebraProcessor algProc = getAlgProcessor();

			while (it.hasNext()) {
				GeoExpPair pair = it.next();
				pair.getGeo()
						.setColorFunction(algProc.evaluateToList(pair.exp));
			}
		} catch (RuntimeException e) {
			dynamicColorList.clear();
			e.printStackTrace();
			errors.add("Invalid dynamic color: " + e.toString());
		}
		dynamicColorList.clear();
	}

	/**
	 * @param attrs
	 *            attributes
	 * @return success
	 */
	protected boolean handleEigenvectors(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoConic())) {
			Log.error(
					"wrong element type for <eigenvectors>: " + geo.getClass());
			return false;
		}
		try {
			GeoConicND conic = (GeoConicND) geo;
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
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleMatrix(LinkedHashMap<String, String> attrs) {
		if (!geo.isGeoConic() && !geo.isGeoQuadric()) {
			Log.error("wrong element type for <matrix>: " + geo.getClass());
			return false;
		}
		try {
			handleMatrixConicOrQuadric(attrs);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * handler matrix for a conic or a quadric
	 * 
	 * @param attrs
	 *            attributes
	 * @throws Exception
	 *             exception
	 */
	protected void handleMatrixConicOrQuadric(
			LinkedHashMap<String, String> attrs) throws Exception {
		if (geo.isGeoConic() && geo.getDefinition() == null) {
			GeoConicND conic = (GeoConicND) geo;
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
	}

	private boolean handleLabelOffset(LinkedHashMap<String, String> attrs) {
		try {
			geo.labelOffsetX = Integer.parseInt(attrs.get("x"));
			geo.labelOffsetY = Integer.parseInt(attrs.get("y"));

			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleLabelMode(LinkedHashMap<String, String> attrs) {
		try {
			geo.setLabelMode(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleTooltipMode(LinkedHashMap<String, String> attrs) {
		try {
			geo.setTooltipMode(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleCoefficients(LinkedHashMap<String, String> attrs) {
		// Application.debug(attrs.toString());
		if (!(geo.isGeoImplicitCurve())) {
			Log.warn(
					"wrong element type for <coefficients>: " + geo.getClass());
			return false;
		}
		try {
			String rep = attrs.get("rep");
			if (rep == null) {
				return false;
			}
			if (attrs.get("rep").equals("array")) {
				String data = attrs.get("data");
				if (data == null) {
					return false;
				}
				ArrayList<ArrayList<Double>> collect = new ArrayList<>();
				ArrayList<Double> newRow = new ArrayList<>();
				int start = 0;
				for (int c = 1; c < data.length(); c++) {
					switch (data.charAt(c)) {
					default:
						// do nothing
						break;
					case '[':
						if (newRow.size() > 0) {
							return false;
						}
						start = c + 1;
						break;
					case ']':
						newRow.add(StringUtil
								.parseDouble(data.substring(start, c)));
						start = c + 1;
						collect.add(newRow);
						newRow = new ArrayList<>();
						c++; // jump over ','
						break;
					case ',':
						newRow.add(StringUtil
								.parseDouble(data.substring(start, c)));
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
				ExpressionNode def = geo.getDefinition();
				/*
				 * Only overwrite coeff from XML when we don't have definition
				 * (setting coeffs explicitly kills factorization)
				 */
				if (def == null) {
					((GeoImplicit) geo).setCoeff(coeff);
				}
				// geo.setDefinition(def);
				return true;
			}
		} catch (RuntimeException e) {
			return false;
		}
		return false;
	}

	private boolean handleUserInput(LinkedHashMap<String, String> attrs) {
		// Application.debug(attrs.toString());
		if (!(geo instanceof GeoImplicit)) {
			Log.warn("wrong element type for <userinput>: " + geo.getClass());
			return false;
		}
		try {
			boolean valid = !"false".equals(attrs.get("valid"));
			if (geo.isIndependent() && valid) {
				String value = attrs.get("value");
				if (value != null) {
					ValidExpression ve = parser.parseGeoGebraExpression(value);
					geo.setDefinition(ve.wrap());
					if (ve.unwrap() instanceof Equation) {
						((GeoImplicit) geo).fromEquation((Equation) ve.unwrap(),
								null);
					}

				}
			}
			if (attrs.get("show") != null && attrs.get("show").equals("true")
					&& valid) {
				((GeoImplicit) geo).setToUser();
			} else {
				((GeoImplicit) geo).setToImplicit();
			}

			return true;
		} catch (Exception e) {
			Log.debug(e.getMessage());
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

		String varStr = attrs.get("var");
		if (varStr != null) {
			String[] vars = varStr.split(",");
			for (String var : vars) {
				cons.registerFunctionVariable(var);
			}
		}

		// Application.debug(name);
		if (name != null) {
			command = new Command(kernel, name, false); // do not translate name
		} else {
			errors.add("name missing in <command>");
		}
		return command;
	}

	private void startCommandElement(String eName,
			LinkedHashMap<String, String> attrs) {
		boolean ok = true;

		if ("input".equals(eName)) {
			ok = handleCmdInput(attrs);
		} else if ("output".equals(eName)) {
			ok = handleCmdOutput(attrs);
		} else if ("outputSizes".equals(eName)) {
			ok = handleCmdOutputSizes(attrs);
		} else if ("casMap".equals(eName)) {
			casMap = new TreeMap<>();
			constMode = MODE_CAS_MAP;
			casMapParent = MODE_CONST_COMMAND;
			ok = true;
		} else {
			Log.error("unknown tag in <command>: " + eName);
		}

		if (!ok) {
			Log.error("error in <command>: " + eName);
		}
	}

	private boolean handleCmdInput(LinkedHashMap<String, String> attrs) {
		GeoElement geo1;
		ExpressionNode en;
		String arg = null;
		if (cmd == null) {
			errors.add("No command set for input");
			return false;
		}
		// Collection<String> values = attrs.values();

		// TODO: it doesn't work with GWT. why?
		// Iterator<String> it = values.iterator();
		// while (it.hasNext()) {

		ArrayList<String> keys = new ArrayList<>(attrs.keySet());
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
						|| cmd.getName().equals("CurveCartesian")
						|| cmd.getName().equals("Surface")) {
					geo1 = null;
				} else {
					geo1 = kernel.lookupLabel(arg);
				}

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
				errors.add("unknown command input: " + arg);
			} catch (Error e) {
				e.printStackTrace();
				errors.add("unknown command input: " + arg);
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

			ArrayList<String> attrKeys = new ArrayList<>(attrs.keySet());
			for (String key : attrKeys) {
				label = attrs.get(key);
				if ("".equals(label)) {
					label = null;
				} else {
					countLabels++;
				}
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
			if (countLabels == 0) {
				return true;
			}
			// process the command
			cmdOutput = getAlgProcessor().processCommand(cmd,
					new EvalInfo(true, casMap));
			cons.registerFunctionVariable(null);
			String cmdName = cmd.getName();
			if (cmdOutput == null) {
				errors.add("processing of command: " + cmd);
				return false;
			}
			cmd = null;

			// ensure that labels are set for invisible objects too
			if (attrs.size() != cmdOutput.length) {
				Log.debug(
						"error in <output>: wrong number of labels for command "
								+ cmdName);
				Log.error("   cmdOutput.length = " + cmdOutput.length
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
				if ("".equals(label)) {
					label = null;
				}

				if (label != null && cmdOutput[i] != null) {
					cmdOutput[i].setLoadedLabel(label);
				}
				i++;
			}
			return true;
		} catch (MyError e) {
			errors.add("processing of command: " + cmd);
			e.printStackTrace();
			return false;
		} catch (RuntimeException e) {
			e.printStackTrace();
			errors.add("processing of command: " + cmd);
			return false;
		}
	}

	/**
	 * handle command output sizes (used only for some algos that have multiple
	 * types for output
	 * 
	 * @param attrs
	 * @return true if proceeded
	 */
	private boolean handleCmdOutputSizes(LinkedHashMap<String, String> attrs) {
		try {
			String[] vals = attrs.get("val").split(",");
			int[] sizes = new int[vals.length];
			for (int i = 0; i < vals.length; i++) {
				sizes[i] = Integer.parseInt(vals[i]);
			}

			cmd.setOutputSizes(sizes);

			return true;
		} catch (MyError e) {
			errors.add("wrong command size for " + cmd);
		} catch (RuntimeException e) {
			errors.add("wrong command size for " + cmd);
		}
		return false;
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
		if (geo1 != null && geo1.getCorrespondingCasCell() != null) {
			return;
		}

		String exp = attrs.get("exp");
		if (exp == null) {
			errors.add("exp missing in <expression>");
			return;
		}

		// type may be vector or point, this is important to distinguish between
		// them
		String type = attrs.get("type");
		// parse expression and process it
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(exp);
			if (label != null) {
				if ("X".equals(ve.getLabel())
						&& cons.getRegisteredFunctionVariable() == null) {
					ve = new Equation(kernel, new Variable(kernel, "X"), ve);
				}
				ve.setLabel(label);
			}

			// enforce point or vector or line or plane type if it was given in
			// attribute type
			if (type != null) {
				if ("point".equals(type) && ve instanceof ExpressionNode) {
					((ExpressionNode) ve).setForcePoint();
				} else if ("vector".equals(type)
						&& ve instanceof ExpressionNode) {
					((ExpressionNode) ve).setForceVector();
					// we must check that we have Equation here as xAxis
					// has also type "line" but is parsed as ExpressionNode
				} else if (ve instanceof Equation) {
					if ("line".equals(type)) {
						((Equation) ve).setForceLine();
					} else if ("plane".equals(type)) {
						((Equation) ve).setForcePlane();
					} else if ("conic".equals(type)) {
						((Equation) ve).setForceConic();
					} else if ("quadric".equals(type)) {
						((Equation) ve).setForceQuadric();
					} else if ("implicitpoly".equals(type)
							|| "function".equals(type)
							|| "implicitPoly".equals(type)) {
						((Equation) ve).setForceImplicitPoly();
					} else if ("implicitsurface".equals(type)) {
						((Equation) ve).setForceSurface();
					}

				}
			}

			// Application.debug(""+getAlgProcessor());

			GeoElementND[] result = getAlgProcessor()
					.processValidExpression(ve);
			cons.registerFunctionVariable(null);
			// ensure that labels are set for invisible objects too
			if (result != null && label != null && result.length == 1) {
				result[0].setLoadedLabel(label);
			} else {
				Log.error(
						"error in <expression>: " + exp + ", label: " + label);
			}

		} catch (Exception e) {
			String msg = "error in <expression>: label=" + label + ", exp= "
					+ exp;
			Log.error(msg);
			e.printStackTrace();
			errors.add(msg);
		} catch (Error e) {
			String msg = "error in <expression>: label = " + label + ", exp = "
					+ exp;
			Log.error(msg);
			e.printStackTrace();
			errors.add(msg);
		}
	}

	private AlgebraProcessor getAlgProcessor() {
		return kernel.getAlgebraProcessor();
	}

	private boolean handleAlgebraViewMode(LinkedHashMap<String, String> attrs) {
		try {
			int val = Integer.parseInt(attrs.get("val"));
			app.getSettings().getAlgebra().setTreeMode(val);
			app.getSettings().getAlgebra().setModeChanged(true);

			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleAlgebraViewShowAuxiliaryObjects(
			LinkedHashMap<String, String> attrs) {
		try {
			boolean b = parseBoolean(attrs.get("show"));
			app.getSettings().getAlgebra().setShowAuxiliaryObjects(b);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleAlgebraViewCollapsedNodes(
			LinkedHashMap<String, String> attrs) {

		try {
			String[] strings = attrs.get("val").split(",");
			int[] vals = new int[strings.length];
			for (int i = 0; i < strings.length; i++) {
				vals[i] = Integer.parseInt(strings[i]);
			}
			app.getSettings().getAlgebra().setCollapsedNodes(vals);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	// ====================================
	// UTILS
	// ====================================

	/**
	 * Parse string to boolean
	 * 
	 * @param str
	 *            input string
	 * @return true for "true", false otherwise
	 */
	protected static boolean parseBoolean(String str) {
		return "true".equals(str);
	}

	/**
	 * Parse string to boolean
	 * 
	 * @param str
	 *            input string
	 * @return false for "fale", true otherwise
	 */
	protected static boolean parseBooleanRev(String str) {
		return !"false".equals(str);
	}
}
