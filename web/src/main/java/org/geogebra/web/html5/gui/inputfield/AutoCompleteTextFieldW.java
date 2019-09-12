package org.geogebra.web.html5.gui.inputfield;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.euclidian.event.FocusListener;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.VirtualKeyboardListener;
import org.geogebra.common.gui.inputfield.AutoComplete;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.gui.inputfield.MyTextField;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.properties.TextAlignment;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.AutoCompleteDictionary;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.event.KeyEventsHandler;
import org.geogebra.web.html5.event.KeyListenerW;
import org.geogebra.web.html5.gui.DummyCursor;
import org.geogebra.web.html5.gui.HasKeyboardTF;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.FormLabel.HasInputElement;
import org.geogebra.web.html5.gui.util.GToggleButton;
import org.geogebra.web.html5.gui.view.autocompletion.CompletionsPopup;
import org.geogebra.web.html5.gui.view.autocompletion.GSuggestBox;
import org.geogebra.web.html5.gui.view.autocompletion.ScrollableSuggestBox;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.util.AltKeys;
import com.himamis.retex.editor.share.util.GWTKeycodes;
import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.editor.web.MathFieldW;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class AutoCompleteTextFieldW extends FlowPanel
		implements AutoComplete, AutoCompleteW, AutoCompleteTextField,
		KeyDownHandler, KeyUpHandler, KeyPressHandler,
		ValueChangeHandler<String>, SelectionHandler<Suggestion>,
		VirtualKeyboardListener, HasKeyboardTF, HasInputElement {

	private static final int BOX_ROUND = 8;

	protected AppW app;
	private Localization loc;
	private StringBuilder curWord;
	private int curWordStart;

	protected AutoCompleteDictionary dict;
	private boolean isCASInput = false;
	private boolean autoComplete;
	private int historyIndex;
	private ArrayList<String> history;

	private boolean handleEscapeKey = false;

	private List<String> completions;

	private HistoryPopupW historyPopup;
	protected ScrollableSuggestBox textField = null;

	private DrawInputBox drawTextField = null;

	// symbol table popup fields
	private GToggleButton showSymbolButton = null;
	private SymbolTablePopupW tablePopup;

	/**
	 * Flag to determine if text must start with "=" to activate autoComplete;
	 * used with spreadsheet cells
	 */
	private boolean equalSignRequired = false;

	/**
	 * Flag to determine if Tab key should behave like usual or disabled.
	 */
	private boolean tabEnabled = true;
	private boolean forCAS;
	private InsertHandler insertHandler = null;
	private OnBackSpaceHandler onBackSpaceHandler = null;
	private boolean suggestionJustHappened = false;
	private GeoInputBox geoUsedForInputBox;
	/**
	 * Pattern to find an argument description as found in the syntax
	 * information of a command.
	 */
	// private static Pattern syntaxArgPattern =
	// Pattern.compile("[,\\[] *(?:<[\\(\\) \\-\\p{L}]*>|\\.\\.\\.)
	// *(?=[,\\]])");
	// Simplified to this as there are too many non-alphabetic character in
	// parameter descriptions:
	private static RegExp syntaxArgPattern = RegExp
			.compile("[,\\[\\(] *(<.*?>|\"<.*?>\"|\\.\\.\\.) *(?=[,\\]\\)])");

	private int actualFontSize = 14;

	private DummyCursor dummyCursor;

    private boolean rightAltDown;
	private boolean leftAltDown;

	public interface InsertHandler {
		void onInsert(String text);
	}

	public interface OnBackSpaceHandler {
		void onBackspace();
	}

	/**
	 * Constructs a new AutoCompleteTextField that uses the dictionary of the
	 * given Application for autocomplete look up. A default model is created
	 * and the number of columns is 0.
	 *
	 * @param columns
	 *            number of columns
	 * @param app
	 *            app
	 *
	 */
	public AutoCompleteTextFieldW(int columns, App app) {
		this(columns, (AppW) app, true, null, false, false);
	}

	/**
	 * @param columns
	 *            number of columns
	 * @param app
	 *            app
	 * @param drawTextField
	 *            associated input box
	 */
	public AutoCompleteTextFieldW(int columns, App app,
			Drawable drawTextField, boolean showSymbolButton) {
		this(columns, (AppW) app, true, null, false, showSymbolButton);
		this.drawTextField = (DrawInputBox) drawTextField;
		addStyleName("FromDrawTextFieldNew");
	}

	/**
	 * @param columns
	 *            number of columns
	 * @param app
	 *            application
	 * @param handleEscapeKey
	 *            whether escape key should be handled
	 * @param keyHandler
	 *            key handler
	 * @param forCAS
	 *            whether to use CAS autocompletion
	 */
	public AutoCompleteTextFieldW(int columns, final AppW app,
			boolean handleEscapeKey, KeyEventsHandler keyHandler,
			boolean forCAS, boolean showSymbolButton) {
		this.forCAS = forCAS;
		this.app = app;
		this.loc = app.getLocalization();
		setAutoComplete(true);
		dummyCursor = new DummyCursor(this, app);
		this.handleEscapeKey = handleEscapeKey;
		curWord = new StringBuilder();

		historyIndex = 0;
		history = new ArrayList<>(50);

		completions = null;

		addStyleName("AutoCompleteTextFieldW");

		// AG not MathTextField and Mytextfield exists yet super(app);
		// allow dynamic width with columns = -1
		CompletionsPopup completionsPopup = new CompletionsPopup();
		textField = new ScrollableSuggestBox(completionsPopup, app.getPanel(), app) {
			@Override
			public void setText(String s) {
				String oldText = super.getText();
				int pos = getValueBox().getCursorPos();
				int wp = InputHelper.updateCurrentWord(false,
						new StringBuilder(), oldText, pos, true);

				super.setText(oldText.substring(0, wp) + s + oldText.substring(pos));
			}

			@Override
			public void onBrowserEvent(Event event) {
				super.onBrowserEvent(event);

				int etype = event.getTypeInt();

				if ((etype == Event.ONMOUSEDOWN || etype == Event.ONTOUCHSTART)
						&& !app.isWhiteboardActive()
						&& app.getGuiManager() != null) {
					app.getGuiManager().setOnScreenKeyboardTextField(
							AutoCompleteTextFieldW.this);
				}

				if (etype == Event.ONMOUSEDOWN
						|| etype == Event.ONMOUSEMOVE
						|| etype == Event.ONMOUSEUP
						|| etype == Event.ONTOUCHMOVE
						|| etype == Event.ONTOUCHSTART
						|| etype == Event.ONTOUCHEND) {
					event.stopPropagation();
					app.getGlobalKeyDispatcher().setFocused(true);
					return;
				}

				// react on enter from system on screen keyboard or hardware
				// keyboard
				if ((event.getTypeInt() == Event.ONKEYUP
						|| event.getTypeInt() == Event.ONKEYPRESS)
						&& event.getKeyCode() == KeyCodes.KEY_ENTER) {
					// app.hideKeyboard();
					// prevent handling in AutoCompleteTextField
					event.stopPropagation();
					endOnscreenKeyboardEditing();
				}
			}
		};

		textField.sinkEvents(
				Event.ONMOUSEMOVE | Event.ONMOUSEUP | Event.TOUCHEVENTS);
		Browser.setAllowContextMenu(textField.getValueBox().getElement(), true);
		if (columns > 0) {
			setColumns(columns);
		}

		textField.addStyleName("TextField");

		completionsPopup.addTextField(this);

		// addKeyListener(this); now in MathTextField <==AG not mathtexfield
		// exist yet
		if (keyHandler == null) {
			textField.getValueBox().addKeyDownHandler(this);
			textField.getValueBox().addKeyUpHandler(this);
			textField.getValueBox().addKeyPressHandler(this);
		} else {
			// This is currently used for
			// MyCellEditorW.SpreadsheetCellEditorKeyListener
			textField.getValueBox().addKeyDownHandler(keyHandler);
			textField.getValueBox().addKeyPressHandler(keyHandler);
			textField.getValueBox().addKeyUpHandler(keyHandler);
		}
		textField.addValueChangeHandler(this);
		textField.addSelectionHandler(this);

		ClickStartHandler.init(textField, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// set this text field to be edited by the keyboard
				app.updateKeyBoardField(AutoCompleteTextFieldW.this);

				// make sure the keyboard is not closed
				CancelEventTimer.keyboardSetVisible();
			}
		});

		textField.getValueBox().addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				requestFocus();
				app.getGlobalKeyDispatcher().setFocused(true);
			}
		});

		add(textField);

		if (showSymbolButton) {
			setupShowSymbolButton();
		}
	}

	@Override
	public void setAuralText(String text) {
		textField.getElement().setAttribute("aria-label", text);
	}

	private void setupShowSymbolButton() {
		showSymbolButton = new GToggleButton();
		showSymbolButton.setText(Unicode.alpha + "");
		showSymbolButton.addStyleName("SymbolToggleButton");

		ClickStartHandler.init(showSymbolButton, new ClickStartHandler(false, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// unfortunate repetition to make it work in all major browsers
				app.getActiveEuclidianView().getBoxForTextField().setVisible(true);
				setFocus(true);

				if (tablePopup != null && tablePopup.isShowing()) {
					hideTablePopup();
				} else {
					showTablePopup();
				}

				Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
					@Override
					public void execute() {
						app.getActiveEuclidianView().getBoxForTextField().setVisible(true);
						setFocus(true);
					}
				});
			}
		});

		add(showSymbolButton);
	}

	/**
	 * @param app
	 *            creates new AutoCompleteTextField with app.
	 */
	public AutoCompleteTextFieldW(App app) {
		this(0, app);
	}

	public void setEnabled(boolean b) {
		this.textField.setEnabled(b);
	}

	public boolean isEnabled() {
		return this.textField.isEnabled();
	}

	@Override
	public DrawInputBox getDrawTextField() {
		return drawTextField;
	}

	@Override
	public ArrayList<String> getHistory() {
		return history;
	}

	/**
	 * Add a history popup list and an embedded popup button. See
	 * AlgebraInputBar
	 */
	public void addHistoryPopup(boolean isDownPopup) {
		if (historyPopup == null) {
			historyPopup = new HistoryPopupW(this, app.getPanel());
		}

		historyPopup.setDownPopup(isDownPopup);
	}

	@Override
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		//
	}

	@Override
	public void showPopupSymbolButton(final boolean showSymbolTableIcon) {
		// only for desktop
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
		autoComplete = val && loc.isAutoCompletePossible();
	}

	@Override
	public List<String> resetCompletions() {
		String text = getText();
		updateCurrentWord(false);
		completions = null;
		if (equalSignRequired && !text.startsWith("=")) {
			return null;
		}

		boolean korean = false; // AG
								// app.getLocale().getLanguage().equals("ko");

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

			boolean englishOnly = cmdInt == null && isFallbackCompletitionAllowed();

			if (englishOnly) {
				cmdInt = app.englishToInternal(cmd);
			}

			String syntaxString;
			if (isCASInput) {
				syntaxString = loc.getCommandSyntaxCAS(cmdInt);
			} else {
				AlgebraProcessor ap = app.getKernel().getAlgebraProcessor();
				syntaxString = englishOnly ? ap.getEnglishSyntax(cmdInt, app.getSettings())
						: ap.getSyntax(cmdInt, app.getSettings());
			}

			if (syntaxString == null) {
				continue;
			}
			if (syntaxString.endsWith(Localization.syntaxCAS)
					|| syntaxString.endsWith(Localization.syntaxStr)) {

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

	private boolean isFallbackCompletitionAllowed() {
		return app.has(Feature.COMMAND_COMPLETION_FALLBACK)
				&& "zh".equals(app.getLocalization().getLanguage());
	}

	public void cancelAutoCompletion() {
		completions = null;
	}

	@Override
	public void enableColoring(boolean b) {
		//
	}

	@Override
	public void setOpaque(boolean b) {
		//
	}

	@Override
	public void setFont(GFont font) {
		actualFontSize = font.getSize();
		Dom.setImportant(textField.getElement().getStyle(), "font-size",
				font.getSize() + "px");

		if (showSymbolButton != null) {
			showSymbolButton.getElement().getStyle().setFontSize(font.getSize(),
					Unit.PX);
			showSymbolButton.getElement().getStyle()
					.setLineHeight(font.getSize(), Unit.PX);
		}
	}

	@Override
	public void setForeground(GColor color) {
		textField.getElement().getStyle()
				.setColor(GColor.getColorString(color));
	}

	@Override
	public void setBackground(GColor color) {
		textField.getElement().getStyle()
				.setBackgroundColor(GColor.getColorString(color));
	}

	@Override
	public void setFocusable(boolean b) {
		//
	}

	@Override
	public void setEditable(boolean b) {
		textField.setEnabled(b);
	}

	@Override
	public void setPrefSize(int width, int height) {
		getTextBox().setWidth(width + "px");
		getTextBox().setHeight(height + "px");
	}

	@Override
	public void setColumns(int columns) {
		if (showSymbolButton != null
				&& (columns > EuclidianConstants.SHOW_SYMBOLBUTTON_MINLENGTH
						|| columns == -1)) {
			prepareShowSymbolButton(true);
		}

		if (this.drawTextField != null) {
			// only use the correct code for members of the EuclidianView

			int columnWidth;
			switch (actualFontSize) {
			case 7:
				columnWidth = 6;
				break;
			case 9:
				columnWidth = 8;
				break;
			case 14:
				columnWidth = 11;
				break;
			case 18:
				columnWidth = 15;
				break; // 18:15:educated guess
			case 19:
				columnWidth = 16;
				break;
			case 28:
				columnWidth = 24;
				break;
			case 56:
				columnWidth = 47;
				break;
			case 112:
				columnWidth = 95;
				break;
			// more precise for FitLine+FitLineX, but unreal
			// default: columnWidth = (int) Math.floor(0.83265 * actualFontSize
			// + 0.4615); break;
			// default: columnWidth = (int) Math.round(0.832 * actualFontSize);
			// break;
			// 20 length * 18 fontSize * 0.002 difference gives just less than 1
			// pixel anyway
			default:
				columnWidth = (int) Math.round(0.83 * actualFontSize);
				break;
			}

			// this is a way to emulate how Java does it in Desktop version,
			// but columnWidth is not always exact (+-1)
			getTextBox().setWidth((columns * columnWidth + 5) + "px");
			// the number 5 comes from experimental testing for small textfields
			// (e.g. columns=1)
			// of course, this is not the most perfect, but at least works...
			// due to Greek letters popup, length should be lessened somewhere
			// else

		} else {
			// GeoGebra GUI (non-GGB GUI) can still use the old code,
			// for compatibility reasons, e.g. Spreadsheet View

			// as the following solution was wrong, since em means vertical
			// height:
			getTextBox().setWidth(columns + "em");
		}
	}

	private String getCurrentWord() {
		return curWord.toString();
	}

	@Override
	public List<String> getCompletions() {
		return completions;
	}

	public int getCurrentWordStart() {
		return curWordStart;
	}

	@Override
	public void addFocusListener(FocusListener listener) {
		if (listener instanceof FocusListenerW) {
			textField.getValueBox().addFocusHandler((FocusListenerW) listener);
			textField.getValueBox().addBlurHandler((FocusListenerW) listener);
		}
	}

	@Override
	public void wrapSetText(String s) {
		//
	}

	@Override
	public int getCaretPosition() {
		return textField.getValueBox().getCursorPos();
	}

	@Override
	public void setCaretPosition(int caretPos) {
		setCaretPosition(caretPos, true);
	}

	/**
	 * Sets the position of caret.
	 *
	 * @param caretPos
	 *            new position
	 * @param moveDummyCursor
	 *            true, if needed to change the dummy cursor position too
	 */
	public void setCaretPosition(int caretPos, boolean moveDummyCursor) {
		if (dummyCursor.isActive() && moveDummyCursor) {
			if (caretPos == textField.getText().length()) {
				return;
			}
			removeDummyCursor();
			addDummyCursor(caretPos);
		} else {
			textField.getValueBox().setCursorPos(caretPos);
		}
	}

	/**
	 * Add dummy cursor for Android/iOS
	 *
	 * @param caretPos
	 *            cursor position
	 */
	public void addDummyCursor(int caretPos) {
		dummyCursor.addAt(caretPos);
	}

	@Override
	public void addDummyCursor() {
		dummyCursor.add();
	}

	@Override
	public int removeDummyCursor() {
		return dummyCursor.remove();
	}

	public boolean hasDummyCursor() {
		return dummyCursor.isActive();
	}

	@Override
	public void setDictionary(boolean forCAS) {
		this.forCAS = forCAS;
		this.dict = null;
	}

	@Override
	public AutoCompleteDictionary getDictionary() {
		if (this.dict == null) {
			this.dict = this.forCAS ? app.getCommandDictionaryCAS()
					: app.getCommandDictionary();
		}
		return dict;
	}

	/**
	 * shows dialog with syntax info
	 *
	 * @param cmd
	 *            is the internal command name
	 */
	private void showCommandHelp(String cmd) {
		// show help for current command (current word)
		String help = loc.getCommandSyntax(cmd);

		// show help if available
		if (help != null) {
			app.showError(MyError.forCommand(loc,
					loc.getMenu("Syntax") + ":\n" + help, cmd, null));
		} else if (app.getGuiManager() != null) {
			app.getGuiManager().openCommandHelp(null);
		}
	}

	private void clearSelection() {
		int start = textField.getText()
				.indexOf(textField.getValueBox().getSelectedText());
		int end = start + textField.getValueBox().getSelectionLength();
		// clear selection if there is one
		if (start != end) {
			int pos = getCaretPosition();
			String oldText = getText();
			StringBuilder sb = new StringBuilder();
			sb.append(oldText.substring(0, start));
			sb.append(oldText.substring(end));
			setText(sb.toString());
			if (pos < sb.length()) {
				setCaretPosition(pos);
			}
		}
	}

	/**
	 * Updates curWord to word at current caret position. curWordStart,
	 * curWordEnd are set to this word's start and end position
	 */
	private void updateCurrentWord(boolean searchRight) {
		int next = InputHelper.updateCurrentWord(searchRight, this.curWord,
				getText(), getCaretPosition(), true);
		if (next > -1) {
			this.curWordStart = next;
		}
	}

	/*
	 * just show syntax error (already correctly formulated by
	 * CommandProcessor.argErr())
	 */
	public void showError(MyError e) {
		app.showError(e);
	}

	@Override
	public boolean getAutoComplete() {
		return autoComplete && loc.isAutoCompletePossible();
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

	private static void mergeKoreanDoubles() {
		// avoid shift on Korean keyboards
		/*
		 * AG dont do that yet if (app.getLocale().getLanguage().equals("ko")) {
		 * String text = getText(); int caretPos = getCaretPosition(); String
		 * mergeText = Korean.mergeDoubleCharacters(text); int decrease =
		 * text.length() - mergeText.length(); if (decrease > 0) {
		 * setText(mergeText); setCaretPosition(caretPos - decrease); } }
		 */
		Log.debug("KoreanDoubles may be needed in AutocompleteTextField");
	}

	private boolean moveToNextArgument(boolean find, boolean updateUI) {
		String text = getText();
		int caretPos = getCaretPosition();

		// make sure it works if caret is just after [
		if (caretPos > 0 && text.length() < caretPos
				&& text.charAt(caretPos) != '(') {
			caretPos--;
		}
		String suffix = text.substring(caretPos);
		int index = -1;
		// AGMatcher argMatcher = syntaxArgPattern.matcher(text);
		MatchResult argMatcher = syntaxArgPattern.exec(suffix);
		// boolean hasNextArgument = argMatcher.find(caretPos);
		boolean hasNextArgument = syntaxArgPattern.test(suffix);
		if (hasNextArgument) {
			index = argMatcher.getIndex() + caretPos;
		}

		if (find && !hasNextArgument) {
			// hasNextArgument = argMatcher.find();
			hasNextArgument = syntaxArgPattern.test(text);
			argMatcher = syntaxArgPattern.exec(text);
			if (hasNextArgument) {
				index = argMatcher.getIndex();
			}
		}
		// if (hasNextArgument && (find || argMatcher.start() == caretPos)) {
		if (hasNextArgument && argMatcher.getGroup(1) != null
				&& (find || index == caretPos)) {
			// setCaretPosition(argMatcher.end();
			// moveCaretPosition(argMatcher.start() + 1);
			if (updateUI) {
				String groupStr = argMatcher.getGroup(1);
				textField.getValueBox().setSelectionRange(index + 2,
					groupStr.length());
			}

			return true;
		}
		return false;
	}

	@Override
	public void onKeyPress(KeyPressEvent e) {
		if (GlobalKeyDispatcherW.isBadKeyEvent(e)) {
			e.preventDefault();
			e.stopPropagation();
			return;
		}

		// only handle parentheses
		char ch = e.getCharCode();

		int caretPos = getCaretPosition();

		String text = getText();

		// checking for isAltDown() because Alt+, prints another character on
		// the PC
		// TODO make this more robust - perhaps it could go in a document change
		// listener
		if (ch == ',' && !e.isAltKeyDown()) {
			if (caretPos < text.length() && text.charAt(caretPos) == ',') {
				// User typed ',' just in ahead of an existing ',':
				// We may be in the position of filling in the next argument of
				// an autocompleted command
				// Look for a pattern of the form ", < Argument Description > ,"
				// or ", < Argument Description > ]"
				// If found, select the argument description so that it can
				// easily be typed over with the value
				// of the argument.
				if (moveToNextArgument(false, true)) {
					e.stopPropagation();
					e.preventDefault();
				}
				return;
			}
		}
		if (MathFieldW.checkCode(e.getNativeEvent(), "NumpadDecimal")) {
			e.preventDefault();
			insertString(".");
			return;
		}
		ArrayList<GeoElement> sel = app.getSelectionManager().getSelectedGeos();
		GeoElement curr = sel.size() != 0 ? sel.get(0) : null;
		if (Browser.isTabletBrowser() && !app.isWhiteboardActive()
				&& e.getNativeEvent().getKeyCode() != GWTKeycodes.KEY_BACKSPACE
				&& e.getNativeEvent().getKeyCode() != 0
				&& !(curr instanceof GeoInputBox)) {
			insertString(Character.toString(ch));
			text = getText();
		}
		if (!(ch == '(' || ch == '{' || ch == '[' || ch == '}' || ch == ')'
				|| ch == ']')) {
			// super.keyTyped(e);
			Log.debug("super.keyTyped needed in AutocompleteTextField");
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
					e.preventDefault();
					caretPos++;
				}
			}
		}

		// auto-close parentheses
		if (caretPos == text.length()) {
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
		}

		// make sure we keep the previous caret position
		setCaretPosition(Math.min(text.length(), caretPos));
	}

	@Override
	public void onKeyDown(KeyDownEvent e) {
		if (!isTabEnabled()) {
			return;
		}
		if (MathFieldW.isRightAlt(e.getNativeEvent())) {
			rightAltDown = true;
		}
		if (MathFieldW.isLeftAlt(e.getNativeEvent())) {
			leftAltDown = true;
		}
		if (leftAltDown) {
			Log.debug("TODO: preventDefault");
		}
		int keyCode = e.getNativeKeyCode();
		app.getGlobalKeyDispatcher();
		if (keyCode == GWTKeycodes.KEY_F1
				|| GlobalKeyDispatcherW.isBadKeyEvent(e)) {
			e.preventDefault();

		}
		if (keyCode == GWTKeycodes.KEY_TAB && moveToNextArgument(true, false)) {
			e.preventDefault();
		}
		if (keyCode == GWTKeycodes.KEY_TAB && usedForInputBox()) {
			e.preventDefault();
			AutoCompleteTextField tf = app.getActiveEuclidianView()
					.getTextField();
			if (tf != null) {
				geoUsedForInputBox.setText(tf.getText());
				tf.setVisible(false);
			}

			app.getGlobalKeyDispatcher().handleTab(e.isControlKeyDown(),
					e.isShiftKeyDown());
			ArrayList<GeoElement> selGeos = app.getSelectionManager().getSelectedGeos();
			GeoElement next = selGeos.isEmpty() ? null : selGeos.get(0);
			if (next instanceof GeoInputBox) {
				app.getActiveEuclidianView().focusTextField((GeoInputBox) next);
			} else {
				app.getActiveEuclidianView().requestFocus();
			}
		}
		handleTabletKeyboard(e);
	}

	private void handleTabletKeyboard(KeyDownEvent e) {
		if (!Browser.isTabletBrowser() || usedForInputBox() || app.isWhiteboardActive()) {
			return;
		}
		int keyCode = e.getNativeKeyCode();
		if (keyCode == 0 && Browser.isIPad()) {
			int arrowType = Browser.getIOSArrowKeys(e.getNativeEvent());
			if (arrowType != -1) {
				keyCode = arrowType;
			}
		}
		switch (keyCode) {
		case GWTKeycodes.KEY_BACKSPACE:
			onBackSpace();
			break;
		case GWTKeycodes.KEY_LEFT:
			onArrowLeft();
			break;
		case GWTKeycodes.KEY_RIGHT:
			onArrowRight();
			break;
		case GWTKeycodes.KEY_UP:
			handleUpArrow();
			break;
		case GWTKeycodes.KEY_DOWN:
			handleDownArrow();
			break;
		default:
			break;
		}
	}

	@Override
	@SuppressFBWarnings({ "SF_SWITCH_FALLTHROUGH",
			"missing break is deliberate" })
	public void onKeyUp(KeyUpEvent e) {
		int keyCode = e.getNativeKeyCode();
		// we don't want to trap AltGr
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltKeyDown() && e.isControlKeyDown()) {
			return;
		}

		switch (keyCode) {

		case GWTKeycodes.KEY_Z:
		case GWTKeycodes.KEY_Y:
			if (e.isControlKeyDown()) {
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
				e.stopPropagation();
			}
			break;
		case GWTKeycodes.KEY_C:
			break;

		// process input

		case GWTKeycodes.KEY_ESCAPE:
			if (!handleEscapeKey) {
				break;
			}

			/* TODO maybe close parent dialog? */
			if (textField.isSuggestionListVisible()) {
				textField.hideSuggestions();
			} else {
				textField.setFocus(false);
				app.getActiveEuclidianView().requestFocus();
			}
			break;

		case GWTKeycodes.KEY_UP:
			handleUpArrow();
			e.stopPropagation();
			break;

		case GWTKeycodes.KEY_DOWN:
			handleDownArrow();
			e.stopPropagation(); // prevent GlobalKeyDispatcherW to move the
									// euclidian view
			break;

		case GWTKeycodes.KEY_F9:
			// needed for applets
			if (app.isApplet()) {
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
			}
			break;
		case GWTKeycodes.KEY_LEFT:
			textField.hideSuggestions();
			e.stopPropagation();
			break;
		case GWTKeycodes.KEY_RIGHT:
			if (moveToNextArgument(false, true)) {
				e.stopPropagation();
				textField.hideSuggestions();
			}
			e.stopPropagation();
			break;

		case GWTKeycodes.KEY_TAB:
			e.preventDefault();
			if (moveToNextArgument(true, true)) {
				e.stopPropagation();
			}
			break;

		case GWTKeycodes.KEY_F1:

			handleF1();

			e.stopPropagation();
			break;

		case GWTKeycodes.KEY_ZERO:
		case GWTKeycodes.KEY_ONE:
		case GWTKeycodes.KEY_TWO:
		case GWTKeycodes.KEY_THREE:
		case GWTKeycodes.KEY_FOUR:
		case GWTKeycodes.KEY_FIVE:
		case GWTKeycodes.KEY_SIX:
		case GWTKeycodes.KEY_SEVEN:
		case GWTKeycodes.KEY_EIGHT:
		case GWTKeycodes.KEY_NINE:
			if (e.isControlKeyDown() && e.isShiftKeyDown()) {
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
			}

			// fall through eg Alt-2 for squared

		default:
			if (MathFieldW.isRightAlt(e.getNativeEvent())) {
				rightAltDown = true;
			}
			if (MathFieldW.isLeftAlt(e.getNativeEvent())) {
				leftAltDown = true;
			}
			// check for eg alt-a for alpha
			// check for eg alt-shift-a for upper case alpha
			if (e.isAltKeyDown() && !rightAltDown) {

				String s = AltKeys.getAltSymbols(keyCode, e.isShiftKeyDown(),
						true);

				if (s != null) {
					insertString(s);
					break;
				}
			}

			/*
			 * Try handling here that is originaly in keyup
			 */
			boolean modifierKeyPressed = e.isControlKeyDown()
					|| e.isAltKeyDown();

			// we don't want to act when AltGr is down
			// as it is used eg for entering {[}] is some locales
			// NB e.isAltGraphDown() doesn't work
			if (e.isAltKeyDown() && e.isControlKeyDown()) {
				modifierKeyPressed = false;
			}

			char charPressed = (char) e.getNativeKeyCode();

			if ((StringUtil.isLetterOrDigitOrUnderscore(charPressed) || modifierKeyPressed)
					&& (e.getNativeKeyCode() != GWTKeycodes.KEY_A)) {
				clearSelection();
			}

			// handle alt-p etc
			// super.keyReleased(e);

			mergeKoreanDoubles();

			if (getAutoComplete()) {
				updateCurrentWord(false);
			}
		}
	}

	private void handleDownArrow() {
		if (!handleEscapeKey) {
			return;
		}
		if (historyPopup != null && historyPopup.isDownPopup()) {
			historyPopup.showPopup();
		} else {
			// Fix for Ticket #463
			if (getNextInput() != null) {
				setText(getNextInput());
			}
		}
	}

	/**
	 * moves the caret left
	 */
	public void onArrowLeft() {
		int caretPos = getCaretPosition();
		if (caretPos > 0) {
			setCaretPosition(caretPos - 1);
		}
	}

	/**
	 * moves the caret right
	 */
	public void onArrowRight() {
		int caretPos = getCaretPosition();
		if (caretPos < getText(true).length()) {
			setCaretPosition(caretPos + 1);
		}
	}

	private void handleF1() {
		if (autoComplete) {
			if (!"".equals(getText())) {
				int pos = getCaretPosition();
				while (pos > 0 && getText().charAt(pos - 1) == '[') {
					pos--;
				}
				String word = MyTextField.getWordAtPos(getText(), pos);
				String lowerCurWord = word.toLowerCase();
				String closest = getDictionary().lookup(lowerCurWord);

				if (closest != null) {
					showCommandHelp(app.getInternalCommand(closest));
				} else if (app.getGuiManager() != null) {
					app.getGuiManager().openHelp(App.WIKI_MANUAL);
				}

			}
		} else if (app.getGuiManager() != null) {
			app.getGuiManager().openHelp(App.WIKI_MANUAL);
		}
	}

	private void handleUpArrow() {
		if (!isSuggesting()) {
			if (!handleEscapeKey) {
				return;
			}
			if (historyPopup == null) {
				String text = getPreviousInput();
				if (text != null) {
					setText(text);
				}
			} else if (!historyPopup.isDownPopup()) {
				historyPopup.showPopup();
			}
		}

	}

	/**
	 * Add input to hinput history.
	 *
	 * @param str
	 *            input
	 */
	public void addToHistory(String str) {
		// exit if the new string is the same as the last entered string
		if (!history.isEmpty() && str.equals(history.get(history.size() - 1))) {
			return;
		}

		history.add(str);
		historyIndex = history.size();
	}

	@Override
	public boolean isSuggesting() {
		return textField.isSuggestionListVisible();
	}

	/**
	 * @return that suggestion is just happened (click or enter, so we don't
	 *         need to run the enter code again
	 */
	public boolean isSuggestionJustHappened() {
		return suggestionJustHappened; // && !isSuggestionClickJustHappened;
	}

	/**
	 * @param suggestion
	 *            whether suggestion happened and ENTER should be ignored
	 */
	public void setIsSuggestionJustHappened(boolean suggestion) {
		suggestionJustHappened = suggestion;
	}

	/* Hopefully happens only on click */
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {

		// textField.getValueBox().getElement().focus();
	}

	@Override
	public void onSelection(SelectionEvent<Suggestion> event) {
		suggestionJustHappened = true;
		int index = completions
				.indexOf(event.getSelectedItem().getReplacementString());
		validateAutoCompletion(index, getCompletions());
	}

	/**
	 * Inserts a string into the text at the current caret position
	 */
	@Override
	public void insertString(String text) {
		int start = getSelectionStart();
		int end = getSelectionEnd();

		setText(start, end, text);
		if (insertHandler != null) {
			insertHandler.onInsert(text);
		}
	}

	/**
	 * add handler for back space event
	 * 
	 * @param handler
	 *            handler
	 */
	public void addOnBackSpaceHandler(OnBackSpaceHandler handler) {
		onBackSpaceHandler = handler;
	}

	/**
	 * Remove a character and move virtual caret.
	 */
	public void onBackSpace() {
		int start = getSelectionStart();
		int end = getSelectionEnd();

		if (end - start < 1) {
			end = getCaretPosition();
			start = end - 1;
		}

		if (start >= 0) {
			setText(start, end, "");
		}

		if (onBackSpaceHandler != null) {
			onBackSpaceHandler.onBackspace();
		}
	}

	private void setText(int start, int end, String text) {
		// clear selection if there is one
		if (start != end) {
			String oldText = getText(true);
			setText(oldText.substring(0, start) + oldText.substring(end));
			setCaretPosition(start, false);
		}

		int pos = getCaretPosition();
		String oldText = getText(true);
		setText(oldText.substring(0, pos) + text + oldText.substring(pos));

		// setCaretPosition(pos + text.length());
		final int newPos = pos + text.length();

		this.updateCurrentWord(false);

		setCaretPosition(newPos, false);

		// TODO: tried to keep the Mac OS from auto-selecting the field by
		// resetting the
		// caret, but not working yet
		// setCaret(new DefaultCaret());
		// setCaretPosition(newPos);
	}

	private int getSelectionEnd() {
		return getSelectionStart()
				+ textField.getValueBox().getSelectionLength();
	}

	private int getSelectionStart() {
		return getText().indexOf(textField.getValueBox().getSelectedText());
	}

	@Override
	public void removeSymbolTable() {
		if (showSymbolButton == null) {
			return;
		}
		this.showSymbolButton.removeFromParent();
		this.showSymbolButton = null;
	}

	/**
	 * Show table popup next to an anchor widget
	 *
	 */
	private void showTablePopup() {
		if (tablePopup == null && this.showSymbolButton != null) {
			tablePopup = new SymbolTablePopupW(app, this, showSymbolButton);
			if (app.getGuiManager() != null) {
				app.getGuiManager().addKeyboardAutoHidePartner(tablePopup);
			}
		}
		if (this.tablePopup != null) {
			tablePopup.showRelativeTo(showSymbolButton);
		}
	}

	/**
	 * Hide symbol popup.
	 */
	public void hideTablePopup() {
		if (this.tablePopup != null) {
			this.tablePopup.hide();
		}
	}

	@Override
	public String getText() {
		String text = textField.getText();
		if (dummyCursor.isActive()) {
			int cpos = getCaretPosition();
			text = text.substring(0, cpos) + text.substring(cpos + 1);
		}
		return text;
	}

	/**
	 * @param withDummyCursor
	 *            whether to include dummy cursor
	 * @return input text
	 */
	public String getText(boolean withDummyCursor) {
		if (withDummyCursor) {
			return textField.getText();
		}
		return getText();
	}

	@Override
	public void setText(String s) {
		textField.getValueBox().setText(s);
	}

	/**
	 * @return wrapped input element
	 */
	public FocusWidget getTextBox() {
		return textField.getValueBox();
	}

	/**
	 * @return input with suggestions
	 */
	public GSuggestBox getTextField() {
		return textField;
	}

	/**
	 * Ticket #1167 Auto-completes input; <br>
	 *
	 * @param index
	 *            index of the chosen command in the completions list
	 * @param completionList
	 *            list of completions
	 * @author Arnaud
	 */
    private void validateAutoCompletion(int index, List<String> completionList) {
		if (completionList == null || index < 0 || index >= completionList.size()) {
			return;
		}

		String command = completionList.get(index);
		int bracketIndex = command.indexOf('[');

		// Special case if the completion is a built-in function
		if (bracketIndex == -1) {
			bracketIndex = command.indexOf('(');
		}

		setCaretPosition(curWordStart + bracketIndex);
		moveToNextArgument(false, true);
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
		textField.setFocus(true);

		if (geoUsedForInputBox != null && !geoUsedForInputBox.isSelected()) {
			app.getSelectionManager().clearSelectedGeos(false);
			app.getSelectionManager().addSelectedGeo(geoUsedForInputBox);
		}
	}

	@Override
	public void setFocusTraversalKeysEnabled(boolean b) {
		// Dummy method
	}

	@Override
	public boolean hasFocus() {
		return false;
	}

	/**
	 * @return if text must start with "=" to activate autocomplete
	 */
	public boolean isEqualsRequired() {
		return equalSignRequired;
	}

	/** sets flag to require text starts with "=" to activate autocomplete */
	public void setEqualsRequired(boolean isEqualsRequired) {
		this.equalSignRequired = isEqualsRequired;
	}

	public void setCASInput(boolean b) {
		this.isCASInput = b;
	}

	@Override
	public void addKeyHandler(KeyHandler handler) {
		textField.getValueBox().addKeyPressHandler(new KeyListenerW(handler));
	}

	/**
	 * @param handler
	 *            Handler to key up events
	 * @return the handler
	 */
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return textField.getValueBox().addKeyUpHandler(handler);
	}

	/**
	 * @param width
	 *            pixel width
	 */
	public void setWidth(int width) {
		if (width > 0) {
			textField.setWidth(width + "px");
			super.setWidth(width + "px");
		}
	}

	@Override
	public String getCommand() {
		this.updateCurrentWord(true);
		return this.getCurrentWord();
	}

	/**
	 * @param handler
	 *            Adds a focus handler to the wrapped textfield.
	 * @return reference to the handler
	 */
	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return textField.getValueBox().addFocusHandler(handler);
	}

	/**
	 * Connect to focus/blur handlers to keyboard; disable native editing on
	 * tablet.
	 */
	public void enableGGBKeyboard() {
		dummyCursor.enableGGBKeyboard();
	}

	/**
	 * Selects all text.
	 */
	public void selectAll() {
		textField.getValueBox().selectAll();
	}

	/**
	 * Adds key handler to the tetxtfield
	 *
	 * @param handler
	 *            Keypresshandler
	 * @return registration
	 */
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return textField.getValueBox().addKeyPressHandler(handler);
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return getTextBox().addBlurHandler(handler);
	}

	private boolean isTabEnabled() {
		return tabEnabled;
	}

	public void setTabEnabled(boolean tabEnabled) {
		this.tabEnabled = tabEnabled;
	}

	/**
	 * use this, if no explicit length set, but symbolbutton must be shown.
	 */
	public void requestToShowSymbolButton() {
		if (showSymbolButton == null) {
			return;
		}
		prepareShowSymbolButton(true);
	}

	@Override
	public void prepareShowSymbolButton(boolean show) {
		if (showSymbolButton == null) {
			return;
		}
		Dom.toggleClass(this, "SymbolCanBeShown", show);
	}

	@Override
	public void setFocus(boolean focus, boolean sv) {
		setFocus(focus);
	}

	@Override
	public void setFocus(boolean focus) {
		textField.setFocus(focus);
	}

	public void addInsertHandler(InsertHandler newInsertHandler) {
		this.insertHandler = newInsertHandler;
	}

	@Override
	public Widget toWidget() {
		return this;
	}

	@Override
	public void ensureEditing() {
		// TODO Auto-generated method stub
	}

	@Override
	public void startOnscreenKeyboardEditing() {
		if (Browser.isAndroid()) {
			addDummyCursor();
		}
	}

	@Override
	public void endOnscreenKeyboardEditing() {
		if (Browser.isAndroid()) {
			removeDummyCursor();
		}
	}

	@Override
	public void drawBounds(GGraphics2D g2, GColor bgColor, GRectangle bounds) {
		drawBounds(g2, bgColor, ((int) bounds.getX()), ((int) bounds.getY()),
				((int) bounds.getWidth()), ((int) bounds.getHeight()));
	}

	@Override
	public void drawBounds(GGraphics2D g2, GColor bgColor, int left, int top,
			int width, int height) {
		g2.setPaint(bgColor);
		g2.fillRoundRect(left, top, width, height, BOX_ROUND, BOX_ROUND);

		// TF Rectangle
		g2.setPaint(GColor.LIGHT_GRAY);
		g2.drawRoundRect(left, top, width, height, BOX_ROUND, BOX_ROUND);
	}

	@Override
	public void hideDeferred(GBox box) {
		// only needed in desktop
	}

	@Override
	public void autocomplete(String s) {
		getTextField().setText(s);
		ArrayList<String> arr = new ArrayList<>();
		arr.add(s);
		validateAutoCompletion(0, arr);
	}

	@Override
	public void onEnter(boolean b) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updatePosition(AbstractSuggestionDisplay sug) {
		sug.setPositionRelativeTo(textField);

	}

	@Override
	public boolean isForCAS() {
		return false;
	}

	@Override
	public boolean needsAutofocus() {
		return false;
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
	public App getApplication() {
		return app;
	}

	@Override
	public Element getInputElement() {
		return getTextField().getElement();
	}

	@Override
	public void setReadOnly(boolean readonly) {
		getTextField().getValueBox().setReadOnly(true);
	}

	@Override
	public int getCursorPos() {
		return getCaretPosition();
	}

	@Override
	public void setCursorPos(int pos) {
		getTextField().getValueBox().setCursorPos(pos);
	}

	@Override
	public void setValue(String text) {
		setText(text);
	}

	@Override
	public String getValue() {
		return getText(true);
	}

	@Override
	public void setSelection(int start, int end) {
		textField.getValueBox().setSelectionRange(start, end - start);
	}

	@Override
	public void setTextAlignmentsForInputBox(TextAlignment alignment) {
		switch (alignment) {
			case LEFT:
				getElement().getFirstChildElement().getStyle().
						setTextAlign(Style.TextAlign.LEFT);
				break;
			case CENTER:
				getElement().getFirstChildElement().getStyle().
						setTextAlign(Style.TextAlign.CENTER);
				break;
			case RIGHT:
				getElement().getFirstChildElement().getStyle().
						setTextAlign(Style.TextAlign.RIGHT);
				break;
		}
	}
}
