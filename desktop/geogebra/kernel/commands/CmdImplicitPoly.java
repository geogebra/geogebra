package geogebra.kernel.commands;

import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunctionNVar;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.main.Application;
import geogebra.main.MyError;

public class CmdImplicitPoly extends CommandProcessor {

	public CmdImplicitPoly(Kernel kernel) {
		super(kernel);
	}

	protected GeoElement doCommand(String a, GeoList b) {
		int n = b.size();
		if (n == 0 || (int) Math.sqrt(9 + 8 * n) != Math.sqrt(9 + 8 * n))
			throw argNumErr(app, "ImplicitCurve", n);

		for (int i = 0; i < n; i++)
			if (!b.get(i).isGeoPoint())
				throw argErr(app, "ImplicitCurve", b.get(i));

		GeoElement ret = kernel.ImplicitPoly(a, b);

		return ret;
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 0:
			throw argNumErr(app, c.getName(), n);
		case 1:
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { doCommand(c.getLabel(), (GeoList) arg[0]) };
				return ret;
			} else if (arg[0] instanceof GeoFunctionNVar) {
				FunctionNVar f=((GeoFunctionNVar)arg[0]).getFunction();
				FunctionVariable[] fvars=f.getFunctionVariables();
				if (fvars.length!=2){
					throw new MyError(app,"InvalidEquation");
				}
				GeoElement[] ret = { kernel.ImplicitPoly(c.getLabel(),((GeoFunctionNVar)arg[0])) };
				return ret;
			} else {
				Application.debug(arg[0] + ": " + arg[0].getClassName() + "; "
						+ arg[0].getClass());
				throw argErr(app, c.getName(), arg[0]);
			}

			// more than one argument
		default:
			if (arg[0].isNumberValue()) {
				// try to create list of numbers
				GeoList list = wrapInList(kernel, arg, arg.length,
						GeoElement.GEO_CLASS_NUMERIC);
				if (list != null) {
					GeoElement[] ret = { doCommand(c.getLabel(), list) };
					return ret;
				}
			} else if (arg[0].isVectorValue()) {
				// try to create list of points (eg FitExp[])
				GeoList list = wrapInList(kernel, arg, arg.length,
						GeoElement.GEO_CLASS_POINT);
				if (list != null) {
					GeoElement[] ret = { doCommand(c.getLabel(), list) };
					return ret;
				}

			}
			throw argNumErr(app, c.getName(), n);
		}
	}
}