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
 * AlgoMaximize
 * Command Minimize[ <dependent variable>, <independent variable> ] (and Minimize[] )
 * which searches for the independent variable which gives the 
 * largest result for the dependent variable.
 * 
 *  Extends abstract class AlgoOptimize
 *  
 * @author 	Hans-Petter Ulven
 * @version 20.02.2011
 * 
 */

public class AlgoMaximize extends AlgoOptimize{

	/** Constructor for Maximize
	 * @param cons construction
	 * @param label label for output
	 * @param dep dependent value
	 * @param indep independent number*/
	public AlgoMaximize(Construction cons,String label,NumberValue dep,GeoNumeric indep){
		super(cons,label,dep,indep,OptimizationType.MAXIMIZE);
		//cons.registerEuclididanViewAlgo(this);
	}//Constructor for Maximize
	

    @Override
	public Algos getClassName() {
    	return Algos.AlgoMaximize;
    }//getClassName()    

	// TODO Consider locusequability

	

}//class AlgoMaximize