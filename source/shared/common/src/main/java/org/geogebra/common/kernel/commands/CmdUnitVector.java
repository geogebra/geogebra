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
import org.geogebra.common.kernel.algos.AlgoUnitVector;
import org.geogebra.common.kernel.algos.AlgoUnitVectorLine;
import org.geogebra.common.kernel.algos.AlgoUnitVectorVector;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.main.MyError;

/**
 * UnitVector[ &lt;GeoLine&gt; ] UnitVector[ &lt;GeoVector&gt; ]
 */
public class CmdUnitVector extends CommandProcessor {

	/**
	 * Whether to use UnitVector rather than Direction
	 */
	protected boolean normalize;

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 * @param normalize
	 *            Whether to use UnitVector rather than Direction
	 */
	public CmdUnitVector(Kernel kernel, boolean normalize) {
		super(kernel);
		this.normalize = normalize;
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();

		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c, info);
			if (arg[0].isGeoLine()) {

				AlgoUnitVector algo = algo((GeoLineND) arg[0]);
				algo.getVector().setLabel(c.getLabel());
				GeoElement[] ret = { (GeoElement) algo.getVector() };
				return ret;
			} else if (arg[0] instanceof VectorNDValue) {

				AlgoUnitVector algo = algo((VectorNDValue) arg[0]);
				algo.getVector().setLabel(c.getLabel());
				GeoElement[] ret = { (GeoElement) algo.getVector() };
				return ret;
			} else {
				return processNotLineNotVector(c, arg[0]);
			}

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * process command in case arg is not a line nor a vector
	 * 
	 * @param c
	 *            command
	 * @param arg
	 *            single argument (not line or vector)
	 * @return result
	 * @throws MyError
	 *             always thrown in 2D; in 3D accepts planar geos
	 */
	protected GeoElement[] processNotLineNotVector(Command c, GeoElement arg)
			throws MyError {
		throw argErr(c, arg);
	}

	/**
	 * 
	 * @param line
	 *            line
	 * @return algo for this line
	 */
	protected AlgoUnitVector algo(GeoLineND line) {
		return new AlgoUnitVectorLine(cons, line, normalize);
	}

	/**
	 * 
	 * @param v
	 *            vector
	 * @return algo for this vector
	 */
	protected AlgoUnitVector algo(VectorNDValue v) {
		return new AlgoUnitVectorVector(cons, v, normalize);
	}
}
