package geogebra.web.gui.util;

import geogebra.html5.gui.tooltip.ToolTipManagerW;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
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
        MouseOverHandler, MouseOutHandler, HasSetIcon {

	private static final long serialVersionUID = 1L;
	private HandlerRegistration actionListener;
	private String toolTipText;
	private int buttonHeight;

	public MyToggleButton2(ImageResource upIcon, int iconHeight) {
		super(new Image(upIcon.getSafeUri()));
		initButton(iconHeight);
	}

	public MyToggleButton2(ImageResource upIcon, ImageResource downIcon,
	        ClickHandler handler, int iconHeight) {
		super(new Image(upIcon.getSafeUri()), new Image(downIcon.getSafeUri()),
		        handler);
		initButton(iconHeight);
	}

	public MyToggleButton2(ImageResource upIcon, ClickHandler handler,
	        int iconHeight) {
		super(new Image(upIcon.getSafeUri()), handler);
		initButton(iconHeight);
	}

	public MyToggleButton2(final Image image, int iconHeight) {
		super(image);
		initButton(iconHeight);
	}

	public MyToggleButton2(Image upImage, Image downImage,
	        ClickHandler handler, int iconHeight) {
		super(upImage, downImage, handler);
		initButton(iconHeight);
	}

	public MyToggleButton2(String string, int iconHeight) {
	    super(string);
	    initButton(iconHeight);
    }

	/**
	 * @param upIcon the icon to show as upImage
	 */
	public MyToggleButton2(ImageResource upIcon) {
		this(new Image(upIcon.getSafeUri()));
    }

	/**
	 * @param image an Image to use as an up Image
	 */
	public MyToggleButton2(Image image) {
	    super(image);
	    initButton(image.getHeight());
    }

	private void initButton(int height) {
		this.buttonHeight = height;
		setDown(false);
		//setHeight(buttonHeight + "px");
		//setWidth(buttonHeight + "px");
		addStyleName("MyToggleButton");
		addMouseOutHandler(this);
		addMouseOverHandler(this);
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
	}

	/**
	 * Returns selection state (Java isSelected => GWT isDown)
	 * 
	 * @return toggle button selection state
	 */
	public boolean isSelected() {
		return isDown();
	}

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
		this.toolTipText = toolTipText;
	}

	public void onMouseOver(MouseOverEvent event) {
		ToolTipManagerW.sharedInstance().showToolTip(toolTipText);
	}

	public void onMouseOut(MouseOutEvent event) {
		ToolTipManagerW.sharedInstance().showToolTip(null);
	}

	public void onMouseDown(MouseDownEvent event) {
		this.setFocus(false);
	}

	public void setIcon(ImageOrText data) {
	    // TODO Auto-generated method stub
	    
    }

}
