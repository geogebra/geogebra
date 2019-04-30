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
import org.geogebra.web.shared.SharedResources;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
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
		if (app.isWhiteboardActive()) {
			popup.getPopupPanel().addStyleName("contextMenu");
		} else if (app.isUnbundled()) {
			popup.getPopupPanel().addStyleName("matMenu");
		}
	}

	@Override
	public void setTitle(String str) {

		AriaMenuItem title = new AriaMenuItem(MainMenu.getMenuBarHtmlClassic(
		        AppResources.INSTANCE.empty().getSafeUri().asString(), str),
		        true, new Command() {
			        @Override
					public void execute() {
						hidePopup();
			        }
		        });
		if (app.isUnbundled()) {
			title.addStyleName("no-hover");
		} else {
			title.addStyleName("menuTitle");
		}
		popup.addItem(title);
	}

	/**
	 * Hide the menu popup
	 */
	protected void hidePopup() {
		popup.setVisible(false);
	}

	@Override
	protected void addEditItems() {
		if (app.isUnbundledOrWhiteboard()) {
			addSeparator();

			addCut();
			addCopy();
			addDuplicate();
			addPaste();
			addDelete();
		} else {
			super.addEditItems();
		}
	}

	@Override
	protected void addShowObject(GeoElement geo) {
		// Show object item is skipped in spreadsheet
	}

	private void addDuplicate() {
		String cmdString = MenuCommand.Duplicate.toString();
		addMenuItem(cmdString, app.getLocalization().getMenu(cmdString),
				!isEmptySelection());
	}

	@Override
	public void addMenuItem(final String cmdString, String text, boolean enabled) {
		String html;
		if (app.isWhiteboardActive()) {
			html = MainMenu.getMenuBarHtmlClassic(getIconUrlNew(cmdString), text);
		} else {
			html = MainMenu.getMenuBarHtmlClassic(
					getIconUrl(cmdString, app.isUnbundled()), text);
		}

		AriaMenuItem mi;
		mi = new AriaMenuItem(html, true, getCommand(cmdString));
		if (!app.isUnbundledOrWhiteboard()) {
			mi.addStyleName("mi_with_image");
		}
		mi.setEnabled(enabled);

		popup.addItem(mi);
	}

	@Override
	public void addCheckBoxMenuItem(final String cmdString, String nonSelected,
			String selected,
			boolean isSelected) {

		String html;

		if (app.isWhiteboardActive()) {
			html = MainMenu.getMenuBarHtmlClassic(getIconUrlNew(cmdString, isSelected),
					"");
		} else {
			html = MainMenu.getMenuBarHtmlClassic(
					getIconUrl(cmdString, app.isUnbundled()), "");
		}

		GCheckBoxMenuItem cbItem = new GCheckBoxMenuItem(html, selected,
				nonSelected,
				getCommand(cmdString), true, app);
		cbItem.setSelected(isSelected, popup.getPopupMenu());
		popup.addItem(cbItem);
	}

	@Override
	public void addCheckBoxMenuItem(final String cmdString, String text,
	        boolean isSelected) {

		String html;
		if (app.isWhiteboardActive()) {
			html = MainMenu.getMenuBarHtmlClassic(getIconUrlNew(cmdString), text);
		} else {
			html = MainMenu.getMenuBarHtmlClassic(
					getIconUrl(cmdString, app.isUnbundled()), text);
		}

		GCheckBoxMenuItem cbItem = new GCheckBoxMenuItem(html,
				getCommand(cmdString), true, app);
		cbItem.setSelected(isSelected, popup.getPopupMenu());
		popup.addItem(cbItem);
	}

	@Override
	public AriaMenuItem addSubMenu(String text, String cmdString) {

		String html;
		if (app.isWhiteboardActive()) {
			html = MainMenu.getMenuBarHtmlClassic(getIconUrlNew(cmdString), text);
		} else {
			html = MainMenu.getMenuBarHtmlClassic(
					getIconUrl(cmdString, app.isUnbundled()), text);
		}

		AriaMenuBar subMenu = new AriaMenuBar();
		AriaMenuItem menuItem = new AriaMenuItem(html, true, subMenu);

		popup.addItem(menuItem);
		return menuItem;
	}

	@Override
	public void addSubMenuItem(Object menu, final String cmdString,
	        String text, boolean enabled) {

		String html;
		if (app.isWhiteboardActive()) {
			html = MainMenu.getMenuBarHtmlClassic(getIconUrlNew(cmdString), text);
		} else {
			html = MainMenu.getMenuBarHtmlClassic(
					getIconUrl(cmdString, app.isUnbundled()),
					text);
		}

		AriaMenuItem mi = new AriaMenuItem(html, true, getCommand(cmdString));
		mi.addStyleName("mi_with_image");
		mi.setEnabled(enabled);

		((AriaMenuItem) menu).getSubMenu().addItem(mi);

	}

	@Override
	public void addSeparator() {
		if (!app.isUnbundled()) {
			popup.addSeparator();
		}
	}

	private Command getCommand(final String cmdString) {
		Command cmd = new Command() {
			@Override
			public void execute() {
				doCommand(cmdString);
			}
		};
		return cmd;
	}

	private static String getIconUrl(String cmdString, boolean isNewDesign) {

		if (cmdString == null) {
			return AppResources.INSTANCE.empty().getSafeUri().asString();
		}

		ImageResource im = null;

		switch (MenuCommand.valueOf(cmdString)) {

		case ShowObject:
			im = AppResources.INSTANCE.mode_showhideobject_16();
			break;
		case ShowLabel:
			if (isNewDesign) {
				im = new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.label_black()
								.getSafeUri(),
						0, 0, 24, 24, false, false);
			} else {
				im = AppResources.INSTANCE.mode_showhidelabel_16();
			}
			break;
		case Copy:
			if (isNewDesign) {
				im =  new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE
						.copy_black().getSafeUri(),
						0, 0, 24, 24, false, false);
			} else {
				im = SharedResources.INSTANCE.edit_copy();
			}
			break;
		case Cut:
			if (isNewDesign) {
				im = new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.cut_black()
								.getSafeUri(),
						0, 0, 24, 24, false, false);
			} else {
				im = AppResources.INSTANCE.edit_cut();
			}
			break;
		case Paste:
			if (isNewDesign) {
				im =  new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE
								.paste_black().getSafeUri(),
				0, 0, 24, 24, false, false);
			} else {
				im = AppResources.INSTANCE.edit_paste();
			}
			break;
		case Duplicate:
			if (isNewDesign) {
				im = new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.duplicate_black()
								.getSafeUri(),
						0, 0, 24, 24, false, false);
			} else {
				im = AppResources.INSTANCE.duplicate20();
			}
			break;
		case Delete:
		case DeleteObjects:
			if (isNewDesign) {
				im = new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.delete_black()
								.getSafeUri(),
						0, 0, 24, 24, false, false);
			} else {
				im = AppResources.INSTANCE.delete_small();
			}
			break;
		case RecordToSpreadsheet:
			if (isNewDesign) {
				im = new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE
								.record_to_spreadsheet_black().getSafeUri(),
						0, 0, 24, 24, false, false);
			} else {
				im = AppResources.INSTANCE.spreadsheettrace();
			}
			break;
		case Properties:
			if (isNewDesign) {
				im = new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.gear().getSafeUri(), 0,
						0, 24, 24, false, false);
			} else {
				im = AppResources.INSTANCE.view_properties16();
			}
			break;
		case SpreadsheetOptions:
			if (isNewDesign) {
				im = new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.gear().getSafeUri(), 0,
						0, 24, 24, false, false);
			} else {
				im = AppResources.INSTANCE.view_properties16();
			}
			break;
		case Create:
			if (isNewDesign) {
				im = new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.add_black()
						.getSafeUri(),
						0, 0, 24, 24, false, false);
			} else {
				im = AppResources.INSTANCE.empty();
			}
			break;
		default:
			im = AppResources.INSTANCE.empty();
		}
		return im.getSafeUri().asString();
	}

	private static String getIconUrlNew(String cmdString, boolean isSelected) {

		if (cmdString == null) {
			return AppResources.INSTANCE.empty().getSafeUri().asString();
		}

		ImageResource im = null;

		if (MenuCommand.valueOf(cmdString) == MenuCommand.ShowLabel) {
			if (isSelected) {
				im = AppResources.INSTANCE.label_off20();
			} else {
				im = AppResources.INSTANCE.label20();
			}
			return im.getSafeUri().asString();
		}
		return getIconUrlNew(cmdString);
	}

	private static String getIconUrlNew(String cmdString) {

		if (cmdString == null) {
			return AppResources.INSTANCE.empty().getSafeUri().asString();
		}

		ImageResource im = null;

		switch (MenuCommand.valueOf(cmdString)) {

		case ShowObject:
			im = AppResources.INSTANCE.mode_showhideobject_16();
			break;
		case ShowLabel:
			im = AppResources.INSTANCE.label20();
			break;
		case Copy:
			im = AppResources.INSTANCE.copy20();
			break;
		case Cut:
			im = AppResources.INSTANCE.cut20();
			break;
		case Paste:
			im = AppResources.INSTANCE.paste20();
			break;
		case Duplicate:
			im = AppResources.INSTANCE.duplicate20();
			break;
		case Delete:
		case DeleteObjects:
			im = AppResources.INSTANCE.delete20();
			break;
		case RecordToSpreadsheet:
			im = AppResources.INSTANCE.record_to_spreadsheet20();
			break;
		case Properties:
			im = AppResources.INSTANCE.properties20();
			break;
		case SpreadsheetOptions:
			im = AppResources.INSTANCE.properties20();
			break;
		default:
			im = AppResources.INSTANCE.empty();
		}
		return im.getSafeUri().asString();
	}

}
