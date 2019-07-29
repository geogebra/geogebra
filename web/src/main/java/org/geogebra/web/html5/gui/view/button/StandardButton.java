package org.geogebra.web.html5.gui.view.button;

import org.geogebra.common.gui.view.ActionView;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.FastButton;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.HasResource;
import org.geogebra.web.html5.gui.util.NoDragImage;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 * 
 */
public class StandardButton extends FastButton implements HasResource, ActionView {

	private App app;
	private ResourcePrototype icon;
	private String label;
	private int width = -1;
	private int height = -1;
	private NoDragImage btnImage;

	/**
	 * @param icon
	 *            - img of button
	 * @param app
	 *            - application
	 */
	public StandardButton(final ImageResource icon, App app) {
		this.app = app;
		setIconAndLabel(icon, null, icon.getWidth(), icon.getHeight());
	}

	/**
	 * @param label
	 *            - text of button
	 * @param app
	 *            - application
	 */
	public StandardButton(final String label, App app) {
		this.app = app;
		setIconAndLabel(null, label, -1, -1);
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
	public StandardButton(final ResourcePrototype icon, final String label,
			int width, App app) {
		this.app = app;
		setIconAndLabel(icon, label, width, -1);
	}

	/**
	 * @param icon
	 *            - img of button
	 * @param label
	 *            - text of button
	 * @param width
	 *            - width of button
	 * @param height
	 *            icon height
	 * @param app
	 *            - application
	 */
	public StandardButton(final ResourcePrototype icon, final String label,
			int width, int height, App app) {
		this.app = app;
		setIconAndLabel(icon, label, width, height);
	}

	private void setIconAndLabel(final ResourcePrototype image,
			final String label, int width, int height) {
		this.width = width;
		this.height = height;
		this.icon = image;
		this.label = label;
		if (image != null) {
			btnImage = new NoDragImage(image, width, height);
			if (label == null) {
				getUpFace().setImage(btnImage);
			} else {
				this.getElement().removeAllChildren();
				this.getElement().appendChild(btnImage.getElement());
				this.getElement().appendChild(new Label(label).getElement());
			}
			btnImage.setPresentation();
			return;
		}

		if (label != null) {
			getUpFace().setText(label);
		}
		Roles.getButtonRole().removeAriaPressedState(getElement());
	}

	@Override
	public void setText(String text) {
		this.label = text;
		setIconAndLabel(this.icon, text, this.width, this.height);
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
		setIconAndLabel(this.icon, label, this.width, this.height);
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
	public void setIcon(final ResourcePrototype icon) {
		setIconAndLabel(icon, this.label, this.width, this.height);
	}

	/**
	 * Use addFastClickHandler instead
	 */
	@Override
	@Deprecated
	public HandlerRegistration addClickHandler(ClickHandler c) {
		return null;
	}

	@Override
	public void setTitle(String title) {
		AriaHelper.setTitle(this, title, app);
	}

	/**
	 * @param altText
	 *            - alt text
	 */
	@Override
	public void setAltText(String altText) {
		if (btnImage != null) {
			btnImage.setPresentation();
		}
		AriaHelper.setLabel(this, altText);
		Roles.getButtonRole().removeAriaPressedState(getElement());
	}

	@Override
	public void setResource(ResourcePrototype res) {
		btnImage.setResource(res);
	}

	@Override
	public void setAction(final Runnable action) {
		if (action != null) {
			addFastClickHandler(new FastClickHandler() {
				@Override
				public void onClick(Widget source) {
					action.run();
				}
			});
		}
	}
}