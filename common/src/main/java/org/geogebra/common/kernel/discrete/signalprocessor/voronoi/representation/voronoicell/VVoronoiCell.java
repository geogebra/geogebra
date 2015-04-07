package org.geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.voronoicell;
//package signalprocesser.voronoi.representation.boundaryproblem.voronoicell;

import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;

public class VVoronoiCell extends VPoint {
//public class VVoronoiCell extends signalprocesser.voronoi.representation.boundaryproblem.VVertex {
    
    public static final double NO_AREA_CALCULATED = -1.0;
    public static final double INVALID_AREA       = -2.0;
    
    public double area = NO_AREA_CALCULATED;
    public VHalfEdge halfedge;
    
    public VVoronoiCell() { super(); }
    public VVoronoiCell(double x, double y) { super(x, y); }
    public VVoronoiCell(VPoint point) { super(point); }
    
    public void resetArea() { area = NO_AREA_CALCULATED; }
    public double getAreaOfCell() {
        if ( area==NO_AREA_CALCULATED ) {
            area = calculateAreaOfCell();
            return ( area==INVALID_AREA ? -1.0 : area );
        } else if ( area==INVALID_AREA ) {
            return -1.0;
        } else {
            return area;
        }
    }

    private double calculateAreaOfCell() {
        // Check initially we have at least a triangle
        if ( halfedge==null || halfedge.getPrev()==null || halfedge.getNext()==null ) {
            return INVALID_AREA;
        }
        
        // Initialise points
        VHalfEdge point0 = halfedge;
        VHalfEdge point1 = halfedge.getPrev();
        VHalfEdge point2 = halfedge.getNext();
        
        // Begin to calculate area
        double totalarea = 0;
        boolean rightside = true;
        while ( true ) {
            // Calculate area of triangle
            totalarea += calculateAreaOfTriangle(
                    Math.sqrt((point0.x-point1.x)*(point0.x-point1.x) + (point0.y-point1.y)*(point0.y-point1.y)) ,
                    Math.sqrt((point1.x-point2.x)*(point1.x-point2.x) + (point1.y-point2.y)*(point1.y-point2.y)) ,
                    Math.sqrt((point2.x-point0.x)*(point2.x-point0.x) + (point2.y-point0.y)*(point2.y-point0.y))
                    );
            
            // Continue to next triangle
            if ( rightside ) {
                // Check if the voronoi cell ends, or if we're finished
                if ( point2.getNext()==null ) {
                    return INVALID_AREA;
                } else if ( point2.getNext()==point1 ) {
                    return totalarea;
                }
                
                // Swap to leftsided triangle
                VHalfEdge tmp = point0;
                point0 = point2;
                point2 = point2.getNext();
                rightside = false;
            } else {
                // Check if the voronoi cell ends, or if we're finished
                if ( point1.getPrev()==null ) {
                    return INVALID_AREA;
                } else if ( point1.getPrev()==point2 ) {
                    return totalarea;
                }
                
                // Swap to rightsided triangle
                point0 = point1;
                point1 = point0.getPrev();
                rightside = true;
            }
        }
    }
    
    // See http://en.wikipedia.org/wiki/Triangle#Using_the_side_lengths_and_a_numerically_stable_formula for method used
    public static double calculateAreaOfTriangle( double a , double b , double c ) {
        // Put triangles into sorted order; a >= b >= c
        double tmp;
        if ( b > a ) { tmp = a; a = b; b = tmp; }
        if ( c > b ) { tmp = b; b = c; c = tmp; }
        
        // Calculate area
        tmp = ( a + (b+c) )*( c - (a-b) )*( c + (a-b) )*( a + (b-c) );
        return 0.25 * Math.sqrt( tmp );
    }
}
