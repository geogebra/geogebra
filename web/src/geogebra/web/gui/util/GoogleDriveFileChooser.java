package geogebra.web.gui.util;

import geogebra.common.main.App;
import geogebra.web.css.GuiResources;
import geogebra.web.helper.MyGoogleApis;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GoogleDriveFileChooser extends PopupPanel implements ClickHandler, DoubleClickHandler {

	private App app;
	VerticalPanel p;
	
	Button open;
	private Button cancel;
	private VerticalPanel filesPanel;
	
	

	public GoogleDriveFileChooser(final App app) {
		this.app = app;
		add(p = new VerticalPanel());
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
	
	private void initAClickHandler() {
	   filenameClick = new ClickHandler() {
		
		public void onClick(ClickEvent event) {
			for (int i = 0; i < filesPanel.getWidgetCount(); i++) {
				filesPanel.getWidget(i).removeStyleName("selected");
			}
			Anchor a = (Anchor) event.getSource();
			a.addStyleName("selected");
		}
	};
    }

	@Override
    public void show(){
	    super.show();
	    if (MyGoogleApis.loggedIn && MyGoogleApis.driveLoaded) {
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
		fileChooser.@geogebra.web.gui.util.GoogleDriveFileChooser::clearFilesPanel()();
		function retrieveAllFiles(callback) {
			  var retrievePageOfFiles = function(request, result) {
			    request.execute(function(resp) {
			      result = result.concat(resp.items);
			      var nextPageToken = resp.nextPageToken;
			      if (nextPageToken) {
			        request = $wnd.gapi.client.drive.files.list({
			          'pageToken': nextPageToken
			        });
			        retrievePageOfFiles(request, result);
			      } else {
			        callback(result);
			      }
			    });
			  }
			  var initialRequest = $wnd.gapi.client.drive.files.list();
			  retrievePageOfFiles(initialRequest, []);
			}
			retrieveAllFiles(function(resp) {
				fileChooser.@geogebra.web.gui.util.GoogleDriveFileChooser::removeSpinner()()
				resp.forEach(function(value, index, array) {
					if (value.mimeType === "application/vnd.geogebra.file") {
						fileChooser.@geogebra.web.gui.util.GoogleDriveFileChooser::createLink(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(value.originalFilename,value.lastModifyingUserName,value.downloadUrl);
						$wnd.console.log(value);
					}
				});
			});
    }-*/;

	public void onClick(ClickEvent event) {
	    
    }
	
	ClickHandler filenameClick;
	
	private void createLink(String fileName, String owner, String downloadLink) {
		Anchor a = new Anchor();
		a.addStyleName("ggbfilelink");
		a.setTitle(owner);
		a.setText(fileName);
		a.addClickHandler(filenameClick);
		a.addDoubleClickHandler(this);
		a.getElement().setAttribute("data-param-downloadurl", downloadLink);
		filesPanel.add(a);
	}

	public void onDoubleClick(DoubleClickEvent event) {
	    // TODO Auto-generated method stub
	    
    }

}
