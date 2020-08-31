package org.geogebra.web.html5.gui.util;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;

public class SliderPanel extends FlowPanel implements HasChangeHandlers,
		HasValue<Integer>, SliderInputHandler {

	private Slider slider;
	private Label sliderLabel;

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
		//minLabel = new Label(String.valueOf(min));
		// add(minLabel);
		slider = new Slider(min, max);
		add(slider);
		//maxLabel = new Label(String.valueOf(max));
		// add(maxLabel);
		sliderLabel = new Label();
		sliderLabel.setText(this.getValue() + "");
		add(sliderLabel);
		sliderLabel.addStyleName("popupSliderLabel");
		setStyleName("optionsSlider");
		Slider.addInputHandler(slider.getElement(), this);
	}

	@Override
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

	@Override
	public HandlerRegistration addValueChangeHandler(
	        ValueChangeHandler<Integer> handler) {
		return slider.addValueChangeHandler(handler);
	}

	@Override
	public void onSliderInput() {
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(),
				this.slider);
		sliderLabel.setText(this.getValue() + "");
	}

	@Override
	public void setValue(Integer value) {
		slider.setValue(value, false);
		sliderLabel.setText(this.getValue() + "");
	}

	@Override
	public void setValue(Integer value, boolean fireEvents) {
		slider.setValue(value, fireEvents);
		sliderLabel.setText(this.getValue() + "");
	}

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return slider.addChangeHandler(handler);
	}
}
