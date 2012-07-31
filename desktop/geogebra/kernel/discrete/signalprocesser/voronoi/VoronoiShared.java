package geogebra.kernel.discrete.signalprocesser.voronoi;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

public class VoronoiShared extends geogebra.common.kernel.discrete.signalprocessor.voronoi.VoronoiShared{
    
    
    
    
    // See http://astronomy.swin.edu.au/~pbourke/geometry/polyarea/ for
    //  explanation as to how area is calculated
    public static double calculateAreaOfShape(ArrayList<VPoint> points) {
        // If less than or equal to 2 points, return 0
        if ( points.size()<=2 ) return 0.0;
        
        // Otherwise, calcualte area
        int area = 0;
        VPoint first = points.get(0);
        VPoint prev, curr = first;
        for ( int x=1 ; x<points.size() ; x++ ) {
            // Set up next points
            prev = curr;
            curr = points.get(x);
            
            // Add to area
            area += prev.x * curr.y  - curr.x  * prev.y;
        }
        
        // If area hasn't been closed off, close off with final area
        if ( first.x!=curr.x || first.y!=curr.y ) {
            area += curr.x * first.y - first.x * curr.y;
        }
        
        // Return total area
        if ( area>=0 ) {
            return ( area /  2.0 );
        } else {
            return ( area / -2.0 );
        }
    }
    
    public static double calculatePerimeterOfShape(ArrayList<VPoint> points) {
        // If less than or equal to 2 points, return 0
        if ( points.size()<=1 ) return 0.0;
        
        // Otherwise, calcualte area
        double perimeter = 0;
        VPoint first = points.get(0);
        VPoint prev, curr = first;
        for ( int x=1 ; x<points.size() ; x++ ) {
            // Set up next points
            prev = curr;
            curr = points.get(x);
            
            // Add to area
            perimeter += Math.sqrt((curr.x-prev.x)*(curr.x-prev.x) + (curr.y-prev.y)*(curr.y-prev.y));
        }
        
        // If area hasn't been closed off, close off with final area
        if ( first.x!=curr.x || first.y!=curr.y ) {
            perimeter += Math.sqrt((first.x-curr.x)*(first.x-curr.x) + (first.y-curr.y)*(first.y-curr.y));
        }
        
        // Return perimeter
        return perimeter;
    }
    
    public static double calculateAreaOfShape(Shape shape) {
        return calculateAreaOfShape( shape.getPathIterator(null) );
    }
    public static double calculateAreaOfShape(PathIterator pathiter) {
        //System.out.println();
        //System.out.println("Calculate area of shape: (note: all areas shown are *twice* what they should be on purpose)");
        
        // Check path iterator not already done
        if ( pathiter.isDone() ) return 0.0;
        
        // Variables
        int type;
        double totalarea = 0, subarea;
        double first[] = new double[2];
        double curr[]  = new double[2];
        double prev[]  = new double[2];
        
        // Sum areas
        while ( true ) {
            // Get the current segment
            type = pathiter.currentSegment(curr);
            
            // Check type is a move to
            if ( type!=PathIterator.SEG_MOVETO ) {
                throw new RuntimeException("Expected PathIterator.SEG_MOVETO; instead type=" + formatPathIteratorType(type));
            }
            
            // Reset subarea to 0
            subarea = 0;
            
            // Remember the start point
            //  (as will need to remember it when SEG_CLOSE is called)
            first[0] = curr[0];
            first[1] = curr[1];
            
            //System.out.println("  - New Shape Starting from (" + first[0] + "," + first[1] + ")");
            
            // Calculate area while consider shape
            while ( true ) {
                // Move to next segment
                pathiter.next();
                
                // Update points
                prev[0] = curr[0];
                prev[1] = curr[1];
                type = pathiter.currentSegment(curr);
                if ( type==PathIterator.SEG_LINETO ) {
                    // Add area
                    subarea += prev[0] * curr[1]  - curr[0]  * prev[1];
                    //System.out.println("    - (" + prev[0] + "," + prev[1] + ") --> (" + curr[0] + "," + curr[1] + ") - Area=" + (prev[0] * curr[1]  - curr[0]  * prev[1]) + ", Subarea=" + subarea);
                    
                    // Continue
                    continue;
                } else if ( type==PathIterator.SEG_CLOSE ) {
                    // Add final area to close shape
                    if ( first[0]!=prev[0] || first[1]!=prev[1] ) {
                        subarea += prev[0] * first[1]  - first[0]  * prev[1];
                        //System.out.println("    - (" + prev[0] + "," + prev[1] + ") --> (" + first[0] + "," + first[1] + ") - Area=" + (prev[0] * first[1]  - first[0]  * prev[1]) + ", Subarea=" + subarea);
                    } else {
                        //System.out.println("    - (" + prev[0] + "," + prev[1] + ") --> (" + first[0] + "," + first[1] + ") - Area=" + (prev[0] * first[1]  - first[0]  * prev[1]) + ", Subarea=" + subarea + " -- identical?");
                    }
                    
                    // Add subarea to the total area
                    totalarea += ( subarea>=0 ? subarea : -1 * subarea );
                    //System.out.println("    - Subarea=" + ( subarea>=0 ? subarea : -1 * subarea ) + " --> Total Area=" + totalarea);
                    
                    // Move to next element
                    pathiter.next();
                    
                    // Check if all iterations have been done
                    if ( pathiter.isDone() ) {
                        //System.out.println("  - Returning area of " + (totalarea / 2.0) + " (1)");
                        return totalarea / 2.0;
                    }
                    
                    // Break (new shape to consider)
                    break;
                } else {
                    throw new RuntimeException("Expected either PathIterator.SEG_LINETO or SEG_CLOSE; instead type=" + formatPathIteratorType(type));
                }
            }
        }
    }
    
    private static String formatPathIteratorType(int type) {
        switch ( type ) {
            case PathIterator.SEG_CLOSE:
                return "SEG_CLOSE";
            case PathIterator.SEG_CUBICTO:
                return "SEG_CUBICTO";
            case PathIterator.SEG_LINETO:
                return "SEG_LINETO";
            case PathIterator.SEG_MOVETO:
                return "SEG_MOVETO";
            case PathIterator.SEG_QUADTO:
                return "SEG_QUADTO";
            default:
                return "UKNOWNTYPE (" + type + ")";
        }
    }
}
