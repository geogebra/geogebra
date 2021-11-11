package org.geogebra.web.html5.util.sliderPanel;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
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
 * Slider based on default DOM range input.
 *
 */
public class SliderW extends FocusWidget implements SliderWI {

	private InputElement range;
	private boolean valueChangeHandlerInitialized;
	private Double curValue;

	/**
	 * @param min
	 *            slider min
	 * @param max
	 *            slider max
	 */
	public SliderW(double min, double max) {
		range = Document.get().createTextInputElement();
		range.setAttribute("type", "range");
		range.setAttribute("min", String.valueOf(min));
		range.setAttribute("max", String.valueOf(max));
		range.setValue(String.valueOf(min));
		setElement(range);
		addMouseDownHandler(this);
		addMouseMoveHandler(this);
		addMouseUpHandler(this);
	}

	@Override
	public Double getValue() {
		return Double.valueOf(range.getValue());
	}

	/**
	 * disable range input
	 * 
	 * @param disable
	 *            true if range should be disabled
	 */
	public void disableSlider(boolean disable) {
		if (disable) {
			range.setAttribute("disabled", "true");
		} else {
			range.removeAttribute("disabled");
		}
	}

	@Override
	public void setMinimum(double min) {
		range.setAttribute("min", String.valueOf(min));
	}

	@Override
	public void setMaximum(double max) {
		range.setAttribute("max", String.valueOf(max));
	}

	@Override
	public void setStep(double step) {
		range.setAttribute("step", String.valueOf(step));
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Double> handler) {
		if (!valueChangeHandlerInitialized) {
			valueChangeHandlerInitialized = true;
			addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					ValueChangeEvent.fire(SliderW.this, getValue());
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
		range.setValue(String.valueOf(value));
	}

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		ValueChangeEvent.fireIfNotEqual(this, curValue, getValue());
		curValue = null;
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		curValue = getValue();
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		event.stopPropagation();
		Double value = getValue();
		if (curValue != null) {
			ValueChangeEvent.fireIfNotEqual(this, curValue, value);
			curValue = value;
		}
	}
}