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

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.RemoveNeeded;

/**
 * Drawable representation of inline-editable construction element.
 */
public interface DrawInline extends RemoveNeeded, HasTransformation {
	/**
	 * Update editor from geo
	 */
	void updateContent();

	/**
	 * Send this to foreground
	 * @param x x mouse coordinates in pixels
	 * @param y y mouse coordinates in pixels
	 */
	void toForeground(int x, int y);

	/**
	 * Send this to background
	 */
	void toBackground();

	@MissingDoc
	BoundingBox<? extends GShape> getBoundingBox();

	/**
	 * @param x x mouse coordinate in pixels
	 * @param y y mouse coordinate in pixels
	 * @return the url of the current coordinate, or null, if there is
	 * nothing at (x, y), or it has no url set
	 */
	String urlByCoordinate(int x, int y);

	@MissingDoc
	HasTextFormat getController();

	@MissingDoc
	void saveContent();
}
