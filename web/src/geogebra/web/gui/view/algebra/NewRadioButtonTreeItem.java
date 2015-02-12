package geogebra.web.gui.view.algebra;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.util.AutoCompleteDictionary;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.inputfield.AutoCompleteW;
import geogebra.html5.gui.inputfield.HasSymbolPopup;
import geogebra.html5.gui.inputfield.HistoryPopupW;
import geogebra.html5.gui.inputfield.SymbolTablePopupW;
import geogebra.html5.gui.util.BasicIcons;
import geogebra.html5.gui.view.autocompletion.CompletionsPopup;
import geogebra.web.gui.layout.panels.AlgebraDockPanelW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * NewRadioButtonTreeItem for creating new formulas in the algebra view
 * 
 * File created by Arpad Fekete
 */
public class NewRadioButtonTreeItem extends RadioButtonTreeItem implements
        AutoCompleteW, HasSymbolPopup, FocusHandler, BlurHandler {

	public static final class ScrollableSuggestionDisplay extends
	        DefaultSuggestionDisplay {

		@Override
		protected Widget decorateSuggestionList(Widget suggestionList) {
			ScrollPanel panel = new ScrollPanel(suggestionList);
			// heuristic
			panel.getElement()
			        .getStyle()
			        .setProperty("max-height",
			                (Window.getClientHeight() / 2) + "px");
			// it's a good question what this number might be, but on
			// big screen (Window.getClientHeight() / 2) is not a problem
			// and on small screens it may also be necessary

			// we do not set max-width but allow it to display and set
			// overflow-x hidden,
			// because overflow-x auto does not work well...
			// TODO: however, in the future we might want to add max-width and
			// set
			// both overflow-x and overflow-y to auto (i.e. remove them)!
			panel.getElement().getStyle().setProperty("overflow-x", "hidden");
			panel.getElement().getStyle().setProperty("overflow-y", "auto");

			// TODO: scroll on mouse movements!
			return panel;
		}

		public void accessShowSuggestions(SuggestOracle.Response res,
		        CompletionsPopup pop, SuggestBox.SuggestionCallback xcb) {
			showSuggestions(null, res.getSuggestions(),
			        pop.isDisplayStringHTML(), true, xcb);

			// not working!
			// getPopupPanel().setHeight("50%");
		}

		public Suggestion accessCurrentSelection() {
			return getCurrentSelection();
		}

		public void accessMoveSelectionDown() {
			this.moveSelectionDown();
		}

		public void accessMoveSelectionUp() {
			this.moveSelectionUp();
		}
	}

	// How large this number should be (e.g. place on the screen, or
	// scrollable?) Let's allow practically everything
	public static int querylimit = 1000;
	public static boolean showSymbolButtonFocused = false;

	private List<String> completions;
	private StringBuilder curWord;
	private int curWordStart;
	protected AutoCompleteDictionary dict;
	protected ScrollableSuggestionDisplay sug;
	protected CompletionsPopup popup;
	protected ToggleButton showSymbolButton = null;
	private SymbolTablePopupW tablePopup;
	private int historyIndex;
	private ArrayList<String> history;
	private HashMap<String, String> historyMap;
	HistoryPopupW historyPopup;

	protected SuggestOracle.Callback popupCallback = new SuggestOracle.Callback() {
		public void onSuggestionsReady(SuggestOracle.Request req,
		        SuggestOracle.Response res) {
			sug.setPositionRelativeTo(ihtml);
			sug.accessShowSuggestions(res, popup, sugCallback);
		}
	};
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
			geogebra.html5.main.DrawEquationWeb.writeLatexInPlaceOfCurrentWord(
			        seMayLatex, sugg, currentWord, true);

			// not to forget making the popup disappear after success!
			sug.hideSuggestions();
		}
	};

	public NewRadioButtonTreeItem(Kernel kern) {
		super(kern);
		this.addStyleName("NewRadioButtonTreeItem");
		curWord = new StringBuilder();
		sug = new ScrollableSuggestionDisplay();
		popup = new CompletionsPopup();
		popup.addTextField(this);
		historyIndex = 0;
		history = new ArrayList<String>(50);
		historyMap = new HashMap<String, String>();
		addHistoryPopup(app.showInputTop());

		// code copied from AutoCompleteTextFieldW,
		// with some modifications!
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

				// NewRadioButtonTreeItem/MQ should not loose focus

				// but this will make the formula flicker, as there
				// may be too much execution! so only do it when
				// showSymbolButton would really get the focus,
				// i.e. do not do it for some event types, e.g.
				// at least in the following three cases:
				if (event.getTypeInt() == Event.ONMOUSEMOVE
				        || event.getTypeInt() == Event.ONMOUSEOVER
				        || event.getTypeInt() == Event.ONMOUSEOUT)
					return;

				// now we can do it
				NewRadioButtonTreeItem.this.setFocus(true);
			}
		};
		String id = DOM.createUniqueId();
		// textField.setShowSymbolElement(this.showSymbolButton.getElement());
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
				if (showSymbolButton.isDown()) {
					// when it is still down, it will be changed to up
					// when it is still up, it will be changed to down

					// showTablePopupRelativeTo(showSymbolButton);
					if (tablePopup == null
					        && NewRadioButtonTreeItem.this.showSymbolButton != null)
						tablePopup = new SymbolTablePopupW(app,
						        NewRadioButtonTreeItem.this,
						        showSymbolButton);
					if (NewRadioButtonTreeItem.this.tablePopup != null) {
						tablePopup.showRelativeTo(showSymbolButton);
					}

				} else {
					// hideTablePopup();
					if (NewRadioButtonTreeItem.this.tablePopup != null) {
						NewRadioButtonTreeItem.this.tablePopup.hide();
					}
				}
				// autoCompleteTextField should not loose focus
				NewRadioButtonTreeItem.this.setFocus(true);
			}
		});

		showSymbolButton.setFocus(false);
		// add(textField);// done in super()

		// it seems this would be part of the Tree, not of TreeItem...
		// why? web programming knowledge helps: we should add position:
		// relative! to ".GeoGebraFrame .gwt-Tree .gwt-TreeItem .elem"
		add(showSymbolButton);

		showSymbolButton.getElement().setAttribute("data-visible", "true");
		addStyleName("SymbolCanBeShown");
	}

	/**
	 * This is the interface of bringing up a popup of suggestions, from a query
	 * string "sub"... in AutoCompleteTextFieldW, this is supposed to be
	 * triggered automatically by SuggestBox, but in NewRadioButtonTreeItem we
	 * have to call this every time for the actual word in the formula (i.e.
	 * updateCurrentWord(true)), when the formula is refreshed a bit! e.g.
	 * DrawEquationWeb.editEquationMathQuillGGB.onKeyUp or something, so this
	 * will be a method to override!
	 */
	@Override
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

	@Override
	public boolean hideSuggestions() {
		if (sug.isSuggestionListShowing()) {
			sug.hideSuggestions();
		}
		return true;
	}

	/**
	 * In case the suggestion list is showing, shuffle its selected element
	 * up/down, otherwise consider up/down event for the history popup!
	 */
	public boolean shuffleSuggestions(boolean down) {
		if (sug.isSuggestionListShowing()) {
			if (down) {
				sug.accessMoveSelectionDown();
			} else {
				sug.accessMoveSelectionUp();
			}
			return false;
		} else if (down) {
			if (historyPopup != null && historyPopup.isDownPopup()) {
				// this would give the focus to the historyPopup,
				// which should catch the key events itself, but maybe it's
				// not everything all right here!
				historyPopup.showPopup();
			} else {
				String text = getNextInput();
				if (text != null) {
					setText(text);
				}
			}
		} else {
			if (historyPopup != null && !historyPopup.isDownPopup()) {
				historyPopup.showPopup();
			} else {
				String text = getPreviousInput();
				if (text != null)
					setText(text);
			}
		}
		return true;
	}

	@Override
	public boolean stopNewFormulaCreation(String newValue0, String latex) {
		if (sug.isSuggestionListShowing()) {
			sugCallback.onSuggestionSelected(sug.accessCurrentSelection());
			return false;
		}
		return super.stopNewFormulaCreation(newValue0, latex);
	}

	public boolean getAutoComplete() {
		return true;
	}

	public String getText() {
		return geogebra.html5.main.DrawEquationWeb
		        .getActualEditedValue(seMayLatex);
	}

	/**
	 * Note that this method should set the text of the MathQuillGGB-editing box
	 * in MathQuillGGB text() format, not latex()... that's why we should have a
	 * mapping from text() format formulas to latex() format formulas, and keep
	 * it in the historyMap class, which should be filled the same time when
	 * addToHistory is filled!
	 */
	public void setText(String s) {
		String slatex = historyMap.get(s);
		if (slatex == null) {
			slatex = s;
		}
		geogebra.html5.main.DrawEquationWeb.updateEditingMathQuillGGB(
		        seMayLatex, slatex);
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

	@Override
	public void setFocus(boolean b) {
		geogebra.html5.main.DrawEquationWeb.focusEquationMathQuillGGB(
		        seMayLatex, b);

		// just allow onFocus/onBlur handlers for new formula creation mode now,
		// a.k.a. this class, but later we may want to add this feature to
		// RadioButtonTreeItem, or editing mode (for existing formulas)
		if (b) {
			onFocus(null);
		} else {
			onBlur(null);
		}
	}

	public void insertString(String text) {
		geogebra.html5.main.DrawEquationWeb.writeLatexInPlaceOfCurrentWord(
		        seMayLatex, text, "", false);
	}

	public void toggleSymbolButton(boolean toggled) {
		if (showSymbolButton == null) {
			return;
		}
		showSymbolButton.setDown(toggled);
	}

	public void showPopup(boolean show) {
		if (this.showSymbolButton == null) {
			return;
		}
		Element showSymbolElement = this.showSymbolButton.getElement();
		// App.debug("AF focused" + show);
		if (showSymbolElement != null
		        && "true"
		                .equals(showSymbolElement.getAttribute("data-visible"))) {
			if (show) {
				// App.debug("AF focused2" + show);
				showSymbolElement.addClassName("shown");
			} else {
				// App.debug("AF focused2" + show);
				if (!"true".equals(showSymbolElement
				        .getAttribute("data-persist"))) {
					showSymbolElement.removeClassName("shown");
				}
			}
		}
	}

	public void onFocus(FocusEvent event) {
		if (((AlgebraViewWeb) app.getGuiManager().getAlgebraView())
		        .isNodeTableEmpty()) {
			((AlgebraDockPanelW) app.getGuiManager().getLayout()
			        .getDockManager().getPanel(App.VIEW_ALGEBRA))
			        .showStyleBarPanel(false);
		}

		Object source = this;
		if (event != null)
			source = event.getSource();

		// this is a static method, and the same which is needed here too,
		// so why duplicate the same thing in another copy?
		// this will call the showPopup method, by the way
		AutoCompleteTextFieldW.showSymbolButtonIfExists(source, true);

		app.getSelectionManager().clearSelectedGeos();

		// this.focused = true; // hasFocus is not needed, AFAIK
	}

	public void onBlur(BlurEvent event) {
		((AlgebraDockPanelW) app.getGuiManager().getLayout().getDockManager()
		        .getPanel(App.VIEW_ALGEBRA)).showStyleBarPanel(true);

		Object source = this;
		if (event != null)
			source = event.getSource();

		// this is a static method, and the same which is needed here too,
		// so why duplicate the same thing in another copy?
		// this will call the showPopup method, by the way
		AutoCompleteTextFieldW.showSymbolButtonIfExists(source, false);

		// this.focused = false; // hasFocus is not needed, AFAIK
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
			historyPopup = new HistoryPopupW(this);

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
		App.debug("setBorderVisible() implementation needed"); // TODO
		                                                       // Auto-generated
	}

	private void setBorderButton(int i, ImageData createUpDownTriangleIcon,
	        ClickHandler al) {
		App.debug("setBorderButton() implementation needed"); // TODO
		                                                      // Auto-generated
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

	@Override
	public void addToHistory(String str, String latex) {
		// exit if the new string is the same as the last entered string
		if (!history.isEmpty() && str.equals(history.get(history.size() - 1)))
			return;

		history.add(str);
		historyIndex = history.size();
		historyMap.put(str, latex);
	}

}
