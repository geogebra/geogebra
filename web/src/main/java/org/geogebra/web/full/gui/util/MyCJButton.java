package org.geogebra.web.full.gui.util;

import org.geogebra.web.html5.gui.util.HasSetIcon;
import org.geogebra.web.html5.gui.util.ImageOrText;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

/**
 * Button using an icon or label
 *
 * @author gabor
 */
public class MyCJButton extends Composite
		implements MouseDownHandler, MouseUpHandler, HasSetIcon {

	private Label button;
	/** whether this is enabled */
	boolean enabled;
	private ImageOrText icon;
	private Label buttonContent;
	private boolean imageMode = false;

	/**
	 * Creates a new button
	 * 
	 */
	public MyCJButton() {
		button = new Label("");
		buttonContent = new Label("");
		buttonContent.setStyleName("buttonContent");
		button.getElement().appendChild(buttonContent.getElement());
		button.addMouseDownHandler(this);
		button.addMouseUpHandler(this);

		initWidget(button);
		setStyleName("MyCanvasButton");
		enabled = true;
	}

	/**
	 * @return {@link Label}
	 */
	public Label getButtonContent() {
		return buttonContent;
	}

	/**
	 * sets the text of the button
	 * 
	 * @param text
	 *            String
	 */
	public void setText(String text) {
		button.setText(text);
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (!enabled) {
			return;
		}

		setDownState(false);
		event.stopPropagation();
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (!enabled) {
			return;
		}
		setDownState(true);
		event.stopPropagation();
	}

	private void setDownState(boolean downState) {
		// TODO less visible
		if (downState) {
			this.addStyleName("selected");
		} else {
			this.removeStyleName("selected");
		}
	}

	@Override
	public void setIcon(ImageOrText icon) {
		if (this.imageMode && icon.getUrl() == null) {
			return;
		}
		if (icon.getUrl() != null) {
			this.imageMode = true;
		}
		this.icon = icon;
		icon.applyToLabel(buttonContent);
		setDownState(false);
	}

	/**
	 * @return {@link ImageOrText}
	 */
	public ImageOrText getIcon() {
		return this.icon;
	}

	/**
	 * @return {@code true} if button is enabled
	 */
	protected boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            boolean
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (enabled) {
			removeStyleName("disabled");
		} else {
			addStyleName("disabled");

		}
	}

	/**
	 * adds a clickHandler to the button and calls the given clickhandler only
	 * if the button is enabled
	 * 
	 * @param handler
	 *            {@link ClickHandler}
	 */
	public void addActionListener(final ClickHandler handler) {
		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!enabled) {
					return;
				}
				handler.onClick(event);
			}
		});
	}

	/**
	 * adds the given clickhandler to the button
	 * 
	 * @param handler
	 *            {@link ClickHandler}
	 * @return {@link HandlerRegistration}
	 */
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return button.addClickHandler(handler);
	}

	/**
	 * Sets the toolTip text
	 * 
	 * @param toolTipText
	 *            toolTip string
	 */
	public void setToolTipText(String toolTipText) {
		setTitle(toolTipText);
	}

	/**
	 * @param handler
	 *            - mouse out
	 * @return handler
	 */
	public final HandlerRegistration addMouseOutHandler(
			MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	/**
	 * @param handler
	 *            - mouse over
	 * @return handler
	 */
	public final HandlerRegistration addMouseOverHandler(
			MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}
}
