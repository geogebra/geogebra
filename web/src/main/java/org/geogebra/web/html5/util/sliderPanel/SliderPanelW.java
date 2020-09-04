package org.geogebra.web.html5.util.sliderPanel;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.util.DoubleUtil;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel containing a slider.
 */
public class SliderPanelW extends FlowPanel implements HasChangeHandlers,
		HasValue<Double> {

	private SliderWI slider;
	private Label minLabel;
	private Label maxLabel;
	private Kernel kernel;
	private String[] parts = new String[2];
	private Double currentMin = null;
	private Double currentMax = null;
	private Double currentStep = null;

	/**
	 * @param min
	 *            slider min
	 * @param max
	 *            slider max
	 * @param kernel
	 *            kernel
	 * @param degrees
	 *            whether to use degrees
	 */
	public SliderPanelW(double min, double max, Kernel kernel, boolean degrees) {
		this.kernel = kernel;
		minLabel = new Label();
		add(minLabel);
		slider = new SliderW(min, max);
		add(slider);
		maxLabel = new Label();
		setMinimum(min, degrees);
		setMaximum(max, degrees);
		add(maxLabel);
		setStyleName("optionsSlider");
	}

	@Override
	public Double getValue() {
		return slider.getValue();
	}

	/**
	 * disable slider
	 * 
	 * @param disable
	 *            true if slider should be disabled
	 */
	public void disableSlider(boolean disable) {
		((SliderW) slider).disableSlider(disable);
	}

	/**
	 * @param min
	 *            slider minimum
	 * @param degrees
	 *            whether to use degrees
	 */
	public void setMinimum(double min, boolean degrees) {
		if (currentMin == null || !DoubleUtil.isEqual(currentMin, min)) {
			currentMin = min;
			slider.setMinimum(min);
			printParts(minLabel, min, degrees);
		}
	}

	private void printParts(Label label, double val, boolean degrees) {
		if (degrees) {
			label.setText(kernel.formatAngle(val,
					StringTemplate.defaultTemplate, true).toString());
			return;
		}
		parts = StringTemplate.printLimitedWidth(val, kernel, parts);
		if (parts[1] == null) {
			label.setText(parts[0]);
		} else {
			label.setText(parts[0] + "\u00D7 10");
			Element exponent = DOM.createElement("sup");
			exponent.setInnerText(parts[1]);
			label.getElement().appendChild(exponent);
		}

	}

	/**
	 * @param max
	 *            slider maximum
	 * @param degrees
	 *            whether to use degrees
	 */
	public void setMaximum(double max, boolean degrees) {
		if (currentMax == null || !DoubleUtil.isEqual(currentMax, max)) {
			currentMax = max;
			slider.setMaximum(max);
			printParts(maxLabel, max, degrees);
		}
	}

	/**
	 * @param step
	 *            slider step
	 */
	public void setStep(double step) {
		if (currentStep == null || !DoubleUtil.isEqual(currentStep, step)) {
			currentStep = step;
			slider.setStep(step);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Double> handler) {
		return slider.addValueChangeHandler(handler);
	}

	@Override
	public void setValue(Double value) {
		slider.setValue(value, false);
	}

	@Override
	public void setValue(Double value, boolean fireEvents) {
		slider.setValue(value, fireEvents);
	}

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return slider.addChangeHandler(handler);
	}

	/**
	 * Resize slider to fit the panel width.
	 * 
	 * @param width
	 *            panel width
	 */
	public void setWidth(double width) {
		double w = width - minLabel.getOffsetWidth()
				- maxLabel.getOffsetWidth();
		slider.asWidget().getElement().getStyle().setWidth(w, Unit.PX);
	}

	public Widget getSlider() {
		return slider.asWidget();
	}
}
