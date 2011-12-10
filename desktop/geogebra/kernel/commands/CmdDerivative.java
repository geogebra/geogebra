package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/**
 * Derivative[ <GeoFunction> ] Derivative[ <GeoFunctionNVar>, <var> ]
 * Derivative[ <GeoCurveCartesian> ]
 */
class CmdDerivative extends CommandProcessorDesktop {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDerivative(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		String label = c.getLabel();
		GeoElement[] arg, arg2;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0] instanceof CasEvaluableFunction) {
				CasEvaluableFunction f = (CasEvaluableFunction) arg[0];
				if (label == null)
					label = getDerivLabel((GeoElement)f.toGeoElement(), 1);
				GeoElement[] ret = { kernel.Derivative(label, f, null, null) };
				return ret;
			} else
				throw argErr(app, "Derivative", arg[0]);

		case 2:
			arg = resArgs(c);
			// Derivative[ f(x), 2]
			if ((arg[0].isGeoFunction()||arg[0].isGeoCurveCartesian())
					&& arg[1].isNumberValue()) {
				double order = ((NumberValue) arg[1]).getDouble();

				CasEvaluableFunction f = (CasEvaluableFunction) arg[0];
				if (label == null) {
					int iorder = (int) Math.round(order);
					label = getDerivLabel((GeoElement)f.toGeoElement(), iorder);
				}
				GeoElement[] ret = { kernel.Derivative(label, f, null,
						(NumberValue) arg[1]) };
				return ret;

			}
			
			// Derivative[ f(a,b), a ]
			try {
				arg2 = resArgsLocalNumVar(c, 1, 1);
				if (arg2[0] instanceof CasEvaluableFunction
						&& arg2[1].isGeoNumeric()) {
					GeoElement[] ret = { kernel.Derivative(label,
							(CasEvaluableFunction) arg2[0], // function
							(GeoNumeric) arg2[1], null) }; // var
					return ret;
				}
			} catch (Throwable t) {
			}

			// Derivative[ f(x, y), x]
			if (arg[0] instanceof CasEvaluableFunction
					&& arg[1].isGeoFunction()) {
				GeoNumeric var = new GeoNumeric(cons);
				var.setLocalVariableLabel(arg[1].toString());
				GeoElement[] ret = { kernel.Derivative(label,
						(CasEvaluableFunction) arg[0], // function
						(GeoNumeric) var, null) }; // var
				return ret;
			}

			// if we get here, the first argument must have been wrong
			throw argErr(app, "Derivative", arg[0]);

		case 3:
			// Derivative[ f(a,b), a, 2 ]
			try {
				arg = resArgsLocalNumVar(c, 1, 1);
				if (arg[0] instanceof GeoFunctionNVar && arg[1].isGeoNumeric()
						&& arg[2].isNumberValue()) {
					GeoElement[] ret = { kernel.Derivative(label,
							(GeoFunctionNVar) arg[0], // function
							(GeoNumeric) arg[1], (NumberValue) arg[2]) }; // var
					return ret;
				}
			} catch (Throwable t) {
			}

			arg = resArgs(c);
			// Derivative[ f(x, y), x, 2]
			if (arg[0] instanceof GeoFunctionNVar && arg[1].isGeoFunction()
					&& arg[2].isNumberValue()) {
				GeoNumeric var = new GeoNumeric(cons);
				var.setLocalVariableLabel(arg[1].toString());
				GeoElement[] ret = { kernel.Derivative(label,
						(GeoFunctionNVar) arg[0], // function
						(GeoNumeric) var, (NumberValue) arg[2]) }; // var
				return ret;
			}
			// if we get here, the first argument must have been wrong
			throw argErr(app, "Derivative", arg[0]);

		default:
			throw argNumErr(app, "Derivative", n);
		}

	}

	private String getDerivLabel(GeoElement geo, int order) {
		String label = null;

		if (geo.isLabelSet()) {
			label = geo.getLabel();
			for (int i = 0; i < order; i++)
				label = label + "'";
		}

		return geo.getFreeLabel(label);
	}
}
