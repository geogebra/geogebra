package geogebra.common.kernel.commands; 
/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.statistics.AlgoSumSquaredErrors;
import geogebra.common.main.MyError;

/** 
 * SumSquaredError[<List of Points>,<Funtion>]
 * 
 * @author Hans-Petter Ulven
 * @version 2010-02-21
 */
public class CmdSumSquaredErrors extends CommandProcessor{
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
    public CmdSumSquaredErrors(Kernel kernel) {super(kernel);}
    
    @Override
	public GeoElement[] process(Command c) throws MyError {
        int n=c.getArgumentNumber();
        GeoElement[] arg=resArgs(c);
        boolean[] ok = new boolean[2];
        switch(n) {
            case 2:
            	if(  (ok[0] = arg[0].isGeoList() )&& 
            			(ok[1] = arg[1].isGeoFunctionable())  ){ 
            		
            		AlgoSumSquaredErrors algo = new AlgoSumSquaredErrors(cons, c.getLabel(),(GeoList)arg[0],(GeoFunctionable) arg[1]);
            		
            		GeoElement[] ret={ algo.getsse() };
                    return ret;
            	}
			throw argErr(app,c.getName(),getBadArg(ok,arg));

            default :
    			throw argNumErr(app, c.getName(), n);
        }//switch(number of arguments)
    }//process(Command) 
}// class CmdSumSquaredErrors