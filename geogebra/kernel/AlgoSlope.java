/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoSlope.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoSlope extends AlgoElement implements AlgoDrawInformation{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoLine g; // input
    private GeoNumeric slope; // output       

    /** Creates new AlgoDirection 
     * @param cons construction
     * @param label label for result
     * @param g line
     */
    AlgoSlope(Construction cons, String label, GeoLine g) {
        super(cons);
        this.g = g;
        slope = new GeoNumeric(cons);
        setInputOutput(); // for AlgoElement

        compute();
        slope.setLabel(label);
        slope.setDrawable(true);
    }
    /**
     * For dummy copy only
     * @param cons
     * @param g
     */
    AlgoSlope(GeoLine g) {
        super(g.cons, false);
        this.g = g;
    }
    
	public String getClassName() {
        return "AlgoSlope";
    }

    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_SLOPE;
    }

    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = g;

        setOutputLength(1);
        setOutput(0,slope);
        setDependencies(); // done by AlgoElement
    }

    /**
     * @return resulting slope
     */
    GeoNumeric getSlope() {
        return slope;
    }
    
    /**
     * @return the line
     */
    public GeoLine getg() {
        return g;
    }

    // direction vector of g
    protected final void compute() {
        if (g.isDefined() && !Kernel.isZero(g.y))
            slope.setValue(-g.x / g.y);
        else
            slope.setUndefined();
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("SlopeOfA",g.getLabel());
    }

	public AlgoDrawInformation copy() {		
		return new AlgoSlope((GeoLine)g.copy());
	}
}
