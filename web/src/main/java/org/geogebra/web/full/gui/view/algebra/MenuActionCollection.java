package org.geogebra.web.full.gui.view.algebra;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Collection of context menu items
 * 
 * @param <T>
 *            item context
 */
public class MenuActionCollection<T> implements Iterable<MenuAction<T>> {

	private List<MenuAction<T>> actions;

	/**
	 * New collection
	 */
	public MenuActionCollection() {
		this.actions = new ArrayList<>();
	}

	@Override
	public Iterator<MenuAction<T>> iterator() {
		return actions.iterator();
	}

	/**
	 * @param actionsToAdd
	 *            new actions
	 */
	@SafeVarargs
	protected final void addActions(MenuAction<T>... actionsToAdd) {
		for (MenuAction<T> action : actionsToAdd) {
			actions.add(action);
		}
	}

	/**
	 * @param index
	 *            insertion index
	 * @param action
	 *            action
	 */
	protected void addAction(int index, MenuAction<T> action) {
		actions.add(index, action);
	}

}
