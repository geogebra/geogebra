package org.geogebra.common.kernel.discrete.tsp.method.tsp;

import java.util.List;

import org.geogebra.common.kernel.discrete.tsp.model.Node;


public class OrOpt implements TspImprovement {	

	public boolean method(List<Node> route) {
		int length = route.size();
		for (int i = 1; i <= length; i++) {
			Node s1 = route.get(i - 1);
			Node t1 = route.get(i % length);
			for (int j = i; j <= i + 2; j++) {
				Node s2 = route.get(j % length);
				Node t2 = route.get((j + 1) % length);
				for (int k = j + 1; k < i + length - 2; k++) {
					Node s3 = route.get(k % length);
					Node t3 = route.get((k + 1) % length);
					double before = s1.getDistance(t1) + s2.getDistance(t2) + s3.getDistance(t3);
					double after = s1.getDistance(t2) + t1.getDistance(t3) + s2.getDistance(s3);
					if (before > after) {
						this.reverse(route, j + 1, k);
						this.reverse(route, i, k);
						return true;
					}
					if (j != i) {
						after = s1.getDistance(t2) + t1.getDistance(s3) + s2.getDistance(t3);
						if (before > after) {
							this.reverse(route, i, j);
							this.reverse(route, j + 1, k);
							this.reverse(route, i, k);
							return true;
						}
						// TODO
//						if (j + 1 != k) {
//							after = s1.getDistance(s3) + t2.getDistance(t1) + s2.getDistance(t3);
//							if (before > after) {
//								List<Node> list = new ArrayList<Node>();
//								for (int l = k; l >= j + 1; l--) {
//									list.add(route.get(l % length));
//								}
////								for (int l = j + 1; l <= k ; l++) {
////									list.add(route.get(k + j + 1 - l));
////								}
//								for (int l = i; l < j + 1; l++) {
//									list.add(route.get(l % length));
//								}
//								for (int l = 0; i < list.size(); i++) {
//									route.set((l + i) % length, list.get(l % length));
//								}
//								System.out.println("Or-Opt");
//								return true;
//							}
//						}
					}
				}
			}
		}
		return false;
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
		return "Or-Opt";
	}

	public boolean method(int[] route, double[][] table) {
		// TODO 
		return false;
	}
}
