package org.geogebra.common.kernel.discrete.tsp.method.tsp;

import java.util.List;

import org.geogebra.common.kernel.discrete.tsp.model.Node;


public class NoImprovement implements TspImprovement {
	public boolean method(List<Node> route) {
		return false;
	}
	public boolean method(int[] route, double[][] table) {
		return false;
	}

	@Override
	public String toString() {
		return "NoImprovement";
	}
}
