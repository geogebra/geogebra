/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoPolarLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint2;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoPolarLine extends AlgoElement {
    
    private GeoConic c;  // input
    private GeoPoint2 P;     // input
    private GeoLine polar;  // output
        
    /** Creates new AlgoPolarLine */
    public AlgoPolarLine(Construction cons, String label, GeoConic c, GeoPoint2 P) {
        super(cons);
        this.P = P;
        this.c = c;                
        polar = new GeoLine(cons);
        
        setInputOutput(); // for AlgoElement
                
        compute();              
        polar.setLabel(label);
    }   
    
    @Override
	public String getClassName() {
        return "AlgoPolarLine";
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_POLAR_DIAMETER;
    }
        
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = c;
        
        super.setOutputLength(1);
        super.setOutput(0, polar);
        setDependencies(); // done by AlgoElement
    }    
    
    GeoPoint2 getPoint() { return P; }    
    GeoConic getConic() { return c; }
    public GeoLine getLine() { return polar; }
    
    // calc polar line of P relativ to c
    @Override
	public final void compute() {     
        c.polarLine(P, polar);
    }
    
    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("PolarLineOfARelativeToB",P.getLabel(),c.getLabel());

    }
}
