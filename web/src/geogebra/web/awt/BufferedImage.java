package geogebra.web.awt;

import geogebra.common.awt.BufferedImageAdapter;
import geogebra.common.awt.Graphics2D;
import geogebra.common.main.AbstractApplication;

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
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	public BufferedImage getSubimage(int xInt, int yInt, int xInt2, int yInt2) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	public static geogebra.web.kernel.gawt.BufferedImage getGawtImage(BufferedImageAdapter img) {
		if(!(img instanceof BufferedImage))
			return null;
		return ((BufferedImage)img).impl;
	    
    }

}
