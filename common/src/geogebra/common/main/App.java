package geogebra.common.main;

import geogebra.common.GeoGebraConstants;
import geogebra.common.awt.GBufferedImage;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GImage;
import geogebra.common.cas.singularws.SingularWebService;
import geogebra.common.euclidian.DrawEquation;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.factories.CASFactory;
import geogebra.common.factories.Factory;
import geogebra.common.factories.SwingFactory;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.infobar.InfoBar;
import geogebra.common.gui.menubar.MenuInterface;
import geogebra.common.gui.menubar.OptionsMenu;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.gui.view.properties.PropertiesView;
import geogebra.common.io.MyXMLio;
import geogebra.common.io.layout.Perspective;
import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.kernel.AnimationManager;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.GeoGebraCasInterface;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.Relation;
import geogebra.common.kernel.UndoManager;
import geogebra.common.kernel.View;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.commands.CommandDispatcher;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.commands.CommandsConstants;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.kernel.parser.cashandlers.ParserFunctions;
import geogebra.common.main.settings.Settings;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.Event;
import geogebra.common.plugin.EventDispatcher;
import geogebra.common.plugin.GeoScriptRunner;
import geogebra.common.plugin.GgbAPI;
import geogebra.common.plugin.ScriptManager;
import geogebra.common.plugin.ScriptType;
import geogebra.common.plugin.jython.PythonBridge;
import geogebra.common.plugin.script.GgbScript;
import geogebra.common.plugin.script.Script;
import geogebra.common.sound.SoundManager;
import geogebra.common.util.AbstractImageManager;
import geogebra.common.util.GeoGebraLogger;
import geogebra.common.util.LowerCaseDictionary;
import geogebra.common.util.NormalizerMinimal;
import geogebra.common.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.Set;

/**
 * Represents an application window, gives access to views and system stuff
 */
public abstract class App implements UpdateSelection{
	/** Script manager */
	protected ScriptManager scriptManager = null;
	/**
	 * True when we are running standalone app or signed applet, false for
	 * unsigned applet
	 */
	protected static boolean hasFullPermissions = false;
	/** whether current construction was saved after last changes */
	protected boolean isSaved = true;
	/** Url for wiki article about functions */
	public static final String WIKI_OPERATORS = "Predefined Functions and Operators";
	/** Url for main page of manual */
	public static final String WIKI_MANUAL = "Manual:Main Page";
	/** Url for wiki article about CAS */
	public static final String WIKI_CAS_VIEW = "CAS_View";
	/** Url for wiki tutorials */
	public static final String WIKI_TUTORIAL = "Tutorial:Main Page";
	/** Url for wiki article about exporting to HTML */
	public static final String WIKI_EXPORT_WORKSHEET = "Export_Worksheet_Dialog";
	/**
	 * Url for wiki article about advanced features (layers, cond. visibility
	 * etc.)
	 */
	public static final String WIKI_ADVANCED = "Advanced Features";
	/** Url for wiki article about functions */
	public static final String WIKI_TEXT_TOOL = "Insert Text Tool";

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
	/** let us break the pattern */
	public static final int VIEW_EVENT_DISPATCHER = 42;
	/**
	 * id for view created from plane; also 1025 to 2047 might be used for this
	 * purpose
	 */
	public static final int VIEW_EUCLIDIAN_FOR_PLANE_START = 1024;
	/** maximal ID of view for plane */
	public static final int VIEW_EUCLIDIAN_FOR_PLANE_END = 2047;
	// please let 1024 to 2047 empty
	/** id for plot panels (small EVs eg in regression analysis tool) */
	public static final int VIEW_PLOT_PANEL = 2048;
	/** id for text preview in text tool */
	public static final int VIEW_TEXT_PREVIEW = 4096;
	/** id for properties view */
	public static final int VIEW_PROPERTIES = 4097;
	/** id for assignment view */
	public static final int VIEW_ASSIGNMENT = 8192;
	/** id for spreadsheet table model */
	public static final int VIEW_TABLE_MODEL = 9000;
	/** id for Python view */
	public static final int VIEW_PYTHON = 16384;
	private boolean showResetIcon = false;
	/**
	 * Whether we are running applet in frame. Not possible with 4.2+ (we need
	 * this to hide reset icon from EV)
	 */
	public boolean runningInFrame = false;
	private ParserFunctions pf = new ParserFunctions();

	private SpreadsheetTraceManager traceManager;

	/**
	 * object is hit if mouse is within this many pixels (more for points, see
	 * geogebra.common.euclidian.DrawPoint)
	 */
	public int capturingThreshold = 3;

	/**
	 * Whether inputbar should be shown on top
	 */
	protected boolean showInputTop = false;

	/**
	 * Whether input bar should be visible
	 */
	protected boolean showAlgebraInput = true;
	/**
	 * Whether toolbar should appear on top
	 */
	protected boolean showToolBarTop = true;
	/**
	 * Whether toolbar help should appear
	 */
	protected boolean showToolBarHelp = false;
	
	/**
	 * Toolbar position 
	 */
	protected int toolbarPosition;
	
	/**
	 * Whether input help toggle button should be visible
	 */
	protected boolean showInputHelpToggle = true;

	/**
	 * Whether AV should show auxiliary objects stored here rather than in
	 * algebra view so that it can be set without creating an AV (compatibility
	 * with 3.2)
	 */
	public boolean showAuxiliaryObjects = false;
	/** whether righ click is enabled */
	protected boolean rightClickEnabled = true;

	/** flag to test whether to draw Equations full resolution */
	public boolean exporting = false;

	private static String CASVersionString = "";

	/**
	 * @param string
	 *            CAS version string
	 */
	public static final void setCASVersionString(String string) {
		CASVersionString = string;

	}

	/**
	 * @return CAS version
	 */
	public static final String getCASVersionString() {
		return CASVersionString;

	}

