/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.keyboard.web;

import org.geogebra.common.main.LocalizationI;
import org.geogebra.keyboard.base.Action;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.event.dom.client.ClickHandler;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.user.client.ui.Image;

/**
 * A keyboard button with functional character. This button doesn't insert a
 * character or anything else, this button performs an action.
 * 
 */
public class FunctionalKeyboardButton extends BaseKeyboardButton {

	/**
	 * the possible actions for a functional button
	 * 
	 */
	// public enum Action {
	// ENTER, BACKSPACE, SHIFT, ARROW_LEFT, ARROW_RIGHT, SWITCH_KEYBOARD;
	// }
	private Image image;
	private Action action;

	/**
	 * used for keyboardButtons with an image.
	 * 
	 * @param image
	 *            {@link ImageResource}
	 * @param handler
	 *            {@link ClickHandler}
	 * @param action
	 *            {@link Action}
	 * @param loc
	 *            localization
	 * @param altText
	 *            alternate text for icon
	 */
	public FunctionalKeyboardButton(SVGResource image,
			ButtonHandler handler,
			Action action, LocalizationI loc, String altText) {
		super(handler);
		this.image = new NoDragImage(image, 24);

		String altTextTranslated = loc.getAltText(altText);

		this.image.setAltText(altTextTranslated);
		getElement().setAttribute("aria-label", altTextTranslated);
		this.action = action;
		add(this.image);
		addStyleName("colored");
		addStyleName("waves-light");
	}

	/**
	 * @param image
	 *            {@link ImageResource}
	 * @param feedback
	 *            - inserted text
	 * @param handler
	 *            {@link ClickHandler}
	 * @param loc
	 *            localization
	 * @param altText
	 *            alternate text for icon
	 */
	public FunctionalKeyboardButton(ImageResource image,
			String feedback, ButtonHandler handler, LocalizationI loc,
			String altText) {
		super(handler);
		this.image = new Image(image.getSafeUri().asString());
		this.image.setAltText(loc.getMenu(altText));
		this.feedback = feedback;
		add(this.image);
	}

	/**
	 * @param svg
	 *            - svg resource
	 * @param feedback
	 *            - text is inserted
	 * @param handler
	 *            {@link ClickHandler}
	 * @param loc
	 *            localization
	 * @param altText
	 *            alternate text for icon
	 */
	public FunctionalKeyboardButton(SVGResource svg, String feedback,
			ButtonHandler handler, LocalizationI loc,
			String altText) {
		super(handler);

		String altTextTranslated = loc.getAltText(altText);

		this.image = new NoDragImage(svg, 24);
		this.image.setAltText(altTextTranslated);
		this.image.getElement().setAttribute("role", "img");
		this.feedback = feedback;
		add(this.image);
		getElement().setAttribute("aria-label", altTextTranslated);
	}

	/**
	 * used for keyboardButtons with an image.
	 * 
	 * @param caption
	 *            String
	 * 
	 * @param handler
	 *            {@link ClickHandler}
	 * @param action
	 *            {@link Action}
	 */
	public FunctionalKeyboardButton(String caption, ButtonHandler handler,
			Action action) {
		super(caption, caption, handler);
		this.action = action;
		addStyleName("colored");
	}

	/**
	 * @return {@link Action}
	 */
	public Action getAction() {
		return this.action;
	}
}
