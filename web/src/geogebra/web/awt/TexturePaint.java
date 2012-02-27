package geogebra.web.awt;

import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;

public class TexturePaint implements geogebra.common.awt.Paint {
	
	Rectangle anchor = null;
	BufferedImage img = null;

	public TexturePaint(TexturePaint tp) {
		this.anchor = new Rectangle();
		this.anchor.setRect(tp.anchor.getX(), tp.anchor.getY(), tp.anchor.getWidth(), tp.anchor.getHeight());
		this.img = new BufferedImage((ImageElement)tp.img.getImageElement().cloneNode(true));
	}

	public TexturePaint(BufferedImage subimage, Rectangle rect) {
	    img = subimage;
	    anchor = rect;
    }

	public TexturePaint(BufferedImage copy, Rectangle2D tr) {
	    img = copy;
	    anchor  = new Rectangle((int) tr.getX(),(int) tr.getY(),(int) tr.getWidth(),(int) tr.getHeight());
    }
	
	public ImageElement getImg() {
		return img.getImageElement();
	}
	
	
}
