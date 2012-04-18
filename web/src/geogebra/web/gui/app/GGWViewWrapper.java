package geogebra.web.gui.app;

import geogebra.common.main.AbstractApplication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class GGWViewWrapper extends Composite {

	private AbstractApplication application;

	private static GGWViewWrapperUiBinder uiBinder = GWT
	        .create(GGWViewWrapperUiBinder.class);

	interface GGWViewWrapperUiBinder extends UiBinder<AlgebraPanel, GGWViewWrapper> {
	}

	@UiField AlgebraPanel algebrapanel;

	public GGWViewWrapper() {
		initWidget(uiBinder.createAndBindUi(this));
		algebrapanel.setSize("100%", "100%");
	}

	public void attachApp(AbstractApplication app) {
		application = app;
		algebrapanel.attachApp(app);
	}
}
