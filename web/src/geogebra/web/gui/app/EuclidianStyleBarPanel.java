package geogebra.web.gui.app;

import geogebra.common.main.App;
import geogebra.web.euclidian.EuclidianStyleBarW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;

public class EuclidianStyleBarPanel extends Composite implements RequiresResize {

	private App application;

	private static EuclidianStyleBarPanelUiBinder uiBinder = GWT
	        .create(EuclidianStyleBarPanelUiBinder.class);

	interface EuclidianStyleBarPanelUiBinder extends UiBinder<AbsolutePanel, EuclidianStyleBarPanel> {
	}

	@UiField AbsolutePanel simplep;
	EuclidianStyleBarW eviewsb = null;

	public EuclidianStyleBarPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		simplep.setSize("100%", "100%");
		simplep.getElement().getStyle().setBackgroundColor("#eeeeee");
		simplep.getElement().getStyle().setRight(1, Style.Unit.PX);
		//aview = Canvas.createIfSupported();
		//algebrapanel.add(aview);
	}

	public void setStyleBar(EuclidianStyleBarW evs) {
		if (evs != eviewsb) {
			if (eviewsb != null)
				simplep.remove(eviewsb);

			simplep.add(eviewsb = evs);
		}
	}

	public AbsolutePanel getSimplePanel() {
	    return simplep;
    }

	public void onResize() {
		App.debug("resized");
    }

	public void attachApp(App app) {
		if (application != app) {
			application = app;
			setStyleBar((EuclidianStyleBarW)application.getActiveEuclidianView().getStyleBar());
		}
	}
}
