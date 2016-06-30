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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * GWT wrapper for JQuery UI slider
 */
public class SliderWJquery extends FocusWidget implements SliderWI {

	private Element range;
	private boolean valueChangeHandlerInitialized;
	private Double curValue;
	private static SliderWJquery currentSlider;

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
	}

	private native void setup(Element range1, double min, double max, double val)/*-{
		var that = this;
		$wnd
				.$ggbQuery(range1)
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

	public void setScale(float zoom) {
		setScale(range, zoom);
	}

	private native void setScale(Element range1, float zoom) /*-{
		$wnd.$ggbQuery(range1).slider("setzoom", zoom);
	}-*/;
	
	private native void setRangeValue(Element range1, double val) /*-{
		$wnd.$ggbQuery(range1).slider("values", [ val ]);
	}-*/;

	public Double getValue() {
		return getRangeValue(range);
	}

	private void stop() {
		stopNative(range);
	}

	private native void stopNative(Element range1) /*-{
		$wnd.$ggbQuery(range1).slider("doCancel");
	}-*/;

	private native double getRangeValue(Element range1) /*-{
		return $wnd.$ggbQuery(range1).slider("values")[0];
	}-*/;

	private native void setProperty(Element range1, String prop,
			double val) /*-{
		$wnd.$ggbQuery(range1).slider("option", prop, val);
	}-*/;

	public void setMaximum(double max) {
		setProperty(range, "max", max);
	}

	public void setMinimum(double min) {
		setProperty(range, "min", min);
	}

	public void setStep(double step) {
		setProperty(range, "step", step);
	}

	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Double> handler) {
		if (!valueChangeHandlerInitialized) {
			valueChangeHandlerInitialized = true;
			addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					ValueChangeEvent.fire(SliderWJquery.this, getValue());
				}
			});
		}
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public void setValue(Double value) {
		setValue(value, false);
	}

	public void setValue(Double value, boolean fireEvents) {
		setRangeValue(range, value);
	}

	/*
	 * private void setSliderValue(String value) { setRangeValue(range, value);
	 * }
	 */

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
		Double value = getValue();
		if (curValue != null) {
			slide(value);
		}
	}

	public static SliderWJquery as(SliderWI obj) {
		return (SliderWJquery) obj;
	}

	/**
	 * Cancels current slider
	 */
	public static void stopSliders() {
		if (currentSlider != null) {
			currentSlider.stop();
		}

	}
}