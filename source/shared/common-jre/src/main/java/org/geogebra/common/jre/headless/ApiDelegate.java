package org.geogebra.common.jre.headless;

import org.geogebra.common.euclidian.EuclidianView;

public interface ApiDelegate {

	/**
	 * @param transparent whether to use transparency
	 * @param dpi dots per inch (metadata in PNG)
	 * @param exportScale export pixels:screen pixels
	 * @param ev view
	 * @return base64 envcoded PNG
	 */
	String base64encodePNG(boolean transparent,
			double dpi, double exportScale, EuclidianView ev);

	/**
	 * Opens a .ggb file.
	 * @param strURL URL of a .ggb file
	 */
	void openFile(String strURL);
}
