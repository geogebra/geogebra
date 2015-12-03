package org.geogebra.web.web.export;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.awt.PrintableW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.layout.DockPanelW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PrintPreviewW extends GPopupPanel implements ClickHandler,
		ChangeHandler {
	AppW app;
	private Button btPrint;
	private Button btCancel;
	ListBox m_cbView;
	HorizontalPanel printPanel;

	public PrintPreviewW(AppW appl) {
		super(true, true, appl.getPanel());
		app = appl;
		createGUI();
		addStyleName("GeoGebraPopup");
		setGlassEnabled(true);
		center();
	}

	protected void createGUI() {
		printPanel = new HorizontalPanel();
		printPanel.setStyleName("printPanel");

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

		createPreview(m_cbView.getSelectedValue());

		setWidget(centerPanel);
	}

	native JavaScriptObject getScreenshotCallback(Element el)/*-{
		return function(pngBase64) {
			var previewImg = document.createElement("img");
			previewImg
					.setAttribute("src", "data:image/png;base64," + pngBase64);
			if (el.hasChildNodes()) {
				el.removeChild(el.lastChild);
			}
			el.appendChild(previewImg);
		};
	}-*/;

	public void onClick(ClickEvent event) {
		if (event.getSource() == btPrint) {
			hide();
			Window.print();
		}

		if (event.getSource() == btCancel) {
			hide();
		}
	}

	public void onChange(ChangeEvent event) {
		if (event.getSource() == m_cbView) {
			createPreview(m_cbView.getSelectedValue());
		}
	}

	public void createPreview(final String printableView) {

		App.debug("create preview from : " + printableView);

		app.forEachView(new App.ViewCallback() {

			public void run(int viewID, String viewName) {
				if (app.getPlain(viewName).equals(printableView)) {
					
					final Widget printables = getPrintables(viewID, app);
					printPanel.clear();
					printPanel.add(printables);
					Document.get().getBody()
							.appendChild(printPanel.getElement());

				}
			}

		});

	}

	static Widget getPrintables(int viewID, AppW app) {
		GuiManagerW gui = (GuiManagerW) app.getGuiManager();
		PrintableW view;
		if (viewID == App.VIEW_CAS) {
			view = (PrintableW) gui.getCasView();
		} else if (viewID == App.VIEW_CONSTRUCTION_PROTOCOL) {
			view = (PrintableW) app.getGuiManager()
					.getConstructionProtocolView();
		} else if (viewID == App.VIEW_SPREADSHEET) {
			view = gui.getSpreadsheetView();
		} else if (viewID == App.VIEW_EUCLIDIAN2) {
			view = app.getEuclidianView2(1);
		} else if (viewID == App.VIEW_ALGEBRA) {
			view = gui.getAlgebraView();
		} else if (viewID == App.VIEW_DATA_ANALYSIS) {
			view = (PrintableW) gui.getDataAnalysisView();
		} else {
			view = app.getEuclidianView1();
		}

		return view.getPrintable();
	}

}
