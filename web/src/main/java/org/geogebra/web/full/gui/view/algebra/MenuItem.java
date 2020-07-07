package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.menubar.MenuAction;
import org.geogebra.web.resources.SVGResource;

/**
 * Menu item with title and icon
 *
 * @author Zbynek
 * @param <T>
 *            item context
 */
public abstract class MenuItem<T> {

	private String title;
	private SVGResource image;
	private MenuAction<T> action;

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

	}

	/**
	 * Action without icon
	 * 
	 * @param title
	 *            translation key
	 */
	public MenuItem(String title, MenuAction<T> action) {
		this.title = title;
		this.action = action;
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
	 * @return action
	 */
	public MenuAction<T> getAction() {
		return action;
	}
}
