package org.geogebra.web.web.gui.app;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.inputbar.AlgebraInputW;

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

	public boolean hasFocus() {
		return algebraInput.hasFocus();
    }
}
