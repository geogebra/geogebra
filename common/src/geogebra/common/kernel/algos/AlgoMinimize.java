/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;


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

	/** Constructor for Minimize
	 * @param cons construction
	 * @param label label for output
	 * @param dep dependent value
	 * @param indep independent number*/
	public AlgoMinimize(Construction cons,String label,NumberValue dep,GeoNumeric indep){
		super(cons,label,dep,indep,OptimizationType.MINIMIZE);
		//cons.registerEuclididanViewAlgo(this);
	}//Constructor for Maximize
	

    @Override
	public Algos getClassName() {
    	return Algos.AlgoMinimize;
    }//getClassName()    

	// TODO Consider locusequability

}//class AlgoMinimize