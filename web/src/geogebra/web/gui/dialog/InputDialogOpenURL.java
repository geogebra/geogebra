package geogebra.web.gui.dialog;

import java.awt.BorderLayout;

import geogebra.common.main.App;
import geogebra.web.gui.view.algebra.InputPanelW.DialogType;
import geogebra.web.gui.InputDialogW;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InputDialogOpenURL extends InputDialogW{
	
	public InputDialogOpenURL(AppW app){
		super(true);
		this.app = app;
		
		initString = "http://";
		
		createGUI(app.getMenu("OpenWebpage"), app.getMenu("EnterAppletAddress"), false, DEFAULT_COLUMNS, 1, false, true, false, false, DialogType.TextArea);		
		wrappedPopup.center();
		inputPanel.getTextComponent().getTextField().setFocus(true);			
	}
	
	public void onClick(ClickEvent e) {
	    Object source = e.getSource();

		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
				if(processInput()) wrappedPopup.hide();
//			} else if (source == btApply) {
//				processInput();
				// app.setDefaultCursor();
			} else if (source == btCancel) {
				wrappedPopup.hide();
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			ex.printStackTrace();
			setVisible(false);
			app.setDefaultCursor();
		}
		
    }
	
	private boolean processInput() {
		return app.getGuiManager().loadURL(inputPanel.getText());
	}

}
