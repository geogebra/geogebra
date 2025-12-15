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

package org.geogebra.web.full.gui.toolbarpanel.tableview;

import java.util.function.Supplier;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarTab;
import org.geogebra.web.full.gui.view.probcalculator.ProbabilityCalculatorViewW;
import org.geogebra.web.full.util.CustomScrollbar;
import org.geogebra.web.full.util.StickyTable;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;
import org.gwtproject.dom.style.shared.Position;

/**
 * Tab of Table Values View.
 * 
 * @author laszlo
 */
public class TableTab extends ToolbarTab {

	private final Supplier<? extends StickyTable<?>> tableSupplier;
	private @CheckForNull StickyTable<?> table;
	private final ToolbarPanel toolbarPanel;
	private final AppW app;
	private ComponentInfoErrorPanel emptyPanel;

	/**
	 * @param toolbarPanel
	 *            toolbar panel
	 */
	public TableTab(ToolbarPanel toolbarPanel, Supplier<? extends StickyTable<?>> table) {
		super(toolbarPanel);
		this.toolbarPanel = toolbarPanel;
		this.tableSupplier = table;
		this.app = toolbarPanel.getApp();
		CustomScrollbar.apply(this);
		buildEmptyTablePanel();
		getContainerElement().getStyle().setPosition(Position.STATIC);
	}

	private void buildEmptyTablePanel() {
		InfoErrorData data = new InfoErrorData("TableValuesEmptyTitle",
				"TableDiscreteDistribution", null,
				MaterialDesignResources.INSTANCE.toolbar_table_view_black());
		emptyPanel = new ComponentInfoErrorPanel(app.getLocalization(),
				data, null);
	}

	@Override
	protected void onActive() {
		if (app.getConfig().hasDistributionView()
				&& isEmptyProbabilityTable()) {
			setWidget(emptyPanel);
		} else  {
			ensureTableExists().setHeight(getTabHeight());
			setWidget(table);
		}
		if (table != null) {
			table.selectFirstCell();
		}
	}

	private StickyTable<?> ensureTableExists() {
		if (table == null) {
			StickyTable<?> table1 = tableSupplier.get();
			TestHarness.setAttr(table1, "TV_table");
			table1.setStyleName("tvTable", true);
			toolbarPanel.getDecorator().decorateTableTab(this, table1);
			table = table1;
			return table1;
		}
		return table;
	}

	@Override
	public DockPanelData.TabIds getID() {
		return DockPanelData.TabIds.TABLE;
	}

	private int getTabHeight() {
		return toolbarPanel.getDecorator().getTabHeight(toolbarPanel.getTabHeight());
	}

	private boolean isEmptyProbabilityTable() {
		if (app.getConfig().hasDistributionView()) {
			ProbabilityCalculatorViewW view = (ProbabilityCalculatorViewW) app
					.getGuiManager().getProbabilityCalculator();
			return !view.hasTableView();
		}
		return true;
	}

	@Override
	public void setLabels() {
		if (emptyPanel != null) {
			buildEmptyTablePanel();
		}
	}

	@Override
	public void open() {
		toolbarPanel.openTableView(true);
	}

	@Override
	public void close() {
		toolbarPanel.close(false);
	}

	@Override
	public void onResize() {
		int w = this.toolbarPanel.getTabWidth();
		int h = toolbarPanel.getTabHeight();
		if (w < 0 || h < 0) {
			return;
		}

		setWidth(w + "px");
		setHeight(h + "px");
		resizeTable(h);
	}

	private void resizeTable(int tabHeight) {
		if (table == null) {
			return;
		}
		boolean smallScreen = app.getAppletFrame()
				.shouldHaveSmallScreenLayout();
		DockPanelDecorator decorator = toolbarPanel.getDecorator();
		if (smallScreen) {
			decorator.resizeTableSmallScreen(tabHeight, table);
		} else {
			decorator.resizeTable(tabHeight, table);
		}
	}

	/**
	 * Scroll table view to the corresponding column of the geo.
	 * 
	 * @param geo
	 *            to scroll.
	 */
	public void scrollTo(GeoEvaluatable geo) {
		if (table instanceof StickyValuesTable) {
			((StickyValuesTable) table).scrollTo(geo);
		}
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		if (table instanceof StickyValuesTable) {
			return ((StickyValuesTable) table).getKeyboardListener();
		}
		return null;
	}

	/**
	 * Open function define dialog for table if table is empty
	 */
	public void openDialogIfEmpty() {
		if (((TableValuesView) app.getGuiManager().getTableValuesView()).hasNoDefinedFunctions()) {
			ensureTableExists().openDefineFunctions();
		}
	}
}
