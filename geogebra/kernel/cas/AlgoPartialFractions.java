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

public class AlgoPartialFractions extends AlgoCasBase {
   
	public AlgoPartialFractions(Construction cons,  String label, CasEvaluableFunction f) {
		super(cons, label, f);
	}

	@Override
	public String getClassName() {
		return "AlgoCasPartialFractions";
	}

	@Override
	protected void applyCasCommand() {
		
		// f.getVarString() can return a number in wrong alphabet (need ASCII)
		boolean internationalizeDigits = kernel.internationalizeDigits;
		kernel.internationalizeDigits = false;
		
		// get variable string with tmp prefix, 
		// e.g. "x" becomes "ggbtmpvarx" here
		boolean isUseTempVariablePrefix = kernel.isUseTempVariablePrefix();
		kernel.setUseTempVariablePrefix(true);
		String varStr = f.getVarString();
		kernel.setUseTempVariablePrefix(isUseTempVariablePrefix);

		 kernel.internationalizeDigits = internationalizeDigits;
		
		 sbAE.setLength(0);
		 sbAE.append("PartialFractions(%");
		 sbAE.append(",");
		 sbAE.append(varStr);		
		 sbAE.append(")");
		 		
		g.setUsingCasCommand(sbAE.toString(), f, false);		
	}

}
