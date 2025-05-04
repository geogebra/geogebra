package org.geogebra.common.jre.headless;

import org.geogebra.common.jre.gui.MyImageJre;

/**
 * Application interface for desktop.
 */
public interface AppDI {

	/**
	 * Add user-supplied image.
	 * @param name filename
	 * @param img image
	 */
	void addExternalImage(String name, MyImageJre img);

	/**
	 * Export active graphics view into an image
	 * @param thumbnailPixelsX width
	 * @param thumbnailPixelsY height
	 * @return exported image
	 */
	MyImageJre getExportImage(double thumbnailPixelsX, double thumbnailPixelsY);

	/**
	 * Get user-supplied image by filename.
	 * @param fileName filename
	 * @return image
	 */
	MyImageJre getExternalImage(String fileName);

}
