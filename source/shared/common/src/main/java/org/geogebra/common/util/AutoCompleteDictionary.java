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

package org.geogebra.common.util;

import java.util.List;

/**
 * This interface defines the API that dictionaries for autocomplete components
 * must implement. Note that implementations of this interface should perform
 * look ups as quickly as possible to avoid delays as the user types.
 */
public interface AutoCompleteDictionary extends Iterable<String> {
	/**
	 * Adds an entry to the dictionary.
	 *
	 * @param s
	 *            The string to add to the dictionary.
	 */
	void addEntry(String s);

	/**
	 * Removes an entry from the dictionary.
	 *
	 * @param s
	 *            The string to remove to the dictionary.
	 * @return True if successful, false if the string is not contained or
	 *         cannot be removed.
	 */
	boolean removeEntry(String s);

	/**
	 * Perform a lookup and returns the closest matching string to the passed
	 * string.
	 *
	 * @param s
	 *            The string to use as the base for the lookup. How this routine
	 *            is implemented determines the behaviour of the component.
	 *            Typically, the closest matching string that completely
	 *            contains the given string is returned.
	 * @return null if no matching string, the closest matching string otherwise
	 * 
	 */
	String lookup(String s);

	/**
	 * Find all possible completions of the argument and return them.
	 *
	 * @param s
	 *            The string to use as the base for the lookup. How this routine
	 *            is implemented determines the behaviour of the component.
	 *            Typically, the closest matching string that completely
	 *            contains the given string is returned.
	 * @return an iterable of strings if there is at least one completion,
	 *         otherwise null.
	 */
	List<MatchedString> getCompletions(String s);

	/**
	 * @param cmdPrefix
	 *            prefix
	 * @return completions
	 */
	List<MatchedString> getCompletionsKorean(String cmdPrefix);

	/**
	 * @return number of completions
	 */
	int size();
}
