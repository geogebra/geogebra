package org.geogebra.common.main;

/**
 * Interface for the switch table for Cubic algo
 */
public interface AlgoCubicSwitchInterface {
	/**
	 * @param n
	 *            index
	 * @param a
	 *            side
	 * @param b
	 *            side
	 * @param c
	 *            side
	 * @return equation in A,B,C
	 */
	public String getEquation(double n, double a, double b, double c);

	/**
	 * @param kw
	 *            index and sides bundled in an object
	 * @return equation in A,B,C
	 */
	public String getEquation(AlgoCubicSwitchParams kw);
}
