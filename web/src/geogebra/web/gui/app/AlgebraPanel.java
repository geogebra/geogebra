package geogebra.web.gui.app;

import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.view.algebra.AlgebraView;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import com.google.gwt.dom.client.Style;

public class AlgebraPanel extends Composite implements RequiresResize {

	private AbstractApplication application;

	private static AlgebraPanelUiBinder uiBinder = GWT
	        .create(AlgebraPanelUiBinder.class);

	interface AlgebraPanelUiBinder extends UiBinder<AbsolutePanel, AlgebraPanel> {
	}

	@UiField AbsolutePanel algebrap;
	AlgebraView aview = null;

	public AlgebraPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		algebrap.setSize("100%", "100%");
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

	public AbsolutePanel getAbsolutePanel() {
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
