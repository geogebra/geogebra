package geogebra.common.kernel.discrete;

import geogebra.common.awt.Point2D;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.VoronoiAlgorithm;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.AbstractRepresentation;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.RepresentationFactory;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.simpletriangulation.SimpleTriangulationRepresentation;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.simpletriangulation.VTriangle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.kernelND.GeoPointND;

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
        	
        	tree.add(new MyLine(AwtFactory.prototype.newPoint2D(triangle.p1.x , triangle.p1.y), AwtFactory.prototype.newPoint2D(triangle.p2.x , triangle.p2.y)));
        	tree.add(new MyLine(AwtFactory.prototype.newPoint2D(triangle.p2.x , triangle.p2.y), AwtFactory.prototype.newPoint2D(triangle.p3.x , triangle.p3.y)));
        	tree.add(new MyLine(AwtFactory.prototype.newPoint2D(triangle.p3.x , triangle.p3.y), AwtFactory.prototype.newPoint2D(triangle.p1.x , triangle.p1.y)));
        	
        }
        
        Iterator<MyLine> it = tree.iterator();
        
        while (it.hasNext()) {
        	MyLine line = it.next();
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
		        
					Point2D p1A = itemA.p1;
					Point2D p2A = itemA.p2;
					Point2D p1B = itemB.p1;
					Point2D p2B = itemB.p2;
					
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
