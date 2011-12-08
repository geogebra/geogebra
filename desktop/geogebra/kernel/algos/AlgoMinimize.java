/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;


/**
 * AlgoMinimize
 * Command Minimize[ <dependent variable>, <independent variable> ] (and Minimize[] )
 * which searches for the independent variable which gives the 
 * smallest result for the dependent variable.
 * 
 *  Extends abstract class AlgoOptimize
 *  
 * @author 	Hans-Petter Ulven
 * @version 20.02.2011
 * 
 */

public class AlgoMinimize extends AlgoOptimize{

	/** Constructor for Maximize*/
	public AlgoMinimize(Construction cons,String label,GeoElement dep,GeoNumeric indep){
		super(cons,label,dep,indep,AlgoOptimize.MINIMIZE);
		//cons.registerEuclididanViewAlgo(this);
	}//Constructor for Maximize
	

    @Override
	public String getClassName() {
    	return "AlgoMinimize";
    }//getClassName()    

}//class AlgoMinimize