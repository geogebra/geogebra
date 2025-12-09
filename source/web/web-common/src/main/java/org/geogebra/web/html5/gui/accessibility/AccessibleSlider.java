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

package org.geogebra.web.html5.gui.accessibility;

import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.util.sliderPanel.SliderW;

/**
 * Accessibility adapter for sliders
 */
public class AccessibleSlider implements AccessibleWidget, HasSliders {

	private SliderW slider;
	private AccessibilityView view;
	private GeoNumeric numeric;

	/**
	 * @param geo
	 *            numeric
	 * @param widgetFactory
	 *            factory for sliders
	 * @param view
	 *            accessibility view
	 */
	public AccessibleSlider(GeoNumeric geo, BaseWidgetFactory widgetFactory,
			final AccessibilityView view) {
		this.numeric = geo;
		this.view = view;
		slider = WidgetFactory.makeSlider(0, this, widgetFactory);
		update();
	}

	@Override
	public List<SliderW> getWidgets() {
		return Collections.singletonList(slider);
	}

	@Override
	public void onValueChange(int index, double value) {
		view.select(numeric);

		double step = numeric.getAnimationStep();
		double intervalMin = numeric.getIntervalMin();
		double numericValue = value * step + intervalMin;
		numeric.setValue(numericValue);
		numeric.updateRepaint();
		slider.getElement().focus();
		updateValueText();
	}

	private void updateValueText() {
		slider.getElement().setAttribute("aria-valuetext", numeric.getAuralText());
	}

	@Override
	public void update() {
		updateNumericRange(slider);
	}

	private void updateNumericRange(SliderW range) {
		double intervalMin = numeric.getIntervalMin();
		double intervalMax = numeric.getIntervalMax();
		double step = numeric.getAnimationStep();
		double value = numeric.getValue();

		range.setMinimum(0);
		range.setMaximum(Math.round((intervalMax - intervalMin) / step));
		range.setStep(1);
		range.setValue((double) Math.round((value - intervalMin) / step));

		updateValueText();
	}

	@Override
	public void setFocus(boolean focus) {
		slider.setFocus(focus);
	}

	@Override
	public boolean isCompatible(GeoElement geo) {
		return geo instanceof GeoNumeric && ((GeoNumeric) geo).isSlider();
	}

}
