package geogebra.gui.dialog;

import geogebra.common.gui.dialog.handler.NumberChangeSignInputHandler;
import geogebra.gui.view.algebra.InputPanelD.DialogType;
import geogebra.main.AppD;

import javax.swing.JCheckBox;

/**
 * InputDialog with checkbox to change sign
 * @author mathieu
 *
 */
public class NumberChangeSignInputDialog extends InputDialogD {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean changingSign;

	/**
	 * 
	 * @param app
	 * @param message
	 * @param title
	 * @param initString
	 * @param handler
	 * @param changingSign says if the sign has to be changed
	 */
	public NumberChangeSignInputDialog(AppD app, String message,
			String title, String initString, 
			NumberChangeSignInputHandler handler, 
			boolean changingSign, String checkBoxText) {
		super(app, message, title, initString, false, 
				handler, true,
				false, null,
				new JCheckBox(checkBoxText,true), DialogType.TextArea);
		
		this.changingSign=changingSign;
	}
	
	@Override
	protected boolean processInputHandler(){
		return ((NumberChangeSignInputHandler) inputHandler).processInput(inputText,changingSign && checkBox.isSelected());
	}
	
	@Override
	protected void loadBtPanel(boolean showApply){
		btPanel.add(checkBox);
		super.loadBtPanel(showApply);
	}	
}