package geogebra.common.kernel.commands; 
/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoFunctionFreehand;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.statistics.AlgoFitPoly;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;

/** 
 * FitPoly[<List of points>,<degree>]
 * 
 * @author Hans-Petter Ulven
 * @version 06.04.08
 */
public class CmdFitPoly extends CommandProcessor{
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
    public CmdFitPoly(Kernel kernel) {super(kernel);}
    
    @Override
	public GeoElement[] process(Command c) throws MyError {
        int n=c.getArgumentNumber();
        GeoElement[] arg=resArgs(c);
        switch(n) {
            case 2:
            if(arg[1].isNumberValue()) {
				if(arg[0].isGeoList()) { 
				    GeoElement[] ret={FitPoly(c.getLabel(),(GeoList)arg[0],(NumberValue) arg[1]) };
				    return ret;
				} else if (arg[0].isGeoFunction()) {
					
					// FitPoly[ <Freehand Function>, <Order> ]
					
					GeoFunction fun = (GeoFunction) arg[0];

					if (fun.getParentAlgorithm() instanceof AlgoFunctionFreehand) {

						GeoList list = wrapFreehandFunctionArgInList(kernelA, (AlgoFunctionFreehand) fun.getParentAlgorithm());

						if (list != null) {
			               	 GeoElement[] ret = { FitPoly(c.getLabel(), list, (NumberValue) arg[1])};
							return ret;             	     	 
						} 

					}
					
				}
				
				throw argErr(app,c.getName(),arg[0]);
			}

            default :
                // try to create list of points
           	 GeoList list = wrapInList(kernelA, arg, arg.length - 1, GeoClass.POINT);
                if (list != null) {
               	 GeoElement[] ret = { FitPoly(c.getLabel(), list, (NumberValue) arg[arg.length - 1])};
                    return ret;             	     	 
                } 
    			throw argNumErr(app, c.getName(), n);
        }//switch(number of arguments)
    }//process(Command) 
    


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


