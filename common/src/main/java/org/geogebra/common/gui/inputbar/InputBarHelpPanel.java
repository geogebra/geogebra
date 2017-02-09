package org.geogebra.common.gui.inputbar;

import org.geogebra.common.gui.GuiManager;
import org.geogebra.common.gui.util.TableSymbols;
import org.geogebra.common.kernel.commands.CommandsConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.util.LowerCaseDictionary;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

public class InputBarHelpPanel {

	protected App mApp;
	private LowerCaseDictionary mMathFuncDict;
	private LowerCaseDictionary mDict;
	private Collection<String> mAllCommands;
	private Collection<String> mMathFunc;
	private LowerCaseDictionary[] mSubDict;
	private TreeMap<String, Integer> mCategoryNameToTableIndex;
	private Collection<String>[] mCommands;
	private StringBuilder mStringBuilder;

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
	 * @return commands tree map
	 */
	final static public TreeMap<String, TreeSet<String>> getCommandTreeMap(
			App app, Comparator<String> comparator) {

		TreeMap<String, TreeSet<String>> cmdTreeMap = new TreeMap<String, TreeSet<String>>(
				comparator);
		LowerCaseDictionary[] subDict = app.getSubCommandDictionary();

		for (int i = 0; i < subDict.length; i++) {

			if (subDict[i].isEmpty()) {
				continue;
			}

			String cmdSetName = app.getKernel().getAlgebraProcessor()
					.getSubCommandSetName(i);

			TreeSet<String> cmdTree = new TreeSet<String>(comparator);

			Iterator<?> it = subDict[i].getIterator();
			while (it.hasNext()) {
				String cmd = subDict[i].get(it.next());
				if (cmd != null && cmd.length() > 0) {
					cmdTree.add(cmd);
				}
			}
			cmdTreeMap.put(cmdSetName, cmdTree);
		}
		return cmdTreeMap;
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

		TreeSet<String> treeSet = new TreeSet<String>(comparator);

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

	public void updateDictionaries() {

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
		mDict = (LowerCaseDictionary) mApp.getCommandDictionary().clone();
		for (String function : mMathFunc) {
			mDict.addEntry(function);
		}
		mAllCommands = mDict.getAllCommands();

		// by category dictionaries
		mSubDict = mApp.getSubCommandDictionary();

		int n = getCategoriesCount();
		mCommands = new Collection[n];
		mCategoryNameToTableIndex = new TreeMap<String, Integer>();

		for (int i = 0; i < n; i++) {
			String categoryName = getCategoryName(i);
			Collection<String> list = getSubDictionary(i).getAllCommands();
			if (list != null) {
				mCategoryNameToTableIndex.put(categoryName, i);
				mCommands[i] = list;
			}
		}

	}

	public Collection<String> getCommands(int i) {
		return mCommands[i];
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

	public String getCategoryName(int i) {
		return mApp.getKernel().getAlgebraProcessor().getSubCommandSetName(i);
	}

	public int getCategoriesCount() {
		return mSubDict.length;
	}

	public TreeMap<String, Integer> getCategories() {
		return mCategoryNameToTableIndex;
	}

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
			String mathFuncHelpURL = mApp.getGuiManager().getHelpURL(GuiManager.Help.GENERIC, App.WIKI_OPERATORS);

			mStringBuilder.append(mathFuncHelpURL);
			mStringBuilder.append(urlCaller);
			String ret = mStringBuilder.toString();
//			Log.debug("math func: " + command + ", url: " + mathFuncHelpURL + ", url with caller: " + ret);
			return ret.replaceAll(" ", "%20");
		}

		// regular command
		String internal = mApp.getReverseCommand(command);
		String url = mApp.getGuiManager().getHelpURL(GuiManager.Help.COMMAND, internal);

		mStringBuilder.setLength(0);
		mStringBuilder.append(url);
		mStringBuilder.append(urlCaller);

		String ret = mStringBuilder.toString();
//		Log.debug("command " + command + ", internal: " + internal + ", url: " + url + ", url with caller: " + ret);
		return ret.replaceAll(" ", "%20");

	}
}
