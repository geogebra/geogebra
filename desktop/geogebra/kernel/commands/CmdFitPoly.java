package geogebra.kernel.commands; 
/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/** 
 * FitPoly[<List of points>,<degree>]
 * 
 * @author Hans-Petter Ulven
 * @version 06.04.08
 */
public class CmdFitPoly extends CommandProcessorDesktop{

    public CmdFitPoly(Kernel kernel) {super(kernel);}
    
    public GeoElement[] process(Command c) throws MyError {
        int n=c.getArgumentNumber();
        GeoElement[] arg=resArgs(c);;
        switch(n) {
            case 2:
            if(arg[1].isNumberValue())
                    if(  (arg[0].isGeoList() )&& (arg[1].isNumberValue())  ){ 
                        GeoElement[] ret={kernel.FitPoly(c.getLabel(),(GeoList)arg[0],(NumberValue) arg[1]) };
                        return ret;
                    }else{
                        throw argErr(app,c.getName(),arg[0]);
                    }//if arg[0] is GeoList 

            default :
                // try to create list of points
           	 GeoList list = wrapInList(kernel, arg, arg.length - 1, GeoClass.POINT);
                if (list != null) {
               	 GeoElement[] ret = { kernel.FitPoly(c.getLabel(), list, (NumberValue) arg[arg.length - 1])};
                    return ret;             	     	 
                } 
    			throw argNumErr(app, c.getName(), n);
        }//switch(number of arguments)
    }//process(Command) 
}// class CmdFitPoly


