package org.geogebra.web.html5.gui.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ListBox;

/**
 * @author gabor
 *
 *         some listbox functionalities
 */
public class ListBoxApi {

	/**
	 * @param value
	 *            the value to search
	 * @param lb
	 *            Listbox for shearching
	 * @return the index if found -1 if not.
	 */
	public static int getIndexOf(String value, ListBox lb) {
		int indexToFind = -1;
		for (int i = 0; i < lb.getItemCount(); i++) {
			if (lb.getValue(i).equals(value)) {
				indexToFind = i;
				break;
			}
		}
		;
		return indexToFind;
	}

	/**
	 * @param lb
	 *            ListBox for getting multiple selection
	 * @return List of selected String items
	 */
	public static List<String> getSelection(ListBox lb) {
		List<String> sel = new ArrayList<String>();
		for (int i = 0; i < lb.getItemCount(); i++) {
			if (lb.isItemSelected(i)) {
				sel.add(lb.getItemText(i));
			}
		}
		return sel;
	}

	/**
	 * @param lb
	 *            ListBox for getting multiple selection
	 * @return List of selected indexes
	 */
	public static List<Integer> getSelectionIndexes(ListBox lb) {
		List<Integer> sel = new ArrayList<Integer>();
		for (int i = 0; i < lb.getItemCount(); i++) {
			if (lb.isItemSelected(i)) {
				sel.add(i);
			}
		}
		return sel;
	}
}
