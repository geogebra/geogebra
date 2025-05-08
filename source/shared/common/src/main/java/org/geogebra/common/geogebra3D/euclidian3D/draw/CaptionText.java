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
