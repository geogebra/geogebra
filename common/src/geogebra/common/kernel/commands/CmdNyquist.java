package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoTransferFunction;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

/**
 * 
 * 
 * Nyquist [ <List of coefficents of numerator> , <List of coefficents of
 * denominator> ] 
 * 
 * Nyquist [ <List of coefficents of numerator> , <List of coefficents of
 * denominator>, omega ]
 * 
 * 
 * 
 * @author Giuliano
 * 
 */
public class CmdNyquist extends CommandProcessor {

	public CmdNyquist(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		switch (n) {
		case 2:
			if (!arg[0].isGeoList()) {
				throw argErr(app, c.getName(), arg[0]);
			}
			if (!arg[1].isGeoList()) {
				throw argErr(app, c.getName(), arg[1]);
			}
			AlgoTransferFunction algo = new AlgoTransferFunction(cons,
					c.getLabel(), (GeoList) arg[0], (GeoList) arg[1]);
			GeoElement[] ret = { algo.getResult() };
			return ret;
		case 3:
			if (!arg[0].isGeoList()) {
				throw argErr(app, c.getName(), arg[0]);
			}
			if (!arg[1].isGeoList()) {
				throw argErr(app, c.getName(), arg[1]);
			}
			if (arg[2].isGeoNumeric()) {
				int omega = (int) arg[2].evaluateDouble();
				if (Double.isNaN(omega)) {
					throw argErr(app, c.getVariableName(2), arg[2]);
				}
				algo = new AlgoTransferFunction(cons, c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1], omega);
				ret = new GeoElement[1];
				ret[0] = algo.getResult();
				return ret;
			}
			throw argErr(app, c.getName(), arg[2]);
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
