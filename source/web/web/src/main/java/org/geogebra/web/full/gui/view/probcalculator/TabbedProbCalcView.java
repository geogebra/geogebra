/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.view.probcalculator;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.tab.ComponentTab;
import org.geogebra.web.shared.components.tab.TabData;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class TabbedProbCalcView extends ProbabilityCalculatorViewW {
	private ComponentTab probabilityTab;
	protected final StatisticsCalculatorW statCalculator;
	protected DistributionPanel distrPanel;
	protected FlowPanel plotSplitPane;
	protected @CheckForNull FlowPanel mainSplitPane;
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
		settingsChanged(getApp().getSettings().getProbCalcSettings());
		buildProbCalcPanel();
		isIniting = false;
		statCalculator = new StatisticsCalculatorW(app);

		buildTab();
		init();
	}

	private void buildTab() {
		TabData distributionTab = new TabData("Distribution", probCalcPanel);
		TabData statisticsTab = new TabData("Statistics", statCalculator.getWrappedPanel());
		probabilityTab = new ComponentTab((AppW) app, "", distributionTab, statisticsTab);
		probabilityTab.addStyleName("probabilityTab");
		probabilityTab.onResize();
		probabilityTab.switchToTab(getApp().getSettings().getProbCalcSettings()
				.getCollection().isActive() ? 1 : 0);
	}

	private void buildButtons() {
		if (!GlobalScope.isExamActive(app)) {
			IconButton btnExport = createExportMenu();
			btnExport.addStyleName("probCalcStylbarBtn");
			plotPanelOptions.add(btnExport);
		}
		lblMeanSigma = new Label();
		lblMeanSigma.addStyleName("lblMeanSigma");
		plotPanelOptions.add(lblMeanSigma);
	}

	private IconButton createExportMenu() {
		GPopupMenuW menuExport = new GPopupMenuW((AppW) app, true);
		menuExport.getPopupMenu().addStyleName("probCalcStylbarBtn");

		if (!getApp().isApplet()) {
			addExportItem(menuExport, "CopyToGraphics", exportToEVAction);
		}
		if (((AppW) app).getLAF().copyToClipboardSupported()) {
			addExportItem(menuExport, "ExportAsPicture", this::showExportDialog);
		}
		IconButton btnExport = new IconButton((AppW) app, null,
				new ImageIconSpec(MaterialDesignResources.INSTANCE.signout_black()), null);
		btnExport.addFastClickHandler(e -> {
			if (menuExport.isMenuShown()) {
				menuExport.hide();
			} else {
				menuExport.show(btnExport, BTN_SIZE - DEFAULT_MENU_WIDTH, BTN_SIZE);
			}
		});
		menuExport.getPopupPanel().addAutoHidePartner(btnExport.getElement());
		menuExport.getPopupPanel().addCloseHandler(i -> btnExport.setActive(false));
		return btnExport;
	}

	private void showExportDialog() {
		String url = getPlotPanel().getExportImageDataUrl(3, true, false);
		((AppW) getApp()).getFileManager().showExportAsPictureDialog(url,
				getApp().getExportTitle(), "png", "ExportAsPicture", getApp());
	}

	private void addExportItem(GPopupMenuW exportMenu, String title,
			Scheduler.ScheduledCommand copyCmd) {
		AriaMenuItem item = new AriaMenuItem(loc.getMenu(title), null, copyCmd);
		item.addStyleName("no-image");
		exportMenu.addItem(item);
	}

	@Override
	public void tabResized() {
		ProbabilityTableW table = (ProbabilityTableW) getTable();
		if (mainSplitPane == null) {
			return;
		}
		int totalWidth = mainSplitPane.getOffsetWidth();
		int tableWidth = isDiscreteProbability() && table != null ? table.getStatTable()
				.getTable().getOffsetWidth() + TABLE_PADDING_AND_SCROLLBAR : 0;
		int width = totalWidth - tableWidth - 5;
		int height = probCalcPanel.getOffsetHeight() - 20;
		if (width > 0) {
			resizePlotPanel(width, height - CONTROL_PANEL_HEIGHT);
			plotSplitPane.setWidth(width + "px");
		}

		if (height > 0 && isDiscreteProbability() && table != null) {
			table.getWrappedPanel().setPixelSize(tableWidth, height);
		}
	}

	/**
	 * @return whether distribution tab is open
	 */
	@Override
	public boolean isDistributionTabOpen() {
		return probabilityTab.getSelectedTabIdx() == 0;
	}

	@Override
	public void setLabels() {
		super.setLabels();
		statCalculator.setLabels();
		distrPanel.rebuild();
		probabilityTab.setLabels();
	}

	@Override
	public ComponentTab getWrapperPanel() {
		return probabilityTab;
	}

	@Override
	public StatisticsCalculator getStatCalculator() {
		return statCalculator;
	}

	protected void buildProbCalcPanel() {
		distrPanel = new DistributionPanel(this, (AppW) app);
		plotSplitPane = new FlowPanel();
		plotSplitPane.add(plotPanelPlus);
		plotSplitPane.add(distrPanel);
		plotSplitPane.addStyleName("plotSplitPane");
		FlowPanel mainPane = new FlowPanel();
		mainPane.addStyleName("mainSplitPanel");
		mainPane.add(plotSplitPane);
		this.mainSplitPane = mainPane;

		probCalcPanel = new FlowPanel();
		probCalcPanel.addStyleName("ProbCalcPanel");
		probCalcPanel.add(mainPane);
	}

	@Override
	protected void addRemoveTable(boolean showTable) {
		ProbabilityTableW table = (ProbabilityTableW) getTable();
		if (table != null && mainSplitPane != null) {
			FlowPanel tablePanel = table.getWrappedPanel();
			if (showTable) {
				mainSplitPane.add(tablePanel);
			} else {
				mainSplitPane.remove(tablePanel);
			}
			tabResized();
		}
	}

	@Override
	protected void onDistributionUpdate() {
		super.onDistributionUpdate();
		lblMeanSigma.setText(getMeanSigma());
	}
}
