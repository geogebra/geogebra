package org.geogebra.common.kernel.discrete.tsp.method.tsp;

import java.util.List;

import org.geogebra.common.kernel.discrete.tsp.model.Node;


public class ImproveRoutine implements TspImprovement {
	TspImprovement[] algorithm;
	
	public ImproveRoutine(TspImprovement... algorithm) {
		this.algorithm = algorithm;
	}

	public boolean method(List<Node> route) {
		for (int i = 0; i < this.algorithm.length; i++) {
			if (this.algorithm[i].method(route)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (TspImprovement tsp : this.algorithm) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(" -> ");
			}
			sb.append(tsp);
		}
		return sb.toString();
	}

	public boolean method(int[] route, double[][] table) {
		throw new Error("Unimplemented method");
	}
}
