package org.geogebra.web.html5.util.sliderPanel;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * GWT wrapper for JQuery UI slider
 */
public class SliderWJquery extends FocusWidget
		implements SliderWI, TouchMoveHandler {

	private Element range;
	private boolean valueChangeHandlerInitialized;
	private Double curValue;
	private static volatile SliderWJquery currentSlider;

	/**
	 * Creates new slider
	 * 
	 * @param min
	 *            min
	 * @param max
	 *            max
	 */
	public SliderWJquery(double min, double max) {
		range = Document.get().createElement("div");
		range.getStyle().setWidth(200, Unit.PX);
		setup(range, min, max, min);

		setElement(range);
		addMouseDownHandler(this);
		addMouseMoveHandler(this);
		addMouseUpHandler(this);
		addTouchMoveHandler(this);
	}

	private native void setup(Element range1, double min, double max, double val)/*-{
		var that = this;
		var j = $wnd.$ggbQuery || $wnd.jQuery;
		j(range1)
				.slider(
						{
							"min" : min,
							"max" : max,
							"animate" : false,
							"values" : [ val ],
							"slide" : function(event, ui) {
								that.@org.geogebra.web.html5.util.sliderPanel.SliderWJquery::slide(D)(ui.value)
							}
						});

	}-*/ ;

	@Override
	public void setScale(double zoom) {
		setScale(range, zoom);
	}

	private native void setScale(Element range1, double zoom) /*-{
		var $ = $wnd.$ggbQuery || $wnd.jQuery;
		$(range1).slider("setzoom", zoom);
	}-*/;
	
	private native void setRangeValue(Element range1, double val) /*-{
		var $ = $wnd.$ggbQuery || $wnd.jQuery;
		$(range1).slider("values", [ val ]);
	}-*/;

	@Override
	public Double getValue() {
		return getRangeValue(range);
	}

	private void stop() {
		stopNative(range);
	}

	private native void stopNative(Element range1) /*-{
		var $ = $wnd.$ggbQuery || $wnd.jQuery;
		$(range1).slider("doCancel");
	}-*/;

	private native double getRangeValue(Element range1) /*-{
		var $ = $wnd.$ggbQuery || $wnd.jQuery;
		return $(range1).slider("values")[0];
	}-*/;

	private native void setProperty(Element range1, String prop,
			double val) /*-{
		var $ = $wnd.$ggbQuery || $wnd.jQuery;
		$(range1).slider("option", prop, val);
	}-*/;

	@Override
	public void setMaximum(double max) {
		setProperty(range, "max", max);
	}

	@Override
	public void setMinimum(double min) {
		setProperty(range, "min", min);
	}

	@Override
	public void setStep(double step) {
		setProperty(range, "step", step);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Double> handler) {
		if (!valueChangeHandlerInitialized) {
			valueChangeHandlerInitialized = true;
			addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					ValueChangeEvent.fire(SliderWJquery.this, getValue());
				}
			});
		}
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public void setValue(Double value) {
		setValue(value, false);
	}

	@Override
	public void setValue(Double value, boolean fireEvents) {
		setRangeValue(range, value);
	}

	/*
	 * private void setSliderValue(String value) { setRangeValue(range, value);
	 * }
	 */

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		ValueChangeEvent.fireIfNotEqual(this, curValue, getValue());
		curValue = getValue();
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (event.getNativeButton() != NativeEvent.BUTTON_RIGHT) {	
			event.stopPropagation();
			currentSlider = this;
		}
		// curValue = getValue();
	}

	private void slide(double val) {
		ValueChangeEvent.fireIfNotEqual(this, curValue, val);
		curValue = val;
	}
	@Override
	public void onMouseMove(MouseMoveEvent event) {

		// event.stopPropagation();
		slideValue();
	}

	private void slideValue() {
		Double value = getValue();
		if (curValue != null) {
			slide(value);
		}
	}

	/**
	 * Cancels current slider
	 */
	public static void stopSliders() {
		if (currentSlider != null) {
			currentSlider.stop();
		}

	}

	public void onTouchMove(TouchMoveEvent event) {
		event.stopPropagation();
		event.preventDefault();
		slideValue();
	}
}