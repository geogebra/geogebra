package org.geogebra.web.html5.gui.util;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

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

	public void setTickSpacing(int step) {
		slider.setTickSpacing(step);
	}

	public void addValueChangeHandler(ValueChangeHandler<Integer> handler) {
		slider.addValueChangeHandler(handler);
	}

	@Override
	public void onSliderInput() {
		slider.notifyValueChangeHandlers();
		sliderLabel.setText(this.getValue() + "");
	}

	/**
	 * @param value slider value
	 */
	public void setValue(Integer value) {
		slider.setValue(value, false);
		sliderLabel.setText(this.getValue() + "");
	}

}
