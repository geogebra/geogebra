package geogebra.common.kernel.discrete;

import geogebra.common.awt.GPoint2D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.discrete.delaunay.Delaunay_Triangulation;
import geogebra.common.kernel.discrete.delaunay.Point_dt;
import geogebra.common.kernel.discrete.delaunay.Triangle_dt;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.simpletriangulation.VTriangle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.kernelND.GeoPointND;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class AlgoDelauneyTriangulation extends AlgoHull{

	public AlgoDelauneyTriangulation(Construction cons, String label, GeoList inputList) {
		super(cons, label, inputList, null);
	}
	
	@Override
    public Algos getClassName() {
        return Algos.AlgoDelauneyTriangulation;
    }
    
    public final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size == 0) {
    		locus.setUndefined();
    		return;
    	} 
    	
        //if (vl == null) vl = new ArrayList<VPoint>();
        //else vl.clear();
   	
		double inhom[] = new double[2];
		
		Point_dt[] points = new Point_dt[size];
   	
        

        for (int i = 0 ; i < size ; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointND p = (GeoPointND)geo;
				p.getInhomCoords(inhom);
				
				points[i] = new Point_dt(inhom[0], inhom[1]);
				
				//vl.add( representation.createPoint(inhom[0], inhom[1]) );			
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
        TreeSet<MyLine> tree = new TreeSet<MyLine>(getComparator());
        
        while (it.hasNext()) {
        	Triangle_dt triangle = it.next();
        	
        	tree.add(new MyLine(new GPoint2D.Double(triangle.p1().x() , triangle.p1().y()), new GPoint2D.Double(triangle.p2().x() , triangle.p2().y())));
        	if (triangle.p3() != null) {
	        	tree.add(new MyLine(new GPoint2D.Double(triangle.p2().x() , triangle.p2().y()), new GPoint2D.Double(triangle.p3().x() , triangle.p3().y())));
	        	tree.add(new MyLine(new GPoint2D.Double(triangle.p3().x() , triangle.p3().y()), new GPoint2D.Double(triangle.p1().x() , triangle.p1().y())));
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
	public static Comparator<MyLine> getComparator() {
		if (lineComparator == null) {
			lineComparator = new Comparator<MyLine>() {
				public int compare(MyLine itemA, MyLine itemB) {
		        
					GPoint2D p1A = itemA.p1;
					GPoint2D p2A = itemA.p2;
					GPoint2D p1B = itemB.p1;
					GPoint2D p2B = itemB.p2;
					
					// return 0 if endpoints the same
					// so no duplicates in the TreeMap
					if (Kernel.isEqual(p1A.getX(), p2B.getX()) && Kernel.isEqual(p1A.getY(), p2B.getY()) && Kernel.isEqual(p2A.getX(), p1B.getX()) && Kernel.isEqual(p2A.getY(), p1B.getY())) {
						//Application.debug("equal2");
						return 0;
					}
					// check this one second (doesn't occur in practice)
					if (Kernel.isEqual(p1A.getX(), p1B.getX()) && Kernel.isEqual(p1A.getY(), p1B.getY()) && Kernel.isEqual(p2A.getX(), p2B.getX()) && Kernel.isEqual(p2A.getY(), p2B.getY())) {
						//Application.debug("equal1");
						return 0;
					}
			
					// need to return something sensible, otherwise tree doesn't work
					return itemA.lengthSquared() > itemB.lengthSquared() ? -1 : 1;
					
				
				}
			};
			
			}
		
			return lineComparator;
		}
	  private static Comparator<MyLine> lineComparator;



}
