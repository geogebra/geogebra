package org.geogebra.common.kernel.discrete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.collections15.Transformer;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoPointND;

import edu.uci.ics.jung.algorithms.shortestpath.MinimumSpanningForest2;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class AlgoMinimumSpanningTree extends AlgoDiscrete {

	protected int edgeCount;

	private static Transformer<MyLink, Double> wtTransformer = new Transformer<MyLink, Double>() {
		public Double transform(MyLink link) {
			return link.weight;
		}
	};

	public AlgoMinimumSpanningTree(Construction cons, String label,
			GeoList inputList) {
		super(cons, label, inputList, null);
	}

	public Commands getClassName() {
		return Commands.MinimumSpanningTree;
	}

	public final void compute() {

		size = inputList.size();
		if (!inputList.isDefined() || size == 0) {
			locus.setUndefined();
			return;
		}

		edgeCount = 0;

		HashMap<GeoPointND, MyNode> nodes = new HashMap<GeoPointND, MyNode>();
		MyNode node1, node2;
		UndirectedSparseMultigraph<MyNode, MyLink> g = new UndirectedSparseMultigraph<MyNode, MyLink>();

		for (int i = 0; i < size - 1; i++) {
			GeoPointND p1 = (GeoPointND) inputList.get(i);
			for (int j = i + 1; j < size; j++) {
				GeoPointND p2 = (GeoPointND) inputList.get(j);

				node1 = nodes.get(p1);
				node2 = nodes.get(p2);
				if (node1 == null) {
					node1 = new MyNode(p1);
					nodes.put(p1, node1);
				}
				if (node2 == null) {
					node2 = new MyNode(p2);
					nodes.put(p2, node2);
				}

				g.addEdge(new MyLink(p1.distance(p2), 1, node1, node2), node1,
						node2, EdgeType.UNDIRECTED);

			}

			MinimumSpanningForest2<MyNode, MyLink> prim = new MinimumSpanningForest2<MyNode, MyLink>(
					g, new DelegateForest<MyNode, MyLink>(),
					DelegateTree.<MyNode, MyLink> getFactory(), wtTransformer);

			Forest<MyNode, MyLink> tree = prim.getForest();

			Iterator<MyLink> it = tree.getEdges().iterator();

			if (al == null) {
				al = new ArrayList<MyPoint>();
			} else {
				al.clear();
			}

			while (it.hasNext()) {
				MyLink edge = it.next();

				Coords coords = edge.n1.id.getInhomCoordsInD2();
				al.add(new MyPoint(coords.get(1), coords.get(2), false));
				coords = edge.n2.id.getInhomCoordsInD2();
				al.add(new MyPoint(coords.get(1), coords.get(2), true));

			}

			locus.setPoints(al);
			locus.setDefined(true);

		}

	}

	class MyLink {
		protected MyNode n1, n2;
		double capacity;
		double weight;
		int id;

		public MyLink(double weight, double capacity, MyNode n1, MyNode n2) {
			this.id = edgeCount++; // This is defined in the outer class.
			this.weight = weight;
			this.capacity = capacity;
			this.n1 = n1;
			this.n2 = n2;
		}

		public String toString() { // Always good for debugging
			return "Edge" + id;
		}
	}
}
