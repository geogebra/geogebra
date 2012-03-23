package geogebra.web.gui.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class GGWCommandLine extends Composite {

	private static GGWCommandLineUiBinder uiBinder = GWT
	        .create(GGWCommandLineUiBinder.class);

	interface GGWCommandLineUiBinder extends UiBinder<Widget, GGWCommandLine> {
	}

	public GGWCommandLine() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
