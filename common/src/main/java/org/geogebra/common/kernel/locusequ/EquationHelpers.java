/**
 * 
 */
package org.geogebra.common.kernel.locusequ;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * @author sergio
 * Just a class full of helpers ready to static import them.
 */
public class EquationHelpers {
    
    /**
     * Returns all dependent points that are predecessors of a given element.
     * @param element whose predecessors points will be returned.
     * @return an array of points. If element is a point it will be the last point in array.
     */
    public static GeoPoint[] getDependentPredecessorPointsForElement(final GeoElement element) {
        // Count points.
    	List<GeoPoint> points = new ArrayList<GeoPoint>(10); // Lucky guess.      
    
        Iterator<GeoElement> it = element.getAllPredecessors().iterator();
        
        GeoElement el;
        
        while(it.hasNext()){
            el = it.next();
            if(el.isGeoPoint() &&
                    !el.isIndependent()){
                points.add((GeoPoint) el);
            }
        }
        
        if(element.isGeoPoint()) {
            points.add((GeoPoint) element);
        }
        
        return points.toArray(new GeoPoint[points.size()]);
    }
    
    /**
     * Return all predecessor points for a given element.
     * @param element whose predecessor points are returned.
     * @return an array of points. If given element is a point it won't be returned.
     */
    public static GeoPoint[] getPredecessorPointsForElement(final GeoElement element) {
    	List<GeoPoint> points = new ArrayList<GeoPoint>(10);
        
        Iterator<GeoElement> it = element.getAllPredecessors().iterator();
        GeoElement el;
        while(it.hasNext()){
            el = it.next();
            if(el.isGeoPoint()){
                points.add((GeoPoint) el);
            }
        }
        return points.toArray(new GeoPoint[points.size()]);
    }
    
    /**
     * Returns the number of dependent predecessor points for element.
     * @param el element whose points will be returned.
     * @return the number of dependent predecessor points.
     */
    public static int countDependentPredecessorsPoints(final GeoElement el) {
        Iterator<GeoElement> it = el.getAllPredecessors().iterator();
        int count = 0;
        GeoElement element;
        while(it.hasNext()) {
            element = it.next();
            if(element.isGeoPoint() &&
                    !element.isIndependent()) count++;
        }
        return count;
    }
    
    /**
     * Counts the number of predecessor points for a given element.
     * @param el element.
     * @return the number of predecessor points for element.
     */
    public static int countPredecessorsPoints(final GeoElement el) {
        Iterator<GeoElement> it = el.getAllPredecessors().iterator();
        int count = 0;
        GeoElement element;
        while(it.hasNext()) {
            element = it.next();
            if(element.isGeoPoint()) count++;
        }
        return count;
    }
}
