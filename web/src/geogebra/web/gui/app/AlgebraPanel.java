package geogebra.web.gui.app;

import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.view.algebra.AlgebraView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;

public class AlgebraPanel extends Composite implements RequiresResize {

	private AbstractApplication application;

	private static AlgebraPanelUiBinder uiBinder = GWT
	        .create(AlgebraPanelUiBinder.class);

	interface AlgebraPanelUiBinder extends UiBinder<ScrollPanel, AlgebraPanel> {
	}

	@UiField ScrollPanel algebrap;
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
			if (aview != null)
				algebrap.remove(aview);

			algebrap.add(aview = av);
		}
	}

	public ScrollPanel getAbsolutePanel() {
	    return algebrap;
    }

	public void onResize() {
	   GWT.log("resized");
    }

	public void attachApp(AbstractApplication app) {
		if (application != app) {
			application = app;
			setAlgebraView((AlgebraView)application.getAlgebraView());
		}
	}
}
