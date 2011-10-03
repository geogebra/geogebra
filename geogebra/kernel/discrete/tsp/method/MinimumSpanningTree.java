package geogebra.kernel.discrete.tsp.method;

import geogebra.kernel.discrete.tsp.gui.DemoPanel;
import geogebra.kernel.discrete.tsp.model.Node;
import geogebra.kernel.discrete.tsp.util.Heap;

import java.util.Comparator;

public class MinimumSpanningTree implements GraphDemonstration {
	public void method(DemoPanel panel) {
		final Node[] nodes = panel.getNodes().toArray(new Node[]{});
		boolean[][] edges = new boolean[nodes.length][nodes.length];
		if (nodes.length > 1) {
			Edge e;
			boolean[] close = new boolean[nodes.length];
			Heap<Edge> open = new Heap<Edge>(11, new Comparator<Edge>() {
				public int compare(Edge o1, Edge o2) {
					double diff = o1.cost - o2.cost;
					if (diff > 0) {
						return 1;
					} else if (diff < 0) {
						return -1;
					} else {
						return 0;
					}
				}
			});
			int index = 0;
			close[index] = true;
			do {
				for (int i = 0; i < nodes.length; i++) {
					if (i != index) {
						if (!close[i]) {
							open.add(new Edge(index, i, nodes[index].getDistance(nodes[i])));
						}
					}
				}
				e = open.poll();
				if (e == null) {
					break;
				}
				edges[e.s][e.t] = true;
				index = e.t;
				close[index] = true;
				panel.set(edges);
			} while (open.size() > 0);
			panel.set(edges);
		}
	}
	class Edge {
		int s;
		int t;
		double cost;
		public Edge(int s, int t, double cost) {
			this.s = s;
			this.t = t;
			this.cost = cost;
		}
		@Override
		public int hashCode() {
			return this.t;
		}
		@Override
		public boolean equals(Object obj) {
			return this.hashCode() == obj.hashCode();
		}
	}

	@Override
	public String toString() {
		return "minimum spanning tree";
	}
}