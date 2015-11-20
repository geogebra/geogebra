package org.geogebra.web.web.export;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.layout.DockPanelW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PrintPreviewW extends GPopupPanel implements ClickHandler,
		ChangeHandler {
	AppW app;
	private Button btPrint;
	private Button btCancel;
	ListBox m_cbView;
	SimplePanel printPanel;

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

		m_cbView = new ListBox();

		app.forEachView(new App.ViewCallback() {
			public void run(int viewID, String viewName) {
				m_cbView.addItem(app.getPlain(viewName));
			}
		});

		DockPanelW focusedPanel = ((GuiManagerW) app.getGuiManager())
				.getLayout().getDockManager().getFocusedPanel();
		if (focusedPanel == null) {
			m_cbView.setItemSelected(0, true); // setSelectedItem(app.getPlain("AllViews"));
		} else {
			String title = app.getPlain(focusedPanel.getViewTitle());
			int index = m_cbView.getItemCount() - 1;
			while (!m_cbView.getItemText(index).equals(title) && index != 0) {
				index--;
			}

			m_cbView.setItemSelected(index, true);
		}

		m_cbView.addChangeHandler(this);

		App.debug("selected index: " + m_cbView.getSelectedIndex());

		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setStyleName("printPopupButtonPanel");
		buttonPanel.add(btPrint);
		buttonPanel.add(btCancel);
		buttonPanel.add(m_cbView);
		centerPanel.add(buttonPanel);

		printPanel = new SimplePanel();
		printPanel.setStyleName("printPanel");

		// app.getGgbApi().getScreenshotURL(
		// ((ConstructionProtocolViewW) app.getGuiManager()
		// .getConstructionProtocolView()).getCpPanel()
		// .getElement(),
		// getScreenshotCallback(printPanel.getElement()));

		centerPanel.add(printPanel);
		createPreview(m_cbView);

		setWidget(centerPanel);
	}

	private native JavaScriptObject getScreenshotCallback(Element el)/*-{
		return function(pngBase64) {
			var previewImg = document.createElement("img");
			previewImg
					.setAttribute("src", "data:image/png;base64," + pngBase64);
			el.appendChild(previewImg);
		};
	}-*/;

	public void onClick(ClickEvent event) {
		if (event.getSource() == btPrint) {
			Window.print();
		}

		if (event.getSource() == btCancel) {
			hide();
		}
	}

	public void onChange(ChangeEvent event) {
		if (event.getSource() == m_cbView) {
			createPreview(m_cbView);
		}
	}

	public void createPreview(final ListBox list) {
		app.forEachView(new App.ViewCallback() {

			public void run(int viewID, String viewName) {
				if (app.getPlain(viewName).equals(list.getSelectedValue())) {
					printPanel.clear();
					printPanel.add(((PrintableW) app.getView(viewID))
							.getPrintable());
				}
			}

		});

	}

}
