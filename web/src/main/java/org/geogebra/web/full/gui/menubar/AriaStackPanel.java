package org.geogebra.web.full.gui.menubar;

import java.util.ArrayList;

import org.geogebra.web.html5.Browser;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.annotations.IsSafeHtml;
import com.google.gwt.safehtml.shared.annotations.SuppressIsSafeHtmlCastCheck;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * List implementation of GWT StackPanel with aria support.
 *
 * @author Zbynek, Laszlo
 *
 */
public class AriaStackPanel extends ComplexPanel
		implements StackPanelInterface {
	private static final String DEFAULT_STYLENAME = "gwt-StackPanel";
	private static final String DEFAULT_ITEM_STYLENAME = DEFAULT_STYLENAME
			+ "Item";

	private int visibleStack = -1;
	private int lastVisibleStack = -1;
	private UListElement ul;
	private ArrayList<Widget> items = new ArrayList<>();
	private ArrayList<Element> headers = new ArrayList<>();
	private ArrayList<Element> contents = new ArrayList<>();

	/**
	 * Creates an empty stack panel.
	 **/
	public AriaStackPanel() {
		ul = Document.get().createULElement();
		setElement(ul);
		addStyleName("gwt-StackPanel");
		sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT
				| Event.ONFOCUS | Event.ONKEYDOWN);
	}

	@Override
	public void add(Widget w) {
		insert(w, getWidgetCount());
	}

	/**
	 * Adds a new child with the given widget and header.
	 *
	 * @param w
	 *            the widget to be added
	 * @param stackText
	 *            the header text associated with this widget
	 */
	@Override
	@SuppressIsSafeHtmlCastCheck
	public void add(Widget w, String stackText) {
		add(w, stackText, false);
	}

	/**
	 * Adds a new child with the given widget and header, optionally
	 * interpreting the header as HTML.
	 *
	 * @param w
	 *            the widget to be added
	 * @param stackHtml
	 *            the header html associated with this widget
	 */
	@Override
	public void add(Widget w, SafeHtml stackHtml) {
		add(w, stackHtml.asString(), true);

	}

	/**
	 * Adds a new child with the given widget and header, optionally
	 * interpreting the header as HTML.
	 *
	 * @param w
	 *            the widget to be added
	 * @param stackText
	 *            the header text associated with this widget
	 * @param asHTML
	 *            <code>true</code> to treat the specified text as HTML
	 */
	@Override
	public void add(Widget w, @IsSafeHtml String stackText, boolean asHTML) {
		add(w);
		items.add(w);
		setStackText(getWidgetCount() - 1, stackText);
	}

	/**
	 * Gets the currently selected child index.
	 *
	 * @return selected child
	 */
	@Override
	public int getSelectedIndex() {
		return visibleStack;
	}

	/**
	 * @return selected index or last oone if all collapsed
	 */
	@Override
	public int getLastSelectedIndex() {
		return visibleStack == -1 ? lastVisibleStack : visibleStack;
	}

	/**
	 * @param widget
	 *            widget
	 * @param beforeIndex
	 *            index
	 */
	public void insert(IsWidget widget, int beforeIndex) {
		insert(asWidgetOrNull(widget), beforeIndex);
	}

	@Override
	public void insert(Widget w, int beforeIndex) {
		// header
		Element li = DOM.createElement("LI");
		getElement().appendChild(li);

		Element button = DOM.createElement("button");

		if (!(Browser.isIPad())) {
			li.setAttribute("role", "menuitem");
		}

		button.setAttribute("role", "menuitem");
		button.setAttribute("type", "button");

		li.setAttribute("hasPopup", "true");
		li.appendChild(button);
		button.setTabIndex(0);
		headers.add(button);

		Element content = DOM.createElement("DIV");
		items.add(beforeIndex, w);
		content.appendChild(w.getElement());
		contents.add(content);
		li.appendChild(content);
		ul.appendChild(li);

		// header styling
		setStyleName(button, DEFAULT_ITEM_STYLENAME, true);
		button.setPropertyInt("__owner", hashCode());
		button.setPropertyInt("__index", beforeIndex);
		content.setPropertyInt("__index", beforeIndex);
		w.getElement().setPropertyInt("__index", beforeIndex);

		updateIndicesFrom(beforeIndex);

		// body styling
		setStyleName(content, DEFAULT_STYLENAME + "Content", true);
		content.setPropertyString("height", "100%");

		// Correct visible stack for new location.
		if (visibleStack == -1) {
			showStack(0);
		} else {
			setStackVisible(beforeIndex, false);
			if (visibleStack >= beforeIndex) {
				setVisibleStack(visibleStack + 1);
			}
			// Reshow the stack to apply style names
			setStackVisible(visibleStack, true);
		}

	}

	@Override
	public boolean remove(int index) {
		return remove(getWidget(index), index);
	}

	@Override
	public boolean remove(Widget child) {
		return remove(child, getWidgetIndex(child));
	}

	@Override
	public Widget getWidget(int index) {
		return items.get(index);
	}

	/**
	 * Sets the text associated with a child by its index.
	 *
	 * @param index
	 *            the index of the child whose text is to be set
	 * @param text
	 *            the text to be associated with it
	 */
	@Override
	@SuppressIsSafeHtmlCastCheck
	public void setStackText(int index, String text) {
		setStackText(index, text, false);
	}

	/**
	 * Sets the html associated with a child by its index.
	 *
	 * @param index
	 *            the index of the child whose text is to be set
	 * @param html
	 *            the html to be associated with it
	 */
	@Override
	public void setStackText(int index, SafeHtml html) {
		setStackText(index, html.asString(), true);
	}

	/**
	 * Sets the text associated with a child by its index.
	 *
	 * @param index
	 *            the index of the child whose text is to be set
	 * @param text
	 *            the text to be associated with it
	 * @param asHTML
	 *            <code>true</code> to treat the specified text as HTML
	 */
	@Override
	public void setStackText(int index, @IsSafeHtml String text,
			boolean asHTML) {
		if (index >= getWidgetCount()) {
			return;
		}
		headers.get(index).setInnerHTML(text);
	}

	/**
	 * Shows the widget at the specified child index.
	 *
	 * @param index
	 *            the index of the child to be shown
	 */
	@Override
	public void showStack(int index) {
		if ((index >= getWidgetCount()) || (index < 0)
				|| (index == visibleStack)) {
			return;
		}

		if (visibleStack >= 0) {
			setStackVisible(visibleStack, false);
		}

		setVisibleStack(index);
		setStackVisible(visibleStack, true);
	}

	/**
	 * Adds the {@code styleName} on the {@code
	 * <tr>
	 * } for the header specified by {@code index}.
	 *
	 * @param index
	 *            the index of the header row to apply to the style to
	 * @param styleName
	 *            the name of the class to add
	 */
	@Override
	public void addHeaderStyleName(int index, String styleName) {
		if (index >= getWidgetCount()) {
			return;
		}
		headers.get(index).addClassName(styleName);
	}

	/**
	 * Removes the {@code styleName} off the {@code
	 * <tr>
	 * } for the header specified by {@code index}.
	 *
	 * @param index
	 *            the index of the header row to remove the style from
	 * @param styleName
	 *            the name of the class to remove
	 */
	@Override
	public void removeHeaderStyleName(int index, String styleName) {
		if (index >= getWidgetCount()) {
			return;
		}
		headers.get(index).removeClassName(styleName);
	}

	@Override
	public int getWidgetCount() {
		return headers.size();
	}

	/**
	 * @param target
	 *            DOM element
	 * @return item that's parent of given element
	 */
	protected int findDividerIndex(Element target) {
		Element elem = target;
		String expando = null;
		while (elem != null) {
			expando = elem.getPropertyString("__index");
			if (expando != null) {
				int index = headers.indexOf(elem);
				return index;
			}
			elem = elem.getParentElement();
		}
		return -1;
	}

	private boolean remove(Widget child, int index) {
		// Make sure to call this before disconnecting the DOM.
		boolean removed = super.remove(child);
		removeFromLists(index);
		return removed;
	}

	private void removeFromLists(int index) {
		if (index > -1) {
			headers.remove(index);
			contents.remove(index);
			items.remove(index);
		}
	}

	/**
	 * @param menu
	 *            stack menu
	 */
	public void removeStack(Widget menu) {
		int index = items.indexOf(menu);
		if (index >= 0 && index < headers.size()) {
			headers.get(index).getParentElement().removeFromParent();
			removeFromLists(index);
			if (index == visibleStack) {
				visibleStack = -1;
				lastVisibleStack = -1;
			}
		}
		updateIndicesFrom(0);
	}

	/**
	 * @param index
	 *            index
	 * @param visible
	 *            whether to show
	 */
	protected void setStackVisible(int index, boolean visible) {
		if (index < 0 || index >= headers.size()) {
			return;
		}
		Element header = headers.get(index);
		Element content = contents.get(index);
		setStyleName(header, DEFAULT_ITEM_STYLENAME + "-selected", visible);
		if (visible) {
			items.get(index).setVisible(true);
		}
		UIObject.setVisible(content, visible);
		int nextIdx = index + 1;
		if (nextIdx < headers.size()) {
			setStyleName(headers.get(nextIdx),
					DEFAULT_ITEM_STYLENAME + "-below-selected", visible);
		}
	}

	private void updateIndicesFrom(int beforeIndex) {
		Element header = headers.get(beforeIndex);
		if (beforeIndex == 0) {
			setStyleName(header, DEFAULT_ITEM_STYLENAME + "-first", true);
		} else {
			setStyleName(header, DEFAULT_ITEM_STYLENAME + "-first", false);
		}
	}

	/** Close all stacks */
	public void closeAll() {
		setStackVisible(visibleStack, false);
		this.visibleStack = -1;
	}

	@Override
	public void onBrowserEvent(Event event) {
		int eventType = DOM.eventGetType(event);

		Element target = DOM.eventGetTarget(event);
		int index = findDividerIndex(target);
		if (eventType == Event.ONMOUSEOVER || eventType == Event.ONKEYDOWN
				|| eventType == Event.ONCLICK) {

			if (index > -1) {
				headers.get(index).focus();
			} else {
				int idx = getContentIndex(target);
				Widget content = idx == -1 ? null : getWidget(idx);
				if (content != null) {
					content.onBrowserEvent(event);
				}
			}
		}
		super.onBrowserEvent(event);
	}

	/**
	 * @param target
	 *            event target
	 * @return index of parent element
	 */
	public int getContentIndex(Element target) {
		Element current = target.getParentElement();
		while (current != null) {
			String idx = current.getPropertyString("__index");
			if (idx != null) {
				return Integer.parseInt(idx);
			}
			current = current.getParentElement();
		}
		return -1;
	}

	/**
	 * Sets the label that screen reader reads to the stack at particular index.
	 *
	 * @param index
	 *            of the stack.
	 * @param label
	 *            to set.
	 * @param expanded
	 *            determines if stack is expanded or collapsed.
	 */
	protected void setAriaLabel(int index, String label, Boolean expanded) {
		if (index < 0 || index > headers.size()) {
			return;
		}
		Element head = headers.get(index);
		Element li = head.getParentElement();
		li.setAttribute("aria-label", label);
		if (expanded != null) {
			head.setAttribute("aria-expanded", expanded.toString());
		}
	}

	/**
	 * Move focus to the header of the stack at given index
	 *
	 * @param index
	 *            Stack index to move focus to.
	 */
	public void focusHeader(int index) {
		if (index < 0 || index > headers.size()) {
			return;
		}
		headers.get(index).focus();
	}

	/**
	 * Resets the all Stacks.
	 */
	public void reset() {
		closeAll();
		setVisibleStack(-1);
	}

	private void setVisibleStack(int visibleStack) {
		this.visibleStack = visibleStack;
		this.lastVisibleStack = visibleStack;
	}

	@Override
	public boolean isCollapsed() {
		return visibleStack != lastVisibleStack;
	}

}