package org.geogebra.web.web.gui.inputfield;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AutoCompleteDictionary;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;
import org.geogebra.web.html5.gui.view.autocompletion.CompletionsPopup;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class InputSuggestions implements HasSuggestions {
	protected AutoCompleteDictionary dict;
	private ScrollableSuggestionDisplay sug;
	public static int querylimit = 5000;
	private List<String> completions;
	StringBuilder curWord;
	private int curWordStart;
	protected CompletionsPopup popup;
	private App app;

	private AutoCompleteW component;

	public InputSuggestions(App app, AutoCompleteW component) {
		this.app = app;
		this.component = component;
		curWord = new StringBuilder();
		popup = new CompletionsPopup();
		popup.addTextField(component);
		sug = new ScrollableSuggestionDisplay(this);

	}

	protected SuggestOracle.Callback popupCallback = new SuggestOracle.Callback() {
		public void onSuggestionsReady(SuggestOracle.Request req,
				SuggestOracle.Response res) {
			updateSuggestions(res);
		}
	};

	protected SuggestBox.SuggestionCallback sugCallback = new SuggestBox.SuggestionCallback() {
		public void onSuggestionSelected(Suggestion s) {

			String sugg = s.getReplacementString();
			autocomplete(sugg, true);
		}
	};

	public void updateCurrentWord(boolean searchRight) {
		// TODO
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

	public void autocomplete(String sugg, boolean replace) {
		component.insertString(sugg);
		sug.hideSuggestions();
	}

	public boolean popupSuggestions() {
		// sub, or query is the same as the current word,
		// so moved from method parameter to automatism
		// updateCurrentWord(true);// although true would be nicer here
		updateCurrentWord(false);// compatibility should be preserved
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
				popup.requestSuggestions(
						new SuggestOracle.Request(this.curWord.toString(),
								querylimit), popupCallback);
			}
		} else {
			hideSuggestions();
		}
		return true;
	}

	public boolean hideSuggestions() {
		if (sug.isSuggestionListShowing()) {
			sug.hideSuggestions();
		}
		return true;
	}

	protected void updateSuggestions(Response res) {
		sug.updateHeight();
		component.updatePosition(sug);
		sug.accessShowSuggestions(res, popup, sugCallback);

	}

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
		// maybe this was also done in the Web case in MathQuillGGB, but
		// it should not do harm to check it twice...
		if (!InputHelper.needsAutocomplete(curWord, app.getKernel())) {
			completions = null;
			Log.debug("ABORT");
			return null;
		}

		String cmdPrefix = curWord.toString();

		if (korean) {
			completions = getDictionary().getCompletionsKorean(cmdPrefix);
		} else {
			completions = getDictionary().getCompletions(cmdPrefix);
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

	/*
	 * Take a list of commands and return all possible syntaxes for these
	 * commands
	 */
	private List<String> getSyntaxes(List<String> commands) {
		if (commands == null) {
			return null;
		}
		ArrayList<String> syntaxes = new ArrayList<String>();
		for (String cmd : commands) {

			String cmdInt = app.getInternalCommand(cmd);
			Localization loc = app.getLocalization();
			String syntaxString;
			if (component.isForCAS()) {
				syntaxString = app.getLocalization()
						.getCommandSyntaxCAS(cmdInt);
			} else {
				syntaxString = app.getExam() == null
						? loc.getCommandSyntax(cmdInt)
						: app.getExam().getSyntax(cmdInt, loc);
			}
			if (syntaxString.endsWith(component.isForCAS()
					? Localization.syntaxCAS : Localization.syntaxStr)) {

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

	public boolean isSuggesting() {
		return sug.isSuggestionListShowing();
	}

	public boolean needsEnterForSuggestion() {
		if (sug.isSuggestionListShowing()) {
			sugCallback.onSuggestionSelected(sug.accessCurrentSelection());
			return true;
		}
		return false;
	}
}
