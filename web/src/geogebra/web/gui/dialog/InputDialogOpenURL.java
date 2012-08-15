package geogebra.web.gui.dialog;

import geogebra.common.gui.view.algebra.DialogType;
import geogebra.web.gui.InputDialogW;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;

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
				app.getActiveEuclidianView().requestFocusInWindow();
//			} else if (source == btApply) {
//				processInput();
				// app.setDefaultCursor();
			} else if (source == btCancel) {
				wrappedPopup.hide();
				app.getActiveEuclidianView().requestFocusInWindow();
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			ex.printStackTrace();
			setVisible(false);
			app.setDefaultCursor();
			app.getActiveEuclidianView().requestFocusInWindow();
		}
		
    }
	
	private boolean processInput() {
		return app.getGuiManager().loadURL(inputPanel.getText());
	}

}
