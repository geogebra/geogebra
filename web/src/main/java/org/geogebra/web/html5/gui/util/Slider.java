package org.geogebra.web.html5.gui.util;

import org.geogebra.common.util.StringUtil;

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

public class Slider extends FocusWidget implements HasChangeHandlers,
        HasValue<Integer>, MouseDownHandler, MouseUpHandler, MouseMoveHandler {

	private Element range;
	private boolean valueChangeHandlerInitialized;
	private Integer valueOnDragStart;

	/**
	 * Create a new slider.
	 * 
	 * @param min
	 *            slider min
	 * @param max
	 *            slider max
	 */
	public Slider(int min, int max) {
		range = Document.get().createElement("input");
		range.setAttribute("type", "range");
		range.setAttribute("min", String.valueOf(min));
		range.setAttribute("max", String.valueOf(max));
		setRangeValue(range, String.valueOf(min));
		setElement(range);
		addMouseDownHandler(this);
		// addMouseMoveHandler(this);
		addMouseUpHandler(this);
	}

	public static native void addInputHandler(Element el,
			SliderInputHandler handler)/*-{
		el.oninput = function() {
			handler.@org.geogebra.web.html5.gui.util.SliderInputHandler::onSliderInput()();
		}
	}-*/;

	private native void setRangeValue(Element range, String value) /*-{
		range.value = value;
	}-*/;

	@Override
	public Integer getValue() {
		return StringUtil.empty(getRangeValue(range)) ? Integer.valueOf(0)
				: Integer.valueOf(getRangeValue(range));
	}

	private native String getRangeValue(Element rangeElement) /*-{
		return rangeElement.value;
	}-*/;

	public void setMinimum(int min) {
		range.setAttribute("min", String.valueOf(min));
	}

	public void setMaximum(int max) {
		range.setAttribute("max", String.valueOf(max));
	}

	public void setTickSpacing(int step) {
		range.setAttribute("step", String.valueOf(step));
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
	        ValueChangeHandler<Integer> handler) {
		if (!valueChangeHandlerInitialized) {
			valueChangeHandlerInitialized = true;
			addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					ValueChangeEvent.fire(Slider.this, getValue());
				}
			});
		}
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public void setValue(Integer value) {
		setValue(value, false);
	}

	@Override
	public void setValue(Integer value, boolean fireEvents) {
		// Integer oldValue = getValue();
		setSliderValue(String.valueOf(value));
		// if (fireEvents) {
		// ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
		// }
	}

	private void setSliderValue(String value) {
		setRangeValue(range, value);
	}

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		ValueChangeEvent.fireIfNotEqual(this, valueOnDragStart, getValue());

	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		valueOnDragStart = getValue();
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		event.stopPropagation();
	}

}
