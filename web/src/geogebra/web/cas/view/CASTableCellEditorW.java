package geogebra.web.cas.view;

import geogebra.common.cas.view.CASTableCellEditor;
import geogebra.web.gui.KeyEventsHandler;
import geogebra.web.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.Widget;

public class CASTableCellEditorW implements CASTableCellEditor{

	private AutoCompleteTextFieldW textField;
	private CASTableW table;
	private AppW app;

	public CASTableCellEditorW(CASTableW table,AppW app){
		this.app=app;
		this.table=table;
		CASKeyEventsHandler handler = new CASKeyEventsHandler();
		textField = new AutoCompleteTextFieldW(0, app, false, handler);
		textField.setCASInput(true);
		textField.setAutoComplete(true);
		textField.showPopupSymbolButton(true);
		
	}
	public int getInputSelectionEnd() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	public int getInputSelectionStart() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	public String getInputSelectedText() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public String getInput() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public void setInputSelectionStart(int selStart) {
	    // TODO Auto-generated method stub
	    
    }

	public void setInputSelectionEnd(int selEnd) {
	    // TODO Auto-generated method stub
	    
    }
	
	public Widget getWidget(){
		return textField;
	}
	
	public class CASKeyEventsHandler implements KeyEventsHandler{

		public void onKeyDown(KeyDownEvent event) {
	        // TODO Auto-generated method stub
	        
        }

		public void onKeyPress(KeyPressEvent event) {
	        // TODO Auto-generated method stub
	        
        }

		public void onKeyUp(KeyUpEvent event) {
	        // TODO Auto-generated method stub
	        
        }
		
	}

	public void setLabels() {
	    textField.setDictionary(app.getCommandDictionaryCAS());
	    
    }

}
