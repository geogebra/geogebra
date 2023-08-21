package org.geogebra.web.html5.gui.util;

import java.util.ArrayList;

import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.InputElement;
import org.gwtproject.event.dom.client.ChangeEvent;
import org.gwtproject.event.dom.client.DomEvent;
import org.gwtproject.event.logical.shared.ValueChangeEvent;
import org.gwtproject.event.logical.shared.ValueChangeHandler;
import org.gwtproject.user.client.ui.FocusWidget;

public abstract class SliderAbstract<T> extends FocusWidget {

	private final InputElement range;
	private boolean valueChangeHandlerInitialized;
	private final ArrayList<ValueChangeHandler<T>> valueChangeHandlers = new ArrayList<>();

	/**
	 * Create a new slider.
	 * @param min slider min
	 * @param max slider max
	 */
	public SliderAbstract(double min, double max) {
		range = Document.get().createTextInputElement();
		range.setAttribute("type", "range");
		range.setAttribute("min", String.valueOf(min));
		range.setAttribute("max", String.valueOf(max));
		range.setValue(String.valueOf(min));
		setElement(range);
		addMouseMoveHandler(DomEvent::stopPropagation);
	}

	public void addInputHandler(SliderInputHandler handler) {
		Dom.addEventListener(range, "input", evt -> handler.onSliderInput());
	}

	public T getValue() {
		return convert(range.getValue());
	}

	protected abstract T convert(String val);

	public void setMinimum(double min) {
		range.setAttribute("min", String.valueOf(min));
	}

	public void setMaximum(double max) {
		range.setAttribute("max", String.valueOf(max));
	}

	public void setStep(double step) {
		range.setAttribute("step", String.valueOf(step));
	}

	public void setTickSpacing(int step) {
		range.setAttribute("step", String.valueOf(step));
	}

	/**
	 * @param handler handler for change event (drag end)
	 */
	public void addValueChangeHandler(ValueChangeHandler<T> handler) {
		if (!valueChangeHandlerInitialized) {
			valueChangeHandlerInitialized = true;
			addDomHandler(event -> this.notifyValueChangeHandlers(), ChangeEvent.getType());
		}
		valueChangeHandlers.add(handler);
	}

	public void setValue(T value) {
		range.setValue(String.valueOf(value));
	}

	/**
	 * Notify change handlers.
	 */
	public void notifyValueChangeHandlers() {
		for (ValueChangeHandler<T> handler: valueChangeHandlers) {
			handler.onValueChange(new SliderValueChangeEvent<>(getValue()));
		}
	}

	private static class SliderValueChangeEvent<E> extends ValueChangeEvent<E> {

		/**
		 * Creates a value change event.
		 * @param value the value
		 */
		protected SliderValueChangeEvent(E value) {
			super(value);
		}
	}
}

