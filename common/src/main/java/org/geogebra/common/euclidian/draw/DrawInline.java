package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.RemoveNeeded;

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

	BoundingBox<? extends GShape> getBoundingBox();

	/**
	 * @param x x mouse coordinate in pixels
	 * @param y y mouse coordinate in pixels
	 * @return the url of the current coordinate, or null, if there is
	 * nothing at (x, y), or it has no url set
	 */
	String urlByCoordinate(int x, int y);

	void saveContent();
}
