package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import java.util.List;

import org.geogebra.common.main.Localization;
import org.geogebra.common.spreadsheet.core.ContextMenuItem;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.core.client.Scheduler;

/**
 * UI builder for context menu and style bar menus.
 */
public class SpreadsheetMenuBuilder {

	private final Localization loc;
	private final Runnable hideCallback;

	/**
	 * @param loc localization
	 * @param hideCallback called when menu item is clicked, responsible for closing the menu
	 */
	public SpreadsheetMenuBuilder(Localization loc, Runnable hideCallback) {
		this.loc = loc;
		this.hideCallback = hideCallback;
	}

	/**
	 * Add items to a menu.
	 * @param popupMenu menu widget
	 * @param items menu items
	 */
	public void addItems(GPopupMenuW popupMenu, List<ContextMenuItem> items) {
		for (ContextMenuItem item : items) {
			if (item instanceof ContextMenuItem.Divider) {
				popupMenu.addSeparator();
			} else if (item instanceof ContextMenuItem.SubMenuItem) {
				popupMenu.addItem(createSubMenuItem((ContextMenuItem.SubMenuItem) item));
			} else if (item instanceof ContextMenuItem.ActionableItem) {
				popupMenu.addItem(createActionableItem((ContextMenuItem.ActionableItem) item));
			}
		}
	}

	private AriaMenuItem createActionableItem(ContextMenuItem.ActionableItem actionableItem) {
		String text = loc.getMenu(actionableItem.getLocalizationKey());
		SVGResource image = getActionIcon(actionableItem.getIdentifier());
		Scheduler.ScheduledCommand scheduledCommand = () -> performAndHideMenu(actionableItem);
		return new AriaMenuItem(text, image, scheduledCommand);
	}

	private AriaMenuItem createSubMenuItem(ContextMenuItem.SubMenuItem subMenuItem) {
		String text = loc.getMenu(subMenuItem.getLocalizationKey());
		SVGResource image = getActionIcon(subMenuItem.getIdentifier());
		AriaMenuBar ariaMenuBar = new AriaMenuBar();
		for (ContextMenuItem item : subMenuItem.getItems()) {
			ariaMenuBar.addItem(createActionableItem((ContextMenuItem.ActionableItem) item));
		}
		return new AriaMenuItem(text, image, ariaMenuBar);
	}

	private void performAndHideMenu(ContextMenuItem.ActionableItem item) {
		item.performAction();
		hideCallback.run();
	}

	private SVGResource getActionIcon(ContextMenuItem.Identifier action) {
		MaterialDesignResources res = MaterialDesignResources.INSTANCE;
		switch (action) {
		case CUT:
			return res.cut_black();
		case COPY:
			return res.copy_black();
		case PASTE:
			return res.paste_black();
		case DELETE:
			return res.delete_black();
		case CALCULATE:
			return res.calculate();
		case CREATE_CHART:
			return res.insert_chart();
		case LINE_CHART:
			return res.table_line_chart();
		case BAR_CHART:
			return res.table_bar_chart();
		case HISTOGRAM:
			return res.table_histogram();
		case PIE_CHART:
			return res.table_pie_chart();
		case INSERT_ROW_ABOVE:
		case INSERT_ROW_BELOW:
		case DELETE_ROW:
		case INSERT_COLUMN_LEFT:
		case INSERT_COLUMN_RIGHT:
		case DELETE_COLUMN:
		default:
			return null;
		}
	}
}
