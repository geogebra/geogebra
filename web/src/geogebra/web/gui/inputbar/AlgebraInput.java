package geogebra.web.gui.inputbar;

import geogebra.web.gui.inputfield.AutoCompleteTextField;
import geogebra.web.gui.view.algebra.InputPanel;
import geogebra.web.main.Application;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author gabor
 * 
 * InputBar for GeoGebraWeb
 *
 */
public class AlgebraInput extends HorizontalPanel implements KeyUpHandler, FocusHandler {
	
	private Application app;
	private Label inputLabel;
	private InputPanel inputPanel;
	private AutoCompleteTextField inputField;

	/**
	 * Creates AlgebraInput for Web
	 */
	AlgebraInput() {
		super();	
	}
	
	/**
	 * @param app Application
	 * 
	 * Attaches Application and creates the GUI of AlgebraInput
	 */
	public void init(Application app) {
		this.app = app;
		//AG I dont think we need this app.removeTraversableKeys(this);
		
		initGUI();
	}

	private void initGUI() {
	    clear();
	    inputLabel = new Label();
	    inputPanel = new InputPanel(null,app,30,true);
	    
	    inputField = inputPanel.getTextComponent();
	    
	    inputField.addKeyUpHandler(this);
	    inputField.addFocusHandler(this);
	    
	    inputField.addHistoryPopup(app.showInputTop());
	    
	    //continue here...
	    
    }

	public void onFocus(FocusEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void onKeyUp(KeyUpEvent event) {
	    // TODO Auto-generated method stub
	    
    }

}
