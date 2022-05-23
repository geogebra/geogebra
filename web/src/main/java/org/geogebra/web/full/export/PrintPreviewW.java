package org.geogebra.web.full.export;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.html5.awt.PrintableW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.StyleInjector;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCollection;

/**
 * Print dialog
 */
public class PrintPreviewW extends ComponentDialog implements ChangeHandler {
	/** view list */
	private ListBox cbView;
	/** print panel */
	private FlowPanel printPanel;
	private SimplePanel scalePanelHolder;
	private Localization loc;

	/**
	 * @param app application
	 */
	public PrintPreviewW(AppW app, DialogData data) {
		super(app, data, false, true);
		this.loc = app.getLocalization();
		createGUI();
		setOnPositiveAction(this::onPositiveButtonAction);
		addStyleName("printPreview");
	}

	private void createGUI() {
		new StyleInjector(GWT.getModuleBaseURL()).inject("css", "print");
		// Maybe there is older print panel, because after open pdf in preview
		// the previous print panel hasn't been removed
		removePrintPanelFromDOM();

		printPanel = new FlowPanel();
		printPanel.setStyleName("printPanel");
		RootPanel.get().add(printPanel);

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

		DockPanelW focusedPanel = ((GuiManagerW) app.getGuiManager())
				.getLayout().getDockManager().getFocusedPanel();
		if (focusedPanel == null) {
			cbView.setItemSelected(0, true);
		} else {
			String id = String.valueOf(focusedPanel.getViewId());
			int index = cbView.getItemCount() - 1;
			while (!cbView.getValue(index).equals(id) && index != 0) {
				index--;
			}

			cbView.setItemSelected(index, true);
		}
		cbView.addChangeHandler(this);

		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(cbView);
		scalePanelHolder = new SimplePanel();
		mainPanel.add(scalePanelHolder);

		setPosBtnDisabled(true);
		addScalePanelOrCreatePreview();

		this.addCloseHandler(event -> onPreviewClose());
		setDialogContent(mainPanel);
	}

	/**
	 * Dialog close callback
	 */
	protected void onPreviewClose() {
		app.setDefaultCursor();
		app.closePopups();
	}

	private void onPositiveButtonAction() {
		if ((cbView.getSelectedValue()
				.equals(App.VIEW_EUCLIDIAN + ""))
				|| (cbView.getSelectedValue()
				.equals(App.VIEW_EUCLIDIAN2 + ""))) {
			createPreview(cbView.getSelectedValue());
		} else {
			DomGlobal.window.print();
			removePrintPanelFromDOM();
		}
	}

	private void addScalePanelOrCreatePreview() {
		AppW appw = (AppW) app;
		if ((App.VIEW_EUCLIDIAN + "").equals(cbView.getSelectedValue())) {
			scalePanelHolder.add(new PrintScalePanelW(appw, app
					.getEuclidianView1()));
			setPosBtnDisabled(false);
		} else if ((App.VIEW_EUCLIDIAN2 + "").equals(cbView
				.getSelectedValue())) {
			scalePanelHolder
					.add(new PrintScalePanelW(appw, app
							.getEuclidianView2(1)));
			setPosBtnDisabled(false);
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
				() -> setPosBtnDisabled(false));
	}

	private void createPrintables(int viewID, AppW app, FlowPanel pPanel,
			Runnable enablePrintBtn) {
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

		view.getPrintable(pPanel, enablePrintBtn) ;
	}

	private static void removePrintPanelFromDOM() {
		HTMLCollection<elemental2.dom.Element> pp = Dom
				.getElementsByClassName("printPanel");
		if (pp.getLength() != 0) {
			pp.getAt(0).remove();
		}
	}
}