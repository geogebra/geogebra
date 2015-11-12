package org.geogebra.web.web.export;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.view.consprotocol.ConstructionProtocolViewW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PrintPreviewW extends GPopupPanel implements ClickHandler {
	AppW app;
	private Button btPrint;
	private Button btCancel;

	public PrintPreviewW(AppW appl) {
		super(true, true, appl.getPanel());
		app = appl;
		createGUI();
		addStyleName("GeoGebraPopup");
		setGlassEnabled(true);
		center();
	}

	protected void createGUI() {
		VerticalPanel centerPanel = new VerticalPanel();

		btPrint = new Button(app.getPlain("Print"));
		btPrint.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btPrint.addClickHandler(this);

		btCancel = new Button(app.getPlain("Cancel"));
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btCancel.addClickHandler(this);

		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(btPrint);
		buttonPanel.add(btCancel);
		centerPanel.add(buttonPanel);

		centerPanel.add(((ConstructionProtocolViewW) app.getGuiManager()
				.getConstructionProtocolView()).getCpPanel());


		setWidget(centerPanel);
	}

	public void onClick(ClickEvent event) {
		if (event.getSource() == btPrint) {
			Window.print();
		}

		if (event.getSource() == btCancel) {
			hide();
		}

	}

}
