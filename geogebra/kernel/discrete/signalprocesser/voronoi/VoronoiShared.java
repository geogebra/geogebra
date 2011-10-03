package geogebra.kernel.discrete.signalprocesser.voronoi;
import geogebra.kernel.discrete.signalprocesser.voronoi.eventqueue.VCircleEvent;
import geogebra.kernel.discrete.signalprocesser.voronoi.eventqueue.VSiteEvent;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

public class VoronoiShared {
    
    // note: the order is very important
    //  make sure index 0 remains (-b + bsqrdplus4ac), and 1 (-b - bsqrdplus4ac)
    public static double[] solveQuadratic(double a, double b, double c) {
        if (a == 0.0) {
            if (b != 0.0) {
                double answers[] = new double[1];
                answers[0] = -1 * c / b;
                return answers;
            } else {
                throw new RuntimeException("Only given a non-zero c value");
            }
        } else {
            double answers[] = new double[2];
            double bsqrdplus4ac = Math.sqrt(b * b - 4d * a * c);
            answers[0] = (-b + bsqrdplus4ac) / ( 2d * a );
            answers[1] = (-b - bsqrdplus4ac) / ( 2d * a );
            return answers;
        }
    }
    
    public static VCircleEvent calculateCenter(VSiteEvent u, VSiteEvent v, VSiteEvent w) {
        double a  = (u.getX() - v.getX())*(v.getY() - w.getY()) - (u.getY() - v.getY())*(v.getX() - w.getX());
        if ( a > 0 ) {
            double b1 = (u.getX() - v.getX())*(u.getX() + v.getX()) + (u.getY() - v.getY())*(u.getY() + v.getY());
            double b2 = (v.getX() - w.getX())*(v.getX() + w.getX()) + (v.getY() - w.getY())*(v.getY() + w.getY());
            
            VCircleEvent centerpoint = new VCircleEvent();
            double x             = ( b1*(double)(v.getY() - w.getY()) - b2*(double)(u.getY() - v.getY()) ) / ( 2.0 * a );
            double center_y      = ( b2*(double)(u.getX() - v.getX()) - b1*(double)(v.getX() - w.getX()) ) / ( 2.0 * a );
            centerpoint.setX(       (int) x  );
            centerpoint.setY(       (int)( center_y + Math.sqrt( (x-u.getX())*(x-u.getX()) + (center_y-u.getY())*(center_y-u.getY()) ) ) );
            centerpoint.setCenterY( (int) center_y );
            return centerpoint;
        } else {
            return null;
        }
    }
    
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
