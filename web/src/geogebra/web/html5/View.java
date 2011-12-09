package geogebra.web.html5;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class View extends Widget {
	
	private HasWidgets container;
	
	public View(HasWidgets container) {
		this.container = container;
    }

	public HasWidgets getContainer() {
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
	  	GWT.log("Showing animation");    
    }

	public String getDataParamFileName() {
	    // TODO Auto-generated method stub
	    return ((ArticleElement) container).getDataParamFileName();
    }

}
