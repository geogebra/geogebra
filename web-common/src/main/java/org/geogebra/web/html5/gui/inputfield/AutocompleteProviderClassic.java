package org.geogebra.web.html5.gui.inputfield;

import java.util.List;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.main.App;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.util.AutoCompleteDictionary;

/**
 * Autocomplete provider for plain text editor
 */
public class AutocompleteProviderClassic {
	protected final AutocompleteProvider provider;
	private List<String> completions;
	private AutoCompleteDictionary dict;
	private App app;
	private boolean forCAS;

	/**
	 * @param app
	 *            application
	 * @param isForCas
	 *            whether to use CAS view dictionary
	 */
	public AutocompleteProviderClassic(App app, boolean isForCas) {
		this.app = app;
		this.forCAS = isForCas;
		this.provider = new AutocompleteProvider(app, isForCas);
	}

	/**
	 * @return completions
	 */
	public List<String> getCompletions() {
		return completions;
	}

	/**
	 * Update completions from input.
	 * 
	 * @param currentWord
	 *            sequence of alphanumeric characters around the cursor
	 * 
	 * @return completions for current word
	 */
	public List<String> resetCompletions(CharSequence currentWord) {
		completions = null;

		boolean korean = false;
		if (app.getLocalization() != null) {
			korean = "ko".equals(app.getLocalization().getLanguage());
		}

		// start autocompletion only for words with at least two characters
		if (!needsAutocomplete(currentWord)) {
			completions = null;
			return null;
		}

		String cmdPrefix = currentWord.toString();

		if (korean) {
			completions = getDictionary().getCompletionsKorean(cmdPrefix);
		} else {
			completions = getDictionary().getCompletions(cmdPrefix);
		}

		if (completions == null && provider.isFallbackCompletionAllowed()) {
			completions = app.getEnglishCommandDictionary()
					.getCompletions(cmdPrefix);
		}

		List<String> commandCompletions = provider.getSyntaxes(completions);

		// Start with the built-in function completions
		completions = app.getParserFunctions().getCompletions(cmdPrefix);
		addToCompletions(commandCompletions);
		return completions;
	}

	private void addToCompletions(List<String> commandCompletions) {
		if (isNullOrEmpty(commandCompletions)) {
			return;
		}
		if (completions.isEmpty()) {
			completions = commandCompletions;
		} else {
			completions.addAll(commandCompletions);
		}
	}

	private boolean isNullOrEmpty(List<String> list) {
		return list == null || list.isEmpty() || (list.size() == 1 && list.get(0).isEmpty());
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
		if (this.dict == null) {
			this.dict = forCAS ? app.getCommandDictionaryCAS()
					: app.getCommandDictionary();
		}
		return dict;
	}

	/**
	 * Reset completions
	 */
	public void cancelAutoCompletion() {
		completions = null;
	}

	/**
	 * @param forCAS
	 *            whether the dictionary is for CAS view
	 */
	public void setDictionary(boolean forCAS) {
		this.forCAS = forCAS;
		this.dict = null;
	}

}
