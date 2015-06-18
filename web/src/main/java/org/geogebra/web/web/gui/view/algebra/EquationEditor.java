package org.geogebra.web.web.gui.view.algebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AutoCompleteDictionary;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.gui.view.autocompletion.CompletionsPopup;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class EquationEditor {
	protected SuggestBox.SuggestionCallback sugCallback = new SuggestBox.SuggestionCallback() {
		public void onSuggestionSelected(Suggestion s) {

			String sugg = s.getReplacementString();
			// For now, we can assume that sugg is in LaTeX format,
			// and if it will be wrong, we can revise it later
			// at the moment we shall focus on replacing the current
			// word in MathQuillGGB with it...

			// Although MathQuillGGB could compute the current word,
			// it might not be the same as the following, as
			// maybe it can be done easily for English characters
			// but current word shall be internationalized to e.g.
			// Hungarian, or even Arabic, Korean, etc. which are
			// known by GeoGebra but unknown by MathQuillGGB...
			updateCurrentWord(false);
			String currentWord = curWord.toString();

			// So we also provide currentWord as a heuristic or helper:
			org.geogebra.web.html5.main.DrawEquationWeb.writeLatexInPlaceOfCurrentWord(
			        component.getLaTeXSpan(), sugg, currentWord, true);

			// not to forget making the popup disappear after success!
			sug.hideSuggestions();
		}
	};

	protected SuggestOracle.Callback popupCallback = new SuggestOracle.Callback() {
		public void onSuggestionsReady(SuggestOracle.Request req,
		        SuggestOracle.Response res) {
			sug.updateHeight();
			component.updatePosition(sug);
			sug.accessShowSuggestions(res, popup, sugCallback);
		}
	};
	protected AutoCompleteDictionary dict;
	private ScrollableSuggestionDisplay sug;
	public static int querylimit = 5000;
	private List<String> completions;
	StringBuilder curWord;
	private int curWordStart;
	protected CompletionsPopup popup;
	private App app;

	private EquationEditorListener component;
	private int historyIndex;
	private ArrayList<String> history;
	private HashMap<String, String> historyMap;

	public EquationEditor(App app, EquationEditorListener component) {
		this.app = app;
		this.component = component;
		historyIndex = 0;
		history = new ArrayList<String>(50);
		historyMap = new HashMap<String, String>();
		curWord = new StringBuilder();
		popup = new CompletionsPopup();
		popup.addTextField(component);
		sug = new ScrollableSuggestionDisplay();

	}
	/**
	 * Updates curWord to word at current caret position. curWordStart,
	 * curWordEnd are set to this word's start and end position Code copied from
	 * AutoCompleteTextFieldW
	 */
	public void updateCurrentWord(boolean searchRight) {
		int next = updateCurrentWord(searchRight, this.curWord,
		        component.getText(),
		        getCaretPosition());
		if (next > -1) {
			this.curWordStart = next;
		}
	}

	public int getCaretPosition() {
		return org.geogebra.web.html5.main.DrawEquationWeb
		        .getCaretPosInEditedValue(component.getLaTeXSpan());
	}
	


	/**
	 * Code copied from AutoCompleteTextFieldW
	 */
	static int updateCurrentWord(boolean searchRight, StringBuilder curWord,
	        String text, int caretPos) {
		int curWordStart;
		if (text == null)
			return -1;

		if (searchRight) {
			// search to right first to see if we are inside [ ]
			boolean insideBrackets = false;
			curWordStart = caretPos;

			while (curWordStart < text.length()) {
				char c = text.charAt(curWordStart);
				if (c == '[' || c == '(' || c == '{')
					break;
				if (c == ']' || c == ')' || c == '}')
					insideBrackets = true;
				curWordStart++;
			}

			// found [, so go back until we get a ]
			if (insideBrackets) {
				while (caretPos > 0 && text.charAt(caretPos) != '['
				        && text.charAt(caretPos) != '('
				        && text.charAt(caretPos) != '{')
					caretPos--;
			}
		}

		// search to the left
		curWordStart = caretPos - 1;
		while (curWordStart >= 0 &&
		// isLetterOrDigitOrOpenBracket so that F1 works
		        StringUtil.isLetterOrDigitOrUnderscore(text
		                .charAt(curWordStart))) {
			--curWordStart;
		}
		curWordStart++;
		// search to the right
		int curWordEnd = caretPos;
		int length = text.length();
		while (curWordEnd < length
		        && StringUtil.isLetterOrDigitOrUnderscore(text
		                .charAt(curWordEnd)))
			++curWordEnd;

		curWord.setLength(0);
		curWord.append(text.substring(curWordStart, curWordEnd));

		// remove '[' at end
		if (curWord.toString().endsWith("[")
		        || curWord.toString().endsWith("(")
		        || curWord.toString().endsWith("{")) {
			curWord.setLength(curWord.length() - 1);
		}
		return curWordStart;
	}

	public boolean popupSuggestions() {
		// sub, or query is the same as the current word,
		// so moved from method parameter to automatism
		// updateCurrentWord(true);// although true would be nicer here
		updateCurrentWord(false);// compatibility should be preserved
		String sub = this.curWord.toString();
		if (sub != null && !"".equals(sub)) {
			if (sub.length() < 2) {
				// if there is only one letter typed,
				// for any reason, this method should
				// hide the suggestions instead!
				hideSuggestions();
			} else {
				popup.requestSuggestions(new SuggestOracle.Request(sub,
				        querylimit), popupCallback);
			}
		}
		return true;
	}

	public List<String> resetCompletions() {
		String text = component.getText();
		updateCurrentWord(false);
		completions = null;
		// if (isEqualsRequired && !text.startsWith("="))
		// return null;

		String cmdPrefix = curWord.toString();

		// if (korean) {
		// completions = getDictionary().getCompletionsKorean(cmdPrefix);
		// } else {
		completions = getDictionary().getCompletions(cmdPrefix);
		// }

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

	public AutoCompleteDictionary getDictionary() {
		if (this.dict == null) {

			this.dict = component.isForCAS() ? app.getCommandDictionaryCAS()
					:
			app.getCommandDictionary();
			App.debug("COMMANDS LOADED" + dict.size());
		}
		return dict;
	}

	public List<String> getCompletions() {
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

			String syntaxString;
			// if (isCASInput) {
			// syntaxString = loc.getCommandSyntaxCAS(cmdInt);
			// } else {
			syntaxString = app.getLocalization().getCommandSyntax(cmdInt);
			// }
			if (syntaxString.endsWith(// isCASInput ? Localization.syntaxCAS :
			        Localization.syntaxStr)) {

				// command not found, check for macros
				Macro macro = // isCASInput ? null :
				app.getKernel().getMacro(cmd);
				if (macro != null) {
					syntaxes.add(macro.toString());
				} else {
					// syntaxes.add(cmdInt + "[]");
					App.debug("Can't find syntax for: " + cmd);
				}

				continue;
			}
			for (String syntax : syntaxString.split("\\n")) {
				syntaxes.add(syntax);
			}
		}
		return syntaxes;
	}

	public boolean hideSuggestions() {
		if (sug.isSuggestionListShowing()) {
			sug.hideSuggestions();
		}
		return true;
	}

	public void setText(String s, boolean shallfocus) {
		String slatex = historyMap.get(s);
		if (slatex == null) {
			slatex = s;
		}
		org.geogebra.web.html5.main.DrawEquationWeb.updateEditingMathQuillGGB(
				component.getLaTeXSpan(), slatex, shallfocus);
	}

	/**
	 * @return next input from input textfield's history
	 */
	protected String getNextInput() {
		if (historyIndex < history.size())
			++historyIndex;
		if (historyIndex == history.size())
			return null;

		return history.get(historyIndex);
	}

	public void addToHistory(String str, String latex) {
		// exit if the new string is the same as the last entered string
		if (!history.isEmpty() && str.equals(history.get(history.size() - 1)))
			return;

		history.add(str);
		historyIndex = history.size();
		historyMap.put(str, latex);
	}

	/**
	 * @return previous input from input textfield's history
	 */
	protected String getPreviousInput() {
		if (history.size() == 0)
			return null;
		if (historyIndex > 0)
			--historyIndex;
		return history.get(historyIndex);
	}

	public ArrayList<String> getHistory() {
		return history;
	}
	
	public boolean isSuggesting() {
		return sug.isSuggestionListShowing();
	}

	public static void updateNewStatic(Element se) {
		se.getStyle().setProperty("display", "-moz-inline-box");
		se.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		se.setDir("ltr");
	}

	/**
	 * As adding focus handlers to JavaScript code would be too complex, let's
	 * do it even before they actually get focus, i.e. make a method that
	 * triggers focus, and then override it if necessary
	 * 
	 * @param b
	 *            focus (false: blur)
	 */
	public void setFocus(boolean b) {
		org.geogebra.web.html5.main.DrawEquationWeb.focusEquationMathQuillGGB(
		        component.getLaTeXSpan(), b);

		// as the focus operation sometimes also scrolls
		// if (b)
		// geogebra.html5.main.DrawEquationWeb.scrollCursorIntoView(this,
		// seMayLatex);
		// put to focus handler
	}

	public boolean shuffleSuggestions(boolean down) {
		if (this.sug.isSuggestionListShowing()) {
			if (down) {
				sug.accessMoveSelectionDown();
			} else {
				sug.accessMoveSelectionUp();
			}
			return true;
		}
		return false;
	}

	public boolean needsEnterForSuggestion() {
		if (sug.isSuggestionListShowing()) {
			sugCallback.onSuggestionSelected(sug.accessCurrentSelection());
			return true;
		}
		return false;
	}

	public void resetLanguage() {
		dict = null;
	}

}