	/** XML input / output handler */
	protected MyXMLio myXMLio;

	/* Font settings */
	/** minimal font size */
	public static final int MIN_FONT_SIZE = 10;
	// gui / menu fontsize (-1 = use appFontSize)
	private int guiFontSize = -1;
	// currently used application fonts
	private int appFontSize;
	// note: It is not necessary to use powers of 2 for view IDs

	// For eg Hebrew and Arabic.
	

	// moved to Application from EuclidianView as the same value is used across
	// multiple EVs
	private int maxLayerUsed = 0;
	/** size of checkboxes */
	public int booleanSize = 13;
	/**
	 * right angle style
	 * 
	 * @see EuclidianStyleConstants#RIGHT_ANGLE_STYLE_SQUARE
	 * @see EuclidianStyleConstants#RIGHT_ANGLE_STYLE_DOT
	 * @see EuclidianStyleConstants#RIGHT_ANGLE_STYLE_L
	 * @see EuclidianStyleConstants#RIGHT_ANGLE_STYLE_NONE
	 */
	public int rightAngleStyle = EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE;
	/** whether Java fonts shall be used in LaTeX formulas */
	public boolean useJavaFontsForLaTeX = false;
	
	/** kernel */
	protected Kernel kernel;
	/** whether points can be created by other tools than point tool */
	protected boolean isOnTheFlyPointCreationActive = true;
	/** Settings object */
	protected Settings settings;
	
	protected SelectionManager selection;
	/**
	 * @return whether Java fonts shall be used by JLatexMath (no effect in Web)
	 * */
	public boolean useJavaFontsForLaTeX() {
		return useJavaFontsForLaTeX;

	}

	/** whether we should use antialisaing in EV */
	protected boolean antialiasing = true;
	/**
	 * whether axes should be visible when EV is created first element of this
	 * array is for x-axis, second for y-axis
	 */
	protected final boolean[] showAxes = { true, true };
	/** whether grid should be visible when EV is created */
	protected boolean showGrid = false;
	/**
	 * this flag is true during initialization phase (until GUI is built and
	 * command line args handled, incl. file loading) or when we are opening a
	 * file
	 */
	protected boolean initing = false;

	private boolean labelDragsEnabled = true;
	/** initial number of columns for spreadsheet */
	public static final int SPREADSHEET_INI_COLS = 26;
	/** initial number of rows for spreadsheet */
	public static final int SPREADSHEET_INI_ROWS = 100;

	private HashMap<String, String> translateCommandTable;
	// command dictionary
	private LowerCaseDictionary commandDict;
	private LowerCaseDictionary commandDictCAS;
	/** Euclidian view */
	protected EuclidianView euclidianView;
	/** Euclidian view's controller */
	protected EuclidianController euclidianController;
	/** selection listener */
	protected GeoElementSelectionListener currentSelectionListener;
	/** whether menubar should be visible */
	protected boolean showMenuBar = true;
	// array of dictionaries corresponding to the sub command tables
	private LowerCaseDictionary[] subCommandDict;

	private String scriptingLanguage;





	/**
	 * We need this method so that we can override it using more powerful
	 * normalizer
	 * 
	 * @return new lowercase dictionary
	 */
	protected LowerCaseDictionary newLowerCaseDictionary() {
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
				&& ((commandDictCAS != null) || getLocalization().isCommandNull())) {
			return;
		}
		GeoGebraCasInterface cas = kernel.getGeoGebraCAS();
		if (cas == null) {
			return;
		}
		getLocalization().setCommandChanged(false);

		commandDictCAS = newLowerCaseDictionary();
		subCommandDict[CommandsConstants.TABLE_CAS].clear();

		// get all commands from the commandDict and write them to the
		// commandDictCAS

		// the keySet contains all commands of the dictionary; see
		// LowerCaseDictionary.addEntry(String s) for more
		Set<String> commandDictContent = commandDict.keySet();

		// write them to the commandDictCAS
		for (String cmd : commandDictContent) {
			commandDictCAS.addEntry(cmd);
		}

