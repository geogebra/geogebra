package org.geogebra.web.full.gui.inputfield;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AutoCompleteDictionary;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;
import org.geogebra.web.html5.gui.view.autocompletion.CompletionsPopup;
import org.geogebra.web.html5.gui.view.autocompletion.GSuggestBox;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.himamis.retex.editor.share.model.Korean;

public class InputSuggestions implements HasSuggestions {
	protected AutoCompleteDictionary dict;
	private ScrollableSuggestionDisplay sug;
	public static final int QUERY_LIMIT = 5000;
	private List<String> completions;
	StringBuilder curWord;
	protected CompletionsPopup popup;
	private App app;

	private AutoCompleteW component;

	/**
	 * @param app
	 *            application
	 * @param component
	 *            text input
	 */
	public InputSuggestions(AppW app, AutoCompleteW component) {
		this.app = app;
		this.component = component;
		curWord = new StringBuilder();
		popup = new CompletionsPopup();
		popup.addTextField(component);
		sug = new ScrollableSuggestionDisplay(this, app.getPanel(), app);
	}

	protected SuggestOracle.Callback popupCallback = new SuggestOracle.Callback() {
		@Override
		public void onSuggestionsReady(SuggestOracle.Request req,
				SuggestOracle.Response res) {
			updateSuggestions(res);

		}
	};

	protected GSuggestBox.SuggestionCallback sugCallback = new GSuggestBox.SuggestionCallback() {
		@Override
		public void onSuggestionSelected(Suggestion s) {

			String sugg = s.getReplacementString();
			autocompleteAndHide(sugg);
		}
	};

	/**
	 * @param searchRight
	 *            TODO whether to check chars to the right?
	 */
	public void updateCurrentWord(boolean searchRight) {
		curWord = new StringBuilder(component.getCommand());
		// int next = InputHelper.updateCurrentWord(searchRight, this.curWord,
		// component.getText(), getCaretPosition(), false);
		// if (next > -1) {
		// this.curWordStart = next;
		// }
	}

	public int getCaretPosition() {
		return 0;
	}

	/**
	 * @param sugg
	 *            suggestion
	 */
	public void autocompleteAndHide(String sugg) {
		component.insertString(sugg);
		sug.hideSuggestions();
	}

	/**
	 * Show suggestions.
	 * 
	 * @return true
	 */
	public boolean popupSuggestions() {
		// sub, or query is the same as the current word,
		// so moved from method parameter to automatism
		// updateCurrentWord(true);// although true would be nicer here
		updateCurrentWord(false); // compatibility should be preserved
		if (curWord != null && curWord.length() > 0
				&& !"sqrt".equals(curWord.toString())) {
			// for length check we also need flattenKorean
			if (!InputHelper.needsAutocomplete(this.curWord, app.getKernel())) {
				// if there is only one letter typed,
				// for any reason, this method should
				// hide the suggestions instead!
				hideSuggestions();
			} else {
				Log.debug("requestingSug" + curWord);
				Log.debug(
						"Korean:" + Korean.unflattenKorean(curWord.toString()));
				popup.requestSuggestions(
						new SuggestOracle.Request(this.curWord.toString(),
								QUERY_LIMIT), popupCallback);
			}
		} else {
			hideSuggestions();
		}
		return true;
	}

	/**
	 * Hide the suggestions.
	 * 
	 * @return true
	 */
	public boolean hideSuggestions() {
		if (sug.isSuggestionListShowing()) {
			sug.hideSuggestions();
		}
		return true;
	}

	/**
	 * Update the suggestions.
	 * 
	 * @param res
	 *            oracle response
	 */
	protected void updateSuggestions(Response res) {
		sug.updateHeight();
		component.updatePosition(sug);
		sug.accessShowSuggestions(res, popup, sugCallback);
	}

	@Override
	public double getMaxSuggestionsHeight() {
		double ret = (app.getHeight() / 2);
		if (component != null) {
			ret = Math.max(29, Math.min(ret, app.getHeight()
					+ ((AppW) app).getAbsTop() - component.getAbsoluteTop()
					- component.toWidget().getOffsetHeight()
					- ((AppW) app).getAppletFrame().getKeyboardHeight()));
		}
		return ret;
	}

	/**
	 * Update completions from input.
	 * 
	 * @return completions for current word
	 */
	public List<String> resetCompletions() {
		updateCurrentWord(false);
		completions = null;
		// if (isEqualsRequired && !text.startsWith("="))
		// return null;

		boolean korean = false;
		if (app.getLocalization() != null) {
			korean = "ko".equals(app.getLocalization().getLanguage());
		}

		// start autocompletion only for words with at least two characters
		if (!InputHelper.needsAutocomplete(curWord, app.getKernel())) {
			completions = null;
			return null;
		}

		String cmdPrefix = curWord.toString();

		if (korean) {
			completions = getDictionary().getCompletionsKorean(cmdPrefix);
		} else {
			completions = getDictionary().getCompletions(cmdPrefix);
		}

		if (completions == null && isFallbackCompletitionAllowed()) {
			completions = app.getEnglishCommandDictionary().getCompletions(cmdPrefix);
		}

		Log.debug(cmdPrefix + ":"
				+ (completions == null ? "-1" : completions.size()));
		List<String> commandCompletions = getSyntaxes(completions);

		// Start with the built-in function completions
		completions = app.getParserFunctions().getCompletions(cmdPrefix);
		// Then add the command completions
		if (completions.isEmpty()) {
			completions = commandCompletions;
		} else if (commandCompletions != null) {
			completions.addAll(commandCompletions);
		}
		return completions;
	}

	private boolean isFallbackCompletitionAllowed() {
		return app.has(Feature.COMMAND_COMPLETION_FALLBACK)
				&& "zh".equals(app.getLocalization().getLanguage());
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
			boolean englishOnly = cmdInt == null && isFallbackCompletitionAllowed();

			if (englishOnly) {
				cmdInt = app.englishToInternal(cmd);
			}
			String syntaxString;
			if (component.isForCAS()) {
				syntaxString = app.getLocalization()
						.getCommandSyntaxCAS(cmdInt);
			} else {
				AlgebraProcessor ap = app.getKernel().getAlgebraProcessor();
				syntaxString = englishOnly ? ap.getEnglishSyntax(cmdInt, app.getSettings())
						: ap.getSyntax(cmdInt, app.getSettings());
			}

			if (syntaxString == null) {
				return syntaxes;
			}

			if (syntaxString.endsWith(Localization.syntaxCAS)
					|| syntaxString.endsWith(Localization.syntaxStr)) {
				// command not found, check for macros
				Macro macro = component.isForCAS() ? null
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
			this.dict = component.isForCAS() ? app.getCommandDictionaryCAS()
					: app.getCommandDictionary();
		}
		return dict;
	}

	public List<String> getCompletions() {
		return completions;
	}

	public void setFocus() {
		sug.focus();
	}

	public boolean isSuggesting() {
		return sug.isSuggestionListShowing();
	}

	/**
	 * @return whether enter should be consumend by suggestions
	 */
	public boolean needsEnterForSuggestion() {
		if (sug.isSuggestionListShowing()) {
			sugCallback.onSuggestionSelected(sug.accessCurrentSelection());
			return true;
		}
		return false;
	}

	public void onKeyDown() {
		sug.moveSelectionDown();
	}

	public void onKeyUp() {
		sug.moveSelectionUp();
	}

}
