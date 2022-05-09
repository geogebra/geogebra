package org.geogebra.web.full.gui.view.probcalculator;

import org.geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import org.geogebra.common.main.App;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class TabbedProbCalcView extends ProbabilityCalculatorViewW {
	private final MyTabLayoutPanel tabbedPane;
	protected final StatisticsCalculatorW statCalculator;
	protected FlowPanel plotSplitPane;
	protected FlowPanel mainSplitPane;
	private Label lblMeanSigma;
	private static final int CONTROL_PANEL_HEIGHT = 180;
	private static final int TABLE_PADDING_AND_SCROLLBAR = 32;

	/**
	 * @param app creates new probabilitycalculatorView
	 */
	public TabbedProbCalcView(AppW app) {
		super(app);
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
		GPopupMenuW btnExport = createExportMenu();
		lblMeanSigma = new Label();
		lblMeanSigma.addStyleName("lblMeanSigma");
		plotPanelOptions.add(lblMeanSigma);
		if (!getApp().isExam() && app.getConfig().getAppCode().equals("classic")) {
			plotPanelOptions.add(btnExport.getPopupMenu());
		}
	}

	private GPopupMenuW createExportMenu() {
		GPopupMenuW btnExport = new GPopupMenuW((AppW) app, true) {
			@Override
			public int getPopupLeft() {
				return getPopupMenu().getAbsoluteLeft();
			}
		};
		btnExport.getPopupMenu().addStyleName("probCalcStylbarBtn");

		AriaMenuBar menu = new AriaMenuBar();

		if (!getApp().isApplet()) {
			AriaMenuItem miToGraphich = new AriaMenuItem(
					loc.getMenu("CopyToGraphics"), false,
					() -> exportToEVAction.execute());

			menu.addItem(miToGraphich);
		}
		if (((AppW) app).getLAF().copyToClipboardSupported()) {
			AriaMenuItem miAsPicture = new AriaMenuItem(
					loc.getMenu("ExportAsPicture"), false, () -> {
				String url = getPlotPanel()
						.getExportImageDataUrl(3, true, false);
				((AppW) getApp()).getFileManager()
						.showExportAsPictureDialog(url,
								getApp().getExportTitle(),
								"png", "ExportAsPicture", getApp());
			});
			menu.addItem(miAsPicture);
		}

		String image = "<img src=\""
				+ MaterialDesignResources.INSTANCE.prob_calc_export().getSafeUri()
				.asString()
				+ "\" >";
		btnExport.addItem(new AriaMenuItem(image, true, menu));
		btnExport.getPopupMenu().removeStyleName("gwt-MenuBar");
		btnExport.getPopupMenu().addStyleName("gwt-ToggleButton");
		btnExport.getPopupMenu().addStyleName("MyToggleButton");
		return btnExport;
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
		if (showTable) {
			mainSplitPane
					.add(((ProbabilityTableW) getTable()).getWrappedPanel());
		} else {
			mainSplitPane
					.remove(((ProbabilityTableW) getTable()).getWrappedPanel());
		}
		tabResized();
	}

	@Override
	protected void onDistributionUpdate() {
		super.onDistributionUpdate();
		lblMeanSigma.setText(getMeanSigma());
	}
}
