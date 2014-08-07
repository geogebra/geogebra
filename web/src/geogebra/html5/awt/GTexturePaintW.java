package geogebra.html5.awt;

import geogebra.html5.gawt.BufferedImage;

public class GTexturePaintW implements geogebra.common.awt.GPaint {
	
	//GRectangleW anchor = null;
	BufferedImage bi;

	public GTexturePaintW(GTexturePaintW tp) {
		//this.anchor = new GRectangleW();
		//this.anchor.setRect(tp.anchor.getX(), tp.anchor.getY(), tp.anchor.getWidth(), tp.anchor.getHeight());
		this.bi = tp.bi; //TODO do we need clone deep?
	}

	public GTexturePaintW(BufferedImage subImage) {
		this.bi = subImage;
	    //anchor = rect;
    }
	
	public BufferedImage getImg() {
		return bi;
	}
	
	
}
