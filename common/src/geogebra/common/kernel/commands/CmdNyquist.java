package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoTransferFunction;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;

/**
 * 
 * 
 * Nyquist [ <List of coefficents of numerator> , <List of coefficents of
 * denominator> ] 
 * 
 * Nyquist [ <List of coefficents of numerator> , <List of
 * coefficents of denominator>, <Boolean high-quality> ]
 * 
 * Nyquist [ <List of coefficents of numerator> , <List of coefficents of
 * denominator>, omega ]
 * 
 * Nyquist [ <List of coefficents of numerator> , <List of coefficents of
 * denominator>, omega , <Boolean high-quality> ]
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
					c.getLabel(), (GeoList) arg[0], (GeoList) arg[1], 50, 1.01);
			GeoElement[] ret = { algo.getResult() };
			return ret;
		case 3:
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
			if (arg[2].isGeoNumeric()) {
				int omega = (int) arg[2].evaluateNum().getNumber().getDouble();
				if (Double.isNaN(omega) || omega < 5) {
					throw argErr(app, c.getVariableName(2), arg[2]);
				}
				algo = new AlgoTransferFunction(cons, c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1], omega, 1.01);
				ret = new GeoElement[1];
				ret[0] = algo.getResult();
				return ret;
			}
			if (arg[2].isGeoBoolean()) {
				if (((GeoBoolean) arg[2]).getBoolean()) {
					algo = new AlgoTransferFunction(cons, c.getLabel(),
							(GeoList) arg[0], (GeoList) arg[1], 50, 1.001);
					ret = new GeoElement[1];
					ret[0] = algo.getResult();
					return ret;
				}
				algo = new AlgoTransferFunction(cons, c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1], 50, 1.01);
				ret = new GeoElement[1];
				ret[0] = algo.getResult();
				return ret;
			}
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
			int omega = 0;
			if (arg[2].isGeoNumeric()) {
				omega = (int) arg[2].evaluateNum().getNumber().getDouble();
				if (Double.isNaN(omega) || omega < 5) {
					throw argErr(app, c.getVariableName(2), arg[2]);
				}
			}
			if (arg[3].isGeoBoolean()) {
				if (((GeoBoolean) arg[3]).getBoolean()) {
					algo = new AlgoTransferFunction(cons, c.getLabel(),
							(GeoList) arg[0], (GeoList) arg[1], omega, 1.001);
					ret = new GeoElement[1];
					ret[0] = algo.getResult();
					return ret;
				}
				algo = new AlgoTransferFunction(cons, c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1], omega, 1.01);
				ret = new GeoElement[1];
				ret[0] = algo.getResult();
				return ret;
			}
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
