package org.geogebra.common.kernel.statistics;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoFunctionFreehand;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * FitPoly[&lt;List of points>,&lt;degree>]
 * 
 * @author Hans-Petter Ulven
 * @version 06.04.08
 */
public class CmdFitPoly extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFitPoly(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);

		switch (n) {
		case 1:
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { fitPoly((GeoList) arg[0], null) };
				ret[0].setLabel(c.getLabel());
				return ret;
			}
			throw argErr(c, arg[0]);
		case 2:
			if (arg[0].isGeoList() && arg[1].isNumberValue()) {
				GeoElement[] ret = {
						fitPoly((GeoList) arg[0], (GeoNumberValue) arg[1]) };
				ret[0].setLabel(c.getLabel());
				return ret;
			} else if (arg[0].isGeoFunction() && arg[1].isNumberValue()) {
				// FitPoly[ <Freehand Function>, <Order> ]
				return fitPolyFunction(c, arg);
			}
			return generalCase(arg, c);

		default:
			return generalCase(arg, c);
		}
	}

	private GeoElement[] generalCase(GeoElement[] arg, Command c) {
		int points = arg.length;
		GeoNumberValue degree = null;
		if (points > 1 && arg[points - 1] instanceof GeoNumberValue) {
			degree = (GeoNumberValue) arg[arg.length - 1];
			points--;
		}
		// try to create list of points
		GeoList list = wrapInList(arg, points, GeoClass.POINT, c);
		if (list != null) {
			GeoElement[] ret = { fitPoly(list, degree) };
			ret[0].setLabel(c.getLabel());
			return ret;
		}
		throw argNumErr(c);
	}

	private GeoElement[] fitPolyFunction(Command c, GeoElement[] arg) {
		GeoFunction fun = (GeoFunction) arg[0];
		if (fun.getParentAlgorithm() instanceof AlgoFunctionFreehand) {

			GeoList list = wrapFreehandFunctionArgInList(kernel,
					(AlgoFunctionFreehand) fun.getParentAlgorithm());

			if (list != null) {
				GeoElement[] ret = { fitPoly(list, (GeoNumberValue) arg[1]) };
				ret[0].setLabel(c.getLabel());
				return ret;
			}
		}
		throw argErr(c, arg[0]);
	}

	/**
	 * FitPoly[list of coords,degree]
	 */
	final private GeoFunction fitPoly(GeoList list, GeoNumberValue degree) {
		AlgoFitPoly algo = new AlgoFitPoly(cons, list, degree);
		GeoFunction function = algo.getFitPoly();
		return function;
	}
}
