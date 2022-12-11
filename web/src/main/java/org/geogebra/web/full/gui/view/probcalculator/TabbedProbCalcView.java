package org.geogebra.web.full.gui.view.probcalculator;

import org.geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import org.geogebra.common.main.App;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.Style;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.ClickHandler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.TabLayoutPanel;

public class TabbedProbCalcView extends ProbabilityCalculatorViewW {
	private final MyTabLayoutPanel tabbedPane;
	protected final StatisticsCalculatorW statCalculator;
	protected FlowPanel plotSplitPane;
	protected FlowPanel mainSplitPane;
	private Label lblMeanSigma;
	private static final int CONTROL_PANEL_HEIGHT = 180;
	private static final int TABLE_PADDING_AND_SCROLLBAR = 32;
	private static final int DEFAULT_MENU_WIDTH = 208;
	private static final int BTN_SIZE = 36; // includes margin

	/**
	 * Creates new probability calculator view
	 * @param app application
	 */
	public TabbedProbCalcView(AppW app) {
		super(app);
		//table panel
		setTable(new ProbabilityTableW(app, this));
		buildButtons();
		buildProbCalcPanel();
		isIniting = false;
		statCalculator = new StatisticsCalculatorW(app);
		tabbedPane = new MyTabLayoutPanel();
		tabbedPane.add(probCalcPanel, loc.getMenu("Distribution"));
		tabbedPane.add(statCalculator.getWrappedPanel(),
				loc.getMenu("Statistics"));

		tabbedPane.onResize();
		tabbedPane.selectTab(getApp().getSettings().getProbCalcSettings()
				.getCollection().isActive() ? 1 : 0);
		init();
	}

	private void buildButtons() {
		lblMeanSigma = new Label();
		lblMeanSigma.addStyleName("lblMeanSigma");
		plotPanelOptions.add(lblMeanSigma);
		if (!getApp().isExam()) {
			ToggleButton btnExport = createExportMenu();
			btnExport.addStyleName("probCalcStylbarBtn");
			plotPanelOptions.add(btnExport);
		}
	}

	private ToggleButton createExportMenu() {
		GPopupMenuW menuExport = new GPopupMenuW((AppW) app, true);
		menuExport.getPopupMenu().addStyleName("probCalcStylbarBtn");

		if (!getApp().isApplet()) {
			addExportItem(menuExport, "CopyToGraphics", exportToEVAction);
		}
		if (((AppW) app).getLAF().copyToClipboardSupported()) {
			addExportItem(menuExport, "ExportAsPicture", this::showExportDialog);
		}
		ToggleButton btnExport = new ToggleButton(MaterialDesignResources.INSTANCE
				.prob_calc_export());
		btnExport.addFastClickHandler(e -> {
			if (menuExport.isMenuShown()) {
				menuExport.hide();
			} else {
				menuExport.show(btnExport, BTN_SIZE - DEFAULT_MENU_WIDTH, BTN_SIZE);
			}
		});
		menuExport.getPopupPanel().addAutoHidePartner(btnExport.getElement());
		menuExport.getPopupPanel().addCloseHandler(i -> btnExport.setSelected(false));
		return btnExport;
	}

	private void showExportDialog() {
		String url = getPlotPanel().getExportImageDataUrl(3, true, false);
		((AppW) getApp()).getFileManager().showExportAsPictureDialog(url,
				getApp().getExportTitle(), "png", "ExportAsPicture", getApp());
	}

	private void addExportItem(GPopupMenuW exportMenu, String title,
			Scheduler.ScheduledCommand copyCmd) {
		AriaMenuItem item = new AriaMenuItem(loc.getMenu(title), false, copyCmd);
		item.addStyleName("no-image");
		exportMenu.addItem(item);
	}

	private class MyTabLayoutPanel extends TabLayoutPanel implements ClickHandler {

		public MyTabLayoutPanel() {
			super(30, Style.Unit.PX);
			this.addDomHandler(this, ClickEvent.getType());
		}

		@Override
		public final void onResize() {
			tabResized();
		}

		@Override
		public void onClick(ClickEvent event) {
			getApp().setActiveView(App.VIEW_PROBABILITY_CALCULATOR);
		}
	}

	@Override
	public void tabResized() {
		int tableWidth = isDiscreteProbability() ? ((ProbabilityTableW) getTable()).getStatTable()
				.getTable().getOffsetWidth() + TABLE_PADDING_AND_SCROLLBAR : 0;
		int width = mainSplitPane.getOffsetWidth()
				- tableWidth
				- 5;
		int height = probCalcPanel.getOffsetHeight() - 20;
		if (width > 0) {
			resizePlotPanel(width, height - CONTROL_PANEL_HEIGHT);
			plotSplitPane.setWidth(width + "px");
		}

		if (height > 0 && isDiscreteProbability()) {
			((ProbabilityTableW) getTable()).getWrappedPanel()
					.setPixelSize(tableWidth, height);
		}
	}

	/**
	 * @return whether distribution tab is open
	 */
	@Override
	public boolean isDistributionTabOpen() {
		return tabbedPane.getSelectedIndex() == 0;
	}

	@Override
	public void setLabels() {
		super.setLabels();
		statCalculator.setLabels();
		tabbedPane.setTabText(0, loc.getMenu("Distribution"));
		tabbedPane.setTabText(1, loc.getMenu("Statistics"));
	}

	@Override
	public TabLayoutPanel getWrapperPanel() {
		return tabbedPane;
	}

	@Override
	public StatisticsCalculator getStatCalculator() {
		return statCalculator;
	}

	protected void buildProbCalcPanel() {
		DistributionPanel distrPanel = new DistributionPanel(this, loc);
		distrPanel.addStyleName("distrPanelClassic");
		setDistributionPanel(distrPanel);
		plotSplitPane = new FlowPanel();
		plotSplitPane.add(plotPanelPlus);
		plotSplitPane.add(distrPanel);
		plotSplitPane.addStyleName("plotSplitPane");
		mainSplitPane = new FlowPanel();
		mainSplitPane.addStyleName("mainSplitPanel");
		mainSplitPane.add(plotSplitPane);

		probCalcPanel = new FlowPanel();
		probCalcPanel.addStyleName("ProbCalcPanel");
		probCalcPanel.add(mainSplitPane);
	}

	@Override
	protected void addRemoveTable(boolean showTable) {
		FlowPanel tablePanel = ((ProbabilityTableW) getTable()).getWrappedPanel();
		if (showTable) {
			mainSplitPane.add(tablePanel);
		} else {
			mainSplitPane.remove(tablePanel);
		}
		tabResized();
	}

	@Override
	protected void onDistributionUpdate() {
		super.onDistributionUpdate();
		lblMeanSigma.setText(getMeanSigma());
	}
}
