package org.geogebra.desktop.util;

import java.awt.image.BufferedImage;

/**
 * Interface for animated GIF export, needed to separate GUI from App
 * 
 * @author Zbynek
 */

public interface FrameCollector {

	/**
	 * Add a frame.
	 * @param img frame content
	 */
	void addFrame(BufferedImage img);

	/**
	 * Finish the export.
	 */
	void finish();

}
