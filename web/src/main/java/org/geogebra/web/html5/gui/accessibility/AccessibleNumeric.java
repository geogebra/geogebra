package org.geogebra.web.html5.gui.accessibility;

import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.html5.util.sliderPanel.SliderW;

public class AccessibleNumeric implements AccessibleWidget, HasSliders {

	private SliderW slider;
	private AccessibilityView view;
	private GeoNumeric numeric;

	/**
	 * @param geo           numeric
	 * @param sliderFactory slider factory
	 * @param view          accessibility view
	 */
	public AccessibleNumeric(GeoNumeric geo, SliderFactory sliderFactory,
			final AccessibilityView view) {
		this.numeric = geo;
		this.view = view;
		slider = sliderFactory.makeSlider(0, this);
		update();
	}

	@Override
	public List<SliderW> getControl() {
		return Collections.singletonList(slider);
	}

	private void updateNumericRange(SliderW range) {
		range.setMinimum(numeric.getIntervalMin());
		range.setMaximum(numeric.getIntervalMax());
		range.setStep(numeric.getAnimationStep());
		range.setValue(numeric.getValue());
		updateValueText(range, numeric);
	}

	@Override
	public void onValueChange(int index, double value) {
		view.select(numeric);
		numeric.setValue(value);
		numeric.updateRepaint();
		slider.getElement().focus();
		updateValueText(slider, numeric);
	}

	/**
	 * @param range slider
	 * @param sel   selected number
	 */
	protected void updateValueText(SliderW range, GeoNumeric sel) {
		range.getElement().setAttribute("aria-valuetext", sel.toValueString(StringTemplate.screenReader));
	}

	@Override
	public void update() {
		updateNumericRange(slider);
	}

}
