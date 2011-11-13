package geogebra.kernel.discrete;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.MyPoint;
import geogebra.kernel.discrete.signalprocesser.voronoi.VPoint;
import geogebra.kernel.discrete.signalprocesser.voronoi.VoronoiAlgorithm;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.AbstractRepresentation;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.RepresentationFactory;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.simpletriangulation.SimpleTriangulationRepresentation;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.simpletriangulation.VTriangle;
import geogebra.kernel.kernelND.GeoPointND;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class AlgoDelauneyTriangulation extends AlgoHull{

	public AlgoDelauneyTriangulation(Construction cons, String label, GeoList inputList) {
		super(cons, label, inputList, null);
	}
	
    public String getClassName() {
        return "AlgoDelauneyTriangulation";
    }
    
    protected final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size == 0) {
    		locus.setUndefined();
    		return;
    	} 
    	
        if (vl == null) vl = new ArrayList<VPoint>();
        else vl.clear();
   	
		double inhom[] = new double[2];
   	
        
        AbstractRepresentation representation;
        representation = RepresentationFactory.createSimpleTriangulationRepresentation();

        for (int i = 0 ; i < size ; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointND p = (GeoPointND)geo;
				p.getInhomCoords(inhom);
				vl.add( representation.createPoint(inhom[0], inhom[1]) );			
			}
		}

        representation = RepresentationFactory.createSimpleTriangulationRepresentation();
        
        SimpleTriangulationRepresentation trianglarrep = (SimpleTriangulationRepresentation) representation;
        
        TestRepresentationWrapper representationwrapper = new TestRepresentationWrapper();
        representationwrapper.innerrepresentation = representation;
        
        VoronoiAlgorithm.generateVoronoi(representationwrapper, vl);
        
        if (al == null) al = new ArrayList<MyPoint>();
        else al.clear();
        
        TreeSet<MyLine> tree = new TreeSet<MyLine>(getComparator());
        
        for ( VTriangle triangle : trianglarrep.triangles ) {
        	
        	tree.add(new MyLine(new Point2D.Double(triangle.p1.x , triangle.p1.y), new Point2D.Double(triangle.p2.x , triangle.p2.y)));
        	tree.add(new MyLine(new Point2D.Double(triangle.p2.x , triangle.p2.y), new Point2D.Double(triangle.p3.x , triangle.p3.y)));
        	tree.add(new MyLine(new Point2D.Double(triangle.p3.x , triangle.p3.y), new Point2D.Double(triangle.p1.x , triangle.p1.y)));
        	
        }
        
        Iterator<MyLine> it = tree.iterator();
        
        while (it.hasNext()) {
        	MyLine line = it.next();
        	al.add(new MyPoint(line.p1.x , line.p1.y, false));
        	al.add(new MyPoint(line.p2.x , line.p2.y, true));
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
		        
					Point2D.Double p1A = itemA.p1;
					Point2D.Double p2A = itemA.p2;
					Point2D.Double p1B = itemB.p1;
					Point2D.Double p2B = itemB.p2;
					
					// return 0 if endpoints the same
					// so no duplicates in the TreeMap
					if (Kernel.isEqual(p1A.x, p2B.x) && Kernel.isEqual(p1A.y, p2B.y) && Kernel.isEqual(p2A.x, p1B.x) && Kernel.isEqual(p2A.y, p1B.y)) {
						//Application.debug("equal2");
						return 0;
					}
					// check this one second (doesn't occur in practice)
					if (Kernel.isEqual(p1A.x, p1B.x) && Kernel.isEqual(p1A.y, p1B.y) && Kernel.isEqual(p2A.x, p2B.x) && Kernel.isEqual(p2A.y, p2B.y)) {
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
