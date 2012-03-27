package geogebra.web.gui.app;

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

public class EuclidianPanel extends Composite implements RequiresResize {

	private static EuclidianPanelUiBinder uiBinder = GWT
	        .create(EuclidianPanelUiBinder.class);

	interface EuclidianPanelUiBinder extends UiBinder<AbsolutePanel, EuclidianPanel> {
	}
	
	@UiField AbsolutePanel euclidianpanel;
	Canvas eview1 = null;

	public EuclidianPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		eview1 = Canvas.createIfSupported();	
		euclidianpanel.add(eview1);
	}

	public Canvas getCanvas() {
	    return eview1;
    }

	public AbsolutePanel getAbsolutePanel() {
	    return euclidianpanel;
    }

	public void onResize() {
	   GWT.log("resized");
    }

}
