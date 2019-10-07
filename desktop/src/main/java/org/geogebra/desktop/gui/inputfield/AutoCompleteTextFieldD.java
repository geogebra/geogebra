package org.geogebra.desktop.gui.inputfield;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.euclidian.event.FocusListener;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.inputfield.AutoComplete;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.gui.inputfield.MyTextField;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.properties.TextAlignment;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.AutoCompleteDictionary;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.awt.GFontD;
import org.geogebra.desktop.euclidian.event.FocusListenerD;
import org.geogebra.desktop.euclidian.event.KeyListenerD;
import org.geogebra.desktop.gui.autocompletion.CommandCompletionListCellRenderer;
import org.geogebra.desktop.gui.autocompletion.CompletionsPopup;
import org.geogebra.desktop.gui.util.GeoGebraIconD;
import org.geogebra.desktop.main.AppD;

public class AutoCompleteTextFieldD extends MathTextField
		implements AutoComplete, AutoCompleteTextField {

	private static final long serialVersionUID = 1L;

	private transient AppD app;
	private transient Localization loc;
	private StringBuilder curWord;
	private int curWordStart;

	protected AutoCompleteDictionary dict;
	protected boolean isCASInput = false;
	protected boolean autoComplete;
	private int historyIndex;
	private ArrayList<String> history;

	private KeyNavigation handleEscapeKey = KeyNavigation.IGNORE;

	private List<String> completions;
	private String cmdPrefix;
	private CompletionsPopup completionsPopup;

	private HistoryPopupD historyPopup;

	private DrawInputBox drawTextField = null;

	/**
	 * Flag to determine if text must start with "=" to activate autoComplete;
	 * used with spreadsheet cells
	 */
	private boolean isEqualsRequired = false;

	private boolean popupSymbolDisabled = false;

	private boolean forCAS;

	/**
	 * Pattern to find an argument description as found in the syntax
	 * information of a command.
	 */
	// private static Pattern syntaxArgPattern =
	// Pattern.compile("[,\\[] *(?:<[\\(\\) \\-\\p{L}]*>|\\.\\.\\.)
	// *(?=[,\\]])");
	// Simplified to this as there are too many non-alphabetic character in
	// parameter descriptions:
	private static Pattern syntaxArgPattern = Pattern
			.compile("[,\\[\\(] *(?:<.*?>|\"<.*?>\"|\\.\\.\\.) *(?=[,\\]\\)])");

	/**
	 * Constructs a new AutoCompleteTextField that uses the dictionary of the
	 * given Application for autocomplete look up. A default model is created
	 * and the number of columns is 0.
	 * 
	 */
	public AutoCompleteTextFieldD(int columns, App app) {
		this(columns, (AppD) app, KeyNavigation.BLUR);
	}

	/**
	 * @param columns
	 *            width
	 * @param app
	 *            Application
	 * @param handleEscapeKey
	 *            how to handle escape key
	 * @param forCAS
	 *            dictionary
	 */
	public AutoCompleteTextFieldD(int columns, AppD app,
			KeyNavigation handleEscapeKey, boolean forCAS) {
		super(app);
		// allow dynamic width with columns = -1
		if (columns > 0) {
			setColumns(columns);
		}

		this.app = app;
		this.loc = app.getLocalization();
		setAutoComplete(true);
		this.handleEscapeKey = handleEscapeKey;
		curWord = new StringBuilder();

		historyIndex = 0;
		history = new ArrayList<>(50);

		completions = null;

		CommandCompletionListCellRenderer cellRenderer = new CommandCompletionListCellRenderer();
		completionsPopup = new CompletionsPopup(this, cellRenderer, 6);
		// addKeyListener(this); now in MathTextField
		setDictionary(forCAS);
		enableLabelColoring(isCASInput);
	}

	public AutoCompleteTextFieldD(int columns, AppD app,
			KeyNavigation handleEscapeKey) {
		this(columns, app, handleEscapeKey, true);
		// setDictionary(app.getAllCommandsDictionary());
	}

	public AutoCompleteTextFieldD(int columns, App app,
			Drawable drawTextField) {
		this(columns, app);
		this.drawTextField = (DrawInputBox) drawTextField;
	}

	@Override
	public DrawInputBox getDrawTextField() {
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

		if (historyPopup == null) {
			historyPopup = new HistoryPopupD(this);
		}

		historyPopup.setDownPopup(isDownPopup);

		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				if (cmd.equals(1 + BorderButtonD.cmdSuffix)) {

					// TODO: should up/down orientation be tied to InputBar?
					// show popup
					historyPopup.showPopup();
				}
			}
		};
		setBorderButton(1, GeoGebraIconD.createUpDownTriangleIcon(false, true),
				al);
		this.setBorderButtonVisible(1, false);
	}

	@Override
	public void showPopupSymbolButton(boolean showPopupSymbolButton) {
		setShowSymbolTableIcon(showPopupSymbolButton && !popupSymbolDisabled);
	}

	@Override
	public void removeSymbolTable() {
		popupSymbolDisabled = true;
	}

	/**
	 * Set whether this is a CAS Input field
	 */
	public void setCASInput(boolean val) {
		isCASInput = val;
		enableLabelColoring(isCASInput);
	}

	/**
	 * Set the dictionary that autocomplete lookup should be performed by.
	 * 
	 * @param forCAS
	 *            whether this is for CAS
	 */
	@Override
	public void setDictionary(boolean forCAS) {
		this.dict = null;
		this.forCAS = forCAS;
	}

	/**
	 * Gets the dictionary currently used for lookups.
	 * 
	 * @return dict The dictionary that will be used for the autocomplete
	 *         lookups.
	 */
	@Override
	public AutoCompleteDictionary getDictionary() {
		if (this.dict == null) {
			this.dict = this.forCAS ? app.getCommandDictionaryCAS()
					: app.getCommandDictionary();
		}
		return this.dict;
	}

	/**
	 * Sets whether the component is currently performing autocomplete lookups
	 * as keystrokes are performed.
	 * 
	 * @param val
	 *            True or false.
	 */
	@Override
	public void setAutoComplete(boolean val) {
		autoComplete = val && app.getLocalization().isAutoCompletePossible();

		if (autoComplete) {
			app.initTranslatedCommands();
		}

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
	@Override
	public boolean getAutoComplete() {
		return autoComplete && loc.isAutoCompletePossible();
	}

	public String getCurrentWord() {
		return curWord.toString();
	}

	public int getCurrentWordStart() {
		return curWordStart;
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

	private GeoInputBox geoUsedForInputBox;

	private boolean previewActive = true;

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		// we don't want to trap AltGr
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown()) {
			return;
		}

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
			if (AppD.isControlDown(e) && e.isShiftDown()) {
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
			}
			break;

		// process input

		case KeyEvent.VK_ESCAPE:
			if (handleEscapeKey == KeyNavigation.IGNORE) {
				break;
			}
			if (handleEscapeKey == KeyNavigation.HISTORY) {
				setText("");
			}

			Component comp = SwingUtilities.getRoot(this);
			if (comp instanceof JDialog) {
				((JDialog) comp).setVisible(false);
				return;
			}

			// loose focus
			app.getActiveEuclidianView().requestFocusInWindow();
			break;

		// removed - what is this for?
		// case KeyEvent.VK_LEFT_PARENTHESIS:
		// break;

		case KeyEvent.VK_UP:
			if (handleEscapeKey == KeyNavigation.IGNORE) {
				break;
			}
			if (historyPopup == null) {
				String text = getPreviousInput();
				if (text != null) {
					setText(text);
				}
			} else if (!historyPopup.isDownPopup()) {
				historyPopup.showPopup();
			}
			break;

		case KeyEvent.VK_DOWN:
			if (handleEscapeKey == KeyNavigation.IGNORE) {
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
			if (app.isApplet()) {
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
			}
			break;

		case KeyEvent.VK_RIGHT:
			if (moveToNextArgument(false)) {
				e.consume();
			}
			break;

		case KeyEvent.VK_TAB:
			if (usedForInputBox()) {
				AutoCompleteTextField tf = app.getActiveEuclidianView()
						.getTextField();
				if (tf != null) {
					geoUsedForInputBox.updateLinkedGeo(tf.getText());
				}
				//
				// app.getGlobalKeyDispatcher().handleTab(e.isControlDown(),
				// e.isShiftDown(), true);
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);

				GeoElement next = app.getSelectionManager().getSelectedGeos()
						.get(0);
				Log.debug("next is " + next);
				if (next instanceof GeoInputBox) {
					GeoInputBox input = (GeoInputBox) next;
					app.getActiveEuclidianView().focusTextField(input);
				} else {
					// app.getActiveEuclidianView().requestFocus();
				}
				//
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
					String word = MyTextField.getWordAtPos(getText(), pos);
					String lowerCurWord = word.toLowerCase();
					String closest = getDictionary().lookup(lowerCurWord);

					if (closest != null) {// &&
						// lowerCurWord.equals(closest.toLowerCase()))
						showCommandHelp(app.getInternalCommand(closest),
								isCASInput);
						commandFound = true;
					}
				}
				if (!commandFound) {
					Object[] options = { loc.getMenu("OK"),
							loc.getMenu("ShowOnlineHelp") };
					int n = JOptionPane.showOptionDialog(app.getMainComponent(),
							loc.getMenu(isCASInput ? "CASFieldHelp"
									: "InputFieldHelp"),
							GeoGebraConstants.APPLICATION_NAME + " - "
									+ loc.getMenu("Help"),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, // do not use a
							// custom Icon
							options, // the titles of buttons
							options[0]); // default button title

					if (n == 1) {
						app.getGuiManager().openHelp(helpURL);
					}

				}
			} else {
				app.getGuiManager().openHelp(helpURL);
			}

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
		boolean modifierKeyPressed = AppD.MAC_OS ? e.isControlDown()
				: e.isAltDown();

		// we don't want to act when AltGr is down
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown()) {
			modifierKeyPressed = false;
		}

		char charPressed = e.getKeyChar();

		if ((StringUtil.isLetterOrDigitOrUnderscore(charPressed)
				|| modifierKeyPressed)
				&& !(ctrlC && (AppD.MAC_OS || AppD.LINUX))
				&& !(e.getKeyCode() == KeyEvent.VK_A && AppD.MAC_OS)) {
			clearSelection();
		}

		// handle alt-p etc
		super.keyReleased(e);

		if (getAutoComplete()) {
			updateCurrentWord(false);
			startAutoCompletion();
		}

		/*
		 * if (charCodePressed == KeyEvent.VK_BACK_SPACE && isTextSelected &&
		 * input.length() > 0) { setText(input.substring(0, input.length())); }
		 */
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
			// set caret position to start
			pos = start;
			if (pos < sb.length()) {
				setCaretPosition(pos);
			}
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

		if (!(ch == '(' || ch == '{' || ch == '[' || ch == '}' || ch == ')'
				|| ch == ']')) {
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
					if (c == '{') {
						count++;
					} else if (c == '}') {
						count--;
					} else if (c == '(') {
						count += 1E3;
					} else if (c == ')') {
						count -= 1E3;
					} else if (c == '[') {
						count += 1E6;
					} else if (c == ']') {
						count -= 1E6;
					}
				}

				if (count == 0) {
					// if brackets match, just move the cursor forwards one
					e.consume();
					caretPos++;
				}
			}

		}

		// auto-close parentheses
		if (!e.isAltDown() && (caretPos == text.length()
				|| org.geogebra.common.gui.inputfield.MyTextField
						.isCloseBracketOrWhitespace(text.charAt(caretPos)))) {
			this.setPreviewActive(false);
			switch (ch) {
			default:
				// do nothing
				break;
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
			this.setPreviewActive(true);
		}

		// make sure we keep the previous caret position
		setCaretPosition(Math.min(text.length(), caretPos));
	}

	private void setPreviewActive(boolean b) {
		previewActive = b;

	}

	/**
	 * Updates curWord to word at current caret position. curWordStart,
	 * curWordEnd are set to this word's start and end position
	 */
	public void updateCurrentWord(boolean searchRight) {
		int next = InputHelper.updateCurrentWord(searchRight, this.curWord,
				getText(), getCaretPosition(), true);
		if (next > -1) {
			this.curWordStart = next;
		}
	}

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
			// do not select the space after , but do select space after [
			if (text.charAt(argMatcher.start()) == ',') {
				moveCaretPosition(argMatcher.start() + 2);
			} else {
				moveCaretPosition(argMatcher.start() + 1);
			}
			return true;
		}
		return false;
	}

	private List<String> resetCompletions() {
		String text = getText();
		updateCurrentWord(false);
		completions = null;
		if (isEqualsRequired && !text.startsWith("=")) {
			return null;
		}

		boolean korean = app.getLocale().getLanguage().equals("ko");
		// start autocompletion only for long enough words
		if (!InputHelper.needsAutocomplete(curWord, app.getKernel())) {
			completions = null;
			return null;
		}

		cmdPrefix = curWord.toString();

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

	/*
	 * Take a list of commands and return all possible syntaxes for these
	 * commands
	 */
	private List<String> getSyntaxes(List<String> commands) {
		if (commands == null) {
			return null;
		}
		ArrayList<String> syntaxes = new ArrayList<>();
		for (String cmd : commands) {

			String cmdInt = app.getInternalCommand(cmd);

			if (cmdInt == null || "undefined".equals(cmdInt)) {
				Log.error("Can't find command " + cmd);
				continue;
			}

			String syntaxString;
			if (isCASInput) {
				syntaxString = loc.getCommandSyntaxCAS(cmdInt);
				if (syntaxString.endsWith(Localization.syntaxCAS)) {
					syntaxString = loc.getCommandSyntax(cmdInt);
				}
			} else {
				syntaxString = loc.getCommandSyntax(cmdInt);
			}
			if (syntaxString.endsWith(Localization.syntaxStr)
					|| syntaxString.endsWith(Localization.syntaxCAS)) {

				// command not found, check for macros
				Macro macro = isCASInput ? null : app.getKernel().getMacro(cmd);
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

	public void startAutoCompletion() {

		// don't show autocompletion popup if the current word
		// is a defined variable

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
		int start = curWordStart;
		String command = completions.get(index);
		String text = getText();

		String before = text.substring(0, curWordStart);
		String after = text.substring(curWordStart + curWord.length());
		int bracketIndex = command.indexOf('[');
		if (bracketIndex == -1) {
			bracketIndex = command.indexOf('(');
		}
		if (bracketIndex > -1
				&& (after.startsWith("[") || after.startsWith("("))) {
			// probably already have some arguments
			// eg user is just changing the command name
			command = command.substring(0, bracketIndex);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(before);
		sb.append(command);
		sb.append(after);
		setText(sb.toString());

		// Special case if the completion is a built-in function
		if (bracketIndex == -1) {
			bracketIndex = command.indexOf('(');
			setCaretPosition(start + bracketIndex + 1);
			return true;
		}
		if (command.indexOf("()") > -1) {
			// eg GetTime[]
			bracketIndex += 2;
		} else if (command.indexOf("( )") > -1) {
			// eg GetTime[ ]
			bracketIndex += 3;
		}

		setCaretPosition(start + bracketIndex);
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
		if (!history.isEmpty() && str.equals(history.get(history.size() - 1))) {
			return;
		}

		history.add(str);
		historyIndex = history.size();
		if (historyPopup != null && !isBorderButtonVisible(1)) {
			setBorderButtonVisible(1, true);
		}
	}

	/**
	 * @return previous input from input textfield's history
	 */
	private String getPreviousInput() {
		if (history.size() == 0) {
			return null;
		}
		if (historyIndex > 0) {
			--historyIndex;
		}
		return history.get(historyIndex);
	}

	/**
	 * @return next input from input textfield's history
	 */
	private String getNextInput() {
		if (historyIndex < history.size()) {
			++historyIndex;
		}
		if (historyIndex == history.size()) {
			return null;
		}
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
		String help = cas ? loc.getCommandSyntaxCAS(cmd)
				: loc.getCommandSyntax(cmd);

		// show help if available
		if (help != null) {
			app.showError(MyError.forCommand(loc,
					loc.getMenu("Syntax") + ":\n" + help, cmd, null));
		} else {
			app.getGuiManager().openCommandHelp(null);
		}
	}

	/**
	 * just show syntax error (already correctly formulated by
	 * CommandProcessor.argErr())
	 * 
	 * @param e
	 *            error
	 */
	public void showError(MyError e) {
		app.showError(e);
	}

	@Override
	public void setFont(GFont font) {
		super.setFont(GFontD.getAwtFont(font));
	}

	/**
	 * Set the font of completions and history commands
	 * 
	 * @param font
	 *            the new font
	 */
	public void setPopupsFont(Font font) {
		if (this.completionsPopup != null) {
			this.completionsPopup.setFont(font);
		}
		if (this.historyPopup != null) {
			this.historyPopup.setFont(font);
		}
	}

	@Override
	public void setForeground(GColor color) {
		super.setForeground(GColorD.getAwtColor(color));

	}

	@Override
	public void setBackground(GColor color) {
		super.setBackground(GColorD.getAwtColor(color));

	}

	@Override
	public void addFocusListener(FocusListener focusListener) {
		if (focusListener instanceof FocusListenerD) {
			super.addFocusListener((FocusListenerD) focusListener);
		}
	}

	@Override
	public void wrapSetText(final String s) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setText(s);
			}
		});

	}

	@Override
	public void setUsedForInputBox(GeoInputBox geo) {
		geoUsedForInputBox = geo;
	}

	@Override
	public boolean usedForInputBox() {
		return geoUsedForInputBox != null;
	}

	@Override
	public void requestFocus() {
		super.requestFocus();
		if (geoUsedForInputBox != null && !geoUsedForInputBox.isSelected()) {
			app.getSelectionManager().clearSelectedGeos(false);
			app.getSelectionManager().addSelectedGeo(geoUsedForInputBox);
		}
	}

	@Override
	public void addKeyHandler(KeyHandler handler) {
		addKeyListener(new KeyListenerD(handler));

	}

	@Override
	public String getCommand() {
		this.updateCurrentWord(true);
		return this.getCurrentWord();
	}

	@Override
	public void setFocus(boolean b) {
		// called from common, needed only in web
	}

	@Override
	public void prepareShowSymbolButton(boolean b) {
		this.setShowSymbolTableIcon(b);
	}

	@Override
	public void drawBounds(GGraphics2D g2, GColor bgColor, GRectangle bounds) {
		drawBounds(g2, bgColor, (int) bounds.getX(), (int) bounds.getY(),
				(int) bounds.getWidth(), (int) bounds.getHeight());
	}

	@Override
	public void drawBounds(GGraphics2D g2, GColor bgColor, int left, int top,
			int width, int height) {

		g2.setPaint(bgColor);
		g2.fillRect(left - 1, top - 1, width - 1, height - 2);

		// TF Rectangle
		g2.setPaint(GColor.LIGHT_GRAY);

		g2.drawRect(left - 1, top - 1, width - 1, height - 2);

	}

	/**
	 * @return whether we may update preview
	 */
	public boolean isPreviewActive() {
		return previewActive;
	}

	@Override
	public void setDrawTextField(DrawInputBox df) {
		drawTextField = df;
	}

	@Override
	public GeoInputBox getInputBox() {
		return geoUsedForInputBox;
	}

	@Override
	public void setPrefSize(int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setAuralText(String text) {
		// not needed
	}

	@Override
	public void setSelection(int start, int end) {
		select(start, end);
	}

	@Override
	public void setTextAlignmentsForInputBox(TextAlignment alignment) {
		this.setHorizontalAlignment(toSwingAlignment(alignment));
	}

	private static int toSwingAlignment(TextAlignment alignment) {
		switch (alignment) {
		case LEFT:
		default:
			return SwingConstants.LEFT;
		case CENTER:
			return SwingConstants.CENTER;
		case RIGHT:
			return SwingConstants.RIGHT;
		}
	}
}
