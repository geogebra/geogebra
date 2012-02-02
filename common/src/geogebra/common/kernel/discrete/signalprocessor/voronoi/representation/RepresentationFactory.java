package geogebra.common.kernel.discrete.signalprocessor.voronoi.representation;

import geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.boundaryproblem.BoundaryProblemRepresentation;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.simpletriangulation.SimpleTriangulationRepresentation;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.triangulation.TriangulationRepresentation;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.voronoicell.VVoronoiCell;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.voronoicell.VoronoiCellRepresentation;

import java.util.ArrayList;

public class RepresentationFactory {
    
    // Don't allow to be instantiated
    private RepresentationFactory() { }
    
    /* ***************************************************** */
    // Create Representation Methods
    
    public static AbstractRepresentation createVoronoiCellRepresentation() {
        return new VoronoiCellRepresentation();
    }
    
    public static AbstractRepresentation createTriangulationRepresentation() {
        return new TriangulationRepresentation();
    }
    
    public static AbstractRepresentation createSimpleTriangulationRepresentation() {
        return new SimpleTriangulationRepresentation();
    }
    
    public static AbstractRepresentation createBoundaryProblemRepresentation() {
        return new BoundaryProblemRepresentation();
    }
    
    /* ***************************************************** */
    // Conversion Methods
    
    public static ArrayList<VPoint> convertPointsToVPoints(ArrayList<VPoint> points) {
        ArrayList<VPoint> newarraylist = new ArrayList<VPoint>();
        for ( VPoint point : points ) {
            newarraylist.add( new VPoint(point) );
        }
        return newarraylist;
    }
    
    public static ArrayList<VPoint> convertPointsToVoronoiCellPoints(ArrayList<VPoint> points) {
        ArrayList<VPoint> newarraylist = new ArrayList<VPoint>();
        for ( VPoint point : points ) {
            newarraylist.add( new VVoronoiCell(point) );
        }
        return newarraylist;
    }
    
    public static ArrayList<VPoint> convertPointsToTriangulationPoints(ArrayList<VPoint> points) {
    	geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.triangulation.VVertex.uniqueid = 1;
        ArrayList<VPoint> newarraylist = new ArrayList<VPoint>();
        for ( VPoint point : points ) {
            newarraylist.add( new geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.triangulation.VVertex(point) );
        }
        return newarraylist;
    }
    
    public static ArrayList<VPoint> convertPointsToSimpleTriangulationPoints(ArrayList<VPoint> points) {
        return convertPointsToVPoints( points );
    }
    
    public static ArrayList<VPoint> convertPointsToBoundaryProblemPoints(ArrayList<VPoint> points) {
        ArrayList<VPoint> newarraylist = new ArrayList<VPoint>();
        for ( VPoint point : points ) {
            newarraylist.add( new geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.boundaryproblem.voronoicell.VVoronoiCell(point) );
        }
        return newarraylist;
    }
    
    /* ***************************************************** */
}
