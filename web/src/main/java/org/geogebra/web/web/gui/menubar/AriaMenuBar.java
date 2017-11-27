package org.geogebra.web.web.gui.menubar;

import java.util.ArrayList;
import java.util.HashMap;

import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.NoDragImage;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

/**Accessible alternative to MenuBar*/
public class AriaMenuBar extends Widget {
	private AriaMenuItem selectedItem;
	private HashMap<AriaMenuItem, Element> domItems = new HashMap<AriaMenuItem, Element>();
	private ArrayList<AriaMenuItem> allItems = new ArrayList<AriaMenuItem>();
	private boolean autoOpen;

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
		domItems.put(item, item.getElement());
		return item;
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
	 * @return the {@link MenuItem} object created
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
	 * Get the index of a {@link MenuItem}.
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
			removeStyleName(selectedItem, "gwt-MenuItem-selected");
		}
		this.selectedItem = item;
		if (item != null && domItems.get(item) != null) {
			domItems.get(item).addClassName("gwt-MenuItem-selected");
			domItems.get(item).focus();
		}
	}

	/**
	 * Remove all items
	 */
	public void clearItems() {
		allItems.clear();
		domItems.clear();
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
			// TODO: KeyCodes.maybeSwapArrowKeysForRtl(
			switch (keyCode) {
			// case KeyCodes.KEY_LEFT:
			// moveToPrevItem();
			// eatEvent(event);
			// break;
			// case KeyCodes.KEY_RIGHT:
			// moveToNextItem();
			// eatEvent(event);
			// break;
			case KeyCodes.KEY_UP:
				if (moveSelectionUp()) {
					eatEvent(event);
				}
				break;
			case KeyCodes.KEY_DOWN:
				if (moveSelectionDown()) {
					eatEvent(event);
				}
				break;
			default:
				break;
			// case KeyCodes.KEY_TAB:
			// closeAllParentsAndChildren();
			// break;
			// case KeyCodes.KEY_ENTER:
			// if (!selectFirstItemIfNoneSelected()) {
			// doItemAction(selectedItem, true, true);
			// eatEvent(event);
			// }
			// break;
			} // end switch(keyCode)

			break;
		} // end case Event.ONKEYDOWN
		} // end switch (DOM.eventGetType(event))
		super.onBrowserEvent(event);
	}

	private static void eatEvent(Event event) {
		event.stopPropagation();
		event.preventDefault();
	}

	private AriaMenuItem findItem(
			Element eventTarget) {
		for (AriaMenuItem it : allItems) {
			if (domItems.get(it).isOrHasChild(eventTarget)) {
				return it;
			}
		}
		return null;
	}

	private static void doItemAction(AriaMenuItem item) {
		final ScheduledCommand cmd = item.getScheduledCommand();
		Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				cmd.execute();
			}
		});

	}

	private void itemOver(AriaMenuItem item) {
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
		Element li = domItems.get(newItem);
		NoDragImage img = new NoDragImage(imgRes, 20, 20);
		AriaHelper.hide(img);
		img.addStyleName("submenuArrow");
		li.appendChild(img.getElement());
	}

	/**
	 * @param item
	 *            item
	 * @return absolute top of the item
	 */
	public int getAbsoluteTop(AriaMenuItem item) {
		return domItems.get(item) == null ? 0
				: domItems.get(item).getAbsoluteTop();
	}

	/**
	 * @param item
	 *            item
	 * @param subleft
	 *            whether submenu icon is on the left
	 * @return horizontal coordinate of menu
	 */
	protected int getAbsoluteHorizontalPos(AriaMenuItem item, boolean subleft) {
		if (domItems.get(item) == null) {
			return 0;
		}
		return subleft ? domItems.get(item).getAbsoluteLeft()
				: domItems.get(item).getAbsoluteRight() + 8;
	}

	/**
	 * Adds a class name to element representing given item
	 * 
	 * @param item
	 *            item to be changed
	 * @param className
	 *            CSS class name
	 */
	public void addStyleName(AriaMenuItem item, String className) {
		if (domItems.get(item) != null) {
			domItems.get(item).addClassName(className);
		}
	}

	/**
	 * Removes a class name of element representing given item
	 * 
	 * @param item
	 *            item to be changed
	 * @param className
	 *            CSS class name
	 */
	public void removeStyleName(AriaMenuItem item, String className) {
		if (domItems.get(item) != null) {
			domItems.get(item).removeClassName(className);
		}
	}

	public void removeSubPopup() {
		// needs override
	}
}
