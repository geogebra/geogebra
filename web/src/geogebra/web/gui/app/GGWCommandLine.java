package geogebra.web.gui.app;

import geogebra.common.main.App;
import geogebra.web.gui.inputbar.AlgebraInputW;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;

public class GGWCommandLine extends Composite implements RequiresResize{
	
	AlgebraInputW algebraInput;

	public GGWCommandLine() {
		algebraInput = new AlgebraInputW();
		initWidget(algebraInput);
	}

	public void attachApp(App app) {
	    algebraInput.init((AppW) app);
    }

	public void onResize() {
		algebraInput.onResize();
	    
    }

}
