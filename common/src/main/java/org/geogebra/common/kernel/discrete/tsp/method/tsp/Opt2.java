package org.geogebra.common.kernel.discrete.tsp.method.tsp;

import java.util.List;

import org.geogebra.common.kernel.discrete.tsp.model.Node;

public class Opt2 implements TspImprovement {
	public boolean method(List<Node> route) {
		int length = route.size();
		for (int i = 1; i < length - 1; i++) {
			Node s1 = route.get(i - 1);
			Node t1 = route.get(i % length);
			double d1 = s1.getDistance(t1);
			for (int j = i + 2; j <= length; j++) {
				Node s2 = route.get(j - 1);
				Node t2 = route.get(j % length);
				double before = d1 + s2.getDistance(t2);
				double after = s1.getDistance(s2) + t1.getDistance(t2);
				if (before > after) {
					for (int k = 0; k < (j - i) / 2; k++) {
						Node tmp = route.get((k + i) % length);
						route.set((k + i) % length, route.get((j - k - 1) % length));
						route.set((j - k - 1) % length, tmp);
					}
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean method(int[] route, double[][] table) {
		int length = route.length;
		for (int i = 1; i < length - 1; i++) {
			int s1 = route[i - 1];
			int t1 = route[i % length];
			double d1 = table[s1][t1];
			for (int j = i + 2; j <= length; j++) {
				int s2 = route[j - 1];
				int t2 = route[j % length];
				double before = d1 + table[s2][t2];
				double after = table[s1][s2] + table[t1][t2];
				if (before > after) {
					for (int k = 0; k < (j - i) / 2; k++) {
						int tmp = route[(k + i) % length];
						route[(k + i) % length] = route[(j - k - 1) % length];
						route[(j - k - 1) % length] = tmp;
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "2-Opt";
	}
}
