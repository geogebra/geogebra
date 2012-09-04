package geogebra.common.kernel.discrete;

import geogebra.common.awt.GPoint2D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.discrete.delaunay.Delaunay_Triangulation;
import geogebra.common.kernel.discrete.delaunay.Point_dt;
import geogebra.common.kernel.discrete.delaunay.Triangle_dt;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.kernelND.GeoPointND;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class AlgoVoronoi extends AlgoHull{

	public AlgoVoronoi(Construction cons, String label, GeoList inputList) {
		super(cons, label, inputList, null);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoVoronoi;
	}

	public final void compute() {

		size = inputList.size();
		if (!inputList.isDefined() ||  size == 0) {
			locus.setUndefined();
			return;
		} 

		double inhom[] = new double[2];

		Point_dt[] points = new Point_dt[size];



		for (int i = 0 ; i < size ; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointND p = (GeoPointND)geo;
				p.getInhomCoords(inhom);

				points[i] = new Point_dt(inhom[0], inhom[1]);

			}
		}

		Delaunay_Triangulation dt = new Delaunay_Triangulation(points);

		if (dt.allCollinear) {
			locus.setUndefined();
			return;
		}

		Iterator<Triangle_dt> it = dt.trianglesIterator();

		if (al == null) {
			al = new ArrayList<MyPoint>();
		} else {
			al.clear();
		}

		// add to TreeSet to remove duplicates (from touching triangles)
		TreeSet<MyLine> tree = new TreeSet<MyLine>(AlgoDelauneyTriangulation.getComparator());

		while (it.hasNext()) {
			Triangle_dt triangle = it.next();

			for (int index = 0 ; index < 3 ; index ++) {

				Point_dt corner = triangle.getCorner(index);

				if (corner != null) {

					Point_dt[] voronoiCell = dt.calcVoronoiCell(triangle, corner);

					if (voronoiCell != null)
						for (int i = 0 ; i < voronoiCell.length - 1 ; i++) {
							tree.add(new MyLine(new GPoint2D.Double(voronoiCell[i].x() , voronoiCell[i].y()), new GPoint2D.Double(voronoiCell[(i+1) % voronoiCell.length].x() , voronoiCell[(i+1) % voronoiCell.length].y())));

						}
				}
			}

		}


		Iterator<MyLine> it2 = tree.iterator();

		while (it2.hasNext()) {
			MyLine line = it2.next();
			al.add(new MyPoint(line.p1.getX() , line.p1.getY(), false));
			al.add(new MyPoint(line.p2.getX() , line.p2.getY(), true));
		}



		locus.setPoints(al);
		locus.setDefined(true);

	}




}
