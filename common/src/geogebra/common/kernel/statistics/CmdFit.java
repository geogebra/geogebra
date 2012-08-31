/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.kernel.statistics; 

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

/** 
 * Fit[<List Points>,<List of Functions>]  (linear combination)
 * Fit[<List Points>, <Function>] (nonlinear with gliders as startvalues)
 * @author Hans-Petter Ulven
 * @version 2011-03-15
 */
public class CmdFit extends CommandProcessor{

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
    public CmdFit(Kernel kernel) {super(kernel);}
    
    @Override
	public GeoElement[] process(Command c) throws MyError {
        int n=c.getArgumentNumber();
        GeoElement[] arg=resArgs(c);
        switch(n) {
            case 2:
                    if(  (arg[0].isGeoList() )&& (arg[1].isGeoList())  ){ 
                    	
                		AlgoFit algo = new AlgoFit(cons, c.getLabel(),(GeoList)arg[0],(GeoList) arg[1]);

                        GeoElement[] ret={ algo.getFit() };
                        return ret;
                    }else if(  (arg[0].isGeoList() )&& (arg[1].isGeoFunction())  ){
                    	
                		AlgoFitNL algo = new AlgoFitNL(cons, c.getLabel(),(GeoList)arg[0],(GeoFunction) arg[1]);

                    	GeoElement[] ret={ algo.getFitNL() };
                    	return ret;
                    }else{
                        throw argErr(app,c.getName(),arg[0]);
                    }//if arg[0] is GeoList 

            default :

    			throw argNumErr(app, c.getName(), n);
        }//switch(number of arguments)
    }//process(Command) 
}// class CmdFit
