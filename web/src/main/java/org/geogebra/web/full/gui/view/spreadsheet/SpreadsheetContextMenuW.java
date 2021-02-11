package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetContextMenu;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GCheckBoxMenuItem;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

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
		String html = MainMenu.getMenuBarHtmlClassic(getIconUrl(cmdString), text);

		AriaMenuItem mi;
		mi = new AriaMenuItem(html, true, getCommand(cmdString));
		mi.addStyleName("mi_with_image");
		mi.setEnabled(enabled);

		popup.addItem(mi);
	}

	@Override
	public void addCheckBoxMenuItem(final String cmdString, String nonSelected,
			String selected,
			boolean isSelected) {

		String html = MainMenu.getMenuBarHtmlClassic(getIconUrl(cmdString), "");

		GCheckBoxMenuItem cbItem = new GCheckBoxMenuItem(html, selected,
				nonSelected,
				getCommand(cmdString), true, app);
		cbItem.setSelected(isSelected, popup.getPopupMenu());
		popup.addItem(cbItem);
	}

	@Override
	public void addCheckBoxMenuItem(final String cmdString, String text,
	        boolean isSelected) {

		String html = MainMenu.getMenuBarHtmlClassic(getIconUrl(cmdString), text);

		GCheckBoxMenuItem cbItem = new GCheckBoxMenuItem(html,
				getCommand(cmdString), true, app);
		cbItem.setSelected(isSelected, popup.getPopupMenu());
		popup.addItem(cbItem);
	}

	@Override
	public AriaMenuItem addSubMenu(String text, String cmdString) {

		String html = MainMenu.getMenuBarHtmlClassic(getIconUrl(cmdString), text);

		AriaMenuBar subMenu = new AriaMenuBar();
		AriaMenuItem menuItem = new AriaMenuItem(html, true, subMenu);

		popup.addItem(menuItem);
		return menuItem;
	}

	@Override
	public void addSubMenuItem(Object menu, final String cmdString,
	        String text, boolean enabled) {

		String html = MainMenu.getMenuBarHtmlClassic(getIconUrl(cmdString), text);

		AriaMenuItem mi = new AriaMenuItem(html, true, getCommand(cmdString));
		mi.addStyleName("mi_with_image");
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

	private static String getIconUrl(String cmdString) {
		if (cmdString == null) {
			return AppResources.INSTANCE.empty().getSafeUri().asString();
		}

		SVGResource resource;
		switch (MenuCommand.valueOf(cmdString)) {
		case ShowLabel:
			resource = MaterialDesignResources.INSTANCE.label_black();
			break;
		case Copy:
			resource = MaterialDesignResources.INSTANCE.copy_black();
			break;
		case Cut:
			resource = MaterialDesignResources.INSTANCE.cut_black();
			break;
		case Paste:
			resource = MaterialDesignResources.INSTANCE.paste_black();
			break;
		case Duplicate:
			resource = MaterialDesignResources.INSTANCE.duplicate_black();
			break;
		case Delete:
		case DeleteObjects:
			resource = MaterialDesignResources.INSTANCE.delete_black();
			break;
		case RecordToSpreadsheet:
			resource = MaterialDesignResources.INSTANCE.record_to_spreadsheet_black();
			break;
		case Properties:
		case SpreadsheetOptions:
			resource = MaterialDesignResources.INSTANCE.gear();
			break;
		case Create:
			resource = MaterialDesignResources.INSTANCE.add_black();
			break;
		default:
			return AppResources.INSTANCE.empty().getSafeUri().asString();
		}
		return resource.getSafeUri().asString();
	}
}
