package org.geogebra.web.full.javax.swing;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;

/**
 * @author csilla
 *
 */
public abstract class CheckMarkSubMenu {
	private List<GCheckmarkMenuItem> items;
	/**
	 * parent menu item of this
	 */
	protected GCollapseMenuItem parentMenu;

	/**
	 * @param parentMenu
	 *            - parent of wrappedPopup
	 */
	public CheckMarkSubMenu(
			GCollapseMenuItem parentMenu) {
		this.parentMenu = parentMenu;
		items = new ArrayList<>();
		initActions();
	}

	/**
	 * Adds a menu item with checkmark
	 * 
	 * @param text
	 *            of the item
	 * @param selected
	 *            if checkmark should be shown or not
	 * @param command
	 *            to execute when selected.
	 * @param withImg
	 *            true if menu item does have img
	 */
	public void addItem(String text, boolean selected, Command command,
			boolean withImg) {
		GCheckmarkMenuItem cm = new GCheckmarkMenuItem(text,
				selected, command);
		if (withImg) {
			cm.getMenuItem().addStyleName("withImg");
		}
		// wrappedPopup.addItem(cm.getMenuItem());
		items.add(cm);
		parentMenu.addItem(cm.getMenuItem());
	}

	/**
	 * @return nr of items
	 */
	public int itemCount() {
		return items.size();
	}

	/**
	 * @param idx
	 *            - index
	 * @return item at idx
	 */
	public GCheckmarkMenuItem itemAt(int idx) {
		return items.get(idx);
	}

	/**
	 * handle the update
	 */
	public abstract void update();

	/**
	 * init
	 */
	protected abstract void initActions();

}
