package org.geogebra.common.cas;

/**
 * Platform (Java / GWT) independent interface for MPReduce interpreter
 *
 */
public interface Evaluate {

	/**
	 * @param exp
	 *            MPReduce input
	 * @param timeoutMilliseconds
	 *            maximal time for computation in ms
	 * @return CAS output
	 * @throws Throwable
	 *             if computation fails or takes too long
	 */
	public String evaluate(String exp, long timeoutMilliseconds)
			throws Throwable;


}
