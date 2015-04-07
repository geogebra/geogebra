/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.commands;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This implementation is based upon the TreeSet collection class to provide
 * quick lookups and default sorting.
 * 
 */
public class CommandDictionary extends TreeSet<String> {

	private static final long serialVersionUID = 1L;

	/**
	 * Adds an entry to the dictionary.
	 *
	 * @param s
	 *            The string to add to the dictionary.
	 */
	public void addEntry(String s) {
		super.add(s);
	}

	/**
	 * Removes an entry from the dictionary.
	 *
	 * @param s
	 *            The string to remove to the dictionary.
	 * @return True if successful, false if the string is not contained or
	 *         cannot be removed.
	 */
	public boolean removeEntry(String s) {
		return super.remove(s);
	}

	/**
	 * Perform a lookup. This routine returns the closest matching string that
	 * completely contains the given string, or null if none is found.
	 *
	 * @param curr
	 *            The string to use as the base for the lookup.
	 * @return curr The closest matching string that completely contains the
	 *         given string.
	 */
	public String lookup(String curr) {
		if ("".equals(curr))
			return null;
		try {
			SortedSet<String> tailSet = tailSet(curr);
			if (tailSet != null) {
				Object firstObj = tailSet.first();
				if (firstObj != null) {
					String first = (String) firstObj;
					if (first.startsWith(curr))
						return first;
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
}
