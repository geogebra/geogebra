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

package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Caption text.
 */
public interface CaptionText {

	@MissingDoc
	String text();

	@MissingDoc
	GFont font();

	@MissingDoc
	int fontSize();

	@MissingDoc
	boolean isSerifFont();

	@MissingDoc
	boolean isLaTeX();

	@MissingDoc
	GColor foregroundColor();

	@MissingDoc
	GColor backgroundColor();

	/**
	 * Update the caption.
	 * @param text new text
	 * @param font font
	 * @param fgColor color
	 */
	void update(String text, GFont font, GColor fgColor);

	@MissingDoc
	String textToDraw();

	@MissingDoc
	GeoElement getGeoElement();

	/**
	 * Create internal font.
	 * @param original base font
	 */
	void createFont(GFont original);

	/**
	 * TODO remove ?
	 * @param geo element
	 */
	void register(GeoElement geo);

	/**
	 * @param text text
	 * @param font font
	 * @return whether text or font are different
	 */
	boolean hasChanged(String text, GFont font);

	/**
	 * @return whether this is valid
	 */
	boolean isValid();
}
