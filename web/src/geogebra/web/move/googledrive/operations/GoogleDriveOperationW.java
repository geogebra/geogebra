package geogebra.web.move.googledrive.operations;

import geogebra.common.move.operations.BaseOperation;
import geogebra.common.move.views.BaseEventView;
import geogebra.common.move.views.EventRenderable;
import geogebra.html5.util.DynamicScriptElement;
import geogebra.web.move.googledrive.events.GoogleDriveLoadedEvent;
import geogebra.web.move.googledrive.models.GoogleDriveModelW;

import com.google.gwt.dom.client.Document;

/**
 * @author gabor
 *
 *	Operational class for Google Drive Api
 */
public class GoogleDriveOperationW extends BaseOperation<EventRenderable> {
	
	private static final String GoogleApiJavaScriptSrc = "https://apis.google.com/js/client.js?onload=GGW_loadGoogleDrive";
	private boolean isDriveLoaded;

	
	/**
	 * creates new google drive operation instance
	 */
	public GoogleDriveOperationW () {
		
		setView(new BaseEventView());
		setModel(new GoogleDriveModelW());
		
	}
	
	@Override
    public GoogleDriveModelW getModel() {
		return (GoogleDriveModelW) super.getModel();
	}
	
	/**
	 * @return the logged in user name
	 */
	public String getUserName() {
		return getModel().getUserName();
	}
	
	/**
	 * Go for the google drive url, and fetch the script
	 */
	public void initGoogleDriveApi() {
		createGoogleApiCallbackFunction();
		fetchScript();
	}

	private void fetchScript() {
		DynamicScriptElement script = (DynamicScriptElement) Document.get().createScriptElement();
		script.setSrc(GoogleApiJavaScriptSrc);
		Document.get().getBody().appendChild(script);
    }

	private native void createGoogleApiCallbackFunction() /*-{
		var _this = this;
		$wnd.GGW_loadGoogleDrive = function() {
			_this.@geogebra.web.move.googledrive.operations.GoogleDriveOperationW::loadGoogleDrive()();
			
		}
    }-*/;
	
	private native void loadGoogleDrive() /*-{
		var _this = this;
	    $wnd.gapi.client.load('drive', 'v2', function() {
	     _this.@geogebra.web.move.googledrive.operations.GoogleDriveOperationW::googleDriveLoaded()();
        });
	}-*/;
	
	private void googleDriveLoaded() {
		this.isDriveLoaded = true;
		onEvent(new GoogleDriveLoadedEvent());
	}

	/**
	 * @return if google drive loaded or not
	 */
	public boolean isDriveLoaded() {
	    return isDriveLoaded;
    }
	
	

	

}
