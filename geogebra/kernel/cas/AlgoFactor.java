/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.cas;

import geogebra.kernel.CasEvaluableFunction;
import geogebra.kernel.Construction;

/**
 * Factor a function
 * 
 * @author Markus Hohenwarter
 */
public class AlgoFactor extends AlgoCasBase {
   
	public AlgoFactor(Construction cons,  String label, CasEvaluableFunction f) {
		super(cons, label, f);
	}

	@Override
	public String getClassName() {
		return "AlgoCasFactor";
	}

	@Override
	protected void applyCasCommand() {
		// factor value form of f
		g.setUsingCasCommand("Factor(%)", f, false);		
	}
}