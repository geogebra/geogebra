/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.gui.accessibility;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.gwtproject.user.client.ui.Widget;

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
