package org.geogebra.web.full.gui.view.algebra;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Collection of context menu items
 */
public class MenuActionCollection implements Iterable<MenuAction> {

	private List<MenuAction> actions;

	/**
	 * New collection
	 */
	public MenuActionCollection() {
		this.actions = new ArrayList<>();
	}

	@Override
	public Iterator<MenuAction> iterator() {
		return actions.iterator();
	}

	/**
	 * @param actionsToAdd
	 *            new actions
	 */
	protected void addActions(MenuAction... actionsToAdd) {
		for (MenuAction action : actionsToAdd) {
			actions.add(action);
		}
	}

}
