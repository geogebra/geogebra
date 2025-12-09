/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.discrete;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.discrete.delaunay.DelaunayTriangulation;
import org.geogebra.common.kernel.discrete.delaunay.PointDt;
import org.geogebra.common.kernel.discrete.delaunay.TriangleDt;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.DoubleUtil;

/**
 * Voronoi diagram
 */
public class AlgoVoronoi extends AlgoDiscrete {
	private static Comparator<GPoint2D> pointComparator;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            points
	 */
	public AlgoVoronoi(Construction cons, String label, GeoList inputList) {
		super(cons, label, inputList);
	}

	@Override
	public Commands getClassName() {
		return Commands.Voronoi;
	}

	@Override
	public final void compute() {

		size = inputList.size();
		if (!inputList.isDefined() || size == 0) {
			locus.setUndefined();
			return;
		}

		double[] inhom = new double[2];

		ArrayList<Double> xcoords = new ArrayList<>();
		ArrayList<Double> ycoords = new ArrayList<>();

		final double delta = 0.0000001;

		// add to TreeSet to remove duplicates (from touching triangles)
		TreeSet<GPoint2D> pointTree = new TreeSet<>(
				getPointComparator());

		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointND p = (GeoPointND) geo;
				p.getInhomCoords(inhom);

				pointTree.add(new GPoint2D(inhom[0], inhom[1]));

			}
		}

		PointDt[] points = new PointDt[pointTree.size()];
		int pointIndex = 0;

		Iterator<GPoint2D> it3 = pointTree.iterator();

		while (it3.hasNext()) {
			GPoint2D p = it3.next();
			double x = p.getX();
			double y = p.getY();

			while (xcoords.contains(x)) {
				x += delta;
			}
			while (ycoords.contains(y)) {
				y += delta;
			}

			// work around a bug in the algorithm for Points with an equal x or
			// y coordinate
			xcoords.add(x);
			ycoords.add(y);

			points[pointIndex++] = new PointDt(x, y);

		}

		DelaunayTriangulation dt = new DelaunayTriangulation(points);

		if (dt.allCollinear) {
			locus.setUndefined();
			return;
		}

		Iterator<TriangleDt> it = dt.trianglesIterator();

		if (al == null) {
			al = new ArrayList<>();
		} else {
			al.clear();
		}

		// add to TreeSet to remove duplicates (from touching triangles)
		TreeSet<MyLine> tree = new TreeSet<>(
				AlgoDelauneyTriangulation.getComparator());

		while (it.hasNext()) {
			TriangleDt triangle = it.next();

			for (int index = 0; index < 3; index++) {

				PointDt corner = triangle.getCorner(index);

				if (corner != null) {

					PointDt[] voronoiCell = dt.calcVoronoiCell(triangle,
							corner);

					if (voronoiCell != null) {
						for (int i = 0; i < voronoiCell.length - 1; i++) {
							tree.add(new MyLine(
									new GPoint2D(voronoiCell[i].x(),
											voronoiCell[i].y()),
									new GPoint2D(
											voronoiCell[(i + 1)
													% voronoiCell.length].x(),
											voronoiCell[(i + 1)
													% voronoiCell.length]
															.y())));

						}
					}
				}
			}

		}

		Iterator<MyLine> it2 = tree.iterator();

		while (it2.hasNext()) {
			MyLine line = it2.next();
			al.add(new MyPoint(line.p1.getX(), line.p1.getY(),
					SegmentType.MOVE_TO));
			al.add(new MyPoint(line.p2.getX(), line.p2.getY(),
					SegmentType.LINE_TO));
		}

		locus.setPoints(al);
		locus.setDefined(true);

	}

	/**
	 * @return comparator used to eliminate duplicate objects (TreeSet deletes
	 *         duplicates ie those that return 0)
	 */
	public static Comparator<GPoint2D> getPointComparator() {
		if (pointComparator == null) {
			pointComparator = (p1, p2) -> {

				// double p1A = itemA.getX();
				// double p1B = itemA.getY();
				// double p2A = itemB.getX();
				// double p2B = itemB.getY();

				// return 0 if endpoints the same
				// so no duplicates in the TreeMap
				if (DoubleUtil.isEqual(p1.getX(), p2.getX())
						&& DoubleUtil.isEqual(p1.getY(), p2.getY())) {
					return 0;
				}

				// need to return something sensible, otherwise tree doesn't
				// work
				return p1.getX() > p2.getX() ? -1 : 1;

			};

		}

		return pointComparator;
	}
}
