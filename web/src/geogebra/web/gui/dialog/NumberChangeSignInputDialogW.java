package geogebra.web.gui.dialog;

import geogebra.common.geogebra3D.euclidian3D.draw.DrawExtrusionOrConify3D;
import geogebra.common.gui.dialog.handler.NumberChangeSignInputHandler;
import geogebra.common.gui.view.algebra.DialogType;
import geogebra.common.kernel.Construction;
import geogebra.common.main.App;
import geogebra.html5.main.AppW;

import com.google.gwt.user.client.ui.CheckBox;

public class NumberChangeSignInputDialogW extends InputDialogW{
	private boolean changingSign;
	private CheckBox checkBox;
	private DrawExtrusionOrConify3D extruder;

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
			boolean changingSign, String checkBoxText, DrawExtrusionOrConify3D extruder) {
		super(app, message, title, initString, false, 
				handler, true,
				false, null,
				 DialogType.TextArea);
		this.checkBox = new CheckBox(checkBoxText,true);
		this.changingSign=changingSign;
		this.extruder = extruder;
	}
	
	@Override
	protected boolean processInputHandler(){
		Construction cons = app.getKernel().getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		App.debug("Extrude input"+inputText);
		boolean success =  ((NumberChangeSignInputHandler) inputHandler).processInput(inputText,changingSign && checkBox.getValue());
		App.debug("Extrude result"+success+","+((NumberChangeSignInputHandler) inputHandler).getNum());
		cons.setSuppressLabelCreation(oldVal);
		if(success){
			this.extruder.extrude(((NumberChangeSignInputHandler) inputHandler).getNum());
		}
		return success;
	}
}
