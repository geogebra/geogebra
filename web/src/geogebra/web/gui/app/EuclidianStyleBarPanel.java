package geogebra.web.gui.app;

import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.view.algebra.AlgebraView;
import geogebra.web.euclidian.EuclidianStyleBar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;

public class EuclidianStyleBarPanel extends Composite implements RequiresResize {

	private AbstractApplication application;

	private static EuclidianStyleBarPanelUiBinder uiBinder = GWT
	        .create(EuclidianStyleBarPanelUiBinder.class);

	interface EuclidianStyleBarPanelUiBinder extends UiBinder<SimplePanel, EuclidianStyleBarPanel> {
	}

	@UiField SimplePanel simplep;
	EuclidianStyleBar eviewsb = null;

	public EuclidianStyleBarPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		simplep.setSize("100%", "100%");
		//aview = Canvas.createIfSupported();
		//algebrapanel.add(aview);
	}

	public void setStyleBar(EuclidianStyleBar evs) {
		if (evs != eviewsb) {
			if (eviewsb != null)
				simplep.remove(eviewsb);

			simplep.add(eviewsb = evs);
		}
	}

	public SimplePanel getSimplePanel() {
	    return simplep;
    }

	public void onResize() {
	   GWT.log("resized");
    }

	public void attachApp(AbstractApplication app) {
		if (application != app) {
			application = app;
			setStyleBar((EuclidianStyleBar)application.getActiveEuclidianView().getStyleBar());
		}
	}
}
