package org.geogebra.web.full.export;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.html5.awt.PrintableW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.resources.StyleInjector;
import org.geogebra.web.shared.DialogBoxW;

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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Print dialog
 *
 */
public class PrintPreviewW extends DialogBoxW implements ClickHandler,
		ChangeHandler {
	private Button btPrint;
	private Button btCancel;
	/** view list */
	private ListBox cbView;
	/** print panel */
	private FlowPanel printPanel;
	private SimplePanel scalePanelHolder;
	private Localization loc;
	/**
	 * printable width at portrait orientation
	 */
	public static final int PWIDTH = 900;
	/**
	 * printable height at portrait orientation
	 */
	public static final int PHEIGHT = 1400;
	/**
	 * printable width at landscape orientation
	 */
	public static final int LWIDTH = 1200;
	/**
	 * printable height at landscape orientation
	 */
	public static final int LHEIGHT = 900;

	/**
	 * @param appl application
	 */
	public PrintPreviewW(AppW appl) {
		super(appl.getPanel(), appl);
		app = appl;
		this.loc = app.getLocalization();
		createGUI();
		addStyleName("GeoGebraPopup");
		if (app.isWhiteboardActive()) {
			addStyleName("mowPrintPrew");
		}
		setGlassEnabled(true);
		getCaption().setText(loc.getMenu("PrintPreview"));
		center();
	}

	private void createGUI() {
		StyleInjector.inject(GuiResources.INSTANCE.printStyle());
		// Maybe there is older print panel, because after open pdf in preview
		// the previous print panel hasn't been removed
		removePrintPanelFromDOM();

		printPanel = new FlowPanel();
		printPanel.setStyleName("printPanel");
		RootPanel.get().add(printPanel);

		FlowPanel centerPanel = new FlowPanel();

		btPrint = new Button(loc.getMenu("Print"));
		btPrint.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btPrint.addClickHandler(this);
		btPrint.setEnabled(false);

		btCancel = new Button(loc.getMenu("Cancel"));
		btCancel.addStyleName("cancelBtn");
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btCancel.addClickHandler(this);

		cbView = new ListBox();

		// We can print EVs yet
		if (app.getGuiManager().showView(App.VIEW_EUCLIDIAN)) {
			cbView.addItem(loc.getMenu("DrawingPad"), App.VIEW_EUCLIDIAN
					+ "");
		}
		if (app.getGuiManager().showView(App.VIEW_EUCLIDIAN2)) {
			cbView.addItem(loc.getMenu("DrawingPad2"), App.VIEW_EUCLIDIAN2
					+ "");
		}

		if (app.getGuiManager().showView(App.VIEW_ALGEBRA)) {
			cbView.addItem(loc.getMenu("AlgebraWindow"), App.VIEW_ALGEBRA
					+ "");
		}

		if (app.getGuiManager().showView(App.VIEW_CONSTRUCTION_PROTOCOL)) {
			cbView.addItem(loc.getMenu("ConstructionProtocol"),
					App.VIEW_CONSTRUCTION_PROTOCOL + "");
		}

		if (cbView.getItemCount() == 0) {
			this.setVisible(false);
			this.hide(false);
		}

		if (cbView.getItemCount() != 0) {

			DockPanelW focusedPanel = ((GuiManagerW) app.getGuiManager())
					.getLayout().getDockManager().getFocusedPanel();
			if (focusedPanel == null) {
				cbView.setItemSelected(0, true);
			} else {
				String title = loc.getMenu(focusedPanel.getViewTitle());
				int index = cbView.getItemCount() - 1;
				while (!cbView.getValue(index).equals(title) && index != 0) {
					index--;
				}

				cbView.setItemSelected(index, true);
			}

			cbView.addChangeHandler(this);

			FlowPanel buttonPanel = new FlowPanel();
			buttonPanel.addStyleName("DialogButtonPanel");

			centerPanel.add(cbView);
			scalePanelHolder = new SimplePanel();
			centerPanel.add(scalePanelHolder);
			buttonPanel.add(btPrint);
			buttonPanel.add(btCancel);
			centerPanel.add(buttonPanel);

			addScalePanelOrCreatePreview();

		} else {
			centerPanel.add(btCancel);
		}

		this.addCloseHandler(event -> onPreviewClose());

		add(centerPanel);
	}

	/**
	 * Dialog close callback
	 */
	protected void onPreviewClose() {
		app.setDefaultCursor();
		app.closePopups();
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == btPrint || event.getSource() == btCancel) {
			hide();
			if (event.getSource() == btPrint) {
				if ((cbView.getSelectedValue()
						.equals(App.VIEW_EUCLIDIAN + ""))
						|| (cbView.getSelectedValue()
						.equals(App.VIEW_EUCLIDIAN2 + ""))) {
					Log.debug("print EV");
					createPreview(cbView.getSelectedValue());
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
		AppW appw = (AppW) app;
		if ((App.VIEW_EUCLIDIAN + "").equals(cbView.getSelectedValue())) {
			scalePanelHolder.add(new PrintScalePanelW(appw, app
					.getEuclidianView1()));
			btPrint.setEnabled(true);
		} else if ((App.VIEW_EUCLIDIAN2 + "").equals(cbView
				.getSelectedValue())) {
			scalePanelHolder
					.add(new PrintScalePanelW(appw, app
							.getEuclidianView2(1)));
			btPrint.setEnabled(true);
		} else {
			createPreview(cbView.getSelectedValue());
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == cbView) {
			scalePanelHolder.clear();
			addScalePanelOrCreatePreview();
		}
	}

	private void createPreview(final String viewID) {
		createPrintables(Integer.parseInt(viewID), (AppW) app, printPanel,
				btPrint);
	}

	private static void createPrintables(int viewID, AppW app, FlowPanel pPanel,
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

	private static void removePrintPanelFromDOM() {
		NodeList<Element> pp = Dom
				.getElementsByClassName("printPanel");
		if (pp.getLength() != 0) {
			pp.getItem(0).removeFromParent();
		}
	}
}