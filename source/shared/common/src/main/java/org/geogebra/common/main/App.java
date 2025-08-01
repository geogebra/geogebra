package org.geogebra.common.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.function.Predicate;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.GeoGebraConstants.Platform;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianHost;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.MaskWidgetList;
import org.geogebra.common.euclidian.draw.dropdown.DrawDropDownList;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidian.inline.InlineFormulaController;
import org.geogebra.common.euclidian.inline.InlineTableController;
import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.euclidian.smallscreen.AdjustViews;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.ExamFeatureRestriction;
import org.geogebra.common.exam.restrictions.ExamRestrictable;
import org.geogebra.common.export.pstricks.GeoGebraExport;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.Format;
import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.gui.AccessibilityManagerNoGui;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.font.FontCreator;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolcategorization.ToolCollection;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFactory;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.gui.toolcategorization.ToolsProvider;
import org.geogebra.common.gui.toolcategorization.impl.CustomToolCollectionFactory;
import org.geogebra.common.gui.toolcategorization.impl.GeometryToolCollectionFactory;
import org.geogebra.common.gui.toolcategorization.impl.Graphing3DToolCollectionFactory;
import org.geogebra.common.gui.toolcategorization.impl.GraphingToolCollectionFactory;
import org.geogebra.common.gui.toolcategorization.impl.SuiteToolCollectionFactory;
import org.geogebra.common.gui.toolcategorization.impl.ToolCollectionSetFilter;
import org.geogebra.common.gui.view.algebra.ProtectiveGeoElementValueConverter;
import org.geogebra.common.gui.view.algebra.filter.AlgebraOutputFilter;
import org.geogebra.common.gui.view.algebra.filter.DefaultAlgebraOutputFilter;
import org.geogebra.common.gui.view.algebra.filter.ProtectiveAlgebraOutputFilter;
import org.geogebra.common.gui.view.properties.PropertiesView;
import org.geogebra.common.gui.view.table.regression.RegressionSpecificationBuilder;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.io.file.ByteArrayZipFile;
import org.geogebra.common.io.file.ZipFile;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.javax.swing.GImageIcon;
import org.geogebra.common.javax.swing.RelationPane;
import org.geogebra.common.kernel.AnimationManager;
import org.geogebra.common.kernel.CommandLookupStrategy;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.GeoGebraCasInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.Relation;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.arithmetic.Surds;
import org.geogebra.common.kernel.arithmetic.simplifiers.Rationalization;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.CommandsConstants;
import org.geogebra.common.kernel.geos.DefaultGeoPriorityComparator;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFormula;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPriorityComparator;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.NotesPriorityComparator;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.parser.function.ParserFunctions;
import org.geogebra.common.kernel.parser.function.ParserFunctionsFactory;
import org.geogebra.common.kernel.statistics.AlgoTableToChart;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.common.main.settings.ConstructionProtocolSettings;
import org.geogebra.common.main.settings.DefaultSettings;
import org.geogebra.common.main.settings.LabelVisibility;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.main.settings.SettingsBuilder;
import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.geogebra.common.main.settings.updater.FontSettingsUpdater;
import org.geogebra.common.main.settings.updater.SettingsUpdater;
import org.geogebra.common.main.settings.updater.SettingsUpdaterBuilder;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.main.undo.DefaultDeletionExecutor;
import org.geogebra.common.main.undo.DeletionExecutor;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.main.undo.UndoableDeletionExecutor;
import org.geogebra.common.media.VideoManager;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventDispatcher;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GeoScriptRunner;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.plugin.script.GgbScript;
import org.geogebra.common.plugin.script.Script;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.CopyPaste;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.LowerCaseDictionary;
import org.geogebra.common.util.MD5Checksum;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.geogebra.common.util.ToStringConverter;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.profiler.FpsProfiler;

import com.himamis.retex.editor.share.editor.EditorFeatures;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Represents an application window, gives access to views and system stuff
 */
