package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.web.resources.SVGResource;

import com.google.gwt.user.client.Command;

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

	public String getTitle() {
		return this.title;
	}

	public SVGResource getImage() {
		return this.image;
	}

	public boolean isAvailable() {
		return true;
	}

}
