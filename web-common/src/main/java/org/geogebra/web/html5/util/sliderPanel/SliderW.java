package org.geogebra.web.html5.util.sliderPanel;

import org.geogebra.web.html5.gui.util.SliderAbstract;

/**
 * Slider based on default DOM range input.
 *
 */
public class SliderW extends SliderAbstract<Double> {

	/**
	 * @param min
	 *            slider min
	 * @param max
	 *            slider max
	 */
	public SliderW(double min, double max) {
		super(min, max);
	}

	@Override
	protected Double convert(String val) {
		return Double.valueOf(val);
	}
}