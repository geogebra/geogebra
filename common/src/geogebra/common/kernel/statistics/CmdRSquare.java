package geogebra.common.kernel.statistics; 
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
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

/** 
 * RSquare[<List of Points>,<Funtion>]
 * 
 * @author G.Sturr
 * @version 2010-9-13
 */
public class CmdRSquare extends CommandProcessor{
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
    public CmdRSquare(Kernel kernel) {super(kernel);}
    
    @Override
	public GeoElement[] process(Command c) throws MyError {
        int n=c.getArgumentNumber();
        GeoElement[] arg=resArgs(c);
        boolean[] ok = new boolean[2];
        switch(n) {
            case 2:
            	if(  (ok[0]=arg[0].isGeoList() )&& (ok[1]=arg[1].isGeoFunctionable())  ){ 
            		
            		AlgoRSquare algo = new AlgoRSquare(cons, c.getLabel(),(GeoList)arg[0],(GeoFunctionable) arg[1]);
            		GeoElement[] ret={
            				 algo.getRSquare()
            				};
                    return ret;
            	}
			throw argErr(app,c.getName(),getBadArg(ok,arg));

            default :
    			throw argNumErr(app, c.getName(), n);
        }//switch(number of arguments)
    }//process(Command) 
}// class CmdRSquare