package org.geogebra.web.full.javax.swing;

import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.InlineLabel;

/**
 * Adds a menu item with a checkmark on its end.
 * 
 * @author laszlo
 * 
 */
public class GCollapseMenuItem {

	private AriaMenuItem menuItem;
	private AriaMenuBar items;
	private FlowPanel itemPanel;
	private boolean expanded;
	private SVGResource icon;
	private Image imgExpand;
	private Image imgCollapse;
	private GPopupMenuW parentMenu;
	private String title;

	/**
	 * @param icon
	 *            icon
	 * @param title
	 *            Title
	 * @param expandUrl
	 *            image of expand
	 * @param collapseUrl
	 *            image of collapse
	 * @param expanded
	 *            initial value.
	 * @param wrappedPopup
	 *            The command to run.
	 */
	public GCollapseMenuItem(SVGResource icon, String title, String expandUrl,
			String collapseUrl,
			boolean expanded, final GPopupMenuW wrappedPopup) {
		this.icon = icon;
		this.title = title;
		imgExpand = new NoDragImage(expandUrl);
		imgExpand.setStyleName("expandImg");
		imgCollapse = new NoDragImage(collapseUrl);
		imgCollapse.addStyleName("collapseImg");

		items = new AriaMenuBar();
		items.clearRole();
		items.addStyleName("collapseSubMenu");

		itemPanel = new FlowPanel();
		itemPanel.addStyleName("collapseMenuItem");
		this.parentMenu = wrappedPopup;
		menuItem = new AriaMenuItem(itemPanel,
				this::toggle);
		setExpanded(expanded);
	}

	/**
	 * Sets the item checked/unchecked.
	 * 
	 * @param value
	 *            to set.
	 */
	public void setExpanded(boolean value) {
		expanded = value;
		itemPanel.clear();
		itemPanel.add(new NoDragImage(icon, 24));
		itemPanel.add(new InlineLabel(title));
		itemPanel.add(expanded ? imgCollapse : imgExpand);
		menuItem.getElement().setAttribute("aria-expanded",
				String.valueOf(expanded));
		if (items.getElement().getParentElement() != null) {
			items.getElement().getParentElement().getStyle()
					.setProperty("listStyle", "none");
			items.getElement().getParentElement().setAttribute("aria-hidden",
					String.valueOf(!expanded));
		}
		menuItem.getElement().setAttribute("aria-label",
				title + (value ? " expanded" : " collapsed"));
		updateItems();

		items.getElement().setTabIndex(-1);
	}

	/**
	 * Attach to parent menu and collapse this
	 */
	public void attachToParent() {
		parentMenu.getPopupMenu().addMenu(items);
		setExpanded(false);
	}

	/**
	 * 
	 * @return The standard menu item with checkmark.
	 */
	public AriaMenuItem getMenuItem() {
		return menuItem;
	}

	/**
	 * 
	 * @return if the menu is expanded or not.
	 */
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * Toggles the menu - expand/collapse.
	 */
	public void toggle() {
		setExpanded(!expanded);
	}

	/**
	 * Collapse submenu
	 */
	public void updateItems() {
		for (AriaMenuItem mi : items.getItems()) {
			Dom.toggleClass(mi, "expanded", "collapsed", expanded);
			AriaHelper.setHidden(mi, !expanded);
			mi.getElement().setTabIndex(expanded ? 0 : -1);
		}
	}

	/**
	 * 
	 * @param item
	 *            to add.
	 */
	public void addItem(AriaMenuItem item) {
		items.addItem(item);
	}

	/**
	 * @return the collapsed items wrapped in a submenu
	 */
	public AriaMenuBar getItems() {
		return items;
	}
}
