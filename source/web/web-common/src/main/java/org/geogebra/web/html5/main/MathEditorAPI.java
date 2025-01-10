package org.geogebra.web.html5.main;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * API for getting / setting the state of equation editor
 * 
 * @author Zbynek
 */
public interface MathEditorAPI {

	/**
	 * @param text
	 *            JSON encoded editor state
	 * @param geo
	 *            edited geo or null for new input
	 */
	void setState(String text, GeoElement geo);

	/**
	 * @return JSON encoded editor state
	 */
	String getState();

}
