package geogebra.web.gui.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class GGWToolBar extends Composite {

	private static GGWToolBarUiBinder uiBinder = GWT
	        .create(GGWToolBarUiBinder.class);

	interface GGWToolBarUiBinder extends UiBinder<Widget, GGWToolBar> {
	}

	public GGWToolBar() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
