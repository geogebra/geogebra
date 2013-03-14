package geogebra.web.awt;

import com.google.gwt.dom.client.ImageElement;

public class GTexturePaintW implements geogebra.common.awt.GPaint {
	
	//GRectangleW anchor = null;
	ImageElement img = null;

	public GTexturePaintW(GTexturePaintW tp) {
		//this.anchor = new GRectangleW();
		//this.anchor.setRect(tp.anchor.getX(), tp.anchor.getY(), tp.anchor.getWidth(), tp.anchor.getHeight());
		this.img = (ImageElement)tp.img.cloneNode(true);
	}

	public GTexturePaintW(GBufferedImageW subimage, GRectangleW rect) {
	    img = subimage.getImageElement();
	    //anchor = rect;
    }

	public GTexturePaintW(GBufferedImageW copy, GRectangle2DW tr) {
	    img = copy.getImageElement();
	    //anchor  = new GRectangleW((int) tr.getX(),(int) tr.getY(),(int) tr.getWidth(),(int) tr.getHeight());
    }
	
	public ImageElement getImg() {
		return img;
	}
	
	
}
