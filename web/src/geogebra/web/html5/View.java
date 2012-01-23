package geogebra.web.html5;

import java.util.Map;

import geogebra.common.main.AbstractApplication;
import geogebra.web.jso.JsUint8Array;
import geogebra.web.main.Application;
import geogebra.web.util.DataUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class View extends Widget {
	
	private Map<String, String> archiveContent;
	
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

	public void showLoadAnimation(String absoluteUrl) {
	  	app.showLoadingAnimation(true); 
    }

	public String getDataParamFileName() {
	    return ((ArticleElement) container).getDataParamFileName();
    }

	public String getDataParamBase64String() {
		return ((ArticleElement) container).getDataParamBase64String();
	}
	
	public void fileContentLoaded(JsUint8Array zippedContent) {
		archiveContent = DataUtil.unzip(zippedContent);
		maybeLoadFile();	    
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
	}

	private void onSyncCanvasSizeWithApplication() {
	   app.getEuclidianView().synCanvasSize();
	   app.getActiveEuclidianView().repaintView();
    }

	public void fileContentLoaded(JsArrayInteger jsBytes) {
		archiveContent = DataUtil.unzip(jsBytes);
		maybeLoadFile();   
    }

}
