package geogebra.html5.awt;

import geogebra.html5.gawt.BufferedImage;
import geogebra.html5.openjdk.awt.geom.Rectangle;
import geogebra.html5.openjdk.awt.geom.Rectangle2D;

import com.google.gwt.dom.client.ImageElement;

public class GTexturePaintW implements geogebra.common.awt.GPaint {
	
	//GRectangleW anchor = null;
	ImageElement img = null;

	public GTexturePaintW(GTexturePaintW tp) {
		//this.anchor = new GRectangleW();
		//this.anchor.setRect(tp.anchor.getX(), tp.anchor.getY(), tp.anchor.getWidth(), tp.anchor.getHeight());
		this.img = (ImageElement)tp.img.cloneNode(true);
	}

	public GTexturePaintW(BufferedImage subimage, Rectangle rect) {
	    img = subimage.getImageElement();
	    //anchor = rect;
    }

	public GTexturePaintW(BufferedImage copy, Rectangle2D tr) {
	    img = copy.getImageElement();
	    //anchor  = new GRectangleW((int) tr.getX(),(int) tr.getY(),(int) tr.getWidth(),(int) tr.getHeight());
    }
	
	public ImageElement getImg() {
		return img;
	}
	
	
}
