package geogebra.common.kernel.discrete;

//import edu.uci.ics.jung.algorithms.shortestpath.PrimMinimumSpanningTree;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.discrete.alds.al.graphs.PrimMinimumSpanningTree;
import geogebra.common.kernel.discrete.alds.ds.graphs.Graph.Type;
import geogebra.common.kernel.discrete.alds.ds.graphs.Vertex;
import geogebra.common.kernel.discrete.alds.ds.graphs.WeightedGraph;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.kernelND.GeoPointND;

import java.util.ArrayList;
import java.util.Map;

public class AlgoMinimumSpanningTree extends AlgoHull {

	public AlgoMinimumSpanningTree(Construction cons, String label, GeoList inputList) {
		super(cons, label, inputList, null);
	}

	public Commands getClassName() {
		return Commands.MinimumSpanningTree;
	}

	public final void compute() {

		size = inputList.size();
		if (!inputList.isDefined() ||  size == 0) {
			locus.setUndefined();
			return;
		} 

		double inhom[] = new double[2];

		WeightedGraph weightedGraph = new WeightedGraph(Type.UNDIRECTED);

		Vertex[] vertices = new Vertex[size];

		for (int i = 0 ; i < size ; i++) {
			// name each vertex with arbitrary number
			vertices[i] = new Vertex(i+"", (GeoPointND) inputList.get(i));
			//weightedGraph.addVertex(vertices[i]);
		}
		
		double maxDistance = 0;

		// find maximum distance between points
		for (int i = 0 ; i < size - 1; i++) {
			GeoPointND p1 = (GeoPointND) inputList.get(i);
			for (int j = i + 1 ; j < size ; j++) {
				GeoPointND p2 = (GeoPointND) inputList.get(j);
				maxDistance = Math.max(maxDistance, p1.distance(p2));
			}

		}
		
		// algorithm uses int, so we need to multiply up by a large factor to distinguish small differences, eg 2.567, 2.568
		double max = Integer.MAX_VALUE;
		double FACTOR = max / maxDistance;

		for (int i = 0 ; i < size - 1; i++) {
			GeoPointND p1 = (GeoPointND) inputList.get(i);
			for (int j = i + 1 ; j < size ; j++) {
				GeoPointND p2 = (GeoPointND) inputList.get(j);
				weightedGraph.addEdge(vertices[i], vertices[j], (int) (FACTOR * p1.distance(p2)));
			}

		}


		PrimMinimumSpanningTree minimumSpanningTree = new PrimMinimumSpanningTree(weightedGraph, vertices[0]);
		Map<Vertex, Vertex> predecessor = minimumSpanningTree.compute().getPredecessorMap();
		Coords coords;

		if (al == null) al = new ArrayList<MyPoint>();
		else al.clear();

		for (int i = 0 ; i < size ; i++) {
			Vertex connectedVertex = predecessor.get(vertices[i]);
			if (connectedVertex != null) {
				GeoPointND point1 = vertices[i].getPoint();
				GeoPointND point2 = connectedVertex.getPoint();
				coords = point1.getInhomCoordsInD(2);
				al.add(new MyPoint(coords.get(1) , coords.get(2), false));
				coords = point2.getInhomCoordsInD(2);
				al.add(new MyPoint(coords.get(1) , coords.get(2), true));
			}

		}

		locus.setPoints(al);
		locus.setDefined(true);

	}


}
