/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.cas;

import geogebra.common.kernel.AlgoCasBase;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.geos.CasEvaluableFunction;


/**
 * Try to expand the given function's expression
 * (e.g. function expression or dependent number's expression). 
 * 
 * @author Markus Hohenwarter
 */
public class AlgoExpand extends AlgoCasBase {
       
    public AlgoExpand(Construction cons, String label, CasEvaluableFunction f) {
    	super(cons, label, f);
    }   
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoExpand;
    }
    private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);
	@Override
	protected void applyCasCommand(StringTemplate tpl) {
		// symbolic expand of f
		g.setUsingCasCommand("Numeric(Expand(%))", f, false,arbconst);		
	}
	
//    final public String toString() {  
//    	StringBuilder sb = new StringBuilder();
//    	sb.append(getCommandDescription());
//    	
//        if (!f.toGeoElement().isIndependent()) { // show the symbolic representation too
//            sb.append(": ");
//            sb.append(g.toGeoElement().getLabel());
//            if (g.toGeoElement() instanceof GeoFunction)
//            {
//            	sb.append('(');
//            	sb.append(((GeoFunction) g.toGeoElement()).getVarString());
//            	sb.append(')');
//            }
//            sb.append(" = ");
//            sb.append(g.toSymbolicString());
//        }
//        return sb.toString();
//    }

	// TODO Consider locusequability

}
