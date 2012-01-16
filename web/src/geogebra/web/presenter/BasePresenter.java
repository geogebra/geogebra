package geogebra.web.presenter;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasWidgets;

import geogebra.common.main.AbstractApplication;
import geogebra.web.html5.View;
import geogebra.web.jso.JsUint8Array;

public abstract class BasePresenter {
	
	private View view;


	public View getView() {
	    return view;
    }

	public void setView(View view) {
	    this.view = view;
    }

	public void onPageLoad() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onCreateApplicationAndAddTo(Element element) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onSyncCanvasSizeWithApplication() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onFileContentLoaded(JsUint8Array zippedContent) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onWorksheetConstructionFailed(String errorMessage) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onWorksheetReady() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }
	
	

}
