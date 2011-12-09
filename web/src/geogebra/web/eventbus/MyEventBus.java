package geogebra.web.eventbus;

import java.util.ArrayList;

import geogebra.web.ggb.ApplicationWrapper;
import geogebra.web.html5.ArticleElement;
import geogebra.web.jso.JsUint8Array;
import geogebra.web.presenter.BasePresenter;
import geogebra.web.presenter.LoadFilePresenter;

import com.google.gwt.user.client.ui.HasWidgets;

public class MyEventBus {
	
	private ArticleElement container;
	
	private ArrayList<BasePresenter> loadHandlers = new ArrayList<BasePresenter>();
	private ArrayList<BasePresenter> createApplicationHandlers = new ArrayList<BasePresenter>();
	private ArrayList<BasePresenter> syncCanvasSizeHandlers  =  new ArrayList<BasePresenter>();
	private ArrayList<BasePresenter> fileContentLoadHandlers = new ArrayList<BasePresenter>();
	private ArrayList<BasePresenter> worksheetConstructionFailedHanlders = new ArrayList<BasePresenter>();
	private ArrayList<BasePresenter> worksheetReadyHanlders = new ArrayList<BasePresenter>();
	
	
 	public MyEventBus(ArticleElement article) {
 		this.container = article;
    }

	public void pageLoad() {
	    
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

	public void addLoadHandler(BasePresenter ... loadListeners) {
		for (int i = 0; i < loadListeners.length; i++) {
			loadHandlers.add(loadListeners[i]);
		}
    }

	public void addFileContentLoadHandler(BasePresenter ... contentLoadListneres) {
		for (int i = 0; i < contentLoadListneres.length; i++) {
			fileContentLoadHandlers.add(contentLoadListneres[i]);
		}
    }

	public void addCreateApplicationHandler(BasePresenter ... createApplicationListeners) {
	    for (int i = 0; i < createApplicationListeners.length; i++) {
	    	createApplicationHandlers.add(createApplicationListeners[i]);
	    }
	    
    }
}
