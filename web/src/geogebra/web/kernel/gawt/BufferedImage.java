package geogebra.web.kernel.gawt;

import geogebra.common.awt.BufferedImageAdapter;

import com.google.gwt.dom.client.ImageElement;

public class BufferedImage extends ImageElement {

	protected BufferedImage(){
		super();
	}

	public BufferedImage(int width, int height, int imageType) {
	    this();
	    setWidth(width);
	    setHeight(height);
    }
	
	
}
