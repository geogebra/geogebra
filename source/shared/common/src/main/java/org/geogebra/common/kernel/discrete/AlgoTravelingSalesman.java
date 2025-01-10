package org.geogebra.common.kernel.discrete;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.discrete.tsp.TSP;
import org.geogebra.common.kernel.discrete.tsp.TSPSolver;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Traveling Salesman with euclidian metric
 *
 */
public class AlgoTravelingSalesman extends AlgoDiscrete {
	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            points
	 */
	public AlgoTravelingSalesman(Construction cons, String label,
			GeoList inputList) {
		super(cons, label, inputList);
	}

	@Override
	public Commands getClassName() {
		return Commands.TravelingSalesman;
	}

	@Override
	public void compute() {

		size = inputList.size();
		if (!inputList.isDefined() || size < 3) {
			locus.setUndefined();
			return;
		}

		double[] inhom = new double[2];

		// Opt3 opt3 = new Opt3();
		// final BranchBound construction = new BranchBound(500, opt3);

		MyPoint[] nodes = new MyPoint[size];

		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointND p = (GeoPointND) geo;
				p.getInhomCoords(inhom);
				nodes[i] = new MyPoint(inhom[0], inhom[1]);
				// Log.error(i + " " + nodes[i].toString());
			}
		}

		final TSP tsp = new TSPSolver();

		tsp.solve(nodes);

		if (al == null) {
			al = new ArrayList<>();
		} else {
			al.clear();
		}

		for (int i = 0; i < size; i++) {
			// Log.error(i + " " + nodes[i].toString());
			nodes[i].setLineTo(i != 0);
			al.add(nodes[i]);
		}

		// // join up
		MyPoint n = nodes[0];
		al.add(new MyPoint(n.getX(), n.getY(), SegmentType.LINE_TO));

		locus.setPoints(al);
		locus.setDefined(true);

	}

}
