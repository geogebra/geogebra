package geogebra.web.gui.dialog;

import geogebra.common.main.App;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.main.AppW;
import geogebra.web.main.GgbAPI;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InputDialogDownloadGGB extends InputDialogW{
	
	Anchor linkDownload;
	
	public InputDialogDownloadGGB(AppW app){
		super(true);
		this.app = app;
		
		createGUI();
		wrappedPopup.center();
		inputPanel.getTextComponent().getTextField().setFocus(true);
		((GgbAPI) app.getGgbApi()).getGGB(true, this.linkDownload.getElement());
		inputPanel.getTextComponent().getTextField().addValueChangeHandler(new ValueChangeHandler<String>(){
			public void onValueChange(ValueChangeEvent<String> event) {
				setFilename(event.getValue());
            }		
		});	
	}
	
	public void setFilename(String newVal){
		if (newVal.equals("")) newVal = "geogebra.ggb";
        linkDownload.getElement().setAttribute("download", newVal);
	}
	
	protected void createGUI() {

		// Create components to be displayed
		inputPanel = new InputPanelW(initString, app, DEFAULT_COLUMNS, 1,
				false/*, type*/);

		
		btCancel = new Button(app.getPlain("Cancel"));
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btCancel.addClickHandler(this);

		linkDownload = new Anchor();
		linkDownload.setText(app.getPlain("Download"));	
		linkDownload.setStyleName("gwt-Button");
		linkDownload.addStyleName("linkDownload");
		linkDownload.getElement().setAttribute(
				"style", "text-decoration: none; color: black");
		linkDownload.addClickHandler(this);
		setFilename("geogebra.ggb");
		
		// create button panel
		FlowPanel btPanel = new FlowPanel();
		btPanel.addStyleName("DialogButtonPanel");
		btPanel.add(linkDownload);
		btPanel.add(btCancel);
		
		VerticalPanel centerPanel = new VerticalPanel();
		centerPanel.add(inputPanel);
		centerPanel.add(btPanel);
		
		wrappedPopup.setWidget(centerPanel);
	}
	
	public void onClick(ClickEvent e) {
	    Object source = e.getSource();
	    App.debug(source.toString());

		try {
//			if (source == btOK || source == inputPanel.getTextComponent()) {
//				if(processInput()) wrappedPopup.hide();
//				app.getActiveEuclidianView().requestFocusInWindow();
//		}else
			if (source == linkDownload){
				//linkDownload is a link with <download> attribute, so if the user clicks on it,
				//it downloads the file "automatically", so it's enough to close the dialog here,
				//until we have no filename-checking.
				wrappedPopup.hide();
			}
			else if (source == btCancel) {
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
