package org.geogebra.web.web.gui.view.algebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AutoCompleteDictionary;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.view.autocompletion.CompletionsPopup;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.MathQuillHelper;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class EquationEditor {
	protected SuggestBox.SuggestionCallback sugCallback = new SuggestBox.SuggestionCallback() {
		public void onSuggestionSelected(Suggestion s) {

			String sugg = s.getReplacementString();
			autocomplete(sugg, true);
		}
	};

	protected SuggestOracle.Callback popupCallback = new SuggestOracle.Callback() {
		public void onSuggestionsReady(SuggestOracle.Request req,
		        SuggestOracle.Response res) {
			updateSuggestions(res);
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
		sug = new ScrollableSuggestionDisplay(this);

	}

	protected void updateSuggestions(Response res) {
		sug.updateHeight();
		component.updatePosition(sug);
		sug.accessShowSuggestions(res, popup, sugCallback);

	}

	public void autocomplete(String sugg, boolean replace) {
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
		MathQuillHelper.writeLatexInPlaceOfCurrentWord(null,
				component.getLaTeXElement(),
						sugg, replace ? currentWord : "", true);

		// not to forget making the popup disappear after success!
		sug.hideSuggestions();

	}
	/**
	 * Updates curWord to word at current caret position. curWordStart,
	 * curWordEnd are set to this word's start and end position Code copied from
	 * AutoCompleteTextFieldW
	 */
	public void updateCurrentWord(boolean searchRight) {
		int next = InputHelper.updateCurrentWord(searchRight, this.curWord,
		        component.getText(),
				getCaretPosition(), false);
		if (next > -1) {
			this.curWordStart = next;
		}
	}

	public int getCaretPosition() {
		return MathQuillHelper
				.getCaretPosInEditedValue(component.getLaTeXElement());
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
				popup.requestSuggestions(new SuggestOracle.Request(
						this.curWord.toString(),
				        querylimit), popupCallback);
			}
		} else {
			hideSuggestions();
		}
		return true;
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
				return null;			
		} 

		String cmdPrefix = curWord.toString();

		if (korean) {
			completions = getDictionary().getCompletionsKorean(cmdPrefix);
		} else {
			completions = getDictionary().getCompletions(cmdPrefix);
		}

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
			Localization loc = app.getLocalization();
			String syntaxString;
			if (component.isForCAS()) {
				syntaxString = app.getLocalization()
						.getCommandSyntaxCAS(cmdInt);
			} else {
				syntaxString = app.getExam() == null ? loc
						.getCommandSyntax(cmdInt) : app.getExam().getSyntax(
						cmdInt, loc);
			}
			if (syntaxString
					.endsWith(component.isForCAS() ? Localization.syntaxCAS
							:
			        Localization.syntaxStr)) {

				// command not found, check for macros
				Macro macro = component.isForCAS() ? null :
				app.getKernel().getMacro(cmd);
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
		Log.debug("HIST READ" + slatex + "," + s);
		if (slatex != null) {
			slatex = slatex.replace("\\$", "\\dollar ")
					.replace("$", "\\dollar ").replace("(", "\\left(")
					.replace(")", "\\right)")
					.replace("\\left\\left(", "\\left(")
					.replace("\\right\\right)", "\\right)");
		}
		MathQuillHelper.updateEditingMathQuillGGB(
				component.getLaTeXElement(), slatex, shallfocus);
	}

	public String getText() {
		return component.getText();
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

	/**
	 * @param str
	 *            plain text
	 * @param latex
	 *            latex
	 */
	public void addToHistory(String str, String latex) {
		// exit if the new string is the same as the last entered string
		if (!history.isEmpty() && str.equals(history.get(history.size() - 1)))
			return;
		history.add(str);
		historyIndex = history.size();
		if (latex != null) {
			historyMap.put(str, latex);
		}
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

	@Deprecated
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
		MathQuillHelper.focusEquationMathQuillGGB(
		        component.getLaTeXElement(), b);

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

	public double getMaxSuggestionsHeight() {
		double ret = (app.getHeight() / 2);
		if (component != null) {
			ret = Math
					.max(29, Math.min(ret,
							app.getHeight()
									+ ((AppW) app).getAbsTop()
									- component.getAbsoluteTop()
					- component.toWidget().getOffsetHeight()
									- ((AppW) app).getAppletFrame()
											.getKeyboardHeight()));
		}
		return ret;
	}

	public void setLaTeX(String plain, String latex) {
		if (this.historyMap.containsKey(plain) || latex == null) {
			return;
		}
		historyMap.put(plain, latex);

	}

	public static String stopCommon(String newValue0) {
		String newValue = newValue0;
		// newValue = newValue0.replace("space *", " ");
		// newValue = newValue.replace("* space", " ");

		// newValue = newValue.replace("space*", " ");
		// newValue = newValue.replace("*space", " ");

		newValue = newValue.replace("space ", " ");
		newValue = newValue.replace(" space", " ");
		newValue = newValue.replace("space", " ");

		// \" is the " Quotation delimiter returned by MathQuillGGB
		// now it's handy that "space" is not in newValue
		newValue = newValue.replace("\\\"", "space");

		// change \" to corresponding unicode characters
		StringBuilder sb = new StringBuilder();
		StringUtil.processQuotes(sb, newValue, Unicode.OPEN_DOUBLE_QUOTE);
		newValue = sb.toString();

		newValue = newValue.replace("space", "\"");

		// do not substitute for absolute value in these cases
		newValue = newValue.replace("||", ExpressionNodeConstants.strOR);
		return newValue;
	}

	public String getCurrentCommand() {
		this.updateCurrentWord(true);
		return curWord.toString();
	}

}
