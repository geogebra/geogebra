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
@SuppressWarnings("javadoc")
public abstract class App {
	protected ScriptManagerCommon scriptManager = null;
	public static final String LOADING_GIF = "http://www.geogebra.org/webstart/loading.gif";

	protected static boolean hasFullPermissions = false;
	protected boolean isSaved = true;

	public static final String WIKI_OPERATORS = "Predefined Functions and Operators";
	public static final String WIKI_MANUAL = "Manual:Main Page";
	public static final String WIKI_CAS_VIEW = "CAS_View";
	public static final String WIKI_TUTORIAL = "Tutorial:Main Page";
	public static final String WIKI_EXPORT_WORKSHEET = "Export_Worksheet_Dialog";
	public static final String WIKI_ADVANCED = "Advanced Features";
	public static final String WIKI_TEXT_TOOL = "Insert Text Tool";

	public static final int VIEW_NONE = 0;
	public static final int VIEW_EUCLIDIAN = 1;
	public static final int VIEW_ALGEBRA = 2;
	public static final int VIEW_SPREADSHEET = 4;
	public static final int VIEW_CAS = 8;
	public static final int VIEW_EUCLIDIAN2 = 16;
	public static final int VIEW_CONSTRUCTION_PROTOCOL = 32;
	public static final int VIEW_PROBABILITY_CALCULATOR = 64;
	public static final int VIEW_FUNCTION_INSPECTOR = 128;
	public static final int VIEW_INSPECTOR = 256;
	public static final int VIEW_EUCLIDIAN3D = 512;
	public static final int VIEW_EUCLIDIAN_FOR_PLANE = 1024;
	//please let 1024 to 2047 empty
	public static final int VIEW_PLOT_PANEL = 2048;
	public static final int VIEW_TEXT_PREVIEW = 4096;
	public static final int VIEW_PROPERTIES = 4097;
	public static final int VIEW_ASSIGNMENT = 8192;
	public static final int VIEW_TABLE_MODEL = 9000;
	private boolean showResetIcon = false;
	public boolean runningInFrame = false; // don't want to show resetIcon if
											// running in Frame
	private ParserFunctions pf = new ParserFunctions();
	
	private SpreadsheetTraceManager traceManager;

	// object is hit if mouse is within this many pixels
	// (more for points, see DrawPoint)
	public int capturingThreshold = 3;
	
	protected boolean showInputTop = false;
	
	protected boolean showAlgebraInput = true;
	protected boolean showInputHelpToggle = true;

	/*
	 * stored here rather than in algebra view so that it can be set without
	 * creating an AV (compatibility with 3.2)
	 */
	public boolean showAuxiliaryObjects = false;

	/** flag to test whether to draw Equations full resolution */
	public boolean exporting = false;

	private static String CASVersionString = "";

	public static void setCASVersionString(String string) {
		CASVersionString = string;

	}

