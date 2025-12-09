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

import java.util.function.Consumer;

import org.gwtproject.event.logical.shared.ValueChangeHandler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class SliderPanel extends FlowPanel implements SliderInputHandler {

	private final Slider slider;
	private final Label sliderLabel;

	public SliderPanel() {
		this(0, 100);
	}

	/**
	 * @param min
	 *            slider min
	 * @param max
	 *            slider max
	 */
	public SliderPanel(int min, int max) {
		slider = new Slider(min, max);
		add(slider);
		sliderLabel = new Label();
		sliderLabel.setText(this.getValue() + "");
		add(sliderLabel);
		sliderLabel.addStyleName("popupSliderLabel");
		setStyleName("optionsSlider");
		slider.addInputHandler(this);
	}

	public Integer getValue() {
		return slider.getValue();
	}

	/**
	 * @param min
	 *            range minimum
	 */
	public void setMinimum(int min) {
		slider.setMinimum(min);
	}

	/**
	 * @param max
	 *            range maximum
	 */
	public void setMaximum(int max) {
		slider.setMaximum(max);
	}

	/**
	 * Set slider step.
	 * @param step step
	 */
	public void setTickSpacing(int step) {
		slider.setTickSpacing(step);
	}

	/**
	 * Add a change handler.
	 * @param handler change handler
	 */
	public void addValueChangeHandler(ValueChangeHandler<Integer> handler) {
		slider.addValueChangeHandler(handler);
	}

	/**
	 * Add an input handler.
	 * @param handler input event handler
	 */
	public void addInputHandler(Consumer<Integer> handler) {
		slider.addInputHandler(() -> handler.accept(slider.getValue()));
	}

	@Override
	public void onSliderInput() {
		sliderLabel.setText(this.getValue() + "");
	}

	/**
	 * @param value slider value
	 */
	public void setValue(Integer value) {
		slider.setValue(value);
		sliderLabel.setText(this.getValue() + "");
	}

}
