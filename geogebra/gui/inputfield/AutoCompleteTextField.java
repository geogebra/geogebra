package geogebra.gui.inputfield;
import geogebra.gui.autocompletion.CommandCompletionListCellRenderer;
import geogebra.gui.autocompletion.CompletionsPopup;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.Korean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Macro;
import geogebra.kernel.commands.MyException;
import geogebra.main.Application;
import geogebra.main.GeoElementSelectionListener;
import geogebra.main.MyError;
import geogebra.util.AutoCompleteDictionary;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class AutoCompleteTextField extends MathTextField implements 
AutoComplete, KeyListener, GeoElementSelectionListener {

	private static final long serialVersionUID = 1L;

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
	private CompletionsPopup completionsPopup;

	private HistoryPopup historyPopup;


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
	private static Pattern syntaxArgPattern = Pattern.compile("[,\\[] *(?:<.*?>|\"<.*?>\"|\\.\\.\\.) *(?=[,\\]])");
	
	/**
	 * Constructs a new AutoCompleteTextField that uses the dictionary of the
	 * given Application for autocomplete look up.
	 * A default model is created and the number of columns is 0.
	 *
	 */
	public AutoCompleteTextField(int columns, Application app) {
		this(columns, app, true); 
	}    

	public AutoCompleteTextField(int columns, Application app, boolean handleEscapeKey, AutoCompleteDictionary dict) {
		super(app);
		// allow dynamic width with columns = -1
		if(columns > 0)
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
		//addKeyListener(this); now in MathTextField
		setDictionary(dict);

	}   

	public AutoCompleteTextField(int columns, Application app, boolean handleEscapeKey){
		this(columns, app, handleEscapeKey, app.getCommandDictionary());
		// setDictionary(app.getAllCommandsDictionary());
	}


	public ArrayList<String> getHistory() {
		return history;
	}

	/**
	 * Add a history popup list and an embedded popup button.
	 * See AlgebraInputBar
	 */
	public void addHistoryPopup(boolean isDownPopup){

		if(historyPopup == null)
			historyPopup = new HistoryPopup(this);
		
		historyPopup.setDownPopup(isDownPopup);

		ActionListener al = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				if(cmd.equals(1 + BorderButton.cmdSuffix)){

					// TODO: should up/down orientation be tied to InputBar?
					// show popup 
					historyPopup.showPopup();
				}			
			}	
		};
		setBorderButton(1, GeoGebraIcon.createUpDownTriangleIcon(false, true), al);
		this.setBorderButtonVisible(1, false);

	}


	public void showPopupSymbolButton(boolean showPopupSymbolButton){
		((MyTextField)this).setShowSymbolTableIcon(showPopupSymbolButton);
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
	 * @param dict The dictionary that will be used for the autocomplete lookups.
	 */
	public void setDictionary(AutoCompleteDictionary dict) {
		this.dict = dict;   
	}

	/**
	 * Gets the dictionary currently used for lookups.
	 *
	 * @return dict The dictionary that will be used for the autocomplete lookups.
	 */
	public AutoCompleteDictionary getDictionary() {
		return dict;
	}

	/**
	 * Sets whether the component is currently performing autocomplete lookups as
	 * keystrokes are performed.
	 *
	 * @param val True or false.
	 */
	public void setAutoComplete(boolean val) {
		autoComplete = val && app.isAutoCompletePossible();

		if (autoComplete) app.initTranslatedCommands();

	}

	public List<String> getCompletions() {
		return completions;
	}

	/**
	 * Gets whether the component is currently performing autocomplete lookups as
	 * keystrokes are performed.
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
			replaceSelection(" " + geo.getLabel() + " ");
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






	//----------------------------------------------------------------------------
	// Protected methods
	//----------------------------------------------------------------------------

	boolean ctrlC = false;

	private boolean isVisibleHistoryButton;




	public void keyPressed(KeyEvent e) {        
		int keyCode = e.getKeyCode(); 

		// we don't want to trap AltGr
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown())
			return;

		// swallow eg ctrl-a ctrl-b ctrl-p on Mac
		if (Application.MAC_OS && e.isControlDown())
			e.consume();



		ctrlC = false;

		switch (keyCode) {

		case KeyEvent.VK_Z:
		case KeyEvent.VK_Y:
			if (Application.isControlDown(e)) {
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
				e.consume();
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
			if (Application.isControlDown(e) && e.isShiftDown())
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
			break;

			// process input
		case KeyEvent.VK_C:
			if (Application.isControlDown(e)) //workaround for MAC_OS
			{
				ctrlC = true;
			}

			break;

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

		case KeyEvent.VK_LEFT_PARENTHESIS:
			break;


		case KeyEvent.VK_UP:
			if (!handleEscapeKey) {
				break;
			}
			if(historyPopup == null){
				String text = getPreviousInput();
				if (text != null) setText(text);
			}
			else if(!historyPopup.isDownPopup()){
				historyPopup.showPopup();
			}
			break;

		case KeyEvent.VK_DOWN:
			if (!handleEscapeKey) {
				break;
			}
			if(historyPopup != null && historyPopup.isDownPopup())
				historyPopup.showPopup();
			else
				setText(getNextInput());
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
			if (moveToNextArgument(true)) {
				e.consume();
			}
			break;

		case KeyEvent.VK_F1:

			if (autoComplete) {
				if (getText().equals("")) {

					Object[] options = {app.getPlain("OK"), app.getPlain("ShowOnlineHelp")};
					int n = JOptionPane.showOptionDialog(app.getMainComponent(),
							app.getPlain("InputFieldHelp"),
							app.getPlain("ApplicationName") + " - " + app.getMenu("Help"),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,     //do not use a custom Icon
							options,  //the titles of buttons
							options[0]); //default button title

					if (n == 1) app.getGuiManager().openHelp(Application.WIKI_MANUAL);

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
						app.getGuiManager().openHelp(Application.WIKI_MANUAL);

				}
			} else app.getGuiManager().openHelp(Application.WIKI_MANUAL);

			e.consume();
			break;
		default:                                
		}                                   
	}


	public void keyReleased(KeyEvent e) {

		//Application.debug(e+"");

		/* test code to generate unicode strings for Virtual Keyboard
		String text = getText();
		String outStr = "";
		for (int i = 0 ; i < text.length() ; i++) {
			int ch = text.charAt(i);
			if (ch < 128) outStr += text.charAt(i);
			else {
				String unicode = Integer.toHexString(ch);
				if (unicode.length() < 4) unicode = "\\u0"+unicode;
				else unicode = "\\u"+unicode;
				outStr += unicode;
			}
		}
		Application.debug(outStr);
		//*/

		// ctrl pressed on Mac
		// or alt on Windows
		boolean modifierKeyPressed = Application.MAC_OS ? e.isControlDown() : e.isAltDown();

		// we don't want to act when AltGr is down
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown())
			modifierKeyPressed = false;

		char charPressed = e.getKeyChar();  

		if ( (!isLetterOrDigit(charPressed) && !modifierKeyPressed) || 
				(ctrlC && Application.MAC_OS) // don't want selection cleared
		) return;        

		clearSelection();

		// handle alt-p etc
		super.keyReleased(e);

		mergeKoreanDoubles();

		if (getAutoComplete()) {
			updateCurrentWord(false);
			startAutoCompletion();
		}

		/*
        if (charCodePressed == KeyEvent.VK_BACK_SPACE &&
          isTextSelected && input.length() > 0) {
            setText(input.substring(0, input.length()));
        }*/	
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
		//    clear selection if there is one
		if (start != end) {
			int pos = getCaretPosition();
			String oldText = getText();
			StringBuilder sb = new StringBuilder();
			sb.append(oldText.substring(0, start));
			sb.append(oldText.substring(end));            
			setText(sb.toString());
			if (pos < sb.length()) setCaretPosition(pos);
		}
	}

	/**
	 * Automatically closes parentheses (, {, [ when next sign
	 * is a space or end of input text.
	 * and ignores ] }, ) if the brackets already match (simple check)
	 */
	public void keyTyped(KeyEvent e) {

		// only handle parentheses
		char ch = e.getKeyChar();
		int caretPos = getCaretPosition();

		String text = getText();

		if (ch == ',') {
			if (caretPos < text.length() && text.charAt(caretPos) == ',') {
				// User typed ',' just in ahead of an existing ',':
				// We may be in the position of filling in the next argument of an autocompleted command
				// Look for a pattern of the form ", < Argument Description > ," or ", < Argument Description > ]"
				// If found, select the argument description so that it can easily be typed over with the value
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
			if (text.length() > caretPos && text.charAt(caretPos)==ch) {
				int count = 0;
				for (int i = 0 ; i < text.length() ; i++) {
					char c = text.charAt(i);
					if (c == '{') count++;
					else if (c == '}') count--;
					else if (c == '(') count+=1E3;
					else if (c == ')') count-=1E3;
					else if (c == '[') count+=1E6;
					else if (c == ']') count-=1E6;
				}

				if (count == 0) { 
					// if brackets match, just move the cursor forwards one
					e.consume();
					caretPos++;
				} 
			}

		}

		// auto-close parentheses
		if (caretPos == text.length() || isCloseBracketOrWhitespace(text.charAt(caretPos))) {		
			switch (ch){
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


	private boolean isCloseBracketOrWhitespace(char c) {
		return Character.isWhitespace(c) || c == ')' || c == ']' || c == '}';
	}

	/**
	 * Updates curWord to word at current caret position.
	 * curWordStart, curWordEnd are set to this word's start and end position
	 */
	public void updateCurrentWord(boolean searchRight) {                    
		String text = getText();  
		if (text == null) return;
		int caretPos = getCaretPosition();   
		
		if (searchRight) {
			// search to right first to see if we are inside [ ]
			boolean insideBrackets = false;
			curWordStart = caretPos;
			
			while (curWordStart < text.length()) {
				char c = text.charAt(curWordStart);
				if (c == '[') break;
				if (c == ']') insideBrackets = true;
				curWordStart++;
			}
			
			// found [, so go back until we get a ]
			if (insideBrackets) {
				while (caretPos > 0 && text.charAt(caretPos) != '[') caretPos--;
			}
		}
		
		
		
		
		// search to the left
		curWordStart = caretPos - 1;
		while (  curWordStart >= 0 &&
				// isLetterOrDigitOrOpenBracket so that F1 works
				isLetterOrDigit( text.charAt(curWordStart))) {
			--curWordStart;     
		}
		curWordStart++;
		// search to the right
		int curWordEnd = caretPos;
		int length = text.length();
		while ( curWordEnd <  length &&
				isLetterOrDigit( text.charAt(curWordEnd) )) ++curWordEnd;        

		curWord.setLength(0);
		curWord.append(text.substring(curWordStart, curWordEnd));

		// remove '[' at end
		if (curWord.toString().endsWith("["))
			curWord.setLength(curWord.length() - 1);
	}

	// returns the word at position pos in text
	public static String getWordAtPos(String text, int pos) {
		// search to the left
		int wordStart = pos - 1;
		while (  wordStart >= 0 &&
				isLetterOrDigit( text.charAt(wordStart)))   --wordStart;
		wordStart++;

		// search to the right
		int wordEnd= pos;
		int length = text.length();
		while (   wordEnd < length &&
				isLetterOrDigit( text.charAt(wordEnd) ))    ++wordEnd;

		if (wordStart >= 0 && wordEnd <= length)
			return text.substring(wordStart, wordEnd);
		else 
			return null;
	}

	private static boolean isLetterOrDigit(char character) {
		switch (character) {
		case '_':  // allow underscore as a valid letter in an autocompletion word
			return true;

		default:
			return Character.isLetterOrDigit(character);
		}
	}

	private static boolean isLetterOrDigitOrOpenBracket(char character) {
		switch (character) {
		case '[':  
		case '_':  // allow underscore as a valid letter in an autocompletion word
			return true;

		default:
			return Character.isLetterOrDigit(character);
		}
	}



	//static String lastTyped = null;

	/* -----------------------------------------
	 * Autocompletion 
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
		} else {
			return false;
		}
	}

	private List<String> resetCompletions() {
		String text = getText();
		updateCurrentWord(false);
		completions = null;
		if(isEqualsRequired && !text.startsWith("="))
			return null;

		boolean korean = app.getLocale().getLanguage().equals("ko");

		//    start autocompletion only for words with at least two characters                 
		if (korean) {
			if (Korean.flattenKorean(curWord.toString()).length() < 2) {
				completions = null; 
				return null;                     				
			}
		} else {
			if (curWord.length() < 2) { 
				completions = null; 
				return null;                     
			} 
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
		for (String cmd: commands) {

			String cmdInt = app.getInternalCommand(cmd);

			String syntaxString;
			if (isCASInput) {
				syntaxString = app.getCommandSyntaxCAS(cmdInt);
			} else {
				syntaxString = app.getCommandSyntax(cmdInt);
			}
			if (syntaxString.endsWith(isCASInput ? app.syntaxCAS : app.syntaxStr)) {
				
				// command not found, check for macros
				Macro macro = isCASInput ? null : app.getKernel().getMacro(cmd);
				if (macro != null) {
					syntaxes.add(macro.toString());
				} else {
					//syntaxes.add(cmdInt + "[]");
					Application.debug("Can't find syntax for: "+cmd);
				}

				continue;
			}
			for (String syntax: syntaxString.split("\\n")) {
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

	public boolean validateAutoCompletion(int index) {
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
		
		setCaretPosition(curWordStart + bracketIndex);
		moveToNextArgument(false);
		return true;
	}

	/**
	 * Adds string to input textfield's history
	 * @param str
	 */          
	public void addToHistory(String str) {
		
		// exit if the new string is the same as the last entered string
		if(!history.isEmpty() && str.equals(history.get(history.size()-1))) 
			return;
		
		history.add(str);
		historyIndex = history.size();
		if(historyPopup != null && !isBorderButtonVisible(1))
			setBorderButtonVisible(1, true);
	}

	/**
	 * @return previous input from input textfield's history
	 */       
	private String getPreviousInput() {
		if (history.size() == 0) return null;
		if (historyIndex > 0) --historyIndex;
		return history.get(historyIndex);
	}

	/**
	 * @return next input from input textfield's history
	 */
	private String getNextInput() {       
		if (historyIndex < history.size()) ++historyIndex;
		if (historyIndex == history.size()) 
			return null;          
		else 
			return history.get(historyIndex);
	}

	/**
	 * shows dialog with syntax info
	 * cmd is the internal command name
	 */
	private void showCommandHelp(String cmd) {   
		// show help for current command (current word)
		String help = app.getCommandSyntax(cmd);

		// show help if available
		if (help != null) {
			app.showError(new MyError(app, app.getPlain("Syntax")+":\n"+help,cmd));					
		} else {
			app.getGuiManager().openCommandHelp(null);
		}
	}

	/**
	 * @param command command name in local language
	 * @return syntax description of command as html text or null
	 */
	private String getCmdSyntax(String command) {
		if (command == null || command.length() == 0) return null;

		// try macro first
		Macro macro = app.getKernel().getMacro(command);
		if (macro != null) {
			return macro.toString();
		}

		// translate command to internal name and get syntax description
		// note: the translation ignores the case of command
		String internalCmd = app.translateCommand(command);
		//String key = internalCmd + "Syntax";
		//String syntax = app.getCommand(key);
		String syntax;
		if (isCASInput) {
			syntax = app.getCommandSyntaxCAS(internalCmd);
		} else {
			syntax = app.getCommandSyntax(internalCmd);
		}

		// check if we really found syntax information
		//if (key.equals(syntax)) return null;
		if (syntax.indexOf(app.syntaxStr) == -1) return null;

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

	/*
	 * attempt to give Syntax help for when eg "Radius[ <Conic> ]" is typed
	 * see CommandProcessor.argErr() for similar error
	 */
	public void showError(Exception e) {
		if (e instanceof MyException) {
			updateCurrentWord(true);
			int err = ((MyException) e).getErrorType();
			if (err == MyException.INVALID_INPUT) {
				String command = app.getReverseCommand(getCurrentWord());
				if (command != null) {

					app.showError(new MyError(app, app.getError("InvalidInput")+"\n\n"+app.getPlain("Syntax")+":\n"+app.getCommandSyntax(command),getCurrentWord()));					
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


}