	public static String getCASVersionString() {
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
	public Settings settings;
	
	public boolean useJavaFontsForLaTeX() {
		return useJavaFontsForLaTeX;

	}
	protected boolean antialiasing = true; 
	protected final boolean[] showAxes = { true, true };
	protected boolean showGrid = false;

	protected boolean initing = false;
	
	protected boolean labelDragsEnabled = true;//private

	public static final int SPREADSHEET_INI_COLS = 26;
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

				}
			}
		}

	}
	
	protected abstract boolean isCommandChanged();
	protected abstract void setCommandChanged(boolean b);
	protected abstract boolean isCommandNull();
	
	public final static String syntaxCAS = ".SyntaxCAS";
	public final static String syntax3D = ".Syntax3D";
	public final static String syntaxStr = ".Syntax";
	
	protected LowerCaseDictionary newLowerCaseDictionary(){
		return new LowerCaseDictionary(new NormalizerMinimal());
	}
	
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
	
	public final LowerCaseDictionary getCommandDictionaryCAS() {
		fillCommandDict();
		fillCasCommandDict();
		return commandDictCAS;
	}
	
	/**
	 * Returns an array of command dictionaries corresponding to the categorized
	 * sub command sets created in CommandDispatcher.
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
	public final LowerCaseDictionary getCommandDictionary() {
		fillCommandDict();
		return commandDict;
	}
	
	public abstract void initCommand();
	
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
			// Application.debug(internal);
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
	 */
	final public String translateCommand(String localname) {
		if (localname == null) {
			return null;
		}
		if (translateCommandTable == null) {
			return localname;
		}

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

	
	public void updateCommandDictionary() {
		// make sure all macro commands are in dictionary
		fillCommandDict();
	}
	
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


	public abstract String getCommand(String cmdName);

	public abstract String getPlain(String cmdName);

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


	public abstract String getMenu(String cmdName);

	public abstract String getError(String cmdName);
	
	public abstract String getSymbol(int key);
	
	public abstract String getSymbolTooltip(int key);



	public abstract void setTooltipFlag();

	public abstract boolean isApplet();

	public abstract void storeUndoInfo();

	public abstract boolean isUsingFullGui();

	public abstract boolean showView(int view);

	/** 
	 *  
	 * @return 2 letter language name, eg "en" 
	 */ 
	public abstract String getLanguage();

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



	public abstract String getInternalCommand(String s);

	public abstract void showError(String s);



	private boolean useBrowserForJavaScript = true;

	/*
	 * desktop: determines whether Rhino will be used (false) or the browser (true)
	 * web: determines whether JS scripting allowed (true) or not (false)
	 */
	public void setUseBrowserForJavaScript(boolean useBrowserForJavaScript) {
		this.useBrowserForJavaScript = useBrowserForJavaScript;
	}

	/*
	 * desktop: determines whether Rhino will be used (false) or the browser (true)
	 * web: determines whether JS scripting allowed (true) or not (false)
	 */
	public boolean useBrowserForJavaScript() {
		return useBrowserForJavaScript;
	}

	final public void initJavaScriptViewWithoutJavascript() {
		getScriptManager().initJavaScriptViewWithoutJavascript();
	}

	public abstract ScriptManagerCommon getScriptManager();

	final public String getTraceXML(GeoElement ge) {
		return getTraceManager().getTraceXML(ge);
	}
	
	public void traceToSpreadsheet(GeoElement ge) {
		getGuiManager().traceToSpreadsheet(ge);
	}

	public void resetTraceColumn(GeoElement ge) {
		getGuiManager().resetTraceColumn(ge);
	}


	
	public void updateMaxLayerUsed(int layer) {
		int newLayer=layer;
		if (layer > EuclidianStyleConstants.MAX_LAYERS) {
			newLayer = EuclidianStyleConstants.MAX_LAYERS;
		}
		if (layer > maxLayerUsed) {
			maxLayerUsed = newLayer;
		}
	}
	
	public boolean is3D() {
		return false;
	}
	
	String[] fontSizeStrings = null;

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
		
		/*
		 * in French, zero is singular, eg 0 dcimale rather than 0 decimal places
		 */
		public boolean isZeroPlural(String lang) {
			if (lang.startsWith("fr")) {
				return false;
			}
			return true;
		}
		
		

		final public static String[] strDecimalSpacesAC = { "0 decimals",
				"1 decimals", "2 decimals", "3 decimals", "4 decimals",
				"5 decimals", "10 decimals", "15 decimals", "", "3 figures",
				"5 figures", "10 figures", "15 figures" };

		// Rounding Menus end

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
	 * geos must contain GeoElement objects only.
	 * 
	 * @param geos
	 */
	final public void setSelectedGeos(ArrayList<GeoElement> geos) {
		clearSelectedGeos(false);
		if (geos != null) {
			for (int i = 0; i < geos.size(); i++) {
				GeoElement geo = geos.get(i);
				addSelectedGeo(geo, false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
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
	
	/*
	 * Michael Borcherds 2008-03-03 modified to select all of a layer pass
	 * layer==-1 to select all objects
	 */
	final public void selectAll(int layer) {
		clearSelectedGeos(false);

		Iterator<GeoElement> it = kernel.getConstruction()
				.getGeoSetLabelOrder().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if ((layer == -1) || (geo.getLayer() == layer)) {
				addSelectedGeo(geo, false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	final public void invertSelection() {

		Iterator<GeoElement> it = kernel.getConstruction()
				.getGeoSetLabelOrder().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (selectedGeos.contains(geo)) {
				removeSelectedGeo(geo, false);
			} else {
				addSelectedGeo(geo, false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	final public void selectAllPredecessors() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			TreeSet<GeoElement> tree = geo.getAllPredecessors();
			Iterator<GeoElement> it2 = tree.iterator();
			while (it2.hasNext()) {
				addSelectedGeo(it2.next(), false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	final public void showHideSelection() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			geo.setEuclidianVisible(!geo.isEuclidianVisible());
			geo.updateVisualStyle();
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	final public void showHideSelectionLabels() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			geo.setLabelVisible(!geo.isLabelVisible());
			geo.updateVisualStyle();
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	public boolean showAuxiliaryObjects() {
		return showAuxiliaryObjects;
	}

	final public void selectAllDescendants() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			TreeSet<GeoElement> tree = geo.getAllChildren();
			Iterator<GeoElement> it2 = tree.iterator();
			while (it2.hasNext()) {
				addSelectedGeo(it2.next(), false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}
	
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

	public abstract boolean freeMemoryIsCritical();

	public abstract long freeMemory();


	/**
	 * given 1, return eg 1st, 1e, 1:e according to the language
	 * 
	 * http://en.wikipedia.org/wiki/Ordinal_indicator
	 */
	StringBuilder sbOrdinal;

	/*
	 * given 1, return eg 1st, 1e, 1:e according to the language
	 * 
	 * http://en.wikipedia.org/wiki/Ordinal_indicator
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
	// for Basque and Hungarian you have to say "A point" instead of "point A"
		private boolean reverseNameDescription = false;
		private boolean isAutoCompletePossible = true;

		
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

	public int getMaxLayerUsed() {
		return maxLayerUsed;
	}

	
	public double countPixels(double min, double max) {
		EuclidianView ev = getEuclidianView1();
		return ev.toScreenCoordXd(max) - ev.toScreenCoordXd(min);
	}


	public abstract AlgebraView getAlgebraView();

	
	public EuclidianView getEuclidianView1(){
		notice("AbstrEuclView");
		return euclidianView;
	}
	
	public void resetMaxLayerUsed() {
		maxLayerUsed = 0;
	}

	public abstract EuclidianViewInterfaceCommon getActiveEuclidianView();


	public void getEuclidianViewXML(StringBuilder sb, boolean asPreference) {
		getEuclidianView1().getXML(sb, asPreference);
		if(hasEuclidianView2EitherShowingOrNot()){
			getEuclidianView1().getXML(sb, asPreference);
		}
	}
	
	public boolean hasEuclidianView3D() {
		return false;
	}

	public EuclidianViewInterfaceCommon getEuclidianView3D() {
		return null;
	}

	public abstract boolean hasEuclidianView2EitherShowingOrNot();

	public abstract boolean isShowingEuclidianView2();

	public abstract AbstractImageManager getImageManager();
	
	public abstract GuiManager getGuiManager();
	
	public abstract DialogManager getDialogManager();

	protected abstract void initGuiManager();

	// Michael Borcherds 2008-06-22
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

	public static void debug(String message) {
		if (logger != null) {
			logger.log(logger.DEBUG, message);
		}
	}

	public static void notice(String message) {
		if (logger != null) {
			logger.log(logger.NOTICE, message);
			}
	}

	public static void info(String message) {
		if (logger != null) {
			logger.log(logger.INFO, message);
			}
	}

	public static void error(String message) {
		if (logger != null) {
			logger.log(logger.ERROR, message);
			}
	}

	public static void warn(String message) {
		if (logger != null) {
			logger.log(logger.WARN, message);
			}
	}

	public static void emergency(String message) {
		if (logger != null) {
			logger.log(logger.EMERGENCY, message);
			}
	}

	public static void alert(String message) {
		if (logger != null) {
			logger.log(logger.ALERT, message);
			}
	}

	public static void trace(String message) {
		if (logger != null) {
			logger.log(logger.TRACE, message);
			}
	}

	public static void critical(String message) {
		if (logger != null) {
			logger.log(logger.CRITICAL, message);
			}
	}
	
	public static GeoGebraLogger logger;
	public static SingularWebService singularWS;

	public boolean isMacOS() {
		return false;
	}

	public boolean isWindows() {
		return false;
	}

	public boolean isWindowsVistaOrLater() {
		return false;
	}
				

	private static boolean miniPropertiesActive = true;
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

	public abstract void evalJavaScript(App app, String script,
			String arg);

	private int[] version = null;
	
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

	
	public static boolean isMiniPropertiesActive() {
		return miniPropertiesActive;
	}

	public static void setMiniPropertiesActive(boolean active) {
		miniPropertiesActive = active;
		// Application.debug("miniprops active:"+miniPropertiesActive);
	}

	public abstract EuclidianView createEuclidianView();
	
	final public int getMode() {
		return this.createEuclidianView().getMode();
	}

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


	public abstract String reverseGetColor(String colorName);

	public abstract String getColor(String string);

	/**
	 * TODO probably we should replace thismethodby something else as images are different in web  
	 * @param fullPath path to image
	 * @return
	 */
	public int getMD5folderLength(String fullPath) {
		return 32;
	}

	public abstract GBufferedImage getExternalImageAdapter(String filename);

	protected abstract String getSyntaxString();
	
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


	final public void clearSelectedGeos() {
		clearSelectedGeos(true);
	}

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


	final public boolean isLabelDragsEnabled() {
		return labelDragsEnabled;
	}

	/**
	 * Enables or disables label dragging in this application. This is useful
	 * for applets.
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

	public String getMacroXML() {
		ArrayList<Macro> macros = kernel.getAllMacros();
		return myXMLio.getFullMacroXML(macros);
	}

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
			// case VIEW_FUNCTION_INSPECTOR: return (View)getGuiManager()..
			// case VIEW_INSPECTOR: return
			// (View)getGuiManager().getSpreadsheetView();
			// case VIEW_EUCLIDIAN3D: return
			// (View)getGuiManager().getSpreadsheetView();
			// case VIEW_EUCLIDIAN_FOR_PLANE: return
			// (View)getGuiManager().getSpreadsheetView();
			// case VIEW_TEXT_PREVIEW: return
			// (View)getGuiManager().getSpreadsheetView();
		}

		return null;
	}


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
			getEuclidianView2().getXML(sb, true);
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
	 */
	public void setLabelingStyle(int labelingStyle) {
		this.labelingStyle = labelingStyle;
	}
	
	/**
	 * Returns labeling style. See the constants in ConstructionDefaults (e.g.
	 * LABEL_VISIBLE_AUTOMATIC)
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
	boolean reverseMouseWheel = true;

	public boolean isMouseWheelReversed() {
		return reverseMouseWheel;
	}

	public void reverseMouseWheel(boolean b) {
		reverseMouseWheel = b;
	}


	

	/**
	 * @param size preferred size 
	 */
	public void setPreferredSize(GDimension size) {
		// TODO Auto-generated method stub
		
	}

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
	 * @param show
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
	 */
	public void setTmpPerspectives(ArrayList<Perspective> perspectives) {
		tmpPerspectives = perspectives;
	}

	public ArrayList<Perspective> getTmpPerspectives() {
		return tmpPerspectives;
	}


	public abstract void setShowConstructionProtocolNavigation(boolean show,
			boolean playButton, double playDelay, boolean showProtButton);

	
	/**
	 * 
	 * @param ttt
	 */
	public void setTooltipTimeout(int ttt) {
		// TODO Auto-generated method stub
		
	}

	

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
	 * @param size
	 * @return
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

	public final ArrayList<GeoElement> getSelectedGeos() {
		return selectedGeos;
	}
	
	protected boolean showToolBar = true;
	
	public void setShowToolBarNoUpdate(boolean toolbar) {
		showToolBar = toolbar;
	}
	
	/**
	 * add first geo to selected geos
	 * @return true if a first geo exists
	 */
	public final boolean addFirstGeoSelected(){
		GeoElement geo = kernel.getFirstGeo();
		addSelectedGeo(geo);
		return (geo!=null);
	}

	public final void addSelectedGeo(GeoElement geo) {
		addSelectedGeo(geo, true);
	}

	public final void addSelectedGeo(GeoElement geo, boolean repaint) {
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
	 * 
	 * @param cons
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
	 * @param element
	 */
	final public void toggleSelectedGeo(GeoElement geo) {
		toggleSelectedGeo(geo, true);
	}

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


	public void setMoveMode() {
		setMode(EuclidianConstants.MODE_MOVE);
	}

	
	
	public abstract SpreadsheetTableModel getSpreadsheetTableModel();
	
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

	public final Kernel getKernel() {
		return kernel;
	}

	abstract public CommandProcessor newCmdBarCode();

		public final int selectedGeosSize() {
		return selectedGeos.size();
	}

	public boolean isRightClick(AbstractEvent e) {
		return e.isRightClick();
	}
	
	public boolean isControlDown(AbstractEvent e) {
		return e.isControlDown();
	}
	
	public boolean isMiddleClick(AbstractEvent e) {
		return e.isMiddleClick();
	}

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

		if (propertiesView != null) {
			propertiesView.updateSelection();
		}
	}
	
	
	public void setPropertiesViewSelection(ArrayList<GeoElement> geos){
		if (propertiesView != null) {
			propertiesView.updateSelection(geos);
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

	public abstract boolean isRightClickEnabled();

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
	 * @param a 
	 * @param b
	 * 
	 * @return random integer between a and b inclusive
	 * 
	 */
	public int getRandomIntegerBetween(double a, double b) {
		// make sure 4.000000001 is not rounded up to 5
		a = Kernel.checkInteger(a);
		b = Kernel.checkInteger(b);
		
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

}
