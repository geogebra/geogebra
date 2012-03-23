package geogebra.web.gui.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class GGWViewWrapper extends Composite {

	private static GGWViewWrapperUiBinder uiBinder = GWT
	        .create(GGWViewWrapperUiBinder.class);

	interface GGWViewWrapperUiBinder extends UiBinder<Widget, GGWViewWrapper> {
	}

	public GGWViewWrapper() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
