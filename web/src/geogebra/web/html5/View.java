package geogebra.web.html5;

import geogebra.web.jso.JsUint8Array;
import geogebra.web.main.Application;
import geogebra.web.main.GeoGebraTubeExportWeb;
import geogebra.web.util.DataUtil;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class View extends Widget {
	
	private HashMap<String, String> archiveContent;
	
	private Element container;
	private Application app;
	
	public View(Element container, Application app) {
		this.app = app;
		this.container = container;
    }

	public Element getContainer() {
	    return container;
    }

	public void promptUserForGgbFile() {		
		GWT.log("No data-param-fileName attribute presented");
    }

	public void showError(String errorMessage) {
	   	GWT.log(errorMessage);    
    }

	public void hide() {
		GWT.log("View.hide called");
    }

	public void showLoadAnimation() {
	  	app.showLoadingAnimation(true); 
    }

	public String getDataParamFileName() {
	    return ((ArticleElement) container).getDataParamFileName();
    }

	public String getDataParamBase64String() {
		return ((ArticleElement) container).getDataParamBase64String();
	}
	
	public void fileContentLoaded(JsUint8Array zippedContent) {
		DataUtil.unzip(zippedContent,this);    
    }
	
	private void maybeLoadFile() {
		if (app == null || archiveContent == null) {
			return;
		}

		try {
			app.loadGgbFile(archiveContent);
		} catch (Exception ex) {
			Application.log(ex.getMessage());
			return;
		}
		archiveContent = null;
		onSyncCanvasSizeWithApplication();

		app.getScriptManager().ggbOnInit();// put this here from Application constructor because we have to delay scripts until the EuclidianView is shown
		
	}

	private void onSyncCanvasSizeWithApplication() {
	   app.getEuclidianView1().synCanvasSize();
	   app.getActiveEuclidianView().repaintView();
    }

	public void fileContentLoaded(JsArrayInteger jsBytes) {
		DataUtil.unzip(jsBytes,this); 
    }

	public void maybeLoadFile(HashMap<String, String> archiveCont) {
	    archiveContent = archiveCont;
	    maybeLoadFile();
    }

}
