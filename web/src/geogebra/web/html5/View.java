package geogebra.web.html5;

import geogebra.common.main.AbstractApplication;
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
	
	public String getDataParamLanguage() {
		return ((ArticleElement) container).getDataParamLanguage();
    }

	public String getDataParamCountry() {
		return ((ArticleElement) container).getDataParamCountry();
    }
	
	public boolean getDataParamEnableLabelDrags() {
		return ((ArticleElement) container).getDataParamEnableLabelDrags();
	}
	
	public boolean getDataParamShowMenuBar() {
		return ((ArticleElement) container).getDataParamShowMenuBar();
    }

	public boolean getDataParamShowAlgebraInput() {
		return ((ArticleElement) container).getDataParamShowAlgebraInput();
    }

	public boolean getDataParamShowToolBar() {
		return ((ArticleElement) container).getDataParamShowToolBar();
    }

	public boolean getDataParamShowToolBarHelp() {
		//return ((ArticleElement) container).getDataParamShowToolBarHelp();
		return false;
    }

	public boolean getDataParamShiftDragZoomEnabled() {
		return ((ArticleElement) container).getDataParamShiftDragZoomEnabled();
    }

	public boolean getDataParamShowResetIcon() {
		return ((ArticleElement) container).getDataParamShowResetIcon();
    }	
	
	public void fileContentLoaded(JsUint8Array zippedContent) {
		AbstractApplication.debug("start unzip");
		DataUtil.unzip(zippedContent,this);    
		AbstractApplication.debug("end unzip");
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

		app.getScriptManager().ggbOnInit();// put this here from Application constructor because we have to delay scripts until the EuclidianView is shown
		
		AbstractApplication.debug("file loaded");
		
	}

	public void fileContentLoaded(JsArrayInteger jsBytes) {
		AbstractApplication.debug("start unzip");
		DataUtil.unzip(jsBytes,this); 
		AbstractApplication.debug("end unzip");
    }

	public void maybeLoadFile(HashMap<String, String> archiveCont) {
	    archiveContent = archiveCont;
	    maybeLoadFile();
    }

	public Application getApplication() {
	    return app;
    }




}
