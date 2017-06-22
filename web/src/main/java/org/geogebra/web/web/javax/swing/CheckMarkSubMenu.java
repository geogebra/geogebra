package org.geogebra.web.web.javax.swing;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.web.web.css.MaterialDesignResources;

import com.google.gwt.user.client.Command;

/**
 * @author csilla
 *
 */
public abstract class CheckMarkSubMenu {
	private GPopupMenuW wrappedPopup;
	private List<GCheckmarkMenuItem> items;
	private String checkmarkUrl;
	/**
	 * parent menu item of this
	 */
	protected GCollapseMenuItem parentMenu;

	/**
	 * @param wrappedPopup
	 *            - pupup menu
	 * @param parentMenu
	 *            - parent of wrappedPopup
	 */
	public CheckMarkSubMenu(GPopupMenuW wrappedPopup,
			GCollapseMenuItem parentMenu) {
		this.wrappedPopup = wrappedPopup;
		this.parentMenu = parentMenu;
		// super(true, "", app);
		checkmarkUrl = MaterialDesignResources.INSTANCE.check_black()
				.getSafeUri().asString();
		// addStyleName("GeoGebraMenuBar");
		// addStyleName("floating-Popup");
		// addStyleName("dotSubMenu");
		items = new ArrayList<GCheckmarkMenuItem>();
		initActions();
		parentMenu.collapse();
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
	 */
	public void addItem(String text, boolean selected, Command command) {
		GCheckmarkMenuItem cm = new GCheckmarkMenuItem(text, checkmarkUrl,
				selected, command);
		wrappedPopup.addItem(cm.getMenuItem());
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
