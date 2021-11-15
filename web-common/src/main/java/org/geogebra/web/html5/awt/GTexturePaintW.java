package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.web.html5.gawt.GBufferedImageW;

public class GTexturePaintW implements GPaint {

	private final GRectangle anchor;
	// GRectangleW anchor = null;
	private GBufferedImageW bi;

	/**
	 * @param image
	 *            image
	 */
	public GTexturePaintW(GBufferedImageW image, GRectangle rect) {
		this.bi = image;
		this.anchor = rect;
	}

	/**
	 * @return wrapped image
	 */
	public GBufferedImageW getImg() {
		return bi;
	}

	public GRectangle getAnchor() {
		return anchor;
	}

}
