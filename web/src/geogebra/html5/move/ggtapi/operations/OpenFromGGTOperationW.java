package geogebra.html5.move.ggtapi.operations;

import geogebra.common.gui.GuiManager;
import geogebra.common.main.App;
import geogebra.common.move.ggtapi.operations.OpenFromGGTOperation;
import geogebra.web.gui.dialog.DialogManagerW;

/**
 * @author gabor
 * Open From GGT operational class for Web
 */
public class OpenFromGGTOperationW extends OpenFromGGTOperation {

	/**
	 * @param app Application
	 * Open from GGT operational class for web
	 */
	public OpenFromGGTOperationW(App app) {
	    super(app);
	    iniNativeEvents();
    }
	
	/**
	 * @return GGTURL for Web
	 */
	public String generateOpenFromGGTURL() {
		return super.generateOpenFromGGTURL(APP_TYPE.WEB);
	}
	
	private native void iniNativeEvents() /*-{
	var t = this;
    $wnd.addEventListener("message",function(event) {
    	var data;
    	//later if event.origin....
    	if (event.data) {
    		data = $wnd.JSON.parse(event.data);
    		if (data.action === "openfromggt") {
    			t.@geogebra.html5.move.ggtapi.operations.OpenFromGGTOperationW::processURL(Ljava/lang/String;)(data.msg);
    		}
    	}
    	}, false);
	}-*/;
	
	private void processURL(String url) {
		app.setWaitCursor();

		// supposing this is an advanced feature, will not be called from AppWsimple anyway
		((GuiManager) app.getGuiManager()).loadURL(url + "?mobile=true");
		((DialogManagerW) app.getDialogManager()).closeOpenFromGGTDialog();
	}

}
