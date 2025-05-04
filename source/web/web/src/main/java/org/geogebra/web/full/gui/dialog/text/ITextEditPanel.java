package org.geogebra.web.full.gui.dialog.text;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * Text edit panel.
 */
public interface ITextEditPanel {

	/**
	 * Update preview panel.
	 */
	void updatePreviewPanel();

	/**
	 * Insert dynamic element reference
	 * @param geo element
	 */
	void insertGeoElement(GeoElement geo);

	/**
	 * Insert constant string
	 * @param text inserted string
	 * @param isLatex whether it's LaTeX
	 */
	void insertTextString(String text, boolean isLatex);

	/**
	 * @return element being edited
	 */
	GeoText getEditGeo();

	/**
	 * Switch to LaTeX mode.
	 */
	void ensureLaTeX();

	/**
	 * Update preview panel
	 * @param byUser whether it was triggered by the user
	 */
	void updatePreviewPanel(boolean byUser);

}