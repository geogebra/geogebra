package geogebra.web.gui.util;

import geogebra.web.euclidian.EuclidianStyleBarW;
import geogebra.web.gui.images.AppResources;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author gabor
 * 
 * MyCanvasJbutton a Canvas that used as a button
 *
 */
public class MyCJButton extends Composite implements MouseDownHandler, MouseUpHandler, HasSetIcon {
	
	
	public static final String DEFAULT_BACKGROUND_STYLE = "white";
	private static final String DEFAULT_BORDER_STYLE = "gray";
	
	/**
	 *  Default button width for CanvasButtons
	 */
	public static int DEFAULT_BUTTON_WIDTH = 20;
	/**
	 * Default button height for CanvasButtons
	 */
	public static int DEFAULT_BUTTON_HEIGHT = 20;
	
	protected static int TEXT_OFFSET = 3;
	
	protected Canvas button;
	Context2d ctx = null;
	private Canvas tempCanvas = null;
	private Context2d tempContext = null;
	ImageData icon = null;
	/**
	 * button width
	 */
	protected int buttonWidth = DEFAULT_BUTTON_WIDTH;
	/**
	 * button height
	 */
	protected int buttonHeight = DEFAULT_BUTTON_HEIGHT;
	
	protected String backgroundStyle = DEFAULT_BACKGROUND_STYLE;
	protected String borderStyle = DEFAULT_BORDER_STYLE;
	private boolean isEnabled;
	
	/**
	 * 
	 * Creates a new button
	 * 
	 * @param icon as imageResource
	 */
	public MyCJButton(ImageResource icon) {
		this(new Image(icon.getSafeUri()));
    }

	public MyCJButton(final Image image) {
		button = Canvas.createIfSupported();
		button.setWidth(buttonWidth+"px");
		button.setHeight(buttonHeight+"px");
		button.setCoordinateSpaceHeight(buttonWidth);
		button.setCoordinateSpaceWidth(buttonHeight);
		ctx = button.getContext2d();
		ctx.setFillStyle(DEFAULT_BACKGROUND_STYLE);
		ctx.fillRect(0, 0, buttonWidth, buttonHeight);
		button.addMouseDownHandler(this);
		button.addMouseUpHandler(this);
		image.addLoadHandler(new LoadHandler() {
			
			public void onLoad(LoadEvent event) {
				ctx.clearRect(0, 0, buttonWidth, buttonHeight);
				ctx.setFillStyle("white");
				ctx.fillRect(0, 0, buttonWidth, buttonHeight);
				getImageDataForIcon(image);
			}
		});
		RootPanel.get().add(image);
		initWidget(button);
		setStyleName("MyCanvasButton");
		isEnabled = true;
    }

	/**
	 *  Creates a CanvasButton with empty image
	 */
	public MyCJButton() {
		this(new Image() {
			{
				setWidth(DEFAULT_BUTTON_WIDTH+"px");
				setHeight(DEFAULT_BUTTON_HEIGHT+"px");
				setUrl(AppResources.INSTANCE.empty().getSafeUri());
			}
		});
    }

	public void onMouseUp(MouseUpEvent event) {
		setDownState(false);
	}

	public void onMouseDown(MouseDownEvent event) {
		setDownState(true);
	}
	
	private void setDownState(boolean downState) {
	   int drawX = (buttonWidth / 2) - (icon.getWidth() /2 );
	   int drawY = (buttonHeight / 2) - (icon.getHeight() / 2);
	   ctx.clearRect(0, 0, buttonWidth, buttonHeight);
	   ctx.setFillStyle(backgroundStyle);
	   ctx.fillRect(0, 0, buttonWidth, buttonHeight);
	   ctx.putImageData(icon, drawX, drawY);
	   if (downState) {
		   ctx.setStrokeStyle(borderStyle);
		   ctx.strokeRect(0, 0, buttonWidth, buttonHeight);
	   }
    }

	

	public HandlerRegistration addClickHandler(EuclidianStyleBarW handler) {
		return button.addClickHandler(handler);
    }
	
