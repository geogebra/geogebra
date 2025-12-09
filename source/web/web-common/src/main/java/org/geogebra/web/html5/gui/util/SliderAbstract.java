/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

	/**
	 * Add input handler.
	 * @param handler input handler
	 */
	public void addInputHandler(SliderInputHandler handler) {
		Dom.addEventListener(range, "input", evt -> handler.onSliderInput());
	}

	/**
	 * @return slider value
	 */
	public T getValue() {
		return convert(range.getValue());
	}

	/**
	 * @param val native value
	 * @return converted value
	 */
	protected abstract T convert(String val);

	/**
	 * Set range minimum.
	 * @param min minimum
	 */
	public void setMinimum(double min) {
		range.setAttribute("min", String.valueOf(min));
	}

	/**
	 * Set range maximum.
	 * @param max maximum
	 */
	public void setMaximum(double max) {
		range.setAttribute("max", String.valueOf(max));
	}

	/**
	 * @param step step
	 */
	public void setStep(double step) {
		range.setAttribute("step", String.valueOf(step));
	}

	/**
	 * @param step tick spacing
	 */
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

	/**
	 * Set slider value.
	 * @param value new value
	 */
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

