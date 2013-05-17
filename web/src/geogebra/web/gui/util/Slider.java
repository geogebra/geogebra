package geogebra.web.gui.util;

import geogebra.common.main.App;
import geogebra.html5.awt.GDimensionW;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasValue;

public class Slider extends FocusWidget implements HasChangeHandlers, HasValue<Integer>, MouseDownHandler, MouseUpHandler, MouseMoveHandler {
	
	private Element range;
	private boolean valueChangeHandlerInitialized;
	private Integer valueOnDragStart;
	 
	public Slider() {
		this(0,100);
	}

	public Slider(int min, int max) {
	   range = Document.get().createElement("input");
	   range.setAttribute("type", "range");
	   range.setAttribute("min", String.valueOf(min));
	   range.setAttribute("max", String.valueOf(max));   
	   setRangeValue(range,String.valueOf(min));
	   setElement(range);
	   addMouseDownHandler(this);
	   //addMouseMoveHandler(this);
	   addMouseUpHandler(this);
	   
    }

	private native void setRangeValue(Element range, String value) /*-{
	   range.value = value;
    }-*/;

	public void removeChangeListener(PopupMenuButton popupMenuButton) {
	    // TODO Auto-generated method stub
	    
    }

	public void addChangeListener(PopupMenuButton popupMenuButton) {
		addChangeHandler(popupMenuButton);
    }
	

	public Integer getValue() {
	   return Integer.valueOf(getRangeValue(range));
    }

	private native  String getRangeValue(Element range) /*-{
	    return range.value;
    }-*/;

	public void setMinimum(int min) {
	    range.setAttribute("min", String.valueOf(min));
    }

	public void setMaximum(int max) {
		range.setAttribute("max", String.valueOf(max));
    };

	public void setMajorTickSpacing(int step) {
		range.setAttribute("step", String.valueOf(step));
    }

	public void setMinorTickSpacing(int step) {
		range.setAttribute("step", String.valueOf(step));
    }

	public void setPaintTicks(boolean b) {
		App.debug("not applicable for range");
    }

	public void setPaintLabels(boolean b) {
		App.debug("not applicable for range");
    }

	public GDimensionW getPreferredSize() {
	   return new GDimensionW(100,10);
    }

	public HandlerRegistration addValueChangeHandler(
            ValueChangeHandler<Integer> handler) {
		 if (!valueChangeHandlerInitialized) {
		      valueChangeHandlerInitialized = true;
		      addChangeHandler(new ChangeHandler() {
		        public void onChange(ChangeEvent event) {
		          ValueChangeEvent.fire(Slider.this, getValue());
		        }
		      });
		    }
		    return addHandler(handler, ValueChangeEvent.getType());
    }

	public void setValue(Integer value) {
		setValue(value,false);
    }

	public void setValue(Integer value, boolean fireEvents) {
		//Integer oldValue = getValue();
	    setSliderValue(String.valueOf(value));
	    //if (fireEvents) {
	    //  ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
	    //}
    }

	private void setSliderValue(String value) {
	   setRangeValue(range, value);
    }

	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
    }

	public void onMouseUp(MouseUpEvent event) {
	   ValueChangeEvent.fireIfNotEqual(this, valueOnDragStart, getValue());
	    
    }

	public void onMouseDown(MouseDownEvent event) {
	   valueOnDragStart = getValue();
    }

	public void onMouseMove(MouseMoveEvent event) {
	    event.stopPropagation();
    }

}
