package geogebra.web.gui.view.spreadsheet;

import geogebra.common.gui.view.spreadsheet.MyTable;
import geogebra.common.gui.view.spreadsheet.SpreadsheetContextMenu;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.menubar.MainMenu;
import geogebra.web.javax.swing.GCheckBoxMenuItem;
import geogebra.web.javax.swing.GPopupMenuW;
import geogebra.web.main.AppW;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

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

		MenuItem title = new MenuItem(MainMenu.getMenuBarHtml(
		        AppResources.INSTANCE.empty().getSafeUri().asString(), str),
		        true, new Command() {
			        public void execute() {
				        popup.setVisible(false);
			        }
		        });
		title.addStyleName("menuTitle");
		popup.addItem(title);
	}

	@Override
	public void addMenuItem(final String cmdString, String text, boolean enabled) {

		String html = MainMenu.getMenuBarHtml(getIconUrl(cmdString), text);

		MenuItem mi;
		if (html != null) {
			mi = new MenuItem(html, true, getCommand(cmdString));
			mi.addStyleName("mi_with_image"); // TEMP
		} else {
			mi = new MenuItem(text, getCommand(cmdString));
			mi.addStyleName("mi_no_image"); // TEMP
		}

		mi.setEnabled(enabled);

		popup.addItem(mi);
	}

	@Override
	public void addCheckBoxMenuItem(final String cmdString, String text,
	        boolean isSelected) {

		String html = MainMenu.getMenuBarHtml(getIconUrl(cmdString), text);

		GCheckBoxMenuItem cbItem = new GCheckBoxMenuItem(html,
		        getCommand(cmdString));
		cbItem.setSelected(isSelected);
		popup.addItem(cbItem);
	}

	@Override
	public Object addSubMenu(String text, String cmdString) {

		String html = MainMenu.getMenuBarHtml(getIconUrl(cmdString), text);
		MenuBar subMenu = new MenuBar(true);
		MenuItem menuItem = new MenuItem(html, true, subMenu);

		popup.addItem(menuItem);
		return menuItem;
	}

	@Override
	public void addSubMenuItem(Object menu, final String cmdString,
	        String text, boolean enabled) {

		String html = MainMenu.getMenuBarHtml(getIconUrl(cmdString), text);
		MenuItem mi = new MenuItem(html, true, getCommand(cmdString));
		mi.addStyleName("mi_with_image");
		mi.setEnabled(enabled);

		((MenuItem) menu).getSubMenu().addItem(mi);
	}

	@Override
	public void addSeparator() {
		popup.addSeparator();
	}

	private Command getCommand(final String cmdString) {
		Command cmd = new Command() {
			public void execute() {
				doCommand(cmdString);
			}
		};
		return cmd;
	}

	private static String getIconUrl(String cmdString) {

		if (cmdString == null) {
			return AppResources.INSTANCE.empty().getSafeUri().asString();
		}

		ImageResource im = null;

		if (cmdString.equals("ShowObject"))
			im = AppResources.INSTANCE.mode_showhideobject_16();

		else if (cmdString.equals("ShowLabel"))
			im = AppResources.INSTANCE.mode_showhidelabel_16();

		else if (cmdString.equals("Copy"))
			im = AppResources.INSTANCE.edit_copy();

		else if (cmdString.equals("Cut"))
			im = AppResources.INSTANCE.edit_cut();

		else if (cmdString.equals("Paste"))
			im = AppResources.INSTANCE.edit_paste();

		else if (cmdString.equals("Delete"))
			im = AppResources.INSTANCE.delete_small();

		else if (cmdString.equals("RecordToSpreadsheet"))
			im = AppResources.INSTANCE.spreadsheettrace();

		else if (cmdString.equals("Properties"))
			im = AppResources.INSTANCE.view_properties16();

		else if (cmdString.equals("SpreadsheetOptions"))
			im = AppResources.INSTANCE.view_properties16();

		else
			im = AppResources.INSTANCE.empty();

		return im.getSafeUri().asString();
	}

}
