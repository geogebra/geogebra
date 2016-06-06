package org.geogebra.common.kernel.discrete;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.discrete.tsp.TSP;
import org.geogebra.common.kernel.discrete.tsp.TSPSolver;
import org.geogebra.common.kernel.discrete.tsp.impl.Point;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoTravelingSalesman extends AlgoDiscrete {

	public AlgoTravelingSalesman(Construction cons, String label,
			GeoList inputList) {
		super(cons, label, inputList, null);
	}

	public Commands getClassName() {
		return Commands.TravelingSalesman;
	}

	public void compute() {

		size = inputList.size();
		if (!inputList.isDefined() || size < 3) {
			locus.setUndefined();
			return;
		}

		double inhom[] = new double[2];

		// Opt3 opt3 = new Opt3();
		// final BranchBound construction = new BranchBound(500, opt3);

		Point[] nodes = new Point[size];

		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointND p = (GeoPointND) geo;
				p.getInhomCoords(inhom);
				nodes[i] = new Point(inhom[0], inhom[1]);
			}
		}

		final TSP tsp = new TSPSolver();

		tsp.solve(nodes);

		if (al == null) {
			al = new ArrayList<MyPoint>();
		} else {
			al.clear();
		}

		for (int i = 0; i < size; i++) {
			Point n = nodes[i];
			al.add(new MyPoint(n.getX(), n.getY(), i != 0));
		}

		// // join up
		Point n = nodes[0];
		al.add(new MyPoint(n.getX(), n.getY(), true));

		locus.setPoints(al);
		locus.setDefined(true);

	}

}
