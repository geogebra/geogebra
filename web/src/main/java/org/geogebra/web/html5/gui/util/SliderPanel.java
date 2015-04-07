package org.geogebra.web.html5.gui.util;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.awt.GDimensionW;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;

public class SliderPanel extends FlowPanel implements HasChangeHandlers,
        HasValue<Integer> {

	private Slider slider;
	private Label minLabel;
	private Label maxLabel;

	public SliderPanel() {
		this(0, 100);
	}

	public SliderPanel(int min, int max) {
		minLabel = new Label(String.valueOf(min));
		add(minLabel);
		slider = new Slider(min, max);
		add(slider);
		maxLabel = new Label(String.valueOf(max));
		add(maxLabel);
		setStyleName("optionsSlider");
	}

	public Integer getValue() {
		return slider.getValue();
	}

	public void setMinimum(int min) {
		slider.setMinimum(min);
		minLabel.setText(String.valueOf(min));
	}

	public void setMaximum(int max) {
		slider.setMaximum(max);
		maxLabel.setText(String.valueOf(max));
	};

	public void setMajorTickSpacing(int step) {
		slider.setMajorTickSpacing(step);
	}

	public void setMinorTickSpacing(int step) {
		slider.setMinorTickSpacing(step);
	}

	public void setPaintTicks(boolean b) {
		App.debug("not applicable for range");
	}

	public void setPaintLabels(boolean b) {
		App.debug("not applicable for range");
	}

	public GDimensionW getPreferredSize() {
		return new GDimensionW(180, 10);
	}

	public HandlerRegistration addValueChangeHandler(
	        ValueChangeHandler<Integer> handler) {
		return slider.addValueChangeHandler(handler);
	}

	public void setValue(Integer value) {
		slider.setValue(value, false);
	}

	public void setValue(Integer value, boolean fireEvents) {
		slider.setValue(value, fireEvents);
	}

	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return slider.addChangeHandler(handler);
	}
}
