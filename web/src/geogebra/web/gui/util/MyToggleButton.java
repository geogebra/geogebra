package geogebra.web.gui.util;

import geogebra.web.euclidian.EuclidianStyleBarW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class MyToggleButton extends Composite implements ClickHandler, HasValue<Boolean> {

	private static final long serialVersionUID = 1L;
	private Canvas button;
	private Context2d ctx = null;
	private boolean isDown = false;
	protected HorizontalPanel wrapper = null;
	private ImageElement icon;
	CanvasElement compiledicon;
	private HandlerRegistration actionListener;
	
	public MyToggleButton() {
	    this(new Image(),0);
    }
	
	public MyToggleButton(ImageResource icon, int iconHeight) {
		this(new Image(icon.getSafeUri()),iconHeight);
	}

	public MyToggleButton(final Image image, int iconHeight) {
		
		image.addLoadHandler(new LoadHandler() {
			
			public void onLoad(LoadEvent event) {
				ctx.drawImage(icon, 0, 0);
				compiledicon = createCanvasElementFromImage(icon);
				setDown(false);
			}
		});
		RootPanel.get().add(image);
		
		icon = ImageElement.as(image.getElement());
		
		createCommonObjects();
	}
	
	public MyToggleButton(CanvasElement ic) {
		   compiledicon = ic;
		   createCommonObjects();
		   setDown(false);
	    }

	private void createCommonObjects() {
	    button = Canvas.createIfSupported();
		button.setWidth(MyCJButton.DEFAULT_BUTTON_WIDTH+"px");
		button.setHeight(MyCJButton.DEFAULT_BUTTON_HEIGHT+"px");
		button.setCoordinateSpaceHeight(MyCJButton.DEFAULT_BUTTON_WIDTH);
		button.setCoordinateSpaceWidth(MyCJButton.DEFAULT_BUTTON_WIDTH);
		ctx = button.getContext2d();
		wrapper = new HorizontalPanel();
		wrapper.add(button);
		button.addClickHandler(this);
		initWidget(wrapper);
		setStyleName("MyToggleButton");
    }
	
	/*
	 * @param ie ImageElement
	 * @return CanvasElement with the image drawn on it.
	 */
	protected CanvasElement createCanvasElementFromImage(ImageElement ie) {
	   Canvas c = Canvas.createIfSupported();
	   int iwidth = ie.getWidth();
	   int iheight = ie.getHeight();
	   c.setWidth(iwidth+"px");
	   c.setHeight(iheight+"px");
	   c.setCoordinateSpaceHeight(iheight);
	   c.setCoordinateSpaceWidth(iwidth);
	   c.getContext2d().drawImage(ie, 0, 0);
	   return c.getCanvasElement();
    }

	void setDown(boolean down) {
		int width = ctx.getCanvas().getWidth();
		int height = ctx.getCanvas().getHeight();
		if (down) {
			ctx.setStrokeStyle("gray");
			isDown = true;
		} else {
			ctx.setStrokeStyle("white");
			isDown = false;
		}
		ctx.clearRect(0, 0, width, height);
		ctx.beginPath();
		ctx.setLineWidth(2);
		ctx.rect(0, 0, width-2, height-2);
		ctx.closePath();
		ctx.stroke();
		ctx.drawImage(compiledicon, 0, 0); 
    }

	

	

	

	public void update(Object[] geos) {
	}

	public void setSelected(boolean selected) {
	    // TODO Auto-generated method stub
	    
    }
	
	/**
	 * @return boolean
	 * 
	 * Determines if MyToggleButton is down state
	 */
	public boolean isDown() {
		return isDown;
	}

	public void onClick(ClickEvent event) {
	    if (isDown()) {
	    	setValue(false,true);
	    } else {
	    	setValue(true,true);
	    }
    }

	public HandlerRegistration addValueChangeHandler(
            ValueChangeHandler<Boolean> handler) {
		actionListener = addHandler(handler, ValueChangeEvent.getType());
		return actionListener;
    }

	public Boolean getValue() {
		 return isDown();
    }

	public void setValue(Boolean value) {
	    setValue(value, false);
	  }

	public void setValue(Boolean value, boolean fireEvents) {
	    if (value == null) {
	      value = Boolean.FALSE;
	    }
	    boolean oldValue = isDown();
	    setDown(value);
	    if (fireEvents) {
	      ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
	    }
	  }

	public void addClickHandler(ClickHandler clickHandler) {
	   button.addClickHandler(clickHandler);
    }

	public CanvasElement getIcon() {
	    return compiledicon;
    }

	public void setIcon(CanvasElement canvas) {
		compiledicon = canvas;
		ctx.clearRect(0, 0, ctx.getCanvas().getWidth(), ctx.getCanvas().getHeight());
	    ctx.drawImage(compiledicon, 0, 0);
    }

	public void setDimension(int w, int h) {
	   button.setWidth(w+"px");
	   button.setHeight(h+"px");
	   button.setCoordinateSpaceWidth(w);
	   button.setCoordinateSpaceHeight(h);
	   ctx.setFillStyle("white");
	   ctx.fillRect(0, 0, w, h);
	   ctx.drawImage(compiledicon, 0, 0);
    }

	public Object getButton() {
	   return button;
    }

	public boolean isSelected() {
	    return isDown;
    }

	public void removeValueChangeHandler(EuclidianStyleBarW euclidianStyleBar) {
		if (actionListener != null) {
			actionListener.removeHandler();
		}
	}
	
	
}
