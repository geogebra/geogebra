package geogebra.web.kernel.gawt;

import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.DOM;

public class BufferedImage {
	
	ImageElement img = null;

	public BufferedImage(int width, int height, int imageType) {

		img = ImageElement.as(DOM.createImg());
		img.setWidth(width);
		img.setHeight(height);

		CanvasElement nc = CanvasElement.createObject().cast();
		nc.setWidth(width);
		nc.setHeight(height);

		/* The above three lines are not working, only the below lines,
		   but I have commented this out as currently everything was traced
		   not just the trace geos... Arpad Fekete, 2012-02-01
		Canvas nc = Canvas.createIfSupported();
		nc.setCoordinateSpaceWidth(width);
		nc.setCoordinateSpaceHeight(height);

		Context2d c2d = nc.getContext2d();
		c2d.setStrokeStyle("#ffffff");
		c2d.setFillStyle("#ffffff");
		c2d.fillRect(0, 0, width, height);
		*/

		img.setSrc(nc.toDataUrl());//TODO: imageType
    }

	public BufferedImage(ImageElement imageElement) {
	    img = imageElement;
    }

	public int getWidth() {
	   return img.getWidth();
    }
	
	public int getHeight() {
		return img.getHeight();
	}

	public ImageElement getImageElement() {
	   return img;
    }
	
	
}
