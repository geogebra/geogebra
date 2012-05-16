package geogebra.web.gui.util;

import geogebra.web.euclidian.EuclidianStyleBar;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class MyCanvasButton extends Composite implements MouseDownHandler, MouseUpHandler {
	
	
	/**
	 *  Default button width for CanvasButtons
	 */
	public static int DEFAULT_BUTTON_WIDTH = 20;
	/**
	 * Default button height for CanvasButtons
	 */
	public static int DEFAULT_BUTTON_HEIGHT = 20;
	
	private Canvas button;
	Context2d ctx = null;
	private ImageElement icon;
	CanvasElement compiledicon;
	
	/**
	 * 
	 * Creates a new button
	 * 
	 * @param icon as imageResource
	 */
	public MyCanvasButton(ImageResource icon) {
		this(new Image(icon));
    }

	public MyCanvasButton(final Image image) {
		icon = ImageElement.as(image.getElement());
		button = Canvas.createIfSupported();
		button.setWidth(DEFAULT_BUTTON_WIDTH+"px");
		button.setHeight(DEFAULT_BUTTON_HEIGHT+"px");
		button.setCoordinateSpaceHeight(DEFAULT_BUTTON_HEIGHT);
		button.setCoordinateSpaceWidth(DEFAULT_BUTTON_WIDTH);
		ctx = button.getContext2d();
		ctx.setFillStyle("white");
		ctx.fillRect(0, 0, ctx.getCanvas().getWidth(), ctx.getCanvas().getHeight());
		button.addMouseDownHandler(this);
		button.addMouseUpHandler(this);
		image.addLoadHandler(new LoadHandler() {
			
			public void onLoad(LoadEvent event) {
				int cwidth = ctx.getCanvas().getWidth();
				int cheight = ctx.getCanvas().getHeight();
				ctx.clearRect(0, 0, cwidth, cheight);
				ctx.setFillStyle("white");
				ctx.fillRect(0, 0, cwidth, cheight);
				ctx.drawImage(icon, 0, 0);
				compiledicon = createCanvasElementFromImage(icon);
			}
		});
		RootPanel.get().add(image);
		initWidget(button);
		setStyleName("MyCanvasButton");
    }
	
	/**
	 * @param ie ImageElement
	 * @return CanvasElement with the image drawn on it.
	 */
	protected CanvasElement createCanvasElementFromImage(ImageElement ie) {
	   Canvas c = Canvas.createIfSupported();
	   int iwidth = ie.getWidth();
	   int iheight = ie.getHeight();
	   c.setWidth(iwidth+"px");
	   c.setHeight(iheight+"px");
	   c.setCoordinateSpaceWidth(iwidth);
	   c.setCoordinateSpaceHeight(iheight);
	   Context2d cctx = c.getContext2d();
	   cctx.clearRect(0,0,iwidth,iheight);
	   cctx.drawImage(ie, 0, 0);
	   return c.getCanvasElement();
    }

	/**
	 *  Creates a CanvasButton with empty image
	 */
	public MyCanvasButton() {
		this(new Image());
    }

	public void onMouseUp(MouseUpEvent event) {
		ctx.setFillStyle("white");
		ctx.fillRect(0, 0, ctx.getCanvas().getWidth(), ctx.getCanvas().getHeight());
		ctx.drawImage(compiledicon, 0, 0);
	}

	public void onMouseDown(MouseDownEvent event) {
		ctx.setStrokeStyle("gray");
		ctx.setLineWidth(2);
		ctx.strokeRect(1, 1, ctx.getCanvas().getWidth()-1, ctx.getCanvas().getHeight()-1);
		ctx.drawImage(compiledicon, 0, 0);
	}

	public HandlerRegistration addClickHandler(EuclidianStyleBar handler) {
		return button.addClickHandler(handler);
    }
	
	public void setIcon(ImageResource is) {
		icon.removeFromParent();
		Image i = new Image(is.getSafeUri());
		icon = ImageElement.as(i.getElement());
		i.addLoadHandler(new LoadHandler() {
			
			public void onLoad(LoadEvent event) {
				int cwidth = ctx.getCanvas().getWidth();
				int cheight = ctx.getCanvas().getHeight();
				ctx.clearRect(0, 0, cwidth, cheight);
				ctx.setFillStyle("white");
				ctx.fillRect(0, 0, cwidth, cheight);
				ctx.drawImage(icon, 0, 0);
				compiledicon = createCanvasElementFromImage(icon);
			}
		});
		RootPanel.get().add(i);
    }

	public void setIcon(CanvasElement ic) {
		if (ic != null) {
				compiledicon = ic;
				int icwidth = ic.getWidth();
				int icheight = ic.getHeight();
				int ctxwidth = ctx.getCanvas().getWidth();
				int ctxheight = ctx.getCanvas().getHeight();
				int drawX = (ctxwidth/2) - (icwidth/2);
				int drawY = (ctxheight/2) - (icheight/2);
				ctx.clearRect(0, 0, ctx.getCanvas().getHeight(), ctx.getCanvas().getWidth());
				ctx.setFillStyle("white");
				ctx.fillRect(0, 0, ctx.getCanvas().getWidth(), ctx.getCanvas().getHeight());
				ctx.drawImage(compiledicon, drawX, drawY);
		}
    }

	public Object getButton() {
	    return button;
    }

	public CanvasElement getIcon() {
	    return compiledicon;
    }

}
