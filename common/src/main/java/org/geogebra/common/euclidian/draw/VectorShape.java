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
