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
import java.util.Set;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.contextmenu.ContextMenuItemFilter;
import org.geogebra.common.contextmenu.TableValuesContextMenuActionHandler;
import org.geogebra.common.contextmenu.TableValuesContextMenuItem;
import org.geogebra.common.gui.FocusableComponent;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.ownership.SuiteScope;
import org.geogebra.common.util.AttributedString;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.Command;
import org.jspecify.annotations.NonNull;

/**
 * Context menu which is opened with the table of values header 3dot button
 */
public final class ContextMenuTV implements TableValuesContextMenuActionHandler.Delegate {
	private final TableValuesView view;
	private final Runnable closeHandler;
	/**
	 * popup for the context menu
	 */
	private GPopupMenuW wrappedPopup;
	/**
	 * application
	 */
	private final AppWFull app;

	private final int columnIdx;

	/**
	 * @param app application
	 * @param view table view
	 * @param column index of column
	 * @param closeHandler runs when popup is closed and focus is not explicitly moved
	 */
	public ContextMenuTV(
			AppWFull app, TableValuesView view, int column, @NonNull Runnable closeHandler) {
		this.app = app;
		this.view = view;
		this.columnIdx = column;
		buildGui(closeHandler);
		app.registerPopup(wrappedPopup.getPopupPanel());
		wrappedPopup
				.getPopupPanel()
				.addCloseHandler(evt -> app.unregisterPopup(wrappedPopup.getPopupPanel()));
		this.closeHandler = closeHandler;
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

	private void buildGui(@NonNull Runnable onFocusReturn) {
		wrappedPopup = new GPopupMenuW(app) {
			@Override
			protected void returnFocus(FocusableComponent component) {
				onFocusReturn.run();
			}
		};
		wrappedPopup.getPopupPanel().addStyleName("tvContextMenu");
		GeoEvaluatable column = view.getEvaluatable(getColumnIdx());
		SuiteScope suiteScope = GlobalScope.getSuiteScope(app);
		Set<ContextMenuItemFilter> contextMenuFilters = suiteScope != null
				? suiteScope.restrictionsController.getContextMenuItemFilters()
				: Set.of();
		boolean isExamActive = suiteScope != null && suiteScope.examController.isExamActive();
		List<TableValuesContextMenuItem> items = ContextMenuFactory.makeTableValuesContextMenu(
				column,
				columnIdx,
				view.getTableValuesModel(),
				app.getConfig().getVersion() == GeoGebraConstants.Version.SCIENTIFIC,
				isExamActive,
				contextMenuFilters);
		TableValuesContextMenuActionHandler tableValuesContextMenuActionHandler =
				new TableValuesContextMenuActionHandler(columnIdx, view, app, this);
		for (TableValuesContextMenuItem item : items) {
			if (item.getItem() == TableValuesContextMenuItem.Item.Separator) {
				wrappedPopup.addVerticalSeparator();
			} else {
				addCommand(
						() -> {
							tableValuesContextMenuActionHandler.handleSelectedItem(item);
							returnFocusIfNeeded(item.getItem());
						},
						item.getLocalizedTitle(app.getLocalization()),
						getTestTitle(item.getItem()));
			}
		}
	}

	private void returnFocusIfNeeded(TableValuesContextMenuItem.Item item) {
		if (item != TableValuesContextMenuItem.Item.Edit
				&& item != TableValuesContextMenuItem.Item.ImportData) {
			closeHandler.run();
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
		AriaMenuItem item = new AriaMenuItem(localizedName, null, command);
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
		DialogManagerW.openTableViewDialog(null, getApp(), closeHandler);
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

	/**
	 * Return focus to the cell that triggered this context menu.
	 * @return whether focus was moved
	 */
	public boolean returnFocus() {
		if (closeHandler != null) {
			closeHandler.run();
			return true;
		}
		return false;
	}
}
