package org.geogebra.common.kernel.discrete.tsp.method.tsp;

import java.util.List;

import org.geogebra.common.kernel.discrete.tsp.model.Node;


public class Opt3 implements TspImprovement {
	public boolean method(List<Node> route) {
		int length = route.size();
		for (int i = 1; i < length - 1; i++) {
			Node s1 = route.get(i - 1);
			Node t1 = route.get(i);
			double d1 = s1.getDistance(t1);
			for (int j = i + 1; j < length; j++) {
				Node s2 = route.get(j - 1);
				Node t2 = route.get(j);
				double d2 = s2.getDistance(t2);
				for (int k = j + 1; k <= length; k++) {
					if ((k + 2) % length == j || (j + 2) % length == i || i + 2 == k) {
						continue;
					}
					Node s3 = route.get(k - 1);
					Node t3 = route.get(k % length);
					double before = d1 + d2 + s3.getDistance(t3);
					double after = s1.getDistance(t2) + s3.getDistance(t1) + s2.getDistance(t3);
					if (before > after) {
						this.reverse(route, i, j - 1);
						this.reverse(route, j, k - 1);
						this.reverse(route, i, k - 1);
						return true;
					}
					after = s1.getDistance(t2) + s3.getDistance(s2) + t1.getDistance(t3);
					if (before > after) {
						this.reverse(route, j, k - 1);
						this.reverse(route, i, k - 1);
						return true;
					}
					after = s1.getDistance(s3) + t2.getDistance(t1) + s2.getDistance(t3);
					if (before > after) {
						this.reverse(route, i, j - 1);
						this.reverse(route, i, k - 1);
						return true;
					}
					after = s1.getDistance(s2) + t1.getDistance(s3) + t2.getDistance(t3);
					if (before > after) {
						this.reverse(route, i, j - 1);
						this.reverse(route, j, k - 1);
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean method(int[] route, double[][] table) {
		int length = route.length;
		for (int i = 1; i < length - 1; i++) {
			int s1 = route[i - 1];
			int t1 = route[i];
			double d1 = table[s1][t1];
			for (int j = i + 1; j < length; j++) {
				int s2 = route[j - 1];
				int t2 = route[j];
				double d2 = table[s2][t2];
				for (int k = j + 1; k <= length; k++) {
					if ((k + 2) % length == j || (j + 2) % length == i || i + 2 == k) {
						continue;
					}
					int s3 = route[k - 1];
					int t3 = route[k % length];
					double before = d1 + d2 + table[s3][t3];
					double after = table[s1][t2] + table[s3][t1] + table[s2][t3];
					if (before > after) {
						this.reverse(route, i, j - 1);
						this.reverse(route, j, k - 1);
						this.reverse(route, i, k - 1);
						return true;
					}
					after = table[s1][t2] + table[s3][s2] + table[t1][t3];
					if (before > after) {
						this.reverse(route, j, k - 1);
						this.reverse(route, i, k - 1);
						return true;
					}
					after = table[s1][s3] + table[t2][t1] + table[s2][t3];
					if (before > after) {
						this.reverse(route, i, j - 1);
						this.reverse(route, i, k - 1);
						return true;
					}
					after = table[s1][s2] + table[t1][s3] + table[t2][t3];
					if (before > after) {
						this.reverse(route, i, j - 1);
						this.reverse(route, j, k - 1);
						return true;
					}
				}
			}
		}
		return false;
	}

	public void reverse(int[] route, int s, int t) {
		int length = route.length;
		for (int i = (t - s) / 2; i >= 0; i--) {
			int tmp = route[(s + i) % length];
			route[(s + i) % length] = route[(t - i) % length];
			route[(t - i) % length] = tmp;
		}
	}
	
	public void reverse(List<Node> route, int s, int t) {
		int length = route.size();
		for (int i = (t - s) / 2; i >= 0; i--) {
			Node tmp = route.get((s + i) % length);
			route.set((s + i) % length, route.get((t - i) % length));
			route.set((t - i) % length, tmp);
		}
	}
	
	@Override
	public String toString() {
		return "3-Opt";
	}
}
