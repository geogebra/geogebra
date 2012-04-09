package geogebra.web.gui.app;

import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.inputbar.AlgebraInput;
import geogebra.web.main.Application;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class GGWCommandLine extends Composite {
	
	@UiField AlgebraInput algebraInput;

	private static GGWCommandLineUiBinder uiBinder = GWT
	        .create(GGWCommandLineUiBinder.class);

	interface GGWCommandLineUiBinder extends UiBinder<Widget, GGWCommandLine> {
	}

	public GGWCommandLine() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void attachApp(AbstractApplication app) {
	    algebraInput.init((Application) app);
    }

}
