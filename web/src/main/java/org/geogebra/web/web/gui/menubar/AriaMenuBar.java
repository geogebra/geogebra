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
	private MenuItem selectedItem;
	private HashMap<MenuItem, Element> domItems = new HashMap<MenuItem, Element>();
	private ArrayList<MenuItem> allItems = new ArrayList<MenuItem>();
	private boolean autoOpen;

	public AriaMenuBar() {
		setElement(Document.get().createULElement());
		sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT
				| Event.ONFOCUS | Event.ONKEYDOWN);
		getElement().setAttribute("role", "menubar");
		getElement().setTabIndex(0);
		addStyleName("gwt-MenuBar");
		addStyleName("gwt-MenuBar-vertical");
	}

	public MenuItem addItem(MenuItem a) {
		Element li = DOM.createElement("LI");
		li.setInnerHTML(a.getElement().getInnerHTML());
		li.setClassName("gwt-MenuItem listMenuItem");
		li.setAttribute("role", "menuitem");
		li.setTabIndex(0);
		getElement().appendChild(li);
		allItems.add(a);
		domItems.put(a, li);
		return a;
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
	public MenuItem addItem(String text, boolean asHTML, ScheduledCommand cmd) {
		return addItem(new MenuItem(text, asHTML, cmd));
	}

	public void focus() {
		getElement().focus();
	}

	protected MenuItem getSelectedItem() {
		return this.selectedItem;
	}

	/**
	 * Get the index of a {@link MenuItem}.
	 *
	 * @return the index of the item, or -1 if it is not contained by this
	 *         MenuBar
	 */
	public int getItemIndex(MenuItem item) {
		return allItems.indexOf(item);
	}

	public void selectItem(MenuItem item) {
		if (selectedItem != null) {
			domItems.get(selectedItem).removeClassName("gwt-MenuItem-selected");
		}
		this.selectedItem = item;
		if (item != null) {
			domItems.get(item).addClassName("gwt-MenuItem-selected");
			domItems.get(item).focus();
		}
	}

	public void clearItems() {
		allItems.clear();
		domItems.clear();
		getElement().removeAllChildren();
		selectItem(null);
	}

	public ArrayList<MenuItem> getItems() {
		return allItems;
	}

	public boolean moveSelectionDown() {
		int next = allItems.indexOf(selectedItem) + 1;
		if (next < allItems.size()) {
			selectItem(allItems.get(next));
			return true;
		}
		return false;
	}

	public boolean moveSelectionUp() {
		int next = allItems.indexOf(selectedItem) - 1;
		if (next >= 0 && next < allItems.size()) {
			selectItem(allItems.get(next));
			return true;
		}
		return false;
	}

	public void addSeparator() {
		Element li = DOM.createElement("LI");
		li.setClassName("menuSeparator");
		li.setAttribute("role", "presentation");
		getElement().appendChild(li);
	}

	public void setAutoOpen(boolean autoOpen) {
		this.autoOpen = autoOpen;
	}

	public boolean isAutoOpen() {
		return autoOpen;
	}

	@Override
	public void onBrowserEvent(Event event) {
		MenuItem item = findItem(DOM.eventGetTarget(event));
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

	private void eatEvent(Event event) {
		event.stopPropagation();
		event.preventDefault();
	}

	private MenuItem findItem(
			Element eventTarget) {
		for(MenuItem it: allItems){
			if (domItems.get(it).isOrHasChild(eventTarget)) {
				return it;
			}
		}
		return null;
	}

	private void doItemAction(MenuItem item) {
		final ScheduledCommand cmd = item.getScheduledCommand();
		Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				cmd.execute();
			}
		});

	}

	private void itemOver(MenuItem item) {
		selectItem(item);
		// if (item != null) {
		// if ((shownChildMenu != null) || (parentMenu != null) || autoOpen) {
		// doItemAction(item, false, focusOnHover);
		// }
		// }
	}

	public void appendSubmenu(MenuItem newItem, ImageResource imgRes) {
		Element li = domItems.get(newItem);
		NoDragImage img = new NoDragImage(imgRes, 20, 20);
		AriaHelper.hide(img);
		img.addStyleName("submenuArrow");
		li.appendChild(img.getElement());

	}

	public int getAbsoluteTop(MenuItem mi0) {
		return domItems.get(mi0).getAbsoluteTop();
	}

	protected int getAbsoluteHorizontalPos(MenuItem mi0, boolean subleft) {
		return subleft ? domItems.get(mi0).getAbsoluteLeft()
				: domItems.get(mi0).getAbsoluteRight() + 8;
	}

	public void addStyleName(MenuItem item, String className) {
		domItems.get(item).addClassName(className);
	}

	public void removeStyleName(MenuItem item, String className) {
		domItems.get(item).removeClassName(className);
	}
}
