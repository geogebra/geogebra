package org.geogebra.common.kernel.discrete.tsp;

import org.geogebra.common.kernel.discrete.tsp.impl.FLS;
import org.geogebra.common.kernel.discrete.tsp.impl.Point;

public final class TSPSolver implements TSP {
	@Override
	public double solve(final Point[] points) {
		return FLS.optimise(points);
	}
}
