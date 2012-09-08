package geogebra.common.kernel.discrete;

import geogebra.common.awt.GPoint2D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.discrete.delaunay.Delaunay_Triangulation;
import geogebra.common.kernel.discrete.delaunay.Point_dt;
import geogebra.common.kernel.discrete.delaunay.Triangle_dt;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.Comparator;
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

		
		ArrayList<Double> xcoords = new ArrayList<Double>();
		ArrayList<Double> ycoords = new ArrayList<Double>();
		
		final double delta =  0.0000001;
		
		// add to TreeSet to remove duplicates (from touching triangles)
		TreeSet<GPoint2D> pointTree = new TreeSet<GPoint2D>(getPointComparator());

		for (int i = 0 ; i < size ; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointND p = (GeoPointND)geo;
				p.getInhomCoords(inhom);
				
				pointTree.add(new GPoint2D.Double(inhom[0],  inhom[1]));
				
			}
		}
		
		Point_dt[] points = new Point_dt[pointTree.size()];
		int indx = 0;

		
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
			
			// work around a bug in the algorithm for Points with an equal x or y coordinate 
			xcoords.add(x);
			ycoords.add(y);

			points[indx++] = new Point_dt(x, y);

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

    /*
     * comparator used to eliminate duplicate objects
     * (TreeSet deletes duplicates ie those that return 0)
     */
	public static Comparator<GPoint2D> getPointComparator() {
		if (pointComparator == null) {
			pointComparator = new Comparator<GPoint2D>() {
				public int compare(GPoint2D p1, GPoint2D p2) {
		        
					//double p1A = itemA.getX();
					//double p1B = itemA.getY();
					//double p2A = itemB.getX();
					//double p2B = itemB.getY();
					
					// return 0 if endpoints the same
					// so no duplicates in the TreeMap
					if (Kernel.isEqual(p1.getX(), p2.getX()) && Kernel.isEqual(p1.getY(), p2.getY())) {
						//Application.debug("equal2");
						return 0;
					}
			
					// need to return something sensible, otherwise tree doesn't work
					return p1.getX() > p2.getX() ? -1 : 1;
					
				
				}
			};
			
			}
		
			return pointComparator;
		}
	  private static Comparator<GPoint2D> pointComparator;



}
