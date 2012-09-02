package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoExtremumMulti;
import geogebra.common.kernel.algos.AlgoExtremumPolynomial;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;

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
	public CmdExtremum(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			ok[0] = arg[0].isGeoFunctionable();
			if (ok[0])
				return Extremum(c.getLabels(),
						((GeoFunctionable) arg[0]).getGeoFunction());
			throw argErr(app, c.getName(), arg[0]);
		case 3: // Ulven 04.02.2011 for Extremum[f,start-x,end-x]
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))

			) {
				
				AlgoExtremumMulti algo = new AlgoExtremumMulti(cons, c.getLabels(), 
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1],
						(NumberValue) arg[2]);
				return algo.getExtremumPoints(); 
			}
			
			throw argErr(app, c.getName(), getBadArg(ok,arg));
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
	

	/**
	 * all Extrema of function f (works only for polynomials)
	 */
	final private GeoPoint[] Extremum(String[] labels, GeoFunction f) {
		// check if this is a polynomial at the moment
		if (!f.isPolynomialFunction(true))
			return null;

		AlgoExtremumPolynomial algo = new AlgoExtremumPolynomial(cons, labels,
				f);
		GeoPoint[] g = algo.getRootPoints();
		return g;
	}
}
