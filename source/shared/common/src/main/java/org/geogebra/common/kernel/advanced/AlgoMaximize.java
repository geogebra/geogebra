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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * AlgoMaximize Command Maximize[ &lt;dependent variable&gt;, &lt;independent
 * variable&gt; ] which searches for the independent variable which gives the
 * largest result for the dependent variable.
 * 
 * Extends abstract class AlgoOptimize
 * 
 * @author Hans-Petter Ulven
 * @version 20.02.2011
 * 
 */

public class AlgoMaximize extends AlgoOptimize {

	/**
	 * Constructor for Maximize
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param dep
	 *            dependent value
	 * @param indep
	 *            independent number
	 */
	public AlgoMaximize(Construction cons, String label, GeoNumberValue dep,
			GeoNumeric indep) {
		super(cons, label, dep, new OptimizerNumeric(dep, indep),
				OptimizationType.MAXIMIZE);
		// cons.registerEuclididanViewAlgo(this);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param dep
	 *            dependent number
	 * @param indep
	 *            moving point
	 */
	public AlgoMaximize(Construction cons, String label, GeoNumberValue dep,
			GeoPointND indep) {
		super(cons, label, dep, new OptimizerPoint(dep, indep),
				OptimizationType.MAXIMIZE);
		// cons.registerEuclididanViewAlgo(this);
	}

	@Override
	public Commands getClassName() {
		return Commands.Maximize;
	}

}