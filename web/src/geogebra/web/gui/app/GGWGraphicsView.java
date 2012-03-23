package geogebra.web.gui.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class GGWGraphicsView extends Composite {

	private static GGWGraphicsViewUiBinder uiBinder = GWT
	        .create(GGWGraphicsViewUiBinder.class);

	interface GGWGraphicsViewUiBinder extends UiBinder<Widget, GGWGraphicsView> {
	}

	public GGWGraphicsView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
