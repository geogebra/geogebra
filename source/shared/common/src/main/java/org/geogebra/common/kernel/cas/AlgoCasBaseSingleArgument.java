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
 * Process a function using single argument command
 * 
 * @author Markus Hohenwarter
 */
public class AlgoCasBaseSingleArgument extends AlgoCasBase {
	private ArbitraryConstantRegistry arbconst = new ArbitraryConstantRegistry(this);

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 * @param cmd
	 *            command
	 * @param info
	 *            evaluation flags
	 */
	public AlgoCasBaseSingleArgument(Construction cons, String label, CasEvaluableFunction f,
			Commands cmd, EvalInfo info) {
		super(cons, label, f, cmd, info);
	}

	@Override
	protected void applyCasCommand(StringTemplate tpl) {
		// factor value form of f
		Commands cmd = this.getClassName();
		g.setUsingCasCommand(cmd.name() + "[%]", f, false, arbconst);
		if (f.isDefined() && !g.isDefined()) {
			g.toGeoElement().set(f.toGeoElement());
		}
	}
}
