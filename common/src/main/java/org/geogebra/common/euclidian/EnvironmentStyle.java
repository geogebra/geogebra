package org.geogebra.common.euclidian;

/**
 * Dascribes the drawing environment
 */
public abstract class EnvironmentStyle {

	/**
	 * @return horizontal scale factor
	 */
	public double getScaleX() {
		return 1;
	}

	/**
	 * @return vertical scale factor
	 */
	public double getScaleY() {
		return 1;
	}

}
