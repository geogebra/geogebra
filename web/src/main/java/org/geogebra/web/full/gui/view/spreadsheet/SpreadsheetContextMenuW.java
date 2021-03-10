package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetContextMenu;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.Command;

/**
 * Subclass of SpreadsheetContextMenu, implements the spreadsheet context menu
 * for web.
 * 
 * @author G. Sturr
 * 
 */
public class SpreadsheetContextMenuW extends SpreadsheetContextMenu {

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
	public Object getMenuContainer() {
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

		AriaMenuItem title = new AriaMenuItem(MainMenu.getMenuBarHtmlClassic(
		        AppResources.INSTANCE.empty().getSafeUri().asString(), str),
		        true, this::hidePopup);
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
		String html = MainMenu.getMenuBarHtml(getIconUrl(cmdString), text);

		AriaMenuItem mi;
		mi = new AriaMenuItem(html, true, getCommand(cmdString));
		mi.setEnabled(enabled);

		popup.addItem(mi);
	}

	@Override
	public void addCheckBoxMenuItem(final String cmdString, String text, boolean isSelected) {
		String html = MainMenu.getMenuBarHtml(getIconUrl(cmdString), text);

		GCheckmarkMenuItem cbItem = new GCheckmarkMenuItem(
				html, isSelected, getCommand(cmdString));
		popup.addItem(cbItem);
	}

	@Override
	public AriaMenuItem addSubMenu(String text, String cmdString) {

		String html = MainMenu.getMenuBarHtml(getIconUrl(cmdString), text);

		AriaMenuBar subMenu = new AriaMenuBar();
		AriaMenuItem menuItem = new AriaMenuItem(html, true, subMenu);

		popup.addItem(menuItem);
		return menuItem;
	}

	@Override
	public void addSubMenuItem(Object menu, final String cmdString,
	        String text, boolean enabled) {

		AriaMenuItem mi = new AriaMenuItem(text, true, getCommand(cmdString));
		mi.setEnabled(enabled);

		((AriaMenuItem) menu).getSubMenu().addItem(mi);
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
