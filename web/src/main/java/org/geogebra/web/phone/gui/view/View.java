package org.geogebra.web.phone.gui.view;

import com.google.gwt.resources.client.ImageResource;

public interface View {

	/**
	 * @return the icon of the view
	 */
	ImageResource getViewIcon();

	/**
	 * @return the panel the view
	 */
	ViewPanel getViewPanel();

	/**
	 * @return the header of the view if exists, null otherwise
	 */
	HeaderPanel getHeaderPanel();
	
	/**
	 * @return the stylebar of the view, if exists, null otherwise
	 */
	StyleBar getStyleBar();

}
