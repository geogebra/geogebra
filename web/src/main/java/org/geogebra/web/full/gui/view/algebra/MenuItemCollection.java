package org.geogebra.web.full.gui.view.algebra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Collection of context menu items
 * 
 * @param <T>
 *            item context
 */
public class MenuItemCollection<T> implements Iterable<MenuItem<T>> {

	private List<MenuItem<T>> items;

	/**
	 * New collection
	 */
	public MenuItemCollection() {
		this.items = new ArrayList<>();
	}

	@Override
	public Iterator<MenuItem<T>> iterator() {
		return items.iterator();
	}

	/**
	 * @param menuItems
	 *            new actions
	 */
	@SafeVarargs
	protected final void addItems(MenuItem<T>... menuItems) {
		items.addAll(Arrays.asList(menuItems));
	}

	/**
	 * @param index
	 *            insertion index
	 * @param action
	 *            action
	 */
	protected void addAction(int index, MenuItem<T> action) {
		items.add(index, action);
	}

}
