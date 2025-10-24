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
import org.geogebra.common.util.debug.Log;

/**
 * DelauneyTriangulation command
 */
public class AlgoDelauneyTriangulation extends AlgoDiscrete {
	private static Comparator<MyLine> lineComparator;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            point list
	 */
	public AlgoDelauneyTriangulation(Construction cons, String label,
			GeoList inputList) {
		super(cons, label, inputList);
	}

	@Override
	public Commands getClassName() {
		return Commands.DelauneyTriangulation;
	}

	@Override
	public final void compute() {
		try {
			size = inputList.size();
			if (!inputList.isDefined() || size == 0) {
				locus.setUndefined();
				return;
			}

			double[] inhom = new double[2];

			PointDt[] points = new PointDt[size];

			for (int i = 0; i < size; i++) {
				GeoElement geo = inputList.get(i);
				if (geo.isDefined() && geo.isGeoPoint()) {
					GeoPointND p = (GeoPointND) geo;
					p.getInhomCoords(inhom);

					points[i] = new PointDt(inhom[0], inhom[1]);

				}
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
			TreeSet<MyLine> tree = new TreeSet<>(getComparator());

			while (it.hasNext()) {
				TriangleDt triangle = it.next();

				tree.add(new MyLine(
						new GPoint2D(triangle.p1().x(),
								triangle.p1().y()),
						new GPoint2D(triangle.p2().x(),
								triangle.p2().y())));
				if (triangle.p3() != null) {
					tree.add(new MyLine(
							new GPoint2D(triangle.p2().x(),
									triangle.p2().y()),
							new GPoint2D(triangle.p3().x(),
									triangle.p3().y())));
					tree.add(new MyLine(
							new GPoint2D(triangle.p3().x(),
									triangle.p3().y()),
							new GPoint2D(triangle.p1().x(),
									triangle.p1().y())));
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
		} catch (Exception e) {
			Log.error(e.getMessage());
			locus.setUndefined();
		}
	}

	/**
	 * @return comparator used to eliminate duplicate objects (TreeSet deletes
	 *         duplicates ie those that return 0)
	 */
	public static Comparator<MyLine> getComparator() {
		if (lineComparator == null) {
			lineComparator = (itemA, itemB) -> {

				GPoint2D p1A = itemA.p1;
				GPoint2D p2A = itemA.p2;
				GPoint2D p1B = itemB.p1;
				GPoint2D p2B = itemB.p2;

				// return 0 if endpoints the same
				// so no duplicates in the TreeMap
				if (DoubleUtil.isEqual(p1A.getX(), p2B.getX())
						&& DoubleUtil.isEqual(p1A.getY(), p2B.getY())
						&& DoubleUtil.isEqual(p2A.getX(), p1B.getX())
						&& DoubleUtil.isEqual(p2A.getY(), p1B.getY())) {
					return 0;
				}
				// check this one second (doesn't occur in practice)
				if (DoubleUtil.isEqual(p1A.getX(), p1B.getX())
						&& DoubleUtil.isEqual(p1A.getY(), p1B.getY())
						&& DoubleUtil.isEqual(p2A.getX(), p2B.getX())
						&& DoubleUtil.isEqual(p2A.getY(), p2B.getY())) {
					return 0;
				}

				// need to return something sensible, otherwise tree doesn't
				// work
				return itemA.lengthSquared() > itemB.lengthSquared() ? -1
						: 1;

			};

		}

		return lineComparator;
	}

}
