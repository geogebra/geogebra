package org.geogebra.keyboard.web;

import org.geogebra.common.main.Localization;
import org.geogebra.keyboard.base.Action;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ImageResource;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;

/**
 * A KeyBoardButton with functional character. This button doesn't insert a
 * character or anything else, this button performs an action.
 * 
 */
public class KeyBoardButtonFunctionalBase extends KeyBoardButtonBase {

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
	public KeyBoardButtonFunctionalBase(ImageResource image,
			ButtonHandler handler,
			Action action, Localization loc, String altText) {
		super(handler);
		this.image = new NoDragImage(image, image.getWidth());

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
	public KeyBoardButtonFunctionalBase(ImageResource image,
			String feedback, ButtonHandler handler, Localization loc,
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
	 * @param addSupSyle
	 *            - true if add sup style
	 * @param loc
	 *            localization
	 * @param altText
	 *            alternate text for icon
	 */
	public KeyBoardButtonFunctionalBase(SVGResource svg, String feedback,
			ButtonHandler handler, boolean addSupSyle, Localization loc,
			String altText) {
		super(handler);

		String altTextTranslated = loc.getAltText(altText);

		this.image = new NoDragImage(svg, 24);
		this.image.setAltText(altTextTranslated);
		this.image.getElement().setAttribute("role", "img");
		this.feedback = feedback;
		add(this.image);
		if (addSupSyle) {
			addStyleName("sup");
		}
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
	public KeyBoardButtonFunctionalBase(String caption, ButtonHandler handler,
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
