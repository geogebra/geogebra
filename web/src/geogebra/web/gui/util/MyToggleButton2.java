package geogebra.web.gui.util;

import geogebra.web.euclidian.EuclidianStyleBarW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;

public class MyToggleButton2 extends ToggleButton implements ClickHandler, HasValue<Boolean> {

	private static final long serialVersionUID = 1L;
	private Canvas button;
	private Context2d ctx = null;
	private boolean isDown = false;
	protected HorizontalPanel wrapper = null;
	private ImageElement icon;
	CanvasElement compiledicon;
	private HandlerRegistration actionListener;
	
	public MyToggleButton2() {
	    this(new Image(),0);
    }
	
	public MyToggleButton2(ImageResource icon, int iconHeight) {
		this(new Image(icon.getSafeUri()),iconHeight);
	}

	public MyToggleButton2(final Image image, int iconHeight) {
		
		super(image);
		setDown(false);
		setHeight(iconHeight+"px");
		setWidth(iconHeight+"px");
		//addClickHandler(this);
		addStyleName("MyToggleButton");
		
	}
	

	public void update(Object[] geos) {
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

	
	public void setSelected(boolean isSelected) {
	    // ignore for now
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
