package geogebra.web.gui.inputfield;

import java.util.ArrayList;
import java.util.List;

import geogebra.common.awt.Color;
import geogebra.common.awt.Font;
import geogebra.common.euclidian.DrawTextField;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.event.FocusListener;
import geogebra.common.gui.VirtualKeyboardListener;
import geogebra.common.gui.inputfield.AutoComplete;
import geogebra.common.gui.inputfield.MyTextField;
import geogebra.common.gui.inputfield.ValidateAutocompletionResult;
import geogebra.common.javax.swing.JLabel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.commands.MyException;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.MyError;
import geogebra.common.util.AutoCompleteDictionary;
import geogebra.common.util.Korean;
import geogebra.web.gui.util.GeoGebraIcon;
import geogebra.web.gui.autocompletion.CompletionsPopup;
import geogebra.web.gui.inputfield.HistoryPopup;
import geogebra.web.main.Application;
import geogebra.web.main.MyKeyCodes;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class AutoCompleteTextField extends SuggestBox implements AutoComplete, geogebra.common.gui.inputfield.AutoCompleteTextField, KeyDownHandler, KeyUpHandler, KeyPressHandler, ValueChangeHandler<String>, SelectionHandler<Suggestion>, VirtualKeyboardListener {
	
	  private Application app;
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
	  private static CompletionsPopup completionsPopup;

	  private HistoryPopup historyPopup;

	  private DrawTextField drawTextField = null;
	  
	  /**
	   * Flag to determine if text must start with "=" to activate autoComplete;
	   * used with spreadsheet cells
	   */
	  private boolean isEqualsRequired = false;
	  
	  /**
	   * Pattern to find an argument description as found in the syntax information
	   * of a command.
	   */
	  // private static Pattern syntaxArgPattern = Pattern.compile("[,\\[] *(?:<[\\(\\) \\-\\p{L}]*>|\\.\\.\\.) *(?=[,\\]])");
	  // Simplified to this as there are too many non-alphabetic character in parameter descriptions:
	  private static RegExp syntaxArgPattern = RegExp
	      .compile("[,\\[] *(?:<.*?>|\"<.*?>\"|\\.\\.\\.) *(?=[,\\]])");

	  /**
	   * Constructs a new AutoCompleteTextField that uses the dictionary of the
	   * given Application for autocomplete look up.
	   * A default model is created and the number of columns is 0.
	   * 
	   */
	  public AutoCompleteTextField(int columns, AbstractApplication app) {
	    this(columns, (Application) app, true);
	  }
	  
	  public AutoCompleteTextField(int columns, Application app,
		      boolean handleEscapeKey) {
		    this(columns, app, handleEscapeKey, app.getCommandDictionary());
		    // setDictionary(app.getAllCommandsDictionary());
	  }
	  
	  public AutoCompleteTextField(int columns, AbstractApplication app,
		      Drawable drawTextField) {
		    this(columns, app);
		    this.drawTextField = (DrawTextField) drawTextField;
	  }

	  public AutoCompleteTextField(int columns, Application app,
		      boolean handleEscapeKey, AutoCompleteDictionary dict) {
		    //AG not MathTextField and Mytextfield exists yet super(app);
		    // allow dynamic width with columns = -1
		  super(completionsPopup = new CompletionsPopup(),new TextBox(), new DefaultSuggestionDisplay());
		    if (columns > 0) {
		      setColumns(columns);
		    }

		    this.app = app;
		    setAutoComplete(true);
		    this.handleEscapeKey = handleEscapeKey;
		    curWord = new StringBuilder();

		    historyIndex = 0;
		    history = new ArrayList<String>(50);

		    completions = null;

		    //CommandCompletionListCellRenderer cellRenderer = new CommandCompletionListCellRenderer();
		    completionsPopup.addTextField(this);
		    
		    // addKeyListener(this); now in MathTextField <==AG not mathtexfield exist yet
		    addKeyDownHandler(this);
		    addKeyUpHandler(this);
		    addKeyPressHandler(this);
		    addValueChangeHandler(this);
		    addSelectionHandler(this);
		    setDictionary(dict);
		    init();
	}
	
	private void init(){
		getTextBox().addMouseUpHandler(new MouseUpHandler(){
			public void onMouseUp(MouseUpEvent event) {
				//AG I dont understand thisAutoCompleteTextField tf = ((AutoCompleteTextField)event.getSource()); 
	            //AG tf.setFocus(true);
				setFocus(true);
            }
		});
	}
	
	public DrawTextField getDrawTextField() {
	    return drawTextField;
	}

	public ArrayList<String> getHistory() {
	    return history;
	}
	
	 /**
	   * Add a history popup list and an embedded popup button.
	   * See AlgebraInputBar
	   */
	  public void addHistoryPopup(boolean isDownPopup) {

	    if (historyPopup == null)
	      historyPopup = new HistoryPopup(this);

	    historyPopup.setDownPopup(isDownPopup);

	    ClickHandler al = new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				//AGString cmd = event.;
		        //AGif (cmd.equals(1 + BorderButton.cmdSuffix)) {
		        // TODO: should up/down orientation be tied to InputBar?
		        // show popup
		        historyPopup.showPopup();
				
			}
		};
	    setBorderButton(1, GeoGebraIcon.createUpDownTriangleIcon(false, true), al);
	    this.setBorderButtonVisible(1, false);
	  }
	
	private void setBorderButtonVisible(int i, boolean b) {
		   AbstractApplication.debug("implementation needed"); //TODO Auto-generated
    }

	private void setBorderButton(int i, Canvas createUpDownTriangleIcon,
            ClickHandler al) {
		   AbstractApplication.debug("implementation needed"); //TODO Auto-generated
    }

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
    }

	
