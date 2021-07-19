package org.geogebra.web.html5.gui.view.button;

import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.HasResource;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 * 
 */
public class StandardButton extends Widget implements HasResource {

	private ResourcePrototype icon;
	private String label;
	private int width = -1;
	private int height = -1;
	private NoDragImage btnImage;

	protected StandardButton() {
		setElement(DOM.createButton());
		// for cursor: pointer
		addStyleName("button");
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
	 */
	public StandardButton(final ResourcePrototype icon, final String label,
			int width, int height) {
		this();
		setIconAndLabel(icon, label, width, height);
	}

	/**
	 * @param icon
	 *            - img of button
	 */
	public StandardButton(final ImageResource icon) {
		this(icon, null, icon.getWidth(), icon.getHeight());
	}

	/**
	 * @param label
	 *            - text of button
	 */
	public StandardButton(final String label) {
		this(null, label, -1, -1);
	}

	/**
	 * @param icon
	 *            - img of button
	 * @param label
	 *            - text of button
	 * @param width
	 *            - width of button
	 */
	public StandardButton(final ResourcePrototype icon, final String label,
			int width) {
		this(icon, label, width, -1);
	}

	/**
	 * @param icon
	 *            - img of button
	 * @param label
	 *            - text of button
	 * @param size
	 *            - width and hight of button
	 */
	public StandardButton(int size, final ResourcePrototype icon, final String label) {
		this();
		this.width = size;
		this.height = -1;
		this.icon = icon;
		this.label = label;
		buildIconAndLabel(icon, label);
	}

	private void buildIconAndLabel(final ResourcePrototype image,
			final String label) {
		SimplePanel imgPanel = new SimplePanel();
		imgPanel.addStyleName("imgHolder");
		btnImage = new NoDragImage(image, width, height);
		btnImage.getElement().setTabIndex(-1);
		imgPanel.add(btnImage);

		this.getElement().removeAllChildren();
		this.getElement().appendChild(imgPanel.getElement());
		this.getElement().appendChild(new Label(label).getElement());
		btnImage.setPresentation();

		Roles.getButtonRole().removeAriaPressedState(getElement());
	}

	private void setIconAndLabel(final ResourcePrototype image,
			final String label, int width, int height) {
		this.width = width;
		this.height = height;
		this.icon = image;
		this.label = label;
		this.getElement().removeAllChildren();
		if (image != null) {
			btnImage = new NoDragImage(image, width, height);
			btnImage.getElement().setTabIndex(-1);

			this.getElement().appendChild(btnImage.getElement());

			if (label != null) {
				this.getElement().appendChild(new Label(label).getElement());
			}
			btnImage.setPresentation();
			return;
		}

		if (label != null) {
			this.getElement().appendChild(new Label(label).getElement());
		}

		Roles.getButtonRole().removeAriaPressedState(getElement());
	}

	/**
	 * Set the text of the button, leaving all other properties unchanged
	 * @param text text of the button
	 */
	public void setText(String text) {
		this.label = text;
		setIconAndLabel(this.icon, text, this.width, this.height);
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

	@Override
	public void setTitle(String title) {
		AriaHelper.setTitle(this, title);
	}

	/**
	 * @param altText
	 *            - alt text
	 */
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

	/**
	 * Toggle the button between enabled and disabled
	 * Changes "disabled" property in DOM, so use :disabled in css
	 * @param enabled whether to add or remove the "disabled" property
	 */
	public void setEnabled(boolean enabled) {
		if (enabled) {
			getElement().removeAttribute("disabled");
		} else {
			getElement().setAttribute("disabled", "true");
		}
	}

	/**
	 * Add a regular click handler to the button
	 * TODO: rename to addClickHandler/onClick/something else separately to avoid huge MR now
	 * @param handler click handler
	 */
	public void addFastClickHandler(FastClickHandler handler) {
		Dom.addEventListener(this.getElement(), "click", (e) -> {
			handler.onClick(this);
			e.stopPropagation();
		});
	}
}
