package org.geogebra.common.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.cas.singularws.SingularWebService;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawList;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidian.smallscreen.AdjustScreen;
import org.geogebra.common.euclidian.smallscreen.AdjustViews;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.euclidian3D.Input3D;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.factories.Factory;
import org.geogebra.common.geogebra3D.util.CopyPaste3D;
import org.geogebra.common.gui.menubar.MenuFactory;
import org.geogebra.common.gui.menubar.OptionsMenu;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.gui.view.properties.PropertiesView;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.javax.swing.GImageIcon;
import org.geogebra.common.kernel.AnimationManager;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.GeoGebraCasInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.Relation;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.UndoManager;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.CommandsConstants;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.parser.cashandlers.ParserFunctions;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.ConstructionProtocolSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventDispatcher;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GeoScriptRunner;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.plugin.SensorLogger;
import org.geogebra.common.plugin.script.GgbScript;
import org.geogebra.common.plugin.script.Script;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.CommandInputField;
import org.geogebra.common.util.CopyPaste;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimer.GTimerListener;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.LowerCaseDictionary;
import org.geogebra.common.util.NormalizerMinimal;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Util;
import org.geogebra.common.util.debug.Log;

/**
 * Represents an application window, gives access to views and system stuff
 */
public abstract class App implements UpdateSelection {
	/** Url for wiki article about functions */
	public static final String WIKI_OPERATORS = "Predefined Functions and Operators";
	/** Url for main page of manual */
	public static final String WIKI_MANUAL = "Manual";
	/** Url for wiki article about CAS */
	public static final String WIKI_CAS_VIEW = "CAS_View";
	/** Url for wiki tutorials */
	public static final String WIKI_TUTORIAL = "Tutorial:Main Page";
	/** Url for Intel RealSense tutorials */
	public static final String REALSENSE_TUTORIAL = "https://www.geogebra.org/b/OaGmb7LE";
	/**
	 * Url for wiki article about exporting to HTML changed to GeoGebra
	 * Materials upload from ggb44
	 */
	public static final String WIKI_EXPORT_WORKSHEET = "Upload_to_GeoGebra_Materials";
	/**
	 * Url for wiki article about advanced features (layers, cond. visibility
	 * etc.)
	 */
	public static final String WIKI_ADVANCED = "Advanced Features";
	/** Url for wiki article about functions */
	public static final String WIKI_TEXT_TOOL = "Text Tool";
	/** id for dummy view */
	public static final int VIEW_NONE = 0;
	/** id for euclidian view */
	public static final int VIEW_EUCLIDIAN = 1;
	/** id for algebra view */
	public static final int VIEW_ALGEBRA = 2;
	/** id for Spreadsheet view */
	public static final int VIEW_SPREADSHEET = 4;
	/** id for CAS view */
	public static final int VIEW_CAS = 8;
	/** id for second euclidian view */
	public static final int VIEW_EUCLIDIAN2 = 16;
	/** id for construction protocol view */
	public static final int VIEW_CONSTRUCTION_PROTOCOL = 32;
	/** id for probability calculator view */
	public static final int VIEW_PROBABILITY_CALCULATOR = 64;
	/** id for data analysis view, ie multi/single/two variable analysisis tools */
	public static final int VIEW_DATA_ANALYSIS = 70;
	/** id for function inspector */
	public static final int VIEW_FUNCTION_INSPECTOR = 128;
	/** id for 3D view */
	public static final int VIEW_EUCLIDIAN3D = 512;
	/** id for 2nd 3D view */
	public static final int VIEW_EUCLIDIAN3D_2 = 513;
	/** let us break the pattern */
	public static final int VIEW_EVENT_DISPATCHER = 42;
	/**
	 * id for view created from plane; also 1025 to 2047 might be used for this
	 * purpose
	 */
	public static final int VIEW_EUCLIDIAN_FOR_PLANE_START = 1024;
	/** maximal ID of view for plane */
	public static final int VIEW_EUCLIDIAN_FOR_PLANE_END = 2047;
	/**
	 * id for plot panels (small EVs eg in regression analysis tool)
	 */
	public static final int VIEW_PLOT_PANEL = 2048;
	/**
	 * id for text preview in text tool
	 */
	public static final int VIEW_TEXT_PREVIEW = 4096;
	/**
	 * id for properties view
	 */
	public static final int VIEW_PROPERTIES = 4097;
	// please let 1024 to 2047 empty
	/** id for assignment view */
	public static final int VIEW_ASSIGNMENT = 8192;
	/** id for spreadsheet table model */
	public static final int VIEW_TABLE_MODEL = 9000;
	/** data collection view (web only) */
	public static final int VIEW_DATA_COLLECTION = 43;
	public static final int DEFAULT_THRESHOLD = 3;
	/**
	 * minimal font size
	 */
	public static final int MIN_FONT_SIZE = 10;
	/**
	 * initial number of columns for spreadsheet
	 */
	public static final int SPREADSHEET_INI_COLS = 26;
	/**
	 * initial number of rows for spreadsheet
	 */
	public static final int SPREADSHEET_INI_ROWS = 100;
	// used by PropertyDialogGeoElement and MenuBarImpl
	// for the Rounding Menus
	/**
	 * Rounding menu options (not internationalized)
	 */
	final private static String[] strDecimalSpacesAC = { "0 decimals",
			"1 decimals", "2 decimals", "3 decimals", "4 decimals",
			"5 decimals", "10 decimals", "15 decimals", "", "3 figures",
			"5 figures", "10 figures", "15 figures"};

	public static String[] getStrDecimalSpacesAC() {
		return strDecimalSpacesAC;
	}
	/** Singular web service (CAS) */
	private static SingularWebService singularWS;

	protected boolean useFullAppGui = false;
	private static String CASVersionString = "";
	private static boolean CASViewEnabled = true;
	private static boolean _3DViewEnabled = true;
	/**
	 * whether axes should be visible when EV is created first element of this
	 * array is for x-axis, second for y-axis
	 */
	protected final boolean[] showAxes = {true, true};
	/** whether axes should be logarithmci when EV is created */
	protected final boolean[] logAxes = {false, false};
	/**
	 * Whether we are running applet in frame. Not possible with 4.2+ (we need
	 * this to hide reset icon from EV)
	 */
	public boolean runningInFrame = false;
	public Vector<GeoImage> images = new Vector<GeoImage>();
	/**
	 * Whether AV should show auxiliary objects stored here rather than in
	 * algebra view so that it can be set without creating an AV (compatibility
	 * with 3.2)
	 */
	public boolean showAuxiliaryObjects = false;
	/** flag to test whether to draw Equations full resolution */
	public ExportType exportType = ExportType.NONE;
	/**
	 * right angle style
	 *
	 * @see EuclidianStyleConstants#RIGHT_ANGLE_STYLE_SQUARE
	 * @see EuclidianStyleConstants#RIGHT_ANGLE_STYLE_DOT
	 * @see EuclidianStyleConstants#RIGHT_ANGLE_STYLE_L
	 * @see EuclidianStyleConstants#RIGHT_ANGLE_STYLE_NONE
	 */
	public int rightAngleStyle = EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE;
	public AlgoKimberlingWeightsInterface kimberlingw = null;
	public AlgoCubicSwitchInterface cubicw = null;
	/**
	 * whether transparent cursor should be used while dragging
	 */
	public boolean useTransparentCursorWhenDragging = false;
	/**
	 * Script manager
	 */
	protected ScriptManager scriptManager = null;
	/** whether current construction was saved after last changes */
	protected boolean isSaved = true;
	/**
	 * object is hit if mouse is within this many pixels (more for points, see
	 * geogebra.common.euclidian.DrawPoint)
	 */
	protected int capturingThreshold = DEFAULT_THRESHOLD;
	/** on touch devices we want larger threshold for point hit testing */
	protected int capturingThresholdTouch = 3 * DEFAULT_THRESHOLD;

	/* Font settings */
	/**
	 * where to show the inputBar (respective inputBox)
	 */
	protected InputPosition showInputTop = InputPosition.algebraView;
	/**
	 * Whether input bar should be visible
	 */
	protected boolean showAlgebraInput = true;
	/**
	 * Whether toolbar should appear on top
	 */
	protected boolean showToolBarTop = true;
	// note: It is not necessary to use powers of 2 for view IDs

	// For eg Hebrew and Arabic.
	/**
	 * Whether toolbar help should appear
	 */
	protected boolean showToolBarHelp = false;
	/**
	 * Toolbar position
	 */
	protected int toolbarPosition = 1;
	/**
	 * Whether input help toggle button should be visible
	 */
	protected boolean showInputHelpToggle = true;
	/**
	 * whether righ click is enabled
	 */
	protected boolean rightClickEnabled = true;
	/**
	 * User Sign in handling
	 */
	protected LogInOperation loginOperation = null;
	/** XML input / output handler */
	protected MyXMLio myXMLio;
	/** gui / menu fontsize (-1 = use appFontSize) */
	protected int guiFontSize = -1;
	/** kernel */
	protected Kernel kernel;
	/** whether points can be created by other tools than point tool */
	protected boolean isOnTheFlyPointCreationActive = true;
	/** Settings object */
	protected Settings settings;
	/** Selections in this app */
	protected SelectionManager selection;
	/** whether we should use antialisaing in EV */
	protected boolean antialiasing = true;
	/** whether grid should be visible when EV is created */
	protected boolean showGrid = false;
	/**
	 * this flag is true during initialization phase (until GUI is built and
	 * command line args handled, incl. file loading) or when we are opening a
	 * file
	 */
	protected boolean initing = false;
	/** Euclidian view */
	protected EuclidianView euclidianView;
	/** Euclidian view's controller */
	protected EuclidianController euclidianController;
	/** selection listener */
	protected GeoElementSelectionListener currentSelectionListener;
	/**
	 * whether menubar should be visible
	 */
	protected boolean showMenuBar = true;
	protected String uniqueId;
	protected ArrayList<Perspective> tmpPerspectives = new ArrayList<Perspective>();
	/**
	 * whether toolbar should be visible
	 */
	protected boolean showToolBar = true;
	/**
	 * whether shift, drag and zoom features are enabled
	 */
	protected boolean shiftDragZoomEnabled = true;
	protected int appletWidth = 0;
	protected int appletHeight = 0;
	protected boolean useFullGui = false;
	protected int appCanvasHeight;
	protected int appCanvasWidth;
	protected boolean needsSpreadsheetTableModel = false;
	protected HashMap<Integer, Boolean> showConstProtNavigationNeedsUpdate = null;
	protected HashMap<Integer, Boolean> showConsProtNavigation = null;
	protected AppCompanion companion;
	protected boolean prerelease;
	protected boolean canary;
	/**
	 * given 1, return eg 1st, 1e, 1:e according to the language
	 * <p/>
	 * http://en.wikipedia.org/wiki/Ordinal_indicator
	 */
	StringBuilder sbOrdinal;
	/**
	 * id for Python view
	 */
	// public static final int VIEW_PYTHON = 16384;
	private boolean showResetIcon = false;
	private ParserFunctions pf;
	private SpreadsheetTraceManager traceManager;
	private ExamEnvironment exam;
	// currently used application fonts
	private int appFontSize = 16;
	// moved to Application from EuclidianView as the same value is used across
	// multiple EVs
	private int maxLayerUsed = 0;
	/**
	 * size of checkboxes, default in GeoGebraPreferencesXML.java
	 * checkboxSize="26"
	 */
	private int booleanSize = EuclidianConstants.DEFAULT_CHECKBOX_SIZE;
	private boolean labelDragsEnabled = true;
	private boolean undoRedoEnabled = true;

	// command dictionary
	private LowerCaseDictionary commandDict;
	private LowerCaseDictionary commandDictCAS;
	// array of dictionaries corresponding to the sub command tables
	private LowerCaseDictionary[] subCommandDict;
	private String scriptingLanguage;
	/**
	 * flag for current state
	 */
	private StoreUndoInfoForSetCoordSystem storeUndoInfoForSetCoordSystem = StoreUndoInfoForSetCoordSystem.NONE;
	private boolean blockUpdateScripts = false;
	private boolean useBrowserForJavaScript = true;
	private EventDispatcher eventDispatcher;
	private int[] versionArray = null;
	private List<SavedStateListener> savedListeners = new ArrayList<SavedStateListener>();
	private Macro macro;
	private int labelingStyle = ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY;
	/**
	 * says that a labeling style is selected in menu
	 * (i.e. all default geos use the selected labeling style)
	 */
	private boolean labelingStyleSelected = true;
	private boolean scriptingDisabled = false;
	private double exportScale = 1;
	private PropertiesView propertiesView;
	private Random random = new Random();
	private GeoScriptRunner geoScriptRunner;
	private GeoElement geoForCopyStyle;
	private OptionsMenu optionsMenu;
	private boolean isErrorDialogsActive = true;
	private ArrayList<OpenFileListener> openFileListener;
	// whether to allow perspective and login popups
	private boolean allowPopUps = false;

	private Versions version;

	// TODO: move following methods somewhere else
	private int tubeID = 0;

	/**
	 * Please call setVersion right after this
	 */
	public App() {
		companion = newAppCompanion();
		resetUniqueId();
	}
	/**
	 * constructor
	 */
	public App(Versions version) {
		this();
		this.version = version;
	}

	/**
	 * Changes version; should be called only once, right after the constructor
	 * 
	 * @param version
	 */
	protected void setVersion(Versions version) {
		this.version = version;
	}

	/**
	 * @return CAS version
	 */
	public static final String getCASVersionString() {
		return CASVersionString;

	}

	/**
	 * @param string CAS version string
	 */
	public static final void setCASVersionString(String string) {
		CASVersionString = string;

	}

	/**
	 * Initializes SingularWS
	 */
	public static void initializeSingularWS() {
		singularWS = new SingularWebService();
		singularWS.enable();
		if (singularWS.isAvailable()) {
			Log.info("SingularWS is available at "
					+ singularWS.getConnectionSite());
			// debug(singularWS.directCommand("ring r=0,(x,y),dp;ideal I=x^2,x;groebner(I);"));
		} else {
			Log.info("No SingularWS is available at "
					+ singularWS.getConnectionSite() + " (yet)");
		}
	}

	public static SingularWebService getSingularWS() {
		return singularWS;
	}

	/* selection handling */

	/**
	 * @param version string version, eg 4.9.38.0
	 * @return version as list of ints, eg [4,9,38,0]
	 */
	static final public int[] getSubValues(String version) {
		String[] values = version.split("\\.");
		int[] ret = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			ret[i] = Integer.parseInt(values[i]);
		}

