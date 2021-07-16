package org.geogebra.web.html5.gui.accessibility;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;

import com.google.gwt.user.client.ui.Widget;

/**
 * Accessibility adapter for a construction element or settings.
 */
public interface AccessibleWidget {

	/**
	 * @return list of DOM elements
	 */
	List<? extends Widget> getWidgets();

	/**
	 * Update the DOM element from construction
	 */
	void update();

	/**
	 * Delegate to the setFocus method of the first widget if possible.
	 * 
	 * @param focus whether to focus or blur
	 */
	void setFocus(boolean focus);

	/**
	 * @param geo construction element
	 * @return whether we can use this widget
	 */
	boolean isCompatible(GeoElement geo);
}
