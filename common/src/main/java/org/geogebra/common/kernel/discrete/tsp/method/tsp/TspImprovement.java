package org.geogebra.common.kernel.discrete.tsp.method.tsp;

import java.util.List;

import org.geogebra.common.kernel.discrete.tsp.model.Node;


public interface TspImprovement {
	public boolean method(List<Node> route);
	public boolean method(int[] route, double[][] table);
}
