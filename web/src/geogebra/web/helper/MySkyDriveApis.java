package geogebra.web.helper;

import geogebra.common.main.App;
import geogebra.web.html5.DynamicScriptElement;

import com.google.gwt.dom.client.Document;

public class MySkyDriveApis {
	
	private App app;
	boolean skyDriveApiLoaded = false;
	boolean signedIn = false;
	
	public MySkyDriveApis(App app) {
		this.app = app;
		goForSkyDriveApi();
	}

	private void goForSkyDriveApi() {
		DynamicScriptElement script = (DynamicScriptElement) Document.get().createScriptElement();
		script.setSrc("//js.live.net/v5.0/wl.js");
		script.addLoadHandler(new ScriptLoadCallback() {
			
			public void onLoad() {
				skyDriveApiLoaded  = true;
			}
		});
		Document.get().getBody().appendChild(script);
    }

	public boolean signedInToSkyDrive() {
	    return signedIn;
    }
	
	public void clearAllTokens() {
		
	}

}
