package org.geogebra.common.gui.inputbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.GuiManager;
import org.geogebra.common.gui.util.TableSymbols;
import org.geogebra.common.kernel.commands.CommandsConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.util.LowerCaseDictionary;

import com.google.j2objc.annotations.Weak;

public class InputBarHelpPanel {

	@Weak
	protected App mApp;
	private LowerCaseDictionary mMathFuncDict;
	private LowerCaseDictionary mDict;
	private Collection<String> mAllCommands;
	private Collection<String> mMathFunc;
	private LowerCaseDictionary[] mSubDict;
	private TreeMap<String, Integer> mCategoryNameToTableIndex;
	private ArrayList<Collection<String>> mCommands;
	private StringBuilder mStringBuilder;

	/**
	 * @param app
	 *            application
	 */
	public InputBarHelpPanel(App app) {
		super();
		this.mApp = app;
		updateDictionaries();
	}

	/**
	 *
	 * @param app
	 *            app
	 * @param comparator
	 *            String comparator
	 * @param index
	 *            category index
	 * @return commands tree map
	 */
	final static public TreeSet<String> getCommandTreeMap(
			App app, Comparator<String> comparator, int index) {

		LowerCaseDictionary[] subDict = app.getSubCommandDictionary();

		if (subDict[index].isEmpty()) {
			return null;
		}

		TreeSet<String> cmdTree = new TreeSet<>(comparator);

		Iterator<?> it = subDict[index].getIterator();
		while (it.hasNext()) {
			String cmd = subDict[index].get(it.next());
			if (cmd != null && cmd.length() > 0) {
				cmdTree.add(cmd);
			}
		}
		return cmdTree;
	}

	/**
	 *
	 * @param app
	 *            app
	 * @param comparator
	 *            String comparator
	 * @return all commands tree set
	 */
	final static public TreeSet<String> getAllCommandsTreeSet(App app,
			Comparator<String> comparator) {

		TreeSet<String> treeSet = new TreeSet<>(comparator);

		LowerCaseDictionary dict = app.getCommandDictionary();
		Iterator<?> it = dict.getIterator();
		while (it.hasNext()) {
			String cmdName = dict.get(it.next());
			if (cmdName != null && cmdName.length() > 0) {
				treeSet.add(cmdName);
			}
		}
		return treeSet;
	}

	/**
	 * Update command dictionaries.
	 */
	public void updateDictionaries() {
		// CAS-Specific Syntaxes
		if (mApp.getConfig().getVersion() == GeoGebraConstants.Version.CAS) {
			mApp.getCommandDictionaryCAS();
		}
		// math functions
		String[] translatedFunctions = TableSymbols
				.getTranslatedFunctions(mApp);
		mMathFuncDict = mApp.newLowerCaseDictionary();
		for (String function : translatedFunctions) {
			// remove start space char
			int index = function.indexOf(' ');
			String insert;
			if (index == -1) {
				insert = function;
			} else {
				insert = function.substring(index + 1);
			}
			mMathFuncDict.addEntry(insert);
		}
		mMathFunc = mMathFuncDict.getAllCommands();

		// all commands dictionary (with math functions){
		mDict = new LowerCaseDictionary(mApp.getCommandDictionary());
		for (String function : mMathFunc) {
			mDict.addEntry(function);
		}
		mAllCommands = mDict.getAllCommands();

		// by category dictionaries
		mSubDict = mApp.getSubCommandDictionary();

		int n = getCategoriesCount();
		mCommands = new ArrayList<>(n);
		mCategoryNameToTableIndex = new TreeMap<>();

		for (int i = 0; i < n; i++) {
			String categoryName = getCategoryName(i);
			Collection<String> list = getSubDictionary(i).getAllCommands();
			if (list != null) {
				mCategoryNameToTableIndex.put(categoryName, i);
				mCommands.add(list);
			}
		}

	}

	public Collection<String> getCommands(int i) {
		return mCommands.get(i < mCommands.size() ? i : 0);
	}

