package org.geogebra.web.html5.util;

import org.geogebra.common.main.App.ExportType;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;

/**
 * Frame collector for animation.
 */
public interface FrameCollectorW {

	/**
	 * @param width pixel width
	 * @param height pixel height
	 * @return encoded animted image
	 */
	String finish(int width, int height);

	/**
	 * @param ev view
	 * @param scale export scale
	 * @param format format
	 */
	void addFrame(EuclidianViewWInterface ev, double scale,
			ExportType format);

}
