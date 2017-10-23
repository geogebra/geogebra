package org.geogebra.common.main;

/**
 * Interface for the switch in Kimberling algo
 */
public interface AlgoKimberlingWeightsInterface {
	/**
	 * @param k
	 *            index
	 * @param a
	 *            side
	 * @param b
	 *            side
	 * @param c
	 *            side
	 * @return weight of first point
	 */
	public double weight(int k, double a, double b, double c);

	/**
	 * @param kw
	 *            index and sides
	 * @return weight of first point
	 */
	public double weight(AlgoKimberlingWeightsParams kw);
}