		return ret;
	}

	/**
	 * Please use !app.isApplet() where appropriate
	 * 
	 * @return whether app is an instance of AppWapplication.
	 */
	@Deprecated
	public boolean isFullAppGui() {
		return useFullAppGui;
	}

	/**
	 * Disables CAS View.
	 */
	public static void disableCASView() {
		CASViewEnabled = false;
	}

	// Rounding Menus end

	/**
	 * Disables 3D View.
	 */
	public static void disable3DView() {
		_3DViewEnabled = false;
	}

	/**
	 * Gets max scale based on EV size; scale down if EV too big to avoid
	 * clipboard errors
	 * 
	 * @param ev
	 *            view
	 * @return maximum scale for clipboard images; default 2
	 */
	public static double getMaxScaleForClipBoard(EuclidianView ev) {
		double size = ev.getExportWidth() * ev.getExportHeight();

		// Windows XP clipboard has trouble with images larger than this
		// at double scale (with scale = 2d)
		if (size > 500000) {
			return 2.0 * Math.sqrt(500000 / size);
		}

		return 2d;
	}

	/**
	 *
	 * @param id
	 *            view id
	 * @return true if id is a 3D view id
	 */
	public static final boolean isView3D(int id) {
		if (id == App.VIEW_EUCLIDIAN3D) {
			return true;
		}

		if (id == App.VIEW_EUCLIDIAN3D_2) {
			return true;
		}

		return false;

	}

	/**
	 * @return global checkbox size 13 or 26 (all checkboxes, both views)
	 */
	public int getCheckboxSize() {
		return booleanSize;
	}

	/**
	 *
	 * set global checkbox size (all checkboxes, both views)
	 *
	 * @param b
	 *            new size for checkboxes (either 13 or 26)
	 */
	public void setCheckboxSize(int b) {
		booleanSize = (b == 13) ? 13 : 26;
	}

	/**
	 * @param type
	 *            mouse or touch
	 * @return capturing threshold
	 */
	public int getCapturingThreshold(PointerEventType type) {
		return type == PointerEventType.TOUCH ? this.capturingThresholdTouch
				: this.capturingThreshold;
	}

	/**
	 * @param i capturing threshold
	 */
	public void setCapturingThreshold(int i) {
		this.capturingThreshold = i;
		this.capturingThresholdTouch = 3 * i;
	}

	/**
	 * We need this method so that we can override it using more powerful
	 * normalizer
	 *
	 * @return new lowercase dictionary
	 */
	public LowerCaseDictionary newLowerCaseDictionary() {
		return new LowerCaseDictionary(new NormalizerMinimal());
	}

	/**
	 * Fills CAS command dictionary and translation table. Must be called before
	 * we start using CAS view.
	 */
	public void fillCasCommandDict() {
		// this method might get called during initialization, when we're not
		// yet
		// ready to fill the casCommandDict. In that case, we will fill the
		// dict during fillCommandDict :)

		if (!getLocalization().isCommandChanged()
				&& ((commandDictCAS != null) || getLocalization()
				.isCommandNull())) {
			return;
		}
		GeoGebraCasInterface cas = kernel.getGeoGebraCAS();
		if (cas == null || subCommandDict == null) {
			return;
		}
		getLocalization().setCommandChanged(false);

		commandDictCAS = newLowerCaseDictionary();
		subCommandDict[CommandsConstants.TABLE_CAS].clear();

		// get all commands from the commandDict and write them to the
		// commandDictCAS

		// the keySet contains all commands of the dictionary; see
		// LowerCaseDictionary.addEntry(String s) for more
		Collection<String> commandDictContent = commandDict.values();

		// write them to the commandDictCAS
		for (String cmd : commandDictContent) {
			commandDictCAS.addEntry(cmd);
		}

		// iterate through all available CAS commands, add them (translated if
		// available, otherwise untranslated)
		for (String cmd : cas.getAvailableCommandNames()) {

			try {
				String local = getLocalization().getCommand(cmd);
				putInTranslateCommandTable(Commands.valueOf(cmd), local);
				if (local != null) {
					commandDictCAS.addEntry(local);
					subCommandDict[CommandsConstants.TABLE_CAS].addEntry(local);
				} else {
					commandDictCAS.addEntry(cmd);
					subCommandDict[CommandsConstants.TABLE_CAS].addEntry(cmd);
				}
			} catch (Exception mre) {
				commandDictCAS.addEntry(cmd);
				subCommandDict[CommandsConstants.TABLE_CAS].addEntry(cmd);
			}
		}
	}

	/**
	 * @return command dictionary for CAS
	 */
	public final LowerCaseDictionary getCommandDictionaryCAS() {
		fillCommandDict();
		fillCasCommandDict();
		return commandDictCAS;
	}

	/**
	 * Returns an array of command dictionaries corresponding to the categorized
	 * sub command sets created in CommandDispatcher.
	 *
	 * @return command dictionaries corresponding to the categories
	 */
	public final LowerCaseDictionary[] getSubCommandDictionary() {

		if (subCommandDict == null) {
			initTranslatedCommands();
		}
		if (getLocalization().isCommandChanged())
			updateCommandDictionary();

		return subCommandDict;
	}

	/**
	 * Initializes the translated command names for this application. Note: this
	 * will load the properties files first.
	 */
	final public void initTranslatedCommands() {
		if (getLocalization().isCommandNull() || subCommandDict == null) {
			getLocalization().initCommand();
			fillCommandDict();
			kernel.updateLocalAxesNames();
		}
	}

	/**
	 * @return command dictionary
	 */
	public final LowerCaseDictionary getCommandDictionary() {
		fillCommandDict();
		return commandDict;
	}

	/**
	 * Fill command dictionary and translation table. Must be called before we
	 * start using Input Bar.
	 */
	protected void fillCommandDict() {
		getLocalization().initCommand();
		if (!getLocalization().isCommandChanged()) {
			return;
		}
		// translation table for all command names in command.properties
		getLocalization().initTranslateCommand();
		// command dictionary for all public command names available in
		// GeoGebra's input field
		// removed check for null: commandDict.clear() removes keys, but they
		// are still available with commandDict.getIterator()
		// so change English -> French -> English doesn't work in the input bar
		// see AutoCompleteTextfield.lookup()
		// if (commandDict == null)
		commandDict = newLowerCaseDictionary();
		// else commandDict.clear();



		// =====================================
		// init sub command dictionaries

		if (subCommandDict == null) {
			subCommandDict = new LowerCaseDictionary[CommandDispatcher.tableCount];
			for (int i = 0; i < subCommandDict.length; i++) {
				subCommandDict[i] = newLowerCaseDictionary();
			}
		}
		for (int i = 0; i < subCommandDict.length; i++) {
			subCommandDict[i].clear();
			// =====================================
		}
		HashMap<String, String> translateCommandTable = getLocalization()
				.getTranslateCommandTable();
		for (Commands comm : Commands.values()) {
			String internal = comm.name();
			if (!companion.tableVisible(comm.getTable())
					|| !kernel.getAlgebraProcessor().isCommandsEnabled()) {
				if (comm.getTable() == CommandsConstants.TABLE_ENGLISH) {
					putInTranslateCommandTable(comm, null);
				}
				continue;
			}

			// Log.debug(internal);
			String local = getLocalization().getCommand(internal);
			putInTranslateCommandTable(comm, local);

			if (local != null) {
				local = local.trim();
				// case is ignored in translating local command names to
				// internal names!
				translateCommandTable.put(StringUtil.toLowerCase(local),
						internal);

				commandDict.addEntry(local);
				// add public commands to the sub-command dictionaries
				subCommandDict[comm.getTable()].addEntry(local);

			}

		}
		getParserFunctions().updateLocale(getLocalization());
		// get CAS Commands
		if (kernel.isGeoGebraCASready()) {
			fillCasCommandDict();
		}
		addMacroCommands();
		getLocalization().setCommandChanged(false);
	}

	private void putInTranslateCommandTable(Commands comm, String local) {
		String internal = comm.name();
		// Check that we don't overwrite local with English
		HashMap<String, String> translateCommandTable = getLocalization()
				.getTranslateCommandTable();
		if (!translateCommandTable
				.containsKey(StringUtil.toLowerCase(internal))) {
			translateCommandTable.put(StringUtil.toLowerCase(internal),
					Commands.englishToInternal(comm).name());
		}
		if (comm.getTable() == CommandsConstants.TABLE_ENGLISH) {
			return;
		}

		if (local != null) {
			translateCommandTable.put(StringUtil.toLowerCase(local), Commands
					.englishToInternal(comm).name());
		}

	}

	/**
	 * translate command name to internal name. Note: the case of localname is
	 * NOT relevant
	 *
	 * @param command local name
	 * @return internal name
	 */
	public String getReverseCommand(String command) {
		// don't init command table on file loading
		if (kernel.isUsingInternalCommandNames()) {
			try {
				Commands.valueOf(command);
				return command;
			} catch (Exception e) {
				// not a valid command, fall through
			}
		}
		initTranslatedCommands();

		return getLocalization().getReverseCommand(command);

	}

	/**
	 * Updates command dictionary
	 */
	public void updateCommandDictionary() {
		// make sure all macro commands are in dictionary
		if (commandDict != null) {
			fillCommandDict();
		}
	}

	/**
	 * Adds macro commands to the dictionary
	 */
	protected void addMacroCommands() {
		if ((commandDict == null) || (kernel == null) || !kernel.hasMacros()) {
			return;
		}

		ArrayList<Macro> macros = kernel.getAllMacros();
		for (int i = 0; i < macros.size(); i++) {
			String cmdName = macros.get(i).getCommandName();
			if (!commandDict.containsValue(cmdName)) {
				commandDict.addEntry(cmdName);
			}
		}
	}

	/**
	 * Remove macros from command dictionary
	 */
	public void removeMacroCommands() {
		if ((commandDict == null) || (kernel == null) || !kernel.hasMacros()) {
			return;
		}

		ArrayList<Macro> macros = kernel.getAllMacros();
		for (int i = 0; i < macros.size(); i++) {
			String cmdName = macros.get(i).getCommandName();
			commandDict.removeEntry(cmdName);
		}
	}

	public abstract boolean isApplet();

	/**
	 * Store current state of construction for undo/redo purposes
	 */
	public abstract void storeUndoInfo();

	/**
	 * Store current state of construction for undo/redo purposes, and state of
	 * construction for mode starting (so undo cancels partial tool preview)
	 */
	public void storeUndoInfoAndStateForModeStarting() {
		storeUndoInfoAndStateForModeStarting(true);
	}

	final public void storeUndoInfoAndStateForModeStarting(boolean storeForMode) {
		if (isUndoActive()) {
			if (storeForMode) {
				kernel.storeUndoInfoAndStateForModeStarting();
			} else {
				kernel.storeUndoInfo();
			}
			setUnsaved();
		}
	}

	/**
	 * store undo info only if view coord system has changed
	 */
	public void storeUndoInfoIfSetCoordSystemOccured() {

		if (storeUndoInfoForSetCoordSystem == StoreUndoInfoForSetCoordSystem.SET_COORD_SYSTEM_OCCURED) {
			storeUndoInfo();
		}

		storeUndoInfoForSetCoordSystem = StoreUndoInfoForSetCoordSystem.NONE;
	}

	/**
	 * tells the application that a view coord system has changed
	 */
	public void setCoordSystemOccured() {

		if (storeUndoInfoForSetCoordSystem == StoreUndoInfoForSetCoordSystem.MAY_SET_COORD_SYSTEM) {
			storeUndoInfoForSetCoordSystem = StoreUndoInfoForSetCoordSystem.SET_COORD_SYSTEM_OCCURED;
		}
	}

	/**
	 * tells the coord sys may be set
	 */
	public void maySetCoordSystem() {
		if (storeUndoInfoForSetCoordSystem == StoreUndoInfoForSetCoordSystem.NONE) {
			storeUndoInfoForSetCoordSystem = StoreUndoInfoForSetCoordSystem.MAY_SET_COORD_SYSTEM;
		}
	}

	/**
	 * @return true if we have access to complete gui (menubar, toolbar); false
	 *         for minimal applets (just one EV, no gui)
	 */
	public abstract boolean isUsingFullGui();

	/**
	 *
	 * @param view
	 *            view ID
	 * @return whether view with given ID is visible
	 */
	public abstract boolean showView(int view);

	public boolean letRename() {
		return true;
	}

	public boolean letDelete() {
		return true;
	}

	public boolean letRedefine() {
		return true;
	}

	/**
	 * @return the blockUpdateScripts
	 */
	public boolean isBlockUpdateScripts() {
		return blockUpdateScripts;
	}

	/**
	 * @param blockUpdateScripts the blockUpdateScripts to set
	 */
	public void setBlockUpdateScripts(boolean blockUpdateScripts) {
		this.blockUpdateScripts = blockUpdateScripts;
	}

	/**
	 * Translates localized command name into internal TODO check whether this
	 * differs from translateCommand somehow and either document it or remove
	 * this method
	 *
	 * @param cmd
	 *            localized command name
	 * @return internal command name
	 */
	public String getInternalCommand(String cmd) {
		initTranslatedCommands();
		String s;
		String cmdLower = StringUtil.toLowerCase(cmd);
		Commands[] values = Commands.values();
		for (Commands c : values) {
			s = Commands.englishToInternal(c).name();

			// make sure that when si[] is typed in script, it's changed to
			// Si[] etc
			if (StringUtil.toLowerCase(getLocalization().getCommand(s)).equals(
					cmdLower)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Show error dialog wiith given text
	 *
	 * @param s error message
	 */
	public abstract void showError(String s);

	/**
	 * Shows error dialog with a given text
	 *
	 * @param s
	 */
	protected abstract void showErrorDialog(String s);

	/**
	 * @param useBrowserForJavaScript
	 *            desktop: determines whether Rhino will be used (false) or the
	 *            browser (true) web: determines whether JS input comes from the
	 *            html file (true) or from the ggb file (false)
	 */
	public void setUseBrowserForJavaScript(boolean useBrowserForJavaScript) {
		this.useBrowserForJavaScript = useBrowserForJavaScript;
	}

	/**
	 * @return desktop: determines whether Rhino will be used (false) or the
	 *         browser (true) web: determines whether JS input comes from the
	 *         html file (true) or from the ggb file (false)
	 */
	public boolean useBrowserForJavaScript() {
		return useBrowserForJavaScript;
	}

	/**
	 * @return script manager
	 */
	public abstract ScriptManager getScriptManager();

	/**
	 * Get the event dispatcher, which dispatches events objects that manage
	 * event driven scripts
	 *
	 * @return the app's event dispatcher
	 */
	public EventDispatcher getEventDispatcher() {
		if (eventDispatcher == null) {
			eventDispatcher = new EventDispatcher(this);
		}
		return eventDispatcher;
	}

	/**
	 * @param ge
	 *            geo
	 * @return trace-related XML elements
	 */
	final public String getTraceXML(GeoElement ge) {
		return getTraceManager().getTraceXML(ge);
	}

	/**
	 * Start tracing geo to spreadsheet
	 *
	 * @param ge geo
	 */
	public void traceToSpreadsheet(GeoElement ge) {
		getTraceManager().traceToSpreadsheet(ge);
	}

	/**
	 * Reset tracing column for given geo
	 *
	 * @param ge geo
	 */
	public void resetTraceColumn(GeoElement ge) {
		getTraceManager().setNeedsColumnReset(ge, true);
	}

	/**
	 * Updates the counter of used layers
	 *
	 * @param layer
	 *            layer to which last element was added
	 */
	public void updateMaxLayerUsed(int layer) {
		int newLayer = layer;
		if (layer > EuclidianStyleConstants.MAX_LAYERS) {
			newLayer = EuclidianStyleConstants.MAX_LAYERS;
		}
		if (layer > maxLayerUsed) {
			maxLayerUsed = newLayer;
		}
	}

	/**
	 * @return whether this is a 3D app or not
	 */
	public boolean is3D() {
		return false;
	}

	/**
	 * @return last created GeoElement
	 */
	final public GeoElement getLastCreatedGeoElement() {
		return kernel.getConstruction().getLastGeoElement();
	}

	/**
	 * Deletes selected objects
	 */
	public void deleteSelectedObjects() {
		if (letDelete()) {
			Object[] geos = selection.getSelectedGeos().toArray();
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!geo.isFixed()) {
					geo.removeOrSetUndefinedIfHasFixedDescendent();
				}
			}

			// also delete just created geos if possible
			ArrayList<GeoElement> geos2 = getActiveEuclidianView()
					.getEuclidianController().getJustCreatedGeos();
			for (int j = 0; j < geos2.size(); j++) {
				GeoElement geo = geos2.get(j);
				if (!geo.isFixed()) {
					geo.removeOrSetUndefinedIfHasFixedDescendent();
				}
			}
			getActiveEuclidianView().getEuclidianController()
					.clearJustCreatedGeos();
			getActiveEuclidianView().getEuclidianController().clearSelections();
			storeUndoInfoAndStateForModeStarting();
		}

	}

	/**
	 * @return whether auxiliary objects are shown in AV
	 */
	public boolean showAuxiliaryObjects() {
		return showAuxiliaryObjects;
	}

	/**
	 * Append XML describing the keyboard to given string builder
	 *
	 * @param sb string builder
	 */
	public void getKeyboardXML(StringBuilder sb) {
		sb.append("<keyboard width=\"");
		sb.append(getSettings().getKeyboard().getKeyboardWidth());
		sb.append("\" height=\"");
		sb.append(getSettings().getKeyboard().getKeyboardHeight());
		sb.append("\" opacity=\"");
		sb.append(getSettings().getKeyboard().getKeyboardOpacity());
		sb.append("\" language=\"");
		sb.append(getSettings().getKeyboard().getKeyboardLocale());
		sb.append("\" show=\"");
		sb.append(getSettings().getKeyboard().isShowKeyboardOnStart());
		sb.append("\"/>");
	}

	/**
	 * @return true if we have critically low free memory
	 */
	public abstract boolean freeMemoryIsCritical();

	/**
	 * @return Approximate amount of remaining memory in bytes
	 */
	public abstract long freeMemory();

	/**
	 * set right angle style
	 *
	 * @param style style
	 */
	public void setRightAngleStyle(int style) {
		rightAngleStyle = style;
	}

	/**
	 * @return the maximal currently used layer
	 */
	public int getMaxLayerUsed() {
		return maxLayerUsed;
	}

	/**
	 * @param min
	 *            real world x min
	 * @param max
	 *            real world x max
	 * @return number of pixels in EV1 between given x coordinates
	 */
	public double countPixels(double min, double max) {
		EuclidianView ev = getEuclidianView1();
		return ev.toScreenCoordXd(max) - ev.toScreenCoordXd(min);
	}

	/**
	 * @return algebra view
	 */
	public abstract AlgebraView getAlgebraView();

	/**
	 * @return EV1
	 */
	public EuclidianView getEuclidianView1() {
		Log.notice("AbstrEuclView");
		return euclidianView;
	}

	/**
	 * Resets the maximal used llayer to 0
	 */
	public void resetMaxLayerUsed() {
		maxLayerUsed = 0;
	}

	/**
	 * @return active euclidian view (may be EV, EV2 or 3D)
	 */
	public abstract EuclidianView getActiveEuclidianView();

	/**
	 * @return whether 3D view was initialized
	 */
	public boolean hasEuclidianView3D() {
		return false;
	}

	public boolean isEuclidianView3Dinited() {
		return false;
	}

	/**
	 * @return 3D view
	 */
	public EuclidianView3DInterface getEuclidianView3D() {
		return null;
	}

	/**
	 * @return whether EV2 was initialized
	 */
	public abstract boolean hasEuclidianView2EitherShowingOrNot(int idx);

	/**
	 * @return whether EV2 is visible
	 */
	public abstract boolean isShowingEuclidianView2(int idx);

	/**
	 * @return image manager
	 */
	public abstract ImageManager getImageManager();

	/**
	 * @return gui manager (it's null in minimal applets)
	 */
	public abstract GuiManagerInterface getGuiManager();

	/**
	 * @return dialog manager
	 */
	public abstract DialogManager getDialogManager();

	/**
	 * Initializes GUI manager
	 */
	protected abstract void initGuiManager();

	/**
	 * Whether we are running on Mac
	 *
	 * @return whether we are running on Mac
	 */
	public boolean isMacOS() {
		return false;
	}

	/**
	 * Whether we are running on Windows
	 *
	 * @return whether we are running on Windows
	 */
	public boolean isWindows() {
		return false;
	}

	/**
	 * Whether we are running on Windows Vista or later
	 *
	 * @return whether we are running on Windows Vista or later
	 */
	public boolean isWindowsVistaOrLater() {
		return false;
	}

	/**
	 * @return the scriptingLanguage
	 */
	public String getScriptingLanguage() {
		// in some files we stored language="null" accidentally
		if ("null".equals(scriptingLanguage)) {
			scriptingLanguage = null;
		}
		return scriptingLanguage;
	}

	/**
	 * @param scriptingLanguage the scriptingLanguage to set
	 */
	public void setScriptingLanguage(String scriptingLanguage) {
		this.scriptingLanguage = scriptingLanguage;
	}

	/**
	 * Runs JavaScript
	 *
	 * @param app
	 *            application
	 * @param script
	 *            JS method name
	 * @param arg
	 *            arguments
	 * @throws Exception
	 *             when script contains errors
	 */
	public abstract void evalJavaScript(App app, String script, String arg)
			throws Exception;

	/**
	 * @param v version parts
	 * @return whether given version is newer than this code
	 */
	public boolean fileVersionBefore(int[] v) {
		if (this.versionArray == null) {
			return true;
		}

		int length = versionArray.length;
		if (v.length < length) {
			length = v.length;
		}

		for (int i = 0; i < length; i++) {
			if (versionArray[i] < v[i]) {
				return true;
			} else if (versionArray[i] > v[i]) {
				return false;
			}
		}

		return versionArray.length < v.length;
	}

	/**
	 * Sets version of currently loaded file
	 *
	 * @param version
	 *            version string
	 */
	public void setFileVersion(String version) {

		// AbstractApplication.debug("file version: " + version);

		if (version == null) {
			this.versionArray = null;
			return;
		}

		this.versionArray = getSubValues(version);
	}

	/**
	 * @return euclidian view; if not present yet, new one is created
	 */
	public abstract EuclidianView createEuclidianView();

	/**
	 * Returns current mode (tool number)
	 *
	 * @return current mode
	 */
	final public int getMode() {
		EuclidianView view = (EuclidianView) getActiveEuclidianView();
		if (view == null) {
			view = getEuclidianView1();
		}
		return view.getMode();
	}

	public void setMode(int mode) {
		setMode(mode, ModeSetter.TOOLBAR);
	}

	/**
	 * Returns labeling style for newly created geos
	 *
	 * @return labeling style; AUTOMATIC is resolved either to
	 * USE_DEFAULTS/POINTS_ONLY (for 3D) or OFF depending on visibility
	 * of AV
	 */
	public int getCurrentLabelingStyle() {
		if (getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC) {

			if ((getGuiManager() != null)
					&& getGuiManager().hasAlgebraViewShowing()) {
				if (getAlgebraView().isVisible()) {
					if (isView3D(getGuiManager().getLayout().getDockManager()
							.getFocusedViewId())) {
						// only points (and sliders and angles) are labeled
						// for 3D
						return ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY;
					}
					// default behaviour for other views
					return ConstructionDefaults.LABEL_VISIBLE_USE_DEFAULTS;
				}
				// no AV: no label
				return ConstructionDefaults.LABEL_VISIBLE_ALWAYS_OFF;
			}
			return ConstructionDefaults.LABEL_VISIBLE_ALWAYS_OFF;

		}
		return getLabelingStyle();
	}

	/**
	 * This is needed for handling paths to images inside .ggb archive TODO
	 * probably we should replace this methodby something else as images are
	 * different in web
	 *
	 * @param fullPath
	 *            path to image
	 * @return legth of MD5 hash output
	 */
	public int getMD5folderLength(String fullPath) {
		return 32;
	}

	/**
	 * @param filename
	 *            filename
	 * @return image wrapped in GBufferedImage
	 */
	public abstract MyImage getExternalImageAdapter(String filename, int width,
													int height);

	/**
	 * @return whether label dragging is enableded
	 */
	final public boolean isLabelDragsEnabled() {
		return labelDragsEnabled;
	}

	/**
	 * Enables or disables label dragging in this application. This is useful
	 * for applets.
	 *
	 * @param flag
	 *            true to allow label dragging
	 */
	public void setLabelDragsEnabled(boolean flag) {
		labelDragsEnabled = flag;
	}

	/**
	 * Enables or disables undo/redo in this application. This is useful for
	 * applets.
	 *
	 * @param flag
	 *            true to allow Undo / Redo
	 */
	public void setUndoRedoEnabled(boolean flag) {
		undoRedoEnabled = flag;
		if (!undoRedoEnabled && kernel != null) {
			kernel.setUndoActive(false);
		}
	}

	/**
	 * @return whether undo / redo are possible
	 */
	public boolean isUndoRedoEnabled() {
		return undoRedoEnabled;
	}

	/**
	 * @param b
	 */
	public void setScrollToShow(boolean b) {
		// TODO Auto-generated method stub

	}

	/**
	 * Sets state of application to "saved", so that no warning appears on
	 * close.
	 *
	 * @author Zbynek Konecny
	 * @version 2010-05-26
	 */
	public void setSaved() {
		isSaved = true;
		for (SavedStateListener sl : savedListeners) {
			sl.stateChanged(true);
		}
	}

	public void registerSavedStateListener(SavedStateListener l) {
		savedListeners.add(l);
	}

	/**
	 * Sets application state to "unsaved" so that user is reminded on close.
	 */
	public void setUnsaved() {
		isSaved = false;
		isAutoSaved = false;
		for (SavedStateListener sl : savedListeners) {
			sl.stateChanged(false);
		}
	}

	public final boolean isSaved() {
		return isSaved || kernel.getConstruction() == null
				|| !kernel.getConstruction().isStarted();
	}

	private boolean isAutoSaved = false;
	private AdjustViews adjustViews = null;

	public final boolean isAutoSaved() {
		return isAutoSaved;
	}

	public final void setAutoSaved(){
		isAutoSaved = true;
	}

	public final void setUnAutoSaved(){
		isAutoSaved = false;
	}

	/**
	 * Makes given view active
	 *
	 * @param evID view id
	 */
	public abstract void setActiveView(int evID);

	public void refreshViews() {
		getEuclidianView1().updateBackground();
		if (hasEuclidianView2(1)) {
			getEuclidianView2(1).updateBackground();
		}
		kernel.notifyRepaint();
	}

	/**
	 * Switches the application to macro editing mode
	 *
	 * @param editMacro Tool to be edited
	 * @author Zbynek Konecny
	 * @version 2010-05-26
	 */
	public void openMacro(Macro editMacro) {
		for (int i = 0; i < editMacro.getKernel().getMacroNumber(); i++) {
			if (editMacro.getKernel().getMacro(i) == editMacro) {
				break;
			}
			kernel.addMacro(editMacro.getKernel().getMacro(i));
		}
		String allXml = getXML();
		String header = allXml.substring(0, allXml.indexOf("<construction"));
		String footer = allXml.substring(allXml.indexOf("</construction>"),
				allXml.length());
		StringBuilder sb = new StringBuilder();
		editMacro.getXML(sb);
		String macroXml = sb.toString();
		String newXml = header
				+ macroXml.substring(macroXml.indexOf("<construction"),
				macroXml.indexOf("</construction>")) + footer;
		this.macro = editMacro;
		setXML(newXml, true);
	}

	public void openMacro(String macroName) {
		Macro editMacro = getKernel().getMacro(macroName);
		Log.debug("[STORAGE] nr: " + getKernel().getMacroNumber()
				+ " macro for open is " + editMacro.getToolName());
		openMacro(editMacro);

		// // for (int i = 0; i < editMacro.getKernel().getMacroNumber(); i++) {
		// // if (editMacro.getKernel().getMacro(i) == editMacro) {
		// // break;
		// // }
		// // kernel.addMacro(editMacro.getKernel().getMacro(i));
		// // }
		// try {
		// getXMLio().processXMLString(macroXml, true, false, false);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// this.macro = getKernel().getMacro(0);
		//
		// }
		// String allXml = getXML();
		// String header = allXml.substring(0, allXml.indexOf("<construction"));
		// String footer = allXml.substring(allXml.indexOf("</construction>"),
		// allXml.length());
		//
		// String newXml = header
		// + macroXml.substring(macroXml.indexOf("<construction"),
		// macroXml.indexOf("</construction>")) + footer;
		// setXML(newXml, true);
	}

	/**
	 * Returns macro if in macro editing mode.
	 *
	 * @return macro being edited (in unchanged state)
	 */
	public Macro getMacro() {
		return macro;
	}

	/**
	 * @return XML for all macros; if there are none, XML header+footer are
	 *         returned
	 */
	public String getMacroXML() {
		ArrayList<Macro> macros = kernel.getAllMacros();
		return getXMLio().getFullMacroXML(macros);
	}

	/**
	 * @return XML for or macros and/or Exercise or empty string if there are
	 *         none
	 */
	public String getMacroXMLorEmpty() {
		if (!kernel.hasMacros() && kernel.getExercise().isEmpty())
			return "";
		ArrayList<Macro> macros = kernel.getAllMacros();
		return getXMLio().getFullMacroXML(macros);
	}

	public boolean hasEuclidianView2(int idx) {
		// TODO Auto-generated method stub
		return false;
	}

	public final void showError(MyError e) {
		String command = e.getcommandName();
		String message = e.getLocalizedMessage();
		if (command == null) {
			showErrorDialog(message);
			return;
		}
		getErrorHandler().showCommandError(command, message);
	}

	public final void showError(Exception e, CommandInputField f) {
		// can't work out anything better, just show "Invalid Input"
		e.printStackTrace();
		showError(getLocalization().getError("InvalidInput"));
	}

	/**
	 * FKH
	 *
	 * @version 20040826
	 * @return full xml for GUI and construction
	 */
	public String getXML() {
		return getXMLio().getFullXML();
	}

	public abstract void showError(String string, String str);

	/**
	 * @param viewID view id
	 * @return view with given ID
	 */
	public View getView(int viewID) {

		// check for PlotPanel ID family first
		if ((getGuiManager() != null)
				&& (getGuiManager().getPlotPanelView(viewID) != null)) {
			return getGuiManager().getPlotPanelView(viewID);
		}
		switch (viewID) {
			case VIEW_EUCLIDIAN:
				return getEuclidianView1();
			case VIEW_EUCLIDIAN3D:
				return getEuclidianView3D();
			case VIEW_ALGEBRA:
				return getAlgebraView();
			case VIEW_SPREADSHEET:
				if (!isUsingFullGui())
					return null;
				else if (getGuiManager() == null)
					initGuiManager();
				if (getGuiManager() == null)
					return null;
				return getGuiManager().getSpreadsheetView();
			case VIEW_CAS:
				if (!isUsingFullGui())
					return null;
				else if (getGuiManager() == null)
					initGuiManager();
				if (getGuiManager() == null)
					return null;
				return getGuiManager().getCasView();
			case VIEW_EUCLIDIAN2:
				return hasEuclidianView2(1) ? getEuclidianView2(1) : null;
			case VIEW_CONSTRUCTION_PROTOCOL:
				if (!isUsingFullGui())
					return null;
				else if (getGuiManager() == null)
					initGuiManager();
				if (getGuiManager() == null)
					return null;
				return getGuiManager().getConstructionProtocolData();
			case VIEW_PROBABILITY_CALCULATOR:
				if (!isUsingFullGui())
					return null;
				else if (getGuiManager() == null)
					initGuiManager();
				if (getGuiManager() == null)
					return null;
				return getGuiManager().getProbabilityCalculator();
			case VIEW_DATA_ANALYSIS:
				if (!isUsingFullGui())
					return null;
				else if (getGuiManager() == null)
					initGuiManager();
				if (getGuiManager() == null)
					return null;
				return getGuiManager().getDataAnalysisView();
		}

		return null;
	}

	/**
	 * @param asPreference true if we need this for prefs XML
	 * @return XML for user interface (EVs, spreadsheet, kernel settings)
	 */
	public String getCompleteUserInterfaceXML(boolean asPreference) {
		StringBuilder sb = new StringBuilder();

		// save gui tag settings
		sb.append(getGuiXML(asPreference));

		// save euclidianView settings
		getEuclidianView1().getXML(sb, asPreference);

		// save euclidian view 2 settings
		// TODO: the EV preferences should be serialized using
		// app.getSettings(), not the view
		if (hasEuclidianView2EitherShowingOrNot(1)) {
			EuclidianView ev2 = getEuclidianView2(1);
			if (ev2 != null) {
				ev2.getXML(sb, asPreference);
			}
		}

		if (getGuiManager() != null) {
			// save spreadsheetView settings
			if (true) {// getGuiManager().hasSpreadsheetView()) {
				getGuiManager().getSpreadsheetViewXML(sb, asPreference);
			}

			// save ProbabilityCalculator settings
			if (getGuiManager().hasProbabilityCalculator()) {
				getGuiManager().getProbabilityCalculatorXML(sb);
			}

			// save AlgebraView settings
			if (getGuiManager().hasAlgebraView()) {
				getGuiManager().getAlgebraViewXML(sb, asPreference);
			}

			// save Data Collection View settings
			if (getGuiManager().hasDataCollectionView()) {
				getGuiManager().getDataCollectionViewXML(sb, asPreference);
			}

		}

		if (asPreference) {
			getKeyboardXML(sb);
		}
		// coord style, decimal places settings etc
		kernel.getKernelXML(sb, asPreference);
		getScriptingXML(sb, asPreference);
		// save cas view seeting and cas session
		// if (casView != null) {
		// sb.append(((geogebra.cas.view.CASView) casView).getGUIXML());
		// sb.append(((geogebra.cas.view.CASView) casView).getSessionXML());
		// }

		return sb.toString();
	}

	private void getScriptingXML(StringBuilder sb, boolean asPreference) {
		sb.append("<scripting");
		if (getScriptingLanguage() != null) {
			sb.append(" language=\"");
			sb.append(getScriptingLanguage());
			sb.append("\"");
		}
		sb.append(" blocked=\"");
		sb.append(isBlockUpdateScripts());

		if (!asPreference) {
			sb.append("\" disabled=\"");
			sb.append(isScriptingDisabled());
		}

		sb.append("\"/>\n");
	}

	final public Settings getSettings() {
		return settings;
	}

	public final String getUniqueId() {
		return uniqueId;
	}

	public final void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public abstract void resetUniqueId();

	/**
	 * @param auxiliaryObjects
	 *            true to show Auxiliary objects
	 */
	public final void setShowAuxiliaryObjects(boolean auxiliaryObjects) {
		if (showAuxiliaryObjects == auxiliaryObjects) {
			return;
		}
		showAuxiliaryObjects = auxiliaryObjects;

		if (getGuiManager() != null) {
			getGuiManager().setShowAuxiliaryObjects(auxiliaryObjects);
			// updateMenubar();
		}
	}

	/**
	 * Returns labeling style. See the constants in ConstructionDefaults (e.g.
	 * LABEL_VISIBLE_AUTOMATIC)
	 *
	 * @return labeling style for new objects
	 */
	public int getLabelingStyle() {
		return labelingStyle;
	}

	/**
	 * Sets labeling style. See the constants in ConstructionDefaults (e.g.
	 * LABEL_VISIBLE_AUTOMATIC)
	 *
	 * @param labelingStyle
	 *            labeling style for new objects
	 */
	public void setLabelingStyle(int labelingStyle) {
		this.labelingStyle = labelingStyle;
		labelingStyleSelected = true;
		getKernel().getConstruction().getConstructionDefaults().resetLabelModeDefaultGeos();
	}

	/**
	 * @return labeling style for new objects for menu
	 */
	public int getLabelingStyleForMenu() {
		if (labelingStyleSelected) {
			return getLabelingStyle();
		}
		return -1;
	}

	/**
	 * set the labeling style not selected,
	 * i.e. at least one default geo has
	 * specific labeling style
	 */
	public void setLabelingStyleIsNotSelected() {
		labelingStyleSelected = false;
		if (getGuiManager() != null){
			getGuiManager().updateMenubar();
		}
	}

	/**
	 * @return the scriptingDisabled
	 */
	public boolean isScriptingDisabled() {
		return scriptingDisabled;
	}

	/**
	 * @param sd
	 *            the scriptingDisabled to set
	 */
	public void setScriptingDisabled(boolean sd) {
		this.scriptingDisabled = sd;
	}

	/**
	 * @param size preferred size
	 */
	public void setPreferredSize(GDimension size) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return timeout for tooltip disappearing (in seconds)
	 */
	public int getTooltipTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Sets tooltip timeout (in seconds)
	 *
	 * @param ttt
	 *            tooltip timeout
	 */
	public void setTooltipTimeout(int ttt) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param ttl tooltip language
	 */
	public void setTooltipLanguage(String ttl) {
		// TODO Auto-generated method stub

	}

	public abstract DrawEquation getDrawEquation();

	public ArrayList<Perspective> getTmpPerspectives() {
		return tmpPerspectives;
	}

	/**
	 * Save all perspectives included in a document into an array with temporary
	 * perspectives.
	 *
	 * @param perspectives
	 *            array of perspetctives in the document
	 */
	public void setTmpPerspectives(ArrayList<Perspective> perspectives) {
		tmpPerspectives = perspectives;
	}

	/**
	 * @return EV2
	 */
	public EuclidianView getEuclidianView2(int idx) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return width of the whole application (central panel) This is needed for
	 *         Corner[6]
	 */
	public abstract double getWidth();

	/**
	 * @return height of the whole application (central panel) This is needed
	 *         for Corner[6]
	 */
	public abstract double getHeight();

	/**
	 *
	 * @param serif
	 *            serif
	 * @param style
	 *            font style
	 * @param size
	 *            font size
	 * @return font with given parameters
	 */
	public GFont getFontCommon(boolean serif, int style, int size) {
		return AwtFactory.getPrototype().newFont(serif ? "Serif" : "SansSerif",
				style, size);
	}

	/**
	 * In Desktop gives current font, in Web creates a new one
	 */
	@Deprecated
	public abstract GFont getPlainFontCommon();

	public boolean isExporting() {
		return exportType != ExportType.NONE;
	}

	public void setExporting(ExportType et, double scale) {
		exportType = et;
		exportScale = scale;
	}

	public ExportType getExportType() {
		return exportType;
	}

	public void setShowToolBarNoUpdate(boolean toolbar) {
		showToolBar = toolbar;
	}

	public void setShowToolBarHelpNoUpdate(boolean toolbarHelp) {
		showToolBarHelp = toolbarHelp;
	}

	public boolean showToolBar() {
		return showToolBar;
	}

	public boolean showMenuBar() {
		return showMenuBar;
	}

	public void setShowMenuBar(boolean flag) {
		showMenuBar = flag;
	}

	public void setShowToolBar(boolean toolbar) {
		showToolBar = toolbar;
	}

	public void setShowToolBar(boolean toolbar, boolean help) {
		showToolBar = toolbar;
		showToolBarHelp = help;
		if (showToolBar && getGuiManager() != null) {
			getGuiManager().setShowToolBarHelp(showToolBarHelp);
		}
	}

	public int getToolbarPosition() {
		return toolbarPosition;
	}

	public void setToolbarPosition(int position, boolean update) {
		// needs to be overridden
	}

	/**
	 * init the kernel (used for 3D)
	 */
	final public void initKernel() {
		kernel = companion.newKernel();
		// ensure that the selection manager is created
		getSelectionManager();
	}

	/**
	 * init the EuclidianView
	 */
	final public void initEuclidianViews() {

		euclidianController = newEuclidianController(kernel);
		euclidianView = newEuclidianView(showAxes, showGrid);
	}

	abstract protected EuclidianView newEuclidianView(boolean[] showAxes1,
													  boolean showGrid1);

	public abstract EuclidianController newEuclidianController(Kernel kernel1);

	/**
	 * Returns undo manager
	 *
	 * @param cons
	 *            construction
	 * @return undo manager
	 */
	public abstract UndoManager getUndoManager(Construction cons);

	/**
	 * TODO refactor to remove this method Creates new animation manager
	 *
	 * @param kernel2
	 *            kernel
	 * @return animation manager
	 */
	public AnimationManager newAnimationManager(Kernel kernel2) {
		return new AnimationManager(kernel2);
	}

	/**
	 * TODO maybe we should create another factory for internal classes like
	 * this
	 *
	 * @return new graphics adapter for geo
	 */
	public abstract GeoElementGraphicsAdapter newGeoElementGraphicsAdapter();

	/**
	 * Repaints the spreadsheet view
	 */
	public void repaintSpreadsheet() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return whether on the fly point creation is active
	 */
	public final boolean isOnTheFlyPointCreationActive() {
		return isOnTheFlyPointCreationActive;
	}

	/**
	 * @param isOnTheFlyPointCreationActive
	 *            Whether points can be created on the fly
	 */
	public final void setOnTheFlyPointCreationActive(
			boolean isOnTheFlyPointCreationActive) {
		this.isOnTheFlyPointCreationActive = isOnTheFlyPointCreationActive;
	}

	/**
	 * @return spreadsheet trace manager
	 */
	final public SpreadsheetTraceManager getTraceManager() {
		if (traceManager == null)
			traceManager = new SpreadsheetTraceManager(this);
		return traceManager;
	}

	/**
	 *
	 * @return true if there is a trace manager
	 */
	final public boolean hasTraceManager() {
		return traceManager != null;
	}

	/**
	 *
	 * @return true if at least one geo is traced
	 */
	final public boolean hasGeoTraced() {
		if (traceManager == null) {
			return false;
		}

		return traceManager.hasGeoTraced();
	}

	/**
	 * Switch current cursor to default cursor
	 */
	public void setDefaultCursor() {
		// TODO Auto-generated method stub

	}

	/**
	 * Switch current cursor to wait cursor
	 */
	public abstract void setWaitCursor();

	/**
	 * Update stylebars of all views
	 */
	public abstract void updateStyleBars();

	/**
	 * Changes current mode to move mode
	 */
	public void setMoveMode() {
		setMode(EuclidianConstants.MODE_MOVE);
	}

	/**
	 * Changes current mode to mode of the toolbar's 1rst tool.
	 */
	public abstract void set1rstMode();

	/**
	 * @return spreadsheet table model
	 */
	public abstract SpreadsheetTableModel getSpreadsheetTableModel();

	/**
	 * Changes current mode (tool number)
	 *
	 * @param mode
	 *            new mode
	 */
	public void setMode(int mode, ModeSetter m) {
		if (mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
			currentSelectionListener = null;
		}

		if (getGuiManager() != null) {
			setModeFromGuiManager(mode, m);
		} else if (euclidianView != null) {
			euclidianView.setMode(mode, m);
		}
	}

	protected void setModeFromGuiManager(int mode, ModeSetter m) {
		getGuiManager().setMode(mode, m);
	}

	/**
	 * Adds geo to Euclidian view (EV1)
	 *
	 * @param geo
	 *            geo
	 */
	public void addToEuclidianView(GeoElement geo) {
		geo.addView(App.VIEW_EUCLIDIAN);
		getEuclidianView1().add(geo);
	}

	/**
	 * Removes geo from Euclidian view (EV1)
	 *
	 * @param geo
	 *            geo
	 */
	public void removeFromEuclidianView(GeoElement geo) {
		geo.removeView(App.VIEW_EUCLIDIAN);
		getEuclidianView1().remove(geo);
	}

	/**
	 * Adds geo to 3D views
	 *
	 * @param geo geo
	 */
	public void addToViews3D(GeoElement geo) {
		geo.addViews3D();
		getEuclidianView3D().add(geo);
	}

	/**
	 * Removes geo from 3D views
	 *
	 * @param geo
	 *            geo
	 */
	public void removeFromViews3D(GeoElement geo) {
		geo.removeViews3D();
		getEuclidianView3D().remove(geo);
	}

	public abstract void setXML(String string, boolean b);

	/**
	 * Returns API that can be used from external applications
	 *
	 * @return GeoGebra API
	 */
	public abstract GgbAPI getGgbApi();

	/**
	 * @return sound manager
	 */
	public abstract SoundManager getSoundManager();

	/**
	 * @return kernel for this window
	 */
	public final Kernel getKernel() {
		return kernel;
	}

	/**
	 * @param e
	 *            event
	 * @return whether right mouse button was clicked or click + ctrl appeared
	 *         on Mac
	 */
	public boolean isRightClick(AbstractEvent e) {
		return e != null && e.isRightClick();
	}

	/**
	 * @param e
	 *            event
	 * @return whether Ctrl on Win/Linux or Meta on Mac was pressed
	 */
	public boolean isControlDown(AbstractEvent e) {
		return e != null && e.isControlDown();
	}

	/**
	 * @param e
	 *            event
	 * @return whether middle button was clicked once
	 */
	public boolean isMiddleClick(AbstractEvent e) {
		return e.isMiddleClick();
	}

	/**
	 * @return whether input bar is visible
	 */
	public abstract boolean showAlgebraInput();

	/**
	 * @return global key dispatcher
	 */
	public abstract GlobalKeyDispatcher getGlobalKeyDispatcher();

	public abstract void callAppletJavaScript(String string, Object[] args);

	/**
	 * Inform current selection listener about newly (un)selected geo
	 *
	 * @param geo
	 *            (un)selected geo
	 * @param addToSelection
	 *            whether it should be added or removed from selection
	 */
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		if (currentSelectionListener != null) {
			currentSelectionListener.geoElementSelected(geo, addToSelection);
		}
	}

	/**
	 * Links properties view to this application
	 *
	 * @param propertiesView
	 *            properties view
	 */
	public void setPropertiesView(PropertiesView propertiesView) {
		this.propertiesView = propertiesView;
	}

	/**
	 * Sets a mode where clicking on an object will notify the given selection
	 * listener.
	 *
	 * @param sl
	 *            selection listener
	 */
	public void setSelectionListenerMode(GeoElementSelectionListener sl) {
		getSelectionManager().resetGeoToggled(); // prevent undo current tool
													// construction
		currentSelectionListener = sl;
		if (sl != null) {
			setMode(EuclidianConstants.MODE_SELECTION_LISTENER);
		} else {
			setMoveMode();
		}
	}

	/**
	 * Update stylebars and menubar (and possibly properties view) to match
	 * selection
	 *
	 * @param updatePropertiesView
	 *            whether to update properties view
	 */
	public void updateSelection(boolean updatePropertiesView) {

		if (!isUsingFullGui() || isIniting()) {
			return;
		}

		// put in to check possible bottleneck
		// Application.debug("Update Selection");

		if (getGuiManager() != null && showMenuBar) {
			getGuiManager().updateMenubarSelection();
		}

		// if showMenuBar is false, we can still update the style bars
		if (getActiveEuclidianView().getMode() == EuclidianConstants.MODE_MOVE
				|| getActiveEuclidianView().getMode() == EuclidianConstants.MODE_TRANSLATEVIEW) {
			updateStyleBars();
		}

		if (updatePropertiesView && propertiesView != null && showMenuBar) {
			propertiesView.updateSelection();
		}
	}

	/**
	 * @param type
	 *            what properties pannel should be showing (object, defults,
	 *            advanced, ...)
	 */
	public void setPropertiesViewPanel(OptionType type) {
		if (propertiesView != null)
			propertiesView.setOptionPanel(type);
	}

	/**
	 * @return whether this app is initializing
	 * @see #initing
	 */
	public boolean isIniting() {
		return initing;
	}

	/**
	 * @return whether shift, drag and zoom features are enabled
	 */
	public final boolean isShiftDragZoomEnabled() {
		return shiftDragZoomEnabled;
	}

	/**
	 * @param shiftDragZoomEnabled whether shift, drag and zoom features are enabled
	 */
	public final void setShiftDragZoomEnabled(boolean shiftDragZoomEnabled) {
		this.shiftDragZoomEnabled = shiftDragZoomEnabled;
	}

	/**
	 * Updates menubar
	 */
	public abstract void updateMenubar();

	/**
	 * @return general font size (used for EV and GUI)
	 */
	public int getFontSize() {
		return appFontSize;
	}

	/**
	 * Changes font size and possibly resets fonts
	 *
	 * @param points font size
	 * @param update whether fonts should be reset
	 * @see #resetFonts()
	 */
	public void setFontSize(int points, boolean update) {
		if (points == appFontSize) {
			return;
		}
		appFontSize = Util.getValidFontSize(points);
		// isSaved = false;
		if (!update) {
			return;
		}

		EuclidianView ev1 = getEuclidianView1();
		if (ev1 != null && ev1.hasStyleBar()) {
			ev1.getStyleBar().reinit();
		}

		if (hasEuclidianView2(1)) {
			EuclidianView ev2 = getEuclidianView2(1);
			if (ev2 != null && ev2.hasStyleBar()) {
				ev2.getStyleBar().reinit();
			}
		}

		if (hasEuclidianView3D() && isEuclidianView3Dinited()
				&& getEuclidianView3D().hasStyleBar()) {
			getEuclidianView3D().getStyleBar().reinit();
		}

		resetFonts();

		updateUI();
	}

	/**
	 * Recursively update all components with current look and feel
	 */
	public abstract void updateUI();

	/**
	 * @return string representation of current locale, eg no_NO_NY
	 */

	/**
	 * Update font sizes of all components to match current GUI font size
	 */
	final public void resetFonts() {
		companion.resetFonts();
	}

	/**
	 * @return font size for GUI; if not specified, general font size is
	 *         returned
	 */
	public int getGUIFontSize() {
		return guiFontSize == -1 ? getFontSize() : guiFontSize;
	}

	/**
	 * @param size GUI font size
	 */
	public void setGUIFontSize(int size) {
		guiFontSize = size;
		// updateFonts();
		// isSaved = false;

		resetFonts();


	}

	/**
	 * Returns font manager
	 * 
	 * @return font manager
	 */
	protected abstract FontManager getFontManager();

	/**
	 * Returns a font that can display testString in plain sans-serif font and
	 * current font size.
	 *
	 * @param testString test string
	 * @return font
	 */
	public GFont getFontCanDisplay(String testString) {
		return getFontCanDisplay(testString, false, GFont.PLAIN, getFontSize());
	}

	/**
	 * Returns a font that can display testString in given font style,
	 * sans-serif and current font size.
	 *
	 * @param testString
	 *            test string
	 * @param fontStyle
	 *            font style
	 * @return font
	 */
	public GFont getFontCanDisplay(String testString, int fontStyle) {
		return getFontCanDisplay(testString, false, fontStyle, getFontSize());
	}

	/**
	 * Returns a font that can display testString and given font size.
	 *
	 * @param testString test string
	 * @param serif      true=serif, false=sans-serif
	 * @param fontStyle  font style
	 * @param fontSize   font size
	 * @return font
	 */
	public GFont getFontCanDisplay(String testString, boolean serif,
								   int fontStyle, int fontSize) {
		return getFontManager().getFontCanDisplay(testString, serif, fontStyle,
				fontSize);
	}

	/**
	 * Returns gui settings in XML format
	 *
	 * @param asPreference whether this is for preferences file
	 * @return gui settings in XML format
	 */
	public String getGuiXML(boolean asPreference) {
		StringBuilder sb = new StringBuilder();
		sb.append("<gui>\n");

		getWindowLayoutXML(sb, asPreference);

		sb.append("\t<font ");
		sb.append(" size=\"");
		sb.append(getFontSize());
		sb.append("\"/>\n");

		if (asPreference) {
			sb.append("\t<menuFont ");
			sb.append(" size=\"");
			sb.append(guiFontSize);
			sb.append("\"/>\n");

			sb.append("\t<tooltipSettings ");
			if (getLocalization().getTooltipLanguageString() != null) {
				sb.append(" language=\"");
				sb.append(getLocalization().getTooltipLanguageString());
				sb.append("\"");
			}
			sb.append(" timeout=\"");
			sb.append(getTooltipTimeout());
			sb.append("\"");

			sb.append("/>\n");
		}
		if (getGuiManager() != null) {
			getGuiManager().getExtraViewsXML(sb);
		}

		sb.append("</gui>\n");

		return sb.toString();
	}



	protected abstract int getWindowWidth();

	protected abstract int getWindowHeight();

	/**
	 * Appends layout settings in XML format to given builder
	 *
	 * @param sb           string builder
	 * @param asPreference whether this is for preferences
	 */
	protected void getWindowLayoutXML(StringBuilder sb, boolean asPreference) {
		sb.append("\t<window width=\"");

		sb.append(getWindowWidth() + "");

		sb.append("\" height=\"");

		sb.append(this.getWindowHeight() + "");

		sb.append("\" />\n");

		getLayoutXML(sb, asPreference);

		// labeling style
		// default changed so we need to always save this now
		// if (labelingStyle != ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC) {
		sb.append("\t<labelingStyle ");
		sb.append(" val=\"");
		sb.append(getLabelingStyle());
		sb.append("\"/>\n");
	}

	protected abstract void getLayoutXML(StringBuilder sb, boolean asPreference);

	public abstract void reset();

	/**
	 * @return selection listener
	 */
	public GeoElementSelectionListener getCurrentSelectionListener() {
		return currentSelectionListener;
	}

	/**
	 * @param sl selection listener
	 */
	public void setCurrentSelectionListener(GeoElementSelectionListener sl) {
		currentSelectionListener = sl;
	}

	/**
	 * @param flag whether reset icon should be visible (in applets)
	 */
	public void setShowResetIcon(boolean flag) {
		if (flag != showResetIcon) {
			showResetIcon = flag;
			euclidianView.updateBackground();
		}
	}

	/**
	 * @return whether reset icon is visible
	 */
	final public boolean showResetIcon() {
		return showResetIcon && !runningInFrame;
	}

	/**
	 * @return whether undo manger can save undo info
	 */
	public boolean isUndoActive() {
		return kernel.isUndoActive();
	}

	/**
	 * (De)activate undo and redo, update toolbar
	 * 
	 * @param undoActive
	 *            whether undo should be active
	 */
	public void setUndoActive(boolean undoActive) {
		boolean flag = undoActive;
		// don't allow undo when data-param-EnableUndoRedo = false
		if (flag && !undoRedoEnabled) {
			flag = false;
		}

		if (kernel.isUndoActive() == flag) {
			return;
		}

		kernel.setUndoActive(flag);
		if (flag) {
			kernel.initUndoInfo();
		}

		if (getGuiManager() != null) {
			getGuiManager().updateActions();
		}

		setSaved();
	}

	/**
	 * @return whether we are running in HTML5 applet
	 */
	public abstract boolean isHTML5Applet();

	/**
	 * @param useTransparentCursorWhenDragging whether transparent cursor should be used while dragging
	 */
	public void setUseTransparentCursorWhenDragging(
			boolean useTransparentCursorWhenDragging) {
		this.useTransparentCursorWhenDragging = useTransparentCursorWhenDragging;
	}

	public void doAfterRedefine(GeoElementND geo) {
		if (getGuiManager() != null) {
			getGuiManager().doAfterRedefine(geo);
		}
	}

	/**
	 * Opens browser with given URL
	 *
	 * @param string
	 *            URL
	 */
	public abstract void showURLinBrowser(String string);

	/**
	 * Opens the upload to GGT dialog
	 */
	public abstract void uploadToGeoGebraTube();

	public boolean getUseFullGui() {
		return useFullGui;
	}

	public int getAppCanvasWidth() {
		return appCanvasWidth;
	}

	public int getAppCanvasHeight() {
		return appCanvasHeight;
	}

	/**
	 * @return where to show the inputBar (respective inputBox)
	 */
	public InputPosition getInputPosition() {
		return showInputTop;
	}

	/**
	 * @deprecated use setInputPositon instead
	 *
	 *             Changes input position between bottom and top
	 *
	 *             if the actual position is AlgebraView, the position will be
	 *             changed to bottom or top (according to the flag)
	 *
	 * @param flag
	 *            whether input should be on top
	 * @param update
	 *            whether layout update is needed afterwards
	 */
	@Deprecated
	public void setShowInputTop(boolean flag, boolean update) {
		if (flag && showInputTop == InputPosition.top || !flag
				&& showInputTop == InputPosition.bottom) {
			return;
		}

		showInputTop = flag ? InputPosition.top : InputPosition.bottom;

		if (update && !isIniting()) {
			updateApplicationLayout();
		}
	}

	/**
	 * Changes input position between bottom and top
	 *
	 * @param flag
	 *            whether input should be on top
	 * @param update
	 *            whether layout update is needed afterwards
	 */
	public void setInputPosition(InputPosition flag, boolean update) {
		if (flag == showInputTop) {
			return;
		}

		showInputTop = flag;

		if (update && !isIniting()) {
			updateApplicationLayout();
		}
	}

	/**
	 * @return whether innput help toggle button should be visible
	 */
	public boolean showInputHelpToggle() {
		return showInputHelpToggle;
	}

	/**
	 * Shows / hides input help toggle button
	 *
	 * @param flag
	 *            whether innput help toggle button should be visible
	 */
	public void setShowInputHelpToggle(boolean flag) {
		if (showInputHelpToggle == flag || getGuiManager() == null) {
			return;
		}

		showInputHelpToggle = flag;
		getGuiManager().updateAlgebraInput();
		updateMenubar();
	}

	/**
	 * Updates application layout
	 */
	public abstract void updateApplicationLayout();

	/**
	 * Returns name or help for given tool
	 *
	 * @param mode
	 *            mode number
	 * @param toolName
	 *            true for name, false for help
	 * @return tool name or help
	 */
	public String getToolNameOrHelp(int mode, boolean toolName) {
		// macro
		String ret;

		if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
			// MACRO
			int macroID = mode - EuclidianConstants.MACRO_MODE_ID_OFFSET;
			try {
				Macro macro1 = kernel.getMacro(macroID);
				if (toolName) {
					// TOOL NAME
					ret = macro1.getToolOrCommandName();
				} else {
					// TOOL HELP
					ret = macro1.getToolHelp();
				}
			} catch (Exception e) {
				Log.debug("Application.getModeText(): macro does not exist: ID = "
						+ macroID);
				// e.printStackTrace();
				return "";
			}
		} else {
			// STANDARD TOOL

			if (toolName) {
				// tool name
				String modeText = EuclidianConstants.getModeText(mode);
				ret = getLocalization().getMenu(modeText);
			} else {
				String modeText = EuclidianConstants.getModeTextSimple(mode);
				// tool help
				ret = getLocalization().getMenu(modeText + ".Help");
			}
		}

		return ret;
	}

	/**
	 * Returns name of given tool.
	 *
	 * @param mode
	 *            number
	 * @return name of given tool.
	 */
	public String getToolName(int mode) {
		return getToolNameOrHelp(mode, true);
	}

	/**
	 * Returns the tool help text for the given tool.
	 *
	 * @param mode
	 *            number
	 * @return the tool help text for the given tool.
	 */
	public String getToolHelp(int mode) {
		return getToolNameOrHelp(mode, false);
	}

	/**
	 * @return parser extension for functions
	 */
	public ParserFunctions getParserFunctions() {
		if (pf == null) {
			pf = new ParserFunctions();
		}
		pf.setInverseTrig(
				kernel.getLoadingMode() && kernel.getInverseTrigReturnsAngle());
		return pf;
	}

	/**
	 * Clears construction
	 *
	 * @return true if successful otherwise false (eg user clicks "Cancel")
	 */
	public abstract boolean clearConstruction();

	/**
	 * create a new GeoGebra window
	 */
	public abstract void createNewWindow();

	public abstract void fileNew();

	/**
	 * Remove references to dynamic bounds, reset selection rectangle
	 */
	protected void resetEVs() {
		if (kernel.getConstruction() != null) {
			kernel.getConstruction().setIgnoringNewTypes(true);
		}
		getEuclidianView1().resetXYMinMaxObjects();
		getEuclidianView1().setSelectionRectangle(null);
		if (hasEuclidianView2EitherShowingOrNot(1)) {
			getEuclidianView2(1).resetXYMinMaxObjects();
			getEuclidianView2(1).setSelectionRectangle(null);
		}
		if (kernel.getConstruction() != null) {
			kernel.getConstruction().setIgnoringNewTypes(false);
		}
	}

	/**
	 * allows use of seeds to generate the same sequence for a ggb file
	 *
	 * @return random number in [0,1]
	 */
	public double getRandomNumber() {
		return random.nextDouble();
	}

	/**
	 * @param a
	 *            low value of distribution interval
	 * @param b
	 *            high value of distribution interval
	 * @return random number from Uniform Distribution[a,b]
	 */
	public double randomUniform(double a, double b) {
		return a + getRandomNumber() * (b - a);
	}

	/**
	 * allows use of seeds to generate the same sequence for a ggb file
	 *
	 * @param low
	 *            least possible value of result
	 * @param high
	 *            highest possible value of result
	 *
	 * @return random integer between a and b inclusive (or NaN for
	 *         getRandomIntegerBetween(5.5, 5.5))
	 *
	 */
	public int getRandomIntegerBetween(double low, double high) {
		// make sure 4.000000001 is not rounded up to 5
		double a = Kernel.checkInteger(low);
		double b = Kernel.checkInteger(high);

		// Math.floor/ceil to make sure
		// RandomBetween[3.2, 4.7] is between 3.2 and 4.7
		int min = (int) Math.ceil(Math.min(a, b));
		int max = (int) Math.floor(Math.max(a, b));

		// eg RandomBetween[5.499999, 5.500001]
		// eg RandomBetween[5.5, 5.5]
		if (min > max) {
			int tmp = max;
			max = min;
			min = tmp;
		}

		return random.nextInt(max - min + 1) + min;

	}

	/**
	 * allows use of seeds to generate the same sequence for a ggb file
	 *
	 * @param seed
	 *            new seed
	 */
	public void setRandomSeed(int seed) {
		random = new Random(seed);
	}

	public abstract boolean loadXML(String xml) throws Exception;

	/**
	 * copy bitmap of EV to clipboard
	 */
	public abstract void copyGraphicsViewToClipboard();

	/**
	 * copy base64 of current .ggb file to clipboard
	 */
	public abstract void copyBase64ToClipboard();

	/**
	 * copy full HTML5 export for current .ggb file to clipboard
	 */
	public abstract void copyFullHTML5ExportToClipboard();

	public String getFullHTML5ExportString() {

		GuiManagerInterface gui = getGuiManager();

		StringBuilder sb = new StringBuilder();

		sb.append("<!DOCTYPE html>\n");
		sb.append("<html>\n");
		sb.append("<head>\n");
		if (has(Feature.TUBE_BETA)) {
			sb.append(
					"<script src=\"https://beta.geogebra.org/scripts/deployggb.js\"></script>\n\n");

		} else {
			sb.append(
					"<script src=\"https://www.geogebra.org/scripts/deployggb.js\"></script>\n\n");

		}
		sb.append("</head>\n");
		sb.append("<body>\n");

		sb.append("<div id=\"ggbApplet\"></div>\n\n");

		sb.append("<script>\n");

		sb.append("var parameters = {\n");
		sb.append("\"id\": \"ggbApplet\",\n");
		sb.append("\"width\":" + (int) getWidth() + ",\n");
		sb.append("\"height\":" + (int) getHeight() + ",\n");
		sb.append("\"showMenuBar\":" + showMenuBar + ",\n");
		sb.append("\"showAlgebraInput\":" + showAlgebraInput + ",\n");

		sb.append("\"showToolBar\":" + showToolBar + ",\n");
		if (showToolBar) {
			if (gui != null) {
				sb.append("\"customToolBar\":\"");
				sb.append(gui.getToolbarDefinition());
				sb.append("\",\n");
			}
			sb.append("\"showToolBarHelp\":" + showToolBarHelp + ",\n");

		}
		sb.append("\"showResetIcon\":false,\n");
		sb.append("\"enableLabelDrags\":false,\n");
		sb.append("\"enableShiftDragZoom\":true,\n");
		sb.append("\"enableRightClick\":false,\n");
		sb.append("\"errorDialogsActive\":false,\n");
		sb.append("\"useBrowserForJS\":false,\n");
		sb.append("\"preventFocus\":false,\n");
		sb.append("\"scale\":1,\n");
		sb.append("\"disableAutoScale\":false,\n");
		sb.append("\"clickToLoad\":false,\n");

		sb.append("\"language\":\"" + getLocalization().getLanguage() + "\",\n");

		sb.append(
				"// use this instead of ggbBase64 to load a material from geogebra.org\n");
		sb.append("// \"material_id\":12345,\n");

		sb.append("// use this instead of ggbBase64 to load a .ggb file\n");
		sb.append("// \"filename\":\"myfile.ggb\",\n");

		sb.append("\"ggbBase64\":\"");
		// don't include preview bitmap
		sb.append(getGgbApi().getBase64(false));
		sb.append("\"};\n");

		// eg var views =
		// {"is3D":1,"AV":0,"SV":0,"CV":0,"EV2":0,"CP":0,"PC":0,"DA":0,"FI":0,"PV":0,"macro":0};

		sb.append("// is3D=is 3D applet using 3D view, AV=Algebra View, SV=Spreadsheet View, CV=CAS View, EV2=Graphics View 2, CP=Construction Protocol, PC=Probability Calculator, DA=Data Analysis, FI=Function Inspector, PV=Python, macro=Macro View\n");
		sb.append("var views = {");
		sb.append("'is3D': "
				+ (kernel.getConstruction().has3DObjects() ? "1" : "0"));
		if (gui != null) {
			sb.append(",'AV': "
					+ (gui.hasAlgebraView() && gui.getAlgebraView().isShowing()
							? "1" : "0"));
			sb.append(",'SV': " + (gui.hasSpreadsheetView()
					&& gui.getSpreadsheetView().isShowing() ? "1" : "0"));
			sb.append(",'CV': " + (gui.hasCasView() ? "1" : "0"));
			sb.append(",'EV2': " + (hasEuclidianView2(1) ? "1" : "0"));
			sb.append(",'CP': "
					+ (gui.isUsingConstructionProtocol() ? "1" : "0"));
			sb.append(",'PC': " + (gui.hasProbabilityCalculator() ? "1" : "0"));
			sb.append(",'DA': " + (gui.hasDataAnalysisView() ? "1" : "0"));
			sb.append(",'FI': "
					+ (getDialogManager().hasFunctionInspector() ? "1" : "0"));
		}
		// TODO
		sb.append(",'macro': " + (false ? "1" : "0"));
		sb.append("};\n");

		sb.append("var applet = new GGBApplet(parameters, '5.0', views);\n");

		// String codeBase = kernel.kernelHas3DObjects() ? "web3d" : "web";
		// sb.append("applet.setHTML5Codebase('http://web.geogebra.org/5.0/"
		// + codeBase + "/');\n");
		sb.append("window.onload = function() {applet.inject('ggbApplet')};\n");

		sb.append("</script>\n");
		sb.append("</body>\n");
		sb.append("</html>\n");

		return sb.toString();
	}

	/**
	 * Resets active EV to standard
	 */
	public final void setStandardView() {
		getActiveEuclidianView().setStandardView(true);
	}

	public abstract void exitAll();

	// protected abstract Object getMainComponent();

	public String getVersionString() {

		if (version != null) {
			return version.getVersionString();
		}

		// fallback in case version not set properly
		return GeoGebraConstants.VERSION_STRING + "?";
	}

	public abstract NormalizerMinimal getNormalizer();

	public final void zoom(double px, double py, double zoomFactor) {
		getActiveEuclidianView().zoom(px, py, zoomFactor, 15, true);
	}

	/**
	 * Sets the ratio between the scales of y-axis and x-axis, i.e. ratio =
	 * yscale / xscale;
	 *
	 * @param axesratio axes scale ratio
	 */
	public final void zoomAxesRatio(double axesratio) {
		getActiveEuclidianView().zoomAxesRatio(axesratio, true);
	}

	/**
	 * Zooms and pans active EV to show all objects
	 *
	 * @param keepRatio true to keep ratio of axes
	 */
	public final void setViewShowAllObjects(boolean keepRatio) {
		getActiveEuclidianView().setViewShowAllObjects(true, keepRatio);
	}

	/**
	 * @return whether right click features are enabled
	 */
	final public boolean isRightClickEnabled() {
		return rightClickEnabled;
	}

	/**
	 * Enables or disables right clicking in this application. This is useful
	 * for applets.
	 *
	 * @param flag whether right click features should be enabled
	 */
	public void setRightClickEnabled(boolean flag) {
		rightClickEnabled = flag;
	}

	/**
	 * @return whether context menu is enabled
	 */
	public final boolean letShowPopupMenu() {
		return rightClickEnabled;
	}

	/**
	 * @return whether properties dialog is enabled
	 */
	public boolean letShowPropertiesDialog() {
		return rightClickEnabled;
	}

	/**
	 * @return preferences XML
	 */
	public String getPreferencesXML() {
		return getXMLio().getPreferencesXML();
	}

	/**
	 * @param geo1   geo
	 * @param string parameter (for input box scripts)
	 */
	public abstract void runScripts(GeoElement geo1, String string);

	public Script createScript(ScriptType type, String scriptText,
							   boolean translate) {
		if (type == ScriptType.GGBSCRIPT && translate) {
			scriptText = GgbScript.localizedScript2Script(this, scriptText);
		}
		return type.newScript(this, scriptText);
	}

	public void startGeoScriptRunner() {
		if (geoScriptRunner == null) {
			geoScriptRunner = new GeoScriptRunner(this);
			getEventDispatcher().addEventListener(geoScriptRunner);
		}
	}

	/**
	 * Compares two objects by using the Relation Tool.
	 *
	 * @param ra
	 *            first object
	 * @param rb
	 *            second object
	 *
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 */
	public void showRelation(final GeoElement ra, final GeoElement rb) {
		Relation.showRelation(this, ra, rb);
	}

	public GeoElement getGeoForCopyStyle() {
		return geoForCopyStyle;
	}

	public void setGeoForCopyStyle(GeoElement geo) {
		geoForCopyStyle = geo;
	}

	public abstract CASFactory getCASFactory();

	public abstract Factory getFactory();

	public void dispatchEvent(Event evt) {
		getEventDispatcher().dispatchEvent(evt);
	}

	public OptionsMenu getOptionsMenu(MenuFactory mf) {

		if (optionsMenu == null) {
			optionsMenu = new OptionsMenu(this, mf);
		}
		return optionsMenu;
	}

	public boolean hasOptionsMenu() {
		return optionsMenu != null;
	}

	public abstract MyXMLio getXMLio();

	public abstract MyXMLio createXMLio(Construction cons);

	public boolean hasEventDispatcher() {
		return eventDispatcher != null;
	}

	/**
	 * This should not be used, just overriden in AppW
	 */
	public void scheduleUpdateConstruction() {
		kernel.getConstruction().updateConstructionLaTeX();
		kernel.notifyRepaint();
	}

	public void setShowAlgebraInput(boolean flag, boolean update) {
		showAlgebraInput = flag;

		if (update) {
			updateApplicationLayout();
			updateMenubar();
		}
	}

	public void setNeedsSpreadsheetTableModel() {
		needsSpreadsheetTableModel = true;
	}

	public boolean needsSpreadsheetTableModel() {
		return needsSpreadsheetTableModel;
	}

	public final int getAppletWidth() {
		return appletWidth;
	}

	public void setAppletWidth(int width) {
		this.appletWidth = width;
	}

	public int getAppletHeight() {
		return appletHeight;
	}

	public void setAppletHeight(int height) {
		this.appletHeight = height;
	}

	public void startCollectingRepaints() {
		getEuclidianView1().getEuclidianController()
				.startCollectingMinorRepaints();
	}

	public void stopCollectingRepaints() {
		getEuclidianView1().getEuclidianController()
				.stopCollectingMinorRepaints();
	}

	public abstract Localization getLocalization();

	public String getMenu(String key) {
		return getLocalization().getMenu(key);
	}

	@Deprecated
	public final ArrayList<GeoElement> getSelectedGeos() {
		return null;
	}

	public SelectionManager getSelectionManager() {
		if (selection == null) {
			selection = new SelectionManager(getKernel(), this);
		}
		return selection;
	}

	/**
	 * Returns the tool name and tool help text for the given tool as an HTML
	 * text that is useful for tooltips.
	 *
	 * @param mode
	 *            : tool ID
	 */
	public String getToolTooltipHTML(int mode) {
		StringBuilder sbTooltip = new StringBuilder();
		sbTooltip.append("<html><b>");
		sbTooltip.append(StringUtil.toHTMLString(getToolName(mode)));
		sbTooltip.append("</b><br>");
		sbTooltip.append(StringUtil.toHTMLString(getToolHelp(mode)));
		sbTooltip.append("</html>");
		return sbTooltip.toString();
	}

	public void resetPen() {

		getEuclidianView1().getEuclidianController().resetPen();

		if (hasEuclidianView2(1)) {
			getEuclidianView2(1).getEuclidianController().resetPen();
		}

	}

	public boolean getShowCPNavNeedsUpdate(int id) {
		if (showConstProtNavigationNeedsUpdate == null){
			return false;
		}

		Boolean update = showConstProtNavigationNeedsUpdate.get(id);
		if (update == null) {
			return false;
		}
		return update;
	}

	private boolean getShowCPNavNeedsUpdate() {
		if (showConstProtNavigationNeedsUpdate == null) {
			return false;
		}


		for (boolean update : showConstProtNavigationNeedsUpdate.values()){
			if (update) {
				return true;
			}
		}

		return false;
	}

	public boolean showConsProtNavigation() {
		if (showView(App.VIEW_CONSTRUCTION_PROTOCOL)) {
			return true;
		}
		if (showConsProtNavigation == null) {
			return false;
		}

		for (boolean show : showConsProtNavigation.values()) {
			if (show) {
				return true;
			}
		}

		return false;
	}

	public void getConsProtNavigationIds(StringBuilder sb) {
		if (showConsProtNavigation == null) {
			if (showView(App.VIEW_CONSTRUCTION_PROTOCOL)) {
				sb.append(App.VIEW_CONSTRUCTION_PROTOCOL);
			}
			return;
		}
		boolean alreadyOne = false;
		for (int id : showConsProtNavigation.keySet()) {
			if (showConsProtNavigation.get(id)) {
				if (alreadyOne) {
					sb.append(" ");
				} else {
					alreadyOne = true;
				}
				sb.append(id);
			}
		}
	}

	public boolean showConsProtNavigation(int id) {
		if (id == App.VIEW_CONSTRUCTION_PROTOCOL) {
			return true;
		}
		if (showConsProtNavigation == null) {
			return false;
		}

		Boolean show = showConsProtNavigation.get(id);
		if (show == null) {
			return false;
		}

		return show;
	}

	/**
	 * @param show
	 *            whether navigation bar should be visible
	 * @param playButton
	 *            whether play button should be visible
	 * @param playDelay
	 *            delay between phases (in seconds)
	 * @param showProtButton
	 *            whether button to show construction protocol should be visible
	 */
	public void setShowConstructionProtocolNavigation(boolean show, int id,
			boolean playButton, double playDelay, boolean showProtButton) {

		ConstructionProtocolSettings cpSettings = getSettings()
				.getConstructionProtocol();
		cpSettings.setShowPlayButton(playButton);
		cpSettings.setPlayDelay(playDelay);
		cpSettings.setShowConsProtButton(showProtButton);

		if (getGuiManager() != null) {
			getGuiManager().applyCPsettings(cpSettings);
		}

		setShowConstructionProtocolNavigation(show, id);

		if (getGuiManager() != null) {

			if (show) {
				getGuiManager().setShowConstructionProtocolNavigation(show, id,
						playButton, playDelay, showProtButton);
			}
		}

	}

	public void setHideConstructionProtocolNavigation() {
		if (!showConsProtNavigation() && (!getShowCPNavNeedsUpdate())) {
			return;
		}

		if (showConsProtNavigation == null) {
			return;
		}

		if (getGuiManager() != null) {
			for (int id : showConsProtNavigation.keySet()) {
				showConsProtNavigation.put(id, false);
				getGuiManager()
						.setShowConstructionProtocolNavigation(false, id);
				setShowConstProtNavigationNeedsUpdate(id, false);
			}
		} else {
			for (int id : showConsProtNavigation.keySet()) {
				setShowConstProtNavigationNeedsUpdate(id, true);
			}
		}
	}

	private void setShowConstProtNavigationNeedsUpdate(int id, boolean flag) {
		if (showConstProtNavigationNeedsUpdate == null) {
			showConstProtNavigationNeedsUpdate = new HashMap<Integer, Boolean>();
		}
		Boolean update = showConstProtNavigationNeedsUpdate.get(id);
		if (update == null || update != flag) {
			showConstProtNavigationNeedsUpdate.put(id, flag);
		}
	}

	/**
	 * Displays the construction protocol navigation
	 *
	 * @param flag
	 *            true to show navigation bar
	 */
	public void setShowConstructionProtocolNavigation(boolean flag) {
		dispatchEvent(new Event(EventType.SHOW_NAVIGATION_BAR, null, flag + ""));
		if (!flag) {
			setHideConstructionProtocolNavigation();
		} else {
			if (!showConsProtNavigation()) {
				// show navigation bar in active view
				setShowConstructionProtocolNavigation(true,
						getActiveEuclidianView().getViewID());
				return;

			} else if (!getShowCPNavNeedsUpdate()) {
				return;
			}

			if (getGuiManager() != null) {
				for (int id : showConsProtNavigation.keySet()) {
					showConsProtNavigation.put(id, true);
					getGuiManager().setShowConstructionProtocolNavigation(true,
							id);
					setShowConstProtNavigationNeedsUpdate(id, false);
				}
			} else {
				for (int id : showConsProtNavigation.keySet()) {
					setShowConstProtNavigationNeedsUpdate(id, true);
				}
			}
		}

	}

	/**
	 * Displays the construction protocol navigation
	 *
	 * @param flag
	 *            true to show navigation bar
	 */
	public void setShowConstructionProtocolNavigation(boolean flag, int id) {

		if (showConsProtNavigation == null) {
			showConsProtNavigation = new HashMap<Integer, Boolean>();
		} else {
			if ((flag == showConsProtNavigation(id))
					&& (!getShowCPNavNeedsUpdate(id))) {
				return;
			}
		}
		showConsProtNavigation.put(id, flag);
		dispatchEvent(new Event(EventType.SHOW_NAVIGATION_BAR, null, "[" + flag
				+ "," + id + "]"));
		if (getGuiManager() != null) {
			getGuiManager().setShowConstructionProtocolNavigation(flag, id);
			setShowConstProtNavigationNeedsUpdate(id, false);
		} else {
			setShowConstProtNavigationNeedsUpdate(id, true);
		}
	}

	public void setNavBarButtonPause() {
		if (getGuiManager() != null) {
			getGuiManager().setNavBarButtonPause();
		}
	}

	public void setNavBarButtonPlay() {
		if (getGuiManager() != null) {
			getGuiManager().setNavBarButtonPlay();
		}
	}

	public void updateCenterPanel(boolean updateUI) {
		Log.debug("App.updateCenterPanel() implementation needed");

	}

	public void toggleShowConstructionProtocolNavigation(int id) {

		setShowConstructionProtocolNavigation(!showConsProtNavigation(id), id);

		setUnsaved();
		// updateCenterPanel(true);

		if (getGuiManager() != null) {
			getGuiManager()
					.updateCheckBoxesForShowConstructinProtocolNavigation(id);
		}

	}

	public GImageIcon wrapGetModeIcon(int mode) {
		// TODO: debug message commented out from Trunk version, probably loops
		// Log.debug("App.wrapGetModeIcon must be overriden");
		return null;
	}

	/**
	 *
	 * useful for benchmarking ie only useful for elapsed time
	 *
	 * accuracy will depend on the platform / browser uses
	 * System.nanoTime()/1000000 in Java performance.now() in JavaScript
	 *
	 * Won't return sub-millisecond accuracy
	 *
	 * Chrome: doesn't work for sub-millisecond yet
	 * https://code.google.com/p/chromium/issues/detail?id=158234
	 *
	 * @return millisecond time
	 */
	public abstract double getMillisecondTime();

	public void updateActions() {
		if (isUsingFullGui() && getGuiManager() != null) {
			getGuiManager().updateActions();
		}
	}

	public void doRepaintViews() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return SignInOperation eventFlow
	 */
	public LogInOperation getLoginOperation() {
		return loginOperation;
	}

	/**
	 * This method is to be overridden in subclasses In Web, this can run in
	 * asyncronous mode
	 *
	 * ** MUST STAY AS ABSTRACT OTHERWISE WEB PROJECT DOESN'T GET SPLIT UP **
	 *
	 * @return AlgoKimberlingWeightsInterface
	 */
	public abstract AlgoKimberlingWeightsInterface getAlgoKimberlingWeights();

	/**
	 * Needed for running part of AlgoKimberling async
	 * <p/>
	 * ** MUST STAY AS ABSTRACT OTHERWISE WEB PROJECT DOESN'T GET SPLIT UP **
	 *
	 * @param kw
	 * @return
	 */
	public abstract double kimberlingWeight(AlgoKimberlingWeightsParams kw);

	/**
	 * This method is to be overridden in subclasses In Web, this can run in
	 * asyncronous mode
	 *
	 * ** MUST STAY AS ABSTRACT OTHERWISE WEB PROJECT DOESN'T GET SPLIT UP **
	 *
	 * @return AlgoCubicSwitchInterface
	 */
	public abstract AlgoCubicSwitchInterface getAlgoCubicSwitch();

	/**
	 * Needed for running part of AlgoKimberling async
	 *
	 * ** MUST STAY AS ABSTRACT OTHERWISE WEB PROJECT DOESN'T GET SPLIT UP **
	 *
	 * @param kw
	 * @return
	 */
	public abstract String cubicSwitch(AlgoCubicSwitchParams kw);

	public abstract CommandDispatcher getCommandDispatcher(Kernel k);

	/**
	 * Should lose focus on Web applets, implement only where appropriate
	 */
	public void loseFocus() {
		Log.debug("Should lose focus on Web applets, ipmelment (override) only where appropriate");
	}

	/**
	 * Whether the app is running just to create a screenshot, some
	 * recomputations can be avoided in such case
	 *
	 * @return false by defaul, overridden in AppW
	 */
	public boolean isScreenshotGenerator() {
		return false;
	}

	/**
	 * Will be overwritten in AppWapplication to toggle the menu for SMART
	 */
	public void toggleMenu() {

	}

	public final boolean isErrorDialogsActive() {
		return isErrorDialogsActive;
	}

	public final void setErrorDialogsActive(boolean isErrorDialogsActive) {
		this.isErrorDialogsActive = isErrorDialogsActive;
	}

	/**
	 * Recompute coord systems in EV and spreadsheet Only needed in web,
	 * overwritten in AppW
	 */
	public void updateViewSizes() {

	}

	public void persistWidthAndHeight() {

	}

	protected AppCompanion newAppCompanion() {
		return new AppCompanion(this);
	}

	public AppCompanion getCompanion() {
		return companion;
	}

	public SensorLogger getSensorLogger() {
		return null;
	}

	public void registerOpenFileListener(OpenFileListener o) {
		if (openFileListener == null) {
			this.openFileListener = new ArrayList<OpenFileListener>();
		}
		this.openFileListener.add(o);
	}

	public void unregisterOpenFileListener(OpenFileListener o) {
		if (openFileListener == null) {
			return;
		}
		this.openFileListener.remove(o);
	}

	protected void onOpenFile() {
		if (this.openFileListener != null) {
			for (OpenFileListener listener : openFileListener) {
				listener.onOpenFile();
			}

		}
	}

	public boolean isShowingMultipleEVs() {
		if (getGuiManager() == null
				|| getGuiManager().getEuclidianViewCount() < 2) {
			return false;
		}
		for (int i = 1; i < getGuiManager().getEuclidianViewCount(); i++) {
			if (getGuiManager().hasEuclidianView2(i)) {
				return true;
			}
		}
		return false;
	}

	public boolean isAllowPopups() {
		return allowPopUps;
	}

	public void setAllowPopups(boolean b) {
		allowPopUps = b;
	}

	public void showPopUps() {
	}

	public void openSearch(String query) {
		// TODO Auto-generated method stub
	}

	/**
	 * Adds a macro from XML
	 *
	 * @param xml macro code (including &lt;macro> wrapper)
	 * @return True if successful
	 */
	public boolean addMacroXML(String xml) {
		boolean ok = true;
		try {
			getXMLio().processXMLString(
					"<geogebra format=\"" + GeoGebraConstants.XML_FILE_FORMAT
							+ "\">" + xml + "</geogebra>", false, true);
		} catch (MyError err) {
			err.printStackTrace();
			showError(err);
			ok = false;
		} catch (Exception e) {
			e.printStackTrace();
			ok = false;
			showError("LoadFileFailed");
		}
		return ok;
	}

	/**
	 * Tells if given View is enabled. 3D be disabled by using command line
	 * option "--show3D=disable". CAS be disabled by using command line option
	 * "--showCAS=disable".
	 *
	 * @return whether the 3D view is enabled
	 */
	public boolean supportsView(int viewID) {
		if (viewID == App.VIEW_EUCLIDIAN3D) {
			return _3DViewEnabled && this.is3D();
		}
		if (viewID == App.VIEW_CAS) {
			return CASViewEnabled;
		}
		return true;
	}

	public void ensureTimerRunning() {
		// only for Web

	}

	public abstract void showCustomizeToolbarGUI();

	public abstract boolean isSelectionRectangleAllowed();

	public abstract String getEnglishCommand(String command);

	public final boolean has(Feature f) {
		switch (f) {

		// leave as prerelease
		case TUBE_BETA:
			return prerelease;

		// leave as prerelease
		case ALL_LANGUAGES:
			return prerelease;

		case EXERCISES:
			return prerelease;

			// when moved to stable, move ImplicitSurface[] from TABLE_ENGLISH
			// in Command.Java
		case IMPLICIT_SURFACES:
			return prerelease;

		case CONTOUR_PLOT_COMMAND:
			return prerelease;

		case KOREAN_KEYBOARD:
			return prerelease;
		case LOCALSTORAGE_FILES:
			return prerelease;
		case POLYGON_TRIANGULATION:
			return prerelease;

			// when moved to stable, move Difference[] from TABLE_ENGLISH in
			// Command.Java
		case POLYGON_OPS:
			return prerelease;

		case TOOL_EDITOR:
			return prerelease;

		// GGB-776
		case ABSOLUTE_TEXTS:
			return prerelease;

		// TRAC-4845
		case LOG_AXES:
			return prerelease;

		case HIT_PARAMETRIC_SURFACE:
			return false;

		case PARAMETRIC_SURFACE_IS_REGION:
			return prerelease;

		case CONVEX_HULL_3D:
			return false;

		case DIFFERENT_AXIS_RATIO_3D:
			return prerelease;
			
		// GGB-335
		case TOOLBAR_ON_SMALL_SCREENS:
			return prerelease;

		// MOB-270
		case ACRA:
			return prerelease;

		case ANALYTICS:

			return prerelease;
		case HANDWRITING:
			return false;

		// GGB-1121
		case AV_PREVIEW:
			return prerelease;

		// GGB-92
		case AV_DEFINITION_AND_VALUE:
			if (isDesktop()) {
				return false;
			}
			if (isAndroid()) {
				return false;
			}
			return true;

		// GGB-1357
		case AV_SINGLE_TAP_EDIT:
			return prerelease;

		// MOB-527
		case MOBILE_LANDSCAPE_FULLSCREEN_INPUT:
			return false;

		// MOB-904
		case MOBILE_KEYBOARD_IN_POPUP:
			return prerelease;

		//MOB-339
		case MOBILE_ANALYTICS:
			return true;

		// MOB-668
		case MOBILE_SET_MODE_IN_TOOLBAR_FROM_APP:
			return true;


		case DATA_COLLECTION:
			if (version != null && version != Versions.WEB_FOR_DESKTOP) {
				return true;
			}

			return false;

			// in web (not tablet apps yet)
			// File -> Enter Exam Mode
		case EXAM_TABLET:
			return prerelease;

		// Java Applets are only used in IE8/9
		// ie on old computers
		// so use old plotting method in Java Applets
		case SHADERS_IN_DESKTOP:
			return !isApplet();

		case SAVE_SETTINGS_TO_FILE:
			// not enabled for linux
			return isWindows() || isMacOS() || prerelease;

			// GGB-641
		case EXPLICIT_LOCUS_VIA_BOTANA:
			return true;

		// GGB-334, TRAC-3401
		case ADJUST_WIDGETS:
			return false;// prerelease;

		// GGB-1288
		case ADJUST_VIEWS:
			return false;// prerelease;

		// GGB-798
		case AV_SCROLL:
			return prerelease;

			// GGB-944
		case EXPORT_ANIMATED_PDF:
			return prerelease;

		//MOB-601
		case MOBILE_LOCAL_SAVE:
			return false;
		case RETEX_EDITOR:
			return prerelease;

			// GGB-790
		case AV_INPUT_BUTTON_COVER:
			return prerelease;

		case MOBILE_DIALOG_WITH_NEW_KEYBOARD:
			return prerelease;

		case MOBILE_DIALOG_FORMULA_INPUT:
				return true; // 5.0.299
		//MOB-788
		case MOBILE_USE_FBO_FOR_3D_IMAGE_EXPORT:
			return false;

		//MOB-823, MOB-824
		case MOBILE_APPS_CONNECTION:
			return true; // 5.0.290

		// MOB-821
		case MOBILE_MATERIAL_RELEVANT_APP_TAG:
			return true; // 5.0.290

		// MOB-825
		case MOBILE_AUTO_SAVE:
			return true; // 5.0.290

		// related to MOB-825
		case MOBILE_NEW_EV_CENTERING:
			return true; // 5.0.290

		//MOB-827
		case MOBILE_ROUNDING_OPTION:
			return true; // 5.0.276
		// GGB-1178
		case ITERATION_LIST_DOUBLE:
			return true;


		// MOB-885
		case MOBILE_PROGRESS_DIALOG_CANCEL:
			return true; // 5.0.290

		// MOB-779
		case MOBILE_COPY_3D_VIEW_TO_IMAGE_VIEW_FOR_ANIMATIONS:
			return true; // 5.0.290

		// MOB-893
		case MOBILE_SHOW_ALL_OBJECTS_ON_LOAD:
			return true; // 5.0.282

		case DRAGGING_NON_MOVEABLE_OBJECT_SPIN_THE_VIEW:
			return true;

		// MOB-882
		case NEW_TOOLBAR_ANDROID:
			return true;

		// MOB-774, MOB-775
		case MOBILE_TOOLBAR_FROM_FILE:
			return true;

		case ONSCREEN_KEYBOARD_AT_EDIT_SV_CELLS:
			return prerelease;

		case ONSCREEN_KEYBOARD_AT_PROBCALC:
			return prerelease;

			// GGB-1349
		case FIX_KEYBOARD_POSITION:
			return prerelease;

		case AUTOSCROLLING_SPREADSHEET:
			return prerelease;

			// GGB-1252
		case KEYBOARD_BEHAVIOUR:
			return prerelease;

		case WHITEBOARD_APP:
			return prerelease;

		case HIT_3D_MOVEABLE_FIRST_IF_NOT_SELECTED:
			return true; // 5.0.290

		case MOBILE_CACHE_FEATURED:
			return false;

			// MOB-961, MOB-962
			case BIND_ANDROID_TO_EXAM_APP:
			return true;

			// MOB-873
			case MOBILE_ANDROID_PROCESS_LAST_VALID_INPUT:
				return prerelease;

			// MOB-555
			case MOBILE_3D_TOOLS_WITH_DIALOGS:
				return prerelease;

			// MOB-1005
			case MOBILE_DO_NOT_AUTOSYNC_APPS:
				return true; //5.0.299

			// related to MOB-1005
			case MOBILE_ASK_WHICH_APP_SHOULD_OPEN:
				return false;

			// related to MOB-1005
			case MOBILE_OPEN_IN_RELEVANT_APP:
				return false;

			// MOB-1014
			case MOBILE_KILL_DIALOG_TITLE:
				return true; // 5.0.294

		default:
			Log.debug("missing case in Feature: " + f);
			return false;

		}
	}

	/**
	 *
	 * @return true if we want to use shaders
	 */
	public boolean useShaders() {
		return false;
	}

	/**
	 * Method is used to determine whether we CAN && SHOULD display LaTeX by
	 * MathQuillGGB, should not be changed or used for a different reason.
	 *
	 * @param tpl
	 *            can be null or StringTemplate.latexTemplateMQ, or something
	 *            that might be StringTemplate.latexTemplateMQ, but maybe not
	 *
	 * @return boolean whether we CAN && SHOULD display LaTeX by MathQuillGGB
	 */
	public final boolean isLatexMathQuillStyle(StringTemplate tpl) {
		if (!isHTML5Applet()) {
			return false;
		}

		return tpl != null && tpl.isMathQuill();
	}

	public final int getTubeId() {
		return tubeID;
	}

	public final void setTubeId(int uniqueId) {
		this.tubeID = uniqueId;
	}

	public boolean hasFocus() {
		return true;
	}

	final public boolean hasEuclidianViewForPlane() {
		return companion.hasEuclidianViewForPlane();
	}

	final public boolean hasEuclidianViewForPlaneVisible() {
		return companion.hasEuclidianViewForPlaneVisible();
	}

	final public EuclidianView getViewForPlaneVisible() {
		return companion.getViewForPlaneVisible();
	}

	/**
	 * add to views for plane (if any)
	 *
	 * @param geo geo
	 */
	final public void addToViewsForPlane(GeoElement geo) {
		companion.addToViewsForPlane(geo);
	}

	/**
	 * remove from views for plane (if any)
	 *
	 * @param geo
	 *            geo
	 */
	final public void removeFromViewsForPlane(GeoElement geo) {
		companion.removeFromViewsForPlane(geo);
	}

	public boolean isModeValid(int mode) {
		return !"".equals(getToolName(mode));
	}

	public void updateKeyboard() {
		// TODO Auto-generated method stub

	}

	/**
	 * handle space key hitted
	 *
	 * @return true if key is consumed
	 */
	public boolean handleSpaceKey() {
		ArrayList<GeoElement> selGeos = selection.getSelectedGeos();
		if (selGeos.size() == 1) {
			GeoElement geo = selGeos.get(0);
			if (geo.isGeoBoolean()) {
				GeoBoolean geoBool = (GeoBoolean) selGeos.get(0);
				geoBool.setValue(!geoBool.getBoolean());
				geoBool.updateRepaint();
			} else if (geo.isGeoInputBox()) {
				getActiveEuclidianView().focusTextField((GeoInputBox) geo);
			} else if (geo.isGeoList()) {
				Drawable d = (Drawable) getActiveEuclidianView()
						.getDrawableFor(geo);
				((DrawList) d).toggleOptions();

			} else {

				geo.runClickScripts(null);
			}

			return true;
		}

		return false;

	}

	public void setAltText() {
		// ignored in desktop

	}

	public int getFontSizeWeb() {
		return Math.max(getFontSize(), 12);
	}


	/**
	 * @return if sliders are displayed in the AV
	 */
	public boolean showAutoCreatedSlidersInEV() {
		return true;
	}

	public ExamEnvironment getExam() {
		return exam;
	}

	public boolean isExam() {
		return exam != null;
	}

	;

	public void setExam(ExamEnvironment exam) {
		this.exam = exam;

	}

	public void setNewExam(){
		setExam(new ExamEnvironment());
	}

	public void setLanguage(String s) {
		// overridden in subtypes
	}

	public void isShowingLogInDialog() {
		// TODO Auto-generated method stub

	}

	public void forEachView(ViewCallback c) {
		if (getGuiManager().showView(App.VIEW_ALGEBRA)) {
			c.run(App.VIEW_ALGEBRA, "AlgebraWindow");
		}
		if (getGuiManager().showView(App.VIEW_CAS)) {
			c.run(App.VIEW_CAS, "CAS");
		}
		if (getGuiManager().showView(App.VIEW_SPREADSHEET)) {
			c.run(App.VIEW_SPREADSHEET, "Spreadsheet");
		}
		if (getGuiManager().showView(App.VIEW_EUCLIDIAN)) {
			c.run(App.VIEW_EUCLIDIAN, "DrawingPad");
		}
		if (getGuiManager().showView(App.VIEW_EUCLIDIAN2)) {
			c.run(App.VIEW_EUCLIDIAN2, "DrawingPad2");
		}
		if (getGuiManager().showView(App.VIEW_CONSTRUCTION_PROTOCOL)) {
			c.run(App.VIEW_CONSTRUCTION_PROTOCOL, "ConstructionProtocol");
		}
		if (getGuiManager().showView(App.VIEW_DATA_ANALYSIS)) {
			c.run(App.VIEW_DATA_ANALYSIS, "DataAnalysis");
		}

	}

	public String getInput3DType() {
		return Input3D.PREFS_NONE;
	}

	public abstract void closePopups();

	public void closePopups(int x, int y) {
		closePopups();
		getActiveEuclidianView().closeDropDowns(x, y);
	}

	public String getExportTitle() {
		String title = getKernel().getConstruction().getTitle();
		return "".equals(title) ? "geogebra-export" : title;
	}

	public double getExportScale() {
		return this.exportScale;
	}

	/**
	 * Creates a new Timer.
	 *
	 * @param delay Milliseconds to run timer after start()1.
	 * @return GTimer descendant instance.
	 */
	public abstract GTimer newTimer(GTimerListener listener, int delay);

	/**
	 * possible positions for the inputBar (respective inputBox)
	 */
	public enum InputPosition {
		/**
		 * inputBox in the AV
		 */
		algebraView,
		/**
		 * inputBar at the top
		 */
		top,
		/**
		 * inputBar at the bottom
		 */
		bottom
	}

	public enum ExportType {
		NONE, PDF_TEXTASSHAPES, PDF_EMBEDFONTS, EPS, EMF, PNG, SVG, PRINTING
	}

	/**
	 * state to know if we'll need to store undo info
	 */
	private enum StoreUndoInfoForSetCoordSystem {
		/** tells that the mouse has been pressed */
		MAY_SET_COORD_SYSTEM,
		/** tells that the coord system has changed */
		SET_COORD_SYSTEM_OCCURED,
		/** no particular state */
		NONE
	}

	public interface ViewCallback {
		public void run(int viewID, String viewName);
	}

	final public boolean loadXML(byte[] zipFile) {
		try {

			// make sure objects are displayed in the correct View
			setActiveView(App.VIEW_EUCLIDIAN);

			getXMLio().readZipFromString(zipFile);

			kernel.initUndoInfo();
			setSaved();
			setCurrentFile(null);
			// command list may have changed due to macros
			updateCommandDictionary();

			hideDockBarPopup();
			return true;
		} catch (Exception err) {
			setCurrentFile(null);
			err.printStackTrace();
			return false;
		}
	}

	abstract public void setCurrentFile(Object file);

	protected void hideDockBarPopup() {
		// only used in desktop
	}

	public void schedulePreview(Runnable scheduledPreview) {
		// this is basic implementation with no scheduled delay
		scheduledPreview.run();
	}

	public void cancelPreview() {
		// not needed in basic implementation
	}

	static final protected long SCHEDULE_PREVIEW_DELAY_IN_MILLISECONDS = 500;

	public String getURLforID(String id) {
		String url;
		if (has(Feature.TUBE_BETA)) {
			url = GeoGebraConstants.GEOGEBRA_WEBSITE_BETA;
		} else {
			url = GeoGebraConstants.GEOGEBRA_WEBSITE;
		}

		// something like
		// http://www.geogebra.org/files/material-1264825.mp3
		url = url + "material/download/format/file/id/" + id;
		return url;
	}


	public ErrorHandler getErrorHandler() {
		return getDefaultErrorHandler();
	}

	public ErrorHandler getDefaultErrorHandler() {
		return ErrorHelper.silent();
	}

	/**
	 * 
	 * @return true if running in native "desktop" Java
	 */
	public boolean isDesktop() {
		return false;
	}

	/**
	 * 
	 * @return true if running on native Android (not WebView)
	 */
	protected boolean isAndroid() {
		return false;
	}

	public void setRounding(String rounding) {
		if (rounding.length() > 0) {
			StringBuilder roundingNum = new StringBuilder("0");
			for (int i = 0; i < rounding.length(); i++) {
				if (rounding.charAt(i) <= '9' && rounding.charAt(i) >= '0') {
					roundingNum.append(rounding.charAt(i));
				}
			}
			int roundInt = Integer.parseInt(roundingNum.toString());
			if (rounding.contains("s")) {
				getKernel().setPrintFigures(roundInt);
			} else {
				getKernel().setPrintDecimals(roundInt);
			}
			if (rounding.contains("r")) {
				GeoElement defNumber = getKernel().getConstruction()
						.getConstructionDefaults()
						.getDefaultGeo(ConstructionDefaults.DEFAULT_NUMBER);
				if (defNumber != null) {
					((GeoNumeric) defNumber).setSymbolicMode(true, false);
				}
			}
		}
	}

	public void examWelcome() {
		// TODO Auto-generated method stub

	}

	public GBufferedImage getActiveEuclidianViewExportImage(double maxX, double maxY){
		return getEuclidianViewExportImage(getActiveEuclidianView(), maxX, maxY);
	}

	final protected GBufferedImage getEuclidianViewExportImage(
			EuclidianView ev, double maxX, double maxY) {

		double scale = Math.min(maxX / ev.getSelectedWidthInPixels(),
				maxY / ev.getSelectedHeightInPixels());

		return ev.getExportImage(scale);
	}


	public void batchUpdateStart() {
		// used in android
	}

	public void batchUpdateEnd() {
		// used in android
	}

	public void adjustScreen() {
		if (!kernel.getApplication().has(Feature.ADJUST_WIDGETS)) {
			return;
		}
		Log.printStacktrace("[AS] adjustScreen()");
		final AdjustScreen as = new AdjustScreen(getActiveEuclidianView());
		as.apply();
	}

	/**
	 * Adjusts Algebra and Euclidian View next to or bellow each other
	 * (Portrait) according to app size.
	 * 
	 * @return if screen became portrait or not.
	 */
	public boolean adjustViews() {
		if (!has(Feature.ADJUST_VIEWS)) {
			return false;
		}

		if (adjustViews == null) {
			adjustViews = new AdjustViews(this);
		}

		adjustViews.apply();
		adjustScreen();

		return adjustViews.isPortait();
	}


	public Versions getVersion() {
		return version;
	}

	final public static String getLabelStyleName(App app, int id) {
		switch (id) {
			case (-1):
				return app.getLocalization().getPlain("Hidden");
			case (GeoElement.LABEL_NAME):
				return app.getLocalization().getPlain("Name");
			case (GeoElement.LABEL_NAME_VALUE):
				return app.getLocalization().getPlain("NameAndValue");
			case (GeoElement.LABEL_VALUE):
				return app.getLocalization().getPlain("Value");
			case (GeoElement.LABEL_CAPTION):
				return app.getLocalization().getPlain("Caption");
			default:
				return "";
		}
	}

	/**
	 * static so that you can copy & paste between instances
	 */
	public static volatile CopyPaste copyPaste = null;

	public CopyPaste getCopyPaste() {

		// return 2D version in AppD, AppW, AppWSimple
		if (copyPaste == null) {
			copyPaste = new CopyPaste3D();
		}

		return copyPaste;
	}

	public void ensureEvSizeSet(EuclidianSettings evSet) {
		// only for applets

	}

	/**
	 * 
	 * @return 9999 (or 200 in web)
	 */
	public int getMaxSpreadsheetRowsVisible() {
		return Kernel.MAX_SPREADSHEET_ROWS_DESKTOP;
	}

	/**
	 * 
	 * @return 9999 (or 200 in web)
	 */
	public int getMaxSpreadsheetColumnsVisible() {
		return Kernel.MAX_SPREADSHEET_COLUMNS_DESKTOP;
	}

	public static boolean singularWSisAvailable() {
		return singularWS != null && singularWS.isAvailable();
	}

	public static String singularWSgetTranslatedCASCommand(String s) {
		if (singularWS == null) {
			return null;
		}
		return singularWS.getTranslatedCASCommand(s);
	}

	public static String singularWSdirectCommand(String s) throws Throwable {
		if (singularWS == null) {
			return null;
		}
		return singularWS.directCommand(s);
	}

}
