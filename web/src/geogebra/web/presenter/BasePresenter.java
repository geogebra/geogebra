package geogebra.web.presenter;

import com.google.gwt.user.client.ui.HasWidgets;

import geogebra.common.main.AbstractApplication;
import geogebra.web.eventbus.MyEventBus;
import geogebra.web.html5.View;
import geogebra.web.jso.JsUint8Array;

public abstract class BasePresenter {
	
	protected MyEventBus eventBus;
	private View view;

	public MyEventBus getEventBus() {
	    return eventBus;
    }

	public void setEventBus(MyEventBus eventBus) {
	    this.eventBus = eventBus;
    }

	public View getView() {
	    return view;
    }

	public void setView(View view) {
	    this.view = view;
    }

	public void onPageLoad() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
	    
    }

	public void onCreateApplicationAndAddTo(HasWidgets container) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
	    
    }

	public void onSyncCanvasSizeWithApplication() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
	    
    }

	public void onFileContentLoaded(JsUint8Array zippedContent) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
	    
    }

	public void onWorksheetConstructionFailed(String errorMessage) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
	    
    }

	public void onWorksheetReady() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated method stub
	    
    }
	
	

}
