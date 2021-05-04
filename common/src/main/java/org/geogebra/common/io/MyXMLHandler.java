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
import java.util.TreeMap;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.dialog.options.OptionsCAS;
import org.geogebra.common.gui.view.data.DataAnalysisModel.Regression;
import org.geogebra.common.gui.view.data.DataDisplayModel.PlotType;
import org.geogebra.common.gui.view.probcalculator.Procedure;
import org.geogebra.common.gui.view.probcalculator.StatisticsCollection;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.DockSplitPaneData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.KernelCAS;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.MacroKernel;
import org.geogebra.common.kernel.PathRegionHandling;
import org.geogebra.common.kernel.SetRandomValue;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.parser.GParser;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.GeoGebraPreferencesXML;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.settings.ConstructionProtocolSettings;
import org.geogebra.common.main.settings.DataAnalysisSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.common.main.settings.TableSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Util;
import org.geogebra.common.util.debug.Log;
import org.xml.sax.SAXException;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.editor.share.util.Unicode;

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

	private int mode;
	private int constMode; // submode for <construction>
	private int casMode; // submode for <cascell>

	/** currently parsed element */

	private GeoCasCell geoCasCell;
	private Command cmd;
	private Macro macro;
	/** application */
	@Weak
	protected final App app;
	/** lacalization */
	protected final Localization loc;

	private String[] macroInputLabels;
	private String[] macroOutputLabels;
	private GeoElementND[] cmdOutput;
	private boolean startAnimation;

	@Weak
	Kernel kernel;
	// for macros we need to change the kernel, so remember the original kernel
	// too
	private Kernel origKernel;
	/** construction */
	@Weak
	protected Construction cons;

	Parser parser;
	private Parser origParser;

	/** errors encountered during load */
	ArrayList<String> errors = new ArrayList<>();
	// construction step stored in <consProtNavigation> : handled after parsing
	private int consStep;
	private final ConsElementXMLHandler geoHandler;
	double ggbFileFormat;

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

	TreeMap<String, String> casMap;

	private int casMapParent;

	private HashMap<EuclidianSettings, String> xmin = new HashMap<>();
	private HashMap<EuclidianSettings, String> xmax = new HashMap<>();
	private HashMap<EuclidianSettings, String> ymin = new HashMap<>();
	private HashMap<EuclidianSettings, String> xtick = new HashMap<>();
	private HashMap<EuclidianSettings, String> ytick = new HashMap<>();
	private HashMap<EuclidianSettings, String> ztick = new HashMap<>();
	private HashMap<EuclidianSettings, String> ymax = new HashMap<>();
	private ArrayList<String> entries;
	private String subAppCode;

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
		geoHandler = new ConsElementXMLHandler(this, app);
	}

	private void reset(boolean start) {
		geoHandler.reset();
		errors.clear();

		if (start) {
			consStep = -2;
		}

		mode = MODE_INVALID;
		constMode = MODE_CONSTRUCTION;
		hasGuiElement = false;
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

	/**
	 * For navigation bar
	 * 
	 * @return current construction step
	 */
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
			StringBuilder sb = new StringBuilder();
			for (String error : errors) {
				sb.append(Unicode.CENTER_DOT).append(' ').append(error)
						.append('\n');
			}
			app.showError(
					new MyError(loc, Errors.LoadFileFailed, sb.toString()));
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
			String appCode = nomalizeApp(attrs.get("app"));
			this.app.setFileVersion(ggbVersion, appCode);
			this.subAppCode = nomalizeApp(attrs.get("subApp"));
			if (subAppCode == null) {
				subAppCode = appCode;
			}
			String uniqueId = attrs.get("id");
			if (uniqueId != null) {
				this.app.setUniqueId(uniqueId);
			}
		}
	}

	private static String nomalizeApp(String string) {
		if (string != null && string
				.matches(
						"graphing|geometry|classic|3d|3D|scientific|suite|cas|notes")) {
			return string;
		}
		return null;
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
		switch (eName) {
		case "euclidianView":
			mode = MODE_EUCLIDIAN_VIEW;
			resetEVsettingsNeeded = true;
			break;
		case "euclidianView3D":
			mode = MODE_EUCLIDIAN_VIEW3D;
			resetEVsettingsNeeded = true;
			break;
		case "algebraView":
			mode = MODE_ALGEBRA_VIEW;
			break;
		case "kernel":
			// default value
			// (make sure old files work)
			kernel.setUsePathAndRegionParameters(PathRegionHandling.ON);
			mode = MODE_KERNEL;
			break;
		case "tableview":
			setTableParameters(attrs);
			break;
		case "spreadsheetView":
			mode = MODE_SPREADSHEET_VIEW;
			break;
		case "scripting":
			startScriptingElement(attrs);
			break;
		case "probabilityCalculator":
			mode = MODE_PROBABILITY_CALCULATOR;
			break;
		case "gui":
			mode = MODE_GUI;
			hasGuiElement = true;
			isPreferencesXML = false;

			// if (ggbFileFormat < 3.3) // safe to reset every time
			tmp_perspective = new Perspective("tmp");
			tmp_perspectives.clear();

			break;
		case "macro":
			mode = MODE_MACRO;
			initMacro(attrs);
			break;
		case "construction":
			mode = MODE_CONSTRUCTION;
			handleConstruction(attrs);
			break;
		case "casSession":
			// old <casSession> is now <cascell> in <construction>
			// not used anymore after 2011-08-16
			mode = MODE_CONSTRUCTION;
			constMode = MODE_CONST_CAS_CELL;
			break;
		case "keyboard":
			handleKeyboard(attrs);
			break;
		case "defaults":
			mode = MODE_DEFAULTS;
			constMode = MODE_DEFAULTS;
			break;
		default:
			Log.error("unknown tag in <geogebra>: " + eName);
		}
	}

	private void setTableParameters(LinkedHashMap<String, String> attrs) {
		TableSettings ts = app.getSettings().getTable();
		ts.setValuesMin(getNumber(attrs.get("min")).getDouble());
		ts.setValuesMax(getNumber(attrs.get("max")).getDouble());
		ts.setValuesStep(getNumber(attrs.get("step")).getDouble());
	}

	protected GeoNumberValue getNumber(String string) {
		return getAlgProcessor().evaluateToNumeric(string, handler);
	}

	private void handleKeyboard(LinkedHashMap<String, String> attrs) {
		app.updateKeyboardSettings(attrs);
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
	 * @return true if ok
	 */
	protected boolean startEuclidianViewElementSwitch(String eName,
			LinkedHashMap<String, String> attrs) {

		boolean ok = true;

		switch (eName) {
		case "axesColor":
			ok = handleAxesColor(evSet, attrs);
			break;
		case "axis":
			ok = handleAxis(evSet, attrs);
			break;
		case "bgColor":
			ok = handleBgColor(evSet, attrs);
			break;
		case "coordSystem":
			ok = handleCoordSystem(evSet, attrs);
			break;
		case "evSettings":
			ok = handleEvSettings(evSet, attrs);
			break;
		case "eraserSize":
			ok = handleEraserSize(evSet, attrs);
			break;
		case "grid":
			ok = handleGrid(evSet, attrs);
			break;
		case "gridColor":
			ok = handleGridColor(evSet, attrs);
			break;
		case "highlighterSize":
			ok = handleHighlighterSize(evSet, attrs);
			break;
		case "highlighterColor":
			ok = handleHighlighterColor(evSet, attrs);
			break;
		case "lineStyle":
			ok = handleLineStyle(evSet, attrs);
			break;
		case "labelStyle":
			ok = handleLabelStyle(evSet, attrs);
			break;
		case "language":
			ok = handleLanguage(app, attrs);
			break;
		case "penSize":
			ok = handlePenSize(evSet, attrs);
			break;
		case "penColor":
			ok = handlePenColor(evSet, attrs);
			break;
		case "rulerColor":
			ok = handleRulerColor(evSet, attrs);
			break;
		case "rulerType":
			ok = handleRulerType(evSet, attrs);
			break;
		case "size":
			ok = handleEvSize(evSet, attrs);
			break;
		case "viewNumber":
			/*
			 * moved earlier, must check first int number =
			 * Integer.parseInt((String) attrs.get("viewNo"));
			 * if(number==2){ viewNo=number; }
			 */
			ok = true;
			break;
		case "viewId":
			/*
			 * moved earlier, must check first if for EuclidianViewForPlane
			 */
			ok = true;
			break;
		default:
			Log.error("unknown tag in <euclidianView>: " + eName);
		}

		return ok;
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

		if (!startEuclidianViewElementSwitch(eName, attrs)) {
			Log.error("error in <euclidianView>: " + eName);
		}
	}

	// ====================================
	// <SpreadsheetView>
	// ====================================
	private void startSpreadsheetViewElement(String eName,
			LinkedHashMap<String, String> attrs) {
		boolean ok = true;

		switch (eName) {
		case "layout":
			ok = handleSpreadsheetLayout(attrs);
			break;
		case "prefCellSize":
			ok = handleSpreadsheetCellSize(attrs);
			break;
		case "size":
			ok = handleSpreadsheetSize(attrs);
			break;
		case "spreadsheetColumn":
			ok = handleSpreadsheetColumn(attrs);
			break;
		case "spreadsheetRow":
			ok = handleSpreadsheetRow(attrs);
			break;
		case "selection":
			ok = handleSpreadsheetInitalSelection(attrs);
			break;
		case "spreadsheetCellFormat":
			ok = handleSpreadsheetFormat(attrs);
			break;
		default:
			Log.error("unknown tag in <spreadsheetView>: " + eName);
		}

		if (!ok) {
			Log.error("error in <spreadsheetView>: " + eName);
		}
	}

	// ====================================
	// <ProbabilityCalculator>
	// ====================================
	private void startProbabilityCalculatorElement(String eName,
			LinkedHashMap<String, String> attrs) {
		boolean ok = true;

		switch (eName) {
		case "distribution":
			ok = handleProbabilityDistribution(attrs);
			break;
		case "interval":
			ok = handleProbabilityInterval(attrs);
			break;
		case "statisticsCollection":
			ok = handleStatisticsCollection(attrs);
			break;
		case "entry":
			ok = handleEntry(attrs);
			break;
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
			GeoNumeric[] parameters = new GeoNumeric[parmStringArray.length];
			for (int i = 0; i < parmStringArray.length; i++) {
				GeoNumberValue val = getNumber(parmStringArray[i]);
				parameters[i] =	val instanceof GeoNumeric ? (GeoNumeric) val
								: new GeoNumeric(cons, Double.NaN);
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
					.setLow(getNumber(attrs.get("low")));
			app.getSettings().getProbCalcSettings()
					.setHigh(getNumber(attrs.get("high")));

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

		switch (eName) {
		case "auxiliary":
			ok = handleAlgebraViewShowAuxiliaryObjects(attrs);
			break;
		case "collapsed":
			ok = handleAlgebraViewCollapsedNodes(attrs);
			break;
		case "mode":
			ok = handleAlgebraViewMode(attrs);
			break;
		default:
			Log.error("unknown tag in <algebraView>: " + eName);
		}

		if (!ok) {
			Log.error("error in <algebraView>: " + eName);
		}
	}

	private boolean handleCoordSystem(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		if (attrs.get("xZero") != null) {
			try {
				double xZero = parseDoubleNaN(attrs.get("xZero"));
				double yZero = parseDoubleNaN(attrs.get("yZero"));
				double scale = StringUtil.parseDouble(attrs.get("scale"));

				// new since version 2.5
				double yscale = scale;
				String strYscale = attrs.get("yscale");
				if (strYscale != null) {
					yscale = StringUtil.parseDouble(strYscale);
				}
				ev.setFileCoordSystem(xZero, yZero, scale, yscale);
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
	 * https://jira.geogebra.org/browse/TRAC-4030
	 * 
	 * @param s
	 *            serialized number
	 * @return 0 for NaN / undefined / null
	 */
	protected static double parseDoubleNaN(String s) {
		if ("NaN".equals(s) || "undefined".equals(s) || "null".equals(s)) {
			return 0;
		}
		return StringUtil.parseDouble(s);
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

			geoHandler.updatePointStyle(attrs);

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

	private static boolean handleRulerType(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		int rulerType = Integer.parseInt(attrs.get("val"));
		ev.setRulerType(rulerType);
		ev.setRulerBold(Boolean.parseBoolean(attrs.get("bold")));
		return true;
	}

	private static boolean handleEraserSize(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		int eraserSize = Integer.parseInt(attrs.get("val"));
		ev.setDeleteToolSize(eraserSize);
		return true;
	}

	private static boolean handlePenSize(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		int penSize = Integer.parseInt(attrs.get("val"));
		ev.setLastPenThickness(penSize);
		return true;
	}

	private static boolean handlePenColor(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		GColor col = handleColorAttrs(attrs);
		if (col == null) {
			return false;
		}
		ev.setLastSelectedPenColor(col);
		return true;
	}

	private static boolean handleHighlighterSize(EuclidianSettings ev,
			 LinkedHashMap<String, String> attrs) {
		int highlighterSize = Integer.parseInt(attrs.get("val"));
		ev.setLastHighlighterThinckness(highlighterSize);
		return true;
	}

	private static boolean handleHighlighterColor(EuclidianSettings ev,
			  LinkedHashMap<String, String> attrs) {
		GColor col = handleColorAttrs(attrs);
		if (col == null) {
			return false;
		}
		ev.setLastSelectedHighlighterColor(col);
		return true;
	}

	private static boolean handleLanguage(App app,
			  LinkedHashMap<String, String> attrs) {
		String lang = attrs.get("val");
		app.setLanguage(lang);
		return true;
	}

	private static boolean handleRulerColor(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		GColor col = handleColorAttrs(attrs);
		if (col == null) {
			return false;
		}
		ev.setBgRulerColor(col);
		return true;
	}

	private static boolean handleLineStyle(EuclidianSettings ev,
			LinkedHashMap<String, String> attrs) {
		try {
			ev.setAxesLineStyle(Integer.parseInt(attrs.get("axes")));
			ev.setGridLineStyle(Integer.parseInt(attrs.get("grid")));
			if (attrs.containsKey("ruler")) {
				ev.setRulerLineStyle(Integer.parseInt(attrs.get("ruler")));
			}
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
			e.printStackTrace();
			return false;
		}
	}

	protected boolean handleGrid(EuclidianSettings ev,
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
		switch (eName) {
		case "angleUnit":
			handleAngleUnit(attrs);
			break;
		case "algebraStyle":
			handleAlgebraStyle(attrs);
			break;
		case "coordStyle":
			handleKernelCoordStyle(attrs);
			break;
		case "angleFromInvTrig":
			handleKernelInvTrig(attrs);
			break;
		case "continuous":
			handleKernelContinuous(attrs);
			break;
		case "usePathAndRegionParameters":
			handleKernelUsePathAndRegionParameters(attrs);
			break;
		case "decimals":
			handleKernelDecimals(attrs);
			break;
		case "significantfigures":
			handleKernelFigures(attrs);
			break;
		case "startAnimation":
			handleKernelStartAnimation(attrs);
			break;
		case "localization":
			handleKernelLocalization(attrs);
			break;
		case "casSettings":
			handleCasSettings(attrs);
			break;
		default:
			if (!"uses3D".equals(eName)) {
				Log.error("unknown tag in <kernel>: " + eName);
			}
		}
	}

	private boolean handleAngleUnit(LinkedHashMap<String, String> attrs) {
		if (!app.getConfig().isAngleUnitSettingEnabled()) {
			return false;
		}
		if (attrs == null) {
			return false;
		}
		String angleUnit = attrs.get("val");
		if (angleUnit == null) {
			return false;
		}

		if (GeoGebraPreferencesXML.ANGLE_DEGREE_XML_NAME.equals(angleUnit)) {
			kernel.setAngleUnit(Kernel.ANGLE_DEGREE);
		} else if (GeoGebraPreferencesXML.ANGLE_RADIANT_XML_NAME.equals(angleUnit)) {
			kernel.setAngleUnit(Kernel.ANGLE_RADIANT);
		} else if (
				GeoGebraPreferencesXML.ANGLE_DEGREES_MINUTES_SECONDS_XML_NAME.equals(angleUnit)) {
                kernel.setAngleUnit(Kernel.ANGLE_DEGREES_MINUTES_SECONDS);
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
		switch (eName) {
		case "consProtColumns":
			ok = handleConsProtColumns(app, attrs);
			break;
		case "consProtocol":
			ok = handleConsProtocol(attrs);
			break;
		case "consProtNavigationBar":
			ok = handleConsProtNavigationBar(app, attrs);
			break;
		case "dataAnalysis":
			ok = handleDataAnalysis(attrs);
			break;
		case "font":
			ok = handleFont(app, attrs);
			break;
		case "graphicsSettings":
			ok = true;
			break;
		case "menuFont":
			ok = handleMenuFont(app, attrs);
			break;
		case "notesToolbarOpen":
			ok = handleNotesToolbarOpen(app, attrs);
			break;
		case "labelingStyle":
			ok = handleLabelingStyle(app, attrs);
			break;
		case "perspectives":
			mode = MODE_GUI_PERSPECTIVES;
			tmp_perspectives.clear();
			break;
		case "show":
			ok = handleGuiShow(app, attrs);
			break;
		case "splitDivider":
			ok = compLayout.handleSplitDivider(attrs);
			break;
		case "settings":
			ok = handleGuiSettings(app, attrs);
			break;
		case "toolbar":
			ok = handleToolbar(attrs);
			break;
		case "tooltipSettings":
			ok = handleTooltipSettings(app, attrs);
			break;
		case "window":
			ok = handleWindowSize(app, attrs);
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
					PlotType.valueOf(attrs.get("plot2")));
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
	 *            app
	 * @param attrs
	 *            gui tag attributes
	 * @return success
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
			app1.setShowAuxiliaryObjects(parseBoolean(str));
			str = attrs.get("algebraInput");
			tmp_perspective.setShowInputPanel(parseBooleanRev(str));
			str = attrs.get("cmdList");
			tmp_perspective.setShowInputPanelCommands(parseBooleanRev(str));
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
	 * &lt;settings ignoreDocument=".." showTitleBar=".." /&gt;
	 * 
	 * @param app
	 *            app
	 * @param attrs
	 *            settings tag attributes
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
	 *            app
	 * @param attrs
	 *            window tag attributes
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

	private static boolean handleNotesToolbarOpen(App app,
			LinkedHashMap<String, String> attrs) {
		try {
			boolean open = Boolean.parseBoolean(attrs.get("val"));
			app.setNotesToolbarOpen(open);
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
	 * Create a new temporary perspective for the current &lt;perspective&gt;
	 * element
	 * 
	 * @param attrs
	 *            perspective attributes
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

		switch (eName) {
		case "dockBar":
			ok = handleDockBar(attrs);
			break;
		case "input":
			ok = handleAlgebraInput(attrs);
			break;
		case "panes":
			mode = MODE_GUI_PERSPECTIVE_PANES;
			break;
		case "show":
			ok = handleGuiShow(app, attrs);
			break;
		case "toolbar":
			ok = handleToolbar(attrs);
			break;
		case "views":
			mode = MODE_GUI_PERSPECTIVE_VIEWS;
			break;
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
	 * Handle a view. &lt;view id=".." visible=".." inframe=".." stylebar=".."
	 * window=".." location=".." size=".." /&gt;
	 * 
	 * @param attrs
	 *            attributes of the view tag
	 * @return success
	 */
	private boolean handleView(LinkedHashMap<String, String> attrs) {
		try {
			int viewId = Integer.parseInt(attrs.get("id"));
			String toolbar = attrs.get("toolbar");
			boolean isVisible = !"false".equals(attrs.get("visible"));
			boolean openInFrame = "true".equals(attrs.get("inframe"));
			DockPanelData.TabIds tabId = getTabId(attrs.get("tab"));
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
			if (tabId != null) {
				dp.setTabId(tabId); // explicitly stored tab overrides config
			}
			// If we are loading a classic app with 3D visible, we should
			// open it in the 3d subApp
			if (isClassicFile() && dp.isVisible()
					&& dp.getViewId() == App.VIEW_EUCLIDIAN3D) {
				this.subAppCode = GeoGebraConstants.G3D_APPCODE;
			}
			tmp_views.add(dp);

			return true;
		} catch (RuntimeException e) {
			Log.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	private boolean isClassicFile() {
		return StringUtil.empty(subAppCode)
				|| GeoGebraConstants.CLASSIC_APPCODE.equals(subAppCode);
	}

	private DockPanelData.TabIds getTabId(String tab) {
		if (tab != null) {
			try {
				return DockPanelData.TabIds.valueOf(tab);
			} catch (RuntimeException e) {
				// enum value not found
			}
		}
		return null;
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
	 * Handle a pane. &lt;pane location".." divider=".." orientation=".." /&gt;
	 * 
	 * @param attrs
	 *            pane attributes
	 * @return success
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
			if (!(kernel instanceof MacroKernel)) {
				app.updateAppCodeSuite(subAppCode, tmp_perspective);
			}
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
			String strShowInToolBar = attrs.get("showInToolBar");
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
		cons.updateConstruction(true);
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
		if ("expression".equals(eName)) {
			ok = handleCasCellOutput(attrs);
		} else {
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
		if ("expression".equals(eName)) {
			ok = handleCasCellInput(attrs);
		} else {
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
				this.geoHandler.initDefault(attrs);
				kernel.setElementDefaultAllowed(old);
			} else {
				Log.error("unknown tag in <default>: " + eName);
			}
			break;

		case MODE_DEFAULT_GEO:
			this.geoHandler.startGeoElement(eName, attrs, errors);
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
				geoHandler.init(attrs);
			} else if ("command".equals(eName)) {
				cons.setOutputGeo(null);
				constMode = MODE_CONST_COMMAND;
				cmd = getCommand(attrs);
			} else if ("expression".equals(eName)) {
				startExpressionElement(attrs);
			} else if ("cascell".equals(eName)) {
				constMode = MODE_CONST_CAS_CELL;
				casMode = MODE_CONST_CAS_CELL;
			} else if ("group".equals(eName)) {
				geoHandler.handleGroup(attrs);
			} else if ("worksheetText".equals(eName)) {
				handleWorksheetText(attrs);
			} else {
				Log.error("unknown tag in <construction>: " + eName);
			}
			break;

		case MODE_CONST_GEO_ELEMENT:
			this.geoHandler.startGeoElement(eName, attrs, errors);
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
		String key = attrs.get("key");
		if (key != null && !key.contains("Random")) { // GGB-2415 old files have
														// Random entries
			this.casMap.put(key, attrs.get("val"));
		}
	}

	private void endConstructionElement(String eName) {
		switch (constMode) {
		case MODE_CONSTRUCTION:
			if ("construction".equals(eName)) {
				// process start points at end of construction
				this.geoHandler.processLists();
				cons.getLayerManager().updateList();
				processEvSizes();

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
				this.geoHandler.finish();
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
				this.geoHandler.processDefaultLists();
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

	protected void processEvSizes() {
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
				NumberValue n = getNumber(xmin.get(ev));
				ev.setXminObject(n, true);
			}
		}
		for (EuclidianSettings ev : eSet) {
			if (xmax.get(ev) == null) {
				ev.setXmaxObject(null, true);
			} else {
				NumberValue n = getNumber(xmax.get(ev));
				ev.setXmaxObject(n, true);
			}
		}
		for (EuclidianSettings ev : eSet) {
			if (ymin.get(ev) == null) {
				ev.setYminObject(null, true);
			} else {
				NumberValue n = getNumber(ymin.get(ev));
				ev.setYminObject(n, true);
			}
		}
		for (EuclidianSettings ev : eSet) {
			if (ymax.get(ev) == null) {
				ev.setYmaxObject(null, true);
			} else {
				NumberValue n = getNumber(ymax.get(ev));
				ev.setYmaxObject(n, true);
			}
			// ev.updateBounds();
		}
		for (EuclidianSettings ev : eSet) {
			if (!StringUtil.empty(xtick.get(ev))) {

				GeoNumberValue n = getNumber(xtick.get(ev));
				ev.setAxisNumberingDistance(0, n);
			}
			// ev.updateBounds();
		}
		for (EuclidianSettings ev : eSet) {
			if (!StringUtil.empty(ytick.get(ev))) {

				GeoNumberValue n = getNumber(ytick.get(ev));
				ev.setAxisNumberingDistance(1, n);
			}
			// ev.updateBounds();
		}
		for (EuclidianSettings ev : eSet) {
			if (!StringUtil.empty(ztick.get(ev))) {

				GeoNumberValue n = getNumber(ztick.get(ev));
				ev.setAxisNumberingDistance(2, n);
			}
			// ev.updateBounds();
		}
	}

	/**
	 * expects r, g, b attributes to build a color
	 * 
	 * @param attrs
	 *            r,g,b
	 * @return color
	 */
	protected static GColor handleColorAttrs(
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

	/**
	 * create absolute start point (coords expected)
	 * 
	 * @param attrs
	 *            tag atributes
	 * @return start point
	 */
	protected GeoPointND handleAbsoluteStartPoint(
			LinkedHashMap<String, String> attrs) {
		double x = StringUtil.parseDouble(attrs.get("x"));
		double y = StringUtil.parseDouble(attrs.get("y"));
		double z = StringUtil.parseDouble(attrs.get("z"));
		GeoPoint p = new GeoPoint(cons);
		p.setCoords(x, y, z);
		return p;
	}

	/**
	 * Handler collecting errors.
	 */
	ErrorHandler handler = new ErrorHandler() {

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
			int countLabels = 0;

			String randomVal = attrs.remove("randomResult");

			for (String value : attrs.values()) {
				String label = value;
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

			if (randomVal != null
					&& cmdOutput[0].getParentAlgorithm() instanceof SetRandomValue) {
				SetRandomValue randomizableAlgo =
						(SetRandomValue) cmdOutput[0].getParentAlgorithm();

				GeoElementND randomResult = getAlgProcessor()
						.evaluateToGeoElement(randomVal, false);

				randomizableAlgo.setRandomValue(randomResult);
			}

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

			int i = 0;
			for (String label : attrs.values()) {
				if (!StringUtil.empty(label) && cmdOutput[i] != null) {
					cmdOutput[i].setLoadedLabel(label);
				}
				i++;
			}

			return true;
		} catch (CommandNotLoadedError e) {
			throw e;
		} catch (Throwable t) {
			errors.add("processing of command: " + cmd);
			t.printStackTrace();
			return false;
		}
	}

	/**
	 * handle command output sizes (used only for some algos that have multiple
	 * types for output
	 * 
	 * @param attrs
	 *            cmd output attributes
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
	 *            ttribute map
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
				if (ve instanceof ExpressionNode) {
					if ("point".equals(type)) {
						((ExpressionNode) ve).setForcePoint();
					} else if ("vector".equals(type)) {
						((ExpressionNode) ve).setForceVector();
						// we must check that we have Equation here as xAxis
						// has also type "line" but is parsed as ExpressionNode
					} else if ("inequality".equals(type)) {
						((ExpressionNode) ve).setForceInequality();
					} else if ("surfacecartesian".equals(type)) {
						((ExpressionNode) ve).setForceSurfaceCartesian();
					}
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
							|| "implicitPoly".equals(type)) {
						((Equation) ve).setForceImplicitPoly();
					} else if ("implicitsurface".equals(type)) {
						((Equation) ve).setForceSurface();
					} else if ("function".equals(type)) {
						((Equation) ve).setForceFunction();
					}
				}
			}

			// Application.debug(""+getAlgProcessor());
			ve.setAsRootNode();
			GeoElementND[] result = getAlgProcessor()
					.processValidExpression(ve,
							new EvalInfo(!cons.isSuppressLabelsActive(), true)
									.withSymbolicMode(
											kernel.getSymbolicMode()));
			cons.registerFunctionVariable(null);
			// ensure that labels are set for invisible objects too
			if (result != null && label != null && result.length == 1) {
				result[0].setLoadedLabel(label);
			} else {
				Log.error(
						"error in <expression>: " + exp + ", label: " + label);
			}

		} catch (Exception | MyError e) {
			String msg = "error in <expression>: label=" + label + ", exp= "
					+ exp;
			Log.error(msg);
			Log.debug(e);
			errors.add(msg);
		}
	}

	protected AlgebraProcessor getAlgProcessor() {
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

	/**
	 * Initiate CAS cache for element
	 */
	public void casMapForElement() {
		casMap = new TreeMap<>();
		constMode = MODE_CAS_MAP;
		casMapParent = MODE_CONST_GEO_ELEMENT;
	}

	public HashMap<EuclidianSettings, String> getXmin() {
		return xmin;
	}

	public HashMap<EuclidianSettings, String> getXmax() {
		return xmax;
	}

	public HashMap<EuclidianSettings, String> getYmin() {
		return ymin;
	}

	public HashMap<EuclidianSettings, String> getYmax() {
		return ymax;
	}

	public void setNeedsConstructionDefaults(boolean needsConstructionDefaults) {
		geoHandler.setNeedsConstructionDefaults(needsConstructionDefaults);
	}
}
