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

package org.geogebra.web.full.gui.view.algebra;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.contextmenu.AlgebraContextMenuActionHandler;
import org.geogebra.common.contextmenu.AlgebraContextMenuItem;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.contextmenu.ContextMenuItemFilter;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.Localization;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.ownership.SuiteScope;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.gwtproject.user.client.ui.Widget;

/**
 * The ... menu for AV items
 *
 */
public class ContextMenuAVItemMore implements SetLabels,
		AlgebraContextMenuActionHandler.Delegate {

	/** visible component */
	protected final GPopupMenuW wrappedPopup;
	/** localization */
	private final Localization loc;
	private final AppWFull mApp;
	private GeoElement geo;
	private final RadioTreeItem item;

	/**
	 * Creates new context menu
	 *
	 * @param item
	 *            application
	 */
	ContextMenuAVItemMore(RadioTreeItem item) {
		mApp = item.getApplication();
		loc = mApp.getLocalization();
		wrappedPopup = new GPopupMenuW(mApp);
		setGeo(item.geo);
		this.item = item;
	}

	/**
	 * @return see {@link AppW}
	 */
	public AppWFull getApp() {
		return mApp;
	}

	/**
	 * Rebuild the UI
	 */
	public void buildGUI() {
		wrappedPopup.clearItems();
		SuiteScope suiteScope = GlobalScope.getSuiteScope(mApp);
		Set<ContextMenuItemFilter> contextMenuFilters = suiteScope != null
				? suiteScope.restrictionsController.getContextMenuItemFilters() : Set.of();
		List<AlgebraContextMenuItem> actions = ContextMenuFactory
				.makeAlgebraContextMenu(geo,
						mApp.getKernel().getAlgebraProcessor(),
						getApp().getSubAppCode(),
						mApp.getSettings().getAlgebra(),
						contextMenuFilters);
		if (!getApp().showToolBar()) {
			actions.remove(AlgebraContextMenuItem.CreateTableValues);
		}
		for (AlgebraContextMenuItem action : actions) {
			addAction(action);
		}
	}

	/**
	 * Sets geo for menu building the menu items
	 * @param geo for
	 */
	public void setGeo(GeoElement geo) {
		this.geo = geo;
		buildGUI();
	}

	/**
	 * @param x
	 *            screen x-coordinate
	 * @param y
	 *            screen y-coordinate
	 */
	public void show(Widget source, int x, int y) {
		wrappedPopup.show(source, x, y);
		wrappedPopup.setAnchor(source.getElement());
		wrappedPopup.getPopupMenu().focusDeferred();
	}

	private void addAction(final AlgebraContextMenuItem menuItem) {
		AlgebraContextMenuActionHandler algebraContextMenuActionHandler =
				new AlgebraContextMenuActionHandler(mApp, mApp.getGuiManager()
						.getTableValuesView(), geo, this);
		AriaMenuItem itemWidget = new AriaMenuItem(menuItem.getLocalizedTitle(loc),
				null, () -> algebraContextMenuActionHandler.handleSelectedItem(menuItem));
		TestHarness.setAttr(itemWidget, "menu" + menuItem.getTranslationKey());
		itemWidget.addStyleName("no-image");
		wrappedPopup.addItem(itemWidget);
	}

	@Override
	public void setLabels() {
		buildGUI();
	}

	/**
	 * Adds menu for clearing input.
	 */
	void addClearInputItem() {
		setGeo(null);
	}

	@Override
	public void clearAlgebraInput() {
		item.onClear();
	}

	@Override
	public void showTableValuesDialog(GeoElement geoElement) {
		mApp.getDialogManager().openTableViewDialog(geoElement);
	}

	@Override
	public void scrollToTableValuesColumn(int columnIndex) {
		GeoEvaluatable evaluatable = mApp.getGuiManager()
				.getTableValuesView().getEvaluatable(columnIndex);
		mApp.getGuiManager().getUnbundledToolbar().openTableView(evaluatable, false);
	}

	@Override
	public void showTableValuesView() {
		mApp.getGuiManager().getUnbundledToolbar().openTableView(null, true);
	}

	@Override
	public void addFormulaToAlgebraView(@Nonnull String formula) {
		RadioTreeItem input = mApp.getAlgebraView().getInputTreeItem();
		RadioTreeItem currentNode = mApp.getAlgebraView().getNode(geo);
		if (currentNode != null) {
			currentNode.selectItem(false);
		}
		if (input != null) {
			input.setText(formula);
			input.setFocus(true);
		}
	}

	@Override
	public void showOldObjectProperties() {
		mApp.getDialogManager().showPropertiesDialog(new ArrayList<>(List.of(geo)));
	}

	@Override
	public void showObjectProperties(@Nonnull PropertyView.TabbedPageSelector tabbedPageSelector) {
		mApp.getDialogManager().showPropertiesDialog(new ArrayList<>(List.of(geo))); // TODO
	}
}
