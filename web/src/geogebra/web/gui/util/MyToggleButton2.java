package geogebra.web.gui.util;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * Extends GWT ToggleButton to support tooltips and Icon image data.
 * 
 */
public class MyToggleButton2 extends ToggleButton implements MouseDownHandler,
        HasSetIcon {

	private HandlerRegistration actionListener;

	/**
	 * @param upText
	 *            String
	 */
	public MyToggleButton2(String upText) {
		super(upText);
		initButton();
	}

	/**
	 * @param image
	 *            an {@link Image} to use as an up Image
	 */
	public MyToggleButton2(Image image) {
		super(image);
		initButton();
	}

	/**
	 * @param upIcon
	 *            the icon to show as up Image
	 */
	public MyToggleButton2(ImageResource upIcon) {
		this(new Image(upIcon.getSafeUri()));
	}

	/**
	 * @param upIcon
	 *            an {@link ImageResource} to use as an up Image
	 * @param handler
	 *            {@link ClickHandler}
	 */
	public MyToggleButton2(ImageResource upIcon, ClickHandler handler) {
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
	public MyToggleButton2(Image upImage, Image downImage, ClickHandler handler) {
		super(upImage, downImage, handler);
		initButton();
	}

	/**
	 * @param upImage
	 *            an {@link Image} to use as an up Image
	 * @param downImage
	 *            an {@link Image} to use as an down Image
	 */
	public MyToggleButton2(Image upImage, Image downImage) {
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
	public MyToggleButton2(ImageResource upIcon, ImageResource downIcon,
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
	public MyToggleButton2(ImageResource upIcon, ImageResource downIcon) {
		this(new Image(upIcon.getSafeUri()), new Image(downIcon.getSafeUri()));
	}

	private void initButton() {
		setDown(false);
		addStyleName("MyToggleButton");
		addMouseDownHandler(this);
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
		if(isSelected) {
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
	 * 
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

	public void onMouseDown(MouseDownEvent event) {
		this.setFocus(false);
	}

	public void setIcon(ImageOrText data) {
	    // TODO Auto-generated method stub
	    
    }
}
