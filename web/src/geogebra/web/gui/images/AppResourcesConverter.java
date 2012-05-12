package geogebra.web.gui.images;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.Image;

public class AppResourcesConverter {

	public static CanvasElement convert(ImageResource ir) {
	    Canvas c = Canvas.createIfSupported();
	    c.setWidth(ir.getWidth()+"px");
	    c.setCoordinateSpaceWidth(ir.getWidth());
	    c.setHeight(ir.getHeight()+"px");
	    c.setCoordinateSpaceHeight(ir.getHeight());
	    SafeUri uri= ir.getSafeUri();        
	    ImageElement ie= ImageElement.as((new Image(uri)).getElement());
	    c.getContext2d().drawImage(ie,0,0);
	    return c.getCanvasElement();    
    }
}
