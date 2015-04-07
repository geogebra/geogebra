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
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * FitPoly[<List of points>,<degree>]
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
		case 2:
			if (arg[1] instanceof GeoNumberValue) {
				if (arg[0].isGeoList()) {
					GeoElement[] ret = { FitPoly(c.getLabel(),
							(GeoList) arg[0], (GeoNumberValue) arg[1]) };
					return ret;
				} else if (arg[0].isGeoFunction()) {

					// FitPoly[ <Freehand Function>, <Order> ]

					GeoFunction fun = (GeoFunction) arg[0];

					if (fun.getParentAlgorithm() instanceof AlgoFunctionFreehand) {

						GeoList list = wrapFreehandFunctionArgInList(kernelA,
								(AlgoFunctionFreehand) fun.getParentAlgorithm());

						if (list != null) {
							GeoElement[] ret = { FitPoly(c.getLabel(), list,
									(NumberValue) arg[1]) };
							return ret;
						}

					}

				}

				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			// try to create list of points
			GeoList list = wrapInList(kernelA, arg, arg.length - 1,
					GeoClass.POINT);
			if (list != null) {
				GeoElement[] ret = { FitPoly(c.getLabel(), list,
						(NumberValue) arg[arg.length - 1]) };
				return ret;
			}
			throw argNumErr(app, c.getName(), n);
		}// switch(number of arguments)
	}// process(Command)

	/**
	 * FitPoly[list of coords,degree] Hans-Petter Ulven
	 */
	final private GeoFunction FitPoly(String label, GeoList list,
			NumberValue degree) {
		AlgoFitPoly algo = new AlgoFitPoly(cons, label, list, degree);
		GeoFunction function = algo.getFitPoly();
		return function;
	}
}// class CmdFitPoly

