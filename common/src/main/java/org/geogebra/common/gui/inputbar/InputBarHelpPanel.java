package org.geogebra.common.gui.inputbar;

import org.geogebra.common.main.App;
import org.geogebra.common.util.AutoCompleteDictionary;
import org.geogebra.common.util.LowerCaseDictionary;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

public class InputBarHelpPanel {

	protected App mApp;
	private LowerCaseDictionary mDict;
	private Collection<String> mAllCommands;
	private LowerCaseDictionary[] mSubDict;
	private TreeMap<String, Integer> mCategoryNameToTableIndex;
	private Collection<String>[] mCommands;

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
			App app,
			Comparator<String> comparator) {

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

		// all commands dictionary
		mDict = mApp.getCommandDictionary();
		mAllCommands = mDict.getAllCommands();

		// by category dictionaries
		mSubDict = mApp.getSubCommandDictionary();

		int n = getCategoriesCount();
		mCommands = new Collection[n];
		mCategoryNameToTableIndex = new TreeMap<String, Integer>();

		for (int i = 0; i < n; i++) {
			String categoryName = getCategoryName(i);
//			Log.debug("==== Category: "+categoryName);
			Collection<String> list = getSubDictionary(i).getAllCommands();
			if (list != null) {
				mCategoryNameToTableIndex.put(categoryName, i);
				mCommands[i] = list;
//				for (String s : list) {
//					Log.debug(s);
//				}
			}
//			else{
//				Log.debug("xxx none");
//			}
		}


	}

	public Collection<String> getCommands(int i) {
		return mCommands[i];
	}

	public Collection<String> getAllCommands() {
		return mAllCommands;
	}

	/**
	 * USED IN ANDROID
	 *
	 * @return all commands dictionnary
	 */
	public AutoCompleteDictionary getDictionary() {
		return this.mDict;
	}

	public LowerCaseDictionary getSubDictionary(int i) {
		return mSubDict[i];
	}

	public String getCategoryName(int i) {
		return mApp.getKernel().getAlgebraProcessor()
				.getSubCommandSetName(i);
	}

	public int getCategoriesCount() {
		return mSubDict.length;
	}

	public TreeMap<String, Integer> getCategories() {
		return mCategoryNameToTableIndex;
	}
}
