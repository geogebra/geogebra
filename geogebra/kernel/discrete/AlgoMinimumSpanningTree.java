package geogebra.kernel.discrete;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.MyPoint;
import geogebra.kernel.discrete.signalprocesser.voronoi.VPoint;
import geogebra.kernel.discrete.signalprocesser.voronoi.VoronoiAlgorithm;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.AbstractRepresentation;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.RepresentationFactory;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.triangulation.VHalfEdge;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.triangulation.VVertex;
import geogebra.kernel.kernelND.GeoPointND;

import java.util.ArrayList;

public class AlgoMinimumSpanningTree extends AlgoHull{

	public AlgoMinimumSpanningTree(Construction cons, String label, GeoList inputList) {
		super(cons, label, inputList, null);
	}
	
    public String getClassName() {
        return "AlgoMinimumSpanningTree";
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
        representation = RepresentationFactory.createTriangulationRepresentation();

        for (int i = 0 ; i < size ; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointND p = (GeoPointND)geo;
				p.getInhomCoords(inhom);
				vl.add( representation.createPoint(inhom[0], inhom[1]) );			
			}
		}

        
        representation = RepresentationFactory.createTriangulationRepresentation();
        
        TriangulationRepresentation trianglarrep = (TriangulationRepresentation) representation;
        
        trianglarrep.setDetermineMinSpanningTreeMode();
       
        TestRepresentationWrapper representationwrapper = new TestRepresentationWrapper();
        representationwrapper.innerrepresentation = representation;
        
        VoronoiAlgorithm.generateVoronoi(representationwrapper, vl);
        
        if (al == null) al = new ArrayList<MyPoint>();
        else al.clear();
        
        for ( VPoint point : trianglarrep.vertexpoints ) {
            VVertex vertex = (VVertex) point;
            
            // Check the vertex has edges
            if ( vertex.hasEdges()==false ) {
                continue;
            }
            

            // Paint each of those edges
            for ( VHalfEdge edge : vertex.getEdges() ) {
                // Simple addition to show MST
                if ( edge.shownonminimumspanningtree==false ) continue;
                
                VVertex vertex2 = edge.next.vertex;
                al.add(new MyPoint(vertex.x , vertex.y, false));
                al.add(new MyPoint(vertex2.x , vertex2.y, true));
                
                 }
            }

		locus.setPoints(al);
		locus.setDefined(true);
       
    }


}
