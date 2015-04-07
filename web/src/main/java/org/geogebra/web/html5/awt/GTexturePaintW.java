package org.geogebra.web.html5.awt;

import org.geogebra.web.html5.gawt.GBufferedImageW;

public class GTexturePaintW implements org.geogebra.common.awt.GPaint {

	// GRectangleW anchor = null;
	GBufferedImageW bi;

	public GTexturePaintW(GTexturePaintW tp) {
		// this.anchor = new GRectangleW();
		// this.anchor.setRect(tp.anchor.getX(), tp.anchor.getY(),
		// tp.anchor.getWidth(), tp.anchor.getHeight());
		this.bi = tp.bi; // TODO do we need clone deep?
	}

	public GTexturePaintW(GBufferedImageW subImage) {
		this.bi = subImage;
		// anchor = rect;
	}

	public GBufferedImageW getImg() {
		return bi;
	}

}
