package geogebra.common.main;

import geogebra.common.awt.BufferedImageAdapter;
import geogebra.common.awt.Dimension;
import geogebra.common.awt.Font;
import geogebra.common.euclidian.AbstractEuclidianController;
import geogebra.common.euclidian.DrawEquationInterface;
import geogebra.common.euclidian.EuclidianStyleConstants;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.gui.GuiManager;
import geogebra.common.gui.view.spreadsheet.SpreadsheetTableModelInterface;
import geogebra.common.gui.view.spreadsheet.SpreadsheetTraceManager;
import geogebra.common.io.layout.Perspective;
import geogebra.common.kernel.AbstractAnimationManager;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.AbstractUndoManager;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.MacroInterface;
import geogebra.common.kernel.View;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.cas.GeoGebraCasInterface;
import geogebra.common.kernel.commands.CommandDispatcher;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.settings.Settings;
import geogebra.common.plugin.GgbAPI;
import geogebra.common.sound.SoundManager;
import geogebra.common.util.AbstractImageManager;
import geogebra.common.util.DebugPrinter;
import geogebra.common.util.LowerCaseDictionary;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.Set;

public abstract class AbstractApplication {
	public static final String LOADING_GIF = "http://www.geogebra.org/webstart/loading.gif";

	public static final String WIKI_OPERATORS = "Predefined Functions and Operators";
	public static final String WIKI_MANUAL = "Manual:Main Page";
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
	public static final int VIEW_PLOT_PANEL = 2048;
	public static final int VIEW_TEXT_PREVIEW = 4096;
	public static final int VIEW_PROPERTIES = 4097;
	public static final int VIEW_ASSIGNMENT = 8192;

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
	public int pointStyle = EuclidianStyleConstants.POINT_STYLE_DOT;
	public int booleanSize = 13;
	public int rightAngleStyle = EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE;

	public boolean useJavaFontsForLaTeX = false;

	protected final ArrayList<GeoElement> selectedGeos = new ArrayList<GeoElement>();

	protected Kernel kernel;

	protected boolean isOnTheFlyPointCreationActive = true;
	protected Settings settings;
	
	public boolean useJavaFontsForLaTeX() {
		return useJavaFontsForLaTeX;

	}
	protected boolean antialiasing = true; 
	protected final boolean[] showAxes = { true, true };
	protected boolean showGrid = false;

	public static final int SPREADSHEET_INI_COLS = 26;
	public static final int SPREADSHEET_INI_ROWS = 100;
	
	private HashMap<String, String> translateCommandTable,
	translateCommandTableScripting;
	// command dictionary
	private LowerCaseDictionary commandDict;
	private LowerCaseDictionary commandDictCAS;
	