public abstract class App implements UpdateSelection, AppInterface, EuclidianHost,
		ExamRestrictable, ToolsProvider {

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
	/**
	 * id for data analysis view, ie multi/single/two variable analysis tools
	 */
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
	/** id for spreadsheet table model */
	public static final int VIEW_TABLE_MODEL = 9000;
	/** accessibility view in Web */
	public static final int VIEW_ACCESSIBILITY = 44;
	/** id for table view */
	public static final int VIEW_TABLE = 8192;
	/** id for tools view */
	public static final int VIEW_TOOLS = 16384;
	/** id for the side panel */
	public static final int VIEW_SIDE_PANEL = 32768;

	public static final int DEFAULT_THRESHOLD = 3;
	public static final int DEFAULT_THRESHOLD_FACTOR_FOR_BLOB_IN_SLIDER = 3;

	/**
	 * minimal font size
	 */
	public static final int MIN_FONT_SIZE = 10;
	/**
	 * initial number of columns for spreadsheet
	 */
	public static final int SPREADSHEET_INI_COLS = 10;
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
			"5 decimals", "10 decimals", "13 decimals", "15 decimals", "",
			"3 figures", "5 figures", "10 figures", "15 figures" };

	private static String CASVersionString = "";
	private static boolean CASViewEnabled = true;
	private static boolean _3DViewEnabled = true;
	/**
	 * whether axes should be visible when EV is created first element of this
	 * array is for x-axis, second for y-axis
	 */
	protected final boolean[] showAxes = { true, true };
	/** whether axes should be logarithmic when EV is created */
	protected final boolean[] logAxes = { false, false };

	/** This filters unwanted info, such as equations generated by tools, from the output. */
	private ToStringConverter valueConverter;
	/**
	 * Whether we are running applet in frame. Not possible with 4.2+ (we need
	 * this to hide reset icon from EV)
	 */
	public boolean runningInFrame = false;
	public Vector<GeoImage> images = new Vector<>();
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

	/**
	 * whether transparent cursor should be used while dragging
	 */
	private boolean useTransparentCursorWhenDragging = false;

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
    /** factor mouse to touch events */
	static final private int TOUCH_FACTOR = 3;
	/** on touch devices we want larger threshold for point hit testing */
	protected int capturingThresholdTouch = TOUCH_FACTOR * DEFAULT_THRESHOLD;

	/* Font settings */
	/**
	 * where to show the inputBar (respective inputBox)
	 */
	protected InputPosition showInputTop = InputPosition.algebraView;
	/**
	 * Whether input bar should be visible
	 */
	protected boolean showAlgebraInput = true;

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
	 * whether right click is enabled
	 */
	protected boolean rightClickEnabled = true;
	/**
	 * whether right click is enabled for Algebra View
	 */
	protected boolean rightClickEnabledForAV = true;
	/**
	 * User Sign in handling
	 */
	protected LogInOperation loginOperation = null;
	/** XML input / output handler */
	private MyXMLio myXMLio;
	/** kernel */
	protected Kernel kernel;
	/** whether points can be created by other tools than point tool */
	protected boolean isOnTheFlyPointCreationActive = true;
	/** Settings object */
	protected Settings settings;
	/** Selections in this app */
	protected SelectionManager selection;
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
	private Perspective tmpPerspective = null;
	/**
	 * whether toolbar should be visible
	 */
	protected boolean showToolBar = true;

	private Set<ToolCollectionFilter> toolFilters = new HashSet<>();

	/**
	 * whether shift, drag and zoom features are enabled
	 */
	protected boolean shiftDragZoomEnabled = true;
	protected boolean useFullGui = false;
	protected boolean needsSpreadsheetTableModel = false;
	protected HashMap<Integer, Boolean> showConstProtNavigationNeedsUpdate = null;
	protected HashMap<Integer, Boolean> showConsProtNavigation = null;
	protected AppCompanion companion;

	private boolean showResetIcon = false;
	private ParserFunctions pf;
	private ParserFunctions pfInputBox;
	private SpreadsheetTraceManager traceManager;

	// moved to Application from EuclidianView as the same value is used across
	// multiple EVs
	private int maxLayerUsed = 0;
	private boolean labelDragsEnabled = true;
	private UndoRedoMode undoMode = UndoRedoMode.GUI;

	// command dictionary
	private LowerCaseDictionary commandDict;
	private LowerCaseDictionary englishCommandDict;
	private LowerCaseDictionary commandDictCAS;
	// array of dictionaries corresponding to the sub command tables
	private LowerCaseDictionary[] subCommandDict;
	private final Object commandDictLock = new Object();
	/**
	 * flag for current state
	 */
	private CoordSystemStateForUndo storeUndoInfoForSetCoordSystem = CoordSystemStateForUndo.NONE;
	private boolean blockUpdateScripts = false;
	private boolean useBrowserForJavaScript = true;
	private EventDispatcher eventDispatcher;

	// gets reset on file load
	private int[] versionArray = App.getSubValues(GeoGebraConstants.VERSION_STRING);
	private final List<SavedStateListener> savedListeners = new ArrayList<>();
	private Macro editMacro;
	private String editMacroPreviousName = "";
	private boolean scriptingDisabled = false;
	private double exportScale = 1;
	private PropertiesView propertiesView;
	private Random random = new Random();
	private GeoScriptRunner geoScriptRunner;
	private GeoElement geoForCopyStyle;
	private boolean isErrorDialogsActive = true;
	private ArrayList<OpenFileListener> openFileListener;
	// whether to allow perspective and login popups
	private boolean allowPopUps = false;

	private Platform platform;

	static final protected long SCHEDULE_PREVIEW_DELAY_IN_MILLISECONDS = 100;

	private ArrayList<String> mLastCommandsSelectedFromHelp;
	// TODO: move following methods somewhere else
	private String tubeID = null;
	private AdjustViews adjustViews = null;
	final static public long CE_ID_COUNTER_START = 1;
	private long ceIDcounter = CE_ID_COUNTER_START;
	private int nextVariableID = 1;
	private SpecialPointsManager specialPointsManager;

	private boolean areCommands3DEnabled = true;
	private boolean spreadsheetRestricted;
	protected AccessibilityManagerInterface accessibilityManager;
	private SettingsUpdater settingsUpdater;
	private FontCreator fontCreator;
	private AlgebraOutputFilter algebraOutputFilter;

	protected AppConfig appConfig = new AppConfigDefault();

	private Material activeMaterial;
	private EditorFeatures editorFeatures;
	/** Syntax filter enforced by AppConfig. */
	protected SyntaxFilter primarySyntaxFilter;
	private final RegressionSpecificationBuilder regressionSpecificationBuilder
			= new RegressionSpecificationBuilder();

	public static String[] getStrDecimalSpacesAC() {
		return strDecimalSpacesAC;
	}

	/**
	 * Please call setPlatform right after this
	 */
	public App() {
		init();
	}

	/**
	 * Create app with specific platform
	 *
	 * @param platform the platform
	 */
	public App(Platform platform) {
		this();
		this.platform = platform;
	}

	protected void init() {
		companion = newAppCompanion();
		resetUniqueId();
	}

	/**
	 * @return default settings
	 */
	public abstract DefaultSettings getDefaultSettings();

	/**
	 * @return font creator
	 */
	public FontCreator getFontCreator() {
		if (fontCreator == null) {
			fontCreator = new FontCreator(getFontManager(), getSettings().getFontSettings());
		}
		return fontCreator;
	}

	/**
	 * Sets the Platform; should be called only once, right after the constructor
	 *
	 * @param platform
	 *            platform
	 */
	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	/**
	 * @return CAS version (for debug info)
	 */
	public static String getCASVersionString() {
		return CASVersionString;

	}

	/**
	 * @param string
	 *            CAS version string (for debug info)
	 */
	public static void setCASVersionString(String string) {
		CASVersionString = string;
	}

	/* selection handling */

	/**
	 * @param version
	 *            string version, eg 4.9.38.0
	 * @return version as list of ints, eg [4,9,38,0]
	 */
	static public int[] getSubValues(String version) {
		String[] values = version.split("\\.");
		int[] ret = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			ret[i] = Integer.parseInt(values[i]);
		}

		return ret;
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
	public static boolean isView3D(int id) {
		if (id == App.VIEW_EUCLIDIAN3D) {
			return true;
		}

		return id == App.VIEW_EUCLIDIAN3D_2;

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
     * @param type
     *            mouse or touch
     * @return factor
     */
    public int getFactorFor(PointerEventType type) {
        return type == PointerEventType.TOUCH ? TOUCH_FACTOR : 1;
    }

	/**
	 * @param i
	 *            capturing threshold
	 */
	public void setCapturingThreshold(int i) {
		this.capturingThreshold = i;
		this.capturingThresholdTouch = 3 * i;
	}

	/**
	 * Call this after localization is available.
	 */
	protected void initLocalization() {
		Localization localization = getLocalization();
		AppConfig config = getConfig();
		localization.setDecimalPlaces(config.getDecimalPlaces());
		localization.setSignificantFigures(config.getSignificantFigures());
		primarySyntaxFilter = config.newCommandSyntaxFilter();
		if (primarySyntaxFilter != null) {
			localization.getCommandSyntax().addSyntaxFilter(primarySyntaxFilter);
		}
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

		if (!getLocalization().isCommandChanged() && ((commandDictCAS != null)
				|| getLocalization().isCommandNull())) {
			return;
		}
		GeoGebraCasInterface cas = kernel.getGeoGebraCAS();
		if (cas == null || subCommandDict == null) {
			return;
		}
		getLocalization().setCommandChanged(false);

		commandDictCAS = new LowerCaseDictionary();
		subCommandDict[CommandsConstants.TABLE_CAS].clear();

		// get all commands from the commandDict and write them to the
		// commandDictCAS

		// Copy all commands from input bar dictionary (already filtered) to CAS dictionary
		for (String cmd : commandDict.values()) {
			commandDictCAS.addEntry(cmd);
		}

		CommandDispatcher commandDispatcher =
				getKernel().getAlgebraProcessor().getCommandDispatcher();
		// iterate through all available CAS commands, add them (translated if
		// available, otherwise untranslated)
		for (String cmd : cas.getAvailableCommandNames()) {
			try {
				if (!commandDispatcher.isAllowedByCommandFilters(Commands.valueOf(cmd))) {
					continue;
				}
			} catch (Exception e) {
				continue; // not a translatable command => skip
			}
			String local = getLocalization().getCommand(cmd);
			putInTranslateCommandTable(Commands.valueOf(cmd), local);
			commandDictCAS.addEntry(local);
			subCommandDict[CommandsConstants.TABLE_CAS].addEntry(local);
		}
	}

	/**
	 * @return command dictionary for CAS
	 */
	public final LowerCaseDictionary getCommandDictionaryCAS() {
		synchronized (commandDictLock) {
			fillCommandDict();
			fillCasCommandDict();
		}
		return commandDictCAS;
	}

	/**
	 * Force rebuilding of standard and CAS dictionaries
	 */
	public void resetCommandDict() {
		commandDict = null;
		commandDictCAS = null;
		getCommandDictionaryCAS();
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
		if (getLocalization().isCommandChanged()) {
			updateCommandDictionary();
		}

		return subCommandDict;
	}

	/**
	 * Initializes the translated command names for this application. Note: this
	 * will load the properties files first.
	 */
	final public void initTranslatedCommands() {
		synchronized (commandDictLock) {
			if (getLocalization().isCommandNull() || subCommandDict == null) {
				getLocalization().initCommand();
				fillCommandDict();
				kernel.updateLocalAxesNames();
			}
		}
	}

	/**
	 * @return command dictionary
	 */
	public final LowerCaseDictionary getCommandDictionary() {
		synchronized (commandDictLock) {
			fillCommandDict();
		}
		return commandDict;
	}

	/**
	 * @return command dictionary
	 */
	public final LowerCaseDictionary getEnglishCommandDictionary() {
		return englishCommandDict;
	}

	/**
	 * Fill command dictionary and translation table. Must be called before we
	 * start using Input Bar.
	 */
	protected void fillCommandDict() {
		getLocalization().initCommand();
		if (!getLocalization().isCommandChanged() && commandDict != null) {
			return;
		}
		// translation table for all command names in command.properties
		getLocalization().initTranslateCommand();
		// command dictionary for all public command names available in
		// GeoGebra's input field
		// removed check for null: commandDict.clear() removes keys, but they
		// are still available with commandDict.iterator()
		// so change English -> French -> English doesn't work in the input bar
		// see AutoCompleteTextfield.lookup()
		// if (commandDict == null)
		commandDict = new LowerCaseDictionary();
		englishCommandDict = new LowerCaseDictionary();
		// else commandDict.clear();

		// =====================================
		// init sub command dictionaries
		CommandDispatcher cf = getKernel().getAlgebraProcessor().getCommandDispatcher();

		createSubCommandDictIfNeeded();
		clearSubCommandDict();

		HashMap<String, String> translateCommandTable = getLocalization()
				.getTranslateCommandTable();

		for (Commands comm : Commands.values()) {
			if (!cf.isAllowedByCommandFilters(comm)) {
				continue;
			}

			if (!companion.tableVisible(comm.getTable())) {
				if (comm.getTable() == CommandsConstants.TABLE_ENGLISH) {
					putInTranslateCommandTable(comm, null);
				}

				continue;
			}
			String internal = comm.name();
			String local = getLocalization().getCommand(internal);
			englishCommandDict.addEntry(getLocalization().getEnglishCommand(internal));
			addCommandEntry(comm, local, translateCommandTable);
		}

		getParserFunctions().updateLocale(getLocalization());
		getParserFunctions(true).updateLocale(getLocalization());
		// get CAS Commands
		if (kernel.isGeoGebraCASready()) {
			fillCasCommandDict();
		}
		addMacroCommands();
		getLocalization().setCommandChanged(false);
	}

	private void createSubCommandDictIfNeeded() {
		if (subCommandDict != null) {
			return;
		}

		subCommandDict = new LowerCaseDictionary[CommandDispatcher.tableCount];
		for (int i = 0; i < subCommandDict.length; i++) {
			subCommandDict[i] = new LowerCaseDictionary();
		}
	}

	private void clearSubCommandDict() {
		for (LowerCaseDictionary lowerCaseDictionary : subCommandDict) {
			lowerCaseDictionary.clear();
		}
	}

	private void addCommandEntry(Commands comm, String translated,
			HashMap<String, String> translateCommandTable) {
		putInTranslateCommandTable(comm, translated);
		if (translated != null) {
			String local = translated.trim();
			// case is ignored in translating local command names to
			// internal names!0
			translateCommandTable.put(StringUtil.toLowerCaseUS(translated),
					comm.name());
			commandDict.addEntry(local);
			// add public commands to the sub-command dictionaries
			subCommandDict[comm.getTable()].addEntry(local);
		}

	}

	private boolean putInTranslateCommandTable(Commands comm, String local) {
		String internal = comm.name();
		// Check that we don't overwrite local with English
		HashMap<String, String> translateCommandTable = getLocalization()
				.getTranslateCommandTable();
		int added = 0;
		String lowerCaseUS = StringUtil.toLowerCaseUS(internal);
		if (!translateCommandTable
				.containsKey(lowerCaseUS)) {
			translateCommandTable.put(lowerCaseUS,
					Commands.englishToInternal(comm).name());
			added++;
		}
		if (comm.getTable() == CommandsConstants.TABLE_ENGLISH) {
			return added > 0;
		}

		if (local != null) {
			String old = translateCommandTable.put(StringUtil.toLowerCaseUS(local),
					Commands.englishToInternal(comm).name());
			if (old == null) {
				added++;
			}
		}
		return added > 0;
	}

	/**
	 * translate command name to internal name. Note: the case of localname is
	 * NOT relevant
	 *
	 * @param command
	 *            local name
	 * @return internal name
	 */
	public String getReverseCommand(String command) {
		// don't init command table on file loading
		if (kernel.getCommandLookupStrategy() != CommandLookupStrategy.USER) {
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
		synchronized (commandDictLock) {
			if (commandDict != null) {
				fillCommandDict();
			}
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

	/**
	 * Store current state of construction for undo/redo purposes, and state of
	 * construction for mode starting (so undo cancels partial tool preview)
	 */
	public void storeUndoInfoAndStateForModeStarting() {
		storeUndoInfoAndStateForModeStarting(true);
	}

	/**
	 * Store global undo point an possibly one for mode.
	 *
	 * @param storeForMode
	 *            whether to store a mode undo point too
	 */
	final public void storeUndoInfoAndStateForModeStarting(
			boolean storeForMode) {
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
	public void storeUndoInfoIfSetCoordSystemOccurred() {
		if (storeUndoInfoForSetCoordSystem == CoordSystemStateForUndo.SET_COORD_SYSTEM_OCCURRED) {
			storeUndoInfo();
		}

		storeUndoInfoForSetCoordSystem = CoordSystemStateForUndo.NONE;
	}

	/**
	 * tells the application that a view coord system has changed
	 */
	public void setCoordSystemOccurred() {
		if (storeUndoInfoForSetCoordSystem == CoordSystemStateForUndo.MAY_SET_COORD_SYSTEM) {
			storeUndoInfoForSetCoordSystem = CoordSystemStateForUndo.SET_COORD_SYSTEM_OCCURRED;
		}
	}

	/**
	 * tells the coord sys may be set
	 */
	public void maySetCoordSystem() {
		if (storeUndoInfoForSetCoordSystem == CoordSystemStateForUndo.NONE) {
			storeUndoInfoForSetCoordSystem = CoordSystemStateForUndo.MAY_SET_COORD_SYSTEM;
		}
	}

	public UndoManager getUndoManager() {
		return kernel.getConstruction().getUndoManager();
	}

	/**
	 * Notify undo manager that properties change occurred.
	 */
	public void setPropertiesOccurred() {
		getUndoManager().setPropertiesOccurred();
	}

	/**
	 * Store undo point for properties change.
	 */
	public void storeUndoInfoForProperties() {
		getUndoManager().storeUndoInfoForProperties(isUndoActive());
	}

	/**
	 * @return whether object renaming is allowed
	 */
	public boolean letRename() {
		return true;
	}

	/**
	 * @return whether deletion is allowed
	 */
	public boolean letDelete() {
		return true;
	}

	/**
	 * @return whether redefinition is allowed
	 */
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
	 * @param blockUpdateScripts
	 *            the blockUpdateScripts to set
	 */
	public void setBlockUpdateScripts(boolean blockUpdateScripts) {
		this.blockUpdateScripts = blockUpdateScripts;
	}

	/**
	 * Translates localized command name into internal TODO check whether this
	 * differs from translateCommand somehow and either document it or remove
	 * this method
	 *
	 * @param localizedCommandName
	 *            localized command name
	 * @return internal command name
	 */
	public String getInternalCommand(String localizedCommandName) {
		initTranslatedCommands();
		String s;
		String localizedCommandNameLower = StringUtil.toLowerCaseUS(localizedCommandName);
		String renamed = Commands.getRenamed(localizedCommandNameLower, getLocalization());
		if (renamed != null) {
			return renamed;
		}
		Commands[] values = Commands.values();
		for (Commands c : values) {
			s = Commands.englishToInternal(c).name();
			// make sure that when si[] is typed in script, it's changed to Si[] etc
			if (StringUtil.toLowerCaseUS(getLocalization().getCommand(s))
					.equals(localizedCommandNameLower)) {
				return s;
			}
		}
		return null;
	}

	/**
	 *
	 * Converts english command name to internal command key.
	 *
	 * @param englishName
	 *             the english command name.
	 * @return the internal key of the command
	 */
	public String englishToInternal(String englishName)  {
		initTranslatedCommands();
		String s;
		String cmdLower = StringUtil.toLowerCaseUS(englishName);
		for (Commands c : Commands.values()) {
			s = Commands.englishToInternal(c).name();

			// make sure that when si[] is typed in script, it's changed to
			// Si[] etc
			if (StringUtil.toLowerCaseUS(getLocalization().getEnglishCommand(s))
					.equals(cmdLower)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Translate key and then show error dialog
	 *
	 * @param key
	 *            error message
	 */
	public void localizeAndShowError(String key) {
		showError(getLocalization().getError(key));
	}

	/**
	 * Shows error dialog with a given text
	 *
	 * @param msg
	 *            error message
	 */
	protected abstract void showErrorDialog(String msg);

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
	final public ScriptManager getScriptManager() {
		if (scriptManager == null) {
			scriptManager = newScriptManager();
		}
		return scriptManager;
	}

	/**
	 * @return whether script manager was initialized
	 */
	public final boolean hasScriptManager() {
		return scriptManager != null;
	}

	/**
	 * Get the event dispatcher, which dispatches events objects that manage
	 * event driven scripts
	 *
	 * @return the app's event dispatcher
	 */
	public EventDispatcher getEventDispatcher() {
		if (eventDispatcher == null) {
			eventDispatcher = newEventDispatcher();
		}
		return eventDispatcher;
	}

	protected EventDispatcher newEventDispatcher() {
		return new EventDispatcher(this);
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
	 * @param ge
	 *            geo
	 */
	public void traceToSpreadsheet(GeoElement ge) {
		getTraceManager().traceToSpreadsheet(ge);
	}

	/**
	 * Reset tracing column for given geo
	 *
	 * @param ge
	 *            geo
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
	public void deleteSelectedObjects(boolean isCut) {
		deleteSelectedObjects(isCut, geo -> !geo.isProtected(EventType.REMOVE));
	}

	/**
	 * Deletes some of the selected objects
	 * @param filter which geos to delete
	 */
	public void deleteSelectedObjects(boolean isCut, Predicate<GeoElement> filter) {
		if (letDelete()) {
			// also delete just created geos if possible
			ArrayList<GeoElement> geos2 = new ArrayList<>(getActiveEuclidianView()
					.getEuclidianController().getJustCreatedGeos());
			geos2.addAll(selection.getSelectedGeos());
			DeletionExecutor recorder = isWhiteboardActive() ? new UndoableDeletionExecutor()
					: new DefaultDeletionExecutor();
			geos2.stream().filter(filter).forEach(geo -> {
				boolean isChartEmbed = geo.getParentAlgorithm() instanceof AlgoTableToChart;
				if (isCut && !isChartEmbed && geo.getParentAlgorithm() != null) {
					for (GeoElement ancestor : geo.getParentAlgorithm().input) {
						if (ancestor.isLabelSet()) {
							recorder.delete(ancestor);
						}
					}
				}
				recorder.delete(geo);
			});

			getActiveEuclidianView().getEuclidianController()
					.clearJustCreatedGeos();
			getActiveEuclidianView().getEuclidianController()
					.clearSelectionAndRectangle();
			if (recorder.storeUndoAction(kernel)) {
				setUnsaved();
			}
		}
	}

	/**
	 * @return recently created geos
	 */
	public ArrayList<GeoElement> getJustCreatedGeos() {
		return getActiveEuclidianView().getEuclidianController()
				.getJustCreatedGeos();
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
	 * @param sb
	 *            string builder
	 */
	public void getKeyboardXML(StringBuilder sb) {
		// desktop only
	}

	/**
	 * set right angle style
	 *
	 * @param style
	 *            style
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
	 * @return EV1
	 */
	public EuclidianView getEuclidianView1() {
		return euclidianView;
	}

	/**
	 * Resets the maximal used llayer to 0
	 */
	public void resetMaxLayerUsed() {
		getKernel().getConstruction().getConstructionDefaults().resetLayers();
		maxLayerUsed = 0;
	}

	/**
	 * @return whether 3D view was initialized
	 */
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
	 * @param v
	 *            version parts
	 * @return whether given version is newer than this code
	 */
	public boolean fileVersionBefore(int... v) {
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
	 * @param appName
	 *            name of the app that saved current file
	 */
	public void setFileVersion(String version, String appName) {
		if (version == null) {
			this.versionArray = null;
			return;
		}

		this.versionArray = getSubValues(version);
	}

	/**
	 * Returns current mode (tool number)
	 *
	 * @return current mode
	 */
	final public int getMode() {
		EuclidianView view = getActiveEuclidianView();
		if (view == null) {
			view = getEuclidianView1();
		}
		return view.getMode();
	}

	/**
	 * Sets the active mode
	 * @param mode mode number (EuclidianConstants.MODE_*)
	 */
	public void setMode(int mode) {
		setMode(mode, ModeSetter.TOOLBAR);
	}

	/**
	 * Returns labeling style for newly created geos
	 *
	 * @return labeling style; AUTOMATIC is resolved either to
	 *         USE_DEFAULTS/POINTS_ONLY (for 3D) or OFF depending on visibility
	 *         of AV
	 */
	public LabelVisibility getCurrentLabelingStyle() {
		LabelVisibility userValue = getSettings().getLabelSettings().getLabelVisibility();
		if (userValue == LabelVisibility.Automatic) {
			if ((getGuiManager() != null)
					&& getGuiManager().hasAlgebraViewShowing()
					&& getAlgebraView().isVisible()) {
				// default behaviour for other views
				return LabelVisibility.UseDefaults;
			}
			// no AV: no label
			return LabelVisibility.AlwaysOff;
		}
		return userValue;
	}

	/**
	 * @return whether label dragging is enabled
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
	 * Enables or disables undo/redo storing and UI in this application.
	 * This is useful for applets.
	 *
	 * @param undoRedoMode
	 *            decides if undo points should be stored and if the undo UI should be visible
	 */
	public void setUndoRedoMode(UndoRedoMode undoRedoMode) {
		this.undoMode = undoRedoMode;
		if (undoRedoMode == UndoRedoMode.DISABLED && kernel != null) {
			kernel.setUndoActive(false);
		}
	}

	/**
	 * @return undo management mode
	 */
	public UndoRedoMode getUndoRedoMode() {
		return undoMode;
	}

	/**
	 * Enable / disable autoscroll in spreadsheet.
	 *
	 * @param scrollToShow
	 *            scrolling flag for spreadsheet
	 */
	public final void setScrollToShow(boolean scrollToShow) {
		if (getGuiManager() != null) {
			getGuiManager().setScrollToShow(scrollToShow);
		}
	}

	/**
	 * Sets state of application to "saved", so that no warning appears on
	 * close.
	 */
	public void setSaved() {
		isSaved = true;
		for (SavedStateListener sl : savedListeners) {
			sl.stateChanged(true);
		}
	}

	/**
	 * Register saved state listener.
	 * @param listener listener
	 */
	public void registerSavedStateListener(SavedStateListener listener) {
		savedListeners.add(listener);
	}

	/**
	 * Sets application state to "unsaved" so that user is reminded on close.
	 */
	public void setUnsaved() {
		isSaved = false;
		for (SavedStateListener sl : savedListeners) {
			sl.stateChanged(false);
		}
	}

	/**
	 * @return whether all changes are saved; ignore EV moves if no objects are
	 *         present
	 */
	public final boolean isSaved() {
		return isSaved || kernel.getConstruction() == null
				|| !kernel.getConstruction().isStarted();
	}

	/**
	 * Update backgrounds and repaint views.
	 */
	public void refreshViews() {
		getEuclidianView1().updateBackground();
		if (hasEuclidianView2(1)) {
			getEuclidianView2(1).updateBackground();
		}
		kernel.notifyRepaint();
	}

	/**
	 * Removes the given macro from the app
	 * @param macro is the macro that needs to be removed
	 */
	public void removeMacro(Macro macro) {
		kernel.removeMacro(macro);
	}

	/**
	 * Removes the macro with the given name from the app
	 * @param macroName is the name of the macro that needs to be removed
	 */
	public void removeMacro(String macroName) {
		kernel.removeMacro(macroName);
	}

	/**
	 * Removes all the macros from the app
	 */
	public void removeAllMacros() {
		kernel.removeAllMacros();
	}

	/**
	 * Returns the previous name of the edit macro - used when the name of the macro is changed
	 * @return the previous name of the edit macro
	 */
	public String getEditMacroPreviousName() {
		return editMacroPreviousName;
	}

	/**
	 * Updates the previous name of the edit macro - used when the name of the macro is changed
	 * @param editMacroPreviousName the new previous name of the edit macro
	 */
	public void setEditMacroPreviousName(String editMacroPreviousName) {
		this.editMacroPreviousName = editMacroPreviousName;
	}

	/**
	 * Switches the application to macro editing mode
	 *
	 * @param editMacro
	 *            Tool to be edited
	 */
	public void openEditMacro(Macro editMacro) {
		String allXml = getXML();
		String header = allXml.substring(0, allXml.indexOf("<construction"));
		String footer = allXml.substring(allXml.indexOf("</construction>"));
		StringBuilder sb = new StringBuilder();
		editMacro.getXML(sb);
		String macroXml = sb.toString();
		String newXml = header
				+ macroXml.substring(macroXml.indexOf("<construction"),
						macroXml.indexOf("</construction>"))
				+ footer;
		this.editMacro = editMacro;
		setEditMacroPreviousName(editMacro.getEditName());
		setXML(newXml, true);
	}

	/**
	 * Returns macro if in macro editing mode.
	 *
	 * @return macro being edited (in unchanged state)
	 */
	public Macro getEditMacro() {
		return editMacro;
	}

	/**
	 * @return XML for all macros; if there are none, XML header+footer are returned
	 */
	public String getAllMacrosXML() {
		ArrayList<Macro> macros = kernel.getAllMacros();
		return getXMLio().getFullMacroXML(macros);
	}

	/**
	 * @return XML for all macros or empty string if there are none
	 */
	public String getAllMacrosXMLorEmpty() {
		if (!kernel.hasMacros()) {
			return "";
		}
		return getAllMacrosXML();
	}

	/**
	 * @param macro is the macro for which the XML is returned
	 * @return XML for the given macro; if there are none, XML header+footer are returned
	 */
	public String getMacroXML(Macro macro) {
		return getXMLio().getFullMacroXML(Arrays.asList(macro));
	}

	/**
	 * @param macro is the macro for which the XML is returned
	 * @return XML for the given macro or empty string if it is null
	 */
	public String getMacroXMLorEmpty(Macro macro) {
		if (macro == null) {
			return "";
		}
		return getMacroXML(macro);
	}

	/**
	 * @param idx
	 *            secondary EV index, 1 for EV2
	 * @return whether secondary EV with given index is showing
	 */
	public boolean hasEuclidianView2(int idx) {
		return (getGuiManager() != null)
				&& getGuiManager().hasEuclidianView2(idx);
	}

	/**
	 * Show localized message for an error.
	 *
	 * @param e
	 *            error
	 */
	public final void showError(MyError e) {
		ErrorHelper.handleError(e, null, getLocalization(), getDefaultErrorHandler());
	}

	/**
	 * Show localized message for an error.
	 *
	 * @param key   main error
	 * @param error extra information
	 */
	public void showError(Errors key, String error) {
		showError(key.getError(getLocalization()), error);
	}

	/**
	 * Show localized message for an error.
	 *
	 * @param key main error
	 */
	public void showError(Errors key) {
		showError(key.getError(getLocalization()));
	}

	/**
	 * Unexpected exception: can't work out anything better, just show "Invalid
	 * Input"
	 *
	 * @param e exception
	 */
	public final void showGenericError(Exception e) {
		Log.debug(e);
		showError(getLocalization().getInvalidInputError());
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

	/**
	 * @param viewID
	 *            view id
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
			if (!isUsingFullGui()) {
				return null;
			} else if (getGuiManager() == null) {
				initGuiManager();
			}
			if (getGuiManager() == null) {
				return null;
			}
			return getGuiManager().getSpreadsheetView();
		case VIEW_CAS:
			if (!isUsingFullGui()) {
				return null;
			} else if (getGuiManager() == null) {
				initGuiManager();
			}
			if (getGuiManager() == null) {
				return null;
			}
			return getGuiManager().getCasView();
		case VIEW_EUCLIDIAN2:
			return hasEuclidianView2(1) ? getEuclidianView2(1) : null;
		case VIEW_CONSTRUCTION_PROTOCOL:
			if (!isUsingFullGui()) {
				return null;
			} else if (getGuiManager() == null) {
				initGuiManager();
			}
			if (getGuiManager() == null) {
				return null;
			}
			return getGuiManager().getConstructionProtocolData();
		case VIEW_PROBABILITY_CALCULATOR:
			if (!isUsingFullGui()) {
				return null;
			} else if (getGuiManager() == null) {
				initGuiManager();
			}
			if (getGuiManager() == null) {
				return null;
			}
			return getGuiManager().getProbabilityCalculator();
		case VIEW_DATA_ANALYSIS:
			if (!isUsingFullGui()) {
				return null;
			} else if (getGuiManager() == null) {
				initGuiManager();
			}
			if (getGuiManager() == null) {
				return null;
			}
			return getGuiManager().getDataAnalysisView();
		}

		return null;
	}

	/**
	 * @param asPreference
	 *            true if we need this for prefs XML
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

		getViewsXML(sb, asPreference);

		if (asPreference) {
			getKeyboardXML(sb);
		}
		// coord style, decimal places settings etc
		kernel.getKernelXML(sb, asPreference);
		getSettings().getTable().getXML(sb);
		getScriptingXML(sb, asPreference);

		return sb.toString();
	}

	protected void getViewsXML(StringBuilder sb, boolean asPreference) {
		// save spreadsheet settings
		getSettings().getSpreadsheet().getXML(sb, asPreference);
		if (getGuiManager() != null) {
			getGuiManager().getViewsXML(sb, asPreference);
		}
	}

	private void getScriptingXML(StringBuilder sb, boolean asPreference) {
		sb.append("<scripting blocked=\"");
		sb.append(isBlockUpdateScripts());

		if (!asPreference) {
			sb.append("\" disabled=\"");
			sb.append(isScriptingDisabled());
		}

		sb.append("\"/>\n");
	}

	/**
	 * @return the root settings object
	 */
	final public Settings getSettings() {
		if (settings == null) {
			initSettings();
		}
		return settings;
	}

	/**
	 * @return The {@link AlgebraStyle} currently set.
	 */
	final public @Nonnull AlgebraStyle getAlgebraStyle() {
		return getSettings().getAlgebra().getStyle();
	}

	public final String getUniqueId() {
		return uniqueId;
	}

	public final void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	/**
	 * @param auxiliaryObjects
	 *            true to show Auxiliary objects
	 */
	public final void setShowAuxiliaryObjects(boolean auxiliaryObjects) {
		if (showAuxiliaryObjects == auxiliaryObjects) {
			return;
		}
		showAuxiliaryObjects = auxiliaryObjects;
		updateGuiForShowAuxiliaryObjects();
	}

    /**
     * update GUI for show auxiliary objects flag
     */
	public final void updateGuiForShowAuxiliaryObjects() {
        if (getGuiManager() != null) {
            getGuiManager().setShowAuxiliaryObjects(showAuxiliaryObjects);
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
		return getSettings().getLabelSettings().getLabelVisibility().getValue();
	}

	/**
	 * Sets labeling style. See the constants in ConstructionDefaults (e.g.
	 * LABEL_VISIBLE_AUTOMATIC)
	 *
	 * @param labelVisibility
	 *            labeling style for new objects
	 */
	public void setLabelingStyle(int labelVisibility) {
		getSettings().getLabelSettings().setLabelVisibility(LabelVisibility.get(labelVisibility));
	}

	/**
	 * @return whether GGBScript scripting is disabled via file XML (UI option no longer exists)
	 */
	public boolean isScriptingDisabled() {
		return scriptingDisabled;
	}

	/**
	 * @param disabled
	 *            see {@link #isScriptingDisabled()}
	 */
	public void setScriptingDisabled(boolean disabled) {
		this.scriptingDisabled = disabled;
	}

	/**
	 * @param size
	 *            preferred size
	 */
	public void setPreferredSize(GDimension size) {
		// implemented per platform
	}

	public GDimension getPreferredSize() {
		return null;
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
	 * @param ttl
	 *            tooltip language, may be either BCP47 tag or Java locale string
	 */
	public void setTooltipLanguage(String ttl) {
		// only in desktop ATM
	}

	public Perspective getTmpPerspective() {
		return tmpPerspective;
	}

	/**
	 * Save all perspectives included in a document into an array with temporary
	 * perspectives.
	 *
	 * @param perspective
	 *            perspective for the document
	 */
	public void setTmpPerspective(Perspective perspective) {
		tmpPerspective = perspective;
	}

	/**
	 * Update the UI perspective.
	 * @param p perspective
	 */
	public void setPerspective(Perspective p) {
		try {
			persistWidthAndHeight();
			getGuiManager().getLayout().applyPerspective(p);
			updateViewSizes();
			getGuiManager().updateMenubar();
			getGuiManager().updateToolbar();
			updateKeyboard();
		} catch (Exception e) {
			Log.debug(e);
		}
	}

	/**
	 * @param idx
	 *            view index; 1 for EV2
	 * @return EV2
	 */
	public EuclidianView getEuclidianView2(int idx) {
		return null;
	}

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

	public boolean isExporting() {
		return exportType != ExportType.NONE;
	}

	/**
	 * Start graphics view export.
	 *
	 * @param et
	 *            export type
	 * @param scale
	 *            export scale
	 */
	public void setExporting(ExportType et, double scale) {
		exportType = et;
		exportScale = scale;
	}

	public ExportType getExportType() {
		return exportType;
	}

	public void setShowToolBarHelpNoUpdate(boolean toolbarHelp) {
		showToolBarHelp = toolbarHelp;
	}

	/**
	 * @return whether the toolbar is shown.
	 */
	public boolean showToolBar() {
		return showToolBar;
	}

	/**
	 * @return whether the menu (bar in classic 5, button in other apps) is shown
	 */
	public boolean showMenuBar() {
		return showMenuBar;
	}

	public void setShowMenuBar(boolean flag) {
		showMenuBar = flag;
	}

	public final void setShowToolBar(boolean toolbar) {
		showToolBar = toolbar;
	}

	/**
	 * Update toolbar settings (no UI update)
	 *
	 * @param toolbar
	 *            show toolbar?
	 * @param help
	 *            show toolbar help?
	 */
	public void setShowToolBar(boolean toolbar, boolean help) {
		showToolBar = toolbar;
		showToolBarHelp = help;
	}

	public int getToolbarPosition() {
		return toolbarPosition;
	}

	/**
	 * Update the toolbar position flag and optionally rebuilds the UI
	 *
	 * @param position
	 *            new position
	 * @param update
	 *            whether to rebuild the UI
	 */
	public void setToolbarPosition(int position, boolean update) {
		// needs to be overridden
	}

	/**
	 * init the kernel (used for 3D)
	 */
	final public void initKernel() {
		kernel = companion.newKernel();
		kernel.setAngleUnit(appConfig.getDefaultAngleUnit());
		kernel.setSymbolicMode(appConfig.getSymbolicMode());
		kernel.setEquationBehaviour(appConfig.getEquationBehaviour());
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
		if (traceManager == null) {
			traceManager = new SpreadsheetTraceManager(this);
		}
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
	 * Changes current mode to move mode if whiteboard is not active
	 */
	public void setMoveMode() {
		setMoveMode(ModeSetter.TOOLBAR);
	}

	/**
	 * Changes current mode to move mode
	 */
	public void setMoveMode(ModeSetter m) {
		if (!isWhiteboardActive()) {
			setMode(EuclidianConstants.MODE_MOVE, m);
		} else {
			setMode(EuclidianConstants.MODE_SELECT_MOW, m);
		}
	}

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
		if (mode != EuclidianConstants.MODE_MOVE) {
			euclidianController.widgetsToBackground();
		}
		if (getGuiManager() != null) {
			setModeFromGuiManager(mode, m);
			this.updateDynamicStyleBars();

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
	 * @param geo
	 *            geo
	 */
	public void addToViews3D(GeoElement geo) {
		geo.addViews3D();
		if (isEuclidianView3Dinited()) {
			getEuclidianView3D().add(geo);
		}
	}

	/**
	 * Removes geo from 3D view
	 *
	 * @param geo
	 *            geo
	 */
	public void removeFromViews3D(GeoElement geo) {
		geo.removeViews3D();
		if (isEuclidianView3Dinited()) {
			getEuclidianView3D().remove(geo);
		}
	}

	/**
	 * @return video manager
	 */
	public VideoManager getVideoManager() {
		return null;
	}

	/**
	 * @return kernel for this window
	 */
	public final @Nonnull Kernel getKernel() {
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
	@Override
	public void updateSelection(boolean updatePropertiesView) {
		if (isIniting()) {
			return;
		}

		if (isUsingFullGui()) {
			if (getGuiManager() != null && showMenuBar) {
				getGuiManager().updateMenubarSelection();
			}

			// if showMenuBar is false, we can still update the style bars
			EuclidianView ev = getActiveEuclidianView();
			if (ev != null
					&& (EuclidianConstants.isMoveOrSelectionMode(ev.getMode())
					|| ev.getMode() == EuclidianConstants.MODE_TRANSLATEVIEW)) {
				updateStyleBars();
			}

			if (isUnbundledOrWhiteboard()) {
				// TODO update only dynamic stylebar
				updateStyleBars();
			}

			if (updatePropertiesView && propertiesView != null && showMenuBar) {
				propertiesView.updateSelection();
			}
		}
		ScreenReader.updateSelection(this);

	}

	/**
	 * @param type
	 *            what properties panel should be showing (object, defaults,
	 *            advanced, ...)
	 */
	public void setPropertiesViewPanel(OptionType type) {
		if (propertiesView != null) {
			propertiesView.setOptionPanel(type);
		}
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
	 * @param shiftDragZoomEnabled
	 *            whether shift, drag and zoom features are enabled
	 */
	public final void setShiftDragZoomEnabled(boolean shiftDragZoomEnabled) {
		this.shiftDragZoomEnabled = shiftDragZoomEnabled;
	}

	/**
	 * @return general font size (used for EV and GUI)
	 */
	public int getFontSize() {
		return settings.getFontSettings().getAppFontSize();
	}

	/**
	 * Changes font size and possibly resets fonts
	 *
	 * @param points
	 *            font size
	 * @param update
	 *            whether fonts should be reset
	 */
	public void setFontSize(int points, boolean update) {
		FontSettingsUpdater fontSettingsUpdater = getFontSettingsUpdater();
		if (update) {
			fontSettingsUpdater.setAppFontSizeAndUpdateViews(points);
		} else {
			fontSettingsUpdater.setAppFontSize(points);
		}
	}

	/**
	 * @return font size for GUI; if not specified, general font size is
	 *         returned
	 */
	public int getGUIFontSize() {
		return getSettings().getFontSettings().getGuiFontSizeSafe();
	}

	/**
	 * @param size
	 *            GUI font size
	 */
	public void setGUIFontSize(int size) {
		getFontSettingsUpdater().setGUIFontSizeAndUpdate(size);
	}

	/**
	 * Returns font manager
	 *
	 * @return font manager
	 */
	public abstract FontManager getFontManager();

	/**
	 * Returns a font that can display testString in plain sans-serif font and
	 * current font size.
	 *
	 * @param testString
	 *            test string
	 * @return font
	 */
	public GFont getFontCanDisplay(String testString) {
		return getFontCreator().newSansSerifFont(testString);
	}

	/**
	 * Returns a font that can display testString and given font size.
	 *
	 * @param testString
	 *            test string
	 * @param serif
	 *            true=serif, false=sans-serif
	 * @param fontStyle
	 *            font style
	 * @param fontSize
	 *            font size
	 * @return font
	 */
	public GFont getFontCanDisplay(String testString, boolean serif,
			int fontStyle, int fontSize) {
		FontCreator fontCreator = getFontCreator();
		if (serif) {
			return fontCreator.newSerifFont(testString, fontStyle, fontSize);
		} else {
			return fontCreator.newSansSerifFont(testString, fontStyle, fontSize);
		}
	}

	/**
	 * Returns gui settings in XML format
	 *
	 * @param asPreference
	 *            whether this is for preferences file
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
			int guiFontSize = settings.getFontSettings().getGuiFontSize();
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
	 * @param sb
	 *            string builder
	 * @param asPreference
	 *            whether this is for preferences
	 */
	protected void getWindowLayoutXML(StringBuilder sb, boolean asPreference) {
		sb.append("\t<window width=\"");

		sb.append(getWindowWidth());

		sb.append("\" height=\"");

		sb.append(getWindowHeight());

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

	protected abstract void getLayoutXML(StringBuilder sb,
			boolean asPreference);

	/**
	 * @return selection listener
	 */
	public GeoElementSelectionListener getCurrentSelectionListener() {
		return currentSelectionListener;
	}

	/**
	 * Reset selection listener
	 */
	public void resetCurrentSelectionListener() {
		currentSelectionListener = null;
	}

	/**
	 * @param flag
	 *            whether reset icon should be visible (in applets)
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
	public boolean showResetIcon() {
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
		if (flag && undoMode == UndoRedoMode.DISABLED) {
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
	 * @param useTransparentCursorWhenDragging
	 *            whether transparent cursor should be used while dragging
	 */
	public void setUseTransparentCursorWhenDragging(
			boolean useTransparentCursorWhenDragging) {
		this.useTransparentCursorWhenDragging = useTransparentCursorWhenDragging;
	}

	/**
	 * Update UI after redefinition
	 *
	 * @param geo
	 *            redefined geo
	 */
	public void doAfterRedefine(GeoElementND geo) {
		if (getGuiManager() != null) {
			getGuiManager().doAfterRedefine(geo);
		}
	}

	/**
	 * Enable the full user interface.
	 */
	public void enableUseFullGui() {
		useFullGui = true;
	}

	@Override
	public boolean isUsingFullGui() {
		return useFullGui;
	}

	/**
	 * @return where to show the inputBar (respective inputBox)
	 */
	public InputPosition getInputPosition() {
		return isUnbundled() ? InputPosition.algebraView : showInputTop;
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
	 * @return whether input help toggle button should be visible
	 */
	public boolean showInputHelpToggle() {
		return showInputHelpToggle;
	}

	/**
	 * Shows / hides input help toggle button
	 *
	 * @param flag
	 *            whether input help toggle button should be visible
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
	 * Returns name of given tool.
	 *
	 * @param mode
	 *            number
	 * @return name of given tool.
	 */
	public String getToolName(int mode) {
		if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
			Macro macro = kernel.getMacro(mode - EuclidianConstants.MACRO_MODE_ID_OFFSET);
			return macro == null ? "" : macro.getToolName();
		} else {
			String modeText = EuclidianConstants.getModeText(mode);
			return getLocalization().getMenu(modeText);
		}
	}

	/**
	 * Returns name of tool with help
	 * @param mode - number
	 * @return name + help of tool
	 */
	public String getToolAriaLabel(int mode) {
		return getToolName(mode) + ". " + getToolHelp(mode);
	}

	/**
	 * Returns the tool help text for the given tool.
	 *
	 * @param mode
	 *            number
	 * @return the tool help text for the given tool.
	 */
	public String getToolHelp(int mode) {
		if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
			Macro macro = kernel.getMacro(mode - EuclidianConstants.MACRO_MODE_ID_OFFSET);
			return macro == null ? "" : macro.getToolHelp();
		} else {

			return getLocalization().getMenu(EuclidianConstants.getHelpTransKey(mode));
		}
	}

	/**
	 * Returns the internal name for the given tool.
	 * @param mode number
	 * @return the tool help text for the given tool.
	 */
	public String getInternalToolName(int mode) {
		if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
			Macro macro = kernel.getMacro(mode - EuclidianConstants.MACRO_MODE_ID_OFFSET);
			return macro == null ? "" : macro.getToolName();
		} else {
			return EuclidianConstants.getModeText(mode);
		}

	}

	/**
	 * @return parser extension for functions
	 */
	public ParserFunctions getParserFunctions(boolean inputBox) {
		if (pf == null) {
			ParserFunctionsFactory factory = getConfig().createParserFunctionsFactory();
			pf = factory.createParserFunctions();
			pfInputBox = factory.createInputBoxParserFunctions();
		}
		pf.setInverseTrig(
				kernel.getLoadingMode() && kernel.getInverseTrigReturnsAngle());
		pfInputBox.setInverseTrig(
				kernel.getLoadingMode() && kernel.getInverseTrigReturnsAngle());

		if (inputBox) {
			return pfInputBox;
		} else {
			return pf;
		}
	}

	public ParserFunctions getParserFunctions() {
		return getParserFunctions(false);
	}

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
		if (isEuclidianView3Dinited()) {
			getEuclidianView3D().resetXYMinMaxObjects();
			getEuclidianView3D().setSettingsToStandardView();
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
		double a = DoubleUtil.checkInteger(low);
		double b = DoubleUtil.checkInteger(high);

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

	/**
	 * copy base64 of current .ggb file to clipboard
	 */
	public void copyBase64ToClipboard() {
		getCopyPaste().copyTextToSystemClipboard(getGgbApi().getBase64());
	}

	/**
	 * copy full HTML5 export for current .ggb file to clipboard
	 */
	public void copyFullHTML5ExportToClipboard() {
		getCopyPaste().copyTextToSystemClipboard(HTML5Export.getFullString(this));
	}

	/**
	 *
	 * @param url
	 *            url
	 * @return url converted to a data URI if possible. If not, returns the URL
	 *         unaltered
	 */
	protected String convertImageToDataURIIfPossible(String url) {
		return url;
	}

	/**
	 * Resets active EV to standard
	 */
	public final void setStandardView() {
		getActiveEuclidianView().setStandardView(true);
	}

	/**
	 * Full version eg X.Y.Zd-prerelease
	 *
	 * @return version string
	 */
	public String getVersionString() {
		if (platform != null) {
			return platform.getVersionString(PreviewFeature.enableFeaturePreviews,
					getConfig().getAppCode());
		}

		// fallback in case version not set properly
		return GeoGebraConstants.VERSION_STRING + "?";
	}

	/**
	 * Zoom the active view.
	 * @param px zoom center's x-coordinate
	 * @param py zoom center's y-coordinate
	 * @param zoomFactor zoom factor
	 */
	public final void zoom(double px, double py, double zoomFactor) {
		getActiveEuclidianView().zoom(px, py, zoomFactor, 15, true);
	}

	/**
	 * Sets the ratio between the scales of y-axis and x-axis, i.e. ratio =
	 * yscale / xscale;
	 *
	 * @param axesratio
	 *            axes scale ratio
	 */
	public final void zoomAxesRatio(double axesratio) {
		getActiveEuclidianView().zoomAxesRatio(axesratio, 1, true);
	}

	/**
	 * Zooms and pans active EV to show all objects checking ratio from config.
	 */
	public final void setViewShowAllObjects() {
		setViewShowAllObjects(appConfig.shouldKeepRatioEuclidian());
	}

	/**
	 * Zooms and pans active EV to show all objects
	 *
	 * @param keepRatio
	 *            true to keep ratio of axes
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
	 * @param flag
	 *            whether right click features should be enabled
	 */
	public void setRightClickEnabled(boolean flag) {
		rightClickEnabled = flag;
	}

	/**
	 * @return whether right click features are enabled for Algebra View
	 */
	final public boolean isRightClickEnabledForAV() {
		return rightClickEnabledForAV;
	}

	/**
	 * Enables or disables right clicking for Algebra View. Used e.g. in Exam
	 * Simple Calc app
	 *
	 * @param flag
	 *            whether right click features should be enabled
	 */
	public void setRightClickEnabledForAV(boolean flag) {
		rightClickEnabledForAV = flag;
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
	 * @param type
	 *            JS or GGBScript
	 * @param scriptText0
	 *            possibly localized text
	 * @param translate
	 *            whether to convert from localized
	 * @return script object
	 */
	public Script createScript(ScriptType type, String scriptText0,
			boolean translate) {
		String scriptText = scriptText0;
		if (type == ScriptType.GGBSCRIPT && translate) {
			scriptText = GgbScript.localizedScript2Script(this, scriptText);
		}
		return type.newScript(this, scriptText);
	}

	/**
	 * Attach GGBScript runner to event dispatcher
	 */
	public void startGeoScriptRunner() {
		if (geoScriptRunner == null) {
			geoScriptRunner = new GeoScriptRunner(this);
			getEventDispatcher().addEventListener(geoScriptRunner);
		}
	}

	/**
	 * Compares 2, 3 or 4 objects by using the Relation Tool.
	 *
	 * @param ra
	 *            first object
	 * @param rb
	 *            second object
	 * @param rc
	 *            third object (optional, can be null)
	 * @param rd
	 *            forth object (optional, can be null)
	 *
	 * @author Zoltan Kovacs
	 */
	public void showRelation(final GeoElement ra, final GeoElement rb,
			final GeoElement rc, final GeoElement rd) {
		new Relation(this, ra, rb, rc, rd).showDialog();
	}

	public GeoElement getGeoForCopyStyle() {
		return geoForCopyStyle;
	}

	public void setGeoForCopyStyle(GeoElement geo) {
		geoForCopyStyle = geo;
	}

	/**
	 * Dispatchan event
	 * @param evt event to dispatch
	 */
	public void dispatchEvent(Event evt) {
		getEventDispatcher().dispatchEvent(evt);
	}

	/**
	 * @return XML input / output utility
	 */
	public MyXMLio getXMLio() {
		if (myXMLio == null) {
			myXMLio = createXMLio(kernel.getConstruction());
		}
		return myXMLio;
	}

	/**
	 * @return whether event dispatcher was initialized.
	 */
	public boolean hasEventDispatcher() {
		return eventDispatcher != null;
	}

	/**
	 * This should not be used, just overridden in AppW
	 */
	public void scheduleUpdateConstruction() {
		kernel.getConstruction().updateConstructionLaTeX();
		kernel.notifyRepaint();
	}

	/**
	 * Show or hide the input bar.
	 *
	 * @param flag
	 *            whether to show
	 * @param update
	 *            whether to update UI
	 */
	public void setShowAlgebraInput(boolean flag, boolean update) {

		showAlgebraInput = flag;

		if (update) {
			updateApplicationLayout();
			updateMenubar();
		}
	}

	/**
	 * Notify that the spreadsheet model is needed.
	 */
	public void setNeedsSpreadsheetTableModel() {
		needsSpreadsheetTableModel = true;
	}

	/**
	 * @return whether spreadsheet table model is needed.
	 */
	public boolean needsSpreadsheetTableModel() {
		return needsSpreadsheetTableModel;
	}

	/**
	 * @return selection manager
	 */
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
	 *            tool ID
	 * @return tool name
	 */
	public String getToolTooltipHTML(int mode) {
		StringBuilder sbTooltip = new StringBuilder();
		if (isUnbundled()) {
			sbTooltip.append("<html><p>");
			sbTooltip.append(StringUtil.toHTMLString(getToolName(mode)));
			sbTooltip.append("</p>");
			if (getWidth() >= 400) {
				sbTooltip.append(StringUtil.toHTMLString(getToolHelp(mode)));
			}
		} else {
			sbTooltip.append("<html><b>");
			sbTooltip.append(StringUtil.toHTMLString(getToolName(mode)));
			sbTooltip.append("</b><br>");
			sbTooltip.append(StringUtil.toHTMLString(getToolHelp(mode)));
		}

		sbTooltip.append("</html>");
		return sbTooltip.toString();
	}

	/**
	 * Make sure we start a new penstroke.
	 */
	public void resetPen() {
		getEuclidianView1().getEuclidianController().resetPen();

		if (hasEuclidianView2(1)) {
			getEuclidianView2(1).getEuclidianController().resetPen();
		}
	}

	/**
	 * @param id
	 *            view ID
	 * @return whether navigation bar in given view needs update
	 */
	public boolean getShowCPNavNeedsUpdate(int id) {
		if (showConstProtNavigationNeedsUpdate == null) {
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

		for (boolean update : showConstProtNavigationNeedsUpdate.values()) {
			if (update) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return whether navigation bar is shown in at least one view
	 */
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

	/**
	 * Add space separated list of view IDs that are showing navigation bar
	 *
	 * @param sb
	 *            XML builder
	 */
	public void getConsProtNavigationIds(StringBuilder sb) {
		if (showConsProtNavigation == null) {
			if (showView(App.VIEW_CONSTRUCTION_PROTOCOL)) {
				sb.append(App.VIEW_CONSTRUCTION_PROTOCOL);
			}
			return;
		}
		boolean alreadyOne = false;
		for (Entry<Integer, Boolean> entry : showConsProtNavigation
				.entrySet()) {
			int id = entry.getKey();
			if (entry.getValue()) {
				if (alreadyOne) {
					sb.append(" ");
				} else {
					alreadyOne = true;
				}
				sb.append(id);
			}
		}
	}

	/**
	 * @param id
	 *            view ID
	 * @return whether navigation bar is shown in view with that ID
	 */
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

	/**
	 * Hide all navigation bars or set the flags if UI not loaded.
	 */
	public void setHideConstructionProtocolNavigation() {
		if (!showConsProtNavigation() && (!getShowCPNavNeedsUpdate())) {
			return;
		}

		if (showConsProtNavigation == null) {
			return;
		}

		if (getGuiManager() != null) {
			showConsProtNavigation.replaceAll((id, ignore) -> {
				getGuiManager().setShowConstructionProtocolNavigation(false,
						id);
				setShowConstProtNavigationNeedsUpdate(id, false);
				return false;
			});
		} else {
			for (int id : showConsProtNavigation.keySet()) {
				setShowConstProtNavigationNeedsUpdate(id, true);
			}
		}
	}

	private void setShowConstProtNavigationNeedsUpdate(int id, boolean flag) {
		if (showConstProtNavigationNeedsUpdate == null) {
			showConstProtNavigationNeedsUpdate = new HashMap<>();
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
		dispatchEvent(
				new Event(EventType.SHOW_NAVIGATION_BAR, null, flag + ""));
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
				showConsProtNavigation.replaceAll((id, any) -> {
					getGuiManager().setShowConstructionProtocolNavigation(true,
							id);
					setShowConstProtNavigationNeedsUpdate(id, false);
					return true;
				});
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
			showConsProtNavigation = new HashMap<>();
		} else {
			if ((flag == showConsProtNavigation(id))
					&& (!getShowCPNavNeedsUpdate(id))) {
				return;
			}
		}
		showConsProtNavigation.put(id, flag);
		dispatchEvent(new Event(EventType.SHOW_NAVIGATION_BAR, null,
				"[" + flag + "," + id + "]"));
		if (getGuiManager() != null) {
			getGuiManager().setShowConstructionProtocolNavigation(flag, id);
			setShowConstProtNavigationNeedsUpdate(id, false);
		} else {
			setShowConstProtNavigationNeedsUpdate(id, true);
		}
	}

	/**
	 * Switch navigation bar buttons to pause.
	 */
	public void setNavBarButtonPause() {
		if (getGuiManager() != null) {
			getGuiManager().setNavBarButtonPause();
		}
	}

	/**
	 * Switch navigation bar buttons to play.
	 */
	public void setNavBarButtonPlay() {
		if (getGuiManager() != null) {
			getGuiManager().setNavBarButtonPlay();
		}
	}

	/**
	 * Toggle navigation bar on given view.
	 *
	 * @param id
	 *            view ID
	 */
	public void toggleShowConstructionProtocolNavigation(int id) {

		setShowConstructionProtocolNavigation(!showConsProtNavigation(id), id);

		setUnsaved();
		// updateCenterPanel(true);

		if (getGuiManager() != null) {
			getGuiManager()
					.updateCheckBoxesForShowConstructionProtocolNavigation(id);
		}
	}

	/**
	 * @param mode
	 *            app mode ID
	 * @return icon
	 */
	public GImageIcon wrapGetModeIcon(int mode) {
		return null;
	}

	/**
	 * Update undo/redo and menu for selection.
	 */
	public void updateActions() {
		if (isUsingFullGui() && getGuiManager() != null) {
			getGuiManager().updateActions();
		}
	}

	/**
	 * @return SignInOperation eventFlow
	 */
	public LogInOperation getLoginOperation() {
		return loginOperation;
	}

	/**
	 * Creates a new command dispatcher (platform dependent, to support code splitting).
	 * @param cmdKernel kernel
	 * @return command dispatcher
	 */
	public abstract CommandDispatcher newCommandDispatcher(Kernel cmdKernel);

	/**
	 * Whether the app is running just to create a screenshot, some
	 * recomputations can be avoided in such case
	 *
	 * @return false by default, overridden in AppW
	 */
	public boolean isScreenshotGenerator() {
		return false;
	}

	public final boolean isErrorDialogsActive() {
		return isErrorDialogsActive;
	}

	public final void setErrorDialogsActive(boolean isErrorDialogsActive) {
		this.isErrorDialogsActive = isErrorDialogsActive;
	}

	/**
	 * Recompute coord systems in EV and spreadsheet Only needed in web,
	 *
	 */
	public void updateViewSizes() {
		// overwritten in AppW
	}

	/**
	 * Persist current width and height.
	 */
	public void persistWidthAndHeight() {
		// overwritten in AppW
	}

	protected AppCompanion newAppCompanion() {
		return new AppCompanion(this);
	}

	public AppCompanion getCompanion() {
		return companion;
	}

	/**
	 * Add file open listener.
	 *
	 * @param openListener
	 *            listener
	 */
	public void registerOpenFileListener(OpenFileListener openListener) {
		if (openFileListener == null) {
			this.openFileListener = new ArrayList<>();
		}
		this.openFileListener.add(openListener);
	}

	protected void onOpenFile() {
		if (this.openFileListener != null) {
			ArrayList<OpenFileListener> toRemove = new ArrayList<>();
			for (OpenFileListener listener : openFileListener) {
				if (listener.onOpenFile()) {
					toRemove.add(listener);
				}
			}
			openFileListener.removeAll(toRemove);
		}
	}

	/**
	 * @return whether more than one of EV1 and EV2 is showing
	 */
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

	/**
	 * @param query
	 *            search query
	 */
	public void openSearch(String query) {
		// TODO Auto-generated method stub
	}

	/**
	 * Adds a macro from XML
	 *
	 * @param xml
	 *            macro code (including &lt;macro&gt; wrapper)
	 * @return True if successful
	 */
	public boolean addMacroXML(String xml) {
		boolean ok = true;
		try {
			getXMLio().processXMLString(
					"<geogebra format=\"" + GeoGebraConstants.XML_FILE_FORMAT
							+ "\">" + xml + "</geogebra>",
					false, true);
		} catch (MyError err) {
			Log.debug(err);
			showError(err);
			ok = false;
		} catch (Exception e) {
			Log.debug(e);
			ok = false;
			showError(Errors.LoadFileFailed);
		}
		return ok;
	}

	/**
	 * @return whether 3D view is enabled
	 */
	public boolean is3DViewEnabled() {
		return _3DViewEnabled && is3D();
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
			return is3DViewEnabled();
		}
		if (viewID == App.VIEW_CAS) {
			return CASViewEnabled;
		}
		return true;
	}

	/**
	 * Make sure view repaint scheduler is running.
	 */
	public void ensureTimerRunning() {
		// only for Web
	}

	protected boolean isNativeMobileAppWithNewUI() {
		return false;
	}

	public boolean isUnbundled() {
		return false;
	}

	public boolean isUnbundledGraphing() {
		return false;
	}

	public boolean isUnbundledGeometry() {
		return false;
	}

	public boolean isWhiteboardActive() {
		return false;
	}

	/**
	 * @return whether we are running suite
	 */
	public boolean isSuite() {
		return false;
	}

	/**
	 * Is running mebis app.
	 *
	 * @return true if mebis is running.
	 */
	public boolean isMebis() {
		return false;
	}

	public boolean isUnbundledOrWhiteboard() {
		return isUnbundled() || isWhiteboardActive();
	}

	/**
	 * TODO move to Web
	 * @return whether the app can resize
	 */
	public boolean canResize() {
		return !isApplet();
	}

	/**
	 *
	 * @return true if we want to use shaders
	 */
	public boolean useShaders() {
		return false;
	}

	public final String getTubeId() {
		return tubeID;
	}

	public final void setTubeId(String uniqueId) {
		this.tubeID = uniqueId;
	}

	/**
	 * @return whether the app has focus
	 */
	public boolean hasFocus() {
		return true;
	}

	/**
	 * @return whether euclidian view for plane was initialized
	 */
	final public boolean hasEuclidianViewForPlane() {
		return companion.hasEuclidianViewForPlane();
	}

	/**
	 * @return whether euclidian view for plane exists and is visible
	 */
	final public boolean hasEuclidianViewForPlaneVisible() {
		return companion.hasEuclidianViewForPlaneVisible();
	}

	final public EuclidianView getViewForPlaneVisible() {
		return companion.getViewForPlaneVisible();
	}

	/**
	 * add to views for plane (if any)
	 *
	 * @param geo
	 *            geo
	 */
	final public void addToViewsForPlane(GeoElement geo) {
		companion.addToViewsForPlane(geo);
	}

	/**
	 * @param mode mode
	 * @return whether the mode is valid (has a known name)
	 */
	public boolean isModeValid(int mode) {
		return mode >= 0 && !"".equals(getToolName(mode));
	}

	/**
	 * Update onscreen keyboard.
	 */
	public void updateKeyboard() {
		// overridden in web
	}

	private boolean handlePressAnimationButton() {
		if (!getActiveEuclidianView().isAnimationButtonSelected()) {
			return false;
		}

		AnimationManager animMgr = kernel.getAnimationManager();
		if (animMgr.isRunning()) {
			animMgr.stopAnimation();
		} else {
			animMgr.startAnimation();
		}
		getActiveEuclidianView().repaintView();
		ScreenReader.readAnimationState(this);
		return true;
	}

	private boolean handleResetButton() {
		if (!getActiveEuclidianView().isResetIconSelected()) {
			return false;
		}
		reset();
		return true;
	}

	/**
	 * handle space key hit
	 *
	 * @return true if key is consumed
	 */
	public boolean handleSpaceKey() {
		if (handlePressAnimationButton() || handleResetButton()) {
			return true;
		}

		ArrayList<GeoElement> selGeos = selection.getSelectedGeos();
		if (selGeos.size() == 1) {
			GeoElement geo = selGeos.get(0);
			if (!selection.isSelectableForEV(geo)) {
				// if some selection-preventing property (e.g. visibility) changed in scripts
				// between selecting the geo and pressing <Space>, just do nothing
				return false;
			}
			if (geo.isGeoBoolean()) {
				if (!geo.isIndependent()) {
					return true;
				}

				GeoBoolean geoBool = (GeoBoolean) selGeos.get(0);
				geoBool.setValue(!geoBool.getBoolean());
				geoBool.updateRepaint();
			} else if (geo.isGeoInputBox()) {
				getActiveEuclidianView()
						.focusAndShowTextField((GeoInputBox) geo);
			} else if (geo.isGeoList() && ((GeoList) geo).drawAsComboBox()) {
				Drawable d = (Drawable) getActiveEuclidianView()
						.getDrawableFor(geo);
				if (d != null) {
					((DrawDropDownList) d).toggleOptions();
				}
			} else if (geo.isGeoNumeric()) {

				// <Space> -> toggle slider animation off/on
				GeoNumeric num = (GeoNumeric) geo;
				if (num.isAnimatable() && isRightClickEnabled()) {
					num.setAnimating(!num.isAnimating());

					storeUndoInfo();
					// update play/pause icon at bottom left
					getActiveEuclidianView().repaint();

					if (num.isAnimating()) {
						num.getKernel().getAnimationManager().startAnimation();
					}
				}

			} else {
				ScreenReader.readSpacePressed(geo);
				geo.runClickScripts(null);
				return true;
			}

			// read *after* state changed!
			ScreenReader.readSpacePressed(geo);

			return true;
		}

		return false;
	}

	/**
	 * Update graphics view alt text.
	 * @param geoText to set
	 */
	public void setAltText(GeoText geoText) {
		// ignored in desktop
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

	/**
	 * @return if sliders are displayed in the AV
	 */
	public boolean showAutoCreatedSlidersInEV() {
		return true;
	}

	/**
	 * Prepares the exam mode and shows the exam welcome message
	 * <p>
	 * Note: Only implemented within AppWFull, method still declared here so it can also be
	 * accessed by {@link org.geogebra.common.plugin.GgbAPI}
	 * </p>
	 */
	public void showExamWelcomeMessage() {
		// Not needed here
	}

	/**
	 * @param lang
	 *            locale description
	 */
	public void setLanguage(String lang) {
		// overridden in subtypes
	}

	/**
	 * Run a callback on each view.
	 *
	 * @param c
	 *            view callback
	 */
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

	/**
	 * Close popups and dropdowns; keep active dropdown at (x,y).
	 *
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 */
	public void closePopups(int x, int y) {
		closePopups();
		EuclidianView view = getActiveEuclidianView();
		view.closeDropDowns(x, y);
	}

	/**
	 * @return title for exported file
	 */
	public String getExportTitle() {
		String title = getKernel().getConstruction().getTitle();
		return "".equals(title) ? "geogebra-export" : title;
	}

	public double getExportScale() {
		return this.exportScale;
	}

	/**
	 * @param subApp subapp code
	 * @param p perspective
	 */
	public void updateAppCodeSuite(SuiteSubApp subApp, Perspective p) {
		// only in Web
	}

	/**
	 * When multiple slides are present give ID of the current one, otherwise
	 * give default slide ID when slides supported or empty string if not.
	 *
	 * @return the string ID of current slide
	 */
	public String getSlideID() {
		return "";
	}

	public Layout getLayout() {
		return getGuiManager() == null ? null : getGuiManager().getLayout();
	}

	public StringTemplate getScreenReaderTemplate() {
		return StringTemplate.screenReaderAscii;
	}

	/**
	 *
	 * @param e event to examine
	 * @return if event has the modifier that user can select multiple elements.
	 */
	public boolean hasMultipleSelectModifier(AbstractEvent e) {
		return e.isControlDown();
	}

	/**
	 * Split selected strokes (if any) and deletes selected objects.
	 */
	public void splitAndDeleteSelectedObjects() {
		getActiveEuclidianView().getEuclidianController().splitSelectedStrokes(true);
		deleteSelectedObjects(false);

	}

	public AsyncManagerI getAsyncManager() {
		return Runnable::run;
	}

	/**
	 * @return set of editor features for math fields
	 */
	public EditorFeatures getEditorFeatures() {
		if (editorFeatures == null) {
			editorFeatures = new EditorFeatures();
		}
		return editorFeatures;
	}

	protected SyntaxAdapterImpl createSyntaxAdapter() {
		return new SyntaxAdapterImpl(kernel);
	}

	public boolean isSpreadsheetEnabled() {
		return getConfig().hasSpreadsheetView() && !spreadsheetRestricted;
	}

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

	/**
	 * Export types.
	 */
	public enum ExportType {
		NONE, PDF_TEXTASSHAPES, PDF_EMBEDFONTS, PDF_HTML5, EPS, EMF,

		PNG, PNG_BRAILLE, SVG, PRINTING, ANIMATED_GIF, WEBP, WEBM;

		/**
		 * @return minus sign for axes description
		 */
		public char getAxisMinusSign() {
			switch (this) {
			case PDF_HTML5:
				return Unicode.MINUS;
			case PDF_EMBEDFONTS:
			case PNG_BRAILLE:
				return '-';

			default:
				return Unicode.N_DASH;
			}
		}
	}

	/**
	 * state to know if we'll need to store undo info
	 */
	private enum CoordSystemStateForUndo {
		/** tells that the mouse has been pressed */
		MAY_SET_COORD_SYSTEM,
		/** tells that the coord system has changed */
		SET_COORD_SYSTEM_OCCURRED,
		/** no particular state */
		NONE
	}

	/**
	 * Runs for every view.
	 */
	public interface ViewCallback {
		/**
		 * @param viewID view ID
		 * @param viewName view name as translation key
		 */
		void run(int viewID, String viewName);
	}

	/**
	 * Load construction from zipped XML.
	 * @param zipFile zip file content
	 * @return success
	 */
	final public boolean loadXML(byte[] zipFile) {
		return loadXML(new ByteArrayZipFile(zipFile));
	}

	/**
	 * Opens a file.
	 *
	 * @param zipFile
	 *            ggb file
	 * @return success
	 */
	final public boolean loadXML(ZipFile zipFile) {
		try {
			// make sure objects are displayed in the correct View
			setActiveView(App.VIEW_EUCLIDIAN);

			// reset equation behaviour to app defaults (to clear out any overrides applied
			// from construction defaults in previously opened files)
			if (appConfig != null) {
				kernel.setEquationBehaviour(appConfig.getEquationBehaviour());
			}

			getXMLio().readZipFromString(zipFile);

			kernel.initUndoInfo();
			setSaved();
			resetCurrentFile();
			// command list may have changed due to macros
			updateCommandDictionary();

			hideDockBarPopup();
			return true;
		} catch (Exception err) {
			resetCurrentFile();
			Log.debug(err);
			return false;
		}
	}

	/**
	 * Reset handle for currently open file.
	 */
	public void resetCurrentFile() {
		//
	}

	/**
	 * Hide desktop dockbar.
	 */
	public void hideDockBarPopup() {
		// only used in desktop
	}

	/**
	 * Schedule input preview action.
	 *
	 * @param scheduledPreview
	 *            input preview action
	 */
	public void schedulePreview(Runnable scheduledPreview) {
		// this is basic implementation with no scheduled delay
		scheduledPreview.run();
	}

	/**
	 * Cancel input bar preview.
	 */
	public void cancelPreview() {
		// not needed in basic implementation
	}

	/**
	 * Get url to eg play an MP3.
	 *
	 * @param id
	 *            material ID
	 * @return download URL
	 */
	public String getURLforID(String id) {
		String url;
		if (PreviewFeature.isAvailable(PreviewFeature.RESOURCES_API_BETA)) {
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
	 * @return true if running in native "desktop" Java
	 */
	public boolean isDesktop() {
		return false;
	}

	/**
	 * @return true if running on native Android (not WebView)
	 */
	protected boolean isAndroid() {
		return false;
	}

	/**
	 * Change rounding; setting may contain "s" for scientific digits or "r" to
	 * prefer rationals.
	 *
	 * @param rounding
	 *            rounding description
	 */
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

	/**
	 * @return relation tool dialog
	 */
	public RelationPane getRelationDialog(String subTitle) {
		// overridden in web
		return null;
	}

	/**
	 * @param maxX
	 *            max width in px
	 * @param maxY
	 *            max height in px
	 * @return export image of current EV
	 */
	public GBufferedImage getActiveEuclidianViewExportImage(double maxX,
			double maxY) {
		return getEuclidianViewExportImage(getActiveEuclidianView(), maxX,
				maxY);
	}

	protected static GBufferedImage getEuclidianViewExportImage(
			EuclidianView ev, double maxX, double maxY) {

		double scale = Math.min(maxX / ev.getSelectedWidthInPixels(),
				maxY / ev.getSelectedHeightInPixels());

		return ev.getExportImage(scale);
	}

	/**
	 * Notify table about batch update start.
	 */
	public void batchUpdateStart() {
		kernel.notifyTableViewAboutBatchUpdate(true);
	}

	/**
	 * Notify table about batch update end.
	 */
	public void batchUpdateEnd() {
		kernel.notifyTableViewAboutBatchUpdate(false);
	}

	/**
	 * Adjusts Algebra and Euclidian View next to or bellow each other
	 * (Portrait) according to app size.
	 *
	 * @param force
	 *            TODO
	 *
	 * @return if screen became portrait or not.
	 */
	public boolean adjustViews(boolean reset, boolean force) {
		if (adjustViews == null) {
			adjustViews = new AdjustViews(this);
		}
		adjustViews.apply(force);
		return adjustViews.isPortrait();
	}

	/**
	 * Get the platform the app is running on.
	 *
	 * @return the platform
	 */
	public Platform getPlatform() {
		return platform;
	}

	/**
	 * Description of a labeling style.
	 *
	 * @param app
	 *            app
	 * @param id
	 *            labeling style (GeoElement.LABEL_*)
	 * @return localized labeling style
	 */
	public static String getLabelStyleName(App app, int id) {
		switch (id) {
		case -1:
			return app.getLocalization().getMenu("Hidden");
		case GeoElementND.LABEL_NAME:
			return app.getLocalization().getMenu("Name");
		case GeoElementND.LABEL_NAME_VALUE:
			return app.getLocalization().getMenu("NameAndValue");
		case GeoElementND.LABEL_VALUE:
			return app.getLocalization().getMenu("Value");
		case GeoElementND.LABEL_CAPTION:
			return app.getLocalization().getMenu("Caption");
		default:
			return "";
		}
	}

	/**
	 * @return copy/paste utility
	 */
	public CopyPaste getCopyPaste() {
		return null;
	}

	/**
	 *
	 * @return 9999 (or 200 in web)
	 */
	public int getMaxSpreadsheetRowsVisible() {
		return Spreadsheet.MAX_ROWS;
	}

	/**
	 *
	 * @return 9999 (or 200 in web)
	 */
	public int getMaxSpreadsheetColumnsVisible() {
		return Spreadsheet.MAX_COLUMNS;
	}

	/**
	 *
	 * @param callback
	 *            GeoGebraToPstricks object
	 */
	public void newGeoGebraToPstricks(
			AsyncOperation<GeoGebraExport> callback) {
		// overridden in AppD, AppW
	}

	/**
	 *
	 * @param callback
	 *            GeoGebraToAsymptote object
	 */
	public void newGeoGebraToAsymptote(
			AsyncOperation<GeoGebraExport> callback) {
		// overridden in AppD, AppW
	}

	/**
	 *
	 * @param callback
	 *            GeoGebraToPgf object
	 */
	public void newGeoGebraToPgf(AsyncOperation<GeoGebraExport> callback) {
		// overridden in AppD, AppW
	}

	/**
	 * last commands selected from help (used in Android and iOS native)
	 *
	 * @param commandName
	 *            last command
	 */
	public void addToLastCommandsSelectedFromHelp(String commandName) {
		if (mLastCommandsSelectedFromHelp == null) {
			mLastCommandsSelectedFromHelp = new ArrayList<>();
		}
		// remove if already in it
		mLastCommandsSelectedFromHelp.remove(commandName);
		mLastCommandsSelectedFromHelp.add(commandName);
	}

	/**
	 * @return last commands selected from help (used in Android and iOS native)
	 */
	public ArrayList<String> getLastCommandsSelectedFromHelp() {
		return mLastCommandsSelectedFromHelp;
	}

	protected EuclidianController getEuclidianController() {
		return euclidianController;
	}

	/**
	 * @return whether to use transparent cursor for dragging
	 */
	final public boolean useTransparentCursorWhenDragging() {
		return useTransparentCursorWhenDragging;
	}

	/**
	 * SMART and Android WebView apps use special native Giac Other web apps use
	 * giac.js GGB6 WebView uses giac.js for now, see GGB-895 Everything else
	 * uses native Giac
	 *
	 * @return true if using native Giac, false if using giac.js
	 */
	public boolean nativeCAS() {
		return true;
	}

	/**
	 * @return next construction element ID
	 */
	public long getNextCeIDcounter() {
		return ceIDcounter++;
	}

	/**
	 * @return next prover variable ID
	 */
	public int getNextVariableID() {
		return nextVariableID++;
	}

	@Override
	public void addToolsFilter(@Nonnull ToolCollectionFilter filter) {
		toolFilters.add(filter);
	}

	@Override
	public void removeToolsFilter(@Nonnull ToolCollectionFilter filter) {
		toolFilters.remove(filter);
	}

	/**
	 * @return the currently available tools. Note that the set of tools may be restricted
	 * depending on platform (iOS, Android) or during exams.
	 */
	@Override
	public @Nonnull ToolCollection getAvailableTools() {
		ToolCollection toolCollection = createToolCollectionFactory().createToolCollection();
		toolCollection.filter(this::isModeValid);
		if (getPlatform().isMobile()) {
			toolCollection.filter(new ToolCollectionSetFilter(
					EuclidianConstants.MODE_TEXT,
					EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX,
					EuclidianConstants.MODE_BUTTON_ACTION,
					EuclidianConstants.MODE_TEXTFIELD_ACTION,
					EuclidianConstants.MODE_FUNCTION_INSPECTOR,
					EuclidianConstants.MODE_MOVE_ROTATE));
		}
		for (ToolCollectionFilter toolFilter : toolFilters) {
			toolCollection.filter(toolFilter);
		}
		return toolCollection;
	}

	/**
	 * @return a tool collection factory
	 * @deprecated Use {@link #getAvailableTools()} instead.
	 */
	@Deprecated
	public ToolCollectionFactory createToolCollectionFactory() {
		GuiManagerInterface guiManager = getGuiManager();
		String toolbarDefinition = guiManager != null ? guiManager.getToolbarDefinition() : null;
		if (toolbarDefinition == null || ToolBar.isDefaultToolbar(toolbarDefinition)) {
			return createDefaultToolCollectionFactory();
		}
		return new CustomToolCollectionFactory(this, toolbarDefinition);
	}

	private ToolCollectionFactory createDefaultToolCollectionFactory() {
		boolean isMobileApp = getPlatform().isMobile();
		ToolCollectionFactory factory = null;
		switch (getConfig().getToolbarType()) {
			case GRAPHING_CALCULATOR:
				factory = new GraphingToolCollectionFactory(isMobileApp);
				break;
			case GEOMETRY_CALC:
				factory = new GeometryToolCollectionFactory(isMobileApp);
				break;
			case GRAPHER_3D:
				factory = new Graphing3DToolCollectionFactory(isMobileApp);
				break;
			case SUITE:
				factory = new SuiteToolCollectionFactory(isMobileApp);
				break;
			default:
				factory = new GraphingToolCollectionFactory(isMobileApp);
		}
		return factory;
	}

	/**
	 * set export will be done on next 3D frame
	 *
	 * @param format - export format
	 */
	public void setExport3D(Format format) {
		companion.setExport3D(format, true);
	}

	/**
	 * export directly
	 * @param format - export format
	 */
	public void setDirectExport3D(Format format) {
		companion.setExport3D(format, false);
	}

	public boolean isPortrait() {
		return getHeight() > getWidth();
	}

	public AppConfig getConfig() {
		return appConfig;
	}

	/**
	 * Set the app config and reinitialize the app.
	 */
	public void setConfig(AppConfig config) {
		if (primarySyntaxFilter != null && getLocalization() != null) {
			getLocalization().getCommandSyntax().removeSyntaxFilter(primarySyntaxFilter);
		}
		this.appConfig = config;

		if (kernel != null) {
			kernel.setEquationBehaviour(config.getEquationBehaviour());
			kernel.getAlgebraProcessor().setEnableStructures(config.isEnableStructures());
			initSettingsUpdater().resetSettingsOnAppStart();
		}
		primarySyntaxFilter = config.newCommandSyntaxFilter();
		if (primarySyntaxFilter != null && getLocalization() != null) {
			getLocalization().getCommandSyntax().addSyntaxFilter(primarySyntaxFilter);
		}
		resetAlgebraOutputFilter();
	}

	/**
	 *
	 * @return the AccessibilityManager.
	 */
	public AccessibilityManagerInterface getAccessibilityManager() {

		if (accessibilityManager == null) {
			accessibilityManager = new AccessibilityManagerNoGui(this);
		}

		return accessibilityManager;
	}

	/**
	 * check is view is 3D WITHOUT creating 3D View
	 *
	 * @param view
	 *            view
	 * @return true if it's 3D
	 */
	public boolean isEuclidianView3D(EuclidianViewInterfaceCommon view) {
		return false;
	}

	/**
	 * Hides burger menu on web
	 */
	public void hideMenu() {
		// overwritten in web
	}

	/**
	 * Lazy load special points manager
	 *
	 * @return special points manager
	 */
	public SpecialPointsManager getSpecialPointsManager() {
		if (this.specialPointsManager == null) {
			specialPointsManager = new SpecialPointsManager(kernel);
			specialPointsManager.registerSpecialPointsListener(kernel);
			specialPointsManager.registerSpecialPointsListener(euclidianController);
		}
		return specialPointsManager;
	}

	/**
	 * @return whether special points manager is initialized
	 */
	public boolean hasSpecialPointsManager() {
		return specialPointsManager != null;
	}

	/**
	 * enable/disable CAS and re-init command table
	 *
	 * @param enable
	 *            whether to enable CAS
	 */
	public void enableCAS(boolean enable) {
		getSettings().getCasSettings().setEnabled(enable);
		getKernel().getAlgebraProcessor().reinitCommands();
	}

	/**
	 * enable/disable CAS and re-init command table
	 *
	 * @param cas
	 *            if CAS is enabled
	 * @param commands3d
	 *            if 3D commands are enabled
	 */
	public void enableCAS3D(boolean cas, boolean commands3d) {
		areCommands3DEnabled = commands3d;
		enableCAS(cas);
	}

	/**
	 * @return whether 3D commands are enabled
	 */
	public boolean areCommands3DEnabled() {
		return areCommands3DEnabled && (getSettings().getEuclidian(-1) == null
				|| getSettings().getEuclidian(-1).isEnabled());
	}

	/**
	 * @param fallback
	 *            value in case temp perspective is not defined
	 * @return perspective called "tmp" or given fallback
	 */
	public Perspective getTmpPerspective(Perspective fallback) {
		return tmpPerspective == null ? fallback : tmpPerspective;
	}

	/**
	 *
	 * @param ext - extension
	 * @param content - contents of file
	 * @param showDialog - whether should show dialog
	 */
	public void exportStringToFile(String ext, String content, boolean showDialog) {
		// needs to be implemented in subclasses
	}

	/**
	 * handle image as appropriate
	 * web: show in lightbox
	 * mobile: share intent (TODO)
	 * desktop: copy to clipboard
	 *
	 * @param base64image base64 encoded PNG/SVG/PDF
	 */
	public void handleImageExport(String base64image) {
		// overridden in AppW, AppD, etc

	}

	/**
	 * @param slideID
	 *            slide name
	 */
	public void setActiveSlide(String slideID) {
		// TODO Auto-generated method stub
	}

	/**
	 * @param imgFileName
	 *            file name
	 * @param imgBase64
	 *            base64 of image content
	 * @param imageOld
	 *            old image
	 * @param autoCorners
	 *            whether to create corners
	 * @param c1
	 *            corner 1
	 * @param c2
	 *            corner 2
	 * @return image
	 */
	public GeoImage createImageFromString(final String imgFileName,
			String imgBase64, GeoImage imageOld, boolean autoCorners, GeoPointND c1,
			GeoPointND c2) {
		return null;
	}

	/**
	 * TODO inline
	 * @param s string
	 * @return MD5 checksum
	 */
	public String md5Encrypt(String s) {
		return MD5Checksum.compute(s);
	}

	public @CheckForNull EmbedManager getEmbedManager() {
		return null;
	}

	/**
	 * @return whether embed manager was initialized
	 */
	public boolean hasEmbedManager() {
		return false;
	}

	protected void initSettings() {
		settings = newSettingsBuilder().newSettings();
	}

	/**
	 * @return settings builder for this app
	 */
	public SettingsBuilder newSettingsBuilder() {
		return new SettingsBuilder(this);
	}

	/**
	 *
	 * @param m
	 *            mode
	 * @return icon as base64 (might be PNG or SVG)
	 */
	public String getModeIconBase64(int m) {
		return "";
	}

	/**
	 * Remove all videos / embeds.
	 */
	public void clearMedia() {
		// remove all videos / embeds
	}

	/**
	 * Enables only the english commands
	 */
	public void forceEnglishCommands() {
		getLocalization().forceEnglishCommands();
		StringTemplate.editorTemplate.setLocalizeCmds(false);
		StringTemplate.editorTemplate.setPrintMethodsWithParenthesis(true);
		StringTemplate.latexTemplate.setLocalizeCmds(false);
		StringTemplate.latexTemplate
				.setPrintMethodsWithParenthesis(true);
	}

	/**
	 *
	 * @return controller for saving materials.
	 */
	public SaveController getSaveController() {
		return null;
	}

	/**
	 *
	 * @return controller for sharing materials.
	 */
	public ShareController getShareController() {
		return null;
	}

	/**
	 * @param keyboardSettings
	 *            parent settings
	 * @return desktop keyboard settings, dummy settings in other platform
	 */
	public AbstractSettings getKeyboardSettings(
			AbstractSettings keyboardSettings) {
		return new AbstractSettings() {
			// no-op
		};
	}

	/**
	 * @param attrs
	 *            XML attributes
	 */
	public void updateKeyboardSettings(LinkedHashMap<String, String> attrs) {
		// only desktop
	}

	/**
	 * If the settingsUpdater is already initialized then returns this field,
	 * otherwise
	 * creates a new SettingsUpdaterBuilder,
	 * creates the SettingsUpdater instance using the SettingsUpdaterBuilder,
	 * initializes the settingsUpdater field with the new SettingsUpdater instance
	 * and returns the settingsUpdater field.
	 * @return The settingsUpdater field.
	 */
	public SettingsUpdater getSettingsUpdater() {
		if (settingsUpdater == null) {
			SettingsUpdaterBuilder settingsUpdaterBuilder = newSettingsUpdaterBuilder();
			settingsUpdater = settingsUpdaterBuilder.newSettingsUpdater();
		}
		return settingsUpdater;
	}

	public FontSettingsUpdater getFontSettingsUpdater() {
		return getSettingsUpdater().getFontSettingsUpdater();
	}

	/**
	 * make sure we create a new settings updater according the new appConfig
	 * @return setting updater
	 */
	public SettingsUpdater initSettingsUpdater() {
		SettingsUpdaterBuilder settingsUpdaterBuilder = newSettingsUpdaterBuilder();
		settingsUpdater = settingsUpdaterBuilder.newSettingsUpdater();
		return settingsUpdater;
	}

	protected SettingsUpdaterBuilder newSettingsUpdaterBuilder() {
		SettingsUpdaterBuilder builder = new SettingsUpdaterBuilder(this);
		builder.setPrototype(getConfig().createSettingsUpdater());
		return builder;
	}

	/**
	 * Copy image to system clipboard
	 * 
	 * @param dataURI data URI of image to copy
	 */
	public void copyImageToClipboard(String dataURI) {
		// implemented in AppD, AppW
	}

	/**
	 * Returns the primary color of the app.
	 *
	 * @return primary color
	 */
	public GColor getPrimaryColor() {
		return GeoGebraColorConstants.GEOGEBRA_ACCENT;
	}

	/**
	 * @return FpsProfiler instance.
	 */
	public FpsProfiler getFpsProfiler() {
		return null;
	}

	/**
	 * Autonomously draws from the coords.json file.
	 */
	public void testDraw() {
		// no-op
	}

	/**
	 * Records the drawing.
	 */
	public void startDrawRecording() {
		// no-op
	}

	/**
	 * Ends the recording of the drawing and logs the results.
	 *
	 * For autonomous drawing, the logged result has to be copied into the coords.json file.
	 */
	public void endDrawRecordingAndLogResults() {
		// no-op
	}

	public MaskWidgetList getMaskWidgets() {
		return null;
	}

	/**
	 * @return The current converter for creating a String from a {@link GeoElement}.
	 * @apiNote DO NOT CACHE THE RETURN VALUE, the converter may change at runtime (e.g., for
	 * certain exams).
	 */
	public @Nonnull ToStringConverter getGeoElementValueConverter() {
		if (valueConverter == null) {
			valueConverter = new ProtectiveGeoElementValueConverter(getAlgebraOutputFilter());
		}
		return valueConverter;
	}

	/**
	 * @return The current {@link AlgebraOutputFilter}.
	 * @apiNote DO NOT CACHE THE RETURN VALUE, the filter may change at runtime (e.g., for certain
	 * exams).
	 */
	public @Nonnull AlgebraOutputFilter getAlgebraOutputFilter() {
		if (algebraOutputFilter == null) {
			if (getConfig().shouldHideEquations()) {
				algebraOutputFilter = new ProtectiveAlgebraOutputFilter();
			} else {
				algebraOutputFilter = new DefaultAlgebraOutputFilter();
			}
		}
		return algebraOutputFilter;
	}

	/**
	 * Visible only for testing.
	 */
	public void resetAlgebraOutputFilter() {
		algebraOutputFilter = null;
	}

	/**
	 * Create an inline text controller iff the view supports inline text
	 * editing.
	 *
	 * @param geo
	 *            inline text
	 *
	 * @return an implementation of the text controller.
	 */
	public InlineTextController createInlineTextController(EuclidianView view,
		   GeoInline geo) {
		return null;
	}

	/**
	 * Create a formula controller for editing in EV.
	 * @param view view
	 * @param geo formula
	 * @return formula controller
	 */
	public InlineFormulaController createInlineFormulaController(EuclidianView view,
			GeoFormula geo) {
		return null;
	}

	/**
	 * Create a Murok table controller for editing in EV.
	 * @param view view
	 * @param table table
	 * @return table controller
	 */
	public InlineTableController createTableController(EuclidianView view, GeoInlineTable table) {
		return null;
	}

	/**
	 * GeoPriorityComparators are used to decide the drawing
	 * and selection orders of Geos
	 * @return the default comparator (layer -&gt; type -&gt; construction order) in every
	 * app except notes, where the geo's `ordering` is used
	 */
	public GeoPriorityComparator getGeoPriorityComparator() {
		if (isWhiteboardActive()) {
			return new NotesPriorityComparator();
		} else {
			return new DefaultGeoPriorityComparator();
		}
	}

	/**
	 * hide the on-screen keyboard (if it is visible)
	 */
	public void hideKeyboard() {
		// Overwritten in subclass - nothing to do here
	}

	/**
	 * Close the menu and hide the keyboard.
	 */
	public void closeMenuHideKeyboard() {
		// nothing here
	}

	@Override
	public void setXML(String xml, boolean clearAll) {
		if (xml == null) {
			return;
		}
		if (clearAll) {
			resetCurrentFile();
		}

		try {
			// make sure objects are displayed in the correct View
			setActiveView(App.VIEW_EUCLIDIAN);
			getXMLio().processXMLString(xml, clearAll, false);
		} catch (MyError err) {
			Log.debug(err);
			showError(err);
		} catch (Exception e) {
			Log.debug(e);
			showError(Errors.LoadFileFailed);
		}
	}

	public String getThreadId() {
		return "[main thread]";
	}

	public @CheckForNull Material getActiveMaterial() {
		return activeMaterial;
	}

	public void setActiveMaterial(Material material) {
		activeMaterial = material;
	}

	@Override
	public MyImage getInternalImageAdapter(String filename, int width, int height) {
		return null;
	}

	// ExamRestrictable

	@Override
	public void applyRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions,
			@Nonnull ExamType examType) {
		resetCommandDict();

		algebraOutputFilter = examType.wrapAlgebraOutputFilter(getAlgebraOutputFilter());
		valueConverter = null;

		if (featureRestrictions.contains(ExamFeatureRestriction.HIDE_SPECIAL_POINTS)) {
			getSpecialPointsManager().isEnabled = false;
		}
		if (featureRestrictions.contains(ExamFeatureRestriction.SURD)) {
			kernel.setSurds(null);
		}
		if (featureRestrictions.contains(ExamFeatureRestriction.RATIONALIZATION)) {
			kernel.setRationalization(null);
		}
		if (featureRestrictions.contains(ExamFeatureRestriction.DISABLE_MIXED_NUMBERS)) {
			getEditorFeatures().setMixedNumbersEnabled(false);
		}
		spreadsheetRestricted = featureRestrictions.contains(ExamFeatureRestriction.SPREADSHEET);
	}

	@Override
	public void removeRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions,
			@Nonnull ExamType examType) {
		// null out filters, to recreate on next use
		algebraOutputFilter = null;
		valueConverter = null;
		if (featureRestrictions.contains(ExamFeatureRestriction.HIDE_SPECIAL_POINTS)) {
			getSpecialPointsManager().isEnabled = true;
		}
		if (featureRestrictions.contains(ExamFeatureRestriction.SURD)) {
			kernel.setSurds(new Surds());
		}
		if (featureRestrictions.contains(ExamFeatureRestriction.RATIONALIZATION)) {
			kernel.setRationalization(new Rationalization());
		}
		if (featureRestrictions.contains(ExamFeatureRestriction.DISABLE_MIXED_NUMBERS)) {
			getEditorFeatures().setMixedNumbersEnabled(true);
		}
		if (featureRestrictions.contains(ExamFeatureRestriction.SPREADSHEET)) {
			spreadsheetRestricted = false;
		}
		resetCommandDict();
	}

	public RegressionSpecificationBuilder getRegressionSpecBuilder() {
		return regressionSpecificationBuilder;
	}

	/**
	 * @return True if the Algebra View is currently focused, false else
	 */
	public boolean isAlgebraViewFocused() {
		GuiManagerInterface guiManager = getGuiManager();
		if (guiManager == null || guiManager.getLayout() == null
				|| guiManager.getLayout().getDockManager() == null) {
			return false;
		}
		return guiManager.getLayout().getDockManager().getFocusedViewId() == VIEW_ALGEBRA;
	}

	/**
	 * Save settings.
	 */
	public void saveSettings() {
		// Overridden for web
	}

	/**
	 * Restore default settings.
	 */
	public void restoreSettings() {
		// Overridden for web
	}

}
