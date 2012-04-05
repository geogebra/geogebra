package geogebra.web.gui.inputfield;

import java.util.ArrayList;
import java.util.List;

import geogebra.common.awt.Color;
import geogebra.common.awt.Font;
import geogebra.common.euclidian.DrawTextField;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.event.FocusListener;
import geogebra.common.javax.swing.JLabel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.AutoCompleteDictionary;
import geogebra.web.gui.inputfield.BorderButton;
import geogebra.web.gui.util.GeoGebraIcon;
import geogebra.web.gui.autocompletion.CommandCompletionListCellRenderer;
import geogebra.web.gui.autocompletion.CompletionsPopup;
import geogebra.web.gui.inputfield.HistoryPopup;
import geogebra.web.main.Application;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;

public class AutoCompleteTextField extends TextBox implements geogebra.common.gui.inputfield.AutoCompleteTextField{
	
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
		    init();
	}
	
	private void init(){
		addMouseUpHandler(new MouseUpHandler(){
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
	   setVisibleLength(length);
    }

	public void addFocusListener(FocusListener listener) {
		super.addFocusHandler((geogebra.web.euclidian.event.FocusListener) listener);
		super.addBlurHandler((geogebra.web.euclidian.event.FocusListener) listener);	    
    }

	public void addKeyListener(geogebra.common.euclidian.event.KeyListener listener) {
		super.addKeyPressHandler((geogebra.web.euclidian.event.KeyListener) listener);
	}
	
	public void wrapSetText(String s) {
		AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public int getCaretPosition() {
		AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    return 0;
    }

	public void setCaretPosition(int caretPos) {
		AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setDictionary(AutoCompleteDictionary dict) {
	    this.dict = dict;
    }

	public AutoCompleteDictionary getDictionary() {
	    return dict;
    }

}
