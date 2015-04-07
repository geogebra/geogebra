package org.geogebra.common.kernel.discrete.signalprocessor.voronoi;

import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.eventqueue.VCircleEvent;
import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.eventqueue.VSiteEvent;

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
            double x             = ( b1*(v.getY() - w.getY()) - b2*(u.getY() - v.getY()) ) / ( 2.0 * a );
            double center_y      = ( b2*(u.getX() - v.getX()) - b1*(v.getX() - w.getX()) ) / ( 2.0 * a );
            centerpoint.setX(       (int) x  );
            centerpoint.setY(       (int)( center_y + Math.sqrt( (x-u.getX())*(x-u.getX()) + (center_y-u.getY())*(center_y-u.getY()) ) ) );
            centerpoint.setCenterY( (int) center_y );
            return centerpoint;
        } else {
            return null;
        }
    }
}
