package geogebra.web.gui.app;

import geogebra.common.main.App;
import geogebra.web.gui.inputbar.AlgebraInputW;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class GGWCommandLine extends Composite implements RequiresResize{
	
	@UiField AlgebraInputW algebraInput;

	private static GGWCommandLineUiBinder uiBinder = GWT
	        .create(GGWCommandLineUiBinder.class);

	interface GGWCommandLineUiBinder extends UiBinder<Widget, GGWCommandLine> {
	}

	public GGWCommandLine() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void attachApp(App app) {
	    algebraInput.init((AppW) app);
    }

	public void onResize() {
		algebraInput.onResize();
	    
    }

}
