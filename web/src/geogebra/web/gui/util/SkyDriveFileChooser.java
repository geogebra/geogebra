package geogebra.web.gui.util;

import geogebra.common.main.App;
import geogebra.web.css.GuiResources;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SkyDriveFileChooser extends DialogBox implements ClickHandler, DoubleClickHandler {

	private App app;
	VerticalPanel p;
	
	Button open;
	private Button cancel;
	private VerticalPanel filesPanel;
	
	
	public SkyDriveFileChooser(final App app) {
		this.app = app;
		setWidget(p = new VerticalPanel());
		filesPanel = new VerticalPanel();
		filesPanel.addStyleName("filesPanel");
		ScrollPanel filesContainer = new ScrollPanel();
		filesContainer.setSize("500px", "300px");
		HorizontalPanel buttonPanel = new HorizontalPanel();
	    buttonPanel.addStyleName("buttonPanel");
	    buttonPanel.add(cancel = new Button(app.getMenu("Cancel")));
	    buttonPanel.add(open = new Button(app.getMenu("Open")));
	    buttonPanel.add(open);
	    filesContainer.add(filesPanel);
	    p.add(filesContainer);
	    p.add(buttonPanel);
	    
	    initAClickHandler();
	    addStyleName("GeoGebraFileChooser");
	    
	    open.addClickHandler(this);
	    
		cancel.addClickHandler(new ClickHandler() {
					
					public void onClick(ClickEvent event) {
						app.setDefaultCursor();
						hide();
					}
				});
		
		this.
		center();
		
		
    }
	
	String currentFileName = null;
	String currentDescription;
	String currentTitle;
	String currentId;
	
	private void initAClickHandler() {
	   filenameClick = new ClickHandler() {
		
		

		public void onClick(ClickEvent event) {
			for (int i = 0; i < filesPanel.getWidgetCount(); i++) {
				filesPanel.getWidget(i).removeStyleName("selected");
			}
			Anchor a = (Anchor) event.getSource();
			a.addStyleName("selected");
			refreshDescriptors(a);
		}
	};
    }

	protected void refreshDescriptors(Anchor a) {
		 currentFileName = a.getElement().getAttribute("data-param-downloadurl");
		 currentTitle = a.getElement().getAttribute("data-param-title");
		 currentDescription = a.getElement().getAttribute("data-param-description");
		 currentId = a.getElement().getAttribute("data-param-id");
    }

	@Override
    public void show(){
	    super.show();
	    if (((AppW) app).getObjectPool().getMyGoogleApis().isLoggedIn() && ((AppW) app).getObjectPool().getMyGoogleApis().driveLoaded) {
			initFileNameItems();
		} else {
			showEmtpyMessage();
		}
	}



	private void showEmtpyMessage() {
	   filesPanel.clear();
	   filesPanel.add(new Label("Google Drive Loading Problem"));
    }

	private void clearFilesPanel() {
		filesPanel.clear();
		filesPanel.add(new HTML(GuiResources.INSTANCE.ggbSpinnerHtml().getText()));
	}
	
	private void removeSpinner() {
		filesPanel.clear();
	}


	private native void initFileNameItems() /*-{
		var fileChooser = this;
		
    }-*/;

	public void onClick(ClickEvent event) {
	    if (currentFileName != null) {
	    	clearFilesPanel();
	    	((AppW) app).getObjectPool().getMyGoogleApis().loadFromGoogleFile(currentFileName, currentDescription, currentTitle, currentId);
	    	//TODO: process descriptors here!
	    }
    }
	
	ClickHandler filenameClick;
	
	private void createLink(String fileName, String owner, String downloadLink, String title, String description, String id) {
		Anchor a = new Anchor();
		a.addStyleName("ggbfilelink");
		a.setTitle(owner);
		a.setText(fileName);
		a.addClickHandler(filenameClick);
		a.addDoubleClickHandler(this);
		a.getElement().setAttribute("data-param-downloadurl", downloadLink);
		a.getElement().setAttribute("data-param-title", title);
		a.getElement().setAttribute("data-param-description", description);
		a.getElement().setAttribute("data-param-id", id);
		filesPanel.add(a);
	}

	public void onDoubleClick(DoubleClickEvent event) {
	    Anchor a = (Anchor) event.getSource();
	    refreshDescriptors(a);
	    this.open.click();
    }

}
