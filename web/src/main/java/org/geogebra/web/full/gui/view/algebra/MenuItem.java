package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.gui.view.algebra.contextmenu.MenuAction;
import org.geogebra.common.main.Localization;
import org.geogebra.web.resources.SVGResource;

/**
 * Menu item with title and icon
 *
 * @author Zbynek
 * @param <T>
 *            item context
 */
public class MenuItem<T> {

	private final String title;
	private final Runnable actionCallback;
	private SVGResource image;
	private final MenuAction<T> action;

	/**
	 * @param title
	 *            title
	 * @param image
	 *            image
	 */
	public MenuItem(String title, SVGResource image, MenuAction<T> action) {
		this.title = title;
		this.image = image;
		this.action = action;
		this.actionCallback = null;
	}

	/**
	 * Action without icon
	 * 
	 * @param title
	 *            translation key
	 * @param actionCallback runs after the action is executed
	 */
	public MenuItem(String title, MenuAction<T> action, Runnable actionCallback) {
		this.title = title;
		this.action = action;
		this.actionCallback = actionCallback;
	}

	public MenuItem(String title, MenuAction<T> action) {
		this(title, action, null);
	}

	/**
	 * @param loc
	 *            localization
	 * @return action title
	 */
	public String getTitle(Localization loc) {
		return loc.getMenu(title);
	}

	/**
	 * @return internal title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return action image
	 */
	public SVGResource getImage() {
		return image;
	}


	/**
	 * Run this item's action
	 * @param element parameter of the action
	 */
	public void executeAction(T element) {
		action.execute(element);
		if (actionCallback != null) {
			actionCallback.run();
		}
	}

	/**
	 * @param element object
	 * @return whether this item should be shown for given element
	 */
	public boolean isAvailable(T element) {
		return action.isAvailable(element);
	}
}
