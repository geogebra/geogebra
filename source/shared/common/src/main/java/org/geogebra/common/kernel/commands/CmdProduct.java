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

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoFoldFunctions;
import org.geogebra.common.kernel.algos.AlgoProduct;
import org.geogebra.common.kernel.algos.AlgoProductMatrices;
import org.geogebra.common.kernel.algos.FoldComputer;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;

/**
 * Product[ list ]
 * 
 * @author Michael Borcherds
 * @version 2008-02-16
 */
public class CmdProduct extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdProduct(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		int n = c.getArgumentNumber();

		// needed for Sum[]
		if (c.getArgumentNumber() == 0) {
			throw argNumErr(c);
		}
		if (c.getArgumentNumber() == 4) {
			GeoElement[] res = CmdSum.processSymb(this, c, Operation.MULTIPLY);
			if (res != null) {
				return res;
			}
		}
		GeoElement[] arg;
		arg = resArgs(c, info);
		if (!arg[0].isGeoList()) {
			throw argErr(c, arg[0]);
		}
		GeoList list = (GeoList) arg[0];
		switch (n) {
		case 1:
			if (!list.isEmptyList() && list.get(0).isMatrix()) {
				AlgoProductMatrices algo = new AlgoProductMatrices(cons,
						c.getLabel(), list);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}
			return productGeneric(arg[0], null, c);
		case 2:
			// Product[<List of Numbers>, <Number>]
			if (arg[1].isGeoNumeric()) {
				return productGeneric(arg[0], (GeoNumeric) arg[1], c);

			}
			// Product[<List of Numbers>, <Frequency>]
			else if (arg[1].isGeoList()) {
				if (arg[0]
						.getGeoElementForPropertiesDialog() instanceof GeoNumberValue) {

					AlgoProduct algo = new AlgoProduct(cons, list,
							(GeoList) arg[1]);
					algo.getResult().setLabel(c.getLabel());
					GeoElement[] ret = { algo.getResult() };
					return ret;
				}
				throw argErr(c, arg[0]);
			}
			throw argErr(c, arg[1]);

		default:
			throw argNumErr(c);
		}
	}

	private GeoElement[] productGeneric(GeoElement geoElement, GeoNumeric limit,
			Command c) {
		GeoList list = (GeoList) geoElement;
		FoldComputer computer = CmdSum.getFoldComputer(list);

		if (computer != null) {
			AlgoFoldFunctions algo = new AlgoFoldFunctions(cons, c.getLabel(),
					list, limit, Operation.MULTIPLY, computer);

			GeoElement[] ret = { algo.getResult() };
			return ret;
		}
		throw argErr(c, geoElement);
	}

}
