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

package org.geogebra.desktop.gui.autocompletion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract base class for {@link CompletionProvider} implementations. Data is
 * backed by an array which will be sorted in the constructor.
 * 
 * @param <T>
 *            The type of the completion options; must implement Comparable
 * 
 * @author Julian Lettner
 */
public abstract class SortedArrayCompletionProvider<T extends Comparable<T>>
		implements CompletionProvider<T> {

	private final T[] sortedCompletionOptions;
	private final boolean caseInsensitiveCompletion;

	/**
	 * Constructor will sort the array. The caller is responsible for creating a
	 * defensive copy.
	 * 
	 * @param unsortedCompletionOptions
	 *            An unsorted array of completion options which will be sorted
	 * @param caseInsensitiveCompletion
	 *            Should the completion be case insensitive?
	 * 
	 */
	public SortedArrayCompletionProvider(T[] unsortedCompletionOptions,
			boolean caseInsensitiveCompletion) {
		this.sortedCompletionOptions = unsortedCompletionOptions;
		this.caseInsensitiveCompletion = caseInsensitiveCompletion;
		// Sort for an intuitive user experience
		Arrays.sort(sortedCompletionOptions);
	}

	@Override
	public List<T> getCompletionOptions(String prefix) {
		// Proper case for prefix
		final String prefixWithProperCase = caseInsensitiveCompletion
				? prefix.toLowerCase() : prefix;

		List<T> options = new ArrayList<>();
		for (T option : sortedCompletionOptions) {
			String optionString = toString(option);
			if (isValidCompletionOption(prefixWithProperCase, optionString)) {
				options.add(option);
			}
		}

		// If there is only a single matching option which has the same length
		// as the prefix, remove it
		if (1 == options.size()
				&& prefix.length() == toString(options.get(0)).length()) {
			options.clear();
		}

		return options;
	}

	private boolean isValidCompletionOption(String prefixWithProperCase, String option) {
		return (caseInsensitiveCompletion ? option.toLowerCase() : option)
				.startsWith(prefixWithProperCase);
	}

}