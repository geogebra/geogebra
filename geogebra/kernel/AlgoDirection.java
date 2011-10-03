/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDirection.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDirection extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoLine g; // input
    private GeoVector v; // output  

    /** Creates new AlgoDirection */    
    AlgoDirection(Construction cons, GeoLine g) {
    	this(cons, null, g);
    }
    
    AlgoDirection(Construction cons, String label, GeoLine g) {
        super(cons);
        this.g = g;
        v = new GeoVector(cons);

        GeoPoint possStartPoint = g.getStartPoint();                  
        if (possStartPoint!= null && possStartPoint.isLabelSet()) {
	        try{
	            v.setStartPoint(possStartPoint);
	        } catch (CircularDefinitionException e) {}
        }

        setInputOutput(); // for AlgoElement

        // compute line through P, Q
        v.z = 0.0d;
        compute();        
        v.setLabel(label);
    }

    public String getClassName() {
        return "AlgoDirection";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = g;

        output = new GeoElement[1];
        output[0] = v;
        setDependencies(); // done by AlgoElement
    }

    GeoVector getVector() {
        return v;
    }
    GeoLine getg() {
        return g;
    }

    // direction vector of g
    protected final void compute() {
        v.x = g.y;
        v.y = -g.x;
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("DirectionOfA",g.getLabel());
    }
}
