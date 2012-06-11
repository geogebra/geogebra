package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.main.MyError;

public class CmdImplicitDerivative extends CommandProcessor {

	public CmdImplicitDerivative(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		GeoElement[] args = resArgs(c);
		if(args.length!=1)
			throw argNumErr(app,c.getName(),args.length);
		if(!(args[0] instanceof FunctionalNVar))
			throw argErr(app,c.getName(),args[0]);
		return new GeoElement[] {kernelA.ImplicitDerivative(c.getLabel(),(FunctionalNVar)args[0])};
	}

}
