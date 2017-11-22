/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.geogebra.web.web.gui.menubar;

import java.util.ArrayList;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.Widget;

/**Accessible alternative to MenuBar*/
public class AriaMenuBar extends Widget {
	private MenuItem selectedItem;
	private ArrayList<MenuItem> allItems;

	public AriaMenuBar() {
		setElement(Document.get().createULElement());
	}
	public MenuItem addItem(MenuItem a) {
		a.getScheduledCommand();
		getElement().appendChild(a.getElement());
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
		// TODO Auto-generated method stub

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
		this.selectedItem = item;
	}
	public void clearItems() {

	}

	public ArrayList<MenuItem> getItems() {
		return allItems;
	}

	public void moveSelectionDown() {

	}

	public void moveSelectionUp() {

	}

	public MenuItemSeparator addSeparator() {
		return new MenuItemSeparator();
	}

	public void addSeparator(MenuItemSeparator sep) {
	}

	public void setAutoOpen(boolean b) {
		// TODO Auto-generated method stub

	}

	public MenuItem addItem(String itemtext, boolean textishtml,
			MenuBar submenupopup) {
		// TODO Auto-generated method stub
		return null;
	}
}
