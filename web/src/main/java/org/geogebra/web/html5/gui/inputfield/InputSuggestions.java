package org.geogebra.web.html5.gui.inputfield;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AutoCompleteDictionary;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;

/**
 * Autocomplete controller for both plain text and equation editor input
 */
public class InputSuggestions {
	private List<String> completions;
	private AutoCompleteDictionary dict;
	private AppW app;
	private boolean forCAS;

	/**
	 * @param app
	 *            application
	 * @param isForCas
	 *            whether to use CAS view dictionary
	 */
	public InputSuggestions(AppW app, boolean isForCas) {
		this.app = app;
		this.forCAS = isForCas;
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
	public List<String> resetCompletions(StringBuilder currentWord) {
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

		if (completions == null && isFallbackCompletitionAllowed()) {
			completions = app.getEnglishCommandDictionary()
					.getCompletions(cmdPrefix);
		}

		Log.debug(cmdPrefix + ":"
				+ (completions == null ? "-1" : completions.size()));
		List<String> commandCompletions = getSyntaxes(completions);

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
	protected boolean needsAutocomplete(StringBuilder currentWord) {
		return InputHelper.needsAutocomplete(currentWord, app.getKernel());
	}

	/**
	 * Take a list of commands and return all possible syntaxes for these
	 * commands
	 * 
	 * @param commands
	 *            commands
	 * @return syntaxes
	 */
	public List<String> getSyntaxes(List<String> commands) {
		if (commands == null) {
			return null;
		}
		ArrayList<String> syntaxes = new ArrayList<>();
		for (String cmd : commands) {
			String cmdInt = app.getInternalCommand(cmd);
			boolean englishOnly = cmdInt == null
					&& isFallbackCompletitionAllowed();

			if (englishOnly) {
				cmdInt = app.englishToInternal(cmd);
			}
			String syntaxString;
			if (forCAS) {
				syntaxString = app.getLocalization()
						.getCommandSyntaxCAS(cmdInt);
			} else {
				AlgebraProcessor ap = app.getKernel().getAlgebraProcessor();
				syntaxString = englishOnly
						? ap.getEnglishSyntax(cmdInt, app.getSettings())
						: ap.getSyntax(cmdInt, app.getSettings());
			}

			if (syntaxString == null) {
				return syntaxes;
			}

			if (syntaxString.endsWith(Localization.syntaxCAS)
					|| syntaxString.endsWith(Localization.syntaxStr)) {
				// command not found, check for macros
				Macro macro = forCAS ? null
						: app.getKernel().getMacro(cmd);
				if (macro != null) {
					syntaxes.add(macro.toString());
				} else {
					// syntaxes.add(cmdInt + "[]");
					Log.debug("Can't find syntax for: " + cmd);
				}

				continue;
			}
			for (String syntax : syntaxString.split("\\n")) {
				syntaxes.add(syntax);
			}
		}
		return syntaxes;
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
	 * @return whether to allow English commands as well
	 */
	protected boolean isFallbackCompletitionAllowed() {
		return "zh".equals(app.getLocalization().getLanguage());
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
