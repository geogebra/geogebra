package org.geogebra.common.kernel.discrete.tsp;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.discrete.tsp.impl.FLS;

public final class TSPSolver implements TSP {

	@Override
	public double solve(final MyPoint[] points) {
		return FLS.optimise(points);
	}
}