	public Collection<String> getAllCommands() {
		return mAllCommands;
	}

	public Collection<String> getMathFunc() {
		return mMathFunc;
	}

	/**
	 *
	 * @return all commands dictionary
	 */
	public LowerCaseDictionary getDictionary() {
		return this.mDict;
	}

	/**
	 *
	 * @return math functions dictionary
	 */
	public LowerCaseDictionary getMathFuncDictionary() {
		return this.mMathFuncDict;
	}

	public LowerCaseDictionary getSubDictionary(int i) {
		return mSubDict[i];
	}

	/**
	 * @param category
	 *            category idex
	 * @return category dictionary
	 */
	public LowerCaseDictionary getCategoryDictionary(int category) {
		if (category == CommandsConstants.MATH_FUNC_INDEX) {
			return getMathFuncDictionary();
		}
		if (category == CommandsConstants.ALL_COMMANDS_INDEX) {
			return getDictionary();
		}
		return getSubDictionary(category);
	}

	public String getCategoryName(int i) {
		return mApp.getKernel().getAlgebraProcessor().getSubCommandSetName(i);
	}

	public int getCategoriesCount() {
		return mSubDict.length;
	}

	public TreeMap<String, Integer> getCategories() {
		return mCategoryNameToTableIndex;
	}

	/**
	 * @param categoryName
	 *            category name
	 * @return all commands in category
	 */
	public Collection<String> getCommandsFromCategory(String categoryName) {
		TreeMap<String, Integer> categories = getCategories();
		if (categories == null || !categories.containsKey(categoryName)) {
			return null;
		}
		return getCommandsFromCategory(categories.get(categoryName));
	}

	/**
	 * @param category
	 *            category index
	 * @return all commands in category
	 */
	public Collection<String> getCommandsFromCategory(int category) {
		if (category == CommandsConstants.MATH_FUNC_INDEX) {
			return getMathFunc();
		}
		if (category == CommandsConstants.ALL_COMMANDS_INDEX) {
			return getAllCommands();
		}
		return getCommands(category);
	}

	public String getMathFunctionsTitle() {
		return mApp.getLocalization().getMenu("MathematicalFunctions");
	}

	public String getAllCommandsTitle() {
		return mApp.getLocalization().getMenu("AllCommands");
	}

	/**
	 * @param command
	 *            command
	 * @param urlCaller
	 *            caller parameter (?caller=phone disables UI)
	 * @return help URL
	 */
	public String getURLForCommand(String command, String urlCaller) {

		// safety check
		if (command == null || command.length() == 0) {
			return null;
		}

		if (mStringBuilder == null) {
			mStringBuilder = new StringBuilder();
		} else {
			mStringBuilder.setLength(0);
		}

		// check if math func
		if (command.contains("(")) {
//			Log.debug("math func");
			String mathFuncHelpURL = mApp.getGuiManager()
					.getHelpURL(GuiManager.Help.GENERIC, App.WIKI_OPERATORS);

			mStringBuilder.append(mathFuncHelpURL);
			mStringBuilder.append(urlCaller);
			String ret = mStringBuilder.toString();
			return ret.replaceAll(" ", "%20");
		}

		// regular command
		String internal = mApp.getReverseCommand(command);
		String url = mApp.getGuiManager().getHelpURL(GuiManager.Help.COMMAND, internal);

		mStringBuilder.setLength(0);
		mStringBuilder.append(url);
		mStringBuilder.append(urlCaller);

		String ret = mStringBuilder.toString();
		return ret.replaceAll(" ", "%20");

	}

	/**
	 * verify that word is not reserved or an existing geo
	 *
	 * @param word
	 *            word arround cursor
	 * @return whether it's a function or geo
	 */
	public boolean checkWordAroundCursorIsUsable(String word) {
		if (word.length() > 0 && (mApp.getParserFunctions().isReserved(word)
				|| mApp.getKernel().lookupLabel(word) != null)) {
			return false;
		}
		return true;
	}
}
