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

package org.geogebra.web.html5.gui.inputfield;

import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.main.App;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.util.AutoCompleteDictionary;
import org.geogebra.common.util.MatchedString;

/**
 * Autocomplete provider for plain text editor
 */
public class AutocompleteProviderClassic {
	protected final AutocompleteProvider provider;
	private List<MatchedString> completions;
	private App app;

	/**
	 * @param app
	 *            application
	 */
	public AutocompleteProviderClassic(App app) {
		this.app = app;
		this.provider = new AutocompleteProvider(app, false);
	}

	/**
	 * @return completions
	 */
	public List<MatchedString> getCompletions() {
		return completions;
	}

	/**
	 * Update completions from input.
	 * 
	 * @param currentWord
	 *            sequence of alphanumeric characters around the cursor
	 */
	public void resetCompletions(CharSequence currentWord) {
		completions = null;

		boolean korean = false;
		if (app.getLocalization() != null) {
			korean = app.getLocalization().languageIs("ko");
		}

		// start autocompletion only for words with at least two characters
		if (!needsAutocomplete(currentWord)) {
			completions = null;
			return;
		}

		String cmdPrefix = currentWord.toString();
		List<MatchedString> completionMatches;
		if (korean) {
			completionMatches = getDictionary().getCompletionsKorean(cmdPrefix);
		} else {
			completionMatches = getDictionary().getCompletions(cmdPrefix);
		}

		if (completionMatches == null && provider.isFallbackCompletionAllowed()) {
			completionMatches = app.getEnglishCommandDictionary()
					.getCompletions(cmdPrefix);
		}

		List<MatchedString> commandCompletions = provider.getSyntaxes(completionMatches);

		// Start with the built-in function completions
		completions = app.getParserFunctions().getCompletions(cmdPrefix).stream()
				.map(c -> new MatchedString(c, 0, currentWord.length()))
				.collect(Collectors.toList());
		addToCompletions(commandCompletions);
	}

	private void addToCompletions(List<MatchedString> commandCompletions) {
		if (isNullOrEmpty(commandCompletions)) {
			return;
		}
		if (completions.isEmpty()) {
			completions = commandCompletions;
		} else {
			completions.addAll(commandCompletions);
		}
	}

	private boolean isNullOrEmpty(List<MatchedString> list) {
		return list == null || list.isEmpty();
	}

	/**
	 * @param currentWord
	 *            current word
	 * @return whether the word is long enough to trigger autocomplete
	 */
	protected boolean needsAutocomplete(CharSequence currentWord) {
		return InputHelper.needsAutocomplete(currentWord, app.getKernel());
	}

	/**
	 * Lazy load the dictionary.
	 * 
	 * @return dictionary of completions
	 */
	public AutoCompleteDictionary getDictionary() {
		return app.getCommandDictionary();
	}

	/**
	 * Reset completions
	 */
	public void cancelAutoCompletion() {
		completions = null;
	}

}
