package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.web.resources.SVGResource;

import com.google.gwt.user.client.Command;

/**
 * Menu item with title and icon
 * 
 * @author Zbynek
 */
public abstract class MenuAction implements Command {

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
	 * @return action title
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
	 * @return whether action is active (grey out otherwise)
	 */
	public boolean isAvailable() {
		return true;
	}

}
