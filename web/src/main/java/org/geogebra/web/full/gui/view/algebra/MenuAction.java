package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.main.Localization;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.resources.SVGResource;

/**
 * Menu item with title and icon
 *
 * @author Zbynek
 * @param <T>
 *            item context
 */
public abstract class MenuAction<T> {

	private String title;
	private SVGResource image;

	/**
	 * @param title
	 *            title
	 * @param image
	 *            image
	 */
	public MenuAction(String title, SVGResource image) {
		this.title = title;
		this.image = image;
	}

	/**
	 * Action without icon
	 * 
	 * @param title
	 *            translation key
	 */
	public MenuAction(String title) {
		this.title = title;
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
	 * @param geo
	 *            element used for context menu
	 * @return whether action is active (grey out otherwise)
	 */
	public boolean isAvailable(T geo) {
		return true;
	}

	/**
	 * @param geo
	 *            element
	 * @param app
	 *            app
	 */
	public abstract void execute(T geo, AppWFull app);

}
