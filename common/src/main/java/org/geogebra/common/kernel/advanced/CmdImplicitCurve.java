package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.main.MyError;

/**
 * ImplicitCurve[&lt;Function&gt;]
 * 
 * @author GSoCImplicitCurve2015
 *
 */
public class CmdImplicitCurve extends CommandProcessor {

	/**
	 * Construct a new Implicit Curve
	 * 
	 * @param kernel
	 *            {@link Kernel}
	 */
	public CmdImplicitCurve(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);
		if (n == 1) {
			if (arg[0] instanceof GeoFunctionNVar) {
				GeoFunctionNVar func = (GeoFunctionNVar) arg[0];
				return new GeoElement[] { getAlgoDispatcher().ImplicitCurve(
						c.getLabel(), func) };
			}
			throw argErr(app, c.getName(), arg[0]);
		}
		throw argNumErr(app, c.getName(), n);
	}

}
