package org.geogebra.common.euclidian.plot;

/**
 * Info that can be pushed on stack using draw curve segments.
 *
 * @author Laszlo
 */
public class CurvePlotterStackItem {
	int dyadic;
	int depth;
	double[] eval;
	boolean onScreen;

	/**
	 *
	 * @param dyadic t of f(t)
	 * @param depth of the bisection.
	 * @param onScreen if the evaluated value on screen
	 * @param eval f(t)
	 */
	public void set(int dyadic, int depth, boolean onScreen, double[] eval) {
		this.dyadic = dyadic;
		this.depth = depth;
		this.onScreen = onScreen;
		this.eval = eval;
	}
}
