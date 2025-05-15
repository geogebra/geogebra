package org.geogebra.web.html5.util;

import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;

/**
 * Frame collector for animation.
 */
public interface FrameCollectorW {

	/**
	 * @param width pixel width
	 * @param height pixel height
	 */
	void finish(int width, int height);

	/**
	 * @param ev view
	 * @param scale export scale
	 */
	void addFrame(EuclidianViewWInterface ev, double scale);

}
