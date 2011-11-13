package geogebra.kernel.discrete.tsp.method.tsp;

import geogebra.kernel.discrete.tsp.gui.DemoPanel;
import geogebra.kernel.discrete.tsp.method.GraphDemonstration;
import geogebra.kernel.discrete.tsp.model.Node;
import geogebra.kernel.discrete.tsp.util.Heap;

public class OneTree implements GraphDemonstration {
	public void method(DemoPanel panel) {
		final Node[] nodes = panel.getNodes().toArray(new Node[]{});
		boolean[][] edges = new boolean[nodes.length][nodes.length];
		if (nodes.length > 1) {
			Edge e;
			boolean[] close = new boolean[nodes.length];
			Heap<Edge> open = new Heap<Edge>();
			int s = (int) (Math.random() * nodes.length);
			close[s] = true;
			int index = (s + 1) % nodes.length;
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
				if (!close[e.t]) {
					edges[e.s][e.t] = true;
					index = e.t;
					close[index] = true;
					panel.set(edges);
				}
			} while (open.size() > 0);
			open.clear();
			for (int i = 0; i < nodes.length; i++) {
				if (s != i) {
					open.add(new Edge(s, i, nodes[s].getDistance(nodes[i])));
				}
			}
			e = open.poll();
			edges[e.s][e.t] = true;
			e = open.poll();
			edges[e.s][e.t] = true;
			panel.set(edges);
		}
	}

	/**
	 * ä¸€æ™‚çš„ã�«è¾ºã‚’è¡¨ç�¾ã�™ã‚‹ã�Ÿã‚�ã�®ã‚¯ãƒ©ã‚¹
	 * @author masayasu
	 */
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
			if (obj instanceof Edge) {
				Edge edge = (Edge) obj;
				return edge.s == this.s && edge.t == this.t;
			}
			return false;
		}
	}

	@Override
	public String toString() {
		return "1-tree";
	}
}