package org.geogebra.web.html5.gui.inputfield;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.euclidian.event.FocusListener;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.VirtualKeyboardListener;
import org.geogebra.common.gui.inputfield.AltKeys;
import org.geogebra.common.gui.inputfield.AutoComplete;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.gui.inputfield.MyTextField;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GWTKeycodes;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.AutoCompleteDictionary;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.event.KeyEventsHandler;
import org.geogebra.web.html5.event.KeyListenerW;
import org.geogebra.web.html5.gui.HasKeyboardTF;
import org.geogebra.web.html5.gui.util.BasicIcons;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.gui.view.autocompletion.CompletionsPopup;
import org.geogebra.web.html5.gui.view.autocompletion.ScrollableSuggestBox;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;

import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class AutoCompleteTextFieldW extends FlowPanel implements AutoComplete,
        AutoCompleteW, MathKeyboardListener,
        org.geogebra.common.gui.inputfield.AutoCompleteTextField, KeyDownHandler,
        KeyUpHandler, KeyPressHandler, ValueChangeHandler<String>, SelectionHandler<Suggestion>,
		VirtualKeyboardListener, HasSymbolPopup, HasKeyboardTF {

	public interface InsertHandler {
		void onInsert(String text);
	}

	private static final int BOX_ROUND = 8;

	protected AppW app;
	private Localization loc;
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

	HistoryPopupW historyPopup;
	protected ScrollableSuggestBox textField = null;

	private DrawInputBox drawTextField = null;

	// symbol table popup fields
	ToggleButton showSymbolButton = null;
	private SymbolTablePopupW tablePopup;
	private boolean showSymbolTableIcon = false;
	public static boolean showSymbolButtonFocused = false;

	/**
	 * Flag to determine if text must start with "=" to activate autoComplete;
	 * used with spreadsheet cells
	 */
	private boolean isEqualsRequired = false;

	/**
	 * Flag to determine if Tab key should behave like usual or disabled.
	 */
	private boolean tabEnabled = true;
	private int columns = 0;
	private boolean forCAS;
	private InsertHandler insertHandler = null;
	/**
	 * Pattern to find an argument description as found in the syntax
	 * information of a command.
	 */
	// private static Pattern syntaxArgPattern =
	// Pattern.compile("[,\\[] *(?:<[\\(\\) \\-\\p{L}]*>|\\.\\.\\.) *(?=[,\\]])");
	// Simplified to this as there are too many non-alphabetic character in
	// parameter descriptions:
	private static com.google.gwt.regexp.shared.RegExp syntaxArgPattern = com.google.gwt.regexp.shared.RegExp
	        .compile("[,\\[\\(] *(<.*?>|\"<.*?>\"|\\.\\.\\.) *(?=[,\\]\\)])");

	boolean keyBoardModeText = false;

	private int actualFontSize = 14;
	private boolean deferredFocus = false;

	private boolean dummyCursor = false;

	/**
	 * Constructs a new AutoCompleteTextField that uses the dictionary of the
	 * given Application for autocomplete look up. A default model is created
	 * and the number of columns is 0.
	 * 
	 */
	public AutoCompleteTextFieldW(int columns, App app) {
		this(columns, (AppW) app, true, null, false);
	}

	public AutoCompleteTextFieldW(int columns, App app, Drawable drawTextField) {
		this(columns, app);
		this.drawTextField = (DrawInputBox) drawTextField;
		addStyleName("FromDrawTextFieldNew");
	}

	public AutoCompleteTextFieldW(int columns, final AppW app,
	        boolean handleEscapeKey, KeyEventsHandler keyHandler, boolean forCAS) {
		this.forCAS = forCAS;
		// AG not MathTextField and Mytextfield exists yet super(app);
		// allow dynamic width with columns = -1
		CompletionsPopup completionsPopup = new CompletionsPopup();
		textField = new ScrollableSuggestBox(completionsPopup, this) {
			@Override
			public void setText(String s) {
				String oldText = super.getText();
				int pos = getValueBox().getCursorPos();
				StringBuilder sb = new StringBuilder();
				int wp = InputHelper.updateCurrentWord(false,
						new StringBuilder(), oldText, pos, true);
				/*
				 * if(wp <= 0){ wp = pos; }
				 */
				sb.append(oldText.substring(0, wp));
				sb.append(s);
				sb.append(oldText.substring(pos));
				super.setText(sb.toString());
				// AutoCompleteTextFieldW.this.moveToNextArgument(false);
			}

			@Override
			public void onBrowserEvent(Event event) {

				// TODO required for mobile devices
				// if (showOnScreenKeyBoard
				// && DOM.eventGetType(event) == FOCUS) {
				// requestFocus();
				//
				// if (keyboardUsed && !keyBoardModeText) {
				// setFocus(false);
				// }
				// } else if (showOnScreenKeyBoard && keyboardUsed) {
				// super.onBrowserEvent(event);
				// super.setFocus(false);
				// } else {
				super.onBrowserEvent(event);
				// }

				// react on enter from system on screen keyboard or hardware
				// keyboard
				if ((event.getTypeInt() == Event.ONKEYUP || event.getTypeInt() == Event.ONKEYPRESS)
						&& event.getKeyCode() == KeyCodes.KEY_ENTER) {
					// app.hideKeyboard();
					// prevent handling in AutoCompleteTextField
					event.stopPropagation();
				}
			}
		};
		if (columns > 0) {
			setColumns(columns);
		}

		// setVerticalAlignment(ALIGN_MIDDLE);
		addStyleName("AutoCompleteTextFieldW");

		String id = DOM.createUniqueId();
		// Log.debug(id);
		// id = id.substring(7);

		textField.addStyleName("TextField");
		textField.getElement().setId(id);

		showSymbolButton = new ToggleButton() {
			@Override
			public void onBrowserEvent(Event event) {
				if (event.getTypeInt() == Event.ONMOUSEDOWN) {

					// set it as focused anyway, because it is needed
					// before the real focus and blur events take place
					showSymbolButton.addStyleName("ShowSymbolButtonFocused");
					showSymbolButtonFocused = true;
				}
				super.onBrowserEvent(event);

				// this insight has been learnt in InputTreeItem
				// i.e. do not do it for some event types, e.g.
				// at least in the following three cases:
				if (event.getTypeInt() == Event.ONMOUSEMOVE
				        || event.getTypeInt() == Event.ONMOUSEOVER
				        || event.getTypeInt() == Event.ONMOUSEOUT)
					return;

				// autoCompleteTextField should not loose focus
				AutoCompleteTextFieldW.this.setFocus(true);
			}
		};
		textField.setShowSymbolElement(this.showSymbolButton.getElement());
		showSymbolButton.getElement().setId(id + "_SymbolButton");
		showSymbolButton.getElement().setAttribute("data-visible", "false");
		// showSymbolButton.getElement().setAttribute("style", "display: none");
		showSymbolButton.setText(Unicode.alpha + "");
		showSymbolButton.addStyleName("SymbolToggleButton");
		showSymbolButton.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				showSymbolButton.removeStyleName("ShowSymbolButtonFocused");
				showSymbolButtonFocused = false;
				// TODO: make it disappear when blurred
				// to a place else than the textfield?
			}
		});
		showSymbolButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				CancelEventTimer.disableBlurEvent();
				if (showSymbolButton.isDown()) {
					// when it is still down, it will be changed to up
					// when it is still up, it will be changed to down
					showTablePopupRelativeTo(showSymbolButton);

				} else {
					hideTablePopup();
				}
				// autoCompleteTextField should not loose focus
				AutoCompleteTextFieldW.this.setFocus(true);
			}
		});

		showSymbolButton.setFocus(false);
		add(textField);
		add(showSymbolButton);

		this.app = app;
		this.loc = app.getLocalization();
		setAutoComplete(true);
		this.handleEscapeKey = handleEscapeKey;
		curWord = new StringBuilder();

		historyIndex = 0;
		history = new ArrayList<String>(50);

		completions = null;

		// CommandCompletionListCellRenderer cellRenderer = new
		// CommandCompletionListCellRenderer();
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

		init();
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

	private void init() {
		textField.getValueBox().addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				// AG I dont understand thisAutoCompleteTextField tf =
				// ((AutoCompleteTextField)event.getSource());
				// AG tf.setFocus(true);
				// textField.setFocus(true);
				requestFocus();
				if (app != null) {
					((GlobalKeyDispatcherW) app.getGlobalKeyDispatcher()).setFocused(true);
				}
			}
		});
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

		if (historyPopup == null)
			historyPopup = new HistoryPopupW(this, app.getPanel());

		historyPopup.setDownPopup(isDownPopup);

		ClickHandler al = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// AGString cmd = event.;
				// AGif (cmd.equals(1 + BorderButton.cmdSuffix)) {
				// TODO: should up/down orientation be tied to InputBar?
				// show popup
				historyPopup.showPopup();

			}
		};
		setBorderButton(1, BasicIcons.createUpDownTriangleIcon(false, true), al);
		this.setBorderButtonVisible(1, false);
	}

	private void setBorderButtonVisible(int i, boolean b) {
		//
	}

	private void setBorderButton(int i, ImageData createUpDownTriangleIcon,
	        ClickHandler al) {
		//
	}

	@Override
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		//
	}

	@Override
	public void showPopupSymbolButton(boolean b) {
		this.showSymbolTableIcon = b;
		if (showSymbolButton == null) {
			return;
		}
		// temp
		// TODO: don't fix the popup button here, but it should appear if mouse
		// clicked into the textfield.
		if ((showSymbolTableIcon)
		        && app.isAllowedSymbolTables()
		        && this.columns > EuclidianConstants.SHOW_SYMBOLBUTTON_MINLENGTH) {
			showSymbolButton.getElement().addClassName("shown");
			showSymbolButton.getElement().setAttribute("data-persist", "true");
		} else {
			showSymbolButton.getElement().removeClassName("shown");
			showSymbolButton.getElement().removeAttribute("data-persist");
		}
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

		/*
		 * if (autoComplete) app.initTranslatedCommands();
		 */

	}

	public List<String> resetCompletions() {
		String text = getText();
		updateCurrentWord(false);
		completions = null;
		if (isEqualsRequired && !text.startsWith("="))
			return null;

		boolean korean = false; // AG
		                        // app.getLocale().getLanguage().equals("ko");

		// start autocompletion only for words with at least two characters
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
		ArrayList<String> syntaxes = new ArrayList<String>();
		for (String cmd : commands) {

			String cmdInt = app.getInternalCommand(cmd);

			String syntaxString;
			if (isCASInput) {
				syntaxString = loc.getCommandSyntaxCAS(cmdInt);
			} else {
				syntaxString = app.getExam() == null ? loc
						.getCommandSyntax(cmdInt) : app.getExam().getSyntax(
								cmdInt, loc, app.getSettings());
			}
			if (syntaxString == null) {
				continue;
			}
			if (syntaxString.endsWith(isCASInput ? Localization.syntaxCAS
			        : Localization.syntaxStr)) {

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
		textField.getElement().getStyle().setFontSize(font.getSize(), Unit.PX);

		if (showSymbolButton != null) {
			showSymbolButton.getElement().getStyle()
			        .setFontSize(font.getSize(), Unit.PX);
			showSymbolButton.getElement().getStyle()
			        .setLineHeight(font.getSize(), Unit.PX);
		}

		if (columns > 0) {
			setColumns(this.columns);
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
	public void setColumns(int columns) {
		this.columns = columns;
		if (showSymbolButton != null
		        && (this.columns > EuclidianConstants.SHOW_SYMBOLBUTTON_MINLENGTH || this.columns == -1)) {
			prepareShowSymbolButton(true);
		}

		if (this.drawTextField != null) {
			// only use the correct code for members of the EuclidianView

			int columnWidth = 11;
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
				break;// 18:15:educated guess
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

	public String getCurrentWord() {
		return curWord.toString();
	}

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
		if (dummyCursor && moveDummyCursor) {
			if (caretPos == textField.getText().length()) {
				return;
			}
			removeDummyCursor();
			addDummyCursor(caretPos);
		} else {
			textField.getValueBox().setCursorPos(caretPos);
		}

	}

	public void addDummyCursor(int caretPos) {
		String text = textField.getText();
		text = text.substring(0, caretPos) + '|' + text.substring(caretPos);

		textField.setValue(text);
		textField.getValueBox().setCursorPos(caretPos);
		dummyCursor = true;
	}

	public void addDummyCursor() {
		int caretPos = getCaretPosition();
		addDummyCursor(caretPos);
	}

	public void removeDummyCursor() {
		if (!dummyCursor) {
			return;
		}
		String text = textField.getText();
		int cpos = getCaretPosition();
		text = text.substring(0, cpos) + text.substring(cpos + 1);

		textField.setValue(text);
		dummyCursor = false;
	}

	public boolean hasDummyCursor() {
		return dummyCursor;
	}

	@Override
	public void setDictionary(boolean forCAS) {
		this.forCAS = forCAS;
		this.dict = null;
	}

	@Override
	public AutoCompleteDictionary getDictionary() {
		if (this.dict == null) {
			this.dict = this.forCAS ? app.getCommandDictionaryCAS() : app
			        .getCommandDictionary();
		}
		return dict;
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
			app.showError(new MyError(loc, loc.getPlain("Syntax") + ":\n"
					+ help, cmd, null));
		} else if (app.getGuiManager() != null) {
			app.getGuiManager().openCommandHelp(null);
		}
	}

	private void clearSelection() {
		int start = textField.getText().indexOf(
		        textField.getValueBox().getSelectedText());
		int end = start + textField.getValueBox().getSelectionLength();
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
	 * Updates curWord to word at current caret position. curWordStart,
	 * curWordEnd are set to this word's start and end position
	 */
	public void updateCurrentWord(boolean searchRight) {
		int next = InputHelper.updateCurrentWord(searchRight, this.curWord,
				getText(),
				getCaretPosition(), true);
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

	public void mergeKoreanDoubles() {
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

	private boolean moveToNextArgument(boolean find) {
		String text = getText();
		int caretPos = getCaretPosition();

		// make sure it works if caret is just after [
		if (caretPos > 0 && text.length() < caretPos
		        && text.charAt(caretPos) != '[')
			caretPos--;
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
			String groupStr = argMatcher.getGroup(1);
			textField.getValueBox().setSelectionRange(index + 2,
			        groupStr.length());

			return true;
		}
		return false;
	}

	// ----------------------------------------------------------------------------
	// Protected methods ..why? :-)
	// ----------------------------------------------------------------------------

	boolean ctrlC = false;

	@Override
	public void onKeyPress(KeyPressEvent e) {
		if (app.getGlobalKeyDispatcher().isBadKeyEvent(e)) {
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
				if (moveToNextArgument(false)) {
					e.stopPropagation();
					e.preventDefault();
				}
				return;
			}
		}

		if (!(ch == '(' || ch == '{' || ch == '[' || ch == '}' || ch == ')' || ch == ']')) {
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
					e.preventDefault();
					caretPos++;
				}
			}

		}

		// auto-close parentheses
		if (caretPos == text.length()
		        || MyTextField
		                .isCloseBracketOrWhitespace(text.charAt(caretPos))) {
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

	// private boolean isBadKeyEvent(KeyEvent e) {
	// return e.isAltKeyDown() && !e.isControlKeyDown()
	// && e.getNativeEvent().getCharCode() > 128;
	// }
	@Override
	public void onKeyDown(KeyDownEvent e) {
		if (!isTabEnabled()) {
			return;
		}
		int keyCode = e.getNativeKeyCode();
		if (keyCode == GWTKeycodes.KEY_TAB || keyCode == GWTKeycodes.KEY_F1
				|| app.getGlobalKeyDispatcher().isBadKeyEvent(e)) {
			e.preventDefault();
			if (keyCode == GWTKeycodes.KEY_TAB && usedForInputBox()) {
				AutoCompleteTextField tf = app.getActiveEuclidianView()
						.getTextField();
				if (tf != null) {
					geoUsedForInputBox.setText(tf.getText());
					tf.setVisible(false);
				}

				app.getGlobalKeyDispatcher().handleTab(e.isControlKeyDown(),
						e.isShiftKeyDown(), true);
				GeoElement next = app.getSelectionManager().getSelectedGeos()
						.get(0);
				if (next instanceof GeoInputBox) {
					app.getActiveEuclidianView()
							.focusTextField((GeoInputBox) next);
				} else {
					app.getActiveEuclidianView().requestFocus();
				}
			}
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
		if (e.isAltKeyDown() && e.isControlKeyDown())
			return;

		// swallow eg ctrl-a ctrl-b ctrl-p on Mac
		/*
		 * AG if (Application.MAC_OS && e.isControlKeyDown()) { e.consume(); }
		 */

		ctrlC = false;

		switch (keyCode) {

		case GWTKeycodes.KEY_Z:
		case GWTKeycodes.KEY_Y:
			if (e.isControlKeyDown()) {
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
				e.stopPropagation();
			}
			break;
		case GWTKeycodes.KEY_C:
			if (e.isControlKeyDown()) // workaround for MAC_OS
			{
				ctrlC = true;
			}
			break;

		// process input

		case GWTKeycodes.KEY_ESCAPE:
			if (!handleEscapeKey) {
				break;
			}

			/*
			 * AG do this if we will have windows Component comp =
			 * SwingUtilities.getRoot(this); if (comp instanceof JDialog) {
			 * ((JDialog) comp).setVisible(false); return; }
			 */
			if (textField.isSuggestionListVisible()) {
				textField.hideSuggestions();
			} else {
				textField.setFocus(false);
				app.getActiveEuclidianView().requestFocus();
			}
			break;

		case GWTKeycodes.KEY_UP:
			if (!isSuggesting()) {
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
			}
			e.stopPropagation();
			break;

		case GWTKeycodes.KEY_DOWN:
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
			e.stopPropagation(); // prevent GlobalKeyDispatcherW to move the
			                     // euclidian view
			break;

		case GWTKeycodes.KEY_F9:
			// needed for applets
			if (app.isApplet())
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
			break;
		case GWTKeycodes.KEY_LEFT:
			textField.hideSuggestions();
			e.stopPropagation();
			break;
		case GWTKeycodes.KEY_RIGHT:
			if (moveToNextArgument(false)) {
				e.stopPropagation();
				textField.hideSuggestions();
			}
			e.stopPropagation();
			break;

		case GWTKeycodes.KEY_TAB:
			e.preventDefault();
			if (moveToNextArgument(true)) {
				e.stopPropagation();
			}
			break;

		case GWTKeycodes.KEY_F1:

			if (autoComplete) {
				if (!"".equals(getText())) {
					int pos = getCaretPosition();
					while (pos > 0 && getText().charAt(pos - 1) == '[') {
						pos--;
					}
					String word = getWordAtPos(getText(), pos);
					String lowerCurWord = word.toLowerCase();
					String closest = getDictionary().lookup(lowerCurWord);

					if (closest != null)// &&
					                    // lowerCurWord.equals(closest.toLowerCase()))
						showCommandHelp(app.getInternalCommand(closest));
					else if (app.getGuiManager() != null) {
						app.getGuiManager().openHelp(App.WIKI_MANUAL);
					}

				}
			} else if (app.getGuiManager() != null) {
				app.getGuiManager().openHelp(App.WIKI_MANUAL);
			}

			e.stopPropagation();
			break;

		case GWTKeycodes.KEY_0:
		case GWTKeycodes.KEY_1:
		case GWTKeycodes.KEY_2:
		case GWTKeycodes.KEY_3:
		case GWTKeycodes.KEY_4:
		case GWTKeycodes.KEY_5:
		case GWTKeycodes.KEY_6:
		case GWTKeycodes.KEY_7:
		case GWTKeycodes.KEY_8:
		case GWTKeycodes.KEY_9:
			if (e.isControlKeyDown() && e.isShiftKeyDown())
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);

			// fall through eg Alt-2 for squared

		default:

			// check for eg alt-a for alpha
			// check for eg alt-shift-a for upper case alpha
			if (e.isAltKeyDown()) {

				String s = AltKeys.getAltSymbols(keyCode,
						e.isShiftKeyDown(), true);

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
			if (e.isAltKeyDown() && e.isControlKeyDown())
				modifierKeyPressed = false;

			char charPressed = Character.valueOf((char) e.getNativeKeyCode());

			if ((StringUtil.isLetterOrDigitOrUnderscore(charPressed) || modifierKeyPressed)
			        && !(ctrlC) && !(e.getNativeKeyCode() == GWTKeycodes.KEY_A)) {
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

	public void addToHistory(String str) {
		// exit if the new string is the same as the last entered string
		if (!history.isEmpty() && str.equals(history.get(history.size() - 1)))
			return;

		history.add(str);
		historyIndex = history.size();
	}

	public boolean isSuggesting() {
		return textField.isSuggestionListVisible();
	}

	private boolean isSuggestionJustHappened = false;
	private boolean isSuggestionClickJustHappened = false;
	private GeoInputBox geoUsedForInputBox;

	/**
	 * @return that suggestion is just happened (click or enter, so we don't
	 *         need to run the enter code again
	 */
	public boolean isSuggestionJustHappened() {
		return isSuggestionJustHappened;// && !isSuggestionClickJustHappened;
	}

	public void setIsSuggestionJustHappened(boolean b) {
		isSuggestionJustHappened = b;
		isSuggestionClickJustHappened = b;
	}

	/* Hopefully happens only on click */
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		isSuggestionClickJustHappened = true;

		// textField.getValueBox().getElement().focus();
	}

	@Override
	public void onSelection(SelectionEvent<Suggestion> event) {
		isSuggestionJustHappened = true;
		int index = completions.indexOf(event.getSelectedItem()
		        .getReplacementString());
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
	}

	private void setText(int start, int end, String text) {
		// clear selection if there is one
		if (start != end) {
			String oldText = getText();
			StringBuilder sb = new StringBuilder();
			sb.append(oldText.substring(0, start));
			sb.append(oldText.substring(end));
			setText(sb.toString());
			setCaretPosition(start, false);
		}

		int pos = getCaretPosition();
		String oldText = getText();
		StringBuilder sb = new StringBuilder();
		sb.append(oldText.substring(0, pos));
		sb.append(text);
		sb.append(oldText.substring(pos));
		setText(sb.toString());

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

	void showTablePopupRelativeTo(Widget w) {
		if (tablePopup == null && this.showSymbolButton != null)
			tablePopup = new SymbolTablePopupW(app, this, showSymbolButton);
		if (this.tablePopup != null) {
			tablePopup.showRelativeTo(w);
		}
	}

	public void hideTablePopup() {
		if (this.tablePopup != null) {
			this.tablePopup.hide();
		}
	}

	@Override
	public String getText() {
		return textField.getText();
	}

	@Override
	public void setText(String s) {
		textField.getValueBox().setText(s);
	}

	public FocusWidget getTextBox() {
		return textField.getValueBox();
	}

	public void toggleSymbolButton(boolean toggled) {
		if (showSymbolButton == null) {
			return;
		}
		showSymbolButton.setDown(toggled);
	}

	public SuggestBox getTextField() {
		return textField;
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
		// String text = getText();
		// StringBuilder sb = new StringBuilder();
		// sb.append(text.substring(0, curWordStart));
		// sb.append(command);
		// sb.append(text.substring(curWordStart + curWord.length()));
		// setText(sb.toString());
		int bracketIndex = command.indexOf('[');// + 1;
		// Special case if the completion is a built-in function
		if (bracketIndex == -1) {
			bracketIndex = command.indexOf('(');
			bracketIndex++;
			/*
			 * setCaretPosition(curWordStart + bracketIndex + 1); return true;
			 */
		}
		setCaretPosition(curWordStart + bracketIndex);
		moveToNextArgument(false);
		return true;
	}

	/**
	 * This method inspects its first parameter - an Object, if there is a
	 * symbol button associated with that. If there is one, this method sets the
	 * visibility of the symbol button according to the second parameter.
	 * 
	 * @param source
	 *            the scanned object
	 * @param show
	 *            true, if the source's symbol button must be visible, false
	 *            otherwise.
	 */
	public static void showSymbolButtonIfExists(Object source, boolean show) {
		if (source instanceof HasSymbolPopup) {
			((HasSymbolPopup) source).showPopup(show);
		}
	}

	@Override
	public void setUsedForInputBox(GeoInputBox geo) {
		geoUsedForInputBox = geo;
	}

	@Override
	public boolean usedForInputBox() {
		return geoUsedForInputBox != null;
	}

	Scheduler.ScheduledCommand cmdDeferredFocus = new Scheduler.ScheduledCommand() {
		public void execute() {
			textField.setFocus(true);
		}
	};


	@Override
	public void requestFocus() {
		// if (app.isPrerelease()) {
		// app.showKeyboard(this);
		//
		// // TODO needs to be removed for mobile devices
		// textField.setFocus(true);
		// } else {

		// #5371
		if (hasDeferredFocus()) {
			Scheduler.get().scheduleDeferred(cmdDeferredFocus);
		}
		textField.setFocus(true);

		// }

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
		Log.debug("Unimplemented");
		return false;
	}

	/** returns if text must start with "=" to activate autocomplete */
	public boolean isEqualsRequired() {
		return isEqualsRequired;
	}

	/** sets flag to require text starts with "=" to activate autocomplete */
	public void setEqualsRequired(boolean isEqualsRequired) {
		this.isEqualsRequired = isEqualsRequired;
	}

	public void setCASInput(boolean b) {
		this.isCASInput = b;
	}

	@Override
	public void addKeyHandler(KeyHandler handler) {
		textField.getValueBox().addKeyPressHandler(new KeyListenerW(handler));

	}

	/**
	 * @param hanlder
	 *            Handler to key up events
	 * @return the handler
	 */
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return textField.getValueBox().addKeyUpHandler(handler);
	}

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
	 */
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return textField.getValueBox().addFocusHandler(handler);

	}

	/**
	 * Selects all text.
	 */
	public void selectAll() {
		textField.getValueBox().selectAll();
	}

	/**
	 * @param handler
	 *            Keypresshandler
	 * 
	 *            Added to tetxtfield as handler
	 */
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return textField.getValueBox().addKeyPressHandler(handler);

	}

	/**
	 * @param handler
	 *            Blurhandler attached to texbox
	 */
	public void addBlurHandler(BlurHandler handler) {
		getTextBox().addBlurHandler(handler);

	}

	public boolean isTabEnabled() {
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
		this.columns = EuclidianConstants.SHOW_SYMBOLBUTTON_MINLENGTH + 1;
		prepareShowSymbolButton(true);
	}

	@Override
	public void prepareShowSymbolButton(boolean b) {
		if (showSymbolButton == null) {
			return;
		}
		if (b) {
			showSymbolButton.getElement().setAttribute("data-visible", "true");
			addStyleName("SymbolCanBeShown");
		} else {
			showSymbolButton.getElement().setAttribute("data-visible", "false");
			removeStyleName("SymbolCanBeShown");
		}
	}

	public void setFocus(boolean b, boolean sv) {
		setFocus(b);
	}

	public void setFocus(boolean b) {
		textField.setFocus(b);
	}

	@Override
	public void showPopup(boolean show) {
		if (this.showSymbolButton == null) {
			return;
		}
		Element showSymbolElement = this.showSymbolButton.getElement();
		// Log.debug("AF focused" + show);
		if (showSymbolElement != null
		        && "true"
		                .equals(showSymbolElement.getAttribute("data-visible"))) {
			if (show) {
				// Log.debug("AF focused2" + show);
				showSymbolElement.addClassName("shown");
			} else {
				// Log.debug("AF focused2" + show);
				if (!"true".equals(showSymbolElement
				        .getAttribute("data-persist"))) {
					showSymbolElement.removeClassName("shown");
				}
			}
		}
	}

	public void addInsertHandler(InsertHandler insertHandler) {
		this.insertHandler = insertHandler;
	}

	public void setKeyBoardModeText(boolean keyBoardModeText) {
		this.keyBoardModeText = keyBoardModeText;
	}

	@Override
	public Widget toWidget() {
		return this;
	}

	@Override
	public void ensureEditing() {
		// TODO Auto-generated method stub

	}

	public void endOnscreenKeyboardEditing() {
		if (Browser.isAndroid() || Browser.isIPad()) {
			setEnabled(true);
			removeDummyCursor();
			removeStyleName("disabledTextfieldEditing");
		}
	}

	public void startOnscreenKeyboardEditing() {
		if (Browser.isAndroid() || Browser.isIPad()) {
			setEnabled(false);
			addDummyCursor();
			addStyleName("disabledTextfieldEditing");
		}

	}

	public boolean hasDeferredFocus() {
		return deferredFocus;
	}

	public void setDeferredFocus(boolean b) {
		deferredFocus = b;
	}

	@Override
	public void drawBounds(GGraphics2D g2, GColor bgColor, int left, int top,
			int width,
			int height) {
		g2.setPaint(bgColor);
		g2.fillRoundRect(left, top, width, height, BOX_ROUND, BOX_ROUND);

		// TF Rectangle
		g2.setPaint(GColor.LIGHT_GRAY);
		g2.drawRoundRect(left, top, width, height, BOX_ROUND, BOX_ROUND);

	}

	public void hideDeferred(final GBox box) {
		CancelEventTimer.blurEventOccured();
		Scheduler.get().scheduleEntry(new RepeatingCommand() {

			public boolean execute() {
				if (CancelEventTimer.cancelBlurEvent()) {
					return true;
				}

				setVisible(false);
				box.setVisible(false);
				// app.getActiveEuclidianView().remove(box);
				return false;
			}
		});
	}

	public void autocomplete(String s) {
		getTextField().setText(s);
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(s);
		validateAutoCompletion(0, arr);
	}

	public void onEnter(boolean b) {
		// TODO Auto-generated method stub
	}

	public void updatePosition(DefaultSuggestionDisplay sug) {
		sug.setPositionRelativeTo(textField);

	}

	public boolean isForCAS() {
		return false;
	}

	public boolean needsAutofocus() {
		return false;
	}

	public void setDrawTextField(DrawInputBox df) {
		drawTextField = df;
	}

	public GeoInputBox getInputBox() {
		return geoUsedForInputBox;
	}
}
