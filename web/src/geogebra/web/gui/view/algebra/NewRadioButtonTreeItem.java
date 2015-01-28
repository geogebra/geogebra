package geogebra.web.gui.view.algebra;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.util.AutoCompleteDictionary;
import geogebra.common.util.StringUtil;
import geogebra.html5.gui.inputfield.AutoCompleteW;

import java.util.ArrayList;
import java.util.List;

/**
 * NewRadioButtonTreeItem for creating new formulas in the algebra view
 * 
 * File created by Arpad Fekete
 */
public class NewRadioButtonTreeItem extends RadioButtonTreeItem implements
        AutoCompleteW {

	private List<String> completions;
	private StringBuilder curWord;
	private int curWordStart;
	protected AutoCompleteDictionary dict;

	public NewRadioButtonTreeItem(Kernel kern) {
		super(kern);
		curWord = new StringBuilder();
	}

	public boolean getAutoComplete() {
		return true;
	}

	public String getText() {
		return geogebra.html5.main.DrawEquationWeb
		        .getActualEditedValue(seMayLatex);
	}

	public List<String> resetCompletions() {
		String text = getText();
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

	public AutoCompleteDictionary getDictionary() {
		if (this.dict == null) {
			this.dict = // this.forCAS ? app.getCommandDictionaryCAS() :
			app.getCommandDictionary();
		}
		return dict;
	}

	/**
	 * Updates curWord to word at current caret position. curWordStart,
	 * curWordEnd are set to this word's start and end position Code copied from
	 * AutoCompleteTextFieldW
	 */
	public void updateCurrentWord(boolean searchRight) {
		int next = updateCurrentWord(searchRight, this.curWord, getText(),
		        getCaretPosition());
		if (next > -1) {
			this.curWordStart = next;
		}
	}

	public int getCaretPosition() {
		return geogebra.html5.main.DrawEquationWeb
		        .getCaretPosInEditedValue(seMayLatex);
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
				if (c == '[')
					break;
				if (c == ']')
					insideBrackets = true;
				curWordStart++;
			}

			// found [, so go back until we get a ]
			if (insideBrackets) {
				while (caretPos > 0 && text.charAt(caretPos) != '[')
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
		if (curWord.toString().endsWith("[")) {
			curWord.setLength(curWord.length() - 1);
		}
		return curWordStart;
	}

	public List<String> getCompletions() {
		return completions;
	}
}
