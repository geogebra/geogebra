package geogebra.web.awt;

import geogebra.common.awt.BufferedImageAdapter;
import geogebra.common.awt.Graphics2D;

public class BufferedImage implements BufferedImageAdapter {
	
	private geogebra.web.kernel.gawt.BufferedImage impl;
	
	public BufferedImage(int width, int height, int imageType) {
		impl = new geogebra.web.kernel.gawt.BufferedImage(width,height,imageType);
	}

	public BufferedImage(BufferedImageAdapter fillImage) {
	    // TODO Auto-generated constructor stub
    }

	
	public int getWidth() {
		return impl.getWidth();
	}

	
	public int getHeight() {
		return impl.getHeight();
	}

	public Graphics2D createGraphics() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public BufferedImage getSubimage(int xInt, int yInt, int xInt2, int yInt2) {
	    // TODO Auto-generated method stub
	    return null;
    }

}
