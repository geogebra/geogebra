package org.geogebra.common.gui.inputbar;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import org.geogebra.common.main.App;
import org.geogebra.common.util.LowerCaseDictionary;

public class InputBarHelpPanel {

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
}
