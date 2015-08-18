package org.geogebra.web.html5.util.sliderPanel;

import org.geogebra.common.awt.GColor;
import org.geogebra.web.html5.awt.GColorW;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;

public class SliderPanelW extends FlowPanel implements HasChangeHandlers,
		HasValue<Double> {

	private SliderWI slider;
	private Label minLabel;
	private Label maxLabel;

	public SliderPanelW(double min, double max) {
		minLabel = new Label(String.valueOf(min));
		add(minLabel);
		slider = new SliderWJquery(min, max);
		add(slider);
		maxLabel = new Label(String.valueOf(max));
		add(maxLabel);
		setStyleName("optionsSlider");
	}

	public Double getValue() {
		return slider.getValue();
	}

	public void setMinimum(double min) {
		slider.setMinimum(min);
		minLabel.setText(String.valueOf(min));
	}

	public void setMaximum(double max) {
		slider.setMaximum(max);
		maxLabel.setText(String.valueOf(max));
	}

	public void setStep(double step) {
		slider.setStep(step);
	}

	public GDimensionW getPreferredSize() {
		return new GDimensionW(180, 10);
	}

	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Double> handler) {
		return slider.addValueChangeHandler(handler);
	}

	public void setValue(Double value) {
		slider.setValue(value, false);
	}

	public void setValue(Double value, boolean fireEvents) {
		slider.setValue(value, fireEvents);
	}

	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return slider.addChangeHandler(handler);
	}

	public void setWidth(double width) {
		double w = width - minLabel.getOffsetWidth()
				- maxLabel.getOffsetWidth();
		slider.asWidget().getElement().getStyle().setWidth(w, Unit.PX);
	}

	public void updateColor(GColor color) {
		GColorW c = new GColorW(color.getRGB());
		c.setAlpha(102);
		// String sColor = GColor.getColorString(color);
		// minLabel.getElement().getStyle().setColor(sColor);
		// maxLabel.getElement().getStyle().setColor(sColor);
		// slider.asWidget().getElement().getStyle()
		// .setBackgroundColor(GColor.getColorString(c));
		Style uiStyle = Dom.querySelectorForElement(getElement(), "ui-slider")
				.getStyle();
		uiStyle.setBackgroundColor(GColor.getColorString(c));
		setUIStyle(getElement(), color);

	}

	private void setUIStyle(Element elem, GColor color) {
		GColorW c = new GColorW(color.getRGB());
		c.setAlpha(102);
		Style style = Dom.querySelectorForElement(elem,
				"ui-state-default").getStyle();
		style.setBackgroundColor(GColor.getColorString(color));

		style.setBorderColor(GColor.getColorString(c));
	}
}
