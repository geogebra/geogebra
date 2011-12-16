package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.main.MyError;
import geogebra.common.kernel.AbstractKernel;

/**
 * Extremum[ <GeoFunction> ]
 */
public class CmdExtremum extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdExtremum(AbstractKernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoFunctionable()))
				return kernelA.Extremum(c.getLabels(),
						((GeoFunctionable) arg[0]).getGeoFunction());
			else
				throw argErr(app, c.getName(), arg[0]);
		case 3: // Ulven 04.02.2011 for Extremum[f,start-x,end-x]
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))

			)
				return kernelA.Extremum(c.getLabels(), 
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1],
						(NumberValue) arg[2]
						);
			else
				throw argErr(app, c.getName(), n);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
