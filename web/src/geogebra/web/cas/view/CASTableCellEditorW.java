package geogebra.web.cas.view;

import geogebra.common.cas.view.CASTableCellEditor;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.main.AppW;

public class CASTableCellEditorW implements CASTableCellEditor {

	private AutoCompleteTextFieldW textField;
	private CASTableW table;
	private AppW app;

	public CASTableCellEditorW(CASTableW table,AppW app, CASTableControllerW ml){
		this.app=app;
		this.table=table;
		textField = new AutoCompleteTextFieldW(0, app, true, null, true);
		textField.setCASInput(true);
		textField.setAutoComplete(true);
		textField.requestToShowSymbolButton();
		textField.showPopupSymbolButton(true);
		textField.addKeyHandler(ml);
		//FIXME experimental fix for CAS in other languages, broken in r27612
		// This will update the CAS commands also
		app.updateCommandDictionary();
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
	    return textField.getText();
    }

	public void setInputSelectionStart(int selStart) {
	    // TODO Auto-generated method stub
	    
    }

	public void setInputSelectionEnd(int selEnd) {
	    // TODO Auto-generated method stub
	    
    }
	
	public AutoCompleteTextFieldW getWidget(){
		return textField;
	}

	public void setLabels() {
	    textField.setDictionary(true);
    }
	
	public void setInput(String input) {
		this.textField.setText(input);
	}

	public void clearInputText() {
	    // TODO Auto-generated method stub
	}

}
