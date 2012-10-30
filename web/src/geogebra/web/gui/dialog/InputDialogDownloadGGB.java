package geogebra.web.gui.dialog;

import geogebra.common.gui.view.algebra.DialogType;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

public class InputDialogDownloadGGB extends InputDialogW{
	
	public InputDialogDownloadGGB(AppW app){
		super(true);
		this.app = app;
		
		//createGUI(app.getMenu("Download..."), null, false, DEFAULT_COLUMNS, 1, false, true, false, false, DialogType.TextArea);		
		createGUI(app.getMenu("OpenWebpage"), app.getMenu("EnterAppletAddress"), false, DEFAULT_COLUMNS, 1, false, true, false, false, DialogType.TextArea);
		this.btOK.addStyleName("downloadButton");
		this.btOK.getElement().setId("downloadButton");
		this.btOK.setEnabled(false);
		this.btOK.getElement().setAttribute("ggburl", "a");
		wrappedPopup.center();
		inputPanel.getTextComponent().getTextField().setFocus(true);
		addEventListenerForDownloadButton(this.btOK.getElement());
		createGGB();
		this.btOK.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				String ggbURL = DOM.getElementById("downloadButton").getAttribute("ggburl");
	            Window.open(ggbURL, "_blank", null);
            }
		});
	}
	
	private native void addEventListenerForDownloadButton(Element downloadButton) /*-{
		//var downloadButton = document.getElementById("downloadButton");
		$wnd.downloadggb.setDownloadButton(downloadButton);
        //downloadButton.addEventListener("click", $wnd.downloadggb.downloadGGBfunction, false);
	}-*/;
	
	private native String createGGB() /*-{
	    $wnd.downloadggb.downloadGGBfunction();
	}-*/;
	
	
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
