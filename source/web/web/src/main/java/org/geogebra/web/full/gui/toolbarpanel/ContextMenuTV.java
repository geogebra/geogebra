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

package org.geogebra.web.full.gui.toolbarpanel;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.contextmenu.ContextMenuItemFilter;
import org.geogebra.common.contextmenu.TableValuesContextMenuActionHandler;
import org.geogebra.common.contextmenu.TableValuesContextMenuActionHandler.PlotActionHandler;
import org.geogebra.common.contextmenu.TableValuesContextMenuItem;
import org.geogebra.common.gui.view.table.TableUtil;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.gui.view.table.regression.RegressionSpecification;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.ownership.SuiteScope;
import org.geogebra.common.util.AttributedString;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.Command;

/**
 * Context menu which is opened with the table of values header 3dot button
 */
public class ContextMenuTV implements TableValuesContextMenuActionHandler.Delegate {
	private final TableValuesView view;
	/**
	 * popup for the context menu
	 */
	protected GPopupMenuW wrappedPopup;
	/**
	 * application
	 */
	protected AppWFull app;
	private final int columnIdx;

	/**
	 * @param app
	 *            see {@link AppW}
	 * @param column
	 *            index of column
	 */
	public ContextMenuTV(AppWFull app, TableValuesView view, int column) {
		this.app = app;
		this.view = view;
		this.columnIdx = column;
		buildGui();
	}

	/**
	 * @return application
	 */
	public AppW getApp() {
		return app;
	}

	/**
	 * @return index of column
	 */
	public int getColumnIdx() {
		return columnIdx;
	}

	private void buildGui() {
		wrappedPopup = new GPopupMenuW(app);
		wrappedPopup.getPopupPanel().addStyleName("tvContextMenu");
		GeoEvaluatable column = view.getEvaluatable(getColumnIdx());
		SuiteScope suiteScope = GlobalScope.getSuiteScope(app);
		Set<ContextMenuItemFilter> contextMenuFilters = suiteScope != null
				? suiteScope.restrictionsController.getContextMenuItemFilters() : Set.of();
		boolean isExamActive = suiteScope != null && suiteScope.examController.isExamActive();
		List<TableValuesContextMenuItem> items = ContextMenuFactory
				.makeTableValuesContextMenu(column, columnIdx, view.getTableValuesModel(),
				app.getConfig().getVersion() == GeoGebraConstants.Version.SCIENTIFIC,
						isExamActive, contextMenuFilters);
		TableValuesContextMenuActionHandler tableValuesContextMenuActionHandler =
				new TableValuesContextMenuActionHandler(columnIdx, view, app,
						app.getLocalization(), this);
		for (TableValuesContextMenuItem item: items) {
			if (item.getItem() == TableValuesContextMenuItem.Item.Separator) {
				wrappedPopup.addVerticalSeparator();
			} else {
				addCommand(() -> tableValuesContextMenuActionHandler.handleSelectedItem(item),
						item.getLocalizedTitle(app.getLocalization()),
						getTestTitle(item.getItem()));
			}
		}
	}

	private String getTestTitle(TableValuesContextMenuItem.Item item) {
		switch (item) {
		case Edit:
			return "edit";
		case ClearColumn:
			return "clear";
		case RemoveColumn:
			return "delete";
		case ShowPoints:
		case HidePoints:
			return "showhide";
		case ImportData:
			return "importData";
		case Regression:
			return "regression";
		case Statistics1:
		case Statistics2:
			return "stats";
		case Separator:
			break;
		}
		return "";
	}

	private void addCommand(Command command, AttributedString localizedName, String testTitle) {
		AriaMenuItem item = new AriaMenuItem(localizedName,
				null, command);
		addItem(item, testTitle);
	}

	private void addItem(AriaMenuItem mi, String testTitle) {
		mi.addStyleName("no-image");
		TestHarness.setAttr(mi, "menu_" + testTitle);
		wrappedPopup.addItem(mi);
	}

	/**
	 * Show the context menu at the (x, y) screen coordinates.
	 * 
	 * @param x
	 *            y coordinate.
	 * @param y
	 *            y coordinate.
	 */
	public void show(Element source, int x, int y) {
		wrappedPopup.showAndFocus(source, x, y);
	}

	/**
	 * Hide the popup menu.
	 */
	public void hide() {
		wrappedPopup.hideMenu();
	}

	@Override
	public void showTableValuesDialog() {
		DialogManager dialogManager = getApp().getDialogManager();
		if (dialogManager != null) {
			dialogManager.openTableViewDialog(null);
		}
	}

	@Override
	public void startEditingAlgebraViewItem(GeoElement geoElement) {
		GuiManagerInterfaceW guiManager = getApp().getGuiManager();
		if (guiManager != null) {
			guiManager.startEditing(geoElement);
		}
	}

	@Override
	public void startDataImport() {
		app.getCsvHandler().execute();
	}

	@Override
	public void showStatisticsDialog(@Nonnull String title, @Nonnull AttributedString header,
			@Nonnull List<StatisticGroup> statisticGroups) {
		DialogData data = new DialogData(title, TableUtil.toHtml(header), "Close", null);
		StatsDialogTV dialog = new StatsDialogTV(app, data);
		dialog.setRowsAndShow(statisticGroups);
	}

	@Override
	public void showRegressionDialog(@Nonnull String title, @Nonnull AttributedString header,
			@Nonnull Map<RegressionSpecification, List<StatisticGroup>> regressionGroups,
			@CheckForNull PlotActionHandler plotActionHandler) {
		DialogData data = new DialogData(title, TableUtil.toHtml(header), "Close",
				plotActionHandler != null ? "Plot" : null);
		StatsDialogTV dialog = new StatsDialogTV(app, data);
		dialog.addRegressionChooser(regressionGroups, plotActionHandler);
	}

	@Override
	public void showErrorDialog(@Nonnull String title, @Nonnull AttributedString header,
			@Nonnull String errorMessage) {
		DialogData errorDialogData = new DialogData(title, TableUtil.toHtml(header), "Close", null);
		ComponentDialog dialog = new ComponentDialog(app, errorDialogData, true, true);
		dialog.addStyleName("statistics error");
		InfoErrorData errorData = new InfoErrorData(
				app.getLocalization().getMenu("StatsDialog.NoData"),
				errorMessage, null, MaterialDesignResources.INSTANCE.bar_chart_black());
		ComponentInfoErrorPanel infoPanel = new ComponentInfoErrorPanel(app.getLocalization(),
				errorData, null);
		dialog.addDialogContent(infoPanel);
		dialog.show();
	}
}
