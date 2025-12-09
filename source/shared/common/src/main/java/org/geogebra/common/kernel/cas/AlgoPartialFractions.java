/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoCasBase;
import org.geogebra.common.kernel.arithmetic.ArbitraryConstantRegistry;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;

/**
 * Algorithm for partial fractions
 *
 */
public class AlgoPartialFractions extends AlgoCasBase {
	private ArbitraryConstantRegistry arbconst = new ArbitraryConstantRegistry(this);

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 * @param info
	 *            evaluation flags
	 */
	public AlgoPartialFractions(Construction cons, String label,
			CasEvaluableFunction f, EvalInfo info) {
		super(cons, label, f, Commands.PartialFractions, info);
	}

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
