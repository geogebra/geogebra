package geogebra.web.gui.util;

import geogebra.web.euclidian.EuclidianStyleBar;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

public class MyCanvasButton extends Composite implements MouseDownHandler, MouseUpHandler {
	
	private Canvas button;
	private Context2d ctx = null;
	private CanvasElement icon = null;

	public MyCanvasButton(ImageResource icon) {
		this(new Image(icon));
    }

	public MyCanvasButton(Image image) {
		icon = createIcon(image);
		button = Canvas.createIfSupported();
		button.setWidth((icon.getWidth()+2)+"px");
		button.setHeight((icon.getHeight()+2)+"px");
		button.setCoordinateSpaceHeight(icon.getHeight()+2);
		button.setCoordinateSpaceWidth(icon.getWidth()+2);
		ctx = button.getContext2d();
		//ctx.drawImage(icon, 0,0);
		drawIcon(icon);
		button.addMouseDownHandler(this);
		button.addMouseUpHandler(this);
		initWidget(button);
		setStyleName("MyToggleButton");
    }
	
	public MyCanvasButton() {
	   this(new Image());
    }

	private CanvasElement createIcon(Image ic) {
	    Canvas c = Canvas.createIfSupported();
	    c.setWidth(ic.getWidth()+"px"+2);
		c.setHeight(ic.getHeight()+"px"+2);
		c.setCoordinateSpaceHeight(ic.getHeight()+2);
		c.setCoordinateSpaceWidth(ic.getWidth()+2);
		Context2d context = c.getContext2d();
		context.drawImage(ImageElement.as(ic.getElement()),2,2);
		return c.getCanvasElement();
    }

	public void onMouseUp(MouseUpEvent event) {
		//set up state

	}

	public void onMouseDown(MouseDownEvent event) {
		//set down state

	}

	public void addClickHandler(EuclidianStyleBar handler) {
		button.addClickHandler(handler);
    }
	
	private void drawIcon(CanvasElement ic) {
		   if (ic != null && ic.getHeight() > 0 && ic.getWidth() > 0) {
			   ctx.drawImage(ic, 0, 0);
		   }
	    }

	public void setIcon(CanvasElement canvas) {
		button.setWidth((canvas.getWidth()+2)+"px");
		button.setHeight((canvas.getHeight()+2)+"px");
		button.setCoordinateSpaceHeight(canvas.getHeight()+2);
		button.setCoordinateSpaceWidth(canvas.getWidth()+2);
		//ctx.drawImage(icon, 0,0);
		drawIcon(canvas);
    }

}
