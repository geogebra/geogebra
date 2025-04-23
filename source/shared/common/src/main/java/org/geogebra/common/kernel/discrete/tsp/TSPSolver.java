package org.geogebra.common.kernel.discrete.tsp;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.discrete.tsp.impl.FLS;

/**
 * Travelling salesman problem solver.
 */
public final class TSPSolver {

	/**
	 * @param points points
	 * @return minimal distance
	 */
	public double solve(final MyPoint[] points) {
		return FLS.optimise(points);
	}
}
