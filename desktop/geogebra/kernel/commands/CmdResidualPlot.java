package geogebra.kernel.commands; 
/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunctionable;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

/** 
 * ResidualPlot[<List of Points>,<Funtion>]
 * 
 * @author G.Sturr
 * @version 2010-9-13
 */
public class CmdResidualPlot extends CommandProcessor{

    public CmdResidualPlot(Kernel kernel) {super(kernel);}
    
    public GeoElement[] process(Command c) throws MyError {
        int n=c.getArgumentNumber();
        GeoElement[] arg=resArgs(c);;
        switch(n) {
            case 2:
            	if(  (arg[0].isGeoList() )&& (arg[1].isGeoFunctionable())  ){ 
            		GeoElement[] ret={
            				kernel.ResidualPlot(c.getLabel(),(GeoList)arg[0],(GeoFunctionable) arg[1]) 
            				};
                    return ret;
            	}else{
            		throw argErr(app,c.getName(),arg[0]);
                }//if args ok

            default :
    			throw argNumErr(app, c.getName(), n);
        }//switch(number of arguments)
    }//process(Command) 
}// class CmdRSquare