/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoCasBase;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;

/**
 * Algorithm for partial fractions
 *
 */
public class AlgoPartialFractions extends AlgoCasBase {
	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 */
	public AlgoPartialFractions(Construction cons, String label,
			CasEvaluableFunction f) {
		super(cons, label, f, Commands.PartialFractions);
	}

	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);

	@Override
	protected void applyCasCommand(StringTemplate tpl) {

		// f.getVarString() can return a number in wrong alphabet (need ASCII)

		// get variable string with tmp prefix,
		// e.g. "x" becomes "ggbtmpvarx" here
		String varStr = f.getVarString(tpl);

		sbAE.setLength(0);
		sbAE.append("PartialFractions[%");
		sbAE.append(",");
		sbAE.append(varStr);
		sbAE.append("]");

		g.setUsingCasCommand(sbAE.toString(), f, false, arbconst);
	}

}
