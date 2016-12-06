package org.geogebra.common.kernel.discrete.tsp;

import org.geogebra.common.kernel.discrete.tsp.impl.Point;

public interface TSP {
	double solve(Point[] points);
}
