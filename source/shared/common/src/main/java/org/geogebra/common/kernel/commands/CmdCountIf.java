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

package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoCountIf;
import org.geogebra.common.kernel.algos.AlgoCountIf3;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * CountIf[ &lt;GeoBoolean&gt;, &lt;GeoList&gt; ]
 */
public class CmdCountIf extends CmdKeepIf {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCountIf(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] getResult2(ValidExpression c, GeoFunction booleanFun,
			GeoElement[] args) {
		AlgoCountIf algo = new AlgoCountIf(cons, c.getLabel(), booleanFun,
				(GeoList) args[1]);
		GeoElement[] ret = { algo.getResult() };

		return ret;
	}

	@Override
	protected GeoElement[] getResult3(ValidExpression c, GeoBoolean arg,
			GeoElement[] vars, GeoList[] over) {
		AlgoCountIf3 algo = new AlgoCountIf3(cons, c.getLabel(), arg, vars[0],
				over[0]);
		GeoElement[] ret = { algo.getResult() };

		return ret;
	}
}
