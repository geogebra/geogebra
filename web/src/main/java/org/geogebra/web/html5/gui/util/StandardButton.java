package org.geogebra.web.html5.gui.util;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.FastButton;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.Label;

/**
 * @author csilla
 * 
 */
public class StandardButton extends FastButton {
	
	private App app;
	private ResourcePrototype icon;
	private String label;
	private int width = -1;

	/**
	 * @param icon
	 *            - img of button
	 * @param app
	 *            - application
	 */
	public StandardButton(final ImageResource icon, App app) {
		this.app = app;
		setIconAndLabel(icon, null, icon.getWidth());
	}

	/**
	 * @param label
	 *            - text of button
	 * @param app
	 *            - application
	 */
	public StandardButton(final String label, App app) {
		this.app = app;
		setIconAndLabel(null, label, -1);
	}

	/**
	 * @param icon
	 *            - img of button
	 * @param label
	 *            - text of button
	 * @param width
	 *            - width of button
	 * @param app
	 *            - application
	 */
	public StandardButton(final ResourcePrototype icon, final String label, int width, App app) {
		this.app = app;
		setIconAndLabel(icon, label, width);
	}

	private void setIconAndLabel(final ResourcePrototype image, final String label, int width) {
		this.width = width;
		this.icon = image;
		this.label = label;
		if (image != null && label != null) {
			this.getElement().removeAllChildren();
			this.getElement().appendChild(
					new NoDragImage(ImgResourceHelper.safeURI(image), width)
							.getElement());
			this.getElement().appendChild(new Label(label).getElement());
			return;
		}

		if (image != null) {
			NoDragImage im = new NoDragImage(ImgResourceHelper.safeURI(image),
					width);
			getUpFace().setImage(im);
		}

		if (label != null) {
			getUpFace().setText(label);
		}
	}
	
	@Override
    public void setText(String text){
		this.label = text;
		setIconAndLabel(this.icon, text, this.width);
	}

	@Override
	public void onHoldPressDownStyle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onHoldPressOffStyle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisablePressStyle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnablePressStyle() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return text of button
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * @param label
	 *            - set text of button
	 */
	public void setLabel(final String label) {
		setIconAndLabel(this.icon, label, this.width);
	}

	/**
	 * @return icon of button
	 */
	public ResourcePrototype getIcon() {
		return this.icon;
	}

	/**
	 * @param icon
	 *            - icon
	 */
	public void setIcon(final ImageResource icon) {
		setIconAndLabel(icon, this.label, this.width);
	}
	
	@Override
    @Deprecated
    /**
     * Use addFastClickHandler instead
     */
	public HandlerRegistration addClickHandler(ClickHandler c){
		return null;
	}

	@Override
	public void setTitle(String title) {
		if (app.has(Feature.TOOLTIP_DESIGN) && !Browser.isMobile()) {
			getElement().removeAttribute("title");
			getElement().setAttribute("data-title", title);
		} else {
			super.setTitle(title);
		}
	}
}