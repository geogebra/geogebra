package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoTransferFunction;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;

/**
 * 
 * 
 * Bode [ <List of coefficents of numerator> , <List of coefficents of
 * denominator> ]
 * 
 * Bode [ <List of coefficents of numerator> , <List of coefficents of
 * denominator>, min omega>0, max omega>min omega ]
 * 
 * @author Giuliano
 * 
 */

public class CmdBode extends CommandProcessor {

	public CmdBode(Kernel kernel) {
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
			if (!arg[0].isGeoList()
					|| !((GeoList) arg[0]).getElementType().equals(
							GeoClass.NUMERIC)
					|| !(((GeoList) arg[0]).size() > 0)) {
				throw argErr(app, c.getName(), arg[0]);
			}
			if (!arg[1].isGeoList()
					|| !((GeoList) arg[1]).getElementType().equals(
							GeoClass.NUMERIC)
					|| !(((GeoList) arg[1]).size() > 0)) {
				throw argErr(app, c.getName(), arg[1]);
			}
			AlgoTransferFunction algo = new AlgoTransferFunction(cons,
					c.getLabel(), (GeoList) arg[0], (GeoList) arg[1], -3, 3);
			GeoElement[] ret = { algo.getResult() };
			return ret;
		case 4:
			if (!arg[0].isGeoList()
					|| !((GeoList) arg[0]).getElementType().equals(
							GeoClass.NUMERIC)
					|| !(((GeoList) arg[0]).size() > 0)) {
				throw argErr(app, c.getName(), arg[0]);
			}
			if (!arg[1].isGeoList()
					|| !((GeoList) arg[1]).getElementType().equals(
							GeoClass.NUMERIC)
					|| !(((GeoList) arg[1]).size() > 0)) {
				throw argErr(app, c.getName(), arg[1]);
			}
			int omegaStart = (int) c.getArgument(2).evaluateNum().getNumber()
					.getDouble();
			if (Double.isNaN(omegaStart)) {
				throw argErr(app, c.getName(), c.getArgument(2));
			}
			int omegaEnd = (int) c.getArgument(3).evaluateNum().getNumber()
					.getDouble();
			if (Double.isNaN(omegaEnd)) {
				throw argErr(app, c.getName(), c.getArgument(3));
			}
			if (omegaStart >= omegaEnd) {
				throw argErr(app, c.getName(), c.getArgument(2));
			}
			algo = new AlgoTransferFunction(cons, c.getLabel(),
					(GeoList) arg[0], (GeoList) arg[1], omegaStart, omegaEnd);
			ret = new GeoElement[1];
			ret[0] = algo.getResult();
			return ret;
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
