package org.geogebra.common.euclidian;

/**
 * Dascribes the drawing environment
 */
public abstract class EnvironmentStyle {

	/**
	 * @return horizontal scale factor
	 */
	public float getScaleX() {
		return 1;
	}

	/**
	 * @return vertical scale factor
	 */
	public float getScaleY() {
		return 1;
	}

}
