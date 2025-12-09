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

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdTwoNumFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * RandomUniform[ &lt;Number&gt;, &lt;Number&gt; ]
 */
public class CmdRandomUniform extends CmdTwoNumFunction {

	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRandomUniform(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement doCommand(String a, GeoNumberValue b,
			GeoNumberValue c) {
		AlgoRandomUniform algo = new AlgoRandomUniform(cons, a, b, c);
		return algo.getResult();
	}

	@Override
	protected GeoElement doCommand2(Command a, GeoNumberValue b,
			GeoNumberValue c, GeoNumberValue d) {
		AlgoRandomUniformList algo = new AlgoRandomUniformList(cons,
				a.getLabel(), b, c, d);
		return algo.getResult();
	}
}
