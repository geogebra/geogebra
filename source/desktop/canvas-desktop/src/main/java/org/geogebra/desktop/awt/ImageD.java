package org.geogebra.desktop.awt;

import java.awt.Graphics2D;
import java.awt.Image;

import org.geogebra.common.awt.MyImage;

public interface ImageD extends MyImage {

	/**
	 * Render in given position.
	 */
	void render(Graphics2D impl, int x, int y);

	/**
	 * @return wrapped image
	 */
	Image getImage();

	/**
	 * Render sub-image at given position.
	 */
	void render(Graphics2D impl, int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh);
}
