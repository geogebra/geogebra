/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoParabolaPointLine.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.LocusEquation;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoParabolaPointLine extends AlgoElement {

    private GeoPoint F;  // input    
    private GeoLine l;  // input    
    private GeoConic parabola; // output             
            
    public AlgoParabolaPointLine(Construction cons, String label, GeoPoint F, GeoLine l) {
        super(cons);
        this.F = F;
        this.l = l;                
        parabola = new GeoConic(cons); 
        setInputOutput(); // for AlgoElement
                
        compute();      
        parabola.setLabel(label);
    }   
    
    @Override
	public Commands getClassName() {
        return Commands.Parabola;
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_PARABOLA;
    }   
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = F;
        input[1] = l;
        
        super.setOutputLength(1);
        super.setOutput(0, parabola);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoConic getParabola() { return parabola; }
    // Made public for LocusEqu
    public GeoPoint getFocus() { return F; }
    // Made public for LocusEqu
    public GeoLine getLine() { return l; }
    
    // compute parabola with focus F and line l
    @Override
	public final void compute() {                           
        parabola.setParabola(F, l);
    }   
    
    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return loc.getPlain("ParabolaWithFocusAandDirectrixB",F.getLabel(tpl),l.getLabel(tpl));

    }

	@Override
	public boolean isLocusEquable() {
		return true;
	}
	
	public EquationElementInterface buildEquationElementForGeo(GeoElement geo, EquationScopeInterface scope) {
		return LocusEquation.eqnParabolaPointLine(geo, this, scope);
	}
}
