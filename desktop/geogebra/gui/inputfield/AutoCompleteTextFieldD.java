package geogebra.gui.inputfield;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.draw.DrawTextField;
import geogebra.common.euclidian.event.FocusListener;
import geogebra.common.gui.inputfield.AutoComplete;
import geogebra.common.javax.swing.GLabel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.MyException;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.main.App;
import geogebra.common.main.MyError;
import geogebra.common.util.AutoCompleteDictionary;
import geogebra.common.util.Korean;
import geogebra.common.util.StringUtil;
import geogebra.gui.autocompletion.CommandCompletionListCellRenderer;
import geogebra.gui.autocompletion.CompletionsPopup;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.main.AppD;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class AutoCompleteTextFieldD extends MathTextField implements
AutoComplete, geogebra.common.gui.inputfield.AutoCompleteTextField {

	private static final long serialVersionUID = 1L;

	private AppD app;
	private StringBuilder curWord;
	private int curWordStart;

	protected AutoCompleteDictionary dict;
	protected boolean isCASInput = false;
	protected boolean autoComplete;
	private int historyIndex;
	private ArrayList<String> history;

	private boolean handleEscapeKey = false;

	private List<String> completions;
	private String cmdPrefix;
	private CompletionsPopup completionsPopup;

	private HistoryPopupD historyPopup;

	private DrawTextField drawTextField = null;

	/**
	 * Flag to determine if text must start with "=" to activate autoComplete;
	 * used with spreadsheet cells
	 */
	private boolean isEqualsRequired = false;

	/**
	 * Pattern to find an argument description as found in the syntax
	 * information of a command.
	 */
	// private static Pattern syntaxArgPattern =
	// Pattern.compile("[,\\[] *(?:<[\\(\\) \\-\\p{L}]*>|\\.\\.\\.) *(?=[,\\]])");
	// Simplified to this as there are too many non-alphabetic character in
	// parameter descriptions:
	private static Pattern syntaxArgPattern = Pattern
			.compile("[,\\[] *(?:<.*?>|\"<.*?>\"|\\.\\.\\.) *(?=[,\\]])");

	/**
	 * Constructs a new AutoCompleteTextField that uses the dictionary of the
	 * given Application for autocomplete look up. A default model is created
	 * and the number of columns is 0.
	 * 
	 */
	public AutoCompleteTextFieldD(int columns, App app) {
		this(columns, (AppD) app, true);
	}

	/**
	 * @param columns
	 *            width
	 * @param app
	 *            Application
	 * @param handleEscapeKey
	 *            how to handle escape key
	 * @param dict
	 *            dictionary
	 */
	public AutoCompleteTextFieldD(int columns, AppD app,
			boolean handleEscapeKey, AutoCompleteDictionary dict) {
		super(app);
		// allow dynamic width with columns = -1
		if (columns > 0)
			setColumns(columns);

		this.app = app;
		setAutoComplete(true);
		this.handleEscapeKey = handleEscapeKey;
		curWord = new StringBuilder();

		historyIndex = 0;
		history = new ArrayList<String>(50);

		completions = null;

		CommandCompletionListCellRenderer cellRenderer = new CommandCompletionListCellRenderer();
		completionsPopup = new CompletionsPopup(this, cellRenderer, 6);
		// addKeyListener(this); now in MathTextField
		setDictionary(dict);
	}

	public AutoCompleteTextFieldD(int columns, AppD app, boolean handleEscapeKey) {
		this(columns, app, handleEscapeKey, app.getCommandDictionary());
		// setDictionary(app.getAllCommandsDictionary());
	}

	public AutoCompleteTextFieldD(int columns, App app, Drawable drawTextField) {
		this(columns, app);
		this.drawTextField = (DrawTextField) drawTextField;
	}

	public DrawTextField getDrawTextField() {
		return drawTextField;
	}

	public ArrayList<String> getHistory() {
		return history;
	}

	/**
	 * Add a history popup list and an embedded popup button. See
	 * AlgebraInputBar
	 */
	public void addHistoryPopup(boolean isDownPopup) {

		if (historyPopup == null)
			historyPopup = new HistoryPopupD(this);

		historyPopup.setDownPopup(isDownPopup);

		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				if (cmd.equals(1 + BorderButtonD.cmdSuffix)) {

					// TODO: should up/down orientation be tied to InputBar?
					// show popup
					historyPopup.showPopup();
				}
			}
		};
		setBorderButton(1, GeoGebraIcon.createUpDownTriangleIcon(false, true),
				al);
		this.setBorderButtonVisible(1, false);
	}

	public void showPopupSymbolButton(boolean showPopupSymbolButton) {
		((MyTextField) this).setShowSymbolTableIcon(showPopupSymbolButton);
	}

	/**
	 * Set whether this is a CAS Input field
	 */
	public void setCASInput(boolean val) {
		isCASInput = val;
	}

	/**
	 * Set the dictionary that autocomplete lookup should be performed by.
	 * 
	 * @param dict
	 *            The dictionary that will be used for the autocomplete lookups.
	 */
	public void setDictionary(AutoCompleteDictionary dict) {
		this.dict = dict;
	}

	/**
	 * Gets the dictionary currently used for lookups.
	 * 
	 * @return dict The dictionary that will be used for the autocomplete
	 *         lookups.
	 */
	public AutoCompleteDictionary getDictionary() {
		return dict;
	}

	/**
	 * Sets whether the component is currently performing autocomplete lookups
	 * as keystrokes are performed.
	 * 
	 * @param val
	 *            True or false.
	 */
	public void setAutoComplete(boolean val) {
		autoComplete = val && app.isAutoCompletePossible();

		if (autoComplete)
			app.initTranslatedCommands();

	}

	public List<String> getCompletions() {
		return completions;
	}

	/**
	 * Gets whether the component is currently performing autocomplete lookups
	 * as keystrokes are performed.
	 * 
	 * @return True or false.
	 */
	public boolean getAutoComplete() {
		return autoComplete && app.isAutoCompletePossible();
	}

	public String getCurrentWord() {
		return curWord.toString();
	}

	public int getCurrentWordStart() {
		return curWordStart;
	}

	public void geoElementSelected(GeoElement geo, boolean add) {
		if (geo != null) {
			replaceSelection(" " + geo.getLabel(StringTemplate.defaultTemplate)
					+ " ");
			requestFocusInWindow();
		}
	}

	/** returns if text must start with "=" to activate autocomplete */
	public boolean isEqualsRequired() {
		return isEqualsRequired;
	}

	/** sets flag to require text starts with "=" to activate autocomplete */
	public void setEqualsRequired(boolean isEqualsRequired) {
		this.isEqualsRequired = isEqualsRequired;
	}

	// ----------------------------------------------------------------------------
	// Protected methods
	// ----------------------------------------------------------------------------

	boolean ctrlC = false;

	private GeoTextField geoUsedForInputBox;

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		// we don't want to trap AltGr
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown())
			return;

		// swallow eg ctrl-a ctrl-b ctrl-p on Mac
		if (AppD.MAC_OS && e.isControlDown()) {
			e.consume();
		}

		ctrlC = false;

		switch (keyCode) {

		case KeyEvent.VK_Z:
		case KeyEvent.VK_Y:
			if (AppD.isControlDown(e)) {
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
				e.consume();
			}
			break;
		case KeyEvent.VK_C:
			if (AppD.isControlDown(e)) // workaround for MAC_OS
			{
				ctrlC = true;
			}
			break;

		case KeyEvent.VK_0:
		case KeyEvent.VK_1:
		case KeyEvent.VK_2:
		case KeyEvent.VK_3:
		case KeyEvent.VK_4:
		case KeyEvent.VK_5:
		case KeyEvent.VK_6:
		case KeyEvent.VK_7:
		case KeyEvent.VK_8:
		case KeyEvent.VK_9:
			if (AppD.isControlDown(e) && e.isShiftDown())
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
			break;

			// process input

		case KeyEvent.VK_ESCAPE:
			if (!handleEscapeKey) {
				break;
			}

			Component comp = SwingUtilities.getRoot(this);
			if (comp instanceof JDialog) {
				((JDialog) comp).setVisible(false);
				return;
			}

			setText(null);
			break;

			// removed - what is this for?
			// case KeyEvent.VK_LEFT_PARENTHESIS:
			// break;

		case KeyEvent.VK_UP:
			if (!handleEscapeKey) {
				break;
			}
			if (historyPopup == null) {
				String text = getPreviousInput();
				if (text != null)
					setText(text);
			} else if (!historyPopup.isDownPopup()) {
				historyPopup.showPopup();
			}
			break;

		case KeyEvent.VK_DOWN:
			if (!handleEscapeKey) {
				break;
			}
			if (historyPopup != null && historyPopup.isDownPopup()) {
				historyPopup.showPopup();
			} else {
				// Fix for Ticket #463
				if (getNextInput() != null) {
					setText(getNextInput());
				}
			}
			break;

		case KeyEvent.VK_F9:
			// needed for applets
			if (app.isApplet())
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
			break;

		case KeyEvent.VK_RIGHT:
			if (moveToNextArgument(false)) {
				e.consume();
			}
			break;

		case KeyEvent.VK_TAB:
			if (usedForInputBox()) {
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
			} else if (moveToNextArgument(true)) {
				e.consume();
			}
			break;

		case KeyEvent.VK_F1:
			String helpURL = isCASInput ? App.WIKI_CAS_VIEW : App.WIKI_MANUAL;
			if (autoComplete) {
				boolean commandFound = false;
				if (!getText().equals("")) {
					int pos = getCaretPosition();
					while (pos > 0 && getText().charAt(pos - 1) == '[') {
						pos--;
					}
					String word = getWordAtPos(getText(), pos);
					String lowerCurWord = word.toLowerCase();
					String closest = dict.lookup(lowerCurWord);

					if (closest != null) {// &&
						// lowerCurWord.equals(closest.toLowerCase()))
						showCommandHelp(app.getInternalCommand(closest),
								isCASInput);
						commandFound = true;
					}
				}
				if (!commandFound) {
					Object[] options = { app.getPlain("OK"),
							app.getPlain("ShowOnlineHelp") };
					int n = JOptionPane.showOptionDialog(
							app.getMainComponent(),
							app.getPlain(isCASInput ? "CASFieldHelp"
									: "InputFieldHelp"),
									app.getPlain("ApplicationName") + " - "
											+ app.getMenu("Help"),
											JOptionPane.YES_NO_OPTION,
											JOptionPane.QUESTION_MESSAGE, null, // do not use a
											// custom Icon
											options, // the titles of buttons
											options[0]); // default button title

					if (n == 1)
						app.getGuiManager().openHelp(helpURL);

				}
			} else
				app.getGuiManager().openHelp(helpURL);

			e.consume();
			break;
		default:
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

		// stop autocompletion re-opening on <Escape>
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE
				|| e.getKeyCode() == KeyEvent.VK_RIGHT
				|| e.getKeyCode() == KeyEvent.VK_LEFT
				|| e.getKeyCode() == KeyEvent.VK_ENTER) {
			e.consume();
			return;
		}
		// Application.debug(e+"");

		/*
		 * test code to generate unicode strings for Virtual Keyboard String
		 * text = getText(); String outStr = ""; for (int i = 0 ; i <
		 * text.length() ; i++) { int ch = text.charAt(i); if (ch < 128) outStr
		 * += text.charAt(i); else { String unicode = Integer.toHexString(ch);
		 * if (unicode.length() < 4) unicode = "\\u0"+unicode; else unicode =
		 * "\\u"+unicode; outStr += unicode; } } Application.debug(outStr); //
		 */

		// ctrl pressed on Mac
		// or alt on Windows
		boolean modifierKeyPressed = AppD.MAC_OS ? e.isControlDown() : e
				.isAltDown();

		// we don't want to act when AltGr is down
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown())
			modifierKeyPressed = false;

		char charPressed = e.getKeyChar();

		if ((StringUtil.isLetterOrDigitOrUnderscore(charPressed) || modifierKeyPressed)
				&& !(ctrlC && (AppD.MAC_OS || AppD.LINUX))
				&& !(e.getKeyCode() == KeyEvent.VK_A && AppD.MAC_OS)) {
			clearSelection();
		}

		// handle alt-p etc
		super.keyReleased(e);

		mergeKoreanDoubles();

		if (getAutoComplete()) {
			updateCurrentWord(false);
			startAutoCompletion();
		}

		/*
		 * if (charCodePressed == KeyEvent.VK_BACK_SPACE && isTextSelected &&
		 * input.length() > 0) { setText(input.substring(0, input.length())); }
		 */
	}

	public void mergeKoreanDoubles() {
		// avoid shift on Korean keyboards
		if (app.getLocale().getLanguage().equals("ko")) {
			String text = getText();
			int caretPos = getCaretPosition();
			String mergeText = Korean.mergeDoubleCharacters(text);
			int decrease = text.length() - mergeText.length();
			if (decrease > 0) {
				setText(mergeText);
				setCaretPosition(caretPos - decrease);
			}
		}
	}

	private void clearSelection() {
		int start = getSelectionStart();
		int end = getSelectionEnd();
		// clear selection if there is one
		if (start != end) {
			int pos = getCaretPosition();
			String oldText = getText();
			StringBuilder sb = new StringBuilder();
			sb.append(oldText.substring(0, start));
			sb.append(oldText.substring(end));
			setText(sb.toString());
			if (pos < sb.length())
				setCaretPosition(pos);
		}
	}

	/**
	 * Automatically closes parentheses (, {, [ when next sign is a space or end
	 * of input text. and ignores ] }, ) if the brackets already match (simple
	 * check)
	 */
	@Override
	public void keyTyped(KeyEvent e) {

		// only handle parentheses
		char ch = e.getKeyChar();
		int caretPos = getCaretPosition();

		String text = getText();

		// checking for isAltDown() because Alt+, prints another character on
		// the PC
		// TODO make this more robust - perhaps it could go in a document change
		// listener
		if (ch == ',' && !e.isAltDown()) {
			if (caretPos < text.length() && text.charAt(caretPos) == ',') {
				// User typed ',' just in ahead of an existing ',':
				// We may be in the position of filling in the next argument of
				// an autocompleted command
				// Look for a pattern of the form ", < Argument Description > ,"
				// or ", < Argument Description > ]"
				// If found, select the argument description so that it can
				// easily be typed over with the value
				// of the argument.
				if (moveToNextArgument(false)) {
					e.consume();
				}
				return;
			}
		}

		if (!(ch == '(' || ch == '{' || ch == '[' || ch == '}' || ch == ')' || ch == ']')) {
			super.keyTyped(e);
			return;
		}

		clearSelection();
		caretPos = getCaretPosition();

		if (ch == '}' || ch == ')' || ch == ']') {

			// simple check if brackets match
			if (text.length() > caretPos && text.charAt(caretPos) == ch) {
				int count = 0;
				for (int i = 0; i < text.length(); i++) {
					char c = text.charAt(i);
					if (c == '{')
						count++;
					else if (c == '}')
						count--;
					else if (c == '(')
						count += 1E3;
					else if (c == ')')
						count -= 1E3;
					else if (c == '[')
						count += 1E6;
					else if (c == ']')
						count -= 1E6;
				}

				if (count == 0) {
					// if brackets match, just move the cursor forwards one
					e.consume();
					caretPos++;
				}
			}

		}

		// auto-close parentheses
		if (!e.isAltDown() && (
				caretPos == text.length()
				|| geogebra.common.gui.inputfield.MyTextField
				.isCloseBracketOrWhitespace(text.charAt(caretPos)))) {
			switch (ch) {
			case '(':
				// opening parentheses: insert closing parenthesis automatically
				insertString(")");
				break;

			case '{':
				// opening braces: insert closing parenthesis automatically
				insertString("}");
				break;

			case '[':
				// opening bracket: insert closing parenthesis automatically
				insertString("]");
				break;
			}
		}

		// make sure we keep the previous caret position
		setCaretPosition(Math.min(text.length(), caretPos));
	}

	/**
	 * Updates curWord to word at current caret position. curWordStart,
	 * curWordEnd are set to this word's start and end position
	 */
	public void updateCurrentWord(boolean searchRight) {
		String text = getText();
		if (text == null)
			return;
		int caretPos = getCaretPosition();

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
	}

	// returns the word at position pos in text
	public static String getWordAtPos(String text, int pos) {
		// search to the left
		int wordStart = pos - 1;
		while (wordStart >= 0
				&& StringUtil.isLetterOrDigitOrUnderscore(text
						.charAt(wordStart)))
			--wordStart;
		wordStart++;

		// search to the right
		int wordEnd = pos;
		int length = text.length();
		while (wordEnd < length
				&& StringUtil.isLetterOrDigitOrUnderscore(text.charAt(wordEnd)))
			++wordEnd;

		if (wordStart >= 0 && wordEnd <= length) {
			return text.substring(wordStart, wordEnd);
		}
		return null;
	}

	// static String lastTyped = null;

	/*
	 * ----------------------------------------- Autocompletion
	 * -----------------------------------------
	 */

	private boolean moveToNextArgument(boolean find) {
		String text = getText();
		int caretPos = getCaretPosition();

		// make sure it works if caret is just after [
		// if (caretPos > 0 && text.charAt(caretPos - 1) == '[') caretPos--;

		Matcher argMatcher = syntaxArgPattern.matcher(text);
		boolean hasNextArgument = argMatcher.find(caretPos);
		if (find && !hasNextArgument) {
			hasNextArgument = argMatcher.find();
		}
		if (hasNextArgument && (find || argMatcher.start() == caretPos)) {
			setCaretPosition(argMatcher.end());
			moveCaretPosition(argMatcher.start() + 1);
			return true;
		}
		return false;
	}
	
	private List<String> resetCompletions() {
		String text = getText();
		updateCurrentWord(false);
		completions = null;
		if (isEqualsRequired && !text.startsWith("="))
			return null;

		boolean korean = app.getLocale().getLanguage().equals("ko");

		// start autocompletion only for words with at least two characters
		if (korean) {
			if (Korean.flattenKorean(curWord.toString()).length() < 2) {
				completions = null;
				return null;
			}
		} else if (curWord.length() < 2) {
			completions = null;
			return null;
		}
		cmdPrefix = curWord.toString();

		if (korean)
			completions = dict.getCompletionsKorean(cmdPrefix);
		else
			completions = dict.getCompletions(cmdPrefix);

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
			String suffix = App.syntaxStr;
			if (isCASInput) {
				syntaxString = app.getCommandSyntaxCAS(cmdInt);
				if (syntaxString.endsWith(App.syntaxCAS)) {
					syntaxString = app.getCommandSyntax(cmdInt);
				} else {
					suffix = App.syntaxCAS;
				}
			} else {
				syntaxString = app.getCommandSyntax(cmdInt);
			}
			if (syntaxString.endsWith(suffix)) {

				// command not found, check for macros
				Macro macro = isCASInput ? null : app.getKernel().getMacro(cmd);
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

	public void startAutoCompletion() {
		resetCompletions();
		completionsPopup.showCompletions();
	}

	public void cancelAutoCompletion() {
		completions = null;
	}

	/**
	 * Ticket #1167 Auto-completes input; <br>
	 * 
	 * @param index
	 *            index of the chosen command in the completions list
	 * @param completions
	 * @return false if completions list is null or index < 0 or index >
	 *         completions.size()
	 * @author Arnaud
	 */
	public boolean validateAutoCompletion(int index, List<String> completions) {
		if (completions == null || index < 0 || index >= completions.size()) {
			return false;
		}
		String command = completions.get(index);
		String text = getText();
		StringBuilder sb = new StringBuilder();
		sb.append(text.substring(0, curWordStart));
		sb.append(command);
		sb.append(text.substring(curWordStart + curWord.length()));
		setText(sb.toString());
		int bracketIndex = command.indexOf('[');// + 1;
		// Special case if the completion is a built-in function
		if (bracketIndex == -1) {
			bracketIndex = command.indexOf('(');
			setCaretPosition(curWordStart + bracketIndex + 1);
			return true;
		}
		if (command.indexOf("[]") > -1) {
			// eg GetTime[]
			bracketIndex += 2;
		} else if (command.indexOf("[ ]") > -1) {
			// eg GetTime[ ]
			bracketIndex += 3;
		}

		setCaretPosition(curWordStart + bracketIndex);
		moveToNextArgument(false);

		return true;
	}

	/**
	 * Adds string to input textfield's history
	 * 
	 * @param str
	 */
	public void addToHistory(String str) {

		// exit if the new string is the same as the last entered string
		if (!history.isEmpty() && str.equals(history.get(history.size() - 1)))
			return;

		history.add(str);
		historyIndex = history.size();
		if (historyPopup != null && !isBorderButtonVisible(1))
			setBorderButtonVisible(1, true);
	}

	/**
	 * @return previous input from input textfield's history
	 */
	private String getPreviousInput() {
		if (history.size() == 0)
			return null;
		if (historyIndex > 0)
			--historyIndex;
		return history.get(historyIndex);
	}

	/**
	 * @return next input from input textfield's history
	 */
	private String getNextInput() {
		if (historyIndex < history.size())
			++historyIndex;
		if (historyIndex == history.size())
			return null;
		return history.get(historyIndex);
	}

	/**
	 * shows dialog with syntax info
	 * 
	 * @param cmd
	 *            is the internal command name
	 */
	private void showCommandHelp(String cmd, boolean cas) {
		// show help for current command (current word)
		String help = cas ? app.getCommandSyntaxCAS(cmd) : app
				.getCommandSyntax(cmd);

		// show help if available
		if (help != null) {
			app.showError(new MyError(app, app.getPlain("Syntax") + ":\n"
					+ help, cmd));
		} else {
			app.getGuiManager().openCommandHelp(null);
		}
	}

	/**
	 * @param command
	 *            command name in local language
	 * @return syntax description of command as html text or null
	 */
	private String getCmdSyntax(String command) {
		if (command == null || command.length() == 0)
			return null;

		// try macro first
		Macro macro = app.getKernel().getMacro(command);
		if (macro != null) {
			return macro.toString();
		}

		// translate command to internal name and get syntax description
		// note: the translation ignores the case of command
		String internalCmd = app.getReverseCommand(command);
		// String key = internalCmd + "Syntax";
		// String syntax = app.getCommand(key);
		String syntax;
		if (isCASInput) {
			syntax = app.getCommandSyntaxCAS(internalCmd);
		} else {
			syntax = app.getCommandSyntax(internalCmd);
		}

		// check if we really found syntax information
		// if (key.equals(syntax)) return null;
		if (syntax.indexOf(App.syntaxStr) == -1)
			return null;

		// build html tooltip
		syntax = syntax.replaceAll("<", "&lt;");
		syntax = syntax.replaceAll(">", "&gt;");
		syntax = syntax.replaceAll("\n", "<br>");
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append(syntax);
		sb.append("</html>");
		return sb.toString();
	}

	/**
	 * Syntax help is shown elsewhere.  
	 * @param e exception
	 */
	public void showError(Exception e) {
		if (e instanceof MyException) {			
			int err = ((MyException) e).getErrorType();
			if (err == MyException.INVALID_INPUT) { 
				updateCurrentWord(true);
				// eg type
				// seg<enter><enter> to show syntax for Segment
				String command = app.getReverseCommand(getCurrentWord()); 
				if (command != null) { 

					app.showError(new MyError(app, app.getError("InvalidInput") 
							+ "\n\n" + app.getPlain("Syntax") + ":\n" 
							+ app.getCommandSyntax(command), getCurrentWord())); 
					return; 
				} 
			} else if (err == MyException.IMBALANCED_BRACKETS) {
				app.showError((MyError)e.getCause());
				return;

			}
		}
		// can't work out anything better, just show "Invalid Input"
		e.printStackTrace();
		app.showError(app.getError("InvalidInput"));

	}

	/**
	 * just show syntax error (already correctly formulated by
	 * CommandProcessor.argErr())
	 * @param e error
	 */
	public void showError(MyError e) {
		app.showError(e);
	}

	public void setFont(GFont font) {
		super.setFont(geogebra.awt.GFontD.getAwtFont(font));
	}

	/**
	 * Set the font of completions and history commands
	 * 
	 * @param font
	 *            the new font
	 */
	public void setPopupsFont(java.awt.Font font) {
		if (this.completionsPopup != null) {
			this.completionsPopup.setFont(font);
		}
		if (this.historyPopup != null) {
			this.historyPopup.setFont(font);
		}
	}

	public void setForeground(GColor color) {
		super.setForeground(geogebra.awt.GColorD.getAwtColor(color));

	}

	public void setBackground(GColor color) {
		super.setBackground(geogebra.awt.GColorD.getAwtColor(color));

	}

	public void setLabel(GLabel label) {
		((geogebra.javax.swing.GLabelD) label).getImpl().setLabelFor(this);
	}

	public void addFocusListener(FocusListener focusListener) {
		super.addFocusListener((geogebra.euclidian.event.FocusListener) focusListener);
	}

	public void addKeyListener(
			geogebra.common.euclidian.event.KeyListener listener) {
		super.addKeyListener((geogebra.euclidian.event.KeyListener) listener);
	}

	public void wrapSetText(final String s) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setText(s);
			}
		});

	}

	public void setUsedForInputBox(GeoTextField geo) {
		geoUsedForInputBox = geo;
	}

	public boolean usedForInputBox() {
		return geoUsedForInputBox != null;
	}

	@Override
	public void requestFocus() {
		super.requestFocus();
		if (geoUsedForInputBox != null && !geoUsedForInputBox.isSelected()) {
			app.clearSelectedGeos(false);
			app.addSelectedGeo(geoUsedForInputBox);
		}
	}

}