	protected AbstractEuclidianView euclidianView;
	protected AbstractEuclidianController euclidianController;

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
			// Application.debug(internal);
			if (!internal.equals("Command")) {
				String local = getScriptingCommand(internal);
				if (local != null) {
					local = local.trim();
					// case is ignored in translating local command names to
					// internal names!
					translateCommandTableScripting.put(local.toLowerCase(),
							internal);

				}
			}
		}

	}
	
	protected abstract boolean isCommandChanged();
	protected abstract void setCommandChanged(boolean b);
	protected abstract boolean isCommandNull();
	
	public void fillCasCommandDict() {
		// this method might get called during initialization, when we're not
		// yet
		// ready to fill the casCommandDict. In that case, we will fill the
		// dict during fillCommandDict :)

		if (!isCommandChanged()
				&& ((commandDictCAS != null) || isCommandNull())) {
			return;
		}

		setCommandChanged(false);

		commandDictCAS = new LowerCaseDictionary();
		subCommandDict[CommandDispatcher.TABLE_CAS].clear();

		// iterate through all available CAS commands, add them (translated if
		// available, otherwise untranslated)
		for (String cmd : kernel.getGeoGebraCAS().getCurrentCAS()
				.getAvailableCommandNames()) {
			try {
				String local = getCommand(cmd);
				if (local != null) {
					commandDictCAS.addEntry(local);
					subCommandDict[CommandDispatcher.TABLE_CAS]
							.addEntry(local);
				} else {
					commandDictCAS.addEntry(cmd);
					subCommandDict[CommandDispatcher.TABLE_CAS]
							.addEntry(cmd);
				}
			} catch (MissingResourceException mre) {
				commandDictCAS.addEntry(cmd);
				subCommandDict[CommandDispatcher.TABLE_CAS]
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

		return subCommandDict;
	}
	public abstract void initTranslatedCommands();
	public final LowerCaseDictionary getCommandDictionary() {
		fillCommandDict();
		return commandDict;
	}
	
	public abstract void getCommandResourceBundle();
	protected void fillCommandDict() {
		getCommandResourceBundle();

		if (!isCommandChanged()) {
			return;
		}
		setCommandChanged(false);

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
		commandDict = new LowerCaseDictionary();
		// else commandDict.clear();

		translateCommandTable.clear();


		Set<String> publicCommandNames = kernel.getAlgebraProcessor()
				.getPublicCommandSet();

		// =====================================
		// init sub command dictionaries
		Set<?>[] publicSubCommandNames = kernel.getAlgebraProcessor()
				.getPublicCommandSubSets();
		if (subCommandDict == null) {
			subCommandDict = new LowerCaseDictionary[publicSubCommandNames.length];
			for (int i = 0; i < subCommandDict.length; i++) {
				subCommandDict[i] = new LowerCaseDictionary();
			}
		}
		for (int i = 0; i < subCommandDict.length; i++) {
			subCommandDict[i].clear();
			// =====================================
		}

		for (Commands comm : Commands.values()) {
			String internal = comm.toString();
			// Application.debug(internal);
			if (!internal.equals("Command")) {
				String local = getCommand(internal);
				if (local != null) {
					local = local.trim();
					// case is ignored in translating local command names to
					// internal names!
					translateCommandTable.put(local.toLowerCase(), internal);

					// only add public commands to the command dictionary
					if (publicCommandNames.contains(internal)) {
						commandDict.addEntry(local);
					}

					// add public commands to the sub-command dictionaries
					for (int i = 0; i < subCommandDict.length; i++) {
						if (publicSubCommandNames[i].contains(internal)) {
							subCommandDict[i].addEntry(local);
						}
					}

				}
			}
		}

		// get CAS Commands
		if (kernel.isGeoGebraCASready()) {
			fillCasCommandDict();
		}
		addMacroCommands();
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
			AbstractApplication.debug("translation not initialized");
			return localname;
		}

		// note: lookup lower case of command name!
		Object value = translateCommandTable.get(localname.toLowerCase());
		if (value == null) {
			fillCommandDictScripting();
			if (translateCommandTableScripting != null) {
				value = translateCommandTableScripting.get(localname
						.toLowerCase());
			}
		}
		if (value == null) {
			return localname;
		} else {
			return (String) value;
		}
	}

	
	public void updateCommandDictionary() {
		// make sure all macro commands are in dictionary
		fillCommandDict();
	}
	
	protected void addMacroCommands() {
		if ((commandDict == null) || (kernel == null) || !kernel.hasMacros()) {
			return;
		}

		ArrayList<MacroInterface> macros = kernel.getAllMacros();
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

		ArrayList<MacroInterface> macros = kernel.getAllMacros();
		for (int i = 0; i < macros.size(); i++) {
			String cmdName = macros.get(i).getCommandName();
			commandDict.removeEntry(cmdName);
		}
	}

	

	
	public abstract void initScriptingBundle();


	public abstract String getScriptingCommand(String internal);


	public abstract String getCommand(String cmdName);

	public abstract String getPlain(String cmdName);

	public abstract String getPlain(String cmdName, String param);

	public abstract String getPlain(String cmdName, String param, String param2);

	public abstract String getPlain(String cmdName, String param,
			String param2, String param3);

	public abstract String getPlain(String cmdName, String param,
			String param2, String param3, String param4);

	public abstract String getPlain(String cmdName, String param,
			String param2, String param3, String param4, String param5);

	public abstract String getMenu(String cmdName);

	public abstract String getError(String cmdName);

	public abstract boolean isRightToLeftReadingOrder();

	public abstract void setTooltipFlag();

	public abstract void clearTooltipFlag();

	public abstract boolean isApplet();

	public abstract void storeUndoInfo();

	public abstract boolean isUsingFullGui();

	public abstract boolean showView(int view);

	public abstract void callJavaScript(String jsFunction, Object[] args);

	public abstract boolean isUsingLocalizedLabels();

	public abstract String getLanguage();

	public abstract boolean languageIs(String s);

	public abstract boolean letRedefine();

	public abstract String translationFix(String s);

	public abstract void traceToSpreadsheet(GeoElement o);

	public abstract void resetTraceColumn(GeoElement o);

	public abstract boolean isReverseNameDescriptionLanguage();

	public abstract boolean isBlockUpdateScripts();

	public abstract void setBlockUpdateScripts(boolean flag);


	public abstract String getInternalCommand(String s);

	public abstract void showError(String s);

	public abstract boolean isScriptingDisabled();

	public abstract boolean useBrowserForJavaScript();

	public abstract void initJavaScriptViewWithoutJavascript();

	public abstract Object getTraceXML(GeoElement geoElement);

	public abstract void removeSelectedGeo(GeoElement geoElement, boolean b);

	public abstract void changeLayer(GeoElement ge, int layer, int layer2);

	public abstract boolean freeMemoryIsCritical();

	public abstract long freeMemory();

	public abstract int getLabelingStyle();

	public abstract String getOrdinalNumber(int i);

	public abstract double getXmin();

	public abstract double getXmax();

	public abstract double getXminForFunctions();

	public abstract double getXmaxForFunctions();

	public abstract double countPixels(double min, double max);

	public abstract int getMaxLayerUsed();

	public abstract Object getAlgebraView();

	public abstract EuclidianViewInterfaceSlim getEuclidianView();

	public abstract EuclidianViewInterfaceSlim getActiveEuclidianView();

	public abstract AbstractEuclidianView createEuclidianViewForPlane(
			Object o);

	public abstract boolean isRightToLeftDigits();

	public abstract boolean isShowingEuclidianView2();

	public abstract AbstractImageManager getImageManager();
	
	public abstract GuiManager getGuiManager();

	// Michael Borcherds 2008-06-22
	public static void printStacktrace(String message) {
		try {

			throw new Exception(message);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	public static void debug(Object s) {
		if (s == null) {
			doDebug("<null>", false, false, 0);
		} else {
			doDebug(s.toString(), false, false, 0);
		}
	}

	public static void debug(Object s[]) {
		debug(s, 0);
	}

	static StringBuilder debugSb = null;

	public static void debug(Object[] s, int level) {
		if (debugSb == null) {
			debugSb = new StringBuilder();
		} else {
			debugSb.setLength(0);
		}

		for (int i = 0; i < s.length; i++) {
			debugSb.append(s[i]);
			debugSb.append('\n');
		}

		debug(debugSb, level);
	}

	public static void debug(Object s, int level) {
		doDebug(s.toString(), false, false, level);
	}

	public static void debug(Object s, boolean showTime, boolean showMemory,
			int level) {
		doDebug(s.toString(), showTime, showMemory, level);
	}

	public static DebugPrinter dbg;

	// Michael Borcherds 2008-06-22
	private static void doDebug(String s, boolean showTime, boolean showMemory,
			int level) {

		String ss = s == null ? "<null>" : s;

		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();

		// String calleeMethod = elements[0].getMethodName();
		String callerMethodName = elements[2].getMethodName();
		String callerClassName = elements[2].getClassName();

		StringBuilder sb = new StringBuilder("*** Message from ");
		sb.append("[");
		sb.append(callerClassName);
		sb.append(".");
		sb.append(callerMethodName);
		sb.append("]");

		if ((dbg != null) && showTime) {
			dbg.getTimeInfo(sb);
		}

		if ((dbg != null) && showMemory) {
			dbg.getMemoryInfo(sb);

		}
		if (dbg == null) {
			System.out.println(ss + sb.toString());
		} else {
			dbg.print(ss, sb.toString(), level);
		}

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

	public String toLowerCase(String substring) {
		return substring.toLowerCase();
	}

	public abstract void evalScript(AbstractApplication app, String script,
			String arg);

	public boolean fileVersionBefore(int[] subValues) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public abstract AbstractEuclidianView createEuclidianView();
	
	final public int getMode() {
		return this.createEuclidianView().getMode();
	}

	/**
	 * @deprecated added when refactoring
	 */
	@Deprecated
	public abstract void updateConstructionProtocol();

	public abstract int getCurrentLabelingStyle();

	public abstract String reverseGetColor(String colorName);

	public abstract String getColor(String string);

	public int getMD5folderLength(String fullPath) {
		return 32;
	}

	public abstract BufferedImageAdapter getExternalImageAdapter(String filename);

	public abstract String getCommandSyntax(String cmd);

	public abstract void clearSelectedGeos();

	public void setScrollToShow(boolean b) {
		// TODO Auto-generated method stub

	}

	public void setUnsaved() {
		// TODO Auto-generated method stub

	}

	public void setActiveView(int viewEuclidian2) {
		// TODO Auto-generated method stub

	}
	
	public void refreshViews() {
		//getEuclidianView().updateBackground();
		//if (hasEuclidianView2()) {
		//	getEuclidianView2().updateBackground();
		//}
		kernel.notifyRepaint();
	}



	public boolean hasEuclidianView2() {
		// TODO Auto-generated method stub
		return false;
	}

	public abstract void showRelation(GeoElement geoElement,
			GeoElement geoElement2);

	public abstract void showError(MyError e);

	public abstract void showError(String string, String str);

	public abstract View getView(int id);

	public String getCompleteUserInterfaceXML(boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	final public Settings getSettings() {
		return settings;
	}

	public abstract void setScriptingDisabled(boolean scriptingDisabled);

	public void setFontSize(int guiSize) {
		// TODO Auto-generated method stub
		
	}

	public void setGUIFontSize(int i) {
		// TODO Auto-generated method stub
		
	}

	public void setUniqueId(String uniqueId) {
		// TODO Auto-generated method stub
		
	}

	public void setFileVersion(String ggbVersion) {
		// TODO Auto-generated method stub
		
	}

	public void setShowAuxiliaryObjects(boolean auxiliaryObjects) {
		// TODO Auto-generated method stub
		
	}

	public void setLabelingStyle(int style) {
		// TODO Auto-generated method stub
		
	}

	public void reverseMouseWheel(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void setUseLocalizedDigits(boolean digits) {
		// TODO Auto-generated method stub
		
	}

	public void setPreferredSize(Dimension size) {
		// TODO Auto-generated method stub
		
	}

	public int getTooltipTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setUseLocalizedLabels(boolean labels) {
		// TODO Auto-generated method stub
		
	}

	public void setTooltipLanguage(String ttl) {
		// TODO Auto-generated method stub
		
	}
	
	public abstract DrawEquationInterface getDrawEquation();

	public void setShowConstructionProtocolNavigation(boolean show) {
		// TODO Auto-generated method stub
		
	}

	public void setTmpPerspectives(ArrayList<Perspective> tmp_perspectives) {
		// TODO Auto-generated method stub
		
	}

	public abstract void setShowConstructionProtocolNavigation(boolean show,
			boolean playButton, double playDelay, boolean showProtButton);

	

	public void setTooltipTimeout(int ttt) {
		// TODO Auto-generated method stub
		
	}

	public boolean isUsingLocalizedDigits() {
		// TODO Auto-generated method stub
		return false;
	}

	public EuclidianViewInterfaceSlim getEuclidianView2() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return width of the whole application (central panel)
	 * This is needed for Corner[6]
	 */
	public abstract double getWidth();
	
	public abstract double getHeight();

	public Font getFontCommon(boolean b, int i, int size) {
		// TODO Auto-generated method stub
		return null;
	}

	public Font getPlainFontCommon() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isExporting() {
		// TODO Auto-generated method stub
		return false;
	}

	public final ArrayList<GeoElement> getSelectedGeos() {
		return selectedGeos;
	}
	
	public abstract void updateSelection();

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
			((Kernel)kernel).notifyRepaint();
		}
		updateSelection();
	
	}

	public final void addSelectedGeos(ArrayList<GeoElement> geos, boolean repaint) {
	
		selectedGeos.addAll(geos);
		for (int i = 0; i < geos.size(); i++) {
			geos.get(i).setSelected(true);
		}
		if (repaint) {
			((Kernel)kernel).notifyRepaint();
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
	abstract protected AbstractEuclidianView newEuclidianView(boolean[] showAxes,boolean showGrid);
	abstract protected AbstractEuclidianController newEuclidianController(Kernel kernel);
	/**
	 * @deprecated
	 * @param cons
	 * @return undo manager
	 */
	@Deprecated
	public abstract AbstractUndoManager getUndoManager(Construction cons);

	public abstract AbstractAnimationManager newAnimationManager(Kernel kernel2);

	public abstract GeoElementGraphicsAdapter newGeoElementGraphicsAdapter();

	public abstract GeoGebraCasInterface newGeoGebraCAS();

	public void repaintSpreadsheet() {
		// TODO Auto-generated method stub
		
	}
	public final boolean isOnTheFlyPointCreationActive() {
		return isOnTheFlyPointCreationActive;
	}

	/*
	 * needs to work if spreadsheet not opened yet
	 */
	public int getHighestUsedColumn() {
		//if (isUsingFullGui() && getGuiManager().hasSpreadsheetView()) {
		//	return getGuiManager().getSpreadsheetView().getHighestUsedColumn();
		//} else
		{
			int highestUsedColumn = -1;
			Iterator<GeoElement> it = kernel.getConstruction().getGeoSetConstructionOrder().iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				geogebra.common.awt.Point location = geo.getSpreadsheetCoords();
				if (location != null && location.x > highestUsedColumn) highestUsedColumn = location.x;
			}

			return highestUsedColumn;
		}
	}

	/*
	 * needs to work if spreadsheet not opened yet
	 */
	public int getHighestUsedRow() {
		//if (isUsingFullGui() && getGuiManager().hasSpreadsheetView()) {
		//	return getGuiManager().getSpreadsheetView().getHighestUsedRow();
		//} else
		{
			int highestUsedRow = -1;
			Iterator<GeoElement> it = kernel.getConstruction().getGeoSetConstructionOrder().iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				geogebra.common.awt.Point location = geo.getSpreadsheetCoords();
				if (location != null && location.y > highestUsedRow) highestUsedRow = location.y;
			}

			return highestUsedRow;
		}
	}

	public boolean isEqualsRequired() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setSpreadsheetValueAt(GeoElement value2, int i, int j) {
		// TODO Auto-generated method stub
		
	}

	public abstract SpreadsheetTraceManager getTraceManager();

	public void setDefaultCursor() {
		// TODO Auto-generated method stub
		
	}

	public void setWaitCursor() {
		// TODO Auto-generated method stub
		
	}

	public abstract AlgoElement newAlgoShortestDistance(Construction cons,
			String label, GeoList list, GeoPointND start, GeoPointND end,
			GeoBoolean weighted);

	public abstract void updateStyleBars();

	public abstract void toggleSelectedGeo(GeoElement geo);

	public abstract void setMoveMode();

	public abstract void removeSelectedGeo(GeoElement geo);
	
	public abstract SpreadsheetTableModelInterface getSpreadsheetTableModel();
	
	public abstract void setMode(int modeMove);

	public abstract void addToEuclidianView(GeoElement geo);

	public abstract void removeFromEuclidianView(GeoElement geo);

	public abstract void setXML(String string, boolean b);

	public abstract GgbAPI getGgbApi();

	public abstract SoundManager getSoundManager();

	public final Kernel getKernel() {
		return kernel;
	}

	abstract public CommandProcessor newCmdBarCode();


}
