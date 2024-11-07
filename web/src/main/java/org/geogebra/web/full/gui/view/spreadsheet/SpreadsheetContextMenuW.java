package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetContextMenu;
import org.geogebra.common.kernel.geos.GeoElement;
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
	public SpreadsheetContextMenuW(MyTable table) {
		super(table);
	}

	@Override
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
	public void addMenuItem(final String cmdString, String text, boolean enabled) {
		AriaMenuItem mi = MainMenu.getMenuBarItem(getIconUrl(cmdString),
				text, getCommand(cmdString));
		mi.setEnabled(enabled);

		popup.addItem(mi);
	}

	@Override
	public void addCheckBoxMenuItem(final String cmdString, String text, boolean isSelected) {
		GCheckmarkMenuItem cbItem = new GCheckmarkMenuItem(
				getIconUrl(cmdString), text, isSelected, getCommand(cmdString));
		popup.addItem(cbItem);
	}

	@Override
	public AriaMenuItem addSubMenu(String text, String cmdString) {
		AriaMenuBar subMenu = new AriaMenuBar();
		AriaMenuItem menuItem = new AriaMenuItem(text, getIconUrl(cmdString), subMenu);

		popup.addItem(menuItem);
		return menuItem;
	}

	@Override
	public void addSubMenuItem(AriaMenuItem menu, final String cmdString,
	        String text, boolean enabled) {

		AriaMenuItem mi = new AriaMenuItem(text, null, getCommand(cmdString));
		mi.setEnabled(enabled);

		menu.getSubMenu().addItem(mi);
	}

	@Override
	public void addSeparator() {
		popup.addSeparator();
	}

	private Command getCommand(final String cmdString) {
		return () -> doCommand(cmdString);
	}

	private static ResourcePrototype getIconUrl(String cmdString) {
		if (cmdString == null) {
			return AppResources.INSTANCE.empty();
		}

		switch (MenuCommand.valueOf(cmdString)) {
		case ShowLabel:
			return MaterialDesignResources.INSTANCE.label_black();
		case Copy:
			return MaterialDesignResources.INSTANCE.copy_black();
		case Cut:
			return MaterialDesignResources.INSTANCE.cut_black();
		case Paste:
			return MaterialDesignResources.INSTANCE.paste_black();
		case Duplicate:
			return MaterialDesignResources.INSTANCE.duplicate_black();
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
