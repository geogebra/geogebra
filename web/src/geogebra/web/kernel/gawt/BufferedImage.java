package geogebra.web.kernel.gawt;

import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;

public class BufferedImage {
	
	CanvasElement img = null;

	public BufferedImage(int width, int height, int imageType) {
	    img = CanvasElement.createObject().cast();
	    img.setWidth(width);
	    img.setHeight(height);
    }

	public BufferedImage(CanvasElement canvasElement) {
	    img = canvasElement;
    }

	public BufferedImage(ImageElement imageElement) {

		Canvas cv = Canvas.createIfSupported();
		cv.setCoordinateSpaceWidth(imageElement.getWidth());
		cv.setCoordinateSpaceHeight(imageElement.getHeight());
		Context2d c2d = cv.getContext2d();
		c2d.drawImage(imageElement, 0, 0);
		img = (CanvasElement)cv.getCanvasElement().cloneNode(true);
    }

	public int getWidth() {
	   return img.getWidth();
    }
	
	public int getHeight() {
		return img.getHeight();
	}

	public CanvasElement getCanvasElement() {
	   return img;
    }
	
	
}
