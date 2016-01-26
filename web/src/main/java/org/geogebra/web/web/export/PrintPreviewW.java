package org.geogebra.web.web.export;

import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.awt.PrintableW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.layout.DockPanelW;

import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PrintPreviewW extends GPopupPanel implements ClickHandler,
		ChangeHandler {
	AppW app;
	private Button btPrint;
	private Button btCancel;
	ListBox m_cbView;
	FlowPanel printPanel;
	private SimplePanel scalePanelHolder;
	/**
	 * printable width at portrait orientation
	 */
	public static int PWIDTH = 900;
	/**
	 * printable height at portrait orientation
	 */
	public static int PHEIGHT = 1400;
	/**
	 * printable width at landscape orientation
	 */
	public static int LWIDTH = 1200;
	/**
	 * printable height at landscape orientation
	 */
	public static int LHEIGHT = 900;


	public PrintPreviewW(AppW appl) {
		super(true, true, appl.getPanel());
		app = appl;
		createGUI();
		addStyleName("GeoGebraPopup");
		setGlassEnabled(true);
		center();
	}

	protected void createGUI() {
		printPanel = new FlowPanel();
		printPanel.setStyleName("printPanel");
		RootPanel.get().add(printPanel);

		VerticalPanel centerPanel = new VerticalPanel();

		btPrint = new Button(app.getPlain("Print"));
		btPrint.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btPrint.addClickHandler(this);
		btPrint.setEnabled(false);

		btCancel = new Button(app.getPlain("Cancel"));
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btCancel.addClickHandler(this);

		m_cbView = new ListBox();

		app.forEachView(new App.ViewCallback() {
			public void run(int viewID, String viewName) {
				m_cbView.addItem(app.getPlain(viewName), viewID + "");
			}
		});

		DockPanelW focusedPanel = ((GuiManagerW) app.getGuiManager())
				.getLayout().getDockManager().getFocusedPanel();
		if (focusedPanel == null) {
			m_cbView.setItemSelected(0, true); // setSelectedItem(app.getPlain("AllViews"));
		} else {
			String title = app.getPlain(focusedPanel.getViewTitle());
			int index = m_cbView.getItemCount() - 1;
			while (!m_cbView.getValue(index).equals(title) && index != 0) {
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
		scalePanelHolder = new SimplePanel();
		centerPanel.add(scalePanelHolder);

		createPreview(m_cbView.getSelectedValue());

		setWidget(centerPanel);
	}

	public void onClick(ClickEvent event) {
		if (event.getSource() == btPrint || event.getSource() == btCancel) {
			hide();
			if (event.getSource() == btPrint) {
				Window.print();
			}
			NodeList<com.google.gwt.dom.client.Element> pp = Dom
					.getElementsByClassName("printPanel");
			if (pp.getLength() != 0) {
				pp.getItem(0).removeFromParent();
			}
			
			Dom.getElementsByClassName("printableView").getItem(0)
					.removeClassName("printableView");
		}

	}

	public void onChange(ChangeEvent event) {
		if (event.getSource() == m_cbView) {
			scalePanelHolder.clear();
			if ((app.VIEW_EUCLIDIAN + "").equals(m_cbView.getSelectedValue())
					|| (app.VIEW_EUCLIDIAN2 + "").equals(m_cbView
							.getSelectedValue())) {
				scalePanelHolder.add(new PrintScalePanelW(app, null));
			}

			createPreview(m_cbView.getSelectedValue());
		}
	}

	public void createPreview(final String viewID) {

		createPrintables(Integer.parseInt(viewID), app, printPanel, btPrint);

	}

	static void createPrintables(int viewID, AppW app, FlowPanel pPanel,
			Button bPrint) {
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

		view.getPrintable(pPanel, bPrint);
	}

}
