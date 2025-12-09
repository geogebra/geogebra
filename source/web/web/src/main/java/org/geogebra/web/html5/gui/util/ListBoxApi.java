/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.gui.util;

import java.util.ArrayList;
import java.util.List;

import org.gwtproject.user.client.ui.ListBox;

/**
 * TODO these static methods should just be methods of ListBox
 * @author gabor
 */
public class ListBoxApi {

	/**
	 * @param value
	 *            the value to search
	 * @param lb
	 *            Listbox for searching
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
		return indexToFind;
	}

	/**
	 * @param lb
	 *            ListBox for getting multiple selection
	 * @return List of selected String items
	 */
	public static List<String> getSelection(ListBox lb) {
		List<String> sel = new ArrayList<>();
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
		List<Integer> sel = new ArrayList<>();
		for (int i = 0; i < lb.getItemCount(); i++) {
			if (lb.isItemSelected(i)) {
				sel.add(i);
			}
		}
		return sel;
	}

	/**
	 * @param valueOf option value
	 * @param cbRows box
	 */
	public static void select(String valueOf, ListBox cbRows) {
		cbRows.setSelectedIndex(getIndexOf(valueOf, cbRows));
	}
}
