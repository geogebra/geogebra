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

package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetContextMenu;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.Command;
import org.gwtproject.user.client.ui.InlineHTML;

/**
 * Subclass of SpreadsheetContextMenu, implements the spreadsheet context menu
 * for web.
 * 
 * @author G. Sturr
 * 
 */
public class SpreadsheetContextMenuW extends SpreadsheetContextMenu<AriaMenuItem> {

	private GPopupMenuW popup;

	/**
	 * Constructor
	 * 
	 * @param table
	 *            spreadsheet table
	 */
	public SpreadsheetContextMenuW(MyTable table, App app) {
		super(table, table.getToolProcessor(app));
		createGUI();
	}

	public GPopupMenuW getMenuContainer() {
		return popup;
	}

	@Override
	public void createGUI() {
		popup = new GPopupMenuW((AppW) app);
		popup.getPopupPanel().addStyleName("geogebraweb-popup-spreadsheet");
		initMenu();
	}

	@Override
	public void setTitle(String str) {
		AriaMenuItem title = new AriaMenuItem(new InlineHTML(str), this::hidePopup);
		title.addStyleName("menuTitle");
		popup.addItem(title);
	}

	/**
	 * Hide the menu popup
	 */
	protected void hidePopup() {
		popup.setVisible(false);
	}

	@Override
	protected void addShowObject(GeoElement geo) {
		// Show object item is skipped in spreadsheet
	}

	@Override
	public void addMenuItem(final MenuCommand command, String text, boolean enabled) {
		AriaMenuItem mi = MainMenu.getMenuBarItem(getIconUrl(command),
				text, getCommand(command));
		mi.setEnabled(enabled);

		popup.addItem(mi);
	}

	@Override
	public void addCheckBoxMenuItem(final MenuCommand command, String text, boolean isSelected) {
		GCheckmarkMenuItem cbItem = new GCheckmarkMenuItem(
				getIconUrl(command), text, isSelected, getCommand(command));
		popup.addItem(cbItem);
	}

	@Override
	public AriaMenuItem addSubMenu(String text, MenuCommand command) {
		AriaMenuBar subMenu = new AriaMenuBar();
		AriaMenuItem menuItem = new AriaMenuItem(text, getIconUrl(command), subMenu);

		popup.addItem(menuItem);
		return menuItem;
	}

	@Override
	public void addSubMenuItem(AriaMenuItem menu, final MenuCommand cmdString,
	        String text, boolean enabled) {

		AriaMenuItem mi = new AriaMenuItem(text, null, getCommand(cmdString));
		mi.setEnabled(enabled);

		menu.getSubMenu().addItem(mi);
	}

	@Override
	public void addSeparator() {
		popup.addSeparator();
	}

	private Command getCommand(final MenuCommand cmdString) {
		return () -> doCommand(cmdString);
	}

	private static ResourcePrototype getIconUrl(MenuCommand command) {
		if (command == null) {
			return AppResources.INSTANCE.empty();
		}

		switch (command) {
		case ShowLabel:
			return MaterialDesignResources.INSTANCE.label_black();
		case Copy:
			return MaterialDesignResources.INSTANCE.copy_black();
		case Cut:
			return MaterialDesignResources.INSTANCE.cut_black();
		case Paste:
			return MaterialDesignResources.INSTANCE.paste_black();
		case Delete:
		case DeleteObjects:
			return MaterialDesignResources.INSTANCE.delete_black();
		case RecordToSpreadsheet:
			return MaterialDesignResources.INSTANCE.record_to_spreadsheet_black();
		case Properties:
		case SpreadsheetOptions:
			return MaterialDesignResources.INSTANCE.gear();
		case Create:
			return MaterialDesignResources.INSTANCE.add_black();
		default:
			return AppResources.INSTANCE.empty();
		}
	}
}
