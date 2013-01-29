package geogebra.web.cas.view;

import geogebra.common.cas.view.CASTableCellEditor;
import geogebra.web.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.web.main.AppW;

public class CASTableCellEditorW implements CASTableCellEditor{

	private AutoCompleteTextFieldW textField;
	private CASTableW table;
	private AppW app;

	public CASTableCellEditorW(CASTableW table,AppW app, CASTableControllerW ml){
		this.app=app;
		this.table=table;
		textField = new AutoCompleteTextFieldW(0, app);
		textField.setCASInput(true);
		textField.setAutoComplete(true);
		textField.showPopupSymbolButton(true);
		textField.addKeyHandler(ml);
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
	    textField.setDictionary(app.getCommandDictionaryCAS());
	    
    }

}
