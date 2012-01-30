package geogebra.web.kernel.gawt;

import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.DOM;

public class BufferedImage {
	
	ImageElement img = null;

	public BufferedImage(int width, int height, int imageType) {
		img = ImageElement.as(DOM.createImg());
		img.setWidth(width);
		img.setHeight(height);
		img.setSrc(
			((CanvasElement)CanvasElement.createObject().cast()).toDataUrl("image/png")
		);
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
