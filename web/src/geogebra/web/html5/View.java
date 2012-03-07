package geogebra.web.html5;

import geogebra.common.main.AbstractApplication;
import geogebra.web.jso.JsUint8Array;
import geogebra.web.main.Application;
import geogebra.web.main.GeoGebraTubeExportWeb;
import geogebra.web.util.DataUtil;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayString;
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

	public void processBase64String(String dataParamBase64String) {
		archiveContent = new HashMap<String, String>();
		populateArchiveContent(dataParamBase64String, GWT.getModuleBaseURL(),this);
    }
	
	private int zippedLength = 0;
	
	private void putIntoArciveContent(String key, String value) {
		archiveContent.put(key, value);
		if (archiveContent.size() == zippedLength) {
			maybeLoadFile();
		}
	}

	private native void populateArchiveContent(String dpb64str, String workerUrls, View view) /*-{
	    var imageRegex = /\.(png|jpg|jpeg|gif)$/;
    	$wnd.zip.workerScriptsPath = workerUrls+"js/zipjs/";
	    $wnd.zip.createReader(new $wnd.zip.Data64URIReader(dpb64str),function(reader) {
	        reader.getEntries(function(entries) {
	        	view.@geogebra.web.html5.View::zippedLength = entries.length;
	            for (var i = 0, l = entries.length; i < l; i++) {
	            	(function(entry){
		            	var filename = entry.filename;
		                if (entry.filename.match(imageRegex)) {
		                        $wnd.console.log(filename+" : image");
		                        //@com.google.gwt.core.client.GWT::log(Ljava/lang/String;)(filename);
		                        entry.getData(new $wnd.zip.Data64URIWriter("image/"+filename.split(".")[1]), function (data) {
		                            view.@geogebra.web.html5.View::putIntoArciveContent(Ljava/lang/String;Ljava/lang/String;)(filename,data);
		                        	@com.google.gwt.core.client.GWT::log(Ljava/lang/String;)(data);
		                        });
		                    } else {
		                        $wnd.console.log(entry.filename+" : text");
		                        //@com.google.gwt.core.client.GWT::log(Ljava/lang/String;)(filename);
		                        entry.getData(new $wnd.zip.TextWriter(), function(text) {
		                          view.@geogebra.web.html5.View::putIntoArciveContent(Ljava/lang/String;Ljava/lang/String;)(filename,text);
		                          @com.google.gwt.core.client.GWT::log(Ljava/lang/String;)(text);
		                        });
		                }
	            	})(entries[i]);
	            } 
	        });
	    },
	    function (error) {
	    	$wnd.console.log(error);
	    });
    }-*/;




}
