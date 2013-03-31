package geogebra.web.gui.util;

import geogebra.common.main.App;
import geogebra.web.main.AppW;
import geogebra.web.util.JSON;

import com.google.gwt.core.client.JavaScriptObject;

public class SkyDriveFileChooser {

	private App app;
	
	
	public SkyDriveFileChooser(final App app) {
		this.app = app;
    }
	
	

    public void show(){  	
	    if (((AppW) app).getObjectPool().getMySkyDriveApis().isLoggedIn() && ((AppW) app).getObjectPool().getMySkyDriveApis().isLoaded()) {
	    	showFileChooser();
		}
	}
    
    private void processFile(JavaScriptObject fileDescriptors) {
		String id = JSON.get(fileDescriptors, "id");
		String name = JSON.get(fileDescriptors, "name");
		String source = JSON.get(fileDescriptors, "source");
		AppW.debug(fileDescriptors);
		((AppW) app).getObjectPool().getMySkyDriveApis().loadFromSkyDrive(id, name, source);
	}



	private native void showFileChooser() /*-{
	  	var _this = this;
	  	function openFromSkyDrive() {
    		$wnd.WindowsLive.fileDialog({
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
    }-*/;

	

}
