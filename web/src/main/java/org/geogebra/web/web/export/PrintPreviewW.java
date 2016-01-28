package org.geogebra.web.web.export;

import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.awt.PrintableW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.layout.DockPanelW;

import com.google.gwt.dom.client.Element;
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

		// Maybe there is older print panel, because after open pdf in preview
		// the previous print panel hasn't been removed
		removePrintPanelFromDOM();

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

		// app.forEachView(new App.ViewCallback() {
		// public void run(int viewID, String viewName) {
		// m_cbView.addItem(app.getPlain(viewName), viewID + "");
		// }
		// });


		// We can print EVs yet
		if (app.getGuiManager().showView(App.VIEW_EUCLIDIAN)) {
			m_cbView.addItem(app.getPlain("DrawingPad"), App.VIEW_EUCLIDIAN
					+ "");
		}
		if (app.getGuiManager().showView(App.VIEW_EUCLIDIAN2)) {
			m_cbView.addItem(app.getPlain("DrawingPad2"), App.VIEW_EUCLIDIAN2
					+ "");
		}

		if (m_cbView.getItemCount() == 0) {
			this.setVisible(false);
			this.hide(false);
		}

		if (m_cbView.getItemCount() != 0) {

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

			HorizontalPanel buttonPanel = new HorizontalPanel();
			buttonPanel.setStyleName("printPopupButtonPanel");

			centerPanel.add(m_cbView);
			scalePanelHolder = new SimplePanel();
			centerPanel.add(scalePanelHolder);
			buttonPanel.add(btPrint);
			buttonPanel.add(btCancel);
			centerPanel.add(buttonPanel);

			// if (!((m_cbView.getSelectedValue().equals(App.VIEW_EUCLIDIAN +
			// ""))
			// || (m_cbView
			// .getSelectedValue().equals(App.VIEW_EUCLIDIAN2 + "")))) {
			// createPreview(m_cbView.getSelectedValue());
			// }

			addScalePanelOrCreatePreview();

		} else {
			centerPanel.add(btCancel);
		}

		setWidget(centerPanel);
	}

	public void onClick(ClickEvent event) {
		if (event.getSource() == btPrint || event.getSource() == btCancel) {
			hide();
			if (event.getSource() == btPrint) {
				if ((m_cbView.getSelectedValue()
						.equals(App.VIEW_EUCLIDIAN + ""))
						|| (m_cbView.getSelectedValue()
								.equals(App.VIEW_EUCLIDIAN2 + ""))) {
					Log.debug("print EV");
					createPreview(m_cbView.getSelectedValue());
				} else {
					Window.print();
					removePrintPanelFromDOM();
				}
			}

			
			NodeList<Element> pw = Dom.getElementsByClassName("printableView");
			if (pw.getLength() != 0) {
				pw.getItem(0).removeClassName("printableView");
			}
		}

	}

	private void addScalePanelOrCreatePreview() {
		if ((App.VIEW_EUCLIDIAN + "").equals(m_cbView.getSelectedValue())) {
			app.getEuclidianView1();
			scalePanelHolder.add(new PrintScalePanelW(app, app
					.getEuclidianView1()));
			btPrint.setEnabled(true);
		} else if ((App.VIEW_EUCLIDIAN2 + "").equals(m_cbView
				.getSelectedValue())) {
			scalePanelHolder.add(new PrintScalePanelW(app, app
					.getEuclidianView2(1)));
			btPrint.setEnabled(true);
		} else {
			createPreview(m_cbView.getSelectedValue());
		}
	}

	public void onChange(ChangeEvent event) {
		if (event.getSource() == m_cbView) {
			scalePanelHolder.clear();
			addScalePanelOrCreatePreview();

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

	public static void removePrintPanelFromDOM() {
		NodeList<com.google.gwt.dom.client.Element> pp = Dom
				.getElementsByClassName("printPanel");
		if (pp.getLength() != 0) {
			pp.getItem(0).removeFromParent();
		}
	}

}
