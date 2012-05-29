package geogebra.web.gui.menubar;

import geogebra.common.main.AbstractApplication;
import geogebra.web.asyncservices.HandleGoogleDriveService;
import geogebra.web.asyncservices.HandleGoogleDriveServiceAsync;
import geogebra.web.gui.images.AppResources;
import geogebra.web.helper.GoogleApiCallback;
import geogebra.web.main.Application;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class FileMenu extends MenuBar {

	private AbstractApplication app;
	
	private final HandleGoogleDriveServiceAsync gdAsync = GWT.create(HandleGoogleDriveService.class);

	public FileMenu(AbstractApplication app) {
	    super(true);
	    this.app = app;
	    addStyleName("GeoGebraMenuBar");
	    initActions();
		update();
    }

	private void update() {
	    // TODO Auto-generated method stub
	    
    }

	private void initActions() {
	    addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),app.getMenu("New")),true,new Command() {
			
			public void execute() {
				app.setWaitCursor();
				app.fileNew();
				app.setDefaultCursor();
			}
		});
	    
	    addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE.document_open().getSafeUri().asString(), app.getMenu("Load")), true, new Command() {
			
			public void execute() {
				gdAsync.saveToGoogleDrive("hello GeoGebraServer", new AsyncCallback<String>() {
					
					public void onSuccess(String result) {
						AbstractApplication.debug("server said: "+result);
					}
					
					public void onFailure(Throwable caught) {
						AbstractApplication.error(caught.getLocalizedMessage());
					}
				});
			}
		});
	    
    }

}
