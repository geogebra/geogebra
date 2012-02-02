package geogebra.common.kernel.discrete.signalprocessor.voronoi.representation;

import geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;


abstract public class AbstractRepresentation implements RepresentationInterface {
    
    public AbstractRepresentation() {
    }

    public abstract VPoint createPoint(double inhom, double inhom2);
            
    
}
