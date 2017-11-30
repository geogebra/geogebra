package org.geogebra.web.html5.gui.util;

import java.util.ArrayList;

import org.geogebra.common.main.App;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**Accessible alternative to MenuBar*/
public class AriaMenuBar extends Widget {
	private AriaMenuItem selectedItem;
	private ArrayList<AriaMenuItem> allItems = new ArrayList<>();
	private ArrayList<AriaMenuBar> submenus = new ArrayList<>();
	private boolean autoOpen;
	private boolean focusOnHover;

	/**
	 * Create new accessible menu
	 */
	public AriaMenuBar() {
		setElement(Document.get().createULElement());
		sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT
				| Event.ONFOCUS | Event.ONKEYDOWN);
		getElement().setAttribute("role", "menubar");
		getElement().setTabIndex(0);
		addStyleName("gwt-MenuBar");
		addStyleName("gwt-MenuBar-vertical");
	}

	/**
	 * @param item
	 *            menu item
	 * @return the item
	 */
	public AriaMenuItem addItem(AriaMenuItem item) {
		getElement().appendChild(item.getElement());
		allItems.add(item);
		return item;
	}

	public void addMenu(AriaMenuBar item) {
		Element li = Document.get().createLIElement();
		li.appendChild(item.getElement());
		getElement().appendChild(li);
		submenus.add(item);
	}

	/**
	 * Adds a menu item to the bar, that will fire the given command when it is
	 * selected.
	 *
	 * @param text
	 *            the item's text
	 * @param asHTML
	 *            <code>true</code> to treat the specified text as html
	 * @param cmd
	 *            the command to be fired
	 * @return the {@link AriaMenuItem} object created
	 */
	public AriaMenuItem addItem(String text, boolean asHTML,
			ScheduledCommand cmd) {
		return addItem(new AriaMenuItem(text, asHTML, cmd));
	}

	/**
	 * Focus the whole menu
	 */
	public void focus() {
		getElement().focus();
	}

	/**
	 * @return selected item (may be null)
	 */
	protected AriaMenuItem getSelectedItem() {
		return this.selectedItem;
	}

	/**
	 * Get the index of a {@link AriaMenuItem}.
	 * 
	 * @param item
	 *            item we are looking for
	 *
	 * @return the index of the item, or -1 if it is not contained by this
	 *         MenuBar
	 */
	public int getItemIndex(AriaMenuItem item) {
		return allItems.indexOf(item);
	}

	/**
	 * Mark item as selected and move focus to it
	 * 
	 * @param item
	 *            item to be selected
	 */
	public void selectItem(AriaMenuItem item) {
		if (selectedItem != null) {
			selectedItem.removeStyleName("gwt-MenuItem-selected");
		}
		this.selectedItem = item;
		if (item != null) {
			item.addStyleName("gwt-MenuItem-selected");
			item.getElement().focus();
		}
	}

	/**
	 * Remove all items
	 */
	public void clearItems() {
		allItems.clear();
		submenus.clear();
		getElement().removeAllChildren();
		selectItem(null);
	}

	/**
	 * @return list of all items
	 */
	public ArrayList<AriaMenuItem> getItems() {
		return allItems;
	}

	/**
	 * Set next item as selected
	 * 
	 * @return whether it was possible
	 */
	public boolean moveSelectionDown() {
		int next = allItems.indexOf(selectedItem) + 1;
		if (next < allItems.size()) {
			selectItem(allItems.get(next));
			return true;
		}
		return false;
	}

	/**
	 * Set previous item as selected
	 * 
	 * @return whether it was possible
	 */
	public boolean moveSelectionUp() {
		int next = allItems.indexOf(selectedItem) - 1;
		if (next >= 0 && next < allItems.size()) {
			selectItem(allItems.get(next));
			return true;
		}
		return false;
	}

	/**
	 * Add separator item
	 */
	public void addSeparator() {
		Element li = DOM.createElement("LI");
		li.setClassName("menuSeparator");
		li.setAttribute("role", "presentation");
		getElement().appendChild(li);
	}

	/**
	 * @param autoOpen
	 *            whether submenu should open on mouseover
	 */
	public void setAutoOpen(boolean autoOpen) {
		this.autoOpen = autoOpen;
	}

	/**
	 * @return whether submenu should open on mouseover
	 */
	public boolean isAutoOpen() {
		return autoOpen;
	}

	@Override
	public void onBrowserEvent(Event event) {
		AriaMenuItem item = findItem(DOM.eventGetTarget(event));
		for (AriaMenuBar submenu : submenus) {
			if (item != null) {
				break;
			}
			item = submenu.findItem(DOM.eventGetTarget(event));
		}
		switch (DOM.eventGetType(event)) {
		case Event.ONCLICK: {
			// TODOFocusPanel.impl.focus(getElement());
			// Fire an item's command when the user clicks on it.
			if (item != null) {
				doItemAction(item);
			}
			break;
		}

		case Event.ONMOUSEOVER: {
			if (item != null) {
				itemOver(item);
			}
			break;
		}

		case Event.ONMOUSEOUT: {
			if (item != null) {
				itemOver(null);
			}
			break;
		}

		case Event.ONFOCUS: {
			// selectFirstItemIfNoneSelected();
			break;
		}

		case Event.ONKEYDOWN: {
			int keyCode = event.getKeyCode();
			switch (keyCode) {
			case KeyCodes.KEY_X:
				if (event.getAltKey() && event.getCtrlKey()) {
					App app = getApp();
					if (app != null) {
						app.getAccessibilityManager().focusInput(true);
					}
					eatEvent(event);
				}
				break;
			default:
				break;
			}

			break;
		} // end case Event.ONKEYDOWN
		} // end switch (DOM.eventGetType(event))
		super.onBrowserEvent(event);
	}

	/**
	 * @return application
	 */
	protected App getApp() {
		// overwritten is subclasses
		return null;
	}

	private static void eatEvent(Event event) {
		event.stopPropagation();
		event.preventDefault();
	}

	private AriaMenuItem findItem(
			Element eventTarget) {
		for (AriaMenuItem item : allItems) {
			if (item.getElement().isOrHasChild(eventTarget)) {
				return item;
			}
		}
		return null;
	}

	private static void doItemAction(AriaMenuItem item) {
		final ScheduledCommand cmd = item.getScheduledCommand();
		if (!item.isEnabled()) {
			return;
		}
		Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				cmd.execute();
			}
		});

	}

	/**
	 * @param item
	 *            item mouse is hovering over
	 */
	protected void itemOver(AriaMenuItem item) {
		selectItem(item);
		removeSubPopup();
		if (item != null
				&& "true".equals(item.getElement().getAttribute("hasPopup"))
				&& autoOpen) {
			doItemAction(item);
		}
	}

	/**
	 * @param newItem
	 *            item with submenu
	 * @param imgRes
	 *            submenu arrow icon
	 */
	public void appendSubmenu(AriaMenuItem newItem, ImageResource imgRes) {
		NoDragImage img = new NoDragImage(imgRes, 20, 20);
		AriaHelper.hide(img);
		img.addStyleName("submenuArrow");
		newItem.getElement().appendChild(img.getElement());
	}

	/**
	 * @param item
	 *            item
	 * @param subleft
	 *            whether submenu icon is on the left
	 * @return horizontal coordinate of menu
	 */
	protected int getAbsoluteHorizontalPos(AriaMenuItem item, boolean subleft) {
		return subleft ? item.getElement().getAbsoluteLeft()
				: item.getElement().getAbsoluteRight() + 8;
	}

	/**
	 * Remove currently open submenu from DOM
	 */
	public void removeSubPopup() {
		// needs override
	}

	/**
	 * @param focusOnHover
	 *            whether mouseover should select items
	 */
	public void setFocusOnHoverEnabled(boolean focusOnHover) {
		this.focusOnHover = focusOnHover;		
	}

	/**
	 * @return whether mouseover should select items
	 */
	public boolean getFocusOnHover() {
		return focusOnHover;
	}
}
