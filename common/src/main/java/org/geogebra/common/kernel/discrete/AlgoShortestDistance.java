package org.geogebra.common.kernel.discrete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections15.Transformer;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.GraphAlgo;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.discrete.AlgoMinimumSpanningTree.MyLink;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoPointND;

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
	private GeoLocus locus;
	private GeoBoolean weighted;
	private ArrayList<MyPoint> al;
	private int edgeCount = 0;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            set of vertices
	 * @param start
	 *            start point
	 * @param end
	 *            end point
	 * @param weighted
	 *            whether to use Euclidian length
	 */
	public AlgoShortestDistance(Construction cons, String label,
			GeoList inputList, GeoPointND start, GeoPointND end,
			GeoBoolean weighted) {
		super(cons);
		this.inputList = inputList;
		this.start = start;
		this.end = end;
		this.weighted = weighted;

		locus = new GeoLocus(cons);

		setInputOutput();
		compute();
		locus.setLabel(label);

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
	public GeoLocus getResult() {
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

		MyNode node1, node2, startNode = null, endNode = null;

		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoSegment()) {
				GeoSegment seg = (GeoSegment) geo;
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
				if (p1 == start) {
					startNode = node1;
				} else if (p1 == end) {
					endNode = node1;
				}

				if (p2 == start) {
					startNode = node2;
				} else if (p2 == end) {
					endNode = node2;
				}

				// add edge to graph
				g.addEdge(
						new MyLink(seg.getLength(), node1, node2, edgeCount++),
						node1,
						node2, EdgeType.UNDIRECTED);

			}
		}

		if (al == null) {
			al = new ArrayList<>();
		} else {
			al.clear();
		}

		if (startNode == null || endNode == null) {
			locus.setPoints(al);
			locus.setDefined(false);
			return;
		}

		DijkstraShortestPath<MyNode, MyLink> alg;

		if (weighted.getBoolean()) {
			alg = new DijkstraShortestPath<>(g, wtTransformer);
		} else {
			// Unweighted Shortest Path
			alg = new DijkstraShortestPath<>(g);
		}

		List<MyLink> list = alg.getPath(startNode, endNode);

		double[] inhom1 = new double[2];
		double[] inhom2 = new double[2];
		double[] inhomLast = new double[2];

		MyNode n1, n2;
		MyLink link = list.get(0);
		n1 = link.n1;
		n2 = link.n2;

		// nodes may not be in the right order, might need n1 or n2
		if (n1 == startNode) {
			n1.id.getInhomCoords(inhomLast);
		} else if (n2 == startNode) {
			n2.id.getInhomCoords(inhomLast);
		} else if (n1 == endNode) {
			n1.id.getInhomCoords(inhomLast);
		} else if (n2 == endNode) {
			n2.id.getInhomCoords(inhomLast);
		}

		MyPoint pt = new MyPoint(inhomLast[0], inhomLast[1],
				SegmentType.MOVE_TO);
		al.add(pt);

		for (int i = 0; i < list.size(); i++) {
			link = list.get(i);
			link.n1.id.getInhomCoords(inhom1);
			link.n2.id.getInhomCoords(inhom2);

			// nodes may not be in the right order, might need n1 or n2
			if (inhom1[1] == inhomLast[1] && inhom1[0] == inhomLast[0]) {
				pt = new MyPoint(inhom2[0], inhom2[1], SegmentType.LINE_TO);
				inhomLast[0] = inhom2[0];
				inhomLast[1] = inhom2[1];
			} else {
				pt = new MyPoint(inhom1[0], inhom1[1], SegmentType.LINE_TO);
				inhomLast[0] = inhom1[0];
				inhomLast[1] = inhom1[1];
			}

			al.add(pt);
		}

		locus.setPoints(al);
		locus.setDefined(true);
	}
}
