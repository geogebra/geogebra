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

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoNumeric;


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
    public AlgoSlope(Construction cons, String label, GeoLine g) {
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
	public Algos getClassName() {
        return Algos.AlgoSlope;
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
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("SlopeOfA",g.getLabel(tpl));
    }

	public AlgoDrawInformation copy() {		
		return new AlgoSlope((GeoLine)g.copy());
	}

	// TODO Consider locusequability
}
