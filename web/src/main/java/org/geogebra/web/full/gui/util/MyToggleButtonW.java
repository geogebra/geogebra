package org.geogebra.web.full.gui.util;

import org.geogebra.web.html5.gui.util.GToggleButton;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

/**
 * Extends GWT ToggleButton to support tooltips and Icon image data.
 * 
 */
public class MyToggleButtonW extends GToggleButton
		implements MouseDownHandler, TouchEndHandler {

	private HandlerRegistration actionListener;
	private boolean ignoreTab = false;

	/**
	 * @param upText
	 *            String
	 */
	public MyToggleButtonW(String upText) {
		super(upText);
		initButton();
	}

	/**
	 * @param image
	 *            an {@link Image} to use as an up Image
	 */
	public MyToggleButtonW(Image image) {
		super(image);
		initButton();
	}

	/**
	 * @param upIcon
	 *            the icon to show as up Image
	 */
	public MyToggleButtonW(ImageResource upIcon) {
		this(new Image(upIcon.getSafeUri()));
	}

	/**
	 * @param upIcon
	 *            an {@link ImageResource} to use as an up Image
	 * @param handler
	 *            {@link ClickHandler}
	 */
	public MyToggleButtonW(ImageResource upIcon, ClickHandler handler) {
		super(new Image(upIcon.getSafeUri()), handler);
		initButton();
	}

	/**
	 * @param upImage
	 *            an {@link Image} to use as an up Image
	 * @param downImage
	 *            an {@link Image} to use as an down Image
	 * @param handler
	 *            {@link ClickHandler}
	 */
	public MyToggleButtonW(Image upImage, Image downImage, ClickHandler handler) {
		super(upImage, downImage, handler);
		initButton();
	}

	/**
	 * @param upImage
	 *            an {@link Image} to use as an up Image
	 * @param downImage
	 *            an {@link Image} to use as an down Image
	 */
	public MyToggleButtonW(Image upImage, Image downImage) {
		super(upImage, downImage);
		initButton();
	}

	/**
	 * @param upIcon
	 *            an {@link ImageResource} to use as an up Image
	 * @param downIcon
	 *            an {@link ImageResource} to use an down Image
	 * @param handler
	 *            {@link ClickHandler}
	 */
	public MyToggleButtonW(ImageResource upIcon, ImageResource downIcon,
	        ClickHandler handler) {
		this(new Image(upIcon.getSafeUri()), new Image(downIcon.getSafeUri()),
		        handler);
	}

	/**
	 * @param upIcon
	 *            an {@link ImageResource} to use as an up Image
	 * @param downIcon
	 *            an {@link ImageResource} to use an down Image
	 */
	public MyToggleButtonW(ImageResource upIcon, ImageResource downIcon) {
		this(new Image(upIcon.getSafeUri()), new Image(downIcon.getSafeUri()));
	}

	private void initButton() {
		setDown(false);
		addStyleName("MyToggleButton");
		addMouseDownHandler(this);
		// fix for touch
		addDomHandler(this, TouchEndEvent.getType());
	}

	/**
	 * Button instances override this method to update the state of the button
	 * (e.g. visibility) based on a given array of GeoElements.
	 * 
	 * @param geos
	 *            Array of GeoElements
	 */
	public void update(Object[] geos) {
		// do nothing
	}

	/**
	 * Sets selection state (Java isSelected => GWT isDown)
	 * 
	 * @param isSelected
	 *            selection flag
	 */
	public void setSelected(boolean isSelected) {
		setDown(isSelected);
		if (isSelected) {
			this.addStyleName("selected");
		} else {
			this.removeStyleName("selected");
		}
	}

	/**
	 * Returns selection state (Java isSelected => GWT isDown)
	 * 
	 * @return toggle button selection state
	 */
	public boolean isSelected() {
		return isDown();
	}

	/**
	 * Remove change handler
	 */
	public void removeValueChangeHandler() {
		if (actionListener != null) {
			actionListener.removeHandler();
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
	        ValueChangeHandler<Boolean> handler) {
		actionListener = addHandler(handler, ValueChangeEvent.getType());
		return actionListener;
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

	@Override
	public void onMouseDown(MouseDownEvent event) {
		this.setFocus(false);
		// if we do not stop propagation, this will close keyboard => rebuild
		// DOM => internal state of button is lost (hovering + capturing flag in
		// ToggleButton), mouseUp will have no effect e.g. with axis button in
		// EV
		event.stopPropagation();
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		setDown(!isDown());
		ValueChangeEvent.fire(this, isDown());
		event.stopPropagation();
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		if (ignoreTab) {
			setTabIndex(-1);
		}

	}

	/**
	 * 
	 * @return if button should ignore tab key.
	 */
	public boolean isIgnoreTab() {
		return ignoreTab;
	}

	/**
	 * FocusWidget sets tabIndex -1 to 0 automatically for accessibility
	 * reasons. Call this to ignore this default behavior and really ignore tab
	 * key.
	 * 
	 */
	public void setIgnoreTab() {
		this.ignoreTab = true;
	}
}
