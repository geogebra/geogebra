package geogebra.web.gui.util;

import geogebra.web.euclidian.EuclidianStyleBar;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class MyToggleButton extends Composite implements ClickHandler, HasValue<Boolean> {

	private static final long serialVersionUID = 1L;
	private Canvas button;
	private Context2d ctx = null;
	private boolean isDown = false;
	protected HorizontalPanel wrapper = null;
	private CanvasElement icon = null;

	public MyToggleButton(Image ic, int iconHeight) {
		icon = createIcon(ic);
		createCommonObjects();
		//Do it from CSS! changeStyle();
		//Dimension d = new Dimension(icon.getIconWidth(), iconHeight);
		//setIcon(GeoGebraIcon.ensureIconSize(icon, d));
	}

	private void createCommonObjects() {
	    wrapper = new HorizontalPanel();
		button = Canvas.createIfSupported();
		button.setWidth((icon.getWidth()+2)+"px");
		button.setHeight((icon.getHeight()+2)+"px");
		button.setCoordinateSpaceHeight(icon.getHeight()+2);
		button.setCoordinateSpaceWidth(icon.getWidth()+2);
		wrapper.add(button);
		ctx = button.getContext2d();
		//ctx.drawImage(icon, 0,0);
		drawIcon(icon);
		setDown(false);
		button.addClickHandler(this);
		initWidget(wrapper);
		setStyleName("MyToggleButton");
    }

	private void drawIcon(CanvasElement ic) {
	   if (ic != null && ic.getHeight() > 0 && ic.getWidth() > 0) {
		   ctx.drawImage(ic, 0, 0);
	   }
    }

	private void setDown(boolean down) {
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
		drawIcon(icon); 
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

	public MyToggleButton(ImageResource icon, int iconHeight) {
		this(new Image(icon.getSafeUri()),iconHeight);
		//changeStyle();
		//Dimension d = new Dimension(icon.getIconWidth(), iconHeight);
		//setIcon(GeoGebraIcon.ensureIconSize(icon, d));
	}

	public MyToggleButton() {
	    this(new Image(),0);
    }

	public MyToggleButton(CanvasElement ic) {
	    icon = createIcon(ic);
	    createCommonObjects();
    }

	private CanvasElement createIcon(CanvasElement ic) {
	    return ic;
    }

	public void changeStyle() {
		getElement().getStyle().setPaddingTop(3, Style.Unit.PX);
		getElement().getStyle().setPaddingLeft(3, Style.Unit.PX);
		getElement().getStyle().setPaddingRight(3, Style.Unit.PX);
		getElement().getStyle().setPaddingBottom(1, Style.Unit.PX);
		getElement().getStyle().setMargin(5, Style.Unit.PX);
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
		return addHandler(handler, ValueChangeEvent.getType());
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
	    return icon;
    }

	public void setIcon(CanvasElement canvas) {
	    icon.setWidth(canvas.getWidth());
	    icon.setHeight(canvas.getHeight());
	    icon.getContext2d().drawImage(canvas, 0, 0);
    }
	
	
}
