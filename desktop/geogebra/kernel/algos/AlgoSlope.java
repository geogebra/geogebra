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

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.algos.AlgoDrawInformation;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.AbstractConstruction;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoLine;
import geogebra.kernel.geos.GeoNumeric;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoSlope extends AlgoElement implements AlgoDrawInformation{

    private GeoLine g; // input
    private GeoNumeric slope; // output       

    /** Creates new AlgoDirection 
     * @param cons construction
     * @param label label for result
     * @param g line
     */
    public AlgoSlope(AbstractConstruction cons, String label, GeoLine g) {
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
     */
    AlgoSlope(GeoLine g) {
        super(g.cons, false);
        this.g = g;
    }
    
	@Override
	public String getClassName() {
        return "AlgoSlope";
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_SLOPE;
    }
   
    // for AlgoElement
    @Override
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
    public GeoNumeric getSlope() {
        return slope;
    }
    
    /**
     * @return the line
     */
    public GeoLine getg() {
        return g;
    }

    // direction vector of g
    @Override
	public final void compute() {
        if (g.isDefined() && !Kernel.isZero(g.y)) {
            slope.setValue(-g.x / g.y);
        } else {
            slope.setUndefined();
        }
    }

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("SlopeOfA",g.getLabel());
    }

	public AlgoDrawInformation copy() {		
		return new AlgoSlope((GeoLine)g.copy());
	}
}
