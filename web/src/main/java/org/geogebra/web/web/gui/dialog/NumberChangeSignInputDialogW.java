package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.gui.dialog.handler.NumberChangeSignInputHandler;
import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.CheckBox;

public class NumberChangeSignInputDialogW extends InputDialogW{
	private boolean changingSign;
	private CheckBox checkBox;

	/**
	 * 
	 * @param app
	 * @param message
	 * @param title
	 * @param initString
	 * @param handler
	 * @param changingSign says if the sign has to be changed
	 * @param extruder 
	 */
	public NumberChangeSignInputDialogW(AppW app, String message,
			String title, String initString, 
			NumberChangeSignInputHandler handler, 
			boolean changingSign, String checkBoxText) {
		super(app, message, title, initString, false, 
				handler, true,
				false, null,
				 DialogType.TextArea);
		this.checkBox = new CheckBox(checkBoxText,true);
		this.changingSign=changingSign;
	}
	
	@Override
	protected boolean processInputHandler(){
//		Construction cons = app.getKernel().getConstruction();
//		boolean oldVal = cons.isSuppressLabelsActive();
//		cons.setSuppressLabelCreation(true);
//		boolean success =  ((NumberChangeSignInputHandler) inputHandler).processInput(inputText,changingSign && checkBox.getValue());
//		cons.setSuppressLabelCreation(oldVal);
//		if(success){
//			//TODO callback to actually extrude
//		}
//		return success;
		
		return ((NumberChangeSignInputHandler) inputHandler).processInput(inputText,changingSign && checkBox.getValue());
	}
}
