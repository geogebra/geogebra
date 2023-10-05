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
		GeoElement[] arg = resArgs(c);
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
