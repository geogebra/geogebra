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

import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GShape;

/**
 * Defines how the vector is drawn
 */
public interface VectorShape {

	/**
	 *
	 * @return the model of the vector to be drawn.
	 */
	DrawVectorModel model();

	/**
	 *
	 * @return the vector line can be drawn.
	 */
	GLine2D body();

	/**
	 *
	 * @return the vector head (arrow) can be drawn.
	 */
	GShape head();

	/**
	 * @param width of the visible area.
	 * @param height of the visible area.
	 * @return the clipped line.
	 */
	GLine2D clipLine(int width, int height);
}
