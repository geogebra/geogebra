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

import org.geogebra.common.geogebra3D.kernel3D.implicit3D.GeoImplicitSurface;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.main.MyError;

/**
 * ImplicitSurface[&lt;f(x, y, z)&gt;]
 * 
 * @author Shamshad Alam
 *
 */
public class CmdImplicitSurface extends CommandProcessor {
	/**
	 * 
	 * @param kernel
	 *            {@link Kernel}
	 */
	public CmdImplicitSurface(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c, info);
		if (n == 1) {
			if (arg[0] instanceof GeoFunctionNVar) {
				ExpressionNode lhs = ((GeoFunctionNVar) arg[0])
						.getFunctionExpression();
				ExpressionNode rhs = new ExpressionNode(kernel, 0.0);
				Equation e = new Equation(kernel, lhs, rhs);
				GeoImplicitSurface surf = new GeoImplicitSurface(cons, e);
				surf.setLabel(c.getLabel());
				return new GeoElement[] { surf };
			}
			throw argErr(c, arg[0]);
		}
		throw argNumErr(c);
	}
}