		// iterate through all available CAS commands, add them (translated if
		// available, otherwise untranslated)
		for (String cmd : cas.getAvailableCommandNames()) {
			putInTranslateCommandTable(Commands.valueOf(cmd));
			try {
				String local = getLocalization().getCommand(cmd);
				if (local != null) {
					commandDictCAS.addEntry(local);
					subCommandDict[CommandsConstants.TABLE_CAS].addEntry(local);
				} else {
					commandDictCAS.addEntry(cmd);
					subCommandDict[CommandsConstants.TABLE_CAS].addEntry(cmd);
				}
			} catch (MissingResourceException mre) {
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
		if (translateCommandTable == null) {
			translateCommandTable = new HashMap<String, String>();
		}

		// command dictionary for all public command names available in
		// GeoGebra's input field
		// removed check for null: commandDict.clear() removes keys, but they
		// are still available with commandDict.getIterator()
		// so change English -> French -> English doesn't work in the input bar
		// see AutoCompleteTextfield.lookup()
		// if (commandDict == null)
		commandDict = newLowerCaseDictionary();
		// else commandDict.clear();

		translateCommandTable.clear();

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

		for (Commands comm : Commands.values()) {
			String internal = comm.name();
			if (!tableVisible(comm.getTable())) {
				if (comm.getTable() == CommandsConstants.TABLE_ENGLISH) {
					putInTranslateCommandTable(comm);
				}
				continue;
			}
			putInTranslateCommandTable(comm);
			// App.debug(internal);
			String local = getLocalization().getCommand(internal);

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
		getParserFunctions().updateLocale(this);
		// get CAS Commands
		if (kernel.isGeoGebraCASready()) {
			fillCasCommandDict();
		}
		addMacroCommands();
		getLocalization().setCommandChanged(false);
	}

	private void putInTranslateCommandTable(Commands comm) {
			String internal = comm.name();
			//Check that we don't overwrite local with English
			if(!translateCommandTable.containsKey(StringUtil.toLowerCase(internal))){
				translateCommandTable.put(StringUtil.toLowerCase(internal),
						Commands.englishToInternal(comm).name());
			}
			String s = getLocalization().getCommand(internal);
			if(s!=null){
				translateCommandTable.put(StringUtil.toLowerCase(s),
						Commands.englishToInternal(comm).name());
			}
		
		
	}

	/**
	 * return true if commands of this table should be visible in input bar help
	 * and autocomplete
	 * 
	 * @param table
	 *            table number, see CommandConstants.TABLE_*
	 * @return true for visible tables
	 */
	protected boolean tableVisible(int table) {
		return !(table == CommandsConstants.TABLE_3D || table == CommandsConstants.TABLE_ENGLISH);
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
		initTranslatedCommands();

		String key = StringUtil.toLowerCase(command);
		
		String ret = translateCommandTable==null? key : translateCommandTable.get(key);
		if(ret!=null)
				return ret;
			// if that fails check internal commands
		for (Commands c : Commands.values()) {
			if (StringUtil.toLowerCase(c.name()).equals(key)) {
				return Commands.englishToInternal(c).name();
			}
		}
		return null;
		
	}

	/**
	 * Updates command dictionary
	 */
	public void updateCommandDictionary() {
		// make sure all macro commands are in dictionary
		fillCommandDict();
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
	 * @param blockUpdateScripts
	 *            the blockUpdateScripts to set
	 */
	public void setBlockUpdateScripts(boolean blockUpdateScripts) {
		this.blockUpdateScripts = blockUpdateScripts;
	}

	private boolean blockUpdateScripts = false;

	/**
	 * Translates localized command name into internal TODO check whether this
	 * differs from translateCommand somehow and either document it or remove
	 * this method
	 * 
	 * @param cmd
	 *            localized command name
	 * @return internal command name
	 */
	final public String getInternalCommand(String cmd) {
		initTranslatedCommands();
		String s;
		String cmdLower = StringUtil.toLowerCase(cmd);
		for (Commands c:Commands.values()) {
			s = Commands.englishToInternal(c).name();
			
				// make sure that when si[] is typed in script, it's changed to
				// Si[] etc
				if (StringUtil.toLowerCase(getLocalization().getCommand(s)).equals(cmdLower)) {
					return s;
				}
			
		}
		return null;
	}

	/**
	 * Show error dialog wiith given text
	 * 
	 * @param s
	 *            error message
	 */
	public abstract void showError(String s);

	/**
	 * Shows error dialog with a given text
	 * 
	 * @param s
	 */
	public abstract void showErrorDialog(String s);

	private boolean useBrowserForJavaScript = true;
	private EventDispatcher eventDispatcher;

	/**
	 * @param useBrowserForJavaScript
	 *            desktop: determines whether Rhino will be used (false) or the
	 *            browser (true) web: determines whether JS scripting allowed
	 *            (true) or not (false)
	 */
	public void setUseBrowserForJavaScript(boolean useBrowserForJavaScript) {
		this.useBrowserForJavaScript = useBrowserForJavaScript;
	}

	/**
	 * @return desktop: determines whether Rhino will be used (false) or the
	 *         browser (true) web: determines whether JS scripting allowed
	 *         (true) or not (false)
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

	// TODO: move following methods somewhere else
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
	 * 
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

	

	/* selection handling */

	/**
	 * @return last created GeoElement
	 */
	final public GeoElement getLastCreatedGeoElement() {
		return kernel.getConstruction().getLastGeoElement();
	}

	// used by PropertyDialogGeoElement and MenuBarImpl
	// for the Rounding Menus
	final public static int roundingMenuLookup[] = { 0, 1, 2, 3, 4, 5, 10, 15,
			-1, 3, 5, 10, 15 };
	final public static int decimalsLookup[] = { 0, 1, 2, 3, 4, 5, -1, -1, -1,
			-1, 6, -1, -1, -1, -1, 7 };
	final public static int figuresLookup[] = { -1, -1, -1, 9, -1, 10, -1, -1,
			-1, -1, 11, -1, -1, -1, -1, 12 };

	

	

	/**
	 * Rounding menu options (not internationalized)
	 */
	final public static String[] strDecimalSpacesAC = { "0 decimals",
			"1 decimals", "2 decimals", "3 decimals", "4 decimals",
			"5 decimals", "10 decimals", "15 decimals", "", "3 figures",
			"5 figures", "10 figures", "15 figures" };

	// Rounding Menus end

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
			storeUndoInfo();
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
	 * @param sb
	 *            string builder
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
	 * given 1, return eg 1st, 1e, 1:e according to the language
	 * 
	 * http://en.wikipedia.org/wiki/Ordinal_indicator
	 */
	StringBuilder sbOrdinal;

	

	
	

	

	
	

	
	
	
	
	/**
	 * Update right angle style to match current locale
	 */
	public void updateRightAngleStyle() {
		if (rightAngleStyle != EuclidianStyleConstants.RIGHT_ANGLE_STYLE_NONE) {
			if (getLocalization().getLanguage().equals("de") || 
					getLocalization().getLanguage().equals("hu")) {
				rightAngleStyle = EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT;
			} else {
				rightAngleStyle = EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE;
			}
		}
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
		notice("AbstrEuclView");
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
	public abstract EuclidianViewInterfaceCommon getActiveEuclidianView();

	/**
	 * XML settings for both EVs
	 * 
	 * @param sb
	 *            string builder
	 * @param asPreference
	 *            whether we need this for preference XML
	 */
	public void getEuclidianViewXML(StringBuilder sb, boolean asPreference) {
		getEuclidianView1().getXML(sb, asPreference);
		if (hasEuclidianView2EitherShowingOrNot()) {
			getEuclidianView2().getXML(sb, asPreference);
		}
	}

	/**
	 * @return whether 3D view was initialized
	 */
	public boolean hasEuclidianView3D() {
		return false;
	}

	/**
	 * @return 3D view
	 */
	public EuclidianViewInterfaceCommon getEuclidianView3D() {
		return null;
	}

	/**
	 * @return whether EV2 was initialized
	 */
	public abstract boolean hasEuclidianView2EitherShowingOrNot();

	/**
	 * @return whether EV2 is visible
	 */
	public abstract boolean isShowingEuclidianView2();

	/**
	 * @return image manager
	 */
	public abstract AbstractImageManager getImageManager();

	/**
	 * @return gui manager (it's null in minimal applets)
	 */
	public abstract GuiManager getGuiManager();

	/**
	 * @return dialog manager
	 */
	public abstract DialogManager getDialogManager();

	/**
	 * Initializes GUI manager
	 */
	protected abstract void initGuiManager();

	/**
	 * Prints a stacktrace (used for debugging)
	 * 
	 * @author Michael Borcherds
	 * @param message
	 *            message to appear on top of the stacktrace
	 */
	public static void printStacktrace(String message) {
		try {
			throw new Exception(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes SingularWS
	 */
	public static void initializeSingularWS() {
		singularWS = new SingularWebService();
		singularWS.enable();
		if (singularWS.isAvailable()) {
			info("SingularWS is available at " + singularWS.getConnectionSite());
			// debug(singularWS.directCommand("ring r=0,(x,y),dp;ideal I=x^2,x;groebner(I);"));
		} else {
			info("No SingularWS is available at "
					+ singularWS.getConnectionSite() + " (yet)");
		}
	}
	
	/**
	 * Prints debugging message, level DEBUG Special debugging format is used
	 * for expression values
	 * 
	 * @param s
	 *            object to be printed
	 */
	public static void debug(Object s) {
		if (s instanceof ExpressionValue) {
			debug(ValidExpression.debugString((ExpressionValue) s));
			return;
		}
		if (s == null) {
			debug("<null>");
		} else {
			debug(s.toString());
		}
	}
	
	/**
	 * Shows an announcement in the infobar.
	 * @param message the information (preferably one line)
	 */
	public static void showAnnouncement(String message) {
		infobar.show(message);
	}

	/**
	 * Hides the announcement in the infobar.
	 */
	public static void hideAnnouncement() {
		infobar.hide();
	}

	/**
	 * Prints debugging message, level DEBUG
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void debug(String message) {
		if (logger != null) {
			logger.log(logger.DEBUG, message);
		}
	}

	/**
	 * Prints debugging message, level NOTICE
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void notice(String message) {
		if (logger != null) {
			logger.log(logger.NOTICE, message);
		}
	}

	/**
	 * Prints debugging message, level INFO
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void info(String message) {
		if (logger != null) {
			logger.log(logger.INFO, message);
		}
	}

	/**
	 * Prints debugging message, level ERROR
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void error(String message) {
		if (logger != null) {
			logger.log(logger.ERROR, message);
		}
	}

	/**
	 * Prints debugging message, level WARN
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void warn(String message) {
		if (logger != null) {
			logger.log(logger.WARN, message);
		}
	}

	/**
	 * Prints debugging message, level EMERGENCY
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void emergency(String message) {
		if (logger != null) {
			logger.log(logger.EMERGENCY, message);
		}
	}

	/**
	 * Prints debugging message, level ALERT
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void alert(String message) {
		if (logger != null) {
			logger.log(logger.ALERT, message);
		}
	}

	/**
	 * Prints debugging message, level TRACE
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void trace(String message) {
		if (logger != null) {
			logger.log(logger.TRACE, message);
		}
	}

	/**
	 * Prints debugging message, level CRITICAL
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void critical(String message) {
		if (logger != null) {
			logger.log(logger.CRITICAL, message);
		}
	}

	/** logger */
	public static GeoGebraLogger logger;
	/** Singular web service (CAS) */
	public static SingularWebService singularWS;
	public static InfoBar infobar;
	
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
	 * @param scriptingLanguage
	 *            the scriptingLanguage to set
	 */
	public void setScriptingLanguage(String scriptingLanguage) {
		this.scriptingLanguage = scriptingLanguage;
	}

	@SuppressWarnings("deprecation")
	public static boolean isWhitespace(char charAt) {
		return Character.isSpace(charAt);
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
	 */
	public abstract void evalJavaScript(App app, String script, String arg);

	private int[] version = null;

	/**
	 * @param v
	 *            version parts
	 * @return whether given version is newer than this code
	 */
	public boolean fileVersionBefore(int[] v) {
		if (this.version == null) {
			return true;
		}

		int length = version.length;
		if (v.length < length) {
			length = v.length;
		}

		for (int i = 0; i < length; i++) {
			if (version[i] < v[i]) {
				return true;
			} else if (version[i] > v[i]) {
				return false;
			}
		}

		return version.length < v.length;
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
			this.version = null;
			return;
		}

		this.version = getSubValues(version);
	}

	/**
	 * @param version
	 *            string version, eg 4.9.38.0
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
	 * @return euclidian view; if not present yet, new one is created
	 */
	public abstract EuclidianView createEuclidianView();

	/**
	 * Returns current mode (tool number)
	 * 
	 * @return current mode
	 */
	final public int getMode() {
		return this.createEuclidianView().getMode();
	}

	/**
	 * Returns labeling style for newly created geos
	 * 
	 * @return labeling style; AUTOMATIC is resovled either to US_DEFAULTS or
	 *         OFF depending on visibility of AV
	 */
	public int getCurrentLabelingStyle() {
		if (getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC) {
			if (isUsingFullGui()) {
				if ((getGuiManager() != null)
						&& getGuiManager().hasAlgebraViewShowing()) {
					return getAlgebraView().isVisible() ? ConstructionDefaults.LABEL_VISIBLE_USE_DEFAULTS
							: ConstructionDefaults.LABEL_VISIBLE_ALWAYS_OFF;
				}
				return ConstructionDefaults.LABEL_VISIBLE_ALWAYS_OFF;
			}
			return ConstructionDefaults.LABEL_VISIBLE_USE_DEFAULTS;
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
	public abstract GBufferedImage getExternalImageAdapter(String filename);

	

	



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
	 * @param b
	 */
	public void setScrollToShow(boolean b) {
		// TODO Auto-generated method stub

	}

	public void setUnsaved() {
		// TODO Auto-generated method stub

	}

	/**
	 * Makes given view active
	 * 
	 * @param evID
	 *            view id
	 */
	public void setActiveView(int evID) {
		// TODO Auto-generated method stub

	}

	public void refreshViews() {
		getEuclidianView1().updateBackground();
		if (hasEuclidianView2()) {
			getEuclidianView2().updateBackground();
		}
		kernel.notifyRepaint();
	}

	/**
	 * Switches the application to macro editing mode
	 * 
	 * @author Zbynek Konecny
	 * @version 2010-05-26
	 * @param editMacro
	 *            Tool to be edited
	 */
	public void openMacro(Macro editMacro) {
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

	private Macro macro;

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
	 * @return XML for or macros or empty string if there are none
	 */
	public String getMacroXMLorEmpty() {
		if (!kernel.hasMacros())
			return "";
		ArrayList<Macro> macros = kernel.getAllMacros();
		if (macros.isEmpty())
			return "";
		return getXMLio().getFullMacroXML(macros);
	}

	private int labelingStyle = ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY;

	public boolean hasEuclidianView2() {
		// TODO Auto-generated method stub
		return false;
	}

	public abstract void showError(MyError e);

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
		case VIEW_ALGEBRA:
			return getAlgebraView();
		case VIEW_SPREADSHEET:
			if (!isUsingFullGui())
				return null;
			else if (getGuiManager() == null)
				initGuiManager();
			return getGuiManager().getSpreadsheetView();
		case VIEW_CAS:
			if (!isUsingFullGui())
				return null;
			else if (getGuiManager() == null)
				initGuiManager();
			return getGuiManager().getCasView();
		case VIEW_EUCLIDIAN2:
			return hasEuclidianView2() ? getEuclidianView2() : null;
		case VIEW_CONSTRUCTION_PROTOCOL:
			if (!isUsingFullGui())
				return null;
			else if (getGuiManager() == null)
				initGuiManager();
			return getGuiManager().getConstructionProtocolData();
		case VIEW_PROBABILITY_CALCULATOR:
			if (!isUsingFullGui())
				return null;
			else if (getGuiManager() == null)
				initGuiManager();
			return getGuiManager().getProbabilityCalculator();
		case VIEW_DATA_ANALYSIS:
			if (!isUsingFullGui())
				return null;
			else if (getGuiManager() == null)
				initGuiManager();
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
		if (hasEuclidianView2()) {
			getEuclidianView2().getXML(sb, asPreference);
		} else if (asPreference && (getGuiManager() != null)) {
			// TODO: After the implementing of getEuclidianView2() on web remove
			// the nullcheck from here
			// getEuclidianView2().getXML(sb, true);
			EuclidianView ev2 = getEuclidianView2();
			if (ev2 != null)
				ev2.getXML(sb, true);
		}

		if (getGuiManager() != null) {
			// save spreadsheetView settings
			if (getGuiManager().hasSpreadsheetView()) {
				getGuiManager().getSpreadsheetViewXML(sb, asPreference);
			}

			// save AlgebraView settings
			// if (getGuiManager().hasAlgebraView()){
			// getGuiManager().getAlgebraViewXML(sb);
			// }

			// save ProbabilityCalculator settings
			if (getGuiManager().hasProbabilityCalculator()) {
				getGuiManager().getProbabilityCalculatorXML(sb);
			}
			
			// save AlgebraView settings
			if (getGuiManager().hasAlgebraView()) {
				getGuiManager().getAlgebraViewXML(sb, asPreference);
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

	public abstract String getUniqueId();

	public abstract void setUniqueId(String uniqueId);

	public abstract void resetUniqueId();

	/**
	 * @param auxiliaryObjects
	 *            true to show Auxiliary objects
	 */
	public void setShowAuxiliaryObjects(boolean auxiliaryObjects) {
		// TODO Auto-generated method stub
		showAuxiliaryObjects = auxiliaryObjects;

		if (getGuiManager() != null) {
			getGuiManager().setShowAuxiliaryObjects(auxiliaryObjects);
			// updateMenubar();
		}
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

	private boolean scriptingDisabled = false;

	// default changed for ggb42 (consistent with the rest of the sotfware
	// world)
	private boolean reverseMouseWheel = true;

	/**
	 * @return true for scroll up = zoom in
	 */
	public boolean isMouseWheelReversed() {
		return reverseMouseWheel;
	}

	/**
	 * @param b
	 *            true for normal scrolling (scrol up = zoom in), false for
	 *            oposite setting
	 */
	public void reverseMouseWheel(boolean b) {
		reverseMouseWheel = b;
	}

	/**
	 * @param size
	 *            preferred size
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
	 * @param ttl
	 *            tooltip language
	 */
	public void setTooltipLanguage(String ttl) {
		// TODO Auto-generated method stub

	}

	public abstract DrawEquation getDrawEquation();

	/**
	 * 
	 * @param show
	 *            true to show navigation bar
	 */
	public void setShowConstructionProtocolNavigation(boolean show) {
		// TODO Auto-generated method stub

	}

	protected ArrayList<Perspective> tmpPerspectives = new ArrayList<Perspective>();

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

	public ArrayList<Perspective> getTmpPerspectives() {
		return tmpPerspectives;
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
	public abstract void setShowConstructionProtocolNavigation(boolean show,
			boolean playButton, double playDelay, boolean showProtButton);

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
	 * @return EV2
	 */
	public EuclidianView getEuclidianView2() {
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
		// TODO Auto-generated method stub
		return null;
	}

	public abstract GFont getPlainFontCommon();

	public boolean isExporting() {
		return exporting;
	}

	/** whether toolbar should be visible */
	protected boolean showToolBar = true;

	public void setShowToolBarNoUpdate(boolean toolbar) {
		showToolBar = toolbar;
	}
	
	public void setShowToolBarHelpNoUpdate(boolean toolbarHelp) {
		showToolBarHelp = toolbarHelp;
	}
	
	public boolean showToolBar() {
		return showToolBar;
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
		if (showToolBar) {
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
	public void initKernel() {
		kernel = new Kernel(this);
		selection = new SelectionManager(kernel,this);
	}

	/**
	 * init the EuclidianView (and EuclidianView3D for 3D)
	 */
	public void initEuclidianViews() {

		euclidianController = newEuclidianController(kernel);
		euclidianView = newEuclidianView(showAxes, showGrid);
		euclidianView.setAntialiasing(antialiasing);
	}

	abstract protected EuclidianView newEuclidianView(boolean[] showAxes1,
			boolean showGrid1);

	abstract protected EuclidianController newEuclidianController(Kernel kernel1);

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
	public abstract AnimationManager newAnimationManager(Kernel kernel2);

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
	 * @return spreadsheet trace manager
	 */
	final public SpreadsheetTraceManager getTraceManager() {
		if (traceManager == null)
			traceManager = new SpreadsheetTraceManager(this);
		return traceManager;
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
	 * @return spreadsheet table model
	 */
	public abstract SpreadsheetTableModel getSpreadsheetTableModel();

	/**
	 * Changes current mode (tool number)
	 * 
	 * @param mode
	 *            new mode
	 */
	public void setMode(int mode,ModeSetter m) {
		if (mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
			currentSelectionListener = null;
		}

		if (getGuiManager() != null) {
			getGuiManager().setMode(mode,m);
		} else if (euclidianView != null) {
			euclidianView.setMode(mode,m);
		}
	}
	
	public void setMode(int mode) {
		setMode(mode,ModeSetter.TOOLBAR);
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
		return e.isRightClick();
	}

	/**
	 * @param e
	 *            event
	 * @return whether Ctrl on Win/Linux or Meta on Mac was pressed
	 */
	public boolean isControlDown(AbstractEvent e) {
		return e.isControlDown();
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

	public abstract void evalPythonScript(App app, String string, String arg);

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

	private PropertiesView propertiesView;
	/** whether shift, drag and zoom features are enabled */
	protected boolean shiftDragZoomEnabled = true;

	

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

		if (getGuiManager() != null && showMenuBar)
			getGuiManager().updateMenubarSelection();

		// if showMenuBar is false, we can still update the style bars
		if (getActiveEuclidianView().getMode() == EuclidianConstants.MODE_MOVE) {
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
	 * @param shiftDragZoomEnabled
	 *            whether shift, drag and zoom features are enabled
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
	 * Changes font size and resets fonts
	 * 
	 * @see #resetFonts()
	 * @param points
	 *            font size
	 */
	public void setFontSize(int points) {
		setFontSize(points, true);
	}

	/**
	 * Changes font size and possibly resets fonts
	 * 
	 * @param points
	 *            font size
	 * @param update
	 *            whether fonts should be reset
	 * @see #resetFonts()
	 */
	public void setFontSize(int points, boolean update) {
		if (points == appFontSize) {
			return;
		}
		appFontSize = points;
		// isSaved = false;
		if (!update) {
			return;
		}

		resetFonts();

		// updateUI();
	}

	/**
	 * Recursively update all components with current look and feel
	 */
	public abstract void updateUI();

	

	/**
	 * Update font sizes of all components to match current GUI font size
	 */
	public void resetFonts() {
		getFontManager().setFontSize(getGUIFontSize());
		if (euclidianView != null) {
			euclidianView.updateFonts();
		}

		if (getGuiManager() != null) {
			getGuiManager().updateFonts();
			if (hasEuclidianView2()) {
				getEuclidianView2().updateFonts();
			}
		}
	}

	/**
	 * @return font size for GUI; if not specified, general font size is
	 *         returned
	 */
	public int getGUIFontSize() {
		return guiFontSize == -1 ? getFontSize() : guiFontSize;
	}

	/**
	 * @param size
	 *            GUI font size
	 */
	public void setGUIFontSize(int size) {
		guiFontSize = size;
		// updateFonts();
		// isSaved = false;

		resetFonts();

		updateUI();
	}

	/**
	 * Returns font manager
	 * 
	 */
	protected abstract FontManager getFontManager();

	/**
	 * Returns a font that can display testString in plain sans-serif font and
	 * current font size.
	 * 
	 * @param testString
	 *            test string
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
		return getFontManager().getFontCanDisplay(testString, serif, fontStyle,
				fontSize);
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
		// just save mouse settings as preference
		if (asPreference) {
			sb.append("\t<mouse reverseWheel=\"");
			sb.append(isMouseWheelReversed());
			sb.append("\"/>\n");
		}

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

		if (!asPreference) {
			sb.append("\t<graphicsSettings");
			sb.append(" javaLatexFonts=\"");
			sb.append(useJavaFontsForLaTeX());
			sb.append("\"/>\n");
		}

		getConsProtocolXML(sb);

		sb.append("</gui>\n");

		return sb.toString();
	}

	/**
	 * Appends construction protocol view settings in XML format
	 * 
	 * @param sb
	 *            string builder
	 */
	public void getConsProtocolXML(StringBuilder sb) {
		if (getGuiManager() == null) {
			return;
		}

		// construction protocol
		if (getGuiManager().isUsingConstructionProtocol()) {
			getGuiManager().getConsProtocolXML(sb);
		}
	}

	

	/**
	 * Appends layout settings in XML format to given builder
	 * 
	 * @param sb
	 *            string builder
	 * @param asPreference
	 *            whether this is for preferences
	 */
	protected abstract void getWindowLayoutXML(StringBuilder sb,
			boolean asPreference);

	public abstract void reset();

	public abstract PythonBridge getPythonBridge();

	// public abstract String getCurrentPythonScript();

	

	/**
	 * @return selection listener
	 */
	public GeoElementSelectionListener getCurrentSelectionListener() {
		return currentSelectionListener;
	}

	/**
	 * @param sl
	 *            selection listener
	 */
	public void setCurrentSelectionListener(GeoElementSelectionListener sl) {
		currentSelectionListener = sl;
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
	 * @return whether we are running in HTML5 applet
	 */
	public abstract boolean isHTML5Applet();

	/**
	 * @param isOnTheFlyPointCreationActive
	 *            Whether points can be created on the fly
	 */
	public final void setOnTheFlyPointCreationActive(
			boolean isOnTheFlyPointCreationActive) {
		this.isOnTheFlyPointCreationActive = isOnTheFlyPointCreationActive;
	}

	/** whether transparent cursor should be used while dragging */
	public boolean useTransparentCursorWhenDragging = false;
	protected int dataParamWidth = 0;
	protected int dataParamHeight = 0;
	protected boolean useFullGui = false;

	/**
	 * @param useTransparentCursorWhenDragging
	 *            whether transparent cursor should be used while dragging
	 */
	public void setUseTransparentCursorWhenDragging(
			boolean useTransparentCursorWhenDragging) {
		this.useTransparentCursorWhenDragging = useTransparentCursorWhenDragging;
	}

	/**
	 * eg StringType.LATEX for desktop (JLaTeXMath) StringType.MATHML for web
	 * (canvasmath)
	 * 
	 * eg AlgoFraction can output to LaTeX or MathML
	 * so can FormulaText[sin(x/2)]
	 * but for text, eg FormulaText["\frac{a}{b}"], FormulaText always needs a LaTeX renderer 
	 * 
	 * @return string type for fomulas (LATEX, MATHML)
	 */
	public abstract StringType getPreferredFormulaRenderingType();

	public void doAfterRedefine(GeoElement geo) {
		if (getGuiManager() != null) {
			getGuiManager().doAfterRedefine(geo);
		}
	}

	/**
	 * @return string representation of current locale, eg no_NO_NY
	 */
	public abstract String getLocaleStr();

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

	public void setUndoActive(boolean undoActive) {
		boolean flag = undoActive;
		// don't allow undo when running with restricted permissions
		if (flag && !hasFullPermissions) {
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

		isSaved = true;
	}

	
	private final StringBuilder sbPlain = new StringBuilder();
	protected static boolean useFullAppGui = false;

	

	public static boolean isFullAppGui() {
		return useFullAppGui;
	}

	protected int appCanvasHeight;
	protected int appCanvasWidth;

	public int getAppCanvasWidth() {
		return appCanvasWidth;
	}

	public int getAppCanvasHeight() {
		return appCanvasHeight;
	}

	/**
	 * Returns image with given filename
	 * 
	 * @param filename
	 *            filename
	 * @return null unless overriden
	 */
	public GImage getInternalImageAdapter(String filename) {
		return null;
	}

	/**
	 * @return whether input bar should be on top
	 */
	public boolean showInputTop() {
		return showInputTop;
	}

	/**
	 * Changes input position between bottom and top
	 * 
	 * @param flag
	 *            whether input should be on top
	 * @param update
	 *            whether layout update is needed afterwards
	 */
	public void setShowInputTop(boolean flag, boolean update) {
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
		if (showInputHelpToggle == flag) {
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
	private String getToolNameOrHelp(int mode, boolean toolName) {
		// macro
		String ret;

		if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
			// MACRO
			int macroID = mode - EuclidianConstants.MACRO_MODE_ID_OFFSET;
			try {
				Macro macro1 = kernel.getMacro(macroID);
				if (toolName) {
					// TOOL NAME
					ret = macro1.getToolName();
					if ("".equals(ret)) {
						ret = macro1.getCommandName();
					}
				} else {
					// TOOL HELP
					ret = macro1.getToolHelp();
					if ("".equals(ret)) {
						ret = macro1.getNeededTypesString();
					}
				}
			} catch (Exception e) {
				App.debug("Application.getModeText(): macro does not exist: ID = "
						+ macroID);
				// e.printStackTrace();
				return "";
			}
		} else {
			// STANDARD TOOL
			String modeText = getKernel().getModeText(mode);
			if (toolName) {
				// tool name
				ret = getMenu(modeText);
			} else {
				// tool help
				ret = getMenu(modeText + ".Help");
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
	 * Translates function name for which plain bundle contains corresponding
	 * Function.* key
	 * 
	 * @param string
	 *            english function name
	 * @return localized function name
	 */
	public String getFunction(String string) {
		return getPlain("Function." + string);
	}

	/**
	 * @return parser extension for functions
	 */
	public ParserFunctions getParserFunctions() {
		return pf;
	}

	/**
	 * Clears construction
	 * @return true if successful otherwise false (eg user clicks "Cancel")
	 */
	public abstract boolean clearConstruction();

	/**
	 * create a new GeoGebra window
	 */
	public abstract void createNewWindow();
	
	public abstract void fileNew();

	/**
	 * @return country nme from GeoIP service
	 * @throws Exception
	 *             when GeoIP can't be reached
	 */
	public abstract String getCountryFromGeoIP() throws Exception;

	private Random random = new Random();
	private GeoScriptRunner geoScriptRunner;

	/**
	 * allows use of seeds to generate the same sequence for a ggb file
	 * 
	 * @return random number in [0,1]
	 */
	public double getRandomNumber() {
		return random.nextDouble();
	}

	/**
	 * allows use of seeds to generate the same sequence for a ggb file
	 * 
	 * @param low
	 *            least possible value of result
	 * @param high
	 *            highest possible value of result
	 * 
	 * @return random integer between a and b inclusive
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

	public abstract void exportToLMS(boolean b);

	public abstract void copyGraphicsViewToClipboard();

	/**
	 * Resets active EV to standard
	 */
	public final void setStandardView() {
		getActiveEuclidianView().setStandardView(true);
	}

	public abstract void exitAll();

	public abstract void addMenuItem(MenuInterface parentMenu, String filename,
			String name, boolean asHtml, MenuInterface subMenu);

	public String getVersionString() {
		return GeoGebraConstants.VERSION_STRING;
	}

	public abstract NormalizerMinimal getNormalizer();

	public String getEmptyIconFileName() {
		return "empty.gif";
	}

	public final void zoom(double px, double py, double zoomFactor) {
		getGuiManager().getActiveEuclidianView().zoom(px, py, zoomFactor, 15,
				true);
	}

	/**
	 * Sets the ratio between the scales of y-axis and x-axis, i.e. ratio =
	 * yscale / xscale;
	 * 
	 * @param axesratio
	 *            axes scale ratio
	 */
	public final void zoomAxesRatio(double axesratio) {
		getGuiManager().getActiveEuclidianView().zoomAxesRatio(axesratio, true);
	}

	/**
	 * Zooms and pans active EV to show all objects
	 */
	public final void setViewShowAllObjects() {
		getGuiManager().getActiveEuclidianView().setViewShowAllObjects(true);
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
	 * @return whether right click features are enabled
	 */
	final public boolean isRightClickEnabled() {
		return rightClickEnabled;
	}

	/**
	 * @return whether context menu is enabled
	 */
	public boolean letShowPopupMenu() {
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
	 * @param geo1
	 *            geo
	 * @param string
	 *            parameter (for input box scripts)
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

	public void showRelation(GeoElement a, GeoElement b) {
		GOptionPane optionPane = getFactory().newGOptionPane();
		optionPane.showConfirmDialog(getMainComponent(),
				new Relation(kernel).relation(a, b),
				getPlain("ApplicationName") + " - " + getLocalization().getCommand("Relation"),
				GOptionPane.DEFAULT_OPTION, GOptionPane.INFORMATION_MESSAGE);

	}

	protected abstract Object getMainComponent();

	private GeoElement geoForCopyStyle;
	private OptionsMenu optionsMenu;

	public GeoElement getGeoForCopyStyle() {
		return geoForCopyStyle;
	}

	public void setGeoForCopyStyle(GeoElement geo) {
		geoForCopyStyle = geo;
	}

	public abstract CASFactory getCASFactory();

	public abstract SwingFactory getSwingFactory();

	public abstract Factory getFactory();

	public void dispatchEvent(Event evt) {
		getEventDispatcher().dispatchEvent(evt);
	}
	
	public OptionsMenu getOptionsMenu() {
		
		if (optionsMenu == null) {
			optionsMenu = new OptionsMenu(this);
		}
		return optionsMenu;
	}

	public boolean hasOptionsMenu() {
		return optionsMenu != null;
	}

	public abstract MyXMLio getXMLio();
	public abstract MyXMLio createXMLio(Construction cons);

	/**
	 * reset ids for 2D view created by planes, etc. Used in 3D.
	 */
	public void resetEuclidianViewForPlaneIds() {
		// used in 3D
		
	}
	
	/**
	 * store view creators (for undo)
	 */
	public void storeViewCreators(){
		// used in 3D
	}
	
	/**
	 * recall view creators (for undo)
	 */
	public void recallViewCreators(){
		// used in 3D
	}
	
	public boolean hasEventDispatcher(){
		return eventDispatcher != null;
	}

	/**
	 * This should not be used, just overriden in AppW
	 */
	public void scheduleUpdateConstruction() {
		kernel.getConstruction().updateConstruction();
		kernel.notifyRepaint();
	}

	public void setShowAlgebraInput(boolean flag, boolean update) {
		showAlgebraInput = flag;
	
		if (update) {
			updateApplicationLayout();
			updateMenubar();
		}
	}	
	
	protected boolean needsSpreadsheetTableModel = false;

	public void setNeedsSpreadsheetTableModel(){
		needsSpreadsheetTableModel = true;
	}
	
	public boolean needsSpreadsheetTableModel(){
		return needsSpreadsheetTableModel;
	}
	
	public void setDataParamWidth(int width) {
		this.dataParamWidth = width;
	}

	public void setDataParamHeight(int height) {
		this.dataParamHeight = height;
	}

	public int getDataParamWidth() {
		return dataParamWidth;
	}

	public int getDataParamHeight() {
		return dataParamHeight;
	}
	
	public void startCollectingRepaints(){
		getEuclidianView1().getEuclidianController().startCollectingMinorRepaints();
	}
	
	public void stopCollectingRepaints(){
		getEuclidianView1().getEuclidianController().stopCollectingMinorRepaints();
	}
	
	public abstract Localization getLocalization();
	
	public String getMenu(String key){
		return getLocalization().getMenu(key);
	}
	
	public String getPlain(String key){
		return getLocalization().getPlain(key);
	}
	
	public String getPlainTooltip(String key){
		return getLocalization().getPlainTooltip(key);
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
	 * CAS type
	 * @return type eg GIAC, MPREDUCE
	 */
	public abstract CasType getCASType();
}
