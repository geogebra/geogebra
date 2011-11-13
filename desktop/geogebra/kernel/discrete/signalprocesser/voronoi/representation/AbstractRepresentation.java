package geogebra.kernel.discrete.signalprocesser.voronoi.representation;

import geogebra.kernel.discrete.signalprocesser.voronoi.VPoint;

import java.awt.Graphics2D;

abstract public class AbstractRepresentation implements RepresentationInterface {
    
    public AbstractRepresentation() {
    }

    public abstract VPoint createPoint(double inhom, double inhom2);
            
    public abstract void paint(Graphics2D g);
    
}
