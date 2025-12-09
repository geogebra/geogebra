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

package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.AlgoParseToNumberOrFunction;
import org.geogebra.common.kernel.algos.AlgoDependentGeoCopy;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

/**
 * ParseToFunction
 */
public class CmdParseToFunction extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdParseToFunction(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c, info);

		boolean ok;

		switch (n) {
		case 1:
			if (arg[0].isGeoText()) {
				return getParseAlgoResult((GeoText) arg[0], c, null);
			}
			throw argErr(arg[0], c);
		case 2:
			if (arg[0].isGeoText() && arg[1].isGeoList()) {
				return getParseAlgoResult((GeoText) arg[0], c, (GeoList) arg[1]);
			}
			if ((ok = arg[0].isGeoFunction() || arg[0].isGeoFunctionNVar())
					&& arg[1].isGeoText()) {
				if (!info.isScripting()) {
					return new GeoElement[0];
				}
				GeoElement fun = arg[0];
				if (!fun.isLabelSet()) {
					AlgoElement algo = fun.getParentAlgorithm();
					if (algo instanceof AlgoDependentGeoCopy) {
						fun = algo.getInput(0).toGeoElement();
					}
				}

				String str = ((GeoText) arg[1]).getTextStringSafe();

				try {
					GeoElement parsed = arg[0].isGeoFunction()
							? kernel.getAlgebraProcessor()
									.evaluateToFunction(str, true)
							: kernel.getAlgebraProcessor()
									.evaluateToFunctionNVar(str, true, false);
					fun.set(parsed);
					fun.updateCascade();
				} catch (Exception e) {
					// eg ParseToFunction[f, "hello"]
					fun.setUndefined();
					fun.updateCascade();
				}

				return fun.asArray();
			} else if (!ok) {
				throw argErr(c, arg[0]);
			} else {
				throw argErr(c, arg[1]);
			}

		default:
			throw argNumErr(c);
		}
	}

	private GeoElement[] getParseAlgoResult(GeoText text, Command c, GeoList vars) {
		AlgoParseToNumberOrFunction parseAlgo = new AlgoParseToNumberOrFunction(cons, text, vars,
				Commands.ParseToFunction, c.getLabel());
		parseAlgo.getOutput(0).setLabel(c.getLabel());
		return parseAlgo.getOutput();
	}
}
