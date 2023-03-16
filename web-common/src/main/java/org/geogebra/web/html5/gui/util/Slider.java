package org.geogebra.web.html5.gui.util;

public class Slider extends SliderAbstract<Integer> {

	/**
	 * Create a new slider.
	 * 
	 * @param min
	 *            slider min
	 * @param max
	 *            slider max
	 */
	public Slider(int min, int max) {
		super(min, max);
	}

	@Override
	protected Integer convert(String val) {
		return "".equals(val) ? 0 : Integer.parseInt(val); // empty string happens in Mockito
	}

}
