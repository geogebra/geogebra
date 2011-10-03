/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.optimization.ExtremumFinder;
import geogebra.kernel.optimization.RealRootFunctionVariable;
import geogebra.main.Application;

/**
 * AlgoSearchMaximum:
 * Command SearchMaximum[ <dependent variable>, <independent variable> ]
 * which searches for the independent variable which gives the 
 * largest result for the dependent variable.
 * 
 *  Packages the relationship as a RealRootFunction for the ExtremumFinder.
 *  
 * @author 	Hans-Petter Ulven
 * @version 10.02.2011
 * 
 * ToDo: Implement the Ggb command chain mechanism.
 * For the time being just a class that can be tested from the outside with scripts.
 * 
 */

public class AlgoSearchMaximum {

	private static final long serialVersionUID = 1L;
	
	private 	 	Construction				cons		=	null;
	private 	 	Kernel						kernel		=	null;
	private 	 	ExtremumFinder				extrFinder	=	null;		//Uses ExtremumFinder for the dirty work
	private 	   	RealRootFunctionVariable	i_am_not_a_real_function=null;	
	private			GeoElement					dep			=	null;
	private			GeoNumeric					indep		=	null;
	
	public  double findMax(double l,double r){

		return extrFinder.findMaximum(l,r,i_am_not_a_real_function,5.0E-8);
		
	}//findMax(l,r)
	
	public double findMin(double l,double r){
		return extrFinder.findMinimum(l,r,i_am_not_a_real_function,5.0E-8);
	}//findMin(l,r)
	
	
	
	// * //--- SNIP (after debugging and testing) ------------------------- 
	
	private final static boolean	DEBUG	=	true;			//debug or errormsg
	
    private final static void debug(String s) {
        if(DEBUG) {
            System.out.println(s);
        }else{
        	Application.debug(s);
        }//if debug or errormsg
    }//debug()
	
    /// --- Test interface --- ///
    
	//  Running tests from external testscripts.
    
    /** Needs a constructor */
    public AlgoSearchMaximum(Construction cons,String depstr,String indepstr){
    	this.cons=cons;
    	kernel=cons.getKernel();
    	GeoElement geo = kernel.lookupLabel(depstr);
    	if( (geo!=null)&& (geo.isGeoElement())){
    		dep=(GeoElement)geo;
    	}//if dep ok
    	geo=kernel.lookupLabel(indepstr);
    	if( (geo!=null) && (geo.isGeoNumeric()) ){
    		indep=(GeoNumeric)geo;
    	}else{
    		indep=null;
    	}//
    	extrFinder=new ExtremumFinder();
    	i_am_not_a_real_function=new RealRootFunctionVariable(dep,indep);
    	
    }//Constructor(cons)
	
    /* Show some info about GeoNumerics *
	public final static void showInfo(String name){
    	double min,max,step,val;
    	GeoElement geo=kernel.lookupLabel(name);
    	GeoNumeric num=null;
    	if( (geo!=null) && (geo.isGeoNumeric()) ){
    		num=(GeoNumeric)geo;
			debug(name+": "+geo+":\n"+
				  "val: "+num.getValue()
			);
		}else{
			debug(name+": not GeoNumeric or null:"+geo);
		}//if dep ok
    }//getX()*/
    

    
    
    
// */ //--- SNIP end ---------------------------------------    
    	
	

}//class AlgoSearchMaximum