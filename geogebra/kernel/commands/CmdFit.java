package geogebra.kernel.commands; 
/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

/** 
 * Fit[<List Points>,<List of Functions>]  (linear combination)
 * Fit[<List Points>, <Function>] (nonlinear with gliders as startvalues)
 * @author Hans-Petter Ulven
 * @version 2011-03-15
 */
public class CmdFit extends CommandProcessor{

    public CmdFit(Kernel kernel) {super(kernel);}
    
    public GeoElement[] process(Command c) throws MyError {
        int n=c.getArgumentNumber();
        GeoElement[] arg=resArgs(c);;
        switch(n) {
            case 2:
                    if(  (arg[0].isGeoList() )&& (arg[1].isGeoList())  ){ 
                        GeoElement[] ret={kernel.Fit(c.getLabel(),(GeoList)arg[0],(GeoList) arg[1]) };
                        return ret;
                    }else if(  (arg[0].isGeoList() )&& (arg[1].isGeoFunction())  ){
                    	GeoElement[] ret={kernel.Fit(c.getLabel(),(GeoList)arg[0],(GeoFunction) arg[1])  };
                    	return ret;
                    }else{
                        throw argErr(app,c.getName(),arg[0]);
                    }//if arg[0] is GeoList 

            default :

    			throw argNumErr(app, c.getName(), n);
        }//switch(number of arguments)
    }//process(Command) 
}// class CmdFit
