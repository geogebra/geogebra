package geogebra.web.gui.app;

import geogebra.common.main.App;
import geogebra.web.euclidian.event.HasOffsets;
import geogebra.web.gui.view.algebra.AlgebraView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;

public class AlgebraPanel extends Composite implements RequiresResize {

	private App application;

	private static AlgebraPanelUiBinder uiBinder = GWT
	        .create(AlgebraPanelUiBinder.class);

	interface AlgebraPanelUiBinder extends UiBinder<ScrollPanel, AlgebraPanel> {
	}

	@UiField ScrollPanel algebrap;
	SimplePanel simplep;
	AlgebraView aview = null;

	public AlgebraPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		algebrap.setSize("100%", "100%");
		algebrap.setAlwaysShowScrollBars(false);
		//aview = Canvas.createIfSupported();
		//algebrapanel.add(aview);
	}

	public void setAlgebraView(AlgebraView av) {
		if (av != aview) {
			if (aview != null && simplep != null) {
				simplep.remove(aview);
				algebrap.remove(simplep);
			}

			simplep = new SimplePanel(aview = av);
			algebrap.add(simplep);
		}
	}

	public ScrollPanel getAbsolutePanel() {
	    return algebrap;
    }

	public void onResize() {
		if(application.getActiveEuclidianView().getEuclidianController() instanceof HasOffsets)
			((HasOffsets)application.getActiveEuclidianView().getEuclidianController()).updateOffsets();
		App.debug("resized");
    }

	public void attachApp(App app) {
		if (application != app) {
			application = app;
			setAlgebraView((AlgebraView)application.getAlgebraView());
		}
	}
}
