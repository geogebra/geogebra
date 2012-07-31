package geogebra.common.main;

import geogebra.common.GeoGebraConstants;
import geogebra.common.awt.GBufferedImage;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GImage;
import geogebra.common.cas.singularws.SingularWebService;
import geogebra.common.euclidian.DrawEquationInterface;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.dialog.DialogManager;
import geogebra.common.gui.menubar.MenuInterface;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.gui.view.properties.PropertiesView;
import geogebra.common.gui.view.spreadsheet.SpreadsheetTableModel;
import geogebra.common.gui.view.spreadsheet.SpreadsheetTraceManager;
import geogebra.common.io.MyXMLio;
import geogebra.common.io.layout.Perspective;
import geogebra.common.kernel.AnimationManager;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.UndoManager;
import geogebra.common.kernel.View;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.cas.GeoGebraCasInterface;
import geogebra.common.kernel.commands.CommandDispatcher;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.commands.CommandsConstants;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.parser.cashandlers.ParserFunctions;
import geogebra.common.main.settings.Settings;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.GgbAPI;
import geogebra.common.plugin.ScriptManagerCommon;
import geogebra.common.plugin.jython.PythonBridge;
import geogebra.common.sound.SoundManager;
import geogebra.common.util.AbstractImageManager;
import geogebra.common.util.GeoGebraLogger;
import geogebra.common.util.LowerCaseDictionary;
import geogebra.common.util.NormalizerMinimal;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public abstract class App {
	/** Script manager */
	protected ScriptManagerCommon scriptManager = null;	
	/** True when we are running standalone app or signed applet, false for unsigned applet*/
	protected static boolean hasFullPermissions = false;
	/** whether current construction was saved after last changes*/
	protected boolean isSaved = true;
	/** Url for wiki article about functions*/
	public static final String WIKI_OPERATORS = "Predefined Functions and Operators";
	/** Url for main page of manual*/
	public static final String WIKI_MANUAL = "Manual:Main Page";
	/** Url for wiki article about CAS*/
	public static final String WIKI_CAS_VIEW = "CAS_View";
	/** Url for wiki tutorials*/
	public static final String WIKI_TUTORIAL = "Tutorial:Main Page";
	/** Url for wiki article about exporting to HTML*/
	public static final String WIKI_EXPORT_WORKSHEET = "Export_Worksheet_Dialog";
	/** Url for wiki article about advanced features (layers, cond. visibility etc.)*/
	public static final String WIKI_ADVANCED = "Advanced Features";
	/** Url for wiki article about functions*/
	public static final String WIKI_TEXT_TOOL = "Insert Text Tool";

	public static final int VIEW_NONE = 0;
	/** id for euclidian view */
	public static final int VIEW_EUCLIDIAN = 1;
	/** id for algebra view */
	public static final int VIEW_ALGEBRA = 2;
	/** id for Spreadsheet view*/
	public static final int VIEW_SPREADSHEET = 4;
	/** id for CAS view */
	public static final int VIEW_CAS = 8;
	/** id for second euclidian view */
	public static final int VIEW_EUCLIDIAN2 = 16;
	/** id for construction protocol view */
	public static final int VIEW_CONSTRUCTION_PROTOCOL = 32;
	/** id for probability calculator view*/
	public static final int VIEW_PROBABILITY_CALCULATOR = 64;
	/** id for data analysis view, ie multi/single/two variable analysisis tools*/
	public static final int VIEW_DATA_ANALYSIS = 70;
	/** id for function inspector */ 
	public static final int VIEW_FUNCTION_INSPECTOR = 128;	
	public static final int VIEW_INSPECTOR = 256;
	/** id for 3D view */
	public static final int VIEW_EUCLIDIAN3D = 512;
	/** id for view created from plane; also 1025 to 2047 might be used for this purpose*/
	public static final int VIEW_EUCLIDIAN_FOR_PLANE = 1024;
	//please let 1024 to 2047 empty
	public static final int VIEW_PLOT_PANEL = 2048;
	/** id for text preview in text tool */
	public static final int VIEW_TEXT_PREVIEW = 4096;
	/** id for properties view */
	public static final int VIEW_PROPERTIES = 4097;
	/** id for assignment view*/
	public static final int VIEW_ASSIGNMENT = 8192;
	/** id for spreadsheet table model */
	public static final int VIEW_TABLE_MODEL = 9000;
	public static final int VIEW_PYTHON = 16384;
	private boolean showResetIcon = false;
	/**
	 * Whether we are running applet in frame. Not possible with 4.2+
	 * (we need this to hide reset icon from EV)
	 */
	public boolean runningInFrame = false; 
	private ParserFunctions pf = new ParserFunctions();
	
	private SpreadsheetTraceManager traceManager;

	/**
	 *  object is hit if mouse is within this many pixels
	 *  (more for points, see geogebra.common.euclidian.DrawPoint)
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
	 * Whether input help toggle button should be visible
	 */
	protected boolean showInputHelpToggle = true;

	/**
	 * Whether AV should show auxiliary objects
	 * stored here rather than in algebra view so that it can be set without
	 * creating an AV (compatibility with 3.2)
	 */
	public boolean showAuxiliaryObjects = false;
	/** whether righ click is enabled */
	protected boolean rightClickEnabled = true;

	/** flag to test whether to draw Equations full resolution */
	public boolean exporting = false;

	private static String CASVersionString = "";

	/**
	 * @param string CAS version string
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
	
	
	
    /*
     * Prover settings (see handleHelpVersionArgs for details)
     */
    public static String proverEngine = "Auto"; // Later: "auto"
    public static int proverTimeout = 5;
    public static int maxTerms = 10000;
    public static String proverMethod = "Wu";
    public static boolean freePointsNeverCollinear = true;
    public static boolean useFixCoordinates = true;
    public static boolean useSingularWebService = true;
    public static String singularWebServiceRemoteURL = "http://ggb1.idm.jku.at:8085/"; // use another port later
    public static int singularWebServiceTimeout = 5;

	public MyXMLio myXMLio;

	/* Font settings */
	/** minimal font size */
	public static final int MIN_FONT_SIZE = 10;
	// gui / menu fontsize (-1 = use appFontSize)
	private int guiFontSize = -1;
	// currently used application fonts
	private int appFontSize;
	// note: It is not necessary to use powers of 2 for view IDs

	// For eg Hebrew and Arabic.
	public static char unicodeDecimalPoint = '.';
	public static char unicodeComma = ','; // \u060c for Arabic comma
	public static char unicodeZero = '0';

	public enum CasType {
		NO_CAS, MATHPIPER, MAXIMA, MPREDUCE
	}

	// moved to Application from EuclidianView as the same value is used across
	// multiple EVs
	public int maxLayerUsed = 0;
	public int booleanSize = 13;
	public int rightAngleStyle = EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE;

	public boolean useJavaFontsForLaTeX = false;

	protected final ArrayList<GeoElement> selectedGeos = new ArrayList<GeoElement>();

	public Kernel kernel;

	protected boolean isOnTheFlyPointCreationActive = true;
	/** Settings object */
	protected Settings settings;
	/**
	 * @return whether Java fonts shall be used by JLatexMath (no effect in Web) 
	 * */
	public boolean useJavaFontsForLaTeX() {
		return useJavaFontsForLaTeX;

	}
	/** whether we should use antialisaing in EV */
	protected boolean antialiasing = true;
	/** whether axes should be visible when EV is created 
	 * first element of this array is for x-axis, second for y-axis*/
	protected final boolean[] showAxes = { true, true };
	/** whether grid should be visible when EV is created */
	protected boolean showGrid = false;
	/** this flag is true during initialization phase (until GUI is built and command line args handled, incl. file loading)
	 *  or when we are opening a file */
	protected boolean initing = false;
	
	private boolean labelDragsEnabled = true;
	/** initial number of columns for spreadsheet */
	public static final int SPREADSHEET_INI_COLS = 26;
	/** initial number of rows for spreadsheet */
	public static final int SPREADSHEET_INI_ROWS = 100;
	
	private HashMap<String, String> translateCommandTable,
	translateCommandTableScripting;
	// command dictionary
	private LowerCaseDictionary commandDict;
	private LowerCaseDictionary commandDictCAS;
	
	protected EuclidianView euclidianView;
	protected EuclidianController euclidianController;
	protected GeoElementSelectionListener currentSelectionListener;
	protected boolean showMenuBar = true;
	// array of dictionaries corresponding to the sub command tables
	private LowerCaseDictionary[] subCommandDict;
	
	private String oldScriptLanguage = null;

	private String scriptingLanguage;

	private void fillCommandDictScripting() {
		if ((scriptingLanguage == null)
				|| scriptingLanguage.equals(oldScriptLanguage)
				|| "null".equals(scriptingLanguage)) {
			return;
		}
		oldScriptLanguage = scriptingLanguage;
		initScriptingBundle();

		// translation table for all command names in command.properties
		if (translateCommandTableScripting == null) {
			translateCommandTableScripting = new HashMap<String, String>();
		}

		// command dictionary for all public command names available in
		// GeoGebra's input field

		translateCommandTableScripting.clear();


		for (Commands comm:Commands.values()) {
			String internal = comm.toString();
			if (tableVisible(comm.getTable())) {
				String local = getScriptingCommand(internal);
				if (local != null) {
					local = local.trim();
					// case is ignored in translating local command names to
					// internal names!
					translateCommandTableScripting.put(StringUtil.toLowerCase(local),
							internal);
					debug(StringUtil.toLowerCase(local));

				}
			}
		}

	}
	/**
	 * @return whether language of command bundle changed since we last updated
	 *  translation table and directories
	 */
	protected abstract boolean isCommandChanged();
	/**
	 * @param b whether language of command bundle changed since we last updated 
	 * translation table and directories
	 */
	protected abstract void setCommandChanged(boolean b);
	/**
	 * @return whether command translation bundle is null
	 */
	protected abstract boolean isCommandNull();
	/** CAS syntax suffix for keys in command bundle */
	public final static String syntaxCAS = ".SyntaxCAS";
	/** 3D syntax suffix for keys in command bundle */
	public final static String syntax3D = ".Syntax3D";
	/** syntax suffix for keys in command bundle */
	public final static String syntaxStr = ".Syntax";
	
	/**
	 * We need this method so that we can override it using more powerful normalizer
	 * @return new lowercase dictionary
	 */
	protected LowerCaseDictionary newLowerCaseDictionary(){
		return new LowerCaseDictionary(new NormalizerMinimal());
	}
	
	/**
	 * Fills CAS command dictionary and translation table.
	 * Must be called before we start using CAS view. 
	 */
	public void fillCasCommandDict() {
		// this method might get called during initialization, when we're not
		// yet
		// ready to fill the casCommandDict. In that case, we will fill the
		// dict during fillCommandDict :)

		if (!isCommandChanged()
				&& ((commandDictCAS != null) || isCommandNull())) {
			return;
		}		
		GeoGebraCasInterface cas = kernel.getGeoGebraCAS();
		if (cas == null) {
			return;
		}
		setCommandChanged(false);

		commandDictCAS = newLowerCaseDictionary();
		subCommandDict[CommandsConstants.TABLE_CAS].clear();
		// iterate through all available CAS commands, add them (translated if
		// available, otherwise untranslated)
		for (String cmd : cas.getAvailableCommandNames()) {
			translateCommandTable.put(StringUtil.toLowerCase(cmd), cmd);
			try {
				String local = getCommand(cmd);
				if (local != null) {
					translateCommandTable.put(StringUtil.toLowerCase(local), cmd);
					commandDictCAS.addEntry(local);
					subCommandDict[CommandsConstants.TABLE_CAS]
							.addEntry(local);
				} else {
					commandDictCAS.addEntry(cmd);
					subCommandDict[CommandsConstants.TABLE_CAS]
							.addEntry(cmd);
				}
			} catch (MissingResourceException mre) {
				commandDictCAS.addEntry(cmd);
				subCommandDict[CommandsConstants.TABLE_CAS]
						.addEntry(cmd);
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
	 * @return command dictionaries corresponding to the categories
	 */
	public final LowerCaseDictionary[] getSubCommandDictionary() {

		if (subCommandDict == null) {
			initTranslatedCommands();
		}
		if(isCommandChanged())
			updateCommandDictionary();

		return subCommandDict;
	}
	
	/**
	 * Initializes the translated command names for this application. Note: this
	 * will load the properties files first.
	 */
	final public void initTranslatedCommands() {
		if (isCommandNull() || subCommandDict == null) {
			initCommand();
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
	 * Initialize the command bundle 
	 * (not needed in Web)
	 */
	public abstract void initCommand();
	
	/**
	 * Fill command dictionary and translation table.
	 * Must be called before we start using Input Bar.
	 */
	protected void fillCommandDict() {
		initCommand();

		if (!isCommandChanged()) {
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


		Set<String> publicCommandNames = kernel.getAlgebraProcessor()
				.getPublicCommandSet();

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
			if(!tableVisible(comm.getTable())){
				if(comm.getTable()==CommandsConstants.TABLE_ENGLISH)
				translateCommandTable.put(StringUtil.toLowerCase(internal), 
						Commands.englishToInternal(comm).name());
				continue;			
			}
			translateCommandTable.put(StringUtil.toLowerCase(internal), internal);
			// App.debug(internal);
			String local = getCommand(internal);
			
			if (local != null) {
				local = local.trim();
				// case is ignored in translating local command names to
				// internal names!
				translateCommandTable.put(StringUtil.toLowerCase(local), internal);

				// only add public commands to the command dictionary
				if (publicCommandNames.contains(internal)) {
					commandDict.addEntry(local);
				}

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
		setCommandChanged(false);
	}
	/**
	 * return true if commands of this table should be visible in input bar help
	 * and autocomplete
	 * @param table table number, see CommandConstants.TABLE_*
	 * @return true for visible tables
	 */
	protected boolean tableVisible(int table) {
		return !(table==CommandsConstants.TABLE_3D || table ==CommandsConstants.TABLE_ENGLISH);
	}

	/**
	 * translate command name to internal name. Note: the case of localname is
	 * NOT relevant
	 * @param localname local name
	 * @return internal name
	 */
	final public String translateCommand(String localname) {
		if (localname == null) {
			return null;
		}
		if (translateCommandTable == null) {
			return localname;
		}
		debug(localname+":"+getScriptingLanguage());
		// note: lookup lower case of command name!
		String value = translateCommandTable.get(localname.toLowerCase());
		if (value == null) {
			fillCommandDictScripting();
			if (translateCommandTableScripting != null) {
				value = translateCommandTableScripting.get(localname
						.toLowerCase());
			}
		}
		if (value == null) {
			return localname;
		}
		return value;
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

	

	
	public abstract void initScriptingBundle();


	public abstract String getScriptingCommand(String internal);


	/**
	 * Gets translation from "command" bundle
	 * @param key key
	 * @return translation of given key
	 */
	public abstract String getCommand(String key);

	/**
	 * Gets translation from "plain" bundle
	 * @param key key
	 * @return translation of given key
	 */
	public abstract String getPlain(String key);

	/**
	 * 
	 * 
	 * only letters, numbers and _ allowed in label names
	 * check for other characters in the properties, and remove them
	 * @param key eg "poly" -> "Name.poly" -> poly -> poly1 as a label
	 * @return "poly" (the suffix is added later)
	 */
	final public String getPlainLabel(String key) {

		String ret = getPlain("Name."+key);

		for (int i = ret.length() - 1 ; i >= 0 ; i--) {
			if (!StringUtil.isLetterOrDigitOrUnderscore(ret.charAt(i))) {

				App.warn("Bad character in key: "+key+"="+ret);

				// remove bad character
				ret = ret.substring(0,  i) + ret.substring(i+1);

			}
		}

		return ret;

	}

	/**
	 * Returns translation of given key from the "menu" bundle
	 * @param key key
	 * @return translation for key
	 */
	public abstract String getMenu(String key);

	/**
	 * Returns translation of given key from the "error" bundle
	 * @param key key
	 * @return translation for key
	 */
	public abstract String getError(String key);
	/**
	 * Returns translation of given key from the "symbol" bundle
	 * @param key key (either "S.1", "S.2", ... for symbols or "T.1", "T.2" ... for tooltips)
	 * @return translation for key
	 */
	public abstract String getSymbol(int key);
	/**
	 * Returns translation of given key from the "symbol" bundle in tooltip language
	 * @param key key (either "S.1", "S.2", ... for symbols or "T.1", "T.2" ... for tooltips)
	 * @return translation for key in tooltip language
	 */
	public abstract String getSymbolTooltip(int key);



	public abstract void setTooltipFlag();

	public abstract boolean isApplet();

	/**
	 * Store current state of construction for undo/redo purposes
	 */
	public abstract void storeUndoInfo();

	/**
	 * @return true if we have access to complete gui (menubar, toolbar);
	 * false for minimal applets (just one EV, no gui)
	 */
	public abstract boolean isUsingFullGui();

	public abstract boolean showView(int view);

	/** 
	 *  
	 * @return 2 letter language name, eg "en" 
	 */ 
	public abstract String getLanguage();

	/**
	 * @param lang two letter language name
	 * @return whether we are currently using given language
	 */
	public boolean languageIs(String lang) {
		return getLanguage().equals(lang);
	}

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
	 * In some languages, a properties file cannot completely describe
	 * translations. This method tries to rewrite a text to the correct form.
	 * 
	 * @param text
	 *            the translation text to fix
	 * @return text the fixed text
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 */
	public String translationFix(String text) {
		// Currently no other language is supported than Hungarian.
		String lang = getLanguage();
		if (!("hu".equals(lang))) {
			return text;
		}
		return translationFixHu(text);
	}

	/**
	 * Text fixer for the Hungarian language
	 * 
	 * @param text
	 *            the translation text to fix
	 * @return the fixed text
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 */

	private static String translationFixHu(String inputText) {
		String text=inputText;
		// Fixing affixes.

		// We assume that object names are usual object names like "P", "O_1"
		// etc.
		// FIXME: This will not work for longer object names, e.g. "X Triangle",
		// "mypoint". To solve this problem, we should check the whole word and
		// its vowels. Probably hunspell for JNA could help (but it can be
		// too big solution for us), http://dren.dk/hunspell.html.
		// TODO: The used method is not as fast as it could be, so speedup is
		// possible.
		String[] affixesList = { "-ra/-re", "-nak/-nek", "-ba/-be",
				"-ban/-ben", "-hoz/-hez", "-val/-vel" };
		String[] endE2 = { "10", "40", "50", "70", "90" };
		// FIXME: Numbers in endings which greater than 999 are not supported
		// yet.
		// Special endings for -val/-vel:
		String[] endO2 = { "00", "20", "30", "60", "80" };

		for (String affixes : affixesList) {
			int match;
			do {
				match = text.indexOf(affixes);
				// match > 0 can be assumed because an affix will not start the
				// text
				if ((match > -1) && (match > 0)) {
					// Affix found. Get the previous character.
					String prevChars = translationFixPronouncedPrevChars(text,
							match, 1);
					if (Unicode.translationFixHu_endE1.indexOf(prevChars) > -1) {
						text = translationFixHuAffixChange(text, match,
								affixes, "e", prevChars);
					} else if (Unicode.translationFixHu_endO1
							.indexOf(prevChars) > -1) {
						text = translationFixHuAffixChange(text, match,
								affixes, "o", prevChars);
					} else if (Unicode.translationFixHu_endOE1
							.indexOf(prevChars) > -1) {
						text = translationFixHuAffixChange(text, match,
								affixes, Unicode.translationFixHu_oe, prevChars);
					} else if (match > 1) {
						// Append the previous character.
						// TODO: This could be quicker: to add only the second
						// char beyond prevChars
						prevChars = translationFixPronouncedPrevChars(text,
								match, 2);
						boolean found2 = false;
						for (String last2fit : endE2) {
							if (!found2 && last2fit.equals(prevChars)) {
								text = translationFixHuAffixChange(text, match,
										affixes, "e", prevChars);
								found2 = true;
							}
						}

						// Special check for preparing -val/-vel:
						if (!found2) {
							for (String last2fit : endO2) {
								if (!found2 && last2fit.equals(prevChars)) {
									text = translationFixHuAffixChange(text,
											match, affixes, "o", prevChars);
									found2 = true;
								}
							}
						}

						if (!found2) {
							// Use heuristics:
							text = translationFixHuAffixChange(text, match,
									affixes, "o", prevChars);
						}

					} else {
						// Use heuristics:
						text = translationFixHuAffixChange(text, match,
								affixes, "o", prevChars);
					}
				}
			} while (match > -1);
		}

		return text;
	}
	
	/**
	 * Gets the previous "pronounced" characters from text before the match
	 * position for the given length. The returned text will be lowercased.
	 * 
	 * Example: translationFixPrevChars("ABC_{123}", 8, 4) gives "c123"
	 * 
	 * @param text
	 *            the text to pronounce
	 * @param match
	 *            starting position
	 * @param length
	 *            required length for the output
	 * @return lowercased output
	 */
	private static String translationFixPronouncedPrevChars(String text, int match,
			int length) {
		int pos = match;
		String rettext = "";
		int rettextlen = 0;
		String thisChar;
		String ignoredChars = "_{}";

		while ((rettextlen < length) && (pos > 0)) {
			thisChar = text.substring(pos - 1, pos);
			if (ignoredChars.indexOf(thisChar) == -1) {
				rettext = thisChar.toLowerCase() + rettext;
				rettextlen++;
			}
			pos--;
		}
		return rettext;
	}

	
	/**
	 * Changes a set of possible affixes to the right one
	 * 
	 * @param text
	 *            the text to be corrected
	 * @param match
	 *            starting position of possible change
	 * @param affixes
	 *            possible affixes to change
	 * @param affixForm
	 *            abbreviation for the change type ("o"/"a"/"e")
	 * @param prevChars
	 * @return the corrected text
	 */
	private static String translationFixHuAffixChange(String inputText, int match,
			String affixes, String affixForm, String prevChars) {
		String text = inputText;
		String replace = "";

		if ("-ra/-re".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "ra";
			} else {
				replace = "re";
			}
		} else if ("-nak/-nek".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "nak";
			} else {
				replace = "nek";
			}
		} else if ("-ba/-be".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "ba";
			} else {
				replace = "be";
			}
		} else if ("-ban/-ben".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "ban";
			} else {
				replace = "ben";
			}
		} else if ("-hoz/-hez".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "hoz";
			} else if ("e".equals(affixForm)) {
				replace = "hez";
			} else {
				replace = Unicode.translationFixHu_hoez;
			}
		} else if ("-val/-vel".equals(affixes)) {
			if ("a".equals(affixForm) || "o".equals(affixForm)) {
				replace = "val";
			} else {
				replace = "vel";
			}

			// Handling some special cases:
			if (prevChars.length() == 1) {
				// f-fel, l-lel etc.
				String sameChars = "flmnrs";
				// y-nal, 3-mal etc.
				String valVelFrom = sameChars + "y356789";
				String valVelTo = sameChars + "nmtttcc";
				int index = valVelFrom.indexOf(prevChars);
				if (index > -1) {
					replace = valVelTo.charAt(index) + replace.substring(1);
				} else {
					// x-szel, 1-gyel etc.
					String valVelFrom2 = "x14";
					String[] valVelTo2 = { "sz", "gy", "gy" };
					index = valVelFrom2.indexOf(prevChars);
					if (index > -1) {
						replace = valVelTo2[index] + replace.substring(1);
					}
				}
			} else if ((prevChars.length() == 2)
					&& prevChars.substring(1).equals("0")) {
				// (Currently the second part of the conditional is
				// unnecessary.)
				// 00-zal, 10-zel, 30-cal etc.
				// FIXME: A_{00}-val will be replaced to A_{00}-zal currently,
				// because we silently assume that 00 is preceeded by another
				// number.
				String valVelFrom = "013456789";
				String valVelTo = "zzcnnnnnn";
				int index = valVelFrom.indexOf(prevChars.charAt(0));
				if (index > -1) {
					replace = valVelTo.charAt(index) + replace.substring(1);
				} else {
					// 20-szal
					if (prevChars.charAt(0) == '2') {
						replace = "sz" + replace.substring(1);
					}
				}
			}
		}

		if ("".equals(replace)) {
			// No replace.
			return text;
		}
		int affixesLength = affixes.length();
		// Replace.
		text = text.substring(0, match) + "-" + replace
				+ text.substring(match + affixesLength);
		return text;
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
	 * Translates localized command name into internal
	 * TODO check whether this differs from translateCommand somehow and either document it
	 * or remove this method
	 * @param s localized command name
	 * @return internal command name
	 */
	public abstract String getInternalCommand(String s);

	/**
	 * Show error dialog wiith given text
	 * @param s error message
	 */
	public abstract void showError(String s);



	private boolean useBrowserForJavaScript = true;

	/**
	 * @param useBrowserForJavaScript desktop: determines whether Rhino will be used (false) or the browser (true)
	 * web: determines whether JS scripting allowed (true) or not (false)
	 */
	public void setUseBrowserForJavaScript(boolean useBrowserForJavaScript) {
		this.useBrowserForJavaScript = useBrowserForJavaScript;
	}

	/**
	 * @return desktop: determines whether Rhino will be used (false) or the browser (true)
	 * web: determines whether JS scripting allowed (true) or not (false)
	 */
	public boolean useBrowserForJavaScript() {
		return useBrowserForJavaScript;
	}

	final public void initJavaScriptViewWithoutJavascript() {
		getScriptManager().initJavaScriptViewWithoutJavascript();
	}

	/**
	 * @return script manager
	 */
	public abstract ScriptManagerCommon getScriptManager();

	//TODO: move following methods somewhere else
	/**
	 * @param ge geo
	 * @return trace-related XML elements
	 */
	final public String getTraceXML(GeoElement ge) {
		return getTraceManager().getTraceXML(ge);
	}
	
	/**
	 * Start tracing geo to spreadsheet
	 * @param ge geo
	 * 
	 */
	public void traceToSpreadsheet(GeoElement ge) {
		getTraceManager().traceToSpreadsheet(ge);
	}

	/**
	 * Reset tracing column for given geo
	 * @param ge geo
	 */
	public void resetTraceColumn(GeoElement ge) {
		getTraceManager().setNeedsColumnReset(ge, true);
	}

	/**
	 * Updates the counter of used layers
	 * @param layer layer to which last element was added
	 */
	public void updateMaxLayerUsed(int layer) {
		int newLayer=layer;
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
	
	private	String[] fontSizeStrings = null;

	/**
	 * @return localized strings describing font sizes (very small, smaall, ...)
	 */
	public String[] getFontSizeStrings() {
		if (fontSizeStrings == null) {
			fontSizeStrings = new String[] { getPlain("ExtraSmall"),
					getPlain("VerySmall"), getPlain("Small"),
					getPlain("Medium"), getPlain("Large"),
					getPlain("VeryLarge"), getPlain("ExtraLarge") };
		}

		return fontSizeStrings;
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

		public String[] getRoundingMenu() {
			String[] strDecimalSpaces = {
					getPlain("ADecimalPlaces", "0"),
					getPlain("ADecimalPlace", "1"),
					getPlain("ADecimalPlaces", "2"),
					getPlain("ADecimalPlaces", "3"),
					getPlain("ADecimalPlaces", "4"),
					getPlain("ADecimalPlaces", "5"),
					getPlain("ADecimalPlaces", "10"),
					getPlain("ADecimalPlaces", "15"),
					"---", // separator
					getPlain("ASignificantFigures", "3"),
					getPlain("ASignificantFigures", "5"),
					getPlain("ASignificantFigures", "10"),
					getPlain("ASignificantFigures", "15") };

			// zero is singular in eg French
			if (!isZeroPlural(getLanguage())) {
				strDecimalSpaces[0] = getPlain("ADecimalPlace", "0");
			}

			return strDecimalSpaces;
		}
		
		/**
		 * in French, zero is singular, eg 0 dcimale rather than 0 decimal places
		 * @param lang language code
		 * @return whether 0 is plural
		 */
		public boolean isZeroPlural(String lang) {
			if (lang.startsWith("fr")) {
				return false;
			}
			return true;
		}
		
		

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
				Object[] geos = getSelectedGeos().toArray();
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
	 * Clears selction and selects given geos.
	 * 
	 * @param geos geos
	 */
	final public void setSelectedGeos(ArrayList<GeoElement> geos) {
		clearSelectedGeos(false);
		if (geos != null) {
			for (int i = 0; i < geos.size(); i++) {
				GeoElement geo = geos.get(i);
				addSelectedGeo(geo, false, false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}
	
	/**
	 * Selects the first geo in the construction
	 */
	final public void setFirstGeoSelectedForPropertiesView(){
		GeoElement geo = getKernel().getFirstGeo();
		if (geo==null)
			return; 
		
		selectedGeos.add(geo);
		geo.setSelected(true);
		kernel.notifyRepaint();
		
		updateSelection(false);
		
	}

	/**
	 * Michael Borcherds 2008-03-03
	 * 
	 * @return -1 if nothing selected return -2 if
	 * objects from more than one layer selected return layer number if objects
	 * from exactly one layer are selected
	 */
	public int getSelectedLayer() {
		Object[] geos = getSelectedGeos().toArray();
		if (geos.length == 0)
			return -1; // return -1 if nothing selected

		int layer = ((GeoElement) geos[0]).getLayer();

		for (int i = 1; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (geo.getLayer() != layer)
				return -2; // return -2 if more than one layer selected
		}
		return layer;
	}
	
	/**
	 * Selects all geos in given layer
	 * @param layer 0 - 9 for particular layer, -1 for all layers
	 * (Michael Borcherds, 2008-03-03)
	 */
	final public void selectAll(int layer) {
		clearSelectedGeos(false);

		Iterator<GeoElement> it = kernel.getConstruction()
				.getGeoSetLabelOrder().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if ((layer == -1) || (geo.getLayer() == layer)) {
				addSelectedGeo(geo, false, false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	/**
	 * Select objects that were not selected so far and vice versa.
	 */
	final public void invertSelection() {

		Iterator<GeoElement> it = kernel.getConstruction()
				.getGeoSetLabelOrder().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (selectedGeos.contains(geo)) {
				removeSelectedGeo(geo, false);
			} else {
				addSelectedGeo(geo, false, false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	/**
	 * Select all predecessors of all selected geos
	 */
	final public void selectAllPredecessors() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			TreeSet<GeoElement> tree = geo.getAllPredecessors();
			Iterator<GeoElement> it2 = tree.iterator();
			while (it2.hasNext()) {
				addSelectedGeo(it2.next(), false, false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	/**
	 * Invert visibility of all selected objects
	 */
	final public void showHideSelection() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			geo.setEuclidianVisible(!geo.isEuclidianVisible());
			geo.updateVisualStyle();
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	/**
	 * Invert visibility of labels of all selected objects
	 */
	final public void showHideSelectionLabels() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			geo.setLabelVisible(!geo.isLabelVisible());
			geo.updateVisualStyle();
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	/**
	 * @return whether auxiliary objects are shown in AV
	 */
	public boolean showAuxiliaryObjects() {
		return showAuxiliaryObjects;
	}

	/**
	 * Selects descendants of all visible objects
	 */
	final public void selectAllDescendants() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			TreeSet<GeoElement> tree = geo.getAllChildren();
			Iterator<GeoElement> it2 = tree.iterator();
			while (it2.hasNext()) {
				addSelectedGeo(it2.next(), false, false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}
	
	/**
	 * Append XML describing the keyboard to given string builder
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

	public abstract long freeMemory();


	/**
	 * given 1, return eg 1st, 1e, 1:e according to the language
	 * 
	 * http://en.wikipedia.org/wiki/Ordinal_indicator
	 */
	StringBuilder sbOrdinal;

	/**
	 * given 1, return eg 1st, 1e, 1:e according to the language
	 * 
	 * http://en.wikipedia.org/wiki/Ordinal_indicator
	 * @param n number
	 * @return corresponding ordinal number
	 */
	public String getOrdinalNumber(int n) {
		String lang = getLanguage();
		
		if ("en".equals(lang)) return getOrdinalNumberEn(n);

		// check here for languages where 1st = 1
		if ("pt".equals(lang) || "ar".equals(lang) || "cy".equals(lang)
				|| "fa".equals(lang) || "ja".equals(lang) || "ko".equals(lang)
				|| "lt".equals(lang) || "mr".equals(lang) || "ms".equals(lang)
				|| "nl".equals(lang) || "si".equals(lang) || "th".equals(lang)
				|| "vi".equals(lang) || "zh".equals(lang)) {
			return n + "";
		}

		if (sbOrdinal == null) {
			sbOrdinal = new StringBuilder();
		} else {
			sbOrdinal.setLength(0);
		}

		// prefixes
		if ("in".equals(lang)) {
			sbOrdinal.append("ke-");
		} else if ("iw".equals(lang)) {
			// prefix and postfix for Hebrew
			sbOrdinal.append("\u200f\u200e");
		}

		sbOrdinal.append(n);

		if ("cs".equals(lang) || "da".equals(lang) || "et".equals(lang)
				|| "eu".equals(lang) || "hr".equals(lang) || "hu".equals(lang)
				|| "is".equals(lang) || "no".equals(lang) || "sk".equals(lang)
				|| "sr".equals(lang) || "tr".equals(lang)) {
			sbOrdinal.append('.');
		} else if ("de".equals(lang)) {
			sbOrdinal.append("th");
		} else if ("fi".equals(lang)) {
			sbOrdinal.append(":s");
		} else if ("el".equals(lang)) {
			sbOrdinal.append('\u03b7');
		} else if ("ro".equals(lang) || "es".equals(lang)
				|| "it".equals(lang) || "pt".equals(lang)) {
			sbOrdinal.append(Unicode.FEMININE_ORDINAL_INDICATOR);
		} else if ("bs".equals(lang) || "sl".equals(lang)) {
			sbOrdinal.append("-ti");
		} else if ("ca".equals(lang)) {
			
			switch (n) {
			// Catalan (masculine)
			case 0 : break; // just "0", not "0e" etc
			case 1 : sbOrdinal.append("r"); break;
			case 2 : sbOrdinal.append("n"); break;
			case 3 : sbOrdinal.append("r"); break;
			case 4 : sbOrdinal.append("t"); break;
			default: sbOrdinal.append("e"); break;
			}
			
		} else if ("sq".equals(lang)) {
			sbOrdinal.append("-te");
		} else if ("gl".equals(lang)) {
			sbOrdinal.append("ava");
		} else if ("mk".equals(lang)) {
			sbOrdinal.append("-\u0442\u0438");
		} else if ("ka".equals(lang)) {
			sbOrdinal.append("-\u10d4");
		} else if ("iw".equals(lang)) {
			sbOrdinal.append("\u200e\u200f");
		} else if ("ru".equals(lang) || "uk".equals(lang)) {
			sbOrdinal.append("-\u0433\u043e");
		} else if ("fr".equals(lang)) {
			if (n == 1) {
				sbOrdinal.append("er"); // could also be "re" for feminine...
			} else {
				sbOrdinal.append("e"); // could also be "es" for plural...
			}
		} else if ("sv".equals(lang)) {
			int unitsDigit = n % 10;
			if ((unitsDigit == 1) || (unitsDigit == 2)) {
				sbOrdinal.append(":a");
			} else {
				sbOrdinal.append(":e");
			}
		} 

		return sbOrdinal.toString();

	}

	
	/**
	 * given 1, return eg 1st (English only)
	 * 
	 * http://en.wikipedia.org/wiki/Ordinal_indicator
	 * @param n number
	 * @return english ordinal number
	 */
	public String getOrdinalNumberEn(int n) {
		/*
		 * http://en.wikipedia.org/wiki/Names_of_numbers_in_English If the
		 * tens digit of a number is 1, then write "th" after the number.
		 * For example: 13th, 19th, 112th, 9,311th. If the tens digit is not
		 * equal to 1, then use the following table: If the units digit is:
		 * 0 1 2 3 4 5 6 7 8 9 write this after the number th st nd rd th th
		 * th th th th
		 */

		int tensDigit = (n / 10) % 10;

		if (tensDigit == 1) {
			return n + "th";
		}

		int unitsDigit = n % 10;

		switch (unitsDigit) {
		case 1:
			return n + "st";
		case 2:
			return n + "nd";
		case 3:
			return n + "rd";
		default:
			return n + "th";
		}

	}
	
	/**
	 * Use localized digits.
	 */
	private boolean useLocalizedDigits = false;

	/**
	 * @return If localized digits are used for certain languages (Arabic,
	 *         Hebrew, etc).
	 */
	public boolean isUsingLocalizedDigits() {
		return useLocalizedDigits;
	}

	/**
	 * Use localized digits for certain languages (Arabic, Hebrew, etc).
	 * 
	 * Calls {@link #updateReverseLanguage(Locale)} to apply the change, but
	 * just if the new flag differs from the current.
	 * @param useLocalizedDigits whether localized digits should be used
	 */
	public void setUseLocalizedDigits(boolean useLocalizedDigits) {
		if (this.useLocalizedDigits == useLocalizedDigits) {
			return;
		}

		this.useLocalizedDigits = useLocalizedDigits;
		updateReverseLanguage(getLanguage());
		getKernel().updateConstruction();
		setUnsaved();

		if (euclidianView != null) {
			euclidianView.updateBackground();
		}
	}

		private boolean reverseNameDescription = false;
		private boolean isAutoCompletePossible = true;

		/**
		 * For Basque and Hungarian you have to say "A point" instead of "point A"
		 * @return whether current alnguage needs revverse order of type and name
		 */
		final public boolean isReverseNameDescriptionLanguage() {
			// for Basque and Hungarian
			return reverseNameDescription;
		}

		/**
		 * Returns whether autocomplete should be used at all. Certain languages
		 * make problems with auto complete turned on (e.g. Korean).
		 */
		final public boolean isAutoCompletePossible() {
			return isAutoCompletePossible;
		}

		// For Hebrew and Arabic. Guy Hed, 25.8.2008
		private boolean rightToLeftReadingOrder = false;

		final public boolean isRightToLeftReadingOrder() {
			return rightToLeftReadingOrder;
		}

		// For Persian and Arabic.
		private boolean rightToLeftDigits = false;

		final public boolean isRightToLeftDigits(StringTemplate tpl) {
			if (!tpl.internationalizeDigits()) {
				return false;
			}
			return rightToLeftDigits;
		}

		
	protected void updateReverseLanguage(String lang) {

		
		// reverseLanguage = "zh".equals(lang); removed Michael Borcherds
		// 2008-03-31
		reverseNameDescription = "eu".equals(lang) || "hu".equals(lang);

		// used for axes labels
		rightToLeftDigits = ("ar".equals(lang) || "fa".equals(lang));

		// Guy Hed, 25.8.2008
		// Guy Hed, 26.4.2009 - added Yiddish and Persian as RTL languages
		rightToLeftReadingOrder = ("iw".equals(lang) || "ar".equals(lang)
				|| "fa".equals(lang) || "ji".equals(lang));

		// Another option:
		// rightToLeftReadingOrder =
		// (Character.getDirectionality(getPlain("Algebra").charAt(1)) ==
		// Character.DIRECTIONALITY_RIGHT_TO_LEFT);

		// turn off auto-complete for Korean
		isAutoCompletePossible = true;// !"ko".equals(lang);

		// defaults
		unicodeDecimalPoint = '.';
		unicodeComma = ',';
		// unicodeThousandsSeparator=',';

		if (isUsingLocalizedDigits()) {
			if (lang.startsWith("ar")) { // Arabic
				unicodeZero = '\u0660'; // Arabic-Indic digit 0
				unicodeDecimalPoint = '\u066b'; // Arabic-Indic decimal point
				unicodeComma = '\u060c'; // Arabic comma
				// unicodeThousandsSeparator = '\u066c'; // Arabic Thousands
				// separator
			} else if (lang.startsWith("fa")) { // Persian
				unicodeZero = '\u06f0'; // Persian digit 0 (Extended
										// Arabic-Indic)
				unicodeDecimalPoint = '\u066b'; // Arabic comma
				unicodeComma = '\u060c'; // Arabic-Indic decimal point
				// unicodeThousandsSeparator = '\u066c'; // Arabic Thousands
				// separators
			} else if (lang.startsWith("ml")) {
				unicodeZero = '\u0d66'; // Malayalam digit 0
			} else if (lang.startsWith("th")) {
				unicodeZero = '\u0e50'; // Thai digit 0
			} else if (lang.startsWith("ta")) {
				unicodeZero = '\u0be6'; // Tamil digit 0
			} else if (lang.startsWith("sd")) {
				unicodeZero = '\u1bb0'; // Sudanese digit 0
			} else if (lang.startsWith("kh")) {
				unicodeZero = '\u17e0'; // Khmer digit 0
			} else if (lang.startsWith("mn")) {
				unicodeZero = '\u1810'; // Mongolian digit 0
			} else if (lang.startsWith("mm")) {
				unicodeZero = '\u1040'; // Mayanmar digit 0
			} else {
				unicodeZero = '0';
			}
		} else {
			unicodeZero = '0';
		}
	}

	/**
	 * Update right angle style to match current locale 
	 */
	public void updateRightAngleStyle() {
		if (rightAngleStyle != EuclidianStyleConstants.RIGHT_ANGLE_STYLE_NONE) {
			if (getLanguage().equals("de")
					|| getLanguage().equals("hu")) {
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
	 * @param min real world x min
	 * @param max real world x max
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
	public EuclidianView getEuclidianView1(){
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
	 * @param sb string builder
	 * @param asPreference whether we need this for preference XML
	 */
	public void getEuclidianViewXML(StringBuilder sb, boolean asPreference) {
		getEuclidianView1().getXML(sb, asPreference);
		if(hasEuclidianView2EitherShowingOrNot()){
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
	 * @author Michael Borcherds
	 * @param message message to appear on top of the stacktrace
	 */
	public static void printStacktrace(String message) {
		try {
			throw new Exception(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initializeSingularWS() {
		singularWS = new SingularWebService();
		singularWS.enable();
		if (singularWS.isAvailable()) {
			info("SingularWS is available at " + singularWS.getConnectionSite());
			// debug(singularWS.directCommand("ring r=0,(x,y),dp;ideal I=x^2,x;groebner(I);"));
		} else {
			info("No SingularWS is available at " + singularWS.getConnectionSite() + " (yet)");
			}
	}
	
	/**
	 * Prints debugging message, level DEBUG
	 * Special debugging format is used for expression values
	 * @param s object to be printed
	 */
	public static void debug(Object s) {
		if(s instanceof ExpressionValue){
			debug(ValidExpression.debugString((ExpressionValue)s));
			return;
		}
		if (s == null) {
			debug("<null>");
		} else {
			debug(s.toString());
		}
	}

	/**
	 * Prints debugging message, level DEBUG
	 * @param message message to be printed
	 */
	public static void debug(String message) {
		if (logger != null) {
			logger.log(logger.DEBUG, message);
		}
	}

	/**
	 * Prints debugging message, level NOTICE
	 * @param message message to be printed
	 */
	public static void notice(String message) {
		if (logger != null) {
			logger.log(logger.NOTICE, message);
			}
	}
	/**
	 * Prints debugging message, level INFO
	 * @param message message to be printed
	 */
	public static void info(String message) {
		if (logger != null) {
			logger.log(logger.INFO, message);
			}
	}
	/**
	 * Prints debugging message, level ERROR
	 * @param message message to be printed
	 */
	public static void error(String message) {
		if (logger != null) {
			logger.log(logger.ERROR, message);
			}
	}
	/**
	 * Prints debugging message, level WARN
	 * @param message message to be printed
	 */
	public static void warn(String message) {
		if (logger != null) {
			logger.log(logger.WARN, message);
			}
	}
	/**
	 * Prints debugging message, level EMERGENCY
	 * @param message message to be printed
	 */
	public static void emergency(String message) {
		if (logger != null) {
			logger.log(logger.EMERGENCY, message);
			}
	}

	/**
	 * Prints debugging message, level ALERT
	 * @param message message to be printed
	 */
	public static void alert(String message) {
		if (logger != null) {
			logger.log(logger.ALERT, message);
			}
	}
	/**
	 * Prints debugging message, level TRACE
	 * @param message message to be printed
	 */
	public static void trace(String message) {
		if (logger != null) {
			logger.log(logger.TRACE, message);
			}
	}
	/**
	 * Prints debugging message, level CRITICAL
	 * @param message message to be printed
	 */
	public static void critical(String message) {
		if (logger != null) {
			logger.log(logger.CRITICAL, message);
			}
	}
	
	public static GeoGebraLogger logger;
	public static SingularWebService singularWS;

	/**
	 * Whether we are running on Mac
	 * @return whether we are running on Mac
	 */
	public boolean isMacOS() {
		return false;
	}

	/**
	 * Whether we are running on Windows
	 * @return whether we are running on Windows
	 */
	public boolean isWindows() {
		return false;
	}
	/**
	 * Whether we are running on Windows Vista or later
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
	 * @param app application
	 * @param script JS method name
	 * @param arg arguments
	 */
	public abstract void evalJavaScript(App app, String script,
			String arg);

	private int[] version = null;
	
	/**
	 * @param v version parts
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

	public void setFileVersion(String version) {

		// AbstractApplication.debug("file version: " + version);

		if (version == null) {
			this.version = null;
			return;
		}

		this.version = getSubValues(version);
	}

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
	 * @return current mode
	 */
	final public int getMode() {
		return this.createEuclidianView().getMode();
	}

	/**
	 * Returns labeling style for newly created geos
	 * @return labeling style; AUTOMATIC is resovled either to US_DEFAULTS or OFF depending on visibility of AV 
	 */
	public int getCurrentLabelingStyle() {
		if (getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC) {
			if (isUsingFullGui()) {
				if ((getGuiManager() != null)
						&& getGuiManager().hasAlgebraView()) {
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
	 * @param colorName localized color name
	 * @return internal color name
	 */
	public abstract String reverseGetColor(String colorName);

	/**
	 * Returns translation of a key in colors bundle
	 * @param key key (color name)
	 * @return localized color name
	 */
	public abstract String getColor(String key);

	/**
	 * This is needed for handling paths to images inside .ggb archive 
	 * TODO probably we should replace this methodby something else as images are different in web  
	 * @param fullPath path to image
	 * @return legth of MD5 hash output
	 */
	public int getMD5folderLength(String fullPath) {
		return 32;
	}

	/**
	 * @param filename filename
	 * @return image wrapped in GBufferedImage 
	 */
	public abstract GBufferedImage getExternalImageAdapter(String filename);

	/**
	 * @return syntaxStr or syntax3D, depending on whether 3d is active 
	 */
	protected abstract String getSyntaxString();
	
	/**
	 * @param key command name
	 * @return command syntax
	 * TODO check whether getSyntaxString works here
	 */
	public String getCommandSyntax(String key) {

		String command = getCommand(key);

		String syntaxString = getSyntaxString();
		
		String syntax = null;

		if(syntaxString != null) {

			syntax = getCommand(key + syntaxString);

			syntax = syntax.replace("[", command + '[');
		}

		return syntax;
	}


	/**
	 * Clears selection and repaints all views
	 */
	final public void clearSelectedGeos() {
		clearSelectedGeos(true);
	}

	/**
	 * Clear selection
	 * @param repaint whether all views need repainting afterwards
	 */
	public void clearSelectedGeos(boolean repaint) {
		int size = selectedGeos.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				GeoElement geo = selectedGeos.get(i);
				geo.setSelected(false);
			}
			selectedGeos.clear();
			if (repaint) {
				kernel.notifyRepaint();
				updateSelection();
			}
			
			
				
		}
		
	}

	/**
	 * @return whether label dragging is enableded
	 */
	final public boolean isLabelDragsEnabled() {
		return labelDragsEnabled;
	}

	/**
	 * Enables or disables label dragging in this application. This is useful
	 * for applets.
	 * @param flag true to allow label dragging
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
	 * 
	 * @param evID
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
	 * @return XML for all macros; if there are none, XML header+footer are returned
	 */
	public String getMacroXML() {
		ArrayList<Macro> macros = kernel.getAllMacros();
		return myXMLio.getFullMacroXML(macros);
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
		return myXMLio.getFullMacroXML(macros);
	}

	private int labelingStyle = ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY;

	public boolean hasEuclidianView2() {
		// TODO Auto-generated method stub
		return false;
	}

	public abstract void showRelation(GeoElement geoElement,
			GeoElement geoElement2);

	public abstract void showError(MyError e);

	// FKH 20040826
	public String getXML() {
		return myXMLio.getFullXML();
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
		if (hasEuclidianView2()) {
			getEuclidianView2().getXML(sb, asPreference);
		} else if (asPreference && (getGuiManager() != null)) {
			//TODO: After the implementing of getEuclidianView2() on web remove the nullcheck from here 
			//getEuclidianView2().getXML(sb, true);
			EuclidianView ev2 = getEuclidianView2();
			if (ev2 != null) ev2.getXML(sb,  true);
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
	 * @param auxiliaryObjects true to show Auxiliary objects 
	 */
	public void setShowAuxiliaryObjects(boolean auxiliaryObjects) {
		// TODO Auto-generated method stub
		showAuxiliaryObjects = auxiliaryObjects;

		if (getGuiManager() != null) {
			getGuiManager().setShowAuxiliaryObjects(auxiliaryObjects);
			//updateMenubar();
		}
	}

	/**
	 * Sets labeling style. See the constants in ConstructionDefaults (e.g.
	 * LABEL_VISIBLE_AUTOMATIC)
	 * @param labelingStyle labeling style for new objects
	 */
	public void setLabelingStyle(int labelingStyle) {
		this.labelingStyle = labelingStyle;
	}
	
	/**
	 * Returns labeling style. See the constants in ConstructionDefaults (e.g.
	 * LABEL_VISIBLE_AUTOMATIC)
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



	// default changed for ggb42 (consistent with the rest of the sotfware world)
	private boolean reverseMouseWheel = true;

	/**
	 * @return true for scroll up = zoom in
	 */
	public boolean isMouseWheelReversed() {
		return reverseMouseWheel;
	}

	/**
	 * @param b true for normal scrolling (scrol up = zoom in), false for oposite setting
	 */
	public void reverseMouseWheel(boolean b) {
		reverseMouseWheel = b;
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
	 * Use localized labels.
	 */
	private boolean useLocalizedLabels = true;

	/**
	 * @return If localized labels are used for certain languages.
	 */
	public boolean isUsingLocalizedLabels() {
		return useLocalizedLabels;
	}

	/**
	 * Use localized labels for certain languages.
	 * @param useLocalizedLabels true to make labels of new geos localized
	 */
	public void setUseLocalizedLabels(boolean useLocalizedLabels) {
		this.useLocalizedLabels = useLocalizedLabels;
	}

	/**
	 * @param ttl tooltip language 
	 */
	public void setTooltipLanguage(String ttl) {
		// TODO Auto-generated method stub
		
	}
	
	public abstract DrawEquationInterface getDrawEquation();

	/**
	 * 
	 * @param show true to show navigation bar
	 */
	public void setShowConstructionProtocolNavigation(boolean show) {
		// TODO Auto-generated method stub
		
	}
	
	protected ArrayList<Perspective> tmpPerspectives = new ArrayList<Perspective>();

	/**
	 * Save all perspectives included in a document into an array with temporary
	 * perspectives.
	 * 
	 * @param perspectives array of perspetctives in the document
	 */
	public void setTmpPerspectives(ArrayList<Perspective> perspectives) {
		tmpPerspectives = perspectives;
	}

	public ArrayList<Perspective> getTmpPerspectives() {
		return tmpPerspectives;
	}


	/**
	 * @param show whether navigation bar should be visible
	 * @param playButton whether play button should be visible
	 * @param playDelay delay between phases (in seconds)
	 * @param showProtButton whether button to show construction protocol should be visible
	 */
	public abstract void setShowConstructionProtocolNavigation(boolean show,
			boolean playButton, double playDelay, boolean showProtButton);

	
	/**
	 * Sets tooltip timeout (in seconds)
	 * @param ttt tooltip timeout
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
	 * @return width of the whole application (central panel)
	 * This is needed for Corner[6]
	 */
	public abstract double getWidth();
	
	public abstract double getHeight();

	/**
	 * 
	 * @param serif serif
	 * @param style font style
	 * @param size font size
	 * @return font with given parameters
	 */
	public GFont getFontCommon(boolean serif, int style, int size) {
		// TODO Auto-generated method stub
		return null;
	}

	public abstract GFont getPlainFontCommon();

	public boolean isExporting() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @return list of selected geos
	 */
	public final ArrayList<GeoElement> getSelectedGeos() {
		return selectedGeos;
	}
	
	/** whether toolbar should be visible */
	protected boolean showToolBar = true;
	
	public void setShowToolBarNoUpdate(boolean toolbar) {
		showToolBar = toolbar;
	}
	

	/**
	 * Adds given geo to selection
	 * @param geo geo
	 */
	public final void addSelectedGeo(GeoElement geo) {
		addSelectedGeo(geo, true, true);
	}

	public final void addSelectedGeo(GeoElement geo, boolean repaint, boolean updateSelection) {
		if ((geo == null) || selectedGeos.contains(geo)) {
			return;
		}
	
		selectedGeos.add(geo);
		geo.setSelected(true);
		if (repaint) {
			kernel.notifyRepaint();
		}
		updateSelection();
	
	}

	public final void addSelectedGeos(ArrayList<GeoElement> geos, boolean repaint) {
	
		selectedGeos.addAll(geos);
		for (int i = 0; i < geos.size(); i++) {
			geos.get(i).setSelected(true);
		}
		if (repaint) {
			kernel.notifyRepaint();
		}
		updateSelection();
	}
	
	/**
	 * init the kernel (used for 3D)
	 */
	public void initKernel() {
		kernel = new Kernel(this);
	}

	/**
	 * init the EuclidianView (and EuclidianView3D for 3D)
	 */
	public void initEuclidianViews() {

		euclidianController = newEuclidianController( kernel);
		euclidianView = newEuclidianView(showAxes, showGrid);
		euclidianView.setAntialiasing(antialiasing);
	}
	abstract protected EuclidianView newEuclidianView(boolean[] showAxes1,boolean showGrid1);
	abstract protected EuclidianController newEuclidianController(Kernel kernel1);
	
	/**
	 * Returns undo manager
	 * @param cons construction
	 * @return undo manager
	 */
	public abstract UndoManager getUndoManager(Construction cons);

	public abstract AnimationManager newAnimationManager(Kernel kernel2);

	public abstract GeoElementGraphicsAdapter newGeoElementGraphicsAdapter();

	public void repaintSpreadsheet() {
		// TODO Auto-generated method stub
		
	}
	public final boolean isOnTheFlyPointCreationActive() {
		return isOnTheFlyPointCreationActive;
	}

	final public SpreadsheetTraceManager getTraceManager() {
		if (traceManager == null)
			traceManager = new SpreadsheetTraceManager(this);
		return traceManager;
	}


	public void setDefaultCursor() {
		// TODO Auto-generated method stub
		
	}

	public abstract void setWaitCursor();

	public abstract AlgoElement newAlgoShortestDistance(Construction cons,
			String label, GeoList list, GeoPointND start, GeoPointND end,
			GeoBoolean weighted);

	public abstract void updateStyleBars();

	/**
	 * Removes or adds given geo to selection and repaints views
	 * @param geo geo to be added / removed
	 */
	final public void toggleSelectedGeo(GeoElement geo) {
		toggleSelectedGeo(geo, true);
	}
	/**
	 * Removes or adds given geo to selection
	 * @param geo geo to be added / removed
	 * @param repaint whether we want to repaint afterwards
	 */
	final public void toggleSelectedGeo(GeoElement geo, boolean repaint) {
		if (geo == null) {
			return;
		}

		boolean contains = selectedGeos.contains(geo);
		if (contains) {
			selectedGeos.remove(geo);
			geo.setSelected(false);
		} else {
			selectedGeos.add(geo);
			geo.setSelected(true);
		}

		if (repaint) {
			kernel.notifyRepaint();
		}
		updateSelection();
	}


	/**
	 * Changes current mode to move mode
	 */
	public void setMoveMode() {
		setMode(EuclidianConstants.MODE_MOVE);
	}

	
	
	public abstract SpreadsheetTableModel getSpreadsheetTableModel();
	
	/**
	 * Changes current mode (tool number)
	 * @param mode new mode
	 */
	public void setMode(int mode) {
		if (mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
			currentSelectionListener = null;
		}

		if (getGuiManager() != null) {
			getGuiManager().setMode(mode);
		} else if (euclidianView != null) {
			euclidianView.setMode(mode);
		}
	}


	public void addToEuclidianView(GeoElement geo) {
		geo.addView(App.VIEW_EUCLIDIAN);
		getEuclidianView1().add(geo);
	}

	public void removeFromEuclidianView(GeoElement geo) {
		geo.removeView(App.VIEW_EUCLIDIAN);
		getEuclidianView1().remove(geo);
	}



	public abstract void setXML(String string, boolean b);

	public abstract GgbAPI getGgbApi();

	public abstract SoundManager getSoundManager();

	/**
	 * @return kernel for this window
	 */
	public final Kernel getKernel() {
		return kernel;
	}

	/**
	 * @return command processor for Bar Code
	 */
	abstract public CommandProcessor newCmdBarCode();

		public final int selectedGeosSize() {
		return selectedGeos.size();
	}

	/**
	 * @param e event
	 * @return whether right mouse button was clicked or click + ctrl appeared on Mac
	 */
	public boolean isRightClick(AbstractEvent e) {
		return e.isRightClick();
	}
	
	/**
	 * @param e event
	 * @return whether Ctrl on Win/Linux or Meta on Mac was pressed
	 */
	public boolean isControlDown(AbstractEvent e) {
		return e.isControlDown();
	}
	
	/**
	 * @param e event
	 * @return whether middle button was clicked once
	 */
	public boolean isMiddleClick(AbstractEvent e) {
		return e.isMiddleClick();
	}

	/**
	 * @return whether input bar is visible
	 */
	public abstract boolean showAlgebraInput();

	public abstract GlobalKeyDispatcher getGlobalKeyDispatcher();

	public abstract void evalPythonScript(App app, String string,
			String arg);

	public abstract void callAppletJavaScript(String string, Object[] args);
 
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		if (currentSelectionListener != null) {
			currentSelectionListener.geoElementSelected(geo, addToSelection);
		}
	}

	private PropertiesView propertiesView;

	protected boolean shiftDragZoomEnabled = true;

	// used when a secondary language is being used for tooltips.
	protected boolean tooltipFlag = false;

	public void setPropertiesView(PropertiesView propertiesView) {
		this.propertiesView = propertiesView;
	}

	/**
	 * Sets a mode where clicking on an object will notify the given selection
	 * listener.
	 */
	public void setSelectionListenerMode(GeoElementSelectionListener sl) {
		currentSelectionListener = sl;
		if (sl != null) {
			setMode(EuclidianConstants.MODE_SELECTION_LISTENER);
		} else {
			setMoveMode();
		}
	}
	
	public void updateSelection() {
		updateSelection(true);
	}

	public void updateSelection(boolean updatePropertiesView) {

		if (!showMenuBar || !isUsingFullGui() || isIniting()) {
			return;
		}

		// put in to check possible bottleneck
		// Application.debug("Update Selection");

		if (getGuiManager() != null)
			getGuiManager().updateMenubarSelection();

		if (getActiveEuclidianView().getMode() == EuclidianConstants.MODE_MOVE) {
			updateStyleBars();
		}

		if (updatePropertiesView && propertiesView != null) {
			propertiesView.updateSelection();
		}
	}
	
	
	
	
	
	final public boolean containsSelectedGeo(GeoElement geo) {
		return selectedGeos.contains(geo);
	}
	
	final public boolean containsSelectedGeos(ArrayList<GeoElement> geos) {
		return selectedGeos.containsAll(geos);
	}

	final public void removeSelectedGeo(GeoElement geo) {
		removeSelectedGeo(geo, true);
	}

	final public void removeSelectedGeo(GeoElement geo, boolean repaint) {
		if (geo == null) {
			return;
		}

		if(selectedGeos.remove(geo)){ 
			//update only if selectedGeos contained geo
			geo.setSelected(false);
			updateSelection();
			if (repaint) {
				kernel.notifyRepaint();
			}
			
		}
	}

	final public void selectNextGeo() {

		TreeSet<GeoElement> tree = kernel.getConstruction()
				.getGeoSetLabelOrder();

		TreeSet<GeoElement> copy = new TreeSet<GeoElement>(tree);

		Iterator<GeoElement> it = copy.iterator();

		// remove geos that don't have isSelectionAllowed()==true
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (!geo.isSelectionAllowed()) {
				tree.remove(geo);
			}
		}

		it = tree.iterator();

		// none selected, select first geo
		if (selectedGeos.size() == 0) {
			if (it.hasNext()) {
				addSelectedGeo(it.next());
			}
			return;
		}

		if (selectedGeos.size() != 1) {
			return;
		}

		// one selected, select next one
		GeoElement selGeo = selectedGeos.get(0);
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (selGeo == geo) {
				removeSelectedGeo(selGeo);
				if (!it.hasNext()) {
					it = tree.iterator();
				}
				addSelectedGeo(it.next());
				break;
			}
		}
	}

	final public void selectLastGeo() {
		if (selectedGeos.size() != 1) {
			return;
		}
		GeoElement selGeo = selectedGeos.get(0);
		GeoElement lastGeo = null;
		TreeSet<GeoElement> tree = kernel.getConstruction()
				.getGeoSetLabelOrder();
		TreeSet<GeoElement> copy = new TreeSet<GeoElement>(tree);
		Iterator<GeoElement> it = copy.iterator();

		// remove geos that don't have isSelectionAllowed()==true
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (!geo.isSelectionAllowed()) {
				tree.remove(geo);
			}
		}

		it = tree.iterator();
		while (it.hasNext()) {
			lastGeo = it.next();
		}

		it = tree.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (selGeo == geo) {
				removeSelectedGeo(selGeo);
				addSelectedGeo(lastGeo);
				break;
			}
			lastGeo = geo;
		}
	}

	
	public boolean isIniting() {
		return initing;
	}

	public final boolean isShiftDragZoomEnabled() {
		return shiftDragZoomEnabled;
	}

	public final void setShiftDragZoomEnabled(boolean shiftDragZoomEnabled) {
		this.shiftDragZoomEnabled = shiftDragZoomEnabled;
	}

	public abstract void updateMenubar();

	public int getFontSize() {
		return appFontSize;
	}
	public void setFontSize(int points) {
		setFontSize(points, true);
	}
	public void setFontSize(int points, boolean update) {
		if (points == appFontSize) {
			return;
		}
		appFontSize = points;
		//isSaved = false;
		if (!update) {
			return;
		}

		resetFonts();

		//updateUI();
	}
	public abstract void updateUI();
	
	public void clearTooltipFlag() {
		tooltipFlag = false;
	}
	
	public void resetFonts() {
		getFontManager().setFontSize(getGUIFontSize());
		updateFonts();
	}

	public int getGUIFontSize() {
		return guiFontSize == -1 ? getFontSize() : guiFontSize;
	}

	public void setGUIFontSize(int size) {
		guiFontSize = size;
		//updateFonts();
		//isSaved = false;

		resetFonts();

		updateUI();
	}


	protected FontManager getFontManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateFonts() {
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
	 * Returns a font that can display testString.
	 */
	public GFont getFontCanDisplay(String testString) {
		return getFontCanDisplay(testString, false, GFont.PLAIN, getFontSize());
	}

	/**
	 * Returns a font that can display testString.
	 */
	public GFont getFontCanDisplay(String testString, int fontStyle) {
		return getFontCanDisplay(testString, false, fontStyle, getFontSize());
	}

	/**
	 * Returns a font that can display testString.
	 */
	public GFont getFontCanDisplay(String testString, boolean serif,
			int fontStyle, int fontSize) {
		return getFontManager().getFontCanDisplay(testString, serif, fontStyle,
				fontSize);
	}
	
	/**
	 * Returns gui settings in XML format
	 */
	public String getGuiXML(boolean asPreference) {
		StringBuilder sb = new StringBuilder();
		sb.append("<gui>\n");

		getWindowLayoutXML(sb,asPreference);
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
			if (getTooltipLanguageString() != null) {
				sb.append(" language=\"");
				sb.append(getTooltipLanguageString());
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

		sb.append(getConsProtocolXML());

		sb.append("</gui>\n");

		return sb.toString();
	}
	public String getConsProtocolXML() {
		if (getGuiManager() == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		// construction protocol
		if (getGuiManager().isUsingConstructionProtocol()) {
			getGuiManager().getConsProtocolXML(sb);
		}

		return sb.toString();
	}
	public abstract String getTooltipLanguageString();
	protected abstract void getWindowLayoutXML(StringBuilder sb, boolean asPreference);

	public abstract void reset();

	public abstract PythonBridge getPythonBridge();
	
	//public abstract String getCurrentPythonScript();
	
	public abstract String getPlainTooltip(String string);

	public GeoElementSelectionListener getCurrentSelectionListener() {
		return currentSelectionListener;
	}

	public void setCurrentSelectionListener(GeoElementSelectionListener sl) {
		currentSelectionListener = sl;
	}
	public void setShowResetIcon(boolean flag) {
		if (flag != showResetIcon) {
			showResetIcon = flag;
			euclidianView.updateBackground();
		}
	}

	final public boolean showResetIcon() {
		return showResetIcon && !runningInFrame;
	}

	public boolean isUndoActive() {
		return kernel.isUndoActive();
	}

	public abstract boolean isHTML5Applet();

	public final void setOnTheFlyPointCreationActive(
			boolean isOnTheFlyPointCreationActive) {
		this.isOnTheFlyPointCreationActive = isOnTheFlyPointCreationActive;
	}

	public boolean useTransparentCursorWhenDragging = false;
	protected int dataParamWidth = 0;
	protected int dataParamHeight = 0;
	protected boolean useFullGui = false;
	
	public void setUseTransparentCursorWhenDragging(
			boolean useTransparentCursorWhenDragging) {
		this.useTransparentCursorWhenDragging = useTransparentCursorWhenDragging;
	}

	/*
	 * eg StringType.LATEX for desktop (JLaTeXMath)
	 * StringType.MATHML for web (canvasmath)
	 */
	public abstract StringType getFormulaRenderingType();

	public void doAfterRedefine(GeoElement geo) {
		if (getGuiManager() != null) {
			getGuiManager().doAfterRedefine(geo);
		}
	}

	public abstract String getLocaleStr();

	public abstract void showURLinBrowser(String string);

	public abstract void uploadToGeoGebraTube();

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
	
	// Michael Borcherds 2008-03-25
	// replace "%0" by arg0
	final public String getPlain(String key, String arg0) {
		String[] ss = { arg0 };
		return getPlain(key, ss);
	}
	
	// Michael Borcherds 2008-03-25
	// replace "%0" by arg0, "%1" by arg1
	final public String getPlain(String key, String arg0, String arg1) {
		String[] ss = { arg0, arg1 };
		return getPlain(key, ss);
	}
	
	// Michael Borcherds 2008-03-30
	// replace "%0" by arg0, "%1" by arg1, "%2" by arg2
	final public String getPlain(String key, String arg0, String arg1,
			String arg2) {
		String[] ss = { arg0, arg1, arg2 };
		return getPlain(key, ss);
	}
	
	// Michael Borcherds 2008-03-30
	// replace "%0" by arg0, "%1" by arg1, "%2" by arg2, "%3" by arg3
	final public String getPlain(String key, String arg0, String arg1,
			String arg2, String arg3) {
		String[] ss = { arg0, arg1, arg2, arg3 };
		return getPlain(key, ss);
	}

	// Michael Borcherds 2008-03-30
	// replace "%0" by arg0, "%1" by arg1, "%2" by arg2, "%3" by arg3, "%4" by
	// arg4
	final public String getPlain(String key, String arg0, String arg1,
			String arg2, String arg3, String arg4) {
		String[] ss = { arg0, arg1, arg2, arg3, arg4 };
		return getPlain(key, ss);
	}
	
	private final StringBuilder sbPlain = new StringBuilder();
	protected static boolean useFullAppGui = false;
	
	// Michael Borcherds 2008-03-25
	// Markus Hohenwarter 2008-09-18
	// replace "%0" by args[0], "%1" by args[1], etc
	final public String getPlain(String key, String[] args) {
		String str = getPlain(key);

		sbPlain.setLength(0);
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == '%') {
				// get number after %
				i++;
				int pos = str.charAt(i) - '0';
				if ((pos >= 0) && (pos < args.length)) {
					// success
					sbPlain.append(args[pos]);
				} else {
					// failed
					sbPlain.append(ch);
				}
			} else {
				sbPlain.append(ch);
			}
		}

		return sbPlain.toString();
	}

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
	 * @param filename filename (without /gui/images prefix) 
	 */
	public GImage getInternalImageAdapter(String filename) {
		return null;
	}
	
	public boolean showInputTop() {
		return showInputTop;
	}
	
	public void setShowInputTop(boolean flag, boolean update) {
		if (flag == showInputTop) {
			return;
		}

		showInputTop = flag;

		if (update && !isIniting()) {
			updateApplicationLayout();
		}
	}
	
	public boolean showInputHelpToggle() {
		return showInputHelpToggle;
	}
	
	public void setShowInputHelpToggle(boolean flag) {
		if (showInputHelpToggle == flag) {
			return;
		}

		showInputHelpToggle = flag;
		getGuiManager().updateAlgebraInput();
		updateMenubar();
	}

	
	public abstract void updateApplicationLayout();

	protected String getToolNameOrHelp(int mode, boolean toolName) {
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
				App
						.debug("Application.getModeText(): macro does not exist: ID = "
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
	 */
	public String getToolName(int mode) {
		return getToolNameOrHelp(mode, true);
	}

	/**
	 * Returns the tool help text for the given tool.
	 * 
	 * @param mode
	 *            number
	 */
	public String getToolHelp(int mode) {
		return getToolNameOrHelp(mode, false);
	}

	public String getFunction(String string) {
		return getPlain("Function."+string);
	}
	
	public ParserFunctions getParserFunctions(){
		return pf;
	}

	public abstract void clearConstruction();
	
	public abstract void fileNew();

	public abstract String getCountryFromGeoIP() throws Exception;

	private Random random = new Random();

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
	 * @param low least possible value of result 
	 * @param high highest possible value of result
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
		int min = (int)Math.ceil(Math.min(a, b));
		int max = (int)Math.floor(Math.max(a, b));

		return  random.nextInt(max - min + 1) + min;
	}
	
	/**
	 * allows use of seeds to generate the same sequence for a ggb file
	 * @param seed new seed 
	 */
	public void setRandomSeed(int seed) {
		random = new Random(seed);
	}
	
	public abstract boolean loadXML(String xml) throws Exception;

	public abstract void exportToLMS(boolean b);

	public abstract void copyGraphicsViewToClipboard();

	public final void setStandardView() {
		getActiveEuclidianView()
				.setStandardView(true);
	}

	public abstract void exitAll();

	public abstract void addMenuItem(MenuInterface parentMenu, String filename, String name,
	        boolean asHtml, MenuInterface subMenu);
	
	public String getVersionString() {
		return GeoGebraConstants.VERSION_STRING;
	}
	
	public abstract NormalizerMinimal getNormalizer();

	public String getEmptyIconFileName(){
		return "empty.gif";
	}
	
	public final void zoom(double px, double py, double zoomFactor) {
		getGuiManager().getActiveEuclidianView().zoom(px, py,
				zoomFactor, 15, true);
	}
	
	/**
	 * Sets the ratio between the scales of y-axis and x-axis, i.e. ratio =
	 * yscale / xscale;
	 */
	public final void zoomAxesRatio(double axesratio) {
		getGuiManager().getActiveEuclidianView()
				.zoomAxesRatio(axesratio, true);
	}

	public final void setViewShowAllObjects() {
		getGuiManager().getActiveEuclidianView()
				.setViewShowAllObjects(true);
	}
	
	/**
	 * Enables or disables right clicking in this application. This is useful
	 * for applets.
	 */
	public void setRightClickEnabled(boolean flag) {
		rightClickEnabled = flag;
	}

	final public boolean isRightClickEnabled() {
		return rightClickEnabled;
	}

	
	public boolean letShowPopupMenu() {
		return rightClickEnabled;
	}

	public boolean letShowPropertiesDialog() {
		return rightClickEnabled;
	}

	public String getPreferencesXML() {
		return myXMLio.getPreferencesXML();
	}

}
