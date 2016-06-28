package org.geogebra.web.html5.gui.util;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.awt.GDimensionW;

import com.google.gwt.core.client.JavaScriptObject;
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
        HasValue<Integer> {

	private Slider slider;
	//private Label minLabel;
	//private Label maxLabel;
	public Label sliderLabel;

	public SliderPanel() {
		this(0, 100);
	}

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
		Slider.addInputHandler(slider.getElement(), getInputHandler(this));
	}

	public Integer getValue() {
		return slider.getValue();
	}

	public void setMinimum(int min) {
		slider.setMinimum(min);
		// minLabel.setText(String.valueOf(min));
	}

	public void setMaximum(int max) {
		slider.setMaximum(max);
		// maxLabel.setText(String.valueOf(max));
	}

	public void setMajorTickSpacing(int step) {
		slider.setMajorTickSpacing(step);
	}

	public void setMinorTickSpacing(int step) {
		slider.setMinorTickSpacing(step);
	}

	public void setPaintTicks(boolean b) {
		Log.debug("not applicable for range");
	}

	public void setPaintLabels(boolean b) {
		Log.debug("not applicable for range");
	}

	public GDimensionW getPreferredSize() {
		return new GDimensionW(180, 10);
	}

	public HandlerRegistration addValueChangeHandler(
	        ValueChangeHandler<Integer> handler) {
		return slider.addValueChangeHandler(handler);
	}

	private native JavaScriptObject getInputHandler(SliderPanel sp)/*-{
		return function() {
			sp.@org.geogebra.web.html5.gui.util.SliderPanel::doOninput()();
		}
	}-*/;

	private void doOninput() {
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(),
				this.slider);
		sliderLabel.setText(this.getValue() + "");
	}

	public void setValue(Integer value) {
		slider.setValue(value, false);
		sliderLabel.setText(this.getValue() + "");
	}

	public void setValue(Integer value, boolean fireEvents) {
		slider.setValue(value, fireEvents);
		sliderLabel.setText(this.getValue() + "");
	}

	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return slider.addChangeHandler(handler);
	}
}