//	public String getText() {
//	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
//	    return null;
//    }
//
//	public void setText(String s) {
//	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
//	    
//    }

	public void showPopupSymbolButton(boolean b) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }
	
	/**
	   * Sets whether the component is currently performing autocomplete lookups as
	   * keystrokes are performed.
	   * 
	   * @param val
	   *          True or false.
	   */
	  public void setAutoComplete(boolean val) {
	    autoComplete = val && app.isAutoCompletePossible();

	    if (autoComplete)
	      app.initTranslatedCommands();

	  }
	  
	  private List<String> resetCompletions() {
		    String text = getText();
		    updateCurrentWord(false);
		    completions = null;
		    if (isEqualsRequired && !text.startsWith("="))
		      return null;

		    boolean korean = false; //AG app.getLocale().getLanguage().equals("ko");

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

		    completions = getSyntaxes(completions);
		    return completions;
		  }

		  /*
		   * Take a list of commands and return all possible syntaxes
		   * for these commands
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
		        syntaxString = app.getCommandSyntaxCAS(cmdInt);
		      } else {
		        syntaxString = app.getCommandSyntax(cmdInt);
		      }
		      if (syntaxString.endsWith(isCASInput ? Application.syntaxCAS
		          : Application.syntaxStr)) {

		        // command not found, check for macros
		        Macro macro = isCASInput ? null : app.getKernel().getMacro(cmd);
		        if (macro != null) {
		          syntaxes.add(macro.toString());
		        } else {
		          // syntaxes.add(cmdInt + "[]");
		          AbstractApplication.debug("Can't find syntax for: " + cmd);
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

	public void enableColoring(boolean b) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setOpaque(boolean b) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setFont(Font font) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setForeground(Color color) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setBackground(Color color) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setFocusable(boolean b) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setEditable(boolean b) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void requestFocus() {
	    AbstractApplication.debug("implementation needed - just finishing"); //TODO Auto-generated
		setFocus(true);
    }

	public void setLabel(JLabel label) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setVisible(boolean b) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setColumns(int length) {
	  //AG getTextBox().setWidth(length+"px");
		AbstractApplication.debug("AutoCompleteTextField.setColumns called");
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

	public void addFocusListener(FocusListener listener) {
		getTextBox().addFocusHandler((geogebra.web.euclidian.event.FocusListener) listener);
		getTextBox().addBlurHandler((geogebra.web.euclidian.event.FocusListener) listener);	    
    }

	public void addKeyListener(geogebra.common.euclidian.event.KeyListener listener) {
		super.addKeyPressHandler((geogebra.web.euclidian.event.KeyListener) listener);
	}
	
	public void wrapSetText(String s) {
		AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public int getCaretPosition() {
		return getTextBox().getCursorPos();
    }

	public void setCaretPosition(int caretPos) {
		getTextBox().setCursorPos(caretPos);
    }

	public void setDictionary(AutoCompleteDictionary dict) {
	    this.dict = dict;
    }

	public AutoCompleteDictionary getDictionary() {
	    return dict;
    }
	
	// returns the word at position pos in text
	  public static String getWordAtPos(String text, int pos) {
	    // search to the left
	    int wordStart = pos - 1;
	    while (wordStart >= 0 && isLetterOrDigit(text.charAt(wordStart)))
	      --wordStart;
	    wordStart++;

	    // search to the right
	    int wordEnd = pos;
	    int length = text.length();
	    while (wordEnd < length && isLetterOrDigit(text.charAt(wordEnd)))
	      ++wordEnd;

	    if (wordStart >= 0 && wordEnd <= length) {
	      return text.substring(wordStart, wordEnd);
	    } else {
	      return null;
	    }
	  }
	
	 private static boolean isLetterOrDigit(char character) {
		    switch (character) {
		      case '_': // allow underscore as a valid letter in an autocompletion word
		        return true;

		      default:
		        return Character.isLetterOrDigit(character);
		    }
		  }
	 
	 /**
	   * shows dialog with syntax info
	   * 
	   * @param cmd
	   *          is the internal command name
	   */
	  private void showCommandHelp(String cmd) {
	    // show help for current command (current word)
	    String help = app.getCommandSyntax(cmd);

	    // show help if available
	    if (help != null) {
	      app.showError(new MyError(app, app.getPlain("Syntax") + ":\n" + help, cmd));
	    } else {
	      app.getGuiManager().openCommandHelp(null);
	    }
	  }
	  
	  private void clearSelection() {
		    int start = getText().indexOf(getTextBox().getSelectedText());
		    int end = start + getTextBox().getSelectionLength();
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
	   * Updates curWord to word at current caret position.
	   * curWordStart, curWordEnd are set to this word's start and end position
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
	        isLetterOrDigit(text.charAt(curWordStart))) {
	      --curWordStart;
	    }
	    curWordStart++;
	    // search to the right
	    int curWordEnd = caretPos;
	    int length = text.length();
	    while (curWordEnd < length && isLetterOrDigit(text.charAt(curWordEnd)))
	      ++curWordEnd;

	    curWord.setLength(0);
	    curWord.append(text.substring(curWordStart, curWordEnd));

	    // remove '[' at end
	    if (curWord.toString().endsWith("[")) {
	      curWord.setLength(curWord.length() - 1);
	    }
	  }

	public void showError(Exception e) {
		 if (e instanceof MyException) {
		      updateCurrentWord(true);
		      int err = ((MyException) e).getErrorType();
		      if (err == MyException.INVALID_INPUT) {
		        String command = app.getReverseCommand(getCurrentWord());
		        if (command != null) {

		          app.showError(new MyError(app, app.getError("InvalidInput") + "\n\n"
		              + app.getPlain("Syntax") + ":\n" + app.getCommandSyntax(command),
		              getCurrentWord()));
		          return;
		        }
		      }
		    }
		    // can't work out anything better, just show "Invalid Input"
		    app.showError(e.getLocalizedMessage());
    }
	
	 /*
	   * just show syntax error (already correctly formulated by CommandProcessor.argErr())
	   */
	  public void showError(MyError e) {
	    app.showError(e);
	  }

	public boolean getAutoComplete() {
		 return autoComplete && app.isAutoCompletePossible();
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
	    else
	      return history.get(historyIndex);
	  }
	  
	  public void mergeKoreanDoubles() {
		    // avoid shift on Korean keyboards
		    /*AG dont do that yet if (app.getLocale().getLanguage().equals("ko")) {
		      String text = getText();
		      int caretPos = getCaretPosition();
		      String mergeText = Korean.mergeDoubleCharacters(text);
		      int decrease = text.length() - mergeText.length();
		      if (decrease > 0) {
		        setText(mergeText);
		        setCaretPosition(caretPos - decrease);
		      }
		    }*/
		  AbstractApplication.debug("KoreanDoubles may be needed in AutocompleteTextField");
		  }
	  
	  private boolean moveToNextArgument(boolean find) {
		  	String text = getText();
		    int caretPos = getCaretPosition();

		    // make sure it works if caret is just after [
		    // if (caretPos > 0 && text.charAt(caretPos - 1) == '[') caretPos--;

		    //AGMatcher argMatcher = syntaxArgPattern.matcher(text);
		    MatchResult argMatcher = syntaxArgPattern.exec(text);
		    //boolean hasNextArgument = argMatcher.find(caretPos);
		    boolean hasNextArgument = syntaxArgPattern.test(text);
		    if (find && !hasNextArgument) {
		      //hasNextArgument = argMatcher.find();
		    	hasNextArgument = syntaxArgPattern.test(text);
		    }
		    //if (hasNextArgument && (find || argMatcher.start() == caretPos)) {
		    if (hasNextArgument && (find || argMatcher.getIndex() == caretPos)) {
		      //setCaretPosition(argMatcher.end();
		      //moveCaretPosition(argMatcher.start() + 1);
		      for (int i = 0; i < argMatcher.getGroupCount(); i++) {
		    	  String groupStr = argMatcher.getGroup(i);
		    	  getTextBox().setSelectionRange(text.indexOf(groupStr)+1, groupStr.length());
		      }
		      return true;
		    } else {
		      return false;
		    }
		  }
	

	  // ----------------------------------------------------------------------------
	  // Protected methods ..why? :-)
	  // ----------------------------------------------------------------------------

	boolean ctrlC = false;


	public void onKeyPress(KeyPressEvent e) {
		if (!keyPressed) {
			keyPressed = true;
			 // only handle parentheses
		    char ch = e.getCharCode();
		    int caretPos = getCaretPosition();
	
		    String text = getText();
	
			// checking for isAltDown() because Alt+, prints another character on the PC
			// TODO make this more robust - perhaps it could go in a document change listener
		    if (ch == ',' && !e.isAltKeyDown()) {
		      if (caretPos < text.length() && text.charAt(caretPos) == ',') {
		        // User typed ',' just in ahead of an existing ',':
		        // We may be in the position of filling in the next argument of an autocompleted command
		        // Look for a pattern of the form ", < Argument Description > ," or ", < Argument Description > ]"
		        // If found, select the argument description so that it can easily be typed over with the value
		        // of the argument.
		        if (moveToNextArgument(false)) {
		          e.stopPropagation();
		        }
		        return;
		      }
		    }
	
		    if (!(ch == '(' || ch == '{' || ch == '[' || ch == '}' || ch == ')' || ch == ']')) {
		      //super.keyTyped(e);
		      AbstractApplication.debug("super.keyTyped needed in AutocompleteTextField");
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
		        || MyTextField.isCloseBracketOrWhitespace(text.charAt(caretPos))) {
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
    }
	
	/* We need a small hack, as keypress fired during key pressed more times.
	 * this makes bracket completion hayware :-)
	 */
	private boolean keyPressed = false;

	public void onKeyDown(KeyDownEvent event) {
	  
    }

	public void onKeyUp(KeyUpEvent e) {
		  int keyCode = e.getNativeKeyCode();

		    // we don't want to trap AltGr
		    // as it is used eg for entering {[}] is some locales
		    // NB e.isAltGraphDown() doesn't work
		    if (e.isAltKeyDown() && e.isControlKeyDown())
		      return;

		    // swallow eg ctrl-a ctrl-b ctrl-p on Mac
		   /*AG if (Application.MAC_OS && e.isControlKeyDown()) {
		      e.consume();
		    }*/

		    ctrlC = false;

		    switch (keyCode) {

		      case MyKeyCodes.KEY_Z:
		      case MyKeyCodes.KEY_Y:
		        if (e.isControlKeyDown()) {
		          app.getGlobalKeyDispatcher().handleGeneralKeys(e);
		          e.stopPropagation();
		        }
		        break;
		      case MyKeyCodes.KEY_C:
		        if (e.isControlKeyDown()) // workaround for MAC_OS
		        {
		          ctrlC = true;
		        }
		        break;

		      case MyKeyCodes.KEY_0:
		      case MyKeyCodes.KEY_1:
		      case MyKeyCodes.KEY_2:
		      case MyKeyCodes.KEY_3:
		      case MyKeyCodes.KEY_4:
		      case MyKeyCodes.KEY_5:
		      case MyKeyCodes.KEY_6:
		      case MyKeyCodes.KEY_7:
		      case MyKeyCodes.KEY_8:
		      case MyKeyCodes.KEY_9:
		        if (e.isControlKeyDown() && e.isShiftKeyDown())
		          app.getGlobalKeyDispatcher().handleGeneralKeys(e);
		        break;

		      // process input

		      case MyKeyCodes.KEY_ESCAPE:
		        if (!handleEscapeKey) {
		          break;
		        }

		        /*AG do this if we will have windows Component comp = SwingUtilities.getRoot(this);
		        if (comp instanceof JDialog) {
		          ((JDialog) comp).setVisible(false);
		          return;
		        }*/
		        AbstractApplication.debug("Implementation needed if some kind of Dialog open");
		        setText(null);
		        break;

		      case MyKeyCodes.KEY_LEFT_PARENTHESIS:
		        break;

		      case MyKeyCodes.KEY_UP:
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

		      case MyKeyCodes.KEY_DOWN:
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

		      case MyKeyCodes.KEY_F9:
		        // needed for applets
		        if (app.isApplet())
		          app.getGlobalKeyDispatcher().handleGeneralKeys(e);
		        break;

		      case MyKeyCodes.KEY_RIGHT:
		        if (moveToNextArgument(false)) {
		          e.stopPropagation();
		        }
		        break;

		      case MyKeyCodes.KEY_TAB:
		        if (moveToNextArgument(true)) {
		          e.stopPropagation();
		        }
		        break;

		      case MyKeyCodes.KEY_F1:

		        if (autoComplete) {
		          if (getText().equals("")) {

		            Object[] options = { app.getPlain("OK"),
		                app.getPlain("ShowOnlineHelp") };
		           /*AG not yet... int n = JOptionPane.showOptionDialog(app.getMainComponent(),
		                app.getPlain("InputFieldHelp"), app.getPlain("ApplicationName")
		                    + " - " + app.getMenu("Help"), JOptionPane.YES_NO_OPTION,
		                JOptionPane.QUESTION_MESSAGE, null, // do not use a custom Icon
		                options, // the titles of buttons
		                options[0]); // default button title
                   
		            if (n == 1)
		              app.getGuiManager().openHelp(AbstractApplication.WIKI_MANUAL);
                   */
		          } else {
		            int pos = getCaretPosition();
		            while (pos > 0 && getText().charAt(pos - 1) == '[') {
		              pos--;
		            }
		            String word = getWordAtPos(getText(), pos);
		            String lowerCurWord = word.toLowerCase();
		            String closest = dict.lookup(lowerCurWord);

		            if (closest != null)// && lowerCurWord.equals(closest.toLowerCase()))
		              showCommandHelp(app.getInternalCommand(closest));
		            else
		              app.getGuiManager().openHelp(AbstractApplication.WIKI_MANUAL);

		          }
		        } else
		          app.getGuiManager().openHelp(AbstractApplication.WIKI_MANUAL);

		        e.stopPropagation();
		        break;
		      default:
		    	  /*Try handling here that is originaly in keyup
		    	   * 
		    	   */
		    	  boolean modifierKeyPressed = e.isControlKeyDown() || e
		    		        .isAltKeyDown();

		    		    // we don't want to act when AltGr is down
		    		    // as it is used eg for entering {[}] is some locales
		    		    // NB e.isAltGraphDown() doesn't work
		    		    if (e.isAltKeyDown() && e.isControlKeyDown())
		    		      modifierKeyPressed = false;

		    		    char charPressed = Character.valueOf((char) e.getNativeKeyCode());

		    		    if ((isLetterOrDigit(charPressed) || modifierKeyPressed)
		    		        && !(ctrlC)
		    		        && !(e.getNativeKeyCode() == MyKeyCodes.KEY_A)) {
		    		      clearSelection();
		    		    }

		    		    // handle alt-p etc
		    		    //super.keyReleased(e);

		    		    mergeKoreanDoubles();

		    		    if (getAutoComplete()) {
		    		      updateCurrentWord(false);
		    		      startAutoCompletion();
		    		    }
		    	  
		    }
	    keyPressed = false;
    }

	public void addToHistory(String input) {
	  AbstractApplication.debug("Implementation needed AutocompleteTextfield.addToHistory");
    }

	public boolean isSuggesting() {
		DefaultSuggestionDisplay display = (DefaultSuggestionDisplay) getSuggestionDisplay();
		return display.isSuggestionListShowing();
    }
	
	private boolean isSuggestionJustHappened = false;
	private boolean isSuggestionClickJustHappened = false;
	
	/**
	 * @return that suggestion is just happened (click or enter,
	 *  so we don't need to run the enter code again
	 */
	public boolean isSuggestionJustHappened() {
		return isSuggestionJustHappened && !isSuggestionClickJustHappened;
	}
	
	public void setIsSuggestionJustHappened(boolean b) {
		isSuggestionJustHappened = b;
		isSuggestionClickJustHappened = b;
	}
	
	/* Hopefully happens only on click */
	public void onValueChange(ValueChangeEvent<String> event) {
	  isSuggestionClickJustHappened = true;
	  getTextBox().getElement().focus();
    }

	public void onSelection(SelectionEvent<Suggestion> event) {
	   isSuggestionJustHappened = true;
	   int index = completions.indexOf(event.getSelectedItem().getReplacementString());
	   validateAutoCompletion(index, getCompletions());
    }
	
	/**
	 * Inserts a string into the text at the current caret position
	 */
	public void insertString(String text) {
		int start = getSelectionStart();
		int end = getSelectionEnd();

		// clear selection if there is one
		if (start != end) {
			String oldText = getText();
			StringBuilder sb = new StringBuilder();
			sb.append(oldText.substring(0, start));
			sb.append(oldText.substring(end));
			setText(sb.toString());
			setCaretPosition(start);
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

		// make sure AutoComplete works
		if (this instanceof AutoCompleteTextField) {
			AutoCompleteTextField tf = (AutoCompleteTextField) this;
			tf.updateCurrentWord(false);
			tf.startAutoCompletion();
		}

		
		setCaretPosition(newPos);
		

		// TODO: tried to keep the Mac OS from auto-selecting the field by
		// resetting the
		// caret, but not working yet
		// setCaret(new DefaultCaret());
		// setCaretPosition(newPos);
	}

	private int getSelectionEnd() {
	   return getSelectionStart() + getTextBox().getSelectionLength();
    }

	private int getSelectionStart() {
	   return getText().indexOf(getTextBox().getSelectedText());
    }

	  /**
	   * Ticket #1167 Auto-completes input; It will keep already entered parameters<br>
	   * and merge them in order. If chosen command has less it will output:<br>
	   * command_name[&lt;original parameter list&gt;]<br>
	   * <br>
	   * e.g.:<br>
	   * Input: der <br>
	   * Choose: Derivative[ &lt;Function&gt; ]<br>
	   * Output: Derivative[ &lt;Function&gt; ]<br>
	   * <br>
	   * Input: derivative[x^2]<br>
	   * Choose: Derivative[ &lt;Function&gt; ]<br>
	   * Output: Derivative[x^2]<br>
	   * <br>
	   * Input: derivative[x^2]<br>
	   * Choose: Derivative[ &lt;Function>, &lt;Number&gt; ]<br>
	   * Output: Derivative[x^2, &lt;Number&gt; ]<br>
	   * <br>
	   * Input: derivative[x^2, &lt;Number&gt; ]<br>
	   * Choose: Derivative[ &lt;Function&gt;, &lt;Number&gt; ]<br>
	   * Output: Derivative[x^2, &lt;Number&gt; ]<br>
	   * <br>
	   * Input: inde[x, &lt;Number&gt; ]<br>
	   * Choose: IndexOf[ &lt;Object&gt;, &lt;List&gt;, &lt;StartIndex&gt; ]<br>
	   * Output: IndexOf[x, &lt;Number&gt; , &lt;StartIndex&gt; ]<br>
	   * <br>
	   * 
	   * @param index
	   *          index of the chosen command in the completions list
	   * @return false if completions list is null or index < 0 or index >
	   *         completions.size()
	   * @author Lucas Binter
	   */
	public boolean validateAutoCompletion(int index, List<String> completions) {
		ValidateAutocompletionResult ret = geogebra.common.gui.inputfield.MyTextField.commonValidateAutocompletion(index, completions,getText(),curWordStart);
		
		if (!ret.returnval) {
			return false;
		}
		
		setText(ret.sb);
		setCaretPosition(ret.carPos);
		
		moveToNextArgument(false);
		return true;
	}
}
