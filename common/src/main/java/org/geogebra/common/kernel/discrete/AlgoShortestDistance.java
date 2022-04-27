package org.geogebra.common.kernel.discrete;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections15.Transformer;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.GraphAlgo;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.discrete.AlgoMinimumSpanningTree.MyLink;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocusND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Shortest path in graph
 */
public class AlgoShortestDistance extends AlgoElement implements GraphAlgo {
	private GeoPointND start;
	private GeoPointND end;
	private GeoList inputList;
	private GeoLocusND<? extends MyPoint> locus;
	private GeoBoolean weighted;
	private int edgeCount = 0;

	/**
	 * @param cons
	 *            construction
	 * @param inputList
	 *            set of vertices
	 * @param start
	 *            start point
	 * @param end
	 *            end point
	 * @param weighted
	 *            whether to use Euclidian length
	 */
	public AlgoShortestDistance(Construction cons, GeoList inputList,
			GeoPointND start, GeoPointND end, GeoBoolean weighted) {
		super(cons);
		this.inputList = inputList;
		this.start = start;
		this.end = end;
		this.weighted = weighted;
		int dimension = Math.max(end.getDimension(), start.getDimension());
		locus = kernel.getGeoFactory().newLocus(dimension, cons);

		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[4];
		input[0] = inputList;
		input[1] = start.toGeoElement();
		input[2] = end.toGeoElement();
		input[3] = weighted;

		setOnlyOutput(locus);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting locus (shortest path)
	 */
	public GeoLocusND<? extends MyPoint> getResult() {
		return locus;
	}

	@Override
	public Commands getClassName() {
		return Commands.ShortestDistance;
	}

	// weighted Shortest Path
	// use length of segments to weight
	private Transformer<MyLink, Double> wtTransformer = new Transformer<MyLink, Double>() {
		@Override
		public Double transform(MyLink link) {
			return link.weight;
		}
	};

	@Override
	public final void compute() {

		int size = inputList.size();
		if (!inputList.isDefined() || !weighted.isDefined() || size == 0) {
			locus.setUndefined();
			return;
		}

		edgeCount = 0;

		HashMap<GeoPointND, MyNode> nodes = new HashMap<>();

		SparseMultigraph<MyNode, MyLink> g = new SparseMultigraph<>();

		MyNode node1, node2;
		NodeMatcher startNode = new NodeMatcher(start);
		NodeMatcher endNode = new NodeMatcher(end);

		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoSegment()) {
				GeoSegmentND seg = (GeoSegmentND) geo;
				GeoPointND p1 = seg.getStartPoint();
				GeoPointND p2 = seg.getEndPoint();
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

				// take note of start and end points
				startNode.check(p1, node1);
				startNode.check(p2, node2);
				endNode.check(p1, node1);
				endNode.check(p2, node2);

				// add edge to graph
				g.addEdge(
						new MyLink(seg.getLength(), node1, node2, edgeCount++),
						node1,
						node2, EdgeType.UNDIRECTED);

			}
		}

		locus.clearPoints();
		if (startNode.node == null || endNode.node == null) {
			locus.setDefined(false);
			return;
		}
		double[] inhomLast = new double[3];
		if (startNode.node == endNode.node) {
			start.getInhomCoords(inhomLast);
			locus.insertPoint(inhomLast[0], inhomLast[1], inhomLast[2], false);
			locus.setDefined(true);
			return;
		}

		DijkstraShortestPath<MyNode, MyLink> alg;

		if (weighted.getBoolean()) {
			alg = new DijkstraShortestPath<>(g, wtTransformer);
		} else {
			// Unweighted Shortest Path
			alg = new DijkstraShortestPath<>(g);
		}

		List<MyLink> list = alg.getPath(startNode.node, endNode.node);

		MyNode n1, n2;
		if (!list.isEmpty()) {
			MyLink link = list.get(0);
			n1 = link.n1;
			n2 = link.n2;

			// nodes may not be in the right order, might need n1 or n2
			if (n1 == startNode.node || n1 == endNode.node) {
				n1.id.getInhomCoords(inhomLast);
			} else if (n2 == startNode.node || n2 == endNode.node) {
				n2.id.getInhomCoords(inhomLast);
			}
			locus.insertPoint(inhomLast[0], inhomLast[1], inhomLast[2],
					false);
		}
		double[] inhom1 = new double[3];
		double[] inhom2 = new double[3];
		for (MyLink link : list) {
			link.n1.id.getInhomCoords(inhom1);
			link.n2.id.getInhomCoords(inhom2);
			// nodes may not be in the right order, might need n1 or n2
			if (inhom1[1] == inhomLast[1] && inhom1[0] == inhomLast[0]) {
				System.arraycopy(inhom2, 0, inhomLast, 0, 3);
			} else {
				System.arraycopy(inhom1, 0, inhomLast, 0, 3);
			}

			locus.insertPoint(inhomLast[0], inhomLast[1], inhomLast[2], true);
		}

		locus.setDefined(true);
	}

	private static class NodeMatcher {
		private final GeoPointND target;
		public MyNode node;
		private boolean exactMatch;

		public NodeMatcher(GeoPointND target) {
			this.target = target;
		}

		public void check(GeoPointND p1, MyNode node2) {
			if (p1 == target) {
				exactMatch = true;
				node = node2;
			} else if (!exactMatch && target.isEqual(p1)) {
				node = node2;
			}
		}
	}
}
