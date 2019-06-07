package org.geogebra.web.html5.gui.accessibility;

import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.html5.util.sliderPanel.SliderW;
import org.geogebra.web.html5.util.sliderPanel.SliderWI;

/**
 * Accessibility adapter for sliders
 */
public class AccessibleNumeric implements AccessibleWidget, HasSliders {

	private SliderW slider;
	private AccessibilityView view;
	private GeoNumeric numeric;

	/**
	 * @param geo           numeric
	 * @param sliderFactory slider factory
	 * @param view          accessibility view
	 */
	public AccessibleNumeric(GeoNumeric geo, WidgetFactory sliderFactory,
			final AccessibilityView view) {
		this.numeric = geo;
		this.view = view;
		slider = sliderFactory.makeSlider(0, this);
		update();
	}

	@Override
	public List<SliderW> getWidgets() {
		return Collections.singletonList(slider);
	}

	private void updateNumericRange(SliderWI range) {
		range.setMinimum(numeric.getIntervalMin());
		range.setMaximum(numeric.getIntervalMax());
		range.setStep(numeric.getAnimationStep());
		range.setValue(numeric.getValue());
		updateValueText();
	}

	@Override
	public void onValueChange(int index, double value) {
		view.select(numeric);
		numeric.setValue(value);
		numeric.updateRepaint();
		slider.getElement().focus();
		updateValueText();
	}

	private void updateValueText() {
		slider.getElement().setAttribute("aria-valuetext",
				numeric.toValueString(StringTemplate.screenReader));
	}

	@Override
	public void update() {
		updateNumericRange(slider);
	}

	@Override
	public void setFocus(boolean focus) {
		slider.setFocus(focus);
	}

}
