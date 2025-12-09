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