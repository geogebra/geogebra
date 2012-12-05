package geogebra.web.gui.dialog;

import geogebra.common.gui.view.algebra.DialogType;
import geogebra.web.main.AppW;
import geogebra.web.main.GgbAPI;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

public class InputDialogDownloadGGB extends InputDialogW{
	
	public InputDialogDownloadGGB(AppW app){
		super(true);
		this.app = app;
		
		createGUI(app.getMenu("Download"), null, false, DEFAULT_COLUMNS, 1, false, true, false, false, DialogType.TextArea);		
		//createGUI(app.getMenu("OpenWebpage"), app.getMenu("EnterAppletAddress"), false, DEFAULT_COLUMNS, 1, false, true, false, false, DialogType.TextArea);
		//this.btOK.addStyleName("downloadButton");
		this.btOK.getElement().setId("downloadButton");
		this.btOK.setEnabled(false);
		this.btOK.getElement().setAttribute("ggburl", "a");
		wrappedPopup.center();
		inputPanel.getTextComponent().getTextField().setFocus(true);
		((GgbAPI) app.getGgbApi()).getGGB(true, this.btOK.getElement(), inputPanel.getTextComponent().getTextField().getElement());
		this.btOK.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				Element dButton = DOM.getElementById("downloadButton");
				String name = inputPanel.getTextComponent().getTextField().getText();
				if (name.equals("")) name = "geogebra";
				dButton.setAttribute("download", name+".ggb");
				String ggbURL = dButton.getAttribute("ggburl");
	            Window.open(ggbURL, "_blank", null);
            }
		});
	}
	
	public void onClick(ClickEvent e) {
	    Object source = e.getSource();

		try {
//			if (source == btOK || source == inputPanel.getTextComponent()) {
//				if(processInput()) wrappedPopup.hide();
//				app.getActiveEuclidianView().requestFocusInWindow();
//		}else
			if (source == btCancel) {
				wrappedPopup.hide();
				app.getActiveEuclidianView().requestFocusInWindow();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			setVisible(false);
			app.setDefaultCursor();
			app.getActiveEuclidianView().requestFocusInWindow();
		}
		
    }

}
