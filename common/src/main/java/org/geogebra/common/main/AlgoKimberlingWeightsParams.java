package org.geogebra.common.main;

/**
 * Parameters for the Kimberling switch
 */
public class AlgoKimberlingWeightsParams {
	/** index */
	public int k;
	/** side */
	public double a;
	/** side */
	public double b;
	/** side */
	public double c;

	/**
	 * @param pk
	 *            index
	 * @param pa
	 *            side length
	 * @param pb
	 *            side length
	 * @param pc
	 *            side length
	 */
	public AlgoKimberlingWeightsParams(int pk, double pa, double pb,
			double pc) {
		this.k = pk;
		this.a = pa;
		this.b = pb;
		this.c = pc;
	}
}
