package geogebra.web.gui.util;

import geogebra.common.main.App;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.JavaScriptObject;

public class SkyDriveFileChooser {

	private App app;
	
	
	public SkyDriveFileChooser(final App app) {
		this.app = app;
    }
	
	

    public void show(){  	
	    if (((AppW) app).getObjectPool().getMySkyDriveApis().isLoggedIn() && ((AppW) app).getObjectPool().getMySkyDriveApis().isLoaded()) {
	    	App.debug("show called inside");
	    	showFileChooser();
		}
	}
    
    private void processFile(JavaScriptObject fileDescriptors) {
		App.debug(fileDescriptors);
	}



	private native void showFileChooser() /*-{
	  	var _this = this;
	  	function openFromSkyDrive() {
    		$.wnd.WL.fileDialog({
        		mode: 'open',
        		select: 'single'
    		}).then(
        		function(response) {
            		var files = response.data.files;
            		for (var i = 0; i < files.length; i++) {
                	var file = files[i];
                	_this.@geogebra.web.gui.util.SkyDriveFileChooser::processFile(Lcom/google/gwt/core/client/JavaScriptObject;)(file);
            }
        },
        function(errorResponse) {
            @geogebra.common.main.App::debug(Ljava/lang/String;)($wnd.JSON.stringify(errorResponse));
        	});
		}
		openFromSkyDrive();
		$wnd.console.log("showfilechooser called");
    }-*/;

	

}
