package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GPaint;
import org.geogebra.web.html5.gawt.GBufferedImageW;

public class GTexturePaintW implements GPaint {

	// GRectangleW anchor = null;
	private GBufferedImageW bi;

	/**
	 * Copy constructor
	 * 
	 * @param tp
	 *            texture
	 */
	public GTexturePaintW(GTexturePaintW tp) {
		// this.anchor = new GRectangleW();
		// this.anchor.setRect(tp.anchor.getX(), tp.anchor.getY(),
		// tp.anchor.getWidth(), tp.anchor.getHeight());
		this.bi = tp.bi; // TODO do we need clone deep?
	}

	/**
	 * @param image
	 *            image
	 */
	public GTexturePaintW(GBufferedImageW image) {
		this.bi = image;
		// anchor = rect;
	}

	/**
	 * @return wrapped image
	 */
	public GBufferedImageW getImg() {
		return bi;
	}

}
