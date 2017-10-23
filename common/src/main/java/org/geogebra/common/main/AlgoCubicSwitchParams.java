package org.geogebra.common.main;

/**
 * Parameters for AlgoCubic
 */
public class AlgoCubicSwitchParams {
	/** index */
	public double n;
	/** side */
	public double a;
	/** side */
	public double b;
	/** side */
	public double c;

	/**
	 * @param pn
	 *            index
	 * @param pa
	 *            side length
	 * @param pb
	 *            side length
	 * @param pc
	 *            side length
	 */
	public AlgoCubicSwitchParams(double pn, double pa, double pb, double pc) {
		this.n = pn;
		this.a = pa;
		this.b = pb;
		this.c = pc;
	}
}
