/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDiameterLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.LocusEquation;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDiameterLine extends AlgoDiameterLineND {

    private GeoVector v;

    /** Creates new AlgoJoinPoints */
    public AlgoDiameterLine(Construction cons, String label, GeoConicND c, GeoLineND g) {
        super(cons, label, c, g);
    }

    @Override
	protected void createOutput(Construction cons){
    	diameter = new GeoLine(cons);
    	v = new GeoVector(cons);
    }

    // calc diameter line of v relativ to c
    @Override
	public final void compute() {
        ((GeoLine) g).getDirection(v);
        c.diameterLine(v, (GeoLine) diameter);
    }


	@Override
	public boolean isLocusEquable() {
		return true;
	}
	
	public EquationElementInterface buildEquationElementForGeo(GeoElement geo, EquationScopeInterface scope) {
		return LocusEquation.eqnDiameterLine(geo, this, scope);
	}
}
