package org.geogebra.web.html5.gui.util;

import java.util.ArrayList;

import org.geogebra.common.main.App;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/** Accessible alternative to MenuBar */
public class AriaMenuBar extends FlowPanel {
	private AriaMenuItem selectedItem;
	private ArrayList<AriaMenuItem> allItems = new ArrayList<>();
	private ArrayList<AriaMenuBar> submenus = new ArrayList<>();
	private boolean autoOpen;
	private boolean handleArrows = true;

	/**
	 * Create new accessible menu
	 */
	public AriaMenuBar() {
		super("UL");
		sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT
				| Event.ONFOCUS | Event.ONKEYPRESS | Event.ONKEYDOWN);
		getElement().setAttribute("role", "menubar");
		getElement().setTabIndex(0);
		addStyleName("gwt-MenuBar");
		addStyleName("gwt-MenuBar-vertical");
	}

	/**
	 * @param handleArrows
	 *            whsether this menu should handle up/down keys on its own
	 */
	protected void setHandleArrows(boolean handleArrows) {
		this.handleArrows = handleArrows;
	}

	/**
	 * @param item
	 *            menu item
	 * @return the item
	 */
	public AriaMenuItem addItem(AriaMenuItem item) {
		super.add(item);
		allItems.add(item);
		return item;
	}

	/**
	 * Add a collapsible submenu as a new item
	 * 
	 * @param item
	 *            collapsible submenu
	 */
	public void addMenu(AriaMenuBar item) {
		Element li = Document.get().createLIElement();
		li.appendChild(item.getElement());
		getElement().appendChild(li);
		li.setAttribute("aria-hidden", "true");
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
	 * Get a {@link AriaMenuItem} at a given index.
	 * 
	 * @param index
	 *            of the item to get.
	 * 
	 * @return the item at the given index.
	 */
	public AriaMenuItem getItemAt(int index) {
		return allItems.get(index);
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
	 * Get the index of the selected item.
	 *
	 * @return the index of the selected item, or -1 if it is not contained by
	 *         this MenuBar
	 */
	public int getSelectedIndex() {
		return allItems.indexOf(selectedItem);
	}

	/**
	 * Mark item as selected and move focus to it
	 * 
	 * @param item
	 *            item to be selected
	 */
	public void selectItem(AriaMenuItem item) {
		unselect();
		this.selectedItem = item;
		if (item != null) {
			focus(item);
			item.addStyleName("selectedItem");
		}
	}

	/**
	 * Removes selection from previously selected item.
	 */
	public void unselect() {
		if (selectedItem != null) {
			selectedItem.removeStyleName("selectedItem");
		}
	}

	/**
	 * Selects the item at the specified index in the menu. Selecting the item
	 * does not perform the item's associated action; it only changes the style
	 * of the item and updates the value of SuggestionMenu.selectedItem.
	 *
	 * @param index
	 *            index
	 */
	public void selectItem(int index) {
		if (index >= 0 && index < allItems.size()) {
			selectItem(allItems.get(index));
		}
	}

	/**
	 * Move focus to an item, may be overriden
	 * 
	 * @param item
	 *            item to move focus to
	 */
	protected void focus(AriaMenuItem item) {
		item.getElement().focus();
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
		if (next >= allItems.size()) {
			next = 0;
		}
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
		if (next < 0) {
			next = allItems.size() - 1;
		}
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
		case Event.ONCLICK:
			// TODOFocusPanel.impl.focus(getElement());
			// Fire an item's command when the user clicks on it.
			if (item != null) {
				doItemAction(item);
			}
			break;
		case Event.ONTOUCHSTART:
		case Event.ONMOUSEOVER:
			if (item != null) {
				itemOver(item);
			}
			break;

		case Event.ONMOUSEOUT:
			if (item != null) {
				itemOver(null);
			}
			break;

		case Event.ONFOCUS: {
			// selectFirstItemIfNoneSelected();
			break;
		}

		case Event.ONKEYPRESS:
			handleActionKey(event, item);
			break;

		case Event.ONKEYDOWN:
			int keyCode = event.getKeyCode();
			if (keyCode == KeyCodes.KEY_UP && handleArrows) {
				moveSelectionUp();
				eatEvent(event);
				return;
			}
			else if (keyCode == KeyCodes.KEY_DOWN && handleArrows) {
				moveSelectionDown();
				eatEvent(event);
				return;
			}
			break;
		} // end switch (DOM.eventGetType(event))

		super.onBrowserEvent(event);
	}

	private void handleActionKey(Event event, AriaMenuItem item) {
		if (!isActionKey(event.getKeyCode()) || item == null) {
			return;
		}

		doItemAction(item);
		eatEvent(event);
	}

	private boolean isActionKey(int keyCode) {
		return keyCode == KeyCodes.KEY_ENTER
				|| keyCode == KeyCodes.KEY_SPACE;
	}

	/**
	 * @return application
	 */
	protected App getApp() {
		// overwritten is subclasses
		return null;
	}

	/**
	 * Stops event propagation and prevents default behavior.
	 * 
	 * @param event
	 *            to eat.
	 */
	public static void eatEvent(Event event) {
		event.stopPropagation();
		event.preventDefault();
	}

	private AriaMenuItem findItem(Element eventTarget) {
		for (AriaMenuItem item : allItems) {
			if (item.getElement().isOrHasChild(eventTarget)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Executes the command associated with the item selected.
	 */
	public void doSelectedItemAction() {
		if (selectedItem == null) {
			return;
		}
		doItemAction(selectedItem);
	}

	private static void doItemAction(AriaMenuItem item) {
		final ScheduledCommand cmd = item.getScheduledCommand();
		if (!item.isEnabled() || cmd == null) {
			return;
		}

		Scheduler.get().scheduleFinally(cmd);
	}

	/**
	 * @param item
	 *            item mouse is hovering over
	 */
	protected void itemOver(AriaMenuItem item) {
		if (item != null) {
			removeSubPopup();
			selectItem(item);
		}
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
	public void appendSubmenu(AriaMenuItem newItem, ResourcePrototype imgRes) {
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
	public int getAbsoluteHorizontalPos(AriaMenuItem item, boolean subleft) {
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
	 * Clears default menu role. Useful for submenus.
	 */
	public void clearRole() {
		getElement().setAttribute("role", "");
	}

	/**
	 * Selects the last item of the menubar.
	 */
	public void selectLastItem() {
		selectItem(allItems.get(allItems.size() - 1));
	}

	/**
	 * Style popup menu appears
	 * @param widget to style.
	 */
	public void stylePopup(Widget widget) {
		// implement in subclasses if needed
	}
}
