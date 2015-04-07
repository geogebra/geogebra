package org.geogebra.web.phone.gui.view;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Interface for the style bars.
 */
public interface StyleBar {

	/**
	 * Get the icon of the style bar.
	 * 
	 * @return the icon
	 */
	ImageResource getStyleBarIcon();

	/**
	 * Get the style bar widget.
	 * 
	 * @return the widget containing the style bar.
	 */
	IsWidget getStyleBar();

	/**
	 * @param showStyleBar
	 *            boolean
	 * 
	 */
	void setOpen(boolean showStyleBar);
}