	/*AG tmppublic void setIcon(ImageResource is) {
		if (is.getWidth() > 0 && is.getHeight() > 0) {
			final Image img = new Image(is.getSafeUri());
			img.addLoadHandler(new LoadHandler() {
				
				public void onLoad(LoadEvent event) {
					setIcon(ImageElement.as(img.getElement()));
				}
			});
			img.setVisible(false);
			RootPanel.get().add(img);
		}
    }

	protected void setIcon(ImageElement ie) {
		int iwidth = ie.getWidth();
		int iheight = ie.getHeight();
	    if (iwidth > 0 && iheight > 0) {
	    	Context2d ctx = getTempContext2D();
	    	tempCanvas.setWidth(iwidth+"px");
	    	tempCanvas.setHeight(iheight+"px");
	    	tempCanvas.setCoordinateSpaceWidth(iwidth);
	    	tempCanvas.setCoordinateSpaceHeight(iheight);
	    	ctx.drawImage(ie, 0, 0, iwidth, iheight);
	    	icon = ctx.getImageData(0, 0, iwidth, iheight);
	    	setDownState(false);
	    }
    }*/

	private Context2d getTempContext2D() {
	    if (tempContext == null) {
	    	tempCanvas = Canvas.createIfSupported();
	    	tempContext = tempCanvas.getContext2d();
	    }
	    return tempContext;
    }
	
	public void setIcon(ImageData ir) {
		icon = ir;
		buttonWidth = ir.getWidth();
		buttonHeight = ir.getHeight();
		button.setWidth(buttonWidth + "px");
		button.setCoordinateSpaceWidth(buttonWidth);
		setDownState(false);
	}

	/*AG tmppublic void setIcon(CanvasElement ce) {
		int cwidth = ce.getWidth();
		int cheight = ce.getHeight();
	    if (cwidth > 0 && cheight > 0) {
	    	Context2d ctx = getTempContext2D();
	    	tempCanvas.setWidth(cwidth+"px");
	    	tempCanvas.setHeight(cheight+"px");
	    	tempCanvas.setCoordinateSpaceWidth(cwidth);
	    	tempCanvas.setCoordinateSpaceHeight(cheight);
	    	ctx.drawImage(ce, 0, 0, cwidth, cheight);
	    	icon = ctx.getImageData(0, 0, cwidth, cheight);
	    	setDownState(false);
	    }
    }*/

	public Object getButton() {
	    return button;
    }

	public ImageData getIcon() {
	    return icon;
    }

	/**
	 * @param image gets the image data from the image
	 */
	void getImageDataForIcon(final Image image) {
	    int imgWidth = image.getWidth();
	    int imgHeight = image.getHeight();
	    Canvas tempC = Canvas.createIfSupported();
	    tempC.setWidth(imgWidth+"px");
	    tempC.setHeight(imgHeight+"px");
	    tempC.setCoordinateSpaceWidth(imgWidth);
	    tempC.setCoordinateSpaceHeight(imgHeight);
	    Context2d tempContext = tempC.getContext2d();
	    tempContext.drawImage(ImageElement.as(image.getElement()), 0, 0);
	    icon = tempContext.getImageData(0, 0, imgWidth, imgHeight);
    }
	
	protected boolean isEnabled() {
		return isEnabled ;
	}
	
	protected void setIsEnabled(boolean enabled) {
		isEnabled = enabled;
	}
	
	protected void addClickHandler(ClickHandler handler) {
		button.addClickHandler(handler);
	}
	
	protected void addMouseEntered(MouseOverHandler hanlder) {
		button.addMouseOverHandler(hanlder);
	}
	
	protected void addBlurHandler(BlurHandler handler) {
		button.addBlurHandler(handler);
	}
	
	protected void addFocusHandler(FocusHandler handler) {
		button.addFocusHandler(handler);
	}
	
	protected int getWidth() {
		return button.getCanvasElement().getWidth();
	}
	
	protected int getHeight() {
		return button.getCanvasElement().getHeight();
	}
	
	public void addActionListener(EuclidianStyleBarW euclidianStyleBar) {
		button.addClickHandler(euclidianStyleBar);
	}

}
