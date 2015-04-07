package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.DomEvent;

public class InputDialogOpenURL extends InputDialogW{
	
	public InputDialogOpenURL(AppW app){
		super(true);
		this.app = app;
		
		initString = "http://";
		
		createGUI(app.getMenu("OpenWebpage"), app.getMenu("EnterAppletAddress"), false, DEFAULT_COLUMNS, 1, false, true, false, false, DialogType.TextArea);		
		wrappedPopup.center();			
	}

	@Override
	protected void actionPerformed(DomEvent e) {
	    Object source = e.getSource();

		try {
			if (source == btOK || sourceShouldHandleOK(source)) {
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
