package org.geogebra.web.full.export;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.html5.awt.PrintableW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.StyleInjector;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RootPanel;
import org.gwtproject.user.client.ui.SimplePanel;

import com.google.gwt.core.client.GWT;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCollection;

/**
 * Print dialog
 */
public class PrintPreviewW extends ComponentDialog {
	private ComponentDropDown viewDropDown;
	private FlowPanel printPanel;
	private SimplePanel scalePanelHolder;
	private final Localization loc;
	private final List<String> keys = new ArrayList<>();
	private final List<Integer> indices = new ArrayList<>();

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

		initKeysAndIndices();
		viewDropDown = new ComponentDropDown((AppW) app, null, keys, getFocusedPanelIdx());
		viewDropDown.setFullWidth(true);
		viewDropDown.addChangeHandler(() -> {
			scalePanelHolder.clear();
			addScalePanelOrCreatePreview();
		});

		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(viewDropDown);
		scalePanelHolder = new SimplePanel();
		mainPanel.add(scalePanelHolder);

		setPosBtnDisabled(true);
		addScalePanelOrCreatePreview();

		this.addCloseHandler(event -> onPreviewClose());
		setDialogContent(mainPanel);
	}

	private void initKeysAndIndices() {
		if (app.getGuiManager().showView(App.VIEW_EUCLIDIAN)) {
			keys.add(loc.getMenu("DrawingPad"));
			indices.add(App.VIEW_EUCLIDIAN);
		}
		if (app.getGuiManager().showView(App.VIEW_EUCLIDIAN2)) {
			keys.add(loc.getMenu("DrawingPad2"));
			indices.add(App.VIEW_EUCLIDIAN2);
		}
		if (app.getGuiManager().showView(App.VIEW_ALGEBRA)) {
			keys.add(loc.getMenu("AlgebraWindow"));
			indices.add(App.VIEW_ALGEBRA);
		}
		if (app.getGuiManager().showView(App.VIEW_CONSTRUCTION_PROTOCOL)) {
			keys.add(loc.getMenu("ConstructionProtocol"));
			indices.add(App.VIEW_CONSTRUCTION_PROTOCOL);
		}
	}

	private int getFocusedPanelIdx() {
		DockPanelW focusedPanel = ((GuiManagerW) app.getGuiManager())
				.getLayout().getDockManager().getFocusedPanel();
		if (focusedPanel == null) {
			return 0;
		} else {
			int id = focusedPanel.getViewId();
			return indices.lastIndexOf(id);
		}
	}

	/**
	 * Dialog close callback
	 */
	protected void onPreviewClose() {
		app.setDefaultCursor();
		app.closePopups();
	}

	private void onPositiveButtonAction() {
		int selectedValue = indices.get(viewDropDown.getSelectedIndex());
		if (selectedValue == App.VIEW_EUCLIDIAN
				|| selectedValue == App.VIEW_EUCLIDIAN2) {
			createPreview(selectedValue + "");
		} else {
			DomGlobal.window.print();
			removePrintPanelFromDOM();
		}
	}

	private void addScalePanelOrCreatePreview() {
		int selectedValue = indices.get(viewDropDown.getSelectedIndex());
		AppW appw = (AppW) app;
		if (App.VIEW_EUCLIDIAN == selectedValue) {
			scalePanelHolder.add(new PrintScalePanelW(appw, app
					.getEuclidianView1()));
			setPosBtnDisabled(false);
		} else if (App.VIEW_EUCLIDIAN2 == selectedValue) {
			scalePanelHolder
					.add(new PrintScalePanelW(appw, app
							.getEuclidianView2(1)));
			setPosBtnDisabled(false);
		} else {
			createPreview(selectedValue + "");
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