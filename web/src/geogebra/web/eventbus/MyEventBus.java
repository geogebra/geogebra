package geogebra.web.eventbus;

import java.util.ArrayList;

import geogebra.web.jso.JsUint8Array;
import geogebra.web.presenter.BasePresenter;

import com.google.gwt.user.client.ui.HasWidgets;

public class MyEventBus {
	
	private ArrayList<BasePresenter> loadHandlers = new ArrayList<BasePresenter>();
	private ArrayList<BasePresenter> createApplicationHandlers = new ArrayList<BasePresenter>();
	private ArrayList<BasePresenter> syncCanvasSizeHandlers  =  new ArrayList<BasePresenter>();
	private ArrayList<BasePresenter> fileContentLoadHandlers = new ArrayList<BasePresenter>();
	private ArrayList<BasePresenter> worksheetConstructionFailedHanlders = new ArrayList<BasePresenter>();
	private ArrayList<BasePresenter> worksheetReadyHanlders = new ArrayList<BasePresenter>();
	
	
 	public MyEventBus(HasWidgets container, JsUint8Array zippedContent) {
 		
    }

	private void pageLoad() {
	    
		for (BasePresenter listener : loadHandlers) {
			listener.onPageLoad();        
        }
	    
    }

	public void createApplicationAndAddTo(HasWidgets container) {
	  for (BasePresenter listener : createApplicationHandlers) {
		  listener.onCreateApplicationAndAddTo(container);
	  }
	    
    }

	public void syncCanvasSizeWithApplication() {
		for (BasePresenter listener : syncCanvasSizeHandlers) {
			  listener.onSyncCanvasSizeWithApplication();
		  }
	    
    }

	public void fileContentLoaded(JsUint8Array zippedContent) {
		for (BasePresenter listener : fileContentLoadHandlers) {
			listener.onFileContentLoaded(zippedContent);
		}
	    
    }
	
	public void worksheetConstructionFailed(String errorMessage) {
		for (BasePresenter listener : worksheetConstructionFailedHanlders) {
			listener.onWorksheetConstructionFailed(errorMessage);
		}
	}
	
	public void worksheetReady() {
		for (BasePresenter listener : worksheetReadyHanlders) {
			listener.onWorksheetReady();
		}
	}
}
